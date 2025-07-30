package Helpers;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Store store;

    //Constructor initializing client socket and store reference
    public ClientHandler(Socket clientSocket, Store store) {
        this.clientSocket = clientSocket;
        this.store = store;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String clientName = in.readLine();  // get client name
            store.addClient(clientName);
            out.println("Hi, what can I do for you today " + clientName);

            String command;
            while ((command = in.readLine()) != null) {
                if (command.equalsIgnoreCase("exit")) {
                    store.clientExited(clientName);
                    out.println("Goodbye, " + clientName + "!");
                    break;
                } else if (command.startsWith("order")) {
                    //parse the order and decide whether it's tea or coffee
                    String[] orderDetails = command.replace("order", "").split(" ");
                    try{
                        String tea = orderDetails[1];
                        String coffee = orderDetails[4];
                        if (tea == null || coffee == null) {
                            out.println("sorry what was that ");
                            continue;
                        }

                        String response = store.addOrder(clientName, tea, coffee);
                        out.println(response);
                    }catch(Exception e){
                        out.println("sorry what was that ");
                    }

                } else if (command.equalsIgnoreCase("status")) {
                    String response = store.getOrderStatus(clientName);
                    out.println(response);
                } else if (command.equalsIgnoreCase("collect")) {
                    String response = store.collectOrder(clientName);
                    out.println(response);
                } else {
                    out.println("I don't understand can you use a command from the menu, please retry.");
                }
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        }
    }
}