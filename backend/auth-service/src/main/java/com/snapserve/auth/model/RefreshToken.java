package com.snapserve.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "refreshToken")
public class RefreshToken {
    @Id
    private ObjectId id;

    @Indexed
    private ObjectId userId;

    private String token;

    private String deviceId;
    private String ipAddress;

    @Indexed(expireAfterSeconds = 0)
    private Instant expiresAt;

    private boolean revoked = false;

    private Instant createdAt;
}