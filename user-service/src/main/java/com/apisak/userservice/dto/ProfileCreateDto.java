package com.apisak.userservice.dto;

public record ProfileCreateDto(
        Long userId,
        String displayName
) {}