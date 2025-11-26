package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalConsultations", consultationRepository.count());
        stats.put("totalBlogPosts", blogPostRepository.count());
        return stats;
    }

    public java.util.List<org.ctpn.chungtayphongngua.entity.User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        org.ctpn.chungtayphongngua.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    public void updateUserRole(Long userId, String roleName) {
        org.ctpn.chungtayphongngua.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        org.ctpn.chungtayphongngua.entity.Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Clear existing roles and add new one (assuming single role for simplicity, or
        // modify logic for multiple)
        // For this implementation, we'll remove all existing roles and add the new one
        userRoleRepository.deleteByUser(user);

        org.ctpn.chungtayphongngua.entity.UserRole userRole = new org.ctpn.chungtayphongngua.entity.UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedAt(java.time.LocalDateTime.now());

        userRoleRepository.save(userRole);
    }
}
