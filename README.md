# Task Manager

Aplicação full-stack de gerenciamento de tarefas, desenvolvida com **Spring Boot** e **React**, com autenticação JWT, API REST, testes automatizados e infraestrutura para desenvolvimento local com Docker.

## Sobre o projeto

O Task Manager é uma aplicação para gerenciamento de tarefas por usuários.

O projeto foi desenvolvido com foco em estudo e prática de desenvolvimento full-stack, aplicando conceitos e tecnologias comuns em projetos Java modernos, como:

- API REST com Spring Boot;
- persistência de dados com JPA/Hibernate;
- PostgreSQL;
- autenticação e autorização com Spring Security e JWT;
- migrations de banco de dados com Flyway;
- validação de dados;
- testes automatizados;
- documentação de API com OpenAPI/Swagger;
- frontend em React com TypeScript;
- execução local com Docker e Docker Compose.

A aplicação possui dois principais contextos de negócio: **usuários** e **tarefas**. Cada tarefa pertence a um usuário autenticado.

## Funcionalidades

### Autenticação

- Cadastro de usuário;
- Login com geração de token JWT;
- Senhas armazenadas utilizando BCrypt;
- Proteção das rotas de tarefas;
- Autenticação stateless;
- Tratamento de acesso não autorizado.

### Gerenciamento de tarefas

Usuários autenticados podem:

- criar tarefas;
- listar suas tarefas;
- consultar uma tarefa específica;
- atualizar tarefas;
- excluir tarefas;
- definir status;
- definir prioridade;
- informar descrição;
- informar data de vencimento.

No frontend, as tarefas podem ser filtradas por:

- status;
- prioridade.

### Frontend

- Página de login;
- página de cadastro;
- rotas protegidas;
- listagem de tarefas;
- criação de tarefas;
- edição de tarefas;
- página de detalhes;
- exclusão de tarefas;
- estados de carregamento, erro e lista vazia;
- tratamento de respostas não autorizadas.

## Tecnologias

### Backend

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot 3.5.4 | Framework principal |
| Spring Web | API REST |
| Spring Data JPA | Persistência |
| Hibernate | ORM |
| PostgreSQL | Banco de dados |
| Spring Security | Segurança e autenticação |
| JJWT | Geração e validação de tokens JWT |
| Bean Validation | Validação dos dados |
| Flyway | Migrations do banco |
| Springdoc OpenAPI | Documentação da API |
| H2 | Banco em memória para testes |
| JaCoCo | Relatório de cobertura de testes |

### Frontend

| Tecnologia | Uso |
|---|---|
| React 19 | Interface |
| TypeScript | Tipagem |
| Vite | Build e desenvolvimento |
| React Router | Roteamento |
| React Hook Form | Gerenciamento de formulários |
| Zod | Validação |
| Vitest | Testes |
| Testing Library | Testes de componentes |
| ESLint | Análise estática |

### Infraestrutura

- Docker;
- Docker Compose;
- PostgreSQL 16.

## Arquitetura

O projeto é dividido em backend e frontend:

```text
task-manager/
├── src/                 # Backend Spring Boot
├── frontend/            # Frontend React
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── .gitignore
```

### Backend

```text
src/main/java/io/github/oliveiraprojetos/taskmanager/
├── config/
├── controller/
├── dto/
├── enums/
├── exception/
├── model/
├── repository/
├── security/
├── service/
└── TaskManagerApplication.java
```

A aplicação segue uma separação por responsabilidades:

- `controller`: endpoints HTTP;
- `service`: regras de negócio;
- `repository`: acesso aos dados;
- `model`: entidades persistidas;
- `dto`: objetos utilizados na entrada e saída da API;
- `security`: autenticação e filtro JWT;
- `exception`: exceções e tratamento global;
- `config`: configurações da aplicação;
- `enums`: valores enumerados utilizados pelo domínio.

### Frontend

```text
frontend/
├── src/
│   ├── components/
│   ├── contexts/
│   ├── pages/
│   ├── schemas/
│   ├── services/
│   ├── test/
│   ├── types/
│   ├── utils/
│   ├── App.tsx
│   ├── App.css
│   ├── index.css
│   └── main.tsx
├── public/
├── package.json
└── ...
```

## Segurança

A API utiliza **Spring Security** com autenticação baseada em JWT.

O fluxo principal é:

```text
Login
  │
  ▼
POST /auth/login
  │
  ▼
JWT
  │
  ▼
Frontend envia:
Authorization: Bearer <token>
  │
  ▼
JwtAuthenticationFilter
  │
  ▼
Acesso às rotas protegidas
```

As rotas de autenticação são públicas:

```text
POST /auth/register
POST /auth/login
```

As operações de tarefas exigem autenticação.

Além disso, as operações de tarefas são associadas ao usuário autenticado. A aplicação busca as tarefas utilizando o identificador do usuário, evitando que um usuário acesse diretamente tarefas pertencentes a outro usuário.

## API

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastra um usuário |
| POST | `/auth/login` | Realiza login e retorna um JWT |

### Tarefas

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/tasks` | Lista as tarefas do usuário autenticado |
| POST | `/tasks` | Cria uma tarefa |
| GET | `/tasks/{id}` | Consulta uma tarefa |
| PUT | `/tasks/{id}` | Atualiza uma tarefa |
| DELETE | `/tasks/{id}` | Exclui uma tarefa |

Os endpoints de tarefas utilizam autenticação Bearer com JWT.

## Documentação da API

A API possui documentação utilizando **OpenAPI/Swagger**.

Com o backend executando localmente, os principais endpoints de documentação são:

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/swagger-ui/
http://localhost:8080/v3/api-docs
```

