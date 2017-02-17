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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Client;

/**
 * FXML Controller class
 *
 * @author Benjamin
 */
public class LoginClientController implements Initializable {

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

    private ClientController cController;
    private Stage clientStage = new Stage();
    private FXMLLoader loader;

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

    }

    @FXML
    private void handleLoginBtn() {
        try {
            if (uname.getText().matches("([\\w\\d])*")) {
                Client client = new Client(cController, serverIP.getText(), Integer.parseInt(portNumber.getText()));

                try {
                    client.login(uname.getText(), encrypt(passw.getText()));
                } catch (Exception ex) {
                    showError("Coding error, please report to the developers");
                }
                cController.setClient(client);
                clientStage.show();
                closeThisStage();
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
        md.update(passw.getText().getBytes("UTF-16"));
        byte[] digest = md.digest();
        return new String(digest);
    }

    @FXML
    private void handleRegBtn() {
        try {
            if (uname.getText().matches("([\\w\\d])*")) {
                Client client = new Client(cController, serverIP.getText(), Integer.parseInt(portNumber.getText()));
                try {
                    client.regNewUser(uname.getText(), encrypt(passw.getText()));
                }  catch (Exception ex) {
                    showError("Coding error, please report to the developers");
                }
                cController.setClient(client);
                clientStage.show();
                closeThisStage();
            } else {
                showError("Uname can only contain letters and numbers");
            }
        } catch (IOException ex) {
            showFatalError();
        } catch (NumberFormatException ex) {
            showError("Empty field or wrong input");
        }
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
        //Platform.exit();
        //System.exit(-1);

    }

    private void startClient() {
        clientStage.show();
    }

    private void closeThisStage() {
        //Grab a random element on the FXML-view so we get the Stage
        //then close.
        ((Stage) btnLogin.getScene().getWindow()).close();
    }

}
