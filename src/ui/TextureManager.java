package ui;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.util.HashMap;

// This is the class that is responsible for storing and loading iamges/textures
// This class is a singleton; only one instance can exist at a time

public class TextureManager {
	
	private static TextureManager instance = null;
	private HashMap<TextureID, Image> mTextures;
	private Toolkit t;
	
	// Creates the single instance of TextureManager
	// If an instance already exists then IllegalStateException is thrown
	TextureManager() {
		if(instance != null)
			throw new IllegalStateException("Already instantiated");
		
		instance = this;
		t = Toolkit.getDefaultToolkit();

		mTextures = new HashMap<TextureID, Image>();
	}
	
	
	// Loads an image and stores it in the map
	// If an image was already loaded then NullPointerException is thrown
	static public void load(TextureID key) {
		
		Image image = instance.t.createImage(key.filename());		
		if(image == null) {
			throw new NullPointerException("Failed to load image: " + key.filename());
		}
		
		// Try to put image in the map
		instance.mTextures.put(key, image);
	}
	
	// Loads an image and stores it in the map
	// If an image was already loaded then NullPointerException is thrown
	// r - The area of texture to be cut out
	static public void load(TextureID key, Rectangle r) {
		
		Image image = instance.t.createImage(key.filename());
		if(image == null) {
			throw new NullPointerException("Failed to load image: " + key.filename());
		}
		
        Image filteredImage = instance.t.createImage(new FilteredImageSource(image.getSource(), new CropImageFilter(r.x, r.y, r.width, r.height)));
		
		// Try to put image in the map
		instance.mTextures.put(key, filteredImage);
	}
	
	// Return reference to the image
	// If failed then NullPointerException is thrown
	static public Image get(TextureID id) {
		Image image = instance.mTextures.get(id);
		
		if(image == null)
			throw new NullPointerException("Failed to get image " + id.filename());		
		return image;
	}	
}
