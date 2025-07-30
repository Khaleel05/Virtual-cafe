import Helpers.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Barista {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            Store store = new Store();
            System.out.println("Barista server...");
            while (true) {
                Socket clientSocket = serverSocket.accept(); //accepts received incoming attempt to create new TCP connection from the remote client.
                ClientHandler clientHandler = new ClientHandler(clientSocket, store); //initialising ClientHandler object.
                //handle each client in a new thread
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}