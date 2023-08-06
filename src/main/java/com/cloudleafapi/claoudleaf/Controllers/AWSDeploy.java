package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;
import com.cloudleafapi.claoudleaf.Entities.RepoEntity;
import com.cloudleafapi.claoudleaf.Requests.StatusRequest;
import com.cloudleafapi.claoudleaf.Services.AWSDeployService;
import com.cloudleafapi.claoudleaf.Services.DeploymentService;
import com.cloudleafapi.claoudleaf.Services.RepoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/deploy")
public class AWSDeploy {

	private final AWSDeployService ec2Service;

	private final RepoService repoService;

	private final StatusController statusController; // Add this line

	private final DeploymentService deploymentService;

	public static class DeployRequest {

		public String full_name; // Note the field name

		public String clone_url;

		public String ssh_url;

		public String port;

		public List<String> dependencies;

		public Map<String, String> environments;

	}

	public AWSDeploy(AWSDeployService ec2Service, RepoService repoService,
			StatusController statusController, DeploymentService deploymentService) {
		this.ec2Service = ec2Service;
		this.repoService = repoService;
		this.statusController = statusController;
		this.deploymentService = deploymentService;
	}

	// Please be aware that there might be a size limit for the HTTP headers size,
	// which is usually around 8KB for most servers, but it depends on the specific server
	// configuration.
	// So if your dependenciesList is large, it might not fit into the headers.
	// Remember to update your backend logic to parse the dependencies header back into a
	// list:
	// List<String> dependenciesList = new Gson().fromJson(dependenciesHeader,
	// List.class);

	@PostMapping
	public List<String> deployRepo(@RequestBody DeployRequest deployRequest) {
		String full_name = deployRequest.full_name;
		String name = full_name.split("/")[1];
		String clone_url = deployRequest.clone_url;
		String ssh_url = deployRequest.ssh_url;
		String port = deployRequest.port;
		List<String> dependencies = deployRequest.dependencies;
		System.out.println(deployRequest.environments);
		Map<String, String> environments = deployRequest.environments;

		// Use RepoService to get the repo details
		Optional<RepoEntity> repoEntityOpt = repoService.getRepoByName(full_name);

		if (repoEntityOpt.isEmpty()) {
			throw new IllegalArgumentException(
					"No repository found with name: " + full_name);
		}

		RepoEntity repoEntity = repoEntityOpt.get();
		UUID repoId = repoEntity.repoId();

		// Check if there are any deployments for this repo
		List<DeploymentEntity> existingDeployments = deploymentService
				.listDeploymentsByRepoId(repoId);

		List<String> ec2Details = new ArrayList<>();
		if (existingDeployments.isEmpty()) { // existingDeployments.size() == 0
			// If there are no existing deployments, deploy the repository to EC2 instance
			ec2Details = ec2Service.deployRepo(name, clone_url, ssh_url, port,
					dependencies, environments);

			UUID userId = repoEntity.userId();

			// Create a new DeploymentEntity and save it to the database
			DeploymentEntity newDeployment = new DeploymentEntity(UUID.randomUUID(),
					userId, repoId, ec2Details.get(1), // EC2 instance ID
					ec2Details.get(0) // EC2 public IP
			);

			// Use DeploymentService to create the deployment
			DeploymentEntity createdDeployment = deploymentService.createDeployment(
					newDeployment.userId().toString(), newDeployment.repoId().toString(),
					newDeployment.ec2InstanceId(), newDeployment.ec2PublicIp());

			if (createdDeployment == null) {
				throw new RuntimeException("Failed to create deployment in database");
			}

			// Trigger checkStatus
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			System.out.println(full_name);
			System.out.println(ec2Details.get(0));

			// StatusRequest statusRequest = new StatusRequest(full_name,
			// ec2Details.get(0));
			// HttpEntity<StatusRequest> request = new HttpEntity<>(statusRequest,
			// headers);
			//
			// restTemplate.postForEntity("http://localhost:8080/status", request,
			// String.class); // replace localhost and port with your actual domain and
			// port
			repoService.updateRepoStatusByRepoName(full_name, "Deploying...");
			StatusRequest statusRequest = new StatusRequest(full_name, ec2Details.get(0));
			statusController.checkStatus(statusRequest);
		}

		return ec2Details;
	}

	@DeleteMapping("/{instanceIp}")
	public void terminateInstance(@PathVariable String instanceIp) {
		Optional<DeploymentEntity> deploymentEntity = deploymentService
				.getDeploymentByInstanceIp(instanceIp.trim());
		if (deploymentEntity.isEmpty()) {
			throw new RuntimeException(
					"Deployment doesn't exist INSTANCE_IP: " + instanceIp);
		}

		Optional<RepoEntity> repoEntity = repoService
				.getRepo(deploymentEntity.get().repoId());
		if (repoEntity.isEmpty()) {
			throw new RuntimeException("Repository doesn't exist or something else...");
		}
		repoService.updateRepoStatusByRepoName(repoEntity.get().repoName(), "Terminated");
		repoService.updateRepoIpAddressByRepoName(repoEntity.get().repoName(), "");

		deploymentService.deleteDeployment(deploymentEntity.get().deploymentId());

		ec2Service.terminateInstance(deploymentEntity.get().ec2InstanceId());
	}

}
