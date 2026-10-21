package com.gunwala.storage;

import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * Stores uploads on local disk under app.upload-dir.
 * Swap this class for an S3 / Azure Blob implementation in production — callers only see relative keys.
 */
@Slf4j
@Service
public class FileStorageService {

    private static final Set<String> QR_TYPES = Set.of("image/png", "image/jpeg");
    private static final Set<String> RECEIPT_TYPES = Set.of("image/png", "image/jpeg", "application/pdf");

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public StoredFile storeQrImage(MultipartFile file) {
        return store(file, "qr", QR_TYPES, "QR image must be a PNG or JPEG file");
    }

    public StoredFile storeReceipt(MultipartFile file) {
        return store(file, "receipts", RECEIPT_TYPES, "Receipt must be a PNG, JPEG or PDF file");
    }

    public Resource load(String relativePath) {
        Path file = root.resolve(relativePath).normalize();
        if (!file.startsWith(root)) {
            throw new NotFoundException("File not found");
        }
        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("File not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new NotFoundException("File not found");
        }
    }

    public void deleteQuietly(String relativePath) {
        try {
            Path file = root.resolve(relativePath).normalize();
            if (file.startsWith(root)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            log.warn("Could not delete file {}", relativePath, e);
        }
    }

    private StoredFile store(MultipartFile file, String folder, Set<String> allowed, String typeError) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        try {
            // Trust the file's bytes, not the client-supplied Content-Type header.
            String detected = detectContentType(file);
            if (detected == null || !allowed.contains(detected)) {
                throw new BadRequestException(typeError);
            }
            String extension = switch (detected) {
                case "image/png" -> ".png";
                case "image/jpeg" -> ".jpg";
                default -> ".pdf";
            };

            Path dir = root.resolve(folder);
            Files.createDirectories(dir);
            String storedName = UUID.randomUUID() + extension;
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dir.resolve(storedName));
            }
            return new StoredFile(folder + "/" + storedName, detected, sanitize(file.getOriginalFilename(), extension));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file", e);
        }
    }

    /** Magic-byte sniffing for the three formats we accept. */
    private String detectContentType(MultipartFile file) throws IOException {
        byte[] h = new byte[8];
        int n;
        try (InputStream in = file.getInputStream()) {
            n = in.readNBytes(h, 0, h.length);
        }
        if (n >= 8 && (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G') return "image/png";
        if (n >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF) return "image/jpeg";
        if (n >= 4 && h[0] == '%' && h[1] == 'P' && h[2] == 'D' && h[3] == 'F') return "application/pdf";
        return null;
    }

    private String sanitize(String original, String fallbackExt) {
        if (!StringUtils.hasText(original)) {
            return "file" + fallbackExt;
        }
        String name = StringUtils.getFilename(StringUtils.cleanPath(original));
        if (name == null || name.isBlank()) {
            return "file" + fallbackExt;
        }
        return name.length() > 200 ? name.substring(name.length() - 200) : name;
    }
}
