import './globals.css';
import Header from '@/components/common/Header';
import Footer from '@/components/common/Footer';
import { Providers } from '@/components/Providers';

export const metadata = {
  title: 'Chung Tay Phòng Ngừa',
  description: 'Nền tảng hỗ trợ phòng ngừa tệ nạn xã hội',
};

export default function RootLayout({ children }) {
  return (
    <html lang="vi">
      <body className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100">
        <Providers>
          <Header />
          <main className="container mx-auto px-4 py-8 min-h-screen">
            {children}
          </main>
          <Footer />
        </Providers>
      </body>
    </html>
  );
}
