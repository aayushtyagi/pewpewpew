package com.zetcode;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Shooter extends Sprite {

    private int dx;
    private int dy;
    private final int SPEED = 5;
    private ArrayList<Missile> missiles;
    private boolean keyPressed = false;
    private int ammo;

    public Shooter(int x, int y, int ammo) {
        super(x, y);
        this.ammo = ammo;
        initCraft();
    }

    private void initCraft() {
        
        missiles = new ArrayList<>();
        loadImage("src/craft.png");
        getImageDimensions();
    }

    public void move() {

        x += dx;
        y += dy;

        if (x < 1) {
            x = 1;
        }

        if (y < 1) {
            y = 1;
        }
    }

    public ArrayList getMissiles() {
        return missiles;
    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
        	if (!keyPressed) { fire(); }
            keyPressed = true;
        }

        if (key == KeyEvent.VK_A) {
            dx = -SPEED;
        }

        if (key == KeyEvent.VK_D) {
            dx = SPEED;
        }

        /*if (key == KeyEvent.VK_W) {
            dy = -SPEED;
        }

        if (key == KeyEvent.VK_S) {
            dy = SPEED;
        }*/
    } 

    public void fire() {
        missiles.add(new Missile(x + width / 2 - 8, y));
        ammo--;
    }
    
    public int ammo() { return ammo; }
    
    public void addAmmo() { ammo++; }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
        	keyPressed = false;
        }
        
        if (key == KeyEvent.VK_A) {
            dx = 0;
        }

        if (key == KeyEvent.VK_D) {
            dx = 0;
        }

        /*if (key == KeyEvent.VK_W) {
            dy = 0;
        }

        if (key == KeyEvent.VK_S) {
            dy = 0;
        }*/
    }
}