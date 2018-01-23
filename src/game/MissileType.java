package game;

import java.io.Serializable;

import ui.TextureID;

public enum MissileType implements Serializable {
	
	Cannon(10, TextureID.Cannonball, 3),
	Magma(8, TextureID.Fireball, 8),
	Storm(50, TextureID.Lightningball, 5);
	
	private final int damage;
	private final TextureID txtrID;
	private final int moveSpeed;
	
	MissileType(int damage, TextureID txtrID, int moveSpeed) {
		this.damage = damage;
		this.txtrID = txtrID;
		this.moveSpeed = moveSpeed;
	}
	
	public int damage() {
		return damage;
	}
	
	public TextureID textureID() {
		return txtrID;
	}
	
	public int moveSpeed() {
		return moveSpeed;
	}

}
