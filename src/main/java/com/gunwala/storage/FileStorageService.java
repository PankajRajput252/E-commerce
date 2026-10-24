package com.gunwala.storage;

import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private static final Map<String, String> IMAGE_TYPES = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg");

    private static final Map<String, String> RECEIPT_TYPES = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg",
            "application/pdf", ".pdf");

    private final S3Client s3;
    private final String bucket;

    public FileStorageService(S3Client s3, @Value("${aws.s3.bucket-name}") String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    /** Admin-uploaded payment QR image (PNG/JPEG only). */
    public StoredFile storeQrImage(MultipartFile file) {
        return store(file, "payments/qr", IMAGE_TYPES, "Only PNG or JPEG images are allowed");
    }

    /** User-uploaded payment receipt (PNG/JPEG/PDF). */
    public StoredFile storeReceipt(MultipartFile file) {
        return store(file, "payments/receipts", RECEIPT_TYPES,
                "Receipt must be a PNG, JPEG or PDF file");
    }

    /** Loads a stored file by its S3 key (the value saved in *_path columns). */
    public Resource load(String key) {
        if (key == null || key.isBlank()) {
            throw new NotFoundException("File not found");
        }
        try {
            byte[] bytes = s3.getObjectAsBytes(
                    GetObjectRequest.builder().bucket(bucket).key(key).build()).asByteArray();
            return new ByteArrayResource(bytes);
        } catch (NoSuchKeyException e) {
            throw new NotFoundException("File not found");
        }
    }

    /** Best-effort delete; never throws (used for rollback cleanup). */
    public void deleteQuietly(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        try {
            s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (Exception e) {
            log.warn("Could not delete S3 object {}: {}", key, e.getMessage());
        }
    }


    private StoredFile store(MultipartFile file, String folder, Map<String, String> allowedTypes, String typeError) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BadRequestException("File must be 5 MB or smaller");
        }
        String contentType = file.getContentType();
        String ext = contentType == null ? null : allowedTypes.get(contentType);
        if (ext == null) {
            throw new BadRequestException(typeError);
        }

        String key = folder + "/" + UUID.randomUUID() + ext;
        try {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read uploaded file", e);
        }
        return new StoredFile(key, contentType, cleanOriginalName(file.getOriginalFilename()));
    }


    private String cleanOriginalName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String clean = name.replace("\\", "/");
        clean = clean.substring(clean.lastIndexOf('/') + 1).trim();
        return clean.length() > 255 ? clean.substring(0, 255) : clean;
    }
}