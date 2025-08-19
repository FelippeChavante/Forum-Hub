-- Removendo a restrição de unicidade para o nome do curso, pois já existe
-- ou será adicionada automaticamente pelo JPA

-- Adicionar restrição de unicidade para título do tópico
ALTER TABLE topico
    ADD CONSTRAINT UK_topico_titulo UNIQUE (titulo);

-- Adicionar restrição ON DELETE para as chaves estrangeiras do tópico
-- Não precisamos remover as chaves estrangeiras existentes, apenas adicionamos as novas com nomes específicos
ALTER TABLE topico
    ADD CONSTRAINT FK_topico_autor FOREIGN KEY (autor_id) REFERENCES usuario (id) ON DELETE RESTRICT;
ALTER TABLE topico
    ADD CONSTRAINT FK_topico_curso FOREIGN KEY (curso_id) REFERENCES curso (id) ON DELETE RESTRICT;

-- Adicionar restrição ON DELETE para a chave estrangeira do autor na resposta
ALTER TABLE resposta
    ADD CONSTRAINT FK_resposta_autor FOREIGN KEY (autor_id) REFERENCES usuario (id) ON DELETE RESTRICT;

-- Adicionar índices para melhorar a performance de consultas frequentes
CREATE INDEX IDX_topico_data_criacao ON topico (data_criacao);
CREATE INDEX IDX_topico_status ON topico (status);
CREATE INDEX IDX_curso_categoria ON curso (categoria);