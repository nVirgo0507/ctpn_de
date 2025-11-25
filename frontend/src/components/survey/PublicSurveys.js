import React, { useState, useEffect } from 'react';
import { FiClock, FiUsers, FiShield, FiCheckCircle, FiArrowRight, FiList } from 'react-icons/fi';
import { toast } from 'react-toastify';

/**
 * Public Surveys Component
 * Author: FullStack-Developer-AI (Cursor)
 * Created: Session #21
 * Version: 1.0
 * Context: Implements FR-017 Survey Management & FR-021 Anonymous Survey System per Document specifications
 */
const PublicSurveys = () => {
  const [surveys, setSurveys] = useState([]);
  const [selectedSurvey, setSelectedSurvey] = useState(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [responses, setResponses] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [view, setView] = useState('list'); // 'list', 'survey', 'completed'
  const [completedSurvey, setCompletedSurvey] = useState(null);

  useEffect(() => {
    fetchPublicSurveys();
  }, []);

  const fetchPublicSurveys = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/surveys/public');
      const data = await response.json();
      
      if (data.success) {
        setSurveys(data.data || []);
      } else {
        toast.error('Lỗi khi tải danh sách khảo sát');
      }
    } catch (error) {
      console.error('Error fetching surveys:', error);
      toast.error('Lỗi kết nối khi tải khảo sát');
    } finally {
      setLoading(false);
    }
  };

  const fetchSurveyDetails = async (surveyId) => {
    try {
      const response = await fetch(`/api/surveys/public/${surveyId}`);
      const data = await response.json();
      
      if (data.success) {
        setSelectedSurvey(data.data);
        setView('survey');
        setCurrentQuestionIndex(0);
        setResponses({});
      } else {
        toast.error('Lỗi khi tải nội dung khảo sát');
      }
    } catch (error) {
      console.error('Error fetching survey details:', error);
      toast.error('Lỗi khi tải chi tiết khảo sát');
    }
  };

  const handleResponse = (questionId, value) => {
    setResponses(prev => ({
      ...prev,
      [questionId]: value
    }));
  };

  const handleNext = () => {
    if (currentQuestionIndex < selectedSurvey.questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    } else {
      handleSubmit();
    }
  };

  const handlePrevious = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
  };

  const handleSubmit = async () => {
    try {
      setSubmitting(true);
      
      // Ensure complete anonymity - no personal data
      const anonymousResponse = {
        responses: responses,
        completed: new Date().toISOString(),
        anonymous: true
      };

      const response = await fetch(`/api/surveys/submit/${selectedSurvey.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(anonymousResponse)
      });

      const data = await response.json();
      
      if (data.success) {
        setCompletedSurvey(selectedSurvey);
        setView('completed');
        toast.success('Cảm ơn bạn đã tham gia khảo sát!');
      } else {
        toast.error(data.message || 'Lỗi khi gửi phản hồi khảo sát');
      }
    } catch (error) {
      console.error('Error submitting survey:', error);
      toast.error('Lỗi khi gửi phản hồi khảo sát');
    } finally {
      setSubmitting(false);
    }
  };

  const renderQuestion = (question) => {
    const currentResponse = responses[question.id];

    switch (question.type) {
      case 'multiple_choice':
        return (
          <div className="space-y-3">
            {question.options.map((option, index) => (
              <label key={index} className="flex items-center space-x-3 p-3 rounded-lg border border-gray-200 hover:bg-blue-50 cursor-pointer transition-colors">
                <input
                  type="radio"
                  name={question.id}
                  value={option}
                  checked={currentResponse === option}
                  onChange={(e) => handleResponse(question.id, e.target.value)}
                  className="text-blue-600 focus:ring-blue-500"
                />
                <span className="vietnamese-text flex-1">{option}</span>
              </label>
            ))}
          </div>
        );

      case 'multiple_select':
        return (
          <div className="space-y-3">
            {question.options.map((option, index) => (
              <label key={index} className="flex items-center space-x-3 p-3 rounded-lg border border-gray-200 hover:bg-blue-50 cursor-pointer transition-colors">
                <input
                  type="checkbox"
                  value={option}
                  checked={Array.isArray(currentResponse) && currentResponse.includes(option)}
                  onChange={(e) => {
                    const current = Array.isArray(currentResponse) ? currentResponse : [];
                    if (e.target.checked) {
                      handleResponse(question.id, [...current, option]);
                    } else {
                      handleResponse(question.id, current.filter(item => item !== option));
                    }
                  }}
                  className="text-blue-600 focus:ring-blue-500"
                />
                <span className="vietnamese-text flex-1">{option}</span>
              </label>
            ))}
          </div>
        );

      case 'text':
        return (
          <textarea
            value={currentResponse || ''}
            onChange={(e) => handleResponse(question.id, e.target.value)}
            placeholder="Nhập phản hồi của bạn..."
            className="glass-input w-full h-32 resize-none"
          />
        );

      case 'rating':
        return (
          <div className="flex justify-center space-x-2">
            {[1, 2, 3, 4, 5].map(rating => (
              <button
                key={rating}
                onClick={() => handleResponse(question.id, rating)}
                className={`w-12 h-12 rounded-full font-semibold transition-colors ${
                  currentResponse === rating
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                {rating}
              </button>
            ))}
          </div>
        );

      default:
        return (
          <input
            type="text"
            value={currentResponse || ''}
            onChange={(e) => handleResponse(question.id, e.target.value)}
            placeholder="Nhập phản hồi của bạn..."
            className="glass-input w-full"
          />
        );
    }
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto p-4">
        <div className="glass-card p-8 text-center">
          <div className="animate-pulse">
            <div className="h-8 bg-gray-300 rounded w-1/3 mx-auto mb-4"></div>
            <div className="h-4 bg-gray-300 rounded w-1/2 mx-auto"></div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-4">
      {/* Header */}
      <div className="glass-card p-6 mb-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-800 vietnamese-text mb-2">
              {view === 'list' ? 'Khảo sát cộng đồng' : selectedSurvey?.title}
            </h1>
            <p className="text-gray-600 vietnamese-text">
              {view === 'list' 
                ? 'Tham gia các khảo sát ẩn danh để đóng góp ý kiến cho cộng đồng'
                : 'Khảo sát ẩn danh - Thông tin của bạn được bảo mật hoàn toàn'
              }
            </p>
          </div>
          
          {view !== 'list' && (
            <button
              onClick={() => {
                setView('list');
                setSelectedSurvey(null);
                setCurrentQuestionIndex(0);
                setResponses({});
              }}
              className="btn-secondary vietnamese-text"
            >
              ← Quay lại
            </button>
          )}
        </div>

        {/* Privacy Notice */}
        <div className="mt-4 p-4 bg-green-50 rounded-lg border border-green-200">
          <div className="flex items-center space-x-2 text-green-800">
            <FiShield className="w-5 h-5" />
            <span className="font-semibold vietnamese-text">Bảo mật thông tin</span>
          </div>
          <p className="text-green-700 vietnamese-text text-sm mt-1">
            Tất cả khảo sát đều ẩn danh và không thu thập thông tin cá nhân. 
            Dữ liệu chỉ được sử dụng cho mục đích nghiên cứu và cải thiện dịch vụ.
          </p>
        </div>
      </div>

      {/* Survey List */}
      {view === 'list' && (
        <div className="grid md:grid-cols-2 gap-6">
          {surveys.map(survey => (
            <div key={survey.id} className="glass-card p-6 hover:transform hover:scale-105 transition-all duration-300">
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <h3 className="text-xl font-semibold text-gray-800 vietnamese-text mb-2">
                    {survey.title}
                  </h3>
                  <p className="text-gray-600 vietnamese-text text-sm mb-3">
                    {survey.description}
                  </p>
                </div>
                <span className="px-3 py-1 bg-blue-100 text-blue-800 text-xs rounded vietnamese-text">
                  {survey.category}
                </span>
              </div>

              <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                <div className="flex items-center space-x-4">
                  <div className="flex items-center space-x-1">
                    <FiClock className="w-4 h-4" />
                    <span>{survey.estimatedTime}</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <FiList className="w-4 h-4" />
                    <span>{survey.totalQuestions} câu hỏi</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <FiUsers className="w-4 h-4" />
                    <span>{survey.responses} phản hồi</span>
                  </div>
                </div>
              </div>

              <button
                onClick={() => fetchSurveyDetails(survey.id)}
                className="w-full btn-primary flex items-center justify-center space-x-2 vietnamese-text"
              >
                <span>Tham gia khảo sát</span>
                <FiArrowRight className="w-4 h-4" />
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Survey Form */}
      {view === 'survey' && selectedSurvey && (
        <div className="glass-card p-8">
          {/* Progress Bar */}
          <div className="mb-8">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm font-medium text-gray-700 vietnamese-text">
                Câu hỏi {currentQuestionIndex + 1} / {selectedSurvey.questions.length}
              </span>
              <span className="text-sm text-gray-500">
                {Math.round(((currentQuestionIndex + 1) / selectedSurvey.questions.length) * 100)}%
              </span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                style={{
                  width: `${((currentQuestionIndex + 1) / selectedSurvey.questions.length) * 100}%`
                }}
              />
            </div>
          </div>

          {/* Question */}
          <div className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-800 vietnamese-text mb-6">
              {selectedSurvey.questions[currentQuestionIndex].text}
            </h2>
            
            {selectedSurvey.questions[currentQuestionIndex].required && (
              <p className="text-red-600 text-sm mb-4 vietnamese-text">* Câu hỏi bắt buộc</p>
            )}

            {renderQuestion(selectedSurvey.questions[currentQuestionIndex])}
          </div>

          {/* Navigation */}
          <div className="flex justify-between">
            <button
              onClick={handlePrevious}
              disabled={currentQuestionIndex === 0}
              className="btn-secondary disabled:opacity-50 disabled:cursor-not-allowed vietnamese-text"
            >
              ← Câu trước
            </button>

            <button
              onClick={handleNext}
              disabled={submitting}
              className="btn-primary disabled:opacity-50 vietnamese-text"
            >
              {submitting ? (
                'Đang gửi...'
              ) : currentQuestionIndex === selectedSurvey.questions.length - 1 ? (
                'Hoàn thành'
              ) : (
                'Câu tiếp theo →'
              )}
            </button>
          </div>
        </div>
      )}

      {/* Completion Message */}
      {view === 'completed' && completedSurvey && (
        <div className="glass-card p-8 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <FiCheckCircle className="w-8 h-8 text-green-600" />
          </div>
          
          <h2 className="text-2xl font-bold text-gray-800 vietnamese-text mb-4">
            Cảm ơn bạn đã tham gia!
          </h2>
          
          <p className="text-gray-600 vietnamese-text mb-6">
            Phản hồi của bạn đã được ghi nhận thành công. Thông tin được lưu trữ ẩn danh 
            và sẽ được sử dụng để cải thiện các dịch vụ hỗ trợ cộng đồng.
          </p>

          <div className="flex justify-center space-x-4">
            <button
              onClick={() => setView('list')}
              className="btn-primary vietnamese-text"
            >
              Khảo sát khác
            </button>
            
            <button
              onClick={() => window.location.href = '/'}
              className="btn-secondary vietnamese-text"
            >
              Về trang chủ
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default PublicSurveys; 