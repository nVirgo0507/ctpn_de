'use client';
import React, { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { FiStar, FiCalendar, FiClock, FiMail, FiPhone, FiAward, FiUser } from 'react-icons/fi';
import { authFetch } from '../../utils/authFetch';
import { useAuthContext } from '../../contexts/AuthContext';
import BookingModal from './BookingModal';

const ConsultantDetail = () => {
    const { id } = useParams();
    const router = useRouter();
    const { user } = useAuthContext();
    const [consultant, setConsultant] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showBooking, setShowBooking] = useState(false);

    useEffect(() => {
        if (id) {
            fetchConsultantDetail();
        }
    }, [id]);

    const fetchConsultantDetail = async () => {
        try {
            setLoading(true);
            // In a real app, you might have a specific endpoint for detail
            // For now, we can reuse the search or list endpoint if needed, or assume we have a detail endpoint
            // Let's assume we fetch from the list and find by ID for now if detail endpoint isn't explicit
            // OR better, let's assume /api/consultations/consultants/{id} exists or we use the list to filter.
            // Given the controller, we only have /consultants and /consultants/search.
            // We might need to fetch all and filter, OR add a detail endpoint.
            // For efficiency, let's try to fetch all and filter client side for this demo, 
            // but ideally we should add a detail endpoint.
            // Wait, the controller DOES NOT have a detail endpoint.
            // I will fetch all and find the one.

            const response = await authFetch('/api/consultations/consultants', {
                headers: {
                    'Content-Type': 'application/json',
                    ...(user && { 'Authorization': `Bearer ${user.token}` })
                }
            });

            if (response.ok) {
                const data = await response.json();
                const found = data.find(c => c.id.toString() === id);
                if (found) {
                    setConsultant(found);
                } else {
                    setError('Không tìm thấy tư vấn viên');
                }
            } else {
                setError('Không thể tải thông tin tư vấn viên');
            }
        } catch (error) {
            console.error('Error fetching consultant:', error);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };

    const handleBookingSuccess = () => {
        alert('Đặt lịch thành công! Vui lòng kiểm tra email để nhận thông báo.');
        router.push('/my-consultations');
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-100 p-6 flex justify-center items-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
            </div>
        );
    }

    if (error || !consultant) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-100 p-6 flex justify-center items-center">
                <div className="bg-white p-8 rounded-2xl shadow-xl text-center">
                    <h2 className="text-2xl font-bold text-red-600 mb-4">Lỗi</h2>
                    <p className="text-gray-600 mb-6">{error || 'Không tìm thấy tư vấn viên'}</p>
                    <button
                        onClick={() => router.back()}
                        className="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                    >
                        Quay lại
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-100 p-6">
            <div className="max-w-5xl mx-auto">
                <button
                    onClick={() => router.back()}
                    className="mb-6 text-gray-600 hover:text-green-600 font-medium flex items-center transition-colors"
                >
                    ← Quay lại danh sách
                </button>

                <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-xl overflow-hidden border border-white/20">
                    {/* Cover / Header */}
                    <div className="h-48 bg-gradient-to-r from-green-600 to-blue-600 relative">
                        <div className="absolute -bottom-16 left-8 md:left-12">
                            <div className="w-32 h-32 rounded-full border-4 border-white bg-white overflow-hidden shadow-lg">
                                {consultant.avatarUrl ? (
                                    <img src={consultant.avatarUrl} alt={consultant.fullName} className="w-full h-full object-cover" />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center bg-gray-100 text-gray-400 text-4xl">👨‍⚕️</div>
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="pt-20 px-8 md:px-12 pb-8">
                        <div className="flex flex-col md:flex-row justify-between items-start">
                            <div>
                                <h1 className="text-3xl font-bold text-gray-800 mb-2">{consultant.fullName}</h1>
                                <div className="flex items-center text-gray-600 mb-4">
                                    <span className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-medium mr-3">
                                        Tư vấn viên
                                    </span>
                                    <span className="flex items-center">
                                        <FiStar className="text-yellow-400 mr-1" />
                                        {consultant.userProfile?.rating || '5.0'} ({consultant.userProfile?.totalReviews || 0} đánh giá)
                                    </span>
                                </div>
                            </div>
                            <button
                                onClick={() => {
                                    if (!user) {
                                        alert('Vui lòng đăng nhập để đặt lịch');
                                        return;
                                    }
                                    setShowBooking(true);
                                }}
                                className="mt-4 md:mt-0 px-8 py-3 bg-green-600 text-white rounded-xl hover:bg-green-700 transition-colors font-bold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all"
                            >
                                Đặt lịch ngay
                            </button>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
                            {/* Left Column: Info */}
                            <div className="md:col-span-2 space-y-8">
                                <div>
                                    <h3 className="text-xl font-bold text-gray-800 mb-4 flex items-center">
                                        <FiUser className="mr-2" /> Giới thiệu
                                    </h3>
                                    <p className="text-gray-600 leading-relaxed">
                                        {consultant.userProfile?.bio || 'Chưa có thông tin giới thiệu.'}
                                    </p>
                                </div>

                                <div>
                                    <h3 className="text-xl font-bold text-gray-800 mb-4 flex items-center">
                                        <FiAward className="mr-2" /> Chuyên môn
                                    </h3>
                                    <div className="flex flex-wrap gap-2">
                                        {consultant.userProfile?.specializations?.map((spec, index) => (
                                            <span key={index} className="bg-blue-50 text-blue-700 px-4 py-2 rounded-lg text-sm font-medium border border-blue-100">
                                                {spec}
                                            </span>
                                        )) || (
                                                <span className="text-gray-500 italic">Đang cập nhật...</span>
                                            )}
                                    </div>
                                </div>

                                <div>
                                    <h3 className="text-xl font-bold text-gray-800 mb-4 flex items-center">
                                        <FiAward className="mr-2" /> Kinh nghiệm
                                    </h3>
                                    <p className="text-gray-600">
                                        {consultant.userProfile?.experienceYears || 0} năm kinh nghiệm trong lĩnh vực tư vấn và hỗ trợ cộng đồng.
                                    </p>
                                </div>
                            </div>

                            {/* Right Column: Contact & Stats */}
                            <div className="space-y-6">
                                <div className="bg-gray-50 rounded-2xl p-6 border border-gray-100">
                                    <h3 className="font-bold text-gray-800 mb-4">Thông tin liên hệ</h3>
                                    <div className="space-y-3">
                                        <div className="flex items-center text-gray-600">
                                            <FiMail className="mr-3" />
                                            <span className="text-sm">{consultant.email}</span>
                                        </div>
                                        {consultant.phoneNumber && (
                                            <div className="flex items-center text-gray-600">
                                                <FiPhone className="mr-3" />
                                                <span className="text-sm">{consultant.phoneNumber}</span>
                                            </div>
                                        )}
                                    </div>
                                </div>

                                <div className="bg-green-50 rounded-2xl p-6 border border-green-100">
                                    <h3 className="font-bold text-green-800 mb-4">Thống kê hoạt động</h3>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="text-center p-3 bg-white rounded-xl shadow-sm">
                                            <div className="text-2xl font-bold text-green-600">{consultant.userProfile?.totalSessions || 0}</div>
                                            <div className="text-xs text-gray-500">Buổi tư vấn</div>
                                        </div>
                                        <div className="text-center p-3 bg-white rounded-xl shadow-sm">
                                            <div className="text-2xl font-bold text-blue-600">{consultant.userProfile?.totalReviews || 0}</div>
                                            <div className="text-xs text-gray-500">Đánh giá</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {showBooking && (
                <BookingModal
                    consultant={consultant}
                    onClose={() => setShowBooking(false)}
                    onSuccess={handleBookingSuccess}
                />
            )}
        </div>
    );
};

export default ConsultantDetail;
