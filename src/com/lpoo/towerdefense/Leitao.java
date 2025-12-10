package com.lpoo.towerdefense;
import java.awt.Color;

public class Leitao extends Enemy {
    public Leitao(GamePanel gp) {
        super(gp, 2, 3);
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }
}