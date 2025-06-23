package com.apisak.authservice.dto;
import jakarta.validation.constraints.*;

public record RegisterDto(
        @NotBlank String username,
        @Size(min=6) String password,
        @Email String email
) {}