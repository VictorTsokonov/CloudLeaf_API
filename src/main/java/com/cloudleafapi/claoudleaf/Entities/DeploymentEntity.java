package com.cloudleafapi.claoudleaf.Entities;

import java.util.UUID;


public record DeploymentEntity(
        UUID deploymentId,
        UUID userId,
        UUID repoId,
        String ec2InstanceId,
        String asgName,
        String elbName,
        String securityGroupId,
        String ec2PublicIp,
        String elbPublicIp,
        DeployStatus status
) {
}
