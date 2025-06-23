package com.apisak.userservice.controller;

import com.apisak.userservice.domain.Profile;
import com.apisak.userservice.dto.ProfileCreateDto;
import com.apisak.userservice.repo.ProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint ที่ service อื่น (เช่น auth-service) เรียกภายในเพื่อสร้าง Profile
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalProfileController {

    private final ProfileRepository repo;

    /**
     * POST /internal/profiles
     * body: { "userId": 1, "displayName": "User 1" }
     */
    @PostMapping("/profiles")
    public ResponseEntity<Void> create(@RequestBody @Valid ProfileCreateDto dto) {

        repo.findByUserId(dto.userId())
                .orElseGet(() -> repo.save(
                        new Profile(dto.userId(), dto.displayName()))
                );

        return ResponseEntity.noContent().build();
    }
}