package game;

import utility.Vector2d;

public class Tower extends Entity {
	
	private static final long serialVersionUID = -4663707384520451542L;
	private TowerType towerType = null;
	private int attackTime;

	public Tower(TowerType towerType, Vector2d pos, Level level) {
		super(towerType.textureID(), pos, level);		
		this.towerType = towerType;
		attackTime = towerType.attackDelay();
	}
	
	public TowerType getType() { 
		return towerType;
	}
	
	public void setType(TowerType tp) {
		towerType = tp;
		setTextureID(tp.textureID());
	}

	@Override
	public boolean update() {
		// Tower shoots every attackTime (inverse of attack speed) time
		// If attackTime is 0 then shoot a missile
		// Otherwise just decrement the attackTime
		attackTime--;
		
		if(attackTime > 0)
			return false;
		
		// Reset the attack time
		attackTime = towerType.attackDelay();;
		
		// Find the closest enemy to the tower
		Enemy enemy = level.findClosestEnemy(getCenterPosition());
		
		// Add a new missile to the map that moves in the direction of that enemy
		// Missile is of type that is determined by the type of the tower
		if(enemy != null) {			
			// Shoot that enemy only if he is within the tower attack range
			// Have to check the distance between tower's and enemy's center position
			int range = towerType.range() * Entity.TILE_SIZE; // This is the function range
			if(enemy.getCenterPosition().distance(getCenterPosition()).magnitude() <= range) {
				MissileType mslType = towerType.toMissileType();
				Vector2d towerPos = getCenterPosition();
				Vector2d mslSize = new Vector2d(mslType.textureID().area().width, mslType.textureID().area().height);
				level.addEntity(new Missile(mslType, new Vector2d(towerPos.x - mslSize.x/2, towerPos.y - mslSize.y/2), enemy, level));
			}
		}
		
		return false;
	}

}
