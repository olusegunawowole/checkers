package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import reusable.Alert;
import reusable.ModalDialog;

public class CheckersBoard extends Canvas {
	public static final double width = 832;
	public static final double height = 832;
	public static final double MARGIN = 18;
	public static final int SQUARE_SIZE = 100;
	public static final int COMPUTER_PLAYER = 1;
	public static final int HUMAN_PLAYER = 2;
	public static final int COMPUTER_PLAYER_INDEX = 0;
	public static final int HUMAN_PLAYER_INDEX = 1;
	public static final int GRID_SIZE = 8;
	private int currentPlayer = 1;
	private int previousWinner = 1;
	/*
	 * It is used to keep track of non-jump moves. It is reset each time a jump move
	 * is made. Game ends in a tie if moveCount = 200
	 */
	private int moveCount;
	private IntegerProperty xProperty, yProperty;
	private GraphicsContext g;
	private Image bgImage, squareImage;
	private Image[] pieceImages;
	private Piece[][] board;
	private ArrayList<Piece> legalMovePieces;
	private Piece selectedPiece;
	private boolean animationInProgress;
	private boolean isJump;
	private boolean moveDisplayed = true;
	private boolean autoplay = true; // true if it is computer vs human
	private boolean savedOnClose = true; // automatic save on exit if true
	private boolean stateSaved; // true if previous session was saved on exit
	private BooleanProperty gameOverProperty;
	private Timeline timeline;
	private Label label;
	private Player[] players;
	private File dataFile;
	private File selectedFile; // store file selected in during profile update

	public CheckersBoard() {
		board = new Piece[GRID_SIZE][GRID_SIZE];
		players = new Player[2];
	}

	public CheckersBoard(String projectPath) {
		setWidth(width);
		setHeight(height);
		g = getGraphicsContext2D();
		dataFile = new File(projectPath + "checkers\\src\\resource\\data.xml");
		board = new Piece[GRID_SIZE][GRID_SIZE];
		players = new Player[2];

		legalMovePieces = new ArrayList<>();
		xProperty = new SimpleIntegerProperty();
		yProperty = new SimpleIntegerProperty();
		gameOverProperty = new SimpleBooleanProperty();
		timeline = new Timeline();
		label = new Label(); // Used to display state of the game
		label.setAlignment(Pos.CENTER);
		label.setFont(Font.font("Arial", FontPosture.REGULAR, 30));
		label.setStyle("-fx-text-fill: yellow");
		label.setPrefWidth(width);

		// Setting up checkersboard background
		String filePath = "file:///" + projectPath + "checkers\\src\\resource\\bg.jpg";
		bgImage = new Image(filePath);
		filePath = "file:///" + projectPath + "checkers\\src\\resource\\square.png";
		squareImage = new Image(filePath);
		pieceImages = new Image[8];
		for (int index = 0; index < pieceImages.length; index++) {
			String str = "file:///" + projectPath + "checkers\\src\\resource\\piece" + index + ".png";
			pieceImages[index] = new Image(str);
		}

		filePath = "file:///" + projectPath + "checkers\\src\\resource\\piece3.png";
		readGame(); // Read data file if exists/no error or do a new set up

		setOnMouseClicked(e -> mousePressed(e));
		draw();
	}

	public BooleanProperty gameOverProperty() {
		return gameOverProperty;
	}

	public boolean isMoveDisplayed() {
		return moveDisplayed;
	}

