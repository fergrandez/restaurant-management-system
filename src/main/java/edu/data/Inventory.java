package edu.data;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class Inventory { //implements CSVParser {
    //attributes
    protected String name = "none";
    protected int id = 0;
    //list to store inventory objects
    protected static List<Inventory> inventory = new ArrayList<>();

    //constructors
    public Inventory() { }

    public Inventory(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Inventory(String[] inventoryObject) {
        this.id = Integer.parseInt(inventoryObject[0]);
        this.name = inventoryObject[1]; //EDITED
    }

    //getters and setters
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public static List<Inventory> getInventory() { return inventory; }

    //method to add an item to the inventory list
    public static void addItem(Inventory item) {
        inventory.add(item);
    }

    //method to delete an item from the inventory list
    public static void deleteItem(Inventory item) {
        inventory.remove(item);
    }
}
