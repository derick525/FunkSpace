package rbadia.voidspace.main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.model.BossShip;
import rbadia.voidspace.sounds.SoundManager;

/**
 * Main game screen. Handles all game graphics updates and some of the game logic.
 */
public class GameScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	private BufferedImage backBuffer;
	private Graphics2D g2d;

	private static final int NEW_SHIP_DELAY = 500;
	private static final int NEW_ASTEROID_DELAY = 500;
	private static final int NEW_ENEMY_SHIP_DELAY = 2000;

	private long lastShipTime;
	private long lastEnemyShipTime;
	private long lastAsteroidTime;

	private Rectangle asteroidExplosion;
	private Rectangle shipExplosion;
	private Rectangle enemyShipExplosion;
	private Rectangle bossShipExplosion;
	private Rectangle backdropShip;

	private JLabel shipsValueLabel;
	private JLabel destroyedValueLabel;
	private JLabel pointsValueLabel;
	private JLabel levelValueLabel;

	private Random rand;

	private Font originalFont;
	private Font bigFont;
	private Font biggestFont;

	private GameStatus status;
	private SoundManager soundMan;
	private GraphicsManager graphicsMan;
	private GameLogic gameLogic;

	//	private int trajectory;
	private ArrayList<Asteroid> asteroids;

	// default values for the equation of a circle
	private boolean doOnce = true;
	private int h;
	private int k;	
	private int radius;  
	private int xCoord;				
	private int yCoord;

	boolean circleBottom = true;
	boolean bossDestroyed = false;

	// default value for the direction of the enemy ship
	private int direction = 1;

	/**
	 * This method initializes 
	 * 
	 */
	public GameScreen() {
		super();
		// initialize random number generator
		rand = new Random();

		initialize(); 

		// init graphics manager
		graphicsMan = new GraphicsManager();

		// init back buffer image
		backBuffer = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();
	}

	/**
	 * Initialization method (for VE compatibility).
	 */
	private void initialize() {
		// set panel properties
		this.setSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setBackground(Color.BLACK);
	}

	/**
	 * Update the game screen.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// draw current backbuffer to the actual game screen
		g.drawImage(backBuffer, 0, 0, this);
	}

	/**
	 * Update the game screen's backbuffer image.
	 */
	public void updateScreen(){
		Ship ship = gameLogic.getShip();
		EnemyShip enemy = gameLogic.getEnemyShip();
		BossShip boss = gameLogic.getBossShip();
		asteroids = gameLogic.getAsteroids();
		List<Bullet> bullets = gameLogic.getBullets();
		List<Bullet> enemyBullets = gameLogic.getEnemyBullets();
		List<Bullet> bossBullets = gameLogic.getBossBullets();

		long currentTime2 = System.currentTimeMillis();


		// set original font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}

		// erase screen
		g2d.setPaint(new Color(0x0000));
		g2d.fillRect(0, 0, getSize().width, getSize().height);

		// draw 50 random stars
		drawStars(50);

		// if the game is starting, draw "Get Ready" message
		if(status.isGameStarting()){
			drawGetReady();
			return;
		}

		// if the game is over, draw the "Game Over" message
		if(status.isGameOver()){
			// draw the message
			drawGameOver();

			long currentTime = System.currentTimeMillis();
			// draw the explosions until their time passes
			if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
			if((currentTime - lastShipTime) < NEW_SHIP_DELAY){
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}

			return;
		}

		// if boss is destroyed, draw the "Game Won" message
		if(status.isGameWon()){
			// draw the message
			drawYouWon();
			return;
		}


		// the game has not started yet
		if(!status.isGameStarted()){

			// draw backdrop ship
			backdropShip = new Rectangle(
					this.getWidth() / 4,
					this.getHeight() / 3,
					this.getWidth() / 2,
					this.getHeight() / 2
					);
			graphicsMan.drawBackdropShip(backdropShip, g2d, this);

			// draw game title screen
			initialMessage();

			return;
		}

		// draw bullets
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			graphicsMan.drawBullet(bullet, g2d, this);

			boolean remove = gameLogic.moveBullet(bullet);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}

		//Draw enemy bullets
		for(int i=0; i<enemyBullets.size(); i++){
			Bullet bullet = enemyBullets.get(i);
			graphicsMan.drawEnemyBullet(bullet, g2d, this);

			boolean remove = gameLogic.moveEnemyBullet(bullet);
			if(remove){
				enemyBullets.remove(i);
				i--;
			}
		}

		//Draw boss bullets
		for(int i=0; i<bossBullets.size(); i++){
			Bullet bullet = bossBullets.get(i);
			graphicsMan.drawEnemyBullet(bullet, g2d, this);

			boolean remove = gameLogic.moveEnemyBullet(bullet);
			if(remove){
				bossBullets.remove(i);
				i--;
			}
		}

		if(status.getAsteroidsDestroyed() ==  5){status.setLevel(2);}
		if(status.getAsteroidsDestroyed() == 15){status.setLevel(3);}
		if(status.getAsteroidsDestroyed() == 25){status.setLevel(4);}
		if(status.getAsteroidsDestroyed() == 35){status.setLevel(5);}	
		if(status.getAsteroidsDestroyed() == 50){status.setLevel(6);}

		if (status.getAsteroidsDestroyed() < 5) {
			// draw asteroid
			if(!status.isNewAsteroid()[0]) {
				switch(asteroids.get(0).getTrajectory()){

				case 0: // vertical

					if( this.contains((int)asteroids.get(0).getX(), (int) asteroids.get(0).getY()) ){
						asteroids.get(0).translate(0, asteroids.get(0).getSpeed());
						graphicsMan.drawAsteroid(asteroids.get(0), g2d, this);
						break;
					}
					else {
						asteroids.get(0).setLocation(rand.nextInt(getWidth() - asteroids.get(0).width),0);
					}
					//sets new trajectory
					asteroids.get(0).setTrajectory(rand.nextInt(5));

					chooseNextLocation(0);


					break;


				case 1: // diagonal to right

					if( (this.contains((int)asteroids.get(0).getX(), (int) asteroids.get(0).getY())) || (this.contains((int)asteroids.get(0).getX() + asteroids.get(0).getAsteroidWidth(), (int) asteroids.get(0).getY() + asteroids.get(0).getAsteroidHeight())) ){
						asteroids.get(0).translate(1, asteroids.get(0).getSpeed()); //asteroid.getSpeed());
						graphicsMan.drawAsteroid(asteroids.get(0), g2d, this);
						break;
					}
					else {
						asteroids.get(0).setLocation(rand.nextInt(getWidth() - asteroids.get(0).width),0);
					}
					//sets new trajectory
					asteroids.get(0).setTrajectory(rand.nextInt(5));


					chooseNextLocation(0);

					break;


				case 2: // diagonal left

					if( (this.contains((int)asteroids.get(0).getX(), (int) asteroids.get(0).getY())) || (this.contains((int)asteroids.get(0).getX() + asteroids.get(0).getAsteroidWidth(), (int) asteroids.get(0).getY() + asteroids.get(0).getAsteroidHeight())) ){
						asteroids.get(0).translate(-1, asteroids.get(0).getSpeed());
						graphicsMan.drawAsteroid(asteroids.get(0), g2d, this);
						break;
					}
					else {
						asteroids.get(0).setLocation(rand.nextInt(getWidth() - asteroids.get(0).width),0);
					}
					//sets new trajectory
					asteroids.get(0).setTrajectory(rand.nextInt(5));


					chooseNextLocation(0);

					break;

				case 3: // diagonal right

					if( (this.contains((int)asteroids.get(0).getX(), (int) asteroids.get(0).getY())) || (this.contains((int)asteroids.get(0).getX() + asteroids.get(0).getAsteroidWidth(), (int) asteroids.get(0).getY() + asteroids.get(0).getAsteroidHeight())) ){
						asteroids.get(0).translate(2, asteroids.get(0).getSpeed());
						graphicsMan.drawAsteroid(asteroids.get(0), g2d, this);
						break;
					}
					else {
						asteroids.get(0).setLocation(rand.nextInt(getWidth() - asteroids.get(0).width),0);
					}
					//sets new trajectory
					asteroids.get(0).setTrajectory(rand.nextInt(5));


					chooseNextLocation(0);

					break;

				case 4:  //diagonal left

					if((this.contains((int)asteroids.get(0).getX(), (int) asteroids.get(0).getY())) || (this.contains((int)asteroids.get(0).getX() + asteroids.get(0).getAsteroidWidth(), (int) asteroids.get(0).getY() + asteroids.get(0).getAsteroidHeight())) ){
						asteroids.get(0).translate(-2, asteroids.get(0).getSpeed());
						graphicsMan.drawAsteroid(asteroids.get(0), g2d, this);
						break;
					}
					else {
						asteroids.get(0).setLocation(rand.nextInt(getWidth() - asteroids.get(0).width),0);
					}
					//sets new trajectory
					asteroids.get(0).setTrajectory(rand.nextInt(5));


					chooseNextLocation(0);

					break;


				}
			}

			else{				
				asteroids.get(0).setTrajectory(rand.nextInt(5));
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
					// draw a new asteroid
					lastAsteroidTime = currentTime;
					status.setNewAsteroid(false, 0);						
					asteroids.get(0).setLocation(rand.nextInt(getWidth() - asteroids.get(0).width),0);

				}
				else{
					// draw explosion
					graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
				}
			}
			// check bullet-asteroid collisions for get(0)
			for(int i=0; i<bullets.size(); i++){
				Bullet bullet = bullets.get(i);
				if(asteroids.get(0).intersects(bullet)){
					// increase asteroids destroyed count
					status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
					// increase score
					status.setScore(status.getScore() + 100);

					// "remove" asteroid
					asteroidExplosion = new Rectangle(
							asteroids.get(0).x,
							asteroids.get(0).y,
							asteroids.get(0).width,
							asteroids.get(0).height);							
					asteroids.get(0).setLocation(-asteroids.get(0).width, -asteroids.get(0).height);							
					status.setNewAsteroid(true, 0);
					lastAsteroidTime = System.currentTimeMillis();

					// play asteroid explosion sound
					soundMan.playAsteroidExplosionSound();

					// remove bullet
					bullets.remove(i);
					break;
				}
			}
			// check ship-asteroid collisions for get(0)
			if(asteroids.get(0).intersects(ship)){
				// decrease number of ships left
				status.setShipsLeft(status.getShipsLeft() - 1);
				// decrease score to a minimum of zero
				if (status.getScore() - 50 < 0) {
					status.setScore(0);
				} else 
					status.setScore(status.getScore() - 50);

				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);

				// "remove" asteroid
				asteroidExplosion = new Rectangle(
						asteroids.get(0).x,
						asteroids.get(0).y,
						asteroids.get(0).width,
						asteroids.get(0).height);
				asteroids.get(0).setLocation(-asteroids.get(0).width, -asteroids.get(0).height);
				status.setNewAsteroid(true, 0);
				lastAsteroidTime = System.currentTimeMillis();

				// "remove" ship
				shipExplosion = new Rectangle(
						ship.x,
						ship.y,
						ship.width,
						ship.height);
				ship.setLocation(this.getWidth() + ship.width, -ship.height);
				status.setNewShip(true);
				lastShipTime = System.currentTimeMillis();

				// play ship explosion sound
				soundMan.playShipExplosionSound();
				// play asteroid explosion sound
				soundMan.playAsteroidExplosionSound();
			}

		}

		else {

			for (int i = 0; i < gameLogic.maxAsteroids; i++) {
				if(!status.isNewAsteroid()[i]) {

					switch(asteroids.get(i).getTrajectory()){

					case 0: // vertical

						if( this.contains((int)asteroids.get(i).getX(), (int) asteroids.get(i).getY()) ){
							asteroids.get(i).translate(0, asteroids.get(i).getSpeed());
							graphicsMan.drawAsteroid(asteroids.get(i), g2d, this);
							break;
						}
						else {
							asteroids.get(i).setLocation(rand.nextInt(getWidth() - asteroids.get(i).width),0);
						}
						//sets new trajectory
						asteroids.get(i).setTrajectory(rand.nextInt(5));
						;

						chooseNextLocation(i);


						break;


					case 1: // diagonal to right

						if( (this.contains((int)asteroids.get(i).getX(), (int) asteroids.get(i).getY())) || (this.contains((int)asteroids.get(i).getX() + asteroids.get(i).getAsteroidWidth(), (int) asteroids.get(i).getY() + asteroids.get(i).getAsteroidHeight())) ){
							asteroids.get(i).translate(1, asteroids.get(i).getSpeed()); //asteroid.getSpeed());
							graphicsMan.drawAsteroid(asteroids.get(i), g2d, this);
							break;
						}
						else {
							asteroids.get(i).setLocation(rand.nextInt(getWidth() - asteroids.get(i).width),0);
						}

						//sets new trajectory
						asteroids.get(i).setTrajectory(rand.nextInt(5));

						chooseNextLocation(i);

						break;


					case 2: // diagonal left

						if( (this.contains((int)asteroids.get(i).getX(), (int) asteroids.get(i).getY())) || (this.contains((int)asteroids.get(i).getX() + asteroids.get(i).getAsteroidWidth(), (int) asteroids.get(i).getY() + asteroids.get(i).getAsteroidHeight())) ){
							asteroids.get(i).translate(-1, asteroids.get(i).getSpeed());
							graphicsMan.drawAsteroid(asteroids.get(i), g2d, this);
							break;
						}
						else {
							asteroids.get(i).setLocation(rand.nextInt(getWidth() - asteroids.get(i).width),0);
						}
						//sets new trajectory
						asteroids.get(i).setTrajectory(rand.nextInt(5));

						chooseNextLocation(i);

						break;

					case 3: // diagonal right

						if( (this.contains((int)asteroids.get(i).getX(), (int) asteroids.get(i).getY())) || (this.contains((int)asteroids.get(i).getX() + asteroids.get(i).getAsteroidWidth(), (int) asteroids.get(i).getY() + asteroids.get(i).getAsteroidHeight())) ){
							asteroids.get(i).translate(2, asteroids.get(i).getSpeed());
							graphicsMan.drawAsteroid(asteroids.get(i), g2d, this);
							break;
						}
						else {
							asteroids.get(i).setLocation(rand.nextInt(getWidth() - asteroids.get(i).width),0);
						}
						//sets new trajectory
						asteroids.get(i).setTrajectory(rand.nextInt(5));

						chooseNextLocation(i);

						break;

					case 4:  //diagonal left

						if((this.contains((int)asteroids.get(i).getX(), (int) asteroids.get(i).getY())) || (this.contains((int)asteroids.get(i).getX() + asteroids.get(i).getAsteroidWidth(), (int) asteroids.get(i).getY() + asteroids.get(i).getAsteroidHeight())) ){
							asteroids.get(i).translate(-2, asteroids.get(i).getSpeed());
							graphicsMan.drawAsteroid(asteroids.get(i), g2d, this);
							break;
						}
						else {
							asteroids.get(i).setLocation(rand.nextInt(getWidth() - asteroids.get(i).width),0);
						}
						//sets new trajectory
						asteroids.get(i).setTrajectory(rand.nextInt(5));

						chooseNextLocation(i);

						break;
					}
				}

				else{	
					//sets new trajectory
					asteroids.get(i).setTrajectory(rand.nextInt(5));
					long currentTime = System.currentTimeMillis();
					if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
						// draw a new asteroid
						lastAsteroidTime = currentTime;
						status.setNewAsteroid(false, i);								
						asteroids.get(i).setLocation(rand.nextInt(getWidth() - asteroids.get(i).width),0);		
					}
					else{
						// draw explosion
						graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
					}
				}
			}


			// check bullet-asteroid collisions for get(i)
			for(int i=0; i<bullets.size(); i++){
				Bullet bullet = bullets.get(i);
				for (int j = 0 ; j < asteroids.size() ; j++){
					if(asteroids.get(j).intersects(bullet)){
						// increase asteroids destroyed count
						status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
						//increase score
						status.setScore(status.getScore() + 100);

						// "remove" asteroid
						asteroidExplosion = new Rectangle(
								asteroids.get(j).x,
								asteroids.get(j).y,
								asteroids.get(j).width,
								asteroids.get(j).height);						
						asteroids.get(j).setLocation(-asteroids.get(j).width, -asteroids.get(j).height);						
						status.setNewAsteroid(true, j);
						lastAsteroidTime = System.currentTimeMillis();

						// play asteroid explosion sound
						soundMan.playAsteroidExplosionSound();

						// remove bullet
						bullets.remove(i);
						break;
					}
				}
			}

			// check ship-asteroid collisions for get(i)
			for (int i = 0 ; i < asteroids.size();i++) {
				if(asteroids.get(i).intersects(ship)){
					// decrease number of ships left
					status.setShipsLeft(status.getShipsLeft() - 1);

					// decrease score to a minimum of zero 
					if (status.getScore() - 50 < 0) {
						status.setScore(0);
					} else 
						status.setScore(status.getScore() - 50);

					status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);

					// "remove" asteroid
					asteroidExplosion = new Rectangle(
							asteroids.get(i).x,
							asteroids.get(i).y,
							asteroids.get(i).width,
							asteroids.get(i).height);
					asteroids.get(i).setLocation(-asteroids.get(i).width, -asteroids.get(i).height);
					status.setNewAsteroid(true, 0);
					lastAsteroidTime = System.currentTimeMillis();

					// "remove" ship
					shipExplosion = new Rectangle(
							ship.x,
							ship.y,
							ship.width,
							ship.height);
					ship.setLocation(this.getWidth() + ship.width, -ship.height);
					status.setNewShip(true);
					lastShipTime = System.currentTimeMillis();

					// play ship explosion sound
					soundMan.playShipExplosionSound();
					// play asteroid explosion sound
					soundMan.playAsteroidExplosionSound();
				}
			}	
		}

		if (status.getAsteroidsDestroyed() >= 15) {

			// draw enemy ship

			int leftLimit = 0 ;
			int rightLimit = this.getWidth() - enemy.getShipWidth();

			if(!status.isNewEnemyShip()){	
				if(enemy.getX() != rightLimit || enemy.getX() != leftLimit)  {
					enemy.translate(direction,0);
					graphicsMan.drawEnemyShip(enemy, g2d, this);	

					if(enemy.getX() == rightLimit)
						direction = -1;
					else if(enemy.getX() == leftLimit)
						direction = 1;

					// fire bullets
					if (currentTime2 % 100 == 0) {
						gameLogic.fireEnemyBullet();
					}	
				}
				else{
					status.setNewEnemyShip(true);	
				}

			}
			else{
				// else draw a new one
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastEnemyShipTime) > NEW_ENEMY_SHIP_DELAY){
					// draw a new asteroid
					lastEnemyShipTime = currentTime;
					status.setNewEnemyShip(false);	
					enemy = gameLogic.newEnemyShip(this);

				}
				else{
					// draw explosion
					graphicsMan.drawShipExplosion(enemyShipExplosion, g2d, this);
				}
			}


			// check ship-enemy ship collisions
			if(enemy.intersects(ship)){
				// decrease number of ships left
				status.setShipsLeft(status.getShipsLeft() - 1);

				// decrease score to a minimum of zero
				if (status.getScore() - 100 < 0) {
					status.setScore(0);
				} else 
					status.setScore(status.getScore() - 100);


				// "remove" enemy ship
				enemyShipExplosion = new Rectangle(
						enemy.x,
						enemy.y,
						enemy.width,
						enemy.height);
				enemy.setLocation(enemy.width, -enemy.height);
				status.setNewEnemyShip(true);
				lastEnemyShipTime = System.currentTimeMillis();

				// "remove" ship
				shipExplosion = new Rectangle(
						ship.x,
						ship.y,
						ship.width,
						ship.height);
				ship.setLocation(this.getWidth() + ship.width, -ship.height);
				status.setNewShip(true);
				lastShipTime = System.currentTimeMillis();

				// play ship explosion sound
				soundMan.playShipExplosionSound();
				// play ship explosion sound
				soundMan.playShipExplosionSound();
			}

			// check ship bullet-enemy ship collisions
			for(int i=0; i<bullets.size(); i++){
				Bullet bullet = bullets.get(i);
				if(enemy.intersects(bullet)){
					//increase score
					status.setScore(status.getScore() + 500);

					// "remove" enemy
					enemyShipExplosion = new Rectangle(
							enemy.x,
							enemy.y,
							enemy.width,
							enemy.height);

					enemy.setLocation(-enemy.width, -enemy.height);

					status.setNewEnemyShip(true);
					lastEnemyShipTime = System.currentTimeMillis();

					// play asteroid explosion sound
					soundMan.playShipExplosionSound();

					// remove bullet
					bullets.remove(i);
					break;
				}
			}
			// check enemy bullet-ship collisions
			for(int i=0; i<enemyBullets.size(); i++){
				Bullet bullet = enemyBullets.get(i);
				if(ship.intersects(bullet)){
					// decrease number of ships
					status.setShipsLeft(status.getShipsLeft() - 1);
					// decrease score to a minimum of zero
					if (status.getScore() - 75 < 0) {
						status.setScore(0);
					} else 
						status.setScore(status.getScore() - 75);



					// "remove" ship
					// "remove" ship
					shipExplosion = new Rectangle(
							ship.x,
							ship.y,
							ship.width,
							ship.height);
					ship.setLocation(this.getWidth() + ship.width, -ship.height);
					status.setNewShip(true);
					lastShipTime = System.currentTimeMillis();

					// play ship explosion sound
					soundMan.playShipExplosionSound();
					// play ship explosion sound
					soundMan.playShipExplosionSound();
					// remove bullet
					enemyBullets.remove(i);
					break;
				}
			}	
		}

		if (status.getAsteroidsDestroyed() >= 25) {
			// increase asteroids speed 
			for (int i = 0; i < asteroids.size(); i++){
				asteroids.get(i).setSpeed(6);
			}
		}

		if (status.getAsteroidsDestroyed() >= 35) {
			// increase asteroids speed
			for (int i = 0; i < asteroids.size(); i++){
				asteroids.get(i).setSpeed(8);
			}


		}

		if (status.getAsteroidsDestroyed() >= 50) {
			// draw boss ship
			if(doOnce){
				h = (int)boss.getX();
				k = (int)boss.getY() -150;	
				radius = 100;  
				xCoord = h;				
				yCoord = k+radius;
				doOnce = false;
			}

			if(!bossDestroyed){
				boss.setLocation(xCoord,yCoord);
				graphicsMan.drawBossShip(boss, g2d, this);
				graphicsMan.drawBossHealthBar(boss, g2d, this);
				// fire bullets
				if (currentTime2 % 25 == 0) {
					gameLogic.fireBossBullet();
				}

				if(circleBottom){
					xCoord += 1;
					yCoord = (int) Math.sqrt(Math.pow(radius, 2) -  Math.pow((xCoord - h), 2)) + k;
					if(xCoord == (h + radius)){
						circleBottom = false;
					}
				}

				else {
					xCoord -=1;
					yCoord = (int) -(Math.sqrt(Math.pow(radius, 2) -  Math.pow((xCoord - h), 2))) + k;
					if(xCoord == (h - radius)){
						circleBottom = true;
					}

				}				
			}			
			else{
				//doesn't need to set new trajectory as there's only one boss
				//draw explosion 
				bossDestroyed = true;
				graphicsMan.drawBossExplosion(bossShipExplosion, g2d, this);
			}

			// check ship-boss ship collisions
			if(ship.intersects(boss)){
				// decrease number of ships left
				status.setShipsLeft(status.getShipsLeft() - 1);

				// decrease score to a minimum of zero
				if (status.getScore() - 500 < 0) {
					status.setScore(0);
				} else 
					status.setScore(status.getScore() - 500);

				// boss can't be destroyed by crashing into it

				// "remove" ship
				shipExplosion = new Rectangle(
						ship.x,
						ship.y,
						ship.width,
						ship.height);
				ship.setLocation(this.getWidth() + ship.width, -ship.height);
				status.setNewShip(true);
				lastShipTime = System.currentTimeMillis();

				// play ship explosion sound
				soundMan.playShipExplosionSound();
				// play asteroid explosion sound
				soundMan.playAsteroidExplosionSound();
			}
			// check ship bullet-boss ship collisions
			for(int i=0; i<bullets.size(); i++){
				Bullet bullet = bullets.get(i);
				if(boss.intersects(bullet)){

					if (boss.getBossHealth() < 1) {
						// increase score
						status.setScore(status.getScore() + 1000);
						// " remove" enemy
						bossShipExplosion = new Rectangle(boss.x, boss.y, boss.width, boss.height);
						boss.setLocation(-boss.width, -boss.height);
						bossDestroyed = true;
						// play asteroid explosion sound
						soundMan.playShipExplosionSound();

					} else {
						boss.setBossHealth(boss.getBossHealth() - 2);
					}
					// remove bullet
					bullets.remove(i);
					break;
				}
			}
			// check boss bullet-ship collisions
			for(int i=0; i<bossBullets.size(); i++){
				Bullet bullet = bossBullets.get(i);
				if(ship.intersects(bullet)){
					// decrease number of ships
					status.setShipsLeft(status.getShipsLeft() - 1);

					// decrease score to a minimum of zero
					if (status.getScore() - 100 < 0) {
						status.setScore(0);
					} else 
						status.setScore(status.getScore() - 100);



					// "remove" ship
					// "remove" ship
					shipExplosion = new Rectangle(
							ship.x,
							ship.y,
							ship.width,
							ship.height);
					ship.setLocation(this.getWidth() + ship.width, -ship.height);
					status.setNewShip(true);
					lastShipTime = System.currentTimeMillis();

					// play ship explosion sound
					soundMan.playShipExplosionSound();
					// play ship explosion sound
					soundMan.playShipExplosionSound();
					// remove bullet
					bossBullets.remove(i);
					break;
				}
			}		
		}


		// draw ship
		if(!status.isNewShip()){
			// draw it in its current location
			graphicsMan.drawShip(ship, g2d, this);

		} 


		else{
			// draw a new one
			long currentTime = System.currentTimeMillis();

			if((currentTime - lastShipTime) > NEW_SHIP_DELAY){
				lastShipTime = currentTime;
				status.setNewShip(false);
				ship = gameLogic.newShip(this);
			}
			else{
				// draw explosion
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
		}


		// update asteroids destroyed label
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));

		// update ships left label
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));

		// update score label
		pointsValueLabel.setText(Long.toString(status.getScore()));

		// update score label
		levelValueLabel.setText(Long.toString(status.getLevel()));
	}


	/**
	 * Draws the "Game Over" message.
	 */
	private void drawGameOver() {
		String gameOverStr = "GAME OVER";
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameOverStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(gameOverStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(gameOverStr, strX, strY);
	}

	/**
	 * Draws the "Game Won" message.
	 */
	private void drawYouWon() {
		String gameWonStr = "GAME WON!";
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameWonStr);
		if(strWidth > this.getWidth() - 20){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(gameWonStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(gameWonStr, strX, strY);
	}

	/**
	 * Draws the initial "Get Ready!" message.
	 */
	public void drawGetReady() {
		String readyStr = "Get Ready!";
		g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
		FontMetrics fm = g2d.getFontMetrics();
		int ascent = fm.getAscent();
		int strWidth = fm.stringWidth(readyStr);
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(readyStr, strX, strY);
	}

	/**
	 * Draws the specified number of stars randomly on the game screen.
	 * @param numberOfStars the number of stars to draw
	 */
	private void drawStars(int numberOfStars) {
		g2d.setColor(Color.WHITE);
		for(int i=0; i<numberOfStars; i++){
			int x = (int)(Math.random() * this.getWidth());
			int y = (int)(Math.random() * this.getHeight());
			g2d.drawLine(x, y, x, y);
		}
	}

	/**
	 * Display initial game title screen.
	 */
	private void initialMessage() {
		String gameTitleStr = "Funk Space";

		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameTitleStr);
		if(strWidth > this.getWidth() - 10){
			bigFont = currentFont;
			biggestFont = currentFont;
			fm = g2d.getFontMetrics(currentFont);
			strWidth = fm.stringWidth(gameTitleStr);
		}
		g2d.setFont(bigFont);
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2 - ascent;
		g2d.setPaint(new Color(0xb666d2));
		g2d.drawString(gameTitleStr, strX, strY);

		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Press <Space> to Start a New Game.";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(newGameStr, strX, strY);

		fm = g2d.getFontMetrics();
		String exitGameStr = "Press <Esc> to Exit the Game.";
		strWidth = fm.stringWidth(exitGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(exitGameStr, strX, strY);
	}

	/**
	 * Prepare screen for game over.
	 */
	public void doGameOver(){
		shipsValueLabel.setForeground(new Color(128, 0, 0));
		if (status.getScore() > 0) 
			pointsValueLabel.setForeground(Color.YELLOW);
		else
			pointsValueLabel.setForeground(new Color(128, 0, 0));

	}

	/**
	 * Prepare screen for "Game Won"!.
	 */
	public void doGameWon(){
		shipsValueLabel.setForeground(Color.YELLOW);
		pointsValueLabel.setForeground(Color.YELLOW);

	}

	/**
	 * Prepare screen for a new game.
	 */
	public void doNewGame(){		
		lastAsteroidTime = -NEW_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;

		bigFont = originalFont;
		biggestFont = null;

		// set labels' text
		pointsValueLabel.setForeground(Color.DARK_GRAY); 
		pointsValueLabel.setText("0");
		shipsValueLabel.setForeground(Color.DARK_GRAY);
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
		levelValueLabel.setText("1");

	}

	/**
	 * Sets the game graphics manager.
	 * @param graphicsMan the graphics manager
	 */
	public void setGraphicsMan(GraphicsManager graphicsMan) {
		this.graphicsMan = graphicsMan;
	}

	/**
	 * Sets the game logic handler
	 * @param gameLogic the game logic handler
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
		this.status = gameLogic.getStatus();
		this.soundMan = gameLogic.getSoundMan();
	}

	/**
	 * Sets the label that displays the value for asteroids destroyed.
	 * @param destroyedValueLabel the label to set
	 */
	public void setDestroyedValueLabel(JLabel destroyedValueLabel) {
		this.destroyedValueLabel = destroyedValueLabel;
	}

	/**
	 * Sets the label that displays the value for ship (lives) left
	 * @param shipsValueLabel the label to set
	 */
	public void setShipsValueLabel(JLabel shipsValueLabel) {
		this.shipsValueLabel = shipsValueLabel;
	}

	/**
	 * Sets the label that displays the value for current points scored.
	 * @param pointsValueLabel the label to set
	 */
	public void setPointsValueLabel(JLabel pointsValueLabel) {
		this.pointsValueLabel = pointsValueLabel;
	}

	/**
	 * Sets the label that displays the current level.
	 * @param levelNumber the label to set
	 */
	public void setLevelNumberLabel(JLabel levelNumber) {
		this.levelValueLabel = levelNumber;
	}

	/**
	 * Chooses the next location to draw asteroid based on its determined trajectory.
	 */
	public void chooseNextLocation(int k) {

		switch (asteroids.get(k).getTrajectory() % 2) {

		case 0:
			// vertical trajectory, so it can be drawn anywhere on screen
			asteroids.get(k).setLocation(getWidth() / 2 - 100 + rand.nextInt(getWidth() / 2),0); 
			break;
		case 1:
			// Trajectory is towards right, so draw it on the left side of the screen
			asteroids.get(k).setLocation(rand.nextInt(getWidth() / 3),0);
			break;
		case 2:
			// Trajectory is towards left, so draw it on the right side of the screen
			asteroids.get(k).setLocation(getWidth() - rand.nextInt(getWidth() / 3),0);
			break;
		}

	}


}