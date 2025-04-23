package com.snapserve.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document(collection="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {
    @Id
    private String id;
    private String email;
    private String passwordHash;
    private String role;
    private boolean enabled;
    private String verificationToken;
    private Instant tokenExpiry;
    private String refreshToken;
}
