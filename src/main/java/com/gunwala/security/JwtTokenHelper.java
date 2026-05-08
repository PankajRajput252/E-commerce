package com.gunwala.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenHelper {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    // Must be minimum 64 characters for HS512
    private final String secret =
            "GunWalaSuperSecureJwtSecretKeyForHS512Authentication2026ProductionServerSecurityKeyVeryLong";

    // Generate secure signing key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Retrieve username from JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Retrieve expiration date from JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Retrieve specific claim from token
    public <T> T getClaimFromToken(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Retrieve all claims from token
    private Claims getAllClaimsFromToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if token expired
    private Boolean isTokenExpired(String token) {

        final Date expiration = getExpirationDateFromToken(token);

        return expiration.before(new Date());
    }

    // Generate token for user
    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        return doGenerateToken(
                claims,
                userDetails.getUsername()
        );
    }

    // Generate JWT token
    private String doGenerateToken(
            Map<String, Object> claims,
            String subject
    ) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(
                        new Date(System.currentTimeMillis())
                )
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + JWT_TOKEN_VALIDITY * 1000
                        )
                )
                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS512
                )
                .compact();
    }

    // Validate token
    public Boolean validateToken(
            String token,
            UserDetails userDetails
    ) {

        final String username =
                getUsernameFromToken(token);

        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token)
        );
    }
}