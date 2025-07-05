package com.ust.gateway_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class GlobalCorsConfig implements WebFluxConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**") // Apply CORS to all endpoints
        .allowedOriginPatterns("*") // Allow calls from any origin
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*") // Allow all headers
        .allowCredentials(true) // Allow cookies/credentials
        .maxAge(3600); // Cache preflight response for 1 hour
  }
}
