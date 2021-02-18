package edu.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Equipment extends Consumables {
    //attributes
    private String itemType = "equipment";

    //list to store equipment items
    public static List<Equipment> equipmentItems = new ArrayList<>();

    //constructors
    public Equipment() { }

    public Equipment( int quantity, double price) {
        super(quantity, price, "N/A");
    }

    public Equipment(String[] equipmentObject) {
        super(equipmentObject);
    }

    //getters and setters
    public String getItemType() { return itemType; }
}
