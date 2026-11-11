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
import java.util.Base64;
import java.util.Optional;

/**
 * Firebase Admin SDK, used to send push notifications to the mobile app.
 *
 * Credentials = the Firebase service-account JSON (Firebase console → Project settings →
 * Service accounts → Generate new private key). Provide ONE of:
 *   FIREBASE_CREDENTIALS_FILE    path to the JSON file on the server
 *   FIREBASE_CREDENTIALS_BASE64  the JSON file, base64-encoded (handy for Elastic Beanstalk env vars)
 *
 * Without credentials the app still starts; pushes are just skipped (logged once).
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials-file:}")
    private String credentialsFile;

    @Value("${firebase.credentials-base64:}")
    private String credentialsBase64;

    @Bean
    public Optional<FirebaseMessaging> firebaseMessaging() {
        try (InputStream credentials = openCredentials()) {
            if (credentials == null) {
                log.warn("Push notifications are OFF: set FIREBASE_CREDENTIALS_FILE or FIREBASE_CREDENTIALS_BASE64");
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
        if (StringUtils.hasText(credentialsBase64)) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(credentialsBase64.trim()));
        }
        if (StringUtils.hasText(credentialsFile)) {
            return new FileInputStream(credentialsFile.trim());
        }
        return null;
    }
}
