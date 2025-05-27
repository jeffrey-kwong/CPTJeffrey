import arc.*;
import java.awt.image.BufferedImage;


public class CPTJeffrey{
	public static void main(String[] args) {
		Console con = new Console();
		// leaderboard file
		
		char charMenu = 'M';
		while (charMenu != 'Q') {
			if (charMenu == 'M') {
				// main menu
				
				// Draw buttons;
				con.println("Press 'P' to play, 'L' to check leaderboards, or 'Q' to quit.");
				con.repaint();
				
				getMenuInput(con, charMenu, 'M');
				con.clear();
			} else if (charMenu == 'P') {
				// play game
				
				con.println("What is your name? ");
				String strName = con.readLine();
				int intMoney = 1000;
				
				play(con, strName, intMoney, charMenu);
				
			} else if (charMenu == 'L') {
				// show leaderboard
				con.println("Leaderboard");
				con.repaint();
				charMenu = con.readChar();
			}
			
		}
		
		 // Close leaderboard file
		 con.closeConsole();
		 
	}
	
	// Plays one game of black jack, and returns p - play again, or l - lost
	public static void play(Console con, String strName, int intMoney, char charMenu){
		while (charMenu == 'P') {
			int[][] intDeck = generateDeck();
			BufferedImage[] images = generateDeckImages(con);
			
			con.println("You have $" + intMoney);
			
			String strResult = game(con, intMoney);
			
			// Win
			if (strResult.equals("Win")) {
				con.println("Win");
				con.println("Play or quit");
				char charInput = getPlayOrQuit(con);
				// Keep playing
				if (charInput == 'P') continue;
				
				// Quit
				else {
					// Add to leaderboard
					charMenu = 'L';
					return;
				}
			}
			
			// Lose
			else {
				con.println("Lose");
				con.println("Play or quit");
				
				// Add to leaderboard
				
				char charInput = getPlayOrQuit(con);
				
				// Play again
				if (charInput == 'P') return;
				
				// Quit
				else {
					charMenu = 'L';
					return;
				}
			}
		}
	}
	
	public static String game(Console con, int intMoney) {
		con.println("Win or lose? (W, L)");
		char charInput = con.readChar();
		if (charInput == 'W') return "Win";
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
		switch (intValue) {
			case 1:
				return "ace";
			case 11:
				return "jack";
			case 12:
				return "queen";
			case 13:
				return "king";
			default:
				return Integer.toString(intValue);
		}
	}
	
	// Get string based on suit value
	public static String getSuitString(int intSuit) {
		switch (intSuit) {
			case 1:
				return "diamonds";
			case 2:
				return "clubs";
			case 3:
				return "hearts";
			default:
				return "spades";
		}
	}
	
	// Get menu input, filters out non-menu options
	public static void getMenuInput(Console con, char charMenu, char charCurrentMenu) {
		while (charMenu == charCurrentMenu) {
			char charInput = (char) con.currentKey();
			if (charInput == 'M' || charInput == 'P' || charInput == 'L' || charInput == 'Q') {
				charMenu = charInput;
			}
		}
	}
	
	// Get play or quit input, filters out other options
	public static char getPlayOrQuit(Console con) {
		char charInput = 'A';
		while (charInput != 'P' && charInput != 'Q') {
			charInput = (char) con.currentKey();
		}
		return charInput;
	}
	
	/*public static void hit();
	public static void stand();
	public static void doubleDown();
	*/
	
	
	
}
