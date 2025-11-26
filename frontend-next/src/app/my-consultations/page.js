'use client';

import ProtectedRoute from '@/components/auth/ProtectedRoute';
import MyConsultations from '@/components/consultation/MyConsultations';

export default function MyConsultationsPage() {
    return (
        <ProtectedRoute>
            <MyConsultations />
        </ProtectedRoute>
    );
}
