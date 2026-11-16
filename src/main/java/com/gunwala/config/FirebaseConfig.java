package com.gunwala.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Firebase Admin SDK, used to send push notifications to the mobile app.
 *
 * Credentials = the Firebase service-account JSON (Firebase console → Project settings →
 * Service accounts → Generate new private key). Provide ONE of:
 *   FIREBASE_CREDENTIALS_JSON    the JSON itself. Recommended on Elastic Beanstalk: store the JSON
 *                                in AWS Secrets Manager and add this env var with Source =
 *                                "Secrets Manager" (EB then injects the secret's value, not the ARN)
 *   FIREBASE_CREDENTIALS_FILE    path to the JSON file on the server
 *   FIREBASE_CREDENTIALS_BASE64  the JSON file, base64-encoded
 *
 * Without credentials the app still starts; pushes are just skipped (logged once).
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials-json:}")
    private String credentialsJson;

    @Value("${firebase.credentials-file:}")
    private String credentialsFile;

    @Value("${firebase.credentials-base64:}")
    private String credentialsBase64;

    @Bean
    public Optional<FirebaseMessaging> firebaseMessaging() {
        try (InputStream credentials = openCredentials()) {
            if (credentials == null) {
                log.warn("Push notifications are OFF: set FIREBASE_CREDENTIALS_JSON, FIREBASE_CREDENTIALS_FILE or FIREBASE_CREDENTIALS_BASE64");
                return Optional.empty();
            }
            FirebaseApp app = FirebaseApp.getApps().isEmpty()
                    ? FirebaseApp.initializeApp(FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentials))
                    .build())
                    : FirebaseApp.getInstance();
            log.info("Push notifications are ON (Firebase project: {})", app.getOptions().getProjectId());
            return Optional.of(FirebaseMessaging.getInstance(app));
        } catch (IOException | RuntimeException e) {
            // Bad credentials must not stop the whole backend from starting
            log.error("Push notifications are OFF: could not initialise Firebase", e);
            return Optional.empty();
        }
    }

    private InputStream openCredentials() throws IOException {
        if (StringUtils.hasText(credentialsJson)) {
            String json = credentialsJson.trim();
            if (json.startsWith("arn:")) {
                // The env var holds the secret's ARN instead of its value.
                log.error("FIREBASE_CREDENTIALS_JSON contains a Secrets Manager ARN, not the JSON. "
                        + "In Elastic Beanstalk set this variable's Source to \"Secrets Manager\" "
                        + "(not \"Plain text\") so the secret's value is injected.");
                return null;
            }
            return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        }
        if (StringUtils.hasText(credentialsBase64)) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(credentialsBase64.trim()));
        }
        if (StringUtils.hasText(credentialsFile)) {
            return new FileInputStream(credentialsFile.trim());
        }
        return null;
    }
}