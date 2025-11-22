package com.mineCryptos.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WebhookLoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(WebhookLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log all POST requests (especially webhooks)
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            logger.info("=== INCOMING POST REQUEST ===");
            logger.info("URL: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            logger.info("Remote IP: {}", httpRequest.getRemoteAddr());
            logger.info("Content-Type: {}", httpRequest.getContentType());
            logger.info("User-Agent: {}", httpRequest.getHeader("User-Agent"));

            // Log all headers
            java.util.Collections.list(httpRequest.getHeaderNames()).forEach(headerName -> {
                if (headerName.toLowerCase().contains("nowpayments") ||
                        headerName.toLowerCase().contains("sig") ||
                        headerName.equalsIgnoreCase("content-type") ||
                        headerName.equalsIgnoreCase("user-agent")) {
                    logger.info("Header {}: {}", headerName, httpRequest.getHeader(headerName));
                }
            });
        }

        chain.doFilter(request, response);

        // Log response status for POST requests
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            logger.info("Response Status: {} for {}", httpResponse.getStatus(), httpRequest.getRequestURI());
        }
    }
}
