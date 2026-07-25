package com.cs.url_shortner_api.service;

import com.cs.url_shortner_api.dto.CreateShortUrlRequestDto;
import com.cs.url_shortner_api.dto.ShortUrlResponseDto;
import com.cs.url_shortner_api.dto.UrlStatsResponseDto;
import com.cs.url_shortner_api.exception.CustomCodeAlreadyExistsException;
import com.cs.url_shortner_api.exception.ShortUrlNotFoundException;
import com.cs.url_shortner_api.model.ClickEvent;
import com.cs.url_shortner_api.model.ShortUrl;
import com.cs.url_shortner_api.repository.ClickEventRepository;
import com.cs.url_shortner_api.repository.ShortUrlRepository;
import com.cs.url_shortner_api.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickEventRepository clickEventRepository;

    @Transactional
    public ShortUrlResponseDto createShortUrl(CreateShortUrlRequestDto request) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.getOriginalUrl());

        boolean hasCustomCode = request.getCustomCode() != null && !request.getCustomCode().isBlank();

        if (hasCustomCode) {
            if (shortUrlRepository.existsByShortCode(request.getCustomCode())) {
                throw new CustomCodeAlreadyExistsException(
                        "Custom code already taken: " + request.getCustomCode()
                );
            }
            shortUrl.setShortCode(request.getCustomCode());
            shortUrlRepository.save(shortUrl);
        } else {
            shortUrl.setShortCode("");
            ShortUrl saved = shortUrlRepository.save(shortUrl);

            saved.setShortCode(Base62Encoder.encode(saved.getId()));
            shortUrlRepository.save(saved);
            shortUrl = saved;
        }

        return toResponseDto(shortUrl);
    }

    public String resolveAndTrackClick(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short code not found: " + shortCode));

        ClickEvent click = new ClickEvent();
        click.setShortUrl(shortUrl);
        clickEventRepository.save(click);

        return shortUrl.getOriginalUrl();
    }

    public UrlStatsResponseDto getStats(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short code not found: " + shortCode));

        List<LocalDateTime> timestamps = shortUrl.getClicks().stream()
                .map(ClickEvent::getClickedAt)
                .toList();

        return new UrlStatsResponseDto(
                shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(),
                timestamps.size(),
                timestamps
        );
    }

    private ShortUrlResponseDto toResponseDto(ShortUrl shortUrl) {
        return new ShortUrlResponseDto(
                shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getCreatedAt()
        );
    }
}
