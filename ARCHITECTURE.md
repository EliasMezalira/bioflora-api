# 🏗️ Arquitetura da BioFlora API

## Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           CLIENTE (Browser/App)                          │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                          HTTP/JSON (REST)
                                 │
┌────────────────────────────────▼────────────────────────────────────────┐
│                    QUARKUS FRAMEWORK 3.37.4                              │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                     REST CONTROLLERS (6)                         │   │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌─────────────┐   │   │
│  │  │UsuarioController│  │LevantamentoCtrl  │  │ImagemCtrl   │   │   │
│  │  └──────────────────┘  └──────────────────┘  └─────────────┘   │   │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌─────────────┐   │   │
│  │  │LoginController   │  │IndividuoCtrl     │  │IndividuoIAC │   │   │
│  │  └──────────────────┘  └──────────────────┘  └─────────────┘   │   │
│  │                                                                  │   │
│  │                  Endpoints: 23 RESTful APIs                     │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                           │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                    BUSINESS LOGIC LAYER                          │   │
│  │                                                                  │   │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌─────────────┐   │   │
│  │  │UsuarioService   │  │LevantamentoSvc   │  │ImagemSvc    │   │   │
│  │  │+ login()         │  │+ criar()         │  │+ upload()   │   │   │
│  │  │+ criarConta()    │  │+ atualizar()     │  │+ deletar()  │   │   │
│  │  │+ bcrypt hashing  │  │+ listar()        │  │+ validar()  │   │   │
│  │  │+ JWT geração     │  │                  │  │             │   │   │
│  │  └──────────────────┘  └──────────────────┘  └─────────────┘   │   │
│  │                                                                  │   │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌─────────────┐   │   │
│  │  │IndividuoService  │  │IndividuoIAService│  │Repositories │   │   │
│  │  │+ criar()         │  │+ completarDados()│  │(4 total)    │   │   │
│  │  │+ listar()        │  │+ chamarIA()      │  │             │   │   │
│  │  │+ atualizar()     │  │+ parseResult()   │  │+ PanacheRep │   │   │
│  │  └──────────────────┘  └──────────────────┘  └─────────────┘   │   │
│  │                                                                  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                           │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │              DATA ACCESS LAYER (Panache ORM)                     │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │   │
│  │  │UsuarioRepository                 │LevantamentoRep│           │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘           │   │
│  │  ┌──────────────┐  ┌──────────────┐                             │   │
│  │  │IndividuoRep   │  │ImagemRep     │                             │   │
│  │  └──────────────┘  └──────────────┘                             │   │
│  │                                                                  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                           │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │              SECURITY & AUTHENTICATION                           │   │
│  │  ┌──────────────────────────────────────────────────────────┐   │   │
│  │  │  JWT Tokens (RS256)  │  Bcrypt Hash  │  Bearer Tokens  │   │   │
│  │  └──────────────────────────────────────────────────────────┘   │   │
│  │                                                                  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                           │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │            EXTERNAL INTEGRATIONS                                 │   │
│  │  ┌──────────────────────────────────────────────────────────┐   │   │
│  │  │  Groq API (LLaMA 3.3 70B)  │  AIService.java          │   │   │
│  │  │  - Análise de imagens                                  │   │   │
│  │  │  - Identificação de flora                              │   │   │
│  │  │  - Vision + JSON response                              │   │   │
│  │  └──────────────────────────────────────────────────────────┘   │   │
│  │                                                                  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                           │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │            DOCUMENTATION (OpenAPI/Swagger)                       │   │
│  │  /swagger-ui  │  /openapi  │  Auto-generated from annotations   │   │
│  │                                                                  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
                                 │
                          JDBC / SQL
                                 │
