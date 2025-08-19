-- Criação da tabela de perfis
CREATE TABLE perfil
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- Criação da tabela de usuários
CREATE TABLE usuario
(
    id    INT AUTO_INCREMENT PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- Criação da tabela de relacionamento entre usuários e perfis
CREATE TABLE usuario_perfil
(
    usuario_id INT NOT NULL,
    perfil_id  INT NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE
);

-- Criação da tabela de cursos
CREATE TABLE curso
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    categoria VARCHAR(100) NOT NULL
);

-- Criação da tabela de tópicos
CREATE TABLE topico
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    titulo       VARCHAR(100) NOT NULL,
    mensagem     TEXT         NOT NULL,
    data_criacao DATETIME     NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    autor_id     INT          NOT NULL,
    curso_id     INT          NOT NULL,
    FOREIGN KEY (autor_id) REFERENCES usuario (id),
    FOREIGN KEY (curso_id) REFERENCES curso (id)
);

-- Criação da tabela de respostas
CREATE TABLE resposta
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    mensagem     TEXT     NOT NULL,
    topico_id    INT      NOT NULL,
    data_criacao DATETIME NOT NULL,
    autor_id     INT      NOT NULL,
    solucao      BOOLEAN  NOT NULL DEFAULT false,
    FOREIGN KEY (topico_id) REFERENCES topico (id) ON DELETE CASCADE,
    FOREIGN KEY (autor_id) REFERENCES usuario (id)
);

-- Inserção de perfis padrão
INSERT INTO perfil (nome)
VALUES ('ROLE_ADMIN');
INSERT INTO perfil (nome)
VALUES ('ROLE_USER');