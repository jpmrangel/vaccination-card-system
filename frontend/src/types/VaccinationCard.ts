import type { PersonResponse } from './Person';
import type { DoseType, VaccineCategory, DoseStatus } from './Enums';

export interface DoseStatusDTO {
  doseType: DoseType;
  status: DoseStatus;
  recordId: number | null;
  applicationDate: string | null; 
}

export interface VaccineStatusDTO {
  vaccineId: number;
  vaccineName: string;
  category: VaccineCategory;
  doses: DoseStatusDTO[];
}

export interface VaccinationCardGridDTO {
  person: PersonResponse;
  vaccines: VaccineStatusDTO[];
}

export interface VaccinationRecordRequestDTO {
  vaccineId: number,
  applicationDate: string,
  dose: DoseType;
}