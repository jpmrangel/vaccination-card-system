import React from 'react';
import { Outlet } from 'react-router-dom';

const MainLayout: React.FC = () => {
  return (
    <div className="main-layout">
      <header>
        <p>Meu App de Vacinação</p>
      </header>
      <main>
        <Outlet /> 
      </main>
      <footer>
      </footer>
    </div>
  );
};

export default MainLayout;