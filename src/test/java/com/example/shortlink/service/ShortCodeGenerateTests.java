package com.example.shortlink.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShortCodeGenerateTests {
    @Test 
    void codeLength() {
        ShortCodeGenerate shortCodeGenerate = new ShortCodeGenerate();
        String originalUrl = "https://www.example.com/some/long/url";
        String shortCode = shortCodeGenerate.generateShortCode(originalUrl);
        assert(shortCode.length() == 10); // Assuming the short code should be 10
    }
}
