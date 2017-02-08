package controller;

import java.net.URL;
import java.util.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import model.Client;

/**
 * FXML Controller class
 *
 * @author s305046, s305080, s305084, s305089
 */
public class ClientController implements Initializable {

    @FXML
    private TableView tvUsers;
    @FXML
    private TableColumn<String, String> tableUsername;
    @FXML
    private Label labelLeftStatus;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnKick;

    private final ObservableList<String> userList = FXCollections.observableList(new ArrayList<>());
    private String activeChat;

    private final Client client = new Client(this);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

	userList.add("StanBoy96");
	userList.add("Truls");
	userList.add("JohnKasper");
	tableUsername.setCellValueFactory((TableColumn.CellDataFeatures<String, String> param)
		-> new SimpleObjectProperty<>(param.getValue()));

	tvUsers.setItems(userList);

	tvUsers.setOnMouseClicked((MouseEvent event) -> {
	    System.out.println(event.getPickResult().toString());
	    //Set active user
	});
    }

    public void printWaring(String warningMsg) {
	labelLeftStatus.setText(warningMsg);
    }

    public void displayList(String[] list) {
	userList.clear();
	for (String user : list) {
	    userList.add(user.substring(1));
	}

    }

}
