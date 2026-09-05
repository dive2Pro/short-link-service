package com.example.shortlink.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.repository.ShortLinkRepository;

@Service
public class ShortLinkService {
    private final ShortLinkRepository shortLinkRepository;

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        if (shortLinkRepository.findByCode(request.code()).isPresent()) {
            return shortLinkRepository.findByCode(request.code()).get();
        }
        ShortLinkResponse response = new ShortLinkResponse(request.originalUrl(), request.code(),
                "http://short.link/" + request.code(), Instant.now().toString());
        shortLinkRepository.save(response);
        return response;
    }
}
