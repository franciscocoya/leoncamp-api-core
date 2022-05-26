package com.hosting.rest.api.models.Search;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.hosting.rest.api.models.User.UserModel;

import lombok.Data;

@Entity
@Data
@Table(name = "USER_SEARCH_HISTORY")
public class UserSearchHistoryModel implements Serializable{

	private static final long serialVersionUID = -6836166690142036766L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer idUserSearchHistory;

	@ManyToOne
	@JoinColumn(name = "ID_USER")
	private UserModel idUser;

	@ManyToOne
	@JoinColumn(name = "ID_SEARCH")
	private SearchModel idSearch;
}
