package org.ctpn.chungtayphongngua.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Request Filter for processing JWT tokens in HTTP requests
 * Implements role-based authentication per Document security requirements
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestPath = request.getRequestURI();
        System.out.println("DEBUG: Checking if should skip JWT filter for: " + requestPath);
        boolean skip = isPublicEndpoint(requestPath);
        if (skip) {
            System.out.println("DEBUG: Skipping JWT filter entirely for public endpoint: " + requestPath);
        }
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain chain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        String role = null;
        
        // JWT Token is in the form "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
                role = jwtUtil.extractRole(jwtToken);
            } catch (Exception e) {
                logger.warn("JWT Token extraction failed: " + e.getMessage());
            }
        }
        
        // Validate token and set authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Create UserDetails from JWT token
            UserDetails userDetails = User.builder()
                    .username(username)
                    .password("") // Password not needed for JWT authentication
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())))
                    .build();
                
            System.out.println("DEBUG: Creating authority: ROLE_" + role.toUpperCase() + " for user: " + username);
            
            // Validate token
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        chain.doFilter(request, response);
    }

    /**
     * Check if the request path is a public endpoint that should skip JWT authentication
     * Supporting FR-002 Public Content Access requirements
     */
    private boolean isPublicEndpoint(String requestPath) {
        // Public endpoints that don't require authentication per SecurityConfig
        String[] publicPaths = {
            "/api/auth/",
            "/api/actuator/",
            "/api/surveys/public/",
            "/api/surveys/submit/",
            "/api/surveys/health",
            "/api/blog/public/",
            "/api/blog/latest",
            "/api/blog/categories",
            "/api/homepage/",
            "/api/public/",
            "/api/courses/health",
            "/api/courses",
            "/api/dashboard/health",
            "/api/test/"
        };
        
        System.out.println("DEBUG: Checking public endpoint for path: " + requestPath);
        
        for (String publicPath : publicPaths) {
            if (requestPath.startsWith(publicPath) || requestPath.equals(publicPath.substring(0, publicPath.length() - 1))) {
                System.out.println("DEBUG: Path " + requestPath + " matches public path: " + publicPath);
                return true;
            }
        }
        
        System.out.println("DEBUG: Path " + requestPath + " is NOT public");
        return false;
    }
} 