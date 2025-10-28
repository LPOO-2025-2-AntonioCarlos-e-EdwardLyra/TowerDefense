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

    // FPS
    int FPS = 60;

    // Controlador de Rounds
    private int currentRound = 0;
    private int[] enemiesPerRound = {0, 3, 5, 8, 16, 18}; // Round 0 é nulo, começamos do 1
    private long spawnDelay = 1500; // 1.5 segundos entre inimigos
    private long lastSpawnTime = 0;
    private int enemiesSpawnedThisRound = 0;
    private boolean roundInProgress = false;
    private long roundEndTime = 0;
    private boolean allRoundsCompleted = false;
    private long timeBetweenRounds = 5000; // 5 segundos entre rounds

    // Controlador do Estado do Jogo
    private enum GameState { NORMAL, PLACING_TOWER_NORMAL, PLACING_TOWER_SNIPER, GAME_OVER } // Define os estados possíveis do jogo
    private GameState gameState = GameState.NORMAL; // Cria a variável que guarda o estado atual do jogo
    
    Thread gameThread;
    /*
    Essa classe Thread serve para iniciar e parar o jogo e é usada para criar um tempo no jogo, pois um jogo em 2d sempre precisa ser atualizado
    */

    KeyHandler keyH = new KeyHandler();
    MouseHandler mouseH = new MouseHandler();
    MapManager mapManager = new MapManager(this);
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
            allRoundsCompleted = true; // Ativa a trava
            currentRound = enemiesPerRound.length - 1; // Trava o contador no último round (3)
        }
    }

    public void update(){

        if (gameState == GameState.GAME_OVER){
            return; // Não faz mais nada se o jogo acabou
        }

        handleMouse();

        // Controlador de rounds
        if (!roundInProgress && !allRoundsCompleted) {
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
                if (!enemy.active && enemy.health > 0) { // Verifica se o inimigo chegou ao fim do caminho sem ter morrido
                    life--; // Subtrai 1 da variável life
                    if (life <= 0) {
                        life = 0; // Trava a vida em 0 para não ficar negativa
                        gameState = GameState.GAME_OVER; // Se a vida chegar em zero muda o estado do jogo para GAME OVER
                        System.out.println("GAME OVER!"); // Imprime a mensagem de fim de jogo no console
                    }
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
            if (gridRow >= 14) { // Clicou na área da UI
                if (gridCol >= 1 && gridCol <= 2 && coins >= Tower.NORMAL_COST) {
                    gameState = GameState.PLACING_TOWER_NORMAL;
                } else if (gridCol >= 3 && gridCol <= 4 && coins >= Tower.SNIPER_COST) {
                    gameState = GameState.PLACING_TOWER_SNIPER;
                }
            } 

            // 2. Lógica de Posicionamento
            else if (gameState == GameState.PLACING_TOWER_NORMAL) {
                if (canPlaceTower(gridCol, gridRow)) {
                    towers.add(new Tower(this, gridCol, gridRow, Tower.TowerType.NORMAL));
                    coins -= Tower.NORMAL_COST;
                    gameState = GameState.NORMAL;
                }
            } else if (gameState == GameState.PLACING_TOWER_SNIPER) {
                if (canPlaceTower(gridCol, gridRow)) {
                    towers.add(new Tower(this, gridCol, gridRow, Tower.TowerType.SNIPER));
                    coins -= Tower.SNIPER_COST;
                    gameState = GameState.NORMAL;
                }
            }
        }

         if (mouseH.rightClicked) {
            // Cancelar posicionamento com botão direito
            if (gameState != GameState.NORMAL) {
                gameState = GameState.NORMAL;
            } else { // Remover torre
                int gridCol = mouseH.mouseX / tileSize;
                int gridRow = mouseH.mouseY / tileSize;
                removeTowerAt(gridCol, gridRow);
            }
        }
        mouseH.resetClicks(); // Reinicia os cliques após processá-los
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

        // Tem como contruir no local?
        if (!mapManager.isPlaceable(col, row)) {
            return false; // Não pode construir se for caminho(2) ou grama clara(3)
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
                coins += tower.cost / 2; // Devolve metade do valor original da torre
                break;
            }
        }
    }
    
    public void paintComponent(Graphics g){ // É um dos métodos padrão para desenhar coisas no JPanel
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;// Converte o Graphics para a classe Graphics2D(o Graphics2D possui mais funções que o Graphics)

        // Desenha o mapa através do MapManager
        mapManager.draw(g2);

        for (Enemy enemy : enemies) {
            enemy.draw(g2); // Desenha cada inimigo
        }
        for (Tower tower : towers) {
            tower.draw(g2);
        }

        //Desenha a UI e o feedback de posicionamento
        drawUI(g2);
        if (gameState != GameState.NORMAL) {
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

        // Se o jogo acabou, desenha a tela de Game Over por cima de tudo
        if (gameState == GameState.GAME_OVER) {
            drawGameOverScreen(g2);
        }

       
        g2.dispose(); // Boa prática para salvar algumas memórias
    }

    private void drawUI(Graphics2D g2) {
        // Fundo da UI
        g2.setColor(new Color(50, 50, 50));
        g2.fillRect(0, 14 * tileSize, screenWidth, 2 * tileSize);
        g2.setFont(new Font("Arial", Font.BOLD, 12));

        // Botão de compra de torre NORMAL
        g2.setColor(Color.BLUE);
        g2.fillRect(1 * tileSize, 14 * tileSize + tileSize/4, tileSize*2, tileSize);
        g2.setColor(Color.WHITE);
        g2.drawString("Normal", 1 * tileSize + 20, 14 * tileSize + tileSize/4 + 20);
        g2.drawString("$" + Tower.NORMAL_COST, 1 * tileSize + 25, 14 * tileSize + tileSize/4 + 40);

        // Botão de compra de torre SNIPER
        g2.setColor(new Color(0, 100, 0));
        g2.fillRect(3 * tileSize + 10, 14 * tileSize + tileSize/4, tileSize*2, tileSize);
        g2.setColor(Color.WHITE);
        g2.drawString("Sniper", 3 * tileSize + 30, 14 * tileSize + tileSize/4 + 20);
        g2.drawString("$" + Tower.SNIPER_COST, 3 * tileSize + 35, 14 * tileSize + tileSize/4 + 40);
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

    private void drawGameOverScreen(Graphics2D g2) {
        // Desenha um fundo escuro semi-transparente
        g2.setColor(new Color(0, 0, 0, 150)); // Cor preta com transparência
        g2.fillRect(0, 0, screenWidth, screeHeight);

        // Define a fonte e a cor do texto
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        String gameOverText = "GAME OVER";

        // Calcula a posição para centralizar o texto
        int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
        int x = (screenWidth - textWidth) / 2;
        int y = screeHeight / 2;

        // Desenha o texto
        g2.drawString(gameOverText, x, y);
    }
}