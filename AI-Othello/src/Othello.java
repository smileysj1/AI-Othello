import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Othello {
	public final int BOARDSIZE = 8;
	
	char[][] boardData = new char[BOARDSIZE][BOARDSIZE];

	boolean blackMove = true;

	public final char emptyToken = '-';

	public Othello() {
		//Fill the board with blanks
		for(char[] row: boardData)
			Arrays.fill(row, '-');

		//Create the starting pattern for the game
		boardData[3][3] = 'W';
		boardData[3][4] = 'B';
		boardData[4][3] = 'B';
		boardData[4][4] = 'W';
	}

	/**
	 * Plays a move at point and updates the game state accordingly.
	 * @param point
	 */
	public void playMove(Coordinate point) {
		final char playerToken = blackMove ? 'B' : 'W';
		final char opponentToken = blackMove ? 'W': 'B';
		
		Set<Coordinate> flips = new HashSet<>();
		
		flips.addAll(isEast(playerToken, opponentToken, point));
		flips.addAll(isWest(playerToken, opponentToken, point));
		flips.addAll(isNorth(playerToken, opponentToken, point));
		flips.addAll(isSouth(playerToken, opponentToken, point));
		flips.addAll(isNorthEast(playerToken, opponentToken, point));
		flips.addAll(isNorthWest(playerToken, opponentToken, point));
		flips.addAll(isSouthEast(playerToken, opponentToken, point));
		flips.addAll(isSouthWest(playerToken, opponentToken, point));

		boardData[point.Y][point.X] = playerToken;
		
		for(Coordinate c : flips) {
			boardData[c.Y][c.X] = playerToken;
		}
		
		blackMove = !blackMove;
	}
	
	/**
	 * Returns a set of all valid moves for the current game state.
	 * @return
	 */
	public Set<Coordinate> getAvailableMoves(){
		Set<Coordinate> moves = new HashSet<Coordinate>();

		final char playerToken = blackMove ? 'B' : 'W';
		final char opponentToken = blackMove ? 'W': 'B';

		for(int y = 0; y < boardData.length; y++) {
			for(int x = 0; x < boardData[0].length; x++) {
				//add all moves adjacent to opponent pieces
				if(boardData[y][x] == opponentToken) {
					moves.addAll(nearbyEmpty(new Coordinate(x, y)));
				}
			}
		}
		
		Set<Coordinate> badMoves = new HashSet<Coordinate>();
		
		for(Coordinate c : moves) {
			if(!validMove(playerToken, opponentToken, c))
				badMoves.add(c);
		}
		
		moves.removeAll(badMoves);

		return moves;
	}

	/**
	 * Returns set of spaces adjacent to point if they are empty
	 * @param point
	 * @return
	 */
	public Set<Coordinate> nearbyEmpty(Coordinate point){
		Set<Coordinate> moves = new HashSet<>();

		//x + 1
		if(point.X < boardData.length - 1 && boardData[point.Y][point.X + 1] == emptyToken) {
			moves.add(new Coordinate(point.X + 1, point.Y));
		}

		//x - 1
		if(point.X > 0 && boardData[point.Y][point.X - 1] == emptyToken) {
			moves.add(new Coordinate(point.X - 1, point.Y));
		}

		//y + 1
		if(point.Y < boardData.length - 1 && boardData[point.Y + 1][point.X] == emptyToken) {
			moves.add(new Coordinate(point.X, point.Y + 1));
		}

		//y - 1
		if(point.Y > 0 && boardData[point.Y - 1][point.X] == emptyToken) {
			moves.add(new Coordinate(point.X, point.Y - 1));
		}

		//x + 1, y + 1
		if(point.X < boardData.length - 1 && point.Y < boardData.length - 1 && boardData[point.Y + 1][point.X + 1] == emptyToken) {
			moves.add(new Coordinate(point.X + 1, point.Y + 1));
		}

		//x - 1, y + 1
		if(point.X > 0 && point.Y < boardData.length - 1 && boardData[point.Y + 1][point.X - 1] == emptyToken) {
			moves.add(new Coordinate(point.X - 1, point.Y + 1));
		}

		//x + 1, y - 1
		if(point.X < boardData.length - 1 && point.Y > 0 && boardData[point.Y - 1][point.X + 1] == emptyToken) {
			moves.add(new Coordinate(point.X + 1, point.Y - 1));
		}
		
		//x - 1, y - 1
		if(point.X > 0 && point.Y > 0 && boardData[point.Y - 1][point.X - 1] == emptyToken) {
			moves.add(new Coordinate(point.X - 1, point.Y - 1));
		}
		
		return moves;
	}

	/**
	 * Checks if a move is valid.
	 * @param playerToken
	 * @param opToken
	 * @param point
	 * @return
	 */
	public boolean validMove(char playerToken, char opToken, Coordinate point) {
		return 	isEast(playerToken, opToken, point).size() 		> 0 ||
				isWest(playerToken, opToken, point).size()		> 0 ||
				isNorth(playerToken, opToken, point).size() 	> 0 ||
				isSouth(playerToken, opToken, point).size() 	> 0 ||
				isNorthEast(playerToken, opToken, point).size() > 0 ||
				isNorthWest(playerToken, opToken, point).size() > 0 ||
				isSouthEast(playerToken, opToken, point).size() > 0 ||
				isSouthWest(playerToken, opToken, point).size() > 0;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the east.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isEast(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int x = point.X + 1; x < boardData.length; x++) {
			Coordinate c = new Coordinate(x, point.Y);
			
			if(boardData[c.Y][c.X] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.X] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the west.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isWest(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int x = point.X - 1; x > 0; x--) {
			Coordinate c = new Coordinate(x, point.Y);

			if(boardData[c.Y][c.X] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.X] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the south.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isSouth(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int y = point.Y + 1; y < boardData.length; y++) {
			Coordinate c = new Coordinate(point.X, y);

			if(boardData[c.Y][c.X] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.X] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the north.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isNorth(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int y = point.Y - 1; y > 0; y--) {
			Coordinate c = new Coordinate(point.X, y);
			
			if(boardData[c.Y][c.X] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.X] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the southeast.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isSouthEast(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int i = 1; point.Y + i < BOARDSIZE && point.X + i < BOARDSIZE; i++) {
			Coordinate c =  new Coordinate(point.X + i, point.Y + i);

			if(boardData[c.Y][c.X] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.X] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the southwest.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isSouthWest(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int i = 1; point.Y + i < BOARDSIZE && point.X - i > 0; i++) {
			Coordinate c = new Coordinate(point.X - i, point.Y + i);
			
			if(boardData[c.Y][c.X] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.X] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the northeast.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isNorthEast(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int i = 1; point.Y - i > 0 && point.X + i < BOARDSIZE; i++) {
			Coordinate c = new Coordinate(point.X + i, point.Y - i);
	
			if(boardData[c.Y][c.X] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.X] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the northwest.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isNorthWest(char token, char opToken, Coordinate point) {
		Set<Coordinate> flips = new HashSet<Coordinate>();
		
		for(int i = 1; point.Y - i > 0 && point.X - i > 0; i++) {
			Coordinate c = new Coordinate(point.X - i, point.Y - i);
			
			if(boardData[c.Y][c.Y] == token) {
				return flips;
			}
			else if(boardData[c.Y][c.Y] == opToken) {
				flips.add(c);
			}
			else if(boardData[c.Y][c.X] == emptyToken) {
				flips.clear();
				return flips;
			}
		}
		
		return flips;
	}
	
	public int boardScore() {
		int total = 0;

		for(int i = 0; i < boardData.length; i++) {
			for(int j = 0; j < boardData[0].length; j++) {
				switch(boardData[i][j]) {
				case 'B':
					total++;
					break;
				case 'W':
					total--;
					break;
				default:
					break;
				}
			}
		}

		return total;
	}

	@Override
	public String toString() {
		String out = "";
		for(int i = 0; i < boardData.length; i++) {
			for(int j = 0; j < boardData[0].length; j++) {
				out += boardData[i][j] + " ";
			}
			out += "\n";
		}

		return out;
	}
	
	public String toString(Set<Coordinate> availableMoves) {
		String out = "";
		final char playerToken = blackMove ? 'B' : 'W';
		final char opponentToken = blackMove ? 'W': 'B';
		
		for(int i = 0; i < boardData.length; i++) {
			for(int j = 0; j < boardData[0].length; j++) {
				if(availableMoves.contains(new Coordinate(j, i))) {
					out += Character.toLowerCase(playerToken) + " ";
				}
				else {
					out += boardData[i][j] + " ";
				}
			}
			out += "\n";
		}

		return out;
	}



}
