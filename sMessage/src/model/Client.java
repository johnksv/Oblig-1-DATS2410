package model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Client {

   Socket clientsocket;
   DataOutputStream outToServer;
   BufferedReader inFromServer;
   private InetAddress ip;
   private int portNr;
    
   public void connectServer(String ip, int port){}
   
   public void connectChat(String userID){}
           
   public void disconnectServer(){}
   
   public void disconnectChat(){}
   
   public String[] getOnlineList(){return null;}
   
   public boolean regNewUser(String uname, String passord){return false;}
   
   public void login(String uname, String passord){}
   
   public void sendMsg(){}
   
   public void getMsg(){}
    
    


}
