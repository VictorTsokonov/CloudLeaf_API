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
                                 @RequestParam String sshUrl
    ) {
        return repoService.createRepo(userID, repoName, cloneUrl, sshUrl);
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


    // Additional methods for list, update and delete if needed
}
