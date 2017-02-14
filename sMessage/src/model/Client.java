package model;

import controller.ClientController;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;
import model.client.Message;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Client {

    Socket clientsocket;
    BufferedWriter outToServer;
    BufferedReader inFromServer;
    ClientController clientController;
    private InetAddress ip;
    private int portNr;

    public Client(ClientController clientController, String ip, int port) throws IOException {
	this.clientController = clientController;
	clientsocket = new Socket(ip, port);
	outToServer = new BufferedWriter(new PrintWriter(clientsocket.getOutputStream()));
	inFromServer = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
	receiveMessages();
    }

    private void receiveMessages() {

	new Thread(() -> {
	    String input;
	    try {
		while ((input = inFromServer.readLine()) != null) {
		    parseCommand(input);
		}
		System.out.println("Thread done");
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}).start();
    }

    public void connectChat(String userID) throws IOException {
	sendCommandToServer("TYPE 0", Command.CONNECT, userID);
    }

    public void disconnectServer() {
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

    private void sendCommandToServer(String... lines) throws IOException {

	for (int i = 0; i < lines.length - 1; i++) {
	    outToServer.write(lines[i]);
	    outToServer.newLine();
	}
	outToServer.write(lines[lines.length - 1]);
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
	if (sub[0].equals("TYPE 0")) {
	    switch (sub[1]) {
		case "CONNECT":

		    break;
		case "DISCONNECT":

		    break;
		case "USERLIST":

		    break;
		case "LOGINFAIL":

		    break;
		case "LOGINSUCCESS":

		    break;

		default:
		    throw new IllegalArgumentException("Bad protocol");
	    }
	} else if (sub[0].equals("TYPE 1")) {
	    String from = sub[1];
	    StringBuilder sb = new StringBuilder();
	    for (int i = 2; i < sub.length; i++) {
		sb.append(sub[i]);
		if (i != sub.length - 1) {
		    sb.append("\n");
		}
	    }

	    Message msg = new Message(from, sb.toString());
	    clientController.addMessageToConversation(from, msg);

	} else {
	    throw new IllegalArgumentException("Bad protocol");
	}
    }
}
