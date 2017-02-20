/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import model.Client;

/**
 * FXML Controller class
 *
 * @author Benjamin
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

    public void loginSuccess() {
	cController.setClient(client);
	cController.setLeftLabelTest(uname.getText());
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

    public void regUserFailed() {
	showError("User already exists. Please log in");
    }

    @FXML
    private void handleLoginBtn() {
	showWaitingOverlay();
	connectToServer(true);
    }

    @FXML
    private void handleRegBtn() {
	showWaitingOverlay();
	connectToServer(false);
    }

    private void connectToServer(boolean login) {
	if (!uname.getText().matches("([\\w\\d])*")) {
	    showError("Uname can only contain letters and numbers.");
	    return;
	}
	if (passw.getText().trim().isEmpty()
		|| serverIP.getText().trim().isEmpty()
		|| portNumber.getText().trim().isEmpty()) {
	    showError("One or more fields are empty.");
	    return;
	}
	int port = 0;
	try {
	    port = Integer.parseInt(portNumber.getText());
	} catch (NumberFormatException ex) {
	    showError("Port number must be an integer number.");
	    return;
	}
	if (port < 0 || port > 65536) {
	    showError("Port number must be between 1 and 65535.");
	    return;
	}

	try {
	    client = new Client(this, cController, serverIP.getText(), port);
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
	Alert alert = new Alert(Alert.AlertType.ERROR);
	alert.setTitle("Error occurred");
	alert.setHeaderText("Could not connect to server.");
	alert.showAndWait();
	hideWaitingOverlay();
    }

    private void showError(String error) {
	Alert alert = new Alert(Alert.AlertType.ERROR);
	alert.setTitle("Error occurred");
	alert.setHeaderText(error);
	alert.showAndWait();
	hideWaitingOverlay();
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
