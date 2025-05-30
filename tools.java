import arc.*;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;


public class tools{
	// Game loop for placing bets, and playing, and returns p - play again, or v - lost
	public static char play(Console con, String strName, char charMenu){
		int intMoney = 1000;
		int intBet = 0;
		int intResult = 0;
		int intGames = 0;
		char charInput;
		
		// Secret bonus for being named statitan
		if (strName.equalsIgnoreCase("statitan")) intMoney = 10000;
		
		// Game loop
		while (true) {
			// Get bet
			intBet = getBet(con, intMoney);
			
			// Play a game
			if (intBet != 0) {
				intResult = game(con, intMoney, intBet);
				intGames++;
				intMoney = intMoney - intBet + intResult * intBet;
				
				con.sleep(700);
				
				con.setDrawColor(Color.white);
				con.setDrawFont(new Font("Berlin Sans FB", Font.BOLD, 120));
				
				if (intResult == 0) {
					con.drawString("You lost", 370, 270);
				}
				else if (intResult == 1) {
					con.drawString("You tied", 370, 270);
				}
				else {
					con.drawString("You won", 370, 270);
				}
				con.repaint();
				con.sleep(1500);
			}
			
			// Quit
			if (intBet == 0) {
				// Add to leaderboard
				addToLeaderboard(strName, intGames, intMoney);
				System.out.println("Logged " + strName + " with score $" + intMoney + " in leaderboard");
				
				
				// Background, Nice Game!, and score screen
				drawGameMenu(con);
				drawScore(con, strName, intMoney, intGames);
				con.setDrawColor(Color.white);
				con.setDrawFont(new Font("Berlin Sans FB", Font.BOLD, 120));
				con.drawString("Nice game!", 305, 10);
				
				con.repaint();
				
				// Wait for player to press quit
				charInput = con.getChar();
				while (charInput != 'q') {
					charInput = con.getChar();
				}
				break;
			}
			
			// Lose
			if (intMoney <= 0) {
				
				// Add to leaderboard
				addToLeaderboard(strName, intGames, intMoney);
				System.out.println("Logged " + strName + " with score $" + intMoney + " in leaderboard");
				
				// Background, Bankrupt!, and score screen
				drawGameMenu(con);
				drawScore(con, strName, intMoney, intGames);
				con.setDrawColor(Color.white);
				con.setDrawFont(new Font("Berlin Sans FB", Font.BOLD, 120));
				con.drawString("Bankrupt!", 343, 10);
				con.repaint();
				
				// Wait for the player to press 'p' to play again or 'q' to quit
				charInput = con.getChar();
				while (charInput != 'p' && charInput != 'q') {
					charInput = con.getChar();
				}
				
				// Play new game
				if (charInput == 'p') return 'p';
				
				// Quit
				else break;
			}
		}
		
		return 'v';
	}
	
