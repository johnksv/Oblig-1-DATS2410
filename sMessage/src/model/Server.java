package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author s305089
 */
public class Server {

    public void start(String[] args) throws IOException {
        int port = 5555;
        try (ServerSocket server = new ServerSocket(port);) {
            int n = 3;
            while (n-- > 0) {
                System.out.println("new! left:" + (int) (1 + n));
                SocketInstanse socketIn = new SocketInstanse(server.accept());
                socketIn.start();
            }
        }
    }
}

class SocketInstanse extends Thread {

    private final Socket socket;

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            InetAddress clientAddr = socket.getInetAddress();
            int clientPort = socket.getPort();
            String receivedText;

            while ((receivedText = in.readLine()) != null) {
                System.out.println("Client [" + clientAddr.getHostAddress() + ":" + clientPort + "] > " + receivedText);
                String outText;
                try {
                    System.out.println(receivedText);
                    outText = "Hello world (" + socket.getPort() + ")!";
                } catch (IllegalArgumentException e) {
                    System.out.println(e.toString());
                    outText = "Sorry, input error";
                }
                out.println(outText);
                System.out.println("Server  > " + outText);
            }
            System.out.println("done!");
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public SocketInstanse(Socket s) {
        socket = s;
        System.out.println("SERVER PORT: " + s.getLocalPort());
    }
}
