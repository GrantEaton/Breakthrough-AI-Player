/* 
 * Author: Grant Eaton, Bryan Lloyd, Stuart Wyse
 * Date: 11/9/2016
 */

package TeamAlphaPlayer;
import java.util.ArrayList;
import breakthrough.*;

import connect4.Connect4State;
import game.*;

public class TeamA01BreakthroughPlayer extends GamePlayer
{
	public static final double MAX_SCORE = 999;
	public final int MAX_DEPTH = 50;
	public int depthLimit;
	protected ScoredBreakthroughMove[] mvStack;

	protected class ScoredBreakthroughMove extends BreakthroughMove
	{
		public double score;

		public ScoredBreakthroughMove()
		{
			super(0,0,0,0);
			score = 0.0;
		}

		public ScoredBreakthroughMove (int r1, int c1, int r2, int c2, double s)
		{
			super(r1, c1, r2, c2);
			score = s;
		}

		public void set(int r1, int c1, int r2, int c2, double s)
		{
			startRow = r1;
			startCol = c1;
			endingRow = r2;
			startCol = c2;
			score = s;
		}
	}

	public TeamA01BreakthroughPlayer(String nname, int d)
	{
		super(nname, "Breakthrough");
		depthLimit = d;
	}

	public void messageFromOpponent(String m)
	{ System.out.println(m); }

	public void init()
	{
		mvStack = new ScoredBreakthroughMove[MAX_DEPTH];
		for(int i = 0; i < MAX_DEPTH; i++)
			mvStack[i] = new ScoredBreakthroughMove();
	}

	protected boolean terminalValue(BreakthroughState brd, ScoredBreakthroughMove mv)
	{
		BreakthroughState.Status status = brd.getStatus();
		boolean isTerminal = true;

		if(status == BreakthroughState.Status.HOME_WIN)
			mv.set(0,0,0,0,MAX_SCORE);
		else if(status == BreakthroughState.Status.AWAY_WIN)
			mv.set(0,0,0,0,-MAX_SCORE);
		else if(status == BreakthroughState.Status.DRAW)
			mv.set(0,0,0,0,0);
		else
			isTerminal = false;

		return isTerminal;
	}

	/* 
	 * returns an int representing the number of rows a piece is from the beginning, plus the second evaluation 
	 * with an average of the total distance of the home players from position 0 on the board
	 */
	private static int eval(BreakthroughState brd, char who)
	{
		int sum = 0;
		int count = 0;
		for(int r = 0; r < brd.N; r++)
			for(int c = 0; c < brd.N; c++)
				if(brd.board[r][c] == who && who == 'W'){
					count ++;
					sum+= (brd.N - r) - 1;
				}
				else if(brd.board[r][c] == who && who == 'B'){
					count ++;
					sum+= r;
				}
		int eval2 = sum/count;
		//System.out.println(brd.toString());
		//System.out.println(count*.80+eval2*.20);
		return (int) (count*.95+eval2*.05);
	}


	public static double evalBoard(BreakthroughState brd)
	{
		int score1 = eval(brd, BreakthroughState.homeSym) - eval(brd, BreakthroughState.awaySym);
		//int score2 = eval2(brd, BreakthroughState.homeSym);

		double score = score1;
		//System.out.println("Score: " + score);
		if(Math.abs(score) >= MAX_SCORE)
		{
			System.err.println("Problem with eval");
			System.exit(0);
		}
		return score;
	}