┌────────────────────────────────▼────────────────────────────────────────┐
│              PostgreSQL Database (4 Tables)                             │
│                                                                          │
│  ┌─────────────┐     ┌────────────────┐     ┌──────────────┐           │
│  │ tb_usuario  │◄────┤tb_levantamento │◄────┤tb_individuo  │           │
│  │             │     │                │     │              │           │
│  │ id (PK)     │     │ id (PK)        │     │ id (PK)      │           │
│  │ nome        │     │ nome           │     │ parcela      │           │
│  │ email       │     │ bioma          │     │ nome_popular │           │
│  │ senha       │     │ ciudad         │     │ nome_cientifico         │
│  │ data_criacao│     │ estado         │     │ diametro_caule          │
│  │             │     │ pais           │     │ vivo_morto   │           │
│  │             │     │ usuario_id (FK)│     │ levantamento_id (FK)    │
│  │             │     │                │     │              │           │
│  │             │     │                │     └──────┬───────┘           │
│  └─────────────┘     └────────────────┘            │                   │
│                                                     │                   │
│                                          ┌──────────▼───────────┐       │
│                                          │   tb_imagem         │       │
│                                          │                     │       │
│                                          │ id (PK)             │       │
│                                          │ nome                │       │
│                                          │ dados (BYTEA/BLOB)  │       │
│                                          │ tipo_mime           │       │
│                                          │ data_upload         │       │
│                                          │ individuo_id (FK)   │       │
│                                          │                     │       │
│                                          └─────────────────────┘       │
│                                                                          │
│  ✅ Índices em FK e colunas de busca                                   │
│  ✅ Cascade DELETE ativado                                             │
│  ✅ Liquibase para versionamento                                       │
│  ✅ BYTEA para armazenamento de imagens                                │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

## Fluxo de Requisição

```
┌─────────┐
│ Cliente │
└────┬────┘
     │ HTTP POST /api/usuarios
     │ {"nome": "...", "email": "...", "senha": "..."}
     │
     ▼
┌────────────────────┐
│ UsuarioController  │
│  + criarConta()    │
└────┬───────────────┘
     │
     ▼
┌────────────────────┐
│ UsuarioService     │
│  + validar()       │
│  + hashSenha()     │
│  + persist()       │
└────┬───────────────┘
     │
     ▼
┌────────────────────┐
│ UsuarioRepository  │
│ (Panache)          │
└────┬───────────────┘
     │
     ▼
┌────────────────────┐
│ PostgreSQL         │
│ INSERT INTO tb_usuario
└────┬───────────────┘
     │
     ▼ UsuarioResponse JSON
┌─────────┐
│ Cliente │
└─────────┘
```

## Fluxo de Autenticação

```
┌─────────────────┐
│ Cliente         │
│ Login Request   │
└────────┬────────┘
         │
         ▼
    POST /api/login
    email + senha
         │
         ▼
┌─────────────────┐
│ LoginController │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│ UsuarioService          │
│  1. Buscar por email    │
│  2. Validar senha       │
│     (Bcrypt.matches)    │
│  3. Gerar JWT Token     │
│     (RS256 signing)     │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ TokenResponse           │
│ {                       │
│   token: "...",         │
│   type: "Bearer"        │
│ }                       │
└────────┬────────────────┘
         │
         ▼
┌─────────────────┐
│ Cliente         │
│ Salva token     │
└────────┬────────┘
         │
         │ Próximas requisições:
         │ Header: Authorization: Bearer <token>
         │
         ▼
┌─────────────────────────┐
│ Quarkus Security        │
│ Valida JWT              │
│ Extrai subject (user_id)│
└─────────────────────────┘
```

## Fluxo de Upload de Imagem e Análise com IA

