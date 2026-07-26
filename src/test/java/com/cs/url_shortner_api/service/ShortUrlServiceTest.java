package com.cs.url_shortner_api.service;

import com.cs.url_shortner_api.dto.CreateShortUrlRequestDto;
import com.cs.url_shortner_api.dto.ShortUrlResponseDto;
import com.cs.url_shortner_api.exception.CustomCodeAlreadyExistsException;
import com.cs.url_shortner_api.exception.ShortUrlNotFoundException;
import com.cs.url_shortner_api.model.ShortUrl;
import com.cs.url_shortner_api.repository.ClickEventRepository;
import com.cs.url_shortner_api.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ClickEventRepository clickEventRepository;

    @InjectMocks
    private ShortUrlService shortUrlService;

    @Test
    void createShortUrl_shouldUseBase62Code_whenNoCustomCodeProvided() {
        CreateShortUrlRequestDto request = new CreateShortUrlRequestDto();
        request.setOriginalUrl("https://www.example.com");

        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> {
            ShortUrl entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(125L);
            }
            return entity;
        });

        ShortUrlResponseDto result = shortUrlService.createShortUrl(request);

        assertThat(result.getShortCode()).isEqualTo("21"); // Base62(125) = "21"
        verify(shortUrlRepository, times(2)).save(any(ShortUrl.class));
    }

    @Test
    void createShortUrl_shouldUseCustomCode_whenProvidedAndAvailable() {
        CreateShortUrlRequestDto request = new CreateShortUrlRequestDto();
        request.setOriginalUrl("https://www.example.com");
        request.setCustomCode("mylink");

        when(shortUrlRepository.existsByShortCode("mylink")).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortUrlResponseDto result = shortUrlService.createShortUrl(request);

        assertThat(result.getShortCode()).isEqualTo("mylink");
        verify(shortUrlRepository, times(1)).save(any(ShortUrl.class));
    }

    @Test
    void createShortUrl_shouldThrow_whenCustomCodeAlreadyTaken() {
        CreateShortUrlRequestDto request = new CreateShortUrlRequestDto();
        request.setOriginalUrl("https://www.example.com");
        request.setCustomCode("taken");

        when(shortUrlRepository.existsByShortCode("taken")).thenReturn(true);

        assertThatThrownBy(() -> shortUrlService.createShortUrl(request))
                .isInstanceOf(CustomCodeAlreadyExistsException.class);

        verify(shortUrlRepository, never()).save(any(ShortUrl.class));
    }

    @Test
    void resolveAndTrackClick_shouldReturnOriginalUrl_andSaveClickEvent() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setShortCode("abc");
        shortUrl.setOriginalUrl("https://www.example.com");

        when(shortUrlRepository.findByShortCode("abc")).thenReturn(Optional.of(shortUrl));

        String result = shortUrlService.resolveAndTrackClick("abc");

        assertThat(result).isEqualTo("https://www.example.com");
        verify(clickEventRepository).save(any());
    }

    @Test
    void resolveAndTrackClick_shouldThrow_whenCodeNotFound() {
        when(shortUrlRepository.findByShortCode("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shortUrlService.resolveAndTrackClick("missing"))
                .isInstanceOf(ShortUrlNotFoundException.class);
    }
}
