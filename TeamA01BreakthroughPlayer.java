package breakthrough;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import connect4.Connect4State;
import game.GameMove;
import game.GamePlayer;
import game.GameState;
import game.GameState.Who;
import game.Util;

public class TeamA01BreakthroughPlayer extends GamePlayer {

	//Maxscore for connect4 is 12*total board spots +1, so i (grant) mimicked that... may need to change,
	//i dont know where 12 came from exactly
	public static final int MAX_SCORE = 12*49 + 1;
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

	//initializes the moveStack
	public void init() {
		mvStack = new ScoredBreakthroughMove[MAX_DEPTH];
		for (int i = 0; i < MAX_DEPTH; i++) {
			mvStack[i] = new ScoredBreakthroughMove();
		}
	}

	//checks if the board is complete
	protected boolean terminalValue(BreakthroughState brd, ScoredBreakthroughMove mv) {
		GameState.Status status = brd.getStatus();
		boolean isTerminal = false;

		for(int j=0; j<brd.N-1;j++){
			if(brd.board[0][j]=='B'){
				return true;
			}
			if(brd.board[brd.N-1][j]=='W'){
				return true;
			}
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

	private void undoMove(BreakthroughState brd, int r, int c){

	}

	private void alphaBeta(BreakthroughState brd, int currDepth,
			double alpha, double beta)
	{
		
		//System.out.println("eval: "+ evalBoard(brd));
		
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;

		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);
		
		//if were at a terminal state (someone won)
		if (isTerminal) {
			//neg = -1 or 1 based on if were home or not
			int neg = toMaximize ? 1: -1;
			mvStack[currDepth].set(0,0,0,0, 999*neg);
			return;
			//if were at the depth, start building the "tree" of evaluation functions (done searching, 
		}  else if (currDepth == depthLimit) {
			// else end recursion; its time to start comparing to pick a best move
			mvStack[currDepth].set(0,0,0,0, evalBoard(brd));
		} else {
			//: make a temp move as our possible solution?
			//ScoredBreakthroughMove tempMv = new ScoredBreakthroughMove();

			//set our best score found to neg infinity if home
			double bestScore = (toMaximize ? 
					Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			ScoredBreakthroughMove bestMove = mvStack[currDepth];
			ScoredBreakthroughMove nextMove = mvStack[currDepth+1];

			bestMove.set(0,0,0,0, bestScore);
			GameState.Who currTurn = brd.getWho();
			
			char me = brd.who == GameState.Who.HOME ?
					BreakthroughState.homeSym : BreakthroughState.awaySym;
			System.out.println("me "+me);
			//shuffle(columns); //we got rid of this function, do we actually need it?
			// iterate through all the positions on the board to check for our p
			for (int r=0; r<brd.N-1; r++) {
				for(int c=0; c<brd.N-1; c++){
					if (brd.board[r][c] == me) {   // if we found one of our players
						BreakthroughState oldBrd = brd;
						//get the moves possible at this location
						ArrayList<ScoredBreakthroughMove> listofMoves = getNextMoves(brd,r,c);
						for(ScoredBreakthroughMove move : listofMoves){
							System.out.println(brd.toString());
							brd.makeMove(move);

							alphaBeta(brd, currDepth+1, alpha, beta);  // Check out move

							// Undo move
							brd.board = oldBrd.board;
							brd.numMoves--;
							brd.status = GameState.Status.GAME_ON;
							brd.who = currTurn;

							// if max, test next move to see if higher than our current best, if min, test next move to see if lower than best
							if (toMaximize && nextMove.score > bestMove.score) {
								bestMove.set(nextMove.startRow,nextMove.startCol, nextMove.endingRow,nextMove.endingCol, nextMove.score);
							} else if (!toMaximize && nextMove.score < bestMove.score) {
								bestMove.set(nextMove.startRow,nextMove.startCol, nextMove.endingRow,nextMove.endingCol, nextMove.score);
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
		}
	}

	//returns a list of scoredBreakthroughMoves
	public ArrayList<ScoredBreakthroughMove> getNextMoves(BreakthroughState brd, int curRow, int curCol){
		System.out.println("current spot: row: "+curRow+ " col: "+ curCol);
		ArrayList<ScoredBreakthroughMove> listofMoves = new ArrayList<>();
		char me = brd.who == GameState.Who.HOME ?
				BreakthroughState.homeSym : BreakthroughState.awaySym;
		char opp = brd.who == GameState.Who.HOME ?
				BreakthroughState.awaySym : BreakthroughState.homeSym;
		int dir = brd.who == GameState.Who.HOME ? +1 : -1;
		ScoredBreakthroughMove mv = new ScoredBreakthroughMove();
		mv.startCol = curCol;
		mv.startRow = curRow;
		mv.endingRow = curRow+dir;
		//get move in front and to the left of us
		if(curCol != 0){
			mv.endingCol = curCol-1;

			//make a new state and make the move, then add it to list of moves
			BreakthroughState modifiedBrd = (BreakthroughState) brd.clone();
			modifiedBrd.makeMove(mv);
			mv.score = evalBoard(modifiedBrd);
			//check if next move is ok before adding 
			if(brd.moveOK(mv)){
				System.out.println("row: "+(curRow+dir)+ " col: "+ (curCol-1));
				listofMoves.add(mv);
			}
			
		}
		//get move in front of us
		if(brd.board[curRow+dir][curCol] != opp){
			mv.endingCol = curCol;
			//make a new state and make the move, then add it to list of moves
			BreakthroughState modifiedBrd = (BreakthroughState) brd.clone();
			modifiedBrd.makeMove(mv);
			mv.score = evalBoard(modifiedBrd);
			if(brd.moveOK(mv)){
				System.out.println("row: "+(curRow+dir)+ " col: "+ (curCol));
				listofMoves.add(mv);
			}
			else{
				System.out.println("error checking for next moves. look in getNextMoves()");
			}
		}
		//get move in front and to the right
		if(curCol != 7){
			mv.endingCol = curCol+1;
			//make a new state and make the move, then add it to list of moves
			BreakthroughState modifiedBrd = (BreakthroughState) brd.clone();
			modifiedBrd.makeMove(mv);
			mv.score = evalBoard(modifiedBrd);
			if(brd.moveOK(mv)){
				System.out.println("row: "+(curRow+dir)+ " col: "+ (curCol+1));
				listofMoves.add(mv);
			}
			else{
				System.out.println("error checking for next moves. look in getNextMoves()");
			}
		}
		if(listofMoves.isEmpty()){
			return null;
		}
		else{
			return listofMoves;
		}
	}

	//called by the game to get our players next move (whatever alpha beta spits out)
	public GameMove getMove(GameState brd, String lastMove)
	{ 
		alphaBeta((BreakthroughState)brd, 0, Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY);
		System.out.println(mvStack[0].score);
		return mvStack[0];
	}

	//evaluation of the board
	public static int evalBoard(BreakthroughState brd)
	{ 
		int score = eval(brd, BreakthroughState.homeSym) - eval(brd, BreakthroughState.awaySym);
		if (Math.abs(score) > MAX_SCORE) {
			System.err.println("Problem with eval");
			System.exit(0);
		}
		return score;
	}

	//evaluation 
	private static int eval(BreakthroughState brd, char who)
	{
		int cnt = 0;
		for (int r=0; r<brd.N-1; r++) {
			for (int c=0; c<brd.N-1; c++) {
				if(brd.board[r][c] == who){ //NOTE: Grant's Edits: i dont know if this works for sure. 
					//my goal is to count up the total of that player and return that #
					cnt++;
				}
			}
		}
		return cnt;
	}

	public static void main(String [] args)
	{
		//TODO: CHANGE DEPTH LIMIT BASED ON TIME
		GamePlayer p = new TeamA01BreakthroughPlayer("team A01 BT+",3);
		//p.compete(args);
		p.solvePuzzles(new String [] { "BTPuzzle2"});
		
	}

}
