package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Entities.UserEntity;
import com.cloudleafapi.claoudleaf.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class User {

	private final UserService userService;

	@Autowired
	public User(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public UserEntity createUser(@RequestParam String github_username,
			@RequestParam String github_access_token) {
		return userService.createUser(github_username, github_access_token);
	}

	@GetMapping("/{id}")
	public UserEntity getUser(@PathVariable UUID id) {
		return userService.getUser(id)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	@GetMapping
	public List<UserEntity> listUsers() {
		return userService.listUsers();
	}

	@PutMapping("/{userId}")
	public ResponseEntity<UserEntity> updateUser(@PathVariable UUID userId,
			@RequestBody UserEntity userEntity) {
		UserEntity currentUser = userService.getUser(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		currentUser = new UserEntity(userId, userEntity.github_username(),
				userEntity.github_access_token());
		UserEntity updatedUser = userService.updateUser(currentUser);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@GetMapping("/token/{accessToken}")
	public UserEntity getUserByAccessToken(@PathVariable String accessToken) {
		return userService.findUserByGithubAccessToken(accessToken)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	// Additional methods for update and delete if needed

}
