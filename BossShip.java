package rbadia.voidspace.model;

import rbadia.voidspace.main.GameScreen;
/**
 * Represents boss ship.
 * 
 *
 */

public class BossShip extends Ship {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int DEFAULT_HEALTH = 50;
	private int bossWidth = 50;
	private int bossHeight = 50;
	private int bossHealth = 50;
	
	/**
	 * Creates new enemy ship.
	 * @param screen the game screen
	 */
	public BossShip(GameScreen screen) {
		super(screen);
		this.setLocation(screen.getWidth() / 2 - this.getShipWidth()/2,
						 screen.getHeight() / 2 - this.getShipHeight()/2);	
	}

	/**
	 * Returns width of boss ship.
	 * @return the bossWidth
	 */
	public int getBossWidth() {
		return bossWidth;
	}

	/**
	 * Returns height of boss ship.
	 * @return the bossHeight
	 */
	public int getBossHeight() {
		return bossHeight;
	}

	/**
	 * Returns boss' current health.
	 * @return bossHealth current boss health.
	 */
	public int getBossHealth() {
		return bossHealth;
	}

	/**
	 * Sets boss' current health.
	 * @param bossHealth the health value to set to boss ship.
	 */
	public void setBossHealth(int bossHealth) {
		this.bossHealth = bossHealth;
	}

	/**
	 * Returns the default health value for boss ship.
	 * @return DEFAULT_HEALTH the default health
	 */
	public int getDefaultHealth() {
		return DEFAULT_HEALTH;
	}
	

}
