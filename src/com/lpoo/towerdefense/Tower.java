package com.lpoo.towerdefense;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Tower {

    private int x, y;
    private GamePanel gp;
    private int attackRange = 180; // Raio de ataque da torre
    private long fireRate = 2000; // Atira a cada 2000ms (2 segundos)
    private long lastFireTime = 0;
    private List<Projectile> projectiles;

    public Tower(GamePanel gp, int col, int row) {
        this.gp = gp;
        this.x = col * gp.tileSize;
        this.y = row * gp.tileSize;
        this.projectiles = new ArrayList<>();
    }

    public void update(Enemy enemy) {
        // Atualiza os projéteis existentes e remove os inativos
        projectiles.removeIf(p -> !p.active);
        for (Projectile p : projectiles) {
            p.update();
        }

        // Verifica se o inimigo está no alcance e se pode atirar
        if (enemy.active && System.currentTimeMillis() - lastFireTime >= fireRate) {
            int towerCenterX = x + gp.tileSize / 2;
            int towerCenterY = y + gp.tileSize / 2;
            double distance = Math.sqrt(Math.pow(enemy.x - towerCenterX, 2) + Math.pow(enemy.y - towerCenterY, 2));

            if (distance <= attackRange) {
                // Atira no inimigo
                projectiles.add(new Projectile(gp, towerCenterX, towerCenterY, enemy));
                lastFireTime = System.currentTimeMillis();
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        // Desenha os projéteis
        for (Projectile p : projectiles) {
            p.draw(g2);
        }
    }
}