package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blog Controller - Complete Blog Management System
 * Author: FullStack-Developer-AI (Cursor)
 * Created: Session 12
 * Version: 1.0
 * Context: Implements FR-015 Blog Management System per Document specifications
 */
@RestController
@RequestMapping("/blog")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BlogController {
    
    @Autowired
    private BlogService blogService;
    
    /**
     * Get published blog posts for public access (FR-001 Homepage requirement)
     * Accessible by all users including guests
     */
    @GetMapping("/public")
    public ResponseEntity<?> getPublishedBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
            Page<Map<String, Object>> blogPosts = blogService.getPublishedBlogPosts(pageable, category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách blog thành công");
            response.put("data", blogPosts);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách blog: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get latest blog posts for homepage (FR-001 requirement)
     * Shows latest 5 blog posts with titles and excerpts
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestBlogPosts() {
        try {
            List<Map<String, Object>> latestPosts = blogService.getLatestBlogPosts(5);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy blog mới nhất thành công");
            response.put("data", latestPosts);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy blog mới nhất: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get blog post by ID for public reading
     */
    @GetMapping("/public/{postId}")
    public ResponseEntity<?> getBlogPostById(@PathVariable Long postId) {
        try {
            Map<String, Object> blogPost = blogService.getBlogPostById(postId, true);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy nội dung blog thành công");
            response.put("data", blogPost);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy nội dung blog: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Create new blog post (Staff+ only per FR-015)
     * Rich text editor support with image upload
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> createBlogPost(
            @RequestBody Map<String, Object> blogPostData,
            Authentication authentication) {
        try {
            Map<String, Object> createdPost = blogService.createBlogPost(blogPostData, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo bài viết thành công. Đã gửi để phê duyệt.");
            response.put("data", createdPost);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi tạo bài viết: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get blog posts for management (includes drafts and pending approval)
     * Staff can see their own posts, Manager+ can see all
     */
    @GetMapping("/manage")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getBlogPostsForManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Map<String, Object>> blogPosts = blogService.getBlogPostsForManagement(
                pageable, status, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách bài viết quản lý thành công");
            response.put("data", blogPosts);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách bài viết: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Approve blog post (Manager+ only per FR-015 approval workflow)
     */
    @PutMapping("/approve/{postId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> approveBlogPost(
            @PathVariable Long postId,
            Authentication authentication) {
        try {
            Map<String, Object> approvedPost = blogService.approveBlogPost(postId, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bài viết đã được phê duyệt và xuất bản");
            response.put("data", approvedPost);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi phê duyệt bài viết: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Reject blog post (Manager+ only)
     */
    @PutMapping("/reject/{postId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> rejectBlogPost(
            @PathVariable Long postId,
            @RequestBody Map<String, String> rejectionData,
            Authentication authentication) {
        try {
            String rejectionReason = rejectionData.get("reason");
            Map<String, Object> rejectedPost = blogService.rejectBlogPost(
                postId, rejectionReason, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bài viết đã bị từ chối");
            response.put("data", rejectedPost);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi từ chối bài viết: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Add comment to blog post (Member+ only per FR-015)
     */
    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> commentData,
            Authentication authentication) {
        try {
            String content = commentData.get("content");
            Map<String, Object> comment = blogService.addComment(postId, content, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bình luận đã được thêm");
            response.put("data", comment);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi thêm bình luận: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get comments for a blog post
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
            Page<Map<String, Object>> comments = blogService.getComments(postId, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách bình luận thành công");
            response.put("data", comments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy bình luận: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get blog categories for filtering and organization
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getBlogCategories() {
        try {
            List<Map<String, Object>> categories = blogService.getBlogCategories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh mục blog thành công");
            response.put("data", categories);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh mục blog: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Like/unlike blog post (Member+ only per FR-016)
     */
    @PostMapping("/{postId}/like")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            Authentication authentication) {
        try {
            Map<String, Object> result = blogService.toggleLike(postId, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật lượt thích thành công");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi cập nhật lượt thích: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 