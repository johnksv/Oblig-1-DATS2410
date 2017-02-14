/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.Client;

/**
 * FXML Controller class
 *
 * @author Benjamin
 */
public class LoginClientController implements Initializable
{

    @FXML
    private Button btnLogin;
    @FXML
    private Button btnRegister;
    @FXML
    private TextField fieldUname;
    @FXML
    private TextField fieldPassw;
    @FXML
    private TextField fieldServerIP;
    @FXML
    private TextField fieldPortNumber;

    ClientController cController = new ClientController();

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        btnLogin.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->
        {            
            try
            {
                Client client = new Client(cController, fieldServerIP.getText(), Integer.parseInt(fieldPortNumber.getText()));
            } catch (IOException ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error occurred");
                alert.setHeaderText("Could not connect to server.");
                alert.showAndWait();
                Platform.exit();
                System.exit(-1);
            }
        });
    }

}
