package com.example.Bug.Tracker.Backend.helper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EndPointConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}