package com.cloudleafapi.claoudleaf.Services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AWSDatabaseService {

	@Value("${aws.accessKeyId}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String secretKey;

	private AmazonRDS rdsClient;

	private final AWSSecretsManagerService ssmService;

	@Autowired
	public AWSDatabaseService(AWSSecretsManagerService ssmService) {
		this.ssmService = ssmService;
	}

	@PostConstruct
	public void initialize() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,
				secretKey);
		this.rdsClient = AmazonRDSClient.builder().withRegion(Regions.EU_CENTRAL_1)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.build();
	}

	public Map<String, String> createDatabase(String githubName, String databaseName, String username, String password, String type) {
        System.out.println("/" + githubName + "/" + databaseName);
        String engine;
        int port = switch (type) {
            case "PostgreSQL" -> {
                engine = "postgres";
                yield 5432;
            }
            case "Redis" -> throw new IllegalArgumentException("Redis is not supported as an RDS engine.");
            case "MySQL" -> {
                engine = "mysql";
                yield 3306;
            }
            default -> throw new IllegalArgumentException("Invalid database type: " + type);
        };

        CreateDBInstanceRequest request = new CreateDBInstanceRequest()
                .withDBInstanceIdentifier(githubName + databaseName)
                .withDBName(databaseName)
                .withDBInstanceClass("db.m5.large")
                .withEngine(engine)
                .withAllocatedStorage(20)
                .withMasterUsername(username)
                .withMasterUserPassword(password)
                .withVpcSecurityGroupIds("sg-0f8b02788f4b233a7")
                .withAvailabilityZone("eu-central-1a")
                .withBackupRetentionPeriod(30)
                .withMultiAZ(false)
                .withPort(port)
                .withDBSubnetGroupName("default-vpc-0d4768750d53c1621")
                .withPubliclyAccessible(true)
                .withStorageEncrypted(true)
                .withDeletionProtection(false);

        rdsClient.createDBInstance(request);

        DescribeDBInstancesRequest describeDBInstancesRequest = new DescribeDBInstancesRequest()
                .withDBInstanceIdentifier(githubName + databaseName);

        System.out.println("Before while loop");

        boolean isAvailable = false;
        while (!isAvailable) {
            try {
                Thread.sleep(10000); // Wait 10 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DescribeDBInstancesResult describeDBInstancesResult = rdsClient.describeDBInstances(describeDBInstancesRequest);
            String status = describeDBInstancesResult.getDBInstances().get(0).getDBInstanceStatus();
            System.out.println("DB Instance Status: " + status); // Debugging line
            isAvailable = "available".equalsIgnoreCase(status);
        }
        System.out.println("After while loop");

        DescribeDBInstancesResult describeDBInstancesResult = rdsClient.describeDBInstances(describeDBInstancesRequest);
        DBInstance dbInstance = describeDBInstancesResult.getDBInstances().get(0);

        String connectionUrl = dbInstance.getEndpoint().getAddress();

        Map<String, String> result = new HashMap<>();
        result.put("databaseName", databaseName);
        result.put("username", username);
        result.put("connectionUrl", connectionUrl);
        result.put("port", String.valueOf(port));

        System.out.println(result);

        ssmService.createParams(
                githubName,
                databaseName,
                connectionUrl,
                username,
                Integer.toString(port)
        );

        return result;
    }

	public String deleteDatabase(String githubName, String databaseName) {
		String dbInstanceIdentifier = githubName + databaseName;
		System.out.println("Deleting " + githubName + "/" + databaseName);

		DeleteDBInstanceRequest request = new DeleteDBInstanceRequest()
				.withDBInstanceIdentifier(dbInstanceIdentifier)
				.withSkipFinalSnapshot(true); // Decide if you want to skip or take a
												// final snapshot

		rdsClient.deleteDBInstance(request);
		ssmService.deleteParams(githubName, databaseName);
		return "Database deleted successfully";
	}

}