'use client';
import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthContext } from '../../contexts/AuthContext';
import axios from 'axios';
import { FiBook, FiAward, FiTrendingUp, FiClock, FiCheckCircle } from 'react-icons/fi';

/**
 * My Courses Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: User enrollment dashboard per Document FR-010 requirements
 */
const MyCourses = () => {
    const { user } = useAuthContext();
    const router = useRouter();
    const [enrollments, setEnrollments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('active'); // 'active', 'completed', 'all'
    const [stats, setStats] = useState(null);

    useEffect(() => {
        if (user) {
            fetchEnrollments();
            fetchStats();
        }
    }, [user, activeTab]);

    const fetchEnrollments = async () => {
        try {
            setLoading(true);
            const endpoint = activeTab === 'all'
                ? '/api/courses/my-enrollments'
                : `/api/courses/my-enrollments/${activeTab}`;

            const response = await axios.get(endpoint, {
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (true) {
                const data = response.data;
                setEnrollments(data);
            } else {
                setError('Không thể tải danh sách khóa học của bạn');
            }
        } catch (error) {
            console.error('Error fetching enrollments:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };

    const fetchStats = async () => {
        try {
            const response = await axios.get('/api/courses/my-stats', {
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (true) {
                const data = response.data;
                setStats({
                    totalCourses: data[0] || 0,
                    averageProgress: data[1] || 0,
                    averageScore: data[2] || 0
                });
            }
        } catch (error) {
            console.error('Error fetching stats:', error);
        }
    };

    const updateProgress = async (courseId, newProgress) => {
        try {
            const response = await axios.put(`/api/courses/${courseId}/progress`, { progress: newProgress }, {
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (true) {
                fetchEnrollments(); // Refresh the list
                fetchStats(); // Refresh stats
            } else {
                setError('Không thể cập nhật tiến độ học tập');
            }
        } catch (error) {
            console.error('Error updating progress:', error);
            setError('Lỗi kết nối đến server');
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'completed':
                return 'bg-green-100 text-green-800';
            case 'active':
                return 'bg-blue-100 text-blue-800';
            case 'dropped':
                return 'bg-red-100 text-red-800';
            default:
                return 'bg-gray-100 text-gray-800';
        }
    };

    const getStatusText = (status) => {
        switch (status) {
            case 'completed':
                return 'Đã hoàn thành';
            case 'active':
                return 'Đang học';
            case 'dropped':
                return 'Đã dừng';
            default:
                return status;
        }
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('vi-VN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    };

    if (!user) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6">
                <div className="max-w-4xl mx-auto text-center">
                    <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-8 border border-white/20">
                        <h2 className="text-2xl font-bold text-gray-800 mb-4">
                            Vui lòng đăng nhập
                        </h2>
                        <p className="text-gray-600">
                            Bạn cần đăng nhập để xem khóa học của mình
                        </p>
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
                        Khóa Học Của Tôi
                    </h1>
                    <p className="text-lg text-gray-600">
                        Theo dõi tiến độ và quản lý việc học tập của bạn
                    </p>
                </div>

                {/* Statistics Cards */}
                {stats && (
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                        <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-6 border border-white/20">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm text-gray-600">Tổng khóa học</p>
                                    <p className="text-3xl font-bold text-blue-600">{stats.totalCourses}</p>
                                </div>
                                <div className="text-4xl">📚</div>
                            </div>
                        </div>

                        <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-6 border border-white/20">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm text-gray-600">Tiến độ trung bình</p>
                                    <p className="text-3xl font-bold text-green-600">
                                        {Math.round(stats.averageProgress || 0)}%
                                    </p>
                                </div>
                                <div className="text-4xl">📈</div>
                            </div>
                        </div>

                        <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-6 border border-white/20">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm text-gray-600">Điểm trung bình</p>
                                    <p className="text-3xl font-bold text-purple-600">
                                        {stats.averageScore ? stats.averageScore.toFixed(1) : '0.0'}
                                    </p>
                                </div>
                                <div className="text-4xl">⭐</div>
                            </div>
                        </div>
                    </div>
                )}

                {/* Tabs */}
                <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-2 mb-8 border border-white/20">
                    <div className="flex space-x-2">
                        {[
                            { key: 'active', label: 'Đang học', icon: '📖' },
                            { key: 'completed', label: 'Đã hoàn thành', icon: '✅' },
                            { key: 'all', label: 'Tất cả', icon: '📚' }
                        ].map((tab) => (
                            <button
                                key={tab.key}
                                onClick={() => setActiveTab(tab.key)}
                                className={`flex items-center px-6 py-3 rounded-xl font-medium transition-colors ${activeTab === tab.key
                                    ? 'bg-blue-600 text-white shadow-lg'
                                    : 'text-gray-600 hover:bg-white/50'
                                    }`}
                            >
                                <span className="mr-2">{tab.icon}</span>
                                {tab.label}
                            </button>
                        ))}
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

                {/* Loading State */}
                {loading && (
                    <div className="flex justify-center items-center h-64">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                        <span className="ml-3 text-blue-600 font-medium">Đang tải...</span>
                    </div>
                )}

                {/* Course Enrollments */}
                {!loading && (
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        {enrollments.map((enrollment) => (
                            <div
                                key={enrollment.enrollmentId}
                                className="bg-white/70 backdrop-blur-sm rounded-2xl p-6 border border-white/20 hover:shadow-2xl transition-all duration-300"
                            >
                                {/* Course Header */}
                                <div className="flex items-start justify-between mb-4">
                                    <div className="flex-1">
                                        <h3 className="text-xl font-bold text-gray-800 mb-2">
                                            {enrollment.course.title}
                                        </h3>
                                        <p className="text-gray-600 text-sm line-clamp-2">
                                            {enrollment.course.description}
                                        </p>
                                    </div>
                                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(enrollment.status)}`}>
                                        {getStatusText(enrollment.status)}
                                    </span>
                                </div>

                                {/* Progress Bar */}
                                <div className="mb-4">
                                    <div className="flex justify-between items-center mb-2">
                                        <span className="text-sm font-medium text-gray-700">Tiến độ</span>
                                        <span className="text-sm font-medium text-blue-600">
                                            {enrollment.progress}%
                                        </span>
                                    </div>
                                    <div className="w-full bg-gray-200 rounded-full h-2">
                                        <div
                                            className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                                            style={{ width: `${enrollment.progress}%` }}
                                        ></div>
                                    </div>
                                </div>

                                {/* Course Info */}
                                <div className="grid grid-cols-2 gap-4 mb-4 text-sm text-gray-600">
                                    <div>
                                        <span className="font-medium">Ngày đăng ký:</span>
                                        <br />
                                        {formatDate(enrollment.enrolledAt)}
                                    </div>
                                    <div>
                                        <span className="font-medium">Số lần thử:</span>
                                        <br />
                                        {enrollment.attemptsCount}/3
                                    </div>
                                </div>

                                {/* Final Score (if completed) */}
                                {enrollment.finalScore && (
                                    <div className="mb-4">
                                        <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                                            <span className="font-medium text-green-800">Điểm cuối khóa:</span>
                                            <span className="text-xl font-bold text-green-600">
                                                {enrollment.finalScore.toFixed(1)}
                                            </span>
                                        </div>
                                    </div>
                                )}

                                {/* Completion Date */}
                                {enrollment.completedAt && (
                                    <div className="mb-4 text-sm text-gray-600">
                                        <span className="font-medium">Ngày hoàn thành:</span> {formatDate(enrollment.completedAt)}
                                    </div>
                                )}

                                {/* Action Buttons */}
                                <div className="flex space-x-3">
                                    {enrollment.status === 'active' && (
                                        <>
                                            <button
                                                onClick={() => {
                                                    const newProgress = Math.min(100, enrollment.progress + 10);
                                                    updateProgress(enrollment.course.courseId, newProgress);
                                                }}
                                                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
                                            >
                                                Tiếp tục học
                                            </button>
                                            <button
                                                onClick={() => {
                                                    // Navigate to course lessons
                                                    alert('Chức năng xem bài học đang được phát triển');
                                                }}
                                                className="px-4 py-2 border border-blue-600 text-blue-600 rounded-lg hover:bg-blue-50 transition-colors font-medium"
                                            >
                                                Xem bài học
                                            </button>
                                        </>
                                    )}

                                    {enrollment.status === 'completed' && (
                                        <button
                                            onClick={() => {
                                                alert('Chức năng tải chứng chỉ đang được phát triển');
                                            }}
                                            className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-medium"
                                        >
                                            Tải chứng chỉ
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {/* Empty State */}
                {!loading && enrollments.length === 0 && (
                    <div className="text-center py-12">
                        <div className="text-6xl mb-4">📚</div>
                        <h3 className="text-xl font-semibold text-gray-700 mb-2">
                            {activeTab === 'active' && 'Bạn chưa có khóa học nào đang học'}
                            {activeTab === 'completed' && 'Bạn chưa hoàn thành khóa học nào'}
                            {activeTab === 'all' && 'Bạn chưa đăng ký khóa học nào'}
                        </h3>
                        <p className="text-gray-600 mb-6">
                            Khám phá các khóa học phong phú để bắt đầu hành trình học tập
                        </p>
                        <button
                            onClick={() => {
                                // Navigate to course list
                                router.push('/courses');
                            }}
                            className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
                        >
                            Xem khóa học
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default MyCourses;