package rbadia.voidspace.main;

/**
 * Container for game flags and/or status variables.
 */
public class GameStatus {
	// game flags
	private boolean gameStarted = false;
	private boolean gameStarting = false;
	private boolean gameOver = false;
	private boolean gameWon = false;
	
	// status variables
	private boolean newShip;
	private boolean newEnemyShip;
	private long asteroidsDestroyed = 0;
	private int shipsLeft;
	private long score = 0;
	private int level = 1;
	private boolean[] newAsteroids = new boolean[3];
	
	public GameStatus(){
		
	}
	
	/**
	 * Indicates if the game has already started or not.
	 * @return if the game has already started or not
	 */
	public synchronized boolean isGameStarted() {
		return gameStarted;
	}
	
	public synchronized void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}
	
	/**
	 * Indicates if the game is starting ("Get Ready" message is displaying) or not.
	 * @return if the game is starting or not.
	 */
	public synchronized boolean isGameStarting() {
		return gameStarting;
	}
	
	public synchronized void setGameStarting(boolean gameStarting) {
		this.gameStarting = gameStarting;
	}
	
	/**
	 * Indicates if the game has ended and the "Game Over" message is displaying.
	 * @return if the game has ended and the "Game Over" message is displaying.
	 */
	public synchronized boolean isGameOver() {
		return gameOver;
	}
	
	public synchronized void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	
	/**
	 * Indicates if the game has ended and the "Game Won!" message is displaying.
	 * @return if the game has ended and the "Game Won!" message is displaying.
	 */
	public synchronized boolean isGameWon() {
		return gameWon;
	}
	
	public synchronized void setGameWon(boolean gameWon) {
		this.gameWon = gameWon;
	}
	
	/**
	 * Indicates if a new ship should be created/drawn.
	 * @return if a new ship should be created/drawn
	 */
	public synchronized boolean isNewShip() {
		return newShip;
	}

	public synchronized void setNewShip(boolean newShip) {
		this.newShip = newShip;
	}
	
	/**
	 * Indicates if a new enemy ship should be created/drawn.
	 * @return if a new enemy ship should be created/drawn
	 */
	public synchronized boolean isNewEnemyShip() {
		return newEnemyShip;
	}

	public synchronized void setNewEnemyShip(boolean newEnemyShip) {
		this.newEnemyShip = newEnemyShip;
	}
	
	/**
	 * Indicates if a new asteroid should be created/drawn.
	 * @return if a new asteroid should be created/drawn
	 */	
	public synchronized boolean[] isNewAsteroid() {
		return newAsteroids;
	}
	
	public synchronized void setNewAsteroid(boolean newAsteroid, int i) {
		this.newAsteroids[i] = newAsteroid;
	}

	/**
	 * Returns the number of asteroid destroyed. 
	 * @return the number of asteroid destroyed
	 */
	public synchronized long getAsteroidsDestroyed() {
		return asteroidsDestroyed;
	}

	public synchronized void setAsteroidsDestroyed(long asteroidsDestroyed) {
		this.asteroidsDestroyed = asteroidsDestroyed;
	}

	/**
	 * Returns the number ships/lives left.
	 * @return the number ships left
	 */
	public synchronized int getShipsLeft() {
		return shipsLeft;
	}

	public synchronized void setShipsLeft(int shipsLeft) {
		this.shipsLeft = shipsLeft;
	}
	
	/**
	 * Returns current score.
	 * @return current score
	 */
	public synchronized long getScore() {
		return score;
	} 
	
	public synchronized void setScore(long score) {
		this.score = score;
	} 
	
	/**
	 * Returns current level.
	 * @return current level
	 */
	public synchronized int getLevel() {
		return level;
	} 
	
	public synchronized void setLevel(int level) {
		this.level = level;
	} 
}
