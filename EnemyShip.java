package rbadia.voidspace.model;

import java.util.Random;

import rbadia.voidspace.main.GameScreen;

/**
 * Represents enemy ship.
 * @author Derick Rodriguez
 *
 */
public class EnemyShip extends Ship {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_SPEED = 4;

	Random rand = new Random();
	/**
	 * Creates new enemy ship.
	 * @param screen the game screen
	 */
	public EnemyShip(GameScreen screen) {
		super(screen);
		this.setLocation(rand.nextInt(screen.getWidth() - super.getShipWidth()),0);
		//this.setLocation((screen.getWidth() - super.getShipWidth())/2,
				//screen.getHeight() - super.getShipHeight() - Y_OFFSET);
		
	}
	
	


}
