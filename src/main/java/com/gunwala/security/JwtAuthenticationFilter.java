package com.gunwala.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private CustomUserDetailService jwtUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        final String requestTokenHeader =
                request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (
                requestTokenHeader != null &&
                        requestTokenHeader.startsWith("Bearer ")
        ) {

            jwtToken = requestTokenHeader.substring(7);

            try {

                username =
                        jwtTokenHelper.getUsernameFromToken(jwtToken);

            } catch (Exception e) {

                System.out.println(
                        "JWT ERROR: " + e.getMessage()
                );

                chain.doFilter(request, response);
                return;
            }
        }

        if (
                username != null &&
                        SecurityContextHolder.getContext()
                                .getAuthentication() == null
        ) {

            UserDetails userDetails =
                    this.jwtUserDetailsService
                            .loadUserByUsername(username);

            if (
                    jwtTokenHelper.validateToken(
                            jwtToken,
                            userDetails
                    )
            ) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}