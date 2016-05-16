package swagmoneyerrday;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
//test
public class Missile extends Sprite {

    private final int MISSILE_SPEED = 10;
    private final int NUM_OF_PEW_PEWS = 5;

    public Missile(int x, int y) {
        super(x, y);

        initMissile();
    }
    
    private void initMissile() {
        
        loadImage("src/missile.png");
        getImageDimensions(); 
        playPewPew();
    }

    public void move() {
        
        y -= MISSILE_SPEED;
        
        if (y < 0) { vis = false; }
    }
    
    private void playPewPew() {
    	int random = (int)(Math.random()*NUM_OF_PEW_PEWS)+1;
    	String filename = "src/pew" + random + ".wav";
    	try {
    		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
    		Clip clip = AudioSystem.getClip();
    		clip.open(audioInputStream);
        	clip.start(); 
    	}
    	catch (Exception e) {System.out.println(e.getMessage());}   
    }
}
