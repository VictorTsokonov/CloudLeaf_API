package com.cloudleafapi.claoudleaf.Controllers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class StatusController {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @PostMapping("/status")
    public CompletableFuture<List<Object>> checkStatus(@RequestParam("ipAddressAndPort") String ipAddressAndPort) {
        CompletableFuture<List<Object>> completableFuture = new CompletableFuture<>();

        long startTime = System.currentTimeMillis();

        executorService.scheduleAtFixedRate(() -> {
            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity("http://" + ipAddressAndPort, String.class);

                if (response.getStatusCode() == HttpStatusCode.valueOf(200)) {
                    long timeToConnect = System.currentTimeMillis() - startTime;
                    completableFuture.complete(Arrays.asList(ipAddressAndPort, timeToConnect));
                }
            } catch (Exception ignored) {
                // If we can't connect, just ignore the exception and try again in 5 seconds
            }
        }, 0, 5, TimeUnit.SECONDS);

        return completableFuture;
    }
}

