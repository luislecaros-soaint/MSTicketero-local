package com.example.MSTicketero.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
@Profile("!test")
public class CgsClientConfig {

    @Bean
    public RestClientCustomizer cgsRestClientCustomizer(
            @Value("${external.api.cgs.connect-timeout}") int connectTimeout,
            @Value("${external.api.cgs.read-timeout}") int readTimeout) {
        
        return builder -> {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(connectTimeout);
            factory.setReadTimeout(readTimeout);
            builder.requestFactory(factory);
        };
    }
}