package elements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class Bird extends Thread implements Runnable{
	
	private int xPosition, yPosition;
	public static int birdWidth = 90, birdHeight= 70;
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

	public Image getBirdImage() {
		return birdImage;
	}

	public void setBirdImage(Image birdImage) {
		this.birdImage = birdImage;
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

	private Image birdImage;
	public boolean isAlive = false;

	private double currentImageAngle = 0;
	
	// Physics variables
	private int vertSpeed; // Negative - goes up,  Positive - goes down
	public static final int jumpSpeed = -20; // Constant speed of jump (When user clicks), goes 20 pixels up
	public static final int fallingConstant = 3; // Accelerates the falling speed of the bird. Constant variable.
	// Falls down 50 pixels each 0.5 a second
	
	public Bird(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.isAlive = false; // Game starts with opening menu
		this.vertSpeed = 0;
		
		ImageIcon iib = new ImageIcon("./Images/bird.png");
		this.birdImage = iib.getImage();
	}
	
	public void run() {
		try {
			while(this.isAlive && !Thread.interrupted()) {
				this.vertSpeed += Bird.fallingConstant;
				this.yPosition += this.vertSpeed;
				if (this.vertSpeed < 25 && this.currentImageAngle != -45) {
					rotateImage(-45);
				}
				else if(this.vertSpeed == 25 && this.currentImageAngle != 0) {
					rotateImage(0);
				}
				else if(this.vertSpeed > 25 && this.vertSpeed < 30 && this.currentImageAngle != 45) {
					rotateImage(45);
				}
				else if(this.vertSpeed > 30 && this.currentImageAngle != 90) {
					rotateImage(90);
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
	
	public void drawBird(Graphics g) {
		g.drawImage(birdImage, this.xPosition, this.yPosition, birdWidth, birdHeight, null);
	}
	
	public void drawBird(Graphics g, int x, int y) {
		g.drawImage(birdImage, x, y, birdWidth, birdHeight, null);
	}
	
	
	// Change this function so it gets a degree and it rotates the image to that degree no matter the former angle was
	void rotateImage(double degree) {
		try {
			BufferedImage blackCanvas = new BufferedImage(this.birdImage.getWidth(null), this.birdImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D)blackCanvas.getGraphics();
			g2.rotate(Math.toRadians(degree - this.currentImageAngle), this.birdImage.getWidth(null) / 2, this.birdImage.getHeight(null) / 2);
			g2.setClip(new Ellipse2D.Float(0, 0, this.birdImage.getWidth(null), this.birdImage.getHeight(null)));
			g2.drawImage(this.birdImage, 0, 0, null);
			this.birdImage = blackCanvas;
			currentImageAngle = degree;
		} catch(Exception e) {
			System.out.println(this.birdImage.getWidth(null));
		}
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
}
