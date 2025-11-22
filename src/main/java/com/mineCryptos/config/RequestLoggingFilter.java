package com.mineCryptos.config;

import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

@Component
@Order(1)
public class RequestLoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        logger.info("=== INCOMING REQUEST ===");
        logger.info("Method: {}", httpRequest.getMethod());
        logger.info("URI: {}", httpRequest.getRequestURI());
        logger.info("Remote Addr: {}", httpRequest.getRemoteAddr());
        logger.info("Content-Type: {}", httpRequest.getContentType());
        logger.info("Headers:");

        Collections.list(httpRequest.getHeaderNames()).forEach(headerName -> {
            logger.info("  {}: {}", headerName, httpRequest.getHeader(headerName));
        });

        chain.doFilter(request, response);
    }
}
