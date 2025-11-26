import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiHome, FiBookOpen, FiMessageCircle, FiEdit3, FiMenu, FiX, FiLogOut, FiUser, FiGrid } from 'react-icons/fi';
import { useAuthContext } from '../../contexts/AuthContext';

const Header = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const navigate = useNavigate();
  const { user, logout, isAuthenticated } = useAuthContext();

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsMenuOpen(false);
  };

  const isAdmin = user && user.role && user.role.toUpperCase() === 'ADMIN';

  const navigationItems = [
    { path: '/', label: 'Trang chủ', icon: FiHome, public: true },
    { path: '/assessment', label: 'Đánh giá rủi ro', icon: FiEdit3, protected: true },
    { path: '/courses', label: 'Khóa học', icon: FiBookOpen, protected: true },
    { path: '/consultation', label: 'Tư vấn', icon: FiMessageCircle, protected: true },
    { path: '/blog', label: 'Blog', icon: FiEdit3, public: true },
    { path: '/surveys', label: 'Khảo sát', icon: FiEdit3, public: true },
    { path: '/admin', label: 'Admin Dashboard', icon: FiGrid, adminOnly: true }
  ];

  return (
    <header className="sticky top-0 z-50 glass-card border-b border-glass-border">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-20">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-3 flex-shrink-0">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center flex-shrink-0">
              <span className="text-white font-bold text-lg">CT</span>
            </div>
            <div className="hidden lg:block">
              <h1 className="text-xl font-bold text-gray-800 vietnamese-text whitespace-nowrap">
                Chung Tay Phòng Ngừa
              </h1>
              <p className="text-sm text-gray-600 vietnamese-text whitespace-nowrap">
                Tỉnh táo là lựa chọn
              </p>
            </div>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden xl:flex space-x-6 items-center">
            <Link to="/" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
              Trang chủ
            </Link>
            <Link to="/assessment" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
              Đánh giá
            </Link>
            <Link to="/courses" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
              Khóa học
            </Link>
            {isAuthenticated && (
              <Link to="/my-courses" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
                Khóa học của tôi
              </Link>
            )}
            <Link to="/consultation" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
              Tư vấn
            </Link>
            {isAuthenticated && (
              <Link to="/my-consultations" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
                Lịch tư vấn
              </Link>
            )}
            <Link to="/blog" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
              Blog
            </Link>
            <Link to="/surveys" className="text-gray-700 hover:text-blue-600 font-medium transition-colors vietnamese-text whitespace-nowrap">
              Khảo sát
            </Link>
            {isAdmin && (
              <Link to="/admin" className="flex items-center space-x-1 text-red-600 hover:text-red-700 font-medium transition-colors vietnamese-text bg-red-50 hover:bg-red-100 px-3 py-1.5 rounded-full whitespace-nowrap">
                <FiGrid className="w-4 h-4" />
                <span>Admin</span>
              </Link>
            )}
          </nav>

          {/* User Actions */}
          <div className="flex items-center space-x-4 flex-shrink-0">
            {isAuthenticated ? (
              <div className="flex items-center space-x-4">
                {/* User Info */}
                <div className="hidden lg:flex items-center space-x-2 text-gray-700">
                  <FiUser className="w-4 h-4" />
                  <span className="vietnamese-text text-sm whitespace-nowrap">
                    Xin chào, {user?.fullName || user?.email || 'User'}
                  </span>
                </div>

                {/* Logout Button */}
                <button
                  onClick={handleLogout}
                  className="flex items-center space-x-2 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors duration-200 whitespace-nowrap"
                >
                  <FiLogOut className="w-4 h-4" />
                  <span className="hidden lg:inline vietnamese-text">Đăng xuất</span>
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-3">
                <Link
                  to="/dang-nhap"
                  className="px-4 py-2 text-gray-700 hover:text-blue-600 font-medium transition-colors duration-200 vietnamese-text whitespace-nowrap"
                >
                  Đăng nhập
                </Link>
                <Link
                  to="/dang-ky"
                  className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors duration-200 vietnamese-text whitespace-nowrap"
                >
                  Đăng ký
                </Link>
              </div>
            )}

            {/* Mobile Menu Button */}
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="xl:hidden p-2 rounded-lg text-gray-700 hover:bg-gray-100 transition-colors duration-200"
            >
              {isMenuOpen ? <FiX className="w-5 h-5" /> : <FiMenu className="w-5 h-5" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="xl:hidden py-4 border-t border-glass-border">
            <nav className="flex flex-col space-y-2">
              {/* User Info on Mobile */}
              {isAuthenticated && (
                <div className="flex items-center space-x-3 px-4 py-3 bg-gray-50 rounded-lg mb-2">
                  <FiUser className="w-4 h-4 text-gray-600" />
                  <span className="vietnamese-text text-sm text-gray-700">
                    {user?.fullName || user?.email || 'User'}
                  </span>
                </div>
              )}

              {/* Navigation Items */}
              {navigationItems.map((item) => {
                const Icon = item.icon;
                const isVisible = item.public || (item.protected && isAuthenticated) || (item.adminOnly && isAdmin);

                if (!isVisible) return null;

                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    onClick={() => setIsMenuOpen(false)}
                    className="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition-all duration-200 vietnamese-text"
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.label}</span>
                  </Link>
                );
              })}

              {/* Additional Auth-specific Mobile Links */}
              {isAuthenticated && (
                <>
                  <Link
                    to="/my-courses"
                    onClick={() => setIsMenuOpen(false)}
                    className="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition-all duration-200 vietnamese-text"
                  >
                    <FiBookOpen className="w-4 h-4" />
                    <span>Khóa học của tôi</span>
                  </Link>
                  <Link
                    to="/my-consultations"
                    onClick={() => setIsMenuOpen(false)}
                    className="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition-all duration-200 vietnamese-text"
                  >
                    <FiMessageCircle className="w-4 h-4" />
                    <span>Lịch tư vấn</span>
                  </Link>
                </>
              )}

              {/* Mobile Auth Links */}
              {!isAuthenticated && (
                <div className="pt-4 border-t border-gray-200 space-y-2">
                  <Link
                    to="/dang-nhap"
                    onClick={() => setIsMenuOpen(false)}
                    className="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition-all duration-200 vietnamese-text"
                  >
                    <span>Đăng nhập</span>
                  </Link>
                  <Link
                    to="/dang-ky"
                    onClick={() => setIsMenuOpen(false)}
                    className="flex items-center space-x-3 px-4 py-3 rounded-lg bg-blue-500 text-white hover:bg-blue-600 transition-all duration-200 vietnamese-text"
                  >
                    <span>Đăng ký</span>
                  </Link>
                </div>
              )}

              {/* Mobile Logout */}
              {isAuthenticated && (
                <div className="pt-4 border-t border-gray-200">
                  <button
                    onClick={handleLogout}
                    className="flex items-center space-x-3 px-4 py-3 rounded-lg text-red-600 hover:bg-red-50 transition-all duration-200 vietnamese-text w-full text-left"
                  >
                    <FiLogOut className="w-4 h-4" />
                    <span>Đăng xuất</span>
                  </button>
                </div>
              )}
            </nav>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;