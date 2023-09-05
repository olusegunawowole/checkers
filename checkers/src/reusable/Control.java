package reusable;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class Control extends GridPane {
	private PanelButton upButton;
	private PanelButton rightButton;
	private PanelButton leftButton;
	private PanelButton downButton;
	private PanelButton centerButton;

	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;

	public Control() {
		upButton = new PanelButton(UP);
		Tooltip tooltip = new Tooltip("Rotate Right");
		Tooltip.install(upButton, tooltip);

		rightButton = new PanelButton(RIGHT);
		tooltip = new Tooltip("Right");
		Tooltip.install(rightButton, tooltip);

		leftButton = new PanelButton(LEFT);
		tooltip = new Tooltip("Left");
		Tooltip.install(leftButton, tooltip);

		downButton = new PanelButton(DOWN);
		tooltip = new Tooltip("Soft Drop");
		Tooltip.install(downButton, tooltip);

		centerButton = new PanelButton();
		tooltip = new Tooltip("Hard Drop");
		Tooltip.install(centerButton, tooltip);

		add(upButton, 1, 0);
		add(centerButton, 1, 1);
		add(rightButton, 2, 1);
		add(downButton, 1, 2);
		add(leftButton, 0, 1);
		setMaxSize(150, 150);
		disableProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				setOpacity(0.5);
			} else {
				setOpacity(1.0);
			}
		});
	}

	public void setHoverColor(Color hoverColor) {
		PanelButton.hoverColor = hoverColor;
	}

	public PanelButton getCenterButton() {
		return centerButton;
	}

	public PanelButton getUpButton() {
		return upButton;
	}

	public PanelButton getRightButton() {
		return rightButton;
	}

	public PanelButton getLeftButton() {
		return leftButton;
	}

	public PanelButton getDownButton() {
		return downButton;
	}

	public static class PanelButton extends StackPane {
		private static double SIZE = 75;
		private static double[][] coordinates = { { 0.0, SIZE / 2, SIZE / 2, 0.0, SIZE, SIZE / 2 },
				{ SIZE / 2, 0.0, SIZE, SIZE / 2, SIZE / 2, SIZE }, { 0.0, SIZE / 2, SIZE, SIZE / 2, SIZE / 2, SIZE },
				{ 0.0, SIZE / 2, SIZE / 2, 0.0, SIZE / 2, SIZE }

		};
		private static Color hoverColor = Color.BLACK;
		private Polygon triangle;
		private Rectangle rectangle;

		public PanelButton() {
			rectangle = new Rectangle(SIZE - 5, SIZE - 5);
			rectangle.setStroke(Color.DARKGRAY);
			rectangle.setFill(Color.TRANSPARENT);

			setOnMouseEntered(e -> rectangle.setFill(hoverColor));
			setOnMouseExited(e -> rectangle.setFill(Color.TRANSPARENT));

			setCursor(Cursor.HAND);
			setOnMousePressed(e -> rectangle.setOpacity(0.8));
			setOnMouseReleased(e -> rectangle.setOpacity(1.0));

			getChildren().add(rectangle);
		}

		public PanelButton(int direction) {
			triangle = new Polygon(coordinates[direction]);
			triangle.setStroke(Color.DARKGRAY);
			triangle.setFill(Color.TRANSPARENT);

			setAlignment(Pos.BASELINE_CENTER);

			setOnMouseEntered(e -> triangle.setFill(hoverColor));
			setOnMouseExited(e -> triangle.setFill(Color.TRANSPARENT));

			setCursor(Cursor.HAND);
			setOnMousePressed(e -> triangle.setOpacity(0.8));
			setOnMouseReleased(e -> triangle.setOpacity(1.0));

			getChildren().add(triangle);
		}

		public void setEffect(boolean val) {
			if (triangle != null) {
				if (val)
					triangle.setOpacity(0.8);
				else
					triangle.setOpacity(1.0);
			}
			if (rectangle != null) {
				if (val)
					rectangle.setOpacity(0.8);
				else
					rectangle.setOpacity(1.0);
			}
		}
	}
}