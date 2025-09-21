package com.lpoo.towerdefense;

import javax.swing.JPanel; // É a área de conteúdo dentro da janela
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    // Configurações de tela

    final int originalTileSize = 16; // Esté vai ser o tamanho padrão de objetos do jogo como alguns inimigos

    final int scale = 3;

    final int tileSize = originalTileSize * scale; // 48x48 = Tamanho real dos objetos que vão ser exibidos na tela do jogo
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // (width = largura) = 48 x 16 = 768 pixels
    final int screeHeight = tileSize * maxScreenRow;// (height = altura) = 48 x 12 = 576 pixels

    Thread gameThread;
    /*
    Essa classe Thread serve para iniciar e parar o jogo e é usada para criar um tempo no jogo, pois um jogo em 2d sempre precisa ser atualizado
    */

    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth, screeHeight)); // Define o tamanho do painel deste GamePanel
        this.setBackground(Color.black); // Define uma cor de fundo para a janela
        this.setDoubleBuffered(true);
    }

    public void startGameThread(){

        gameThread = new Thread(this); // Passa a classe GamePanel para o construtor deste thread
        gameThread.start();
    }

    @Override
    public void run() { // Local onde vai ser criado o Loop do jogo

        while(gameThread != null){ // Enquanto o "gameThread" existir/for diferente de null o que está dentro das chaves vai ficar num loop

            // 1 UPDATE: Atualiza informações como as posições dos personagens
            update();
            // 2 DRAW: Desenha a tela com as informações atualizadas
            repaint(); // É dessa forma que se chama o paintComponent
        }
    }
    public void update(){ // O void=vazio significa que o comando vai executar uma ação, mas não vai retornar nenhum valor

    }
    public void paintComponent(Graphics g){ // É um dos métodos padrão para desenhar coisas no JPanel

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;// Converte o Graphics para a classe Graphics2D(o Graphics2D possui mais funções que o Graphics)

        g2.setColor(Color.white);

        g2.fillRect(100, 100, tileSize, tileSize); // Cria um retângulo de início

        g2.dispose(); // Boa prática para salvar algumas memórias
    }
}
