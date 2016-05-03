package rbadia.voidspace.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.model.BossShip;

/**
 * Manages and draws game graphics and images.
 */
public class GraphicsManager {
	private BufferedImage shipImg;
	private BufferedImage bulletImg;
	private BufferedImage enemyBulletImg;
	private BufferedImage asteroidImg;
	private BufferedImage asteroidExplosionImg;
	private BufferedImage shipExplosionImg;
	private BufferedImage bossExplosionImg;
	private BufferedImage enemyImg;
	private BufferedImage bossImg;
	private BufferedImage backdropShipImg;
	
	/**
	 * Creates a new graphics manager and loads the game images.
	 */
	public GraphicsManager(){
    	// load images
		try {
			this.shipImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/ship.png"));
			this.asteroidImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/asteroid.png"));
			this.asteroidExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/asteroidExplosion.png"));
			this.shipExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/shipExplosion.png"));
			this.bossExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/bossExplosion.png"));
			this.bulletImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/bullet.png"));
			this.enemyBulletImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/enemyBullet.png"));
			this.enemyImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/enemy.png"));
			this.bossImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/boss.png"));
		    this.backdropShipImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/backdropShip.png"));
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The graphic files are either corrupt or missing.",
					"VoidSpace - Fatal Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Draws a ship image to the specified graphics canvas.
	 * @param ship the ship to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawShip(Ship ship, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipImg, ship.x, ship.y, observer);
	}
	
	/**
	 * Draws a boss ship image to the specified graphic canvas.
	 * @param boss the boss ship to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawBossShip(BossShip boss, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bossImg, boss.x, boss.y, observer);
	}
	
	/**
	 * Draws an enemy ship image to the specified graphic canvas.
	 * @param enemy the enemy ship to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawEnemyShip(EnemyShip enemy, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(enemyImg, enemy.x, enemy.y, observer);
	}


	/**
	 * Draws a bullet image to the specified graphics canvas.
	 * @param bullet the bullet to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawBullet(Bullet bullet, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bulletImg, bullet.x, bullet.y, observer);
	}
	
	/**
	 * Draws an enemy bullet image to the specified graphics canvas.
	 * @param bullet the bullet to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawEnemyBullet(Bullet bullet, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(enemyBulletImg, bullet.x, bullet.y, observer);
	}

	/**
	 * Draws an asteroid image to the specified graphics canvas.
	 * @param asteroid the asteroid to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawAsteroid(Asteroid asteroid, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(asteroidImg, asteroid.x, asteroid.y, observer);
	}

	/**
	 * Draws a ship explosion image to the specified graphics canvas.
	 * @param shipExplosion the bounding rectangle of the explosion
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawShipExplosion(Rectangle shipExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipExplosionImg, shipExplosion.x, shipExplosion.y, observer);
	}

	/**
	 * Draws an asteroid explosion image to the specified graphics canvas.
	 * @param asteroidExplosion the bounding rectangle of the explosion
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawAsteroidExplosion(Rectangle asteroidExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(asteroidExplosionImg, asteroidExplosion.x, asteroidExplosion.y, observer);
	}
	
	/**
	 * Draws a boss explosion image to the specified graphics canvas.
	 * @param bossExplosion the bounding rectangle of the explosion
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawBossExplosion(Rectangle bossExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bossExplosionImg, bossExplosion.x, bossExplosion.y, observer);
	}
	
	/**
	 * Draws the boss ship's health bar to the specified graphic canvas.
	 * @param bossShip the ship to draw the health bar for
	 * @param g2d the graphic canvas
	 * @param observer object to be notified
	 */
	public void drawBossHealthBar(BossShip bossShip, Graphics2D g2d, ImageObserver observer) {
		// draw outer border
		g2d.setColor(Color.GRAY);
		g2d.fillRect(bossShip.x, bossShip.y - 5, bossShip.getBossWidth(), 1);
		
		// draw life bar
		if (bossShip.getBossHealth() > bossShip.getDefaultHealth()/2)
			g2d.setColor(Color.GREEN); 
		else if (bossShip.getBossHealth() > bossShip.getDefaultHealth()/4)
			g2d.setColor(Color.YELLOW);
		else
			g2d.setColor(Color.RED);
			
		g2d.fillRect(bossShip.x, bossShip.y - 5, bossShip.getBossHealth(), 1);

	}
	
	/**
	 * Draws a backdrop ship for the initial game screen.
	 * @param backdrop the backdrop ship to draw
	 * @param g2d the graphic canvas
	 * @param observer object to be notified
	 */
	 public void drawBackdropShip(Rectangle backdrop, Graphics2D g2d, ImageObserver observer) {
	        g2d.drawImage(backdropShipImg, backdrop.x, backdrop.y, observer);
	    }

	
	
	
}
