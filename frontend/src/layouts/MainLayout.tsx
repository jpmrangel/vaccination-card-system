import React from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { logout } from '../services/authService';

const MainLayout: React.FC = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="main-layout">
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 20px', backgroundColor: '#f0f0f0', borderBottom: '1px solid #ccc' }}>
        <p style={{ margin: 0, fontWeight: 'bold' }}>Meu App de Vacinação</p>
        <button onClick={handleLogout} style={{ padding: '5px 10px' }}>
          Logout
        </button>
      </header>
      <main style={{ padding: '20px' }}>
        <Outlet /> 
      </main>
      <footer>
      </footer>
    </div>
  );
};

export default MainLayout;