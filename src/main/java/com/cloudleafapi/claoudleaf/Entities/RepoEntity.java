package com.cloudleafapi.claoudleaf.Entities;

import java.util.UUID;

public record RepoEntity(
        UUID repoId,
        UUID userId,
        String repoName,
        String cloneUrl,
        String sshUrl
) {
    
}
