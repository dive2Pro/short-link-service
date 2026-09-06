package com.example.shortlink.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.exception.LinkNotFoundException;
import com.example.shortlink.service.ShortLinkService;

import jakarta.validation.Valid;

@RestController
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    public ShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @PostMapping ("/api/links")
    public ShortLinkResponse createShortLink(@Valid @RequestBody CreateShortLinkRequest request) {
        return shortLinkService.createShortLink(request);
    }
    
    @GetMapping ("/{code}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String code) {
        Optional<ShortLinkResponse> shortLinkResponse = shortLinkService.findByCode(code);
        System.out.println("shortLinkResponse: " + shortLinkResponse);
        if (!shortLinkResponse.isPresent()) {
            // TODO metrics: increment shortlink.redirect.not_found here.
            throw new LinkNotFoundException("Code not found");
        }
        String originalUrl = shortLinkResponse.get().originalUrl();
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
        
    }
}
