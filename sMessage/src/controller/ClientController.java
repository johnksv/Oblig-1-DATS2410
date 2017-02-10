package controller;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import model.Client;
import model.client.Conversation;
import model.client.Message;

/**
 * FXML Controller class
 *
 * @author s305046, s305080, s305084, s305089
 */
public class ClientController implements Initializable {

    @FXML
    private TableView tvFriends;
    @FXML
    private TableView tvUsers;
    @FXML
    private TableColumn<String, String> columnUsername;
    @FXML
    private TableColumn<Conversation, String> columnFriends;
    @FXML
    private Label labelLeftStatus;
    @FXML
    private Label labelTalkingWIth;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnKick;

    private final ObservableList<Conversation> friendList = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<String> userList = FXCollections.observableList(new ArrayList<>());
    private String activeChat;

    private Client client;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	//initClient();

	forTesting();
	initTabel();
    }

    private void initTabel() {
	columnUsername.setCellValueFactory((TableColumn.CellDataFeatures<String, String> param)
		-> new SimpleObjectProperty<>(param.getValue()));
	columnFriends.setCellValueFactory((TableColumn.CellDataFeatures<Conversation, String> param)
		-> new SimpleObjectProperty<>(param.getValue().getTalkingWithUsername()));

	tvFriends.setItems(friendList);
	tvUsers.setItems(userList);

	tvFriends.setOnMouseClicked((MouseEvent event) -> {
	    int idx = tvUsers.getSelectionModel().getFocusedIndex();
	    labelTalkingWIth.setText("Talking with: " + userList.get(idx));
	    //Set active user
	});

	tvUsers.setOnMouseClicked((MouseEvent event) -> {
	    int idx = tvUsers.getSelectionModel().getFocusedIndex();
	    String user = userList.get(idx);

	    Alert alert = new Alert(AlertType.CONFIRMATION);
	    alert.setTitle("Confirm Connection");
	    alert.setHeaderText("Do you want to connect with " + user);
	    alert.setContentText("We will alert you when your peer "
		    + "has responded to the request.");

	    Optional<ButtonType> answer = alert.showAndWait();
	    if (answer.isPresent()) {
		try {
		    client.connectChat(user);
		} catch (IOException ex) {
		    Alert alertErr = new Alert(AlertType.ERROR);
		    alertErr.setTitle("Error occurred");
		    alertErr.setHeaderText("An IOException occurred");

		    TextArea txtArea = new TextArea(ex.toString());
		    alert.getDialogPane().setExpandableContent(txtArea);
		}
	    }
	});
    }

    private void initClient() {
	Alert loadAlert = new Alert(AlertType.INFORMATION);
	loadAlert.setHeaderText("Connecting to server");
	loadAlert.setContentText("Username: " + "\n" + ", ip: " + ":");
	loadAlert.show();
	try {
	    client = new Client(this, "192.168.0.1", 15);
	} catch (IOException ex) {
	    loadAlert.close();
	    Alert alert = new Alert(AlertType.ERROR);
	    alert.setTitle("Error occurred");
	    alert.setHeaderText("Could not connect to server.");
	    alert.showAndWait();
	    Platform.exit();
	    System.exit(-1);
	}
	loadAlert.close();
    }

    public void addMessageToConversation(String userName, Message msg) {
	for (Conversation cnv : friendList) {
	    if (cnv.getTalkingWithUsername().equals(userName)) {
		cnv.addMessage(msg);
		return;
	    }
	}
	Alert alert = new Alert(AlertType.ERROR);
	alert.setTitle("Error occurred");

	alert.setContentText("This should not happen. Server should have control over this.");
	alert.showAndWait();

    }

    public void printMessageToView(String msg, boolean waring) {
	labelLeftStatus.setText(msg);
    }

    public void displayList(String[] list) {
	userList.clear();
	for (String user : list) {
	    userList.add(user.substring(1));
	}

    }

    private void forTesting() {
	userList.add("StanBoy96");
	userList.add("Truls");
	userList.add("JohnKasper");
    }

}
