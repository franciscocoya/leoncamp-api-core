package com.hosting.rest.api.services.Auth;

import static com.hosting.rest.api.Utils.AppUtils.isNotNull;
import static com.hosting.rest.api.Utils.AppUtils.isStringNotBlank;
import static com.hosting.rest.api.Utils.ServiceParamValidator.validateParam;
import static com.hosting.rest.api.Utils.ServiceParamValidator.validateParamNotFound;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hosting.rest.api.configuration.security.JwtUtils;
import com.hosting.rest.api.models.Auth.ResetPasswordPayload;
import com.hosting.rest.api.models.Auth.ResetPasswordResponsePayload;
import com.hosting.rest.api.models.Auth.SignUpRequest;
import com.hosting.rest.api.models.User.UserModel;
import com.hosting.rest.api.models.User.UserConfiguration.UserConfigurationModel;
import com.hosting.rest.api.repositories.User.IUserRepository;
import com.hosting.rest.api.repositories.User.UserConfiguration.IUserConfigurationRepository;

/**
 * 
 * @author Francisco Coya
 * @version v1.0.0
 * @apiNote Servicio que gestiona la autenticación de usuarios.
 *
 */
@Service
public class AuthServiceImpl implements AuthService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private IUserRepository userRepo;

	@Autowired
	private IUserConfigurationRepository userConfigRepo;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JavaMailSender emailSender;

	@Value("${booking.api.contextPath}")
	private String contextPath;

	@Value("${booking.api.supportEmail}")
	private String supportEmail;

	/**
	 * Crear un nuevo usuario con nombre, apellidos, email y contraseña.
	 * 
	 * @param signUpPayload
	 * 
	 * @return
	 * 
	 */
	@Override
	public ResponseEntity<?> addNewUser(final SignUpRequest signUpPayload) {
		// Validar el usuario pasado como parámetro
		validateParam(isNotNull(signUpPayload), "Alguna propiedad del usuario a crear falta o no es válida.");

		// Validar nombre
		validateParam(isStringNotBlank(signUpPayload.getName()), "El nombre es obligatorio");

		// Validar apellidos
		validateParam(isStringNotBlank(signUpPayload.getSurname()), "Los apellidos son obligatorios");

		// Validar correo electrónico
		validateParam(isStringNotBlank(signUpPayload.getEmail()), "El correo electrónico es obligatorio");

		// Validar contraseña
		validateParam(isStringNotBlank(signUpPayload.getPassword()), "La contraseña es obligatoria");

		// Validar contraseña repetida
		validateParam(isStringNotBlank(signUpPayload.getRepeatedPassword()), "Repite la contraseña");

		// Comprobar que coinciden las contraseñas
		if (!signUpPayload.getPassword().equals(signUpPayload.getRepeatedPassword())) {
			return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
		}

		// Comprobar si existe el usuario
		if (userRepo.existsByEmail(signUpPayload.getEmail())) {
			return ResponseEntity.badRequest().body("Ya existe un usuario con el email " + signUpPayload.getEmail());
		}

		UserConfigurationModel defaultUserConfig = userConfigRepo.getById(1);

		// Datos del nuevo usuario
		UserModel newUser = new UserModel(signUpPayload.getName(), signUpPayload.getSurname(), signUpPayload.getEmail(),
				defaultUserConfig, passwordEncoder.encode(signUpPayload.getPassword()));

		// Crear el usuario
		userRepo.save(newUser);

		return ResponseEntity.ok(newUser);
	}

	@Override
	public ResponseEntity<?> resetPassword(final String emailToResetPassword) {
		// Validar nueva contraseña
		if (!isStringNotBlank(emailToResetPassword)) {
			return ResponseEntity.badRequest().body("Por favor, introduce el email.");
		}

		UserModel user = userRepo.findByEmail(emailToResetPassword).get();

		// Comprobar si existe un usuario con el email
		if (!userRepo.existsByEmail(emailToResetPassword)) {
			return ResponseEntity.badRequest().body("No existe ningún usuario con el email " + emailToResetPassword);
		}

		// Credenciales del usuario
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPass()));

		// Generación del token
		String resetToken = JwtUtils.generateToken(authentication);

		// Fecha de expiración del token
		Date tokenExpiresAt = new Date(System.currentTimeMillis() + ResetPasswordResponsePayload.EXPIRATION_TIME);

		// Enviar correo de recuperación al usuario.
		emailSender.send(constructResetTokenEmail(resetToken, user, tokenExpiresAt));

		return ResponseEntity.ok(new ResetPasswordResponsePayload(resetToken, tokenExpiresAt));
	}

	/**
	 * Crea el enlace de recuperación de la contraseña.
	 * 
	 * @param locale
	 * @param token
	 * @param expiryDate
	 * 
	 * @return
	 */
	private SimpleMailMessage constructResetTokenEmail(final String token, final UserModel userTo,
			final Date expiryDate) {

		String url = contextPath + CHANGE_PASSWORD_URI + token;

		String message = "Hola " + userTo.getName()
				+ "\n\nHas solicitado un cambio de contraseña. A continuación, te enviamos un link de recuperación.\n\n";

		return constructEmail("[LeonCamp support - No reply] Reset Password", message + " \r\n" + url,
				userTo.getEmail());
	}

	/**
	 * Crea un mensaje de correo electrónico a partir de los datos pasados como
	 * parámetro.
	 * 
	 * @param subject
	 * @param body
	 * @param emailTo
	 * 
	 * @return
	 */
	private SimpleMailMessage constructEmail(final String subject, final String body, final String emailTo) {
		SimpleMailMessage email = new SimpleMailMessage();

		email.setSubject(subject);
		email.setText(body);
		email.setTo(emailTo);
		email.setFrom(supportEmail);

		return email;
	}

	/**
	 * Resetea la contraseña del usuario con id <code>userId</code> con la nueva
	 * contraseña.
	 * 
	 * Se comprueba que el usuario tenga la contraseña indicada en el payload y que
	 * además la nueva contraseña y su repetición coincidan.
	 * 
	 */
	@Override
	@Transactional
	public void resetPasswordLoggedUser(final Integer userId, final ResetPasswordPayload resetPasswordPayload) {

		// Comprobar que la contraseña actual es válida
		String userPass = userRepo.getById(userId).getPass().trim();

		// Validar contraseña actual
		validateParam(isStringNotBlank(resetPasswordPayload.getOldPassword().trim().replace(" ", "")), "La contraseña actual es obligatoria");
		
		// Validar nueva
		validateParam(isStringNotBlank(resetPasswordPayload.getNewPassword().trim().replace(" ", "")), "La nueva contraseña es obligatoria");
		
		// Validar contraseña actual
		validateParam(isStringNotBlank(resetPasswordPayload.getNewPasswordRepeated().replace(" ", "")), "Es obligatorio repetir la nueva contraseña");
		
		validateParamNotFound(passwordEncoder.matches(resetPasswordPayload.getOldPassword().trim(), userPass),
				"La contraseña actual es incorrecta");

		// Comprobar que la nueva contraseña y la repetida coinciden
		validateParam(resetPasswordPayload.getNewPassword().equals(resetPasswordPayload.getNewPasswordRepeated()),
				"Las contraseñas no coinciden");

		em.createQuery("UPDATE UserModel um " + "SET um.pass = :newPass " + "WHERE um.id = :userId")
				.setParameter("newPass", passwordEncoder.encode(resetPasswordPayload.getNewPassword()))
				.setParameter("userId", userId).executeUpdate();
	}
}
