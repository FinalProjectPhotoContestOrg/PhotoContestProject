package com.example.photocontestproject.dtos.in;

public class RegisterDto extends UserInDto {

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
