package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
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
import model.Command;
import model.Server;
import model.User;

/**
 * Controller for the Server screen.
 * @author s305046, s305080, s305084, s305089
 */
public class ServerController implements Initializable {

    @FXML
    public Label ipLabel;
    @FXML
    private Label portLabel;
    @FXML
    Label labelServerStatus;
    @FXML
    private Canvas canvasServerStatus;
    @FXML
    private CheckBox chboxPortAutomatic;
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
    private TableColumn<User, String> tableColumnStatus;

    private Server server;
    private boolean serverRunning = false;
    private final ObservableList<User> userList = FXCollections.observableList(new ArrayList<>());


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        drawServerStatus();
        initTabel();

        initFXMLNodes();

    }

    private void initTabel() {

        tableColumnUsername.setCellValueFactory((TableColumn.CellDataFeatures<User, String> param)
                -> new SimpleObjectProperty<>(param.getValue().getUname()));
        tableColumnStatus.setCellValueFactory((TableColumn.CellDataFeatures<User, String> param)
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

    public void printWarning(String s) {

    }

    public void update(Command command, User user) {
        switch (command) {
            case REGUSER:
                userList.add(user);
                break;
        }
    }

    @FXML
    private void handleToogleServerStatus() {

        if (serverRunning) {
            serverRunning = false;
            labelServerStatus.setText("Server is stopped");
            btnToogleServerStatus.setText("Turn on server");
            portLabel.setText("");
            ipLabel.setText("");

            portLabel.getScene().getWindow().setOnCloseRequest(null);
            server.stop();
            server = null;
        } else {
            serverRunning = true;
            labelServerStatus.setText("Server is running");
            btnToogleServerStatus.setText("Turn off server");
            try {
                if (chboxPortAutomatic.isSelected()) {
                    //TODO: Use stop or start instead
                    server = new Server(this, 0);

                } else {
                    server = new Server(this, Integer.parseInt(txtFieldPortManual.getText()));

                }
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
                handleToogleServerStatus();

            }

        }
        chboxPortAutomatic.setDisable(serverRunning);
        if (!chboxPortAutomatic.isSelected()) {
            txtFieldPortManual.setDisable(serverRunning);
        }

        drawServerStatus();
    }

    public void updateStatus() {
        tableViewUsers.refresh();
    }
}
