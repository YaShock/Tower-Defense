package ui;

import game.Entity;
import game.Level;
import game.TileType;
import game.Tower;
import game.TowerType;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import utility.Vector2d;

public class Game implements ActionListener, MouseListener {
	
	// Game
	private DrawPanel drawPanel; // DrawPanel draws all the entities and the background
	private final Vector2d drawPanelOriginalSize; // The original size of drawPanel after it's created
	@SuppressWarnings("unused")
	private TextureManager textureManager;
	private Level level; // Level that contains all the game entities
	private Timer timer; // Timer used to control fps
	private int gameSpeed = 2;
	
	// Determines if playing is currently buying a tower
	// (in that case a tower icon is dragged with the cursor
	private boolean buying = false;
	GameState gameState = GameState.Over;
	
	// Controls
	private JFrame window; // Main frame
	private JButton btnBuy; // Button used to buy towers
	private JComboBox<String> cbTower; // Combobox for choosing what tower to buy
	
	// Display items
	private JLabel lblLevel;
	private JLabel lblScore;
	private JLabel lblEnemies;
	private JLabel lblGold;
	
	// Menu items
	private JMenuBar menuBar;
	private JMenuItem mniNew, mniLoad, mniSave, mniPause, mniExit;
	private JMenuItem mniSpeedSlow, mniSpeedNorm, mniSpeedFast;
	private JMenu mnuFile, mnuOpts, mnuSpeed;
	
	// Layouts
	private JPanel panelDisplay; // Vertically aligns display labels
	private JPanel panelControls; // Vertically aligns controls
	private JPanel panelHorizontal; // Horizontally aligns the two vertical panels
	
	public Game() {
		// Initiliaze window
		window = new JFrame(); //creating instance of JFrame
		window.setTitle("Tower Defense");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BoxLayout boxLayout = new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS);
		window.setLayout(boxLayout);
		window.setMinimumSize(new Dimension(500, 475));
		
		// Initialize texture manager and load all images
		textureManager = new TextureManager();
		for(TextureID k : TextureID.values()) {
			TextureManager.load(k, k.area());
		}
		
		// Initialize game elements
		drawPanel = new DrawPanel();
		drawPanel.addMouseListener(this);
		level = new Level(drawPanel);
		
		// Initialize components
		initMenu();
		initDisplays();
		initControls();
		
		// Set layouts
		panelHorizontal = new JPanel();
		panelHorizontal.setLayout(new BoxLayout(panelHorizontal, BoxLayout.X_AXIS));
		panelHorizontal.add(Box.createRigidArea(new Dimension(20, 0)));
		panelHorizontal.add(panelDisplay);
		panelHorizontal.add(Box.createHorizontalGlue());
		panelHorizontal.add(panelControls);
		panelHorizontal.add(Box.createRigidArea(new Dimension(20, 0)));
		
		window.add(panelHorizontal);
		window.add(drawPanel);
        
		window.setSize(970, 785);
		window.setVisible(true);
		
		drawPanelOriginalSize = new Vector2d(drawPanel.getSize().width, drawPanel.getSize().height);

