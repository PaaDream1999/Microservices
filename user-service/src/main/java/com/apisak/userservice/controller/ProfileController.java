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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileRepository repo;
    private final WebClient.Builder web;

    @Value("${service.auth:http://auth-service:8081}")
    private String authUrl;

    @GetMapping("/me")
    public ResponseEntity<Profile> me(Principal principal) {
        long id = Long.parseLong(principal.getName());
        Profile p = repo.findByUserId(id)
                .orElseGet(() -> repo.save(new Profile(id, "User " + id)));
        return ResponseEntity.ok(p);
    }

    @PutMapping("/me")
    public ResponseEntity<Profile> replace(@RequestBody Profile in, Principal principal) {
        long id = Long.parseLong(principal.getName());
        Profile p = repo.findByUserId(id).orElseThrow();
        p.setDisplayName(in.getDisplayName());
        p.setBio(in.getBio());
        p.setAvatarUrl(in.getAvatarUrl());
        return ResponseEntity.ok(repo.save(p));
    }

    @PatchMapping("/me")
    public ResponseEntity<Profile> patch(@RequestBody Map<String,Object> patch,
                                         Principal principal) {

        long id = Long.parseLong(principal.getName());
        Profile p = repo.findByUserId(id).orElseThrow();

        patch.forEach((k, v) -> {
            switch (k) {
                case "displayName" -> p.setDisplayName(v.toString());
                case "bio"         -> p.setBio(v.toString());
                case "avatarUrl"   -> p.setAvatarUrl(v.toString());
                default            -> log.debug("skip unknown field {}", k);
            }
        });

        return ResponseEntity.ok(repo.save(p));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Principal principal) {
        long id = Long.parseLong(principal.getName());

        repo.findByUserId(id).ifPresent(repo::delete);
        log.info("profile {} deleted", id);

        try {
            web.build().delete()
                    .uri(authUrl + "/internal/users/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("auth-service user {} deleted", id);
        } catch (Exception ex) {
            log.warn("auth-service unreachable — user {} left in authdb: {}", id, ex.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
}