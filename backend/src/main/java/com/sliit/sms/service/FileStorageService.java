package com.sliit.sms.service;

import com.sliit.sms.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * Stores uploaded employee photos on local disk under sms.uploads.dir
 * (served back out via WebMvcConfig's "/uploads/**" resource handler) and
 * returns the public path the frontend renders directly as an <img src>.
 * Not a JPA-backed "business" service, so it doesn't need the
 * interface/impl split the rest of the domain services use.
 */
@Component
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${sms.uploads.dir}")
    private String uploadsDir;

    /**
     * Saves a new employee photo, deleting the previous one (if any) so
     * re-uploads don't leak orphaned files. Returns the public URL path.
     */
    public String storeEmployeePhoto(MultipartFile file, String previousPhotoUrl) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("No file was uploaded.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessRuleException("Only JPEG, PNG or WEBP images are allowed.");
        }

        try {
            Path dir = Paths.get(uploadsDir, "employees");
            Files.createDirectories(dir);

            String extension = switch (contentType) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> ".jpg";
            };
            String filename = UUID.randomUUID() + extension;
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target);

            deleteIfLocal(previousPhotoUrl);

            return "/uploads/employees/" + filename;
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to store the uploaded photo: " + e.getMessage());
        }
    }

    private void deleteIfLocal(String previousPhotoUrl) {
        if (!StringUtils.hasText(previousPhotoUrl) || !previousPhotoUrl.startsWith("/uploads/")) {
            return;
        }
        try {
            Path previous = Paths.get(uploadsDir, previousPhotoUrl.substring("/uploads/".length()));
            Files.deleteIfExists(previous);
        } catch (IOException ignored) {
            // best-effort cleanup; a stray orphaned file isn't worth failing the upload over
        }
    }
}
