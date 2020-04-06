import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
	
		Othello game = new Othello();
		
		System.out.println(game);	
		
		/*System.out.println(game.toString(game.getAvailableMoves()));
		System.out.println(game.boardScore());
		
		game.playMove(new Coordinate(2, 3));
		
		System.out.println(game.toString(game.getAvailableMoves()));
		System.out.println(game.boardScore());
		*/
		
		Random rand = new Random();
		
		Set<Coordinate> moves = game.getAvailableMoves();
		
		while(moves.size() > 0) {
			
			//get a random move
			int index = rand.nextInt(moves.size());
			Iterator<Coordinate> iter = moves.iterator();
			for(int i = 0; i < index; i++) {
				iter.next();
			}

			//play random move
			game.playMove(iter.next());
			
			//calculate moves for next player
			moves = game.getAvailableMoves();
			
			//show board state
			System.out.println(game.toString());
			System.out.println(game.boardScore());
		}
	}

}
