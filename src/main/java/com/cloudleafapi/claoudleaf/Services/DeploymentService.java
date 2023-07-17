package com.cloudleafapi.claoudleaf.Services;

import com.cloudleafapi.claoudleaf.Entities.DeployStatus;
import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;
import com.cloudleafapi.claoudleaf.Repositories.DeploymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;

    @Autowired
    public DeploymentService(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    public DeploymentEntity createDeployment(String userID, String repoID, String ec2InstanceId, String asgName, String elbName, String securityGroupId, String ec2PublicIp, String elbPublicIp, DeployStatus status) {
        return deploymentRepository.createDeployment(userID, repoID, ec2InstanceId, asgName, elbName, securityGroupId, ec2PublicIp, elbPublicIp, status);
    }

    public Optional<DeploymentEntity> getDeployment(UUID deploymentId) {
        return deploymentRepository.getDeployment(deploymentId);
    }

    public List<DeploymentEntity> listDeploymentsByUserId(UUID userId) {
        return deploymentRepository.listDeploymentsByUserId(userId);
    }

    public List<DeploymentEntity> listDeploymentsByRepoId(UUID repoId) {
        return deploymentRepository.listDeploymentsByRepoId(repoId);
    }

    public void deleteDeployment(UUID deploymentId) {
        deploymentRepository.deleteDeployment(deploymentId);
    }

    public DeploymentEntity updateDeployment(DeploymentEntity deploymentEntity) {
        return deploymentRepository.updateDeployment(deploymentEntity);
    }

    public List<DeploymentEntity> listDeploymentsByStatus(DeployStatus status) {
        return deploymentRepository.listDeploymentsByStatus(status);
    }
}
