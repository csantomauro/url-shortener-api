package com.cs.url_shortner_api.controller;

import com.cs.url_shortner_api.dto.CreateShortUrlRequestDto;
import com.cs.url_shortner_api.dto.ShortUrlResponseDto;
import com.cs.url_shortner_api.dto.UrlStatsResponseDto;
import com.cs.url_shortner_api.service.ShortUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortUrlResponseDto> createShortUrl(
            @Valid @RequestBody CreateShortUrlRequestDto request) {
        ShortUrlResponseDto created = shortUrlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = shortUrlService.resolveAndTrackClick(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/{code}/stats")
    public ResponseEntity<UrlStatsResponseDto> getStats(@PathVariable String code) {
        return ResponseEntity.ok(shortUrlService.getStats(code));
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("hello", "world");
    }
}
