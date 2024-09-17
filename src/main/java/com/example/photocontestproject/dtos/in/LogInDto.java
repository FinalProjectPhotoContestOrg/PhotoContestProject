package com.example.photocontestproject.dtos.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LogInDto {
    @NotEmpty(message = "Username and password are required.")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters long")
    private String username;

    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters long")
    @NotEmpty(message = "Username and password are required.")
    private String password;

    public LogInDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
