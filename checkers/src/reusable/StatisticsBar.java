package reusable;

import java.text.DecimalFormat;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class StatisticsBar extends VBox {
	private Label leftLabel;
	private Label rightLabel;
	private Label headerLabel;

	private double barWidth;
	private double barHeight;

	private Rectangle leftOverlay;
	private Rectangle leftBackground;
	private Rectangle rightOverlay;
	private Rectangle rightBackground;

	private Color color;
	
	public StatisticsBar(String title, int leftVal, int rightVal) {
		this(title, leftVal,rightVal, false);
	}
	
	public StatisticsBar(String title, double leftVal, double rightVal) {
		this(title, leftVal, rightVal, true);
	}

	private StatisticsBar(String title, double leftVal, double rightVal, boolean isDouble) {
		color = Color.WHITE;
		
		String colorAsString = convertColorToString(color);

		barWidth = 250;
		barHeight = 25;
		
		leftOverlay = new Rectangle(0, barHeight);
		leftOverlay.setFill(color);
		leftBackground = new Rectangle(barWidth, barHeight);
		leftBackground.setFill(Color.rgb(128, 128, 128, 0.5));

		StackPane leftBarPane = new StackPane(leftBackground, leftOverlay);
		leftBarPane.setAlignment(Pos.CENTER_RIGHT);
		leftBarPane.setPadding(new Insets(2));
		leftBarPane.setMaxHeight(30);
		leftBarPane.setStyle("-fx-border-width: 1px; -fx-border-color: " + colorAsString);

		rightOverlay = new Rectangle(0, barHeight);
		rightOverlay.setFill(color);

		rightBackground = new Rectangle(barWidth, barHeight);
		rightBackground.setFill(Color.rgb(128, 128, 128, 0.5));

		StackPane rightBarPane = new StackPane(rightBackground, rightOverlay);
		rightBarPane.setAlignment(Pos.CENTER_LEFT);
		rightBarPane.setPadding(new Insets(2));
		rightBarPane.setMaxHeight(30);
		rightBarPane.setStyle("-fx-border-width: 1px; -fx-border-color: " + colorAsString);

		headerLabel = new Label(title);
		headerLabel.setStyle("-fx-text-fill: " + colorAsString);
		headerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

		leftLabel = new Label(25 + "");
		leftLabel.setStyle("-fx-text-fill: " + colorAsString);
		leftLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		leftLabel.setMinWidth(100);
		leftLabel.setAlignment(Pos.BOTTOM_RIGHT);
		
		rightLabel = new Label(25 + "");
		rightLabel.setStyle("-fx-text-fill: " + colorAsString);
		rightLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		rightLabel.setMinWidth(100);
		
		setValue(leftVal, rightVal, isDouble);

		HBox root = new HBox();
		root.setSpacing(5);
		root.getChildren().addAll(leftLabel, leftBarPane, rightBarPane, rightLabel);
		root.setAlignment(Pos.CENTER);
		getChildren().addAll(headerLabel, root);
		setAlignment(Pos.CENTER);
		setSpacing(5);
	}
	
	private void setValue(double leftVal, double rightVal, boolean setDouble) {
		double sum = leftVal + rightVal;
		double newLeftVal = sum > 0 ? (leftVal / sum * barWidth) : 0;
		double newRightVal = sum > 0 ? (rightVal / sum * barWidth) : 0;
		DecimalFormat format = new DecimalFormat("#.##");

		if(setDouble) {
			rightLabel.setText(format.format(rightVal));
			leftLabel.setText(format.format(leftVal));
		}
		else {
			String str = (int) rightVal + "";
			rightLabel.setText(str);
			str = (int) leftVal + "";
			leftLabel.setText(str);
		}
		KeyValue leftBarKeyValue = new KeyValue(leftOverlay.widthProperty(), newLeftVal);
		KeyValue RightBarKeyValue = new KeyValue(rightOverlay.widthProperty(), newRightVal);

		KeyFrame keyFrame = new KeyFrame(Duration.millis(250), leftBarKeyValue, RightBarKeyValue);
		
		Timeline timeline = new Timeline(keyFrame);
		timeline.play();
	}

	public void setValue(double leftVal, double rightVal) {
		setValue(leftVal, rightVal, true);
	}
	
	public void setValue(int leftVal, int rightVal) {
		setValue(leftVal, rightVal, true);
	}

	private String convertColorToString(Color color) {
		StringBuilder sb = new StringBuilder("rgba(");
		final int constant = 255;
		int red = (int) (color.getRed() * constant);
		int green = (int) (color.getGreen() * constant);
		int blue = (int) (color.getBlue() * constant);
		sb.append(red + ", ");
		sb.append(green + ", ");
		sb.append(blue + ")");
		return sb.toString();
	}
}
