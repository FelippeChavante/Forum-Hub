# ForumHub API

API REST para um sistema de fórum de discussão, desenvolvida com Spring Boot 3.

## Tecnologias

- Java 21
- Spring Boot 3.5.4
- Spring Security (JWT)
- Spring Data JPA
- MySQL 8
- Flyway

## Requisitos

- Java 21+
- Maven 4+

## Variáveis de Ambiente

| Variável           | Descrição                        | Padrão     |
|--------------------|----------------------------------|------------|
| DB_ROOT_PASSWORD   | Senha do root do MySQL           | root       |
| DB_USERNAME        | Usuário do banco                 | root       |
| DB_PASSWORD        | Senha do banco                   | root       |
| DB_NAME            | Nome do banco de dados           | forumhub   |
| JWT_SECRET         | Chave para gerar tokens JWT      | 12345678   |

## Autenticação

A API usa JWT. Para endpoints protegidos:

1. Faça login: `POST /login`
2. Receba o token
3. Use o header: `Authorization: Bearer {token}`

## Endpoints

### Autenticação

- `POST /login` → Login (público)

### Tópicos

- `GET /topicos` → Listar
- `GET /topicos/{id}` → Detalhar
- `POST /topicos` → Criar (autenticado)
- `PUT /topicos/{id}` → Atualizar (autenticado)
- `DELETE /topicos/{id}` → Excluir (autenticado)

### Respostas

- `GET /respostas` → Listar
- `GET /respostas/{id}` → Detalhar
- `POST /respostas` → Criar (autenticado)
- `PUT /respostas/{id}` → Atualizar (autenticado)
- `DELETE /respostas/{id}` → Excluir (autenticado)

### Cursos

- `GET /cursos` → Listar
- `GET /cursos/{id}` → Detalhar
- `POST /cursos` → Criar (ADMIN)
- `PUT /cursos/{id}` → Atualizar (ADMIN)
- `DELETE /cursos/{id}` → Excluir (ADMIN)

### Usuários

- `GET /usuarios` → Listar (ADMIN)
- `GET /usuarios/{id}` → Detalhar (autenticado)
- `POST /usuarios` → Criar (ADMIN)
- `PUT /usuarios/{id}` → Atualizar (autenticado)
- `DELETE /usuarios/{id}` → Excluir (ADMIN)

## Paginação

Use os parâmetros:

- `page` (ex: `0`)
- `size` (ex: `10`)
- `sort` (ex: `data_criacao,desc`)

Exemplo: `/topicos?page=0&size=10&sort=data_criacao,desc`

## Dados de Teste

- Admin: `admin@forumhub.com` / `123456`
- Usuário: `joao@forumhub.com` / `123456`

## Estrutura

```
├── controller/           # Controladores REST
├── domain/
│   ├── dto/              # Objetos de transferência de dados
│   ├── model/            # Entidades JPA
│   ├── repository/       # Repositórios Spring Data
│   └── service/          # Serviços de negócio
└── infra/
    └── security/         # Configurações de segurança
```

## Banco de Dados

Tabelas principais:

- usuario
- perfil
- usuario_perfil
- curso
- topico
- resposta
