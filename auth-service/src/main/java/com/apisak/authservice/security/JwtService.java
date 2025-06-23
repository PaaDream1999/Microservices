package com.apisak.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Service สำหรับจัดการสร้าง / อ่าน JWT
 */
@Service
public class JwtService {

    /** คีย์ใช้เข้ารหัส JWT */
    private final Key key;

    /** ชื่อคีย์ใน JWT payload ที่เก็บ role ต่าง ๆ */
    public static final String ROLES_KEY = "roles";

    /**
     * สร้าง key สำหรับเซ็น JWT ด้วย HMAC-SHA
     * @param secret คีย์ความยาวขั้นต่ำ 32 ตัวอักษร
     */
    public JwtService(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * สร้าง JWT token ด้วย subject (userId หรือ username) และ roles เช่น ["USER"]
     * @param subject รหัสหรือ user id
     * @param roles รายการ role เช่น ["USER", "ADMIN"]
     */
    public String generateToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim(ROLES_KEY, roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS))) // อายุ 1 ชั่วโมง
                .signWith(key)
                .compact();
    }

    /**
     * อ่าน subject จาก JWT (มักจะเป็น userId หรือ username)
     */
    public String extractSubject(String token) {
        return parse(token).getSubject();
    }

    /**
     * อ่าน roles จาก JWT (field ที่ชื่อว่า "roles")
     */
    public List<String> extractRoles(String token) {
        Object raw = parse(token).get(ROLES_KEY);
        if (raw instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    /**
     * ใช้ภายในเพื่อแปลง token เป็น Claims
     */
    private Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}