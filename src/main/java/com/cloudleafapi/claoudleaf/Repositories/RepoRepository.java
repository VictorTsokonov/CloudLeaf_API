package com.cloudleafapi.claoudleaf.Repositories;

import com.cloudleafapi.claoudleaf.Entities.RepoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepoRepository {
    RepoEntity createRepo(String userID, String repoName, String cloneUrl, String sshUrl, String status);

    Optional<RepoEntity> getRepo(UUID repoID);

    List<RepoEntity> listRepoByUserId(UUID userID);

    void deleteRepo(UUID repoId);

    RepoEntity updateRepo(RepoEntity repoEntity);

    Optional<RepoEntity> findRepoByUserIdAndRepoName(String userId, String repoName);

    Optional<RepoEntity> getRepoByName(String repoName);

    List<RepoEntity> listReposByUserName(String userName);

    void updateRepoStatusByRepoName(String repoName, String status);

    void updateRepoIpAddressByRepoName(String repoName, String ipAddress);
}


