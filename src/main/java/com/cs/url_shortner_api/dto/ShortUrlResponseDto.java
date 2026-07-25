package com.cs.url_shortner_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ShortUrlResponseDto {
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
}
