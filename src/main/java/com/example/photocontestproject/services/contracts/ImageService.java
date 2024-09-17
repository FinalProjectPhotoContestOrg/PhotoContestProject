package com.example.photocontestproject.services.contracts;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    byte[] resizeImage(MultipartFile multipartFile) throws IOException;
}
