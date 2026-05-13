CREATE TABLE clases (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL UNIQUE,
                        disciplina VARCHAR(100) NOT NULL,
                        capacidad INT NOT NULL,
                        entrenador_id BIGINT NOT NULL
);

