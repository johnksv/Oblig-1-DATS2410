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
 * FXML Controller class
 *
 * @author Benjamin
 */
public class LoginClientController implements Initializable {

    @FXML
    private StackPane root;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnRegister;
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
	    System.err.println("IOException occured. Exiting.\nError:\n" + ex.toString());
	    Platform.exit();
	    System.exit(1);
	}
	Label label = new Label("Wating on respons from server");
	label.setFont(Font.font(18));
	ProgressIndicator progIndicator = new ProgressIndicator();

	vBoxOverlay = new VBox(label, progIndicator);
	vBoxOverlay.setSpacing(10);
	vBoxOverlay.setAlignment(Pos.CENTER);

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

    public void loginSuccess() {
	cController.setClient(client);
	clientStage.show();
	closeThisStage();

    }

    @FXML
    private void handleLoginBtn() {

	try {
	    if (uname.getText().matches("([\\w\\d])*")) {
		showWaitingOverlay();
		client = new Client(this, cController, serverIP.getText(), Integer.parseInt(portNumber.getText()));

		try {
		    client.login(uname.getText(), encrypt(passw.getText()));
		} catch (Exception ex) {
		    showError("Coding error, please report to the developers");
		}
		loginSuccess();
	    } else {
		showError("Uname can only contain letters and numbers");
	    }
	} catch (IOException ex) {
	    showFatalError();
	} catch (NumberFormatException ex) {
	    showError("Empty field or wrong input");
	}
    }

    @FXML
    private void handleRegBtn() {
	try {
	    if (uname.getText().matches("([\\w\\d])*")) {
		showWaitingOverlay();
		client = new Client(this, cController, serverIP.getText(), Integer.parseInt(portNumber.getText()));
		try {
		    client.regNewUser(uname.getText(), encrypt(passw.getText()));
		} catch (Exception ex) {
		    showError("Coding error, please report to the developers");
		}
	    } else {
		showError("Uname can only contain letters and numbers");
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

    private void showError(String errorM) {
	Alert alert = new Alert(Alert.AlertType.ERROR);
	alert.setTitle("Error occurred");
	alert.setHeaderText(errorM);
	alert.showAndWait();
    }

    private void showFatalError() {
	Alert alert = new Alert(Alert.AlertType.ERROR);
	alert.setTitle("Error occurred");
	alert.setHeaderText("Could not connect to server.");
	alert.showAndWait();
	hideWaitingOverlay();

    }

    private void closeThisStage() {
	//Grab a random element on the FXML-view so we get the Stage
	//then close.
	((Stage) btnLogin.getScene().getWindow()).close();
    }

    private void showWaitingOverlay() {
	//Disable all children of the vbox with content
	vboxContainer.setDisable(true);
	root.getChildren().add(vBoxOverlay);
    }

    private void hideWaitingOverlay() {
	vboxContainer.setDisable(false);
	root.getChildren().remove(vBoxOverlay);
    }

}
