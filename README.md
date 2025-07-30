# Virtual Café System

## Description
The Virtual Café System is a virtual simulation of a café where clients (customers) can place orders for tea and coffee, check the status of their orders, collect their items, and exit the cafe whenever they want. This system consists of multiple components: a Client (Customer), a Server (Barista), and an Order class to manage the items ordered by customers.

The system allows for:
- Placing orders consisting of tea and coffee.
- Checks for the clients order status 
- Collecting the clients order 
- Exiting the cafe.
- Interactive communication between the client and server.

The Client (Customer) communicates with the server (Barista) through a socket-based system. The server processes the order and response to the client with an update.

---

## Notable Features
- **Multithreaded Server**: Handles multiple client connections concurrently. 
- **Customer Interaction**: Customers can place orders, check the status of their orders, and collect items.
- **Brewing Simulation**: simulating brewing times for tea (30 seconds) and coffee (45second).
- **Brewing Orders Concurrently**: It will run the brewing tea thread and the brewing coffee thread when they are placed in the same order. 
- **Active Communication**: The customer and server communicate in real time. simulating an interactive experience.  
- **Barista Log State**: The Barista will provide a log of the current state of the shop for monitoring. 

---

## Instructions

### Prerequisites
To run this system, you need:
- **Java Development Kit (JDK)** version 8 or higher installed on your machine.
- The Terminal or a text editor or IDE to edit and compile Java files (e.g., IntelliJ IDEA or  any text editor with Java support).

### Steps to Compile and Run

1. **Compile the java files for the Barista server**:
    Open a terminal or command prompt in the project directory and compile all Java files:
    ```Terminal:
    javac -cp "." Barista.java
    ```

2. **Run the Server (Barista)**:
    Open a new terminal window and run the BaristaServer (server) program. This will listen for incoming connections from customers.
    ```Terminal
    java -cp "." Barista
    ```

3. **Compile the java files for Customer server (Client)**:
    Open another terminal window and run the Customer (client) program. You can start interacting with the café by placing orders.
    ```Terminal
    javac -cp "." Customer
    ```
4. **Run the Customer (Client)**:
   Open another terminal window and run the Customer (client) program. You can start interacting with the café by placing orders.
    ```Terminal
    java -cp "." Customer
    ```

---

## Known Issues & Limitations
- **Oreder Parsing**: The command parsing for orders is simplistic and does not handle various formats.
- **Status command**: the command was changed from Order status to Status. 
- **No Error Handling for Invalid Commands**: The system does not currently provide detailed feedback for invalid commands entered by the customer.
- **Server Greeting Message**: The customer greeting message was implement on the client side. 
- **Handling Multiple Orders**: When a client makes a new order it would update the customer order in all the area instead of placing in the waiting area. 
- **Order Handling**: Doesn't split the order into in individual items.  

---

## Future Enhancements
- **Flexible Parsing**: Implement a more sophisticated command parser to handle various order formats
- **Order handling**: Accumulating Orders, Quantity Updates, Order Tracking
- **Brewing area constraints**: in each of the brewing areas there should not be more than 2 tea or 2 coffees in each.  
- **Proactive Messaging**: Modify the server to send messages to clients independently of client requests.

---
