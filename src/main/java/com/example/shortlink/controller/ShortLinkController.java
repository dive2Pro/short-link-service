package com.example.shortlink.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;

@RestController
@RequestMapping ("/api")
public class ShortLinkController {
     

    @PostMapping ("/links")
    public ShortLinkResponse createShortLink(@RequestBody CreateShortLinkRequest request) {
        return new ShortLinkResponse(request.originalUrl(), request.code(), "http://localhost:8080/" + request.code(), java.time.LocalDateTime.now().toString());
    }
}
