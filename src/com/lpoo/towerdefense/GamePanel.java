package com.lpoo.towerdefense;

import javax.swing.JPanel; // É a área de conteúdo dentro da janela
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements Runnable{

    // Configurações de tela

    final int originalTileSize = 16; // Esté vai ser o tamanho padrão de objetos do jogo como alguns inimigos

    final int scale = 2;

    final int tileSize = originalTileSize * scale; // 48x48 = Tamanho real dos objetos que vão ser exibidos na tela do jogo
    final int maxScreenCol = 16;
    final int maxScreenRow = 16;
    final int screenWidth = tileSize * maxScreenCol; // (width = largura) = 48 x 16 = 768 pixels
    final int screeHeight = tileSize * maxScreenRow;// (height = altura) = 48 x 12 = 576 pixels
    
    int life = 3;
    int coins = 8;
    
    // Mapa do percurso
    int[][] mapLayout = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
        {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 0},
        {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0},
        {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
        {1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    
    // FPS
    int FPS = 60;

    // Gerenciamento de Rounds
    private int currentRound = 0;
    private int[] enemiesPerRound = {0, 3, 5, 8}; // Round 0 é nulo, começamos do 1
    private long spawnDelay = 1500; // 1.5 segundos entre inimigos
    private long lastSpawnTime = 0;
    private int enemiesSpawnedThisRound = 0;
    private boolean roundInProgress = false;
    private long roundEndTime = 0;
    private long timeBetweenRounds = 5000; // 5 segundos entre rounds

    // Estado do Jogo para compra de torres
    private enum GameState { NORMAL, PLACING_TOWER }
    private GameState gameState = GameState.NORMAL;
    private int towerCost = 8;

    Thread gameThread;
    /*
    Essa classe Thread serve para iniciar e parar o jogo e é usada para criar um tempo no jogo, pois um jogo em 2d sempre precisa ser atualizado
    */

    KeyHandler keyH = new KeyHandler();
    MouseHandler mouseH = new MouseHandler(); 
    List<Enemy> enemies;
    List<Tower> towers;

    //Variáveis para posições e velocidade do player/teste
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;
    /*
    O jogo não vai ter player e também não vai ter como controlar os inimigos, mas essas variáveis vão ser criadas para testar a funcionalidade do Key Input
     */

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth, screeHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH); // Adiciona o listener de mouse
        this.addMouseMotionListener(mouseH); // Adiciona o listener de movimento do mouse
        this.setFocusable(true);
        enemies = new ArrayList<>();
        towers = new ArrayList<>(); // Inicializa a lista de torres
    }

    public void startGameThread(){

        gameThread = new Thread(this); // Passa a classe GamePanel para o construtor deste thread
        gameThread.start();
    }

    @Override
    public void run() { // Local onde vai ser criado o Loop do jogo

        while(gameThread != null){ // Enquanto o "gameThread" existir/for diferente de null o que está dentro das chaves vai ficar num loop

            double drawInterval = (double) 1000000000 /FPS; // A tela vai ser atualizada 60 vezes por segundo = atualizada a cada 0,01666 segundos
            double delta = 0;
            long lastTime = System.nanoTime();
            long currentTime;


            while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if(delta >= 1){
                update(); // 1 ATUALIZAR: atualiza informações como a posição do personagem
                repaint(); // 2 DESENHAR: desenha a tela com as informações atualizadas
                delta--;
            }
        }
    }
} 

