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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel{
	
	// Board Dimensions
	public static final int B_WIDTH = 1280;
	public static final int B_HEIGHT = 720;
	
	// Universal Elements
	private static Bird bird;
	
//	Pause and Resume last bird properties
	private int lastBirdX;
	private int lastBirdY;
	private int lastVertSpeed;
	
//	Determines which state the user is in and handles the code according to the situation
	private static enum UserState 
	{
		IN_GAME, MAIN_MENU, RETRY_PAGE, PAUSE_PAGE, LEVEL_SELECTION_PAGE
	}
	
	private static UserState userState;
	
//	Buttons used
	private static PlayButton playButton;
	private static StylizedButton retryButton;
	private static StylizedButton mainMenuButton;
	private static StylizedButton logOutButton;
	private static StylizedButton level1;
	private static StylizedButton level2;
	private static StylizedButton level3;
	
//	Player information, mid and off game
	private static Player currentPlayer = null;
	private static boolean loggedIn = false;
	private static int currentScore = 0;
	private static int currentLevel;
	private static int scoreToUnlockLevel = 2;
	
//	ArrayLists being used containing game elements
	private static List<Player> players;
	private static List<Background> backgroundImages;
	public static List<PillarStructure> pillars;
	public static List<Mine> mines;
	public static List<Coin> coins;
	
//	Static background image for menu
	private Image backgroundImage;
	
//	Rectangles defining game borders
	public static Rectangle groundRect;
	public static Rectangle skyRect;
	
	// Timers in use, neccessary for game functionality
	private Timer collisionTimer; // Timer for checking collisions
	private Timer playButtonTimer; // play Button timer hovering animation
	private Timer backgroundChangeTimer; // Looping backgroundTimer
	private Timer pillarTimer; // looping pillars
	private Timer impactEffectTimer; // Timer which fades in and out the color black of the JPanel to simulate impact
	private Timer mineTimer; // Timer which checks if the mine is out of the panel borders, and if so kills it
	private Timer coinTimer; // Timer which checks when the coin is out of the panel borders, if so kills it
	private Timer repaintTimer; // Timer which in charge of repainting the screen every millisecond
	
	public Board(){
		super();
		initBoard();
	}
	
//	Initialises board (frame)
	private void initBoard() {
		setLayout(null);
		setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
		
		addMouseListener(new MAdapter());
		addKeyListener(new KAdapter());
		this.setRequestFocusEnabled(true);
		this.setFocusable(true);
		
		initButtons();
		initArrays();
		initTimers();
		initPlayers();
		initMenu(); 
	}
	
//	Initialises buttons used in game
	private void initButtons() {
		Board.retryButton = new StylizedButton();
		Board.mainMenuButton = new StylizedButton();
		Board.logOutButton = new StylizedButton();
		
		Board.level1 = new StylizedButton();
		Board.level2 = new StylizedButton();
		Board.level3 = new StylizedButton();
		
		Board.playButton = new PlayButton(540, 250);
	}
	
//	Initialises arrays used in game
	private void initArrays() {
		Board.backgroundImages = new ArrayList<>();
		Board.pillars = new ArrayList<>();
		Board.mines = new ArrayList<>();
		Board.coins = new ArrayList<>();
		Board.players = new ArrayList<>();
	}

//	Initialises all timers used in game
	private void initTimers() {
		// Timer used for hovering animation for Play Button
		this.playButtonTimer = new Timer(50, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playButton.changeButtonPosition();
			}
		});
		playButtonTimer.setInitialDelay(0);
		
		// Timer to loop the same background Image
		this.backgroundChangeTimer = new Timer(20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (backgroundImages.get(1).getxPosition() == 0) {
					backgroundImages.remove(0);
					backgroundImages.add(new Background(1280, 0));
				}
				for (Background image: backgroundImages)
					image.move();
			}
		});
		backgroundChangeTimer.setInitialDelay(0);
		
		// Timer to create a looping pillars 
		this.pillarTimer = new Timer(20, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 4 pixels, first movement of the pillar, gets in JPanel
				if(pillars.get(0).getxPosition() <= 1280 && pillars.get(0).getxPosition() > (1280 / 2)) {
					if(pillars.get(0).getxPosition() == 1280) {
						pillars.add(new PillarStructure());
						
						if (currentLevel >= 2) {
						mines.add(new Mine(pillars.get(1), pillars.get(2)));
						mines.get(mines.size() - 1).start();
						}
						
						if (currentLevel >= 3) {
						coins.add(new Coin(pillars.get(1), pillars.get(2)));
						coins.get(coins.size() - 1).start();
						}
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
					
					if (currentLevel >= 2) {
						mines.get(0).setStatus("INTERRUPTED");
					
						mines.add(new Mine(pillars.get(1), pillars.get(2)));
						mines.get(mines.size() - 1).start();
					}
					
					if (currentLevel >= 3) {
						coins.get(0).setStatus("INTERRUPTED");
						
						coins.add(new Coin(pillars.get(1), pillars.get(2)));
						coins.get(coins.size() - 1).start();						
					}
				}
			};
		});
		pillarTimer.setInitialDelay(0);
		
