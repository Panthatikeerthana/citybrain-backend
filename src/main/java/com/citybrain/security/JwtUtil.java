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
        // Priority 1: Environment variable JWT_SECRET (standard on Render)
        // Priority 2: Environment variable CITYBRAIN_JWT_SECRET
        // Priority 3: Spring property citybrain.jwt.secret
        String rawSecret = System.getenv("JWT_SECRET");
        if (rawSecret == null || rawSecret.isBlank()) {
            rawSecret = System.getenv("CITYBRAIN_JWT_SECRET");
        }
        if (rawSecret == null || rawSecret.isBlank()) {
            rawSecret = propertySecret;
        }

        String secret = (rawSecret != null) ? rawSecret.trim() : "";

        // Check if missing or set to an unresolved template placeholder
        if (secret.isEmpty() || secret.startsWith("${") || secret.equalsIgnoreCase("${JWT_SECRET}")) {
            throw new IllegalStateException(
                "CRITICAL: JWT signing secret is missing or unconfigured. " +
                "Please set the 'JWT_SECRET' environment variable in your Render Dashboard to a secure random string of at least 32 characters (256 bits)."
            );
        }

        // Validate cryptographic key strength per RFC 7518 Section 3.2 (HMAC-SHA256 requires >= 256 bits / 32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                "CRITICAL: JWT_SECRET is too short (" + keyBytes.length + " bytes / " + (keyBytes.length * 8) + " bits). " +
                "Cryptographic HMAC-SHA256 signing requires a secret of at least 32 bytes (256 bits). " +
                "Please generate a 64+ character random secret and update JWT_SECRET in your Render Dashboard."
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
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