import java.util.ArrayList;
import java.util.HashMap;

/**
 * Authors: Scott Salerno & Kurt Mace
 * Project: Json Server
 * Professor: Kenneth Yarnall
 * 8/4/2019
 */
import java.net.*;
import java.io.*;
import java.util.HashMap;
public class Server  {
    private static HashMap<String, Item> database = new HashMap<String, Item>();
    private static int portNum = 5050;
    /**
     * Static class to handle multiple clients
     */
    private static class clientHandler extends Thread{
      private HashMap<String,Item> database;
      private Socket socket;
      private DataOutputStream output;
      private DataInputStream input;
        public clientHandler(HashMap<String,Item> database, Socket socket, DataOutputStream output, DataInputStream input){
            this.database = database;
            this.socket = socket;
            this.output = output;
            this.input = input;

      }
      @Override
        public void run(){

      }

    }


/*

 */
    public ArrayList getItems(){
        return null;
    }
    /*

     */
    public void Purchase(String name, int count){

    }
    /*

     */
    public void Restock(String name, int count){

    }

    public static void main(String[] args) throws UnknownHostException {
       System.out.println("Server is Started, IP Address is: "+ InetAddress.getLocalHost());
       try{
           ServerSocket server = new ServerSocket(portNum);
           while(true){
               Socket client = server.accept();
               DataInputStream input = new DataInputStream(client.getInputStream());
               DataOutputStream output = new DataOutputStream(client.getOutputStream());
               clientHandler ch = new clientHandler(database,client,output,input);

               ch.start();
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
    }



}
