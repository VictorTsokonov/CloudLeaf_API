package com.cloudleafapi.claoudleaf.Services;

import com.cloudleafapi.claoudleaf.Entities.UserEntity;
import com.cloudleafapi.claoudleaf.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createUser(String github_username, String github_access_token) {
        Optional<UserEntity> optionalUserEntity = userRepository.findUserByGithubUsername(github_username);
        if (optionalUserEntity.isPresent()) {
            UserEntity currentUser = optionalUserEntity.get();
            if (!currentUser.github_access_token().equals(github_access_token)) {
                currentUser = new UserEntity(currentUser.user_id(), github_username, github_access_token);
                return userRepository.updateUser(currentUser);
            }
            return currentUser;
        } else {
            return userRepository.createUser(github_username, github_access_token);
        }
    }


    public Optional<UserEntity> getUser(UUID user_id) {
        return userRepository.getUser(user_id);
    }

    public Optional<UserEntity> findUserByGithubUsername(String username) {
        return userRepository.findUserByGithubUsername(username);
    }

    public List<UserEntity> listUsers() {
        return userRepository.listUsers();
    }

    public void deleteUser(UUID userId) {
        userRepository.deleteUser(userId);
    }

    public UserEntity updateUser(UserEntity userEntity) {
        return userRepository.updateUser(userEntity);
    }
}