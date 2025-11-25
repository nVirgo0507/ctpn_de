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

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalConsultations", consultationRepository.count());
        stats.put("totalBlogPosts", blogPostRepository.count());
        return stats;
    }
}
