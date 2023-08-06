package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Services.AWSSecretsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ssm")
public class AWSSecretsManager {

	private final AWSSecretsManagerService secretsManagerService;

	@Autowired
	public AWSSecretsManager(AWSSecretsManagerService secretsManagerService) {
		this.secretsManagerService = secretsManagerService;
	}

	@GetMapping("/parameters")
	public List<Object> getParameters(@RequestParam String githubName) {
		return secretsManagerService.getParametersByGithubName(githubName);
	}

}