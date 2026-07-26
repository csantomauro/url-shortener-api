package com.cs.url_shortner_api.controller;

import com.cs.url_shortner_api.dto.CreateShortUrlRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShortUrlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void statsEndpoint_shouldReturnPopulatedJson_afterCreatingAndClicking() throws Exception {
        CreateShortUrlRequestDto request = new CreateShortUrlRequestDto();
        request.setOriginalUrl("https://www.example.com");
        request.setCustomCode("integrationtest");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/integrationtest"))
                .andExpect(status().isFound());

        mockMvc.perform(get("/integrationtest/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("integrationtest"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.totalClicks").value(1))
                .andExpect(jsonPath("$.clickTimestamps").isArray());
    }
}