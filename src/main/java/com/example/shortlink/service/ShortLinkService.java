package com.example.shortlink.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.exception.LinkCodeAlreadyExistsException;
import com.example.shortlink.exception.ShortCodeGenerationException;
import com.example.shortlink.repository.ShortLinkRepository;

@Service
public class ShortLinkService {
    private static final int MAX_GENERATION_ATTEMPTS = 5;
    private final ShortLinkRepository shortLinkRepository;
    private final ShortCodeGenerate shortCodeGenerate;

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this(shortLinkRepository, new ShortCodeGenerate());
    }

    @Autowired
    public ShortLinkService(ShortLinkRepository shortLinkRepository, ShortCodeGenerate shortCodeGenerate) {
        this.shortLinkRepository = shortLinkRepository;
        this.shortCodeGenerate = shortCodeGenerate;
    }

    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        if (request.code() != null && !request.code().isBlank()) {
            ShortLinkResponse response = buildResponse(request.originalUrl(), request.code());
            if (!shortLinkRepository.saveIfAbsent(response)) {
                throw new LinkCodeAlreadyExistsException(": " + request.code());
            }
            return response;
        }

        // Random collisions are unlikely, but the database remains the authority.
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            ShortLinkResponse response = buildResponse(request.originalUrl(), shortCodeGenerate.generateShortCode());
            if (shortLinkRepository.saveIfAbsent(response)) {
                return response;
            }
        }
        throw new ShortCodeGenerationException("Could not allocate a unique short code");
    }

    public Optional<ShortLinkResponse> findByCode(String code) {
        return shortLinkRepository.findByCode(code);
    }

    private ShortLinkResponse buildResponse(String originalUrl, String code) {
        return new ShortLinkResponse(originalUrl,
                code,
                "localhost:8080/" + code,
                Instant.now().toString());
    }
}
