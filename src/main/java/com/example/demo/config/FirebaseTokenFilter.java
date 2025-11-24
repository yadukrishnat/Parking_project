package com.example.demo.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    // Public endpoints that do not require token
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/register",
            "/api/login",
            "/api/test",
            "/register",
            "/login",
            "/h2-console"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip validation for public paths
        for (String p : PUBLIC_PATHS) {
            if (path.startsWith(p)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");
        logger.info("Incoming request to: " + path + " | Authorization header: " + authHeader);

        if (authHeader == null || authHeader.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Missing Authorization header\"}");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid Authorization header format\"}");
            return;
        }

        String token = authHeader.substring(7).trim(); // remove "Bearer "

        try {
            // Verify token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            // Add default ROLE_USER
//            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // TODO: If you want, fetch user from DB and add ROLE_LAND_OWNER dynamically
            // if (user.getUserType().equals("LAND_OWNER")) {
            //     authorities.add(new SimpleGrantedAuthority("ROLE_LAND_OWNER"));
            // }
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            decodedToken.getUid(),
//                            null,
//                            authorities
//                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Forward request
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Firebase token verification failed: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid Token\"}");
        }
    }
}