```
┌─────────────────────────────────────────────────────┐
│ Cliente                                             │
│ 1. POST /api/imagens/individuo/1 (com arquivo)     │
│ 2. POST /api/individuos/1/completar-dados-ia       │
└────────────┬────────────────────────────────────────┘
             │
             ▼
    ┌────────────────────┐
    │ImagemController    │
    │  upload()          │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │ImagemService       │
    │  validar()         │
    │  - tipo MIME       │
    │  - tamanho (10MB)  │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │ImagemRepository    │
    │ PERSIST (BLOB)     │ ──────────────┐
    └────────┬───────────┘               │
             │                           │
             ▼                           │
    Resposta ImagemResponse              │
             │                           │
             │ [Cliente aguarda]         │
             │                           │
             ▼                           │
    POST /api/individuos/1/               │
    completar-dados-ia                   │
             │                           │
             ▼                           │
    ┌────────────────────┐               │
    │IndividuoIAController               │
    └────────┬───────────┘               │
             │                           │
             ▼                           │
    ┌────────────────────┐               │
    │IndividuoIAService  │               │
    │  completarDados()  │               │
    └────────┬───────────┘               │
             │                           │
             ▼ (busca imagens)           │
             └──────────────────────────►┤
                                         │
                                         ▼
                    ┌──────────────────────────────┐
                    │ImagemRepository              │
                    │ findByIndividuo()            │
                    │ Recupera BLOB do BD          │
                    └─────────────┬────────────────┘
                                  │
                                  ▼
                    ┌──────────────────────────────┐
                    │AIService                     │
                    │  consultaEspecieIA()         │
                    │  1. Converte BLOB para temp  │
                    │  2. Envia para Groq API      │
                    │  3. Vision + Contexto local  │
                    │  4. Parse JSON response      │
                    └─────────────┬────────────────┘
                                  │
                    HTTP POST ─────▼──────► Groq API
                    multipart/form-data    (LLaMA 3.3)
                                           │
                                           ▼
                    ◄──── JSON Response ────┘
                    {
                      "taxon": {
                        "nome_comum_confirmado": "...",
                        "especie": "..."
                      }
                    }
                                  │
                                  ▼
                    ┌──────────────────────────────┐
                    │ Parse & Update Individuo     │
                    │  nome_popular = ...          │
                    │  nome_cientifico = ...       │
                    └─────────────┬────────────────┘
                                  │
                                  ▼
                    ┌──────────────────────────────┐
                    │IndividuoRepository PERSIST   │
                    │ UPDATE tb_individuo          │
                    └─────────────┬────────────────┘
                                  │
                                  ▼ Resposta EspecieCompletaResponse
                                ┌─────────────────────────────────────┐
                                │ Cliente                             │
                                │ Dados preenchidos pela IA!          │
                                └─────────────────────────────────────┘
```

## Stack Tecnológico

```
┌─────────────────────────────────────────────────┐
│          Camada de Apresentação                 │
│  OpenAPI/Swagger UI  │  REST API (JSON/XML)    │
└────────────┬──────────────────────────────────────┘
             │
┌────────────▼──────────────────────────────────────┐
│        Quarkus Framework 3.37.4                   │
│  ┌──────────────┐  ┌──────────────┐             │
│  │ JAX-RS/REST  │  │ SmallRye JWT  │             │
│  └──────────────┘  └──────────────┘             │
│  ┌──────────────┐  ┌──────────────┐             │
│  │ Jackson JSON │  │ Quarkus Sec  │             │
│  └──────────────┘  └──────────────┘             │
└────────────┬──────────────────────────────────────┘
             │
┌────────────▼──────────────────────────────────────┐
│        Hibernate ORM + Panache                    │
│  Active Record Pattern + Repository Pattern       │
└────────────┬──────────────────────────────────────┘
             │
┌────────────▼──────────────────────────────────────┐
│     PostgreSQL JDBC Driver                        │
│  Connection Pooling  │  Transaction Management   │
└────────────┬──────────────────────────────────────┘
             │
┌────────────▼──────────────────────────────────────┐
│        PostgreSQL Database 12+                    │
│  ┌─────┐  ┌──────────┐  ┌──────────┐  ┌────────┐│
│  │user │  │levantment│  │individuo │  │imagem  ││
│  └─────┘  └──────────┘  └──────────┘  └────────┘│
└────────────────────────────────────────────────────┘
             │
┌────────────▼──────────────────────────────────────┐
│        External Services                          │
│  ┌──────────────────────────────────────┐        │
│  │ Groq API (LLaMA 3.3 70B Vision)      │        │
│  │ - Image Analysis                    │        │
│  │ - JSON Mode Response                │        │
│  └──────────────────────────────────────┘        │
└────────────────────────────────────────────────────┘

Dependências Principais:
├── io.quarkus:quarkus-rest (JAX-RS)
├── io.quarkus:quarkus-hibernate-orm-panache
├── io.quarkus:quarkus-jdbc-postgresql
├── io.quarkus:quarkus-security
├── io.smallrye.jwt:smallrye-jwt-build
├── io.quarkus:quarkus-liquibase
├── org.mindrot:jbcrypt (Bcrypt)
└── io.quarkus:quarkus-smallrye-openapi
```

## Padrões de Projeto Utilizados

- **Repository Pattern** - Acesso a dados centralizado
- **Active Record** - Panache simplificando ORM
- **Service Pattern** - Lógica de negócio isolada
- **DTO Pattern** - Request/Response separado de Entity
- **Strategy Pattern** - Diferentes estratégias de validação
- **Singleton** - Services como ApplicationScoped beans
- **Factory** - Criação de tokens JWT
- **Adapter** - Conversão entre Entity e DTO
- **Observer** - Listeners JPA (@PrePersist, @PreUpdate)

