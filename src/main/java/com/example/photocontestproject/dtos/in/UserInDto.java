package com.example.photocontestproject.dtos.in;

import jakarta.validation.constraints.Size;

public class UserInDto {
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters long")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters long")
    private String lastName;

    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters long")
    private String username;

    @Size(min = 5, max = 70, message = "email must be at between 5 and 70 characters long")
    private String email;

    @Size(min = 6, max = 50, message = "Password must be at least 6 characters long")
    private String password;

    public UserInDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
