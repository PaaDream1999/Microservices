package com.apisak.authservice.service;

import com.apisak.authservice.domain.User;
import com.apisak.authservice.dto.LoginDto;
import com.apisak.authservice.dto.RegisterDto;
import com.apisak.authservice.repo.UserRepository;
import com.apisak.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final UserEventPublisher eventPublisher;

    /** สมัครสมาชิกและปล่อย Event */
    @Transactional
    public void register(RegisterDto dto) {
        if (repo.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPasswordHash(encoder.encode(dto.password()));
        user.setEmail(dto.email());
        user.getRoles().add("ROLE_USER");

        repo.save(user);
        eventPublisher.publishUserCreated(user);
    }

    /** ล็อกอินและคืน JWT */
    public String login(LoginDto dto) {
        User user = repo.findByUsername(dto.username())
                .orElseThrow(() -> new IllegalArgumentException("Username not found"));

        if (!encoder.matches(dto.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return jwt.generate(user);
    }
}