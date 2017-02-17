package controller;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import model.client.ClientUser;
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
    private TableColumn<ClientUser, String> columnUsername;
    @FXML
    private TableColumn<Conversation, String> columnFriends;
    @FXML
    private Label labelLeftStatus;
    @FXML
    private Label labelTalkingWIth;
    @FXML
    private Button btnKick;
    @FXML
    private TextArea txtAreaMessages;
    @FXML
    private TextArea txtAreaNewMessage;

    private final ObservableList<Conversation> friendList = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<ClientUser> userList = FXCollections.observableList(new ArrayList<>());
    private VBox vBoxOverlay;

    private Client client;
    private Conversation activeConversation;
    private boolean newCon = true;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createOverlay();
        initTabel();
        //forTesting();
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
        columnUsername.setCellValueFactory((TableColumn.CellDataFeatures<ClientUser, String> param)
                -> new SimpleObjectProperty<>(param.getValue().getUserName()));
        columnFriends.setCellValueFactory((TableColumn.CellDataFeatures<Conversation, String> param)
                -> new SimpleObjectProperty<>(param.getValue().getTalkingWithUsername()));

        tvFriends.setItems(friendList);
        tvUsers.setItems(userList);

        tvFriends.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change c) -> {
            int idx = tvFriends.getSelectionModel().getFocusedIndex();
            Conversation conv = friendList.get(idx);
            setActiveConversation(conv);
        });

        tvUsers.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change c) -> {
            if (newCon) {
                int idx = tvUsers.getSelectionModel().getFocusedIndex();
                if (idx == -1) {
                    return;
                }
                ClientUser user = userList.get(idx);

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirm Connection");
                alert.setHeaderText("Do you want to connect with " + user.getUserName());
                alert.setContentText("We will alert you when your peer "
                        + "has responded to the request.");

                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    try {
                        client.connectChat(user.getUserName());
                    } catch (IOException ex) {
                        showAlertIOException(ex);
                    }
                }
            }
        });

    }

    private void showAlertIOException(IOException ex) {
        Alert alertErr = new Alert(AlertType.ERROR);
        alertErr.setTitle("Error occurred");
        alertErr.setHeaderText("An IOException occurred");

        TextArea txtArea = new TextArea(ex.toString());
        alertErr.getDialogPane().setExpandableContent(txtArea);
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
                if (activeConversation == cnv) {
                    appendMsgToConversation();
                } else {
                    //TODO: Set the text of the user sent to to bold
                }
                return;
            }
        }

        showAlertBoxError("Error occurred",
                "This should not happen. Server should have control over this.");
    }

    public void updateUserList(String restOfArray) {
        if (restOfArray.equals("")) {
            return;
        }
        String[] users = restOfArray.split(";");
        for (int i = 0; i < users.length; i += 2) {
            userList.add(new ClientUser(users[i], users[i + 1]));
        }
    }

    public void updateStatus(String username, String status) {
        ClientUser user = new ClientUser(username, status);
        Conversation con = new Conversation(user);
        if (userList.contains(user)) {
            //TODO
        } else if (friendList.contains(con)) {
            //TODO
        } else {
            userList.add(user);
        }
        /*
    if (username.charAt(0) == '+') {
	    //TODO go through friendlist and userlist
	} else if (username.charAt(0) == '-') {

	} else if (username.charAt(0) == '0') {

	}*/
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
                client.disconnectServer();
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

        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.isPresent()) {
            try {
                String respons = answer.get() == accept ? "YES" : "NO";
                client.sendRespons(username, respons);
                if (respons.equals("YES")) {
                    moveFromUsersToFriends(username, false);
                }
            } catch (IOException ex) {
                showAlertIOException(ex);
            }
        }
    }

    public void moveFromUsersToFriends(String username, boolean showAlert) {
        newCon = false;
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserName().equals(username)) {
                friendList.add(new Conversation(userList.get(i)));
                userList.remove(i);

                break;
            }
        }
        newCon = true;

        if (showAlert) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Accepted");
            alert.setContentText(username + " is added to your friends list. You can chat now.");
            alert.show();
        }
    }

    public void moveFromFriendsToUser(String username, boolean showAlert) {

        for (int i = 0; i < friendList.size(); i++) {
            if (friendList.get(i).getTalkingWithUsername().equals(username)) {
                userList.add(userList.get(i).getUser());
                friendList.remove(i);
                break;
            }
        }

        if (showAlert) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Disconnected");
            alert.setContentText(username + " disconnected, and has been moved to the user list.");
            alert.show();
        }
    }

    public void negativeResponse(String username) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Rejected");
        alert.setContentText(username + " doesn't want to talk with you...");

    }

    public void setClient(Client client) {
        this.client = client;
        try {
            client.getUserList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveConversation(Conversation conv) {
        activeConversation = conv;
        labelTalkingWIth.setText("Talking with: " + conv.getTalkingWithUsername());
        appendMsgToConversation();
    }

    @FXML
    private void handleSendMsg() {
        try {
            String msg = txtAreaNewMessage.getText();
            activeConversation.addMessage(new Message("Me", msg));
            appendMsgToConversation();
            client.sendMsg(activeConversation.getTalkingWithUsername(), msg.replace("\\", "&#92").replace(";", "&#59"));
            txtAreaNewMessage.clear();
        } catch (IOException ex) {
            showAlertIOException(ex);
        }
    }

    private void appendMsgToConversation() {

        txtAreaMessages.clear();
        for (Message msg : activeConversation.getMessages()) {
            txtAreaMessages.appendText(msg.toString().replace("&#92", "\\").replace("&#59", ";"));
            txtAreaMessages.appendText("\n");
        }
    }

    @FXML
    private void handleDisconnectFromUser() {
        try {
            String username = activeConversation.getTalkingWithUsername();
            moveFromFriendsToUser(username, false);
            client.disconnectChat(username);
        } catch (IOException ex) {
            showAlertIOException(ex);
        }
    }
}
