import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="glass-card mt-16 border-t border-glass-border">
      <div className="container mx-auto px-4 py-8">
        <div className="grid md:grid-cols-3 gap-8">
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800 vietnamese-text">
              Chung Tay Phòng Ngừa
            </h3>
            <p className="text-gray-600 vietnamese-text leading-relaxed">
              Nền tảng hỗ trợ phòng ngừa tệ nạn xã hội với các công cụ đánh giá khoa học 
              và dịch vụ tư vấn chuyên nghiệp.
            </p>
          </div>
          
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800 vietnamese-text">
              Liên kết nhanh
            </h3>
            <ul className="space-y-2">
              <li><Link to="/" className="text-gray-600 hover:text-blue-600 vietnamese-text">Trang chủ</Link></li>
              <li><Link to="/danh-gia" className="text-gray-600 hover:text-blue-600 vietnamese-text">Đánh giá rủi ro</Link></li>
              <li><Link to="/khoa-hoc" className="text-gray-600 hover:text-blue-600 vietnamese-text">Khóa học</Link></li>
              <li><Link to="/tu-van" className="text-gray-600 hover:text-blue-600 vietnamese-text">Tư vấn</Link></li>
            </ul>
          </div>
          
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800 vietnamese-text">
              Liên hệ
            </h3>
            <div className="space-y-2 text-gray-600 vietnamese-text">
              <p>📞 0337315535</p>
              <p>✉️ chungtay.adm@gmail.com</p>
              <p>📍 7 Đường D1, Long Thạnh Mỹ, TP. Thủ Đức, TP. HCM</p>
            </div>
          </div>
        </div>
        
        <div className="border-t border-glass-border mt-8 pt-8 text-center">
          <p className="text-gray-600 vietnamese-text">
            © 2025 Chung Tay Phòng Ngừa. Dự án học thuật - Không dành cho mục đích thương mại.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
