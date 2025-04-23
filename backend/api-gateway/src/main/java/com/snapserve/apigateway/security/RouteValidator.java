package com.snapserve.apigateway.security;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
@Getter
public class RouteValidator {

    private static final List<String> openEndpoints = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/public/**"
    );

    public Predicate<String> isSecured = path -> openEndpoints
            .stream()
            .noneMatch(uri -> path.matches(convertToRegex(uri)));

    private String convertToRegex(String pattern) {
        return pattern.replace("**", ".*");
    }
}
