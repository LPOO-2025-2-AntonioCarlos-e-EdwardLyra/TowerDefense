package com.lpoo.towerdefense;

import java.awt.Color;

public class Javali extends Enemy {
    public Javali(GamePanel gp) {
        // Apenas os valores, na ordem: GamePanel, velocidade, vida
        super(gp, 1, 3); 
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}