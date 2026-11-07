CREATE DATABASE IF NOT EXISTS rpg_arena_db;
USE rpg_arena_db;

CREATE TABLE IF NOT EXISTS jugadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    rol ENUM('jugador', 'admin') NOT NULL DEFAULT 'jugador'
);

CREATE TABLE IF NOT EXISTS monstruos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    vida INT NOT NULL,
    ataque INT NOT NULL
);

CREATE TABLE IF NOT EXISTS victorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    jugador_id INT NOT NULL,
    monstruo_id INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (jugador_id) REFERENCES jugadores(id) ON DELETE CASCADE,
    FOREIGN KEY (monstruo_id) REFERENCES monstruos(id)
);

INSERT INTO jugadores (usuario, clave, rol) VALUES
    ('admin', 'admin123', 'admin'),
    ('thor', '1234', 'jugador');

INSERT INTO monstruos (nombre, vida, ataque) VALUES
    ('Goblin', 40, 8),
    ('Orco', 80, 14),
    ('Dragon', 200, 30);