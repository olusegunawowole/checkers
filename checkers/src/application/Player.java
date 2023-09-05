package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Player {
	public static final int LOSS = -1, TIED = 0, WIN = 1;
	private int number;
	private String name;
	private String imageUrl;
	private IntegerProperty scoreProperty;

	// Variables for statistics
	private int gamePlayed, moveCount;
	private int wins, currentWinningStreak, longestWinningStreak;
	private int losses, currentLosingStreak, longestLosingStreak;
	private int tied;
	private int allTimeScore;
	private int allTimekings, mostKings, currentKingCount;
	private int currentJumpStreak, longestJump;

	public Player(int number) {
		this.number = number;
		this.name = "Player" + number;
		scoreProperty = new SimpleIntegerProperty();
	}
	
	public Player() {
		this(1);
	}

	public void reset() {
		scoreProperty.set(0);
		currentJumpStreak = 0;
		currentKingCount = 0;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return scoreProperty.get();
	}

	public void setScore(int score) {
		this.scoreProperty.set(score);
	}

	public void setScore() {
		int score = scoreProperty.get() + 1;
		scoreProperty.set(score);
		allTimeScore++;
	}

	public IntegerProperty scoreProperty() {
		return scoreProperty;

	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * Set the statistics at the end of a game.
	 * 
	 * @param val if val < 0 it means a loss,if val = 0, it means a tie and if > 0
	 *            it means a win
	 */
	public void setStatistics(int val) {
		if (val > TIED) {
			wins++;
			currentWinningStreak++;
			currentLosingStreak = 0;
			if (currentWinningStreak > longestWinningStreak) {
				longestWinningStreak = currentWinningStreak;
			}
		} else if (val < TIED) {
			losses++;
			currentLosingStreak++;
			currentWinningStreak = 0;
			if (currentLosingStreak > longestLosingStreak) {
				longestLosingStreak = currentLosingStreak;
			}
		} else {
			tied++;
			currentLosingStreak = 0;
			currentWinningStreak = 0;
		}
	}

	public int getGamePlayed() {
		return gamePlayed;
	}

	public void setGamePlayed(int gamePlayed) {
		this.gamePlayed = gamePlayed;
	}

	public void increaseGameCount() {
		gamePlayed++;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getCurrentWinningStreak() {
		return currentWinningStreak;
	}

	public void setCurrentWinningStreak(int currentWinningStreak) {
		this.currentWinningStreak = currentWinningStreak;
	}

	public int getLongestWinningStreak() {
		return longestWinningStreak;
	}

	public void setLongestWinningStreak(int longestWinningStreak) {
		this.longestWinningStreak = longestWinningStreak;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public int getCurrentLosingStreak() {
		return currentLosingStreak;
	}

	public void setCurrentLosingStreak(int currentLosingStreak) {
		this.currentLosingStreak = currentLosingStreak;
	}

	public int getLongestLosingStreak() {
		return longestLosingStreak;
	}

	public void setLongestLosingStreak(int longestLosingStreak) {
		this.longestLosingStreak = longestLosingStreak;
	}

	public int getTied() {
		return tied;
	}

	public void setTied(int tied) {
		this.tied = tied;
	}

	public int getAllTimeScore() {
		return allTimeScore;
	}

	public void setAllTimeScore(int allTimeScore) {
		this.allTimeScore = allTimeScore;
	}

	public double getAverageScore() {
		if (gamePlayed == 0)
			return 0.0;
		return (double) allTimeScore / gamePlayed;
	}

	public int getAllTimeKings() {
		return allTimekings;
	}

	public void setAllTimeKings(int allTimekings) {
		this.allTimekings = allTimekings;
	}

	public void setAllTimeKings() {
		allTimekings++;
		currentKingCount++;
		if (currentKingCount > mostKings) {
			mostKings = currentKingCount;
		}
	}

	public int getCurrentKingCount() {
		return currentKingCount;
	}

	public void setCurrentKingCount(int currentKingCount) {
		this.currentKingCount = currentKingCount;
	}

	public int getMostKings() {
		return mostKings;
	}

	public void setMostKings(int mostKings) {
		this.mostKings = mostKings;
	}

	public double getAvgKings() {
		if (gamePlayed == 0)
			return 0.0;
		return (double) allTimekings / gamePlayed;
	}

	public int getCurrentJumpStreak() {
		return currentJumpStreak;
	}

	public void setCurrentJumpStreak(int jumps) {
		this.currentJumpStreak = jumps;
	}

	public void increaseJumpStreak() {
		currentJumpStreak++;
		if (currentJumpStreak > longestJump) {
			longestJump = currentJumpStreak;
		}
	}

	public void resetJumpStreak() {
		currentJumpStreak = 0;
	}

	public int getLongestJump() {
		return longestJump;
	}

	public void setLongestJump(int longestJump) {
		this.longestJump = longestJump;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

	public void countMove() {
		moveCount++;
	}

	public double getAvgMove() {
		if (gamePlayed == 0)
			return 0.0;
		return (double) moveCount / gamePlayed;
	}
}
