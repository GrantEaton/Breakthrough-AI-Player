package breakthrough;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import connect4.Connect4State;
import connect4.MiniMaxConnect4Player.ScoredConnect4Move;
import game.GameMove;
import game.GamePlayer;
import game.GameState;
import game.Util;

public class TeamA01BreakthroughPlayer extends GamePlayer {

	public final int MAX_DEPTH = 50;
	public int depthLimit;
	protected ScoredBreakthroughMove[] mvStack;

	protected class ScoredBreakthroughMove extends BreakthroughMove{
		public double score;

		public ScoredBreakthroughMove(int r1, int c1, int r2, int c2, double s) {
			super(r1,c1,r2,c2);
			score = s;
		}
		//initializes all to 0
		public ScoredBreakthroughMove() {
			super(0,0,0,0);
			score = 0.0;
		}

		public void set(int r1, int c1, int r2, int c2, double s) {
			startRow = r1;
			startCol = c1;
			endingRow = r2;
			endingCol = c2;
			score = s;
		}

	}


	public TeamA01BreakthroughPlayer(String n, int d) 
	{
		super(n, "Breakthrough");
		depthLimit = d;
	}

	public void init() {
		mvStack = new ScoredBreakthroughMove[MAX_DEPTH];
		for (int i = 0; i < MAX_DEPTH; i++) {
			mvStack[i] = new ScoredBreakthroughMove(0,0,7,7,0.0);
		}
	}

	protected boolean terminalValue(GameState brd, ScoredBreakthroughMove mv) {
		GameState.Status status = brd.getStatus();
		boolean isTerminal = true;

		if (status == GameState.Status.HOME_WIN) {
			mv.set(0, MAX_SCORE);
		} else if (status == GameState.Status.AWAY_WIN) {
			mv.set(0, -MAX_SCORE);
		} else if (status == GameState.Status.DRAW) {
			mv.set(0, 0);
		} else {
			isTerminal = false;
		}
		return isTerminal;
	}

	public void messageFromOpponent(String m)
	{ System.out.println(m); }
	private boolean validSquare(BreakthroughState brd, int row, int col)
	{
		return row >= 0 && row < BreakthroughState.N && col >= 0 && col < BreakthroughState.N;
	}
	private boolean safeMove(BreakthroughState brd, BreakthroughMove mv, int dir, char me, char opp)
	{
		int supportingRow = mv.endingRow - dir;
		int supportingCol1 = mv.endingCol - 1;
		int supportingCol2 = mv.endingCol + 1;

		int attackingRow = mv.endingRow + dir;
		int attackingCol1 = supportingCol1;
		int attackingCol2 = supportingCol2;

		boolean canBeTaken = false;

		if (validSquare(brd, attackingRow, attackingCol1) && brd.board[attackingRow][attackingCol1] == opp) {
			canBeTaken = true;
		} else if (validSquare(brd, attackingRow, attackingCol2) && brd.board[attackingRow][attackingCol2] == opp) {
			canBeTaken = true;
		}

		boolean safe = !canBeTaken;

		if (canBeTaken) {
			if (validSquare(brd, supportingRow, supportingCol1) && brd.board[supportingRow][supportingCol1] == me) {
				safe = supportingCol1 != mv.startCol;
			} else if (validSquare(brd, supportingRow, supportingCol2) && brd.board[supportingRow][supportingCol2] == me) {
				safe = supportingCol2 != mv.startCol;	
			}
		}

		return safe;
	}

	private void alphaBeta(Connect4State brd, int currDepth,
			double alpha, double beta)
	{
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;

		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);

		if (isTerminal) {
			;
		} else if (currDepth == depthLimit) {
			mvStack[currDepth].set(0, evalBoard(brd));
		} else {
			ScoredBreakthroughMove tempMv = new ScoredBreakthroughMove();

			double bestScore = (toMaximize ? 
					Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			ScoredBreakthroughMove bestMove = mvStack[currDepth];
			ScoredBreakthroughMove nextMove = mvStack[currDepth+1];

			bestMove.set(0, bestScore);
			GameState.Who currTurn = brd.getWho();

			int [] columns = new int [COLS];
			for (int j=0; j<COLS; j++) {
				columns[j] = j;
			}
			shuffle(columns);
			for (int i=0; i<Connect4State.NUM_COLS; i++) {
				int c = columns[i];
				if (brd.numInCol[c] < Connect4State.NUM_ROWS) {
					tempMv.col = c;				// initialize move
					brd.makeMove(tempMv);

					alphaBeta(brd, currDepth+1, alpha, beta);  // Check out move

					// Undo move
					brd.numInCol[c]--;
					int row = brd.numInCol[c]; 
					brd.board[row][c] = Connect4State.emptySym;
					brd.numMoves--;
					brd.status = GameState.Status.GAME_ON;
					brd.who = currTurn;

					// Check out the results, relative to what we've seen before
					if (toMaximize && nextMove.score > bestMove.score) {
						bestMove.set(c, nextMove.score);
					} else if (!toMaximize && nextMove.score < bestMove.score) {
						bestMove.set(c, nextMove.score);
					}

					// Update alpha and beta. Perform pruning, if possible.
					if (toMinimize) {
						beta = Math.min(bestMove.score, beta);
						if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
							return;
						}
					} else {
						alpha = Math.max(bestMove.score, alpha);
						if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
							return;
						}
					}
				}
			}
		}
	}

	public GameMove getMove(GameState state, string lastMove)
	{
		
		return //RETURN A GAME MOVE BASED ON A GLOBAL;


	}
	public static void main(String [] args)
	{
		//TODO: CHANGE DEPTH LIMIT BASED ON TIME
		GamePlayer p = new TeamA01BreakthroughPlayer("team A01 BT+",3);
		p.compete(args);
	}
	@Override
	public GameMove getMove(GameState state, String lastMv) {
		// TODO Auto-generated method stub
		return null;
	}
}
