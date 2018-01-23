package game;

import java.util.ArrayList;

import utility.Vector2d;

public class Missile extends Entity {
	
	private static final long serialVersionUID = -2597887591168858332L;
	MissileType type; // Type of the missile
	Vector2d dir; // Direction in which the missile is moving
	Enemy dest; // Destination enemy of the missile
	Enemy prevDest = null; // Previous destination, used for storm missiles
	int bounces = 0;
	
	public Missile(MissileType type, Vector2d pos, Enemy dest, Level level) {
		super(type.textureID(), pos, level);
		this.dest = dest;
		this.type = type;
		
		// Set appropriate size for missile
		setSize(new Vector2d(getTextureID().area().width, getTextureID().area().height));
	}

	@Override
	public boolean update() {
		// Adjust the direction to the destination entity
		// Get the vector between destination and current position of the missile
		dir = Vector2d.subtract(dest.getCenterPosition(), getCenterPosition());
		// Normalize the vector
		dir.normalize();
		
		// Move in the direction with the speed of the missile type
		move(Vector2d.multiply(dir, type.moveSpeed()));
		
		// Check collision with the enemy
		// If they collide, destroy the missile and reduce the health of the enemy
		
		//System.out.println("Missile: " + getPosition().x + "; " + getPosition().y + "; " + Double.toString(getPosition().x + getSize().x) + "; " + Double.toString(getPosition().y + getSize().y));
		//System.out.println("Enemy: " + dest.getPosition().x + "; " + dest.getPosition().y + "; " + Double.toString((dest.getPosition().x + dest.getSize().x)) + "; " + Double.toString(dest.getPosition().y + dest.getSize().y));
		
		if (dest.colliding(this) /*colliding(dest)*/) {
			// Magma does splash damage to surrounding enemies
			if(type == MissileType.Magma) {
				splashDamage(type.damage()/2);
			}

			// All missiles deal damage to the main target
			// Damage also depends on bounces (if it's the storm missile), it deals less with each bounce
			dest.addHealth(-type.damage()/(bounces+1));
			
			if(type == MissileType.Storm && bounces < 2) {
				// Check if it can bounce to the closest enemy
				Enemy temp = dest;
				dest = bounce();
				prevDest = temp;
				// If it found an enemy then preserve it, otherwise destroy the missile
				return (dest == null);
			}
			
			return true;
		}
		else return false;
	}
	
	public MissileType getType() {
		return type;
	}
	
	public void splashDamage(int dmg) {
		ArrayList<Enemy> lst = level.getEnemiesInArea(dest.getTilePosition(), 1);
		
		// Damage all enemies in the area except for the main target (he receives full damage)
		for(Enemy e : lst) {
			if(e != dest) {
				e.addHealth(-dmg);
			}
		}
	}
	
	public Enemy bounce() {
		bounces++;
		return level.findClosestBounce(dest, prevDest);
	}

}