//		Timer to interrupt mines and removing them from the array
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
			}
			
		});
		mineTimer.setInitialDelay(0);
		
//		Timer to interrupt coins and removing them from the array
		this.coinTimer = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Coin> coinsCopy = new ArrayList<Coin>(coins);
				for(Coin coin: coinsCopy) {
					int index = coinsCopy.indexOf(coin);
					if(coin.getxPosition() <= -Coin.coinWidth) {
						coins.get(index).setStatus("DEAD");
						coins.get(index).interrupt();
						coins.remove(0);
					}
				}
			}
		});
		coinTimer.setInitialDelay(0);
		
//		Timer to repaint screen
		this.repaintTimer = new Timer(2, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
			
		});
		this.repaintTimer.setInitialDelay(0);
		
		// Repaint Timer never stops
		this.repaintTimer.restart();
		
//	Collision timer used to check for Collisions, runs checkCollision
		this.collisionTimer = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (userState == UserState.IN_GAME)
					checkCollision();
			}
			
		});
		this.collisionTimer.setInitialDelay(0);
		
//		Timer that changes screen color rapidly for impact effect
//		This timer activates only when user fails
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
			};
		});
		pillarTimer.setInitialDelay(0);
	}
	
//	Initialises/Updates written information over existing players in players.txt
	private void initPlayers() {
		currentPlayer = null;
		Board.players.clear();
		File file = new File("./Files/players.txt");
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String[] playerInfo = sc.nextLine().split(" ");
				Board.players.add(new Player(playerInfo[0], playerInfo[1], Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3])));
			}
			sc.close();
			System.out.println(players.size());
			System.out.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

//	Initilises Menu Page
	private void initMenu() {
		// Games starts out in Main Menu, thus userState is set to MAIN_MENU
		userState = UserState.MAIN_MENU;
		
		// Loading backgroundImage from local storage
		ImageIcon iib = new ImageIcon("./Images/background.jpg");
		this.backgroundImage = iib.getImage();
		
		// Starts play button timer
		this.playButtonTimer.restart();
		
		// Initialises a new static bird (does not start the thread)
		Board.bird = new Bird(590, 150);
		Board.bird.isAlive = false;
	}
	
//	Initialises game, player starts playing
	public void initGame() {
		// Clearing the arrays from last try elements
		Board.backgroundImages.clear();
		Board.pillars.clear();
		Board.mines.clear();
		Board.coins.clear();
		// Adding 2 background Images for simulating looping background
		Board.backgroundImages.add(new Background(0, 0));
		Board.backgroundImages.add(new Background(1280, 0));
		// Adding 2 pillar structure
		Board.pillars.add(new PillarStructure());
		Board.pillars.add(new PillarStructure());
		
		// Adding 1 mine and 1 coin and starting them
		if (currentLevel >= 2) {
			mines.add(new Mine(pillars.get(0), pillars.get(1)));
			mines.get(mines.size() - 1).start();			
		}
		
		if (currentLevel >= 3) {
			coins.add(new Coin(pillars.get(0), pillars.get(1)));
			coins.get(coins.size() - 1).start();			
		}
		
		// Stops Timer for hovering play button
		playButtonTimer.stop();
		
		backgroundChangeTimer.restart();
		
		pillarTimer.restart();

		if (currentLevel >= 2)
			mineTimer.restart();
		
		if (currentLevel >= 3)
			coinTimer.restart();
		
		// 150px - Height of ground, Rectangle which is a boundary, if bird gets to ground it dies
		Board.groundRect = new Rectangle(0, 720 - 150, 1280, 150);
		// Skt rectangle border, if bird 
		Board.skyRect = new Rectangle(0, -300, 1280, 150);
		
		this.collisionTimer.restart(); // Checks for collisions
	}
	
