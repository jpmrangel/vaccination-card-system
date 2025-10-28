import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { DoseTypeValues, VaccineCategoryValues, type DoseType, type VaccineCategory } from "../types/Enums";
import type { VaccineRequest } from "../types/Vaccine";
import { createVaccine } from "../services/vaccineService";
import axios from "axios";

const RegisterVaccinePage: React.FC = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [category, setCategory] = useState<VaccineCategory>(VaccineCategoryValues.CARTEIRA_NACIONAL);
  const [selectedDoses, setSelectedDoses] = useState<DoseType[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleDoseChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const dose = e.target.value as DoseType;
    const isChecked = e.target.checked;

    setSelectedDoses((prevDoses) => {
      if (isChecked) {
        return [...new Set([...prevDoses, dose])];
      } else {
        return prevDoses.filter((d) => d !== dose);
      }
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);

    if (!name || selectedDoses.length === 0) {
      setError("Nome da vacina e pelo menos uma dose são obrigatórios.");
      setIsLoading(false);
      return;
    }

    const vaccineData: VaccineRequest = {
      name,
      category,
      doseSchedule: selectedDoses,
    };

    try {
      const createdVaccine = await createVaccine(vaccineData);
      setSuccessMessage(`Vacina "${createdVaccine.name}" cadastrada com sucesso! Redirecionando...`);

      setName('');
      setCategory(VaccineCategoryValues.CARTEIRA_NACIONAL);
      setSelectedDoses([]);
      
      setTimeout(() => {
        navigate('/');
      }, 2000);

    } catch (err: any) {
      if (axios.isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Erro ao cadastrar vacina. Verifique os dados ou tente novamente.');
      }
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h1>Cadastrar Nova Vacina</h1>

      {isLoading && <p>Salvando...</p>}
      {error && <p style={{ color: 'red' }}>Erro: {error}</p>}
      {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Nome da Vacina:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="category">Categoria:</label>
          <select
            id="category"
            name="category"
            value={category}
            onChange={(e) => setCategory(e.target.value as VaccineCategory)}
            required
          >
            {Object.values(VaccineCategoryValues).map((catValue) => (
              <option key={catValue} value={catValue}>
                 {catValue.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label>Esquema de Doses Aplicáveis:</label>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}> 
            {Object.values(DoseTypeValues).map((doseValue) => (
              <label key={doseValue}>
                <input
                  type="checkbox"
                  value={doseValue}
                  checked={selectedDoses.includes(doseValue)}
                  onChange={handleDoseChange}
                />
                {doseValue.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())}
              </label>
            ))}
          </div>
        </div>

        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Cadastrando...' : 'Cadastrar Vacina'}
        </button>
      </form>
    </div>
  );
};

export default RegisterVaccinePage;