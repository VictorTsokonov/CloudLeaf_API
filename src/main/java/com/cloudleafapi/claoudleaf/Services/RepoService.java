package com.cloudleafapi.claoudleaf.Services;

import com.cloudleafapi.claoudleaf.Entities.RepoEntity;
import com.cloudleafapi.claoudleaf.Repositories.RepoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RepoService {

	private final RepoRepository repoRepository;

	@Autowired
	public RepoService(RepoRepository repoRepository) {
		this.repoRepository = repoRepository;
	}

	public RepoEntity createRepo(String userID, String repoName, String cloneUrl,
			String sshUrl, String status) {
		Optional<RepoEntity> optionalRepoEntity = repoRepository
				.findRepoByUserIdAndRepoName(userID, repoName);

		if (optionalRepoEntity.isPresent()) {
			RepoEntity currentRepo = optionalRepoEntity.get();

			if (!currentRepo.cloneUrl().equals(cloneUrl)
					|| !currentRepo.sshUrl().equals(sshUrl)) {
				// update repo if urls have changed or status has changed
				RepoEntity updatedRepo = new RepoEntity(currentRepo.repoId(),
						UUID.fromString(userID), repoName, cloneUrl, sshUrl, null, null,
						status);
				return repoRepository.updateRepo(updatedRepo);
			}

			// if urls are same and status is the same, return existing repo
			return currentRepo;
		}
		else {
			// if repo does not exist, create new one
			return repoRepository.createRepo(userID, repoName, cloneUrl, sshUrl, status);
		}
	}

	public Optional<RepoEntity> getRepo(UUID repoID) {
		return repoRepository.getRepo(repoID);
	}

	public List<RepoEntity> listRepoByUserId(UUID userID) {
		return repoRepository.listRepoByUserId(userID);
	}

	public void deleteRepo(UUID repoId) {
		repoRepository.deleteRepo(repoId);
	}

	public RepoEntity updateRepo(RepoEntity repoEntity) {
		return repoRepository.updateRepo(repoEntity);
	}

	public Optional<RepoEntity> getRepoByName(String repoName) {
		return repoRepository.getRepoByName(repoName);
	}

	public List<RepoEntity> listReposByUserName(String userName) {
		return repoRepository.listReposByUserName(userName);
	}

	public void updateRepoStatusByRepoName(String repoName, String status) {
		repoRepository.updateRepoStatusByRepoName(repoName, status);
	}

	public void updateRepoIpAddressByRepoName(String repoName, String ipAddress) {
		repoRepository.updateRepoIpAddressByRepoName(repoName, ipAddress);
	}

}
