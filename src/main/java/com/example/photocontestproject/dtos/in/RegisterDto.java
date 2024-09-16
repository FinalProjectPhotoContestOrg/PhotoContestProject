package com.example.photocontestproject.dtos.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class RegisterDto extends UserInDto {
    @Size(min = 6, max = 50, message = "Password must be at least 6 characters long")
    private String confirmPassword;

    public RegisterDto() {
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
