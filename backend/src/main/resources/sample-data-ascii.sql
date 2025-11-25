-- Sample Data for Vietnamese Drug Prevention Platform (Chung Tay Phong Ngua)
-- Author: FullStack-Developer-AI (Cursor)
-- Created: Wed Jan 1 20:00:00 +07 2025
-- Purpose: Enable comprehensive testing of all system workflows
-- Compliance: Document/functional_requirements_state_1.md

-- =============================================
-- 1. ROLES AND BASIC USERS
-- =============================================
SET search_path TO ctpn_core, ctpn_assessment, ctpn_learning, ctpn_consultation, ctpn_content, ctpn_audit, public;
-- Insert roles (matching RBAC requirements)
INSERT INTO ctpn_core.roles (role_name, description) VALUES
('Guest', 'Anonymous users with limited access'),
('Member', 'Registered users with access to assessments and courses'),
('Staff', 'Staff members with content management access'),
('Consultant', 'Volunteer consultants providing counseling services'),
('Manager', 'Managers with oversight and reporting access'),
('Admin', 'System administrators with full access')
ON CONFLICT (role_name) DO NOTHING;
-- Insert sample users with ASCII names
INSERT INTO ctpn_core.users (email, password_hash, full_name, phone_number, date_of_birth, is_verified, is_deleted) VALUES
-- Admin and Staff
('admin@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nguyen Van Quan', '0901234567', '1985-05-15', true, false),
('manager@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Tran Thi Hanh', '0901234568', '1988-08-20', true, false),
('staff@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Le Minh Tuan', '0901234569', '1990-12-03', true, false),

-- Consultants (Volunteer counselors)
('consultant1@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Bac si Pham Thi Lan', '0901234570', '1982-03-22', true, false),
('consultant2@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Thac si Vu Dinh Nam', '0901234571', '1979-11-18', true, false),
('consultant3@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Tien si Hoang Thi Mai', '0901234572', '1975-07-12', true, false),

-- Members (Test users)
('member1@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nguyen Thanh Hai', '0901234573', '1995-01-25', true, false),
('member2@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Tran Thi Huong', '0901234574', '2005-09-14', true, false),
('member3@ctpn.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Le Van Dung', '0901234575', '1992-06-30', true, false),

-- Hard-coded test user account
('testuser@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test User', '0900000000', '2000-01-01', true, false)
ON CONFLICT (email) DO NOTHING; 