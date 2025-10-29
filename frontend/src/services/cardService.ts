import api from './api';
import type { VaccinationCardGridDTO, VaccinationRecordRequestDTO } from '../types/VaccinationCard';
import type { VaccineCategory } from '../types/Enums';

export const getVaccinationCardGrid = async (
  personId: number,
  category?: VaccineCategory
): Promise<VaccinationCardGridDTO> => {
  try {
    const params = category ? { category } : {};
    const response = await api.get<VaccinationCardGridDTO>(`/persons/${personId}/card`, { params });
    return response.data;
  } catch (error) {
    console.error(`Erro ao buscar cartão para pessoa ${personId}:`, error);
    throw error;
  }
};

export const addVaccination = async (
  personId: number,
  recordData: VaccinationRecordRequestDTO
) : Promise<VaccinationCardGridDTO> => {
  try {
    const response = await api.post<VaccinationCardGridDTO>(`/persons/${personId}/card`, recordData);
    return response.data;
  } catch (error) {
    console.error(`Erro ao adicionar vacinação para pessoa ${personId}:`, error);
    throw error;
  }
};

export const deleteVaccinationRecord = async (
  personId: number,
  recordId: number
) : Promise<void> => {
  try {
    await api.delete<VaccinationCardGridDTO>(`/persons/${personId}/card/records/${recordId}`);
  } catch (error) {
    console.error(`Erro ao deletar registro ${recordId} para pessoa ${personId}:`, error);
    throw error;
  }
};