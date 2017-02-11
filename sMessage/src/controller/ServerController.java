package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author s305046, s305080, s305084, s305089
 */
public class ServerController implements Initializable {

    @FXML
    private Label labelServerStatus;
    @FXML
    private Canvas canvasServerStatus;
    @FXML
    private CheckBox chboxPortAutomatic;
    @FXML
    private TextField txtFieldPortManual;
    @FXML
    private Button btnToogleServerStatus;
    @FXML
    private TableView tableViewUsers;
    @FXML
    private TableColumn<String, String> tableColumnUsername;
    @FXML
    private TableColumn<String, String> tableColumnStatus;

    private boolean serverRunning = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	drawServerStatus();

	chboxPortAutomatic.selectedProperty().addListener(
		(ObservableValue<? extends Boolean> obs, Boolean old, Boolean newValue) -> {
		    txtFieldPortManual.setDisable(newValue);
		});
	TextFormatter<Integer> formater = new TextFormatter<>((TextFormatter.Change t) -> {
	    if (t.getText().matches("\\d")) {
		//TODO Check if portnumber is between 1 - 65535 
		return t;
	    }
	    return null;
	});
	
	txtFieldPortManual.setTextFormatter(formater);

    }

    private void drawServerStatus() {
	if (serverRunning) {
	    canvasServerStatus.getGraphicsContext2D().setFill(Color.GREEN);
	} else {
	    canvasServerStatus.getGraphicsContext2D().setFill(Color.RED);
	}
	canvasServerStatus.getGraphicsContext2D().fillOval(0, 0, 16, 16);
    }

    public void printWarning(String s) {

    }

    @FXML
    private void handleToogleServerStatus() {
	if (serverRunning) {
	    serverRunning = false;
	    labelServerStatus.setText("Server is stopped");
	    btnToogleServerStatus.setText("Turn on server");

	} else {
	    serverRunning = true;
	    labelServerStatus.setText("Server is running");
	    btnToogleServerStatus.setText("Turn off server");
	}
	chboxPortAutomatic.setDisable(serverRunning);
	if (!chboxPortAutomatic.isSelected()) {
	    txtFieldPortManual.setDisable(serverRunning);
	}
	drawServerStatus();
    }
}
