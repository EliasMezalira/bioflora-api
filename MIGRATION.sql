-- SQL de migração para BioFlora API
-- Execute estas queries no PostgreSQL para criar as tabelas

-- Tabela de Usuários
CREATE TABLE tb_usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Tabela de Levantamentos
CREATE TABLE tb_levantamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    bioma VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE
);

-- Índice para busca de levantamentos por usuário
CREATE INDEX idx_levantamento_usuario_id ON tb_levantamento(usuario_id);

-- Tabela de Indivíduos
CREATE TABLE tb_individuo (
    id BIGSERIAL PRIMARY KEY,
    parcela VARCHAR(50) NOT NULL,
    nome_popular VARCHAR(100),
    nome_cientifico VARCHAR(200),
    diametro_caule DOUBLE PRECISION,
    vivo_morto VARCHAR(10) NOT NULL,
    data_levantamento TIMESTAMP NOT NULL,
    levantamento_id BIGINT NOT NULL,
    FOREIGN KEY (levantamento_id) REFERENCES tb_levantamento(id) ON DELETE CASCADE
);

-- Índice para busca de indivíduos por levantamento
CREATE INDEX idx_individuo_levantamento_id ON tb_individuo(levantamento_id);

-- Tabela de Imagens
CREATE TABLE tb_imagem (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    dados BYTEA NOT NULL,
    tipo_mime VARCHAR(50) NOT NULL,
    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    individuo_id BIGINT NOT NULL,
    FOREIGN KEY (individuo_id) REFERENCES tb_individuo(id) ON DELETE CASCADE
);

-- Índice para busca de imagens por indivíduo
CREATE INDEX idx_imagem_individuo_id ON tb_imagem(individuo_id);

-- Criar índices de busca
CREATE INDEX idx_usuario_email ON tb_usuario(email);
CREATE INDEX idx_levantamento_data_criacao ON tb_levantamento(data_criacao);
CREATE INDEX idx_individuo_data_levantamento ON tb_individuo(data_levantamento);
