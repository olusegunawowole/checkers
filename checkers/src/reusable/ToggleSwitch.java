package reusable;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class ToggleSwitch extends Pane {
	private Rectangle background;
	private Circle trigger;
	private Label text;
	private BooleanProperty status = new SimpleBooleanProperty(false);

	private FillTransition fillTransition = new FillTransition(Duration.seconds(0.10));
	private TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.10));
	private ParallelTransition parallelTransition = new ParallelTransition(translateTransition, fillTransition);

	public ToggleSwitch() {
		background = new Rectangle(50, 25);
		background.setArcWidth(25);
		background.setArcHeight(25);
		background.setStroke(Color.DARKGRAY);
		background.setFill(Color.DARKGRAY);

		trigger = new Circle(12);
		trigger.setCenterX(12);
		trigger.setCenterY(12);
		trigger.setFill(Color.WHITE);
		trigger.setStroke(Color.LIGHTGRAY);

		translateTransition.setNode(trigger);
		fillTransition.setShape(background);

		text = new Label("OFF");
		text.setTranslateX(25);
		text.setTranslateY(5);
		text.setStyle("-fx-text-fill:white;");
		text.setFont(Font.font(null, FontWeight.BOLD, null, 12));

		getChildren().addAll(background, trigger, text);

		status.addListener((obs, oldState, newState) -> {
			boolean isOn = newState.booleanValue();
			switchTo(isOn);
		});

		setCursor(Cursor.HAND);

		setOnMouseClicked(e -> {
			status.set(!status.get());
		});
	}

	public BooleanProperty stateProperty() {
		return status;
	}
	
	public boolean getStatus() {
		return status.get();
	}

	public void switchTo(boolean state) {
		boolean isOn = state;
		status.set(state);
		translateTransition.setToX(isOn ? 30 : 0);
		fillTransition.setFromValue(isOn ? Color.DARKGRAY : Color.LIGHTGREEN);
		fillTransition.setToValue(isOn ? Color.LIGHTGREEN : Color.DARKGRAY);
		text.setText(isOn ? "ON" : "OFF");
		text.setTranslateX(isOn ? 5 : 25);
		parallelTransition.play();
	}
}
