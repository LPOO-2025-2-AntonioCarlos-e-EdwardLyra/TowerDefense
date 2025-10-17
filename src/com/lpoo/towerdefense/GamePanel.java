package com.lpoo.towerdefense;

import javax.swing.JPanel; // É a área de conteúdo dentro da janela
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    // Configurações de tela

    final int originalTileSize = 16; // Esté vai ser o tamanho padrão de objetos do jogo como alguns inimigos

    final int scale = 3;

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
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    
    // FPS
    int FPS = 60;


    Thread gameThread;
    /*
    Essa classe Thread serve para iniciar e parar o jogo e é usada para criar um tempo no jogo, pois um jogo em 2d sempre precisa ser atualizado
    */

    KeyHandler keyH = new KeyHandler();
    Enemy enemy; // Cria um inimigo
     Tower tower; // Cria uma torre

    //Variáveis para posições e velocidade do player/teste
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;
    /*
    O jogo não vai ter player e também não vai ter como controlar os inimigos, mas essas variáveis vão ser criadas para testar a funcionalidade do Key Input
     */

    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth, screeHeight)); // Define o tamanho do painel deste GamePanel
        this.setBackground(Color.black); // Define uma cor de fundo para a janela
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        enemy = new Enemy(this); // Cria um inimigo
        tower = new Tower(this, 7, 8); // Cria uma torre na posição 7,7
    }

    public void startGameThread(){

        gameThread = new Thread(this); // Passa a classe GamePanel para o construtor deste thread
        gameThread.start();
    }

    @Override
    public void run() { // Local onde vai ser criado o Loop do jogo

        while(gameThread != null){ // Enquanto o "gameThread" existir/for diferente de null o que está dentro das chaves vai ficar num loop

            double drawInterval = (double) 1000000000 /FPS; // A tela vai ser atualizada 60 vezes por segundo = atualizada a cada 0,01666 segundos
            double nextDrawTime = System.nanoTime() + drawInterval; // Define quando deve ocorrer o próximo frame

            // 1 UPDATE: Atualiza informações como as posições dos personagens
            update();

            // 2 DRAW: Desenha a tela com as informações atualizadas
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();

                remainingTime = remainingTime/1000000; // O sleep só aceita milissegundos, logo devemos converter o remaininTime para milissegundos

                if(remainingTime < 0){ // Se o update e o repaint demorarem mais do que o drawInterval não vai existir tempo restante
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime); // Faz o Thread dormir pelo tempo que restou fazendo com que a execução continue no momento certo do próximo frame

                nextDrawTime = nextDrawTime + drawInterval;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void update(){ // O void(vazio) significa que o comando vai executar uma ação, mas não vai retornar nenhum valor

        if(keyH.upPressed){
            playerY -= playerSpeed; // playerY = playerY - playerSpeed;
        }
        else if(keyH.downPressed) {
            playerY += playerSpeed;
        }
        else if (keyH.leftPressed) {
            playerX -= playerSpeed;
        }
        else if (keyH.rightPressed) {
            playerX += playerSpeed;
        }

        if (enemy.active) {
            enemy.update(); // Atualiza o inimigo
            if (!enemy.active  && enemy.health > 0) { // Se o inimigo se tornou inativo nesta atualização (chegou ao fim)
                life--;
            }
        }

        tower.update(enemy); // Atualiza a torre e seus projéteis 
    }
    public void paintComponent(Graphics g){ // É um dos métodos padrão para desenhar coisas no JPanel

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;// Converte o Graphics para a classe Graphics2D(o Graphics2D possui mais funções que o Graphics)

        // Desenha o fundo do percurso
        g2.setColor(Color.white);
        g2.setFont(new Font("Monospaced", Font.PLAIN, tileSize));

        for(int row = 0; row < maxScreenRow; row++){
            for(int col = 0; col < maxScreenCol; col++){
                if(mapLayout[row][col] == 1){
                    // Desenha o caractere '@' na posição do mapa
                    g2.drawString("@", col * tileSize, (row + 1) * tileSize - (tileSize/4));
                }
            }
        }
        // Desenha o quadrado branco móvel por cima do fundo
        g2.setColor(Color.white);
        g2.fillRect(playerX, playerY, tileSize, tileSize); // Cria um retângulo de início
        
        enemy.draw(g2); // Desenha o inimigo
        tower.draw(g2); // Desenha a torre

        // Desenha o contador de vida
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String lifeText = "Life: " + life;
        g2.drawString(lifeText, screenWidth - 100, 30);

        String coinsText = "Coins: " + coins;
        g2.drawString(coinsText, 20, 30);
        g2.dispose(); // Boa prática para salvar algumas memórias
    }
}
