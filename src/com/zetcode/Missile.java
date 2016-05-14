package com.zetcode;

public class Missile extends Sprite {

    private final int MISSILE_SPEED = 10;

    public Missile(int x, int y) {
        super(x, y);

        initMissile();
    }
    
    private void initMissile() {
        
        loadImage("src/missile.png");
        getImageDimensions();        
    }

    public void move() {
        
        y -= MISSILE_SPEED;
        
        if (y < 0) { vis = false; }
    }
}
