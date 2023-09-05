package application;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import reusable.ModalDialog;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {
	public static final String PROJECT_PATH = "C:/Users/peace/eclipse-workspace/";
	public static Stage stage;

	@Override
	public void start(Stage primaryStage) {
		try {
			CheckersBoard checkersBoard = new CheckersBoard(PROJECT_PATH);
			VBox leftPane = new VBox(2, checkersBoard.getLabel(), checkersBoard);
			leftPane.setPadding(new Insets(5, 5, 0, 5));
			leftPane.setStyle("-fx-border-color: yellow; -fx-border-width: 2px");

			Timeline slideLeft = new Timeline();
			Timeline slideRight = new Timeline();
			ModalDialog dialog = new ModalDialog();
			VBox playerPane = new VBox(150, createThumbnail(checkersBoard.getPlayer1()),
					createThumbnail(checkersBoard.getPlayer2()));

			StackPane tempPane = new StackPane(new Label("Work in progress"));
			tempPane.setMinSize(300, 600);
			tempPane.setStyle("-fx-border-color: red; -fx-border-width: 2px");
			tempPane.setOnMouseClicked(e -> {
				slideLeft.play();
			});

			VBox homePane = new VBox(10, configureMenuButton(slideRight), playerPane);
			homePane.setPadding(new Insets(5, 5, 0, 5));
			homePane.setStyle("-fx-border-color: yellow; -fx-border-width: 2px");
			homePane.setPrefWidth(300);
			homePane.setMinWidth(300);

			Menu menu = new Menu(checkersBoard, slideLeft, playerPane, dialog);

			StackPane rightPane = new StackPane(menu, homePane); // Place both homePane and MenuPane on each other

			setAnimation(homePane, menu, slideLeft, slideRight, 300, 890);
			HBox mainPane = new HBox(5, leftPane, rightPane); // Combine both leftPane and rightPane
			mainPane.setPadding(new Insets(5));
			StackPane pane = new StackPane(mainPane, dialog);
			pane.setMaxSize(1161.0, 889.0);
			StackPane root = new StackPane(pane);
			root.setAlignment(Pos.CENTER);
			root.setStyle("-fx-background-image: url(file:///" + PROJECT_PATH + "checkers/src/resource/bg.png);"
					+ " -fx-background-repeat:repeat; " + "-fx-background-position: center center");

			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.setMinHeight(935);
			primaryStage.setMinWidth(1175);
			primaryStage.show();
			stage = primaryStage;
			primaryStage.setOnCloseRequest(e -> {
				e.consume();
				checkersBoard.exit(dialog, stage);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static StackPane createThumbnail(Player player) {
		String filePath;
		if (player.getImageUrl() == null || !(new File(player.getImageUrl()).exists())) {
			// If there is no image file, use default image.
			filePath = "file:///" + PROJECT_PATH + "checkers\\\\src\\\\resource\\\\defaultimage.jpg";
		} else {
			filePath = "file:///" + player.getImageUrl();
		}
		Image image = new Image(filePath);
		Circle clip = new Circle(100);
		clip.setEffect(new DropShadow(10, Color.BLACK));
		clip.setFill(new ImagePattern(image));

		Label nameLabel = new Label(player.getName());
		nameLabel.setStyle("-fx-text-fill: yellow");
		nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 30));

		Label scoreLabel = new Label();
		scoreLabel.textProperty().bind(player.scoreProperty().asString()); // score label will change every score
																			// changes
		scoreLabel.setStyle("-fx-text-fill: yellow");
		scoreLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));

		if (player.getNumber() == 1)
			filePath = "file:///" + PROJECT_PATH + "checkers\\src\\resource\\piece0.png";
		else
			filePath = "file:///" + PROJECT_PATH + "checkers\\src\\resource\\piece2.png";

		ImageView iv = new ImageView(new Image(filePath));
		StackPane scorePane = new StackPane(iv, scoreLabel);

		VBox imagePane = new VBox(5, clip, nameLabel, scorePane);
		imagePane.setAlignment(Pos.TOP_CENTER);
		return new StackPane(imagePane);
	}

	private StackPane configureMenuButton(Timeline timeline) {
		StackPane root = new StackPane();
		root.setStyle("-fx-background-color: transparent; -fx-border-width: 2px; -fx-border-color: yellow");

		Label label = new Label("Menu");
		label.setStyle("-fx-text-fill: yellow");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 25));
		root.getChildren().add(label);
		root.setOnMouseEntered(e -> {
			label.setStyle("-fx-text-fill: black");
			root.setStyle("-fx-background-color: yellow; -fx-border-width: 2px; -fx-border-color: yellow");
		});

		root.setOnMouseExited(e -> {
			label.setStyle("-fx-text-fill: yellow");
			root.setStyle("-fx-background-color: transparent; -fx-border-width: 2px; -fx-border-color: yellow");
		});

		root.setOnMousePressed(e -> {
			root.setStyle(
					"-fx-opacity: 0.8; -fx-background-color: yellow; -fx-border-width: 2px; -fx-border-color: yellow");
		});

		root.setOnMouseReleased(e -> {
			root.setStyle(
					"-fx-opacity: 1.0; -fx-background-color: yellow; -fx-border-width: 2px; -fx-border-color: yellow");
		});

		root.setOnMouseClicked(e -> {
			timeline.play();
		});
		return root;
	}

	private void setAnimation(Pane page1, Pane page2, Timeline slideLeft, Timeline slideRight, double w, double h) {
		Rectangle clipRect = new Rectangle();
		clipRect.setWidth(w);
		clipRect.setHeight(h);
		clipRect.translateXProperty().set(0);
		page1.setClip(clipRect);
		page1.translateXProperty().set(0);

		Rectangle clipRect2 = new Rectangle();
		clipRect2.setWidth(0);
		clipRect2.setHeight(h);
		clipRect2.translateXProperty().set(0);
		page2.setClip(clipRect2);
		page2.translateXProperty().set(w);

		// Move left
		KeyValue kvMoveLeft1 = new KeyValue(clipRect.translateXProperty(), 0);
		KeyValue kvMoveLeft2 = new KeyValue(page1.translateXProperty(), 0);
		KeyValue kvMoveLeft3 = new KeyValue(clipRect2.widthProperty(), 0);
		KeyValue kvMoveLeft4 = new KeyValue(page2.translateXProperty(), w);
		KeyFrame kfMoveLeft = new KeyFrame(Duration.seconds(.25), kvMoveLeft1, kvMoveLeft2, kvMoveLeft3, kvMoveLeft4);
		slideLeft.getKeyFrames().add(kfMoveLeft);

		// Move right
		KeyValue kvRight1 = new KeyValue(clipRect.translateXProperty(), w);
		KeyValue kvRight2 = new KeyValue(page1.translateXProperty(), -w);
		KeyValue kvRight3 = new KeyValue(clipRect2.widthProperty(), w);
		KeyValue kvRight4 = new KeyValue(page2.translateXProperty(), 0);
		KeyFrame kfRight = new KeyFrame(Duration.seconds(.25), kvRight1, kvRight2, kvRight3, kvRight4);
		slideRight.getKeyFrames().add(kfRight);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