		level.setDisplayPanels(lblLevel, lblScore, lblEnemies, lblGold);
	}
	
	private void initDisplays() {
		// Initializes display labels and their panel
		lblLevel = new JLabel("Level: 0");
		lblLevel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		lblScore = new JLabel("Score: 0");
		lblScore.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		lblEnemies = new JLabel("Enemies remaining: 0");
		lblEnemies.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		lblGold = new JLabel("Gold: 0");
		lblGold.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		// Create and set display panel
		panelDisplay = new JPanel();
		panelDisplay.setLayout(new BoxLayout(panelDisplay, BoxLayout.Y_AXIS));
		panelDisplay.add(lblLevel);
		panelDisplay.add(lblGold);
		panelDisplay.add(lblScore);
		panelDisplay.add(lblEnemies);
		panelDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
	}
	
	private void initControls() {
		// Initialize controls
		btnBuy = new JButton("Buy");
		btnBuy.addActionListener(this);
		
		String[] towerStrings = {"Cannon " + TowerType.Cannon.cost(), "Magma " + TowerType.Magma.cost(), "Storm " + TowerType.Storm.cost()};
		cbTower = new JComboBox<String>(towerStrings);
		cbTower.setMaximumSize(new Dimension(200, 25));
		cbTower.addActionListener(this);

		// Create and set control panel
		panelControls = new JPanel();
		panelControls.add(Box.createRigidArea(new Dimension(0, 10)));
		panelControls.add(cbTower);
		panelControls.add(Box.createRigidArea(new Dimension(0, 10)));
		panelControls.add(btnBuy);
		panelControls.setLayout(new BoxLayout(panelControls, BoxLayout.Y_AXIS));
		panelControls.setMaximumSize(new Dimension(100, 80));
		panelControls.setAlignmentX(Component.RIGHT_ALIGNMENT);
	}
	
	private void initMenu() {
		// Initiliaze and set menu
		mniNew = new JMenuItem("New Game");
		mniLoad = new JMenuItem("Load Game");
		mniSave = new JMenuItem("Save Game");
		mniPause = new JMenuItem("Pause/Resume");
		mniExit = new JMenuItem("Exit");

		mniSpeedSlow = new JMenuItem("Slow");
		mniSpeedNorm = new JMenuItem("Normal");
		mniSpeedFast = new JMenuItem("Fast");
		  
		mniNew.addActionListener(this);
		mniLoad.addActionListener(this);
		mniSave.addActionListener(this);
		mniPause.addActionListener(this);
		mniExit.addActionListener(this);
		mniSpeedSlow.addActionListener(this);
		mniSpeedNorm.addActionListener(this);
		mniSpeedFast.addActionListener(this);
		  
		menuBar = new JMenuBar();
		  
		mnuFile = new JMenu("File");
		mnuOpts = new JMenu("Options");
		mnuSpeed = new JMenu("Speed");
		  
		mnuFile.add(mniNew);
		mnuFile.add(mniLoad);
		mnuFile.add(mniSave);
		mnuFile.add(mniPause);
		mnuFile.add(mniExit);
		mnuOpts.add(mnuSpeed);
		mnuSpeed.add(mniSpeedSlow);
		mnuSpeed.add(mniSpeedNorm);
		mnuSpeed.add(mniSpeedFast);
		  
		menuBar.add(mnuFile);
		menuBar.add(mnuOpts);
		
		window.setJMenuBar(menuBar);
	}
	
	private void start() {
		// If there was a timer already running, stop it
		if(timer != null)
			timer.cancel();
		
		// Set timer to run at 60 fps		
		timer = new Timer();
		timer.schedule(new TimerTick(), 0, 1000 / (30 * gameSpeed));
		// Set the game state
		gameState = GameState.Playing;
				
		// Start a new game
		level.startNewGame();
	}
	
	public void gameLoop() {
		// If player is currently buying a tower then draw the cursor tower
		// System.out.println(System.currentTimeMillis());
		if(gameState == GameState.Playing) {
			
			if(level.playerLost()) {
	            gameState = GameState.Over;
			}
			else if(level.playerWon()) {
				gameState = GameState.Over;
			}
			else {
				if(buying) {
					
					//System.out.println("Buying");
					//Point pos = MouseInfo.getPointerInfo().getLocation();
					Point pos = drawPanel.getMousePosition();					

					if(pos != null)
					{
						Vector2d zoom = new Vector2d((double)(drawPanel.getSize().width / drawPanelOriginalSize.x), (double)(drawPanel.getSize().height / drawPanelOriginalSize.y));
						if(drawPanel.getCursorTower() != null)
							drawPanel.getCursorTower().setPosition(new Vector2d((pos.x/zoom.x - Entity.TILE_SIZE/2), (pos.y/zoom.y - Entity.TILE_SIZE/2)));
						
						//System.out.println("Cursor: " + pos.x + ":" + pos.y);
						//System.out.println("Panel: " + drawPanel.getX() + ":" + drawPanel.getY());
					}
				}
				// Update game logic
				level.update();
				// Render elements
				level.render();
			}
		}
		else if(gameState == GameState.Paused)
            drawPanel.drawText("Paused");
		else if(gameState == GameState.Over) {
			if(level.playerWon())
				drawPanel.drawText("You have won the game!");
			else if(level.playerLost())
				drawPanel.drawText("You have lost the game!");
		}
		
		// Repaint drawing panel
		drawPanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mniExit)  
			window.dispose();
		
		else if(e.getSource() == mniSpeedSlow) {
			gameSpeed = 1;
		}
		else if(e.getSource() == mniSpeedNorm) {
			gameSpeed = 2;
		}
		else if(e.getSource() == mniSpeedFast) {
			gameSpeed = 3;
		}
		
		else if(e.getSource() == btnBuy) {
			// If game isn't running or already buying something then player can't puchase
			if(gameState != GameState.Playing || buying)
				return;
			
			// Get tower type from the combobox
			TowerType tp = TowerType.fromString((String)cbTower.getSelectedItem());
			
			if (level.getGold() >= tp.cost()) {
				level.addGold(-tp.cost());
				
				lblGold.setText("Gold: " + Integer.toString(level.getGold()));
				buying = true;
				drawPanel.setCursorTower(new Tower(tp, new Vector2d(0, -Entity.TILE_SIZE), level));
			}
		}
		
		else if(e.getSource() == mniNew) {
			start();
		}
		else if(e.getSource() == mniPause) {
			if (gameState == GameState.Playing) {
				gameState = GameState.Paused;
			}
			else if(gameState == GameState.Paused) {
				gameState = GameState.Playing;
			}
		}
		else if(e.getSource() == mniSave) {
			try {
				level.save();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(window, "Error writing file", "File error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
				return;
			} 
		}
		else if(e.getSource() == mniLoad) {
			try {
				level.load();
			} catch (ClassNotFoundException e1) {
				JOptionPane.showMessageDialog(window, "Error reading file, contents are corrupted", "File error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
				return;
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(window, "Error reading file, file is not found", "File error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
				return;
			} catch(ArrayStoreException e1) {
				JOptionPane.showMessageDialog(window, "Error reading file, contents are corrupted", "File error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			
			// If there was a timer already running, stop it
			if(timer != null)
				timer.cancel();
			
			// Set timer to run at 60 fps		
			timer = new Timer();
			timer.schedule(new TimerTick(), 0, 1000 / 60);
			// Set the game state
			gameState = GameState.Playing;
		}
		
		// If the game was already running when user changed the speed, update the timer schedule
		if(e.getSource() == mniSpeedSlow || e.getSource() == mniSpeedNorm || e.getSource() == mniSpeedFast) {
			if(timer != null) {
				timer.cancel();			
				// Set timer to run at 60 fps		
				timer = new Timer();
				timer.schedule(new TimerTick(), 0, 1000 / (30 * gameSpeed));
				// Set the game state
				gameState = GameState.Playing;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(buying) {
			// Get the position for tower by tiles
			Vector2d zoom = new Vector2d((double)(drawPanel.getSize().width / drawPanelOriginalSize.x), (double)(drawPanel.getSize().height / drawPanelOriginalSize.y));
			Vector2d normPos = new Vector2d((int)(e.getX()/zoom.x/Entity.TILE_SIZE), (int)(e.getY()/zoom.y/Entity.TILE_SIZE));
			//System.out.println("Normalized pos: " + normPos.x + ":" + normPos.y);
			// Create tower and add it to the map			
			if(level.getMap()[(int) normPos.y][(int) normPos.x] == TileType.Grass){
				
				Tower t = new Tower(drawPanel.getCursorTower().getType(), new Vector2d(normPos.x*Entity.TILE_SIZE, normPos.y*Entity.TILE_SIZE), level);
				level.addEntity(t);
				buying = false;
				drawPanel.setCursorTower(null);;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {		
	}
	
	private class TimerTick extends TimerTask {

        @Override
        public void run() {
        	gameLoop();
        }
    }
	
	private enum GameState {
		Over, Playing, Paused;
	}
}