//	Initialises retry page and starts animation for impact effect
	public void initRetryPage() {	
		impactEffectTimer.restart();
	}
	
//	Pauses the game when users presses 'P'
	public void pause() {
		stopGame();
		if (currentLevel >= 2)
			this.mineTimer.stop();
		if (currentLevel >= 3)
			this.coinTimer.stop();
		this.lastBirdX = Board.bird.getxPosition();
		this.lastBirdY = Board.bird.getyPosition();
		this.lastVertSpeed = Board.bird.getVertSpeed();
	}
	
//	Resumes the game when users presses 'P'
	public void resume() {
		this.collisionTimer.start();
		this.pillarTimer.start();
		this.backgroundChangeTimer.start();
		if (currentLevel >= 2)
			this.mineTimer.start();
		if (currentLevel >= 3)
			this.coinTimer.start();
		Board.bird = new Bird(this.lastBirdX, this.lastBirdY);
		Board.bird.isAlive = true;
		Board.bird.setVertSpeed(this.lastVertSpeed);
		bird.start();
	}
	
//	Stops the game mechanics
	public void stopGame() {
		// Stops the timers for background images and looping pillars
		this.collisionTimer.stop();
		this.pillarTimer.stop();
		this.backgroundChangeTimer.stop();
		this.mineTimer.stop();
		this.coinTimer.stop();
		Board.bird.isAlive = false;
		bird.interrupt();
	}

//	Checks if the user has collided with the ground, sky pillars or mines 
	private void checkCollision() {
		// Creates a rectangle around the bird and for every pillar (bottom and upper) in every pillar structure
		boolean isHit = false;
		Rectangle birdRect = new Rectangle(bird.getxPosition(), bird.getyPosition(), bird.getBirdImage().getWidth(null), bird.getBirdImage().getHeight(null));
		for(PillarStructure pillar: pillars) {
			Rectangle upperRect = new Rectangle(1280 - pillar.getDistance(), 0, PillarStructure.pillarWidth, pillar.getUpperHeight());
			Rectangle bottomRect = new Rectangle(1280 - pillar.getDistance(), 720 - pillar.getBottomHeight(), PillarStructure.pillarWidth, pillar.getBottomHeight());
			// if the bird hit one of the pillars
			if (birdRect.intersects(upperRect) || birdRect.intersects(bottomRect))
				isHit = true;
			if(birdRect.getCenterX() >= upperRect.getCenterX() && !pillar.isPassed()) {
				Board.currentScore++;
				pillar.setPassed(true);
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
				Board.currentScore++;
				Coin.playCoinSound();
			}
		}
		// if the bird hit the ground
		if (birdRect.intersects(Board.groundRect) || birdRect.intersects(Board.skyRect))
			isHit = true;
		if(isHit) {	
			// If Score achieved is greater than highest score, it is updated in the file
			if(Board.currentScore > currentPlayer.getHighestScore())
				Board.currentPlayer.updateHighestScore(Board.currentScore);
			// User is not playing anymore and bird thread is dead
			bird.playImpactSound();
			
			userState = UserState.RETRY_PAGE;
			Board.bird.isAlive = false;
			bird.interrupt();
			
			// Killing all remaining mines
			for(Mine mine: mines) {
				mine.setStatus("DEAD");
				mine.interrupt();
			}
			
			// Kills all remaining coins
			for(Coin coin: coins) {
				coin.setStatus("DEAD");
				coin.interrupt();
			}
			
			stopGame();
			// impactEffectTimer and playButtonTimer stop by themselves
			// Initialises change of background
			initRetryPage();
//			Checks if the user has surpasses the number of points to unlock the next level
//			If the user has completed the level before, the user keeps playing and the game doesn't stop
		} else if(currentScore >= scoreToUnlockLevel && currentPlayer.getLevelsAvailable() < 3) {
			if(currentPlayer.getLevelsAvailable() == Board.currentLevel && currentPlayer.getLevelsAvailable() <= 2) {
				stopGame();
				currentPlayer.updateLevelsAvailable();	
				currentScore = 0;
				userState = UserState.LEVEL_SELECTION_PAGE;
			}
		}
	}
	
