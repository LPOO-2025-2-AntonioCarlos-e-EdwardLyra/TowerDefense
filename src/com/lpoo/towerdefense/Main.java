package com.lpoo.towerdefense;

import javax.swing.JFrame; // O JFrame é a janela principal

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame(); // Usamos o JFrame para criar uma janela
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Comando para poder fechar a janela corretamente
        window.setResizable(false); // Para não poder redimensionar a janela
        window.setTitle("BOAR X HUNTER"); // Título do jogo

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel); // Adiciona este gamePanel a esta janela
        window.pack();

        window.setLocationRelativeTo(null); // A janela será exibida no centro da tela
        window.setVisible(true); // Torna a janela visível

        gamePanel.startGameThread();

        
    }
}
