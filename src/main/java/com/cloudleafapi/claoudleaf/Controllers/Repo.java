package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Entities.RepoEntity;
import com.cloudleafapi.claoudleaf.Services.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/repos")
public class Repo {

    private final RepoService repoService;

    @Autowired
    public Repo(RepoService repoService) {
        this.repoService = repoService;
    }

    @PostMapping
    public RepoEntity createRepo(@RequestParam String userID,
                                 @RequestParam String repoName,
                                 @RequestParam String cloneUrl,
                                 @RequestParam String sshUrl,
                                 @RequestParam(required = false) String status
    ) {
        return repoService.createRepo(userID, repoName, cloneUrl, sshUrl, status);
    }

    @GetMapping("/{id}")
    public RepoEntity getRepo(@PathVariable UUID id) {
        return repoService.getRepo(id).orElseThrow(() -> new IllegalArgumentException("Repository not found"));
    }

    @GetMapping("/name")
    public RepoEntity getRepoByName(@RequestHeader("repoName") String name) {
        return repoService.getRepoByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found"));
    }

    @GetMapping("/user/{username}")
    public List<RepoEntity> listReposByUserName(@PathVariable String username) {
        return repoService.listReposByUserName(username);
    }

    @PutMapping("/status")
    public void updateRepoStatusByRepoName(@RequestParam String repoName, @RequestParam String status) {
        repoService.updateRepoStatusByRepoName(repoName, status);
    }

    @PutMapping("/ipaddress")
    public void updateRepoIpAddressByRepoName(@RequestParam String repoName, @RequestParam String ipAddress) {
        repoService.updateRepoIpAddressByRepoName(repoName, ipAddress);
    }

    @PutMapping("/ipAndStatus")
    public void updateRepoIpAndStatusByRepoName(@RequestParam String repoName, @RequestParam String ipAddress, @RequestParam String status) {
        repoService.updateRepoIpAddressByRepoName(repoName, ipAddress);
        repoService.updateRepoStatusByRepoName(repoName, status);
    }


    // Additional methods for list, update and delete if needed
}