//	Main paint method
//	This method divides to multiple drawing method depending on userState
	@Override
	public void paintComponent(Graphics g) {
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
		case PAUSE_PAGE:
			drawPausePage(g);
			break;
		case LEVEL_SELECTION_PAGE:
			drawLevelSelectionPage(g);
			break;
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
//	Draws level selection page
	public void drawLevelSelectionPage(Graphics g) {
//		Draws level selection page
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);
		g.setFont(getFont().deriveFont(Font.PLAIN, 40));
		if(currentPlayer.getLevelsAvailable() != 3)
			g.drawString("Reach  score  " + Board.scoreToUnlockLevel + "  to  unlock  the  next  level!", 300, 50);
		else
			g.drawString("All  levels  unlocked!", 300, 50);
//		Level 0 is accessible to every user
		Board.level1.drawUnpressedButton(g, 500, 100);
		switch (currentPlayer.getLevelsAvailable()) {
		case 1:
			level2.drawUnpressedButtonGreyScale(g, 500, 300);
			level3.drawUnpressedButtonGreyScale(g, 500, 500);
			break;
		case 2:
			level2.drawUnpressedButton(g, 500, 300);
			level3.drawUnpressedButtonGreyScale(g, 500, 500);
			break;
		case 3:
			level2.drawUnpressedButton(g, 500, 300);
			level3.drawUnpressedButton(g, 500, 500);
			break;
		}
		g.drawString("Level 1", 560, 170);
		g.drawString("Level 2", 560, 370);
		g.drawString("Level 3", 560, 570);
	}

//	Draws pause page
	public void drawPausePage(Graphics g) {
		drawGame(g);
		g.setFont(getFont().deriveFont(Font.PLAIN, 90));
		g.drawString("PAUSED", 500, 300);
	}
	
//	Draws retry page
	public void drawRetryPage(Graphics g) {
		drawGame(g);
		Board.retryButton.drawUnpressedButton(g, 315, 280);
		Board.mainMenuButton.drawUnpressedButton(g, 700, 280);
		g.setFont(getFont().deriveFont(Font.PLAIN, 70));
		g.drawString("RETRY", 350, 370);
		g.drawString("MAIN", 755, 345);
		g.drawString("MENU", 755, 390);
	}
	
//	Draws game when the user plays
	private void drawGame(Graphics g) {
		// Draws all background images, pillar structures and the bird
		for (Background image: backgroundImages)
			image.drawBackground(g);
		
		for (PillarStructure pillar: pillars)
			pillar.drawPillar(g);
		
		if (currentLevel >= 2)
			for (Mine mine: mines) {
				mine.drawMine(g);
			}
		if (currentLevel >= 3)
			for(Coin coin: coins)
				if(coin.isCollected() == false) {
					coin.drawCoin(g);
				}
		
		g.drawString(String.valueOf(Board.currentScore), 590, 130);
		
		bird.drawBird(g);
	}
	
