'use client';

import ProtectedRoute from '@/components/auth/ProtectedRoute';
import ConsultantDetail from '@/components/consultation/ConsultantDetail';

export default function ConsultantDetailPage() {
    return (
        <ProtectedRoute>
            <ConsultantDetail />
        </ProtectedRoute>
    );
}
