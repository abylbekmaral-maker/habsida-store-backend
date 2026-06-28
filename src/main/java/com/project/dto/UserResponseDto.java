package com.project.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
}
