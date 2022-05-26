package com.hosting.rest.api.services.User;

import java.util.List;

import com.hosting.rest.api.models.User.UserModel;

/**
 * @author Francisco Coya · https://github.com/FranciscoCoya
 * @version v1.0.1
 * @description
 **/
public interface IUserService {

	public UserModel updateUser(final Integer userId, final UserModel userToUpdate);

	public void deleteUserById(final Integer userId);

	public List<UserModel> findAllStartedUsers();

	public UserModel getUserById(final Integer userId);
	
	public Integer getUserIdByEmail(final String email);

	public List<UserModel> findAllUsers();
	
	public void updateProfileImage(final Integer userId, final String imgUrl);
}
