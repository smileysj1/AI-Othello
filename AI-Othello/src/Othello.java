import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Othello {
	private static final int MAX_SEARCH_DEPTH = 5;

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

	public void playMove(Coordinate point) {
		boardData = playMove(boardData, getPlayerToken(), point);
		
		blackMove = !blackMove;
	}
	
	/**
	 * Plays a move at point and updates the game state accordingly.
	 * @param point
	 */
	public char[][] playMove(char[][] board, char token, Coordinate point) {
		char[][] tempBoard = copyBoard(board, BOARDSIZE);
		Set<Coordinate> flips = new HashSet<>();
		
		flips.addAll(isEast(tempBoard, getPlayerToken(), getOpponentToken(), point));
		flips.addAll(isWest(tempBoard, getPlayerToken(), getOpponentToken(), point));
		flips.addAll(isNorth(tempBoard, getPlayerToken(), getOpponentToken(), point));
		flips.addAll(isSouth(tempBoard, getPlayerToken(), getOpponentToken(), point));
		flips.addAll(isNorthEast(tempBoard, getPlayerToken(), getOpponentToken(), point));
		flips.addAll(isNorthWest(tempBoard, getPlayerToken(), getOpponentToken(), point));
		flips.addAll(isSouthEast(tempBoard, getPlayerToken(), getOpponentToken(), point));
		flips.addAll(isSouthWest(tempBoard, getPlayerToken(), getOpponentToken(), point));

		tempBoard[point.Y][point.X] = token;
		
		for(Coordinate c : flips) {
			tempBoard[c.Y][c.X] = token;
		}
		
		return tempBoard;
	}
	
	public Coordinate getBestMoveMinimax(char[][] board, char playerToken, char opToken) {
		char[][] tempBoard = copyBoard(board, BOARDSIZE);
		
		Set<Coordinate> moves = getAvailableMoves(tempBoard, playerToken, opToken);
		
		int bestVal;
		
		//Set best based on if we are white or black
		if(playerToken == 'B') {
			bestVal = Integer.MIN_VALUE;
		}
		else {
			bestVal = Integer.MAX_VALUE;
		}
		
		Coordinate bestMove = null;
		
		for(Coordinate move : moves) {
			//pick highest value move
			if(playerToken == 'B') {	//black wants higher scores
				int val = getValueMoveMinimax(tempBoard, move, playerToken, opToken, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
				
				if(val > bestVal) {
					bestVal = val;
					bestMove = move;
				}
			}
			
			if(playerToken == 'W') {	//white wants lower scores
				int val = getValueMoveMinimax(tempBoard, move, playerToken, opToken, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
				
				if(val < bestVal) {
					bestVal = val;
					bestMove = move;
				}
			}
		}
		
		
		return bestMove;
	}
	
	public int getValueMoveMinimax(char[][] board, Coordinate move, char playerToken, char opToken, int searchDepth, int alpha, int beta, boolean maxTurn) {
		char[][] tempBoard = copyBoard(board, BOARDSIZE);
		
		tempBoard = playMove(tempBoard, playerToken, move);
		
		Set<Coordinate> moves = getAvailableMoves(tempBoard, playerToken, opToken);
		
		int best = maxTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		if(moves.isEmpty() || searchDepth == MAX_SEARCH_DEPTH) return boardScore(board, BOARDSIZE);
		
		//Recurse down the tree
		for (Coordinate m : moves) {
			int val = getValueMoveMinimax(tempBoard, m, opToken, playerToken, searchDepth + 1, alpha, beta, !maxTurn);
			
			if(maxTurn) {
				best = Math.max(best, val);
				alpha = Math.max(alpha, best);
				
				if(beta <= alpha) {
					//System.out.println("branch pruned | alpha " + alpha + " | beta " + beta);
					break;
				}
			}else {
				best = Math.min(best, val);
				beta = Math.min(beta, best);
				
				if(beta <= alpha) {
					//System.out.println("branch pruned | alpha " + alpha + " | beta " + beta);
					break;
				}
			}
		}
		
		return best;
	}
	
	/**
	 * Returns a set of all valid moves for the current game state.
	 * @return
	 */
	public Set<Coordinate> getAvailableMoves(){
		return getAvailableMoves(boardData, getPlayerToken(), getOpponentToken());
	}
	
	public Set<Coordinate> getAvailableMoves(char[][] board, char playerToken, char opToken){
		Set<Coordinate> moves = new HashSet<Coordinate>();

		for(int y = 0; y < board.length; y++) {
			for(int x = 0; x < board[0].length; x++) {
				//add all moves adjacent to opponent pieces
				if(board[y][x] == opToken) {
					moves.addAll(nearbyEmpty(new Coordinate(x, y)));
				}
			}
		}
		
		Set<Coordinate> badMoves = new HashSet<Coordinate>();
		
		for(Coordinate c : moves) {
			if(!validMove(board, playerToken, opToken, c))
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
	public boolean validMove(char[][] board, char playerToken, char opToken, Coordinate point) {
		return 	isEast(board, playerToken, opToken, point).size() 		> 0 ||
				isWest(board, playerToken, opToken, point).size()		> 0 ||
				isNorth(board, playerToken, opToken, point).size() 	> 0 ||
				isSouth(board, playerToken, opToken, point).size() 	> 0 ||
				isNorthEast(board, playerToken, opToken, point).size() > 0 ||
				isNorthWest(board, playerToken, opToken, point).size() > 0 ||
				isSouthEast(board, playerToken, opToken, point).size() > 0 ||
				isSouthWest(board, playerToken, opToken, point).size() > 0;
	}
	
	/**
	 * Returns flippable pieces between the point and the first token to the east.
	 * @param token
	 * @param opToken
	 * @param point
	 * @return
	 */
	public Set<Coordinate> isEast(char[][] boardData, char token, char opToken, Coordinate point) {
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
	public Set<Coordinate> isWest(char[][] boardData, char token, char opToken, Coordinate point) {
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
	public Set<Coordinate> isSouth(char[][] boardData, char token, char opToken, Coordinate point) {
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
	public Set<Coordinate> isNorth(char[][] boardData, char token, char opToken, Coordinate point) {
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
	public Set<Coordinate> isSouthEast(char[][] boardData, char token, char opToken, Coordinate point) {
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
	public Set<Coordinate> isSouthWest(char[][] boardData, char token, char opToken, Coordinate point) {
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
	public Set<Coordinate> isNorthEast(char[][] boardData, char token, char opToken, Coordinate point) {
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
	public Set<Coordinate> isNorthWest(char[][] boardData, char token, char opToken, Coordinate point) {
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
	
	public char getPlayerToken() {
		return blackMove ? 'B' : 'W';
	}
	
	public char getOpponentToken() {
		return blackMove ? 'W': 'B';
	}
	
	/**
	 * Returns score of board.  Positive means black is winning, negative means white is winning.
	 * @return
	 */
	public int boardScore() {
		int total = 0;

		for(int i = 0; i < BOARDSIZE; i++) {
			for(int j = 0; j < BOARDSIZE; j++) {
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
	
	/**
	 * Returns score of board.  Positive means black is winning, negative means white is winning.
	 * @return
	 */
	public int boardScore(char[][] board, int boardsize) {
		int total = 0;

		for(int i = 0; i < boardsize; i++) {
			for(int j = 0; j < boardsize; j++) {
				switch(board[i][j]) {
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
	
	public static char[][] copyBoard(char[][] board, int boardsize){
		char[][] out = new char[boardsize][boardsize];
		
		for(int i = 0; i < boardsize; i++) {
			for(int j = 0; j < boardsize; j++) {
				out[i][j] = board[i][j];
			}
		}
		
		return out;
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
	
	/**
	 * Alternate to string that also shows current available moves as lowercase letters.
	 * @param availableMoves
	 * @return
	 */
	public String toString(Set<Coordinate> availableMoves) {
		String out = "";

		for(int i = 0; i < boardData.length; i++) {
			for(int j = 0; j < boardData[0].length; j++) {
				if(availableMoves.contains(new Coordinate(j, i))) {
					out += Character.toLowerCase(getPlayerToken()) + " ";
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