//	Draws main menu page
	private void drawMainMenu(Graphics g) {
		// Draws the background image, game name, bird and the play button
		g.drawImage(this.backgroundImage, 0, 0, 1280, 720, this);
		g.setFont(getFont());
		g.drawString("FLAPPY BIRD", 220, 120); // 220 120
		bird.drawBird(g, 590, 150);
		Board.playButton.drawPlayButton(g);
		if(loggedIn) {
			g.setColor(Color.black);
			Board.logOutButton.drawUnpressedButton(g, 960, 520);
			g.drawString("LOG", 1040, 580);
			g.drawString("OUT", 1040, 620);
		}
	}
	
	// Method which gets the pixelated font and returns it
	public Font getFont() {
		Font font = null;
		try {
			URL fontURL = new URL("file:./Fonts/PixalatedFont.TTF");
			
			font = Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream());
			font = font.deriveFont(Font.PLAIN, 150);

		} catch (FontFormatException | IOException e) {
			
			e.printStackTrace();
		}
		return font;
	}
	
	// Small class which allows for recording and running code when pressing a key on the mouse and actuating a key press
	private class MAdapter extends MouseAdapter{
		
		// A method which gets a mouse events and handles it accordingly
				public void mouseClicked(MouseEvent e) {
					if (userState == UserState.MAIN_MENU) {
						// Creates a point the the mouse event coordinates, creates a rectangle around the playButton image
						Point point = new Point(e.getX(), e.getY());
						Rectangle playButtonBounds = new Rectangle(playButton.getX(), playButton.getY(), PlayButton.playButtonWidth, PlayButton.playButtonHeight);
						// Checks if the user clicked the play button in the main menu
						if(e.getButton() == MouseEvent.BUTTON1 && playButtonBounds.contains(point)) {
//							If not logged in, shows the panes to sign in as a user
							if(loggedIn == false) {
								JPanel p1 = new JPanel();
								JPanel p2 = new JPanel();
								// p1 components (username and password fields)
							    JLabel newUserLabel = new JLabel("Please enter a new username and password");
							    JTextField newUserTextField = new JTextField("Your username",16);
							    JPasswordField newUserPasswordField = new JPasswordField("", 16);
							  
							    // Styling
							    newUserTextField.setSelectionColor(Color.YELLOW);
							    newUserTextField.setSelectedTextColor(Color.RED);
							    newUserTextField.setHorizontalAlignment(JTextField.CENTER);
							    newUserPasswordField.setSelectionColor(Color.YELLOW);
							    newUserPasswordField.setSelectedTextColor(Color.RED);
							    newUserPasswordField.setHorizontalAlignment(JTextField.CENTER);
							    
//							    New User Button
							    JButton newUserButton = new JButton("OK");
//							    Errors and warnings
							    JLabel sameUsernameError = new JLabel();
							    JLabel emptyUsernameError = new JLabel();
							    JLabel warningLabel = new JLabel("<html>Spaces will be removed if included</html>");
							    sameUsernameError.setBackground(Color.red);
							    emptyUsernameError.setBackground(Color.red);
//							    Adding the components
							    p1.add(newUserLabel);
							    p1.add(newUserTextField);
							    p1.add(newUserPasswordField);
							    p1.add(newUserButton);
							    p1.add(warningLabel);
							    // p2 components
//							    radio buttons for existing users
							    ButtonGroup bg = new ButtonGroup();
							    for(Player player: players) {
							    	JRadioButton rb = new JRadioButton(player.getName());
							    		p2.add(rb);
							    	bg.add(rb);
							    }
//							    password field and button
							    JPasswordField existingUserPasswordField = new JPasswordField("", 16);
								existingUserPasswordField.setActionCommand("OK");
								JButton existingUserButton = new JButton("OK");
								if(players.size() == 0) {
									sameUsernameError.setText("<html><font color='red'>No users exist</font></html>");
									p2.add(sameUsernameError);
								} else {
									p2.add(existingUserPasswordField);
									p2.add(existingUserButton);
								}
//								Adding panes to container
							    JTabbedPane tp = new JTabbedPane();
							    tp.setBounds(500 ,300 ,400,300);  
							    tp.add("New User",p1);  
							    tp.add("Existing User",p2);
							    Board.this.add(tp);
							    
//							    If the user presses the button to register a new user
							    newUserButton.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
//										Removes all spaces
										String usernameInput = newUserTextField.getText().replaceAll(" ", "");
										String passwordInput = convertPasswordIntoString(newUserPasswordField.getPassword()).replaceAll(" ", "");
//										Checks if empty
										if(usernameInput.equals("") || passwordInput.equals("")) {
											emptyUsernameError.setText("<html><font color='red'>Username or passoword cannot be an empty string!</font></html>");
											p1.add(emptyUsernameError);
										}
										else if(!isUsernameExist(usernameInput)) {
											p1.remove(emptyUsernameError);
											addNewUsername(usernameInput, passwordInput);
											initPlayers();
											currentPlayer = findPlayer(usernameInput) != null ? findPlayer(usernameInput) : null;
											loggedIn = true;
											Board.this.remove(tp);
										} else {
											p1.remove(emptyUsernameError);
											sameUsernameError.setText("<html><font color='red'>Username already exists, please pick another!</font></html>");
											p1.add(sameUsernameError);
										}
									}
							    });
							    
//							    If the user presses the button to sign in as an existing user
							    existingUserButton.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										String password = convertPasswordIntoString(existingUserPasswordField.getPassword()).replaceAll(" " , "");
											if(bg.getSelection() != null) {
											currentPlayer = findPlayer(getSelectedButtonText(bg));
											if(currentPlayer.getPassword().equals(password) == true) {
											loggedIn = true;
											Board.this.remove(tp);
											} else {
												sameUsernameError.setText("<html><font color='red'>Password is incorrect</font></html>");
												p2.add(sameUsernameError);
												}
										} else {
											sameUsernameError.setText("<html><font color='red'>User is not picked</font></html>");
											p2.add(sameUsernameError);
											}
									}
									
								});
							    
							  
							} else { 
//								User is picking a level
								userState = UserState.LEVEL_SELECTION_PAGE;
							}
						}
//						Checks if the user pressed the log out button
						Rectangle logOutBounds = new Rectangle(logOutButton.getXPosition(), logOutButton.getYPosition(), logOutButton.getButtonWidth(), logOutButton.getButtonHeight());
						if(e.getButton() == MouseEvent.BUTTON1 && logOutBounds.contains(point)) {
							loggedIn = false;
							currentPlayer = null;
						}
					}
