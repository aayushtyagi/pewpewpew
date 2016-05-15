package com.swag;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private Shooter craft;
    private ArrayList<Trump> trumps;
    private boolean gameover;
    private boolean gamestarted = false;
    private int spawnRate = 100; //check difference between frameatlastspawn and 
    private int frameAtLastSpawn = 0;
    private int currentFrame = 0;
    private int score = 0;
    private final int STARTING_AMMO = 30;
    private final int TRUMP_SIZE = 32;
    private final int B_WIDTH = 1500;
    private final int B_HEIGHT = 700;
    private final int ICRAFT_X = B_WIDTH/2;
    private final int ICRAFT_Y = B_HEIGHT-TRUMP_SIZE;
    private final int DELAY = 15;
    private final boolean KILL_DEATH_EXTRAVAGANZA = false;

    /**
     * Constructor - see initBoard()
     */
    public Board() { initBoard(); }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);
        gameover = false;
        gamestarted = false;
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        if (KILL_DEATH_EXTRAVAGANZA) { spawnRate = 1; }
        craft = new Shooter(ICRAFT_X, ICRAFT_Y, STARTING_AMMO);
        
        initTrumps();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void initTrumps() {
        trumps = new ArrayList<Trump>();
        trumps.add(new Trump(B_WIDTH, 100, 1, 0));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gamestarted) { drawStartScreen(g); }
        else if (gameover) { drawGameOver(g); } 
        else if (gamestarted) { drawObjects(g); } 
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics g) {
        if (craft.isVisible()) { 
        	g.drawImage(craft.getImage(), craft.getX(), craft.getY(), this);
        }
        ArrayList<Missile> ms = craft.getMissiles();
        for (Missile m : ms) {
            if (m.isVisible()) {
                g.drawImage(m.getImage(), m.getX(), m.getY(), this);
            }
        }
        for (Trump a : trumps) {
            if (a.isVisible()) {
                g.drawImage(a.getImage(), a.getX(), a.getY(), this);
            }
        }
        String accuracy = "";
        if (craft.shots() > 0) { accuracy = " Accuracy: " + score*100/craft.shots() + "%"; }
        g.setColor(Color.WHITE);
        g.drawString("Aliens killed: " + score + " Ammo left: " + craft.ammo() + accuracy, 5, 15);
    }

    private void drawStartScreen(Graphics g) {
    	String msg = "Press Space to Start";
        Font small = new Font("Helvetica", Font.BOLD, 36);
        FontMetrics fm = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2,
                B_HEIGHT / 2);
    }
    
    private void drawGameOver(Graphics g) {

        String msg = "Game Over :( Score: " + score + " Accuracy: " + score*100/craft.shots() + "%";
        Font small = new Font("Helvetica", Font.BOLD, 36);
        FontMetrics fm = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2,
                B_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        inGame();
        if (gamestarted) {
        	currentFrame++;
        	updateCraft();
        	updateMissiles();
        	updateAliens();
        	checkCollisions();
        	repaint();
        }
    }
    
    private void inGame() {
    	if (craft.ammo() <= 0) { gameover = true; }
    	if (gameover) { timer.stop(); }
    }

    private void updateCraft() {
        if (craft.isVisible()) { craft.move(); }
    }

    private void updateMissiles() {

        ArrayList<Missile> ms = craft.getMissiles();

        for (int i = 0; i < ms.size(); i++) {
            Missile m = ms.get(i);
            if (m.isVisible()) { m.move(); } 
            else { ms.remove(i); }
        }
    }

    private void updateAliens() {
        for (int i = 0; i < trumps.size(); i++) {
            Trump a = trumps.get(i);
            if (a.isVisible() && a.getX() >= 0 && a.getX() <= B_WIDTH ) {
                a.move();
            } else {
                trumps.remove(i);
            }
        }
        
        if (currentFrame - frameAtLastSpawn > spawnRate) {
        	spawnEnemy();
        }
        
    }
    
    private void spawnEnemy() {
    	int dir = 1;
    	int height = B_HEIGHT/2;
    	if (!KILL_DEATH_EXTRAVAGANZA) {
    		dir = (int)(Math.random()+.5)*2 - 1; //generates either -1 or 1
    		height = (int)(Math.random()*(B_HEIGHT-200))+100;
    	}
    	int multiplier = (int)(Math.random()*10) + 5;
    	int vy = (int)(Math.random()*10)-5;
    	double a = Math.random()*.1;
    	int x;
    	if (dir > 0) { x = 0; }
    	else { x = B_WIDTH; }
    	trumps.add(new Trump(x, height, dir * multiplier, vy, -dir * a));
    	//playAudio("src/imreallyrich.wav");
    	frameAtLastSpawn = currentFrame;
    	if (spawnRate > 10) { spawnRate--; }
    }

    public void checkCollisions() {

        ArrayList<Missile> ms = craft.getMissiles();

        for (Missile m : ms) {

            Rectangle r1 = m.getBounds();

            for (Trump trump : trumps) {

                Rectangle r2 = trump.getBounds();

                if (r1.intersects(r2)) {
                    m.setVisible(false);
                    trump.setVisible(false);
                    score++;
                    craft.addAmmo();
                }
            }
        }
    }

    private void playAudio(String filename) {
    	try {
    		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
    		Clip clip = AudioSystem.getClip();
    		clip.open(audioInputStream);
        	clip.start(); 
    	}
    	catch (Exception e) {System.out.println(e.getMessage());}   	
    }
    
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
        	craft.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
        	if (!gamestarted && e.getKeyCode() == KeyEvent.VK_SPACE) { 
        		gamestarted = true;
        		//play game audio here
            }
        	else {
        		craft.keyPressed(e);
        	}
        }
    }
} 