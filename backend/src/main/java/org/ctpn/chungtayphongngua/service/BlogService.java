package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.entity.User;
import org.ctpn.chungtayphongngua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Blog Service - Complete Blog Management Implementation
 * Author: FullStack-Developer-AI (Cursor)
 * Created: Session 12
 * Version: 1.0
 * Context: Implements FR-015 Blog Management System per Document specifications
 */
@Service
@Transactional
public class BlogService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get published blog posts for public access
     * Supports pagination and category filtering
     */
    public Page<Map<String, Object>> getPublishedBlogPosts(Pageable pageable, String category) {
        List<Map<String, Object>> blogPosts = new ArrayList<>();
        long totalElements = 0;

        try (Connection conn = dataSource.getConnection()) {
            // Build query with optional category filter
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT bp.post_id, bp.title, bp.excerpt, bp.featured_image, ")
                    .append("bp.published_at, bp.view_count, bp.likes_count, ")
                    .append("u.full_name as author_name, c.name as category_name ")
                    .append("FROM ctpn_content.blog_posts bp ")
                    .append("JOIN ctpn_core.users u ON bp.author_id = u.user_id ")
                    .append("LEFT JOIN ctpn_content.categories c ON bp.category_id = c.category_id ")
                    .append("WHERE bp.status = 'published' AND bp.is_deleted = false ");

            if (category != null && !category.isEmpty()) {
                queryBuilder.append("AND c.name = ? ");
            }

            queryBuilder.append("ORDER BY bp.published_at DESC ")
                    .append("LIMIT ? OFFSET ?");

            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());
            int paramIndex = 1;

            if (category != null && !category.isEmpty()) {
                stmt.setString(paramIndex++, category);
            }

            stmt.setInt(paramIndex++, pageable.getPageSize());
            stmt.setInt(paramIndex, pageable.getPageNumber() * pageable.getPageSize());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> post = new HashMap<>();
                post.put("postId", rs.getLong("post_id"));
                post.put("title", rs.getString("title"));
                post.put("excerpt", rs.getString("excerpt"));
                post.put("featuredImage", rs.getString("featured_image"));
                post.put("publishedAt", rs.getTimestamp("published_at"));
                post.put("viewsCount", rs.getInt("view_count"));
                post.put("likesCount", rs.getInt("likes_count"));
                post.put("authorName", rs.getString("author_name"));
                post.put("categoryName", rs.getString("category_name"));

                blogPosts.add(post);
            }

            // Get total count for pagination
            StringBuilder countQuery = new StringBuilder();
            countQuery.append("SELECT COUNT(*) FROM ctpn_content.blog_posts bp ")
                    .append("LEFT JOIN ctpn_content.categories c ON bp.category_id = c.category_id ")
                    .append("WHERE bp.status = 'published' AND bp.is_deleted = false ");

            if (category != null && !category.isEmpty()) {
                countQuery.append("AND c.name = ?");
            }

            PreparedStatement countStmt = conn.prepareStatement(countQuery.toString());
            if (category != null && !category.isEmpty()) {
                countStmt.setString(1, category);
            }

            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalElements = countRs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách blog: " + e.getMessage(), e);
        }

        return new PageImpl<>(blogPosts, pageable, totalElements);
    }

    /**
     * Get latest blog posts for homepage (FR-001 requirement)
     */
    public List<Map<String, Object>> getLatestBlogPosts(int limit) {
        List<Map<String, Object>> latestPosts = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT bp.post_id, bp.title, bp.excerpt, bp.featured_image, " +
                    "bp.published_at, u.full_name as author_name, c.name as category_name " +
                    "FROM ctpn_content.blog_posts bp " +
                    "JOIN ctpn_core.users u ON bp.author_id = u.user_id " +
                    "LEFT JOIN ctpn_content.categories c ON bp.category_id = c.category_id " +
                    "WHERE bp.status = 'published' AND bp.is_deleted = false " +
                    "ORDER BY bp.published_at DESC LIMIT ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, limit);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> post = new HashMap<>();
                post.put("postId", rs.getLong("post_id"));
                post.put("title", rs.getString("title"));
                post.put("excerpt", rs.getString("excerpt"));
                post.put("featuredImage", rs.getString("featured_image"));
                post.put("publishedAt", rs.getTimestamp("published_at"));
                post.put("authorName", rs.getString("author_name"));
                post.put("categoryName", rs.getString("category_name"));

                latestPosts.add(post);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy blog mới nhất: " + e.getMessage(), e);
        }

        return latestPosts;
    }

    /**
     * Get blog post by ID with view count increment
     */
    public Map<String, Object> getBlogPostById(Long postId, boolean incrementViews) {
        Map<String, Object> blogPost = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT bp.post_id, bp.title, bp.content, bp.excerpt, bp.featured_image, " +
                    "bp.published_at, bp.view_count, bp.likes_count, " +
                    "u.full_name as author_name, u.profile_picture_url as author_avatar, c.name as category_name " +
                    "FROM ctpn_content.blog_posts bp " +
                    "JOIN ctpn_core.users u ON bp.author_id = u.user_id " +
                    "LEFT JOIN ctpn_content.categories c ON bp.category_id = c.category_id " +
                    "WHERE bp.post_id = ? AND bp.status = 'published' AND bp.is_deleted = false";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, postId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                blogPost.put("postId", rs.getLong("post_id"));
                blogPost.put("title", rs.getString("title"));
                blogPost.put("content", rs.getString("content"));
                blogPost.put("excerpt", rs.getString("excerpt"));
                blogPost.put("featuredImage", rs.getString("featured_image"));
                blogPost.put("publishedAt", rs.getTimestamp("published_at"));
                blogPost.put("viewsCount", rs.getInt("view_count"));
                blogPost.put("likesCount", rs.getInt("likes_count"));
                blogPost.put("authorName", rs.getString("author_name"));
                blogPost.put("authorAvatar", rs.getString("author_avatar"));
                blogPost.put("categoryName", rs.getString("category_name"));

                // Calculate read time (approx 200 words per minute)
                String content = rs.getString("content");
                int wordCount = content != null ? content.split("\\s+").length : 0;
                int readTime = (int) Math.ceil(wordCount / 200.0);
                blogPost.put("readTime", readTime > 0 ? readTime : 1);

                // Increment view count if requested
                if (incrementViews) {
                    String updateQuery = "UPDATE ctpn_content.blog_posts " +
                            "SET view_count = view_count + 1 " +
                            "WHERE post_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setLong(1, postId);
                    updateStmt.executeUpdate();

                    // Update the returned count
                    blogPost.put("viewsCount", rs.getInt("view_count") + 1);
                }
            } else {
                throw new RuntimeException("Không tìm thấy bài viết");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy nội dung blog: " + e.getMessage(), e);
        }

        return blogPost;
    }

    /**
     * Create new blog post (Staff+ only)
     */
    public Map<String, Object> createBlogPost(Map<String, Object> blogPostData, String userEmail) {
        Map<String, Object> createdPost = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            // Get user ID
            User author = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            String query = "INSERT INTO ctpn_content.blog_posts " +
                    "(author_id, category_id, title, content, excerpt, featured_image, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'draft') RETURNING post_id";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, author.getUserId());
            stmt.setObject(2, blogPostData.get("categoryId"));
            stmt.setString(3, (String) blogPostData.get("title"));
            stmt.setString(4, (String) blogPostData.get("content"));
            stmt.setString(5, (String) blogPostData.get("excerpt"));
            stmt.setString(6, (String) blogPostData.get("featuredImage"));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                createdPost.put("postId", rs.getLong("post_id"));
                createdPost.put("title", blogPostData.get("title"));
                createdPost.put("status", "draft");
                createdPost.put("createdAt", LocalDateTime.now());
                createdPost.put("authorName", author.getFullName());
                createdPost.put("message", "Bài viết đã được tạo và đang chờ phê duyệt");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo bài viết: " + e.getMessage(), e);
        }

        return createdPost;
    }

    /**
     * Get blog posts for management dashboard
     */
    public Page<Map<String, Object>> getBlogPostsForManagement(Pageable pageable, String status, String userEmail) {
        List<Map<String, Object>> blogPosts = new ArrayList<>();
        long totalElements = 0;

        try (Connection conn = dataSource.getConnection()) {
            // Check if user is Manager+ (can see all posts) or Staff (can see only own
            // posts)
            boolean isManagerOrAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) ||
                    SecurityContextHolder.getContext().getAuthentication()
                            .getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT bp.post_id, bp.title, bp.excerpt, bp.status, ")
                    .append("bp.published_at, bp.view_count, bp.likes_count, ")
                    .append("u.full_name as author_name, c.name as category_name ")
                    .append("FROM ctpn_content.blog_posts bp ")
                    .append("JOIN ctpn_core.users u ON bp.author_id = u.user_id ")
                    .append("LEFT JOIN ctpn_content.categories c ON bp.category_id = c.category_id ")
                    .append("WHERE bp.is_deleted = false ");

            if (!isManagerOrAdmin) {
                queryBuilder.append("AND u.email = ? ");
            }

            if (status != null && !status.isEmpty()) {
                queryBuilder.append("AND bp.status = ? ");
            }

            queryBuilder.append("ORDER BY bp.post_id DESC LIMIT ? OFFSET ?");

            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());
            int paramIndex = 1;

            if (!isManagerOrAdmin) {
                stmt.setString(paramIndex++, userEmail);
            }

            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }

            stmt.setInt(paramIndex++, pageable.getPageSize());
            stmt.setInt(paramIndex, pageable.getPageNumber() * pageable.getPageSize());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> post = new HashMap<>();
                post.put("postId", rs.getLong("post_id"));
                post.put("title", rs.getString("title"));
                post.put("excerpt", rs.getString("excerpt"));
                post.put("status", rs.getString("status"));
                post.put("publishedAt", rs.getTimestamp("published_at"));
                post.put("viewsCount", rs.getInt("view_count"));
                post.put("likesCount", rs.getInt("likes_count"));
                post.put("authorName", rs.getString("author_name"));
                post.put("categoryName", rs.getString("category_name"));

                blogPosts.add(post);
            }

            // Get total count
            StringBuilder countQuery = new StringBuilder();
            countQuery.append("SELECT COUNT(*) FROM ctpn_content.blog_posts bp ")
                    .append("JOIN ctpn_core.users u ON bp.author_id = u.user_id ")
                    .append("WHERE bp.is_deleted = false ");

            if (!isManagerOrAdmin) {
                countQuery.append("AND u.email = ? ");
            }

            if (status != null && !status.isEmpty()) {
                countQuery.append("AND bp.status = ? ");
            }

            PreparedStatement countStmt = conn.prepareStatement(countQuery.toString());
            paramIndex = 1;

            if (!isManagerOrAdmin) {
                countStmt.setString(paramIndex++, userEmail);
            }

            if (status != null && !status.isEmpty()) {
                countStmt.setString(paramIndex++, status);
            }

            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalElements = countRs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách bài viết quản lý: " + e.getMessage(), e);
        }

        return new PageImpl<>(blogPosts, pageable, totalElements);
    }

    /**
     * Approve blog post (Manager+ only)
     */
    public Map<String, Object> approveBlogPost(Long postId, String approverEmail) {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "UPDATE ctpn_content.blog_posts " +
                    "SET status = 'published', published_at = CURRENT_TIMESTAMP " +
                    "WHERE post_id = ? AND status = 'draft'";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, postId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                result.put("postId", postId);
                result.put("status", "published");
                result.put("publishedAt", LocalDateTime.now());
                result.put("approvedBy", approverEmail);
            } else {
                throw new RuntimeException(
                        "Không thể phê duyệt bài viết. Bài viết có thể đã được xuất bản hoặc không tồn tại.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi phê duyệt bài viết: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Reject blog post (Manager+ only)
     */
    public Map<String, Object> rejectBlogPost(Long postId, String rejectionReason, String rejectorEmail) {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "UPDATE ctpn_content.blog_posts " +
                    "SET status = 'rejected' " +
                    "WHERE post_id = ? AND status = 'draft'";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, postId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                result.put("postId", postId);
                result.put("status", "rejected");
                result.put("rejectionReason", rejectionReason);
                result.put("rejectedBy", rejectorEmail);
                result.put("rejectedAt", LocalDateTime.now());
            } else {
                throw new RuntimeException(
                        "Không thể từ chối bài viết. Bài viết có thể đã được xử lý hoặc không tồn tại.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi từ chối bài viết: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Add comment to blog post
     */
    public Map<String, Object> addComment(Long postId, String content, String userEmail) {
        Map<String, Object> comment = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            // Get user ID
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            String query = "INSERT INTO ctpn_content.blog_comments " +
                    "(post_id, user_id, content, status) " +
                    "VALUES (?, ?, ?, 'pending') RETURNING comment_id, created_at";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, postId);
            stmt.setLong(2, user.getUserId());
            stmt.setString(3, content);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                comment.put("commentId", rs.getLong("comment_id"));
                comment.put("content", content);
                comment.put("userName", user.getFullName());
                comment.put("status", "pending");
                comment.put("createdAt", rs.getTimestamp("created_at"));
                comment.put("message", "Bình luận đã được thêm và đang chờ kiểm duyệt");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm bình luận: " + e.getMessage(), e);
        }

        return comment;
    }

    /**
     * Get comments for a blog post
     */
    public Page<Map<String, Object>> getComments(Long postId, Pageable pageable) {
        List<Map<String, Object>> comments = new ArrayList<>();
        long totalElements = 0;

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT bc.comment_id, bc.content, bc.created_at, " +
                    "u.full_name as user_name " +
                    "FROM ctpn_content.blog_comments bc " +
                    "JOIN ctpn_core.users u ON bc.user_id = u.user_id " +
                    "WHERE bc.post_id = ? AND bc.status = 'approved' AND bc.is_deleted = false " +
                    "ORDER BY bc.created_at ASC LIMIT ? OFFSET ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, postId);
            stmt.setInt(2, pageable.getPageSize());
            stmt.setInt(3, pageable.getPageNumber() * pageable.getPageSize());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> comment = new HashMap<>();
                comment.put("commentId", rs.getLong("comment_id"));
                comment.put("content", rs.getString("content"));
                comment.put("userName", rs.getString("user_name"));
                comment.put("createdAt", rs.getTimestamp("created_at"));

                comments.add(comment);
            }

            // Get total count
            String countQuery = "SELECT COUNT(*) FROM ctpn_content.blog_comments " +
                    "WHERE post_id = ? AND status = 'approved' AND is_deleted = false";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            countStmt.setLong(1, postId);

            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalElements = countRs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy bình luận: " + e.getMessage(), e);
        }

        return new PageImpl<>(comments, pageable, totalElements);
    }

    /**
     * Get blog categories
     */
    public List<Map<String, Object>> getBlogCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT category_id, name, description " +
                    "FROM ctpn_content.categories " +
                    "WHERE 1=1 " + // Simplified where clause
                    "ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> category = new HashMap<>();
                category.put("categoryId", rs.getLong("category_id"));
                category.put("name", rs.getString("name"));
                category.put("description", rs.getString("description"));

                categories.add(category);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh mục blog: " + e.getMessage(), e);
        }

        return categories;
    }

    /**
     * Toggle like for blog post
     */
    public Map<String, Object> toggleLike(Long postId, String userEmail) {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            // Get user ID
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Check if user has already liked this post
            String checkQuery = "SELECT like_id FROM ctpn_content.blog_likes " +
                    "WHERE post_id = ? AND user_id = ? AND is_deleted = false";

            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setLong(1, postId);
            checkStmt.setLong(2, user.getUserId());

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Unlike: soft delete the like
                String unlikeQuery = "UPDATE ctpn_content.blog_likes " +
                        "SET is_deleted = true " +
                        "WHERE post_id = ? AND user_id = ?";
                PreparedStatement unlikeStmt = conn.prepareStatement(unlikeQuery);
                unlikeStmt.setLong(1, postId);
                unlikeStmt.setLong(2, user.getUserId());
                unlikeStmt.executeUpdate();

                // Decrease like count
                String decreaseQuery = "UPDATE ctpn_content.blog_posts " +
                        "SET likes_count = GREATEST(likes_count - 1, 0) " +
                        "WHERE post_id = ?";
                PreparedStatement decreaseStmt = conn.prepareStatement(decreaseQuery);
                decreaseStmt.setLong(1, postId);
                decreaseStmt.executeUpdate();

                result.put("action", "unliked");
                result.put("liked", false);

            } else {
                // Like: insert new like
                String likeQuery = "INSERT INTO ctpn_content.blog_likes (post_id, user_id) " +
                        "VALUES (?, ?) " +
                        "ON CONFLICT (post_id, user_id) DO UPDATE SET is_deleted = false";
                PreparedStatement likeStmt = conn.prepareStatement(likeQuery);
                likeStmt.setLong(1, postId);
                likeStmt.setLong(2, user.getUserId());
                likeStmt.executeUpdate();

                // Increase like count
                String increaseQuery = "UPDATE ctpn_content.blog_posts " +
                        "SET likes_count = likes_count + 1 " +
                        "WHERE post_id = ?";
                PreparedStatement increaseStmt = conn.prepareStatement(increaseQuery);
                increaseStmt.setLong(1, postId);
                increaseStmt.executeUpdate();

                result.put("action", "liked");
                result.put("liked", true);
            }

            // Get updated like count
            String countQuery = "SELECT likes_count FROM ctpn_content.blog_posts WHERE post_id = ?";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            countStmt.setLong(1, postId);
            ResultSet countRs = countStmt.executeQuery();

            if (countRs.next()) {
                result.put("likesCount", countRs.getInt("likes_count"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật lượt thích: " + e.getMessage(), e);
        }

        return result;
    }
}