package com.example.photocontestproject.dtos.in;

import jakarta.validation.constraints.NotEmpty;

public class LogInDto {
    @NotEmpty(message = "Username and password are required.")
    private String username;
    @NotEmpty(message = "Username and password are required.")
    private String password;

    public LogInDto() {
    }

    public LogInDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public @NotEmpty(message = "Username and password are required.") String getUsername() {
        return username;
    }

    public void setUsername(@NotEmpty(message = "Username and password are required.") String username) {
        this.username = username;
    }

    public @NotEmpty(message = "Username and password are required.") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "Username and password are required.") String password) {
        this.password = password;
    }
}
