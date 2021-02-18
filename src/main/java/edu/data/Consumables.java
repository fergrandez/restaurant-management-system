package edu.data;

public abstract class Consumables extends Inventory {
    //attributes
    protected double price = 0;
    protected String expirationDate = "";
    protected int quantity = 0;

    //constructors
    public Consumables() { }

    public Consumables(int quantity, double price, String expirationDate) {
        this.quantity = quantity;
        this.price = price;
        this.expirationDate = expirationDate;
    }

    public Consumables(String[] consumableObject) {
        super(consumableObject);
        quantity = Integer.parseInt(consumableObject[2]);
        price = Double.parseDouble(consumableObject[3]);
        expirationDate = consumableObject[4];
    }

    //getters and setters
    public void setPrice(double price) { this.price = price; }

    public double getPrice() { return price; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getQuantity() { return quantity; }

    public void setExpirationDate(String date) { expirationDate = date; }

    public String getExpirationDate() {
        return expirationDate;
    }
}
