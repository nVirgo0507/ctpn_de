package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.dto.request.LoginRequest;
import org.ctpn.chungtayphongngua.dto.request.RegisterRequest;
import org.ctpn.chungtayphongngua.dto.response.AuthResponse;
import org.ctpn.chungtayphongngua.entity.Role;
import org.ctpn.chungtayphongngua.entity.User;
import org.ctpn.chungtayphongngua.entity.UserRole;
import org.ctpn.chungtayphongngua.repository.RoleRepository;
import org.ctpn.chungtayphongngua.repository.UserRepository;
import org.ctpn.chungtayphongngua.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Authentication Service implementation per Document/functional_requirements_state_1.md
 * Handles user registration (FR-003) and authentication (FR-004) with RBAC (FR-005)
 */
@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private NotificationService notificationService;
    
    /**
     * User registration per FR-003 requirements
     * - Email signup with password policy enforcement
     * - Automatic Member role assignment
     * - Auto-verification for development environment
     */
    public AuthResponse registerUser(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        
        // Auto-verify user for development convenience
        user.setIsVerified(true);
        
        // Save user
        user = userRepository.save(user);
        
        // Assign Member role by fetching it from the database
        Role memberRole = roleRepository.findByRoleName("MEMBER")
                .orElseThrow(() -> new RuntimeException("Error: Role MEMBER is not found."));
        
        UserRole userRole = new UserRole(user, memberRole);
        
        // Initialize userRoles if null
        if (user.getUserRoles() == null) {
            user.setUserRoles(new java.util.HashSet<>());
        }
        user.getUserRoles().add(userRole);
        
        // Save user with role
        user = userRepository.save(user);

        // Create a welcome notification
        notificationService.createWelcomeNotification(user);
        
        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("")
                .authorities(Collections.emptyList())
                .build();
        
        String token = jwtUtil.generateToken(userDetails, "MEMBER");
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30); // Member session timeout
        
        return new AuthResponse(token, user.getEmail(), user.getFullName(), 
                               "MEMBER", expiresAt, user.getIsVerified());
    }
    
    /**
     * User authentication per FR-004 requirements
     * - Email/password login with session management
     * - Role-based session timeout enforcement
     * - Failed login protection (handled by Spring Security)
     */
    public AuthResponse authenticateUser(LoginRequest request) {
        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));
        
        // Authenticate user
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        
        // Get user role
        String roleName = user.getUserRoles().stream()
                .findFirst()
                .map(ur -> ur.getRole().getRoleName())
                .orElse("MEMBER");
        
        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT token with role-based expiration
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("")
                .authorities(Collections.emptyList())
                .build();
        
        String token = jwtUtil.generateToken(userDetails, roleName);
        LocalDateTime expiresAt = getExpirationTimeForRole(roleName);
        
        return new AuthResponse(token, user.getEmail(), user.getFullName(), 
                               roleName, expiresAt, user.getIsVerified());
    }
    
    /**
     * Email verification per FR-003 requirements
     * - Account verification within 60 seconds of OTP delivery
     */
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByValidVerificationToken(token, LocalDateTime.now());
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        
        userRepository.save(user);
        return true;
    }
    
    /**
     * Get role-based session expiration per RBAC matrix
     */
    private LocalDateTime getExpirationTimeForRole(String role) {
        long minutes = switch (role.toUpperCase()) {
            case "ADMIN" -> 15; // 15 minutes for highest security
            case "MANAGER", "STAFF", "CONSULTANT", "MEMBER" -> 30; // 30 minutes
            default -> 30;
        };
        
        return LocalDateTime.now().plusMinutes(minutes);
    }
} 