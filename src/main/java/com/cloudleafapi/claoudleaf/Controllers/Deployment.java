package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;
import com.cloudleafapi.claoudleaf.Services.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deployments")
public class Deployment {

	private final DeploymentService deploymentService;

	@Autowired
	public Deployment(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}

	@PostMapping
	public DeploymentEntity createDeployment(
			@RequestBody DeploymentEntity deploymentEntity) {
		return deploymentService.createDeployment(deploymentEntity.userId().toString(),
				deploymentEntity.repoId().toString(), deploymentEntity.ec2InstanceId(),
				deploymentEntity.ec2PublicIp());
	}

	@GetMapping("/{id}")
	public DeploymentEntity getDeployment(@PathVariable UUID id) {
		return deploymentService.getDeployment(id)
				.orElseThrow(() -> new IllegalArgumentException("Deployment not found"));
	}

	@GetMapping("/user/{userId}")
	public List<DeploymentEntity> listDeploymentsByUserId(@PathVariable UUID userId) {
		return deploymentService.listDeploymentsByUserId(userId);
	}

	@GetMapping("/repo/{repoId}")
	public List<DeploymentEntity> listDeploymentsByRepoId(@PathVariable UUID repoId) {
		return deploymentService.listDeploymentsByRepoId(repoId);
	}

	@PutMapping("/{id}")
	public DeploymentEntity updateDeployment(@PathVariable UUID id,
			@RequestBody DeploymentEntity deploymentEntity) {
		if (!id.equals(deploymentEntity.deploymentId())) {
			throw new IllegalArgumentException(
					"Deployment ID in the path variable and the request body do not match");
		}
		return deploymentService.updateDeployment(deploymentEntity);
	}

	@DeleteMapping("/{id}")
	public void deleteDeployment(@PathVariable UUID id) {
		deploymentService.deleteDeployment(id);
	}

}
