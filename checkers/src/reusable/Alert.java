package reusable;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Alert extends VBox {
	private StackPane[] buttons;
	private Label label;
	private static String backgroundColor = "black";
	private static String fontColor = "white";
	private static String borderColor = "white";

	public Alert() {
		ProgressIndicator PI = new ProgressIndicator();
		label = new Label("Wait...");
		label.setStyle("-fx-text-fill: " + fontColor);
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
		label.setMinHeight(70);
		label.setAlignment(Pos.CENTER);
		setSpacing(5);
		setAlignment(Pos.CENTER);
		setPrefSize(300, 85);
		setMaxSize(300, 135);
		setPadding(new Insets(10, 10, 10, 10));
		setStyle("-fx-background-color: " + backgroundColor
				+ "; -fx-border-width: 2px; -fx-border-style: solid; -fx-border-color: " + borderColor);
		getChildren().addAll(PI, label);
	}

	public Alert(String message, String... buttonName) {
		label = new Label(message);
		label.setWrapText(true);
		label.setStyle("-fx-text-fill: " + fontColor);
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
		label.setMinHeight(70);
		label.setAlignment(Pos.TOP_LEFT);

		HBox hb = new HBox(10);

		buttons = new StackPane[buttonName.length];
		for (int i = 0; i < buttonName.length; i++) {
			Label name = new Label(buttonName[i]);
			name.setStyle("-fx-text-fill: " + fontColor);
			name.setFont(Font.font("Arial", FontWeight.NORMAL, 15));

			buttons[i] = new StackPane(name);
			buttons[i].setStyle(
					"-fx-background-color: transparent; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: "
							+ borderColor);
			buttons[i].setMinHeight(25);
			buttons[i].setMinWidth(100);
			int index = i;
			buttons[i].setOnMouseEntered(e -> {
				buttons[index].setStyle("-fx-background-color: " + fontColor
						+ "; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: " + borderColor);
				name.setStyle("-fx-text-fill: " + backgroundColor);
			});
			buttons[i].setOnMouseExited(e -> {
				buttons[index].setStyle(
						"-fx-background-color: transparent; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: "
								+ borderColor);
				name.setStyle("-fx-text-fill: " + fontColor);
			});
			buttons[i].setCursor(Cursor.HAND);
			hb.getChildren().add(buttons[i]);
		}
		hb.setAlignment(Pos.CENTER);

		setSpacing(25);
		setMaxSize(300, 135);
		setPadding(new Insets(10, 10, 10, 10));
		setStyle("-fx-background-color: " + backgroundColor
				+ "; -fx-border-width: 2px; -fx-border-style: solid; -fx-border-color: " + borderColor);
		getChildren().addAll(label, hb);

	}

	public void setButtonOnAction(EventHandler<MouseEvent> evt, int buttonNumber) {
		buttons[buttonNumber].setOnMouseClicked(evt);
	}

	public StackPane getButton(int buttonNumber) {
		return buttons[buttonNumber];
	}

	public static String getBackgroundColor() {
		return backgroundColor;
	}

	public static void setBackgroundColor(String backgroundColor) {
		Alert.backgroundColor = backgroundColor;
	}

	public static String getFontColor() {
		return fontColor;
	}

	public static void setFontColor(String fontColor) {
		Alert.fontColor = fontColor;
	}

	public static String getBorderColor() {
		return borderColor;
	}

	public static void setBorderColor(String borderColor) {
		Alert.borderColor = borderColor;
	}
}
