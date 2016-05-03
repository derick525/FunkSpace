package rbadia.voidspace.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;
import rbadia.voidspace.model.BossShip;


/**
 * Handles general game logic and status.
 */
public class GameLogic {
	protected GameScreen gameScreen;
	protected GameStatus status;
	private SoundManager soundMan;
	protected int maxAsteroids = 3;
	
	private Ship ship;
	protected List<Bullet> bullets;
	
	private EnemyShip enemyShip;
	protected List<Bullet> enemyBullets;
	
	private BossShip bossShip;
	protected List<Bullet> bossBullets;
	
	private ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>(maxAsteroids);
	
	
	/**
	 * Create a new game logic handler
	 * @param gameScreen the game screen
	 */
	public GameLogic(GameScreen gameScreen){
		this.gameScreen = gameScreen;
		
		// initialize game status information
		status = new GameStatus();
		// initialize the sound manager
		soundMan = new SoundManager();
		
		// init some variables
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		bossBullets = new ArrayList<Bullet>();	
		asteroids = new ArrayList<Asteroid>();  
	}

	/**
	 * Returns the game status
	 * @return the game status 
	 */
	public GameStatus getStatus() {
		return status;
	}

	/**
	 * Returns the sound manager
	 * @return the sound manager
	 */
	public SoundManager getSoundMan() {
		return soundMan;
	}

	/**
	 * Returns the game screen.
	 * @return the game screen
	 */
	public GameScreen getGameScreen() {
		return gameScreen;
	}

	/**
	 * Prepare for a new game.
	 */
	public void newGame(){  	
		
		status.setGameStarting(true);
		soundMan.playArcadeFunk();
		
		// init game variables
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		bossBullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();  
		
		status.setShipsLeft(3);
		status.setGameOver(false);
		status.setGameWon(false);
		status.setAsteroidsDestroyed(0);
			
		for (int i = 0; i < maxAsteroids; i++) {
        	status.setNewAsteroid(false, i);
        }
		
		// init the ship , enemy ship, boss and asteroids
        newShip(gameScreen);       
        newEnemyShip(gameScreen);      
        newBossShip(gameScreen);
          
		for (int i = 0; i < maxAsteroids; i++) {
	    	newAsteroid(gameScreen,i);
	    }

        // prepare game screen
        gameScreen.doNewGame();
        
        // delay to display "Get Ready" message for 1.5 seconds
		Timer timer = new Timer(1500, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameStarting(false);
				status.setGameStarted(true);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	

	/**
	 * Check game or level ending conditions.
	 */
	public void checkConditions(){
		// check game over conditions
		if(!status.isGameOver() && status.isGameStarted()){
			if(status.getShipsLeft() < 1){
				gameOver();
			}
		}
		// check game won conditions
		if(!status.isGameWon() && status.isGameStarted()) {
			if(gameScreen.bossDestroyed){
				gameWon();
			}
		}
	}
	
	/**
	 * Actions to take when the game is over.
	 */
	public void gameOver(){
		status.setGameStarted(false);
		status.setGameOver(true);
		gameScreen.doGameOver();
		
        // delay to display "Game Over" message for 3 seconds
		Timer timer = new Timer(3000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameOver(false);
				
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Actions to take when you win the game.
	 */
	public void gameWon(){
		status.setGameStarted(false);
		status.setGameWon(true);
		gameScreen.doGameWon();
		
        // delay to display "Game Over" message for 3 seconds
		Timer timer = new Timer(3000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameWon(false);
				gameScreen.bossDestroyed = false;
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Fire a bullet from ship.
	 */
	public void fireBullet(){
		Bullet bullet = new Bullet(ship);
		bullets.add(bullet);
		soundMan.playBulletSound();
	}
	
	/**
	 * Fire a bullet from the enemy ship.
	 */
	public void fireEnemyBullet(){
		Bullet bullet = new Bullet(enemyShip);
		enemyBullets.add(bullet);
		soundMan.playBulletSound();
	}
	
	/**
	 * Fire a bullet from the boss ship.
	 */
	public void fireBossBullet(){
		Bullet bullet = new Bullet(bossShip);
		bossBullets.add(bullet);
		soundMan.playBulletSound();
	}
	
	/**
	 * Move a bullet once fired.
	 * @param bullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBullet(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() >= 0){
			bullet.translate(0, -bullet.getSpeed());
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * Move an enemy bullet once fired.
	 * @param bullet the bullet to move
	 * @return if the bullet should be removed from the screen
	 */
	public boolean moveEnemyBullet(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() <= gameScreen.getHeight()){
			bullet.translate(0, bullet.getSpeed());
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * Create a new ship (and replace current one).
	 */
	public Ship newShip(GameScreen screen){
		this.ship = new Ship(screen);
		return ship;
	}
	
	/**
	 * Creates a new enemy ship (and replace current one).
	 */
	public EnemyShip newEnemyShip(GameScreen screen) {
		this.enemyShip = new EnemyShip(screen);
		return enemyShip;
	}
	
	/**
	 * Creates a new boss ship.
	 */
	public BossShip newBossShip(GameScreen screen) {
		this.bossShip = new BossShip(screen);
		return bossShip;
	}
	
	/**
	 * Create a new asteroid.
	 */
	public void newAsteroid(GameScreen screen, int i){
		this.asteroids.add(i, new Asteroid(screen));
		//return asteroids.get(i);
	}
	
	/**
	 * Returns the ship.
	 * @return the ship
	 */
	public Ship getShip() {
		return ship;
	}
	
	/**
	 * Returns the enemy ship.
	 * @return enemy ship
	 */
	public EnemyShip getEnemyShip() {
		return enemyShip;
	}
	
	/**
	 * Returns the boss ship.
	 * @return boss ship
	 */
	public BossShip getBossShip() {
		return bossShip;
	}

	/**
	 * Returns the asteroid.
	 * @return the asteroid
	 */
	public ArrayList<Asteroid> getAsteroids() {
		return asteroids;
	}

	/**
	 * Returns the list of bullets.
	 * @return the list of bullets
	 */
	public List<Bullet> getBullets() {
		return bullets;
	}
	
	/**
	 * Returns the list of enemy bullets.
	 * @return the list of enemy bullets
	 */
	public List<Bullet> getEnemyBullets() {
		return enemyBullets;
	}
	
	/**
	 * Returns the list of boss bullets.
	 * @return the list of boss bullets
	 */
	public List<Bullet> getBossBullets() {
		return bossBullets;
	}


}
