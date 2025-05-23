import arc.*;
import java.util.Collections;

public class CPTJeffrey{
	public static void main(String[] args) {
		Console con = new Console();
		int[][] intCardDeck = generateDeck();
		shuffleDeck(intCardDeck);
		printDeck(con, intCardDeck);
	}
	
	// Generate a new deck of card
	public static int[][] generateDeck() {
		int[][] intCardDeck = new int[52][2];
		
		// Thirteen ranks (A - K) and four suits (D - S)
		for (int intRank = 0; intRank < 13; intRank++) {
			for (int intSuit = 0; intSuit < 4; intSuit++) {
				int[] intCard = intCardDeck[4 * intRank + intSuit];
				intCard[0] = intRank + 1;
				intCard[1] = intSuit + 1;
			}
		}
		return intCardDeck;
	}
	
	// Shuffles the deck
	public static void shuffleDeck(int[][] intCardDeck) {
		int[] intTemp;
		int intRand;
		
		for (int intCard = 0; intCard < intCardDeck.length; intCard++) {
			intRand = (int)(Math.random()*intCardDeck.length);
			intTemp = intCardDeck[intCard];
			intCardDeck[intCard] = intCardDeck[intRand];
			intCardDeck[intRand] = intTemp;
		}
	}
	
	// Test function for seeing a deck on console
	public static void printDeck(Console con, int[][] intCardDeck) {
		for (int intCard = 0; intCard < intCardDeck.length; intCard++) {
			con.println(intCardDeck[intCard][0] + " of " + intCardDeck[intCard][1]);
		}
	}
}
