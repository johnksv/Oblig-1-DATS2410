package model;

import controller.ClientController;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

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


   public Client(ClientController clientController){
       this.clientController = clientController;
   }

   public void connectServer(String ip, int port) throws IOException {
       clientsocket = new Socket(ip, port);
       outToServer = new BufferedWriter(new PrintWriter(clientsocket.getOutputStream()));
       inFromServer = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
       receiveMessages();
   }

   private void receiveMessages() {

      new Thread(()->{
         String input;
          try {
              while((input = inFromServer.readLine()) != null){
                  parseInput(input);
           }
              System.out.println("Thread done");
          } catch (IOException e) {
              e.printStackTrace();
          }
      }).start();
   }

    private void parseInput(String input) {
        System.out.println("From server: " + input);
    }

    public void connectChat(String userID) throws IOException {
       executeCommand("TYPE 0", "CONNECT", userID);
    }
           
   public void disconnectServer(){}
   
   public void disconnectChat(String userID) throws IOException {
       executeCommand("TYPE 0", "DISCONNECT", userID);
   }
   
   public void getOnlineList() throws IOException {
       executeCommand("TYPE 0", "GETUSERS");
   }
   private void executeCommand(String... lines) throws IOException {

       for(int i = 0; i < lines.length-1; i++){
           outToServer.write(lines[i]);
           outToServer.newLine();
       }
       outToServer.write(lines[lines.length-1]);
       outToServer.flush();
   }

   public void regNewUser(String uname, String passord) throws IOException {

       executeCommand("TYPE 0", "REGUSER",uname, new String(Base64.getEncoder().encode(passord.getBytes())));
   }
   
   public void login(String uname, String passord) throws IOException {
       executeCommand("TYPE 0", "LOGIN",uname, new String(Base64.getEncoder().encode(passord.getBytes())));
   }
   
   public void sendMsg(String reveiverID, String msg) throws IOException {
       executeCommand("TYPE 1", reveiverID, msg);
   }

    


}
