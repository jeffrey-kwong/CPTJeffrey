import arc.*;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;


public class CPTJeffrey{
	public static void main(String[] args) {
		Console con = new Console("Jeff's Black Jack Game", 1280, 720);
		// leaderboard file
		
		char charMenu = 'm';
		while (charMenu != 'q') {
			if (charMenu == 'm') {
				// main menu
				drawMainMenu(con);
				
				while (charMenu == 'm') {
					char charInput = con.getChar();
					if (charInput == 'p' || charInput == 'v' || charInput == 'q' || charInput == 'h') {
						charMenu = charInput;
					}
				}
				
			} else if (charMenu == 'p') {
				// Play game
				drawPregameMenu(con);
				
				// setup the reader to the typing box position
				con.print("\n\n\n\n\n\n\n\n\n\n\n\n\n" + "                                           ");
				String strName = con.readLine();
				con.clear();
				
				int intMoney = 1000;
				
				play(con, strName, intMoney, charMenu);
				
			} else if (charMenu == 'v') {
				// Show leaderboard
				con.println("Leaderboard");
				con.repaint();
				charMenu = con.readChar();
				
			} else if (charMenu == 'h') {
				// Show helpful info
				
			}
			
		}
		
		 // Close leaderboard file
		 con.closeConsole();
		 
	}
	
	// Plays one game of black jack, and returns p - play again, or l - lost
	public static void play(Console con, String strName, int intMoney, char charMenu){
		while (charMenu == 'p') {
			int[][] intDeck = generateDeck();
			BufferedImage[] images = generateDeckImages(con);
			
			String strResult = game(con, intMoney);
			
			char charInput;
			
			// Win
			if (strResult.equals("Win")) {
				// Win screen
				con.println("Win");
				con.println("Play or quit");
				
				// Wait for the correct input
				charInput = con.getChar();
				while (charInput != 'p' && charInput != 'q') {
					charInput = con.getChar();
				}
				
				// Keep playing
				if (charInput == 'p') continue;
				
				// Quit
				else {
					// Add to leaderboard
					// System.out.println(); the logged thing
					
					charMenu = 'v';
					return;
				}
			}
			
			// Lose
			else {
				// Lose screen
				con.println("Lose");
				con.println("Play or quit");
				
				// Add to leaderboard
				// System.out.println(); the logged thing
				
				// Wait for the correct input
				charInput = con.getChar();
				while (charInput != 'p' && charInput != 'q') {
					charInput = con.getChar();
				}
				
				// Play again
				if (charInput == 'p') return;
				
				// Quit
				else {
					charMenu = 'v';
					return;
				}
			}
		}
	}
	
	public static String game(Console con, int intMoney) {
		drawGameMenu(con);
		
		con.println("Win or lose? (W, L)");
		char charInput = con.getChar();
		if (charInput == 'w') return "Win";
		else return "Lose";
	}
	
	// Generate a new deck of card
	public static int[][] generateDeck() {
		int[][] intDeck = new int[52][3];
		
		// Thirteen ranks (A - K) and four suits (D - S)
		for (int intValue = 1; intValue <= 13; intValue++) {
			for (int intSuit = 1; intSuit <= 4; intSuit++) {
				int[] intCard = intDeck[4 * (intValue - 1) + intSuit - 1];
				intCard[0] = intValue; // Value
				intCard[1] = intSuit; // Suit
				intCard[2] = (int) (Math.random() * 100); // Order of shuffle
			}
		}
		
		// Shuffle deck using bubble sort
		shuffleDeck(intDeck);
		
		return intDeck;
	}
	
	// Shuffles deck
	public static void shuffleDeck(int[][] intDeck) {
		
		//Bubble sort the random integer to shuffle
		for (int i = 0; i < intDeck.length - 1; i++) {
			for (int j = 0; j < intDeck.length - i - 1; j++) {
				if (intDeck[j][2] > intDeck[j + 1][2]) {
					// Swap the cards at those indexes
					int[] temp = intDeck[j];
					intDeck[j] = intDeck[j + 1];
					intDeck[j + 1] = temp;
				}
			}
		}
	}
	
