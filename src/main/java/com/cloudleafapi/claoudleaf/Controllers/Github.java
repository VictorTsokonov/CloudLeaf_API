package com.cloudleafapi.claoudleaf.Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class Github {

    private final WebClient webClient;

    @Value("${github.clientID}")
    private String CLIENT_ID;

    @Value("${github.clientSecret}")
    private String CLIENT_SECRET;

    public Github() {
        this.webClient = WebClient.builder().build();
    }

    @GetMapping("/getAccessToken")
    public Mono<String> getAccessToken(@RequestParam String code) {
        String params = "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
                + "&code=" + code;
        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("https://github.com/login/oauth/access_token" + params)
                .header("Accept", "application/json").retrieve();

        return responseSpec.bodyToMono(String.class).doOnNext(System.out::println);
    }

    @GetMapping("/getUserData")
    public Mono<String> getUserData(
            @RequestHeader("Authorization") String authorization) {
        WebClient.ResponseSpec responseSpec = webClient.get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, authorization).retrieve();

        return responseSpec.bodyToMono(String.class).doOnNext(System.out::println); // Logs
        // the
        // response
    }


}