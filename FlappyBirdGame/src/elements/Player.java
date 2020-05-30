package elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

// This class contains the player information
public class Player {
	private String name;
	private String password;
	private int levelsAvailable;
	private int highestScore;
	
	public Player(String name, String password, int levelsAvailable, int highestScore) {
		this.name = name;
		this.password = password;
		this.levelsAvailable = levelsAvailable;
		this.highestScore = highestScore;
	}
	
//	Updates the highest score in the file
	public void updateHighestScore(int highestScore) {
		File file = new File("./Files/players.txt");
		File tempFile = new File("./Files/fileTemp.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			
			String lineToRemove = this.getName() + " " + this.getPassword() + " " + this.getLevelsAvailable() + " " + this.getHighestScore();
			String currentLine;
			this.setHighestScore(highestScore);
			
			while((currentLine = reader.readLine()) != null) {
				currentLine.trim();
				if(currentLine.equals(lineToRemove)) {
					writer.write(this.getName() + " " + this.getPassword() + " " + this.getLevelsAvailable() + " " + this.getHighestScore());
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
	
//	Updates the levels available in the file
	public void updateLevelsAvailable() {
		File file = new File("./Files/players.txt");
		File tempFile = new File("./Files/fileTemp.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			
			String lineToRemove = this.getName() + " " + this.getPassword() + " " + this.getLevelsAvailable() + " " + this.getHighestScore();
			String currentLine;
			this.setLevelsAvailable(this.getLevelsAvailable() + 1);
			
			while((currentLine = reader.readLine()) != null) {
				currentLine.trim();
				if(currentLine.equals(lineToRemove)) {
					writer.write(this.getName() + " " + this.getPassword() + " " + this.getLevelsAvailable() + " " + this.getHighestScore());
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
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getLevelsAvailable() {
		return levelsAvailable;
	}

	public void setLevelsAvailable(int levelsAvailable) {
		this.levelsAvailable = levelsAvailable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHighestScore() {
		return highestScore;
	}

	public void setHighestScore(int highestScore) {
		this.highestScore = highestScore;
	}
}
