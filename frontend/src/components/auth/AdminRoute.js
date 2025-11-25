import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuthContext } from '../../contexts/AuthContext';

const AdminRoute = () => {
  const { isAuthenticated, user, loading } = useAuthContext();

  if (loading) {
    return <div>Loading...</div>; // Or a spinner component
  }

  if (!isAuthenticated) {
    return <Navigate to="/dang-nhap" />;
  }

  // Check if the user has the ADMIN role (case-insensitive check for safety)
  const isAdmin = user && user.role && user.role.toUpperCase() === 'ADMIN';

  return isAdmin ? <Outlet /> : <Navigate to="/" />; // Redirect non-admins to homepage
};

export default AdminRoute;
