package application;

import java.io.File;

import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import reusable.Alert;
import reusable.CustomField;
import reusable.ModalDialog;
import reusable.StatisticsBar;
import reusable.ToggleSwitch;

public class Menu extends VBox {
	private CheckersBoard checkersBoard;
	private VBox playerPane;
	private GridPane settingsPane;
	private ModalDialog dialog;
	private String defaultImgUrl = Main.PROJECT_PATH + "checkers\\\\src\\\\resource\\\\defaultimage.jpg";

	public Menu(CheckersBoard checkersBoard, Timeline timeline, VBox playerPane, ModalDialog dialog) {
		this.checkersBoard = checkersBoard;
		this.playerPane = playerPane;
		this.dialog = dialog;
		configureSettingsPane();

		boolean gameOver = checkersBoard.gameOverProperty().get();
		MenuItem exitMenu = new MenuItem(">>>", true, false);
		exitMenu.setOnMouseClicked(e -> timeline.play());

		MenuItem newGame = new MenuItem("New Game", true, false);
		newGame.setDisable(!gameOver);
		newGame.setOnMouseClicked(e -> createNewGame());

		MenuItem resignGame = new MenuItem("Resign", true, false);
		resignGame.setDisable(gameOver);
		resignGame.setOnMouseClicked(e -> resignGame());

		MenuItem profile = new MenuItem("Profiles", true, false);
		profile.setOnMouseClicked(e -> dialog.open(configureProfileUpdater()));

		MenuItem stats = new MenuItem("Statistics", true, false);
		stats.setOnMouseClicked(e -> dialog.open(getStatistics()));

		MenuItem settings = new MenuItem("Settings", true, false);
		settings.setOnMouseClicked(e -> dialog.open(settingsPane));

		MenuItem exitGame = new MenuItem("Exit", true, true);
		exitGame.setOnMouseClicked(e -> checkersBoard.exit(dialog, Main.stage));
		getChildren().addAll(exitMenu, newGame, resignGame, profile, stats, settings, exitGame);

		setPadding(new Insets(5, 5, 0, 5));
		setStyle("-fx-border-color: yellow; -fx-border-width: 2px");
		setPrefWidth(300);
		setMinWidth(300);
		setAlignment(Pos.BASELINE_CENTER);

		checkersBoard.gameOverProperty().addListener((obj, oldVal, newVal) -> {
			newGame.setDisable(!newVal); // Disable menuItem if game in progress
			resignGame.setDisable(newVal); // Enable resign menuItem if game in progress
		});

	}

	private void createNewGame() {
		String msg = "Are you sure you want to start a new Game?";
		Alert alert = new Alert(msg, "Yes", "No");
		alert.setButtonOnAction(e -> {
			checkersBoard.createNewGame();
			dialog.close();
		}, 0);
		alert.setButtonOnAction(e -> {
			dialog.close();
		}, 1);
		dialog.open(alert);
	} // End of createNewGame

	private void resignGame() {
		String msg;
		Alert alert;
		if (checkersBoard.isAutoplay()) {
			msg = "Quitting an ongoing game counts as a loss. Proceed anyway?";
			alert = new Alert(msg, "Resign", "Cancel");
		} else {
			msg = "Are you sure you want to quit the current game?";
			alert = new Alert(msg, "Yes", "No");
		}
		alert.setButtonOnAction(e -> {
			checkersBoard.resignGame();
			dialog.close();
		}, 0);
		alert.setButtonOnAction(e -> {
			dialog.close();
		}, 1);
		dialog.open(alert);
	} // End of resignGame

	private Button createButton(String text) {
		String style = "-fx-background-color: transparent; -fx-color: rgb(0, 0, 0); -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: darkgray; -fx-cursor: hand";
		String hoverStyle = "-fx-background-color: white; -fx-color: rgb(255, 255, 255); -fx-border-width: 1px; -fx-border-color: white;";
		Button btn = new Button(text);
		btn.setPrefWidth(150);
		btn.setStyle(style);
		btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
		btn.setOnMouseExited(e -> btn.setStyle(style));
		btn.setOnMousePressed(e -> btn.setOpacity(0.6));
		btn.setOnMouseReleased(e -> btn.setOpacity(1.0));
		return btn;
	}// End of createButton

