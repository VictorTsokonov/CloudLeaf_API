package com.cloudleafapi.claoudleaf.Controllers;

import com.cloudleafapi.claoudleaf.Services.AWSDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AWSDatabase {

    private final AWSDatabaseService awsDatabaseService;

    @Autowired
    public AWSDatabase(AWSDatabaseService awsDatabaseService) {
        this.awsDatabaseService = awsDatabaseService;
    }

    @PostMapping("/createDatabase")
    public ResponseEntity<Map<String, String>> createDatabase(@RequestBody Map<String, String> payload) {
        try {
            String githubName = payload.get("githubName");
            String databaseName = payload.get("databaseName");
            String username = payload.get("username");
            String password = payload.get("password");
            String type = payload.get("type");

            // Validating the input
            if (databaseName == null || username == null || password == null || type == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please fill out all the fields."));
            }

            // Creating the database using the service
            String responseMessage = awsDatabaseService.createDatabase(githubName, databaseName, username, password, type).toString();

            return ResponseEntity.ok(Map.of("message", responseMessage));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to create database. Please try again."));
        }
    }
}
