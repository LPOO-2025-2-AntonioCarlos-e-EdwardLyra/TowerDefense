package com.lpoo.towerdefense;

import java.awt.Color;

public class Javali extends Enemy {
    public Javali(GamePanel gp) {
        super(gp, 1, 4);
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}