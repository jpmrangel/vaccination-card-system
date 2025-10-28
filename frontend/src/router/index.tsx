import { createBrowserRouter } from 'react-router-dom';

import LoginPage from '../pages/LoginPage.tsx';
import HomePage from '../pages/HomePage.tsx';
import RegisterPersonPage from '../pages/RegisterPersonPage.tsx';
import SearchPersonPage from '../pages/SearchPersonPage.tsx';
import VaccinationCardPage from '../pages/VaccinationCardPage.tsx';
import MainLayout from '../layouts/MainLayout.tsx'; 
import RegisterVaccinePage from '../pages/RegisterVaccinePage.tsx';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: 'persons',
        element: <SearchPersonPage />,
      },
      {
        path: 'persons/new',
        element: <RegisterPersonPage />,
      },
      {
        path: 'persons/:personId/card',
        element: <VaccinationCardPage />,
      },
      {
        path: 'vaccines/new',
        element: <RegisterVaccinePage />,
      }
    ],
  },
]);

export default router;