'use client';
import React, { useState, useEffect } from 'react';
import { useAuthContext } from '../../contexts/AuthContext';
import { FiCalendar, FiUser, FiTag, FiMessageCircle, FiHeart, FiShare2, FiSearch } from 'react-icons/fi';

/**
 * Blog Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
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

  useEffect(() => {
    fetchPosts();
    fetchCategories();
  }, []);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/posts');
      if (response.ok) {
        const data = await response.json();
        setPosts(data);
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
      const response = await fetch('http://localhost:8080/api/categories');
      if (response.ok) {
        const data = await response.json();
        setCategories(data);
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const handleLike = async (postId) => {
    if (!user) {
      alert('Vui lòng đăng nhập để thích bài viết');
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/posts/${postId}/like`, {
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
    <div className="min-h-screen bg-gradient-to-br from-purple-50 to-pink-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-800 mb-4">
            Blog & Chia Sẻ
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Khám phá những bài viết hữu ích, chia sẻ kinh nghiệm và kiến thức về phòng ngừa xâm hại trẻ em
          </p>
        </div>

        {/* Search and Filter */}
        <div className="mb-12">
          <div className="flex flex-col md:flex-row gap-4 justify-between items-center bg-white/70 backdrop-blur-sm p-4 rounded-2xl border border-white/20 shadow-sm">
            {/* Search */}
            <div className="relative w-full md:w-96">
              <FiSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input
                type="text"
                placeholder="Tìm kiếm bài viết..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 rounded-xl border border-gray-200 focus:ring-2 focus:ring-purple-500 focus:border-transparent bg-white/50"
              />
            </div>

            {/* Categories */}
            <div className="flex gap-2 overflow-x-auto w-full md:w-auto pb-2 md:pb-0">
              <button
                onClick={() => setSelectedCategory('all')}
                className={`px-4 py-2 rounded-xl whitespace-nowrap transition-colors ${selectedCategory === 'all'
                    ? 'bg-purple-600 text-white'
                    : 'bg-white text-gray-600 hover:bg-purple-50'
                  }`}
              >
                Tất cả
              </button>
              {categories.map(category => (
                <button
                  key={category.id}
                  onClick={() => setSelectedCategory(category.slug)}
                  className={`px-4 py-2 rounded-xl whitespace-nowrap transition-colors ${selectedCategory === category.slug
                      ? 'bg-purple-600 text-white'
                      : 'bg-white text-gray-600 hover:bg-purple-50'
                    }`}
                >
                  {category.name}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Featured Post (First one) */}
        {!loading && filteredPosts.length > 0 && (
          <div className="mb-12">
            <div className="group relative bg-white rounded-3xl overflow-hidden shadow-xl hover:shadow-2xl transition-all duration-300">
              <div className="grid md:grid-cols-2 gap-0">
                <div className="relative h-64 md:h-auto overflow-hidden">
                  <img
                    src={filteredPosts[0].thumbnail || 'https://source.unsplash.com/random/800x600?education'}
                    alt={filteredPosts[0].title}
                    className="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute top-4 left-4">
                    <span className="px-3 py-1 bg-purple-600 text-white text-xs font-bold rounded-full uppercase tracking-wider">
                      Mới nhất
                    </span>
                  </div>
                </div>
                <div className="p-8 md:p-12 flex flex-col justify-center">
                  <div className="flex items-center gap-4 text-sm text-gray-500 mb-4">
                    <span className="flex items-center gap-1">
                      <FiCalendar /> {formatDate(filteredPosts[0].createdAt)}
                    </span>
                    <span className="flex items-center gap-1">
                      <FiTag /> {filteredPosts[0].category?.name || 'Tin tức'}
                    </span>
                  </div>
                  <h2 className="text-3xl font-bold text-gray-800 mb-4 group-hover:text-purple-600 transition-colors">
                    {filteredPosts[0].title}
                  </h2>
                  <p className="text-gray-600 mb-6 line-clamp-3">
                    {filteredPosts[0].excerpt || filteredPosts[0].content.substring(0, 150)}...
                  </p>
                  <div className="flex items-center justify-between mt-auto">
                    <div className="flex items-center gap-3">
                      <img
                        src={filteredPosts[0].author?.avatar || 'https://ui-avatars.com/api/?name=' + filteredPosts[0].author?.fullName}
                        alt={filteredPosts[0].author?.fullName}
                        className="w-10 h-10 rounded-full border-2 border-purple-100"
                      />
                      <div>
                        <p className="text-sm font-bold text-gray-800">{filteredPosts[0].author?.fullName || 'Admin'}</p>
                        <p className="text-xs text-gray-500">Tác giả</p>
                      </div>
                    </div>
                    <button className="text-purple-600 font-bold hover:text-purple-800 transition-colors">
                      Đọc tiếp →
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Posts Grid */}
        {loading ? (
          <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {filteredPosts.slice(1).map(post => (
              <article
                key={post.postId}
                className="bg-white rounded-2xl overflow-hidden shadow-lg hover:shadow-xl transition-all duration-300 group flex flex-col"
              >
                <div className="relative h-48 overflow-hidden">
                  <img
                    src={post.thumbnail || `https://source.unsplash.com/random/800x600?sig=${post.postId}`}
                    alt={post.title}
                    className="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute top-4 right-4 bg-white/90 backdrop-blur-sm px-3 py-1 rounded-full text-xs font-bold text-purple-600">
                    {post.category?.name || 'Kiến thức'}
                  </div>
                </div>

                <div className="p-6 flex-1 flex flex-col">
                  <div className="flex items-center gap-3 text-xs text-gray-500 mb-3">
                    <span className="flex items-center gap-1">
                      <FiCalendar /> {formatDate(post.createdAt)}
                    </span>
                    <span className="flex items-center gap-1">
                      <FiClock /> {post.readTime || '5'} phút đọc
                    </span>
                  </div>

                  <h3 className="text-xl font-bold text-gray-800 mb-3 line-clamp-2 group-hover:text-purple-600 transition-colors">
                    {post.title}
                  </h3>

                  <p className="text-gray-600 text-sm mb-4 line-clamp-3 flex-1">
                    {post.excerpt || post.content.substring(0, 100)}...
                  </p>

                  <div className="border-t border-gray-100 pt-4 mt-auto flex items-center justify-between">
                    <div className="flex items-center gap-4">
                      <button
                        onClick={() => handleLike(post.postId)}
                        className={`flex items-center gap-1 text-sm ${post.isLiked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'} transition-colors`}
                      >
                        <FiHeart className={post.isLiked ? 'fill-current' : ''} />
                        {post.likesCount || 0}
                      </button>
                      <button className="flex items-center gap-1 text-sm text-gray-500 hover:text-blue-500 transition-colors">
                        <FiMessageCircle />
                        {post.commentsCount || 0}
                      </button>
                    </div>
                    <button className="text-gray-400 hover:text-purple-600 transition-colors">
                      <FiShare2 />
                    </button>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}

        {/* Empty State */}
        {!loading && filteredPosts.length === 0 && (
          <div className="text-center py-12">
            <div className="text-6xl mb-4">📝</div>
            <h3 className="text-xl font-semibold text-gray-700 mb-2">
              Không tìm thấy bài viết nào
            </h3>
            <p className="text-gray-600">
              Thử tìm kiếm với từ khóa khác hoặc chọn danh mục khác
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Blog;
