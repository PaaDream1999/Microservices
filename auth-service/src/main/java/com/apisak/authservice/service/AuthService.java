package com.apisak.authservice.service;

import com.apisak.authservice.domain.User;
import com.apisak.authservice.dto.LoginDto;
import com.apisak.authservice.dto.RegisterDto;
import com.apisak.authservice.repo.UserRepository;
import com.apisak.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final WebClient.Builder web;

    @Transactional
    public void register(@NonNull RegisterDto dto) {
        if (repo.findByUsername(dto.username()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPasswordHash(encoder.encode(dto.password()));
        user.setEmail(dto.email());
        user.setRoles(List.of("USER"));
        repo.save(user);

        web.build()
                .post()
                .uri("lb://user-service/internal/profiles")
                .bodyValue(Map.of(
                        "userId", user.getId(),
                        "displayName", "User " + user.getId()
                ))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public @NonNull String login(@NonNull LoginDto dto) {
        User user = repo.findByUsername(dto.username())
                .orElseThrow(() -> new IllegalArgumentException("Bad credentials"));

        if (!encoder.matches(dto.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Bad credentials");
        }

        return jwt.generateToken(user.getId().toString(), user.getRoles());
    }
}