package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.Command;
import model.Server;
import model.User;
import model.client.Conversation;

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

    private Server server;
    private boolean serverRunning = false;
    private final ObservableList<User> userList = FXCollections.observableList(new ArrayList<>());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	drawServerStatus();
	initTabel();

	initFXMLNodes();
    }

    private void initTabel() {
	tableColumnUsername.setCellValueFactory((TableColumn.CellDataFeatures<String, String> param)
		-> new SimpleObjectProperty<>(param.getValue()));
	tableColumnStatus.setCellValueFactory((TableColumn.CellDataFeatures<String, String> param)
		-> new SimpleObjectProperty<>(param.getValue()));

	tableViewUsers.setItems(userList);

	tableViewUsers.setOnMouseClicked((MouseEvent event) -> {
	    int idx = tableViewUsers.getSelectionModel().getFocusedIndex();
	    if (idx < 0) {
		return;
	    }
	    String user = userList.get(idx).getUname();
	});

    }

    private void initFXMLNodes() {
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

    public void update(Command command, User user) {
	switch (command) {
	    case REGUSER:
		userList.add(user);
		break;
	}
    }

    @FXML
    private void handleToogleServerStatus() {
	if (serverRunning) {
	    serverRunning = false;
	    labelServerStatus.setText("Server is stopped");
	    btnToogleServerStatus.setText("Turn on server");
	    server = null;

	} else {
	    serverRunning = true;
	    labelServerStatus.setText("Server is running");
	    btnToogleServerStatus.setText("Turn off server");
	    try {
		if (chboxPortAutomatic.isSelected()) {
		    //TODO: Use stop or start instead
		    server = new Server(this);
		} else {
		    server = new Server(this, Integer.parseInt(txtFieldPortManual.getText()));
		}
	    } catch (IOException ex) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error occurred");
		alert.setHeaderText("An IOException occurred");

		TextArea txtArea = new TextArea(ex.toString());
		alert.getDialogPane().setExpandableContent(txtArea);
	    }
	}
	chboxPortAutomatic.setDisable(serverRunning);
	if (!chboxPortAutomatic.isSelected()) {
	    txtFieldPortManual.setDisable(serverRunning);
	}
	drawServerStatus();
    }
}
