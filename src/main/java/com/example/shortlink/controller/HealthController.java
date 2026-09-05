package com.example.shortlink.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping ("/api")
public class HealthController {

    public final class HealthResponse {
        private final String status;

        public HealthResponse(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("OK, I'm ok!");
    }

}
