'use client';

import ProtectedRoute from '@/components/auth/ProtectedRoute';
import MyCourses from '@/components/learning/MyCourses';

export default function MyCoursesPage() {
    return (
        <ProtectedRoute>
            <MyCourses />
        </ProtectedRoute>
    );
}
