package com.lpoo.towerdefense;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    public int mouseX, mouseY;
    public boolean leftClicked;
    public boolean rightClicked;

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Botão esquerdo
            leftClicked = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) { // Botão direito
            rightClicked = true;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // Método para resetar o estado do clique após ser processado no loop do jogo
    public void resetClicks() {
        leftClicked = false;
        rightClicked = false;
    }
}