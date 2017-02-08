package controller;

import java.net.URL;
import java.util.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableColumn<String, String> tabelUsername;
    @FXML
    private TableColumn<String, Button> tabelCommand;
    @FXML
    private Label labelLeftStatus;
    private final ObservableList<String> userList = FXCollections.observableList(new ArrayList<>());

    private final Client client = new Client(this);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

	userList.add("StanBoy96");
	userList.add("Truls");
	userList.add("JohnKasper");	
	tabelUsername.setCellValueFactory((TableColumn.CellDataFeatures<String, String> param) -> new SimpleObjectProperty<>(param.getValue()));
	tvUsers.setItems(userList);

	tabelUsername = new TableColumn<String,String>("User Name");
	tabelUsername.setCellValueFactory(new PropertyValueFactory("firstName"));
	
	 tvUsers.getColumns().setAll(tabelUsername);



	

//	lastNameCol.setCellValueFactory(new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
	//	    public ObservableValue<String> call(CellDataFeatures<String, String> p) {
	//		return p.toString();
	//	    }
	//	});

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
