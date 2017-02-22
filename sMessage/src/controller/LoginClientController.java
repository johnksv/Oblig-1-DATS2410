package controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Client;

/**
 * Controller for the Login screen.
 * All methods needed for the functionality of the login screen.
 * Will initiate the Client class and handle user input under login. 
 * 
 * @author Member(1-2-3-4)
 */
public class LoginClientController implements Initializable {

    @FXML
    private StackPane root;
    @FXML
    private TextField uname;
    @FXML
    private PasswordField passw;
    @FXML
    private TextField serverIP;
    @FXML
    private TextField portNumber;
    @FXML
    private VBox vBoxOverlay;
    @FXML
    private VBox vboxContainer;

    private ClientController cController;
    private Stage clientStage = new Stage();
    private FXMLLoader loader;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	loader = new FXMLLoader(getClass().getResource("/view/Client.fxml"));
	try {
	    Scene scene = new Scene(loader.load());
	    cController = loader.getController();
	    clientStage.setScene(scene);
	    clientStage.setResizable(true);
	    clientStage.setMinWidth(850);
	    clientStage.setMinHeight(650);
            clientStage.setTitle("sMessage - Client");
	} catch (IOException ex) {
	    System.err.println("IOException occured. Exiting. Error:\n" + ex.toString());
	    System.err.println(ex.getStackTrace().toString());
	    Platform.exit();
	    System.exit(0);
	}

	Label label = new Label("Wating on respons from server");
	label.setFont(Font.font(18));
	ProgressIndicator progIndicator = new ProgressIndicator();

	vBoxOverlay = new VBox(label, progIndicator);
	vBoxOverlay.setSpacing(10);
	vBoxOverlay.setAlignment(Pos.CENTER);
	root.getChildren().add(vBoxOverlay);
	vBoxOverlay.setVisible(false);

    }
    
    /**
     * Changes the stage to the {@link ClientController Client stage}. <br>
     * Sets a client object with the server connection and the username to the client controller.<br>
     * Sets a listener on closing the closing of the new stage that calls {@link model.Client#disconnectServer() disconnectServer} method in the {@link model.Client client} stored in the {@link ClientController contoller}. <br>
     * Gets call by the {@link model.Client#parseCommand(java.lang.String) client class}.
     */
    public void loginSuccess() {
	cController.setClient(client);
	cController.setYourUnameLabel(uname.getText());
	clientStage.show();
	clientStage.setOnCloseRequest(event -> {
	    try {
		cController.getClient().disconnectServer();
	    } catch (IOException ex) {
		Logger.getLogger(LoginClientController.class.getName()).log(Level.SEVERE, null, ex);
	    }
	});
	closeThisStage();
    }

    /**
     * Shows error message to user.
     * Gives the option to end the application or try login one more time. <br>
     * Gets call by the {@link model.Client#parseCommand(java.lang.String) client class}.
     * @param reason why login failed.
     */
    public void loginFailed(String reason) {
	//This is a label as defined in the initilaizer
	((Label) vBoxOverlay.getChildren().get(0)).setText(reason);
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
		hideWaitingOverlay();
		client.shutdown();
	    } catch (IOException ex) {
		showError("An I/O error occured. Please try again");
	    }
	});

	close.setOnAction(event -> {
	    try {
		client.shutdown();
	    } catch (IOException ex) {
		System.err.println("I/O Exception while shutingdown client.\n" + ex.toString());
	    }
	    Platform.exit();
	    System.exit(-1);
	});
    }
    
    /**
     * Shows the user that registration failed. '
     * Gives no option to quit the application, but tells the user to log in. 
     */
    public void regUserFailed() {
	showError("User already exists. Please log in");
    }

    @FXML
    private void handleLoginBtn() {
	showWaitingOverlay();
	new Thread(() -> connectToServer(true)).start();
    }

    @FXML
    private void handleRegBtn() {
	showWaitingOverlay();
	new Thread(() -> connectToServer(false)).start();
    }

    private void connectToServer(boolean login) {
	if (!uname.getText().matches("([\\w\\d])*")) {
	    showError("Username can only contain letters and numbers.");
	    return;
	}
        if (uname.getText().length() > 25) {
	    showError("Username can't be longer then 25 characters.");
	    return;
	}
	if (passw.getText().trim().isEmpty()
		|| serverIP.getText().trim().isEmpty()
		|| portNumber.getText().trim().isEmpty()) {
	    showError("One or more fields are empty.");
	    return;
	}
	int port;
	try {
	    port = Integer.parseInt(portNumber.getText().trim());
	} catch (NumberFormatException ex) {
	    showError("Port number must be an integer number.");
	    return;
	}
	if (port < 0 || port > 65536) {
	    showError("Port number must be between 1 and 65535.");
	    return;
	}

	try {
	    client = new Client(this, cController, serverIP.getText().trim(), port);
	    try {
		if (login) {
		    client.login(uname.getText(), encrypt(passw.getText()));
		} else {
		    client.regNewUser(uname.getText(), encrypt(passw.getText()));
		}
	    } catch (Exception ex) {
		showError("Coding error, please report to the developers");
	    }

	} catch (IOException ex) {
	    showFatalError();
	} catch (NumberFormatException ex) {
	    showError("Empty field or wrong input");
	}

    }

    private String encrypt(String encrypt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
	MessageDigest md = MessageDigest.getInstance("SHA-256");
	md.update(encrypt.getBytes("UTF-16"));
	byte[] digest = md.digest();
	return new String(digest);
    }

    private void showFatalError() {
	Platform.runLater(() -> {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle("Error occurred");
	    alert.setHeaderText("Could not connect to server.");
	    alert.showAndWait();
	    hideWaitingOverlay();
	});
    }

    private void showError(String error) {
	Platform.runLater(() -> {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle("Error occurred");
	    alert.setHeaderText(error);
	    alert.showAndWait();
	    hideWaitingOverlay();
	});
    }

    private void closeThisStage() {
	//Grab a random element on the FXML-view so we get the Stage
	//then close.
	((Stage) root.getScene().getWindow()).close();
    }

    private void showWaitingOverlay() {

	vBoxOverlay.setVisible(true);
	//Disable all children of the vbox with content
	vboxContainer.setDisable(true);
    }

    private void hideWaitingOverlay() {
	vboxContainer.setDisable(false);
	vBoxOverlay.setVisible(false);
    }

}
