package reusable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CustomField extends VBox {

	private TextField textField;
	private BooleanProperty edit;
	private Label errLabel;
	private boolean error;
	private String title;
	private Label label;
	private BooleanProperty errorProperty;

	public CustomField(String title) {
		errorProperty = new SimpleBooleanProperty(false);
		label = new Label(title);
		label.setStyle("-fx-text-fill: white");
		label.setFont(Font.font("Arial", FontWeight.BOLD, 15));

		errLabel = new Label();
		errLabel.setStyle("-fx-text-fill: #FADADD");
		errLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));

		String style = "-fx-border-width: 1px;-fx-border-style: solid inside; -fx-border-color: lightgray; -fx-background-color: white";

		textField = new TextField();
		textField.setPrefWidth(200);
		textField.setPrefHeight(27);
		textField.setStyle(style);
		textField.setPromptText(title);
		this.title = title;
		
		VBox.setMargin(label, new Insets(0, 0, 2, 2));
		getChildren().addAll(label, textField, errLabel);
		widthProperty().addListener(e -> {
			double width = getWidth();
			textField.setPrefWidth(width);
		});
	}

	public void setCustomMaxWidth(double width) {
		widthProperty().addListener(e -> {
			setCustomWidth(width);
		});
	}

	public void setCustomWidth(double width) {
		textField.setPrefWidth(width);
		textField.setMinWidth(width);
	}

	public void setValue(String value) {
		textField.setText(value);
	}

	public String getValue() {
			return textField.getText().trim();
		}
		
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		label.setText(title);
	}

	public String getErrorMessage() {
		return errLabel.getText();
	}

	public void setError(boolean err, String errMsg) {
		errorProperty.set(err);
		this.error = err;
		if (err) {
			textField.setStyle("-fx-border-color: red; -fx-background-color: #FADADD");
			errLabel.setText(errMsg);
		} else {
			textField.setStyle("-fx-border-color: lightgray; -fx-background-color: white");
			errLabel.setText("");
		}
	}

	public void clear() {
		textField.clear();
	}

	public TextField getTextField() {
		return textField;
	}

	public void setPromptText(String text) {
			textField.setPromptText(text);
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		setError(error, "");
	}

	public boolean isEditable() {
		return edit.get();
	}

	public BooleanProperty errorProperty() {
		return errorProperty;
	}
	
	public void focus() {
		textField.requestFocus();
	}
}
