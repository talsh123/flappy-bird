package elements;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class PillarStructure{
	
	private int bottomY, upperY;
	private Image pillarImage;
	
	public static final int pillarWidth= 159;
	public static final int pillarHeight= 471;
	
	private int distance = 0; // x distance from 1280, 1280 is where pillars when drawn for first time
	
	private int upperHeight;
	private int bottomHeight;
	
	private boolean isPassed;

	public PillarStructure() {
		ImageIcon bic = new ImageIcon("./Images/pillar.png");
		pillarImage = bic.getImage();
		this.isPassed = false;
		
		initRectangles();
	}
	
	public Image getPillarImage() {
		return pillarImage;
	}

	public void setPillarImage(Image pillarImage) {
		this.pillarImage = pillarImage;
	}

	private void initRectangles() {
		// Randomizes the height for one of the pillars so the space for the bird to go through is in different places on the board
		// 720 - 200 - 150 = 370 // maximum height for upper pillar, 150 minimum height for upper pillar
		this.bottomHeight = (int) Math.round((Math.random() * ((370 - 150) + 1)) + 150); 
		// Open Space for Bird to go through is 200px, minimum height size for a pillar is 150px
		// Sizes are negative because the rectangle needs to be upside down
		this.bottomY = 720 - bottomHeight; // Y position where upper pillar is drawn
		this.upperHeight = 720 - bottomHeight - 200;
		this.upperY = 0 - (PillarStructure.pillarHeight - upperHeight); // Y position where bottom pillar is drawn
	}
	
	public int getxPosition() {
		return 1280 - this.distance;
	}
	
	public int getUpperHeight() {
		return upperHeight;
	}

	public void setUpperHeight(int upperHeight) {
		this.upperHeight = upperHeight;
	}

	public int getBottomHeight() {
		return bottomHeight;
	}

	public void setBottomHeight(int bottomHeight) {
		this.bottomHeight = bottomHeight;
	}

	public void drawPillar(Graphics g) {
		g.drawImage(this.pillarImage, 1280 - this.distance, this.upperY + PillarStructure.pillarHeight, PillarStructure.pillarWidth, -PillarStructure.pillarHeight, null);// Upper Pillar
		g.drawImage(this.pillarImage, 1280 - this.distance, this.bottomY, PillarStructure.pillarWidth, PillarStructure.pillarHeight, null);// Bottom Pillar
	}
	
	public int getBottomY() {
		return bottomY;
	}

	public void setBottomY(int bottomY) {
		this.bottomY = bottomY;
	}

	public int getUpperY() {
		return upperY;
	}

	public void setUpperY(int upperY) {
		this.upperY = upperY;
	}

	public void move() {
		this.distance += 4;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public boolean isPassed() {
		return isPassed;
	}

	public void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}
}
