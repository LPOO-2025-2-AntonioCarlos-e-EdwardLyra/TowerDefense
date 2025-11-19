package com.lpoo.towerdefense;

import java.awt.Color;
import java.awt.Graphics2D;

public class MapManager implements Drawable {

    GamePanel gp;

    // Define as cores finais dos blocos do mapa
    final Color PATH_COLOR = new Color(139, 69, 19); // Marrom
    final Color PLACEABLE_GRASS_COLOR = new Color(34, 139, 34); // Verde Escuro
    final Color DECORATION_GRASS_COLOR = new Color(50, 205, 50); // Verde Claro

    // O layout do mapa
    int[][] mapLayout = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0},
            {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 0},
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 0},
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 0},
            {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 0},
            {3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0},
            {3, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
            {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // Construtor
    public MapManager(GamePanel gp) {
        this.gp = gp;
    }

    // Desenha o mapa com caminhos e gramas na tela
    @Override
    public void draw(Graphics2D g2) {

        for (int row = 0; row < gp.maxScreenRow; row++) {
            for (int col = 0; col < gp.maxScreenCol; col++) {

                // Pula a área da UI (User Interface = Interface de Usuário)
                if (row >= 14) {
                    continue;
                }

                int tileType = mapLayout[row][col];

                if (tileType == 2) {
                    // 2 = Caminho de terra
                    g2.setColor(PATH_COLOR);
                } else if (tileType == 3) {
                    // 3 = Grama Clara
                    g2.setColor(DECORATION_GRASS_COLOR);
                } else {
                    // 0 = Grama Escura (Local onde pode construir as torres)
                    g2.setColor(PLACEABLE_GRASS_COLOR);
                }

                // Desenha o retângulo preenchido na posição correta
                g2.fillRect(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
            }
        }
    }

    // Verifica se um tile específico no mapa pode ser um local para construir uma torre
    public boolean isPlaceable(int col, int row) {
        // Verifica se está dentro dos limites do array
        if (col < 0 || col >= gp.maxScreenCol || row < 0 || row >= gp.maxScreenRow) {
            return false; // Fora dos limites não pode construir
        }
        // Retorna true APENAS se o tile for 0 (Grama Escura)
        return mapLayout[row][col] == 0;
    }
}