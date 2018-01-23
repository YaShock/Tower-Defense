package game;

import java.io.Serializable;

import ui.TextureID;

public enum TowerType implements Serializable {
	Cannon (100, 5, TextureID.TowerCannon, 30),
	Magma (200, 2, TextureID.TowerMagma, 10),
	Storm (500, 10, TextureID.TowerStorm, 200);
	
	private final int cost;
	private final int range;
	private final TextureID txtrID;
	private final int attackDelay;
	
	TowerType(int cost, int range, TextureID txtrKey, int attackDelay) {
		this.cost = cost;
		this.range = range;
		this.txtrID = txtrKey;
		this.attackDelay = attackDelay;
	}
	
	public TextureID textureID() {
		return txtrID;
	}
	
	public int cost() {
		return cost;
	}
	
	public int range() {
		return range;
	}
	
	public int attackDelay() {
		return attackDelay;
	}
	
	// Get a MissileType from the TowerType
	public MissileType toMissileType() {
		switch (this) {		
			case Cannon:
				return MissileType.Cannon;
			case Magma:
				return MissileType.Magma;
			case Storm:
				return MissileType.Storm;
			default:
				return null;			
		}
	}
	
	// Check the beginning of the strong and return a TowerType accordingly
	static public TowerType fromString(String s) {
		if(s.startsWith("Cannon")) {
			return TowerType.Cannon;
		}
		else if(s.startsWith("Magma")) {
			return TowerType.Magma;
		}
		else if(s.startsWith("Storm")) {
			return TowerType.Storm;
		}
		else return null;
	}
}
