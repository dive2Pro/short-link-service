package com.example.shortlink.service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.shortlink.dto.ShortLinkResponse;

@Component
public class InMemoryLinkLookupCache implements LinkLookupCache {
    private final long CACHE_DURATION = 1 * 60 * 1000; // 1 minutes

    ConcurrentHashMap<String, CacheValue> cache = new ConcurrentHashMap<>();

    @Override
    public void put(String code, ShortLinkResponse response) {
        cache.put(code, new CacheValue(response, Instant.now()));
    }

    @Override
    public Optional<ShortLinkResponse> get(String code) {
        CacheValue cacheValue = cache.get(code);
        if (cacheValue != null) {
            if (Instant.now().isBefore(cacheValue.createdAt().plusMillis(CACHE_DURATION))) {
                return Optional.of(cacheValue.response());
            } else {
                cache.remove(code, cacheValue);
            }
        }
        return Optional.empty();
    }
   
}


