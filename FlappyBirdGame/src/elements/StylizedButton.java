package elements;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class StylizedButton {
	private Image unpressedButton;
	private int xPosition = 0, yPosition = 0;
	private int width, height;
	
	public StylizedButton() {
		ImageIcon iiu = new ImageIcon("./Images/unpressedButton.png");
		this.unpressedButton = iiu.getImage();
	}
	
	public void drawUnpressedButton(Graphics g, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = this.unpressedButton.getWidth(null);
		this.height = this.unpressedButton.getHeight(null);
		g.drawImage(this.unpressedButton, x, y, 257, 132, null);
	}

	public int getButtonWidth() {
		return width;
	}

	public void setButtonWidth(int width) {
		this.width = width;
	}

	public int getButtonHeight() {
		return height;
	}

	public void setButtonHeight(int height) {
		this.height = height;
	}

	public int getXPosition() {
		return xPosition;
	}

	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}

	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public Image getUnpressedButton() {
		return unpressedButton;
	}

	public void setUnpressedButton(Image unpressedButton) {
		this.unpressedButton = unpressedButton;
	}
}
