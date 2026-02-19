package com.fitness.dto;

import com.fitness.domain.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String name;
    private String email;
    private UserRole role;
    private String message;
}
