package com.example.shortlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.exception.LinkCodeAlreadyExistsException;
import com.example.shortlink.repository.InMemoryShortLinkRepository;

class ShortLinkServiceTest {
    private ShortLinkService newService(InMemoryShortLinkRepository repository) {
        return new ShortLinkService(
                repository,
                new ShortCodeGenerate(),
                new SimpleMeterRegistry(),
                new InMemoryLinkLookupCache());
    }

    @Test
    void createsAnAutomaticTenCharacterCode() {
        ShortLinkService service = newService(new InMemoryShortLinkRepository());

        var response = service.createShortLink(new CreateShortLinkRequest("https://example.com", null));

        assertEquals(10, response.code().length());
        assertEquals("https://example.com", response.originalUrl());
    }

    @Test
    void rejectsDuplicateCustomCode() {
        ShortLinkService service = newService(new InMemoryShortLinkRepository());
        var request = new CreateShortLinkRequest("https://example.com", "custom-code");

        service.createShortLink(request);
        assertThrows(LinkCodeAlreadyExistsException.class, () -> service.createShortLink(request));
    }

    @Test
    void cachesSuccessfulRepositoryFallback() {
        InMemoryShortLinkRepository repository = new InMemoryShortLinkRepository();
        ShortLinkService service = newService(repository);
        ShortLinkResponse persisted = new ShortLinkResponse(
                "https://example.com/first", "existing", "localhost:8080/existing", "2026-01-01T00:00:00Z");
        repository.save(persisted);

        assertEquals(Optional.of(persisted), service.findByCode("existing"));

        ShortLinkResponse changed = new ShortLinkResponse(
                "https://example.com/changed", "existing", "localhost:8080/existing", "2026-01-02T00:00:00Z");
        repository.save(changed);

        assertEquals(Optional.of(persisted), service.findByCode("existing"));
    }
}
