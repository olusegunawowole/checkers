package application;

import java.util.ArrayList;
/**
 * 
 * @author Olusegun
 * This class represents a computer player. It contains methods that generate moves for computer 
 * using minimax algorithm.
 *
 */
public class ComputerPlayer extends Player {

	public ComputerPlayer() {

	}

	public Move makeMove(CheckersBoard board) {
		LegalMoveState resultMove = minimax(new LegalMoveState(board, null), 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		return resultMove.move;
	}

	private LegalMoveState minimax(LegalMoveState currState, int depth, int alpha, int beta, boolean maxPlayer) {
		if (depth == 0 || currState.board.isGameOver()) {
			return currState;
		}
		if (maxPlayer) {
			int maxEvaluation = Integer.MIN_VALUE;
			LegalMoveState bestState = null;
			for (LegalMoveState state : getLegalMoveStates(currState.board, CheckersBoard.COMPUTER_PLAYER)) {
				int evaluation = minimax(state, depth - 1, alpha, beta, false).board.evaluate(false);
				maxEvaluation = Math.max(evaluation, maxEvaluation);
				if (maxEvaluation == evaluation) {
					bestState = state;
				}
				alpha = Math.max(alpha, evaluation);
				if(beta <=alpha)
					break;
			}
			return bestState;
		} else {
			int minEvaluation = Integer.MAX_VALUE;
			LegalMoveState bestState = null;
		for (LegalMoveState state : getLegalMoveStates(currState.board, CheckersBoard.HUMAN_PLAYER)) {
				int evaluation = minimax(state, depth - 1, alpha, beta, true).board.evaluate(true);
				minEvaluation = Math.min(evaluation, minEvaluation);
				if (minEvaluation == evaluation) {
					bestState = state;
				}
				beta = Math.min(beta, evaluation);
				if(beta <=alpha)
					break;
			}
			return bestState;
		}
	}

	private ArrayList<LegalMoveState> getLegalMoveStates(CheckersBoard board, int playerNumber) {
		ArrayList<LegalMoveState> legalMoveStates = new ArrayList<>();
		for (Move move : board.getLegalMoves(playerNumber)) {
			CheckersBoard tempBoard = board.copy();
			CheckersBoard newBoard = tempBoard.makeMove(tempBoard, move);
			LegalMoveState state = new LegalMoveState(newBoard, move);
			legalMoveStates.add(state);
		}
		return legalMoveStates;
	}

	//It holds state of checkersboard and move selected.
	private static class LegalMoveState {
		CheckersBoard board;
		Move move;
		
		public LegalMoveState(CheckersBoard board, Move move) {
			this.board = board;
			this.move = move;
	}
	}
}
