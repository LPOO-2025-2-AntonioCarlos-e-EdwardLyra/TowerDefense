package com.lpoo.towerdefense;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Tower {

    public enum TowerType {
        NORMAL, SNIPER
    }

    public int x, y;
    public int col, row;
    private GamePanel gp;
    private List<Projectile> projectiles;

    // Atributos da Torre
    private TowerType type;
    public int cost;
    private int attackRange;
    private long fireRate;
    private int damage;
    private long lastFireTime = 0;

    // Custos estáticos para acesso externo
    public static final int NORMAL_COST = 8;
    public static final int SNIPER_COST = 11;

    public Tower(GamePanel gp, int col, int row, TowerType type) {
        this.gp = gp;
        this.col = col;
        this.row = row;
        this.x = col * gp.tileSize;
        this.y = row * gp.tileSize;
        this.projectiles = new ArrayList<>();
        this.type = type;

        // Define os atributos com base no tipo
        switch (type) {
            case NORMAL:
                this.cost = NORMAL_COST;
                this.attackRange = 180; // Raio de 5-6 quadrados
                this.fireRate = 2000; // 2 segundos
                this.damage = 1;
                break;
            case SNIPER:
                this.cost = SNIPER_COST;
                this.attackRange = 288; // Raio de 9 quadrados (16*2*9)
                this.fireRate = 3500; // 3,5 segundos
                this.damage = 2;
                break;
        }
    }
            

    public void update(List<Enemy> enemies) {
        // Atualiza os projéteis existentes e remove os inativos
        projectiles.removeIf(p -> !p.active);
        for (Projectile p : projectiles) {
            p.update();
        }

        // Se não puder atirar, não faz nada
        if (System.currentTimeMillis() - lastFireTime < fireRate) {
            return;
        }

        Enemy target = findTarget(enemies);
        if (target != null) {
            int towerCenterX = x + gp.tileSize / 2;
            int towerCenterY = y + gp.tileSize / 2;

            // 1. Adicione esta linha para verificar se a torre é Sniper
            boolean shouldSlow = (this.type == TowerType.SNIPER);

            // 2. Modifique a linha 74 para passar 'shouldSlow' como o 6º argumento
            projectiles.add(new Projectile(gp, towerCenterX, towerCenterY, target, this.damage, shouldSlow));

            lastFireTime = System.currentTimeMillis();
        }
    }

    private Enemy findTarget(List<Enemy> enemies) {
        Enemy target = null;
        int towerCenterX = x + gp.tileSize / 2;
        int towerCenterY = y + gp.tileSize / 2;

        if (type == TowerType.SNIPER) {
            // Lógica da Sniper: Acha o inimigo com mais vida no alcance
            int maxHealth = -1;
            for (Enemy enemy : enemies) {
                if (enemy.active) {
                    double distance = Math.hypot(enemy.x - towerCenterX, enemy.y - towerCenterY);
                    if (distance <= attackRange && enemy.health > maxHealth) {
                        maxHealth = enemy.health;
                        target = enemy;
                    }
                }
            }
        } else {
            // Lógica Normal: Acha o primeiro inimigo no alcance
            for (Enemy enemy : enemies) {
                if (enemy.active) {
                    double distance = Math.hypot(enemy.x - towerCenterX, enemy.y - towerCenterY);
                    if (distance <= attackRange) {
                        target = enemy;
                        break;
                    }
                }
            }
        }
        return target;
    }

    public void draw(Graphics2D g2) {
        // Cor baseada no tipo
        g2.setColor(type == TowerType.NORMAL ? Color.BLUE : new Color(0, 100, 0)); // Azul ou Verde Escuro
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        for (Projectile p : projectiles) {
            p.draw(g2);
        }
    }
}