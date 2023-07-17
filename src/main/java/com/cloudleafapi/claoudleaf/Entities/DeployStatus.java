package com.cloudleafapi.claoudleaf.Entities;

public enum DeployStatus {
    DEPLOYED("Deployed"),
    DEPLOYING("Deploying..."),
    NOT_DEPLOYED("Not Deployed");

    private final String status;

    DeployStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
