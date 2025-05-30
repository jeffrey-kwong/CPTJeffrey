import arc.*;
import java.awt.Font;
import java.awt.Color;

public class CPTJeffrey{
	public static void main(String[] args) {
		Console con = new Console("Jeff's Black Jack Game", 1280, 720);
		con.setTextFont(new Font("SansSerif", Font.BOLD, 30));
		
		char charMenu = 'm';
		char charInput;
		
		while (charMenu != 'q') {
			if (charMenu == 'm') {
				// main menu
				tools.drawMainMenu(con);
				
				// Wait for one of the menu options
				while (charMenu == 'm') {
					charInput = con.getChar();
					if (charInput == 'p' || charInput == 'v' || charInput == 'q') {
						charMenu = charInput;
					}
				}
				
			} else if (charMenu == 'p') {
				// Play game
				
				// Ask for player name
				tools.drawPregameMenu(con);
				
				// setup the reader to the typing box position
				con.setTextFont(new Font("SansSerif", Font.BOLD, 23));
				con.setTextColor(Color.black);
				con.print("\n\n\n\n\n\n\n\n\n\n\n" + "                                                                      ");
				String strName = con.readLine();
				con.clear();
				
				
				charMenu = tools.play(con, strName, charMenu);
				
			} else if (charMenu == 'v') {
				// Show leaderboard
				tools.drawLeaderboard(con);
				con.repaint();
				
				// Wait for either 'p' - play again or 'm' - (m)ain menu
				while (charMenu == 'v') {
					charInput = con.getChar();
					if (charInput == 'p' || charInput == 'm') {
						charMenu = charInput;
					}
				}
			}
		}
		 con.closeConsole();
		 
	}
	
	}
