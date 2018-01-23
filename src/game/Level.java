package game;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JLabel;

import ui.DrawPanel;
import ui.TextureID;
import utility.Vector2d;

public class Level {
	
	private DrawPanel drawPanel; // Reference to the draw panel which will draw our entities
	private CopyOnWriteArrayList<Entity> lstEntity; // List of entities
	private int gold = 250; // Gold that is available to player
	private int score = 0; // Score of the player
	private int levelCount = 0; // Current level count
	private TileType[][] map = null;
	private int spawning = 0; // Number of enemies yet to spawn
	private int enemyCount = 0; // Number of enemies remaining on the level
	private final int FINAL_LEVEL = 10; // The last level | number of levels
	private final int BREAK_TIME = 60*30; // Determines how much time player has between levels (60 fps * 30 seconds)
	private int breakCounter = 0; // Counting the break
	
	// Reference to display labels from the Game object
	private JLabel lblLevel = null;
	private JLabel lblScore = null;
	private JLabel lblEnemies = null;
	private JLabel lblGold = null;
	private boolean playerLost = false;
	private boolean playerWon = false;
	
	
	public Level(DrawPanel dp) {
		drawPanel = dp;
		lstEntity = new CopyOnWriteArrayList<Entity>();
		
		// Creating an two dimensional array of integers to represent the map for convinience
		// 0 is grass tile, 1 is road tile
		int intMap[][] = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1 },
				{ 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0 },
				{ 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0 },
				{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		};

