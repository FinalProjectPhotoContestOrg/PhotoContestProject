package com.example.photocontestproject.services;

import java.io.IOException;

public interface CloudinaryImageService {
    String uploadImage(byte[] imageBytes) throws IOException;
}
