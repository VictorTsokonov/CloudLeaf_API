package com.cloudleafapi.claoudleaf.Entities;

import java.util.UUID;

public record DeploymentEntity(
        UUID deploymentId,
        UUID userId,
        UUID repoId,
        String ec2InstanceId,
        String ec2PublicIp
) {
}