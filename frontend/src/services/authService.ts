import api from './api';
import type { LoginRequest, AuthResponse, RegisterRequest } from '../types/Auth';

export const login = async (credentials: LoginRequest): Promise<AuthResponse> => {
  try {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    // Salva o token no localStorage após o login bem-sucedido
    if (response.data.accessToken) {
        localStorage.setItem('authToken', response.data.accessToken);
    }
    return response.data;
  } catch (error) {
    console.error("Erro no login:", error);
    // Remove qualquer token antigo se o login falhar
    localStorage.removeItem('authToken');
    delete api.defaults.headers.common['Authorization'];
    // TODO: Tratar erro específico (ex: 401 Unauthorized - credenciais inválidas)
    throw error;
  }
};

export const register = async (userData: RegisterRequest): Promise<string> => { // A API retorna uma string de sucesso
  try {
    const response = await api.post<string>('/auth/register', userData);
    return response.data; // Ex: "Usuário registrado com sucesso!"
  } catch (error: any) {
    console.error("Erro no registro:", error);
    // Retorna a mensagem de erro do backend (ex: "Username já existe")
    if (axios.isAxiosError(error) && error.response?.data) {
        throw new Error(error.response.data);
    }
    throw error; // Lança outros erros
  }
};

export const logout = () => {
    localStorage.removeItem('authToken');
};

export const getToken = (): string | null => {
    return localStorage.getItem('authToken');
};

import axios from 'axios';