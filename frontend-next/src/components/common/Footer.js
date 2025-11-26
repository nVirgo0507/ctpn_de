import React from 'react';
import Link from 'next/link';
import { FiFacebook, FiInstagram, FiYoutube, FiMail, FiPhone, FiMapPin } from 'react-icons/fi';

const Footer = () => {
  return (
    <footer className="mt-20 relative z-10">
      <div className="absolute inset-0 bg-gradient-to-b from-transparent to-white/50 pointer-events-none"></div>
      <div className="container mx-auto px-4 pb-8 relative">
        <div className="glass-card p-8 md:p-12">
          <div className="grid md:grid-cols-4 gap-12">
            {/* Brand Section */}
            <div className="col-span-1 md:col-span-2">
              <Link href="/" className="flex items-center space-x-3 mb-6">
                <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-cyan-400 rounded-xl flex items-center justify-center shadow-lg">
                  <span className="text-white font-bold text-lg">CT</span>
                </div>
                <span className="text-xl font-bold text-gray-800 vietnamese-text">
                  Chung Tay Phòng Ngừa
                </span>
              </Link>
              <p className="text-gray-600 vietnamese-text leading-relaxed mb-6 max-w-md">
                Nền tảng hỗ trợ phòng ngừa tệ nạn xã hội với các công cụ đánh giá khoa học,
                khóa học giáo dục và dịch vụ tư vấn chuyên nghiệp từ các chuyên gia hàng đầu.
              </p>
              <div className="flex space-x-4">
                <a href="#" className="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center text-blue-600 hover:bg-blue-500 hover:text-white transition-all duration-300">
                  <FiFacebook className="w-5 h-5" />
                </a>
                <a href="#" className="w-10 h-10 rounded-full bg-pink-50 flex items-center justify-center text-pink-600 hover:bg-pink-500 hover:text-white transition-all duration-300">
                  <FiInstagram className="w-5 h-5" />
                </a>
                <a href="#" className="w-10 h-10 rounded-full bg-red-50 flex items-center justify-center text-red-600 hover:bg-red-500 hover:text-white transition-all duration-300">
                  <FiYoutube className="w-5 h-5" />
                </a>
              </div>
            </div>

            {/* Quick Links */}
            <div>
              <h3 className="text-lg font-bold text-gray-800 mb-6 vietnamese-text">
                Liên kết nhanh
              </h3>
              <ul className="space-y-4">
                <li>
                  <Link href="/assessment" className="text-gray-600 hover:text-blue-600 transition-colors vietnamese-text flex items-center group">
                    <span className="w-1.5 h-1.5 rounded-full bg-blue-200 mr-2 group-hover:bg-blue-500 transition-colors"></span>
                    Đánh giá rủi ro
                  </Link>
                </li>
                <li>
                  <Link href="/courses" className="text-gray-600 hover:text-blue-600 transition-colors vietnamese-text flex items-center group">
                    <span className="w-1.5 h-1.5 rounded-full bg-blue-200 mr-2 group-hover:bg-blue-500 transition-colors"></span>
                    Khóa học
                  </Link>
                </li>
                <li>
                  <Link href="/consultation" className="text-gray-600 hover:text-blue-600 transition-colors vietnamese-text flex items-center group">
                    <span className="w-1.5 h-1.5 rounded-full bg-blue-200 mr-2 group-hover:bg-blue-500 transition-colors"></span>
                    Tư vấn chuyên gia
                  </Link>
                </li>
                <li>
                  <Link href="/blog" className="text-gray-600 hover:text-blue-600 transition-colors vietnamese-text flex items-center group">
                    <span className="w-1.5 h-1.5 rounded-full bg-blue-200 mr-2 group-hover:bg-blue-500 transition-colors"></span>
                    Tin tức & Sự kiện
                  </Link>
                </li>
              </ul>
            </div>

            {/* Contact Info */}
            <div>
              <h3 className="text-lg font-bold text-gray-800 mb-6 vietnamese-text">
                Liên hệ
              </h3>
              <ul className="space-y-4">
                <li className="flex items-start space-x-3 text-gray-600">
                  <FiPhone className="w-5 h-5 text-blue-500 mt-0.5 flex-shrink-0" />
                  <span className="vietnamese-text">0337315535</span>
                </li>
                <li className="flex items-start space-x-3 text-gray-600">
                  <FiMail className="w-5 h-5 text-blue-500 mt-0.5 flex-shrink-0" />
                  <span className="vietnamese-text">chungtay.adm@gmail.com</span>
                </li>
                <li className="flex items-start space-x-3 text-gray-600">
                  <FiMapPin className="w-5 h-5 text-blue-500 mt-0.5 flex-shrink-0" />
                  <span className="vietnamese-text">
                    7 Đường D1, Long Thạnh Mỹ, TP. Thủ Đức, TP. HCM
                  </span>
                </li>
              </ul>
            </div>
          </div>

          <div className="border-t border-gray-200/50 mt-12 pt-8 text-center">
            <p className="text-gray-500 text-sm vietnamese-text">
              © 2025 Chung Tay Phòng Ngừa. Dự án học thuật - Không dành cho mục đích thương mại.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
