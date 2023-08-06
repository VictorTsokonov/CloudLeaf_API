package com.cloudleafapi.claoudleaf.Requests;

public class StatusRequest {

	private final String ipAddressAndPort;

	private final String repoName;

	public StatusRequest(String repoName, String ipAddressAndPort) {
		this.repoName = repoName;
		this.ipAddressAndPort = ipAddressAndPort;
	}

	public String getIpAddressAndPort() {
		return ipAddressAndPort;
	}

	public String getRepoName() {
		return repoName;
	}

	// getters and setters

}