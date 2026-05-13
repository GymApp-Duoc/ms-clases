CREATE TABLE clases (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL UNIQUE,
                        disciplina VARCHAR(100) NOT NULL,
                        capacidad INT NOT NULL,
                        entrenador_id BIGINT NOT NULL
);

INSERT INTO clases (nombre, disciplina, capacidad, entrenador_id) VALUES ('CrossFit Elite AM', 'CrossFit', 15, 1);
INSERT INTO clases (nombre, disciplina, capacidad, entrenador_id) VALUES ('Yoga Relax PM', 'Yoga', 20, 2);
INSERT INTO clases (nombre, disciplina, capacidad, entrenador_id) VALUES ('Spinning Pro', 'Spinning', 12, 1);

