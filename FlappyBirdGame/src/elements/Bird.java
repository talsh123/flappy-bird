package elements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

// This class contains the Bird thread within the game.
// This Bird thread is being started and interrupted interchangeably, depending on the user's actions.
public class Bird extends Thread implements Runnable{
	
	private int xPosition, yPosition;
	private static BufferedImage birdImage;
	
//	Whether the bird is alive or not.
	public boolean isAlive = false;
	
//	The amount of degrees to rotate the image is calculated 
//	according to vertical speed.
	private int degreeToRotate;
	
	// Physics variables
	private int vertSpeed; // Vertical speed of the bird. The smaller, the more the bird vertical speed upwards the bird has.
	public static final int jumpSpeed = -20; // Constant speed of bird jump (When user clicks), goes 20 pixels up
	public static final int fallingConstant = 3; // Acts like gravity. fallingConstant (3) is being added to the vertSpeed 
//	of the bird constantly as time passes, accelerating the bird downwards. Constant variable.
	
	public Bird(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.isAlive = false; // Game starts with opening menu, bird thread is not starting.
		this.vertSpeed = 0;
		
		Bird.birdImage = LoadImage();
	}
	
//	The run method of the bird thread.
//	Changes the position of the bird vertically on screen according to vertSpeed.
//	Sets the degree to rotate the image according to vertSpeed.
	public void run() {
		try {
			while(this.isAlive && !Thread.interrupted()) {
				this.vertSpeed += Bird.fallingConstant;
				this.yPosition += this.vertSpeed;
//				Bird is rotated 45 degrees upwards
				if (this.vertSpeed <= 30) {
					this.degreeToRotate = -45;
				}
//				Bird is steady
				else if(this.vertSpeed > 30 && this.vertSpeed <= 45) {
					this.degreeToRotate = 0;
				}
//				Bird is rotated 45 degrees downwards
				else if(this.vertSpeed > 45 && this.vertSpeed <= 60) {
					this.degreeToRotate = 45;
				}
//				Bird is rotated 90 degrees downwards
				else if(this.vertSpeed > 60) {
					this.degreeToRotate = 90;
				}
				Thread.sleep(35);
			}
		} catch (InterruptedException e) {
		}
	}
	
//	Bird jumps. The vertical speed resets to the speed of the jump
//	and the bird position changes on screen.
	public void jump() {
		this.vertSpeed = Bird.jumpSpeed;
		this.yPosition += this.vertSpeed;
	}
	
//	Loads the bird image
	private BufferedImage LoadImage() {
		BufferedImage bird = null;
		try {
			bird = ImageIO.read(new File("./Images/bird.png"));
		} catch (IOException e) {
		}
		return bird;
	}
	
//	This method actually does the rotating and draws the bird on screen
	public void drawBird(Graphics g) {
		AffineTransform at = AffineTransform.getTranslateInstance(this.xPosition, this.yPosition);
		at.rotate(Math.toRadians(this.degreeToRotate), Bird.birdImage.getWidth() / 2, Bird.birdImage.getHeight() / 2);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(Bird.birdImage, at, null);
	}
	
//	Draws static bird image (used on main page)
	public void drawBird(Graphics g, int x, int y) {
		g.drawImage(Bird.birdImage, x, y, null);
	}
	
//	Plays the impact sound when the bird hits a pillar or the ground
	public void playImpactSound() {
		Clip clip = null;
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("./Sounds/impact.wav"));
			DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(inputStream);
			clip.loop(0);
		} catch(Exception e) {
			e.printStackTrace();
		} 
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

	public BufferedImage getBirdImage() {
		return birdImage;
	}

	public int getVertSpeed() {
		return vertSpeed;
	}

	public void setVertSpeed(int vertSpeed) {
		this.vertSpeed = vertSpeed;
	}
}
