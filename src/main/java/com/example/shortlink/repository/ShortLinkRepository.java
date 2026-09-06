package com.example.shortlink.repository;
import java.util.Optional;

import com.example.shortlink.dto.ShortLinkResponse;

public interface ShortLinkRepository {
    public Optional<ShortLinkResponse> findByCode(String code);

    /**
     * Saves only when the code is still unused; returns false on a conflict.
     * Concrete repositories should override this atomically (the included
     * implementations do); this fallback only preserves the old interface for
     * simple adapters and is not safe as a concurrency primitive.
     */
    default boolean saveIfAbsent(ShortLinkResponse shortLinkResponse) {
        if (findByCode(shortLinkResponse.code()).isPresent()) {
            return false;
        }
        save(shortLinkResponse);
        return true;
    }

    public void save(ShortLinkResponse shortLinkResponse);
}
