package com.snapserve.booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${server.port:9002}")
  private String serverPort;

  @Bean
  public OpenAPI bookingServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("SnapServe Booking Service API")
                .description(
                    "REST API for managing bookings and reviews in the SnapServe platform.")
                .version("v1.0.0")
                .contact(new Contact().name("SnapServe Team").email("support@snapserve.com"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local development server"),
                new Server()
                    .url("http://booking-service:9002")
                    .description("Docker internal server")));
  }
}
