'use client';

import ProtectedRoute from '@/components/auth/ProtectedRoute';
import Assessment from '@/components/assessment/Assessment';

export default function AssessmentPage() {
    return (
        <ProtectedRoute>
            <Assessment />
        </ProtectedRoute>
    );
}
