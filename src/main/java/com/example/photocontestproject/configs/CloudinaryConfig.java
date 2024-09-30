package com.example.photocontestproject.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "ddhamcef1",
                "api_key", "682898194631151",
                "api_secret", "5SmH-10qNWbEgwd3MTuUGOgSjbs"
        );
        return new Cloudinary(config);
    }
}