//					If the user presses either the retry or main menu buttons
					else if (userState == UserState.RETRY_PAGE) {
						Point point = new Point(e.getX(), e.getY());
						Rectangle retryImageBounds = new Rectangle(retryButton.getXPosition(), retryButton.getYPosition(), retryButton.getButtonWidth(), retryButton.getButtonHeight());
						Rectangle mainMenuImageBounds = new Rectangle(mainMenuButton.getXPosition(), mainMenuButton.getYPosition(), mainMenuButton.getButtonWidth(), mainMenuButton.getButtonHeight());
						if(e.getButton() == MouseEvent.BUTTON1 && retryImageBounds.contains(point)) {
							currentScore = 0;
							userState = UserState.IN_GAME;
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
//						If the user presses the mouse when in game then the bird jumps
					} else if (userState == UserState.IN_GAME) {
						bird.jump();
//						If the user presses either of the levels
					} else if(userState == UserState.LEVEL_SELECTION_PAGE) {
						Point point = new Point(e.getX(), e.getY());
						Rectangle level1Bounds = new Rectangle(level1.getXPosition(), level1.getYPosition(), level1.getButtonWidth(), level1.getButtonHeight());
						Rectangle level2Bounds = new Rectangle(level2.getXPosition(), level2.getYPosition(), level2.getButtonWidth(), level2.getButtonHeight());
						Rectangle level3Bounds = new Rectangle(level3.getXPosition(), level3.getYPosition(), level3.getButtonWidth(), level3.getButtonHeight());
						boolean startGame = false;
						if(e.getButton() == MouseEvent.BUTTON1 && level1Bounds.contains(point)) {
							currentLevel = 1;
							startGame = true;
						} else if(e.getButton() == MouseEvent.BUTTON1 && level2Bounds.contains(point) && currentPlayer.getLevelsAvailable() >= 2) {
							currentLevel = 2;
							startGame = true;
						} else if(e.getButton() == MouseEvent.BUTTON1 && level3Bounds.contains(point) && currentPlayer.getLevelsAvailable() >= 3) {
							currentLevel = 3;
							startGame = true;
						}
							
							if(startGame == true) {
//							User is now playing
							userState = UserState.IN_GAME;
							
							// Creates a new bird thread for user to play with
							bird = new Bird(590, 150);
							bird.isAlive = true;
							bird.start();
							bird.jump();
							
							// Initialise pillars and timers and starts the game
							initGame();
							}
						} 
					}
		
//		Gets a username and and returns the player info if exists, otherwise null 
		private Player findPlayer(String usernameInput) {
			for(Player player: players) {
				if(player.getName().equals(usernameInput))
					return player;
			}
			return null;
		}

//		Gets a username and returns if the username exists, otherwise false 
		private boolean isUsernameExist(String usernameInput) {
			for(Player player: players) {
				if(player.getName().equals(usernameInput))
					return true;
			}
			return false;
		}
		
//		Converts a array of chars into a string, used to convert a password
		private String convertPasswordIntoString(char [] password) {
			String stringPassword = "";
			for(int i = 0; i < password.length; i++) {
				stringPassword += password[i];
			}
			return stringPassword;
		}

//		Adds a new username to players.txt
		private void addNewUsername(String usernameInput, String passwordInput) {
			File file = new File("./Files/players.txt");
			try {
				Scanner sc = new Scanner(file);
				FileWriter fw = new FileWriter(file, true);
				if(Board.players.size() != 0)
					fw.write("\n");
				fw.write(usernameInput + " " + passwordInput + " 1 " + "0");
				fw.close();
				sc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		Returns the button text from a button group that is being used at the existing users pane tab
		private String getSelectedButtonText(ButtonGroup bg) {
			 for (Enumeration<AbstractButton> buttons = bg.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();
		            if (button.isSelected()) {
		                return button.getText();
		            }
		        }
			 return null;
		}
	}
	
	private class KAdapter implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}

//		If the user presses 'P':
//		1. If the user is in-game, the game is paused
//		2. If the user is in pause page, the game is resumed
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_P) {
				if(Board.userState == UserState.IN_GAME) {
					pause();
					Board.userState = UserState.PAUSE_PAGE;
				} else if(Board.userState == UserState.PAUSE_PAGE) {
					userState = UserState.IN_GAME;
					resume();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}
	}
}
