package elements;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Mine extends Thread implements Runnable{
	private int xPosition, yPosition;
	private Image mine;
	public final static int mineWidth = 50, mineHeight = 50;
	private static enum Status
	{
		RUNNING, DEAD, INTERRUPTED;
	}
	
	private Status status;
	
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
		int lowestHeight = ((this.leftPillar.getBottomHeight() < this.rightPillar.getBottomHeight()) ? this.leftPillar.getBottomHeight() : this.rightPillar.getBottomHeight());
		int highestHeight = ((this.leftPillar.getUpperHeight() < this.rightPillar.getUpperHeight()) ? this.rightPillar.getUpperHeight() : this.leftPillar.getUpperHeight());
		this.yPosition = (int) (Math.floor(Math.random() * ((highestHeight - lowestHeight) + 1)) + lowestHeight);
		this.xPosition = 1280 + (1280 / 4);
	}
	
	public void run() {
		try {
			while(status == Status.RUNNING && !Thread.interrupted()) {
				if(this.leftPillar.getxPosition() <= 3 * (1280 / 4) && this.leftPillar.getxPosition() > (1280 / 2)) {
					this.xPosition = this.leftPillar.getxPosition() + (1280 / 4);
				}
				if(this.leftPillar.getxPosition() <= (1280/ 2) && this.leftPillar.getxPosition() > -PillarStructure.pillarWidth) {
					this.xPosition = (this.leftPillar.getxPosition() + this.rightPillar.getxPosition()) / 2;
				}
				Thread.sleep(4);
			}
			while(status == Status.INTERRUPTED && !Thread.interrupted()) {
				this.xPosition -= 3;
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
