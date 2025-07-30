package Helpers;

import java.util.*;
import java.util.concurrent.*;

public class Store {
    private final ConcurrentMap<String, Order> waitingArea = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Order> brewingTeaArea = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Order> brewingCoffeeArea = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Order> trayArea = new ConcurrentHashMap<>();
    private final Set<String> clientsInCafe = ConcurrentHashMap.newKeySet();

    // maximum capacity for the brewing areas.
    private static final int MAX_BREWING_TEA = 2;
    private static final int MAX_BREWING_COFFEE = 2;

    // adding new clients to the café
    public synchronized void addClient(String clientName) {
        clientsInCafe.add(clientName);
        logState();
    }

    //adds an order to the waiting area and starts the brewing process which adds order to the brewingArea
    public synchronized String addOrder(String clientName, String tea, String coffee) {
        Order existingOrder = waitingArea.get(clientName); //checks if the client already has an order

        int additionalTea = Integer.parseInt(tea);
        int additionalCoffee = Integer.parseInt(coffee);

        if (trayArea.containsKey(clientName) || waitingArea.containsKey(clientName) || brewingTeaArea.containsKey(clientName) || brewingCoffeeArea.containsKey(clientName)) {
            // if order exists, update the quantities (this changes the every ordered coffees and teas for the client)
            existingOrder.setTeaOrdered(additionalTea);
            existingOrder.setCoffeeOrdered(additionalCoffee);

            //check and start brewing based on the updated quantities
            startBrewing(existingOrder);

            logState();
            return "Updated your order: " + additionalTea + " teas and " + additionalCoffee + " coffees.";
        }
        if(!trayArea.containsKey(clientName) && !waitingArea.containsKey(clientName) && !brewingTeaArea.containsKey(clientName) && !brewingCoffeeArea.containsKey(clientName)) {
            //if no existing order, create a new order
            Order newOrder = new Order(clientName, additionalTea, additionalCoffee);
            waitingArea.put(newOrder.getClientName(), newOrder);

            //check and start brewing based on the quantities
            startBrewing(newOrder);

            logState();
            return "Order received for " + clientName + ": " + tea + " teas, " + coffee + " coffees.";
        }
        return "this should never happen";
    }