	public File getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}

	public void setMoveDisplayed(boolean moveDisplayed) {
		this.moveDisplayed = moveDisplayed;
		draw();
	}

	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}

	public boolean isSavedOnClose() {
		return savedOnClose;
	}

	public void setSavedOnClose(boolean savedOnClose) {
		this.savedOnClose = savedOnClose;
	}

	public Player getPlayer1() {
		return players[0];
	}

	public Player getPlayer2() {
		return players[1];
	}

	public Label getLabel() {
		return label;
	}

	public int getCurrentPlayerNumber() {
		return currentPlayer;
	}

	// *********************AI related methods begins***********************
	public CheckersBoard copy() {
		CheckersBoard checkersBoard = new CheckersBoard();
		checkersBoard.board = copyBoard();
		checkersBoard.players = new Player[2];
		checkersBoard.players[0] = new Player();
		checkersBoard.players[1] = new Player(2);
		checkersBoard.currentPlayer = currentPlayer;
		return checkersBoard;

	}

	private Piece[][] copyBoard() {
		Piece[][] newBoard = new Piece[GRID_SIZE][GRID_SIZE];
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				Piece piece = board[row][col];
				if (piece != null) {
					newBoard[row][col] = piece.copy();
				}
			}
		}
		return newBoard;
	}

	public boolean isGameOver() {
		if (countPiece(COMPUTER_PLAYER) == 0)
			return true;
		if (countPiece(HUMAN_PLAYER) == 0)
			return true;
		if (getLegalMoves(COMPUTER_PLAYER).isEmpty())
			return true;
		if (getLegalMoves(HUMAN_PLAYER).isEmpty())
			return true;
		return false;
	}

	public int evaluate2(boolean max) {
		int humanKing = 0;
		int humanPiece = 0;
		int computerKing = 0;
		int computerPiece = 0;

		for (Piece piece : getAllPieces()) {
			if (piece.playerNumber == COMPUTER_PLAYER) {
				if (piece.isKing)
					computerKing++;
				else {
					computerPiece++;
				}
			} else {
				if (piece.isKing)
					humanKing++;
				else {
					humanPiece++;
				}
			}
		}
		int results = (computerPiece - humanPiece) + (computerKing * 2 - humanKing * 2);
		if (!max)
			results = -results;
		return results;
	}

	// Reference: https://github.com/kevingregor/Checkers
	public int evaluate(boolean max) {
		HeuristicData computerData = new HeuristicData();
		HeuristicData humanData = new HeuristicData();

		for (Piece piece : getAllPieces()) {
			if (piece.playerNumber == COMPUTER_PLAYER) {
				if (piece.isKing) { // Check for kings
					computerData.setKing(computerData.getKing() + 1);
				} else { // Check for pawns
					computerData.setPawn(computerData.getPawn() + 1);
				}

				int row = piece.row;
				int col = piece.col;

				if (row == 0) { // Check for back rows
					computerData.setBackRowPiece(computerData.getBackRowPiece() + 1);
					computerData.setProtectedPiece(computerData.getProtectedPiece() + 1);
				} else {
					if (row == 3 || row == 4) { // Check for middle rows
						if (col >= 2 && col <= 5) {
							computerData.setMiddleBoxPiece(computerData.getMiddleBoxPiece() + 1);
						} else {// Non-box
							computerData.setMiddleRowPiece(computerData.getMiddleRowPiece() + 1);
						}
					}
					// Check if the piece can be taken
					if (piece.isLeftVulnerable())
						computerData.setVulnerable(computerData.getVulnerable() + 1);
					if (piece.isRightVulnerable())
						computerData.setVulnerable(computerData.getVulnerable() + 1);

					// Check for protected checkers
					if (piece.isProtected())
						computerData.setProtectedPiece(computerData.getProtectedPiece() + 1);

				}
			} else if (piece.playerNumber == HUMAN_PLAYER) {
				if (piece.isKing) { // Check for kings
					humanData.setKing(humanData.getKing() + 1);
				} else { // Check for pawns
					humanData.setPawn(humanData.getPawn() + 1);
				}

				int row = piece.row;
				int col = piece.col;

				if (row == 7) { // Check for back rows
					humanData.setBackRowPiece(humanData.getBackRowPiece() + 1);
					humanData.setProtectedPiece(humanData.getProtectedPiece() + 1);
				} else {
					if (row == 3 || row == 4) { // Check for middle rows
						if (col >= 2 && col <= 5) {
							humanData.setMiddleBoxPiece(humanData.getMiddleBoxPiece() + 1);
						} else {// Non-box
							humanData.setMiddleRowPiece(humanData.getMiddleRowPiece() + 1);
						}
					}
					// Check if the piece can be taken
					if (piece.isLeftVulnerable())
						humanData.setVulnerable(humanData.getVulnerable() + 1);
					if (piece.isRightVulnerable())
						humanData.setVulnerable(humanData.getVulnerable() + 1);

					// Check for protected checkers
					if (piece.isProtected())
						humanData.setProtectedPiece(humanData.getProtectedPiece() + 1);

				}
			}
		}
		int sum = (int) computerData.subtract(humanData).getSum();
		if (max)
			return (int) sum;
		else {
			return -(int) sum;
		}
	}

	private void makeAutoplayMove() {
		//System.out.println("Checkers: Autoplay Move");
		ComputerPlayer computer = new ComputerPlayer();
		Move move = computer.makeMove(this);
		selectedPiece = board[move.getFromRow()][move.getFromCol()];
		makeMove(move);
	}

	public CheckersBoard makeMove(CheckersBoard checkersBoard, Move move) {
		Piece piece = checkersBoard.board[move.getFromRow()][move.getFromCol()];
		if (piece != null) {
			checkersBoard.board[move.getToRow()][move.getToCol()] = piece;
			if (move.getToRow() == 7 && piece.playerNumber == 1)
				piece.isKing = true;
			else if (move.getToRow() == 0 && piece.playerNumber == 2)
				piece.isKing = true;
			checkersBoard.removePieceAt(move.getFromRow(), move.getFromCol());
			if (move.isJump()) {
				checkersBoard.removePieceAt(move.getSkippedRow(), move.getSkippedCol());
			} else {
				checkersBoard.currentPlayer = checkersBoard.currentPlayer == HUMAN_PLAYER ? COMPUTER_PLAYER
						: HUMAN_PLAYER;
			}
		}
		return checkersBoard;
	}

	private boolean isMoveVulnerable(Move move) {
		Piece piece = board[move.getFromRow()][move.getFromCol()];
		int row = move.getToRow();
		int col = move.getToCol();
		if (row == 0 || row == 7 || col == 0 || col == 7 || piece == null)
			return false;
		if (piece.playerNumber == COMPUTER_PLAYER) {
			Piece enemy = board[row + 1][col - 1]; // Check bottom left
			if (enemy != null && enemy.playerNumber != COMPUTER_PLAYER)
				return true;
			enemy = board[row + 1][col + 1]; // Check bottom right
			if (enemy != null && enemy.playerNumber != COMPUTER_PLAYER)
				return true;

			if (piece.isKing) {
				enemy = board[row - 1][col - 1]; // Check top left
				if (enemy != null && enemy.playerNumber != COMPUTER_PLAYER && enemy.isKing)
					return true;
				enemy = board[row - 1][col + 1]; // Check top right
				if (enemy != null && enemy.playerNumber != COMPUTER_PLAYER && enemy.isKing)
					return true;
			}
		} else {
			Piece enemy = board[row - 1][col - 1]; // Check top left
			if (enemy != null && enemy.playerNumber != HUMAN_PLAYER)
				return true;
			enemy = board[row - 1][col + 1]; // Check top right
			if (enemy != null && enemy.playerNumber != HUMAN_PLAYER)
				return true;

			if (piece.isKing) {
				enemy = board[row + 1][col - 1]; // Check bottom left
				if (enemy != null && enemy.playerNumber != HUMAN_PLAYER && enemy.isKing)
					return true;
				enemy = board[row + 1][col + 1]; // Check bottom right
				if (enemy != null && enemy.playerNumber != HUMAN_PLAYER && enemy.isKing)
					return true;
			}
		}
		return false;
	}

	private ArrayList<Move> getDefensiveMoves(int playerNumber) {
		ArrayList<Move> moves = new ArrayList<>();
		ArrayList<Location> locations = new ArrayList<>();
		ArrayList<Piece> vulnerablePieces = new ArrayList<>();
		// Search for vulnerable pieces and save empty locations that must be occupied
		// to protect the pieces
		for (Piece piece : getAllPieces()) {
			boolean isVulnerable = false;
			if (piece.isLeftVulnerable() && piece.playerNumber == playerNumber) {
				isVulnerable = true;
				if (piece.playerNumber == COMPUTER_PLAYER)
					locations.add(new Location(piece.row - 1, piece.col + 1));
				else
					locations.add(new Location(piece.row + 1, piece.col + 1));
			}
			if (piece.isRightVulnerable() && piece.playerNumber == playerNumber) {
				isVulnerable = true;
				if (piece.playerNumber == COMPUTER_PLAYER)
					locations.add(new Location(piece.row - 1, piece.col - 1));
				else
					locations.add(new Location(piece.row + 1, piece.col - 1));
			}
			if (isVulnerable)
				vulnerablePieces.add(piece);
		}

		if (!locations.isEmpty()) {// There are vulnerable pieces
			ArrayList<Move> tempMoves = new ArrayList<>();
			for (Piece piece : getAllPieces()) {
				// Search for legal moves for a given player
				if (piece.canMove() && piece.playerNumber == playerNumber) {
					tempMoves.addAll(piece.moves);
				}
			}
			while (!tempMoves.isEmpty()) {
				Move m = tempMoves.remove(tempMoves.size() - 1);
				// Check if this move can fill any of the locations identified above.
				for (Location loc : locations) {
					if (loc.row == m.getToRow() && loc.col == m.getToCol()) {
						moves.add(m);
						break;
					}
				}
			}
			if (!moves.isEmpty())
				return moves;
		}
		// Getting here means no vulnerable pieces or no moves to protect them.
		// Check if vulnerable pieces can move out of danger by themselves
		if (!vulnerablePieces.isEmpty()) {
			if (playerNumber == COMPUTER_PLAYER)
				for (Piece piece : vulnerablePieces) {
					if (piece.canMove() && piece.playerNumber == playerNumber) {
						for (Move m : piece.moves) { // Select moves that do not make piece vulnerable
							if (!isMoveVulnerable(m))
								moves.add(m);
						}
					}
				}

			if (!moves.isEmpty())
				return moves;
		}

		if (!vulnerablePieces.isEmpty()) {
			if (playerNumber == COMPUTER_PLAYER)
				for (Piece piece : vulnerablePieces) {
					if (piece.canMove() && piece.playerNumber == playerNumber) {
						moves.addAll(piece.moves);
					}
				}
		}
		return moves;
	}

	public ArrayList<Move> getLegalMoves(int playerNumber) {
		ArrayList<Move> moves = new ArrayList<>();
		// Legal Jump moves
		for (Piece piece : getAllPieces()) {
			// Search for legal jump moves for a given player
			if (piece.canJump() && piece.playerNumber == playerNumber) {
				moves.addAll(piece.moves);
			}
		}
		
		if (moves.isEmpty()) {
			// Legal Defensive Moves
			moves = getDefensiveMoves(playerNumber);
		}
		
		if (moves.isEmpty()) {
			// Legal moves that turn piece to king
			for (Piece piece : getAllPieces()) {
				if (piece.canMove() && piece.playerNumber == playerNumber) {
					for (Move move : piece.moves) {
						if (playerNumber == COMPUTER_PLAYER) {
							if (move.getToRow() == 7 && !piece.isKing) {
								moves.add(move);
							}
						} else {
							if (move.getToRow() == 0 && !piece.isKing) {
								moves.add(move);
							}
						}
					}
				}
			}
		}

		
		
		if (moves.isEmpty()) {
			for (Piece piece : getAllPieces()) {
				// Search for legal moves for a given player
				if (piece.canMove() && piece.playerNumber == playerNumber) {
					for (Move m : piece.moves) {
						if (!isMoveVulnerable(m)) { // Prevent irrational moves when possible. That is, unnecessary
													// exposure of pieces to attack
							moves.add(m);
						}
					}
				}
			}
		}

		if (moves.isEmpty()) {// Normal moves 
			for (Piece piece : getAllPieces()) {
				// Search for legal moves for a given player
				if (piece.canMove() && piece.playerNumber == playerNumber) {
					moves.addAll(piece.moves);
				}
			}
		}

		return moves; // Empty if no legal move
	}

	// **************************AI related methods ends****************************

	// This method finds all legal moves. It finds jump moves first. If no result,
	// it will find normal legal moves and set variable isJump to either true or
	// false depending on the search results
	private void getLegalMovePiece(int playerNumber) {
		legalMovePieces.clear();
		isJump = false;
		for (Piece piece : getAllPieces()) {
			// Search for legal jump moves for a given player
			if (piece.canJump() && piece.playerNumber == playerNumber) {
				legalMovePieces.add(piece);
			}
		}
		if (!legalMovePieces.isEmpty()) {
			if (legalMovePieces.size() == 1) {
				// If we find only one legal jump, select the piece.
				selectedPiece = legalMovePieces.get(0);
			}
			// If we find legal jump(s), return. No need to find other
			// moves as player must jump
			isJump = true;
			return;
		}
		// If we get here that means no legal jump move.
		for (Piece piece : getAllPieces()) {
			// Search for legal moves for a given player
			if (piece.canMove() && piece.playerNumber == playerNumber) {
				legalMovePieces.add(piece);
			}
		}
		if (legalMovePieces.size() == 1) {
			// If we find only one legal move, select the piece.
			selectedPiece = legalMovePieces.get(0);
		}
		// Note: legalMovePieces will be empty if there is no legal move for the player.
	}

	private void makeMove(Move move) {
		if (move == null || !inbounds(move.getToRow(), move.getToCol()))
			return;
		int fromX = move.getFromCol() * SQUARE_SIZE;
		int fromY = move.getFromRow() * SQUARE_SIZE;
		int toX = move.getToCol() * SQUARE_SIZE;
		int toY = move.getToRow() * SQUARE_SIZE;
		xProperty = new SimpleIntegerProperty(fromX);
		yProperty = new SimpleIntegerProperty(fromY);
		moveCount++;
		players[currentPlayer - 1].countMove();
		if (move.isJump()) {
			// If it is a jump move, increase the current player's score
			if (autoplay) {
				players[currentPlayer - 1].setScore();
			} else {
				int score = players[currentPlayer - 1].getScore() + 1;
				players[currentPlayer - 1].setScore(score);
			}
			moveCount = 0;
		}

		yProperty.addListener(e -> {
			if (move.isJump()) {
				int row = move.getSkippedRow();
				int col = move.getSkippedCol();
				double time = timeline.getCurrentTime().toMillis();
				if (getPieceAt(row, col) != null && time > 75) {
					removePieceAt(row, col);
				}
			}
			draw();
		});

		animationInProgress = true;
		removePieceAt(move.getFromRow(), move.getFromCol());

		KeyValue xKeyValue = new KeyValue(xProperty, toX);
		KeyValue yKeyValue = new KeyValue(yProperty, toY);
		KeyFrame keyFrame = new KeyFrame(Duration.millis(150), xKeyValue, yKeyValue);

		// Timeline
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
		timeline.setOnFinished(e -> {
			draw();
			selectedPiece.moveTo(move);
			animationInProgress = false;
			passTurn();
		});
	}

	private void passTurn() {
		if (isJump) {// Check if the previous move was a jump
			if (autoplay) {
				players[currentPlayer - 1].increaseJumpStreak();
			}
			if (countPiece(currentPlayer, true) == 0) {// Check if opponent does not have any piece left
				String msg = "Game Over: " + players[currentPlayer - 1].getName() + " WON!!!";
				if (currentPlayer == COMPUTER_PLAYER) {
					gameOver(msg, COMPUTER_PLAYER_INDEX, HUMAN_PLAYER_INDEX);
				} else {
					gameOver(msg, HUMAN_PLAYER_INDEX, COMPUTER_PLAYER_INDEX);
				}
				return;
			}
			// If we get here, that means opponent has some piece(s) left
			// Check if current player can still jump with the selected piece
			isJump = selectedPiece.canJump();
		}

		if (isJump) {// Another jump move is found. Current player must make the next move.
			if (autoplay && currentPlayer == COMPUTER_PLAYER) {
				makeAutoplayMove();
				return;
			}
			String msg = players[currentPlayer - 1].getName() + " must jump";
			label.setText(msg);
			draw();
			return;
		}
		// Check if moveCount is up to 200
		if (moveCount >= 200) { // It is a tile
			gameOver("Game Over: It's a TIE", COMPUTER_PLAYER_INDEX, HUMAN_PLAYER_INDEX, true);
			return;
		}
		// If we get this place, reset currentPlayer's jumpStreak,
		// set selectedPiece to null and change currentPlayer
		players[currentPlayer - 1].resetJumpStreak();
		selectedPiece = null;
		int previousPlayer = currentPlayer; // Take note of the previous player. This variable will be useful if we
											// don't find move(s) for the new current player.
		if (currentPlayer == COMPUTER_PLAYER) {
			currentPlayer = HUMAN_PLAYER;
		} else {
			currentPlayer = COMPUTER_PLAYER;
		}
		getLegalMovePiece(currentPlayer);
		if (legalMovePieces.isEmpty()) {// No legal move found for the new current player. Game over.
			String msg = "Game Over: " + players[previousPlayer - 1].getName() + " WON!!!";
			if (previousPlayer == COMPUTER_PLAYER) {
				gameOver(msg, COMPUTER_PLAYER_INDEX, HUMAN_PLAYER_INDEX);
			} else {
				gameOver(msg, HUMAN_PLAYER_INDEX, COMPUTER_PLAYER_INDEX);
			}
			return;
		}
		if (autoplay && currentPlayer == COMPUTER_PLAYER) {
			makeAutoplayMove();
			return;
		}
		String msg = players[currentPlayer - 1].getName() + ": Make your move.";
		label.setText(msg);
		draw();
	}

	/**
	 * count pieces of a specified player or of their opponent.
	 * 
	 * @param playerNumber the player to count their pieces or their opponent's
	 *                     pieces.
	 * @param opponent     if true count opponent's piece or specified player's
	 *                     piece if otherwise.
	 * @return the number remaining pieces.
	 */
	private int countPiece(int playerNumber, boolean opponent) {
		int count = 0;
		if (opponent) {
			for (Piece piece : getAllPieces()) {
				if (piece.playerNumber != playerNumber)
					count++;
			}
		} else {
			for (Piece piece : getAllPieces()) {
				if (piece.playerNumber == playerNumber)
					count++;
			}
		}
		return count;
	}

	private int countPiece(int playerNumber) {
		return countPiece(playerNumber, false);
	}

	public void createNewGame() {
		setUpGame(); // Set up the pieces.
		players[COMPUTER_PLAYER_INDEX].increaseGameCount();
		players[HUMAN_PLAYER_INDEX].increaseGameCount();
		gameOverProperty.set(false);
		if (autoplay && currentPlayer == COMPUTER_PLAYER) {
			label.setText(players[currentPlayer - 1].getName() + " is thinking...");
			draw();
			new Thread() {
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
					Platform.runLater(() -> {
						makeAutoplayMove();
					});
				}
			}.start();
		} else {
			getLegalMovePiece(currentPlayer);// Get current player's legal moves.
			label.setText(players[currentPlayer - 1].getName() + ":  Make your move.");
		}
		draw();
	}

	/**
	 * Human or current player resigns.
	 */
	public void resignGame() {
		selectedPiece = null;
		legalMovePieces.clear();
		draw();
		if (autoplay) {// Computer cannot resign so it counts against human player.
			String str = players[HUMAN_PLAYER_INDEX].getName() + " resigns. " + players[COMPUTER_PLAYER_INDEX].getName()
					+ " wins";
			gameOver(str, COMPUTER_PLAYER_INDEX, HUMAN_PLAYER_INDEX);
			return;
		}
		if (currentPlayer == COMPUTER_PLAYER) {// indicating human player1 resigned
			String str = players[COMPUTER_PLAYER_INDEX].getName() + " resigns. " + players[HUMAN_PLAYER_INDEX].getName()
					+ " wins";
			gameOver(str, HUMAN_PLAYER_INDEX, COMPUTER_PLAYER_INDEX);
		} else {
			String str = players[HUMAN_PLAYER_INDEX].getName() + " resigns. " + players[COMPUTER_PLAYER_INDEX].getName()
					+ " wins";
			gameOver(str, COMPUTER_PLAYER_INDEX, HUMAN_PLAYER_INDEX);
		}
	}

	private void gameOver(String str, int winnderIndex, int loserIndex) {
		gameOver(str, winnderIndex, loserIndex, false);
	}

	private void gameOver(String str, int winnerIndex, int loserIndex, boolean tied) {
		gameOverProperty.set(true);
		label.setText(str);
		if (autoplay && tied) {
			players[winnerIndex].setStatistics(Player.TIED);
			players[loserIndex].setStatistics(Player.TIED);
			currentPlayer = previousWinner;
			return;
		}
		previousWinner = winnerIndex + 1;
		currentPlayer = winnerIndex + 1; // Winner moves first in the next game.
		if (autoplay) {
			players[winnerIndex].setStatistics(Player.WIN);
			players[loserIndex].setStatistics(Player.LOSS);
		}
	}

	public void exit(ModalDialog dialog, Stage stage) {
		String text;
		Alert alert;
		if (savedOnClose) {
			text = "Are you sure you want to exit game?";
			alert = new Alert(text, "Yes", "No");
			alert.setButtonOnAction(e -> { // Setting Yes button on action
				stateSaved = true;
				saveGame();
				stage.close();
			}, 0);

			alert.setButtonOnAction(e -> { // Setting No button on action
				dialog.close();
			}, 1);

		} else {
			if (autoplay && !gameOverProperty.get())
				text = "There is an ongoing autoplay game. Exiting without saving it counts as a loss.\nDo you want to save it?";
			else if (!gameOverProperty.get())
				text = "There is an ongoing game.\nDo you want to save it?";
			else
				text = "Want to save the current state?";
			alert = new Alert(text, "Save", "Don't Save", "Cancel");

			alert.setButtonOnAction(e -> {// Setting Save button on action
				stateSaved = true;
				saveGame();
				stage.close();
			}, 0);

			alert.setButtonOnAction(e -> {// Setting Don't Save button on action
				if (autoplay) {
					gameOver("", COMPUTER_PLAYER_INDEX, HUMAN_PLAYER_INDEX);
				}
				stateSaved = false;
				saveGame();
				stage.close();
			}, 1);

			alert.setButtonOnAction(e -> {// Setting Cancel button on action
				dialog.close();
			}, 2);
		}
		if (dialog.isOpen()) {
			dialog.set(alert);
		} else {
			dialog.open(alert);
		}
	}

	private boolean inbounds(int row, int col) {
		return row >= 0 && col >= 0 && row < GRID_SIZE && col < GRID_SIZE;
	}

	public boolean isEmpty(int row, int col) {
		if (!inbounds(row, col)) {
			return false;
		}
		return board[row][col] == null;
	}

	public boolean contains(int player, int row, int col) {
		return !isEmpty(row, col) && getPieceAt(row, col).playerNumber == player;
	}

	private int getXOrY(int val) {
		return SQUARE_SIZE * val;
	}

	public Piece getPieceAt(int row, int col) {
		if (!inbounds(row, col))
			return null;
		return board[row][col];
	}

	public void removePieceAt(int row, int col) {
		if (!inbounds(row, col))
			return;// null;
		// Piece piece = board[row][col];
		board[row][col] = null;
		// return piece;
	}

	private ArrayList<Piece> getAllPieces() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				if (row % 2 == col % 2 || board[row][col] == null)
					continue; // skip white and empty spaces
				Piece piece = board[row][col];
				pieces.add(piece);
			}
		}
		return pieces;
	}

	private int getPieceImageIndex(Piece piece) {
		if (piece.playerNumber == 1) {
			if (piece.isKing)
				return 1;
			else
				return 0;
		} else {
			if (piece.isKing)
				return 3;
			else
				return 2;
		}
	}

	private void setUpGame() {
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				if (row % 2 != col % 2) {
					if (row < 3)
						board[row][col] = new Piece(1, row, col);
					else if (row > 4)
						board[row][col] = new Piece(2, row, col);
					else
						board[row][col] = null;
				} else {
					board[row][col] = null;
				}
			}
		}
		players[COMPUTER_PLAYER_INDEX].reset();
		players[HUMAN_PLAYER_INDEX].reset();
		selectedPiece = null;
		moveCount = 0;
	}

	// Write state of checkersboard to file
	public void saveGame() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(dataFile));
			out.println("<?xml version=\"1.0\"?>");
			out.println("<checkers version=\"1.0\">");
			out.println("  <current-player value='" + currentPlayer + "' />");
			out.println("  <previous-winner value='" + previousWinner + "' />");
			out.println("  <move-count value='" + moveCount + "' />");
			out.println("  <move-displayed value='" + moveDisplayed + "' />");
			out.println("  <autoplay value='" + autoplay + "' />");
			out.println("  <saved-on-close value='" + savedOnClose + "' />");
			out.println("  <state_saved value='" + stateSaved + "' />");
			out.println("  <game-over value='" + gameOverProperty.get() + "' />");
			if (selectedFile != null)
				out.println("  <selected-file value='" + selectedFile.getAbsolutePath() + "' />");
			if (selectedPiece != null)
				out.println("  <selected-piece row='" + selectedPiece.row + "' col='" + selectedPiece.col
						+ "' player-number='" + selectedPiece.playerNumber + "' is-king='" + selectedPiece.isKing
						+ "' />");
			out.println("  <board>");
			for (Piece piece : getAllPieces()) {
				out.println("    <piece row='" + piece.row + "' col='" + piece.col + "' player-number='"
						+ piece.playerNumber + "' is-king='" + piece.isKing + "' />");
			}
			out.println("  </board>");

			out.println("  <player-list>");
			for (Player player : players) {
				out.print("    <player number='" + player.getNumber() + "' ");
				if (player.getImageUrl() != null)
					out.print("image-url='" + player.getImageUrl() + "' ");
				out.print("name='" + player.getName() + "' ");
				out.print("score='" + player.scoreProperty().get() + "' ");

				out.print("game-played='" + player.getGamePlayed() + "' ");
				out.print("move-count='" + player.getMoveCount() + "' ");
				out.print("wins='" + player.getWins() + "' ");
				out.print("current-winning-streak='" + player.getCurrentWinningStreak() + "' ");
				out.print("longest-winning-streak='" + player.getLongestWinningStreak() + "' ");
				out.print("losses='" + player.getLosses() + "' ");
				out.print("current-losing-streak='" + player.getCurrentLosingStreak() + "' ");
				out.print("longest-losing-streak='" + player.getLongestLosingStreak() + "' ");
				out.print("tied='" + player.getTied() + "' ");
				out.print("all-time-score='" + player.getAllTimeScore() + "' ");
				out.print("all-time-kings='" + player.getAllTimeKings() + "' ");
				out.print("most-kings='" + player.getMostKings() + "' ");
				out.print("current-king-count='" + player.getCurrentKingCount() + "' ");
				out.print("longest-jump='" + player.getLongestJump() + "' ");
				out.println(" />");
			}
			out.println("  </player-list>");
			out.println("</checkers>");
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Read data file if exists and no error, or do a new set up
	private void readGame() {
		try {
			if (dataFile == null || !dataFile.exists()) {
				throw new FileNotFoundException("Data file not found.");
			}
			Document xmldoc;
			DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmldoc = docReader.parse(dataFile);
			Element rootElement = xmldoc.getDocumentElement();
			if (!rootElement.getNodeName().equals("checkers"))
				throw new Exception("Data file is invalid.");
			String version = rootElement.getAttribute("version");
			double versionNumber = Double.parseDouble(version);
			if (versionNumber > 1.0)
				throw new Exception("Data file requires a newer version of checkers.");

			NodeList nodes = rootElement.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i) instanceof Element) {
					Element element = (Element) nodes.item(i);
					if (element.getTagName().equals("current-player")) {
						currentPlayer = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("previous-winner")) {
						previousWinner = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("move-count")) {
						moveCount = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("move-displayed")) {
						moveDisplayed = Boolean.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("autoplay")) {
						autoplay = Boolean.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("saved-on-close")) {
						savedOnClose = Boolean.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("state_saved")) {
						stateSaved = Boolean.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("game-over")) {
						boolean gameOver = Boolean.valueOf(element.getAttribute("value"));
						gameOverProperty.set(gameOver);
					} else if (element.getTagName().equals("selected-file")) {
						String filePath = element.getAttribute("value");
						if (filePath != null && !filePath.trim().isEmpty()) {
							selectedFile = new File(filePath);
						}
					} else if (element.getTagName().equals("selected-piece")) {
						int row = Integer.valueOf(element.getAttribute("row"));
						int col = Integer.valueOf(element.getAttribute("col"));
						int playerNumber = Integer.valueOf(element.getAttribute("player-number"));
						boolean isKing = Boolean.valueOf(element.getAttribute("is-king"));
						Piece piece = new Piece(playerNumber, row, col);
						piece.isKing = isKing;
						selectedPiece = piece;
					} else if (element.getTagName().equals("board")) {
						if (stateSaved) {// Load previous board state
							NodeList pieceNodeList = element.getChildNodes();
							for (int j = 0; j < pieceNodeList.getLength(); j++) {
								if (pieceNodeList.item(j) instanceof Element) {
									Element pieceElement = (Element) pieceNodeList.item(j);
									int row = Integer.valueOf(pieceElement.getAttribute("row"));
									int col = Integer.valueOf(pieceElement.getAttribute("col"));
									int playerNumber = Integer.valueOf(pieceElement.getAttribute("player-number"));
									boolean isKing = Boolean.valueOf(pieceElement.getAttribute("is-king"));
									Piece piece = new Piece(playerNumber, row, col);
									piece.isKing = isKing;
									board[row][col] = piece;
								}
							}
						}
					} else if (element.getTagName().equals("player-list")) {
						ArrayList<Player> playerList = new ArrayList<>();
						NodeList playerNodeList = element.getChildNodes();
						for (int j = 0; j < playerNodeList.getLength(); j++) {
							if (playerNodeList.item(j) instanceof Element) {
								Element pieceElement = (Element) playerNodeList.item(j);
								int number = Integer.valueOf(pieceElement.getAttribute("number"));
								Player player = new Player(number);
								player.setName(pieceElement.getAttribute("name"));
								String imgUrl = pieceElement.getAttribute("image-url");
								if (imgUrl != null && !imgUrl.isEmpty())
									player.setImageUrl(imgUrl);
								player.setScore(Integer.valueOf(pieceElement.getAttribute("score")));

								player.setGamePlayed(Integer.valueOf(pieceElement.getAttribute("game-played")));
								player.setMoveCount(Integer.valueOf(pieceElement.getAttribute("move-count")));
								player.setWins(Integer.valueOf(pieceElement.getAttribute("wins")));
								player.setCurrentWinningStreak(
										Integer.valueOf(pieceElement.getAttribute("current-winning-streak")));
								player.setLongestWinningStreak(
										Integer.valueOf(pieceElement.getAttribute("longest-winning-streak")));
								player.setLosses(Integer.valueOf(pieceElement.getAttribute("losses")));
								player.setCurrentLosingStreak(
										Integer.valueOf(pieceElement.getAttribute("current-losing-streak")));
								player.setLongestLosingStreak(
										Integer.valueOf(pieceElement.getAttribute("longest-losing-streak")));
								player.setTied(Integer.valueOf(pieceElement.getAttribute("tied")));
								player.setAllTimeScore(Integer.valueOf(pieceElement.getAttribute("all-time-score")));
								player.setAllTimeKings(Integer.valueOf(pieceElement.getAttribute("all-time-kings")));
								player.setMostKings(Integer.valueOf(pieceElement.getAttribute("most-kings")));
								player.setCurrentKingCount(
										Integer.valueOf(pieceElement.getAttribute("current-king-count")));
								player.setLongestJump(Integer.valueOf(pieceElement.getAttribute("longest-jump")));
								playerList.add(player);
							}
						}
						for (int index = 0; index < playerList.size(); index++) {
							players[index] = playerList.get(index);
						}
					}

				}

			}
			if (stateSaved) {
				if (!gameOverProperty.get()) {// Check if there is a game in progress
					if (autoplay && currentPlayer == COMPUTER_PLAYER) {
						label.setText(players[currentPlayer - 1].getName() + " is thinking...");
						new Thread() {
							public void run() {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException ie) {
									Thread.currentThread().interrupt();
								}
								Platform.runLater(() -> {
									makeAutoplayMove();
								});
							}
						}.start();
					} else {
						getLegalMovePiece(currentPlayer); // Get legal moves for the current player
						if (selectedPiece != null) {
							for (Piece piece : legalMovePieces) {
								if (piece.row == selectedPiece.row && piece.col == selectedPiece.col) {
									selectedPiece = piece;
									label.setText("Click the square you want to move to.");
								}
							}
						}
					}
				} else {
					label.setText("No game in progress");
				}
			} else {
				createNewGame();
			}

		} catch (Exception e) {
			//System.err.println(e);
			e.printStackTrace();
			players[0] = new Player(COMPUTER_PLAYER);
			players[0].setName("Computer");
			players[1] = new Player(HUMAN_PLAYER);
			players[1] = new Player(2);
			players[1].setName("Human");
			// setUpGame();
			// gameOverProperty.set(true);
			// label.setText("No game in progress");
			createNewGame();
		}
	}

	private void draw() {
		g.drawImage(bgImage, 0, 0, width, height);
		double x, y;
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				y = row * SQUARE_SIZE;
				x = col * SQUARE_SIZE;
				if ((row % 2) == (col % 2)) {// draw white squares on the board
					g.drawImage(squareImage, x + MARGIN, y + MARGIN);
				}
				Piece piece = getPieceAt(row, col);
				if (piece != null) { // If the square is not empty, draw a piece
					int index = getPieceImageIndex(piece);
					Image img = pieceImages[index];
					g.drawImage(img, x + MARGIN + 10, y + MARGIN + 10);
				}
				drawPosition(row, col, x, y);
			}
		}

		if (moveDisplayed && !animationInProgress) {
			for (Piece piece : legalMovePieces) {
				// Highlight the legal move pieces
				g.setStroke(Color.AQUA);
				g.setLineWidth(5);
				x = getXOrY(piece.col);
				y = getXOrY(piece.row);
				g.strokeOval(x + MARGIN + 7.5, y + MARGIN + 7.5, SQUARE_SIZE - 15, SQUARE_SIZE - 15);
			}

			if (selectedPiece != null && legalMovePieces.contains(selectedPiece) && !animationInProgress) {
				// If selectedPiece is not null and has legal move(s), highlight it with color
				// yellow
				g.setStroke(Color.YELLOW);
				g.setLineWidth(5);
				x = getXOrY(selectedPiece.col);
				y = getXOrY(selectedPiece.row);
				g.strokeOval(x + MARGIN + 7.5, y + MARGIN + 7.5, SQUARE_SIZE - 15, SQUARE_SIZE - 15);

				// Draw faint piece on the possible locations a legal move piece can move to.
				for (Move move : selectedPiece.moves) {
					x = getXOrY(move.getToCol());
					y = getXOrY(move.getToRow());
					int imgIndex = getPieceImageIndex(selectedPiece) + 4;
					Image img = pieceImages[imgIndex];
					g.drawImage(img, x + MARGIN + 10, y + MARGIN + 10);
				}
			}
		}
		if (selectedPiece != null && animationInProgress) { // draw the moving piece
			int imgIndex = getPieceImageIndex(selectedPiece);
			Image img = pieceImages[imgIndex];
			g.drawImage(img, xProperty.get() + MARGIN + 10, yProperty.get() + MARGIN + 10);
		}
		// Draw border around board.
		g.save();
		g.setEffect(new DropShadow(13, 0, 2, Color.BLACK));
		g.setLineWidth(15);
		fill3DRect(Color.ANTIQUEWHITE, 7.5, 7.5, width - 15, height - 15);
		g.restore();
	}

	// To be removed
	private void drawPosition(int row, int col, double x, double y) {
		if ((row % 2) != (col % 2)) {
			g.save();
			// g.setFill(Color.AQUA);
			// g.fillRect(x+35, y+40, 75, 40);
			g.setFill(Color.WHITE);
			g.setFont(Font.font("Arial", FontWeight.BOLD, 20));
			String str = "(" + row + "," + col + ")";
			g.fillText(str, x + 45 - 5 + 5, y + 80 - 10 + 10);
			g.restore();
		}
	}

	private void mousePressed(MouseEvent evt) {
		if (animationInProgress)
			return;
		if (gameOverProperty.get()) {
			label.setText("No game in progress. You may start a new game.");
			return;
		}
		if (autoplay && currentPlayer == COMPUTER_PLAYER) {
			label.setText(players[currentPlayer - 1].getName() + " is thinking...");
			return;
		}

		int row, col;
		col = (int) (evt.getX() / SQUARE_SIZE);
		row = (int) (evt.getY() / SQUARE_SIZE);
		if (row % 2 == col % 2) {
			if (selectedPiece == null)
				label.setText("Click the piece you want to move.");
			else
				label.setText("Click the square you want to move to.");
			return;
		}
		Piece piece = board[row][col];

		if (piece == null && selectedPiece == null) {
			label.setText("Click the piece you want to move.");
			return;
		}

		if (piece != null && piece.playerNumber != currentPlayer) {
			String msg = "It is " + players[currentPlayer - 1].getName() + "'s turn.";
			label.setText(msg);
			return;
		}

		if (selectedPiece != null && selectedPiece.canMoveTo(row, col)) {
			makeMove(selectedPiece.getMoveAt(row, col));
			return;
		}
		if (selectedPiece != null && isEmpty(row, col) && !selectedPiece.canMoveTo(row, col)) {
			label.setText("Click the square you want to move to.");
			return;
		}

		if (!legalMovePieces.contains(piece)) {
			String msg = isJump ? "You must jump." : "You cannot move the piece.";
			label.setText(msg);
			return;
		} else {
			selectedPiece = piece;
			if (piece.canMoveTo(row, col)) {
				makeMove(piece.getMoveAt(row, col));
			} else {
				label.setText("Click the square you want to move to.");
				draw();
			}
		}
	}

	private void fill3DRect(Color color, double x, double y, double width, double height) {
		GraphicsContext g = getGraphicsContext2D();
		double h = color.getHue();
		double b = color.getBrightness();
		double s = color.getSaturation();
		if (b > 0.8) {
			b = 0.8;
			g.setFill(Color.hsb(h, s, b));
		} else if (b < 0.2) {
			b = 0.2;
			g.setFill(Color.hsb(h, s, b));
		}
		g.setStroke(Color.hsb(h, s, b + 0.2));
		g.strokeLine(x + 0.5, y + 0.5, x + width - 0.5, y + 0.5);
		g.strokeLine(x + 0.5, y + 0.5, x + 0.5, y + height - 0.5);
		g.setStroke(Color.hsb(h, s, b - 0.2));
		g.strokeLine(x + width - 0.5, y + 1.5, x + width - 0.5, y + height - 0.5);
		g.strokeLine(x + 1.5, y + height - 0.5, x + width - 0.5, y + height - 0.5);
		g.strokeRect(x, y, width, height);
	}

	// Represents a piece on the checkersboard.
	private class Piece {
		int playerNumber;
		int row;
		int col;
		boolean isKing;
		ArrayList<Move> moves;

		public Piece(int playerNumber, int row, int col) {
			this.playerNumber = playerNumber;
			this.row = row;
			this.col = col;
			moves = new ArrayList<>();
		}

		/**
		 * This method checks if this piece can jump
		 * 
		 * @return
		 */
		private boolean canJump() {
			moves.clear();
			if (playerNumber == HUMAN_PLAYER) {
				Location topFarLeft = new Location(row - 2, col - 2);
				Location topFarRight = new Location(row - 2, col + 2);
				if (isEmpty(topFarLeft.row, topFarLeft.col) && contains(COMPUTER_PLAYER, row - 1, col - 1)) {
					Move move = new Move(row, col, row - 1, col - 1, topFarLeft.row, topFarLeft.col);
					moves.add(move);
				}
				if (isEmpty(topFarRight.row, topFarRight.col) && contains(COMPUTER_PLAYER, row - 1, col + 1)) {
					Move move = new Move(row, col, row - 1, col + 1, topFarRight.row, topFarRight.col);
					moves.add(move);
				}
				if (isKing) {
					Location bottomFarLeft = new Location(row + 2, col - 2);
					Location bottomFarRight = new Location(row + 2, col + 2);
					if (isEmpty(bottomFarLeft.row, bottomFarLeft.col) && contains(COMPUTER_PLAYER, row + 1, col - 1)) {
						Move move = new Move(row, col, row + 1, col - 1, bottomFarLeft.row, bottomFarLeft.col);
						moves.add(move);
					}
					if (isEmpty(bottomFarRight.row, bottomFarRight.col)
							&& contains(COMPUTER_PLAYER, row + 1, col + 1)) {
						Move move = new Move(row, col, row + 1, col + 1, bottomFarRight.row, bottomFarRight.col);
						moves.add(move);
					}
				}
			} else if (playerNumber == COMPUTER_PLAYER) {
				Location bottomFarLeft = new Location(row + 2, col - 2);
				Location bottomFarRight = new Location(row + 2, col + 2);
				if (isEmpty(bottomFarLeft.row, bottomFarLeft.col) && contains(HUMAN_PLAYER, row + 1, col - 1)) {
					Move move = new Move(row, col, row + 1, col - 1, bottomFarLeft.row, bottomFarLeft.col);
					moves.add(move);
				}
				if (isEmpty(bottomFarRight.row, bottomFarRight.col) && contains(HUMAN_PLAYER, row + 1, col + 1)) {
					Move move = new Move(row, col, row + 1, col + 1, bottomFarRight.row, bottomFarRight.col);
					moves.add(move);
				}
				if (isKing) {
					Location topFarLeft = new Location(row - 2, col - 2);
					Location topFarRight = new Location(row - 2, col + 2);
					if (isEmpty(topFarLeft.row, topFarLeft.col) && contains(HUMAN_PLAYER, row - 1, col - 1)) {
						Move move = new Move(row, col, row - 1, col - 1, topFarLeft.row, topFarLeft.col);
						moves.add(move);
					}
					if (isEmpty(topFarRight.row, topFarRight.col) && contains(HUMAN_PLAYER, row - 1, col + 1)) {
						Move move = new Move(row, col, row - 1, col + 1, topFarRight.row, topFarRight.col);
						moves.add(move);
					}
				}
			}
			return !moves.isEmpty();
		}

		// This method checks if this piece can move
		private boolean canMove() {
			moves.clear(); // clear existing moves
			if (playerNumber == HUMAN_PLAYER) {
				Location topLeft = new Location(row - 1, col - 1);
				Location topRight = new Location(row - 1, col + 1);
				if (isEmpty(topLeft.row, topLeft.col)) {
					Move move = new Move(row, col, topLeft.row, topLeft.col);
					moves.add(move);
				}
				if (isEmpty(topRight.row, topRight.col)) {
					Move move = new Move(row, col, topRight.row, topRight.col);
					moves.add(move);
				}
				if (isKing) {
					Location bottomLeft = new Location(row + 1, col - 1);
					Location bottomRight = new Location(row + 1, col + 1);
					if (isEmpty(bottomLeft.row, bottomLeft.col)) {
						Move move = new Move(row, col, bottomLeft.row, bottomLeft.col);
						moves.add(move);
					}
					if (isEmpty(bottomRight.row, bottomRight.col)) {
						Move move = new Move(row, col, bottomRight.row, bottomRight.col);
						moves.add(move);
					}
				}
			} else if (playerNumber == COMPUTER_PLAYER) {
				Location bottomLeft = new Location(row + 1, col - 1);
				Location bottomRight = new Location(row + 1, col + 1);
				if (isEmpty(bottomLeft.row, bottomLeft.col)) {
					Move move = new Move(row, col, bottomLeft.row, bottomLeft.col);
					moves.add(move);
				}
				if (isEmpty(bottomRight.row, bottomRight.col)) {
					Move move = new Move(row, col, bottomRight.row, bottomRight.col);
					moves.add(move);
				}
				if (isKing) {
					Location topLeft = new Location(row - 1, col - 1);
					Location topRight = new Location(row - 1, col + 1);

					if (isEmpty(topLeft.row, topLeft.col)) {
						Move move = new Move(row, col, topLeft.row, topLeft.col);
						moves.add(move);
					}
					if (isEmpty(topRight.row, topRight.col)) {
						Move move = new Move(row, col, topRight.row, topRight.col);
						moves.add(move);
					}
				}
			}
			return !moves.isEmpty();
		}

		private void moveTo(Move move) {
			if (move == null)
				return;
			moveTo(move.getToRow(), move.getToCol());
		}

		private void moveTo(int row, int col) {
			if (!isEmpty(row, col))
				return;
			this.row = row;
			this.col = col;
			board[row][col] = this;
			if (!isKing && playerNumber == 1) {
				if (row == 7) {
					isKing = true;
					if (autoplay)
						players[COMPUTER_PLAYER_INDEX].setAllTimeKings();
				}
			} else if (!isKing && playerNumber == 2) {
				if (row == 0) {
					isKing = true;
					if (autoplay)
						players[HUMAN_PLAYER_INDEX].setAllTimeKings();
				}
			}
		}

		// This method checks if this piece can move to (row, col). It will return
		// true if piece can move to the given location or false if otherwise.
		private boolean canMoveTo(int row, int col) {
			for (Move move : moves) {
				if (move.getToRow() == row && move.getToCol() == col)
					return true;
			}
			return false;
		}

		private Move getMoveAt(int row, int col) {
			for (Move move : moves) {
				if (move.getToRow() == row && move.getToCol() == col)
					return move;
			}
			return null;
		}

		// Check if a piece cannot be taken until it moves or another piece moves.
		private boolean isProtected() {
			if (row == 0 || col == 0 || row == 7 || col == 7) // It is at edge of the board, it cannot be taken.
				return true;
			if (playerNumber == COMPUTER_PLAYER) {
				boolean leftProtected = containsBuddy(row - 1, col - 1) && !containsOpponentKing(row - 1, col - 1);
				boolean rightProtected = containsBuddy(row - 1, col + 1) && !containsOpponentKing(row - 1, col + 1);
				return leftProtected && rightProtected;
			}
			boolean leftProtected = containsBuddy(row + 1, col - 1) && !containsOpponentKing(row + 1, col - 1);
			boolean rightProtected = containsBuddy(row + 1, col + 1) && !containsOpponentKing(row + 1, col + 1);
			return leftProtected && rightProtected;
		}

		// Check if a piece can be taken this turn - left to right jump
		private boolean isLeftVulnerable() {
			if (playerNumber == COMPUTER_PLAYER) {
				return isEmpty(row - 1, col + 1) && containsOpponentPiece(row + 1, col - 1);
			}
			return isEmpty(row + 1, col + 1) && containsOpponentPiece(row - 1, col - 1);
		}

		// Check if a piece can be taken this turn - right to left jump
		private boolean isRightVulnerable() {
			if (playerNumber == COMPUTER_PLAYER) {
				return isEmpty(row - 1, col - 1) && containsOpponentPiece(row + 1, col + 1);
			}
			return isEmpty(row + 1, col - 1) && containsOpponentPiece(row - 1, col + 1);
		}

		// Check if a piece can be taken this turn
		// private boolean isVulnerable() {
		// if (playerNumber == COMPUTER_PLAYER) {
		// boolean rightAttack = isEmpty(row - 1, col - 1) && containsOpponentPiece(row
		// + 1, col + 1);
		// boolean leftAttack = isEmpty(row - 1, col + 1) && containsOpponentPiece(row +
		// 1, col - 1);
		// return rightAttack || leftAttack;
		// }
		// boolean rightAttack = isEmpty(row + 1, col - 1) && containsOpponentPiece(row
		// - 1, col + 1);
		// boolean leftAttack = isEmpty(row + 1, col + 1) && containsOpponentPiece(row -
		// 1, col - 1);
		// return rightAttack || leftAttack;
		// }

		// Check if piece at a given location is buddy or not.
		private boolean containsBuddy(int row, int col) {
			if (!inbounds(row, col) || isEmpty(row, col))
				return false;
			return board[row][col].playerNumber == playerNumber;
		}

		// Check if piece at a given location is opponent's piece
		private boolean containsOpponentPiece(int row, int col) {
			if (!inbounds(row, col) || isEmpty(row, col))
				return false;
			return board[row][col].playerNumber != playerNumber;
		}

		// Check if piece at a given location is opponent's king
		private boolean containsOpponentKing(int row, int col) {
			if (!inbounds(row, col) || isEmpty(row, col))
				return false;
			return board[row][col].playerNumber != playerNumber && board[row][col].isKing;
		}

		private Piece copy() {
			Piece piece = new Piece(playerNumber, row, col);
			piece.isKing = isKing;
			for (Move move : moves) {
				piece.moves.add(move.copy());
			}
			return piece;
		}
	}

	// Nested class to store possible location of a piece
	private class Location {
		int row;
		int col;

		public Location(int r, int c) {
			row = r;
			col = c;
		}
	}
}
