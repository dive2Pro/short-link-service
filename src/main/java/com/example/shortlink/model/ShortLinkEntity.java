package com.example.shortlink.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "short_links", 
        uniqueConstraints = {
            @UniqueConstraint(
                        columnNames = { "code" },
                        name = "unique_code_constraint"
            )
        }
    )
public class ShortLinkEntity {
    
    @Id 
    @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true, length = 16)
    private String code;

    @Column (name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column (name = "created_at", nullable = false)
    private Instant createdAt;

    protected ShortLinkEntity() {}

    public ShortLinkEntity(String code, String originalUrl, Instant createdAt) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
