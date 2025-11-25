import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuthContext } from '../../contexts/AuthContext';
import { FiCalendar, FiClock, FiMessageCircle, FiStar } from 'react-icons/fi';
import { authFetch } from '../../utils/authFetch';

/**
 * Consultant List Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 2.0
 * Context: Consultant browsing and booking per Document FR-013 requirements
 */
const ConsultantList = () => {
    const { user } = useAuthContext();
    const [consultants, setConsultants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedConsultant, setSelectedConsultant] = useState(null);
    const [showBooking, setShowBooking] = useState(false);
    const [filters, setFilters] = useState({
        specialization: ''
    });
    
    // Specializations based on Document requirements
    const specializations = [
        'Tư vấn nghiện chất',
        'Hỗ trợ thanh thiếu niên', 
        'Trị liệu gia đình',
        'Tâm lý học đường',
        'Phục hồi chức năng',
        'Tư vấn nhóm',
        'Can thiệp khủng hoảng'
    ];
    
    useEffect(() => {
        fetchConsultants();
    }, [filters]);
    
    const fetchConsultants = async () => {
        try {
            setLoading(true);
            const queryParams = new URLSearchParams({
                ...(filters.specialization && { specialization: filters.specialization })
            });
            
            const endpoint = filters.specialization 
                ? `/api/consultations/consultants/search?${queryParams}`
                : '/api/consultations/consultants';
                
            const response = await authFetch(endpoint, {
                headers: {
                    'Content-Type': 'application/json',
                    ...(user && { 'Authorization': `Bearer ${user.token}` })
                }
            });
            
            if (response.ok) {
                const data = await response.json();
                setConsultants(data || []);
            } else {
                setError('Không thể tải danh sách tư vấn viên');
            }
        } catch (error) {
            console.error('Error fetching consultants:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };
    
    const handleBookingClick = (consultant) => {
        if (!user) {
            setError('Vui lòng đăng nhập để đặt lịch tư vấn');
            return;
        }
        setSelectedConsultant(consultant);
        setShowBooking(true);
    };
    
    const formatRating = (rating) => {
        return rating ? rating.toFixed(1) : '0.0';
    };
    
    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-100 p-6">
                <div className="max-w-7xl mx-auto">
                    <div className="flex justify-center items-center h-64">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
                        <span className="ml-3 text-green-600 font-medium">Đang tải danh sách tư vấn viên...</span>
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
                        Tư Vấn Viên Tình Nguyện
                    </h1>
                    <p className="text-lg text-gray-600">
                        Đội ngũ tư vấn viên chuyên nghiệp, hỗ trợ miễn phí 24/7
                    </p>
                    <div className="mt-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg inline-block">
                        <strong>🌟 100% Miễn Phí</strong> - Tất cả dịch vụ tư vấn được cung cấp hoàn toàn miễn phí
                    </div>
                </div>
                
                {/* Filters */}
                <div className="bg-white/70 backdrop-blur-sm rounded-2xl p-6 mb-8 border border-white/20">
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Chuyên môn
                            </label>
                            <select
                                value={filters.specialization}
                                onChange={(e) => setFilters(prev => ({ ...prev, specialization: e.target.value }))}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                            >
                                <option value="">Tất cả chuyên môn</option>
                                {specializations.map((spec) => (
                                    <option key={spec} value={spec}>
                                        {spec}
                                    </option>
                                ))}
                            </select>
                        </div>
                        
                        <div className="flex items-end">
                            <button
                                onClick={() => setFilters({ specialization: '' })}
                                className="w-full px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                            >
                                Xóa bộ lọc
                            </button>
                        </div>
                        
                        <div className="flex items-end">
                            <button
                                onClick={fetchConsultants}
                                className="w-full px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                            >
                                Tìm kiếm
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
                
                {/* Consultant Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-8">
                    {consultants.map((consultant) => (
                        <div 
                            key={consultant.userId}
                            className="bg-white/70 backdrop-blur-sm rounded-2xl overflow-hidden border border-white/20 hover:shadow-2xl transition-all duration-300 hover:transform hover:scale-[1.02]"
                        >
                            {/* Profile Image */}
                            <div className="h-48 bg-gradient-to-br from-green-400 to-blue-600 relative">
                                {consultant.avatarUrl ? (
                                    <img 
                                        src={consultant.avatarUrl} 
                                        alt={consultant.fullName}
                                        className="w-full h-full object-cover"
                                    />
                                ) : (
                                    <div className="flex items-center justify-center h-full">
                                        <div className="text-white text-6xl opacity-50">👨‍⚕️</div>
                                    </div>
                                )}
                                
                                {/* Volunteer Badge */}
                                <div className="absolute top-4 right-4">
                                    <span className="bg-green-500 text-white px-3 py-1 rounded-full text-sm font-medium">
                                        Tình nguyện
                                    </span>
                                </div>
                            </div>
                            
                            {/* Consultant Info */}
                            <div className="p-6">
                                <h3 className="text-xl font-bold text-gray-800 mb-2">
                                    {consultant.fullName}
                                </h3>
                                
                                {/* Specializations placeholder */}
                                <div className="mb-3">
                                    <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-xs font-medium">
                                        Tư vấn nghiện chất
                                    </span>
                                </div>
                                
                                {/* Bio placeholder */}
                                <p className="text-gray-600 mb-4 line-clamp-3">
                                    Chuyên gia với nhiều năm kinh nghiệm trong lĩnh vực phòng chống tệ nạn xã hội. 
                                    Cam kết hỗ trợ và đồng hành cùng bạn trên con đường phục hồi.
                                </p>
                                
                                {/* Stats */}
                                <div className="flex items-center justify-between mb-4 text-sm text-gray-600">
                                    <div className="flex items-center">
                                        <span>⭐ {formatRating(4.8)}</span>
                                        <span className="ml-1">(156 đánh giá)</span>
                                    </div>
                                    <div className="flex items-center">
                                        <span>📅 Có sẵn</span>
                                    </div>
                                </div>
                                
                                {/* Contact Info */}
                                <div className="text-sm text-gray-600 mb-4">
                                    <div className="flex items-center mb-1">
                                        <span>📧 {consultant.email}</span>
                                    </div>
                                    {consultant.phoneNumber && (
                                        <div className="flex items-center">
                                            <span>📞 {consultant.phoneNumber}</span>
                                        </div>
                                    )}
                                </div>
                                
                                {/* Action Buttons */}
                                <div className="flex space-x-3">
                                    <button
                                        onClick={() => handleBookingClick(consultant)}
                                        className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-medium"
                                    >
                                        Đặt lịch tư vấn
                                    </button>
                                    <button
                                        onClick={() => {
                                            alert('Chức năng xem hồ sơ đang được phát triển');
                                        }}
                                        className="px-4 py-2 border border-green-600 text-green-600 rounded-lg hover:bg-green-50 transition-colors font-medium"
                                    >
                                        Xem hồ sơ
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
                
                {/* Empty State */}
                {consultants.length === 0 && !loading && (
                    <div className="text-center py-12">
                        <div className="text-6xl mb-4">👨‍⚕️</div>
                        <h3 className="text-xl font-semibold text-gray-700 mb-2">
                            Không tìm thấy tư vấn viên
                        </h3>
                        <p className="text-gray-600 mb-6">
                            Thử thay đổi bộ lọc hoặc liên hệ hotline để được hỗ trợ
                        </p>
                        <div className="bg-green-100 border border-green-400 text-green-700 px-6 py-4 rounded-lg inline-block">
                            <strong>Hotline 24/7:</strong> 1900-1234 (miễn phí)
                        </div>
                    </div>
                )}
                
                {/* Booking Modal Placeholder */}
                {showBooking && selectedConsultant && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white rounded-2xl p-8 max-w-md w-full mx-4">
                            <h3 className="text-xl font-bold text-gray-800 mb-4">
                                Đặt lịch tư vấn với {selectedConsultant.fullName}
                            </h3>
                            <p className="text-gray-600 mb-6">
                                Chức năng đặt lịch đang được phát triển. Vui lòng liên hệ hotline để được hỗ trợ.
                            </p>
                            <div className="flex space-x-3">
                                <button
                                    onClick={() => setShowBooking(false)}
                                    className="flex-1 px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                                >
                                    Đóng
                                </button>
                                <button
                                    onClick={() => {
                                        window.open('tel:1900-1234', '_self');
                                    }}
                                    className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                                >
                                    Gọi hotline
                                </button>
                            </div>
                        </div>
                    </div>
                )}
                
                {/* Service Information */}
                <div className="bg-gradient-to-r from-green-100 to-blue-100 rounded-2xl p-8 border border-white/20">
                    <h3 className="text-2xl font-bold text-gray-800 mb-4 text-center">
                        Cam Kết Dịch Vụ
                    </h3>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div className="text-center">
                            <div className="text-4xl mb-3">🆓</div>
                            <h4 className="font-bold text-lg text-gray-800 mb-2">100% Miễn Phí</h4>
                            <p className="text-gray-600">Tất cả dịch vụ tư vấn được cung cấp hoàn toàn miễn phí</p>
                        </div>
                        <div className="text-center">
                            <div className="text-4xl mb-3">🔒</div>
                            <h4 className="font-bold text-lg text-gray-800 mb-2">Bảo Mật Tuyệt Đối</h4>
                            <p className="text-gray-600">Thông tin cá nhân được bảo mật theo tiêu chuẩn quốc tế</p>
                        </div>
                        <div className="text-center">
                            <div className="text-4xl mb-3">🤝</div>
                            <h4 className="font-bold text-lg text-gray-800 mb-2">Hỗ Trợ 24/7</h4>
                            <p className="text-gray-600">Luôn sẵn sàng lắng nghe và hỗ trợ bạn mọi lúc mọi nơi</p>
                        </div>
                    </div>
                </div>
      </div>
    </div>
  );
};

export default ConsultantList;
