package game;

import ui.TextureID;
import utility.Vector2d;

public class Enemy extends Entity {

	private static final long serialVersionUID = 1461556172489934624L;
	static public int MAX_HP = 100;
	static public double MOVE_SPEED = 1;
	private Vector2d dir = new Vector2d(0, -1); // Direction in which the enemy moves, initialize to going up
	private int pixelsMoved = 0; // How much enemy moved on the current tile he's on
	private int hp = MAX_HP; // An enemy starts off 100 health points
	
	public Enemy(TextureID txtrKey, Vector2d pos, Level lvl) {
		super(txtrKey, pos, lvl);
	}
	
	void addHealth(int h) {
		hp += h;
	}
	
	public int getHealth() {
		return hp;
	}

	@Override
	public boolean update() {
		// Moves the enemy in the direction
		// If the enemy cannot move in the direction next turn then find a new direction
		// Enemy always moves on the road
		
		// If the enemy has no more health, destroy it
		if(hp <= 0)
			return true;
		
		// If enemy started moving one direction and hasn't gone a full tile then continue that diection
		if(pixelsMoved + MOVE_SPEED < Entity.TILE_SIZE) {
			move(new Vector2d(dir.x * MOVE_SPEED, dir.y * MOVE_SPEED));
			pixelsMoved += MOVE_SPEED;
			return false;
		}

		// If the enemy is at the final position then player lost the game
		if(getTilePosition().equals(new Vector2d(14, 0))) {
			level.playerLose();
			return false;
		}
		
		// If enemy has moved an entire tile then check if he can continue moving there
		
		if(canMove(dir)) {
			// Reset movement to zero to make go another tile	
			move(new Vector2d(dir.x * MOVE_SPEED, dir.y * MOVE_SPEED));		
			pixelsMoved = 0;
		}
		
		// Find a new direction to move
		else {
			dir = findDirection();
			move(new Vector2d(dir.x * MOVE_SPEED, dir.y * MOVE_SPEED));
			pixelsMoved = 0;
		}
		
		return false;
	}
	
	public boolean canMove(Vector2d dir) {
		
		// Get the tile position enemy is on
		Vector2d checkPos = new Vector2d(getTilePosition().x + dir.x, getTilePosition().y + dir.y);
		
		//System.out.println("Position: " + getTilePosition().x + ":" + getTilePosition().y);
		//System.out.println("Can move: " + checkPos.x + ":" + checkPos.y);
		
		// Check if the position enemy tries to go isn't out of boundries of level and is of road tile		
		return (level.getTile((int) checkPos.x, (int) checkPos.y) != null && level.getTile((int) checkPos.x, (int) checkPos.y) == TileType.Road);
	}
	
	public Vector2d findDirection() {
		
		// Find the direction enemy can move to and also set the texture so that enemy image is facing that direction		
		
		// Try moving right
		Vector2d newDir = new Vector2d(1, 0);
		
		// If the previous direction wasn't left and can move right then set new direction to right
		if(dir.equals(new Vector2d(-1, 0)) == false && canMove(newDir)) {
			setTextureID(TextureID.EnemyRight);
			return newDir;
		}		

		// Try moving left
		newDir.x = -1;
		newDir.y = 0;
		
		// If the previous direction wasn't right and can move left then set new direction to left
		if(dir.equals(new Vector2d(1, 0)) == false && canMove(newDir)) {
			setTextureID(TextureID.EnemyLeft);
			return newDir;
		}
		
		// Try moving down
		newDir.x = 0;
		newDir.y = 1;
		
		// If the previous direction wasn't up and can move down then set new direction to down
		if(dir.equals(new Vector2d(0, -1)) == false && canMove(newDir)) {
			setTextureID(TextureID.EnemyDown);
			return newDir;
		}
		
		// The only remaining direction is up
		newDir.x = 0;
		newDir.y = -1;
		setTextureID(TextureID.EnemyUp);
		return newDir;		
	}
}
