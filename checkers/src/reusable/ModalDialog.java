package reusable;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ModalDialog extends StackPane {
    private boolean isOpen;
    public ModalDialog() {
	setAlignment(Pos.CENTER);
	setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
	setScaleX(0);
	setScaleY(0);
	}

    public void close() {
	EventHandler <ActionEvent>onFinished = new EventHandler<ActionEvent>() {

	    @Override
	    public void handle(ActionEvent event) {
		getChildren().clear();
		isOpen = false;
	    }

	};
	ScaleTransition scale = new ScaleTransition(Duration.seconds(.25), this);
	scale.setToX(0);
	scale.setToY(0);
	scale.setAutoReverse(false);
	scale.setCycleCount(1);
	scale.play();
	scale.setOnFinished(onFinished);	
    }

    public void open(Node pane) {
	if(isOpen)
	    return;
	isOpen = true;
	getChildren().add(pane);
	ScaleTransition scale = new ScaleTransition(Duration.seconds(.25), this);
	scale.setToX(1);
	scale.setToY(1);
	scale.setAutoReverse(false);
	scale.setCycleCount(1);
	scale.play();	
    }

    public boolean isOpen() {
	return isOpen;
    }

    public void setOpen(boolean isOpen) {
	this.isOpen = isOpen;
    }
    
    public void set(Node pane) {
    	getChildren().clear();
    	getChildren().add(pane);
		
	}
}
