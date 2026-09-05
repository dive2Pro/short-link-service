package com.example.shortlink.dto;

public record ShortLinkResponse(String originalUrl, String code, String  shortUrl, String createdAt) {
    
}
