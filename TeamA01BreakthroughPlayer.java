package breakthrough;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import game.GameMove;
import game.GamePlayer;
import game.GameState;
import game.Util;

public class TeamA01BreakthroughPlayer extends GamePlayer {

	public final int MAX_DEPTH = 50;
	public int depthLimit;
	
	protected class scoredBreakthroughMove extends BreakthroughMove{
		public double score;
		public scoredBreakthroughMove(int r1, int c1, int r2, int c2, double s) {
			super(r1,c1,r2,c2);
			score = s;
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
	
	
	public GameMove getMove(GameState state, string lastMove)
	{
		BreakthroughState board = (BreakthroughState)state;
		
		Comparator<>
		PriorityQueue<>
		return 
		
		
	}
	public static void main(String [] args)
	{
		GamePlayer p = new TeamA01BreakthroughPlayer("team A01 BT+");
		p.compete(args);
	}
	@Override
	public GameMove getMove(GameState state, String lastMv) {
		// TODO Auto-generated method stub
		return null;
	}
}
