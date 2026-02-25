# 🧬 Vaccination API

REST API for managing digital vaccination cards.

This is the **backend** service for the `vaccination-card-system`, responsible for all business logic, data management, and vaccination rules.


## 🏗️ 1. Architectural Decisions

The architecture of this project was designed to be **robust**, **scalable**, and **easy to maintain**, following **Clean Architecture** principles.

### 🔹 Use Case Pattern

Instead of a traditional `Service` layer, the **Use Case** pattern was adopted.  
Each business functionality (e.g., *Create Person*, *Add Vaccination*) is isolated in its own class.

**Benefit:** Promotes the *Single Responsibility Principle (SRP)*, making each class simpler, focused, and **easy to test**.
* **Tests:** Unit tests were implemented for most Use Cases of the main business logic (registration, search, vaccination rules), using Mockito to isolate dependencies. _(Note: Tests for the authentication layer were not implemented)._

### 🔹 Layer Separation

The application flow is **strictly unidirectional**:

-   **Controller (API Layer):** Receives HTTP requests and delegates. Contains no business logic.  
-   **UseCase (Business Layer):** Orchestrates logic, executes validations (e.g., sequential doses) and calls repositories.  
-   **Mapper:** Converts Entities (`Person`) into DTOs (`PersonResponse`), keeping the code clean and DRY.  
-   **Repository (Data Layer):** Spring Data JPA interfaces to abstract database access.

### 🔹 DTOs (Data Transfer Objects)

We never expose database entities directly.  
We use DTOs (`PersonRequest`, `PersonResponse`) to define the API contract, increasing control and security.

### 🔹 Data Integrity with Enums

Critical fields such as `DoseType`, `Sex`, and `VaccineCategory` are **Enums**, ensuring that only valid values are saved.

### 🔹 Business Logic in the Backend

Rules such as *"a booster can only be applied after the last primary dose"* are validated in the backend (`AddVaccinationUseCase`), ensuring data integrity.

### 🔹 "Smart" API for the Grid

The route `GET /api/persons/{id}/card` returns a pre-processed "Grid" DTO with the status of each dose (`TAKEN`, `MISSING`, `NOT_APPLICABLE`), simplifying rendering on the frontend.

### 🔹 Authentication and Security

* The system uses **Spring Security** to manage authentication.
* Authentication is based on **JWT Tokens (JSON Web Tokens)**, ensuring a *stateless* API.
* Endpoints under `/api/auth/**` (login, registration) are public, while all other endpoints (`/api/**`) require a valid JWT token in the `Authorization: Bearer <token>` header.
* User passwords are stored securely using `BCryptPasswordEncoder`.


## ⚙️ 2. Setup and Execution

Follow the steps below to configure the local environment.


### 🧩 Prerequisites

- **Java (JDK 21)**  :
  Make sure the `JAVA_HOME` environment variable points to JDK 21.  
  Verify with:
  ```bash
  java -version
  javac -version
  ```
- **Maven**:
The project uses the Maven Wrapper (mvnw), so there is no need to install Maven manually.

- **PostgreSQL**:
Relational database used by the application.

#### 🗃️ 1. Database Configuration

Create the database and user manually before starting the application:

```
# 1. Access psql as superuser (Linux)
sudo -u postgres psql

# 2. Create the database
CREATE DATABASE vaccination_db;

# 3. Create the 'admin' user with password 'admin' (or use your preference)
CREATE USER admin WITH PASSWORD 'admin';

# 4. Grant the user permission to connect to the database
GRANT ALL PRIVILEGES ON DATABASE vaccination_db TO admin;

# 5. Connect to the new database
\c vaccination_db

# 6. Allow table creation in the 'public' schema
GRANT ALL ON SCHEMA public TO admin;

# 7. Exit psql
\q
```

#### ⚙️ 2. Application Configuration

Clone the repository and navigate to the backend folder:
```
git clone https://github.com/your-username/vaccination-card-system.git
cd vaccination-card-system/backend
```
Create (or edit) the src/main/resources/application.properties file with the database credentials:
```
# Application Name
spring.application.name=vaccination-api

# Database (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/vaccination_db
spring.datasource.username=admin
spring.datasource.password=admin

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server
server.port=8080
```

## 🚀 3. Execution and Tests

### ▶️ Running the Application

At the root of the `backend/` folder (where the `pom.xml` file is), execute the command below to start the application:

```bash
./mvnw clean spring-boot:run
```

The clean parameter ensures that the previous build is cleaned before the new execution.

After initialization, the API will be available at:
👉 http://localhost:8080

### 🧪 Running Tests

To run the unit test suite, use:

```bash
./mvnw test
```


