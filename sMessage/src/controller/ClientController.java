package controller;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
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
    private StackPane root;
    @FXML
    private VBox vboxContainer;
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
    private VBox vBoxOverlay;

    private Client client;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	createOverlay();
	initTabel();
	forTesting();
    }

    private void createOverlay() {
	Label label = new Label("Wating on respons from server");
	label.setFont(Font.font(20));
	ProgressIndicator progIndicator = new ProgressIndicator();

	vBoxOverlay = new VBox(label, progIndicator);
	vBoxOverlay.setSpacing(10);
	vBoxOverlay.setAlignment(Pos.CENTER);
	//Disable all children of the vbox with content
	vboxContainer.setDisable(true);
	root.getChildren().add(vBoxOverlay);
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

    private void forTesting() {
	userList.add("StanBoy96");
	userList.add("Truls");
	userList.add("JohnKasper");
    }

    private void showAlertBoxError(String title, String contentText) {
	Alert alert = new Alert(AlertType.ERROR);
	alert.setTitle(title);
	alert.setContentText(contentText);
	alert.showAndWait();
    }

    public void addMessageToConversation(String userName, Message msg) {
	for (Conversation cnv : friendList) {
	    if (cnv.getTalkingWithUsername().equals(userName)) {
		cnv.addMessage(msg);
		return;
	    }
	}

	showAlertBoxError("Error occurred",
		"This should not happen. Server should have control over this.");
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

    public void updateUserList(String restOfArray) {
	String[] users = restOfArray.split(";");
	for (String user : users) {
	    userList.add(user);
	}
    }

    public void updateStatus(String username) {
	if (username.charAt(0) == '+') {
	    //TODO go through friendlist and userlist
	} else if (username.charAt(0) == '-') {

	} else if (username.charAt(0) == '0') {

	}
    }

    public void loginFailed() {
	//This is a label as defined in the initilaizer
	((Label) vBoxOverlay.getChildren().get(0)).setText("Login failed. Wrong username or password");
	//Remove the progress indicator
	vBoxOverlay.getChildren().remove(1);

	Button tryAgain = new Button("Try again");
	Button close = new Button("Close");
	HBox hbox = new HBox(tryAgain, close);
	hbox.setSpacing(5);
	hbox.setAlignment(Pos.CENTER);
	vBoxOverlay.getChildren().add(hbox);

	tryAgain.setOnAction(event -> {
	    try {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginClient.fxml"));
		Stage stage = (Stage) vboxContainer.getScene().getWindow();
		Scene scene = new Scene(loader.load());
		stage.setScene(scene);

	    } catch (IOException ex) {
		System.err.println("IOException occured. Exiting.\nError:\n" + ex.toString());
		Platform.exit();
		System.exit(1);
	    }
	});

	close.setOnAction(event -> {
	    Platform.exit();
	    System.exit(-1);
	});

    }

    public void loginSuccess() {
	root.getChildren().remove(vBoxOverlay);
	vboxContainer.setDisable(false);
    }

    /**
     * Reacts to an request from another client.
     *
     * @param username
     */
    public void connectRequest(String username) {
	ButtonType accept = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
	ButtonType reject = new ButtonType("Reject", ButtonBar.ButtonData.CANCEL_CLOSE);
	Alert alert = new Alert(AlertType.CONFIRMATION, "", accept, reject);
	alert.setTitle("Confirm Connection");
	alert.setHeaderText(username + " wants to connect to you.");

	Optional< ButtonType> answer = alert.showAndWait();
	if (answer.isPresent()) {
	    try {
		String respons = answer.get() == accept ? "YES" : "NO";
		client.sendRespons(username, respons);
	    } catch (IOException ex) {
		Alert alertErr = new Alert(AlertType.ERROR);
		alertErr.setTitle("Error occurred");
		alertErr.setHeaderText("An IOException occurred");

		TextArea txtArea = new TextArea(ex.toString());
		alert.getDialogPane().setExpandableContent(txtArea);
	    }
	}
    }

    public void setClient(Client client) {
	this.client = client;
    }

}