private void startNextRound() {
    currentRound++;
    if (currentRound < enemiesPerRound.length) {
        enemiesSpawnedThisRound = 0;
        roundInProgress = true;
        System.out.println("Starting Round " + currentRound);
    } else {
        System.out.println("All rounds completed!");
        // Fim de jogo (vitória)
    }
}

    public void update(){
        handleMouse(); 

        // Gerenciamento de rounds
        if (!roundInProgress) {
            if (System.currentTimeMillis() - roundEndTime > timeBetweenRounds) {
                startNextRound();
            }
        } else {
            if (enemiesSpawnedThisRound < enemiesPerRound[currentRound] && System.currentTimeMillis() - lastSpawnTime > spawnDelay) {
                enemies.add(new Enemy(this));
                enemiesSpawnedThisRound++;
                lastSpawnTime = System.currentTimeMillis();
            }
        }
        
        // Atualiza todos os inimigos e remove os inativos
        enemies.removeIf(enemy -> !enemy.active);
        boolean anyEnemyActive = false;
        for (Enemy enemy : enemies) {
            if (enemy.active) {
                enemy.update();
                if (!enemy.active && enemy.health > 0) { // Chegou ao fim
                    life--;
                }
                anyEnemyActive = true;
            }
        }

         // Verifica se o round terminou
        if (roundInProgress && enemiesSpawnedThisRound == enemiesPerRound[currentRound] && !anyEnemyActive) {
            roundInProgress = false;
            roundEndTime = System.currentTimeMillis();
            System.out.println("Round " + currentRound + " completed!");
        }

        for (Tower tower : towers) {
            tower.update(enemies);
        } 
    }

    private void handleMouse() {
        if (mouseH.leftClicked) {
            int mouseX = mouseH.mouseX;
            int mouseY = mouseH.mouseY;
            int gridCol = mouseX / tileSize;
            int gridRow = mouseY / tileSize;

            // 1. Lógica de Compra na UI
            // A UI está na linha 15 (índice 14)
            if (gridRow == 14 && gridCol >= 0 && gridCol <= 2) {
                if (coins >= towerCost) {
                    gameState = GameState.PLACING_TOWER;
                }
            }
            
            // 2. Lógica de Posicionamento
            else if (gameState == GameState.PLACING_TOWER) {
                if (canPlaceTower(gridCol, gridRow)) {
                    towers.add(new Tower(this, gridCol, gridRow));
                    coins -= towerCost;
                    gameState = GameState.NORMAL;
                }
            }
        }

        if (mouseH.rightClicked) {
            // Cancelar posicionamento com botão direito
            if (gameState == GameState.PLACING_TOWER) {
                gameState = GameState.NORMAL;
            } else { // Remover torre
                int gridCol = mouseH.mouseX / tileSize;
                int gridRow = mouseH.mouseY / tileSize;
                removeTowerAt(gridCol, gridRow);
            }
        }
        mouseH.resetClicks(); // Reseta os cliques após processá-los
    }
        
    private boolean canPlaceTower(int col, int row) {
        // Fora dos limites do mapa?
        if (col < 0 || col >= maxScreenCol || row < 0 || row >= maxScreenRow) {
            return false;
        }
        // Está na área da UI? (últimas 2 linhas)
        if (row >= 14) {
            return false;
        }
        // Está no caminho do inimigo?
        if (mapLayout[row][col] >= 1) {
            return false;
        }
        // Já existe uma torre lá?
        for (Tower tower : towers) {
            if (tower.col == col && tower.row == row) {
                return false;
            }
        }
        return true;
    }
    
    private void removeTowerAt(int col, int row) {
        Iterator<Tower> iterator = towers.iterator();
        while (iterator.hasNext()) {
            Tower tower = iterator.next();
            if (tower.col == col && tower.row == row) {
                iterator.remove();
                coins += towerCost / 2; // Devolve metade do valor
                break;
            }
        }
    }
    
    public void paintComponent(Graphics g){ // É um dos métodos padrão para desenhar coisas no JPanel
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;// Converte o Graphics para a classe Graphics2D(o Graphics2D possui mais funções que o Graphics)

        // Desenha o fundo do percurso
         g2.setColor(new Color(100, 100, 100));
        for(int row = 0; row < maxScreenRow; row++){
            for(int col = 0; col < maxScreenCol; col++){
                if(mapLayout[row][col] == 1){
                    g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
            }
        }
        
        for (Enemy enemy : enemies) {
            enemy.draw(g2); // Desenha cada inimigo
        }
        for (Tower tower : towers) {
            tower.draw(g2);
        }

        //Desenha a UI e o feedback de posicionamento
        drawUI(g2);
        if (gameState == GameState.PLACING_TOWER) {
            drawPlacementPreview(g2);
        }
        
        // Desenha o contador de vida
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String lifeText = "Life: " + life;
        g2.drawString(lifeText, screenWidth - 100, 30);

        String coinsText = "Coins: " + coins;
        g2.drawString(coinsText, 20, 30);
       
         // Desenha o round atual
        String roundText = "Round: " + currentRound;
        g2.drawString(roundText, screenWidth / 2 - 50, 30);
        
       
        g2.dispose(); // Boa prática para salvar algumas memórias
    }

    private void drawUI(Graphics2D g2) {
        // Fundo da UI
        g2.setColor(new Color(50, 50, 50));
        g2.fillRect(0, 14 * tileSize, screenWidth, 2 * tileSize);

        // Botão de compra de torre
        g2.setColor(Color.BLUE);
        g2.fillRect(1 * tileSize, 14 * tileSize + tileSize/4, tileSize, tileSize);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Torre", 1 * tileSize + 10, 14 * tileSize + tileSize/4 + 28);
        g2.drawString("$" + towerCost, 1 * tileSize + 12, 14 * tileSize + tileSize/4 + 42);
    }

    private void drawPlacementPreview(Graphics2D g2) {
        int gridCol = mouseH.mouseX / tileSize;
        int gridRow = mouseH.mouseY / tileSize;

        if (canPlaceTower(gridCol, gridRow)) {
            g2.setColor(new Color(0, 255, 0, 100)); // Verde transparente
        } else {
            g2.setColor(new Color(255, 0, 0, 100)); // Vermelho transparente
        }
        g2.fillRect(gridCol * tileSize, gridRow * tileSize, tileSize, tileSize);
    }
}
