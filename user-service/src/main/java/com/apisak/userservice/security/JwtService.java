package com.apisak.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.List;

@Service
public class JwtService {

    private final Key key;
    private static final String ROLES_KEY = "roles";

    public JwtService(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public List<String> extractRoles(String token) {
        Object raw = parse(token).get(ROLES_KEY);
        if (raw instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    public String extractSubject(String token) {
        return parse(token).getSubject();
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}