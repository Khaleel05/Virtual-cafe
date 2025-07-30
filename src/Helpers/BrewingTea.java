package Helpers;

public class BrewingTea extends Thread {
    private final Order order;
    private final Store store;

    public BrewingTea(Order order, Store store) {
        this.order = order;
        this.store = store;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(30000); //simulate brewing tea for 30 seconds
            order.setStatus("Completed");

            //check if there's a coffee order being brewed for the same client
            if (store.isCoffeeBrewingForClient(order.getClientName())) {
                System.out.println(order.getClientName() + "'s tea has finished brewing.");
            } else {
                store.moveToTrayArea(order); //move the order to tray area
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

