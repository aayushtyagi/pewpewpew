package swagmoneyerrday;

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

/**
 * The main logic of the game
 * @authors Aayush Tyagi, Nikhil Swaminathan, Martin Lee, Paramdeep Atwal, Kevin Lin
 * @period 5
 */
public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private Shooter craft;
    private ArrayList<Trump> trumps;
    private boolean gameover = false;
    private boolean gamestarted = false;
    private int spawnRate = 100; //check difference between frameatlastspawn and 
    private int frameAtLastSpawn = 0;
    private int currentFrame = 0;
    private int score = 0;
    private final int STARTING_AMMO = 15;
    private final int TRUMP_SIZE = 32;
    private final int B_WIDTH = 1300;
    private final int B_HEIGHT = 700;
    private final int ICRAFT_X = B_WIDTH/2;
    private final int ICRAFT_Y = B_HEIGHT-TRUMP_SIZE;
    private final int DELAY = 15;
    private final boolean KILL_DEATH_EXTRAVAGANZA = false;

    /**
     * Constructor - see initBoard()
     */
    public Board() { initBoard(); }

    /**
     * begins the game by setting keyboard listener, background color, dimensions,
     * craft, trumps and timer
     * 
     * also checks for killdeath mode
     */
    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        if (KILL_DEATH_EXTRAVAGANZA) { spawnRate = 1; }
        craft = new Shooter(ICRAFT_X, ICRAFT_Y, STARTING_AMMO);
        
        gameover = false;
        gamestarted = false;
        spawnRate = 100;
        frameAtLastSpawn = 0;
        currentFrame = 0;
        score = 0;
        initTrumps();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    /**
     * initializes array of Trumps and adds the first one
     */
    public void initTrumps() {
        trumps = new ArrayList<Trump>();
        trumps.add(new Trump(B_WIDTH, 100, 1, 0));
    }

    /**
     * overridden method called every frame, displays all components, 
     * unless game is not yet started or game is over
     * 
     * also syncs the game to current frame, corrected for lag
     * @param g - the graphics being used by the Board
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gamestarted) { drawStartScreen(g); }
        else if (gameover) { drawGameOver(g); } 
        else if (gamestarted) { drawObjects(g); } 
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * called in the paintComponent method, draws craft, each missile, 
     * and each trump, and updates score in top left
     * @param g - graphics for the Board
     */
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
        g.drawString("Trumps killed: " + score + " Ammo left: " + craft.ammo() + accuracy, 5, 15);
    }

    /**
     * called in the paintComponent method, creates two font objects and 
     * uses the metrics to calculate where to draw the two Strings before
     * the game starts
     * @param g - graphics used by Board
     */
    private void drawStartScreen(Graphics g) {
    	String msg = "Press Space to Start";
        Font large = new Font("Helvetica", Font.BOLD, 36);
        FontMetrics fm = getFontMetrics(large);

        g.setColor(Color.white);
        g.setFont(large);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2,
                B_HEIGHT / 2);
        
        
        String instructions = "Shoot as many Trumps as you can!!!";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fm2 = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(instructions, (B_WIDTH - fm2.stringWidth(instructions)) / 2,
                B_HEIGHT / 2 + fm.getHeight());

        String controls = "Ctrls: Left/Right to move, Space to shoot, Shift to move half speed.";
        g.drawString(controls, (B_WIDTH - fm2.stringWidth(controls)) / 2,
                B_HEIGHT / 2 + fm.getHeight() + fm2.getHeight() * 2);
    }
    
    /**
     * called in the paintComponent method, creates two font objects and 
     * uses the metrics to calculate where to draw the two Strings at the 
     * end of the game
     * @param g - graphics used by Board
     */
    private void drawGameOver(Graphics g) {
    	playAudio("src/imreallyrich.wav");
        String msg = "Game Over :(";
        Font large = new Font("Helvetica", Font.BOLD, 36);
        FontMetrics fm = getFontMetrics(large);
        
        String msg2 = "Score: " + score + " Accuracy: " + score*100/craft.shots() + "%";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fm2 = getFontMetrics(small);
        
        String msg3 = "Press Space to reset!";

        g.setColor(Color.white);
        g.setFont(large);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2,
                B_HEIGHT / 2);
        g.setFont(small);
        g.drawString(msg2, (B_WIDTH - fm2.stringWidth(msg2)) / 2,
                B_HEIGHT / 2 + fm.getHeight());
        g.drawString(msg3, (B_WIDTH - fm2.stringWidth(msg2)) / 2,
                B_HEIGHT / 2 + fm.getHeight() + fm2.getHeight() * 2);
    }

    /**
     * if the game has started, this is called every frame to update
     * all components, and repaint them afterwards
     * @param e - not used, part of superclass
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gamestarted) {
            checkGameOver();
        	currentFrame++;
        	updateCraft();
        	updateMissiles();
        	updateTrumps();
        	checkCollisions();
        	repaint();
        }
    }
    
    /**
     * checks if game is over (if there is any ammo) and stops timer
     */
    private void checkGameOver() {
    	if (craft.ammo() <= 0) { gameover = true; }
    	if (gameover) { timer.stop(); }
    }

    /**
     * called in actionPerformed, calls the move() method of craft
     */
    private void updateCraft() {
        if (craft.isVisible()) { craft.move(); }
    }

    /**
     * called in actionPerformed, loops through each missile and calls
     * the move method, deletes if missile is no longer alive
     */
    private void updateMissiles() {
        ArrayList<Missile> ms = craft.getMissiles();
        for (int i = 0; i < ms.size(); i++) {
            Missile m = ms.get(i);
            if (m.isVisible()) { m.move(); } 
            else { ms.remove(i); }
        }
    }

    /**
     * called in actionPerformed, loops through each trump and checks if
     * if it is within bounds. If it is, trump is moved, if it isn't, 
     * trump is killed
     * if it has been a certain number of frames since the last enemy, spawn
     * a new Trump
     */
    private void updateTrumps() {
        for (int i = 0; i < trumps.size(); i++) {
            Trump a = trumps.get(i);
            if (a.isVisible() && a.getX() >= 0 && a.getX() <= B_WIDTH ) { a.move(); } 
            else { trumps.remove(i); }
        }
        
        if (currentFrame - frameAtLastSpawn > spawnRate) { spawnEnemy(); }
    }
    
    /**
     * called in updateTrumps, creates a trump with random position, direction, 
     * y velocity, x velocity, and x acceleration using lots of Math.random()
     * 
     * Acceleration is always opposite the direction of motion, and if killdeath
     * mode is on, all the trumps spawn from the same spot to look like a fountain
     */
    private void spawnEnemy() {
    	int dir = 1;
    	int height = B_HEIGHT/2;
    	if (!KILL_DEATH_EXTRAVAGANZA) {
    		dir = (int)(Math.random()+.5)*2 - 1; //generates either -1 or 1
    		height = (int)(Math.random()*(B_HEIGHT-200))+100; //between 100 and height-100
    	}
    	int multiplier = (int)(Math.random()*10) + 5; //between 5 and 14
    	int vy = (int)(Math.random()*10)-5; //between -5(up) and 4(down)
    	double a = Math.random()*.1; //between -.1 and .1
    	int x;
    	if (dir > 0) { x = 0; }
    	else { x = B_WIDTH; }
    	trumps.add(new Trump(x, height, dir * multiplier, vy, -dir * a));
    	frameAtLastSpawn = currentFrame; //update the frameAtLastSpawn for reference in updateTrumps
    	if (spawnRate > 10) { spawnRate--; } //speed up spawn rate until it's really fast
    }

    /**
     * called in actionPerformed, checks if any missile has hit any trump, and
     * if it has adds one ammo and increments score, destroys both trump and missile
     */
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

    /**
     * called in drawGameOver, plays an audio file (copy pasted from stack overflow)
     * @param filename - file to be played
     */
    private void playAudio(String filename) {
    	try {
    		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
    		Clip clip = AudioSystem.getClip();
    		clip.open(audioInputStream);
        	clip.start(); 
    	}
    	catch (Exception e) {System.out.println(e.getMessage());}   	
    }
    
    /**
     * checks for keypressed, either in beginning of game of from the craft
     */
    private class TAdapter extends KeyAdapter {

    	/**
    	 * check craft for key released
    	 */
        @Override
        public void keyReleased(KeyEvent e) { craft.keyReleased(e); }
        
        /**
         * check craft for keypressed or if game has not started
         */
        @Override
        public void keyPressed(KeyEvent e) {
        	if (!gamestarted && e.getKeyCode() == KeyEvent.VK_SPACE) { gamestarted = true; }
        	else if (gameover && e.getKeyCode() == KeyEvent.VK_SPACE) { 
        		initBoard(); 
        		repaint();
        	}
        	else if (gamestarted && currentFrame > 3) { craft.keyPressed(e); }
        }
    }
} 