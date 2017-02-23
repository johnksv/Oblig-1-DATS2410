package model;

import controller.ClientController;
import controller.LoginClientController;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import model.client.Message;

/**
 * Client class for communicating with a {@link Server server}.
 * @author Member(1-2-3-4)
 */
public class Client {


    private final Socket clientsocket;
    private final BufferedWriter outToServer;
    private final BufferedReader inFromServer;
    private final ClientController clientController;
    private final LoginClientController loginController;
    public boolean loggedin = false;

    /**
     * Constructor for the client class.
     * Starts a new thread to listen for server messages.
     * @param loginController Is for communicating between the controllers.
     * @param clientController Is for communicating between the controllers.
     * @param ip Server IP
     * @param port Server port
     * @throws IOException if an I/O error occurs.
     */
    public Client(LoginClientController loginController, ClientController clientController, String ip, int port) throws IOException {
        this.clientController = clientController;
        this.loginController = loginController;
        clientsocket = new Socket(ip, port);
        outToServer = new BufferedWriter(new PrintWriter(clientsocket.getOutputStream()));
        inFromServer = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        receiveMessages();
    }

    /**
     * Sends a status update to the server.
     * @param status The new status.
     * @throws IOException if an I/O error occurs.
     */
    public void sendStatusUpdate(Status status) throws IOException {
        String newStatus = status == Status.ONLINE ? "+" : "-";
        sendCommandToServer("TYPE 0", Command.STATUSUPDATE, newStatus);
    }