	// Plays one game of black jack
	public static int game(Console con, int intMoney, int intBet) {	
		int[][] intDeck = generateDeck();
		BufferedImage[] images = generateDeckImages(con);
		int[][] intDrawnCards = new int[52][2]; // {x-pos, y-pos}
		int intCards = 0;
		
		int[][] intPlayer = new int[5][2];
		int[][] intDealer = new int[5][2];
		
		drawGameMenu(con, intMoney, intBet);
		con.repaint();
		
		// Dealing initial cards
		intCards = drawCard(con, 530, 530, intDeck, images, intCards, intDrawnCards, intPlayer[0], intMoney, intBet);
		intCards = drawCard(con, 530, 10, intDeck, images, intCards, intDrawnCards, intDealer[0], intMoney, intBet);
		intCards = drawCard(con, 555, 530, intDeck, images, intCards, intDrawnCards, intPlayer[1], intMoney, intBet);
		
		drawGameMenu(con, intMoney, intBet);
		drawDrawnCards(con, intDeck, images, intCards, intDrawnCards);
		drawTotals(con, intPlayer, intDealer);
		con.repaint();
		
		// Check if start off with a win
		if (getTotal(intPlayer) == 21) {
			return 3;
		}
		
		// Player's turn
		char charAction = 'h';
		int intPlayerHit = 0;
		
		while (charAction != 's') {
			// Allow double down on first turn, when card total is between 9-11, and when there is enough money
			if (intPlayerHit == 0 && (9 <= intPlayer[0][0] + intPlayer[1][0] && intPlayer[0][0] + intPlayer[1][0] <= 11) && intMoney - intBet * 2 >= 0) {
				charAction = getAction(con, true);
				
				// Double down
				if (charAction == 'd') {
					intBet *= 2;
					intCards = drawCard(con, 580 + 25 * intPlayerHit, 530, intDeck, images, intCards, intDrawnCards, intPlayer[2 + intPlayerHit], intMoney, intBet);
					intPlayerHit++;
					
					drawGameMenu(con, intMoney, intBet);
					drawDrawnCards(con, intDeck, images, intCards, intDrawnCards);
					drawTotals(con, intPlayer, intDealer);
					con.repaint();
					
					charAction = 's';
					con.sleep(400);
				}
			}
			else {
				charAction = getAction(con, false);
			}
			
			// Hit
			if (charAction == 'h') {
				intCards = drawCard(con, 580 + 25 * intPlayerHit, 530, intDeck, images, intCards, intDrawnCards, intPlayer[2 + intPlayerHit], intMoney, intBet);
				intPlayerHit++;
			}
			drawGameMenu(con, intMoney, intBet);
			drawDrawnCards(con, intDeck, images, intCards, intDrawnCards);
			drawTotals(con, intPlayer, intDealer);
			con.repaint();
			
			// Bust
			if (getTotal(intPlayer) > 21) {
				return 0;
			}
			
			// Player gets 5 cards without busting and wins
			if (intPlayerHit >= 3) {
				return 3;
			}
		}
	
		// Dealer's turn
		int intDealerHit = 0;
		while (getTotal(intDealer) < 17) {
			intCards = drawCard(con, 555 + 25 * intDealerHit, 10, intDeck, images, intCards, intDrawnCards, intDealer[1 + intDealerHit], intMoney, intBet);
			intDealerHit++;
			drawGameMenu(con, intMoney, intBet);
			drawDrawnCards(con, intDeck, images, intCards, intDrawnCards);
			drawTotals(con, intPlayer, intDealer);
			con.repaint();
			
			con.sleep(500);
			
			// Dealer bust
			if (getTotal(intDealer) > 21) {
				return 2;
			}
			
			// Dealer gets 5 cards without busting and wins
			if (intDealerHit >= 4) {
				return 0;
			}
		}

		if (getTotal(intDealer) > getTotal(intPlayer)) {
			// Player lost
			return 0;
		} else if (getTotal(intDealer) == getTotal(intPlayer)) {
			// Player tied
			return 1;
		}
		// player won
		else return 2;
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
		for (int inti = 0; inti < intDeck.length - 1; inti++) {
			for (int intj = 0; intj < intDeck.length - inti - 1; intj++) {
				if (intDeck[intj][2] > intDeck[intj + 1][2]) {
					// Swap the cards at those indexes
					int[] temp = intDeck[intj];
					intDeck[intj] = intDeck[intj + 1];
					intDeck[intj + 1] = temp;
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
	public static BufferedImage getCardImage(Console con, int[][] intDeck, BufferedImage[] images, int intCard) {
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
		con.drawImage(con.loadImage("Images/MainMenuBackground.png"), 0, 0);
		
		// title / logo
		con.drawImage(con.loadImage("Images/BlackJackTitle.png"), (1280 - 750) / 2 , 0);
		
		con.setDrawFont(new Font("SansSerif", 0, 27));
		
		// play button
		con.setDrawColor(new Color(50, 168, 82));
		con.fillRoundRect(500, 320, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(p)lay", 640 - 40, 320 + 10);
		
		// leaderboard
		con.setDrawColor(new Color(227, 227, 66));
		con.fillRoundRect(500, 320 + 100, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(v)iew high scores", 640 - 120, 320 + 100 + 10);
		
		// quit
		con.setDrawColor(new Color(217, 57, 33));
		con.fillRoundRect(500, 320 + 200, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(q)uit", 640 - 40, 320 + 200 + 10);
		
		con.repaint();
	}
	
	// Draws the screen that asks for the player's name
	public static void drawPregameMenu(Console con) {
		// background
		con.drawImage(con.loadImage("Images/BrownBackground.png"), 0, 0);
		
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
		con.drawImage(con.loadImage("Images/BlackJackTable.png"), 0, 0);
		
		// deck of cards
		con.drawImage(con.loadImage("Cards/FaceDownCard.png"), 1030, 140);
	}
	
	// Draws the game screen with the total money and bet displayed
	public static void drawGameMenu(Console con, int intMoney, int intBet) {
		drawGameMenu(con);
		
		// bet
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 50));
		con.setDrawColor(Color.white);
		con.drawString("Money: $" + (intMoney - intBet), 80, 50);
		con.drawString("Bet: $" + intBet, 80, 110);
	}
	
	// Draw drawn cards
	public static void drawDrawnCards(Console con, int[][] intDeck, BufferedImage[] images, int intCards, int[][] intDrawnCards) {
		for (int inti = 0; inti < intCards; inti++) {
			con.drawImage(getCardImage(con, intDeck, images, inti), intDrawnCards[inti][0], intDrawnCards[inti][1]);
		}
	}
	
	// Draws bet menu
	public static void drawBetButtons(Console con, int intMoney) {
		con.setDrawFont(new Font("SansSerif", 0, 30));
		// (1) Bet $50
		con.setDrawColor(new Color(52, 134, 227));
		con.fillRoundRect(50, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(1) Bet $50", 103, 500 + 8);
		
		// (2) Bet $100
		con.setDrawColor(new Color(53, 219, 67));
		con.fillRoundRect(350, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(2) Bet $100", 403, 500 + 8);
		
		// (3) Bet 50%
		con.setDrawColor(new Color(235, 226, 54));
		con.fillRoundRect(650, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(3) Bet 50%", 703, 500 + 8);
		
		// (4) Bet 100%
		con.setDrawColor(new Color(214, 49, 49));
		con.fillRoundRect(950, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(4) Bet 100%", 1003, 500 + 8);
		
		// (5) Enter amount
		con.setDrawColor(new Color(230, 44, 214));
		con.fillRoundRect(50, 620, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(5) Enter amount", 67, 620 + 8);
		
		// (q)uit
		con.setDrawColor(new Color(161, 66, 245));
		con.fillRoundRect(950, 620, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(q)uit", 1047, 620 + 8);
		
		// additional words for info
		con.setDrawFont(new Font("SansSerif", 0, 17));
		con.setDrawColor(new Color(30, 30, 30));
		
		con.drawString("$" + (int) (Math.ceil(intMoney * 0.5)), 760, 500 + 40);
		con.drawString("$" + intMoney, 1060, 500 + 40);
	}
	
	// Draws bet menu without the enter amount text
	public static void drawBetButtons(Console con, int intMoney, boolean doNotDrawEnterAmount) {
		con.setDrawFont(new Font("SansSerif", 0, 30));
		// (1) Bet $50
		con.setDrawColor(new Color(52, 134, 227));
		con.fillRoundRect(50, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(1) Bet $50", 103, 500 + 8);
		
		// (2) Bet $100
		con.setDrawColor(new Color(53, 219, 67));
		con.fillRoundRect(350, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(2) Bet $100", 403, 500 + 8);
		
		// (3) Bet 50%
		con.setDrawColor(new Color(235, 226, 54));
		con.fillRoundRect(650, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(3) Bet 50%", 703, 500 + 8);
		
		// (4) Bet 100%
		con.setDrawColor(new Color(214, 49, 49));
		con.fillRoundRect(950, 500, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(4) Bet 100%", 1003, 500 + 8);
		
		// (5) Enter amount
		con.setDrawColor(new Color(230, 44, 214));
		con.fillRoundRect(50, 620, 280, 70, 30, 30);
		
		if (!doNotDrawEnterAmount) {
			con.setDrawColor(Color.black);
			con.drawString("(5) Enter amount", 67, 620 + 8);
		}
		// (q)uit
		con.setDrawColor(new Color(161, 66, 245));
		con.fillRoundRect(950, 620, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(q)uit", 1047, 620 + 8);
		
		// additional words for info
		con.setDrawFont(new Font("SansSerif", 0, 17));
		con.setDrawColor(new Color(30, 30, 30));
		
		con.drawString("$" + (int) (Math.ceil(intMoney * 0.5)), 760, 500 + 40);
		con.drawString("$" + intMoney, 1060, 500 + 40);
	}
	
	//  Getting bet input
	public static int getBet(Console con, int intMoney) {
		drawGameMenu(con);
		drawBetButtons(con, intMoney);
		
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 50));
		con.setDrawColor(Color.white);
		con.drawString("Money: $" + intMoney, 80, 50);
		
		con.repaint();
		
		// Get input as 1, 2, 3, 4, 5, or q
		char charInput;
		while (true) {
			charInput = con.getChar();
			if (charInput == '1' || charInput == '2' || charInput == '3' || charInput == '4' || charInput == '5' || charInput == 'q') {
				break;
			}
		}
		
		if (charInput == '1') {
			return 50;
		} else if (charInput == '2') {
			return 100;
		} else if (charInput == '3') {
			return (int) (Math.ceil(intMoney * 0.5));
		} else if (charInput == '4') {
			return intMoney;
		} else if (charInput == '5') {
			drawBetButtons(con, intMoney, true);
			int intBet;
			
			while (true) {
				con.clear();
				con.setTextFont(new Font("SansSerif", 0, 29));
				con.setTextColor(Color.black);
				con.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "              $");
				intBet = con.readInt();
				
				// Flash red and black if amount is 0 or less or greater than total money
				if (intBet > intMoney || intBet <= 0) {
					con.setTextColor(new Color(255, 25, 0));
					con.sleep(400);
					con.setTextColor(Color.black);
					con.sleep(400);
					con.setTextColor(new Color(255, 25, 0));
					con.sleep(400);
				}
				else break;
			}
			con.clear();
			return intBet;
		} else return 0;
	}
	
	// Draws a card
	public static int drawCard(Console con, int intx, int inty, int[][] intDeck, BufferedImage[] images, int intCards, int[][] intDrawnCards, int[] intCardSlot, int intMoney, int intBet) {
		animateDrawCard(con, intx, inty, intDeck, images, intCards, intDrawnCards, intMoney, intBet);
		intDrawnCards[intCards][0] = intx;
		intDrawnCards[intCards][1] = inty;
		
		intCardSlot[0] = intDeck[intCards][0]; // Put the value of the card in either the dealer's or player's cards
		intCardSlot[1] = intDeck[intCards][1]; // Put the suit of the card in either the dealer's or player's cards
		
		return intCards + 1;
	}
	
	// Animates the card being drawn
	public static void animateDrawCard(Console con, int intx2, int inty2, int[][] intDeck, BufferedImage[] images, int intCards, int[][] intDrawnCards, int intMoney, int intBet) {
		int intx1 = 1030;
		int inty1 = 140;
		BufferedImage card = con.loadImage("Cards/FaceDownCard.png");

		double dbldx = intx2 - intx1;
		double dbldy = inty2 - inty1;

		for (int inti = 0; inti <= 60; inti++) {
			double dblPercent = (double) inti / 60;

			// Smooth interpolation using smooth step
			double dblPercentSmooth = dblPercent * dblPercent * (3 - 2 * dblPercent);

			int currentX = (int) Math.round(intx1 + dbldx * dblPercentSmooth);
			int currentY = (int) Math.round(inty1 + dbldy * dblPercentSmooth);

			drawGameMenu(con, intMoney, intBet);
			drawDrawnCards(con, intDeck, images, intCards, intDrawnCards);
			con.drawImage(card, currentX, currentY);

			con.repaint();
			con.sleep(3);
		}

		con.sleep(20); // Small pause after the draw finishes
	}
	
	// Returns whether the player hit, stand, or double down
	public static char getAction(Console con, boolean doubleDown) {
		drawActions(con, doubleDown);
		con.repaint();
		
		// Get input as h, s, d
		char charInput;
		while (true) {
			charInput = con.getChar();
			if (charInput == 'h' || charInput == 's' || charInput == 'd') {
				break;
			}
		}
		
		return charInput;
	}
	
	// Draws hit, stand, and double down buttons
	public static void drawActions(Console con, boolean doubleDown) {
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 32));
		
		if (doubleDown) {
			// Hit
			con.setDrawColor(new Color(52, 134, 227));
			con.fillRoundRect(200, 400, 280, 70, 30, 30);
			
			con.setDrawColor(Color.black);
			con.drawString("(h)it", 305, 400 + 8);
			
			// Stand
			con.setDrawColor(new Color(53, 219, 67));
			con.fillRoundRect(500, 400, 280, 70, 30, 30);
			
			con.setDrawColor(Color.black);
			con.drawString("(s)tand", 580, 400 + 8);
			
			// Double down
			con.setDrawColor(new Color(235, 226, 54));
			con.fillRoundRect(800, 400, 280, 70, 30, 30);
			
			con.setDrawColor(Color.black);
			con.drawString("(d)ouble down", 823, 400 + 8);
		} else {
			// Hit
			con.setDrawColor(new Color(52, 134, 227));
			con.fillRoundRect(300, 400, 280, 70, 30, 30);
			
			con.setDrawColor(Color.black);
			con.drawString("(h)it", 405, 400 + 8);
			
			// Stand
			con.setDrawColor(new Color(53, 219, 67));
			con.fillRoundRect(700, 400, 280, 70, 30, 30);
			
			con.setDrawColor(Color.black);
			con.drawString("(s)tand", 780, 400 + 8);
		}
	}
	
	// Returns the total value of cards in either the player's or dealer's hand
	public static int getTotal(int[][] intCards) {
		int intTotal = 0;
		int intAces = 0;
		for (int intCard = 0; intCard < intCards.length; intCard++) {
			if (intCards[intCard][0] == 1) {
				intTotal += 11;
				intAces += 1;
			}
			else if (intCards[intCard][0] == 11) intTotal += 10;
			else if (intCards[intCard][0] == 12) intTotal += 10;
			else if (intCards[intCard][0] == 13) intTotal += 10;
			else intTotal += intCards[intCard][0];
		}
		
		// Converts aces from 11 to 1 if necessary
		while (intTotal > 21) {
			if (intAces > 0) {
				intTotal -= 10;
				intAces--;
			}
			else break;
		}
		return intTotal;
	}
	
	// Draws the total value of the cards above the player's hand and below the dealer's hand
	public static void drawTotals(Console con, int[][] intPlayer, int[][] intDealer) {
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 33));
		con.setDrawColor(Color.black);
		
		con.drawString("" + getTotal(intDealer), 621, 203);
		con.drawString("" + getTotal(intPlayer), 621, 471);
	}
	
	// Draws the ending score in the game
	public static void drawScore(Console con, String strName, int intMoney, int intGames) {
		// Background
		con.setDrawColor(new Color(0, 0, 0, 170));
		con.fillRect(230, 0, 820, 720);
		
		// Statistics
		con.setDrawColor(Color.white);
		con.setDrawFont(new Font("SansSerif", 0, 50));
		
		con.drawString("Name: " + strName, 300, 300);
		con.drawString("Total games: " + intGames, 300, 370);
		con.drawString("Total money: $" + intMoney, 300, 440);
		
		con.setDrawFont(new Font("SansSerif", 0, 30));
		con.drawString("Score saved to leaderboard!", 450, 530);
		
		// (q)uit
		con.setDrawColor(new Color(161, 66, 245));
		con.fillRoundRect(950, 620, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(q)uit", 1047, 620 + 8);
	}
	
	// Formats the leaderboard into a 2D String array
	public static String[][] getLeaderboard() {
		TextInputFile leaderboard = new TextInputFile("leaderboard.txt");
		String[][] leaderboardArray = new String[100][3];
		
		int inti = 0;
		while (leaderboard.eof() == false) {
			leaderboardArray[inti][0] = leaderboard.readLine();
			leaderboardArray[inti][1] = leaderboard.readLine();
			leaderboardArray[inti][2] = leaderboard.readLine();
			inti++;
		}
		
		leaderboard.close();
		return leaderboardArray;
	}
	
	// Adds an element to the leaderboard file
	public static void addToLeaderboard(String strName, int intGames, int intMoney) {
		String[][] leaderboardArray = getLeaderboard();
		
		leaderboardArray = addToLeaderboardArray(leaderboardArray, strName, intGames, intMoney);
		
		int intLength = getLeaderboardArrayLength(leaderboardArray);
		
		TextOutputFile leaderboardOutput = new TextOutputFile("leaderboard.txt");
		
		for (int intElement = 0; intElement < intLength; intElement++) {
			leaderboardOutput.println(leaderboardArray[intElement][0]);
			leaderboardOutput.println(leaderboardArray[intElement][1]);
			leaderboardOutput.println(leaderboardArray[intElement][2]);
		}
		
		leaderboardOutput.close();
		
		
	}
	
	// Returns the amount of elements in the leaderboard file
	public static int getLeaderboardLength() {
		TextInputFile leaderboard = new TextInputFile("leaderboard.txt");
		
		int intLength = 0;
		while (leaderboard.eof() == false) {
			leaderboard.readLine();
			leaderboard.readInt();
			leaderboard.readInt();
			intLength++;
		}
		
		leaderboard.close();
		return intLength;
	}
	
	// Returns the amount of elements in a leaderboard array
	public static int getLeaderboardArrayLength(String[][] leaderboardArray) {
		int intLength = 0;
		while (leaderboardArray[intLength][0] != null) {
			intLength++;
		}
		return intLength;
	}
	
	// Adds an element to a leaderboard array and sorts it by amount of money
	public static String[][] addToLeaderboardArray(String[][] oldLeaderboardArray, String strName, int intGames, int intMoney) {
		String[][] newLeaderboardArray = new String[100][3];
		
		// Find leaderboard length
		int intLength = getLeaderboardLength();
		
		// Check if file is empty
		if (intLength == 0) {
			newLeaderboardArray[0][0] = strName;
			newLeaderboardArray[0][1] = "" + intGames;
			newLeaderboardArray[0][2] = "" + intMoney;
			return newLeaderboardArray;
		}
		
		// Find how many elements have greater money than the new element
		int intAbove = 0;
		while (oldLeaderboardArray[intAbove][2] != null) {
			if (Integer.parseInt(oldLeaderboardArray[intAbove][2]) > intMoney) {
				intAbove++;
			}
			else break;
		}
		
		for (int intElement = 0; intElement < intLength + 1; intElement++) {
			// Elements that are greater than the new one
			if (intElement < intAbove) {
				newLeaderboardArray[intElement][0] = oldLeaderboardArray[intElement][0];
				newLeaderboardArray[intElement][1] = oldLeaderboardArray[intElement][1];
				newLeaderboardArray[intElement][2] = oldLeaderboardArray[intElement][2];
			}
			// Elements that are below the new one
			else if (intElement > intAbove) {
				newLeaderboardArray[intElement][0] = oldLeaderboardArray[intElement - 1][0];
				newLeaderboardArray[intElement][1] = oldLeaderboardArray[intElement - 1][1];
				newLeaderboardArray[intElement][2] = oldLeaderboardArray[intElement - 1][2];
			}
			// The new element placement
			else {
				newLeaderboardArray[intElement][0] = strName;
				newLeaderboardArray[intElement][1] = "" + intGames;
				newLeaderboardArray[intElement][2] = "" + intMoney;
			}
		}
		
		return newLeaderboardArray;
	}
	
	// Draws the leaderboard menu
	public static void drawLeaderboard(Console con) {
		TextInputFile leaderboard = new TextInputFile("leaderboard.txt");
		
		// background
		con.drawImage(con.loadImage("Images/LeaderboardBackground.png"), 0, 0);
		
		con.setDrawColor(Color.white);
		
		// Title
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 100));
		con.drawString("Leaderboard", 300, 10);
		
		// Headings
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 20));
		
		// Rank
		con.drawString("Rank", 330, 160);
		// Name
		con.drawString("Player", 430, 160);
		// Games
		con.drawString("Games", 720, 160);
		// Money
		con.drawString("Money", 840, 160);
		
		con.fillRoundRect(250,  160 + 38, 780, 8, 8, 16);
		
		// Elements
		con.setDrawFont(new Font("SansSerif", Font.BOLD, 30));
		
		int intElement = 1;
		int inty;
		while (leaderboard.eof() == false && intElement < 10) {
			inty = 160 + 45 * intElement;
			// Rank
			con.drawString("" + intElement, 330, inty);
			// Name
			con.drawString(leaderboard.readLine(), 430, inty);
			// Games
			con.drawString("" + leaderboard.readInt(), 720, inty);
			// Money
			con.drawString("$" + leaderboard.readInt(), 840, inty);
			
			con.fillRoundRect(280, inty + 45, 720, 4, 5, 5);
			intElement++;
		}
		
		// (m)ain menu
		con.setDrawColor(new Color(227, 227, 66));
		con.fillRoundRect(50, 620, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(m)ain menu", 50 + 42, 620 + 10);
		
		// (p)lay
		con.setDrawColor(new Color(50, 168, 82));
		con.fillRoundRect(950, 620, 280, 70, 30, 30);
		
		con.setDrawColor(Color.black);
		con.drawString("(p)lay", 950 + 94, 620 + 10);
		
	}
	

}
