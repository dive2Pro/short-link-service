package com.example.shortlink.repository;


import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.shortlink.dto.ShortLinkResponse;

@Repository 
public class InMemoryShortLinkRepository implements ShortLinkRepository {
    private final ConcurrentHashMap<String, ShortLinkResponse> shortLinks = new ConcurrentHashMap<>();

    @Override
    public Optional<ShortLinkResponse> findByCode(String code) {
        return Optional.ofNullable(shortLinks.get(code));
    }

    @Override 
    public void save(ShortLinkResponse shortLinkResponse) {
        shortLinks.put(shortLinkResponse.code(), shortLinkResponse);
    }
}