	private File openFileChooser() {
		FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png",
				"*.jpeg");
		FileChooser fileDialog = new FileChooser();
		fileDialog.getExtensionFilters().add(imageFilter);
		fileDialog.setTitle("Select Photo");
		fileDialog.setInitialFileName(null); // No file is initially selected.
		if (checkersBoard.getSelectedFile() == null)
			fileDialog.setInitialDirectory(new File(System.getProperty("user.home")));
		else
			fileDialog.setInitialDirectory(checkersBoard.getSelectedFile().getParentFile());
		File file = fileDialog.showOpenDialog(Main.stage);
		if (file == null)
			return null; // User canceled.
		checkersBoard.setSelectedFile(file);
		return file;
	} // End of openFileChooser

	// Statistics configuration begins here.
	private HBox getStatistics() {
		HBox root = new HBox();
		Player p1 = checkersBoard.getPlayer1();
		Player p2 = checkersBoard.getPlayer2();
		Label header = new Label("Autoplay Game Statistics");
		header.setStyle("-fx-text-fill: white");
		header.setFont(Font.font("Arial", FontWeight.NORMAL, 25));
		StackPane headerPane = new StackPane(header);
		VBox.setMargin(headerPane, new Insets(0, 0, 25, 0));

		VBox pane = new VBox(5);
		pane.getChildren().add(headerPane);
		pane.getChildren().add(new StatisticsBar("Won Games", p1.getWins(), p2.getWins()));
		pane.getChildren().add(new StatisticsBar("Tied Games", p1.getTied(), p2.getTied()));
		pane.getChildren().add(new StatisticsBar("Average Move", p1.getAvgMove(), p2.getAvgMove()));
		pane.getChildren().add(new StatisticsBar("Average Score", p1.getAverageScore(), p2.getAverageScore()));
		pane.getChildren().add(new StatisticsBar("Current Winning Streak", p1.getCurrentWinningStreak(),
				p2.getCurrentWinningStreak()));
		pane.getChildren().add(new StatisticsBar("Longest Winning Streak", p1.getLongestWinningStreak(),
				p2.getLongestWinningStreak()));
		pane.getChildren().add(
				new StatisticsBar("Longest Losing Streak", p1.getLongestLosingStreak(), p2.getLongestLosingStreak()));
		pane.getChildren().add(new StatisticsBar("Most Kings", p1.getMostKings(), p2.getMostKings()));
		pane.getChildren().add(new StatisticsBar("Average King", p1.getAvgKings(), p2.getAvgKings()));
		pane.getChildren().add(new StatisticsBar("Longest Jump", p1.getLongestJump(), p2.getLongestJump()));

		Button btn = createButton("Close");
		btn.setOnAction(e -> dialog.close());
		StackPane btnPane = new StackPane(btn);
		VBox.setMargin(btnPane, new Insets(25, 0, 0, 0));
		pane.getChildren().add(btnPane);

		root.setStyle("-fx-background-color: black; -fx-border-width: 2px; -fx-border-color: white");
		root.setMaxHeight(700);
		root.setMaxWidth(700);
		root.setSpacing(5);
		root.setPadding(new Insets(20));
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(createStatsPlayerThumbnail(p1), pane, createStatsPlayerThumbnail(p2));
		return root;
	} // End of getStatistics

	private VBox createStatsPlayerThumbnail(Player player) {
		Image image;
		if (player.getImageUrl() == null)
			image = new Image("file:///" + defaultImgUrl);
		else
			image = new Image("file:///" + player.getImageUrl());
		Circle clip = new Circle(75);
		clip.setEffect(new DropShadow(20, Color.WHITE));
		clip.setFill(new ImagePattern(image));

		Label nameLabel = new Label(player.getName());
		nameLabel.setStyle("-fx-text-fill: white");
		nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 25));

		VBox imagePane = new VBox(5, clip, nameLabel);
		imagePane.setAlignment(Pos.BASELINE_CENTER);
		imagePane.setMaxHeight(200);
		return imagePane;
	} // End of createStatsPlayerThumbnail

	// Statistics configuration ends here

	private void configureSettingsPane() {
		GridPane root = new GridPane();
		Label header = new Label("Settings");
		header.setStyle("-fx-text-fill: white");
		header.setFont(Font.font("Arial", FontWeight.NORMAL, 25));
		StackPane headerPane = new StackPane(header);
		GridPane.setMargin(headerPane, new Insets(0, 0, 25, 0));
		root.add(headerPane, 0, 0, 2, 1);

		Label autoplayLabel = new Label("Autoplay");
		autoplayLabel.setDisable(!checkersBoard.gameOverProperty().get());
		autoplayLabel.setStyle("-fx-text-fill: white");
		autoplayLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		root.add(autoplayLabel, 0, 1);

		ToggleSwitch autoplaySwitch = new ToggleSwitch();
		autoplaySwitch.switchTo(checkersBoard.isAutoplay());
		autoplaySwitch.setDisable(!checkersBoard.gameOverProperty().get());
		root.add(autoplaySwitch, 1, 1);
		checkersBoard.gameOverProperty().addListener((obj, oldVal, newVal) -> {
			// Not allow to change game type while a game is in progress
			autoplayLabel.setDisable(!checkersBoard.gameOverProperty().get());
			autoplaySwitch.setDisable(!checkersBoard.gameOverProperty().get());
		});

		autoplaySwitch.stateProperty().addListener((obj, oldVal, newVal) -> {
			checkersBoard.setAutoplay(newVal);
		});

		Label displayMoveLabel = new Label("Display Moves");
		displayMoveLabel.setStyle("-fx-text-fill: white");
		displayMoveLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		root.add(displayMoveLabel, 0, 2);

		ToggleSwitch displayMoveSwitch = new ToggleSwitch();
		displayMoveSwitch.switchTo(checkersBoard.isMoveDisplayed());
		displayMoveSwitch.stateProperty().addListener((obj, oldVal, newVal) -> {
			checkersBoard.setMoveDisplayed(newVal);
		});
		root.add(displayMoveSwitch, 1, 2);

		Label saveOnCloseLabel = new Label("Save on Close");
		saveOnCloseLabel.setStyle("-fx-text-fill: white");
		saveOnCloseLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		root.add(saveOnCloseLabel, 0, 3);

		ToggleSwitch saveOnCloseSwitch = new ToggleSwitch();
		saveOnCloseSwitch.switchTo(checkersBoard.isSavedOnClose());
		saveOnCloseSwitch.stateProperty().addListener((obj, oldVal, newVal) -> {
			checkersBoard.setSavedOnClose(newVal);
		});
		root.add(saveOnCloseSwitch, 1, 3);

		Button btn = createButton("Close");
		btn.setOnAction(e -> {
			dialog.close();
		});

		root.add(btn, 0, 4, 2, 1);
		GridPane.setHalignment(btn, HPos.CENTER);
		GridPane.setMargin(btn, new Insets(25, 0, 0, 0));

		root.setStyle("-fx-background-color: black; -fx-border-width: 2px; -fx-border-color: white");
		root.setMaxHeight(300);
		root.setMaxWidth(350);
		root.setVgap(20);
		root.setHgap(100);
		root.setPadding(new Insets(20));
		root.setAlignment(Pos.CENTER);
		settingsPane = root;
	}

	// Profile update configuration begins here
	private VBox configureProfileUpdater() {
		Label p1Label = checkersBoard.isAutoplay() ? new Label("Computer Player") : new Label("Player 1");
		p1Label.setStyle("-fx-text-fill: white");
		p1Label.setPrefWidth(225);
		p1Label.setAlignment(Pos.CENTER);
		p1Label.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

		Label p2Label = checkersBoard.isAutoplay() ? new Label("Human Player") : new Label("Player 2");
		p2Label.setStyle("-fx-text-fill: white");
		p2Label.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		p2Label.setPrefWidth(225);
		p2Label.setAlignment(Pos.CENTER);

		HBox labelPane = new HBox(5, p1Label, p2Label);

		ProfileUpdater updater1 = new ProfileUpdater(checkersBoard.getPlayer1());
		ProfileUpdater updater2 = new ProfileUpdater(checkersBoard.getPlayer2());

		HBox updaterPane = new HBox(5, updater1, updater2);
		Button saveBtn = createButton("Save");

		saveBtn.setOnAction(e -> {
			if (!updater1.nameField.isError() && !updater2.nameField.isError()) {
				updater1.save();
				updater2.save();
				dialog.close();
				StackPane p1Pane = Main.createThumbnail(checkersBoard.getPlayer1());
				StackPane p2Pane = Main.createThumbnail(checkersBoard.getPlayer2());
				playerPane.getChildren().clear();
				playerPane.getChildren().addAll(p1Pane, p2Pane);
				checkersBoard.getLabel().setText("Players' profiles updated.");
			}
		});
		Button cancelBtn = createButton("Cancel");
		cancelBtn.setOnAction(e -> {
			dialog.close();
		});
		HBox btnPane = new HBox(5, saveBtn, cancelBtn);
		btnPane.setMaxWidth(310);
		VBox root = new VBox(25, labelPane, updaterPane, btnPane);
		VBox.setMargin(btnPane, new Insets(30, 0, 0, 0));
		root.setStyle("-fx-background-color: black; -fx-border-width: 2px; -fx-border-color: white");
		root.setMaxHeight(500);
		root.setMaxWidth(500);
		root.setSpacing(5);
		root.setPadding(new Insets(20));
		root.setAlignment(Pos.CENTER);
		return root;
	}
	// Profile update configuration ends here

