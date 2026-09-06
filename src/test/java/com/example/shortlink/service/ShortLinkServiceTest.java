package com.example.shortlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.exception.LinkCodeAlreadyExistsException;
import com.example.shortlink.repository.InMemoryShortLinkRepository;

class ShortLinkServiceTest {
    @Test
    void createsAnAutomaticTenCharacterCode() {
        ShortLinkService service = new ShortLinkService(new InMemoryShortLinkRepository(), null, null, null);

        var response = service.createShortLink(new CreateShortLinkRequest("https://example.com", null));

        assertEquals(10, response.code().length());
        assertEquals("https://example.com", response.originalUrl());
    }

    @Test
    void rejectsDuplicateCustomCode() {
        ShortLinkService service = new ShortLinkService(new InMemoryShortLinkRepository(), null, null, null);
        var request = new CreateShortLinkRequest("https://example.com", "custom-code");

        service.createShortLink(request);
        assertThrows(LinkCodeAlreadyExistsException.class, () -> service.createShortLink(request));
    }
}
