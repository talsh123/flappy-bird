package elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener{
	
	// Board Attributes
	public static final int B_WIDTH = 1280;
	public static final int B_HEIGHT = 720;
	
	private int currentScore = 0;
	
	//Universal Elements
	static Bird bird;
	
	private static enum UserState 
	{
		// Determines which state the user is in and handles the code according to the situation
		IN_GAME, MAIN_MENU, RETRY_PAGE
	}
	
	private UserState userState;
	
	private StylizedButton retryButton;
	private StylizedButton mainMenuButton;
	private StylizedButton logOutButton;
	
	private Player currentPlayer = null;
	private boolean loggedIn = false;
	
	private List<Player> players;
	private static List<Background> backgroundImages;
	public static List<PillarStructure> pillars;
	public static List<Mine> mines;
	private static List<Coin> coins;
	
	private Image backgroundImage;
	
	public static Rectangle groundRect;
	
	// Timers in use
	private Timer collisionTimer; // Timer for checking collisions
	private Timer playButtonTimer; // play Button timer hovering animation
	private Timer backgroundChangeTimer; // Looping backgroundTimer
	private Timer pillarTimer; // looping pillars
	private Timer impactEffectTimer; // Timer which fades in and out the color black of the JPanel to simulate impact
	private Timer mineTimer; // Timer which checks if the mine is out of the panel borders, and if so kills it
	private Timer coinTimer; //Timer which checks when the coin is out of the panel borders, if so kills it
	
	// Main Menu Attributes
	private static PlayButton playButton = new PlayButton(540, 250);
	
	public Board(){
		super();
		initBoard();
	}
	
	private void initBoard() {
		setLayout(null);
		setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
		
		addMouseListener(new MAdapter());
		
		initButtons();
		initArrays();
		initTimers();
		initPlayers();
		initMenu(); 
	}
	
	private void initPlayers() {
		// Fetching written information over existing players
		players = new ArrayList<>();
		currentPlayer = null;
		File file = new File("C:\\Users\\talsh\\OneDrive\\Desktop\\Flash Drive Backup\\Amal B Computer Programming 14th Grade\\Mario\\Project\\FlappyBirdGame\\Files\\players.txt");
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String[] playerInfo = sc.nextLine().split(" ");
				players.add(new Player(playerInfo[0], Integer.parseInt(playerInfo[1])));
			}
			sc.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}
	
	private void updatePlayers() {
		currentPlayer = null;
		players.clear();
		File file = new File("C:\\Users\\talsh\\OneDrive\\Desktop\\Flash Drive Backup\\Amal B Computer Programming 14th Grade\\Mario\\Project\\FlappyBirdGame\\Files\\players.txt");
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String[] playerInfo = sc.nextLine().split(" ");
				players.add(new Player(playerInfo[0], Integer.parseInt(playerInfo[1])));
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initButtons() {
		// Setting up buttons for retry page
		this.retryButton = new StylizedButton();
		this.mainMenuButton = new StylizedButton();
		this.logOutButton = new StylizedButton();
	}

	private void initArrays() {
		Board.backgroundImages = new ArrayList<>();
		Board.pillars = new ArrayList<>();
		Board.mines = new ArrayList<>();
		Board.coins = new ArrayList<>();
	}

	private void initTimers() {
		// PlayButtonTimer used for hovering animation for Play Button
		this.playButtonTimer = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playButton.changeButtonPosition();
				repaint();
			}
			
		});
		playButtonTimer.setInitialDelay(0);
		
		// Timer to loop the same background Image
		this.backgroundChangeTimer = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (backgroundImages.get(1).getxPosition() == 0) {
					backgroundImages.remove(0);
					backgroundImages.add(new Background(1280, 0));
				}
				for (Background image: backgroundImages)
					image.move();
				repaint();
			}
		});
		backgroundChangeTimer.setInitialDelay(0);
		
		// Timer to create a looping pillars 
		this.pillarTimer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 4 pixels, first movement of the pillar, gets in jpanel
				if(pillars.get(0).getxPosition() <= 1280 && pillars.get(0).getxPosition() > (1280 / 2)) {
					if(pillars.get(0).getxPosition() == 1280) {
						pillars.add(new PillarStructure());
						
						mines.add(new Mine(pillars.get(1), pillars.get(2)));
						mines.get(mines.size() - 1).start();
						
						coins.add(new Coin(pillars.get(1), pillars.get(2)));
						coins.get(coins.size() - 1).start();
					}
					pillars.get(0).move();
				}
				else if(pillars.get(0).getxPosition() <= (1280 / 2) && pillars.get(0).getxPosition() > 0) {
					for(int i= 0; i < 2; i++) {
						pillars.get(i).move();
					}
				}
				else if(pillars.get(0).getxPosition() <= 0 && pillars.get(0).getxPosition() > -PillarStructure.pillarWidth) {
					for(int i = 0; i < 3; i++) {
						pillars.get(i).move();
					}
				}
				else if(pillars.get(0).getxPosition() <= -PillarStructure.pillarWidth) {
					pillars.add(new PillarStructure());
					pillars.remove(0);
					
					mines.get(0).setStatus("INTERRUPTED");
					
					mines.add(new Mine(pillars.get(1), pillars.get(2)));
					mines.get(mines.size() - 1).start();
					
					coins.get(0).setStatus("INTERRUPTED");
					
					coins.add(new Coin(pillars.get(1), pillars.get(2)));
					coins.get(coins.size() - 1).start();
				}
				repaint();
			};
		});
		pillarTimer.setInitialDelay(0);
		
		this.mineTimer = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Mine> minesCopy = new ArrayList<Mine>(mines);
				for(Mine mine: minesCopy) {
					int index = minesCopy.indexOf(mine);
					if(mine.getxPosition() <= -Mine.mineWidth) {
						mines.get(index).setStatus("DEAD");
						mines.get(index).interrupt();
						mines.remove(0);
					}
				}
				repaint();
			}
			
		});
		mineTimer.setInitialDelay(0);
		
		this.coinTimer = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Coin> coinsCopy = new ArrayList<Coin>(coins);
				for(Coin coin: coinsCopy) {
					int index = coinsCopy.indexOf(coin);
					if(coin.getxPosition() <= -Coin.coinWidth) {
						coins.get(index).setStatus("DEAD");
						//coins.get(index).stopAnimation();
						coins.get(index).interrupt();
						coins.remove(0);
					}
				}
			}
		});
		coinTimer.setInitialDelay(0);
		
		// Collision timer used to check for Collisions
		this.collisionTimer = new Timer(1, this);
		this.collisionTimer.setInitialDelay(0);
		
		impactEffectTimer = new Timer(1, new ActionListener() {
			int alpha = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				if (alpha < 250) {
					setBackground(new Color(0, 0, 0, alpha));
					alpha += 50;
				} else {
					impactEffectTimer.stop();
				}
				repaint();
			};
		});
		pillarTimer.setInitialDelay(0);
	}

	private void initMenu() {
		// Games starts out in Main Menu, thus userState is set to MAIN_MENU
		userState = UserState.MAIN_MENU;
		
		// Loading backgroundImage from local storage
		ImageIcon iib = new ImageIcon("./Images/background.jpg");
		this.backgroundImage = iib.getImage();
		
		this.playButtonTimer.restart();
		
		if(!Bird.interrupted() && bird != null) {
			bird.isAlive = false;
			bird.interrupt();
		}
		
		// Initializes a new static bird (does not start the thread)
		bird = new Bird(590, 150);
	}
	
	public void initGame() {
		// Clearing the arrays
		// Makes sure that the last position of all elements are drawn before cleaning of the arrays
		Board.backgroundImages.clear();
		Board.pillars.clear();
		Board.mines.clear();
		Board.coins.clear();
		// Adding 2 background Images for simulating looping background
		Board.backgroundImages.add(new Background(0, 0));
		Board.backgroundImages.add(new Background(1280, 0));
		// Adding 2 pillar structure and 1 mines
		Board.pillars.add(new PillarStructure());
		Board.pillars.add(new PillarStructure());
		
		mines.add(new Mine(pillars.get(0), pillars.get(1)));
		mines.get(mines.size() - 1).start();
		
		coins.add(new Coin(pillars.get(0), pillars.get(1)));
		coins.get(coins.size() - 1).start();
		
		// Stops Timer for hovering play button
		playButtonTimer.stop();
		
		backgroundChangeTimer.restart();
		
		pillarTimer.restart();

		mineTimer.restart(); // Starts when score is 20
		
		coinTimer.restart(); // Starts when score is 40
		
		// 150px - Height of ground, Rectangle which is a boundary, if bird gets to ground it dies
		Board.groundRect = new Rectangle(0, 720 - 150, 1280, 150);
		
		this.collisionTimer.restart();
		
	}
	
	public void initRetryPage() {	
		impactEffectTimer.restart();
	}
	
	// collisionTimer exectutes this method
	@Override
	public void actionPerformed(ActionEvent e) {
		if (userState == UserState.IN_GAME)
			checkCollision();
	}

	private void checkCollision() {
		// Creates a rectangle around the bird and for every pillar (bottom and upper) in every pillar structure
		boolean isHit = false;
		Rectangle birdRect = new Rectangle(bird.getxPosition(), bird.getyPosition(), bird.getBirdImage().getWidth(null) - 100, bird.getBirdImage().getHeight(null) - 100);
		for(PillarStructure pillar: pillars) {
			Rectangle upperRect = new Rectangle(1280 - pillar.getDistance(), 0, PillarStructure.pillarWidth, pillar.getUpperHeight());
			Rectangle bottomRect = new Rectangle(1280 - pillar.getDistance(), 720 - pillar.getBottomHeight(), PillarStructure.pillarWidth, pillar.getBottomHeight());
			// if the bird hit one of the pillars
			if (birdRect.intersects(upperRect) || birdRect.intersects(bottomRect))
				isHit = true;
			if(birdRect.getCenterX() - 1.5 == upperRect.getCenterX()) {
				this.currentScore++;
				Coin.playCoinSound();
			}
		}
		for(Mine mine: mines) {
			Rectangle mineRect = new Rectangle(mine.getxPosition(), mine.getyPosition(), Mine.mineWidth, Mine.mineHeight);
			if (birdRect.intersects(mineRect))
				isHit = true;
		}
		for(Coin coin: coins) {
			Rectangle coinRect = new Rectangle(coin.getxPosition(), coin.getyPosition(), Coin.coinWidth, Coin.coinHeight);
			if(birdRect.intersects(coinRect) && coin.isCollected() == false) {
				coin.setCollected(true);
				this.currentScore++;
				Coin.playCoinSound();
			}
		}
		// if the bird hit the ground
		if (birdRect.intersects(Board.groundRect))
			isHit = true;
		if(isHit) {
			// If Score acheived is greater than highest score, it is updated in the file
			if(this.currentScore > currentPlayer.getScore())
				this.currentPlayer.incrementHighestScore(this.currentScore);
			// User is not playing anymore and bird thread is dead
			bird.playImpactSound();
			userState = UserState.RETRY_PAGE;
			Board.bird.isAlive = false;
			bird.interrupt();
			
			// Killing all remaining mines in the mines array
			for(Mine mine: mines) {
				mine.setStatus("DEAD");
				mine.interrupt();
			}
			
			for(Coin coin: coins) {
				//coin.stopAnimation();
				coin.setStatus("DEAD");
				coin.interrupt();
			}
			
			// Stops the timers for background images and looping pillars
			this.collisionTimer.stop();
			this.pillarTimer.stop();
			this.backgroundChangeTimer.stop();
			this.mineTimer.stop();
			this.coinTimer.stop();
			// impactEffectTimer and playButtonTimer stop by themselves
			
			// Initializes change of background
			initRetryPage();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		// Important! - Do not delete
		//super.paintComponent(g);
		switch(userState) {
		case IN_GAME:
			//Drawing when use is playing the game
			drawGame(g);
			break;
		case MAIN_MENU:
			//Drawing when user is in Main Menu
			drawMainMenu(g);
			break;
		case RETRY_PAGE:
			// Drawing when user is in retry page
			if(!impactEffectTimer.isRunning())
				drawRetryPage(g);
			break;
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void drawRetryPage(Graphics g) {
		drawGame(g);
		this.retryButton.drawUnpressedButton(g, 315, 280);
		this.mainMenuButton.drawUnpressedButton(g, 700, 280);
		g.setFont(getFont().deriveFont(Font.PLAIN, 70));
		g.drawString("RETRY", 350, 370);
		g.drawString("MAIN", 755, 345);
		g.drawString("MENU", 755, 390);
	}
	
	private void drawGame(Graphics g) {
		// Draws all background images, pillar structures and the bird
		for (Background image: backgroundImages)
			image.drawBackground(g);
		
		for (PillarStructure pillar: pillars)
			pillar.drawPillar(g);
		
		for (Mine mine: mines)
			mine.drawMine(g);
		
		for(Coin coin: coins)
			if(coin.isCollected() == false)
				coin.drawCoin(g);
		
		g.drawString(String.valueOf(this.currentScore), 590, 130);
		
		bird.drawBird(g);
	}
	
	private void drawMainMenu(Graphics g) {
		// Draws the background image, game name, bird and the play button
		g.drawImage(this.backgroundImage, 0, 0, 1280, 720, this);
		g.setFont(getFont());
		g.drawString("FLAPPY BIRD", 220, 120); // 220 120
		bird.drawBird(g, 590, 150);
		Board.playButton.drawPlayButton(g);
		if(loggedIn) {
			g.setColor(Color.black);
			this.logOutButton.drawUnpressedButton(g, 960, 520);
			g.drawString("LOG", 1040, 580);
			g.drawString("OUT", 1040, 620);
		}
	}
	
	// Method which gets the pixelated font and returns it
	public Font getFont() {
		Font font = null;
		try {
			URL fontURL = new URL("file:///C:\\Users\\talsh\\OneDrive\\Desktop\\Flash Drive Backup\\Amal B Computer Programming 14th Grade\\Mario\\Project\\FlappyBirdGame\\Fonts\\PixalatedFont.ttf");
			
			font = Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream());
			font = font.deriveFont(Font.PLAIN, 150);

		} catch (FontFormatException | IOException e) {
			
			e.printStackTrace();
		}
		return font;
	}
	
	// Small class which allows for recording and running code when pressing a key on the mouse and actuating a key press
	private class MAdapter extends MouseAdapter{
		
		private Player findPlayer(String usernameInput) {
			for(Player player: players) {
				if(player.getName().equals(usernameInput))
					return player;
			}
			return null;
		}

		private boolean isUsernameExist(String usernameInput) {
			File file = new File("C:\\Users\\talsh\\OneDrive\\Desktop\\Flash Drive Backup\\Amal B Computer Programming 14th Grade\\Mario\\Project\\FlappyBirdGame\\Files\\players.txt");
			try {
				Scanner sc = new Scanner(file);
				while(sc.hasNextLine()) {
					String[] playerInfo = sc.nextLine().split(" ");
					if(playerInfo[0].equals(usernameInput)) {
						sc.close();
						return true; 
					}
				}
				sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return false;
		}

		private void addNewUsername(String usernameInput) {
			File file = new File("C:\\Users\\talsh\\OneDrive\\Desktop\\Flash Drive Backup\\Amal B Computer Programming 14th Grade\\Mario\\Project\\FlappyBirdGame\\Files\\players.txt");
			try {
				Scanner sc = new Scanner(file);
				FileWriter fw = new FileWriter(file, true);
				fw.write("\n" + usernameInput + " 0");
				fw.close();
				sc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private String getSelectedButtonText(ButtonGroup bg) {
			 for (Enumeration<AbstractButton> buttons = bg.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();

		            if (button.isSelected()) {
		                return button.getText();
		            }
		        }
			 
			 return null;
		}
		
		// A method which gets a mouse events and handles it accordingly
		public void mouseClicked(MouseEvent e) {
			if (userState == UserState.MAIN_MENU) {
				// Creates a point the the mouse event coordinates, creates a rectangle around the playButton image
				Point point = new Point(e.getX(), e.getY());
				Rectangle playButtonBounds = new Rectangle(playButton.getX(), playButton.getY(), PlayButton.playButtonWidth, PlayButton.playButtonHeight);
				// Checks if the click was inside the image
				if(e.getButton() == MouseEvent.BUTTON1 && playButtonBounds.contains(point)) {
					if(loggedIn == false) {
						JPanel p1 = new JPanel();
						JPanel p2 = new JPanel();
						// p1 components
					    JLabel newUserLabel = new JLabel("Please enter a new username");
					    JTextField newUserTextField = new JTextField("Your username",16);
					    
					    newUserTextField.setSelectionColor(Color.YELLOW);
					    newUserTextField.setSelectedTextColor(Color.RED);
					    newUserTextField.setHorizontalAlignment(JTextField.CENTER);
					    
					    JButton newUserButton = new JButton("OK");
					    JLabel errorLabel = new JLabel();
					    JLabel warningLabel = new JLabel("<html>Spaces will be removed if included</html>");
					    errorLabel.setBackground(Color.red);
					    p1.add(newUserLabel);
					    p1.add(newUserTextField);
					    p1.add(newUserButton);
					    p1.add(warningLabel);	
					    // p2 components
					    ButtonGroup bg = new ButtonGroup();
					    for(Player player: players) {
					    	JRadioButton rb = new JRadioButton(player.getName());
					    	p2.add(rb);
					    	bg.add(rb);
					    }
					    JButton existingUserButton = new JButton("OK");
					    p2.add(existingUserButton);
					    JTabbedPane tp = new JTabbedPane(); 
					    tp.setBounds(500 ,300 ,300,300);  
					    tp.add("New User",p1);  
					    tp.add("Existing User",p2);   
					    Board.this.add(tp);
					    
					    existingUserButton.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								if(bg.getSelection() != null) {
									
									currentPlayer = findPlayer(getSelectedButtonText(bg));
									loggedIn = true;
									Board.this.remove(tp);
								} else {
									errorLabel.setText("<html><font color='red'>You have to pick one of the options</font></html>");
									p2.add(errorLabel);
									}
							}
					    	
					    });
					    
					    newUserButton.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								String usernameInput = newUserTextField.getText().replaceAll(" ", "");
								if(!isUsernameExist(usernameInput)) {
									addNewUsername(usernameInput); //works
									updatePlayers();
									currentPlayer = findPlayer(usernameInput) != null ? findPlayer(usernameInput) : null;
									loggedIn = true;
									Board.this.remove(tp);
								} else {
									errorLabel.setText("<html><font color='red'>Username already exists, please pick another</font></html>");
									p1.add(errorLabel);
								}
							}
					    });
					} else { 
						// User is now playing
						userState = UserState.IN_GAME;
						// Interrupts the last bird thread (static image in main menu/bird from last try)
						if(!Bird.interrupted()) {
							bird.isAlive = false;
							bird.interrupt();
						}
						
						// Creates a new bird thread for user to play with
						bird = new Bird(590, 150);
						bird.isAlive = true;
						bird.start();
						bird.jump();
						
						// Initialize pillars and timers and starts the game
						initGame();
						//repaint();
					}
				} 
				Rectangle logOutBounds = new Rectangle(logOutButton.getXPosition(), logOutButton.getYPosition(), logOutButton.getButtonWidth(), logOutButton.getButtonHeight());
				if(e.getButton() == MouseEvent.BUTTON1 && logOutBounds.contains(point) && loggedIn) {
					loggedIn = false;
					currentPlayer = null;
				}
			}
			else if (userState == UserState.RETRY_PAGE) {
				Point point = new Point(e.getX(), e.getY());
				Rectangle retryImageBounds = new Rectangle(retryButton.getXPosition(), retryButton.getYPosition(), retryButton.getButtonWidth(), retryButton.getButtonHeight());
				Rectangle mainMenuImageBounds = new Rectangle(mainMenuButton.getXPosition(), mainMenuButton.getYPosition(), mainMenuButton.getButtonWidth(), mainMenuButton.getButtonHeight());
				if(e.getButton() == MouseEvent.BUTTON1 && retryImageBounds.contains(point)) {
					currentScore = 0;
					userState = UserState.IN_GAME;
					if(!Bird.interrupted()) {
						bird.isAlive = false;
						bird.interrupt();
					}
					bird = new Bird(590, 150);
					bird.isAlive = true;
					bird.start();
					bird.jump();
					initGame();
				}
				else if(e.getButton() == MouseEvent.BUTTON1 && mainMenuImageBounds.contains(point)) {
					currentScore = 0;
					userState = UserState.MAIN_MENU;
					initMenu();
				}
				repaint();
			} else if (userState == UserState.IN_GAME) {
				bird.jump();
				repaint();
			}
		}
	}
}