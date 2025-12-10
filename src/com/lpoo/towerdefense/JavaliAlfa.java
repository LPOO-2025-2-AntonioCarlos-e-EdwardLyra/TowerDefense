package com.lpoo.towerdefense;
import java.awt.Color;

public class JavaliAlfa extends Enemy {
    public JavaliAlfa(GamePanel gp) {
        super(gp, 1, 12);
    }

    @Override
    public Color getColor() {
        return new Color(75, 0, 130);
    }
}