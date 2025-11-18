package com.lpoo.towerdefense;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Projectile {
    public int x, y;
    private int speed;
    private Enemy target;
    public boolean active = true;
    private int damage;
    private GamePanel gp;
    private boolean appliesSlow;

    public Projectile(GamePanel gp, int startX, int startY, Enemy target, int damage, boolean appliesSlow) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.speed = 5;
        this.damage = damage;
        this.appliesSlow = appliesSlow;
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

        // Verifica a colisão e também pode aplicar o slow
        double distance = Point.distance(x, y, targetX, targetY);
        if (distance < speed) {
            target.takeDamage(damage);
            if (appliesSlow) {
                target.applySlow(1500); // Aplica o slow por 1.5 segundos
            }
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
