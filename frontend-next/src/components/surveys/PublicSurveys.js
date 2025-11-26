'use client';
import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { FiCheckSquare, FiClock, FiAward, FiArrowRight } from 'react-icons/fi';

/**
 * Public Surveys Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Public access survey listing per Document FR-015 requirements
 */
const PublicSurveys = () => {
    const router = useRouter();
    const [surveys, setSurveys] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchSurveys();
    }, []);

    const fetchSurveys = async () => {
        try {
            setLoading(true);
            const response = await fetch('http://localhost:8080/api/surveys/public');
            if (response.ok) {
                const data = await response.json();
                setSurveys(data);
            } else {
                setError('Không thể tải danh sách khảo sát');
            }
        } catch (error) {
            console.error('Error fetching surveys:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-cyan-50 p-6">
            <div className="max-w-7xl mx-auto">
                {/* Header */}
                <div className="text-center mb-12">
                    <h1 className="text-4xl font-bold text-gray-800 mb-4">
                        Khảo Sát Cộng Đồng
                    </h1>
                    <p className="text-lg text-gray-600 max-w-2xl mx-auto">
                        Tham gia các bài khảo sát để giúp chúng tôi hiểu rõ hơn về nhận thức và nhu cầu của cộng đồng về phòng chống xâm hại trẻ em
                    </p>
                </div>

                {/* Loading State */}
                {loading && (
                    <div className="flex justify-center items-center h-64">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                    </div>
                )}

                {/* Error Message */}
                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6 text-center max-w-2xl mx-auto">
                        {error}
                    </div>
                )}

                {/* Surveys Grid */}
                {!loading && !error && (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {surveys.map((survey) => (
                            <div 
                                key={survey.surveyId}
                                className="bg-white rounded-2xl p-8 shadow-lg hover:shadow-xl transition-all duration-300 border border-gray-100 flex flex-col"
                            >
                                <div className="flex items-start justify-between mb-6">
                                    <div className="p-3 bg-blue-100 rounded-xl text-blue-600">
                                        <FiCheckSquare className="text-2xl" />
                                    </div>
                                    <span className="px-3 py-1 bg-green-100 text-green-700 text-xs font-bold rounded-full uppercase tracking-wider">
                                        Đang mở
                                    </span>
                                </div>
                                
                                <h3 className="text-xl font-bold text-gray-800 mb-3 line-clamp-2">
                                    {survey.title}
                                </div>
                                
                                <p className="text-gray-600 mb-6 line-clamp-3 flex-1">
                                    {survey.description}
                                </p>
                                
                                <div className="grid grid-cols-2 gap-4 mb-6 text-sm text-gray-500">
                                    <div className="flex items-center gap-2">
                                        <FiClock />
                                        <span>{survey.estimatedTime || '5-10'} phút</span>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        <FiAward />
                                        <span>{survey.points || '10'} điểm</span>
                                    </div>
                                </div>
                                
                                <button
                                    onClick={() => {
                                        // Navigate to survey detail/take survey page
                                        // For now, just alert as the page might not exist yet
                                        alert('Chức năng làm khảo sát đang được phát triển');
                                    }}
                                    className="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors flex items-center justify-center gap-2 group"
                                >
                                    Tham gia ngay
                                    <FiArrowRight className="group-hover:translate-x-1 transition-transform" />
                                </button>
                            </div>
                ))}
            </div>
                )}

            {/* Empty State */}
            {!loading && !error && surveys.length === 0 && (
                <div className="text-center py-16 bg-white/50 backdrop-blur-sm rounded-3xl border border-white/50">
                    <div className="text-6xl mb-6">📋</div>
                    <h3 className="text-2xl font-bold text-gray-800 mb-2">
                        Hiện chưa có khảo sát nào
                    </h3>
                    <p className="text-gray-600 mb-8">
                        Vui lòng quay lại sau để tham gia các khảo sát mới nhất
                    </p>
                    <button
                        onClick={() => router.push('/')}
                        className="px-8 py-3 bg-white border-2 border-blue-600 text-blue-600 rounded-xl font-bold hover:bg-blue-50 transition-colors"
                    >
                        Về trang chủ
                    </button>
                </div>
            )}
        </div>
        </div >
    );
};

export default PublicSurveys;
