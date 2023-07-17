package com.cloudleafapi.claoudleaf.Repositories;

import com.cloudleafapi.claoudleaf.Entities.DeployStatus;
import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeploymentRepository {
    DeploymentEntity createDeployment(String userID, String repoID, String ec2InstanceId,
                                      String asgName, String elbName, String securityGroupId,
                                      String ec2PublicIp, String elbPublicIp,
                                      DeployStatus status);

    Optional<DeploymentEntity> getDeployment(UUID deploymentId);

    List<DeploymentEntity> listDeploymentsByUserId(UUID userId);

    List<DeploymentEntity> listDeploymentsByRepoId(UUID repoId);

    void deleteDeployment(UUID deploymentId);

    DeploymentEntity updateDeployment(DeploymentEntity deploymentEntity);

    List<DeploymentEntity> listDeploymentsByStatus(DeployStatus status);

}


//    UserEntity createUser(String github_username, String github_access_token);
//
//    Optional<UserEntity> getUser(UUID user_id);
//
//    List<UserEntity> listUsers();