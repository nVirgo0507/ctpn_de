package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

@RestController
@RequestMapping("/admin") // Corrected: Removed /api prefix
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = adminService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<java.util.List<org.ctpn.chungtayphongngua.entity.User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@org.springframework.web.bind.annotation.PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "User deleted successfully"));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @org.springframework.web.bind.annotation.PathVariable Long userId,
            @org.springframework.web.bind.annotation.RequestBody Map<String, String> request) {
        String roleName = request.get("roleName");
        adminService.updateUserRole(userId, roleName);
        return ResponseEntity.ok(Map.of("success", true, "message", "User role updated successfully"));
    }
}
