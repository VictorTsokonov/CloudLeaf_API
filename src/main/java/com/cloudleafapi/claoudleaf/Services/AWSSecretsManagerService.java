package com.cloudleafapi.claoudleaf.Services;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterRequest;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AWSSecretsManagerService {

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    private AWSSimpleSystemsManagement ssmClient;
    private final Gson gson = new Gson();

    @PostConstruct
    public void initializeClient() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        ssmClient = AWSSimpleSystemsManagementClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public void createParams(String githubName, String databaseName, String connectionUrl, String username, String port) {
        String paramName = "/" + githubName + "/" + databaseName;
        Map<String, String> paramsMap = Map.of(
                "connectionUrl", connectionUrl,
                "databaseName", databaseName,
                "username", username,
                "port", port
        );
        String paramValue = gson.toJson(paramsMap);

        PutParameterRequest request = new PutParameterRequest()
                .withName(paramName)
                .withValue(paramValue)
                .withType("String");

        System.out.println(paramName + paramValue);

        ssmClient.putParameter(request);
    }
}