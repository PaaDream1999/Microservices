package com.apisak.userservice.controller;

import com.apisak.userservice.domain.Profile;
import com.apisak.userservice.repo.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;

/**
 * REST API ของ service “user-service”
 * <p>
 *  - GET  /users/me           ดึง Profile ของผู้ใช้ปัจจุบัน (สร้างให้อัตโนมัติถ้าไม่มี)
 *  - PUT  /users/me           อัปเดต Profile ทั้งก้อน
 *  - PATCH /users/me          อัปเดตเฉพาะ field ที่ส่งมา
 *  - DELETE /users/me         ลบ Profile + ยิงไปลบบัญชี Auth จริง
 *
 * <p>**คำเตือน “never used / never assigned” ใน IDE**
 * Spring จะ “อิมพอร์ต”​ Bean เหล่านี้ผ่านการสแกน @Component-scan
 * จึงไม่จำเป็นต้องถูกเรียกโดยโค้ดอื่น เราใส่ <code>@SuppressWarnings("unused")</code> ครอบคลุมไว้ให้ IDE เงียบ
 */
@SuppressWarnings("unused")
@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProfileController {

    /* Dependencies */
    private final ProfileRepository repo; // JPA repository
    private final WebClient.Builder webClient; // WebClient แบบ load-balanced (ประกาศใน @Configuration อื่น)

    /**
     * base URL ของ auth-service
     * ถ้าไม่ตั้งใน application.yml จะ fallback เป็นค่า default หลัง ':'
     */
    @Value("${service.auth:http://auth-service}")
    private String authServiceUrl;

    /* API */
    /** GET /users/me */
    @GetMapping("/me")
    public ResponseEntity<Profile> me(Principal principal) {
        Long userId = Long.valueOf(principal.getName());

        Profile profile = repo.findByUserId(userId)
                .orElseGet(() -> {
                    Profile p = repo.save(new Profile(userId, "User " + userId));
                    log.info("created default profile for user {}", userId);
                    return p;
                });

        return ResponseEntity.ok(profile);
    }

    /** PUT /users/me – อัปเดตทุก field */
    @PutMapping("/me")
    public ResponseEntity<Profile> update(@RequestBody Profile payload, Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        Profile p = repo.findByUserId(userId).orElseThrow();

        p.setDisplayName(payload.getDisplayName());
        p.setBio(payload.getBio());
        p.setAvatarUrl(payload.getAvatarUrl());

        return ResponseEntity.ok(repo.save(p));
    }

    /** PATCH /users/me – อัปเดตเฉพาะ field ที่ส่งมา */
    @PatchMapping("/me")
    public ResponseEntity<Profile> partial(@RequestBody Profile payload, Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        Profile p = repo.findByUserId(userId).orElseThrow();

        if (payload.getDisplayName() != null) p.setDisplayName(payload.getDisplayName());
        if (payload.getBio()         != null) p.setBio(payload.getBio());
        if (payload.getAvatarUrl()   != null) p.setAvatarUrl(payload.getAvatarUrl());

        return ResponseEntity.ok(repo.save(p));
    }

    /** DELETE /users/me – ลบ Profile + ลบบัญชีผู้ใช้ที่ auth-service */
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Principal principal) {
        Long userId = Long.valueOf(principal.getName());

        // 1) ลบ profile ฝั่ง user-service
        repo.findByUserId(userId).ifPresent(repo::delete);
        log.info("profile of user {} deleted", userId);

        // 2) เรียก auth-service ผ่าน WebClient (load-balanced ด้วย Eureka)
        webClient.build()
                .delete()
                .uri(authServiceUrl + "/internal/users/{id}", userId)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("user account {} deleted at auth-service", userId);
        return ResponseEntity.noContent().build();
    }
}