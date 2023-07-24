package com.cloudleafapi.claoudleaf.postgresql;

import com.cloudleafapi.claoudleaf.Entities.RepoEntity;
import com.cloudleafapi.claoudleaf.Repositories.RepoRepository;
import com.cloudleafapi.claoudleaf.RowMappers.RepoRowMapper;
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
public class PostgresRepoRepository implements RepoRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate txTemplate;

    @Autowired
    public PostgresRepoRepository(JdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }

    @Override
    public RepoEntity createRepo(String userID, String repoName, String cloneUrl, String sshUrl) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO repos(repo_id, user_id, repo_name, clone_url, ssh_url) VALUES(?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                UUID repoId = UUID.randomUUID();
                ps.setObject(1, repoId);
                ps.setObject(2, UUID.fromString(userID));
                ps.setString(3, repoName);
                ps.setString(4, cloneUrl);
                ps.setString(5, sshUrl);
                return ps;
            }, keyHolder);
            UUID repoId = (UUID) Objects.requireNonNull(keyHolder.getKeys()).get("repo_id");
            return getRepo(repoId).orElseThrow(() -> new RuntimeException("Repo not found"));
        });
    }


    @Override
    public Optional<RepoEntity> getRepo(UUID repoID) {
        String sql = "SELECT * FROM repos WHERE repo_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new RepoRowMapper(), repoID));
    }

    @Override
    public List<RepoEntity> listRepoByUserId(UUID userID) {
        String sql = "SELECT * FROM repos WHERE user_id = ?";
        return jdbcTemplate.query(sql, new RepoRowMapper(), userID);
    }

    @Override
    public void deleteRepo(UUID repoId) {
        String sql = "DELETE FROM repos WHERE repo_id = ?";
        jdbcTemplate.update(sql, repoId);
    }

    @Override
    public RepoEntity updateRepo(RepoEntity repoEntity) {
        String sql = "UPDATE repos SET user_id = ?, repo_name = ?, clone_url = ?, ssh_url = ? WHERE repo_id = ?";
        jdbcTemplate.update(sql,
                UUID.fromString(String.valueOf(repoEntity.userId())),
                repoEntity.repoName(),
                repoEntity.cloneUrl(),
                repoEntity.sshUrl(),
                UUID.fromString(String.valueOf(repoEntity.repoId())));
        return getRepo(UUID.fromString(String.valueOf(repoEntity.repoId()))).orElseThrow(() -> new RuntimeException("Repo not found"));
    }

    public Optional<RepoEntity> findRepoByUserIdAndRepoName(String userId, String repoName) {
        String sql = "SELECT * FROM repos WHERE user_id = ? AND repo_name = ?";
        UUID uuid = UUID.fromString(userId);

        List<RepoEntity> repos = jdbcTemplate.query(sql, new RepoRowMapper(), uuid, repoName);

        return repos.isEmpty() ? Optional.empty() : Optional.ofNullable(repos.get(0));
    }

    @Override
    public Optional<RepoEntity> getRepoByName(String repoName) {
        String sql = "SELECT * FROM repos WHERE repo_name = ?";
        List<RepoEntity> repos = jdbcTemplate.query(sql, new RepoRowMapper(), repoName);
        return repos.isEmpty() ? Optional.empty() : Optional.ofNullable(repos.get(0));
    }

    @Override
    public List<RepoEntity> listReposByUserName(String userName) {
        String sql = "SELECT * FROM repos JOIN users ON repos.user_id = users.user_id WHERE users.github_username = ?";
        return jdbcTemplate.query(sql, new RepoRowMapper(), userName);
    }


}
