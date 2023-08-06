package com.cloudleafapi.claoudleaf.Repositories;

import com.cloudleafapi.claoudleaf.Entities.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

	UserEntity createUser(String github_username, String github_access_token);

	Optional<UserEntity> getUser(UUID user_id);

	Optional<UserEntity> findUserByGithubUsername(String username);

	List<UserEntity> listUsers();

	void deleteUser(UUID userId);

	UserEntity updateUser(UserEntity userEntity);

	Optional<UserEntity> findUserByGithubAccessToken(String accessToken);

}
