import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FiCheckCircle, FiBookOpen, FiMessageCircle, FiArrowRight, FiUsers, FiHeart, FiBook, FiBarChart2 } from 'react-icons/fi';

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
      <div className="min-h-screen flex items-center justify-center">
        <div className="glass-card p-8 text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="vietnamese-text text-gray-600">Đang tải...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="glass-card p-8 text-center max-w-md">
          <h2 className="text-xl font-bold text-red-600 mb-4 vietnamese-text">
            Lỗi kết nối
          </h2>
          <p className="text-gray-600 vietnamese-text mb-4">
            Không thể tải dữ liệu trang chủ. Vui lòng thử lại.
          </p>
          <button
            onClick={fetchHomepageData}
            className="bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 vietnamese-text"
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
      icon: FiCheckCircle,
      title: 'Đánh giá rủi ro',
      description: 'Kiểm tra mức độ rủi ro với công cụ đánh giá chuẩn quốc tế ASSIST và CRAFFT',
      link: '/assessment',
      color: 'bg-blue-500'
    },
    {
      icon: FiBookOpen,
      title: 'Khóa học học tập',
      description: 'Tham gia các khóa học giáo dục về tác hại và kỹ năng phòng ngừa',
      link: '/courses',
      color: 'bg-green-500'
    },
    {
      icon: FiMessageCircle,
      title: 'Tư vấn chuyên gia',
      description: 'Kết nối với các chuyên gia tư vấn tình nguyện có kinh nghiệm',
      link: '/consultation',
      color: 'bg-purple-500'
    }
  ];

  return (
    <div className="space-y-16">
      {/* Hero Section */}
      <section className="hero-gradient rounded-glass p-8 md:p-16 text-center text-white">
        <div className="max-w-4xl mx-auto">
          <h1 className="text-4xl md:text-6xl font-bold mb-6 vietnamese-text">
            {homepageData?.organization?.name || 'Chung Tay Phòng Ngừa'}
          </h1>
          <p className="text-xl md:text-2xl mb-4 vietnamese-text opacity-90">
            {homepageData?.organization?.tagline || 'Cùng nhau xây dựng cộng đồng lành mạnh, an toàn'}
          </p>
          <p className="text-lg mb-8 vietnamese-text opacity-80 max-w-2xl mx-auto">
            {homepageData?.mission?.content || 'Nền tảng hỗ trợ phòng ngừa tệ nạn xã hội với các công cụ đánh giá khoa học, khóa học giáo dục và dịch vụ tư vấn chuyên nghiệp.'}
          </p>
          <Link
            to="/assessment"
            className="inline-flex items-center space-x-2 bg-white text-blue-600 px-8 py-4 rounded-lg font-semibold hover:bg-opacity-90 transition-all duration-300 vietnamese-text"
          >
            <span>Bắt đầu đánh giá ngay</span>
            <FiArrowRight className="w-5 h-5" />
          </Link>
        </div>
      </section>

      {/* Statistics Section */}
      {homepageData?.statistics && (
        <section className="glass-card p-8 md:p-12">
          <h2 className="text-3xl font-bold mb-8 text-center text-gray-800 vietnamese-text">
            {homepageData.statistics.title}
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {homepageData.statistics.stats.map((stat, index) => {
              const StatIcon = getStatIcon(stat.icon);
              return (
                <div key={index} className="text-center">
                  <div className="bg-blue-500 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-3">
                    <StatIcon className="w-6 h-6 text-white" />
                  </div>
                  <div className="text-2xl font-bold text-blue-600 vietnamese-text">
                    {stat.value}
                  </div>
                  <div className="text-sm text-gray-600 vietnamese-text">
                    {stat.label}
                  </div>
                </div>
              );
            })}
          </div>
        </section>
      )}

      {/* Features Section */}
      <section className="grid md:grid-cols-3 gap-8">
        {features.map((feature, index) => {
          const Icon = feature.icon;
          return (
            <Link
              key={index}
              to={feature.link}
              className="glass-card p-8 text-center hover:transform hover:scale-105 transition-all duration-300 group"
            >
              <div className={`${feature.color} w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300`}>
                <Icon className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-xl font-semibold mb-4 text-gray-800 vietnamese-text">
                {feature.title}
              </h3>
              <p className="text-gray-600 vietnamese-text leading-relaxed">
                {feature.description}
              </p>
              <div className="mt-6 flex items-center justify-center text-blue-600 group-hover:text-blue-700">
                <span className="vietnamese-text">Tìm hiểu thêm</span>
                <FiArrowRight className="w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform duration-300" />
              </div>
            </Link>
          );
        })}
      </section>

      {/* Latest Blog Posts Section (FR-001 requirement) */}
      {homepageData?.latestBlogs && homepageData.latestBlogs.length > 0 && (
        <section className="glass-card p-8 md:p-12">
          <div className="flex justify-between items-center mb-8">
            <h2 className="text-3xl font-bold text-gray-800 vietnamese-text">
              Bài viết mới nhất
            </h2>
            <Link
              to="/blog"
              className="text-blue-600 hover:text-blue-700 vietnamese-text font-medium"
            >
              Xem tất cả
            </Link>
          </div>
          <div className="grid md:grid-cols-2 gap-6">
            {homepageData.latestBlogs.map((post, index) => (
              <article key={post.postId || index} className="bg-white bg-opacity-60 rounded-lg p-6 hover:bg-opacity-80 transition-all duration-300">
                <h3 className="text-lg font-semibold mb-3 text-gray-800 vietnamese-text line-clamp-2">
                  {post.title}
                </h3>
                <p className="text-gray-600 vietnamese-text leading-relaxed mb-4 line-clamp-3">
                  {post.excerpt}
                </p>
                <div className="flex justify-between items-center text-sm text-gray-500">
                  <span className="vietnamese-text">{post.categoryName}</span>
                  <span className="vietnamese-text">
                    {new Date(post.publishedAt).toLocaleDateString('vi-VN')}
                  </span>
                </div>
              </article>
            ))}
          </div>
        </section>
      )}

      {/* About Section with Dynamic Content */}
      <section className="glass-card p-8 md:p-12">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-3xl font-bold mb-6 text-gray-800 vietnamese-text">
            {homepageData?.mission?.title || 'Về Chung Tay Phòng Ngừa'}
          </h2>
          <div className="grid md:grid-cols-2 gap-8 items-center">
            <div className="text-left">
              <p className="text-gray-600 vietnamese-text leading-relaxed mb-6">
                {homepageData?.mission?.content || 'Chung Tay Phòng Ngừa là nền tảng trực tuyến hỗ trợ cộng đồng trong việc phòng ngừa tệ nạn xã hội, đặc biệt là các vấn đề liên quan đến ma túy.'}
              </p>
              <div className="space-y-2">
                {homepageData?.mission?.values ? (
                  homepageData.mission.values.map((value, index) => (
                    <div key={index} className="flex items-center space-x-3">
                      <FiCheckCircle className="w-5 h-5 text-green-500" />
                      <span className="vietnamese-text text-gray-700">{value}</span>
                    </div>
                  ))
                ) : (
                  <>
                    <div className="flex items-center space-x-3">
                      <FiCheckCircle className="w-5 h-5 text-green-500" />
                      <span className="vietnamese-text text-gray-700">Công cụ đánh giá chuẩn quốc tế</span>
                    </div>
                    <div className="flex items-center space-x-3">
                      <FiCheckCircle className="w-5 h-5 text-green-500" />
                      <span className="vietnamese-text text-gray-700">Chương trình giáo dục chất lượng</span>
                    </div>
                    <div className="flex items-center space-x-3">
                      <FiCheckCircle className="w-5 h-5 text-green-500" />
                      <span className="vietnamese-text text-gray-700">Tư vấn miễn phí từ chuyên gia</span>
                    </div>
                  </>
                )}
              </div>
            </div>
            <div className="glass-card p-6 bg-blue-50">
              <h3 className="text-xl font-semibold mb-4 text-gray-800 vietnamese-text">
                Liên hệ với chúng tôi
              </h3>
              <div className="space-y-3 text-sm vietnamese-text text-gray-600">
                <p><strong>Điện thoại:</strong> {homepageData?.contact?.phone || '0337315535'}</p>
                <p><strong>Email:</strong> {homepageData?.contact?.email || 'chungtay.adm@gmail.com'}</p>
                <p><strong>Địa chỉ:</strong> {homepageData?.contact?.address || 'Tp. Hồ Chí Minh, Việt Nam'}</p>
                {homepageData?.contact?.workingHours && (
                  <p><strong>Giờ làm việc:</strong> {homepageData.contact.workingHours}</p>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Homepage;
