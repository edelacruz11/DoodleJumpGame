package com.mygdx.jump;

public class DropButton {
    // Rectángulo en la parte inferior-centro de la pantalla
    // Texturas drop-on y drop-off

    public DropButton() {
        // Calcular posición y cargar Textures
    }

    public boolean hit(int screenX, int screenY) {
        // Devolver true si el toque cae dentro del botón
        return false;
    }

    public void render(/* SpriteBatch batch */) {
        // Dibujar drop-on o drop-off según esté presionado
    }
}
