package com.hosting.rest.api.services.User.UserReview;

import static com.hosting.rest.api.Utils.AppUtils.isIntegerValidAndPositive;
import static com.hosting.rest.api.Utils.AppUtils.isNotNull;
import static com.hosting.rest.api.Utils.ServiceParamValidator.validateParam;
import static com.hosting.rest.api.Utils.ServiceParamValidator.validateParamNotFound;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hosting.rest.api.models.User.HostReviewModel;
import com.hosting.rest.api.repositories.User.IUserRepository;
import com.hosting.rest.api.repositories.User.UserReview.IUserReviewRepository;

/**
 * 
 * @author Francisco Coya
 * @version v1.0.3
 * @apiNote Servicio que gestiona las valoraciones de un usuario de la
 *          aplicación.
 *
 */
@Service
public class UserReviewServiceImpl implements IUserReviewService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private IUserReviewRepository userReviewRepo;

	@Autowired
	private IUserRepository userRepo;

	/**
	 * Añade una nueva valoración a un usuario.
	 * 
	 * @param newUserReview
	 * 
	 * @return
	 */
	@Override
	public HostReviewModel addNewUserReview(final HostReviewModel newUserReview) {
		// Validar valoracion pasada como parametro
		validateParam(isNotNull(newUserReview), "Los datos introducidos para el nuevo usuario no son válidos.");

		// Comprobar si existe la valoracion
		validateParamNotFound(!userReviewRepo.existsById(newUserReview.getId()),
				"Ya existe una valoración para el usuario.");

		return userReviewRepo.save(newUserReview);
	}

	/**
	 * Actualiza el contenido de una valoración de un usuario.
	 * 
	 * @param userId
	 * @param userReviewToUpdate
	 * 
	 * @return
	 * 
	 * @throws NumberFormatException Si el id de usuario no es un número.
	 */
	@Transactional
	@Override
	public HostReviewModel updateUserReview(final Integer userId, final HostReviewModel userReviewToUpdate)
			throws NumberFormatException {
		// Validar id del usuario
		validateParam(isIntegerValidAndPositive(userId),
				"El id del usuario [ " + userId + " ] introducido no es válido.");

		HostReviewModel originalHostReview = userReviewRepo.findById(userId).get();

		// Validar valoracion original
		validateParam(isNotNull(originalHostReview), "La valoración a actualizar los datos no existe.");

		// Comprobar si existe el usuario
		validateParamNotFound(userRepo.existsById(userId), "El usuario con id [ " + userId + " ] no existe.");

		// Contenido de la review
		originalHostReview.setContent(userReviewToUpdate.getContent());

		// Estrellas de la review
		originalHostReview.setStars(userReviewToUpdate.getStars());

		return userReviewRepo.save(originalHostReview);
	}

	/**
	 * Elimina una valoración de un usuario por el id de usuario <code>userId</code>
	 * 
	 * @param userId
	 * 
	 * @throws NumberFormatException Si el id de usuario no es un número.
	 * 
	 */
	@Override
	public void deleteUserReview(final Integer userId) throws NumberFormatException {
		// Validar id de usuario.
		validateParam(isIntegerValidAndPositive(userId), "El id de usuario [ " + userId + " ] no es válido.");

		// Comprobar si existe el usuario.
		validateParamNotFound(userRepo.existsById(userId),
				"El usuario con id [ " + userId + " ] a borrar su valoración no existe.");

		userReviewRepo.deleteById(userId);
	}

	/**
	 * Listado de las valoraciones a un usuario <code>userId</code>.
	 * 
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws NumberFormatException Si el id de usuario no es un número.
	 */
	@Override
	public List<HostReviewModel> findByUserId(final Integer userId) throws NumberFormatException {
		// Validar id de usuario
		validateParam(isIntegerValidAndPositive(userId), "El id de usuario [ " + userId + " ] no es válido.");

		// Comprobar si existe el usuario
		validateParamNotFound(userRepo.existsById(userId),
				"El usuario con id [ " + userId + " ] a borrar su valoración no existe.");

		String findByUserIdQuery = "SELECT hr " + " FROM HostReviewModel hr " + " INNER JOIN hr.idUserA hu "
				+ " WHERE hu.id = :userId";

		TypedQuery<HostReviewModel> userReviews = em.createQuery(findByUserIdQuery, HostReviewModel.class);

		userReviews.setParameter("userId", userId);

		return userReviews.getResultList();
	}

}
