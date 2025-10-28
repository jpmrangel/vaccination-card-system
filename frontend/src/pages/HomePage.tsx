import React from 'react';
import { Link } from 'react-router-dom';

const HomePage: React.FC = () => {
  return (
    <div>
      <h1>Página Inicial</h1>
      <nav>
        <ul>
          <li>
            <Link to="/persons/new">Cadastrar Pessoa</Link>
          </li>
          <li>
            <Link to="/persons">Buscar Pessoa</Link>
          </li>
          <li>
            <Link to="/vaccines/new">Cadastrar Vacina</Link>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default HomePage;