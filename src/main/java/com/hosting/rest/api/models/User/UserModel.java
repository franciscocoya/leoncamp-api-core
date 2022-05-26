package com.hosting.rest.api.models.User;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hosting.rest.api.models.User.UserConfiguration.UserConfigurationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "APP_USER")
public class UserModel implements Serializable {

	private static final long serialVersionUID = -9181598620645816069L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "UNAME")
	private String name;

	@Column(name = "SURNAME")
	private String surname;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE")
	private String phone;

	@JsonIgnore
	@Column(name = "PASS")
	private String pass;

	@Column(name = "PROFILE_IMG")
	private String profileImage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_APP_CONFIGURATION")
	private UserConfigurationModel idUserConfiguration;

	@Column(name = "CREATED_AT")
	@CreatedDate
	private LocalDateTime createdAt;
	
	
	public UserModel(final String name, final String surname, final String email, final String password) {
		setName(name);
		setSurname(surname);
		setEmail(email);
		setPass(password);
	}
	
	public UserModel(final String name, final String surname, final String email, final UserConfigurationModel userConfig, final String password) {
		this(name, surname, email, password);
		this.idUserConfiguration = userConfig;
	}
	
	public UserModel(final Integer id, final String name, final String surname, final String email, final String phone, final UserConfigurationModel userConfig, final String password) {
		this(name, surname, email, password);
		this.id = id;
		this.phone = phone;
		this.idUserConfiguration = userConfig;
	}
}
