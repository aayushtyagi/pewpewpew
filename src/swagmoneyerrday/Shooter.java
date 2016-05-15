package swagmoneyerrday;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Shooter extends Sprite {

    private int dx;
    private final int SPEED = 10;
    private int speed = SPEED;
    private ArrayList<Missile> missiles;
    private boolean keyPressed = false;
    private int ammo;
    private int shots = 0;

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
        if (x < 1) { x = 1; }
        if (y < 1) { y = 1; }
    }

    public ArrayList getMissiles() { return missiles; }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
        	if (!keyPressed) { fire(); }
            keyPressed = true;
        }
        if (key == KeyEvent.VK_SHIFT) { speed = SPEED / 2;}
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) { dx = -speed; }
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) { dx = speed; }
    } 

    public void fire() {
        missiles.add(new Missile(x + width / 2 - 8, y));
        ammo--;
        shots++;
    }
    
    public int ammo() { return ammo; }
    
    public int shots() { return shots; }
    
    public void addAmmo() { ammo++; }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) { keyPressed = false; }
        if (key == KeyEvent.VK_SHIFT) { speed  = SPEED; }
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) { dx = 0; }
        if (key == KeyEvent.VK_D  || key == KeyEvent.VK_RIGHT) { dx = 0; }
    }
}