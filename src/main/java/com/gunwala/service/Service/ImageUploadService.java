package com.gunwala.service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
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

//    public String uploadFile(MultipartFile file) throws IOException {
//        String fileKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        PutObjectRequest putRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileKey)
//                .contentType(file.getContentType())
//                .build();
//
//        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
//
//        // return just the key (to save in DB)
//        return fileKey;
//    }
//
//    public String generatePresignedUrl(String fileKey) {
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileKey)
//                .build();
//
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(15)) // URL valid for 15 minutes
//                .getObjectRequest(getObjectRequest)
//                .build();
//
//        return s3Presigner.presignGetObject(presignRequest).url().toString();
//    }

    private static final long MAX_SIZE_BYTES = 2L * 1024 * 1024; // 2 MB target
    private static final int MAX_DIMENSION = 1920;               // longest side in px
    private static final float START_QUALITY = 0.85f;
    private static final float MIN_QUALITY = 0.40f;
    private static final int MIN_DIMENSION = 400;

    // Formats Java ImageIO can read out of the box (GIF skipped to keep animations intact)
    private static final Set<String> COMPRESSIBLE_TYPES =
            Set.of("image/jpeg", "image/jpg", "image/png", "image/bmp");

    public String uploadFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";

        // Compress only images that are over the limit
        if (contentType != null
                && COMPRESSIBLE_TYPES.contains(contentType.toLowerCase())
                && bytes.length > MAX_SIZE_BYTES) {

            byte[] compressed = compressImage(bytes);
            if (compressed != bytes) {                 // compression actually happened
                bytes = compressed;
                contentType = "image/jpeg";
                fileName = replaceExtension(fileName, "jpg");
            }
        }

        String fileKey = UUID.randomUUID() + "_" + fileName.replaceAll("\\s+", "_");

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));

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

    // ---------------- Image compression helpers ----------------

    /**
     * Resizes and re-encodes the image as JPEG until it is at or below MAX_SIZE_BYTES.
     * Returns the original array unchanged if the image cannot be decoded.
     */
    private byte[] compressImage(byte[] original) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(original));
        if (image == null) {
            return original; // not a readable image; upload as-is
        }

        // Step 1: cap dimensions (biggest win for phone photos)
        image = toRgb(resizeToFit(image, MAX_DIMENSION));

        // Step 2: lower JPEG quality step by step
        float quality = START_QUALITY;
        byte[] result = writeJpeg(image, quality);
        while (result.length > MAX_SIZE_BYTES && quality > MIN_QUALITY) {
            quality -= 0.10f;
            result = writeJpeg(image, quality);
        }

        // Step 3: still too large -> shrink dimensions further
        while (result.length > MAX_SIZE_BYTES && Math.max(image.getWidth(), image.getHeight()) > MIN_DIMENSION) {
            image = scale(image, 0.75);
            result = writeJpeg(image, quality);
        }

        return result;
    }

    private BufferedImage resizeToFit(BufferedImage src, int maxDim) {
        int w = src.getWidth();
        int h = src.getHeight();
        int longest = Math.max(w, h);
        if (longest <= maxDim) {
            return src;
        }
        return scale(src, (double) maxDim / longest);
    }

    private BufferedImage scale(BufferedImage src, double factor) {
        int newW = Math.max(1, (int) Math.round(src.getWidth() * factor));
        int newH = Math.max(1, (int) Math.round(src.getHeight() * factor));

        BufferedImage out = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, newW, newH, null);
        g.dispose();
        return out;
    }

    /** JPEG has no transparency: paint onto a white background (fixes black PNG backgrounds). */
    private BufferedImage toRgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB) {
            return src;
        }
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, src.getWidth(), src.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return rgb;
    }

    private byte[] writeJpeg(BufferedImage image, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG writer available");
        }
        ImageWriter writer = writers.next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(Math.max(0.05f, Math.min(1f, quality)));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
        return baos.toByteArray();
    }

    private String replaceExtension(String fileName, String newExt) {
        int dot = fileName.lastIndexOf('.');
        String base = dot > 0 ? fileName.substring(0, dot) : fileName;
        return base + "." + newExt;
    }
}
