package com.example.photocontestproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhotoContestProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoContestProjectApplication.class, args);
    }
}
