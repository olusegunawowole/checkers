package reusable;

import java.text.DecimalFormat;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class AnimatedLabel extends Label {
	private Font animationFont;
	private Font font;
	private double duration = 0.75; // in seconds
	private IntegerProperty valueProperty;
	private String fontColor;

	public AnimatedLabel(int value) {
		super(String.valueOf(value));
		DecimalFormat format = new DecimalFormat("#");
		format.setGroupingUsed(true);
		format.setGroupingSize(3);
		valueProperty = new SimpleIntegerProperty(value);
		//textProperty().bind( valueProperty.asString());
		valueProperty.addListener(e->{
			setText(format.format(valueProperty.get()));
		});
		setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		font = getFont();
		animationFont = getFont();
		fontColor = "black";
	}

	public void setFontColor(String colorName) {
		fontColor = colorName;
		setStyle("-fx-text-fill: " + colorName);
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setAnimationFont(Font font) {
		animationFont = font;
	}

	public Font getAnimationFont() {
		return animationFont;
	}

	public void setAnimationDuration(double seconds) {
		duration = seconds;
	}

	public double getAnimationDuration() {
		return duration;
	}

	public void setValue(int value) {
		font = getFont();
		setFont(animationFont);
		KeyValue keyValue = new KeyValue(valueProperty, value, Interpolator.EASE_OUT);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(duration), keyValue);
		Timeline timeline = new Timeline(keyFrame);
		timeline.setOnFinished(e -> {
			setFont(font);
		});
		timeline.play();
	}
	
	public int getValue() {
		return valueProperty.get();
	}
	
	public void updateValue(int value) {
		font = getFont();
		setFont(animationFont);
		int newValue = valueProperty.get() + value;
		if (newValue < 0)
			newValue = 0;
		KeyValue keyValue = new KeyValue(valueProperty, newValue, Interpolator.EASE_OUT);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(duration), keyValue);
		Timeline timeline = new Timeline(keyFrame);
		timeline.setOnFinished(e -> {
			setFont(font);
		});
		timeline.play();
	}
}
