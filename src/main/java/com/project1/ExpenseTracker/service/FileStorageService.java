package com.project1.ExpenseTracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    // Maximum file size = 10 MB
    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    // Allowed file types
    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "application/pdf"
            );

    public FileStorageService(
            @Value("${file.upload-dir}") String uploadDir) {

        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {

            Files.createDirectories(uploadPath);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not create upload directory",
                    e);
        }
    }

    public String storeFile(MultipartFile file) {

        // -----------------------------
        // 1. Check empty file
        // -----------------------------

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "File cannot be empty");
        }

        // -----------------------------
        // 2. Check file size
        // -----------------------------

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new IllegalArgumentException(
                    "File size cannot exceed 10 MB");
        }

        // -----------------------------
        // 3. Check file type
        // -----------------------------

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES
                        .contains(contentType)) {

            throw new IllegalArgumentException(
                    "Invalid file type. " +
                            "Only PDF, JPG and PNG files are allowed");
        }

        // -----------------------------
        // 4. Get original filename
        // -----------------------------

        String originalFilename =
                file.getOriginalFilename();

        if (originalFilename == null ||
                originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid file name");
        }

        // -----------------------------
        // 5. Get extension
        // -----------------------------

        String extension = "";

        int dotIndex =
                originalFilename.lastIndexOf(".");

        if (dotIndex >= 0) {

            extension =
                    originalFilename.substring(dotIndex)
                            .toLowerCase();
        }

        // -----------------------------
        // 6. Generate unique filename
        // -----------------------------

        String filename =
                UUID.randomUUID() + extension;

        // -----------------------------
        // 7. Create target path
        // -----------------------------

        Path targetLocation =
                uploadPath
                        .resolve(filename)
                        .normalize();

        // -----------------------------
        // 8. Security check
        // -----------------------------

        if (!targetLocation.startsWith(uploadPath)) {

            throw new IllegalArgumentException(
                    "Invalid file path");
        }

        // -----------------------------
        // 9. Save file
        // -----------------------------

        try {

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not store file",
                    e);
        }

        // -----------------------------
        // 10. Return database path
        // -----------------------------

        return "uploads/" + filename;
    }

    // =====================================================
    // Load file for downloading
    // =====================================================

    public Path loadFile(String filePath) {

        Path file = Paths.get(filePath)
                .toAbsolutePath()
                .normalize();

        // Security check against path traversal
        if (!file.startsWith(uploadPath)) {

            throw new IllegalArgumentException(
                    "Invalid file path");
        }

        if (!Files.exists(file)) {

            throw new RuntimeException(
                    "File not found");
        }

        return file;
    }
}