package game;

import java.awt.Rectangle;
import java.io.Serializable;

import ui.TextureID;
import utility.Vector2d;

public abstract class Entity implements Serializable {
	
	private static final long serialVersionUID = -4447200032995065268L;
	transient protected Level level; // Reference to the level
	private TextureID txtrID;
	private Vector2d pos;
	static public final int TILE_SIZE = 64;
	// By default Entity's size is the same as TILE_SIZE, but it can be changed (for missiles)
	private Vector2d size = new Vector2d(TILE_SIZE, TILE_SIZE);
	
	public Entity(TextureID txtrKey, Vector2d pos, Level level) {
		this.txtrID = txtrKey;
		this.pos = pos;
		this.level = level;
	}
	
	public void setLevel(Level lvl) {
		level = lvl;
	}
	
	public Vector2d getPosition() {
		return pos;
	}
	
	public void setPosition(Vector2d pos) {
		this.pos = pos;
	}
	
	// Return the center position of the entity	
	public Vector2d getCenterPosition() {
		return new Vector2d(pos.x + size.x / 2, pos.y + size.y / 2);
	}
	
	public Vector2d getTilePosition() {
		// Converts the absolute center of the entity to tile position
		return new Vector2d((int) (getCenterPosition().x / TILE_SIZE), (int) (getCenterPosition().y / TILE_SIZE));
	}
	
	public Vector2d getSize() {
		return size;
	}
	
	public void setSize(Vector2d v) {
		size = v;
	}
	
	public boolean colliding(Rectangle area) {
		
		// Check if area is inside the boundries of the entity
		if (area.x + area.width < getPosition().x 	||
			area.x > getPosition().x + size.x		||
			area.y + area.height < getPosition().y	||
			area.y > getPosition().y + size.y		)
			return false;
		else return true;
	}
	
	public boolean colliding(Entity e) {
		// Convert boundries of argument entity into a rectangle
		return colliding(new Rectangle((int)e.getPosition().x, (int)e.getPosition().y, (int)size.x, (int)size.y));
	}
	
	public void move(Vector2d dir) {
		// Enemy moves in that direction
		// Add direction to the current position and set the result as the new position
		setPosition(Vector2d.add(getPosition(), dir));
	}
	
	protected void setTextureID(TextureID id) {
		txtrID = id;
	}
	
	public TextureID getTextureID() {
		return txtrID;
	}
	
	// Returns true if the entity is to be destroy
	abstract public boolean update();
	
}
