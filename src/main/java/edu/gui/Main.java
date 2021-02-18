package edu.gui;

import edu.data.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //connecting to data base
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/InventoryData.db");
        } catch(SQLException e) {
            System.out.println("Connection failed.");
        }

        //Creating the Statement
        Statement stmt = connection.createStatement();
        //Query to retrieve records
        String query = "Select * from Employee";
        ResultSet rs = stmt.executeQuery(query);

        //passing in employees from data base
        String employeeData[] = new String[4];
        while(rs.next()) {
            employeeData[0] = Integer.toString(rs.getInt("id"));
            employeeData[1] = rs.getString("name");
            employeeData[2] = rs.getString("position");
            employeeData[3] = rs.getString("password");

            Employee employee = new Employee(employeeData);
            Inventory.addItem(employee);
            Employee.employees.add(employee);
            //new
            System.out.println(employee.getId() + " " + employee.getPassword());
        }

        //passing in Food from data base
        query = "Select * from Food";
        rs = stmt.executeQuery(query);

        String foodData[] = new String[5];
        while(rs.next()) {
            foodData[0] = Integer.toString(rs.getInt("id"));
            foodData[1] = rs.getString("name");
            foodData[2] = Integer.toString(rs.getInt("quantity"));
            foodData[3] = Double.toString(rs.getDouble("price"));
            foodData[4] = rs.getString("expirationDate");

            Food food = new Food(foodData);

            //checking to see if food is expired and remove it from the data base if it is
            String expirationDate = food.getExpirationDate();
            LocalDate localDate = LocalDate.parse(expirationDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            if(localDate.isBefore(LocalDate.now())) {
                String sql = "DELETE FROM Food WHERE id = ?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, food.getId());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not delete item.");
                }
            } else {
                Inventory.addItem(food);
                Food.foodItems.add(food);
            }
        }

        //passing in drinks from data base
        query = "Select * from Drink";
        rs = stmt.executeQuery(query);

        String drinkData[] = new String[5];
        while(rs.next()) {
            drinkData[0] = Integer.toString(rs.getInt("id"));
            drinkData[1] = rs.getString("name");
            drinkData[2] = Integer.toString(rs.getInt("quantity"));
            drinkData[3] = Double.toString(rs.getDouble("price"));
            drinkData[4] = rs.getString("expirationDate");

            Drink drink = new Drink(drinkData);

            //checking to see if drink is expired and remove it from the data base if it is
            String expirationDate = drink.getExpirationDate();
            LocalDate localDate = LocalDate.parse(expirationDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            if(localDate.isBefore(LocalDate.now())) {
                String sql = "DELETE FROM Drink WHERE id = ?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, drink.getId());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not delete item.");
                }
            } else {
                Inventory.addItem(drink);
                Drink.drinkItems.add(drink);
            }
        }

        //passing in equipment from data base
        query = "Select * from Equipment";
        rs = stmt.executeQuery(query);

        String equipmentData[] = new String[5];
        while(rs.next()) {
            equipmentData[0] = Integer.toString(rs.getInt("id"));
            equipmentData[1] = rs.getString("name");
            equipmentData[2] = Integer.toString(rs.getInt("quantity"));
            equipmentData[3] = Double.toString(rs.getDouble("price"));
            equipmentData[4] = "N/A"; //NEW

            Equipment equipment = new Equipment(equipmentData);
            Inventory.addItem(equipment);
            Equipment.equipmentItems.add(equipment);
        }

        //Calls log in scene
        Parent root = FXMLLoader.load(getClass().getResource("returningUser.fxml"));
        primaryStage.setTitle("Inventory Program");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }


}
