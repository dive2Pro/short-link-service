package com.example.shortlink.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;

@Service 
public class ShortLinkService {

    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        
        return new ShortLinkResponse(request.originalUrl(), request.code(), "http://short.link/" + request.code(), Instant.now().toString());
    }
    
}
