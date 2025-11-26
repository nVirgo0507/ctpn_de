'use client';
import React, { useState, useEffect } from 'react';
import { useAuthContext } from '../../contexts/AuthContext';
import axios from 'axios';
import { FiClock, FiUsers, FiStar, FiBookOpen } from 'react-icons/fi';
import { authFetch } from '../../utils/authFetch';

/**
 * Course List Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]  
 * Version: 1.0
 * Context: Course catalog display per Document FR-009, FR-010 requirements
 */
const CourseList = () => {
    const { user } = useAuthContext();
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filters, setFilters] = useState({
        title: '',
        category: '',
        level: ''
    });
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [enrolling, setEnrolling] = useState(null);

    // Categories based on Document requirements
    const categories = [
        'Giáo dục tổng quát',
        'Giáo dục phòng ngừa',
        'Tư vấn cơ bản',
        'Tư vấn chuyên sâu',
        'Điều trị và phục hồi',
        'Hỗ trợ tâm lý',
        'Kỹ năng sống',
        'Nâng cao nhận thức',
        'Hoạt động cộng đồng'
    ];

    const levels = ['beginner', 'intermediate', 'advanced'];

    useEffect(() => {
        fetchCourses();
    }, [page, filters]);

    const fetchCourses = async () => {
        try {
            setLoading(true);
            const queryParams = new URLSearchParams({
                page: page.toString(),
                size: '9',
                ...(filters.title && { title: filters.title }),
                ...(filters.category && { category: filters.category }),
                ...(filters.level && { level: filters.level })
            });

            const endpoint = Object.keys(filters).some(key => filters[key])
                ? `/api/courses/search?${queryParams}`
                : `/api/courses?${queryParams}`;

            const response = await fetch(`http://localhost:8080${endpoint}`, {
                headers: {
                    'Content-Type': 'application/json',
                    ...(user && { 'Authorization': `Bearer ${user.token}` })
                }
            });

            if (response.ok) {
                const data = await response.json();
                setCourses(data.content || []);
                setTotalPages(data.totalPages || 0);
            } else {
                setError('Không thể tải danh sách khóa học');
            }
        } catch (error) {
            console.error('Error fetching courses:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (field, value) => {
        setFilters(prev => ({
            ...prev,
            [field]: value
        }));
        setPage(0); // Reset to first page when filtering
    };

    const handleEnrollment = async (courseId) => {
        if (!user) {
            setError('Vui lòng đăng nhập để đăng ký khóa học');
            return;
        }

        try {
            setEnrolling(courseId);
            const response = await authFetch(`http://localhost:8080/api/courses/${courseId}/enroll`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${user.token}`
                }
            });

            if (response.ok) {
                alert('Đăng ký khóa học thành công!');
                fetchCourses(); // Refresh the list
            } else {
                const errorData = await response.json();
                setError(errorData.error || 'Không thể đăng ký khóa học');
            }
        } catch (error) {
            console.error('Error enrolling in course:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setEnrolling(null);
        }
    };

    const formatLevel = (level) => {
        const levelMap = {
            'beginner': 'Cơ bản',
            'intermediate': 'Trung cấp',
            'advanced': 'Nâng cao'
        };
        return levelMap[level] || level;
    };

    const formatPrice = (price) => {
        return price && price > 0 ? `${price.toLocaleString()} VNĐ` : 'Miễn phí';
    };

    if (loading && courses.length === 0) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6">
                <div className="max-w-7xl mx-auto">
                    <div className="flex justify-center items-center h-64">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                        <span className="ml-3 text-blue-600 font-medium">Đang tải khóa học...</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6">
            <div className="max-w-7xl mx-auto">
                {/* Header */}
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-bold text-gray-800 mb-4">
                        Khóa Học Phòng Chống Tệ Nạn Xã Hội
                    </h1>
                    <p className="text-lg text-gray-600">
                        Nâng cao kiến thức và kỹ năng phòng ngừa tệ nạn xã hội
                    </p>
                </div>

                {/* Filters */}
                <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-6 mb-8 border border-white/20">
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Tìm kiếm khóa học
                            </label>
                            <input
                                type="text"
                                value={filters.title}
                                onChange={(e) => handleFilterChange('title', e.target.value)}
                                placeholder="Nhập tên khóa học..."
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Danh mục
                            </label>
                            <select
                                value={filters.category}
                                onChange={(e) => handleFilterChange('category', e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            >
                                <option value="">Tất cả danh mục</option>
                                {categories.map((category) => (
                                    <option key={category} value={category}>
                                        {category}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Cấp độ
                            </label>
                            <select
                                value={filters.level}
                                onChange={(e) => handleFilterChange('level', e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            >
                                <option value="">Tất cả cấp độ</option>
                                {levels.map((level) => (
                                    <option key={level} value={level}>
                                        {formatLevel(level)}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="flex items-end">
                            <button
                                onClick={() => {
                                    setFilters({ title: '', category: '', level: '' });
                                    setPage(0);
                                }}
                                className="w-full px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                            >
                                Xóa bộ lọc
                            </button>
                        </div>
                    </div>
                </div>

                {/* Error Message */}
                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6">
                        {error}
                        <button
                            onClick={() => setError('')}
                            className="float-right text-red-700 hover:text-red-900"
                        >
                            ×
                        </button>
                    </div>
                )}

                {/* Course Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-8">
                    {courses.map((course) => (
                        <div
                            key={course.courseId}
                            className="bg-white/70 backdrop-blur-sm rounded-2xl overflow-hidden border border-white/20 hover:shadow-2xl transition-all duration-300 hover:transform hover:scale-[1.02]"
                        >
                            {/* Course Image */}
                            <div className="h-48 bg-gradient-to-br from-blue-400 to-indigo-600 relative">
                                {course.imageUrl ? (
                                    <img
                                        src={course.imageUrl}
                                        alt={course.title}
                                        className="w-full h-full object-cover"
                                    />
                                ) : (
                                    <div className="flex items-center justify-center h-full">
                                        <div className="text-white text-6xl opacity-50">📚</div>
                                    </div>
                                )}

                                {/* Course Level Badge */}
                                <div className="absolute top-4 right-4">
                                    <span className="bg-white/90 text-gray-800 px-3 py-1 rounded-full text-sm font-medium">
                                        {formatLevel(course.level)}
                                    </span>
                                </div>
                            </div>

                            {/* Course Content */}
                            <div className="p-6">
                                <div className="mb-2">
                                    <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-xs font-medium">
                                        {course.category}
                                    </span>
                                </div>

                                <h3 className="text-xl font-bold text-gray-800 mb-3 line-clamp-2">
                                    {course.title}
                                </h3>

                                <p className="text-gray-600 mb-4 line-clamp-3">
                                    {course.description}
                                </p>

                                {/* Course Info */}
                                <div className="flex items-center justify-between mb-4 text-sm text-gray-600">
                                    <div className="flex items-center">
                                        <span>⏱️ {course.durationHours}h</span>
                                    </div>
                                    <div className="flex items-center">
                                        <span>⭐ {course.rating?.toFixed(1) || '0.0'}</span>
                                        <span className="ml-1">({course.totalReviews})</span>
                                    </div>
                                </div>

                                {/* Price and Enrollment */}
                                <div className="flex items-center justify-between">
                                    <div className="text-lg font-bold text-blue-600">
                                        {formatPrice(course.price)}
                                    </div>

                                    {user ? (
                                        <button
                                            onClick={() => handleEnrollment(course.courseId)}
                                            disabled={enrolling === course.courseId}
                                            className={`px-6 py-2 rounded-lg font-medium transition-colors ${enrolling === course.courseId
                                                    ? 'bg-gray-400 text-white cursor-not-allowed'
                                                    : 'bg-blue-600 text-white hover:bg-blue-700'
                                                }`}
                                        >
                                            {enrolling === course.courseId ? 'Đang đăng ký...' : 'Đăng ký'}
                                        </button>
                                    ) : (
                                        <button
                                            onClick={() => setError('Vui lòng đăng nhập để đăng ký khóa học')}
                                            className="px-6 py-2 bg-gray-400 text-white rounded-lg font-medium hover:bg-gray-500 transition-colors"
                                        >
                                            Đăng nhập
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Empty State */}
                {courses.length === 0 && !loading && (
                    <div className="text-center py-12">
                        <div className="text-6xl mb-4">📚</div>
                        <h3 className="text-xl font-semibold text-gray-700 mb-2">
                            Không tìm thấy khóa học
                        </h3>
                        <p className="text-gray-600">
                            Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm
                        </p>
                    </div>
                )}

                {/* Pagination */}
                {totalPages > 1 && (
                    <div className="flex justify-center items-center space-x-2">
                        <button
                            onClick={() => setPage(page - 1)}
                            disabled={page === 0}
                            className={`px-4 py-2 rounded-lg font-medium ${page === 0
                                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                                    : 'bg-blue-600 text-white hover:bg-blue-700'
                                }`}
                        >
                            Trước
                        </button>

                        <span className="px-4 py-2 text-gray-700">
                            Trang {page + 1} / {totalPages}
                        </span>

                        <button
                            onClick={() => setPage(page + 1)}
                            disabled={page >= totalPages - 1}
                            className={`px-4 py-2 rounded-lg font-medium ${page >= totalPages - 1
                                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                                    : 'bg-blue-600 text-white hover:bg-blue-700'
                                }`}
                        >
                            Sau
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default CourseList;
