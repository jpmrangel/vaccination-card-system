import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPersons, searchPersonByCpf, deletePerson } from '../services/personService';
import type { Page } from '../types/Page';
import type { PersonResponse } from '../types/Person';
import PersonCard from '../components/PersonCard';

const ITEMS_PER_PAGE = 10;

const SearchPersonPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [personsPage, setPersonsPage] = useState<Page<PersonResponse> | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [isDeleting, setIsDeleting] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fetchPersons = useCallback(async (page: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await getPersons({ page, size: ITEMS_PER_PAGE, sort: 'name,asc' });
      setPersonsPage(data);
      setCurrentPage(page);
    } catch (err) {
      setError('Erro ao carregar lista de pessoas.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPersons(0);
  }, [fetchPersons]);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const handleSearchSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!searchTerm.trim()) {
      fetchPersons(0);
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      const person = await searchPersonByCpf(searchTerm.trim());

      const fakePage: Page<PersonResponse> = {
        content: [person],
        pageable: { pageNumber: 0, pageSize: 1, } as any,
        totalPages: 1,
        totalElements: 1,
        last: true,
        first: true,
        size: 1,
        number: 0,
        sort: {} as any,
        numberOfElements: 1,
        empty: false,
      };
      setPersonsPage(fakePage);
      setCurrentPage(0);
    } catch (err: any) {
      if (axios.isAxiosError(err) && err.response?.status === 404) {
          setError(`Nenhuma pessoa encontrada com o CPF: ${searchTerm.trim()}`);
          setPersonsPage(null);
      } else {
          setError('Erro ao buscar pessoa.');
          console.error(err);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeletePerson = async (personId: number, personName: string) => {
    if (!window.confirm(`Tem certeza que deseja excluir ${personName} e todos os seus registros de vacinação?`)) {
      return;
    }

    setIsDeleting(personId);
    setError(null);

    try {
      await deletePerson(personId);

      alert(`${personName} excluído com sucesso!`);
      fetchPersons(currentPage); 

    } catch (err) {
      setError(`Erro ao excluir ${personName}. Tente novamente.`);
      console.error(err);
    } finally {
      setIsDeleting(null);
    }
  };

  const handleNextPage = () => {
    if (personsPage && !personsPage.last) {
      fetchPersons(currentPage + 1);
    }
  };

  const handlePreviousPage = () => {
    if (personsPage && !personsPage.first) {
      fetchPersons(currentPage - 1);
    }
  };
    
  const handlePersonClick = (personId: number) => {
      navigate(`/persons/${personId}/card`);
  };

  return (
    <div>
      <h1>Buscar Pessoa</h1>

      <form onSubmit={handleSearchSubmit}>
        <input
          type="text"
          placeholder="Buscar por CPF..."
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Buscando...' : 'Buscar'}
        </button>
         <button 
           type="button" 
           onClick={() => { setSearchTerm(''); fetchPersons(0); }}
           disabled={isLoading}
         >
           Limpar Busca
         </button>
      </form>

      {isLoading && !isDeleting && <p>Carregando...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      {!isLoading && !error && personsPage && personsPage.content.length > 0 && (
        <div>
          {personsPage.content.map((person) => (
            <PersonCard 
              key={person.id} 
              person={person} 
              onClick={() => handlePersonClick(person.id)} 
              onDelete={handleDeletePerson}
            />
          ))}
        </div>
      )}

       {!isLoading && !error && (!personsPage || personsPage.empty) && (
           <p>Nenhuma pessoa encontrada.</p>
       )}

      {!isLoading && personsPage && personsPage.totalPages > 1 && (
        <div>
          <button onClick={handlePreviousPage} disabled={personsPage.first}>
            Anterior
          </button>
          <span>
            Página {personsPage.number + 1} de {personsPage.totalPages}
          </span>
          <button onClick={handleNextPage} disabled={personsPage.last}>
            Próxima
          </button>
        </div>
      )}
    </div>
  );
};

export default SearchPersonPage;

import axios from 'axios';