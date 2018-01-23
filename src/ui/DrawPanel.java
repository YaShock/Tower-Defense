package ui;

import game.Enemy;
import game.Entity;
import game.Tower;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

// DrawPanel is a JPanel that draws the level and its contents, displays graphics
public class DrawPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage backbuffer; // This is where everything's drawn before displaying on the panel
	private Graphics bbGraphics; // Graphics that draw on backbuffer
	private Tower cursorTower = null; // A tower under the cursor that appears when player is buying a tower
	private boolean paused = false;
	private FontMetrics fm = null;
	
	DrawPanel() {
		super();
		this.setDoubleBuffered(true);
        setBackground(Color.BLACK);
		
        // Create backbuffer
        backbuffer = new BufferedImage(960, 640, BufferedImage.TYPE_INT_ARGB);
        // Create graphics that draw on backbuffer
        bbGraphics = backbuffer.createGraphics();
        fm = bbGraphics.getFontMetrics();
	}
	
	private void render(Graphics g) {        
        // Draw cursor tower if player is currently placing a new tower
        if(cursorTower != null) {
        	bbGraphics.drawImage(TextureManager.get(cursorTower.getTextureID()), (int)cursorTower.getPosition().x, (int)cursorTower.getPosition().y, this);
        }

        // Render on screen
		// First draw the background
        g.drawImage(TextureManager.get(TextureID.Background), 0, 0, this);

		// Finally draw backbuffer, which contains the map to the panel
        g.drawImage(backbuffer, 0, 0, this.getWidth(), this.getHeight(), this);
        
        // Clear backbuffer if the game is not paused
        if(!paused) {
        	bbGraphics.drawImage(TextureManager.get(TextureID.Background), 0, 0, this);
        }
	}
	
	@Override public void paint(Graphics g) {
		// Repaint the panel
		super.paintComponent(g);
		// Draw all elements
        render(g);
	}
	
	public void drawEntity(Entity e) {
		bbGraphics.drawImage(TextureManager.get(e.getTextureID()), (int)e.getPosition().x, (int)e.getPosition().y, this);
		
		// If the entity is an enemy then display his current health as well
		if(e.getClass() == Enemy.class) {
			displayHealth((Enemy) e);
		}
	}
	
	public void displayHealth(Enemy e) {
		// Length represents the length of character string to be displayed
		int length = fm.charsWidth(Integer.toString(e.getHealth()).toCharArray(), 0, Integer.toString(e.getHealth()).length());
		// Draw the health of the enemy on the top, center of the enemy
		bbGraphics.drawString(Integer.toString(e.getHealth()), (int)(e.getCenterPosition().x - length/2), (int)(e.getPosition().y - 5));
	}
	
	public void setCursorTower(Tower t) {
		cursorTower = t;
	}
	
	public Tower getCursorTower() {
		return cursorTower;
	}
	
	public void drawText(String str) {
        bbGraphics.drawString(str, backbuffer.getWidth()/2 - fm.stringWidth(str)/2, backbuffer.getHeight()/2);
	}
}
