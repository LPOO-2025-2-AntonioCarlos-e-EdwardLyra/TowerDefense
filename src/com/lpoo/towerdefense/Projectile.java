package com.lpoo.towerdefense;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Projectile {
    public int x, y;
    private int speed;
    private Enemy target;
    public boolean active = true;
    private int damage = 1;
    private GamePanel gp;

    public Projectile(GamePanel gp, int startX, int startY, Enemy target) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.speed = 5; // Velocidade do projétil
    }

    public void update() {
        if (!active || !target.active) {
            active = false;
            return;
        }

        // Mira no centro do inimigo
        int targetX = target.x + gp.tileSize / 2;
        int targetY = target.y + gp.tileSize / 2;

        // Move em direção ao alvo
        double angle = Math.atan2(targetY - y, targetX - x);
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);

        // Verifica a colisão
        double distance = Point.distance(x, y, targetX, targetY);
        if (distance < speed) {
            target.takeDamage(damage);
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
        if (active) {
            g2.setColor(Color.YELLOW);
            g2.fillOval(x - 3, y - 3, 6, 6); // Desenha um pequeno círculo
        }
    }
}
