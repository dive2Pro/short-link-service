package com.example.shortlink.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.service.ShortLinkService;

@RestController
@RequestMapping ("/api")
public class ShortLinkController {
    private final ShortLinkService shortLinkService;
    
    public ShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @PostMapping ("/links")
    public ShortLinkResponse createShortLink(@RequestBody CreateShortLinkRequest request) {
        return shortLinkService.createShortLink(request);
    }
}
