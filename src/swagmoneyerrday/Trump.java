package swagmoneyerrday;

public class Trump extends Sprite {

    private double vx;
    private double vy = 0;
    private double a;
    public Trump(int x, int y, int vx, double a) {
        super(x, y);
        this.vx = vx;
        this.a = a;
        initAlien();
    }
    
    public Trump(int x, int y, int vx, int vy, double a) {
        super(x, y);
        this.vx = vx;
        this.vy = vy;
        this.a = a;
        initAlien();
    }

    private void initAlien() {

        loadImage("src/trump.png");
        getImageDimensions();
    }

    //TODO make them come from right or left, maybe add acceleration
    public void move() {
    	y += (int)vy;
        x += (int)vx;
        vx += a;
    }
}