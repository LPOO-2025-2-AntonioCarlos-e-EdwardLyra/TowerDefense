package com.lpoo.towerdefense;
import java.awt.Color;

public class JavaliAlfa extends Enemy {
    public JavaliAlfa(GamePanel gp) {
        super(gp, 1, 10); // Velocidade 1, Vida 10
    }

    @Override
    public Color getColor() {
        return new Color(75, 0, 130); // Roxo
    }
}