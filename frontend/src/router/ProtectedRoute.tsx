import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { getToken } from '../services/authService';

const ProtectedRoute: React.FC = () => {
  const isAuthenticated = !!getToken();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

export default ProtectedRoute;