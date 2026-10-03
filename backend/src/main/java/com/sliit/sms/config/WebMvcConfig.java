package com.sliit.sms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Serves uploaded employee photos from sms.uploads.dir at "/uploads/**" so
 * the React app can render them directly as <img src="http://.../uploads/...">
 * without needing to attach a JWT (SecurityConfig permits this path).
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${sms.uploads.dir}")
    private String uploadsDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + new File(uploadsDir).getAbsolutePath() + File.separator;
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
