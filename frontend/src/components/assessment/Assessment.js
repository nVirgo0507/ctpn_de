import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '../../contexts/AuthContext';
import { authFetch } from '../../utils/authFetch';

/**
 * Assessment Component - ASSIST/CRAFFT Implementation
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Implements Document FR-006 Drug Risk Self-Assessment
 */
const Assessment = () => {
  const { user } = useAuthContext();
  const [currentStep, setCurrentStep] = useState('age-verification');
  const [userAge, setUserAge] = useState('');
  const [assessmentType, setAssessmentType] = useState('');
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  // Age verification step
  const handleAgeSubmit = async (e) => {
    e.preventDefault();
    if (!userAge || userAge < 1 || userAge > 100) {
      setError('Vui lòng nhập tuổi hợp lệ (1-100)');
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const response = await authFetch(`http://localhost:8080/api/assessment/questions?age=${userAge}`);

      const data = await response.json();
      
      if (data.success) {
        setAssessmentType(data.data.assessmentType);
        setQuestions(JSON.parse(data.data.questions.questions || '[]'));
        setCurrentStep('assessment');
      } else {
        setError(data.message || 'Lỗi khi tải câu hỏi đánh giá');
      }
    } catch (err) {
      setError('Lỗi kết nối. Vui lòng thử lại.');
      console.error('Error fetching questions:', err);
    } finally {
      setIsLoading(false);
    }
  };

  // Handle answer changes
  const handleAnswerChange = (questionId, value) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: value
    }));
  };

  // Submit assessment
  const handleAssessmentSubmit = async (e) => {
    e.preventDefault();
    
    // Validate all questions answered
    const unansweredQuestions = questions.filter(q => !answers[q.id]);
    if (unansweredQuestions.length > 0) {
      setError('Vui lòng trả lời tất cả các câu hỏi');
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const requestData = {
        typeId: assessmentType === 'ASSIST' ? 1 : 2,
        assessmentType: assessmentType,
        answers: answers,
        age: parseInt(userAge)
      };

      const response = await authFetch('http://localhost:8080/api/assessment/submit', {
        method: 'POST',
        body: JSON.stringify(requestData)
      });

      const data = await response.json();
      
      if (data.success) {
        setResult(data.data);
        setCurrentStep('results');
      } else {
        setError(data.message || 'Lỗi khi xử lý đánh giá');
      }
    } catch (err) {
      setError('Lỗi kết nối. Vui lòng thử lại.');
      console.error('Error submitting assessment:', err);
    } finally {
      setIsLoading(false);
    }
  };

  // Reset assessment
  const resetAssessment = () => {
    setCurrentStep('age-verification');
    setUserAge('');
    setAssessmentType('');
    setQuestions([]);
    setAnswers({});
    setResult(null);
    setError('');
  };

  // Get risk level color
  const getRiskLevelColor = (riskLevel) => {
    switch (riskLevel?.toUpperCase()) {
      case 'LOW': return 'text-green-600 bg-green-50';
      case 'MODERATE': return 'text-yellow-600 bg-yellow-50';
      case 'HIGH': return 'text-red-600 bg-red-50';
      default: return 'text-gray-600 bg-gray-50';
    }
  };

  // Render age verification step
  const renderAgeVerification = () => (
    <div className="glass-card p-8 max-w-md mx-auto">
      <h2 className="text-2xl font-bold text-center mb-6 vietnamese-text">Xác thực tuổi</h2>
      <p className="text-gray-600 vietnamese-text mb-6 text-center">
        Để chọn bài đánh giá phù hợp, vui lòng nhập tuổi của bạn
      </p>
      
      <form onSubmit={handleAgeSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 vietnamese-text mb-2">
            Tuổi của bạn
          </label>
          <input
            type="number"
            min="1"
            max="100"
            value={userAge}
            onChange={(e) => setUserAge(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Nhập tuổi"
            required
          />
        </div>

        {error && (
          <div className="text-red-600 text-sm vietnamese-text text-center">
            {error}
          </div>
        )}

        <button
          type="submit"
          disabled={isLoading}
          className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50 vietnamese-text"
        >
          {isLoading ? 'Đang tải...' : 'Tiếp tục'}
        </button>
      </form>
    </div>
  );

  // Render default questions for demo
  const getDefaultQuestions = () => {
    if (assessmentType === 'ASSIST') {
      return [
        { id: 'q1', text: 'Trong 3 tháng qua, bạn có sử dụng rượu bia không?', type: 'frequency' },
        { id: 'q2', text: 'Trong 3 tháng qua, bạn có sử dụng thuốc lá không?', type: 'frequency' },
        { id: 'q3', text: 'Trong 3 tháng qua, bạn có sử dụng ma túy không?', type: 'frequency' },
        { id: 'q4', text: 'Bạn có bao giờ cảm thấy cần phải sử dụng nhiều hơn để có cùng cảm giác?', type: 'frequency' },
        { id: 'q5', text: 'Bạn có bao giờ muốn cắt bỏ hoặc giảm việc sử dụng chất kích thích?', type: 'frequency' }
      ];
    } else {
      return [
        { id: 'q1', text: 'Bạn có bao giờ lái xe khi đã sử dụng chất kích thích?', type: 'yesno' },
        { id: 'q2', text: 'Bạn có bao giờ sử dụng chất kích thích để thư giãn hay tự tin hơn?', type: 'yesno' },
        { id: 'q3', text: 'Bạn có bao giờ sử dụng chất kích thích khi một mình?', type: 'yesno' },
        { id: 'q4', text: 'Bạn có bao giờ quên những gì đã làm khi sử dụng chất kích thích?', type: 'yesno' },
        { id: 'q5', text: 'Gia đình hay bạn bè có bao giờ nói bạn nên giảm sử dụng chất kích thích?', type: 'yesno' },
        { id: 'q6', text: 'Bạn có bao giờ gặp rắc rối khi sử dụng chất kích thích?', type: 'yesno' }
      ];
    }
  };

  // Render assessment questions
  const renderAssessment = () => {
    const questionsToRender = questions.length > 0 ? questions : getDefaultQuestions();

    return (
      <div className="glass-card p-8 max-w-4xl mx-auto">
        <div className="mb-6">
          <h2 className="text-2xl font-bold vietnamese-text mb-2">
            Đánh giá {assessmentType} 
          </h2>
          <p className="text-gray-600 vietnamese-text">
            {assessmentType === 'ASSIST' 
              ? 'Hãy trả lời thành thật về việc sử dụng các chất kích thích trong 3 tháng qua'
              : 'Hãy trả lời có/không cho các câu hỏi sau đây'
            }
          </p>
        </div>

        <form onSubmit={handleAssessmentSubmit} className="space-y-6">
          {questionsToRender.map((question, index) => (
            <div key={question.id} className="border-b border-gray-200 pb-4">
              <label className="block text-sm font-medium text-gray-900 vietnamese-text mb-3">
                {index + 1}. {question.text}
              </label>
              
              {question.type === 'frequency' ? (
                <div className="space-y-2">
                  {['Không bao giờ', 'Một hoặc hai lần', 'Hàng tháng', 'Hàng tuần', 'Hàng ngày'].map((option, optIndex) => (
                    <label key={optIndex} className="flex items-center">
                      <input
                        type="radio"
                        name={question.id}
                        value={optIndex.toString()}
                        checked={answers[question.id] === optIndex.toString()}
                        onChange={(e) => handleAnswerChange(question.id, e.target.value)}
                        className="mr-2"
                      />
                      <span className="vietnamese-text">{option}</span>
                    </label>
                  ))}
                </div>
              ) : (
                <div className="space-y-2">
                  {['Không', 'Có'].map((option, optIndex) => (
                    <label key={optIndex} className="flex items-center">
                      <input
                        type="radio"
                        name={question.id}
                        value={optIndex.toString()}
                        checked={answers[question.id] === optIndex.toString()}
                        onChange={(e) => handleAnswerChange(question.id, e.target.value)}
                        className="mr-2"
                      />
                      <span className="vietnamese-text">{option}</span>
                    </label>
                  ))}
                </div>
              )}
            </div>
          ))}

          {error && (
            <div className="text-red-600 text-sm vietnamese-text text-center">
              {error}
            </div>
          )}

          <div className="flex space-x-4">
            <button
              type="button"
              onClick={resetAssessment}
              className="flex-1 bg-gray-500 text-white py-2 px-4 rounded-md hover:bg-gray-600 vietnamese-text"
            >
              Làm lại
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50 vietnamese-text"
            >
              {isLoading ? 'Đang xử lý...' : 'Hoàn thành đánh giá'}
            </button>
          </div>
        </form>
      </div>
    );
  };

  // Render results
  const renderResults = () => (
    <div className="glass-card p-8 max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-center mb-6 vietnamese-text">Kết quả đánh giá</h2>
      
      <div className="grid md:grid-cols-2 gap-6">
        <div className="space-y-4">
          <div className="glass-card p-4">
            <h3 className="font-semibold vietnamese-text mb-2">Loại đánh giá</h3>
            <p className="text-gray-600 vietnamese-text">{result?.assessmentType}</p>
          </div>
          
          <div className="glass-card p-4">
            <h3 className="font-semibold vietnamese-text mb-2">Điểm tổng</h3>
            <p className="text-2xl font-bold text-blue-600">{result?.totalScore}</p>
          </div>
          
          <div className={`glass-card p-4 ${getRiskLevelColor(result?.riskLevel)}`}>
            <h3 className="font-semibold vietnamese-text mb-2">Mức độ rủi ro</h3>
            <p className="font-bold vietnamese-text">{result?.riskLevelDescription}</p>
          </div>
        </div>
        
        <div className="space-y-4">
          <div className="glass-card p-4">
            <h3 className="font-semibold vietnamese-text mb-2">Khuyến nghị</h3>
            <p className="text-gray-700 vietnamese-text">{result?.recommendations}</p>
          </div>
          
          <div className="glass-card p-4 bg-blue-50">
            <h3 className="font-semibold vietnamese-text mb-2">Bước tiếp theo</h3>
            <p className="text-blue-700 vietnamese-text">{result?.nextSteps}</p>
          </div>
        </div>
      </div>
      
      <div className="mt-6 text-center space-x-4">
        <button
          onClick={resetAssessment}
          className="bg-blue-600 text-white py-2 px-6 rounded-md hover:bg-blue-700 vietnamese-text"
        >
          Làm đánh giá mới
        </button>
        <button
          onClick={() => window.location.href = '/courses'}
          className="bg-green-600 text-white py-2 px-6 rounded-md hover:bg-green-700 vietnamese-text"
        >
          Xem khóa học đề xuất
        </button>
      </div>
    </div>
  );

  // Auth check
  if (!user) {
  return (
      <div className="max-w-4xl mx-auto glass-card p-8 text-center">
        <h2 className="text-2xl font-bold mb-4 vietnamese-text">Yêu cầu đăng nhập</h2>
        <p className="text-gray-600 vietnamese-text mb-4">
          Bạn cần đăng nhập để sử dụng tính năng đánh giá rủi ro.
        </p>
        <button
          onClick={() => window.location.href = '/dang-nhap'}
          className="bg-blue-600 text-white py-2 px-6 rounded-md hover:bg-blue-700 vietnamese-text"
        >
          Đăng nhập ngay
        </button>
        </div>
    );
  }

  // Main render
  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold vietnamese-text mb-2">Đánh giá rủi ro sử dụng chất kích thích</h1>
        <p className="text-gray-600 vietnamese-text">
          Công cụ đánh giá khoa học giúp xác định mức độ rủi ro và đưa ra lời khuyên phù hợp
        </p>
      </div>

      {currentStep === 'age-verification' && renderAgeVerification()}
      {currentStep === 'assessment' && renderAssessment()}
      {currentStep === 'results' && renderResults()}
    </div>
  );
};

export default Assessment;
