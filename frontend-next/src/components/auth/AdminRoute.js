'use client';
import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthContext } from '../../contexts/AuthContext';

const AdminRoute = ({ children }) => {
  const { isAuthenticated, user, loading } = useAuthContext();
  const router = useRouter();

  useEffect(() => {
    if (!loading) {
      if (!isAuthenticated) {
        router.push('/dang-nhap');
      } else if (user && (!user.role || user.role.toUpperCase() !== 'ADMIN')) {
        router.push('/');
      }
    }
  }, [loading, isAuthenticated, user, router]);

  if (loading) {
    return <div>Loading...</div>;
  }

  const isAdmin = user && user.role && user.role.toUpperCase() === 'ADMIN';

  if (!isAuthenticated || !isAdmin) {
    return null;
  }

  return children;
};

export default AdminRoute;
