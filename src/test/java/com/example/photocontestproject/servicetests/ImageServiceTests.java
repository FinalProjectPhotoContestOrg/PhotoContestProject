package com.example.photocontestproject.servicetests;

import com.example.photocontestproject.services.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTests {

    private ImageServiceImpl imageService;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageServiceImpl();
    }

    @Test
    void resizeImage_Should_ReturnResizedImage() throws IOException {
        BufferedImage originalImage = new BufferedImage(4000, 3000, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        when(multipartFile.getInputStream()).thenReturn(bais);

        byte[] resizedImage = imageService.resizeImage(multipartFile);

        assertNotNull(resizedImage);
        BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(resizedImage));
        assertTrue(resultImage.getWidth() <= 1920 && resultImage.getHeight() <= 1080);
    }

    @Test
    void resizeImage_Should_ThrowIOException_ForInvalidImage() throws IOException {
        when(multipartFile.getInputStream()).thenThrow(new IOException("Invalid image"));

        assertThrows(IOException.class, () -> imageService.resizeImage(multipartFile));
    }
}