    private void receiveMessages() {

        Thread th = new Thread(() -> {
            String input;
            try {
                while ((input = inFromServer.readLine()) != null) {
                    
                    final String finalInput = input;
                    //The parsing and actions should be done on the JavaFX thread
                    Platform.runLater(() -> {
                        try {
                            parseCommand(finalInput);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                }
                if (loggedin) {
		    shutdown();
                    Platform.runLater(() -> {
			System.out.println("Shutdown line 77");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Server closed");
                        alert.setHeaderText("Server shutdown occurred.");
                        alert.showAndWait();
                        Platform.exit();
                        System.exit(-1);
                    });
                }
                System.out.println("Thread done");
            } catch (SocketException e) {
                if (loggedin) {
		    try {
			shutdown();
		    } catch (IOException ignored) {
		    }
		    Platform.runLater(() -> {
			System.out.println("Shutdown line 947");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Server closed");
                        alert.setHeaderText("Server shutdown occurred.");
                        alert.showAndWait();
                        Platform.exit();
                        System.exit(-1);
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        th.setDaemon(true);
        th.start();
    }

    /**
     * Asks the server to connect to the user userID.
     * @param userID is the username of the user you want to connect to.
     * @throws IOException if an I/O error occurs.
     */
    public void connectChat(String userID) throws IOException {
        sendCommandToServer("TYPE 0", Command.CONNECT, userID);
    }

    /**
     * Disconnects the this {@link Client client} from the {@link Server server} .
     * @throws IOException if an I/O error occurs.
     */
    public void disconnectServer() throws IOException {
        System.out.println("Loging off and shuting down socket.");
        sendCommandToServer("TYPE 0", Command.LOGOFF);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            try {
                shutdown();
            } catch (IOException ex) {
                System.err.println("Could not close socket: " + ex.toString());
            }
        });

    }

    /**
     * Shuts down all input and output from this {@link Client client}.
     * @throws IOException if an I/O error occurs.
     */
    public void shutdown() throws IOException {

        clientsocket.shutdownOutput();
        clientsocket.close();
    }

    /**
     * Disconnects the client from the user with the username userID.
     * @param userID is the name of the user you want to disconnect from.
     * @throws IOException if an I/O error occurs.
     */
    public void disconnectChat(String userID) throws IOException {
        sendCommandToServer("TYPE 0", Command.DISCONNECT, userID);
    }

    /**
     * Asks the {@link Server server} for a complete list of all the users.
     * @throws IOException if a I/O error occurs.
     */
    public void getUserList() throws IOException {
        sendCommandToServer("TYPE 0", Command.GETUSERS);
    }

    /**
     * Registers a new user
     * @param uname Is the username of the new user.
     * @param passord Is the password for the new user.
     * @throws IOException If a I/O error occurs.
     */
    public void regNewUser(String uname, String passord) throws IOException {
        sendCommandToServer("TYPE 0", Command.REGUSER, uname, new String(Base64.getEncoder().encode(passord.getBytes())));
    }

    /**
     * Tries to log in with username and password.
     * @param uname The username that is trying to log in.
     * @param passord The password for the Username.
     * @throws IOException If an I/O error occurs
     */
    public void login(String uname, String passord) throws IOException {
        sendCommandToServer("TYPE 0", Command.LOGIN, uname, new String(Base64.getEncoder().encode(passord.getBytes())));
    }

    /**
     * Sends a message to a user.
     * @param receiverID The username of the receiver.
     * @param msg The message to be sent.
     * @throws IOException If an I/O error occurs.
     */
    public void sendMsg(String receiverID, String msg) throws IOException {
        sendCommandToServer("TYPE 1", receiverID, msg);
    }

    /**
     * Sends a response to a CONNECT request from another user.
     * @param username The name of the reciever.
     * @param respons The response, always yes or no.
     * @throws IOException if an I/O error occurs.
     */
    public void sendRespons(String username, String respons) throws IOException {
        sendCommandToServer("TYPE 0", Command.RESPONSE, username, respons.toUpperCase());
    }

    private void sendCommandToServer(String... lines) throws IOException {

        for (int i = 0; i < lines.length - 1; i++) {
            outToServer.write(lines[i]);
            outToServer.write(";");
        }
        outToServer.write(lines[lines.length - 1]);
        outToServer.newLine();
        outToServer.flush();
    }

    private void sendCommandToServer(String type, Command command, String... lines) throws IOException {
        String[] newCommand = new String[lines.length + 2];
        newCommand[0] = type;
        newCommand[1] = command.toString();
        System.arraycopy(lines, 0, newCommand, 2, lines.length);
        sendCommandToServer(newCommand);
    }

    private void parseCommand(String cmd) throws IOException {
        String[] sub = cmd.split(";");
        System.out.println(cmd);
        if (sub[0].equals("TYPE 0")) {
            switch (sub[1]) {
                case "CONNECT":
                    clientController.connectRequest(restOfArray(sub, 2));
                    break;
                case "RESPONSE":
                    if (sub[3].toUpperCase().equals("YES")) {
                        clientController.moveFromUsersToFriends(sub[2], true);
                    } else {
                        clientController.negativeResponse(sub[2]);
                    }
                    break;
                case "DISCONNECT":
                    clientController.moveFromFriendsToUser(restOfArray(sub, 2), true);
                    break;
                case "USERLIST":
                    clientController.updateUserList(restOfArray(sub, 2));
                    break;
                case "LOGINFAIL":
                    loginController.loginFailed(sub[2]);
                    break;
                case "LOGINSUCCESS":
                    loggedin = true;
                    loginController.loginSuccess();
                    break;
                case "STATUSUPDATE":
                    clientController.updateStatus(sub[2], sub[3]);
                    break;
                case "ERROR":
                    clientController.showError(restOfArray(sub, 2));
                    break;
                case "REGUSERFAIL":
                    loginController.regUserFailed();
                    break;
                default:
                    throw new IllegalArgumentException("Bad protocol");
            }
        } else if (sub[0].equals("TYPE 1")) {
            String from = sub[1];

            Message msg = new Message(from, restOfArray(sub, 2));
            clientController.addMessageToConversation(from, msg);

        } else {
            throw new IllegalArgumentException("Bad protocol");
        }
    }

    private String restOfArray(String[] sub, int from) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < sub.length; i++) {
            sb.append(sub[i]);
            if (i != sub.length - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
