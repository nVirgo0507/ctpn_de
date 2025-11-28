'use client';
import React, { useState, useEffect } from 'react';
import { useAuthContext } from '../../contexts/AuthContext';
import { FiCalendar, FiUser, FiTag, FiMessageCircle, FiHeart, FiShare2, FiSearch, FiClock, FiArrowRight } from 'react-icons/fi';

import BlogDetailModal from './BlogDetailModal';

/**
 * Blog Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 2.0
 * Context: Community engagement feature per Document FR-014 requirements
 */
const Blog = () => {
  const { user } = useAuthContext();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [categories, setCategories] = useState([]);
  const [selectedPostId, setSelectedPostId] = useState(null);

  useEffect(() => {
    fetchPosts();
    fetchCategories();
  }, []);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/blog/public');
      if (response.ok) {
        const data = await response.json();
        setPosts(data.data.content); // Backend returns { success: true, data: Page<...> }
      } else {
        setError('Không thể tải danh sách bài viết');
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
      setError('Lỗi kết nối đến server');
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/blog/categories');
      if (response.ok) {
        const data = await response.json();
        setCategories(data.data); // Backend returns { success: true, data: List<...> }
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const handleLike = async (postId, e) => {
    e.stopPropagation(); // Prevent opening modal when clicking like
    if (!user) {
      alert('Vui lòng đăng nhập để thích bài viết');
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/blog/${postId}/like`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${user.token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        // Update local state
        setPosts(posts.map(post => {
          if (post.postId === postId) {
            return {
              ...post,
              likesCount: post.isLiked ? post.likesCount - 1 : post.likesCount + 1,
              isLiked: !post.isLiked
            };
          }
          return post;
        }));
      }
    } catch (error) {
      console.error('Error liking post:', error);
    }
  };

  const filteredPosts = posts.filter(post => {
    const matchesSearch = post.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      post.content.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === 'all' || post.category?.slug === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 font-sans">
      {/* Hero Section */}
      <div className="bg-white border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 md:py-24 text-center">
          <span className="inline-block py-1 px-3 rounded-full bg-purple-100 text-purple-600 text-sm font-semibold mb-4">
            Blog & Chia Sẻ
          </span>
          <h1 className="text-4xl md:text-5xl font-extrabold text-gray-900 tracking-tight mb-4">
            Kiến thức & Kinh nghiệm <span className="text-purple-600">Phòng ngừa</span>
          </h1>
          <p className="text-lg text-gray-500 max-w-2xl mx-auto mb-8">
            Cùng nhau xây dựng một môi trường an toàn cho trẻ em thông qua việc chia sẻ kiến thức và kinh nghiệm thực tế.
          </p>

          {/* Search Bar in Hero */}
          <div className="max-w-xl mx-auto relative">
            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
              <FiSearch className="h-5 w-5 text-gray-400" />
            </div>
            <input
              type="text"
              className="block w-full pl-11 pr-4 py-4 bg-white border border-gray-200 rounded-full text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent shadow-lg"
              placeholder="Tìm kiếm bài viết, chủ đề..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Categories Filter */}
        <div className="flex flex-wrap justify-center gap-2 mb-12">
          <button
            onClick={() => setSelectedCategory('all')}
            className={`px-6 py-2 rounded-full text-sm font-medium transition-all duration-200 ${selectedCategory === 'all'
              ? 'bg-purple-600 text-white shadow-md transform scale-105'
              : 'bg-white text-gray-600 hover:bg-gray-100 border border-gray-200'
              }`}
          >
            Tất cả
          </button>
          {categories.map(category => (
            <button
              key={category.id}
              onClick={() => setSelectedCategory(category.slug)}
              className={`px-6 py-2 rounded-full text-sm font-medium transition-all duration-200 ${selectedCategory === category.slug
                ? 'bg-purple-600 text-white shadow-md transform scale-105'
                : 'bg-white text-gray-600 hover:bg-gray-100 border border-gray-200'
                }`}
            >
              {category.name}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
          </div>
        ) : (
          <>
            {/* Featured Post */}
            {filteredPosts.length > 0 && (
              <div className="mb-16">
                <div
                  onClick={() => setSelectedPostId(filteredPosts[0].postId)}
                  className="group relative bg-white rounded-3xl overflow-hidden shadow-xl hover:shadow-2xl transition-all duration-300 border border-gray-100 cursor-pointer"
                >
                  <div className="grid md:grid-cols-2 gap-0">
                    <div className="relative h-72 md:h-auto overflow-hidden">
                      <div className="absolute inset-0 bg-gray-200 animate-pulse" />
                      <img
                        src={filteredPosts[0].thumbnail || 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80'}
                        alt={filteredPosts[0].title}
                        className="absolute inset-0 w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-700"
                      />
                      <div className="absolute top-6 left-6">
                        <span className="px-4 py-2 bg-white/90 backdrop-blur-md text-purple-700 text-xs font-bold rounded-full uppercase tracking-wider shadow-sm">
                          Bài viết mới nhất
                        </span>
                      </div>
                    </div>
                    <div className="p-8 md:p-12 flex flex-col justify-center bg-white">
                      <div className="flex items-center gap-4 text-sm text-gray-500 mb-6">
                        <span className="flex items-center gap-2 bg-gray-50 px-3 py-1 rounded-full">
                          <FiCalendar className="text-purple-500" />
                          {formatDate(filteredPosts[0].publishedAt)}
                        </span>
                        <span className="flex items-center gap-2 bg-gray-50 px-3 py-1 rounded-full">
                          <FiTag className="text-purple-500" />
                          {filteredPosts[0].category?.name || 'Tin tức'}
                        </span>
                      </div>

                      <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-6 group-hover:text-purple-600 transition-colors leading-tight">
                        {filteredPosts[0].title}
                      </h2>

                      <p className="text-gray-600 mb-8 line-clamp-3 text-lg leading-relaxed">
                        {filteredPosts[0].excerpt || filteredPosts[0].content.substring(0, 200)}...
                      </p>

                      <div className="flex items-center justify-between mt-auto pt-6 border-t border-gray-100">
                        <div className="flex items-center gap-3">
                          <img
                            src={filteredPosts[0].author?.avatar || `https://ui-avatars.com/api/?name=${filteredPosts[0].author?.fullName || 'Admin'}&background=random`}
                            alt={filteredPosts[0].author?.fullName}
                            className="w-12 h-12 rounded-full border-2 border-white shadow-sm"
                          />
                          <div>
                            <p className="text-sm font-bold text-gray-900">{filteredPosts[0].author?.fullName || 'Admin'}</p>
                            <p className="text-xs text-gray-500">Tác giả</p>
                          </div>
                        </div>
                        <button className="flex items-center gap-2 text-purple-600 font-bold hover:text-purple-800 transition-colors group/btn">
                          Đọc tiếp
                          <FiArrowRight className="transform group-hover/btn:translate-x-1 transition-transform" />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Posts Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {filteredPosts.slice(1).map(post => (
                <article
                  key={post.postId}
                  onClick={() => setSelectedPostId(post.postId)}
                  className="bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 group flex flex-col border border-gray-100 cursor-pointer"
                >
                  <div className="relative h-56 overflow-hidden">
                    <div className="absolute inset-0 bg-gray-200 animate-pulse" />
                    <img
                      src={post.thumbnail || `https://source.unsplash.com/random/800x600?sig=${post.postId}`}
                      alt={post.title}
                      className="absolute inset-0 w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
                    />
                    <div className="absolute top-4 right-4">
                      <span className="px-3 py-1 bg-white/90 backdrop-blur-md text-purple-600 text-xs font-bold rounded-full shadow-sm">
                        {post.category?.name || 'Kiến thức'}
                      </span>
                    </div>
                  </div>

                  <div className="p-6 flex-1 flex flex-col">
                    <div className="flex items-center gap-4 text-xs text-gray-500 mb-4">
                      <span className="flex items-center gap-1">
                        <FiCalendar className="text-gray-400" />
                        {formatDate(post.publishedAt)}
                      </span>
                      <span className="flex items-center gap-1">
                        <FiClock className="text-gray-400" />
                        {post.readTime || '5'} phút đọc
                      </span>
                    </div>

                    <h3 className="text-xl font-bold text-gray-900 mb-3 line-clamp-2 group-hover:text-purple-600 transition-colors">
                      {post.title}
                    </h3>

                    <p className="text-gray-600 text-sm mb-6 line-clamp-3 flex-1 leading-relaxed">
                      {post.excerpt || post.content.substring(0, 100)}...
                    </p>

                    <div className="flex items-center justify-between pt-4 border-t border-gray-50">
                      <div className="flex items-center gap-4">
                        <button
                          onClick={(e) => handleLike(post.postId, e)}
                          className={`flex items-center gap-1.5 text-sm font-medium transition-colors ${post.isLiked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'
                            }`}
                        >
                          <FiHeart className={post.isLiked ? 'fill-current' : ''} />
                          <span>{post.likesCount || 0}</span>
                        </button>
                        <button className="flex items-center gap-1.5 text-sm font-medium text-gray-500 hover:text-blue-500 transition-colors">
                          <FiMessageCircle />
                          <span>{post.commentsCount || 0}</span>
                        </button>
                      </div>
                      <button className="text-gray-400 hover:text-purple-600 transition-colors p-2 hover:bg-purple-50 rounded-full">
                        <FiShare2 />
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>

            {/* Empty State */}
            {filteredPosts.length === 0 && (
              <div className="text-center py-20 bg-white rounded-3xl border border-gray-100 shadow-sm">
                <div className="text-6xl mb-6">🔍</div>
                <h3 className="text-2xl font-bold text-gray-900 mb-3">
                  Không tìm thấy bài viết nào
                </h3>
                <p className="text-gray-500 max-w-md mx-auto">
                  Rất tiếc, chúng tôi không tìm thấy bài viết nào phù hợp với từ khóa "{searchTerm}".
                  Hãy thử tìm kiếm với từ khóa khác.
                </p>
                <button
                  onClick={() => { setSearchTerm(''); setSelectedCategory('all'); }}
                  className="mt-8 px-6 py-3 bg-purple-600 text-white rounded-full font-medium hover:bg-purple-700 transition-colors shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
                >
                  Xóa bộ lọc
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Blog Detail Modal */}
      {selectedPostId && (
        <BlogDetailModal
          postId={selectedPostId}
          onClose={() => setSelectedPostId(null)}
        />
      )}
    </div>
  );
};

export default Blog;
