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

public class Bird extends Thread implements Runnable{
	
	private int xPosition, yPosition;
	private static BufferedImage birdImage;
	public boolean isAlive = false;
	private double currentImageAngle = 0;
	private int degreeToRotate;
	
	// Physics variables
	private int vertSpeed; // Negative - goes up,  Positive - goes down
	public static final int jumpSpeed = -20; // Constant speed of jump (When user clicks), goes 20 pixels up
	public static final int fallingConstant = 3; // Accelerates the falling speed of the bird. Constant variable.
	
	public Bird(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.isAlive = false; // Game starts with opening menu
		this.vertSpeed = 0;
		
		Bird.birdImage = LoadImage();
	}
	
	public void run() {
		try {
			while(this.isAlive && !Thread.interrupted()) {
				this.vertSpeed += Bird.fallingConstant;
				this.yPosition += this.vertSpeed;
				if (this.vertSpeed < 25 && this.currentImageAngle != -45) {
					this.degreeToRotate = -45;
				}
				else if(this.vertSpeed == 25 && this.currentImageAngle != 0) {
					this.degreeToRotate = 0;
				}
				else if(this.vertSpeed > 25 && this.vertSpeed < 30 && this.currentImageAngle != 45) {
					this.degreeToRotate = 45;
				}
				else if(this.vertSpeed > 30 && this.currentImageAngle != 90) {
					this.degreeToRotate = 90;
				}
				Thread.sleep(35);
			}
		} catch (InterruptedException e) {
		}
	}
	
	public void jump() {
		this.vertSpeed = Bird.jumpSpeed;
		this.yPosition += this.vertSpeed;
	}
	
	private BufferedImage LoadImage() {
		BufferedImage bird = null;
		
		try {
			bird = ImageIO.read(new File("./Images/bird.png"));
		} catch (IOException e) {
		}
		
		return bird;
	}
	
	public void drawBird(Graphics g) {
		AffineTransform at = AffineTransform.getTranslateInstance(this.xPosition, this.yPosition);
		at.rotate(Math.toRadians(this.degreeToRotate), Bird.birdImage.getWidth() / 2, Bird.birdImage.getHeight() / 2);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(Bird.birdImage, at, null);
	}
	
	public void drawBird(Graphics g, int x, int y) {
		g.drawImage(Bird.birdImage, x, y, null);
	}
	
	public void playImpactSound() {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("./Sounds/impact.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
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

	public double getCurrentImageAngle() {
		return currentImageAngle;
	}
	
	public void setCurrentImageAngle(double currentImageAngle) {
		this.currentImageAngle = currentImageAngle;
	}

	public int getVertSpeed() {
		return vertSpeed;
	}

	public void setVertSpeed(int vertSpeed) {
		this.vertSpeed = vertSpeed;
	}
}
