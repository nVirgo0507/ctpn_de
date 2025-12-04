'use client';
import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter, usePathname } from 'next/navigation';
import { FiHome, FiBookOpen, FiMessageCircle, FiEdit3, FiMenu, FiX, FiLogOut, FiUser, FiGrid, FiCalendar, FiLayers } from 'react-icons/fi';
import { useAuthContext } from '../../contexts/AuthContext';

const Header = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const router = useRouter();
  const pathname = usePathname();
  const { user, logout, isAuthenticated } = useAuthContext();

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleLogout = () => {
    logout();
    router.push('/');
    setIsMenuOpen(false);
  };

  const isAdmin = user && user.role && user.role.toUpperCase() === 'ADMIN';

  const navigationItems = [
    { path: '/', label: 'Trang chủ', icon: FiHome, public: true },
    { path: '/assessment', label: 'Đánh giá rủi ro', icon: FiEdit3, protected: true },
    { path: '/courses', label: 'Khóa học', icon: FiBookOpen, protected: true },
    { path: '/consultation', label: 'Tư vấn', icon: FiMessageCircle, protected: true },
    { path: '/my-consultations', label: 'Lịch tư vấn', icon: FiCalendar, protected: true },
    { path: '/my-courses', label: 'Khóa học của tôi', icon: FiLayers, protected: true },
    { path: '/blog', label: 'Blog', icon: FiEdit3, public: true },
    { path: '/surveys', label: 'Khảo sát', icon: FiEdit3, public: true },
    { path: '/admin', label: 'Admin Dashboard', icon: FiGrid, adminOnly: true }
  ];

  return (
    <header className={`sticky top-0 z-50 transition-all duration-300 ${scrolled ? 'glass-nav py-2' : 'bg-transparent py-4'}`}>
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between">
          {/* Logo */}
          <Link href="/" className="flex items-center space-x-3 group">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-cyan-400 rounded-xl flex items-center justify-center shadow-lg group-hover:shadow-blue-500/30 transition-all duration-300">
              <span className="text-white font-bold text-lg">CT</span>
            </div>
            <div className="hidden lg:block">
              <h1 className="text-xl font-bold text-gray-800 vietnamese-text group-hover:text-blue-600 transition-colors">
                Chung Tay Phòng Ngừa
              </h1>
            </div>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden xl:flex items-center space-x-1 bg-white/30 backdrop-blur-md px-4 py-2 rounded-full border border-white/40 shadow-sm">
            {navigationItems.map((item) => {
              const isVisible = item.public || (item.protected && isAuthenticated) || (item.adminOnly && isAdmin);
              if (!isVisible) return null;

              const isActive = pathname === item.path;

              return (
                <Link
                  key={item.path}
                  href={item.path}
                  className={`px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 ${isActive
                    ? 'bg-blue-500 text-white shadow-md'
                    : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'
                    } vietnamese-text whitespace-nowrap`}
                >
                  {item.label}
                </Link>
              );
            })}
          </nav>

          {/* User Actions */}
          <div className="flex items-center space-x-4">
            {isAuthenticated ? (
              <div className="flex items-center space-x-3">
                <div className="hidden lg:flex items-center space-x-2 px-3 py-1.5 bg-blue-50 rounded-full border border-blue-100">
                  <FiUser className="w-4 h-4 text-blue-600" />
                  <span className="text-sm font-medium text-blue-800 vietnamese-text">
                    {user?.fullName || 'User'}
                  </span>
                </div>
                <button
                  onClick={handleLogout}
                  className="p-2 text-red-500 hover:bg-red-50 rounded-full transition-colors"
                  title="Đăng xuất"
                >
                  <FiLogOut className="w-5 h-5" />
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-3">
                <Link
                  href="/dang-nhap"
                  className="px-5 py-2 text-gray-700 font-medium hover:text-blue-600 transition-colors vietnamese-text"
                >
                  Đăng nhập
                </Link>
                <Link
                  href="/dang-ky"
                  className="px-5 py-2 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-full font-medium shadow-lg shadow-blue-500/30 hover:shadow-blue-500/40 hover:-translate-y-0.5 transition-all duration-300 vietnamese-text"
                >
                  Đăng ký
                </Link>
              </div>
            )}

            {/* Mobile Menu Button */}
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="xl:hidden p-2 text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
            >
              {isMenuOpen ? <FiX className="w-6 h-6" /> : <FiMenu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="xl:hidden mt-4 glass-card p-4 animate-fade-in-down">
            <nav className="flex flex-col space-y-2">
              {navigationItems.map((item) => {
                const isVisible = item.public || (item.protected && isAuthenticated) || (item.adminOnly && isAdmin);
                if (!isVisible) return null;
                const Icon = item.icon;
                const isActive = pathname === item.path;

                return (
                  <Link
                    key={item.path}
                    href={item.path}
                    onClick={() => setIsMenuOpen(false)}
                    className={`flex items-center space-x-3 px-4 py-3 rounded-xl transition-all duration-200 ${isActive
                      ? 'bg-blue-50 text-blue-600 font-semibold'
                      : 'text-gray-600 hover:bg-gray-50'
                      }`}
                  >
                    <Icon className="w-5 h-5" />
                    <span>{item.label}</span>
                  </Link>
                );
              })}

              {isAuthenticated && (
                <button
                  onClick={handleLogout}
                  className="flex items-center space-x-3 px-4 py-3 rounded-xl text-red-600 hover:bg-red-50 transition-all duration-200 w-full text-left"
                >
                  <FiLogOut className="w-5 h-5" />
                  <span>Đăng xuất</span>
                </button>
              )}
            </nav>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;