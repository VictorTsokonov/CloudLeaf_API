package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;
import com.cloudleafapi.claoudleaf.Entities.RepoEntity;
import com.cloudleafapi.claoudleaf.Services.AWSDeployService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deploy")
public class AWSDeploy {
    private final AWSDeployService ec2Service;


    public AWSDeploy(AWSDeployService ec2Service) {
        this.ec2Service = ec2Service;
    }

    @PostMapping
    public List<String> deployRepo(@RequestParam String full_name, @RequestParam String clone_url, @RequestParam String ssh_url) {

        String name = full_name.split("/")[1];

        // Deploy the repository to EC2 instance
        List<String> ec2Details = ec2Service.deployRepo(name, clone_url, ssh_url); // 0 -> IP, 1 -> ID

        // Send GET request to /api/repos/name endpoint to get the repo details
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders deployHeaders = new HttpHeaders();
        deployHeaders.set("repoName", full_name);
        HttpEntity<String> entity = new HttpEntity<>("parameters", deployHeaders);

        ResponseEntity<RepoEntity> repoEntityResponse = restTemplate.exchange(
                "http://localhost:8080/api/repos/name",
                HttpMethod.GET,
                entity,
                RepoEntity.class
        );

        // Check if the repo exists
        if (!repoEntityResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("No repository found with name: " + full_name);
        }

        RepoEntity repoEntity = repoEntityResponse.getBody();

//        assert repoEntity != null;
        UUID repoId = repoEntity.repoId();
        UUID userId = repoEntity.userId();

        // Create a new DeploymentEntity and save it to the database
        DeploymentEntity newDeployment = new DeploymentEntity(
                UUID.randomUUID(),
                userId,
                repoId,
                ec2Details.get(1), // EC2 instance ID
                ec2Details.get(0)  // EC2 public IP
        );

        // Send POST request to /api/deployments endpoint
        HttpHeaders deploymentHeaders = new HttpHeaders();
        deploymentHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DeploymentEntity> request = new HttpEntity<>(newDeployment, deploymentHeaders);

        ResponseEntity<DeploymentEntity> responseEntity = restTemplate.exchange(
                "http://localhost:8080/api/deployments",
                HttpMethod.POST,
                request,
                DeploymentEntity.class
        );

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create deployment");
        }

        return ec2Details;
    }


    @GetMapping("/test")
    public List<String> deployTest() {
        return ec2Service.deployRepo(
                "TestHostRepo",
                "https://github.com/VictorTsokonov/TestHostRepo.git",
                "git@github.com:VictorTsokonov/TestHostRepo.git"); // 0 -> IP, 1 -> ID
//        return ec2Service.toString();
    }

    @GetMapping("what")
    public String o() {
        return "Nice what";
    }


}
