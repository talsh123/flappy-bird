package elements;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ImageIcon;

public class Mine extends Thread implements Runnable{
	private int xPosition, yPosition, distance;
	private Image mine;
	public final static int mineWidth = 50, mineHeight = 50;
	private static enum Status
	{
		RUNNING, DEAD, INTERRUPTED;
	}
	
	private Status status;
	
	private Rectangle mineRect;
	
	private PillarStructure leftPillar, rightPillar;
	
	public Mine(PillarStructure leftPillar, PillarStructure rightPillar) {
		this.status = Status.RUNNING;
		
		ImageIcon iim = new ImageIcon("./Images/Mine.png");
		this.mine = iim.getImage();
		
		this.leftPillar = leftPillar;
		this.rightPillar = rightPillar;
		
		initMine();
	}
	
	private void initMine() {
		boolean initNewMine = false;
		int lowestHeight = ((this.leftPillar.getBottomHeight() < this.rightPillar.getBottomHeight()) ? (720 - this.leftPillar.getBottomHeight() - Mine.mineHeight) : (720 - this.rightPillar.getBottomHeight() - Mine.mineHeight));
		int highestHeight = ((this.leftPillar.getUpperHeight() < this.rightPillar.getUpperHeight()) ? this.leftPillar.getUpperHeight() : this.rightPillar.getUpperHeight());
		int max = 1280 + 1280 / 2 - 100;
		int min = 1280 + PillarStructure.pillarWidth + 100;
		do {
			initNewMine = false;
			if(lowestHeight <= highestHeight)
				this.yPosition = ThreadLocalRandom.current().nextInt(lowestHeight, highestHeight + 1);
			else 
				this.yPosition = ThreadLocalRandom.current().nextInt(highestHeight, lowestHeight + 1);
			this.xPosition = ThreadLocalRandom.current().nextInt(min, max + 1);
			this.distance = this.xPosition - 1280;
			this.mineRect = new Rectangle(this.xPosition, this.yPosition, Mine.mineWidth, Mine.mineHeight);
			for(Coin coin: Board.coins) {
				if(isOverlapping(coin.getCoinRect()) || this.mineRect.intersects(coin.getCoinRect()))
					initNewMine = true;
			}
		} while(initNewMine == true);
	}
	
	public boolean isOverlapping(Rectangle rect) {
		Point l1 = new Point((int)this.mineRect.getX(), (int)this.mineRect.getY());
		Point l2 = new Point((int)rect.getX(), (int)rect.getY());
		Point r1 = new Point((int)(this.mineRect.getX() + this.mineRect.getWidth()), (int)(this.mineRect.getY() + this.mineRect.getHeight()));
		Point r2 = new Point((int)(rect.getX() + rect.getWidth()), (int)(rect.getY() + rect.getHeight()));
		// If one rectangle is on left side of other  
        if (l1.x > r2.x || l2.x > r1.x) { 
            return false; 
        } 
  
        // If one rectangle is above other  
        if (l1.y < r2.y || l2.y < r1.y) { 
            return false; 
        } 
  
        return true; 
	}
	
	public Rectangle getMineRect() {
		return mineRect;
	}

	public void setMineRect(Rectangle mineRect) {
		this.mineRect = mineRect;
	}

	public void run() {
		try {
			while(status == Status.RUNNING && !Thread.interrupted()) {
				if(this.leftPillar.getxPosition() > -PillarStructure.pillarWidth)
					this.xPosition = this.leftPillar.getxPosition() + this.distance;
				this.mineRect.setLocation(this.xPosition, this.yPosition);
				Thread.sleep(4);
			}
			while(status == Status.INTERRUPTED && !Thread.interrupted()) {
				this.xPosition -= 3;
				this.mineRect.setLocation(this.xPosition, this.yPosition);
				Thread.sleep(12);
			}
		}catch(InterruptedException e) {
		}
	}
	
	public PillarStructure getLeftPillar() {
		return leftPillar;
	}

	public void setLeftPillar(PillarStructure leftPillar) {
		this.leftPillar = leftPillar;
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

	public void drawMine(Graphics g) {
		g.drawImage(this.mine, this.xPosition, this.yPosition, Mine.mineWidth, Mine.mineHeight, null);
		g.drawRect((int)this.mineRect.getX(), (int)this.mineRect.getY(), Mine.mineWidth, Mine.mineHeight);
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

	public Image getMine() {
		return mine;
	}

	public void setMine(Image mine) {
		this.mine = mine;
	}
}
