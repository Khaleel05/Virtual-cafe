package Helpers;

public class BrewingCoffee extends Thread {
    private final Order order;
    private final Store store;

    public BrewingCoffee(Order order, Store store) {
        this.order = order;
        this.store = store;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(45000); // Simulate brewing coffee for 45 seconds
            order.setStatus("Completed");
            store.moveToTrayArea(order); // Move the order to tray area
            System.out.println(order.getClientName() + "'s coffee has finished brewing.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
