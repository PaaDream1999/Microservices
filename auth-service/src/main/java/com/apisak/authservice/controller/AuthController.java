package com.apisak.authservice.controller;

import com.apisak.authservice.dto.JwtDto;
import com.apisak.authservice.dto.LoginDto;
import com.apisak.authservice.dto.RegisterDto;
import com.apisak.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST-API สำหรับสมัครสมาชิก / ล็อกอิน
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /** POST /api/auth/register */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDto dto) {
        authService.register(dto);
        return ResponseEntity.ok().build();
    }

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody @Valid LoginDto dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(new JwtDto(token));
    }
}