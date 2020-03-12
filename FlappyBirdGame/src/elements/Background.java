package elements;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Background {
	private Image background;
	private int xPosition, yPosition;
	
	public Background(int xPosition, int yPosition) {
		ImageIcon iib = new ImageIcon("./Images/background.jpg");
		this.background = iib.getImage();
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public void drawBackground(Graphics g) {
		g.drawImage(this.background, this.xPosition, this.yPosition, null);
	}
	
	public void move() {
		this.xPosition -= 4;
	}

	public Image getBackground() {
		return background;
	}

	public void setBackground(Image background) {
		this.background = background;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}
}
