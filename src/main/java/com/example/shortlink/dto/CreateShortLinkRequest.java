package com.example.shortlink.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateShortLinkRequest(
    @NotBlank(message = "Original URL must not be blank")
    @URL (message = "Original URL must be a valid URL")
        String originalUrl,
        // @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", 
        //         message = "Code must be 4-16 characters long and can only contain letters, numbers, hyphens, and underscores")            
        String code) {
    
}
