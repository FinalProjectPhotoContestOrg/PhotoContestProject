package com.example.photocontestproject.dtos.in;

import jakarta.validation.constraints.Size;

public class PasswordDto {
    @Size(min = 6, max = 50, message = "Password must be at least 6 characters long")
    private String password;
    @Size(min = 6, max = 50, message = "Password Confirmation must be at least 6 characters long")
    private String confirmPassword;

    public PasswordDto() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
