package com.apisak.authservice.dto;

public record LoginDto(
        String username,
        String password
) {}