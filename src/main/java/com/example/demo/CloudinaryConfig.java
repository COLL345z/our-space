package com.example.demo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dppgcjad7",
                "api_key", "669341677861388",
                "api_secret", "GmNnLFReGcRxcyTZTiNWwRnn9oc"
        ));
    }
}