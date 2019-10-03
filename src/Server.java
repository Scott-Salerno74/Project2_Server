import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.http.HttpResponse;
import java.util.*;

/**
 * Authors: Scott Salerno & Kurt Mace
 * Project: Json Server
 * Professor: Kenneth Yarnall
 * 8/4/2019
 */
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server  {

    private static HashMap<String, Item> database = new HashMap<String, Item>();

    public static void setDatabase(HashMap<String, Item> database) {
        Item helmet = new Item("Helmet",100.00,10);
        Item shoulder_pads = new Item("Shoulder Pads", 250.00, 20);
        Item cleats = new Item("Cleats",150.00,15);
        Item mouth_piece = new Item("Mouth Piece",20.00,100);
        database.put("Helmet", helmet);
        database.put("Shoulder Pads", shoulder_pads);
        database.put("Cleats", cleats);
        database.put("Mouth Piece", mouth_piece);


    }

    private static int portNum = 5050;

    private static final String PARAMETER_TYPE_ISSUE = "PARAMETER_TYPE_ISSUE"; // 1
    private static final String MISSING_PARAMETER = "MISSING_PARAMETER"; // 2
    private static final String PRECONDITION_VIOLATION = "PRECONDITION_VIOLATION"; // 3
    private static final String INVALID_METHOD_NAME = "INVALID_METHOD_NAME"; // 4
    private static final String INVALID_STOCK = "INVALID_STOCK"; // 5
    private static final String INVALID_ITEM_NAME = "INVALID_ITEM_NAME"; // 6


//    /**
//     * Static class to handle multiple clients
//     */
//    private static class clientHandler extends Thread{
//      private HashMap<String,Item> database;
//      private Socket socket;
//      private DataOutputStream output;
//      private DataInputStream input;
//        public clientHandler(HashMap<String,Item> database, Socket socket, DataOutputStream output, DataInputStream input){
//            this.database = database;
//            this.socket = socket;
//            this.output = output;
//            this.input = input;
//
//      }
//      @Override
//        public void run(){
//           while(true){
//
//           }
//      }
//
//    }



    /**
     * - go through database looking for any items that include filter
     *
     * - give methodResults two fields: "error" and "results"
     *      - error is either null or one of the static errors listed above
     *      - results is what goes in the "return" part of our response, can be null
     */

    public static JSONObject getItems(JSONObject[] params){
        JSONObject methodResults = new JSONObject();
        String filter = "";

        boolean filterFound = false;
        for (JSONObject obj : params) {
            if (obj.get("name").toString().equals("filter")) {
                filter = obj.get("value").toString();
                filterFound = true;
            }
        }

        if (!filterFound) {
            methodResults.put("error", MISSING_PARAMETER);
            methodResults.put("results", null);
            return methodResults;
        }

        JSONArray resultList = new JSONArray();

        //For each loop to look at items in Database &&
        for (String itemName : database.keySet()) {
            if (itemName.contains(filter)) {
                JSONObject name = new JSONObject();
                name.put("name", "name");
                name.put("type", "string");
                name.put("value", database.get(itemName).getName());

                JSONObject price = new JSONObject();
                price.put("name", "price");
                price.put("type", "double");
                price.put("value", database.get(itemName).getPrice());

                JSONObject stock = new JSONObject();
                stock.put("name", "stock");
                stock.put("type", "integer");
                stock.put("value", database.get(itemName).getStock());

                JSONArray item = new JSONArray();
                item.add(name);
                item.add(price);
                item.add(stock);

                resultList.add(item);
            }
        }


        methodResults.put("results", resultList);
        methodResults.put("error", null);

        return methodResults;
    }

    /*

     */
    public static JSONObject purchase(JSONObject[] params) {
        JSONObject methodResults = new JSONObject();
        JSONObject returnResults = new JSONObject();

        String name = "";
        int count = 0;
        int tempStock = 0;
        double finalPrice = 0.0;

        boolean nameFound = false;
        boolean countFound = false;

        for (JSONObject obj : params) {
            if (obj.get("name").toString().equals("name")) {
                name = obj.get("value").toString();
                nameFound = true;
            }
            if (obj.get("name").toString().equals("count")) {
                count = (int) obj.get("value");
                countFound = true;
            }
        }

        // catch if there are missing params
        if (!nameFound || !countFound) {
            methodResults.put("error", MISSING_PARAMETER);
            methodResults.put("results", null);
            return methodResults;
        }

        for (String itemName : database.keySet()){
            if (itemName.equalsIgnoreCase(name)) {
                tempStock = database.get(itemName).getStock();

                if (tempStock < count) {
                    methodResults.put("error", INVALID_STOCK);
                    methodResults.put("results", null);
                    return methodResults;
                }
                else {
                    database.get(itemName).setStock(tempStock - count);
                    finalPrice = database.get(itemName).getPrice() * count;

                    returnResults.put("name", "cost");
                    returnResults.put("type", "double");
                    returnResults.put("value", finalPrice);

                    methodResults.put("results", returnResults);
                    methodResults.put("error", null);
                    return methodResults;
                }
            }
        }

        // at this point, the item couldn't be found
        methodResults.put("error", INVALID_ITEM_NAME);
        methodResults.put("results", null);

        return methodResults;
    }

    /*

     */
    public static JSONObject restock(JSONObject[] params) {
        JSONObject methodResults = new JSONObject();

        String name = "";
        int count = 0;
        int tempStock = 0;

        boolean nameFound = false;
        boolean countFound = false;

        for (JSONObject obj : params) {
            if (obj.get("name").toString().equals("name")) {
                name = obj.get("value").toString();
                nameFound = true;
            }
            if (obj.get("name").toString().equals("count")) {
                count = (int) obj.get("value");
                countFound = true;
            }
        }

        // catch if there are missing parameters
        if (!nameFound || !countFound) {
            methodResults.put("error", MISSING_PARAMETER);
            methodResults.put("results", null);
            return methodResults;
        }

        if (!database.keySet().contains(name)) {
            methodResults.put("error", INVALID_ITEM_NAME);
            methodResults.put("results", null);
            return methodResults;
        }

        for (String itemName : database.keySet()) {
            if (itemName.equalsIgnoreCase(name)) {
                tempStock = database.get(itemName).getStock();
                tempStock += count;
                database.get(itemName).setStock(tempStock);

                methodResults.put("error", null);
                methodResults.put("results", null);
                return methodResults;
            }
        }

        return methodResults;
    }

    /**
     * Store database to a file
     * @throws IOException
     */
    public static void storeDatabase (ConcurrentHashMap < String, Item > database) throws IOException {
        Properties prop = new Properties();
        for (Map.Entry<String, Item> entry : database.entrySet()) {
            prop.put(entry.getKey(), entry.getValue());

        }
        prop.store(new FileOutputStream("database.properties"), null);
    }
    /**
     * Load in database
     * @throws IOException
     */
    public static ConcurrentHashMap loadDataBase () throws IOException {
        ConcurrentHashMap<String, Item> data = new ConcurrentHashMap<>();
        Properties prop = new Properties();
        prop.load(new FileInputStream("data.properties"));
        data = new ConcurrentHashMap<String, Item>(prop);
    }

    public static void main (String[]args) throws UnknownHostException {
        System.out.println("Server is Started, IP Address is: " + InetAddress.getLocalHost());
        setDatabase(database);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(portNum), 0);
            HttpContext context = server.createContext("/");
            context.setHandler(Server::handleJson);
            server.start();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void handleJson (HttpExchange httpExchange)  throws IOException {
        String methodType = httpExchange.getRequestMethod();
        System.out.println("Request Method is: " + httpExchange.getRequestMethod());
        if (methodType.equals("POST")) {
            JSONParser parser = new JSONParser();
            InputStream input = httpExchange.getRequestBody();
            int numBytes = input.available();
            byte[] bytes = new byte[numBytes];
            input.read(bytes);
            String request = new String(bytes);

            JSONObject jsonRequest;
            try {
                jsonRequest = (JSONObject) parser.parse(request);

                int id = (int) jsonRequest.get("id");
                String methodName = jsonRequest.get("methodName").toString();
                Double version = (double) jsonRequest.get("version");
                JSONObject[] params = (JSONObject[]) jsonRequest.get("params");

                JSONObject methodResults;
                // methodResults has to have two fields, error and results, no matter what happens

                switch (methodName) {
                    case "getItems":
                        methodResults = getItems(params);
                        break;
                    case "purchase":
                        methodResults = purchase(params);
                        break;
                    case "restock":
                        methodResults = restock(params);
                        break;
                    default:
                        methodResults = new JSONObject();
                        methodResults.put("error", INVALID_METHOD_NAME);
                        methodResults.put("results", null);
                }

                //Craft our response
                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put("version", version);
                jsonResponse.put("id", id);


                // TODO: add all possible error types as cases in this switch statement
                switch (methodResults.get("error").toString()) {
                    case PARAMETER_TYPE_ISSUE:
                        jsonResponse.put("status", 1);
                        jsonResponse.put("error", "Parameter Type Issue");
                        break;
                    case MISSING_PARAMETER:
                        jsonResponse.put("status", 2);
                        jsonResponse.put("error", "Missing Parameter");
                        break;
                    case PRECONDITION_VIOLATION:
                        jsonResponse.put("status", 3);
                        jsonResponse.put("error", "Precondition Violation");
                        break;
                    case INVALID_METHOD_NAME:
                        jsonResponse.put("status", 4);
                        jsonResponse.put("error", "Invalid Method Name");
                        break;
                    case INVALID_STOCK:
                        jsonResponse.put("status", 5);
                        jsonResponse.put("error", "Invalid Stock");
                        break;
                    case INVALID_ITEM_NAME:
                        jsonResponse.put("status", 6);
                        jsonResponse.put("error", "Invalid Item Name");
                        break;
                    default:
                        jsonResponse.put("status", 0);
                        jsonResponse.put("error", null);
                }

                jsonResponse.put("return", methodResults.get("results"));


                //Send a Response

                httpExchange.sendResponseHeaders(200, jsonResponse.toJSONString().getBytes().length);
                OutputStream output = httpExchange.getResponseBody();
                output.write(jsonResponse.toJSONString().getBytes());
                output.close();

            } catch (ParseException e) {
                System.out.println(e);
            }


        }


    }
}

