package com.example.photocontestproject.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity) {
        super(String.format("%s not found.", entity));
    }
}
