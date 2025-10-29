import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../services/authService';
import type { RegisterRequest } from '../types/Auth';

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleRegister = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);
    setSuccessMessage(null);

    if (password !== confirmPassword) {
      setError('As senhas não coincidem.');
      return;
    }
    if (password.length < 6) {
        setError('A senha deve ter pelo menos 6 caracteres.');
        return;
    }

    setIsLoading(true);

    const userData: RegisterRequest = { username, password };

    try {
      const message = await register(userData);
      setSuccessMessage(`${message} Você será redirecionado para o login.`);
      
      setUsername('');
      setPassword('');
      setConfirmPassword('');
      
      setTimeout(() => {
        navigate('/login');
      }, 3000);

    } catch (err: any) {
      setError(err.message || 'Erro ao registrar. Tente novamente.'); 
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h1>Registrar Novo Usuário</h1>

      {successMessage && <p style={{ color: 'green', marginBottom: '15px' }}>{successMessage}</p>}
      {error && <p style={{ color: 'red', marginBottom: '15px' }}>{error}</p>}

      <form onSubmit={handleRegister}>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="username" style={{ display: 'block', marginBottom: '5px' }}>Usuário:</label>
          <input
            type="text" id="username" value={username} onChange={(e) => setUsername(e.target.value)} required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="password" style={{ display: 'block', marginBottom: '5px' }}>Senha:</label>
          <input
            type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
         <div style={{ marginBottom: '15px' }}>
          <label htmlFor="confirmPassword" style={{ display: 'block', marginBottom: '5px' }}>Confirmar Senha:</label>
          <input
            type="password" id="confirmPassword" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>

        <button type="submit" disabled={isLoading || !!successMessage} style={{ width: '100%', padding: '10px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
          {isLoading ? 'Registrando...' : 'Registrar'}
        </button>
      </form>

      <p style={{ textAlign: 'center', marginTop: '15px' }}>
        Já tem uma conta? <Link to="/login">Faça login</Link>
      </p>
    </div>
  );
};

export default RegisterPage;