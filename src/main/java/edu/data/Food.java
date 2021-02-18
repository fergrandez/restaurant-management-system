package edu.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Food extends Consumables {
    //attributes
    private String itemType = "food";

    //list to store equipment items
    public static List<Food> foodItems = new ArrayList<>();

    //constructors
    public Food() { }

    public Food(int quantity, double price, String expiration) {
        super(quantity, price, expiration);
    }

    public Food(String[] foodObject) {
        super(foodObject);
    }

    //getters and setters
    public String getItemType() { return itemType; }
}
