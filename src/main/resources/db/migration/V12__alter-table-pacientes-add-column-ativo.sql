ALTER TABLE pacientes ADD ativo tinyint;
UPDATE pacientes SET ativo = 1;
ALTER TABLE pacientes MODIFY ativo tinyint NOT NULL;