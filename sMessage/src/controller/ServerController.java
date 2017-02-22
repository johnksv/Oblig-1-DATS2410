package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.Server;
import model.Status;
import model.User;

/**
 * Controller for the Server screen.
 *
 * @author Member(1-2-3-4)
 */
public class ServerController implements Initializable {

    @FXML
    private Label ipLabel;
    @FXML
    private Label portLabel;
    @FXML
    private Label labelSaveFileLoc;
    @FXML
    Label labelServerStatus;
    @FXML
    private Canvas canvasServerStatus;
    @FXML
    private CheckBox chboxPortAutomatic;
    @FXML
    private CheckBox chboxLoadUserFromSave;
    @FXML
    private TextField txtFieldPortManual;
    @FXML
    private Button btnToogleServerStatus;
    @FXML
    private TableView tableViewUsers;
    @FXML
    private SplitPane split;
    @FXML
    private TableColumn<User, String> tableColumnUsername;
    @FXML
    private TableColumn<User, Status> tableColumnStatus;

    private Server server;
    private boolean serverRunning = false;
    private final ObservableList<User> userList = FXCollections.observableList(new ArrayList<>());

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	drawServerStatus();
	initTabel();

	initFXMLNodes();

	File file = new File("usernames.txt");
	labelSaveFileLoc.setText("Location: " + file.getAbsolutePath());
	labelSaveFileLoc.setWrapText(true);

    }

    private void initTabel() {

	tableColumnUsername.setCellValueFactory((TableColumn.CellDataFeatures<User, String> param)
		-> new SimpleObjectProperty<>(param.getValue().getUname()));
	tableColumnStatus.setCellValueFactory((TableColumn.CellDataFeatures<User, Status> param)
		-> new SimpleObjectProperty<>(param.getValue().getStatus()));

	tableViewUsers.setItems(userList);

	tableViewUsers.setOnMouseClicked((MouseEvent event) -> {
	    int idx = tableViewUsers.getSelectionModel().getFocusedIndex();
	    if (idx < 0) {
		return;
	    }
	});
	tableViewUsers.prefWidthProperty().bind(split.widthProperty());

    }

    private void initFXMLNodes() {
	chboxPortAutomatic.selectedProperty().addListener(
		(ObservableValue<? extends Boolean> obs, Boolean old, Boolean newValue) -> {
		    txtFieldPortManual.setDisable(newValue);
		});
	TextFormatter<Integer> formater = new TextFormatter<>((TextFormatter.Change t) -> {
	    if (t.getText().matches("\\d*")) {
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

    /**
     * Prints warning to the server admin.
     *
     * @param warning
     */
    public void printWarning(String warning) {
	Platform.runLater(() -> {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle("Error");
	    alert.setHeaderText(warning);
	    alert.showAndWait();
	});
    }

    /**
     * Adds new user to the list of users.
     *
     * @param user User to add.
     */
    public void addNewUser(User user) {
	userList.add(user);
    }

    @FXML
    private void handleToogleServerStatus() {

	if (serverRunning) {
	    serverRunning = false;
	    labelServerStatus.setText("Server is stopped");
	    btnToogleServerStatus.setText("Turn on server");
	    portLabel.setText("");
	    ipLabel.setText("");
	    userList.clear();
	    portLabel.getScene().getWindow().setOnCloseRequest(null);
	    server.stop();
	    server = null;
	} else {
	    try {
		boolean loadUsers = chboxLoadUserFromSave.isSelected();
		if (chboxPortAutomatic.isSelected()) {
		    server = new Server(this, 0, loadUsers);
		} else {
		    server = new Server(this, Integer.parseInt(txtFieldPortManual.getText()), loadUsers);

		}
		serverRunning = true;
		labelServerStatus.setText("Server is running");
		btnToogleServerStatus.setText("Turn off server");
		portLabel.setText(server.getPort());
		ipLabel.setText(InetAddress.getLocalHost().getHostAddress());
		txtFieldPortManual.setText(server.getPort());

		portLabel.getScene().getWindow().setOnCloseRequest(e -> server.stop());

	    } catch (IOException ex) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error occurred");
		alert.setHeaderText("An IOException occurred");

		TextArea txtArea = new TextArea(ex.toString());
		alert.getDialogPane().setExpandableContent(txtArea);
		alert.show();
	    }

	}
	chboxPortAutomatic.setDisable(serverRunning);
	if (!chboxPortAutomatic.isSelected()) {
	    txtFieldPortManual.setDisable(serverRunning);
	}

	drawServerStatus();
    }

    @FXML
    private void handleClearSaveFile() {

	File file = new File(".\\usernames.smf");
	try (PrintWriter out
		= new PrintWriter(
			new BufferedWriter(
				new FileWriter(file, false)), true)) {
	    out.println("");
	} catch (IOException e) {
	    System.err.println("Could clear file:\n" + e.toString());
	}
    }

    /**
     * Refreshes the TableView. The TableView listing all users status.
     */
    public void updateStatus() {
	tableViewUsers.refresh();
    }
}
