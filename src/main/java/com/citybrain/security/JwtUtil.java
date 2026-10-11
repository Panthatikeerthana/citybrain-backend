package com.citybrain.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 24 hours expiry in ms
    private static final long EXPIRATION_TIME = 86_400_000L;

    private final SecretKey signingKey;

    public JwtUtil(@Value("${citybrain.jwt.secret:}") String propertySecret) {
        // Priority 1: Environment variable CITYBRAIN_JWT_SECRET
        // Priority 2: application.properties citybrain.jwt.secret
        // Priority 3: Fallback secure dev secret (>= 256 bits)
        String envSecret = System.getenv("CITYBRAIN_JWT_SECRET");
        String secretToUse;
        if (envSecret != null && !envSecret.isBlank()) {
            secretToUse = envSecret.trim();
        } else if (propertySecret != null && !propertySecret.isBlank()) {
            secretToUse = propertySecret.trim();
        } else {
            secretToUse = "citybrain-intelligent-urban-management-system-secret-key-must-be-256-bits-long";
        }
        this.signingKey = Keys.hmacShaKeyFor(secretToUse.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email, String role, String fullName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("fullName", fullName);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object id = claims.get("userId");
            if (id instanceof Number) {
                return ((Number) id).longValue();
            }
            return null;
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return (email != null && email.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationTimeMs() {
        return EXPIRATION_TIME;
    }
}
