import type { Sex } from './Enums';

export interface PersonResponse {
  id: number;
  name: string;
  cpf: string;
  dateOfBirth: string;
  sex: Sex;
}

export interface PersonRequest {
  name: string;
  cpf: string;
  dateOfBirth: string;
  sex: Sex;
}