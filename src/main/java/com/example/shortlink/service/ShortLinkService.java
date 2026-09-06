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

import io.micrometer.core.instrument.MeterRegistry;

@Service
public class ShortLinkService {
    private static final int MAX_GENERATION_ATTEMPTS = 5;
    private final ShortLinkRepository shortLinkRepository;
    private final ShortCodeGenerate shortCodeGenerate;
    private final MeterRegistry meterRegistry;

    private final LinkLookupCache linkLookupCache;

    @Autowired
    public ShortLinkService(
                ShortLinkRepository shortLinkRepository,
                ShortCodeGenerate shortCodeGenerate,
                MeterRegistry meterRegistry,
                LinkLookupCache linkLookupCache) {
        this.shortLinkRepository = shortLinkRepository;
        this.shortCodeGenerate = shortCodeGenerate;
        this.meterRegistry = meterRegistry;
        this.linkLookupCache = linkLookupCache;
    }

    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        if (request.code() != null && !request.code().isBlank()) {
            ShortLinkResponse response = buildResponse(request.originalUrl(), request.code());
            if (!shortLinkRepository.saveIfAbsent(response)) {

                meterRegistry.counter("shortlink.custom_code.conflicts").increment();
                throw new LinkCodeAlreadyExistsException(": " + request.code());
            }

            meterRegistry.counter("shortlink.created", "type", "custom").increment();
            linkLookupCache.put(request.code(), response);
            return response;
        }

        // Random collisions are unlikely, but the database remains the authority.
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            ShortLinkResponse response = buildResponse(request.originalUrl(), shortCodeGenerate.generateShortCode());
            if (shortLinkRepository.saveIfAbsent(response)) {
                meterRegistry.counter("shortlink.created", "type", "generated").increment();
                linkLookupCache.put(response.code(), response);
                return response;
            }
            meterRegistry.counter("shortlink.code_collisions").increment(); 
        }
        meterRegistry.counter("shortlink.code_generation_exhausted").increment();
        throw new ShortCodeGenerationException("Could not allocate a unique short code");
    }

    public Optional<ShortLinkResponse> findByCode(String code) {
        Optional<ShortLinkResponse> cachedResponse = linkLookupCache.get(code);
        if (cachedResponse.isPresent()) {
            return cachedResponse;
        }
        Optional<ShortLinkResponse> response = shortLinkRepository.findByCode(code);
        response.ifPresent(found -> linkLookupCache.put(code, found));
        return response;
    }

    private ShortLinkResponse buildResponse(String originalUrl, String code) {
        return new ShortLinkResponse(originalUrl,
                code,
                "localhost:8080/" + code,
                Instant.now().toString());
    }
}
