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
import java.util.Base64;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Server {

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<SocketInstanse> onlineClients = new ArrayList<>();
    ServerSocket server;
    private boolean running = true;
    private ServerController serverController;

    public Server(ServerController serverController) throws IOException {
        this.serverController = serverController;
        server = new ServerSocket();
    }

    public Server(ServerController serverController, int port) throws IOException {
        this.serverController = serverController;
        server = new ServerSocket(port);
    }

    public boolean regNewUser(String uname, String passord) {
        for (User u : userList) {
            if (u.getUname().equals(uname)) {
                return false;
            }
        }

        userList.add(new User(uname, passord));
        return true;
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
		private  ArrayList<SocketInstanse> openConnections;

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
            StringBuilder users = new StringBuilder();

            for (SocketInstanse connections : openConnections) {
                users.append("+").append(connections.uname).append("\n");
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

        private void parseCommand(String s) throws IOException {
            String[] sub = s.split("\n");
            if (sub[0].equals("TYPE 0")) {
                switch (sub[1]) {
                    case "REGUSER":
                        regNewUser(sub[2], sub[3]);
                        break;
                    case "GETUSERS":
                        getUsers();
                        break;
                    case "LOGIN":
                        try {
                            logIn(sub);
                            sendCommandFromServer("TYPE 0", Command.LOGINSUCCESS);
                        } catch (LoginException e) {
                            sendCommandFromServer("TYPE 0", Command.LOGINFAIL);
                        }
                        break;
                    case "LOGOF":
                        logOff();
                        break;
                    case "CONNECT":
                        //Stian: Burde være sub[2] basert på github, men vet ikke om det er oppdatert
                        connectTo(sub[3]);
                        break;

                    default:
                        throw new IllegalArgumentException("Bad protocol");
                }
            } else if (sub[0].equals("TYPE 1")) {
                //TODO open
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
        //Stian: Må legge denne socketen inn i onlineClients listen
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
