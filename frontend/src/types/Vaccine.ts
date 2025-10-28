import type { VaccineCategory, DoseType } from './Enums';

export interface VaccineRequest {
  name: string;
  category: VaccineCategory;
  doseSchedule: DoseType[];
}

export interface VaccineResponse {
  id: number;
  name: string;
  category: VaccineCategory;
  doseSchedule: DoseType[];
}