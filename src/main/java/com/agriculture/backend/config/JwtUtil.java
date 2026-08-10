package com.agriculture.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getCle() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private static final long DUREE_VALIDITE_MS = 1000L * 60 * 60 * 10;

    public String genererToken(String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + DUREE_VALIDITE_MS))
                .signWith(getCle())
                .compact();
    }

    public Claims extraireClaims(String token) {
        return Jwts.parser()
                .verifyWith(getCle())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraireEmail(String token) {
        return extraireClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extraireRoles(String token) {
        return extraireClaims(token).get("roles", List.class);
    }

    public boolean estValide(String token) {
        try {
            extraireClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}