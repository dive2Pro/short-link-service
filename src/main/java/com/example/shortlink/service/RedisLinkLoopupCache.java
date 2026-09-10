package com.example.shortlink.service;

import java.time.Duration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.example.shortlink.dto.ShortLinkResponse;

import io.micrometer.core.instrument.MeterRegistry;
import tools.jackson.databind.ObjectMapper;

@Component 
public class RedisLinkLoopupCache implements LinkLookupCache {

  private static final Logger log = LoggerFactory.getLogger(RedisLinkLoopupCache.class);
    @Autowired
    private final MeterRegistry meterRegistry;
    @Autowired
    private final StringRedisTemplate redis;

    @Autowired
    private final ObjectMapper objectMapper;

    private final long ttlSeconds;

    public RedisLinkLoopupCache(
                MeterRegistry meterRegistry,
                StringRedisTemplate redis,
                ObjectMapper mapper,
            @Value("${shortlink.cache.ttl-seconds}") long ttlSeconds) {
        this.meterRegistry = meterRegistry;
        this.objectMapper = mapper;
        this.redis = redis;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public void put(String code, ShortLinkResponse response) {
        String json = objectMapper.writeValueAsString(response);
        try {
            redis.opsForValue().set("shortlink.cache:" + code, json, Duration.ofSeconds(ttlSeconds));
        } catch (RedisConnectionFailureException e) {
            meterRegistry.counter("shortlink.cache.degraded").increment();
            log.warn("cache unavailable, falling back to database: code={}",
  code, e);
        }
    }

    @Override
    public Optional<ShortLinkResponse> get(String code) {
        try {
            String jsonString = redis.opsForValue().get("shortlink.cache:" + code);
        if (jsonString == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.convertValue(jsonString, ShortLinkResponse.class));
    } catch (RedisConnectionFailureException e) {
            meterRegistry.counter("shortlink.cache.degraded").increment();
                      log.warn("cache unavailable, falling back to database: code={}",
  code, e);
        return Optional.empty();
        }
    }
    
}
