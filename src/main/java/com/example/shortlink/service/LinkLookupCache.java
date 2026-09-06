package com.example.shortlink.service;

import java.time.Instant;
import java.util.Optional;
import com.example.shortlink.dto.ShortLinkResponse;
 

record CacheValue(ShortLinkResponse response, Instant createdAt) {
    public CacheValue(ShortLinkResponse response, Instant createdAt) {
        this.response = response;
        this.createdAt = createdAt;
    }
}


public interface LinkLookupCache {
    void put(String code, ShortLinkResponse response);
    Optional<ShortLinkResponse> get(String code);  
}