A documentação também possui configuração de autenticação Bearer para utilização do JWT nas rotas protegidas.

## Banco de dados

O projeto utiliza **PostgreSQL** no ambiente principal.

O schema inicial é criado através do Flyway:

```text
src/main/resources/db/migration/
└── V1__create_initial_schema.sql
```

A estrutura atual possui as tabelas:

```text
users
  │
  │ 1:N
  ▼
tasks
```

Cada tarefa possui uma referência ao usuário através de `user_id`.

### Principais campos

**users**

- `id`
- `username`
- `password`

**tasks**

- `id`
- `status`
- `priority`
- `title`
- `description`
- `due_date`
- `created_at`
- `updated_at`
- `user_id`

No ambiente de testes, o projeto utiliza **H2 em memória** com `create-drop`, permitindo executar os testes sem depender de um banco PostgreSQL externo.

## Testes

O projeto possui testes automatizados para diferentes camadas do backend e frontend.

### Backend

Existem testes para:

- configurações;
- controllers;
- DTOs;
- exceções;
- entidades;
- repositories;
- segurança;
- services.

Os testes do backend utilizam **JUnit/Spring Boot Test**, **Spring Security Test** e **H2**.

### Frontend

Existem testes para:

- componentes;
- páginas;
- rotas protegidas;
- formulários;
- listagem e edição de tarefas.

O frontend utiliza **Vitest** e **Testing Library**.

### Cobertura

O backend possui configuração do **JaCoCo** para geração de relatório de cobertura durante a execução dos testes.

## Como executar

### Pré-requisitos

Para executar o projeto localmente, tenha instalado:

- Java 21;
- Maven;
- Node.js e npm;
- Docker e Docker Compose, caso queira utilizar a infraestrutura Docker.

### 1. Backend

O backend utiliza variáveis de ambiente para configuração do banco de dados e do JWT.

Exemplo para execução local:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskmanager
export DB_USERNAME=taskmanager
export DB_PASSWORD=taskmanager123
export JWT_SECRET=<sua-chave-secreta>
export JWT_EXPIRATION=3600000
```

Depois, execute:

```bash
mvn spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

### 2. Frontend

Entre no diretório do frontend:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

O projeto utiliza a variável:

```env
VITE_API_URL=http://localhost:8080
```

Ela está configurada no arquivo local `frontend/.env`.

Para iniciar o frontend:

```bash
npm run dev
```

O Vite disponibilizará a aplicação no endereço padrão de desenvolvimento:

```text
http://localhost:5173
```

### 3. Testes do frontend

Para executar os testes:

```bash
npm test
```

Para executar uma vez sem o modo interativo:

```bash
npm run test:run
```

Para verificar o lint:

```bash
npm run lint
```

Para gerar o build:

```bash
npm run build
```

## Executando com Docker

O projeto possui Docker Compose para facilitar a execução local do PostgreSQL e da aplicação.

O `docker-compose.yml` define:

- PostgreSQL 16;
- banco `taskmanager`;
- usuário `taskmanager`;
- volume persistente para os dados;
- aplicação na porta `8080`.

O `Dockerfile` utiliza uma imagem Java 21 JRE e copia o JAR previamente gerado para dentro da imagem.

Por isso, primeiro gere o JAR:

```bash
mvn clean package
```

Depois, execute:

```bash
docker compose up --build
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

O frontend continua sendo executado separadamente durante o desenvolvimento:

```bash
cd frontend
npm run dev
```

### Observação sobre o ambiente Docker

Dentro do Docker Compose, a aplicação utiliza o nome do serviço PostgreSQL como host:

```text
jdbc:postgresql://postgres:5432/taskmanager
```

Isso é diferente da configuração utilizada quando o backend é executado diretamente na máquina local.

As credenciais presentes no `docker-compose.yml` fazem parte da configuração de desenvolvimento local deste projeto.

## Estrutura do projeto

```text
task-manager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── ...
│
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── .gitignore
└── README.md
```

Arquivos e diretórios específicos do ambiente local, como `.git/`, `.idea/` e `target/`, não fazem parte da estrutura versionada apresentada acima.

## Qualidade e boas práticas

Durante o desenvolvimento foram aplicadas práticas como:

- separação de responsabilidades entre camadas;
- utilização de DTOs para entrada e saída da API;
- tratamento global de exceções;
- validação de dados;
- autenticação stateless com JWT;
- proteção de recursos por usuário;
- migrations versionadas com Flyway;
- testes automatizados no backend e frontend;
- testes de componentes e páginas React;
- análise estática com ESLint;
- relatório de cobertura com JaCoCo;
- documentação da API com OpenAPI/Swagger;
- configuração da aplicação por variáveis de ambiente.

## Próximos passos

O projeto continuará evoluindo com foco em:

- criação de pipeline de CI com GitHub Actions;
- deploy da aplicação;
- refinamentos de código e arquitetura;
- melhorias de infraestrutura e configuração;
- evolução contínua do projeto para portfólio.

## Status do projeto

**Em desenvolvimento.**

A aplicação já possui backend, frontend, autenticação, gerenciamento de tarefas, testes automatizados, documentação da API e configuração para execução local com Docker.

As próximas etapas planejadas são a preparação final para publicação no GitHub, criação do CI e posteriormente o deploy.

## Autor

**Gabriel Oliveira**

Projeto desenvolvido para estudo e construção de portfólio em desenvolvimento Java e full-stack.
