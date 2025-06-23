package com.apisak.authservice.listener;

import com.apisak.authservice.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * ฟัง UserCreatedEvent แล้ว POST ไป user-service (ผ่าน Gateway)
 * – ใน production อาจเพิ่ม JWT service-to-service / retry ฯลฯ
 */
@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(UserCreatedListener.class);
    private static final String PROFILE_API = "http://api-gateway:8080/api/users";

    private final RestTemplate rest;   // มาจาก RestConfig

    @EventListener
    public void handle(UserCreatedEvent e) {

        CreateProfileRequest body = new CreateProfileRequest(e.userId(), e.username());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            rest.postForLocation(PROFILE_API, new HttpEntity<>(body, headers));
            log.info("✓ profile created request sent for userId={}", e.userId());
        } catch (Exception ex) {
            log.error("✗ failed to create profile for userId={}: {}", e.userId(), ex.getMessage());
        }
    }

    /** payload ที่ user-service รับ */
    record CreateProfileRequest(java.util.UUID userId, String username) {}
}