import React, { useState, useEffect } from 'react';
import { FiClock, FiUser, FiTag, FiHeart, FiMessageCircle, FiPlus, FiEdit, FiEye } from 'react-icons/fi';
import { useAuthContext } from '../../contexts/AuthContext';
import { toast } from 'react-toastify';

const Blog = () => {
  const { user, isAuthenticated } = useAuthContext();
  const token = localStorage.getItem('jwt_token');
  const [posts, setPosts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedPost, setSelectedPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [view, setView] = useState('list'); // 'list', 'detail', 'create', 'manage'

  // Blog Post Creation States
  const [newPost, setNewPost] = useState({
    title: '',
    content: '',
    excerpt: '',
    categoryId: '',
    featuredImage: ''
  });

  useEffect(() => {
    fetchBlogPosts();
    fetchCategories();
  }, [currentPage, selectedCategory]);

  const fetchBlogPosts = async () => {
    try {
      setLoading(true);
      const url = view === 'manage' && user?.role !== 'GUEST' && user?.role !== 'MEMBER'
        ? `/api/blog/manage?page=${currentPage}&size=10${selectedCategory ? `&status=${selectedCategory}` : ''}`
        : `/api/blog/public?page=${currentPage}&size=10${selectedCategory ? `&category=${selectedCategory}` : ''}`;
      
      const headers = {};
      if (view === 'manage' && token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(url, { headers });
      const data = await response.json();
      
      if (data.success) {
        setPosts(data.data.content || []);
        setTotalPages(data.data.totalPages || 0);
      }
    } catch (error) {
      console.error('Error fetching blog posts:', error);
      toast.error('Lỗi khi tải danh sách blog');
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await fetch('/api/blog/categories');
      const data = await response.json();
      if (data.success) {
        setCategories(data.data || []);
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const fetchPostDetail = async (postId) => {
    try {
      const response = await fetch(`/api/blog/public/${postId}`);
      const data = await response.json();
      if (data.success) {
        setSelectedPost(data.data);
        setView('detail');
        fetchComments(postId);
      }
    } catch (error) {
      console.error('Error fetching post detail:', error);
      toast.error('Lỗi khi tải nội dung blog');
    }
  };

  const fetchComments = async (postId) => {
    try {
      const response = await fetch(`/api/blog/${postId}/comments`);
      const data = await response.json();
      if (data.success) {
        setComments(data.data.content || []);
      }
    } catch (error) {
      console.error('Error fetching comments:', error);
    }
  };

  const handleLike = async (postId) => {
    if (!user || !token) {
      toast.warn('Vui lòng đăng nhập để thích bài viết');
      return;
    }

    try {
      const response = await fetch(`/api/blog/${postId}/like`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();
      if (data.success) {
        // Update post likes count
        setPosts(posts.map(post => 
          post.postId === postId 
            ? { ...post, likesCount: data.data.likesCount, isLiked: data.data.isLiked }
            : post
        ));
        if (selectedPost && selectedPost.postId === postId) {
          setSelectedPost({ ...selectedPost, likesCount: data.data.likesCount, isLiked: data.data.isLiked });
        }
      }
    } catch (error) {
      console.error('Error toggling like:', error);
      toast.error('Lỗi khi cập nhật lượt thích');
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!user || !token) {
      toast.warn('Vui lòng đăng nhập để bình luận');
      return;
    }
    if (!newComment.trim()) return;

    try {
      const response = await fetch(`/api/blog/${selectedPost.postId}/comments`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ content: newComment })
      });
      const data = await response.json();
      if (data.success) {
        setComments([...comments, data.data]);
        setNewComment('');
        toast.success('Bình luận đã được thêm');
      }
    } catch (error) {
      console.error('Error adding comment:', error);
      toast.error('Lỗi khi thêm bình luận');
    }
  };

  const handleCreatePost = async (e) => {
    e.preventDefault();
    if (!user || !token) {
      toast.warn('Vui lòng đăng nhập để tạo bài viết');
      return;
    }

    try {
      const response = await fetch('/api/blog/create', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newPost)
      });
      const data = await response.json();
      if (data.success) {
        toast.success('Bài viết đã được tạo và gửi phê duyệt');
        setNewPost({ title: '', content: '', excerpt: '', categoryId: '', featuredImage: '' });
        setView('list');
        fetchBlogPosts();
      } else {
        toast.error(data.message || 'Lỗi khi tạo bài viết');
      }
    } catch (error) {
      console.error('Error creating post:', error);
      toast.error('Lỗi khi tạo bài viết');
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const canCreatePost = user && ['STAFF', 'MANAGER', 'ADMIN'].includes(user.role);
  const canManage = user && ['STAFF', 'MANAGER', 'ADMIN'].includes(user.role);

  if (loading) {
    return (
      <div className="max-w-6xl mx-auto p-4">
        <div className="glass-card p-8 text-center">
          <div className="animate-pulse">
            <div className="h-8 bg-gray-300 rounded w-1/4 mx-auto mb-4"></div>
            <div className="h-4 bg-gray-300 rounded w-1/2 mx-auto"></div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-4">
      {/* Header */}
      <div className="glass-card p-6 mb-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-800 vietnamese-text mb-2">
              {view === 'detail' ? selectedPost?.title : 'Blog Chung Tay Phòng Ngừa'}
            </h1>
            <p className="text-gray-600 vietnamese-text">
              {view === 'detail' 
                ? 'Nội dung chi tiết bài viết' 
                : 'Chia sẻ kiến thức và kinh nghiệm phòng ngừa tệ nạn xã hội'
              }
            </p>
          </div>
          
          <div className="flex gap-2">
            {view === 'detail' && (
              <button
                onClick={() => setView('list')}
                className="btn-secondary vietnamese-text"
              >
                ← Quay lại
              </button>
            )}
            
            {view === 'list' && canCreatePost && (
              <button
                onClick={() => setView('create')}
                className="btn-primary flex items-center space-x-2 vietnamese-text"
              >
                <FiPlus className="w-4 h-4" />
                <span>Tạo bài viết</span>
              </button>
            )}
            
            {view === 'list' && canManage && (
              <button
                onClick={() => setView('manage')}
                className="btn-secondary flex items-center space-x-2 vietnamese-text"
              >
                <FiEdit className="w-4 h-4" />
                <span>Quản lý</span>
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Create Post Form */}
      {view === 'create' && (
        <div className="glass-card p-6 mb-6">
          <h2 className="text-2xl font-bold text-gray-800 vietnamese-text mb-4">Tạo bài viết mới</h2>
          <form onSubmit={handleCreatePost} className="space-y-4">
            <div>
              <label className="form-label">Tiêu đề</label>
              <input
                type="text"
                value={newPost.title}
                onChange={(e) => setNewPost({ ...newPost, title: e.target.value })}
                className="glass-input w-full"
                required
              />
            </div>
            
            <div>
              <label className="form-label">Danh mục</label>
              <select
                value={newPost.categoryId}
                onChange={(e) => setNewPost({ ...newPost, categoryId: e.target.value })}
                className="glass-input w-full"
                required
              >
                <option value="">Chọn danh mục</option>
                {categories.map(category => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>
            
            <div>
              <label className="form-label">Tóm tắt</label>
              <textarea
                value={newPost.excerpt}
                onChange={(e) => setNewPost({ ...newPost, excerpt: e.target.value })}
                className="glass-input w-full h-20"
                required
              />
            </div>
            
            <div>
              <label className="form-label">Nội dung</label>
              <textarea
                value={newPost.content}
                onChange={(e) => setNewPost({ ...newPost, content: e.target.value })}
                className="glass-input w-full h-40"
                required
              />
            </div>
            
            <div>
              <label className="form-label">Ảnh đại diện (URL)</label>
              <input
                type="url"
                value={newPost.featuredImage}
                onChange={(e) => setNewPost({ ...newPost, featuredImage: e.target.value })}
                className="glass-input w-full"
              />
            </div>
            
            <div className="flex gap-2">
              <button type="submit" className="btn-primary vietnamese-text">
                Tạo bài viết
              </button>
              <button
                type="button"
                onClick={() => setView('list')}
                className="btn-secondary vietnamese-text"
              >
                Hủy
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Filters */}
      {view === 'list' && (
        <div className="glass-card p-4 mb-6">
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => setSelectedCategory('')}
              className={`px-4 py-2 rounded-lg vietnamese-text transition-colors ${
                selectedCategory === '' 
                  ? 'bg-blue-500 text-white' 
                  : 'bg-white text-gray-700 hover:bg-blue-50'
              }`}
            >
              Tất cả
            </button>
            {categories.map(category => (
              <button
                key={category.categoryId}
                onClick={() => setSelectedCategory(category.name)}
                className={`px-4 py-2 rounded-lg vietnamese-text transition-colors ${
                  selectedCategory === category.name 
                    ? 'bg-blue-500 text-white' 
                    : 'bg-white text-gray-700 hover:bg-blue-50'
                }`}
              >
                {category.name}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Blog Posts List */}
      {view === 'list' && (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {posts.map(post => (
            <div key={post.postId} className="glass-card overflow-hidden hover:transform hover:scale-105 transition-all duration-300">
              {post.featuredImage && (
                <img
                  src={post.featuredImage}
                  alt={post.title}
                  className="w-full h-48 object-cover"
                />
              )}
              <div className="p-6">
                <div className="flex items-center justify-between mb-2">
                  <span className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded vietnamese-text">
                    {post.categoryName}
                  </span>
                  <div className="flex items-center space-x-4 text-sm text-gray-500">
                    <div className="flex items-center space-x-1">
                      <FiEye className="w-4 h-4" />
                      <span>{post.viewsCount || 0}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <FiHeart className="w-4 h-4" />
                      <span>{post.likesCount || 0}</span>
                    </div>
                  </div>
                </div>
                
                <h3 className="text-lg font-semibold text-gray-800 vietnamese-text mb-2 line-clamp-2">
                  {post.title}
                </h3>
                
                <p className="text-gray-600 vietnamese-text text-sm mb-4 line-clamp-3">
                  {post.excerpt}
                </p>
                
                <div className="flex items-center justify-between">
                  <div className="flex items-center text-sm text-gray-500">
                    <FiUser className="w-4 h-4 mr-1" />
                    <span className="vietnamese-text">{post.authorName}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-500">
                    <FiClock className="w-4 h-4 mr-1" />
                    <span>{formatDate(post.publishedAt || post.createdAt)}</span>
                  </div>
                </div>
                
                <button
                  onClick={() => fetchPostDetail(post.postId)}
                  className="w-full mt-4 btn-primary vietnamese-text"
                >
                  Đọc tiếp
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Blog Post Detail */}
      {view === 'detail' && selectedPost && (
        <div className="space-y-6">
          <div className="glass-card p-8">
            {selectedPost.featuredImage && (
              <img
                src={selectedPost.featuredImage}
                alt={selectedPost.title}
                className="w-full h-64 object-cover rounded-lg mb-6"
              />
            )}
            
            <div className="flex items-center justify-between mb-4">
              <span className="px-3 py-1 bg-blue-100 text-blue-800 rounded vietnamese-text">
                {selectedPost.categoryName}
              </span>
              <div className="flex items-center space-x-4 text-sm text-gray-500">
                <div className="flex items-center space-x-1">
                  <FiEye className="w-4 h-4" />
                  <span>{selectedPost.viewsCount}</span>
                </div>
                <div className="flex items-center space-x-1">
                  <FiHeart className="w-4 h-4" />
                  <span>{selectedPost.likesCount}</span>
                </div>
              </div>
            </div>
            
            <h1 className="text-3xl font-bold text-gray-800 vietnamese-text mb-4">
              {selectedPost.title}
            </h1>
            
            <div className="flex items-center text-gray-600 mb-6">
              <FiUser className="w-4 h-4 mr-2" />
              <span className="vietnamese-text mr-4">{selectedPost.authorName}</span>
              <FiClock className="w-4 h-4 mr-2" />
              <span>{formatDate(selectedPost.publishedAt)}</span>
            </div>
            
            <div className="prose max-w-none vietnamese-text text-gray-700 leading-relaxed">
              {selectedPost.content.split('\n').map((paragraph, index) => (
                <p key={index} className="mb-4">{paragraph}</p>
              ))}
            </div>
            
            {user && (
              <div className="mt-8 pt-6 border-t border-gray-200">
                <button
                  onClick={() => handleLike(selectedPost.postId)}
                  className={`flex items-center space-x-2 px-4 py-2 rounded-lg transition-colors vietnamese-text ${
                    selectedPost.isLiked 
                      ? 'bg-red-100 text-red-600 hover:bg-red-200' 
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`}
                >
                  <FiHeart className={`w-5 h-5 ${selectedPost.isLiked ? 'fill-current' : ''}`} />
                  <span>{selectedPost.isLiked ? 'Đã thích' : 'Thích'} ({selectedPost.likesCount})</span>
                </button>
              </div>
            )}
          </div>
          
          {/* Comments Section */}
          <div className="glass-card p-6">
            <h3 className="text-xl font-semibold text-gray-800 vietnamese-text mb-4 flex items-center">
              <FiMessageCircle className="w-5 h-5 mr-2" />
              Bình luận ({comments.length})
            </h3>
            
            {user && (
              <form onSubmit={handleAddComment} className="mb-6">
                <textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Viết bình luận của bạn..."
                  className="glass-input w-full h-20 mb-3"
                />
                <button type="submit" className="btn-primary vietnamese-text">
                  Gửi bình luận
                </button>
              </form>
            )}
            
            <div className="space-y-4">
              {comments.map(comment => (
                <div key={comment.commentId} className="bg-gray-50 rounded-lg p-4">
                  <div className="flex items-center justify-between mb-2">
                    <span className="font-medium text-gray-800 vietnamese-text">
                      {comment.authorName}
                    </span>
                    <span className="text-sm text-gray-500">
                      {formatDate(comment.createdAt)}
                    </span>
                  </div>
                  <p className="text-gray-700 vietnamese-text">{comment.content}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Pagination */}
      {view === 'list' && totalPages > 1 && (
        <div className="flex justify-center mt-8">
          <div className="flex space-x-2">
            {[...Array(totalPages)].map((_, i) => (
              <button
                key={i}
                onClick={() => setCurrentPage(i)}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  currentPage === i
                    ? 'bg-blue-500 text-white'
                    : 'bg-white text-gray-700 hover:bg-blue-50'
                }`}
              >
                {i + 1}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Blog;
