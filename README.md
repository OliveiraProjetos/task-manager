# Task Manager

![CI](https://github.com/OliveiraProjetos/task-manager/actions/workflows/ci.yml/badge.svg)

Aplicação full-stack de gerenciamento de tarefas desenvolvida com **Java, Spring Boot, React e TypeScript**. O projeto reúne uma API REST com autenticação JWT, persistência em PostgreSQL, testes automatizados e ambiente de desenvolvimento com Docker Compose.

## Funcionalidades

- Cadastro e login de usuários com autenticação JWT.
- Senhas protegidas com BCrypt.
- CRUD completo de tarefas.
- Definição de título, descrição, status, prioridade e data de vencimento.
- Filtros de tarefas por status e prioridade.
- Rotas protegidas e tratamento de sessões não autenticadas.
- Validação de dados e tratamento centralizado de exceções.
- Documentação interativa da API com Swagger/OpenAPI.
- Testes automatizados no backend e no frontend.
- Pipeline de CI com GitHub Actions.

## Tecnologias

### Backend
- Java 21
- Spring Boot 3.5.4
- Spring Web
- Spring Data JPA e Hibernate
- Spring Security e JWT (JJWT)
- Bean Validation
- PostgreSQL 16
- Flyway
- Springdoc OpenAPI / Swagger
- JUnit, Spring Boot Test, Spring Security Test e H2
- JaCoCo

### Frontend
- React 19
- TypeScript
- Vite
- React Router
- React Hook Form e Zod
- Vitest e Testing Library
- ESLint

### Infraestrutura e qualidade
- Docker e Docker Compose
- GitHub Actions

## Arquitetura

O repositório contém o backend Spring Boot na raiz e o frontend React no diretório `frontend/`.

```text
task-manager/
├── src/
│   ├── main/
│   │   ├── java/io/github/oliveiraprojetos/taskmanager/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/db/migration/
│   └── test/
├── frontend/
│   └── src/
│       ├── components/
│       ├── contexts/
│       ├── pages/
│       ├── schemas/
│       ├── services/
│       ├── test/
│       ├── types/
│       └── utils/
├── .github/workflows/ci.yml
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

O backend separa responsabilidades entre controllers, services e repositories. DTOs definem os dados de entrada e saída da API, enquanto Spring Security e o filtro JWT protegem os endpoints privados.

Cada tarefa está associada ao usuário que a criou. As consultas e operações de tarefas consideram o usuário autenticado, evitando acesso direto às tarefas de outras contas.

## API REST

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/auth/register` | Cadastra um usuário |
| `POST` | `/auth/login` | Autentica e retorna um JWT |

### Tarefas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/tasks` | Lista as tarefas do usuário autenticado |
| `POST` | `/tasks` | Cria uma tarefa |
| `GET` | `/tasks/{id}` | Consulta uma tarefa |
| `PUT` | `/tasks/{id}` | Atualiza uma tarefa |
| `DELETE` | `/tasks/{id}` | Exclui uma tarefa |

As rotas de tarefas exigem um token JWT no cabeçalho:

```http
Authorization: Bearer <token>
```

### Swagger / OpenAPI

Com o backend em execução, acesse:

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- Especificação OpenAPI: <http://localhost:8080/v3/api-docs>

O Swagger permite explorar os endpoints e testar as operações autenticadas usando o token JWT.

## Como executar localmente

### Pré-requisitos

- Java 21
- Maven
- Node.js e npm
- Docker com Docker Compose

### 1. Configurar variáveis de ambiente

Na raiz do repositório, crie um arquivo `.env` para o Docker Compose:

```env
JWT_SECRET=coloque_aqui_uma_chave_secreta_forte
```

Use uma chave longa e aleatória. Não compartilhe esse valor e não o versione no Git.

### 2. Iniciar backend e PostgreSQL com Docker

Gere o JAR do backend:

```bash
./mvnw clean package
```

Se o repositório não tiver o Maven Wrapper, use:

```bash
mvn clean package
```

Inicie os serviços:

```bash
docker compose up --build
```

A API ficará disponível em <http://localhost:8080>. O Docker Compose inicia o PostgreSQL e a aplicação; o Flyway gerencia as migrations do banco.

Para encerrar os serviços:

```bash
docker compose down
```

Os dados do PostgreSQL são mantidos no volume configurado pelo Compose. Para removê-los também, use `docker compose down -v` — isso apaga os dados locais do banco.

### 3. Iniciar o frontend

Em outro terminal:

```bash
cd frontend
npm ci
```

Crie `frontend/.env` com:

```env
VITE_API_URL=http://localhost:8080
```

Inicie o servidor de desenvolvimento:

```bash
npm run dev
```

O frontend ficará disponível em <http://localhost:5173>.

> **Observação:** o arquivo `frontend/.env` e o `.env` da raiz contêm configurações locais e não devem ser enviados ao Git. Não use as credenciais de desenvolvimento em um ambiente público.

## Testes e qualidade

### Backend

Executar testes e verificações Maven:

```bash
./mvnw --batch-mode --no-transfer-progress clean verify
```

Ou, se não houver Maven Wrapper:

```bash
mvn --batch-mode --no-transfer-progress clean verify
```

Os testes usam H2 em memória para evitar a dependência de um PostgreSQL externo durante a execução.

O JaCoCo está configurado para gerar relatório de cobertura durante a verificação Maven.

### Frontend

Dentro do diretório `frontend/`:

```bash
npm ci
npm run test:run
npm run lint
npm run build
```

- `test:run`: executa os testes sem modo interativo.
- `lint`: verifica padrões e possíveis problemas no código.
- `build`: verifica os tipos TypeScript e gera a versão de produção.

### Integração contínua

O GitHub Actions executa automaticamente as verificações do backend e do frontend em pushes para `main` e em pull requests direcionados a `main`.

O status da pipeline aparece no badge no início deste README e na aba **Actions** do repositório.

## Banco de dados

O banco principal é PostgreSQL. As migrations versionadas ficam em:

```text
src/main/resources/db/migration/
```

O modelo atual inclui usuários (`users`) e tarefas (`tasks`), com relacionamento de um usuário para várias tarefas por meio de `tasks.user_id`.

No ambiente de testes, o projeto usa H2 em memória.

## Status

**Backend, frontend, testes automatizados, Docker e CI implementados.**

O deploy ainda não foi realizado; a aplicação é executada localmente seguindo as instruções acima.

## Autor

**Gabriel Oliveira**

Projeto desenvolvido para praticar Java, Spring Boot e desenvolvimento full-stack, com foco em organização de código, segurança, testes automatizados e fluxo de desenvolvimento profissional.
