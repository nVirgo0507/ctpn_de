import React, { useState, useEffect } from 'react';
import { useAuthContext } from '../../contexts/AuthContext';
import { FiCalendar, FiClock, FiMessageCircle, FiVideo, FiCheckCircle, FiXCircle } from 'react-icons/fi';
import { authFetch } from '../../utils/authFetch';

/**
 * My Consultations Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: User consultation management per Document FR-013 requirements
 */
const MyConsultations = () => {
    const { user } = useAuthContext();
    const [consultations, setConsultations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('upcoming'); // 'upcoming', 'completed', 'all'
    const [showRatingModal, setShowRatingModal] = useState(false);
    const [selectedConsultation, setSelectedConsultation] = useState(null);
    const [rating, setRating] = useState(0);
    const [feedback, setFeedback] = useState('');
    
    useEffect(() => {
        if (user) {
            fetchConsultations();
        }
    }, [user, activeTab]);
    
    const fetchConsultations = async () => {
        try {
            setLoading(true);
            const endpoint = activeTab === 'upcoming' 
                ? '/api/consultations/upcoming'
                : '/api/consultations/my-consultations';
                
            const response = await authFetch(endpoint, {
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (response.ok) {
                const data = await response.json();
                let filteredData = data;
                
                if (activeTab === 'completed') {
                    filteredData = data.filter(c => c.status === 'completed');
                }
                
                setConsultations(filteredData);
            } else {
                setError('Không thể tải danh sách lịch tư vấn');
            }
        } catch (error) {
            console.error('Error fetching consultations:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };
    
    const handleCancelConsultation = async (consultationId) => {
        if (!window.confirm('Bạn có chắc chắn muốn hủy lịch tư vấn này?')) {
            return;
        }
        
        try {
            const response = await authFetch(`http://localhost:8080/api/consultations/${consultationId}/cancel`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ reason: 'Hủy bởi người dùng' })
            });
            
            if (response.ok) {
                alert('Hủy lịch tư vấn thành công');
                fetchConsultations();
            } else {
                const errorData = await response.json();
                setError(errorData.error || 'Không thể hủy lịch tư vấn');
            }
        } catch (error) {
            console.error('Error cancelling consultation:', error);
            setError('Lỗi kết nối đến server');
        }
    };
    
    const handleSubmitRating = async () => {
        if (rating === 0) {
            setError('Vui lòng chọn số sao đánh giá');
            return;
        }
        
        try {
            const response = await authFetch(`http://localhost:8080/api/consultations/${selectedConsultation.consultationId}/complete`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ rating, feedback })
            });
            
            if (response.ok) {
                alert('Gửi đánh giá thành công');
                setShowRatingModal(false);
                setRating(0);
                setFeedback('');
                fetchConsultations();
            } else {
                const errorData = await response.json();
                setError(errorData.error || 'Không thể gửi đánh giá');
            }
        } catch (error) {
            console.error('Error submitting rating:', error);
            setError('Lỗi kết nối đến server');
        }
    };
    
    const getStatusColor = (status) => {
        switch (status) {
            case 'scheduled':
                return 'bg-blue-100 text-blue-800';
            case 'completed':
                return 'bg-green-100 text-green-800';
            case 'cancelled':
                return 'bg-red-100 text-red-800';
            case 'no_show':
                return 'bg-yellow-100 text-yellow-800';
            default:
                return 'bg-gray-100 text-gray-800';
        }
    };
    
    const getStatusText = (status) => {
        switch (status) {
            case 'scheduled':
                return 'Đã lên lịch';
            case 'completed':
                return 'Đã hoàn thành';
            case 'cancelled':
                return 'Đã hủy';
            case 'no_show':
                return 'Không tham gia';
            default:
                return status;
        }
    };
    
    const formatDateTime = (dateTimeString) => {
        return new Date(dateTimeString).toLocaleString('vi-VN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };
    
    const canCancel = (consultation) => {
        const scheduledTime = new Date(consultation.scheduledAt);
        const now = new Date();
        const hoursUntil = (scheduledTime - now) / (1000 * 60 * 60);
        return consultation.status === 'scheduled' && hoursUntil >= 24;
    };
    
    if (!user) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-100 p-6">
                <div className="max-w-4xl mx-auto text-center">
                    <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-8 border border-white/20">
                        <h2 className="text-2xl font-bold text-gray-800 mb-4">
                            Vui lòng đăng nhập
                        </h2>
                        <p className="text-gray-600">
                            Bạn cần đăng nhập để xem lịch tư vấn của mình
                        </p>
                    </div>
                </div>
            </div>
        );
    }
    
    return (
        <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-100 p-6">
            <div className="max-w-7xl mx-auto">
                {/* Header */}
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-bold text-gray-800 mb-4">
                        Lịch Tư Vấn Của Tôi
                    </h1>
                    <p className="text-lg text-gray-600">
                        Quản lý và theo dõi các buổi tư vấn của bạn
                    </p>
                </div>
                
                {/* Tabs */}
                <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-2 mb-8 border border-white/20">
                    <div className="flex space-x-2">
                        {[
                            { key: 'upcoming', label: 'Sắp tới', icon: '📅' },
                            { key: 'completed', label: 'Đã hoàn thành', icon: '✅' },
                            { key: 'all', label: 'Tất cả', icon: '📋' }
                        ].map((tab) => (
                            <button
                                key={tab.key}
                                onClick={() => setActiveTab(tab.key)}
                                className={`flex items-center px-6 py-3 rounded-xl font-medium transition-colors ${
                                    activeTab === tab.key
                                        ? 'bg-green-600 text-white shadow-lg'
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
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
                        <span className="ml-3 text-green-600 font-medium">Đang tải...</span>
                    </div>
                )}
                
                {/* Consultations List */}
                {!loading && (
                    <div className="grid grid-cols-1 gap-6">
                        {consultations.map((consultation) => (
                            <div 
                                key={consultation.consultationId}
                                className="bg-white/70 backdrop-blur-sm rounded-2xl p-6 border border-white/20 hover:shadow-2xl transition-all duration-300"
                            >
                                {/* Consultation Header */}
                                <div className="flex items-start justify-between mb-4">
                                    <div className="flex-1">
                                        <div className="flex items-center mb-2">
                                            <h3 className="text-xl font-bold text-gray-800 mr-3">
                                                Tư vấn với {consultation.consultant?.fullName || consultation.member?.fullName}
                                            </h3>
                                            <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(consultation.status)}`}>
                                                {getStatusText(consultation.status)}
                                            </span>
                                        </div>
                                        <p className="text-gray-600">
                                            📅 {formatDateTime(consultation.scheduledAt)}
                                        </p>
                                        <p className="text-gray-600">
                                            ⏱️ {consultation.durationMinutes} phút
                                        </p>
                                    </div>
                                </div>
                                
                                {/* Meeting Link */}
                                {consultation.googleMeetLink && consultation.status === 'scheduled' && (
                                    <div className="mb-4 p-3 bg-blue-50 rounded-lg">
                                        <p className="text-sm font-medium text-blue-800 mb-2">Link tham gia:</p>
                                        <a 
                                            href={consultation.googleMeetLink}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="text-blue-600 hover:text-blue-800 underline"
                                        >
                                            {consultation.googleMeetLink}
                                        </a>
                                    </div>
                                )}
                                
                                {/* Notes */}
                                {consultation.notes && (
                                    <div className="mb-4">
                                        <p className="text-sm font-medium text-gray-700 mb-1">Ghi chú:</p>
                                        <p className="text-gray-600 text-sm">{consultation.notes}</p>
                                    </div>
                                )}
                                
                                {/* Rating (if completed) */}
                                {consultation.status === 'completed' && consultation.rating && (
                                    <div className="mb-4 p-3 bg-green-50 rounded-lg">
                                        <p className="text-sm font-medium text-green-800 mb-2">Đánh giá của bạn:</p>
                                        <div className="flex items-center mb-2">
                                            {[1, 2, 3, 4, 5].map((star) => (
                                                <span 
                                                    key={star}
                                                    className={`text-xl ${star <= consultation.rating ? 'text-yellow-400' : 'text-gray-300'}`}
                                                >
                                                    ⭐
                                                </span>
                                            ))}
                                            <span className="ml-2 text-sm text-gray-600">({consultation.rating}/5)</span>
                                        </div>
                                        {consultation.feedback && (
                                            <p className="text-gray-600 text-sm">{consultation.feedback}</p>
                                        )}
                                    </div>
                                )}
                                
                                {/* Action Buttons */}
                                <div className="flex space-x-3">
                                    {consultation.status === 'scheduled' && (
                                        <>
                                            {canCancel(consultation) && (
                                                <button
                                                    onClick={() => handleCancelConsultation(consultation.consultationId)}
                                                    className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-medium"
                                                >
                                                    Hủy lịch
                                                </button>
                                            )}
                                            <button
                                                onClick={() => {
                                                    if (consultation.googleMeetLink) {
                                                        window.open(consultation.googleMeetLink, '_blank');
                                                    } else {
                                                        alert('Link tham gia chưa có sẵn');
                                                    }
                                                }}
                                                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
                                            >
                                                Tham gia
                                            </button>
                                        </>
                                    )}
                                    
                                    {consultation.status === 'completed' && !consultation.rating && (
                                        <button
                                            onClick={() => {
                                                setSelectedConsultation(consultation);
                                                setShowRatingModal(true);
                                            }}
                                            className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-medium"
                                        >
                                            Đánh giá
                                        </button>
                                    )}
                                    
                                    {consultation.status === 'completed' && (
                                        <button
                                            onClick={() => {
                                                alert('Chức năng quyên góp đang được phát triển');
                                            }}
                                            className="px-4 py-2 border border-green-600 text-green-600 rounded-lg hover:bg-green-50 transition-colors font-medium"
                                        >
                                            Quyên góp tự nguyện
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
                
                {/* Empty State */}
                {!loading && consultations.length === 0 && (
                    <div className="text-center py-12">
                        <div className="text-6xl mb-4">📅</div>
                        <h3 className="text-xl font-semibold text-gray-700 mb-2">
                            {activeTab === 'upcoming' && 'Bạn chưa có lịch tư vấn nào sắp tới'}
                            {activeTab === 'completed' && 'Bạn chưa hoàn thành buổi tư vấn nào'}
                            {activeTab === 'all' && 'Bạn chưa có lịch tư vấn nào'}
                        </h3>
                        <p className="text-gray-600 mb-6">
                            Hãy đặt lịch tư vấn để được hỗ trợ từ các chuyên gia
                        </p>
                        <button
                            onClick={() => {
                                window.location.href = '/consultation';
                            }}
                            className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-medium"
                        >
                            Đặt lịch tư vấn
                        </button>
                    </div>
                )}
                
                {/* Rating Modal */}
                {showRatingModal && selectedConsultation && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white rounded-2xl p-8 max-w-md w-full mx-4">
                            <h3 className="text-xl font-bold text-gray-800 mb-4">
                                Đánh giá buổi tư vấn
                            </h3>
                            
                            <div className="mb-4">
                                <p className="text-sm font-medium text-gray-700 mb-2">Đánh giá chất lượng:</p>
                                <div className="flex space-x-1">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <button
                                            key={star}
                                            onClick={() => setRating(star)}
                                            className={`text-3xl ${star <= rating ? 'text-yellow-400' : 'text-gray-300'} hover:text-yellow-400 transition-colors`}
                                        >
                                            ⭐
                                        </button>
                                    ))}
                                </div>
                            </div>
                            
                            <div className="mb-6">
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Nhận xét (tùy chọn):
                                </label>
                                <textarea
                                    value={feedback}
                                    onChange={(e) => setFeedback(e.target.value)}
                                    rows={3}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                                    placeholder="Chia sẻ cảm nghĩ của bạn về buổi tư vấn..."
                                />
                            </div>
                            
                            <div className="flex space-x-3">
                                <button
                                    onClick={() => {
                                        setShowRatingModal(false);
                                        setRating(0);
                                        setFeedback('');
                                    }}
                                    className="flex-1 px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                                >
                                    Hủy
                                </button>
                                <button
                                    onClick={handleSubmitRating}
                                    className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                                >
                                    Gửi đánh giá
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default MyConsultations; 