# 🧬 Vaccination API

API REST para gerenciamento de cartões de vacinação digital.

Este é o serviço de **backend** para o sistema `vaccination-card-system`, responsável por toda a lógica de negócio, gerenciamento de dados e regras de vacinação.


## 🏗️ 1. Decisões Arquiteturais

A arquitetura deste projeto foi desenhada para ser **robusta**, **escalável** e de **fácil manutenção**, seguindo princípios da **Clean Architecture**.

### 🔹 Padrão de Use Case (Caso de Uso)

Em vez de uma camada de `Service` tradicional, foi adotado o padrão de **Use Case**.  
Cada funcionalidade de negócio (ex: *Criar Pessoa*, *Adicionar Vacinação*) é isolada em sua própria classe.

**Benefício:** Promove o *Princípio da Responsabilidade Única (SRP)*, tornando cada classe mais simples, focada e fácil de testar.

### 🔹 Separação de Camadas

O fluxo da aplicação é **estritamente unidirecional**:

- **Controller (Camada de API):** Recebe requisições HTTP e delega. Não contém lógica de negócio.  
- **UseCase (Camada de Negócio):** Orquestra a lógica, executa validações (ex: doses sequenciais) e chama os repositórios.  
- **Mapper:** Converte Entidades (`Person`) em DTOs (`PersonResponse`), mantendo o código limpo e DRY.  
- **Repository (Camada de Dados):** Interfaces Spring Data JPA para abstrair o acesso ao banco.

### 🔹 DTOs (Data Transfer Objects)

Nunca expomos as entidades do banco diretamente.  
Usamos DTOs (`PersonRequest`, `PersonResponse`) para definir o contrato da API, aumentando o controle e a segurança.

### 🔹 Integridade de Dados com Enums

Campos críticos como `DoseType`, `Sex`, e `VaccineCategory` são **Enums**, garantindo que apenas valores válidos sejam salvos.

### 🔹 Lógica de Negócio no Backend

Regras como *“um reforço só pode ser aplicado após a última dose primária”* são validadas no backend (`AddVaccinationUseCase`), garantindo integridade dos dados.

### 🔹 API "Inteligente" para o Grid

A rota `GET /api/persons/{id}/card` retorna um DTO de “Grid” pré-processado, com status de cada dose (`TAKEN`, `MISSING`, `NOT_APPLICABLE`), simplificando a renderização no frontend.


## ⚙️ 2. Setup e Execução

Siga os passos abaixo para configurar o ambiente local.


### 🧩 Pré-requisitos

- **Java (JDK 21)**  :
  Certifique-se de que a variável de ambiente `JAVA_HOME` aponta para o JDK 21.  
  Verifique com:
  ```bash
  java -version
  javac -version
  ```
- **Maven**:
O projeto utiliza o Maven Wrapper (mvnw), portanto não é necessário instalar o Maven manualmente.

- **PostgreSQL**:
Banco de dados relacional utilizado pela aplicação.

#### 🗃️ 1. Configuração do Banco de Dados

Crie o banco de dados e o usuário manualmente antes de iniciar a aplicação:

```
# 1. Acesse o psql como superusuário (Linux)
sudo -u postgres psql

# 2. Crie o banco de dados
CREATE DATABASE vaccination_db;

# 3. Crie o usuário 'admin' com senha 'admin' (ou use sua preferência)
CREATE USER admin WITH PASSWORD 'admin';

# 4. Dê ao usuário permissão para conectar ao banco
GRANT ALL PRIVILEGES ON DATABASE vaccination_db TO admin;

# 5. Conecte-se ao novo banco
\c vaccination_db

# 6. Permita criação de tabelas no schema 'public'
GRANT ALL ON SCHEMA public TO admin;

# 7. Saia do psql
\q
```

#### ⚙️ 2. Configuração da Aplicação

Clone o repositório e acesse a pasta backend:
```
git clone https://github.com/seu-usuario/vaccination-card-system.git
cd vaccination-card-system/backend
```
Crie (ou edite) o arquivo src/main/resources/application.properties com as credenciais do banco:
```
# Nome da Aplicação
spring.application.name=vaccination-api

# Banco de Dados (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/vaccination_db
spring.datasource.username=admin
spring.datasource.password=admin

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Servidor
server.port=8080
```

## 🚀 3. Execução e Testes

### ▶️ Executando a Aplicação

Na raiz da pasta `backend/` (onde está o arquivo `pom.xml`), execute o comando abaixo para iniciar a aplicação:

```bash
./mvnw clean spring-boot:run
```

O parâmetro clean garante que o build anterior seja limpo antes da nova execução.

Após a inicialização, a API estará disponível em:
👉 http://localhost:8080
🧪 Executando os Testes

Para rodar a suíte de testes unitários, utilize:

```bash
./mvnw test
```


## 📘 4. Documentação da API (Endpoints)

A URL base para todos os endpoints é:

👉 **http://localhost:8080**


### 👤 Recurso: Persons (Pessoas)

Gerencia o cadastro de pessoas no sistema.


#### ➕ POST `/api/persons`

Cria uma nova pessoa.

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

Lista todas as pessoas de forma paginada.

Query Params (opcionais): 
- `page: número da página (inicia em 0)`, 
- `size: quantidade de itens por página (padrão: 20)`,
- `sort: campo para ordenação (ex: name,asc)`

Exemplo de chamada:

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

Busca uma pessoa específica pelo CPF.

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

Erro — 404 Not Found: se o CPF não for encontrado.

#### 🗑️ DELETE `/api/persons/{id}`

Deleta uma pessoa e todos os seus registros de vacinação associados.

Exemplo:

`DELETE /api/persons/1`

Success Response — 204 No Content

### 💉 Recurso: Vaccines (Vacinas)

Gerencia os tipos de vacina disponíveis no sistema.
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

Lista todos os tipos de vacina cadastrados.

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

### 💊 Recurso: Vaccination Card (Cartão de Vacinação)

Gerencia os registros de vacinação (o "grid") de uma pessoa específica.

#### 📄 GET `/api/persons/{personId}/card`

Busca o grid completo do cartão de vacinação de uma pessoa.

Query Param (opcional):
- `category: filtra as vacinas por categoria (ex: ANTI_RABICA)`

Exemplo de chamada:

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

Registra uma nova dose de vacina para uma pessoa.

Request Body:
```json
{
  "vaccineId": 2,
  "applicationDate": "2025-10-26",
  "dose": "PRIMEIRA_DOSE"
}
```

Success Response — 201 Created: retorna o DTO atualizado.

Erro — 400 Bad Request: caso a lógica de validação falhe.
```json
{
  "message": "1st dose is required before registering the 2nd dose."
}
```

#### 🗑️ `DELETE /api/persons/{personId}/card/records/{recordId}`

Exclui um registro de vacinação específico (uma dose aplicada).

Exemplo:

DELETE `/api/persons/1/card/records/101`

Success Response — 204 No Content