		// Get a tile type map fron the integer map
		map = mapFromIntArray(intMap);
	}
	
	public void setDisplayPanels(JLabel level, JLabel score, JLabel enemies, JLabel lgold) {
		lblLevel = level;
		lblScore = score;
		lblEnemies = enemies;
		lblGold = lgold;
	}
	
	public TileType[][] mapFromIntArray(int arr[][]) {
		
		TileType[][] temp = new TileType[arr.length][arr[0].length];
		
		for(int y = 0; y < arr.length; y++) {
			for(int x = 0; x < arr[0].length; x++) {
				if(arr[y][x] == 0) {
					temp[y][x] = TileType.Grass;
				}
				else if (arr[y][x] == 1) {
					temp[y][x] = TileType.Road;
				}
			}
		}		
		return temp;
	}
	
	public void render() {
		// Draw all entities on the level		
		for (int i = 0; i < lstEntity.size(); i++) {
		    drawPanel.drawEntity(lstEntity.get(i));
		}
	}
		
	public void update() {
		// Update all entities on the level
		// If update returns true then remove the entity from the list		
		Iterator<Entity> iter = lstEntity.iterator();
		
		if(breakCounter > 0 && breakCounter < BREAK_TIME) {
			breakCounter++;
		}
		else if(breakCounter >= BREAK_TIME) {
			startNewLevel();
			breakCounter = 0;
		}
		
		while(iter.hasNext()) {
			Entity e = iter.next();
			if(e.update()) {
				lstEntity.remove(e);
				
				// Check if this was an enemy
				if(e.getClass() == Enemy.class) {
					// If so, change the unit count and update the label
					enemyCount--;
					lblEnemies.setText("Enemies remaining: " + enemyCount);
					
					// Also add score and update the label
					score += 50;
					lblScore.setText("Score: " + score);
					
					// Add gold to play
					addGold(20);
					
					// If there are no more enemies, start a new level
					if(enemyCount == 0) {
						// Increase level count
						levelCount++;
						// Give player some bonus gold
						addGold(200);
						
						if(levelCount > FINAL_LEVEL) {
							playerWon = true;
							return;
						}
						
						breakCounter = 1;
					}
				}
			}
		}
		
		// If there are more enemies to spawn and the there is no enemy on the starting tile or next to it, add an enemy
		if(spawning > 0) {
			boolean blocked = false;
			for (Entity e : lstEntity) {
				if(e.colliding(new Rectangle(0, Entity.TILE_SIZE * 9, Entity.TILE_SIZE - 1, Entity.TILE_SIZE - 1))) {
					blocked = true;
					break;
				}
			}
			
			if(!blocked) {
				addEntity(new Enemy(TextureID.EnemyUp, new Vector2d(0, Entity.TILE_SIZE * 9), this));
				spawning--;
			}
		}
	}
	
	public void startNewGame() {
		// If there were any entities remaining, delete them and reset the enemy count
		lstEntity.clear();
		enemyCount = 0;
		spawning = 0;
		lblEnemies.setText("Enemies remaining: 0");
		gold = 250;
		lblGold.setText("Gold: 250");
		score = 0;
		lblScore.setText("Score: 0");
		
		playerLost = false;
		playerWon = false;
		
		// Start the first level
		levelCount = 1;
		startNewLevel();
	}
	
	private void startNewLevel() {
		// If this was the last level, return from function and set the game state
		if(levelCount > FINAL_LEVEL) {
			playerWon = true;
			return;
		}
		
		// Update level label
		lblLevel.setText("Level " + levelCount);
		
		// Spawn enemies, the enemy count depends on the level
		spawnEnemies(levelCount * 5);
		Enemy.MAX_HP = 200 + ((levelCount - 1) * 10) + (int)Math.pow((levelCount - 1)*3, 2);
		Enemy.MOVE_SPEED = (levelCount / 5) + 1;
	}
	
	public void addEntity(Entity e) {
		lstEntity.add(e);
	}
	
	public int getGold() {
		return gold;
	}
	
	public void addGold(int g) {
		gold += g;
		lblGold.setText("Gold: " + gold);
	}
	
	public int getScore() {
		return score;
	}
	
	public TileType[][] getMap() {
		return map;
	}
	
	public TileType getTile(int x, int y) {
		// Check the boundries
		if(x < 0 || y < 0 || x > 14 || y > 9)
			return null;
		
		return map[y][x];
	}
	
	public void playerLose() {
		playerLost = true;
	}
	
	public boolean playerLost() {
		return playerLost;
	}
	
	public boolean playerWon() {
		return playerWon;
	}
	
	public void spawnEnemies(int cntEnemies) {
		// Add the param to spawning and existing enemy count
		spawning += cntEnemies;
		enemyCount += cntEnemies;
		lblEnemies.setText("Enemies remaining: " + enemyCount);
	}
	
	// Find the closest enemy to the position passed as argument and return its reference
	// If there are no enemies on the map a null reference is returned
	public Enemy findClosestEnemy(Vector2d pos) {
		if(lstEntity.size() == 0)
			return null;
		
		// An iterator that goes through the entities
		// Init it to the diagonal of the entire draw panel (the max distance)
		double distance = Vector2d.distance(new Vector2d(0, 0), new Vector2d(drawPanel.getWidth(), drawPanel.getHeight())).magnitude();
		// Reference to the closest enemy yet
		Enemy closestEnemy = null;
		
		// Go through all enemies and find the closest one		
		for (Entity e : lstEntity) {
			// Check only enemies
			if(e.getClass() == Enemy.class) {
				double tempDist = pos.distance(e.getCenterPosition()).magnitude();
				if(tempDist < distance) {
					distance = tempDist;
					closestEnemy = (Enemy) e;
				}
			}
		}
		
		return closestEnemy;
	}
	
	
	// Find the closest enemy to the position passed as argument and return its reference
	// If there are no enemies on the map a null reference is returned
	public Enemy findClosestBounce(Enemy enemy, Enemy exclude) {
		if(lstEntity.size() == 0)
			return null;
		
		// An iterator that goes through the entities
		// Init it to the diagonal of the entire draw panel (the max distance)
		double distance = Vector2d.distance(new Vector2d(0, 0), new Vector2d(drawPanel.getWidth(), drawPanel.getHeight())).magnitude();
		// Reference to the closest enemy yet
		Enemy closestEnemy = null;
		
		// Go through all enemies and find the closest one		
		for (Entity e : lstEntity) {
			// Check only enemies that are not identical to the original or the excluded
			if(e.getClass() == Enemy.class && e != enemy && e != exclude) {
				double tempDist = enemy.getCenterPosition().distance(e.getCenterPosition()).magnitude();
				if(tempDist < distance) {
					distance = tempDist;
					closestEnemy = (Enemy) e;
				}
			}
		}
		
		return closestEnemy;
	}
	
	// Searches for the enemies in a specific area
	// @tilePos - The tile of the area
	// @rad - Radius of the area (in tiles)
	// @return - The list of the enemies in the area
	public ArrayList<Enemy> getEnemiesInArea(Vector2d tilePos, int rad) {
		ArrayList<Enemy> list = new ArrayList<Enemy>();
		
		for(Entity e : lstEntity) {
			// Ignore non-enemy entities
			if(e.getClass() != Enemy.class)
				continue;
			
			Vector2d distance = Vector2d.subtract(e.getTilePosition(), tilePos);
			
			// If the enemy is next to the position
			if(Math.abs(distance.x) <= 1 && Math.abs(distance.y) <= 1) {
				list.add((Enemy) e);
			}
		}
		
		return list;
	}
	
	public void save() throws IOException {
		// Create and load files
		FileOutputStream fileOut = new FileOutputStream("res/save.td");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
					
		// Write all objects to files
        out.writeObject(gold);
        out.writeObject(score);
        out.writeObject(levelCount);
        out.writeObject(map);
        out.writeObject(spawning);
        out.writeObject(enemyCount);
        out.writeObject(breakCounter);
        out.writeObject(lstEntity);
        
        // Close files
        out.close();
        fileOut.close();
	}
	
	@SuppressWarnings("unchecked")
	public void load() throws ClassNotFoundException, IOException {
		// Create and load files
		FileInputStream fileIn = new FileInputStream("res/save.td");
		ObjectInputStream in = new ObjectInputStream(fileIn);			
		
		// Read all objects and convert them
		gold = (int) in.readObject();
		score = (int) in.readObject();
		levelCount = (int) in.readObject();
		map = (TileType[][]) in.readObject();
		spawning = (int) in.readObject();
		enemyCount = (int) in.readObject();
		breakCounter = (int) in.readObject();
		lstEntity = (CopyOnWriteArrayList<Entity>) in.readObject();
		
		// Set the displays according to loaded data

		lblGold.setText("Gold: " + gold);
		lblScore.setText("Score: " + score);
		lblEnemies.setText("Enemies remaining: " + enemyCount);
		lblLevel.setText("Level " + levelCount);
		
		// For each entity loaded set the level
		for (Entity e : lstEntity) {
			e.setLevel(this);
		}
        
		// Close files
        in.close();
        fileIn.close();
	}
}
