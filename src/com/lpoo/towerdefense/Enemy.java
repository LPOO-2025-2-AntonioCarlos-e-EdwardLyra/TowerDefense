package com.lpoo.towerdefense;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;

public class Enemy {

    private GamePanel gp;
    public int x, y;
    private int speed = 1; // Velocidade de movimento em pixels
    public int health;
    public boolean active = true;

    private List<Point> path;
    private int currentWaypointIndex;

    // Variáveis para controlar o movimento de 1 segundo
    

    public Enemy(GamePanel gp) {
        this.gp = gp;
        this.health = 3;
        buildPath();
        setDefaultValues();
    }

    private void buildPath() {
        path = new ArrayList<>();
        
        // Pontos de virada (waypoints) baseados no enemyLayout
        path.add(new Point(gp.tileSize * 0, gp.tileSize * 3));
        path.add(new Point(gp.tileSize * 13, gp.tileSize * 3));
        path.add(new Point(gp.tileSize * 13, gp.tileSize * 6));
        path.add(new Point(gp.tileSize * 1, gp.tileSize * 6));
        path.add(new Point(gp.tileSize * 1, gp.tileSize * 10));
        path.add(new Point(gp.tileSize * 15, gp.tileSize * 10));
    
    }

    public void setDefaultValues() {
        // Posição inicial do inimigo (primeiro ponto do caminho)
        currentWaypointIndex = 0;
        Point startPoint = path.get(currentWaypointIndex);
        x = startPoint.x;
        y = startPoint.y;
        currentWaypointIndex++; // Prepara para se mover para o próximo ponto
    }
    
    public void update() {  
        if (!active) {
            return;
        }
        // Verifica se o inimigo ainda tem um caminho a seguir
        if (currentWaypointIndex < path.size()) {
            Point target = path.get(currentWaypointIndex);

            // Calcula a direção para o próximo waypoint
            int dx = target.x - x;
            int dy = target.y - y;

            // Move na direção X
            if (dx > 0) {
                x += speed;
            } else if (dx < 0) {
                x -= speed;
            }

            // Move na direção Y
            if (dy > 0) {
                y += speed;
            } else if (dy < 0) {
                y -= speed;
            }

            // Calcula a distância até o alvo para verificar se chegou
            double distance = Math.sqrt(Math.pow(target.x - x, 2) + Math.pow(target.y - y, 2));

            // Se o inimigo está perto o suficiente do waypoint, avança para o próximo
            if (distance < speed) {
                x = target.x; // Garante que ele chegue exatamente no ponto
                y = target.y;
                currentWaypointIndex++;
            }
        } else {
                // O inimigo chegou ao final do caminho
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

    

    public void draw(Graphics2D g2) {
        if(!active) {
            return;
        }
        // Cria um triângulo para representar o inimigo
        Polygon triangle = new Polygon();
        triangle.addPoint(x + gp.tileSize / 2, y); // Ponto superior
        triangle.addPoint(x, y + gp.tileSize);     // Ponto inferior esquerdo
        triangle.addPoint(x + gp.tileSize, y + gp.tileSize); // Ponto inferior direito

        g2.setColor(Color.RED);
        g2.fill(triangle);

        // Desenha a vida do inimigo
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString(String.valueOf(health), x + gp.tileSize / 2 - 4, y + gp.tileSize / 2 + 4);
    }
}