package com.zetcode;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class PewPewPew extends JFrame {

    public PewPewPew() {
        
        initUI();
    }
    
    private void initUI() {
        
        add(new Board());
        
        setResizable(false);
        pack();
        
        setTitle("PewPewPew");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PewPewPew ex = new PewPewPew();
                ex.setVisible(true);
            }
        });
    }
}