-- Sample Data for Vietnamese Drug Prevention Platform (Chung Tay Phòng Ngừa)
-- Author: FullStack-Developer-AI (Cursor)
-- Created: Wed Jan 1 20:00:00 +07 2025
-- Purpose: Enable comprehensive testing of all system workflows
-- Compliance: Document/functional_requirements_state_1.md

-- =============================================
-- 1. ROLES AND BASIC USERS
-- =============================================
SET search_path TO ctpn_core, ctpn_assessment, ctpn_learning, ctpn_consultation, ctpn_content, ctpn_audit, public;
-- Insert roles (matching RBAC requirements)
INSERT INTO roles (role_name, description) VALUES
('Guest', 'Anonymous users with limited access'),
('Member', 'Registered users with access to assessments and courses'),
('Staff', 'Staff members with content management access'),
('Consultant', 'Volunteer consultants providing counseling services'),
('Manager', 'Managers with oversight and reporting access'),
('Admin', 'System administrators with full access')
ON CONFLICT (role_name) DO NOTHING;
-- Insert sample users with Vietnamese names and realistic data
INSERT INTO users (email, password_hash, full_name, phone_number, date_of_birth, is_verified) VALUES
-- Admin and Staff
('admin@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nguyen Van Quan', '0901234567', '1985-05-15', true),
('manager@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Tran Thi Hanh', '0901234568', '1988-08-20', true),
('staff@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Le Minh Tuan', '0901234569', '1990-12-03', true),

-- Consultants (Volunteer counselors)
('consultant1@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Bac si Pham Thi Lan', '0901234570', '1982-03-22', true),
('consultant2@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Thac si Vu Dinh Nam', '0901234571', '1979-11-18', true),
('consultant3@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Tien si Hoang Thi Mai', '0901234572', '1975-07-12', true),

-- Members (Test users)
('member1@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nguyen Thanh Hai', '0901234573', '1995-01-25', true),
('member2@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Tran Thi Huong', '0901234574', '2005-09-14', true),
('member3@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Le Van Dung', '0901234575', '1992-06-30', true)
ON CONFLICT (email) DO NOTHING;

