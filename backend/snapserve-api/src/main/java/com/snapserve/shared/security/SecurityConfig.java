package com.snapserve.shared.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorization ->
                authorization
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        "/actuator/health/**",
                        "/api/v1/auth/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .build();
  }

  @Bean
  JwtDecoder jwtDecoder(JwtProperties properties) {
    var signingKey =
        new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA512");
    return NimbusJwtDecoder.withSecretKey(signingKey).macAlgorithm(MacAlgorithm.HS512).build();
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
    return converter;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource(
      @Value("${app.cors.allowed-origins:http://localhost:3000}") String allowedOrigins) {
    var configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(
        Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isBlank())
            .toList());
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
    configuration.setExposedHeaders(List.of("Location"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Object rolesClaim = jwt.getClaims().get("roles");
    if (rolesClaim instanceof Collection<?> roles) {
      return roles.stream()
          .map(Object::toString)
          .map(this::toRoleAuthority)
          .map(authority -> (GrantedAuthority) authority)
          .toList();
    }

    Object roleClaim = jwt.getClaims().get("role");
    if (roleClaim == null || roleClaim.toString().isBlank()) {
      return List.of();
    }
    return List.of(toRoleAuthority(roleClaim.toString()));
  }

  private SimpleGrantedAuthority toRoleAuthority(String role) {
    var normalizedRole = role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role;
    return new SimpleGrantedAuthority("ROLE_" + normalizedRole.toUpperCase(Locale.ROOT));
  }
}
