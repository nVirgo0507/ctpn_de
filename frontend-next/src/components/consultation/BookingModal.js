'use client';
import React, { useState } from 'react';
import { FiX, FiCalendar, FiClock, FiMessageSquare } from 'react-icons/fi';
import { authFetch } from '../../utils/authFetch';
import { useAuthContext } from '../../contexts/AuthContext';

const BookingModal = ({ consultant, onClose, onSuccess }) => {
    const { user } = useAuthContext();
    const [step, setStep] = useState(1);
    const [date, setDate] = useState('');
    const [time, setTime] = useState('');
    const [notes, setNotes] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [availableSlots, setAvailableSlots] = useState([]);
    const [loadingSlots, setLoadingSlots] = useState(false);

    // Generate time slots for demonstration if API is not fully ready for slots
    // In a real app, you would fetch these from the backend based on the selected date
    const fetchTimeSlots = async (selectedDate) => {
        setLoadingSlots(true);
        try {
            // Simulate API call or use real one if available
            // const response = await authFetch(`/api/consultations/consultants/${consultant.id}/slots?date=${selectedDate}`);
            // if (response.ok) {
            //     const data = await response.json();
            //     setAvailableSlots(data);
            // }

            // Fallback/Mock slots for now
            setTimeout(() => {
                setAvailableSlots([
                    '09:00', '10:00', '11:00', '14:00', '15:00', '16:00'
                ]);
                setLoadingSlots(false);
            }, 500);
        } catch (err) {
            console.error(err);
            setLoadingSlots(false);
        }
    };

    const handleDateChange = (e) => {
        const selectedDate = e.target.value;
        setDate(selectedDate);
        setTime('');
        if (selectedDate) {
            fetchTimeSlots(selectedDate);
        }
    };

    const handleSubmit = async () => {
        if (!date || !time) {
            setError('Vui lòng chọn ngày và giờ');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const scheduledAt = `${date}T${time}:00`;

            const response = await authFetch('/api/consultations/book', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${user.token}`
                },
                body: JSON.stringify({
                    consultantId: consultant.id,
                    scheduledAt,
                    notes
                })
            });

            if (response.ok) {
                onSuccess();
                onClose();
            } else {
                const data = await response.json();
                setError(data.error || 'Đặt lịch thất bại');
            }
        } catch (err) {
            console.error(err);
            setError('Lỗi kết nối đến server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-2xl w-full max-w-md overflow-hidden shadow-2xl">
                {/* Header */}
                <div className="bg-green-600 p-6 text-white flex justify-between items-center">
                    <h3 className="text-xl font-bold">Đặt lịch tư vấn</h3>
                    <button onClick={onClose} className="text-white/80 hover:text-white transition-colors">
                        <FiX size={24} />
                    </button>
                </div>

                {/* Body */}
                <div className="p-6">
                    <div className="mb-6 flex items-center">
                        <div className="w-12 h-12 rounded-full bg-gray-200 overflow-hidden mr-4">
                            {consultant.avatarUrl ? (
                                <img src={consultant.avatarUrl} alt={consultant.fullName} className="w-full h-full object-cover" />
                            ) : (
                                <div className="w-full h-full flex items-center justify-center text-gray-400 text-xl">👨‍⚕️</div>
                            )}
                        </div>
                        <div>
                            <p className="text-sm text-gray-500">Tư vấn viên</p>
                            <h4 className="font-bold text-gray-800">{consultant.fullName}</h4>
                        </div>
                    </div>

                    {error && (
                        <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm">
                            {error}
                        </div>
                    )}

                    <div className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                <FiCalendar className="inline mr-2" />
                                Chọn ngày
                            </label>
                            <input
                                type="date"
                                min={new Date().toISOString().split('T')[0]}
                                value={date}
                                onChange={handleDateChange}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                            />
                        </div>

                        {date && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    <FiClock className="inline mr-2" />
                                    Chọn giờ
                                </label>
                                {loadingSlots ? (
                                    <div className="text-center py-4 text-gray-500">Đang tải khung giờ...</div>
                                ) : (
                                    <div className="grid grid-cols-3 gap-2">
                                        {availableSlots.map(slot => (
                                            <button
                                                key={slot}
                                                onClick={() => setTime(slot)}
                                                className={`py-2 rounded-lg text-sm font-medium transition-colors ${time === slot
                                                    ? 'bg-green-600 text-white'
                                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                                    }`}
                                            >
                                                {slot}
                                            </button>
                                        ))}
                                    </div>
                                )}
                            </div>
                        )}

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                <FiMessageSquare className="inline mr-2" />
                                Ghi chú (Tùy chọn)
                            </label>
                            <textarea
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                rows={3}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                                placeholder="Mô tả ngắn gọn vấn đề của bạn..."
                            />
                        </div>
                    </div>

                    <div className="mt-8 flex space-x-3">
                        <button
                            onClick={onClose}
                            className="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors font-medium"
                        >
                            Hủy
                        </button>
                        <button
                            onClick={handleSubmit}
                            disabled={loading || !date || !time}
                            className={`flex-1 px-4 py-2 rounded-lg text-white font-medium transition-colors ${loading || !date || !time
                                ? 'bg-gray-400 cursor-not-allowed'
                                : 'bg-green-600 hover:bg-green-700'
                                }`}
                        >
                            {loading ? 'Đang xử lý...' : 'Xác nhận đặt lịch'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BookingModal;
