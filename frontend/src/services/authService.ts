import api from './api';
import type { LoginRequest, AuthResponse, RegisterRequest } from '../types/Auth';

export const login = async (credentials: LoginRequest): Promise<AuthResponse> => {
  try {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    if (response.data.accessToken) {
        localStorage.setItem('authToken', response.data.accessToken);
    }
    return response.data;
  } catch (error) {
    console.error("Erro no login:", error);
    localStorage.removeItem('authToken');
    delete api.defaults.headers.common['Authorization'];
    throw error;
  }
};

export const register = async (userData: RegisterRequest): Promise<string> => {
  try {
    const response = await api.post<string>('/auth/register', userData);
    return response.data;
  } catch (error: any) {
    console.error("Erro no registro:", error);
    if (axios.isAxiosError(error) && error.response?.data) {
        throw new Error(error.response.data);
    }
    throw error;
  }
};

export const logout = () => {
    localStorage.removeItem('authToken');
};

export const getToken = (): string | null => {
    return localStorage.getItem('authToken');
};

import axios from 'axios';