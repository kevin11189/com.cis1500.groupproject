package com.cis1500.groupproject;

import com.cis1500.groupproject.entities.Player;
import com.cis1500.groupproject.gfx.ImageLoader;
import com.cis1500.groupproject.gfx.ImageManager;
import com.cis1500.groupproject.gfx.SpriteSheet;
import com.cis1500.groupproject.io.Save;
import com.cis1500.groupproject.tiles.TileMap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	public static final int HEIGHT = 256, WIDTH = 256, SCALE = 3, PLAYER_HEIGHT = 24, PLAYER_WIDTH = 24;
	public static final int TILE_WIDTH = 16, TILE_HEIGHT = 16, SPEED = 10, ANIMATION_TICK = 7;
	public static final int OFFSET_TOP = 8, OFFSET_BOTTOM = 40, OFFSET_LEFT = 22, OFFSET_RIGHT = 30;
    public static boolean running = false;
    public Thread gameThread;
    private BufferedImage spriteSheetPlayer;
    private BufferedImage spriteSheetTile;
    private ImageManager imp;
    private ImageManager imt;
    private static TileMap tileMap;
    private static Player player;
    public static Save save;
    
    public void init() {
    	ImageLoader loader = new ImageLoader();
    	spriteSheetPlayer = loader.load("/spritesheetplayer.png");
    	spriteSheetTile = loader.load("/spritesheettile.png");
    	SpriteSheet ssp = new SpriteSheet(spriteSheetPlayer);
    	SpriteSheet sst = new SpriteSheet(spriteSheetTile);
      	imp = new ImageManager(ssp);
    	imt = new ImageManager(sst);
       	tileMap = new TileMap(imt);
    	player = new Player( (WIDTH/2) * SCALE,  (HEIGHT/2)* SCALE, imp);
    	try {
			save = new Save("savefile.sav");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	this.addKeyListener(new KeyManager());
    }

    public synchronized void start() {
        if (running)return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public synchronized void stop() {
        if(!running)return;
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
    	init();
        long lastTime = System.nanoTime();
        final double AMOUNT_OF_TICKS = 60;
        double ns = 1000000000 / AMOUNT_OF_TICKS;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += ( now - lastTime ) / ns;
            lastTime = now;
            if(delta >= 1) {
                tick();
                delta--;
                render();
            }
        }
        stop();
    }

    public void tick() {
    	player.tick();
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
        tileMap.render(g);
        player.render(g);
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        JFrame frame = new JFrame("Tile RPG");
        frame.setSize(WIDTH * SCALE, HEIGHT * SCALE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.setVisible(true);
        game.start();
    }
    
    public static Player getPlayer() {
    	return player;
    }
}