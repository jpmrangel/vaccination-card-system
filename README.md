# Vaccination Card System

This repository contains the complete source code for a digital vaccination card management system, divided into a Spring Boot backend and a React frontend.

## Overview

The goal of this project is to allow the registration of people, types of vaccines (with their dose schedules), and the record of vaccines administered to each person. The main interface displays a visual "grid" of the vaccination card, showing taken, missing, and non-applicable doses.

## Monorepo Structure

* **`/backend`**: Contains the REST API developed with Java 21, Spring Boot 3.5+, Spring Security (JWT), and Spring Data JPA, connecting to a PostgreSQL database. It follows the Use Cases architecture pattern.
    * [See Backend README](./backend/README.md) for development and API details.
* **`/frontend`**: Contains the user interface application developed with React, TypeScript, and Vite. It consumes the backend API to display and manage data.
    * [See Frontend README](./frontend/README.md) for details.
* **`docker-compose.yml`**: Defines the necessary services (PostgreSQL and Backend API) to easily run the complete environment with Docker.

## Main Technologies

* **Backend:** Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate, PostgreSQL, Maven
* **Frontend:** React, TypeScript, Vite, Axios, React Router DOM
* **Database:** PostgreSQL
* **Containerization:** Docker, Docker Compose

## 🚀 Running the Project (Docker Compose - Recommended)

This is the easiest way to run the complete environment (API + Database).

1.  **Prerequisites:**
    * **Docker:** [Install Docker Desktop](https://www.docker.com/products/docker-desktop/) or Docker Engine/Compose on your system. Make sure the Docker service is running.
    * **Git:** To clone the repository.

2.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/jpmrangel/vaccination-card-system.git](https://github.com/jpmrangel/vaccination-card-system.git)
    cd vaccination-card-system
    ```

3.  **Start the Services:**
    At the project's **root** (where `docker-compose.yml` is located), run:
    ```bash
    docker-compose up --build -d
    ```
    * `--build`: Ensures the Java API image is (re)built if there are code changes. It might take a few minutes the first time.
    * `-d`: (Optional) Runs containers in *detached mode* (background). Without `-d`, logs will appear in the current terminal.

4.  **Wait:** Wait a few moments for the PostgreSQL container to start and the Spring Boot API to connect to it. You can check the logs with `docker-compose logs -f vaccination-api`.

5.  **Access:**
    * **Backend API:** Will be available at `http://localhost:8080`.
    * **Frontend Application:** Navigate to the `frontend` folder in another terminal and run `npm install` (only the first time) and `npm run dev`. Access `http://localhost:5173` (or the port indicated by Vite).

**To Stop Containers:**
At the project's root, run:
```bash
docker-compose down
```
(Use docker-compose down -v to also remove the postgres_data volume and delete the database data).
