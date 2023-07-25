package com.cloudleafapi.claoudleaf.RowMappers;

import com.cloudleafapi.claoudleaf.Entities.RepoEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RepoRowMapper implements RowMapper<RepoEntity> {
    @Override
    public RepoEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RepoEntity(
                UUID.fromString(rs.getString("repo_id")),
                UUID.fromString(rs.getString("user_id")),
                rs.getString("repo_name"),
                rs.getString("clone_url"),
                rs.getString("ssh_url"),
                rs.getString("ec2_instance_id"),
                rs.getString("ec2_public_ip"),
                rs.getString("status")
        );
    }
}
