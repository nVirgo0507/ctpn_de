package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard Controller for Role-Based Dashboards (FR-024)
 * Provides customized information displays per user role per Document requirements
 * 
 * Dashboard Requirements per FR-024:
 * - Member dashboard: personal progress, upcoming sessions, course recommendations
 * - Staff dashboard: program management, user tracking, content moderation
 * - Consultant dashboard: appointments, client management, performance metrics
 * - Manager dashboard: program oversight, analytics, system reports
 * - Admin dashboard: system management, user administration, security monitoring
 */
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get Member Dashboard (FR-024 compliance)
     * Member dashboard: personal progress, upcoming sessions, course recommendations
     */
    @GetMapping("/member")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMemberDashboard(Authentication authentication) {
        try {
            Map<String, Object> dashboard = dashboardService.getMemberDashboard(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy dashboard cá nhân thành công");
            response.put("data", dashboard);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy dashboard cá nhân: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get Staff Dashboard (FR-024 compliance)
     * Staff dashboard: program management, user tracking, content moderation
     */
    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStaffDashboard(Authentication authentication) {
        try {
            Map<String, Object> dashboard = dashboardService.getStaffDashboard(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy dashboard nhân viên thành công");
            response.put("data", dashboard);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy dashboard nhân viên: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get Consultant Dashboard (FR-024 compliance)
     * Consultant dashboard: appointments, client management, performance metrics
     */
    @GetMapping("/consultant")
    @PreAuthorize("hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getConsultantDashboard(Authentication authentication) {
        try {
            Map<String, Object> dashboard = dashboardService.getConsultantDashboard(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy dashboard tư vấn viên thành công");
            response.put("data", dashboard);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy dashboard tư vấn viên: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get Manager Dashboard (FR-024 compliance)
     * Manager dashboard: program oversight, analytics, system reports
     */
    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getManagerDashboard(Authentication authentication) {
        try {
            Map<String, Object> dashboard = dashboardService.getManagerDashboard(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy dashboard quản lý thành công");
            response.put("data", dashboard);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy dashboard quản lý: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get Admin Dashboard (FR-024 compliance)
     * Admin dashboard: system management, user administration, security monitoring
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminDashboard(Authentication authentication) {
        try {
            Map<String, Object> dashboard = dashboardService.getAdminDashboard(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy dashboard admin thành công");
            response.put("data", dashboard);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy dashboard admin: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get real-time dashboard updates
     * FR-024: Real-time data updates and refresh capabilities
     */
    @GetMapping("/refresh")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> refreshDashboardData(
            @RequestParam String role,
            Authentication authentication) {
        try {
            Map<String, Object> refreshData = dashboardService.getRefreshData(role, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật dashboard thành công");
            response.put("data", refreshData);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi cập nhật dashboard: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get dashboard widgets configuration
     * FR-024: Customizable dashboard layouts and widgets
     */
    @GetMapping("/widgets")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardWidgets(
            @RequestParam String role,
            Authentication authentication) {
        try {
            Map<String, Object> widgets = dashboardService.getDashboardWidgets(role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy cấu hình widget thành công");
            response.put("data", widgets);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy cấu hình widget: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Export dashboard data
     * FR-024: Export functionality for dashboard data
     */
    @GetMapping("/export")
    @PreAuthorize("hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> exportDashboardData(
            @RequestParam String role,
            @RequestParam(defaultValue = "json") String format,
            Authentication authentication) {
        try {
            Map<String, Object> exportData = dashboardService.exportDashboardData(role, format, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xuất dữ liệu dashboard thành công");
            response.put("data", exportData);
            response.put("format", format);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi xuất dữ liệu dashboard: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Dashboard health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> dashboardHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Dashboard Controller");
        health.put("roleBasedAccess", true);
        health.put("realTimeUpdates", true);
        health.put("exportCapability", true);
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
} 