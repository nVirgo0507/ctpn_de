'use client';
import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { FiCheckCircle, FiBookOpen, FiMessageCircle, FiArrowRight, FiUsers, FiHeart, FiBook, FiBarChart2, FiShield, FiActivity } from 'react-icons/fi';

const Homepage = () => {
  const [homepageData, setHomepageData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHomepageData();
  }, []);

  const fetchHomepageData = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/homepage/data', {
        method: 'GET',
        headers: {
          'Origin': 'http://localhost:3000',
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();

      if (result.success) {
        setHomepageData(result.data);
      } else {
        throw new Error(result.message || 'Failed to fetch homepage data');
      }
    } catch (err) {
      console.error('Error fetching homepage data:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Loading state
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-50">
        <div className="glass-card p-8 text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="vietnamese-text text-gray-600 font-medium">Đang tải dữ liệu...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-50">
        <div className="glass-card p-8 text-center max-w-md border-red-100">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <FiShield className="w-8 h-8 text-red-500" />
          </div>
          <h2 className="text-xl font-bold text-gray-800 mb-2 vietnamese-text">
            Lỗi kết nối
          </h2>
          <p className="text-gray-600 vietnamese-text mb-6">
            Không thể tải dữ liệu trang chủ. Vui lòng kiểm tra kết nối mạng của bạn.
          </p>
          <button
            onClick={fetchHomepageData}
            className="glass-button-primary px-6 py-2 rounded-full font-medium vietnamese-text"
          >
            Thử lại
          </button>
        </div>
      </div>
    );
  }

  // Icon mapping for statistics
  const getStatIcon = (iconName) => {
    const iconMap = {
      'users': FiUsers,
      'heart': FiHeart,
      'book': FiBook,
      'chart-bar': FiBarChart2
    };
    return iconMap[iconName] || FiCheckCircle;
  };

  const features = [
    {
      icon: FiActivity,
      title: 'Đánh giá rủi ro',
      description: 'Kiểm tra mức độ rủi ro với công cụ đánh giá chuẩn quốc tế ASSIST và CRAFFT',
      link: '/assessment',
      color: 'from-blue-400 to-blue-600'
    },
    {
      icon: FiBookOpen,
      title: 'Khóa học giáo dục',
      description: 'Tham gia các khóa học giáo dục về tác hại và kỹ năng phòng ngừa',
      link: '/courses',
      color: 'from-cyan-400 to-cyan-600'
    },
    {
      icon: FiMessageCircle,
      title: 'Tư vấn chuyên gia',
      description: 'Kết nối với các chuyên gia tư vấn tình nguyện có kinh nghiệm',
      link: '/consultation',
      color: 'from-indigo-400 to-indigo-600'
    }
  ];

  return (
    <div className="space-y-24 pb-20">
      {/* Hero Section */}
      <section className="relative pt-20 pb-32 overflow-hidden">
        {/* Background Elements */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full h-full z-0">
          <div className="absolute top-20 left-10 w-72 h-72 bg-blue-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
          <div className="absolute top-20 right-10 w-72 h-72 bg-cyan-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
          <div className="absolute -bottom-8 left-20 w-72 h-72 bg-indigo-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>
        </div>

        <div className="container mx-auto px-4 relative z-10 text-center">
          <div className="inline-block mb-6 px-4 py-1.5 rounded-full bg-blue-50 border border-blue-100 text-blue-600 text-sm font-medium vietnamese-text animate-fade-in-up">
            👋 Chào mừng đến với Chung Tay Phòng Ngừa
          </div>

          <h1 className="text-5xl md:text-7xl font-bold mb-8 vietnamese-text tracking-tight">
            <span className="text-gray-900">Xây dựng cộng đồng</span>
            <br />
            <span className="text-gradient">Lành mạnh & An toàn</span>
          </h1>

          <p className="text-xl text-gray-600 mb-10 max-w-2xl mx-auto vietnamese-text leading-relaxed">
            {homepageData?.mission?.content || 'Nền tảng hỗ trợ phòng ngừa tệ nạn xã hội với các công cụ đánh giá khoa học, khóa học giáo dục và dịch vụ tư vấn chuyên nghiệp.'}
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link
              href="/assessment"
              className="glass-button-primary px-8 py-4 rounded-full font-semibold text-lg flex items-center gap-2 group vietnamese-text"
            >
              <span>Bắt đầu đánh giá</span>
              <FiArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
            </Link>
            <Link
              href="/courses"
              className="px-8 py-4 rounded-full font-semibold text-lg text-gray-600 hover:text-blue-600 hover:bg-blue-50 transition-all vietnamese-text"
            >
              Tìm hiểu thêm
            </Link>
          </div>
        </div>
      </section>

      {/* Statistics Section */}
      {homepageData?.statistics && (
        <section className="container mx-auto px-4">
          <div className="glass-card p-12 relative overflow-hidden">
            <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-500 via-cyan-500 to-blue-500"></div>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-8 md:gap-12">
              {homepageData.statistics.stats.map((stat, index) => {
                const StatIcon = getStatIcon(stat.icon);
                return (
                  <div key={index} className="text-center group">
                    <div className="w-16 h-16 mx-auto mb-4 rounded-2xl bg-blue-50 flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                      <StatIcon className="w-8 h-8 text-blue-600" />
                    </div>
                    <div className="text-4xl font-bold text-gray-900 mb-2 vietnamese-text">
                      {stat.value}
                    </div>
                    <div className="text-sm font-medium text-gray-500 uppercase tracking-wider vietnamese-text">
                      {stat.label}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </section>
      )}

      {/* Features Section */}
      <section className="container mx-auto px-4">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4 vietnamese-text">
            Giải pháp toàn diện
          </h2>
          <p className="text-gray-600 max-w-2xl mx-auto vietnamese-text">
            Chúng tôi cung cấp các công cụ và dịch vụ cần thiết để hỗ trợ cộng đồng trong việc phòng ngừa và giảm thiểu rủi ro.
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <Link
                key={index}
                href={feature.link}
                className="group relative"
              >
                <div className="absolute inset-0 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-3xl opacity-0 group-hover:opacity-5 transition-opacity duration-500"></div>
                <div className="glass-card p-8 h-full hover:-translate-y-2 transition-transform duration-300 border-transparent hover:border-blue-100">
                  <div className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${feature.color} flex items-center justify-center mb-6 shadow-lg shadow-blue-500/20 group-hover:scale-110 transition-transform duration-300`}>
                    <Icon className="w-7 h-7 text-white" />
                  </div>
                  <h3 className="text-xl font-bold text-gray-900 mb-3 vietnamese-text group-hover:text-blue-600 transition-colors">
                    {feature.title}
                  </h3>
                  <p className="text-gray-600 leading-relaxed mb-6 vietnamese-text">
                    {feature.description}
                  </p>
                  <div className="flex items-center text-blue-600 font-medium vietnamese-text group/link">
                    <span>Khám phá ngay</span>
                    <FiArrowRight className="w-4 h-4 ml-2 group-hover/link:translate-x-1 transition-transform" />
                  </div>
                </div>
              </Link>
            );
          })}
        </div>
      </section>

      {/* Latest Blog Posts Section */}
      {homepageData?.latestBlogs && homepageData.latestBlogs.length > 0 && (
        <section className="container mx-auto px-4">
          <div className="flex justify-between items-end mb-12">
            <div>
              <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4 vietnamese-text">
                Tin tức & Sự kiện
              </h2>
              <p className="text-gray-600 vietnamese-text">
                Cập nhật những thông tin mới nhất từ cộng đồng
              </p>
            </div>
            <Link
              href="/blog"
              className="hidden md:flex items-center text-blue-600 font-medium hover:text-blue-700 vietnamese-text group"
            >
              <span>Xem tất cả</span>
              <FiArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {homepageData.latestBlogs.map((post, index) => (
              <article
                key={post.postId || index}
                className="glass-card p-6 hover:shadow-xl transition-all duration-300 group cursor-pointer"
              >
                <div className="flex items-center gap-4 mb-4 text-sm text-gray-500">
                  <span className="px-3 py-1 rounded-full bg-blue-50 text-blue-600 font-medium text-xs uppercase tracking-wider vietnamese-text">
                    {post.categoryName}
                  </span>
                  <span className="vietnamese-text">
                    {new Date(post.publishedAt).toLocaleDateString('vi-VN')}
                  </span>
                </div>
                <h3 className="text-xl font-bold text-gray-900 mb-3 vietnamese-text group-hover:text-blue-600 transition-colors line-clamp-2">
                  {post.title}
                </h3>
                <p className="text-gray-600 vietnamese-text leading-relaxed mb-6 line-clamp-3">
                  {post.excerpt}
                </p>
                <div className="flex items-center text-blue-600 font-medium text-sm vietnamese-text">
                  Đọc tiếp →
                </div>
              </article>
            ))}
          </div>

          <div className="mt-8 text-center md:hidden">
            <Link
              href="/blog"
              className="inline-flex items-center text-blue-600 font-medium hover:text-blue-700 vietnamese-text"
            >
              <span>Xem tất cả bài viết</span>
              <FiArrowRight className="w-5 h-5 ml-2" />
            </Link>
          </div>
        </section>
      )}

      {/* About Section */}
      <section className="container mx-auto px-4">
        <div className="glass-card p-8 md:p-16 relative overflow-hidden">
          <div className="absolute top-0 right-0 w-1/2 h-full bg-gradient-to-l from-blue-50 to-transparent opacity-50 pointer-events-none"></div>

          <div className="relative z-10 max-w-4xl mx-auto text-center">
            <h2 className="text-3xl md:text-4xl font-bold mb-8 text-gray-900 vietnamese-text">
              {homepageData?.mission?.title || 'Về Chung Tay Phòng Ngừa'}
            </h2>

            <div className="grid md:grid-cols-2 gap-12 items-center text-left">
              <div>
                <p className="text-lg text-gray-600 vietnamese-text leading-relaxed mb-8">
                  {homepageData?.mission?.content || 'Chung Tay Phòng Ngừa là nền tảng trực tuyến hỗ trợ cộng đồng trong việc phòng ngừa tệ nạn xã hội, đặc biệt là các vấn đề liên quan đến ma túy.'}
                </p>

                <div className="space-y-4">
                  {homepageData?.mission?.values ? (
                    homepageData.mission.values.map((value, index) => (
                      <div key={index} className="flex items-center space-x-3 group">
                        <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center group-hover:scale-110 transition-transform">
                          <FiCheckCircle className="w-5 h-5 text-green-600" />
                        </div>
                        <span className="vietnamese-text text-gray-700 font-medium">{value}</span>
                      </div>
                    ))
                  ) : (
                    <>
                      <div className="flex items-center space-x-3 group">
                        <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center group-hover:scale-110 transition-transform">
                          <FiCheckCircle className="w-5 h-5 text-green-600" />
                        </div>
                        <span className="vietnamese-text text-gray-700 font-medium">Công cụ đánh giá chuẩn quốc tế</span>
                      </div>
                      <div className="flex items-center space-x-3 group">
                        <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center group-hover:scale-110 transition-transform">
                          <FiCheckCircle className="w-5 h-5 text-green-600" />
                        </div>
                        <span className="vietnamese-text text-gray-700 font-medium">Chương trình giáo dục chất lượng</span>
                      </div>
                      <div className="flex items-center space-x-3 group">
                        <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center group-hover:scale-110 transition-transform">
                          <FiCheckCircle className="w-5 h-5 text-green-600" />
                        </div>
                        <span className="vietnamese-text text-gray-700 font-medium">Tư vấn miễn phí từ chuyên gia</span>
                      </div>
                    </>
                  )}
                </div>
              </div>

              <div className="glass-card p-8 bg-white/50 border-blue-100">
                <h3 className="text-xl font-bold mb-6 text-gray-900 vietnamese-text flex items-center">
                  <span className="w-2 h-8 bg-blue-500 rounded-full mr-3"></span>
                  Thông tin liên hệ
                </h3>
                <div className="space-y-4 vietnamese-text text-gray-600">
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center flex-shrink-0">
                      <FiMessageCircle className="w-5 h-5 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-sm text-gray-500 font-medium mb-1">Điện thoại</p>
                      <p className="text-gray-900 font-semibold">{homepageData?.contact?.phone || '0337315535'}</p>
                    </div>
                  </div>

                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center flex-shrink-0">
                      <FiMessageCircle className="w-5 h-5 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-sm text-gray-500 font-medium mb-1">Email</p>
                      <p className="text-gray-900 font-semibold">{homepageData?.contact?.email || 'chungtay.adm@gmail.com'}</p>
                    </div>
                  </div>

                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center flex-shrink-0">
                      <FiMessageCircle className="w-5 h-5 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-sm text-gray-500 font-medium mb-1">Địa chỉ</p>
                      <p className="text-gray-900 font-semibold">{homepageData?.contact?.address || 'Tp. Hồ Chí Minh, Việt Nam'}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Homepage;
