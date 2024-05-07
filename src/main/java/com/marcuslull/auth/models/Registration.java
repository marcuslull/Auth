package com.marcuslull.auth.models;

public record Registration(String email, String password, String confirmPassword, String currentPassword, boolean isReset) {
}
