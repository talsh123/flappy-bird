package elements;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

// This class contains a button a use can click to log out
// and to select a level
public class StylizedButton {
//	The buttons, greyscaled and normal
	private Image unpressedButton;
	private Image unpressedButtonGreyScale;
//	X and Y positions on screen
	private int xPosition = 0, yPosition = 0;
	private int width, height;
	
	public StylizedButton() {
		ImageIcon iiu = new ImageIcon("./Images/unpressedButton.png");
		this.unpressedButton = iiu.getImage();
		ImageIcon iib = new ImageIcon("./Images/unpressedButtonGreyScale.png");
		this.unpressedButtonGreyScale = iib.getImage();
	}
	
//	Draws normal button on screen
	public void drawUnpressedButton(Graphics g, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = this.unpressedButton.getWidth(null);
		this.height = this.unpressedButton.getHeight(null);
		g.drawImage(this.unpressedButton, x, y, 257, 132, null);
	}
	
//	Draws greyscaled button on screen
	public void drawUnpressedButtonGreyScale(Graphics g, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = this.unpressedButtonGreyScale.getWidth(null);
		this.height = this.unpressedButtonGreyScale.getHeight(null);
		g.drawImage(this.unpressedButtonGreyScale, x, y, 257, 132, null);
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
	
	public Image getUnpressedButtonGreyScale() {
		return unpressedButtonGreyScale;
	}

	public void SetUnpressedButtonGreyScale(Image unpressedButtonGreyScale) {
		this.unpressedButtonGreyScale = unpressedButtonGreyScale;
	}
}
