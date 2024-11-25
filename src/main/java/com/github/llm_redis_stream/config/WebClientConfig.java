package com.github.llm_redis_stream.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${llm.claude.api.key}")
    private String claudeApiKey;

    @Value("${llm.claude.api.endpoint}")
    private String claudeApiEndpoint;

    @Bean
    public WebClient claudeWebClient() {
        return WebClient.builder()
                .baseUrl(claudeApiEndpoint)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("x-api-key", claudeApiKey)
                .build();
    }

}
