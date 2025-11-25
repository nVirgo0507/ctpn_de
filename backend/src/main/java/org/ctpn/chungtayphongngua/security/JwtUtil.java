package org.ctpn.chungtayphongngua.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility class implementation per Document security requirements
 * Supports role-based session timeouts from Document/rbac_matrix_state_1.md
 */
@Component
public class JwtUtil {
    
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    // Role-based session timeouts per Document specifications
    private static final long MEMBER_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    private static final long STAFF_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    private static final long CONSULTANT_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    private static final long MANAGER_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    private static final long ADMIN_TIMEOUT = 15 * 60 * 1000; // 15 minutes
    private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000; // Default 30 minutes
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    public String generateToken(UserDetails userDetails, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, userDetails.getUsername(), role);
    }
    
    private String createToken(Map<String, Object> claims, String subject, String role) {
        long expirationTime = getExpirationTimeForRole(role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * Get role-based session timeout per Document/rbac_matrix_state_1.md
     * Security-first policy: shorter timeouts for higher privileges
     */
    private long getExpirationTimeForRole(String role) {
        if (role == null) {
            return DEFAULT_TIMEOUT;
        }
        
        return switch (role.toUpperCase()) {
            case "ADMIN" -> ADMIN_TIMEOUT; // 15 minutes - highest security
            case "MANAGER" -> MANAGER_TIMEOUT; // 30 minutes
            case "STAFF" -> STAFF_TIMEOUT; // 30 minutes
            case "CONSULTANT" -> CONSULTANT_TIMEOUT; // 30 minutes
            case "MEMBER" -> MEMBER_TIMEOUT; // 30 minutes
            default -> DEFAULT_TIMEOUT; // 30 minutes default
        };
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public Boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Refresh token if it's still valid and not close to expiration
     * Prevents frequent re-authentication while maintaining security
     */
    public String refreshTokenIfNeeded(String token, UserDetails userDetails, String role) {
        try {
            if (!validateToken(token, userDetails)) {
                return null;
            }
            
            Date expiration = extractExpiration(token);
            long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
            long refreshThreshold = getExpirationTimeForRole(role) / 4; // Refresh when 25% time left
            
            if (timeUntilExpiration < refreshThreshold) {
                return generateToken(userDetails, role);
            }
            
            return token; // No refresh needed
        } catch (Exception e) {
            return null;
        }
    }
} 