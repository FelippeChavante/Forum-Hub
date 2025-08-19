-- Inserir usuários de teste
-- Senha: 123456 (BCrypt encoded)
INSERT INTO usuario (nome, email, senha)
VALUES ('Admin', 'admin@forumhub.com',
        '$2a$10$IsX3dtOD59RoMawlIHLGfuswYcm6hn9NgVx2tau/JvPaMd1qnKHEy'),
       ('João Silva', 'joao@forumhub.com',
        '$2a$10$IsX3dtOD59RoMawlIHLGfuswYcm6hn9NgVx2tau/JvPaMd1qnKHEy'),
       ('Maria Santos', 'maria@forumhub.com',
        '$2a$10$IsX3dtOD59RoMawlIHLGfuswYcm6hn9NgVx2tau/JvPaMd1qnKHEy');

-- Associar perfis aos usuários
INSERT INTO usuario_perfil (usuario_id, perfil_id)
VALUES (1, 1), -- Admin tem perfil ROLE_ADMIN
       (1, 2), -- Admin também tem perfil ROLE_USER
       (2, 2), -- João tem perfil ROLE_USER
       (3, 2);
-- Maria tem perfil ROLE_USER

-- Inserir cursos de teste
INSERT INTO curso (nome, categoria)
VALUES ('Spring Boot', 'Programação'),
       ('Java Básico', 'Programação'),
       ('Banco de Dados', 'Infraestrutura'),
       ('DevOps', 'Infraestrutura');

-- Inserir tópicos de teste
INSERT INTO topico (titulo, mensagem, data_criacao, status, autor_id, curso_id)
VALUES ('Dúvida sobre Spring Security', 'Como configurar o Spring Security corretamente?', NOW(),
        'NAO_SOLUCIONADO', 2, 1),
       ('Erro ao executar migrations', 'Estou tendo problemas com o Flyway', NOW(),
        'NAO_RESPONDIDO', 3, 1),
       ('Java vs Kotlin', 'Qual linguagem devo aprender primeiro?', NOW(), 'NAO_SOLUCIONADO', 2, 2),
       ('Problema com Docker', 'Container não está iniciando', NOW(), 'SOLUCIONADO', 3, 4);

-- Inserir respostas de teste
INSERT INTO resposta (mensagem, topico_id, data_criacao, autor_id, solucao)
VALUES ('Você precisa criar uma classe de configuração com @EnableWebSecurity', 1, NOW(), 1, false),
       ('Recomendo começar com Java e depois aprender Kotlin', 3, NOW(), 1, false),
       ('Verifique se as portas não estão em uso por outro processo', 4, NOW(), 2, true);