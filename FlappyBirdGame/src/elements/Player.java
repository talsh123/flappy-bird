package elements;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Player {
	private String name;
	private int highestScore;
	
	public Player(String name,int score) {
		this.name = name;
		this.highestScore = score;
	}
	
	public void incrementHighestScore(int highestScore) {
		File file = new File("./Files/players.txt");
		File tempFile = new File("./Files/fileTemp.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			
			String lineToRemove = this.getName() + " " + this.getScore();
			String currentLine;
			this.highestScore = highestScore;
			
			while((currentLine = reader.readLine()) != null) {
				currentLine.trim();
				if(currentLine.equals(lineToRemove)) {
					writer.write(this.getName() + " " + this.highestScore);
					continue;
				}
				writer.write(currentLine + System.getProperty("line.separator"));
				}
			reader.close();
			writer.close();
			file.delete();
			tempFile.renameTo(file); // Returns true is rename is successful, otherwise false
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void drawScore(Graphics g) {
		g.drawString(String.valueOf(this.highestScore), 550, 130);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return highestScore;
	}

	public void setScore(int score) {
		this.highestScore = score;
	}
}
