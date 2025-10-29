import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    // Get token dynamically *before* sending the request
    // We need to import getToken *inside* the interceptor function 
    // or ensure AuthService is fully loaded. A simple way is to re-import locally if needed,
    // or rely on the module system having resolved it by the time a request is made.
    // Let's try relying on the initial load first.
    const token = localStorage.getItem('authToken'); // Directly access localStorage here is safer from circular dependencies
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;