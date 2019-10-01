import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.http.HttpResponse;
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
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
           while(true){

           }
      }

    }


/*

 */
    public static JSONObject getItems(JSONObject[] params){
        return null;
    }
    /*

     */
    public static void purchase(JSONObject[] params){

    }
    /*

     */
    public static  void restock(JSONObject[] params){

    }

    public static void main(String[] args) throws UnknownHostException {
       System.out.println("Server is Started, IP Address is: "+ InetAddress.getLocalHost());
       try{
           HttpServer server = HttpServer.create(new InetSocketAddress(portNum),0);
           HttpContext context = server.createContext("/");
           context.setHandler(Server::handleJson);
           server.start();
//           while(true){
//               Socket client = server.accept();
//               DataInputStream input = new DataInputStream(client.getInputStream());
//               DataOutputStream output = new DataOutputStream(client.getOutputStream());
//               clientHandler ch = new clientHandler(database,client,output,input);
//
//               ch.start();
//           }
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    private static void handleJson(HttpExchange httpExchange)  throws IOException{
           String methodType = httpExchange.getRequestMethod();
           System.out.println("Request Method is: " + httpExchange.getRequestMethod());
           if(methodType.equals("POST")){
               JSONParser parser = new JSONParser();
               InputStream input = httpExchange.getRequestBody();
               int numBytes = input.available();
               byte[] bytes = new byte[numBytes];
               input.read(bytes);
               String request = new String(bytes);

               JSONObject jsonRequest;
               try{
                   jsonRequest = (JSONObject)parser.parse(request);
                   int id = (int)jsonRequest.get("id");
                   String methodName =  jsonRequest.get("methodName").toString();
                   Double version = (double) jsonRequest.get("version");
                   JSONObject[] params = (JSONObject[])jsonRequest.get("params");
                   JSONObject response = null;
                   switch (methodName){
                       case "getItems":
                           response = getItems(params);
                           break;
                       case "purchase":
                           purchase(params);
                           break;
                       case "restock":
                           restock(params);
                           break;
                   }
                   //Craft our response
                   JSONObject jsonResponse = new JSONObject();
                   jsonResponse.put("version",version);
                   jsonResponse.put("id",id);
                   jsonResponse.put("status",0);
                   jsonResponse.put("return",response);
                   jsonResponse.put("error",400);

                   //Send a Response
                   if(response != null){
                     httpExchange.sendResponseHeaders(200,response.toJSONString().getBytes().length);
                     OutputStream output = httpExchange.getResponseBody();
                     output.write(response.toJSONString().getBytes());
                     output.close();
                   }
                   else{
                       httpExchange.sendResponseHeaders(210,0);
                   }
               } catch (ParseException e) {
                   e.printStackTrace();
               }


           }


    }


}
