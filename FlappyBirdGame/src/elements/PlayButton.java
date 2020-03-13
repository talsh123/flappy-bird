package elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;

public class PlayButton {
	
	private int x,y;
	private Image playButtonImage;
	boolean changeAnimationDirection;
	String startGameText = "Start Game";
	public final static int playButtonWidth = 200, playButtonHeight = 200;
	
	public PlayButton(int x, int y) {
		this.x = x;
		this.y = y;
		
		ImageIcon iip = new ImageIcon("./Images/playIcon.png");
		playButtonImage = iip.getImage();
		
		changeAnimationDirection = false;
	}
	
	public Font getFont() {
		Font font = null;
		try {
			URL fontURL = new URL("file:./Fonts/PixalatedFont.ttf");
			
			font = Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream());
			font = font.deriveFont(Font.PLAIN, 65);

		} catch (FontFormatException | IOException e) {
			
			e.printStackTrace();
		}
		return font;
	}

	public void changeButtonPosition() {
		if(!changeAnimationDirection) {
			this.y++;
		}
		else {
			this.y--;
		}
		if(y == 270)
			changeAnimationDirection = true;
		if(y == 230)
			changeAnimationDirection = false;
	}
	
	public void drawPlayButton(Graphics g) {
		g.drawImage(this.playButtonImage, this.x, this.y, PlayButton.playButtonWidth, PlayButton.playButtonHeight, null);
		g.setFont(getFont());
		Color darkGreen =  Color.decode("#006400");
		g.setColor(darkGreen);
		g.drawString(startGameText, this.x - 70, this.y + 250);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
