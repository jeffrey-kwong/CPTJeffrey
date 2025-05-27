import arc.*;
import java.awt.image.BufferedImage;


public class CPTJeffrey{
	public static void main(String[] args) {
		Console con = new Console();
		int[][] intDeck = generateDeck();
	}
	
	// Generate a new deck of card
	public static int[][] generateDeck() {
		int[][] intDeck = new int[52][3];
		
		// Thirteen ranks (A - K) and four suits (D - S)
		for (int intRank = 0; intRank < 13; intRank++) {
			for (int intSuit = 0; intSuit < 4; intSuit++) {
				int[] intCard = intDeck[4 * intRank + intSuit];
				intCard[0] = intRank + 1; // Value
				intCard[1] = intSuit + 1; // Suit
				intCard[2] = (int) (Math.random() * 100); // Order of shuffle
			}
		}
		
		// Shuffle deck using bubble sort
		shuffleDeck(intDeck);
		
		return intDeck;
	}
	
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
	
	/*public static void hit();
	public static void stand();
	public static void doubleDown();
	*/
	
	
	
}
