# BioFlora API [![CD - API](../../actions/workflows/deploy.yml/badge.svg)](../../actions/workflows/deploy.yml)

API Backend para levantamento de flora desenvolvida com Quarkus e PostgreSQL.

## 📋 Características

- ✅ Autenticação JWT
- ✅ CRUD completo para Usuários, Levantamentos e Indivíduos
- ✅ Upload de imagens de plantas (BLOB no DB)
- ✅ Integração com IA (Groq/LLaMA) para identificação de espécies
- ✅ Paginação em endpoints de listagem
- ✅ Versionamento de banco de dados com Liquibase
- ✅ Documentação automática com Swagger/OpenAPI

## 🚀 Quick Start

### Pré-requisitos

- Java 25+
- Maven 3.8+
- PostgreSQL 12+

### Instalação

1. Clone o repositório e entre na pasta

2. Configure o banco de dados em `src/main/resources/application.properties`:
```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=seu_usuario
quarkus.datasource.password=sua_senha
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/bioflora
```

3. Configure as variáveis de ambiente:
```bash
export OPENAI_API_KEY=sua_chave_groq
export GROQ_MODEL=llama-3.3-70b-versatile
```

4. Crie o banco de dados PostgreSQL:
```sql
CREATE DATABASE bioflora;
```

5. **IMPORTANTE - Configurar Liquibase:**
   - Crie a pasta: `src/main/resources/db/changelog/`
   - Copie os arquivos XML do Liquibase (veja `LIQUIBASE_SETUP.md`)
   - Alternativamente, execute as SQL em `MIGRATION.sql` manualmente

6. Build do projeto:
```bash
./mvnw clean package
```

7. Execute:
```bash
./mvnw quarkus:dev
```

A API estará disponível em `http://localhost:8080`

## 📚 Documentação da API

Acesse a documentação interativa em: `http://localhost:8080/swagger-ui`

### Endpoints Principais

#### Autenticação

- **POST** `/api/usuarios` - Criar conta de usuário
- **POST** `/api/login` - Login e obter JWT token
- **GET** `/api/usuarios/{id}` - Obter dados do usuário
- **PUT** `/api/usuarios/{id}` - Atualizar usuário
- **DELETE** `/api/usuarios/{id}` - Deletar usuário
- **GET** `/api/usuarios?page=0&size=10` - Listar usuários (paginado)

#### Levantamentos

- **POST** `/api/levantamentos?usuarioId={id}` - Criar levantamento
- **GET** `/api/levantamentos/{id}` - Obter levantamento
- **PUT** `/api/levantamentos/{id}` - Atualizar levantamento
- **DELETE** `/api/levantamentos/{id}` - Deletar levantamento
- **GET** `/api/levantamentos?page=0&size=10` - Listar levantamentos (paginado)
- **GET** `/api/levantamentos/usuario/{usuarioId}?page=0&size=10` - Levantamentos do usuário

#### Indivíduos

- **POST** `/api/individuos/levantamento/{levantamentoId}` - Criar indivíduo
- **GET** `/api/individuos/{id}` - Obter indivíduo
- **PUT** `/api/individuos/{id}` - Atualizar indivíduo
- **DELETE** `/api/individuos/{id}` - Deletar indivíduo
- **GET** `/api/individuos?page=0&size=10` - Listar indivíduos (paginado)
- **GET** `/api/individuos/levantamento/{levantamentoId}?page=0&size=10` - Indivíduos do levantamento
- **POST** `/api/individuos/{id}/completar-dados-ia` - Completar dados com IA ⭐

#### Imagens

- **POST** `/api/imagens/individuo/{individuoId}` - Upload de imagem (multipart/form-data)
- **GET** `/api/imagens/{id}` - Download/visualizar imagem
- **GET** `/api/imagens/individuo/{individuoId}` - Listar imagens do indivíduo
- **DELETE** `/api/imagens/{id}` - Deletar imagem

#### IA (Consulta de Espécie)

- **POST** `/consulta-especie/identificacao` - Identificar espécie via IA (multipart/form-data)

## 🔐 Autenticação

A API usa JWT (JSON Web Token) para autenticação.

### Fluxo de Login

1. Criar conta:
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Silva", "email": "joao@example.com", "senha": "senha123"}'
```

2. Fazer login:
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email": "joao@example.com", "senha": "senha123"}'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

3. Usar o token nos headers das requisições:
```bash
curl -X GET http://localhost:8080/api/levantamentos/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 📤 Upload de Imagens

### Exemplo com cURL

```bash
curl -X POST http://localhost:8080/api/imagens/individuo/1 \
  -H "Authorization: Bearer TOKEN" \
  -F "arquivo=@foto_planta.jpg"
```

## 🤖 Integração com IA

### Fluxo de Completar Dados com IA

1. Criar um indivíduo com nome popular (opcional):
```bash
POST /api/individuos/levantamento/1
{
  "parcela": "A1",
  "nomePopular": "Ipê",
  "vivoMorto": "vivo",
  "dataLevantamento": "2024-01-15T10:30:00"
}
```

2. Upload de imagem(s) do indivíduo:
```bash
POST /api/imagens/individuo/1
-F "arquivo=@foto_planta.jpg"
```

3. Chamar endpoint para completar dados com IA:
```bash
POST /api/individuos/1/completar-dados-ia
```

A IA retornará:
- Nome científico identificado
- Nome popular confirmado
- Dados de taxonomia
- Informações de ecologia

## 🗄️ Banco de Dados

Consulte `MIGRATION.sql` para ver as estruturas de tabelas.

### Migração com Liquibase

Consulte `LIQUIBASE_SETUP.md` para instruções detalhadas de configuração.

## 🛠️ Desenvolvimento

### Rodar em modo dev com hot reload:
```bash
./mvnw quarkus:dev
```

### Build nativo:
```bash
./mvnw package -Dnative
```

## 📞 Mais Informações

- Consulte `LIQUIBASE_SETUP.md` para configurar o Liquibase
- Consulte `MIGRATION.sql` para ver as estruturas SQL das tabelas
- A documentação interativa do Swagger está em `/swagger-ui`
- OpenAPI spec em `/openapi`
