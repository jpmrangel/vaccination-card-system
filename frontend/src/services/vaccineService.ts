import type { VaccineRequest, VaccineResponse } from "../types/Vaccine";
import api from "./api";

export const createVaccine = async (vaccineData: VaccineRequest): Promise<VaccineResponse> => {
  try {
    const response = await api.post<VaccineResponse>('/vaccines', vaccineData);
    return response.data;
  } catch (error) {
    console.error("Erro ao criar vacina:", error);
    throw error;
  }
};