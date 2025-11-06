package com.mineCryptos.service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
public class ImageUploadService {
//    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
//
//    public ImageUploadService(S3Client s3Client) {
//        this.s3Client = s3Client;
//    }

//    public String uploadFile(MultipartFile file) throws IOException {
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        PutObjectRequest request = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .contentType(file.getContentType())
//                .build();
//
//        s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
//
//        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
//    }


    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public ImageUploadService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

//    public String uploadFile(MultipartFile file) throws IOException {
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .contentType(file.getContentType())
//                .build();
//
//        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//
//        // Generate presigned URL (valid for 15 minutes)
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .build();
//
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(15))
//                .getObjectRequest(getObjectRequest)
//                .build();
//
//        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
//
//        return presignedUrl.toString();
//    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileKey = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        // return just the key (to save in DB)
        return fileKey;
    }

    public String generatePresignedUrl(String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // URL valid for 15 minutes
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
