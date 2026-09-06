package com.example.shortlink.service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.shortlink.dto.ShortLinkResponse;

@Component
public class InMemoryLinkLookupCache implements LinkLookupCache {
    private static final long CACHE_DURATION_MILLIS = 5 * 60 * 1000L;

    private final ConcurrentHashMap<String, CacheValue> cache = new ConcurrentHashMap<>();

    @Override
    public void put(String code, ShortLinkResponse response) {
        Instant now = Instant.now();
        cache.entrySet().removeIf(entry -> !now.isBefore(
                entry.getValue().createdAt().plusMillis(CACHE_DURATION_MILLIS)));
        cache.put(code, new CacheValue(response, now));
    }

    @Override
    public Optional<ShortLinkResponse> get(String code) {
        CacheValue cacheValue = cache.get(code);
        if (cacheValue != null) {
            if (Instant.now().isBefore(cacheValue.createdAt().plusMillis(CACHE_DURATION_MILLIS))) {
                return Optional.of(cacheValue.response());
            } else {
                cache.remove(code, cacheValue);
            }
        }
        return Optional.empty();
    }
   
}
