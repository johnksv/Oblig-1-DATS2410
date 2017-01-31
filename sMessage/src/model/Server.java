package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Server {

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<SocketInstanse> onlineClients = new ArrayList<>();
    ServerSocket server;
    private boolean running = true;

    public Server() throws IOException {
	server = new ServerSocket();
    }

    public Server(int port) throws IOException {
	server = new ServerSocket(port);
    }

    private boolean regNewUser(String uname, String passord) {
	for (User u : userList) {
	    if (u.getUname().equals(uname)) {
		return false;
	    }
	}

	userList.add(new User(uname, passord));
	return true;
    }

    private boolean setUserOnline(String uname, String passord) throws LoginException {

	for (User u : userList) {
	    if (u.getUname().equals(uname)) {
		u.login(passord);
		return true;
	    }
	}
	throw new LoginException("Wrong username or password.");
    }

    private void setUserOffline(String uname) {
	for (User u : userList) {
	    if (u.getUname().equals(uname)) {
		u.logOff();
		return;
	    }
	}
    }

    public boolean kickUser(String uname) {
	return false;
    }

    public void banIP(String uname) {
    }

    public void start() throws IOException {

	while (running) {
	    SocketInstanse socketIn = new SocketInstanse(server.accept());
	    socketIn.start();
	    onlineClients.add(socketIn);
	}

    }

    public void stop() throws IOException {
	running = false;
	server.close();

    }

    private class SocketInstanse extends Thread {

	private final Socket socket;
	private final BufferedWriter out;
	private boolean online = false;
	private String uname;
	private ArrayList<SocketInstanse> openConnections;

	public SocketInstanse(Socket s) throws IOException {
	    socket = s;
	    out = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
	    System.out.println("SERVER PORT: " + s.getLocalPort());
	}

	public InetAddress getIP() {
	    return socket.getInetAddress();
	}

	@Override
	public void run() {
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
		InetAddress clientAddr = socket.getInetAddress();
		int clientPort = socket.getPort();
		String receivedText;

		while ((receivedText = in.readLine()) != null) {
		    System.out.println("Client [" + clientAddr.getHostAddress() + ":" + clientPort + "] > " + receivedText);
		    parseCommand(receivedText);
		}
		System.out.println("Done! Closing socket");
		socket.close();
	    } catch (IOException e) {
		System.err.println(e.getMessage());
	    }
	}

	public void sendMsg(String uname, String msg) throws IOException {
	    sendCommandFromServer("TYPE 1", uname, msg);
	}

	public void disconnectUser(String uname) throws IOException {
	    sendCommandFromServer("TYPE 0", Command.DISCONNECT, uname);
	}

	public void getUsers() throws IOException {
	    //TODO	    
	    //sendCommandFromServer("TYPE 0", Command.GETUSERS);
	}

	private void sendCommandFromServer(String... lines) throws IOException {

	    for (int i = 0; i < lines.length - 1; i++) {
		out.write(lines[i]);
		out.newLine();
	    }
	    out.write(lines[lines.length - 1]);
	    out.flush();
	}

	private void sendCommandFromServer(String type, Command command, String... lines) throws IOException {
	    String[] newCommand = new String[lines.length + 2];
	    newCommand[0] = type;
	    newCommand[1] = command.toString();
	    System.arraycopy(lines, 0, newCommand, 2, lines.length);
	    sendCommandFromServer(newCommand);
	}

    }

    private void parseCommand(String s) {
	String[] sub = s.split("\n");
	if (sub[0].equals("TYPE 0")) {

	} else if (sub[0].equals("TYPE 1")) {
	    //TODO open
	    for (SocketInstanse partner : onlineClients) {
		if (partner.uname.equals(sub[1])) {
		    //TODO: Send rest of message

		}

	    }

	} else {
	    throw new IllegalArgumentException("Bad protocol");
	}
    }
}