## 📘 4. API Documentation (Endpoints)

The base URL for all endpoints is:

👉 **http://localhost:8080**


### 👤 Resource: Persons

Manages person registration in the system.


#### ➕ POST `/api/persons`

Creates a new person.

**Request Body:**
```json
{
  "name": "Ana Paula",
  "cpf": "12345678900",
  "dateOfBirth": "1990-10-20",
  "sex": "FEMININO"
}
```

Success Response — 201 Created:

```json
{
  "id": 1,
  "name": "Ana Paula",
  "cpf": "12345678900",
  "dateOfBirth": "1990-10-20",
  "sex": "FEMININO"
}
```

#### 📄 GET `/api/persons`

Lists all persons in a paginated format.

Query Params (optional): 
- `page: page number (starts at 0)`, 
- `size: number of items per page (default: 20)`,
- `sort: field for sorting (e.g., name,asc)`

Example call:

`GET /api/persons?page=0&size=5&sort=name,asc`

Success Response — 200 OK:
```json
{
  "content": [
    {
      "id": 1,
      "name": "Ana Paula",
      "cpf": "12345678900",
      "dateOfBirth": "1990-10-20",
      "sex": "FEMININO"
    }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "empty": false
}
```

#### 🔍 GET `/api/persons/search?cpf=12345678900`

Searches for a specific person by CPF.

Success Response — 200 OK:
```json
{
  "id": 1,
  "name": "Ana Paula",
  "cpf": "12345678900",
  "dateOfBirth": "1990-10-20",
  "sex": "FEMININO"
}
```

Error — 404 Not Found: if the CPF is not found.

#### 🗑️ DELETE `/api/persons/{id}`

Deletes a person and all their associated vaccination records.

Example:

`DELETE /api/persons/1`

Success Response — 204 No Content

### 💉 Resource: Vaccines

Manages vaccine types available in the system.
#### ➕ POST `/api/vaccines`

Cadastra um novo tipo de vacina.

Request Body:
```json
{
  "name": "BCG",
  "category": "CARTEIRA_NACIONAL",
  "doseSchedule": ["PRIMEIRA_DOSE"]
}
```

Success Response — 201 Created:
```json
{
  "id": 1,
  "name": "BCG",
  "category": "CARTEIRA_NACIONAL",
  "doseSchedule": ["PRIMEIRA_DOSE"]
}
```

#### 📄 GET `/api/vaccines`

Lists all registered vaccine types.

Success Response — 200 OK:
```json
[
  {
    "id": 1,
    "name": "BCG",
    "category": "CARTEIRA_NACIONAL",
    "doseSchedule": ["PRIMEIRA_DOSE"]
  }
]
```

### 💊 Resource: Vaccination Card

Manages vaccination records (the "grid") for a specific person.

#### 📄 GET `/api/persons/{personId}/card`

Retrieves the complete vaccination card grid for a person.

Query Param (optional):
- `category: filters vaccines by category (e.g., ANTI_RABICA)`

Example call:

GET `/api/persons/1/card`

Success Response — 200 OK:
```json
{
  "person": {
    "id": 1,
    "name": "Ana Paula"
    ...
  },
  "vaccines": [
    {
      "vaccineId": 1,
      "vaccineName": "BCG",
      "category": "CARTEIRA_NACIONAL",
      "doses": [
        {
          "doseType": "PRIMEIRA_DOSE",
          "status": "TAKEN",
          "recordId": 101,
          "applicationDate": "2023-01-10"
        },
        {
          "doseType": "SEGUNDA_DOSE",
          "status": "NOT_APPLICABLE",
          "recordId": null,
          "applicationDate": null
        }
      ]
    },
    {
      "vaccineId": 2,
      "vaccineName": "Hepatite B",
      "category": "CARTEIRA_NACIONAL",
      "doses": [
        {
          "doseType": "PRIMEIRA_DOSE",
          "status": "MISSING",
          "recordId": null,
          "applicationDate": null
        }
        ...
      ]
    }
  ]
}
```

#### ➕ `POST /api/persons/{personId}/card`

Registers a new vaccine dose for a person.

Request Body:
```json
{
  "vaccineId": 2,
  "applicationDate": "2025-10-26",
  "dose": "PRIMEIRA_DOSE"
}
```

Success Response — 201 Created: returns the updated DTO.

Error — 400 Bad Request: if validation logic fails.
```json
{
  "message": "1st dose is required before registering the 2nd dose."
}
```

#### 🗑️ `DELETE /api/persons/{personId}/card/records/{recordId}`

Deletes a specific vaccination record (an applied dose).

Example:

DELETE `/api/persons/1/card/records/101`

Success Response — 204 No Content