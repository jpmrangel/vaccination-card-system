# Sistema de Cartão de Vacinação (Vaccination Card System)

Este repositório contém o código-fonte completo para um sistema de gerenciamento de cartões de vacinação digital, dividido em um backend Spring Boot e um frontend React.

## Visão Geral

O objetivo deste projeto é permitir o cadastro de pessoas, tipos de vacinas (com seus esquemas de doses), e o registro das vacinas aplicadas a cada pessoa. A interface principal exibe um "grid" visual do cartão de vacinação, mostrando doses tomadas, faltosas e não aplicáveis.

## Estrutura do Monorepo

* **`/backend`**: Contém a API REST desenvolvida com Java 21, Spring Boot 3.5+, Spring Security (JWT) e Spring Data JPA, conectando-se a um banco de dados PostgreSQL. Segue o padrão de arquitetura de Use Cases.
    * [Ver README do Backend](./backend/README.md) para detalhes de desenvolvimento e API.
* **`/frontend`**: Contém a aplicação de interface do usuário desenvolvida com React, TypeScript e Vite. Consome a API do backend para exibir e gerenciar os dados.
    * [Ver README do Frontend](./frontend/README.md) para detalhes.
* **`docker-compose.yml`**: Define os serviços necessários (PostgreSQL e a API Backend) para rodar o ambiente completo facilmente com Docker.

## Tecnologias Principais

* **Backend:** Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate, PostgreSQL, Maven
* **Frontend:** React, TypeScript, Vite, Axios, React Router DOM
* **Banco de Dados:** PostgreSQL
* **Containerização:** Docker, Docker Compose

## 🚀 Executando o Projeto (Docker Compose - Recomendado)

Esta é a maneira mais fácil de rodar o ambiente completo (API + Banco de Dados).

1.  **Pré-requisitos:**
    * **Docker:** [Instale o Docker Desktop](https://www.docker.com/products/docker-desktop/) ou Docker Engine/Compose no seu sistema. Certifique-se de que o serviço Docker esteja rodando.
    * **Git:** Para clonar o repositório.

2.  **Clone o Repositório:**
    ```bash
    git clone https://github.com/jpmrangel/vaccination-card-system.git
    cd vaccination-card-system
    ```

3.  **Inicie os Serviços:**
    Na **raiz** do projeto (onde o `docker-compose.yml` está), execute:
    ```bash
    docker-compose up --build -d
    ```
    * `--build`: Garante que a imagem da API Java seja (re)construída se houver mudanças no código. Pode demorar alguns minutos na primeira vez.
    * `-d`: (Opcional) Roda os containers em *detached mode* (background). Sem `-d`, os logs aparecerão no terminal atual.

4.  **Aguarde:** Espere alguns instantes para o container do PostgreSQL iniciar e a API Spring Boot conectar-se a ele. Você pode verificar os logs com `docker-compose logs -f vaccination-api`.

5.  **Acesse:**
    * **API Backend:** Estará disponível em `http://localhost:8080`.
    * **Aplicação Frontend:** Navegue até a pasta `frontend` em outro terminal e rode `npm install` (apenas na primeira vez) e `npm run dev`. Acesse `http://localhost:5173` (ou a porta indicada pelo Vite).

**Para Parar os Containers:**
Na raiz do projeto, execute:
```bash
docker-compose down
```
(Use docker-compose down -v para remover também o volume postgres_data e apagar os dados do banco).