	// Test function for seeing a deck on console
	public static void printDeck(Console con, int[][] intDeck) {
		for (int intCard = 0; intCard < intDeck.length; intCard++) {
			con.println(intDeck[intCard][0] + " of " + intDeck[intCard][1]);	
		}
	}
	
	// Loads all card images into an array
	public static BufferedImage[] generateDeckImages(Console con) {
		BufferedImage[] images = new BufferedImage[52];
		
		// Card order is ace of diamonds, ace of clubs, ace of hearts, ace of spades, two of diamonds...
		for (int intValue = 1; intValue <= 13; intValue++) {
			for (int intSuit = 1; intSuit <= 4; intSuit++) {
				images[4 * (intValue - 1) + intSuit - 1] = con.loadImage("Cards/" + getValueString(intValue) + "_of_" + getSuitString(intSuit) + ".png");
			}
		}
		
		return images;
	}
	
	// Get card image
	public static BufferedImage getCardImage(int[][] intDeck, BufferedImage[] images, int intCard) {
		return images[4 * (intDeck[intCard][0] - 1) + intDeck[intCard][1] - 1];
	}
	
	// Get string based on card value
	public static String getValueString(int intValue) {
		if (intValue == 1) {
			return "ace";
		} else if (intValue == 11) {
			return "jack";
		} else if (intValue == 12) {
			return "queen";
		} else if (intValue == 13) {
			return "king";
		} else {
			return Integer.toString(intValue);
		}
}
	
	// Get string based on suit value
	public static String getSuitString(int intSuit) {
		if (intSuit == 1) {
			return "diamonds";
		} else if (intSuit == 2) {
			return "clubs";
		} else if (intSuit == 3) {
			return "hearts";
		} else {
			return "spades";
		}
	}
	
	// Draws the main menu
	public static void drawMainMenu(Console con) {
		// background
		con.drawImage(con.loadImage("MainMenuBackground.png"), 0, 0);
		
		// title / logo
		con.drawImage(con.loadImage("BlackJackTitle.png"), (1280 - 750) / 2 , 0);
		
		con.setDrawFont(new Font("SansSerif", 0, 27));
		
		// play button
		con.setDrawColor(new Color(50, 168, 82));
		con.fillRoundRect(500, 280, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(p)lay", 640 - 40, 280 + 10);
		
		// leaderboard
		con.setDrawColor(new Color(227, 227, 66));
		con.fillRoundRect(500, 280 + 100, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(v)iew high scores", 640 - 120, 280 + 100 + 10);
		
		// help
		//50, 146, 201
		con.setDrawColor(new Color(50, 146, 201));
		con.fillRoundRect(500, 280 + 200, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(h)elp", 640 - 40, 280 + 200 + 10);
		
		// quit
		con.setDrawColor(new Color(217, 57, 33));
		con.fillRoundRect(500, 280 + 300, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(q)uit", 640 - 40, 280 + 300 + 10);
		
		con.repaint();
	}
	
	// Draws the screen that asks for the player's name
	public static void drawPregameMenu(Console con) {
		// background
		con.drawImage(con.loadImage("BrownBackground.png"), 0, 0);
		
		// what is your name text
		con.setDrawColor(Color.white);
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 50));
		con.drawString("What is your name?", 640 - 240, 200);
		
		// box for typing
		con.setDrawColor(new Color(50, 50, 50));
		con.fillRoundRect(498, 298, 284, 54, 35, 35);
		
		con.setDrawColor(new Color(56, 145, 89));
		con.fillRoundRect(500, 300, 280, 50, 35, 35);
		
		con.repaint();
	}
	
	// Draws the game screen
	public static void drawGameMenu(Console con) {
		// background
		con.drawImage(con.loadImage("BlackJackTable.png"), 0, 0);
		
		// deck of cards
		con.drawImage(con.loadImage("FaceDownCard.png"), 1030, 140);
		
		con.repaint();
	}
	
	/*public static void hit();
	public static void stand();
	public static void doubleDown();
	*/
	
	
	
}
