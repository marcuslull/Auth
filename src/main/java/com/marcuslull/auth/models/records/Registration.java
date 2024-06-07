package com.marcuslull.auth.models.records;

public record Registration(
        String email,
        String password,
        String confirmPassword,
        String currentPassword,
        boolean isReset
) {
}
