package com.cloudleafapi.claoudleaf.Services;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Base64;
import java.util.List;

@Service
public class AWSDeployService {

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    private Ec2Client ec2;

    @PostConstruct
    public void init() {
        this.ec2 = Ec2Client.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public List<String> deployRepo(String name, String clone_url, String ssh_url, String port, List<String> dependencies) {
        // we will need to take:
        // the port - YES
        // the language or framework - YES
        // then we create a dependencies HashMap full of bash scripts for installation
        String userData = // This here will need work
                "#!/bin/bash\n" +
                        "sudo apt-get update -y\n" +

                        "wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add -\n" +
                        "sudo add-apt-repository 'deb https://apt.corretto.aws stable main' -y\n" +
                        "sudo apt-get update; sudo apt-get install -y java-17-amazon-corretto-jdk\n" +

//                        "curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -\n" +
//                        "sudo apt-get install -y nodejs\n" +

                        "sudo apt-get install -y git\n" +
                        "git clone " + clone_url + "\n" +

                        "cd " + name + "\n" +
                        "chmod 777 run_script.sh\n" +
                        "./run_script.sh\n";

//        Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
//        ami-04e601abe3e1a910f (64-bit (x86)) / ami-0329d3839379bfd15 (64-bit (Arm))

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId("ami-0329d3839379bfd15")  // replace with your AMI ID --> Ubuntu
                .instanceType(InstanceType.A1_MEDIUM)
                .maxCount(1)
                .minCount(1)
                .userData(Base64.getEncoder().encodeToString(userData.getBytes()))
                .keyName("CloudLeafKey")  // replace with your key pair name
                .securityGroupIds("sg-044b705a44c5f27b4")  // replace with your security group ID --> PublicApps
                .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);

        String instanceId = response.instances().get(0).instanceId();
        Tag tag = Tag.builder()
                .key(name)
                .value(instanceId)
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();

        try {
            ec2.createTags(tagRequest);
            System.out.printf(
                    "Successfully started EC2 instance %s based on AMI %s",
                    instanceId, "ami-0329d3839379bfd15");
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done!");

        // Create a DescribeInstancesRequest
        DescribeInstancesRequest describeRequest = DescribeInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        // Initialize instance to null before the loop
        Instance instance;

        // Continue polling the instance state until it becomes running
        while (true) {
            // Describe the instance
            DescribeInstancesResponse describeInstancesResponse = ec2.describeInstances(describeRequest);

            instance = describeInstancesResponse.reservations().get(0).instances().get(0);
            InstanceState instanceState = instance.state();

            if (instanceState.name().equals(InstanceStateName.RUNNING)) {
                break;
            }

            try {
                // sleep for 10 seconds before the next request
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                // handle the exception
            }
        }

        // Return the instance's public IP + port 8080 in our case
        return List.of(instance.publicIpAddress() + ":" + port, instanceId); // DYNAMIC PORT TO CHANGE

    }

    public void terminateInstance(String instanceId) {
        // Create a request to terminate the instance
        TerminateInstancesRequest terminateRequest = TerminateInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        try {
            // Attempt to terminate the instance
            TerminateInstancesResponse terminateResponse = ec2.terminateInstances(terminateRequest);

            // Check the response to make sure the instance was successfully terminated
            InstanceStateChange stateChange = terminateResponse.terminatingInstances().get(0);
            if (!stateChange.currentState().name().equals(InstanceStateName.TERMINATED)
                    && !stateChange.currentState().name().equals(InstanceStateName.SHUTTING_DOWN)) {
                throw new RuntimeException("Failed to terminate instance " + instanceId);
            }
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error terminating instance " + instanceId, e);
        }

        System.out.println("Successfully requested termination of instance " + instanceId);
    }


}
