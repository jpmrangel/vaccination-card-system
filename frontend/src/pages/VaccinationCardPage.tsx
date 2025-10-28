import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import Modal from 'react-modal';
import { addVaccination, deleteVaccinationRecord, getVaccinationCardGrid } from '../services/cardService';
import type { VaccinationCardGridDTO, DoseStatusDTO, VaccineStatusDTO, VaccinationRecordRequestDTO } from '../types/VaccinationCard';
import type { DoseType, VaccineCategory } from '../types/Enums';
import { VaccineCategoryValues, DoseStatusValues, DoseTypeValues } from '../types/Enums';
import '../styles/modalStyles.css'
import axios from 'axios';

interface DoseBlockProps {
  dose: DoseStatusDTO;
  vaccine: VaccineStatusDTO;
  personId: number;
  onOpenModal: (type: 'add' | 'view_delete', data: any) => void;
}
const DoseBlock: React.FC<DoseBlockProps> = ({ dose, vaccine, personId, onOpenModal }) => {
  let backgroundColor = '#eee';
  let text = '-';
  let isClickable = false;

  if (dose.status === DoseStatusValues.TAKEN) {
    backgroundColor = 'lightgreen';
    text = `Tomada ${dose.applicationDate}`;
    isClickable = true;
  } else if (dose.status === DoseStatusValues.MISSING) {
    backgroundColor = 'white';
    text = 'Faltosa';
    isClickable = true;
  }

  const style: React.CSSProperties = {
    border: '1px solid #ccc',
    padding: '8px',
    margin: '2px',
    minWidth: '100px',
    textAlign: 'center',
    backgroundColor,
    cursor: isClickable ? 'pointer' : 'default',
    fontSize: '0.8em'
  };

  const handleClick = () => {
    if (!isClickable) return;

    if (dose.status === DoseStatusValues.MISSING) {
      onOpenModal('add', {
        personId,
        vaccineId: vaccine.vaccineId,
        vaccineName: vaccine.vaccineName,
        doseType: dose.doseType
      });      
    } else if (dose.status === DoseStatusValues.TAKEN) {
      onOpenModal('view_delete', {
        personId,
        recordId: dose.recordId,
        vaccineName: vaccine.vaccineName,
        doseType: dose.doseType,
        applicationDate: dose.applicationDate
      });
    }
  };

  return <td style={style} onClick={handleClick}>{text}</td>;
};


