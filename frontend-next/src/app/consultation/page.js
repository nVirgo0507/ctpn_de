'use client';

import ProtectedRoute from '@/components/auth/ProtectedRoute';
import ConsultantList from '@/components/consultation/ConsultantList';

export default function ConsultationPage() {
    return (
        <ProtectedRoute>
            <ConsultantList />
        </ProtectedRoute>
    );
}
