package com.cloudleafapi.claoudleaf.postgresql;

import com.cloudleafapi.claoudleaf.Entities.UserEntity;
import com.cloudleafapi.claoudleaf.Repositories.UserRepository;
import com.cloudleafapi.claoudleaf.RowMappers.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate txTemplate;

    @Autowired
    public PostgresUserRepository(JdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }

    @Override
    public UserEntity createUser(String github_username, String github_access_token) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO users(user_id, github_username, github_access_token) VALUES(?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                UUID userId = UUID.randomUUID();
                ps.setObject(1, userId);
                ps.setString(2, github_username);
                ps.setString(3, github_access_token);
                return ps;
            }, keyHolder);
            UUID userId = (UUID) Objects.requireNonNull(keyHolder.getKeys()).get("user_id");
            return new UserEntity(
                    userId,
                    github_username,
                    github_access_token
            );
        });
    }


    @Override
    public Optional<UserEntity> getUser(UUID user_id) {
        String sql = """
                SELECT * FROM users WHERE user_id = ?
                """;
        return Optional.ofNullable(jdbcTemplate
                .queryForObject(sql, new UserRowMapper(), user_id));
    }

    @Override
    public Optional<UserEntity> findUserByGithubUsername(String username) {
        String sql = "SELECT * FROM users WHERE github_username = ?";
        List<UserEntity> users = jdbcTemplate.query(sql, new UserRowMapper(), username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public List<UserEntity> listUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public void deleteUser(UUID userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        String sql = "UPDATE users SET github_username = ?, github_access_token = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, userEntity.github_username(), userEntity.github_access_token(), userEntity.user_id());
        return getUser(userEntity.user_id()).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