//***********************ProfileUpdater Nested Class begins here*******************
	private class ProfileUpdater extends VBox {
		private Player player;
		private String imgUrl;
		private CustomField nameField;
		private Label removeLabel, changeLabel;
		private VBox imgPreviewPane;

		public ProfileUpdater(Player player) {
			this.player = player;
			imgPreviewPane = new VBox();
			imgPreviewPane.setAlignment(Pos.BASELINE_CENTER);
			imgPreviewPane.setMaxHeight(120);
			boolean noImage = player.getImageUrl() == null || !(new File(player.getImageUrl()).exists());
			if (noImage) {
				createPreviewImage(defaultImgUrl);
			} else {
				createPreviewImage(player.getImageUrl());
			}
			imgUrl = player.getImageUrl();
			changeLabel = (noImage) ? new Label("Add photo") : new Label("Change photo");
			changeLabel.setStyle("-fx-text-fill: white; -fx-cursor: hand;");
			changeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
			changeLabel.setOnMouseEntered(e -> {
				changeLabel.setStyle("-fx-underline: true; -fx-text-fill: white; -fx-cursor: hand");
			});
			changeLabel.setOnMouseExited(e -> {
				changeLabel.setStyle("-fx-underline: false; -fx-text-fill: white; -fx-cursor: hand");
			});

			changeLabel.setOnMouseClicked(e -> {
				File file = openFileChooser();
				if (file != null) {
					imgUrl = file.getAbsolutePath();
					createPreviewImage(imgUrl);
					if (removeLabel.isDisable())
						changeLabel.setText("Change photo");
					removeLabel.setDisable(false);
				}
			});

			removeLabel = new Label("Remove photo");
			removeLabel.setDisable(noImage);
			removeLabel.setStyle("-fx-text-fill: white; -fx-cursor: hand;");
			removeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

			removeLabel.setOnMouseEntered(e -> {
				removeLabel.setStyle("-fx-underline: true; -fx-text-fill: white; -fx-cursor: hand");
			});
			removeLabel.setOnMouseExited(e -> {
				removeLabel.setStyle("-fx-underline: false; -fx-text-fill: white; -fx-cursor: hand");
			});

			removeLabel.setOnMouseClicked(e -> {
				imgUrl = null;
				createPreviewImage(defaultImgUrl);
				changeLabel.setText("Add photo");
				removeLabel.setDisable(true);
			});

			Label dividerLabel = new Label("|");
			dividerLabel.setStyle("-fx-text-fill: white");
			dividerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

			HBox labelPane = new HBox(10, changeLabel, dividerLabel, removeLabel);
			labelPane.setPadding(new Insets(20, 0, 0, 0));

			nameField = new CustomField("");
			nameField.setValue(player.getName());
			nameField.getTextField().positionCaret(player.getName().length());
			nameField.getTextField().textProperty().addListener(e -> {
				validateName();
			});

			VBox.setMargin(nameField, new Insets(25, 0, 0, 0));

			getChildren().addAll(imgPreviewPane, labelPane, nameField);
			setStyle("-fx-border-width: 2px; -fx-border-color: white");
			setPadding(new Insets(10));
		}

		private void createPreviewImage(String imgUrl) {
			Image image = new Image("file:///" + imgUrl);
			Circle clip = new Circle(100);
			clip.setEffect(new DropShadow(20, Color.WHITE));
			clip.setFill(new ImagePattern(image));
			imgPreviewPane.getChildren().clear();
			imgPreviewPane.getChildren().add(clip);
		}

		private void save() {
			player.setImageUrl(imgUrl);
			player.setName(nameField.getValue());
		}

		private boolean validateName() {
			String name = nameField.getValue();
			if (name == null || name.trim().isEmpty()) {
				nameField.setError(true, "Name cannot be empty.");
				return false;
			}
			if (!Character.isLetter(name.charAt(0))) {
				nameField.setError(true, "Name must begin with a letter.");
				return false;
			}
			if (name.trim().length() == 1) {
				nameField.setError(true, "Name must has at least 2 characters.");
				return false;
			}
			nameField.setError(false);
			return true;
		}

	}// ***********************ProfileUpdater Nested Class begins
		// here*******************

	// ***********************MenuItem Nested Class begins here*******************
	private class MenuItem extends StackPane {
		private Label titleLabel;

		public MenuItem(String title, boolean topBorder, boolean bottomBorder) {
			titleLabel = new Label(title);
			titleLabel.setStyle("-fx-text-fill: yellow;");
			titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

			GridPane pane = new GridPane();
			pane.setPadding(new Insets(5, 0, 0, 5));
			pane.setStyle("-fx-background-color: transparent");
			pane.add(titleLabel, 0, 0);
			setPrefWidth(400);
			setPadding(new Insets(2, 0, 2, 0));
			pane.setAlignment(Pos.CENTER);
			if (topBorder && bottomBorder)
				setStyle("-fx-border-width: 1px 0px 1px 0px; -fx-border-color: yellow");
			else if (topBorder)
				setStyle("-fx-border-width: 1px 0px 0px 0px; -fx-border-color: yellow");
			else if (bottomBorder)
				setStyle("-fx-border-width: 0px 0px 1px 0px; -fx-border-color: yellow");
			else
				setStyle("-fx-border-width: 0px 0px 0px 0px; -fx-border-color: yellow");
			getChildren().add(pane);
			setOnMouseEntered(e -> {
				titleLabel.setStyle("-fx-text-fill: black;");

				pane.setStyle("-fx-background-color: yellow");
			});
			setOnMouseExited(e -> {
				titleLabel.setStyle("-fx-text-fill: yellow;");
				pane.setStyle("-fx-background-color: transparent");
			});
			setOnMousePressed(e -> {
				pane.setStyle("-fx-background-color: rgba(250, 250, 0, 0.5)");
			});
			setOnMouseReleased(e -> {
				pane.setStyle("-fx-background-color: rgba(250, 250, 0, 1.0)");

			});
			setCursor(Cursor.HAND);
		}
	}// ***********************MenuItem Nested Class ends here*******************
}
