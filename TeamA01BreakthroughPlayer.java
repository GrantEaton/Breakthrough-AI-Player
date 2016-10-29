package breakthrough;

import java.util.ArrayList;

import game.GameMove;
import game.GamePlayer;
import game.GameState;
import game.Util;

public class TeamA01BreakthroughPlayer extends GamePlayer {

	public TeamA01BreakthroughPlayer(String n) 
	{
		super(n, "Breakthrough");
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
	public GameMove getMove(GameState state, String lastMove)
	{
		BreakthroughState board = (BreakthroughState)state;
		ArrayList<BreakthroughMove> takes = new ArrayList<BreakthroughMove>();  
		ArrayList<BreakthroughMove> safeMoves = new ArrayList<BreakthroughMove>();  
		ArrayList<BreakthroughMove> vulnerableMoves = new ArrayList<BreakthroughMove>();  
		BreakthroughMove mv = new BreakthroughMove();
		char me = state.who == GameState.Who.HOME ?
				BreakthroughState.homeSym : BreakthroughState.awaySym;
		char opp = state.who == GameState.Who.HOME ?
				BreakthroughState.awaySym : BreakthroughState.homeSym;
		int dir = state.who == GameState.Who.HOME ? +1 : -1;
		for (int r=0; r<BreakthroughState.N; r++) {
			for (int c=0; c<BreakthroughState.N; c++) {
				mv.startRow = r;
				mv.startCol = c;
				mv.endingRow = r+dir; mv.endingCol = c;
				if (board.moveOK(mv)) {
					if (board.board[mv.endingRow][mv.endingCol] == opp) {
						takes.add((BreakthroughMove)mv.clone());
					} else if (safeMove(board, mv, dir, me, opp)){
						safeMoves.add((BreakthroughMove)mv.clone());
					} else {
						vulnerableMoves.add((BreakthroughMove)mv.clone());						
					}
				}
				mv.endingRow = r+dir; mv.endingCol = c+1;
				if (board.moveOK(mv)) {
					if (board.board[mv.endingRow][mv.endingCol] == opp) {
						takes.add((BreakthroughMove)mv.clone());
					} else if (safeMove(board, mv, dir, me, opp)){
						safeMoves.add((BreakthroughMove)mv.clone());
					} else {
						vulnerableMoves.add((BreakthroughMove)mv.clone());						
					}
				}
				mv.endingRow = r+dir; mv.endingCol = c-1;
				if (board.moveOK(mv)) {
					if (board.board[mv.endingRow][mv.endingCol] == opp) {
						takes.add((BreakthroughMove)mv.clone());
					} else if (safeMove(board, mv, dir, me, opp)){
						safeMoves.add((BreakthroughMove)mv.clone());
					} else {
						vulnerableMoves.add((BreakthroughMove)mv.clone());						
					}
				}
			}
		}
		ArrayList<BreakthroughMove> list;  
		if (takes.size() > 0) {
			list = takes;
		} else if (safeMoves.size() > 0){
			list = safeMoves;
		} else {
			list = vulnerableMoves;
		}
		int which = Util.randInt(0, list.size()-1);
		return list.get(which);
	}
	public static void main(String [] args)
	{
		GamePlayer p = new TeamA01BreakthroughPlayer("Greedy BT+");
		p.compete(args);
	}
}
