import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { getToken } from '../services/authService'; // Função que busca o token

const ProtectedRoute: React.FC = () => {
  const isAuthenticated = !!getToken(); // Verifica se o token existe (simplificado)
  // TODO: No futuro, você pode adicionar uma validação mais robusta do token aqui
  // (ex: verificar expiração no frontend, embora o backend já faça isso)

  if (!isAuthenticated) {
    // Se não está autenticado, redireciona para a página de login
    // 'replace' evita que a página protegida entre no histórico do navegador
    return <Navigate to="/login" replace />;
  }

  // Se está autenticado, renderiza o componente filho da rota (usando Outlet)
  return <Outlet />;
};

export default ProtectedRoute;