-- Assign user roles
INSERT INTO user_roles (user_id, role_id) VALUES
-- Admin
((SELECT user_id FROM users WHERE email = 'admin@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Admin')),
-- Manager
((SELECT user_id FROM users WHERE email = 'manager@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Manager')),
-- Staff
((SELECT user_id FROM users WHERE email = 'staff@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Staff')),
-- Consultants
((SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Consultant')),
((SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Consultant')),
((SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Consultant')),
-- Members
((SELECT user_id FROM users WHERE email = 'member1@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Member')),
((SELECT user_id FROM users WHERE email = 'member2@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Member')),
((SELECT user_id FROM users WHERE email = 'member3@ctpn.org'), (SELECT role_id FROM roles WHERE role_name = 'Member'))
ON CONFLICT (user_id, role_id) DO NOTHING;

-- =============================================
-- 2. CONSULTANT PROFILES AND AVAILABILITY
-- =============================================

-- Create consultant profiles
INSERT INTO user_profiles (user_id, bio, specializations, certifications, qualifications, experience_years, is_consultant, rating, total_reviews, total_sessions) VALUES
-- Consultant 1: Addiction Counselor
((SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'), 
 'Bác sĩ chuyên khoa tâm thần với 12 năm kinh nghiệm trong điều trị nghiện chất. Tốt nghiệp Đại học Y Hà Nội, có chứng chỉ tư vấn nghiện chất quốc tế.',
 ARRAY['addiction_counseling', 'family_therapy'], 
 '{"degrees": ["MD - Psychiatry", "Certificate in Addiction Counseling"], "licenses": ["Medical License VN-2024"], "training": ["WHO Addiction Treatment Guidelines"]}',
 'Bác sĩ Chuyên khoa I Tâm thần, Chứng chỉ Tư vấn Nghiện chất WHO',
 12, true, 4.8, 156, 234),

-- Consultant 2: Youth Support Specialist  
((SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org'),
 'Thạc sĩ Tâm lý học chuyên về tư vấn trẻ em và thanh thiếu niên. Có 8 năm kinh nghiệm làm việc với các em có nguy cơ sử dụng chất kích thích.',
 ARRAY['youth_support', 'family_therapy'],
 '{"degrees": ["MS - Psychology", "Certificate in Youth Counseling"], "licenses": ["Psychology License VN-2024"], "training": ["CRAFFT Assessment Training", "Family Systems Training"]}',
 'Thạc sĩ Tâm lý học, Chuyên gia Tư vấn Thanh thiếu niên',
 8, true, 4.9, 89, 145),

-- Consultant 3: Family Therapy Expert
((SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'),
 'Tiến sĩ Công tác xã hội với chuyên môn về liệu pháp gia đình và can thiệp cộng đồng. Có 15 năm kinh nghiệm trong lĩnh vực phòng chống tệ nạn xã hội.',
 ARRAY['family_therapy', 'community_intervention'],
 '{"degrees": ["PhD - Social Work", "Certificate in Family Therapy"], "licenses": ["Social Work License VN-2024"], "training": ["Systemic Family Therapy", "Community Intervention Methods"]}',
 'Tiến sĩ Công tác xã hội, Chuyên gia Liệu pháp Gia đình',
 15, true, 4.7, 203, 387)
ON CONFLICT (user_id) DO NOTHING;

-- =============================================
-- 3. ASSESSMENT TYPES (WHO ASSIST & CeASAR CRAFFT)
-- =============================================

-- Insert ASSIST assessment for adults (18+)
INSERT INTO assessment_types (type_name, age_group, description, scoring_method, questions_json) VALUES
('ASSIST', 'adult', 'WHO Alcohol, Smoking and Substance Involvement Screening Test - Vietnamese version', 'frequency_scoring',
'{"instructions": "Hãy trả lời thành thật về việc sử dụng các chất kích thích trong 3 tháng qua. Thông tin của bạn sẽ được bảo mật.", "scoring": {"low": {"min": 0, "max": 10, "description": "Nguy cơ thấp"}, "moderate": {"min": 11, "max": 26, "description": "Nguy cơ trung bình"}, "high": {"min": 27, "max": 100, "description": "Nguy cơ cao"}}, "options": {"frequency": ["Không bao giờ", "Một hoặc hai lần", "Hàng tháng", "Hàng tuần", "Hàng ngày"]}, "questions": [{"id": "q1", "text": "Trong 3 tháng qua, bạn có sử dụng rượu bia không?", "type": "frequency", "category": "alcohol", "weight": 2}, {"id": "q2", "text": "Trong 3 tháng qua, bạn có sử dụng thuốc lá không?", "type": "frequency", "category": "tobacco", "weight": 2}, {"id": "q3", "text": "Trong 3 tháng qua, bạn có sử dụng ma túy không?", "type": "frequency", "category": "drugs", "weight": 3}, {"id": "q4", "text": "Bạn có cảm thấy cần phải sử dụng nhiều hơn để có cùng cảm giác?", "type": "frequency", "category": "tolerance", "weight": 4}, {"id": "q5", "text": "Bạn có muốn cắt bỏ hoặc giảm việc sử dụng chất kích thích?", "type": "frequency", "category": "control", "weight": 3}]}'),

-- Insert CRAFFT assessment for youth (<18)
('CRAFFT', 'youth', 'CeASAR CRAFFT Screening Tool - Vietnamese version for adolescents', 'binary_scoring',
'{"instructions": "Hãy trả lời có/không cho các câu hỏi sau đây. Thông tin này sẽ giúp chúng tôi đưa ra lời khuyên phù hợp.", "scoring": {"low": {"min": 0, "max": 1, "description": "Nguy cơ thấp"}, "high": {"min": 2, "max": 6, "description": "Nguy cơ cao"}}, "options": {"yesno": ["Không", "Có"]}, "questions": [{"id": "q1", "text": "Bạn có bao giờ lái xe khi đã sử dụng chất kích thích?", "type": "yesno", "category": "car", "weight": 1}, {"id": "q2", "text": "Bạn có sử dụng chất kích thích để thư giãn hay tự tin hơn?", "type": "yesno", "category": "relax", "weight": 1}, {"id": "q3", "text": "Bạn có sử dụng chất kích thích khi một mình?", "type": "yesno", "category": "alone", "weight": 1}, {"id": "q4", "text": "Bạn có quên những gì đã làm khi sử dụng chất kích thích?", "type": "yesno", "category": "forget", "weight": 1}, {"id": "q5", "text": "Gia đình hay bạn bè có nói bạn nên giảm sử dụng chất kích thích?", "type": "yesno", "category": "family", "weight": 1}, {"id": "q6", "text": "Bạn có gặp rắc rối khi sử dụng chất kích thích?", "type": "yesno", "category": "trouble", "weight": 1}]}')
ON CONFLICT (type_name) DO NOTHING;

-- =============================================
-- 4. SAMPLE COURSES (FR-009, FR-010, FR-011)
-- =============================================

-- Insert sample courses
INSERT INTO courses (title, description, instructor_id, category, level, duration_hours, max_students, rating, total_reviews, price) VALUES
-- Course 1: Basic Prevention
('Hiểu biết cơ bản về phòng chống tệ nạn xã hội', 
 'Khóa học cung cấp kiến thức cơ bản về tác hại của ma túy, rượu bia và các chất gây nghiện. Phù hợp cho mọi lứa tuổi, đặc biệt là thanh thiếu niên và phụ huynh.',
 (SELECT user_id FROM users WHERE email = 'staff@ctpn.org'),
 'Giáo dục phòng ngừa', 'beginner', 4, 50, 4.5, 123, 0.00),

-- Course 2: Family Support
('Hỗ trợ gia đình có người nghiện chất',
 'Khóa học hướng dẫn các thành viên gia đình cách nhận biết, ứng phó và hỗ trợ người thân có vấn đề với chất gây nghiện. Bao gồm kỹ năng giao tiếp và chăm sóc tâm lý.',
 (SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'),
 'Hỗ trợ gia đình', 'intermediate', 8, 30, 4.7, 89, 0.00),

-- Course 3: Youth Prevention
('Kỹ năng sống và phòng chống tệ nạn cho thanh thiếu niên',
 'Chương trình giáo dục kỹ năng sống, nâng cao nhận thức về tác hại của ma túy và rượu bia. Phát triển khả năng từ chối và ra quyết định đúng đắn.',
 (SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org'), 
 'Giáo dục thanh thiếu niên', 'beginner', 6, 40, 4.8, 156, 0.00),

-- Course 4: Community Intervention
('Can thiệp cộng đồng trong phòng chống tệ nạn xã hội',
 'Khóa học nâng cao dành cho các nhà hoạt động xã hội, giáo viên, và cán bộ địa phương. Hướng dẫn cách xây dựng chương trình phòng chống tệ nạn tại cộng đồng.',
 (SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'),
 'Can thiệp cộng đồng', 'advanced', 12, 25, 4.6, 67, 0.00),

-- Course 5: Recovery Support
('Hỗ trợ phục hồi và tái hòa nhập xã hội',
 'Chương trình hỗ trợ người đã từng sử dụng chất gây nghiện trong quá trình phục hồi và tái hòa nhập xã hội. Bao gồm liệu pháp nhóm và kỹ năng sống.',
 (SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'),
 'Phục hồi và tái hòa nhập', 'intermediate', 10, 20, 4.9, 45, 0.00)
ON CONFLICT DO NOTHING;

-- =============================================
-- 5. COURSE LESSONS AND QUIZZES
-- =============================================

-- Insert lessons for Course 1 (Basic Prevention)
INSERT INTO lessons (course_id, title, content, duration, order_index) VALUES
((SELECT course_id FROM courses WHERE title LIKE 'Hiểu biết cơ bản%'), 
 'Bài 1: Tổng quan về tệ nạn xã hội', 
 'Giới thiệu về các loại chất gây nghiện phổ biến tại Việt Nam, thống kê và tác động đến xã hội. Nội dung bao gồm: định nghĩa ma túy, phân loại các chất gây nghiện, tình hình sử dụng chất gây nghiện ở Việt Nam.',
 45, 1),

((SELECT course_id FROM courses WHERE title LIKE 'Hiểu biết cơ bản%'),
 'Bài 2: Tác hại của ma túy đối với sức khỏe',
 'Phân tích chi tiết các tác hại của ma túy đối với sức khỏe thể chất và tinh thần. Bao gồm: tác hại ngắn hạn và dài hạn, ảnh hưởng đến các cơ quan trong cơ thể, hậu quả tâm lý và xã hội.',
 50, 2),

((SELECT course_id FROM courses WHERE title LIKE 'Hiểu biết cơ bản%'),
 'Bài 3: Nhận biết dấu hiệu sử dụng chất gây nghiện',
 'Hướng dẫn nhận biết các dấu hiệu cho thấy một người đang sử dụng chất gây nghiện. Kỹ năng quan sát và đánh giá, cách tiếp cận và hỗ trợ.',
 40, 3),

((SELECT course_id FROM courses WHERE title LIKE 'Hiểu biết cơ bản%'),
 'Bài 4: Phương pháp phòng ngừa hiệu quả',
 'Các biện pháp phòng ngừa tại gia đình, trường học và cộng đồng. Xây dựng môi trường sống lành mạnh, giáo dục ý thức và kỹ năng sống.',
 45, 4)
ON CONFLICT DO NOTHING;

-- Insert sample quiz for Course 1
INSERT INTO quizzes (course_id, title, questions_json, time_limit, max_attempts, passing_score) VALUES
((SELECT course_id FROM courses WHERE title LIKE 'Hiểu biết cơ bản%'),
 'Kiểm tra hiểu biết cơ bản',
 '{"questions": [{"id": "q1", "question": "Theo thống kê, độ tuổi bắt đầu sử dụng ma túy phổ biến nhất ở Việt Nam là?", "type": "multiple_choice", "options": ["10-13 tuổi", "14-18 tuổi", "19-25 tuổi", "26-30 tuổi"], "correct": 1, "explanation": "Theo nghiên cứu, đa số người sử dụng ma túy bắt đầu từ độ tuổi 14-18."}, {"id": "q2", "question": "Dấu hiệu nào sau đây KHÔNG phải là biểu hiện của việc sử dụng ma túy?", "type": "multiple_choice", "options": ["Thay đổi đột ngột về hành vi", "Giảm cân nhanh chóng", "Tăng khả năng tập trung", "Thường xuyên mệt mỏi"], "correct": 2, "explanation": "Sử dụng ma túy thường làm giảm khả năng tập trung, không tăng."}, {"id": "q3", "question": "Phương pháp phòng ngừa hiệu quả nhất là gì?", "type": "multiple_choice", "options": ["Cấm đoán nghiêm khắc", "Giáo dục và nâng cao nhận thức", "Trừng phạt nặng", "Cách ly hoàn toàn"], "correct": 1, "explanation": "Giáo dục và nâng cao nhận thức là phương pháp phòng ngừa bền vững và hiệu quả nhất."}]}',
 30, 3, 70.00)
ON CONFLICT DO NOTHING;

-- =============================================
-- 6. SAMPLE ENROLLMENTS AND PROGRESS
-- =============================================

-- Enroll sample members in courses
INSERT INTO enrollments (user_id, course_id, progress, final_score, status) VALUES
-- Member 1 enrollments
((SELECT user_id FROM users WHERE email = 'member1@ctpn.org'), 
 (SELECT course_id FROM courses WHERE title LIKE 'Hiểu biết cơ bản%'), 
 75, 85.5, 'active'),
 
((SELECT user_id FROM users WHERE email = 'member1@ctpn.org'),
 (SELECT course_id FROM courses WHERE title LIKE 'Hỗ trợ gia đình%'),
 100, 92.0, 'completed'),

-- Member 2 enrollments  
((SELECT user_id FROM users WHERE email = 'member2@ctpn.org'),
 (SELECT course_id FROM courses WHERE title LIKE 'Kỹ năng sống%'),
 60, NULL, 'active'),

-- Member 3 enrollments
((SELECT user_id FROM users WHERE email = 'member3@ctpn.org'),
 (SELECT course_id FROM courses WHERE title LIKE 'Hiểu biết cơ bản%'),
 100, 78.5, 'completed')
ON CONFLICT (user_id, course_id) DO NOTHING;

-- =============================================
-- 7. CONSULTANT AVAILABILITY
-- =============================================

-- Set up availability for consultants
INSERT INTO consultant_availability (user_id, day_of_week, start_time, end_time, is_available) VALUES
-- Consultant 1: Available Monday, Wednesday, Friday
((SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'), 1, '09:00:00', '17:00:00', true),
((SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'), 3, '09:00:00', '17:00:00', true), 
((SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'), 5, '09:00:00', '17:00:00', true),

-- Consultant 2: Available Tuesday, Thursday, Saturday
((SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org'), 2, '10:00:00', '18:00:00', true),
((SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org'), 4, '10:00:00', '18:00:00', true),
((SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org'), 6, '08:00:00', '16:00:00', true),

-- Consultant 3: Available Monday through Friday  
((SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'), 1, '08:00:00', '16:00:00', true),
((SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'), 2, '08:00:00', '16:00:00', true),
((SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'), 3, '08:00:00', '16:00:00', true),
((SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'), 4, '08:00:00', '16:00:00', true),
((SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'), 5, '08:00:00', '16:00:00', true)
ON CONFLICT DO NOTHING;

-- =============================================
-- 8. SAMPLE CONSULTATIONS
-- =============================================

-- Insert sample consultations
INSERT INTO consultations (member_id, consultant_id, scheduled_at, status, notes, rating, feedback) VALUES
-- Completed consultations
((SELECT user_id FROM users WHERE email = 'member1@ctpn.org'),
 (SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'),
 '2025-01-01 14:00:00', 'completed',
 'Tư vấn về cách hỗ trợ con trai có dấu hiệu sử dụng chất kích thích',
 5, 'Bác sĩ tư vấn rất chi tiết và hữu ích. Cảm ơn rất nhiều!'),

((SELECT user_id FROM users WHERE email = 'member3@ctpn.org'),
 (SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org'),
 '2025-01-01 16:00:00', 'completed',
 'Tư vấn kỹ năng từ chối và tự bảo vệ bản thân',
 4, 'Rất bổ ích, giúp em hiểu rõ hơn về cách bảo vệ mình.'),

-- Upcoming consultations
((SELECT user_id FROM users WHERE email = 'member2@ctpn.org'),
 (SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org'),
 '2025-01-03 10:00:00', 'confirmed',
 'Tư vấn về áp lực học tập và nguy cơ sử dụng chất kích thích',
 NULL, NULL)
ON CONFLICT DO NOTHING;

-- =============================================
-- 9. BLOG POSTS AND CONTENT
-- =============================================

-- Insert content categories
INSERT INTO categories (name, description) VALUES
('Giáo dục phòng ngừa', 'Các bài viết giáo dục về phòng chống tệ nạn xã hội'),
('Hỗ trợ gia đình', 'Hướng dẫn và hỗ trợ các gia đình có người nghiện chất'),
('Nghiên cứu khoa học', 'Các nghiên cứu và báo cáo khoa học về phòng chống tệ nạn'),
('Hoạt động cộng đồng', 'Tin tức về các hoạt động phòng chống tệ nạn tại cộng đồng'),
('Câu chuyện thành công', 'Chia sẻ từ những người đã vượt qua nghiện chất')
ON CONFLICT (name) DO NOTHING;

-- Insert sample blog posts  
INSERT INTO blog_posts (title, content, author_id, category_id, status, published_at, view_count) VALUES
('5 dấu hiệu nhận biết con em có nguy cơ sử dụng chất gây nghiện',
 'Là cha mẹ, việc nhận biết sớm các dấu hiệu cho thấy con em có nguy cơ tiếp xúc với chất gây nghiện là vô cùng quan trọng. Dưới đây là 5 dấu hiệu cần lưu ý:

1. **Thay đổi đột ngột về hành vi và tính cách**: Con em trở nên bí mật, tránh giao tiếp với gia đình, thường xuyên nói dối về nơi đi, người bạn.

2. **Thay đổi về học tập và công việc**: Điểm số giảm sút đột ngột, thường xuyên vắng mặt, mất hứng thú với các hoạt động từng yêu thích.

3. **Thay đổi về ngoại hình**: Giảm cân hoặc tăng cân đột ngột, mắt đỏ, mùi lạ trên người, không chú ý đến vệ sinh cá nhân.

4. **Thay đổi về bạn bè và môi trường**: Kết giao với nhóm bạn mới, tránh giới thiệu bạn bè với gia đình, thường xuyên về nhà muộn.

5. **Thay đổi về tài chính**: Thường xuyên xin tiền với lý do không rõ ràng, mất tiền hoặc đồ vật có giá trị trong nhà.

Nếu phát hiện con em có các dấu hiệu trên, cha mẹ nên bình tĩnh, tìm hiểu kỹ hơn và tìm kiếm sự hỗ trợ từ các chuyên gia.',
 (SELECT user_id FROM users WHERE email = 'staff@ctpn.org'),
 (SELECT category_id FROM categories WHERE name = 'Giáo dục phòng ngừa'),
 'published', '2025-01-01 10:00:00', 1250),

('Cách xây dựng môi trường gia đình tích cực phòng chống tệ nạn',
 'Gia đình là tuyến phòng thủ đầu tiên và quan trọng nhất trong việc bảo vệ con em khỏi tệ nạn xã hội. Dưới đây là những gợi ý để xây dựng một môi trường gia đình tích cực:

**1. Tạo không gian giao tiếp mở**
- Dành thời gian trò chuyện cùng con em mỗi ngày
- Lắng nghe không phán xét, tạo cảm giác an toàn cho con chia sẻ
- Thảo luận về những vấn đề con em quan tâm

**2. Thiết lập ranh giới rõ ràng nhưng hợp lý**
- Đặt ra các quy tắc gia đình phù hợp với tuổi tác
- Giải thích lý do của các quy tắc
- Nhất quán trong việc thực thi

**3. Làm gương tích cực**
- Cha mẹ là tấm gương đầu tiên của con em
- Thể hiện cách giải quyết stress và khó khăn một cách tích cực
- Tránh sử dụng rượu bia quá mức trước mặt con em

**4. Khuyến khích các hoạt động tích cực**
- Hỗ trợ con tham gia các hoạt động thể thao, nghệ thuật
- Tạo cơ hội cho con phát triển sở thích và tài năng
- Khen ngợi và động viên những nỗ lực tích cực

**5. Giáo dục về tác hại của chất gây nghiện**
- Cung cấp thông tin chính xác, phù hợp với độ tuổi
- Sử dụng các tình huống cụ thể để giáo dục
- Khuyến khích con đặt câu hỏi và thảo luận',
 (SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org'),
 (SELECT category_id FROM categories WHERE name = 'Hỗ trợ gia đình'),
 'published', '2025-01-01 14:30:00', 890),

('Câu chuyện của Minh: Từ bờ vực thẳm trở về với cuộc sống',
 'Minh (tên đã thay đổi), 28 tuổi, đã từng là một trong những trường hợp nghiêm trọng mà trung tâm chúng tôi tiếp nhận. Hôm nay, sau 3 năm hồi phục, Minh đã sẵn sàng chia sẻ câu chuyện của mình để động viên những người đang trong hoàn cảnh tương tự.

**Khởi đầu của nghiện chất**

"Tôi bắt đầu sử dụng ma túy từ năm 19 tuổi, khi đang học đại học. Lúc đầu chỉ là tò mò, muốn thử nghiệm cùng bạn bè. Không ai trong chúng tôi nghĩ rằng chỉ vài lần thử nghiệm sẽ dẫn đến hậu quả nghiêm trọng như vậy."

**Những năm tháng tăm tối**

Trong 6 năm tiếp theo, cuộc sống của Minh hoàn toàn đảo lộn. Bỏ học, mất việc, xa cách gia đình. "Tôi đã mất hết tất cả - bằng cấp, công việc, tình yêu, và quan trọng nhất là niềm tin của gia đình."

**Bước ngoặt**

Điểm chuyển mình xảy ra khi Minh tham gia chương trình hỗ trợ của chúng tôi. "Lần đầu tiên sau nhiều năm, tôi cảm thấy có người thật sự hiểu và không phán xét tôi. Các chuyên gia tại đây không chỉ giúp tôi cai nghiện mà còn dạy tôi cách yêu thương bản thân."

**Hành trình hồi phục**

Quá trình hồi phục không hề dễ dàng. "Có những lúc tôi muốn từ bỏ, nhưng sự hỗ trợ từ gia đình, bạn bè và đội ngũ chuyên gia đã giúp tôi vượt qua."

**Cuộc sống hiện tại**

Hiện tại, Minh đang làm việc ổn định, có gia đình hạnh phúc và tích cực tham gia các hoạt động hỗ trợ cộng đồng. "Tôi muốn gửi gắm đến những người đang trong hoàn cảnh như tôi ngày xưa: Đừng bao giờ từ bỏ hy vọng. Sự thay đổi là có thể, và cuộc sống tươi đẹp đang chờ đợi các bạn."',
 (SELECT user_id FROM users WHERE email = 'manager@ctpn.org'),
 (SELECT category_id FROM categories WHERE name = 'Câu chuyện thành công'),
 'published', '2025-01-01 16:45:00', 2340)
ON CONFLICT DO NOTHING;

-- =============================================
-- 10. SAMPLE ASSESSMENTS AND RESULTS
-- =============================================

-- Insert sample assessments
INSERT INTO assessments (user_id, type_id, completed_at, total_score, risk_level, recommendations) VALUES
-- Member 1: ASSIST assessment - Moderate risk
((SELECT user_id FROM users WHERE email = 'member1@ctpn.org'),
 (SELECT type_id FROM assessment_types WHERE type_name = 'ASSIST'),
 '2025-01-01 11:30:00', 18, 'moderate',
 'Kết quả cho thấy bạn có nguy cơ trung bình. Khuyến nghị: Tham gia khóa học "Hỗ trợ gia đình có người nghiện chất" và đặt lịch tư vấn với chuyên gia.'),

-- Member 2: CRAFFT assessment - Low risk (youth)  
((SELECT user_id FROM users WHERE email = 'member2@ctpn.org'),
 (SELECT type_id FROM assessment_types WHERE type_name = 'CRAFFT'),
 '2025-01-01 13:15:00', 1, 'low',
 'Kết quả tốt! Bạn có nguy cơ thấp. Khuyến nghị: Tiếp tục duy trì lối sống lành mạnh và tham gia khóa học "Kỹ năng sống và phòng chống tệ nạn cho thanh thiếu niên".'),

-- Member 3: ASSIST assessment - Low risk
((SELECT user_id FROM users WHERE email = 'member3@ctpn.org'),
 (SELECT type_id FROM assessment_types WHERE type_name = 'ASSIST'),
 '2025-01-01 15:45:00', 5, 'low',
 'Kết quả tốt! Bạn có nguy cơ thấp. Khuyến nghị: Tham gia các hoạt động giáo dục phòng ngừa để củng cố kiến thức.')
ON CONFLICT DO NOTHING;

-- =============================================
-- 11. SAMPLE SURVEYS FOR ANONYMOUS ACCESS
-- =============================================

-- Insert additional public surveys
INSERT INTO surveys (title, description, questions_json, is_anonymous, is_active, response_count) VALUES
('Khảo sát nhận thức về tác hại của ma túy trong thanh thiếu niên',
 'Khảo sát nhằm đánh giá mức độ nhận thức của thanh thiếu niên về tác hại của các chất gây nghiện',
 '{"questions": [{"id": "q1", "question": "Bạn thuộc độ tuổi nào?", "type": "single_choice", "options": ["13-15", "16-18", "19-22", "Trên 22"], "required": true}, {"id": "q2", "question": "Bạn có biết về tác hại của ma túy không?", "type": "single_choice", "options": ["Rất rõ", "Biết một phần", "Biết ít", "Không biết"], "required": true}, {"id": "q3", "question": "Nguồn thông tin chính về tác hại của ma túy bạn nhận được từ đâu?", "type": "multiple_choice", "options": ["Gia đình", "Trường học", "Bạn bè", "Internet", "TV/Báo chí", "Tổ chức xã hội"], "required": true}, {"id": "q4", "question": "Bạn có từng bị ai rủ rê sử dụng chất gây nghiện không?", "type": "single_choice", "options": ["Có", "Không", "Không muốn trả lời"], "required": false}]}',
 true, true, 423),

('Khảo sát về môi trường phòng chống tệ nạn xã hội tại cộng đồng',
 'Đánh giá hiệu quả các biện pháp phòng chống tệ nạn xã hội tại địa phương',
 '{"questions": [{"id": "q1", "question": "Bạn đánh giá như thế nào về tình hình tệ nạn xã hội tại địa phương?", "type": "single_choice", "options": ["Rất nghiêm trọng", "Nghiêm trọng", "Bình thường", "Ít nghiêm trọng", "Không nghiêm trọng"], "required": true}, {"id": "q2", "question": "Các hoạt động phòng chống tệ nạn tại địa phương có hiệu quả không?", "type": "single_choice", "options": ["Rất hiệu quả", "Hiệu quả", "Bình thường", "Ít hiệu quả", "Không hiệu quả"], "required": true}, {"id": "q3", "question": "Bạn có muốn tham gia các hoạt động phòng chống tệ nạn xã hội không?", "type": "single_choice", "options": ["Rất muốn", "Muốn", "Có thể", "Không muốn", "Hoàn toàn không"], "required": true}]}',
 true, true, 287)
ON CONFLICT DO NOTHING;

-- =============================================
-- 12. UPDATE STATISTICS AND FINALIZE
-- =============================================

-- Update user profile statistics based on sample data
UPDATE user_profiles SET 
    total_sessions = (SELECT COUNT(*) FROM consultations WHERE consultant_id = user_profiles.user_id AND status = 'completed'),
    rating = CASE 
        WHEN user_profiles.user_id = (SELECT user_id FROM users WHERE email = 'consultant1@ctpn.org') THEN 4.8
        WHEN user_profiles.user_id = (SELECT user_id FROM users WHERE email = 'consultant2@ctpn.org') THEN 4.9  
        WHEN user_profiles.user_id = (SELECT user_id FROM users WHERE email = 'consultant3@ctpn.org') THEN 4.7
        ELSE rating
    END
WHERE is_consultant = true;

-- Add indexes for performance (if not exist)
CREATE INDEX IF NOT EXISTS idx_sample_consultations_member ON consultations(member_id);
CREATE INDEX IF NOT EXISTS idx_sample_consultations_consultant ON consultations(consultant_id);
CREATE INDEX IF NOT EXISTS idx_sample_enrollments_progress ON enrollments(progress, status);
CREATE INDEX IF NOT EXISTS idx_sample_blog_posts_category ON blog_posts(category_id, status);

-- Sample data insertion completed
-- Total users: 9 (1 Admin, 1 Manager, 1 Staff, 3 Consultants, 3 Members)
-- Total courses: 5 with lessons and quizzes
-- Total consultations: 3 (2 completed, 1 upcoming)
-- Total assessments: 3 (covering both ASSIST and CRAFFT)
-- Total blog posts: 3 with Vietnamese content
-- Total surveys: 5 (3 existing + 2 new) 