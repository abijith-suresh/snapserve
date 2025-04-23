package com.snapserve.apigateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> implements Ordered {

    private final JwtService jwtService;
    private final RouteValidator routeValidator;

    public static class Config {
    }

    public JwtAuthenticationFilter(JwtService jwtService, RouteValidator routeValidator) {
        super(Config.class);
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            if (!routeValidator.isSecured.test(path)) {
                System.out.println("Open endpoint, proceeding without JWT validation: " + path);
                return chain.filter(exchange);
            }

            String token = extractToken(exchange.getRequest().getHeaders());
            if (token == null || !jwtService.validateToken(token)) {
                return onError(exchange, "Invalid or missing JWT", HttpStatus.UNAUTHORIZED);
            }

            Claims claims = jwtService.getClaims(token);
            exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", claims.getSubject())
                    .build();

            return chain.filter(exchange);
        };
    }

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
