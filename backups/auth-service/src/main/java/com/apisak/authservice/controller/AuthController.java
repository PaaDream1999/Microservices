package com.apisak.authservice.controller;

import com.apisak.authservice.dto.JwtDto;
import com.apisak.authservice.dto.LoginDto;
import com.apisak.authservice.dto.RegisterDto;
import com.apisak.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Validated @RequestBody RegisterDto dto) {
        auth.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public JwtDto login(@RequestBody LoginDto dto) {
        return new JwtDto(auth.login(dto));
    }
}