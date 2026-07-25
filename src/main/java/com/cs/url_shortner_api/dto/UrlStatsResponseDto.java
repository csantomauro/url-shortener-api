package com.cs.url_shortner_api.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class UrlStatsResponseDto {
    private String shortCode;
    private String originalUrl;
    private long totalClicks;
    private List<LocalDateTime> clickTimestamps;
}
