import React, { useState } from 'react';
import { SexValues } from '../types/Enums';
import axios from 'axios';
import { createPerson } from '../services/personService';
import { useNavigate } from 'react-router-dom';
import type { PersonRequest } from '../types/Person';

const RegisterPersonPage: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<PersonRequest>({
    name: '',
    cpf: '',
    dateOfBirth: '',
    sex: SexValues.MASCULINO
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);

    if (!formData.name || !formData.cpf || !formData.dateOfBirth) {
      setError('Todos os campos são obrigatórios.');
      setIsLoading(false);
      return;
    }

    try {
      const createdPerson = await createPerson(formData);
      setSuccessMessage(`Pessoa cadastrada com sucesso! ID: ${createdPerson.id}`);
      setFormData({ name: '', cpf: '', dateOfBirth: '', sex: SexValues.MASCULINO });

      setTimeout(() => {
        navigate(`/persons`);
      }, 2000);

    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Erro ao cadastrar pessoa. Tente novamente.');
      }
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h1>Cadastrar Nova Pessoa</h1>

      {isLoading && <p>Salvando...</p>}
      {error && <p style={{ color: 'red' }}>Erro: {error}</p>}
      {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Nome:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="cpf">CPF:</label>
          <input
            type="text"
            id="cpf"
            name="cpf"
            value={formData.cpf}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="dateOfBirth">Data de Nascimento:</label>
          <input
            type="date"
            id="dateOfBirth"
            name="dateOfBirth"
            value={formData.dateOfBirth}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="sex">Sexo:</label>
          <select
            id="sex"
            name="sex"
            value={formData.sex}
            onChange={handleChange}
            required
          >
            {Object.values(SexValues).map((sexValue) => (
              <option key={sexValue} value={sexValue}>
                {sexValue.charAt(0) + sexValue.slice(1).toLowerCase()} 
              </option>
            ))}
          </select>
        </div>

        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Cadastrando...' : 'Cadastrar Pessoa'}
        </button>
      </form>
    </div>
  );
};

export default RegisterPersonPage;