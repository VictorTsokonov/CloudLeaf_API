package com.cloudleafapi.claoudleaf.postgresql;

import com.cloudleafapi.claoudleaf.Entities.DeployStatus;
import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;
import com.cloudleafapi.claoudleaf.Repositories.DeploymentRepository;
import com.cloudleafapi.claoudleaf.RowMappers.DeploymentRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresDeploymentRepository implements DeploymentRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostgresDeploymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DeploymentEntity createDeployment(String userID,
                                             String repoID,
                                             String ec2InstanceId,
                                             String asgName,
                                             String elbName,
                                             String securityGroupId,
                                             String ec2PublicIp,
                                             String elbPublicIp,
                                             DeployStatus status) {
        UUID deploymentId = UUID.randomUUID();
        String sql = "INSERT INTO deployments (deployment_id, user_id, repo_id, ec2_instance_id, asg_name, elb_name, security_group_id, ec2_public_ip, elb_public_ip, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                deploymentId,
                UUID.fromString(userID),
                UUID.fromString(repoID),
                ec2InstanceId,
                asgName,
                elbName,
                securityGroupId,
                ec2PublicIp,
                elbPublicIp,
                status.toString());
        return getDeployment(deploymentId).orElseThrow(() -> new RuntimeException("Deployment not found"));
    }

    @Override
    public Optional<DeploymentEntity> getDeployment(UUID deploymentId) {
        String sql = "SELECT * FROM deployments WHERE deployment_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new DeploymentRowMapper(), deploymentId));
    }

    @Override
    public List<DeploymentEntity> listDeploymentsByUserId(UUID userId) {
        String sql = "SELECT * FROM deployments WHERE user_id = ?";
        return jdbcTemplate.query(sql, new DeploymentRowMapper(), userId);
    }

    @Override
    public List<DeploymentEntity> listDeploymentsByRepoId(UUID repoId) {
        String sql = "SELECT * FROM deployments WHERE repo_id = ?";
        return jdbcTemplate.query(sql, new DeploymentRowMapper(), repoId);
    }

    @Override
    public void deleteDeployment(UUID deploymentId) {
        String sql = "DELETE FROM deployments WHERE deployment_id = ?";
        jdbcTemplate.update(sql, deploymentId);
    }

    @Override
    public DeploymentEntity updateDeployment(DeploymentEntity deploymentEntity) {
        String sql = "UPDATE deployments SET user_id = ?, repo_id = ?, ec2_instance_id = ?, asg_name = ?, elb_name = ?, security_group_id = ?, ec2_public_ip = ?, elb_public_ip = ?, status = ? WHERE deployment_id = ?";
        jdbcTemplate.update(sql,
                UUID.fromString(String.valueOf(deploymentEntity.userId())),
                UUID.fromString(String.valueOf(deploymentEntity.repoId())),
                deploymentEntity.ec2InstanceId(),
                deploymentEntity.asgName(),
                deploymentEntity.elbName(),
                deploymentEntity.securityGroupId(),
                deploymentEntity.ec2PublicIp(),
                deploymentEntity.elbPublicIp(),
                deploymentEntity.status().toString(),
                UUID.fromString(String.valueOf(deploymentEntity.deploymentId())));
        return getDeployment(UUID.fromString(String.valueOf(deploymentEntity.deploymentId()))).orElseThrow(() -> new RuntimeException("Deployment not found"));
    }

    @Override
    public List<DeploymentEntity> listDeploymentsByStatus(DeployStatus status) {
        String sql = "SELECT * FROM deployments WHERE status = ?";
        return jdbcTemplate.query(sql, new DeploymentRowMapper(), status.toString());
    }
}
