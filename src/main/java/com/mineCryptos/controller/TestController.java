package com.mineCryptos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;

@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @PostMapping("/test-webhook")
    public ResponseEntity<String> testWebhook(
            @RequestBody(required = false) String body,
            HttpServletRequest request) {

        logger.info("=== TEST WEBHOOK CALLED ===");
        logger.info("Method: {}", request.getMethod());
        logger.info("Content-Type: {}", request.getContentType());
        logger.info("Body: {}", body);

        // Log all headers
        Collections.list(request.getHeaderNames()).forEach(header -> {
            logger.info("Header {}: {}", header, request.getHeader(header));
        });

        return ResponseEntity.ok("Test received at: " + new Date());
    }

    @GetMapping("/test-get")
    public ResponseEntity<String> testGet() {
        logger.info("=== TEST GET CALLED ===");
        return ResponseEntity.ok("GET works at: " + new Date());
    }
}