    //start brewing process with added logic to check brewing limits
    private void startBrewing(Order order) {
        int teaOrdered = order.getTeaOrdered();
        int coffeeOrdered = order.getCoffeeOrdered();

        //handle tea brewing
        while (teaOrdered > 0 && brewingTeaArea.size() >= MAX_BREWING_TEA) {
            //wait until there's space for tea in the brewing area
            try {
                wait();  //this will pause the thread until space is available
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (teaOrdered > 0 && brewingTeaArea.size() < MAX_BREWING_TEA) {
            brewingTeaArea.put(order.getClientName(), order);
            new BrewingTea(order, this).start();  // start brewing tea
        }

        // handle coffee brewing
        while (coffeeOrdered > 0 && brewingCoffeeArea.size() >= MAX_BREWING_COFFEE) {
            // wait until there's space for coffee in the brewing area
            try {
                wait();  //this will pause the thread until space is available
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (coffeeOrdered > 0 && brewingCoffeeArea.size() < MAX_BREWING_COFFEE) {
            brewingCoffeeArea.put(order.getClientName(), order);
            new BrewingCoffee(order, this).start();  //start brewing coffee
        }
    }

    //move the completed order to tray area
    public synchronized void moveToTrayArea(Order order) {
        brewingTeaArea.remove(order.getClientName());
        brewingCoffeeArea.remove(order.getClientName());
        trayArea.put(order.getClientName(), order);

        // Adjust the waiting area counts
        adjustWaitingArea(order);

        // Notify waiting threads that space is available
        notifyAll();
        logState();
    }

    //checks if the coffee is also be brewed.
    public synchronized boolean isCoffeeBrewingForClient(String clientName) {
        return brewingCoffeeArea.containsKey(clientName);
    }

    // Adjust waiting area counts after the order has been brewed and moved to the tray
    private void adjustWaitingArea(Order order) {
        Order waitingOrder = waitingArea.get(order.getClientName());
        if (waitingOrder != null) {
            int teaLeft = waitingOrder.getTeaOrdered() - order.getTeaOrdered();
            int coffeeLeft = waitingOrder.getCoffeeOrdered() - order.getCoffeeOrdered();

            // Update the order in the waiting area with the remaining teas and coffees
            waitingOrder.setTeaOrdered(teaLeft);
            waitingOrder.setCoffeeOrdered(coffeeLeft);

            if (teaLeft == 0 && coffeeLeft == 0) {
                waitingArea.remove(order.getClientName());
            }
        }
    }

    public synchronized String getOrderStatus(String clientName) {
        StringBuilder status = new StringBuilder("Order status for " + clientName + ":");

        if (waitingArea.containsKey(clientName)) {
            Order order = waitingArea.get(clientName);
            status.append("- " + order.getTeaOrdered() + " teas and " + order.getCoffeeOrdered() + " coffees in waiting area");
        }

        if (brewingTeaArea.containsKey(clientName) || brewingCoffeeArea.containsKey(clientName)) {
            Order order = brewingTeaArea.get(clientName);
            status.append("- order is being prepared. Tea: " + order.getTeaOrdered() + "Coffee: " + order.getCoffeeOrdered());
        }

        if (trayArea.containsKey(clientName)) {
            Order order = trayArea.get(clientName);
            status.append("- " + order.getTeaOrdered() + " teas and " + order.getCoffeeOrdered() + " coffees in tray area");
        }
        if(!waitingArea.containsKey(clientName) && !brewingTeaArea.containsKey(clientName) && !brewingCoffeeArea.containsKey(clientName) && !trayArea.containsKey(clientName)){
            status.append("- You have not ordered, would you like to place one? ");
        }

        return status.toString();
    }

    public synchronized String collectOrder(String clientName) {
        if (!trayArea.containsKey(clientName) && waitingArea.containsKey(clientName) || brewingTeaArea.containsKey(clientName) || brewingCoffeeArea.containsKey(clientName)) {
            return "Your order is still pending. Please wait.";
        }
        if(!trayArea.containsKey(clientName) && !waitingArea.containsKey(clientName) && !brewingTeaArea.containsKey(clientName) && !brewingCoffeeArea.containsKey(clientName)){
            logState();
            return "You have not made an order yet";
        }

        trayArea.remove(clientName);

        // Reduce the number of clients waiting for orders if their order is completed
        waitingArea.values().removeIf(order -> order.getClientName().equals(clientName));

        logState();
        return "Thank you for collecting your order!";
    }

    //client exits the café, orders are discarded
    public synchronized void clientExited(String clientName) {
        clientsInCafe.remove(clientName);
        waitingArea.remove(clientName);
        brewingTeaArea.remove(clientName);
        brewingCoffeeArea.remove(clientName);
        trayArea.remove(clientName);
        logState();
    }

    //log the current state of the café
    private void logState() {
        System.out.println("*** VC Barista log ***");
        System.out.println("Number of clients in café: " + clientsInCafe.size());
        System.out.println("Number of clients waiting for orders: " + waitingArea.size());

        System.out.println("Waiting area:");
        waitingArea.values().forEach(order -> System.out.println("[" + order.getClientName() + "]" + ":" +"- Tea: " + order.getTeaOrdered() +"\n[" + order.getClientName() + "]" + " Coffee: " + order.getCoffeeOrdered()));


        System.out.println("Brewing area:");
        brewingTeaArea.values().forEach(order -> System.out.println("[" + order.getClientName() + "]" + "- Tea: " + order.getTeaOrdered()));
        brewingCoffeeArea.values().forEach(order -> System.out.println("[" + order.getClientName() + "]" + "- Coffee: " + order.getCoffeeOrdered()));


        System.out.println("Tray area:");
        trayArea.values().forEach(order -> System.out.println("[" + order.getClientName() + "]" + ":" +"- Tea: " + order.getTeaOrdered() + "\n[" + order.getClientName() + "]" + " Coffee: " + order.getCoffeeOrdered()));
        System.out.println("**** End of Barista log ****\n");
    }
}

