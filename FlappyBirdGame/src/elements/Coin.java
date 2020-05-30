package elements;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

// This class contains a coin the can be collected by the bird.
// Every coin is a thread, the coin thread dies when it leaves the screen.
public class Coin extends Thread implements Runnable{
//	X and Y position on screen.
	private int yPosition, xPosition, distance;
//	Pillars to the left and the right, used to randomise the position of the coin on screen.
	private PillarStructure leftPillar, rightPillar;
	private Image coin;
//	Rectangle around the coin, being used to make sure Mines and Coin aren't overlapping each other.
	private Rectangle coinRect;
	
//	Whether the coin has been collected or not.
	private boolean isCollected;
	
//	Three stages for the THREAD:
//	RUNNING - The thread is running from the point of creation (Thread.start())
//	INTERRUPTED - The thread has been set the status of interrupted
//	but it has not yet been Thread.interrupt(). I do this because the thread hasn't yet to
//	leave the screen, and due to how the game is designed, only 2 coin threads need to be
//	inside the ArrayList in order to be generated correctly.
//	DEAD - The thread has actually been interrupted using Thread.interrupt().
//	The thread has left the screen.
	private static enum Status
	{
		RUNNING, INTERRUPTED, DEAD
	}
	
//	Coin Dimensions
	public final static int coinWidth = 60, coinHeight = 60;

//	Status of the thread
	private Status status;
	
	public Coin(PillarStructure leftPillar, PillarStructure rightPillar) {
		this.coin = Toolkit.getDefaultToolkit().createImage("./Images/coin.gif");
		this.status = Status.RUNNING;
		this.leftPillar = leftPillar;
		this.rightPillar = rightPillar;
		
		initCoin();
	}

//	This method generates a new coin on the screen and randomises
//	it's position to be between the left and right pillar
//	all while checking it is not overlapping over other mines on the screen.
	public void initCoin() {
		this.isCollected = false;
		boolean initNewCoin = false;
		int lowestHeight = ((this.leftPillar.getBottomHeight() < this.rightPillar.getBottomHeight()) ? (720 - this.leftPillar.getBottomHeight() - Coin.coinHeight) : (720 - this.rightPillar.getBottomHeight() - Coin.coinHeight));
		int highestHeight = ((this.leftPillar.getUpperHeight() < this.rightPillar.getUpperHeight()) ? this.leftPillar.getUpperHeight() : this.rightPillar.getUpperHeight());
		int max = 1280 + 1280 / 2 - 100;
		int min = 1280 + PillarStructure.pillarWidth + 100;
		do {
			initNewCoin = false;
			if(lowestHeight <= highestHeight)
				this.yPosition = ThreadLocalRandom.current().nextInt(lowestHeight, highestHeight + 1);
			else 
				this.yPosition = ThreadLocalRandom.current().nextInt(highestHeight, lowestHeight + 1);
			this.xPosition = ThreadLocalRandom.current().nextInt(min, max + 1);
			this.distance = this.xPosition - 1280;
			this.coinRect = new Rectangle(this.xPosition, this.yPosition, Coin.coinWidth, Coin.coinHeight);
			for(Mine mine: Board.mines) {
				if(isOverlapping(mine.getMineRect()) || this.coinRect.intersects(mine.getMineRect()))
					initNewCoin = true;
			}
		} while(initNewCoin == true);
	}
	
//	Checks if the current coin is overlapping the given rectangle.
	public boolean isOverlapping(Rectangle rect) {
		Point l1 = new Point((int)this.coinRect.getX(), (int)this.coinRect.getY());
		Point l2 = new Point((int)rect.getX(), (int)rect.getY());
		Point r1 = new Point((int)(this.coinRect.getX() + this.coinRect.getWidth()), (int)(this.coinRect.getY() + this.coinRect.getHeight()));
		Point r2 = new Point((int)(rect.getX() + rect.getWidth()), (int)(rect.getY() + rect.getHeight()));
		// If one rectangle is on left side of other  
        if (l1.x > r2.x || l2.x > r1.x) { 
            return false; 
        } 
  
        // If one rectangle is above other  
        if (l1.y < r2.y || l2.y < r1.y) { 
            return false; 
        } 
        
        if(rect.intersects(this.coinRect))
        	return false;
  
        return true; 
	}
	
	public Rectangle getCoinRect() {
		return coinRect;
	}

	public void setCoinRect(Rectangle coinRect) {
		this.coinRect = coinRect;
	}

	public boolean isCollected() {
		return isCollected;
	}

	public void setCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}

//	The run() method of the Coin thread.
//	positions the coin accordingly on screen depends on it's status.
	public void run() {
		try {
			while(status == Status.RUNNING && !Thread.interrupted()) {
				if(this.leftPillar.getxPosition() > -PillarStructure.pillarWidth)
					this.xPosition = this.leftPillar.getxPosition() + this.distance;
				this.coinRect.setLocation(this.xPosition, this.yPosition);
				Thread.sleep(4);
			}
			while(status == Status.INTERRUPTED && !Thread.interrupted()) {
				this.xPosition -= 3;
				this.coinRect.setLocation(this.xPosition, this.yPosition);
				Thread.sleep(12);
			}
		} catch (InterruptedException e) {
		}
	}
	
//	This method plays the Mario coin sound when 
//	the user collects coins.
	public static void playCoinSound() {
		Clip clip = null;
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("./Sounds/coinSound.wav"));
			DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
			clip = (Clip)AudioSystem.getLine(info);
	        clip.open(inputStream);
			clip.loop(0);
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public PillarStructure getLeftPillar() {
		return leftPillar;
	}

	public void setLeftPillar(PillarStructure leftPillar) {
		this.leftPillar = leftPillar;
	}

	public PillarStructure getRightPillar() {
		return rightPillar;
	}

	public void setRightPillar(PillarStructure rightPillar) {
		this.rightPillar = rightPillar;
	}

	public String getStatus() {
		if(this.status == Status.RUNNING)
			return "RUNNING";
		else if(this.status == Status.INTERRUPTED)
			return "INTERRUPTED";
		else if(this.status == Status.DEAD)
			return "DEAD";
		return null;
	}

	public void setStatus(String status) {
		if(status == "RUNNING")
			this.status = Status.RUNNING;
		else if(status == "INTERRUPTED")
			this.status = Status.INTERRUPTED;
		else if(status == "DEAD")
			this.status = Status.DEAD;
	}

	public static int getCoinwidth() {
		return coinWidth;
	}

	public static int getCoinheight() {
		return coinHeight;
	}

	public void drawCoin(Graphics g) {
		g.drawImage(this.coin, this.xPosition, this.yPosition, Coin.coinWidth, Coin.coinHeight, null);
	}
}
