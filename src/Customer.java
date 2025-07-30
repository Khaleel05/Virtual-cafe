import java.io.*;
import java.net.*;

public class Customer {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8888);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Enter your name:");
            String name = userIn.readLine();
            out.println(name);

            String serverResponse = in.readLine();
            System.out.println(serverResponse);

            String command;
            // Client command loop
            while (true) {
                System.out.println("\nMenu:\n" +
                        "1. order <quantity> tea and <quantity> coffee e.g. order 1 tea and 1 coffee \n" +
                        "2. status\n" +
                        "3. collect\n" +
                        "4. exit\n" +
                        "Enter a command:");
                command = userIn.readLine();
                out.println(command);

                if (command.equalsIgnoreCase("exit")) {
                    serverResponse = in.readLine();
                    System.out.println(serverResponse);
                    break;
                }

                serverResponse = in.readLine();
                System.out.println(serverResponse);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}