	public ArrayList<ScoredBreakthroughMove> getNextMoves(BreakthroughState brd, int r, int c)
	{
		//System.out.println("Looking for moves from: row: "+r+ " col: "+ c);
		ArrayList<ScoredBreakthroughMove> listOfMoves = new ArrayList<>();

		char me = brd.who == GameState.Who.HOME ?
				BreakthroughState.homeSym : BreakthroughState.awaySym;
		char opp = brd.who == GameState.Who.HOME ?
				BreakthroughState.awaySym : BreakthroughState.homeSym;

		int dir = brd.who == GameState.Who.HOME ? 1 : -1;
		//int side = brd.who == GameState.Who.HOME ? -1 : 1;

		int startRow = r;
		int startCol = c;
		int endingRow = startRow + dir;
		int endingCol;

		if(startCol != 0 && brd.board[endingRow][startCol-1] != me)
		{
			endingCol = startCol-1;

			ScoredBreakthroughMove mv = new ScoredBreakthroughMove(startRow, startCol,
					endingRow, endingCol, 0.0);
			//System.out.println("row: "+(endingRow)+ " col: "+ (endingCol));

			if(brd.moveOK(mv))
				listOfMoves.add(mv);
			else
				System.out.println("Illegal Move: " + mv.toString());
		}

		if(brd.board[endingRow][startCol] == BreakthroughState.emptySym)
		{
			endingCol = startCol;

			ScoredBreakthroughMove mv = new ScoredBreakthroughMove(startRow, startCol,
					endingRow, endingCol, 0.0);
			//System.out.println("row: "+(endingRow)+ " col: "+ (endingCol));

			if(brd.moveOK(mv))
				listOfMoves.add(mv);
			else
				System.out.println("Illegal Move: " + mv.toString());
		}

		if(startCol != brd.N-1 && brd.board[endingRow][startCol+1] != me)
		{
			endingCol = startCol+1;

			ScoredBreakthroughMove mv = new ScoredBreakthroughMove(startRow, startCol,
					endingRow, endingCol, 0.0);
			//System.out.println("row: "+(endingRow)+ " col: "+ (endingCol));

			if(brd.moveOK(mv))
				listOfMoves.add(mv);
			else
				System.out.println("Illegal Move: " + mv.toString());
		}

		return listOfMoves;
	}

	private void alphaBeta(BreakthroughState brd, int currDepth, double alpha, double beta)
	{

		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;

		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);

		if(isTerminal)
			;
		else if(currDepth == depthLimit)
			mvStack[currDepth].set(0,0,0,0,evalBoard(brd));
		else
		{
			double bestScore = (toMaximize ?
					Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			ScoredBreakthroughMove bestMove = mvStack[currDepth];
			ScoredBreakthroughMove nextMove = mvStack[currDepth+1];

			bestMove.set(0,0,0,0,bestScore);
			//GameState.Who currTurn = brd.getWho();

			char me = brd.who == GameState.Who.HOME ?
					BreakthroughState.homeSym : BreakthroughState.awaySym;

			for(int r = 0; r < brd.N; r++)
				for(int c = 0; c < brd.N; c++)
					if(brd.board[r][c] == me)
					{
						ArrayList<ScoredBreakthroughMove> listOfMoves = getNextMoves(brd, r, c);
						for(ScoredBreakthroughMove move : listOfMoves)
						{
							BreakthroughState newBrd = (BreakthroughState)brd.clone();
							//System.out.println("Current Depth: " + currDepth);
							//System.out.println("Making " + move.toString());
							newBrd.makeMove(move);
							//System.out.println("Made " + move.toString());

							alphaBeta(newBrd, currDepth+1, alpha, beta);

							//brd = (BreakthroughState)oldBrd.clone();

							// Check out the results, relative to what we've seen before
							if (toMaximize && nextMove.score > bestMove.score) {
								bestMove.startRow = move.startRow;
								bestMove.startCol = move.startCol;
								bestMove.endingRow = move.endingRow;
								bestMove.endingCol = move.endingCol;
								bestMove.score = nextMove.score;
							}
							else if (!toMaximize && nextMove.score < bestMove.score) {
								bestMove.startRow = move.startRow;
								bestMove.startCol = move.startCol;
								bestMove.endingRow = move.endingRow;
								bestMove.endingCol = move.endingCol;
								bestMove.score = nextMove.score;
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

	public GameMove getMove(GameState brd, String lastMove)
	{
		BreakthroughState newBrd = (BreakthroughState)brd.clone();
		alphaBeta((BreakthroughState)newBrd, 0,
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		System.out.println(mvStack[0].score);
		return mvStack[0];
	}

	public static void main(String[] args)
	{
		int depth = 6;
		GamePlayer p = new TeamA01BreakthroughPlayer("team A01 BT+",depth);
		p.compete(args);
		//p.solvePuzzles(new String [] {"BTPuzzle2"});
	}
}
