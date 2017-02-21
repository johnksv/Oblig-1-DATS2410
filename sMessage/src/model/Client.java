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
import model.client.Status;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Client {

    private Socket clientsocket;
    private BufferedWriter outToServer;
    private BufferedReader inFromServer;
    private ClientController clientController;
    private LoginClientController loginController;
    private boolean loggedin = false;

    /**
     * 
     * @param loginController
     * @param clientController
     * @param ip
     * @param port
     * @throws IOException
     */
    public Client(LoginClientController loginController, ClientController clientController, String ip, int port) throws IOException {
        this.clientController = clientController;
        this.loginController = loginController;
        clientsocket = new Socket(ip, port);
        outToServer = new BufferedWriter(new PrintWriter(clientsocket.getOutputStream()));
        inFromServer = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        receiveMessages();
    }

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
                    Platform.runLater(() -> {
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
                    Platform.runLater(() -> {
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

    public void connectChat(String userID) throws IOException {
        sendCommandToServer("TYPE 0", Command.CONNECT, userID);
    }

    public void disconnectServer() throws IOException {
        System.out.println("Loging off and shuting down socket.");
        sendCommandToServer("TYPE 0", Command.LOGOFF);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            try {
                shutdown();
            } catch (IOException ex) {
                System.err.println("Could not close socket: " + ex.toString());
            }
        });

    }

    public void shutdown() throws IOException {
        clientsocket.shutdownInput();
        clientsocket.shutdownOutput();
        outToServer.close();
        inFromServer.close();
        //TODO: Check if we should warn the server that we are closing first, then wait.
        clientsocket.close();
    }

    public void disconnectChat(String userID) throws IOException {
        sendCommandToServer("TYPE 0", Command.DISCONNECT, userID);
    }

    public void getUserList() throws IOException {
        sendCommandToServer("TYPE 0", Command.GETUSERS);
    }

    public void regNewUser(String uname, String passord) throws IOException {
        sendCommandToServer("TYPE 0", Command.REGUSER, uname, new String(Base64.getEncoder().encode(passord.getBytes())));
    }

    public void login(String uname, String passord) throws IOException {
        sendCommandToServer("TYPE 0", Command.LOGIN, uname, new String(Base64.getEncoder().encode(passord.getBytes())));
    }

    public void sendMsg(String receiverID, String msg) throws IOException {
        sendCommandToServer("TYPE 1", receiverID, msg);
    }

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
