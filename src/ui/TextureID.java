package ui;

import java.awt.Rectangle;
import java.io.Serializable;


public enum TextureID implements Serializable {
	
	// Set filename for each TextureID and the size of the texture	
	Background ("res/map2.png", new Rectangle(0, 0, 960, 640)), TowerCannon ("res/tower cannon.png", new Rectangle(0, 0, 64, 64)),
	TowerMagma("res/tower magma.png", new Rectangle(0, 0, 64, 64)), TowerStorm("res/tower storm 2.png", new Rectangle(0, 0, 64, 64)),
	EnemyUp("res/zombies.png", new Rectangle(0, 0, 64, 64)), EnemyRight("res/zombies.png", new Rectangle(64, 0, 64, 64)),
	EnemyDown("res/zombies.png", new Rectangle(128, 0, 64, 64)), EnemyLeft("res/zombies.png", new Rectangle(192, 0, 64, 64)),
	Fireball("res/fireball.png", new Rectangle(0, 0, 25, 25)), Cannonball(("res/cannonball.png"), new Rectangle(0, 0, 25, 25)),
	Lightningball(("res/lb_small.png"), new Rectangle(0, 0, 20, 20));
			
	private final String filename; // Filename
	private final Rectangle area; // Texture area
	
	TextureID(String filename, Rectangle area) {
		this.filename = filename;
		this.area = area;
	}
	
	public String filename() {
		return filename;
	}
	
	public Rectangle area() {
		return area;
	}
}
