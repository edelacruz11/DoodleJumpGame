package com.mygdx.jump;

public class Player {
    // Posición, velocidad, estado (PREPARE, JUMP, FALLING)
    public Player() {
        // Inicializar valores por defecto
    }

    public void moveLeft() {
        // Mover lateralmente a la izquierda
    }

    public void moveRight() {
        // Mover lateralmente a la derecha
    }

    public void startDrop() {
        // Activar caída rápida
    }

    public void update(float delta) {
        // Física: gravedad, saltos automáticos, detección de plataforma
    }

    public void render(/* SpriteBatch batch */) {
        // Dibujar el sprite correcto (front, jump o duck)
    }
}
