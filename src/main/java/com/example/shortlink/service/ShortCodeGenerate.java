package com.example.shortlink.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

/** Generates opaque, URL-safe short codes without process-local state. */
@Component
public class ShortCodeGenerate {
    public static final int DEFAULT_LENGTH = 10;
    private final SecureRandom random;

    public ShortCodeGenerate() {
        this(new SecureRandom());
    }

    // Package-private constructor keeps the generator deterministic in unit tests.
    ShortCodeGenerate(SecureRandom random) {
        this.random = random;
    }

    public String generateShortCode() {
        StringBuilder code = new StringBuilder(DEFAULT_LENGTH);
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            // nextInt(bound) avoids modulo bias when selecting an alphabet character.
            code.append(Base62Codec.ALPHABET.charAt(random.nextInt(Base62Codec.ALPHABET.length())));
        }
        return code.toString();
    }

    /**
     * Kept for compatibility with the original exercise API. The URL is not
     * used deliberately: hashing it would make codes predictable and would
     * couple code identity to URL changes.
     */
    public String generateShortCode(String originalUrl) {
        return generateShortCode();
    }
}
