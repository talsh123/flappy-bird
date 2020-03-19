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

public class Coin extends Thread implements Runnable{
	
	private int yPosition, xPosition, distance;
	private PillarStructure leftPillar, rightPillar;
	private Image coin;
	private Rectangle coinRect;
	
	private boolean isCollected;
	
	private static enum Status
	{
		RUNNING, DEAD, INTERRUPTED;
	}
	
	public final static int coinWidth = 60, coinHeight = 60;

	private Status status;
	
	public Coin(PillarStructure leftPillar, PillarStructure rightPillar) {
		this.coin = Toolkit.getDefaultToolkit().createImage("./Images/coin.gif");
		this.status = Status.RUNNING;
		this.leftPillar = leftPillar;
		this.rightPillar = rightPillar;
		
		initCoin();
	}

	public void stopAnimation() {
		this.coin.flush();
	}

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
	
	public static void playCoinSound() {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("./Sounds/coinSound.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
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
