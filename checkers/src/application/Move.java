package application;

/**
 * 
 * @author Olusegun This class represents a move on checkers board. However, it
 *         does not verify if the move is legal or not.
 *
 */
public class Move {
	private int fromRow;
	private int fromCol;
	private int toRow;
	private int toCol;
	private int skippedRow = -1;
	private int skippedCol = -1;

	public Move(int fromRow, int fromCol, int skippedRow, int skippedCol, int toRow, int toCol) {
		this.fromRow = fromRow;
		this.fromCol = fromCol;
		this.skippedRow = skippedRow;
		this.skippedCol = skippedCol;
		this.toRow = toRow;
		this.toCol = toCol;
	}

	public Move(int fromRow, int fromCol, int toRow, int toCol) {
		this(fromRow, fromCol, -1, -1, toRow, toCol);
	}

	/**
	 * 
	 * @return a copy of the move
	 */
	public Move copy() {
		return new Move(fromRow, fromCol, skippedRow, skippedCol, toRow, toCol);
	}

	/**
	 * Test whether this move is a jump.
	 * 
	 * @return return true if it is a jump or false if otherwise
	 */
	public boolean isJump() {
		return skippedRow > -1 && skippedCol > -1;
	}

	/**
	 * @return the fromRow
	 */
	public int getFromRow() {
		return fromRow;
	}

	/**
	 * @param fromRow the fromRow to set
	 */
	public void setFromRow(int fromRow) {
		this.fromRow = fromRow;
	}

	/**
	 * @return the fromCol
	 */
	public int getFromCol() {
		return fromCol;
	}

	/**
	 * @param fromCol the fromCol to set
	 */
	public void setFromCol(int fromCol) {
		this.fromCol = fromCol;
	}

	/**
	 * @return the toRow
	 */
	public int getToRow() {
		return toRow;
	}

	/**
	 * @param toRow the toRow to set
	 */
	public void setToRow(int toRow) {
		this.toRow = toRow;
	}

	/**
	 * @return the toCol
	 */
	public int getToCol() {
		return toCol;
	}

	/**
	 * @param toCol the toCol to set
	 */
	public void setToCol(int toCol) {
		this.toCol = toCol;
	}

	/**
	 * @return the skippedRow
	 */
	public int getSkippedRow() {
		return skippedRow;
	}

	/**
	 * @param skippedRow the skippedRow to set
	 */
	public void setSkippedRow(int skippedRow) {
		this.skippedRow = skippedRow;
	}

	/**
	 * @return the skippedCol
	 */
	public int getSkippedCol() {
		return skippedCol;
	}

	/**
	 * @param skippedCol the skippedCol to set
	 */
	public void setSkippedCol(int skippedCol) {
		this.skippedCol = skippedCol;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("From: (");
		sb.append(fromRow + "," + fromCol + ")\n");
		if (isJump()) {
			sb.append("Skipped: (" + skippedRow + "," + skippedCol + ")\n");
		}
		sb.append("To: (" + toRow + "," + toCol + ")\n");
		return sb.toString();
	}
}
