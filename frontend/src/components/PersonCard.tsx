import React from 'react';
import type { PersonResponse } from '../types/Person';

interface PersonCardProps {
  person: PersonResponse;
  onClick: () => void;
  onDelete: (personId: number, personName: string) => void;
}

const cardStyle: React.CSSProperties = {
  border: '1px solid #ccc',
  padding: '10px',
  margin: '10px 0',
  cursor: 'pointer',
  borderRadius: '5px',
  position: 'relative',
};

const deleteButtonStyle: React.CSSProperties = {
  position: 'absolute',
  top: '10px',
  right: '10px',
  padding: '3px 8px',
  backgroundColor: '#f44336',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '0.8em',
};

const PersonCard: React.FC<PersonCardProps> = ({ person, onClick, onDelete }) => {

  const calculateAge = (birthDateString: string): number | null => {
    try {
      const birthDate = new Date(birthDateString);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const m = today.getMonth() - birthDate.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age;
    } catch (e) {
        return null;
    }
  };

  const age = calculateAge(person.dateOfBirth);

  const handleDeleteClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    onDelete(person.id, person.name);
  };

  return (
    <div 
      style={cardStyle} 
      onClick={onClick} 
      role="button" 
      tabIndex={0} 
      onKeyDown={(e) => e.key === 'Enter' && onClick()}
    >
      <h3>{person.name}</h3>
      <p>CPF: {person.cpf}</p>
      <p>Sexo: {person.sex}</p>
      <p>Data Nasc.: {person.dateOfBirth}</p>
      {age !== null && <p>Idade: {age}</p>}

      <button 
        style={deleteButtonStyle} 
        onClick={handleDeleteClick}
        aria-label={`Excluir ${person.name}`}
      >
        Excluir
      </button>
    </div>
  );
};

export default PersonCard;