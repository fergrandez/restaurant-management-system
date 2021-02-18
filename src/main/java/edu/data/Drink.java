package edu.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Drink extends Consumables {
    //attributes
    private String itemType = "drink";

    //list to store drink items
    public static List<Drink> drinkItems = new ArrayList<>();

    //constructors
    public Drink() { }

    public Drink(int quantity, double price, String expirationDate) {
        super(quantity, price, expirationDate);
    }

    public Drink(String[] drinkObject) {
        super(drinkObject);
    }

    //getters and setters
    public String getItemType() { return itemType; }
}
