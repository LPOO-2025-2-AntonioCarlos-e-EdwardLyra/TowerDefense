package com.lpoo.towerdefense;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;

// CLASSE ABSTRATA BASE
public abstract class Enemy implements Drawable {

    // O protected vai ser acessíveis apenas por esta classe e a suas filhas
    protected GamePanel gp;
    public int x, y;
    protected int speed;
    public int health;
    public boolean active = true;

    protected long slowEffectEndTime = 0;
    protected boolean isSlowed = false;
    protected int updateCounter = 0;

    protected List<Point> path;
    protected int currentWaypointIndex;

    public Enemy(GamePanel gp, int speed, int health) {
        this.gp = gp;
        this.speed = speed;
        this.health = health;
        buildPath(); // Cria o caminho assim que o inimigo nasce
        setDefaultValues(); // Posiciona o inimigo no início
    }

    public abstract Color getColor();

    private void buildPath() {
        path = new ArrayList<>();
        path.add(new Point(gp.tileSize * 0, gp.tileSize * 3));
        path.add(new Point(gp.tileSize * 13, gp.tileSize * 3));
        path.add(new Point(gp.tileSize * 13, gp.tileSize * 6));
        path.add(new Point(gp.tileSize * 1, gp.tileSize * 6));
        path.add(new Point(gp.tileSize * 1, gp.tileSize * 10));
        path.add(new Point(gp.tileSize * 15, gp.tileSize * 10));
    }

    public void setDefaultValues() {
        currentWaypointIndex = 0;
        Point startPoint = path.get(currentWaypointIndex);
        x = startPoint.x;
        y = startPoint.y;
        currentWaypointIndex++;
    }

    public void applySlow(long durationMillis) {
        this.isSlowed = true;
        this.slowEffectEndTime = System.currentTimeMillis() + durationMillis;
    }

    public void update() {
        if (isSlowed && System.currentTimeMillis() > slowEffectEndTime) {
            isSlowed = false;
        }
        if (isSlowed) {
            updateCounter++;
            if (updateCounter % 2 != 0) return;
        }
        if (!active) return;

        if (currentWaypointIndex < path.size()) {
            Point target = path.get(currentWaypointIndex);
            int dx = target.x - x;
            int dy = target.y - y;

            if (dx > 0) x += speed;
            else if (dx < 0) x -= speed;
            if (dy > 0) y += speed;
            else if (dy < 0) y -= speed;

            double distance = Math.sqrt(Math.pow(target.x - x, 2) + Math.pow(target.y - y, 2));
            if (distance < speed) {
                x = target.x;
                y = target.y;
                currentWaypointIndex++;
            }
        } else {
            active = false;
        }
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            active = false;
            gp.coins += 3; 
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if(!active) return;

        // Cria o formato do inimigo
        Polygon triangle = new Polygon();
        triangle.addPoint(x + gp.tileSize / 2, y);
        triangle.addPoint(x, y + gp.tileSize);
        triangle.addPoint(x + gp.tileSize, y + gp.tileSize);

        g2.setColor(this.getColor()); 
        
        g2.fill(triangle);

        // Desenha a vida sobre o inimigo
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString(String.valueOf(health), x + gp.tileSize / 2 - 4, y + gp.tileSize / 2 + 4);
    }
}