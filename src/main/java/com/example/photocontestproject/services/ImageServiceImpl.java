package com.example.photocontestproject.services;

import com.example.photocontestproject.services.contracts.ImageService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public byte[] resizeImage(MultipartFile multipartFile) throws IOException {

        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());


        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(1920, 1080)
                .keepAspectRatio(true)
                .asBufferedImage();


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);
        return outputStream.toByteArray();
    }
}