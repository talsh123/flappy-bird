package elements;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Background {
	private static Image background;
	private int xPosition, yPosition;
	
	public Background(int xPosition, int yPosition) {
		ImageIcon iib = new ImageIcon("./Images/background.jpg");
		Background.background = iib.getImage();
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public void drawBackground(Graphics g) {
		g.drawImage(Background.background, this.xPosition, this.yPosition, null);
	}
	
	public void move() {
		this.xPosition -= 4;
	}

	public Image getBackground() {
		return background;
	}

	public void setBackground(Image background) {
		Background.background = background;
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
