package Helpers;

public class Order {
    private final String clientName;
    private String status;
    private String tea;
    private String coffee;
    private int coffeeOrdered;
    private int teaOrdered;

    //constructor initializes order with client name and details, status defaults to "Pending"
    public Order(String clientName, int teaOrdered, int coffeeOrdered) {
        this.teaOrdered = teaOrdered;
        this.coffeeOrdered = coffeeOrdered;
        this.clientName = clientName;
        this.status = "Pending";
    }

    public String getClientName() {
        return clientName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTea() {
        return tea;
    }

    public void setTea(String tea) {
        this.tea = tea;
    }

    public String getCoffee() {
        return coffee;
    }

    public void setCoffee(String coffee) {
        this.coffee = coffee;
    }

    public int getCoffeeOrdered() {
        return coffeeOrdered;
    }

    public void setCoffeeOrdered(int coffeeOrdered) {
        this.coffeeOrdered += coffeeOrdered;
    }

    public int getTeaOrdered() {
        return teaOrdered;
    }

    public void setTeaOrdered(int teaOrdered) {
        this.teaOrdered += teaOrdered;
    }

}