const VaccinationCardPage: React.FC = () => {
  const { personId } = useParams<{ personId: string }>();
  const [cardData, setCardData] = useState<VaccinationCardGridDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<VaccineCategory | undefined>(undefined); 

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState<'add' | 'view_delete' | null>(null);
  const [modalData, setModalData] = useState<any>(null);
  const [modalError, setModalError] = useState<string | null>(null);

  const numericId = personId ? parseInt(personId, 10) : NaN;

  const fetchCard = useCallback(async () => {
    if (isNaN(numericId)){
      setError("ID da pessoa inválido.");
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      const data = await getVaccinationCardGrid(numericId, selectedCategory);
      setCardData(data);
    } catch (err) {
      setError('Erro ao carregar o cartão de vacinação.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, [numericId, selectedCategory]); 

  useEffect(() => {
    fetchCard();
  }, [fetchCard])

  const handleOpenModal = (type: 'add' | 'view_delete', data: any) => {
    setModalData(data);
    setModalType(type);
    setIsModalOpen(true);
    setModalError(null);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setModalType(null);
    setModalData(null);
    setModalError(null);
  };

  // funções do modal
  const handleAddVaccinationSubmit = async (applicationDate: string) => {
    if (!modalData || isNaN(numericId)) return;
    setIsLoading(true);
    setModalError(null);

    const requestData: VaccinationRecordRequestDTO = {
      vaccineId: modalData.vaccineId,
      dose: modalData.doseType,
      applicationDate: applicationDate,
    }

    try {
      await addVaccination(numericId, requestData);
      handleCloseModal();
      fetchCard();
    } catch (err: any) {
      if (axios.isAxiosError(err) && err.response?.data?.message) {
        setModalError(err.response.data.message)
      } else {
        setModalError("Erro ao registrar a vacina.");
      }
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }

  const handleDeleteRecord = async () => {
    if (!modalData?.recordId || isNaN(numericId)) return;
    setIsLoading(true);
    setModalError(null);

    try {
      await deleteVaccinationRecord(numericId, modalData.recordId);
      handleCloseModal();
      fetchCard();
    } catch (err) {
      setModalError("Erro ao deletar registro.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }

  // filtro de categoria
  const handleCategoryChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const value = event.target.value;
    setSelectedCategory(value === "TODAS" ? undefined : value as VaccineCategory);
  };

  // renderização
  if (isLoading && !isModalOpen) {
    return <p>Carregando cartão...</p>;
  } 

  if (error) {
    return <p style={{ color: 'red' }}>{error}</p>;
  }

  if (!cardData) {
    return <p>Nenhum dado encontrado para esta pessoa.</p>;
  }

  // ordem das doses para as colunas da tabela
  const doseOrder: DoseType[] = Object.values(DoseTypeValues)
  const vaccinesInOrder = cardData.vaccines || [];

  return (
    <div>
      <h1>Cartão de Vacinação</h1>

      <div>
        <h2>Dados da Pessoa</h2>
        <p>Nome: {cardData.person.name}</p>
        <p>CPF: {cardData.person.cpf}</p>
        <p>Data Nasc.: {cardData.person.dateOfBirth}</p>
        <p>Sexo: {cardData.person.sex}</p>
      </div>

      <div>
        <label htmlFor="category-select">Filtrar por Categoria: </label>
        <select 
          id="category-select" 
          value={selectedCategory === undefined ? "TODAS" : selectedCategory} 
          onChange={handleCategoryChange}
        >
          <option value="TODAS">Todas</option>
          {Object.values(VaccineCategoryValues).map(cat => (
            <option key={cat} value={cat}>{cat.replace('_', ' ')}</option>
          ))}
        </select>
      </div>

      <h2>Vacinas</h2>
      <table style={{ borderCollapse: 'collapse', width: 'auto' }}>
        <thead>
          <tr>
            <th style={{ border: '1px solid black', padding: '8px', minWidth: '150px' }}>Doses</th>

            {vaccinesInOrder.map(vaccine => (
              <th key={vaccine.vaccineId} style={{ border: '1px solid black', padding: '8px', minWidth: '120px' }}>
                {vaccine.vaccineName}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {doseOrder.map(doseType => (
            <tr key={doseType}>
              <td style={{ border: '1px solid black', padding: '8px' }}>
                {doseType.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())}
              </td>

              {vaccinesInOrder.map(vaccineStatus => {
                 const dose = vaccineStatus.doses.find(d => d.doseType === doseType);
                 
                 return dose ? 
                  <DoseBlock 
                    key={`${vaccineStatus.vaccineId}-${doseType}`} 
                    dose={dose} 
                    vaccine={vaccineStatus}
                    personId={numericId}
                    onOpenModal={handleOpenModal}
                  /> 
                  : <td key={`${vaccineStatus.vaccineId}-${doseType}`}>Erro</td>;
              })}
            </tr>
          ))}
        </tbody>
      </table>

      <Modal
        isOpen={isModalOpen}
        onRequestClose={handleCloseModal}
        contentLabel="Detalhes da Vacinação"
        className="modal"
        overlayClassName="overlay"
      >
        {modalType === 'add' && modalData && (
          <div>
            <h2>Registrar Vacina</h2>
            <p>Vacina: {modalData.vaccineName}</p>
            <p>Dose: {modalData.doseType.replace('_', ' ').toLowerCase().replace(/\b\w/g, (l: string) => l.toUpperCase())}</p>
            <form onSubmit={(e) => {
              e.preventDefault();
              const dateInput = (e.target as HTMLFormElement).elements.namedItem('applicationDate') as HTMLInputElement;
              handleAddVaccinationSubmit(dateInput.value);
            }}>
              <label htmlFor="applicationDate">Data de Aplicação:</label>
              <input type="date" id="applicationDate" name="applicationDate" required/>
              {modalError && <p style={{ color: 'red' }}>{modalError}</p>}
              <div className="button-group">
                <button type="submit" disabled={isLoading}>
                  {isLoading ? 'Salvando...' : 'Salvar Registro'}
                </button>
                <button type="button" onClick={handleCloseModal}>Cancelar</button>
              </div>
            </form>
          </div>
        )}

        {modalType === 'view_delete' && modalData && (
          <div>
            <h2>Detalhes da Vacina</h2>
            <p>Vacina: {modalData.vaccineName}</p>
            <p>Dose: {modalData.doseType.replace('_', ' ').toLowerCase().replace(/\b\w/g, (l: string) => l.toUpperCase())}</p>
            <p>Data Aplicação: {modalData.applicationDate}</p>
            {modalError && <p style={{ color: 'red' }}>{modalError}</p>}
            <div className="button-group">
              <button 
                type="button"
                onClick={handleDeleteRecord}
                disabled={isLoading}
                style={{ backgroundColor: 'red', color: 'white' }}
              >
                {isLoading ? 'Excluindo...' : 'Excluir Registro'}
              </button>
              <button type="button" onClick={handleCloseModal}>Fechar</button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default VaccinationCardPage;