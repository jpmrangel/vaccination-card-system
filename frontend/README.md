# 💉 Vaccination Card System - Frontend

React + TypeScript web application for managing and viewing digital vaccination cards.

This is the **frontend** service for the `vaccination-card-system`, providing a user-friendly interface for authentication, person management, and vaccination record tracking.


## 🚀 Features

- **User Authentication:** Secure login and registration with JWT tokens
- **Person Management:** Register and search for persons in the system
- **Vaccination Cards:** View and manage vaccination records with an intuitive grid interface
- **Vaccination Records:** Add and delete vaccination doses with validation
- **Protected Routes:** Role-based access control for authenticated users
- **Responsive Design:** Mobile-friendly interface using modern CSS


## 🛠️ Tech Stack

- **React 19** - UI library
- **TypeScript** - Type-safe JavaScript
- **Vite** - Fast build tool and dev server
- **Axios** - HTTP client for API communication
- **React Router** - Client-side routing
- **CSS Modules** - Scoped styling


## 📋 Prerequisites

- **Node.js** (v16 or higher)
- **npm** or **yarn** package manager
- **Backend API** running on `http://localhost:8080`


## ⚙️ Setup and Installation

### 1. Clone and Navigate

```bash
git clone https://github.com/your-username/vaccination-card-system.git
cd vaccination-card-system/frontend
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Environment Configuration

Create a `.env.local` file (or `.env` for Vite) in the frontend root:

```
VITE_API_URL=http://localhost:8080
```

This tells the frontend where the backend API is running.

### 4. Start Development Server

```bash
npm run dev
```

The application will be available at:
👉 **http://localhost:5173**


## 📁 Project Structure

```
src/
├── components/          # Reusable UI components
│   └── PersonCard.tsx   # Card component for displaying person info
│
├── pages/               # Page components (full screens)
│   ├── HomePage.tsx           # Dashboard/home page
│   ├── LoginPage.tsx          # User login
│   ├── RegisterPage.tsx       # User registration
│   ├── RegisterPersonPage.tsx # Register a new person
│   ├── RegisterVaccinePage.tsx # Register a vaccination dose
│   ├── SearchPersonPage.tsx   # Search for a person
│   └── VaccinationCardPage.tsx # View vaccination card grid
│
├── layouts/             # Layout components
│   └── MainLayout.tsx   # Main app layout with navigation
│
├── router/              # Routing configuration
│   ├── index.tsx        # Route definitions
│   └── ProtectedRoute.tsx # Route protection wrapper
│
├── services/            # API communication
│   ├── api.ts           # Axios instance configuration
│   ├── authService.ts   # Authentication endpoints
│   ├── cardService.ts   # Vaccination card endpoints
│   ├── personService.ts # Person management endpoints
│   └── vaccineService.ts # Vaccine endpoints
│
├── types/               # TypeScript type definitions
│   ├── Auth.ts          # Authentication types
│   ├── Enums.ts         # Enum definitions
│   ├── Page.ts          # Pagination types
│   ├── Person.ts        # Person model
│   ├── VaccinationCard.ts # Vaccination card model
│   └── Vaccine.ts       # Vaccine model
│
├── styles/              # Global and component styles
│   ├── index.css        # Global styles
│   └── modalStyles.css  # Modal component styles
│
├── App.tsx              # Root component
└── main.tsx             # Application entry point
```


## 🚀 Available Scripts

### Development

```bash
npm run dev
```

Starts the Vite development server with hot module replacement (HMR).

### Production Build

```bash
npm run build
```

Creates an optimized production build in the `dist/` directory.

### Preview Build

```bash
npm run preview
```

Serves the production build locally for testing.

### Linting

```bash
npm run lint
```

Runs ESLint to check code quality and identify issues.


## 🔐 Authentication Flow

1. User accesses the application
2. If not authenticated, redirected to **LoginPage**
3. User can:
   - Log in with credentials (redirects to **HomePage**)
   - Register a new account
4. JWT token stored in browser session
5. Token included in all API requests via `Authorization: Bearer <token>` header
6. Protected routes enforce authentication


## 📱 Key Pages

### LoginPage
- User login with email/CPF and password
- Link to registration

### RegisterPage
- User registration (creates auth account)
- Redirects to login after successful registration

### HomePage
- Dashboard with navigation options
- Quick access to main features
- Welcome message for authenticated users

### SearchPersonPage
- Search for persons by CPF
- Display person details
- Navigate to vaccination card

### RegisterPersonPage
- Register a new person in the system
- Input: name, CPF, date of birth, sex

### VaccinationCardPage
- View complete vaccination grid for a person
- Shows vaccine status for each dose (TAKEN, MISSING, NOT_APPLICABLE)
- Add new vaccination records
- Delete vaccination records

### RegisterVaccinePage
- Register a new vaccination dose for a person
- Select vaccine and dose type
- Set application date
- Backend validates dose sequence rules


## 🔌 API Integration

All API calls are managed through service modules in `src/services/`:

- **authService.ts** - Login, register, authentication
- **personService.ts** - Create, search, list persons
- **cardService.ts** - Fetch vaccination card grid, add/delete records
- **vaccineService.ts** - List available vaccines
- **api.ts** - Axios configuration with base URL and interceptors

### Example: Fetching a Vaccination Card

```typescript
import { cardService } from '@/services';

const response = await cardService.getCard(personId);
console.log(response.data.vaccines); // Array of vaccines with doses
```


## 📝 Type Definitions

Key TypeScript types are defined in `src/types/`:

- **Person** - { id, name, cpf, dateOfBirth, sex }
- **Vaccine** - { id, name, category, doseSchedule }
- **VaccinationRecord** - { id, personId, vaccineId, doseType, applicationDate }
- **DoseStatus** - 'TAKEN' | 'MISSING' | 'NOT_APPLICABLE'


## 🎨 Styling

The project uses CSS modules and plain CSS:

- **index.css** - Global styles and reset
- **modalStyles.css** - Styles for modal dialogs
- Component-level styles are imported as modules


## 🔗 Backend Integration

The frontend communicates with the backend API running on `http://localhost:8080`. 

Key endpoints used:
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register` - User registration
- `GET /api/persons/search?cpf=...` - Search person
- `GET /api/persons/{id}/card` - Get vaccination card
- `POST /api/persons/{id}/card` - Add vaccination record
- `DELETE /api/persons/{id}/card/records/{recordId}` - Delete record
- `GET /api/vaccines` - List vaccines


## 🚢 Deployment

### Build for Production

```bash
npm run build
```

This creates a `dist/` folder with optimized files ready for deployment.

### Serve Production Build

```bash
npm run preview
```

This serves the production build locally on port 4173 for testing.

### Deploy to Hosting

The `dist/` folder can be deployed to any static hosting service:
- Vercel
- Netlify
- GitHub Pages
- AWS S3 + CloudFront
- etc.


## 📝 Notes

- The frontend expects the backend API to be running and accessible
- JWT tokens are stored in the browser session (not persisted)
- The vaccination card grid is pre-processed by the backend for easier rendering
- Date format used: YYYY-MM-DD
- All dates in the system are in UTC
