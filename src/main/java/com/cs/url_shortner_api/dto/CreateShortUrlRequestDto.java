package com.cs.url_shortner_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShortUrlRequestDto {

    @NotBlank(message = "Original URL is required")
    @Pattern(regexp = "^https?://.+", message = "URL must start with http:// or https://")
    private String originalUrl;

    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Custom code can only contain letters, numbers, hyphens and underscores")
    private String customCode;
}
