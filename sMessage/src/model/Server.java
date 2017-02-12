package model;

import controller.ServerController;

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

    private final ArrayList<User> userList = new ArrayList<>();
    private final ArrayList<SocketInstanse> onlineClients = new ArrayList<>();
    ServerSocket server;
    private boolean running = true;
    private ServerController serverController;

    public Server(ServerController serverController) throws IOException {
	this.serverController = serverController;
	server = new ServerSocket();
	start();
    }

    public Server(ServerController serverController, int port) throws IOException {
	this.serverController = serverController;
	server = new ServerSocket(port);
	start();
    }

    public boolean regNewUser(String uname, String passord) {
	for (User u : userList) {
	    if (u.getUname().equals(uname)) {
		return false;
	    }
	}

	User user = new User(uname, passord, true);
	userList.add(user);
	serverController.update(Command.REGUSER, user);
	return true;
    }

    public boolean kickUser(String uname) {
	return false;
    }

    public void banIP(String uname) {
    }

    private void start() throws IOException {
	new Thread(() -> {
	    try {
		while (running) {
		    SocketInstanse socketIn = new SocketInstanse(server.accept());
		    socketIn.start();
		    onlineClients.add(socketIn);
		}
	    } catch (IOException ex) {
		Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}).start();

    }

    public void stop() throws IOException {
	running = false;
	server.close();

    }

    private class SocketInstanse extends Thread {

	private final Socket socket;
	private final BufferedWriter out;
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

		//TODO: Denne gir feil. vi kan ikke bruke \n. Kanskje vi heller må bruke ; for å skille
		while ((receivedText = in.readLine()) != null) {
		    System.out.println("Client [" + clientAddr.getHostAddress() + ":" + clientPort + "] > " + receivedText);
		    parseCommand(receivedText);
		}
		System.out.println("Done! Closing socket");
		socket.close();
		onlineClients.remove(this);

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

	public void sendUsers() throws IOException {
	    StringBuilder users = new StringBuilder();

	    for (User u : userList) {
		if (!u.isOnline()) {
		    users.append("0").append(u.getUname()).append("\n");
		} else if (u.isBusy()) {
		    users.append("-").append(u.getUname()).append("\n");
		} else {
		    users.append("+").append(u.getUname()).append("\n");
		}
	    }

	    sendCommandFromServer("TYPE 0", Command.USERLIST, users.toString());
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

	private void sendUpdateToAll(String type, Command command, String... lines) throws IOException {
	    String[] newCommand = new String[lines.length + 2];
	    newCommand[0] = type;
	    newCommand[1] = command.toString();
	    System.arraycopy(lines, 0, newCommand, 2, lines.length);
	    for (SocketInstanse user : onlineClients) {
		for (int i = 0; i < lines.length - 1; i++) {
		    user.out.write(lines[i]);
		    user.out.newLine();
		}
		user.out.write(lines[lines.length - 1]);
		user.out.flush();
	    }
	}

	private void parseCommand(String s) throws IOException {
	    String[] sub = s.split(";");
	    if (sub[0].equals("TYPE 0")) {
		switch (sub[1]) {
		    case "REGUSER":
			if (regNewUser(sub[2], sub[3])) {
			    uname = sub[2];
			} else {
			    sendCommandFromServer("TYPE 0", Command.ERROR, "Could not create user");
			}

			break;
		    case "GETUSERS":
			sendUsers();
			break;
		    case "LOGIN":
			try {
			    logIn(sub);
			    sendCommandFromServer("TYPE 0", Command.LOGINSUCCESS);
			    sendUpdateToAll("TYPE 0", Command.STATUSUPDATE, "uname", "+");
			} catch (LoginException e) {
			    sendCommandFromServer("TYPE 0", Command.LOGINFAIL);
			}
			break;
		    case "LOGOFF":
			sendUpdateToAll("TYPE 0", Command.STATUSUPDATE, "uname", "0");
			logOff();
			break;
		    case "CONNECT":
			connectTo(sub[2]);
			break;

		    default:
			throw new IllegalArgumentException("Bad protocol");
		}
	    } else if (sub[0].equals("TYPE 1")) {
		for (SocketInstanse partner : openConnections) {
		    if (partner.uname.equals(sub[1])) {
			StringBuilder msg = new StringBuilder();
			for (int i = 2; i < sub.length; i++) {
			    msg.append(sub[i]);
			}
			try {
			    partner.sendMsg(uname, msg.toString());
			} catch (IOException e) {
			    serverController.printWarning(uname + " could not send message to " + partner.uname);
			}
			break;
		    }

		}

	    } else {
		throw new IllegalArgumentException("Bad protocol");
	    }
	}

	private void connectTo(String s) {
	    // TODO
	}

	//Stian: hmm tror kanksje dette ble tull, og vi har en online variabel, men den trengs nok ikke
	//Tror kanskje at vi må fjærne socketen fra onlineClients og så sende en disconnect til alle vi snakker med 
	//og etter det fjærne de fra listen openConnections om jeg tror skal være de denne clienten snakker med nå
	private void logOff() {
	    for (User u : userList) {
		if (u.getUname().equals(uname)) {
		    u.logOff();
		    for (SocketInstanse connection : openConnections) {
			for (int i = 0; i < connection.openConnections.size(); i++) {
			    if (connection.openConnections.get(i).uname.equals(uname)) {
				connection.openConnections.remove(i);
				break;
			    }
			}
		    }
		}
	    }
	}

	private void logIn(String[] sub) throws LoginException {
	    for (User u : userList) {
		if (u.getUname().equals(sub[2])) {
		    u.login(sub[3]);
		    uname = sub[2];
		    return;
		}
	    }
	    throw new LoginException("Wrong username or password.");
	}

    }
}
