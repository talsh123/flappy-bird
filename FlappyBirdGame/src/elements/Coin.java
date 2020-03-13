package elements;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Coin extends Thread implements Runnable{
	
	private int yPosition, xPosition, distance;
	private PillarStructure leftPillar, rightPillar;
	private Image coin;
	
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
		int highestHeight = (this.leftPillar.getUpperHeight() > this.rightPillar.getUpperHeight()) ? this.leftPillar.getUpperHeight() : this.rightPillar.getUpperHeight();
		int lowestHeight = (this.leftPillar.getBottomHeight() < this.rightPillar.getBottomHeight()) ? this.leftPillar.getBottomHeight() : this.rightPillar.getBottomHeight();
		do {
			initNewCoin = false;
			this.yPosition = (int) (Math.floor(Math.random() * ((highestHeight - lowestHeight) + 1)) + lowestHeight);
			this.distance = (int) (Math.floor(Math.random() * (((640 - Coin.coinWidth) - PillarStructure.pillarWidth) + 1)) + PillarStructure.pillarWidth); // distance from the right pillar
			this.xPosition = this.leftPillar.getxPosition() + this.distance;
			Rectangle coinRect = new Rectangle(this.xPosition, this.yPosition, Coin.coinWidth, Coin.coinHeight);
			//System.out.println("Coin " + coinRect.getX() + " " + coinRect.getY() + " " + coinRect.getBounds());
			for(Mine mine: Board.mines) {
				Rectangle mineRect = new Rectangle(mine.getxPosition(), mine.getyPosition(), Mine.mineWidth , Mine.mineHeight );
				//System.out.println("Mine " + mineRect.getX() + " " + mineRect.getY() + " " + mineRect.getBounds());
				if(coinRect.intersects(mineRect))
					initNewCoin = true;
			}
		} while(initNewCoin == true);
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
				if(this.leftPillar.getxPosition() > -PillarStructure.pillarWidth) {
					this.xPosition = this.leftPillar.getxPosition() + this.distance;
				}
				Thread.sleep(4);
			}
			while(status == Status.INTERRUPTED && !Thread.interrupted()) {
				this.xPosition -= 3;
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
