'use client';
import React, { useState, useEffect } from 'react';
import { FiX, FiCalendar, FiClock, FiUser, FiTag, FiHeart, FiMessageCircle, FiShare2 } from 'react-icons/fi';
import { useAuthContext } from '../../contexts/AuthContext';

const BlogDetailModal = ({ postId, onClose }) => {
    const { user } = useAuthContext();
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [comments, setComments] = useState([]);
    const [commentLoading, setCommentLoading] = useState(false);
    const [newComment, setNewComment] = useState('');

    useEffect(() => {
        console.log('BlogDetailModal mounted with postId:', postId);
        if (postId) {
            fetchPostDetails();
            fetchComments();
        }
    }, [postId]);

    const fetchPostDetails = async () => {
        try {
            setLoading(true);
            console.log('Fetching post details for:', postId);
            const response = await fetch(`http://localhost:8080/api/blog/public/${postId}`);
            console.log('Fetch response status:', response.status);

            if (response.ok) {
                const data = await response.json();
                console.log('Fetch data:', data);
                setPost(data.data);
            } else {
                const errorData = await response.json();
                console.error('Fetch failed with status:', response.status);
                console.error('Error details:', errorData);
                setError(errorData.message || 'Không thể tải nội dung bài viết');
            }
        } catch (error) {
            console.error('Error fetching post details:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };

    const fetchComments = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/blog/${postId}/comments`);
            if (response.ok) {
                const data = await response.json();
                setComments(data.data.content);
            }
        } catch (error) {
            console.error('Error fetching comments:', error);
        }
    };

    const handleLike = async () => {
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
                const data = await response.json();
                setPost(prev => ({
                    ...prev,
                    likesCount: data.data.likesCount,
                    isLiked: data.data.liked
                }));
            }
        } catch (error) {
            console.error('Error liking post:', error);
        }
    };

    const handleComment = async (e) => {
        e.preventDefault();
        if (!user) {
            alert('Vui lòng đăng nhập để bình luận');
            return;
        }
        if (!newComment.trim()) return;

        try {
            setCommentLoading(true);
            const response = await fetch(`http://localhost:8080/api/blog/${postId}/comments`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ content: newComment })
            });

            if (response.ok) {
                setNewComment('');
                fetchComments(); // Refresh comments
                alert('Bình luận của bạn đã được gửi và đang chờ duyệt');
            }
        } catch (error) {
            console.error('Error posting comment:', error);
            alert('Có lỗi xảy ra khi gửi bình luận');
        } finally {
            setCommentLoading(false);
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return '';
        return new Date(dateString).toLocaleDateString('vi-VN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    if (!postId) return null;

    return (
        <div className="fixed inset-0 z-[100] overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
            <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                {/* Background overlay */}
                <div
                    className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
                    aria-hidden="true"
                    onClick={onClose}
                ></div>

                {/* Modal panel */}
                <div className="inline-block align-bottom bg-white rounded-2xl text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full">
                    {/* Close button */}
                    <button
                        onClick={onClose}
                        className="absolute top-4 right-4 z-10 p-2 bg-white/50 backdrop-blur-sm rounded-full hover:bg-white transition-colors"
                    >
                        <FiX className="w-6 h-6 text-gray-500" />
                    </button>

                    {loading ? (
                        <div className="flex justify-center items-center h-96">
                            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
                        </div>
                    ) : error ? (
                        <div className="p-12 text-center">
                            <div className="text-red-500 mb-4">⚠️ {error}</div>
                            <button
                                onClick={onClose}
                                className="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
                            >
                                Đóng
                            </button>
                        </div>
                    ) : post && (
                        <div className="bg-white">
                            {/* Hero Image */}
                            <div className="relative h-64 sm:h-96">
                                <img
                                    src={post.featuredImage || `https://source.unsplash.com/random/1200x600?sig=${post.postId}`}
                                    alt={post.title}
                                    className="w-full h-full object-cover"
                                />
                                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                                <div className="absolute bottom-0 left-0 right-0 p-8 text-white">
                                    <div className="flex items-center gap-4 mb-4 text-sm font-medium">
                                        <span className="bg-purple-600 px-3 py-1 rounded-full">
                                            {post.categoryName || 'Tin tức'}
                                        </span>
                                        <span className="flex items-center gap-1">
                                            <FiCalendar /> {formatDate(post.publishedAt)}
                                        </span>
                                        <span className="flex items-center gap-1">
                                            <FiClock /> {post.readTime || '5'} phút đọc
                                        </span>
                                    </div>
                                    <h1 className="text-3xl sm:text-4xl font-bold leading-tight mb-4">
                                        {post.title}
                                    </h1>
                                    <div className="flex items-center gap-3">
                                        <img
                                            src={post.authorAvatar || `https://ui-avatars.com/api/?name=${post.authorName}&background=random`}
                                            alt={post.authorName}
                                            className="w-10 h-10 rounded-full border-2 border-white"
                                        />
                                        <div>
                                            <p className="font-bold">{post.authorName}</p>
                                            <p className="text-xs opacity-80">Tác giả</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="p-8 sm:p-12">
                                {/* Content */}
                                <div className="prose prose-lg max-w-none mb-12 text-gray-700">
                                    <div dangerouslySetInnerHTML={{ __html: post.content }} />
                                </div>

                                {/* Interaction Bar */}
                                <div className="flex items-center justify-between py-6 border-t border-b border-gray-100 mb-12">
                                    <div className="flex items-center gap-6">
                                        <button
                                            onClick={handleLike}
                                            className={`flex items-center gap-2 text-lg font-medium transition-colors ${post.isLiked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'
                                                }`}
                                        >
                                            <FiHeart className={post.isLiked ? 'fill-current' : ''} />
                                            <span>{post.likesCount || 0} yêu thích</span>
                                        </button>
                                        <div className="flex items-center gap-2 text-lg font-medium text-gray-500">
                                            <FiMessageCircle />
                                            <span>{comments.length} bình luận</span>
                                        </div>
                                    </div>
                                    <button className="flex items-center gap-2 text-gray-500 hover:text-purple-600 transition-colors">
                                        <FiShare2 />
                                        <span>Chia sẻ</span>
                                    </button>
                                </div>

                                {/* Comments Section */}
                                <div className="max-w-3xl mx-auto">
                                    <h3 className="text-2xl font-bold text-gray-900 mb-8">Bình luận</h3>

                                    {/* Comment Form */}
                                    {user ? (
                                        <form onSubmit={handleComment} className="mb-12">
                                            <div className="flex gap-4">
                                                <img
                                                    src={user.avatar || `https://ui-avatars.com/api/?name=${user.fullName}&background=random`}
                                                    alt={user.fullName}
                                                    className="w-10 h-10 rounded-full"
                                                />
                                                <div className="flex-1">
                                                    <textarea
                                                        value={newComment}
                                                        onChange={(e) => setNewComment(e.target.value)}
                                                        placeholder="Chia sẻ suy nghĩ của bạn..."
                                                        className="w-full p-4 border border-gray-200 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent min-h-[100px] resize-y"
                                                    />
                                                    <div className="flex justify-end mt-2">
                                                        <button
                                                            type="submit"
                                                            disabled={commentLoading || !newComment.trim()}
                                                            className="px-6 py-2 bg-purple-600 text-white rounded-full font-medium hover:bg-purple-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                                        >
                                                            {commentLoading ? 'Đang gửi...' : 'Gửi bình luận'}
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </form>
                                    ) : (
                                        <div className="bg-gray-50 rounded-xl p-6 text-center mb-12">
                                            <p className="text-gray-600 mb-4">Vui lòng đăng nhập để tham gia thảo luận</p>
                                            <a href="/login" className="text-purple-600 font-bold hover:underline">Đăng nhập ngay</a>
                                        </div>
                                    )}

                                    {/* Comments List */}
                                    <div className="space-y-8">
                                        {comments.map(comment => (
                                            <div key={comment.commentId} className="flex gap-4">
                                                <img
                                                    src={comment.userAvatar || `https://ui-avatars.com/api/?name=${comment.userName}&background=random`}
                                                    alt={comment.userName}
                                                    className="w-10 h-10 rounded-full"
                                                />
                                                <div>
                                                    <div className="bg-gray-50 rounded-2xl p-4 mb-2">
                                                        <div className="flex items-center justify-between mb-2">
                                                            <h4 className="font-bold text-gray-900">{comment.userName}</h4>
                                                            <span className="text-xs text-gray-500">{formatDate(comment.createdAt)}</span>
                                                        </div>
                                                        <p className="text-gray-700">{comment.content}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {comments.length === 0 && (
                                            <p className="text-center text-gray-500 italic">Chưa có bình luận nào. Hãy là người đầu tiên!</p>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default BlogDetailModal;
