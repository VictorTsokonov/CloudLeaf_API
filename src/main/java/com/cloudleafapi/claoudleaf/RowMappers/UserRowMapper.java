package com.cloudleafapi.claoudleaf.RowMappers;

import com.cloudleafapi.claoudleaf.Entities.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRowMapper implements RowMapper<UserEntity> {

	@Override
	public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new UserEntity(UUID.fromString(rs.getString("user_id")),
				rs.getString("github_username"), rs.getString("github_access_token"));
	}

}
