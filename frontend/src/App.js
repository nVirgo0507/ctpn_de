import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

// Error Logging
import errorLogger, { logError } from './utils/errorLogger';

// Components
import Header from './components/common/Header';
import Footer from './components/common/Footer';
import Homepage from './components/pages/Homepage';
import Assessment from './components/assessment/Assessment';
import CourseList from './components/learning/CourseList';
import MyCourses from './components/learning/MyCourses';
import ConsultantList from './components/consultation/ConsultantList';
import MyConsultations from './components/consultation/MyConsultations';
import Blog from './components/pages/Blog';
import PublicSurveys from './components/survey/PublicSurveys';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import ProtectedRoute from './components/auth/ProtectedRoute';
import AdminRoute from './components/auth/AdminRoute';
import AdminDashboard from './components/admin/AdminDashboard';

// Contexts
import { AuthProvider } from './contexts/AuthContext';
import { UserProvider } from './contexts/UserContext';

// Error Boundary Component
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    // Log the error using our error logger
    logError({
      type: 'COMPONENT_ERROR',
      message: error.message,
      stack: error.stack,
      componentStack: errorInfo.componentStack,
      props: this.props
    });
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100 flex items-center justify-center">
          <div className="glass-card p-8 max-w-md mx-auto text-center">
            <h2 className="text-2xl font-bold mb-4 text-red-600 vietnamese-text">
              Đã xảy ra lỗi
            </h2>
            <p className="text-gray-600 vietnamese-text mb-4">
              Ứng dụng gặp lỗi không mong muốn. Vui lòng thử lại.
            </p>
            <button
              onClick={() => window.location.reload()}
              className="bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 vietnamese-text"
            >
              Tải lại trang
            </button>
            {process.env.NODE_ENV === 'development' && (
              <details className="mt-4 text-left">
                <summary className="cursor-pointer text-sm text-gray-500">
                  Chi tiết lỗi (Development)
                </summary>
                <pre className="text-xs text-red-600 mt-2 overflow-auto">
                  {this.state.error?.toString()}
                </pre>
              </details>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

function App() {
  // Initialize error logging in development
  React.useEffect(() => {
    if (process.env.NODE_ENV === 'development') {
      console.log('🔧 Error Logger initialized for development');
      // Add global debug commands
      window.debugErrorLogger = () => errorLogger.debugInfo();
      window.exportErrorLogs = () => errorLogger.exportLogs();
      window.clearErrorLogs = () => errorLogger.clearLogs();
    }
  }, []);

  return (
    <ErrorBoundary>
      <AuthProvider>
        <UserProvider>
          <Router>
            <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100">
              <Header />
              
              <main className="container mx-auto px-4 py-8 min-h-screen">
                <Routes>
                  {/* Public Routes */}
                  <Route path="/" element={<Homepage />} />
                  <Route path="/dang-nhap" element={<LoginForm />} />
                  <Route path="/dang-ky" element={<RegisterForm />} />
                  <Route path="/blog" element={<Blog />} />
                  <Route path="/surveys" element={<PublicSurveys />} />
                  <Route path="/khao-sat" element={<PublicSurveys />} />
                  
                  {/* Protected Routes (Member+) */}
                  <Route element={<ProtectedRoute />}>
                    <Route path="/assessment" element={<Assessment />} />
                    <Route path="/courses" element={<CourseList />} />
                    <Route path="/my-courses" element={<MyCourses />} />
                    <Route path="/consultation" element={<ConsultantList />} />
                    <Route path="/my-consultations" element={<MyConsultations />} />
                  </Route>
                  
                  {/* Admin Routes */}
                  <Route element={<AdminRoute />}>
                    <Route path="/admin" element={<AdminDashboard />} />
                  </Route>
                  
                  {/* Fallback for other admin-prefixed routes */}
                  <Route path="/quan-ly/*" element={<div>Management Panel (Coming Soon)</div>} />
                  <Route path="/staff/*" element={<div>Staff Panel (Coming Soon)</div>} />
                </Routes>
              </main>
              
              <Footer />
              
              <ToastContainer
                position="top-right"
                autoClose={5000}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme="light"
              />
            </div>
          </Router>
        </UserProvider>
      </AuthProvider>
    </ErrorBoundary>
  );
}

export default App;
