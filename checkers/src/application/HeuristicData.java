package application;

// Reference: https://github.com/kevingregor/Checkers
public class HeuristicData {
	private int pawn; // Number of normal pieces
	private int king; // Number of kings
	private int backRowPiece; // Number of pieces at the back row
	private int middleBoxPiece; // Number of pieces in middle 4 columns of middle 2 rows
	private int middleRowPiece; // Number of pieces in middle 2 rows but not the middle 4 columns
	private int vulnerable; // Number of pieces that can be taken by opponent on the next turn
	private int protectedPiece; // Number of pieces that can not be taken until pieces behind it (or itself) are
								// moved
	public static final double PAWN_WEIGHT = 5/2;
	public static final double KING_WEIGHT = 7.75;
	public static final double BACK_ROW_PIECE_WEIGHT = 4;
	public static final double MIDDLE_BOX_PIECE_WEIGHT = 2.5;
	public static final double MIDDLE_ROW_PIECE_WEIGHT = 0.5;
	public static final double VULNERABLE_WEIGHT = -3;
	public static final double PROTECTED_PIECE_WEIGHT = 3;

	public HeuristicData() {
	}

	public int getPawn() {
		return pawn;
	}

	public void setPawn(int pawn) {
		this.pawn = pawn;
	}

	public int getKing() {
		return king;
	}

	public void setKing(int king) {
		this.king = king;
	}

	public int getBackRowPiece() {
		return backRowPiece;
	}

	public void setBackRowPiece(int backRowPiece) {
		this.backRowPiece = backRowPiece;
	}

	public int getMiddleBoxPiece() {
		return middleBoxPiece;
	}

	public void setMiddleBoxPiece(int middleBoxPiece) {
		this.middleBoxPiece = middleBoxPiece;
	}

	public int getMiddleRowPiece() {
		return middleRowPiece;
	}

	public void setMiddleRowPiece(int middleRowPiece) {
		this.middleRowPiece = middleRowPiece;
	}

	public int getVulnerable() {
		return vulnerable;
	}

	public void setVulnerable(int vulnerable) {
		this.vulnerable = vulnerable;
	}

	public int getProtectedPiece() {
		return protectedPiece;
	}

	public void setProtectedPiece(int protectedPiece) {
		this.protectedPiece = protectedPiece;
	}

	public HeuristicData subtract(HeuristicData data) {
		pawn -= data.pawn;
		king -= data.king;
		backRowPiece -= data.backRowPiece;
		vulnerable -= data.vulnerable;
		protectedPiece -= data.protectedPiece;
		middleBoxPiece -= data.middleBoxPiece;
		middleRowPiece -= data.middleRowPiece;
		return this;
	}

	public double getSum() {
		double sum = pawn * PAWN_WEIGHT;
		sum += king * KING_WEIGHT;
		sum += backRowPiece * BACK_ROW_PIECE_WEIGHT;
		sum += middleBoxPiece * MIDDLE_BOX_PIECE_WEIGHT;
		sum += middleRowPiece * MIDDLE_ROW_PIECE_WEIGHT;
		sum += vulnerable * VULNERABLE_WEIGHT;
		sum += protectedPiece * PROTECTED_PIECE_WEIGHT;
		return sum;
	}
}
