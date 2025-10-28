import api from './api';
import type { Page, PageableParams } from '../types/Page';
import type { PersonRequest, PersonResponse } from '../types/Person';

export const getPersons = async (params: PageableParams = {}): Promise<Page<PersonResponse>> => {
  try {
    const response = await api.get<Page<PersonResponse>>('/persons', { params });
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar pessoas:", error);
    throw error; 
  }
};

export const searchPersonByCpf = async (cpf: string): Promise<PersonResponse> => {
  try {
    const response = await api.get<PersonResponse>('/persons/search', {
      params: { cpf } 
    });
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar pessoa por CPF:", error);
    throw error;
  }
};

export const createPerson = async (personData: PersonRequest): Promise<PersonResponse> => {
  try {
    const response = await api.post<PersonResponse>('/persons', personData);
    return response.data;
  } catch (error) {
    console.error("Erro ao criar pessoa:", error);
    throw error;
  }
}

export const deletePerson = async (personId: number): Promise<void> => {
  try {
    await api.delete(`/persons/${personId}`);
  } catch (error) {
    console.error(`Erro ao deletar pessoa ${personId}:`, error);
    throw error;
  }
}