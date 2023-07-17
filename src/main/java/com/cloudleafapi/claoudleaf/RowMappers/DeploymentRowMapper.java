package com.cloudleafapi.claoudleaf.RowMappers;

import com.cloudleafapi.claoudleaf.Entities.DeployStatus;
import com.cloudleafapi.claoudleaf.Entities.DeploymentEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DeploymentRowMapper implements RowMapper<DeploymentEntity> {
    @Override
    public DeploymentEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DeploymentEntity(
                UUID.fromString(rs.getString("deployment_id")),
                UUID.fromString(rs.getString("user_id")),
                UUID.fromString(rs.getString("repo_id")),

                rs.getString("ec2_instance_id"),
                rs.getString("asg_name"),
                rs.getString("elb_name"),
                rs.getString("security_group_id"),
                rs.getString("ec2_public_ip"),
                rs.getString("elb_public_ip"),
                DeployStatus.valueOf(rs.getString("status"))
        );
    }
}
