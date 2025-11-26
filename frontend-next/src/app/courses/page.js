'use client';

import ProtectedRoute from '@/components/auth/ProtectedRoute';
import CourseList from '@/components/learning/CourseList';

export default function CoursesPage() {
    return (
        <ProtectedRoute>
            <CourseList />
        </ProtectedRoute>
    );
}
