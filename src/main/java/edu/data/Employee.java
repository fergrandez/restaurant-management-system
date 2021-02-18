package edu.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Employee extends Inventory {
    //attributes
    private String itemType = "employee";
    protected String position = "none";
    protected String password = null;

    //list to store employees
    public static List<Employee> employees = new ArrayList<>();

    //constructors
    public Employee() { }

    public Employee(int id, String name, String job) {
        super(name, id);
        this.position = job;
    }

    public Employee(String[] employee) {
        super(employee);
        position = employee[2];
        password = employee[3];
    }

    //getters and setters
    public void setJob(String job) { this.position = job; }

    public String getJob() { return position; }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() { return password; }

    public String getItemType() { return itemType; }
}
