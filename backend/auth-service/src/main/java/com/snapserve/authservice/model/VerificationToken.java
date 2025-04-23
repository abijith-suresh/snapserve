package com.snapserve.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "verification_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {

    @Id
    private String id;

    private String token;

    private String userId;

    private Instant expiryDate;

}