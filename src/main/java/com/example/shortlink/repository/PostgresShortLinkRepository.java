package com.example.shortlink.repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.model.ShortLinkEntity;

@Repository
@Profile ("postgres")
public class PostgresShortLinkRepository implements ShortLinkRepository {
    
    private final ShortLinkJpaRepository shortLinkJpaRepository;
    
    public PostgresShortLinkRepository(ShortLinkJpaRepository shortLinkJpaRepository) {
        this.shortLinkJpaRepository = shortLinkJpaRepository;
    }

    @Override
    public Optional<ShortLinkResponse> findByCode(String code) {
        // Implement the logic to retrieve the short link from PostgreSQL database
        return shortLinkJpaRepository.findByCode(code)
                .map(this::toDmain);
    }

    @Override
    public boolean saveIfAbsent(ShortLinkResponse shortLinkResponse) {
        ShortLinkEntity entity = new ShortLinkEntity(
                shortLinkResponse.code(),
                shortLinkResponse.originalUrl(),
                java.time.Instant.parse(shortLinkResponse.createdAt())
        );
        try {
            // Flushing here makes the UNIQUE(code) result visible before returning.
            shortLinkJpaRepository.saveAndFlush(entity);
            return true;
        } catch (DataIntegrityViolationException exception) {
            return false;
        }
    }

    @Override
    public void save(ShortLinkResponse shortLinkResponse) {
        // Implement the logic to save the short link to PostgreSQL database
        ShortLinkEntity entity = new ShortLinkEntity(
                shortLinkResponse.code(),
                shortLinkResponse.originalUrl(),
                java.time.Instant.parse(shortLinkResponse.createdAt())
        );
        shortLinkJpaRepository.save(entity);
    }
    
    private ShortLinkResponse toDmain(ShortLinkEntity entity) {

        return new ShortLinkResponse(
                entity.getOriginalUrl(),
                entity.getCode(),
                entity.getCode(),
                entity.getCreatedAt().toString()
        );
    }
}
