package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Entities.RepoEntity;
import com.cloudleafapi.claoudleaf.Requests.StatusRequest;
import com.cloudleafapi.claoudleaf.Services.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class StatusController {
    private final RepoService repoService;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Autowired
    public StatusController(RepoService repoService) {
        this.repoService = repoService;
    }

    @PostMapping("/status")
    public CompletableFuture<List<Object>> checkStatus(@RequestBody StatusRequest statusRequest) {
        System.out.println("IN CHECK STATUS: BEGINNING");
        CompletableFuture<List<Object>> completableFuture = new CompletableFuture<>();

        long startTime = System.currentTimeMillis();
        AtomicInteger retryCount = new AtomicInteger(0);

        final ScheduledFuture<?>[] scheduledFuture = new ScheduledFuture<?>[1];

        scheduledFuture[0] = executorService.scheduleAtFixedRate(() -> {
            if (retryCount.incrementAndGet() > 240) { // Limit to 240 retries i.e., around 20 minutes.
                completableFuture.completeExceptionally(new Exception("Exceeded maximum retry attempts"));
                throw new RuntimeException("Exceeded maximum retry attempts");
            }
            try {
                System.out.println("IN CHECK STATUS: MIDDLE");
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity("http://" + statusRequest.getIpAddressAndPort(), String.class);

                if (response.getStatusCode() == HttpStatusCode.valueOf(200)) {
                    long timeToConnect = System.currentTimeMillis() - startTime;
                    completableFuture.complete(Arrays.asList(statusRequest.getIpAddressAndPort(), timeToConnect));

                    Optional<RepoEntity> original = repoService.getRepoByName(statusRequest.getRepoName());
                    if (original.isEmpty()) {
                        throw new RuntimeException("The repo with this name: " + statusRequest.getRepoName() + " doesn't exist");
                    }

                    // Check the status of the original RepoEntity
                    if (!original.get().status().equals("Terminated")) {
                        RepoEntity updatedRepo = new RepoEntity(
                                original.get().repoId(),
                                original.get().userId(),
                                original.get().repoName(),
                                original.get().cloneUrl(),
                                original.get().sshUrl(),
                                original.get().ec2InstanceId(),
                                statusRequest.getIpAddressAndPort(),
                                "Deployed"
                        );
                        repoService.updateRepo(updatedRepo);
                    }

                    scheduledFuture[0].cancel(false); // Stop the task once the status code is 200
                }
            } catch (Exception ignored) {
                // If we can't connect, just ignore the exception and try again in 5 seconds
            }
        }, 0, 5, TimeUnit.SECONDS);

        System.out.println("IN CHECK STATUS: AFTER");

        return completableFuture;
    }


}

