import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {

	private static final int HUMAN = 0;
	private static final int RANDOM = 1;
	private static final int MINIMAX = 2;
	
	public static void main(String[] args) {

		Othello game = new Othello();
		Random rand = new Random();
		Set<Coordinate> moves = game.getAvailableMoves();
		Scanner scan = new Scanner(System.in);
		
		
		System.out.println("Welcome to Othello.");
		System.out.println("Score > 0 means Black is winning.  Score < 0 means White is winning.");
		
		System.out.println("Enter Black AI type (0: Human, 1: Random AI, 2: Minimax): ");
		int blackType = scan.nextInt();
		
		System.out.println("Enter White AI type (0: Human, 1: Random AI, 2: Minimax): ");
		int whiteType = scan.nextInt();
		

		while(!moves.isEmpty()) {
			int playerType = -1;
			
			if(game.blackMove) {
				System.out.println("It's black's move.");
				playerType = blackType;
				
			}
			else { //white plays randomly
				System.out.println("It's white's move.");
				playerType = whiteType;
			}
			

			//show board state
			System.out.println(game.toString());
			System.out.println("Score: " + game.boardScore() + "\n");
			
			
			switch(playerType) {
			case HUMAN:
				System.out.println("Available moves: " + moves);
				System.out.println("Input move X: ");
				
				int x = scan.nextInt();
				
				System.out.println("Input move Y: ");
				
				int y = scan.nextInt();
				
				game.playMove(new Coordinate(x, y));
				
				break;
			case RANDOM:
				//get a random move
				int index = rand.nextInt(moves.size());
				Iterator<Coordinate> iter = moves.iterator();
				for(int i = 0; i < index; i++) {
					iter.next();
				}

				//play random move
				game.playMove(iter.next());
				
				break;
			case MINIMAX:
				Coordinate move = game.getBestMoveMinimax(game.boardData, game.getPlayerToken(), game.getOpponentToken());
				
				game.playMove(move);
				break;
			}

			//calculate moves for next player
			moves = game.getAvailableMoves();
		}
		
		
		//print who won
		System.out.println("\n\n****GAME OVER****");
		System.out.println(game.boardScore() == 0 ? "TIE" : (game.boardScore() > 0 ? "BLACK WIN" : "WHITE WIN"));
		System.out.println(game.toString());
		System.out.println("Score: " + game.boardScore() + "\n");
		
	}

}
