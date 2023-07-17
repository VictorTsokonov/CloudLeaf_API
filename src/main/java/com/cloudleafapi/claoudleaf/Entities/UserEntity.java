package com.cloudleafapi.claoudleaf.Entities;

import java.util.UUID;

public record UserEntity(
        UUID user_id,
        String github_username,
        String github_access_token
) {
}
