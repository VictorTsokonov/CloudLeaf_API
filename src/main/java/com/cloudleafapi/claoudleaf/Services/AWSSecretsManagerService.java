package com.cloudleafapi.claoudleaf.Services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.*;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AWSSecretsManagerService {

	private AWSSimpleSystemsManagement ssmClient;

	private final Gson gson = new Gson();

	@PostConstruct
	public void initializeClient() {
		ssmClient = AWSSimpleSystemsManagementClientBuilder.standard()
				.withRegion(Regions.EU_CENTRAL_1)
				.build();
	}

	public void createParams(String githubName, String databaseName, String connectionUrl,
			String username, String port) {
		System.out.println("In create params");
		String paramName = "/" + githubName + "/" + databaseName;
		Map<String, String> paramsMap = Map.of("connectionUrl", connectionUrl,
				"databaseName", databaseName, "username", username, "port", port);
		String paramValue = gson.toJson(paramsMap);

		PutParameterRequest request = new PutParameterRequest().withName(paramName)
				.withValue(paramValue).withType("String");

		System.out.println(paramName + paramValue);

		ssmClient.putParameter(request);
	}

	public void deleteParams(String githubName, String databaseName) {
		System.out.println("In delete params");
		String paramName = "/" + githubName + "/" + databaseName;

		DeleteParameterRequest request = new DeleteParameterRequest().withName(paramName);

		System.out.println("Deleting parameter: " + paramName);

		ssmClient.deleteParameter(request);
	}

	public List<Object> getParametersByGithubName(String githubName) {
		String path = "/" + githubName + "/";

		GetParametersByPathRequest request = new GetParametersByPathRequest()
				.withPath(path).withRecursive(true).withWithDecryption(true);

		GetParametersByPathResult result = ssmClient.getParametersByPath(request);

		return result.getParameters().stream().map(Parameter::getValue)
				.map(jsonString -> new Gson().fromJson(jsonString, Object.class))
				.collect(Collectors.toList());
	}

}