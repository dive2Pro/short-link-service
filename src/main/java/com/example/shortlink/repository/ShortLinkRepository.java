package com.example.shortlink.repository;
import java.util.Optional;

import com.example.shortlink.dto.ShortLinkResponse;

public interface ShortLinkRepository {
    public Optional<ShortLinkResponse> findByCode(String code);
    public void save(ShortLinkResponse shortLinkResponse);
}
