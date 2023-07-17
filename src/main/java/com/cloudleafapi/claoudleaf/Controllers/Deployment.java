package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;
import com.cloudleafapi.claoudleaf.Services.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/deployments")
public class Deployment {

    private final DeploymentService deploymentService;

    @Autowired
    public Deployment(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

//    @PostMapping
//    public DeploymentEntity createDeployment(/*your parameters here*/) {
//        return deploymentService.createDeployment(/*your parameters here*/);
//    }

    @GetMapping("/{id}")
    public DeploymentEntity getDeployment(@PathVariable UUID id) {
        return deploymentService.getDeployment(id).orElseThrow(() -> new IllegalArgumentException("Deployment not found"));
    }

    // Additional methods for list, update and delete if needed
}
