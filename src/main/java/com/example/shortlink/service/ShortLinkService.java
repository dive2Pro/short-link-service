package com.example.shortlink.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.exception.LinkCodeAlreadyExistsException;
import com.example.shortlink.repository.ShortLinkRepository;

@Service
public class ShortLinkService {
    private final ShortLinkRepository shortLinkRepository;

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        if (shortLinkRepository.findByCode(request.code()).isPresent()) {
            throw new LinkCodeAlreadyExistsException("Code already exists");
        }
        String code = nextCode();
        ShortLinkResponse response = new ShortLinkResponse(request.originalUrl(), 
                code,
                "localhost:8080/" + code,              
                Instant.now().toString());
        shortLinkRepository.save(response);
        return response;
    }

    public Optional<ShortLinkResponse> findByCode(String code) {
        return shortLinkRepository.findByCode(code);
    }


    private long sequence = 0;

    public String nextCode() {
        sequence++;
        return String.valueOf(sequence);
    }
}


