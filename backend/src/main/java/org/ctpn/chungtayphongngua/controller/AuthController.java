package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.dto.request.LoginRequest;
import org.ctpn.chungtayphongngua.dto.request.RegisterRequest;
import org.ctpn.chungtayphongngua.dto.response.AuthResponse;
import org.ctpn.chungtayphongngua.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Auth controller is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.registerUser(request);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
            responseBody.put("data", response);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticateUser(request);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Đăng nhập thành công!");
            responseBody.put("data", response);
            
            return ResponseEntity.ok(responseBody);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            boolean verified = authService.verifyEmail(token);
            
            Map<String, Object> responseBody = new HashMap<>();
            if (verified) {
                responseBody.put("success", true);
                responseBody.put("message", "Xác thực email thành công!");
            } else {
                responseBody.put("success", false);
                responseBody.put("message", "Token xác thực không hợp lệ hoặc đã hết hạn.");
            }
            
            return ResponseEntity.ok(responseBody);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi xác thực email: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 