package edu.gui;

import edu.data.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewUserController {
    @FXML
    private Button logInButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField employeeID;
    @FXML
    private PasswordField password;
    @FXML
    private Label logInLabel;

    //variable to hold whether employee is front of house, back of house, or admin
    static String accessType = "";

    //method to connect to data base
    private Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/InventoryData.db");
        } catch (SQLException e) {
            System.out.println("Connection failed.");
        }
        return connection;
    }

    public void logInButtonOnAction(ActionEvent event) throws IOException {
        try {
            //checks to see if ID is in the database, the program won't allow you to log in if your ID is not
            if (!verifyEmployeeID(Integer.parseInt(employeeID.getText()))) {
                logInLabel.setText("Employee ID not found in database. Please try again.");
            } else { //this means the user is in the data base but hasn't used the program before or set up a password
                int index = 0; //variable to store index of employee
                for (Employee employee : Employee.employees) { //loop searching for employee
                    if (employee.getId() == Integer.parseInt(employeeID.getText())) {
                        index = Employee.employees.indexOf(employee);
                    }
                }
                boolean isCorrect = false; //variable that keeps track of whether the password is formatted correct
                boolean hasLowercase = false; //variable that holds if the password has a lowercase character
                boolean hasUppercase = false; //variable that holds if the password has an uppercase character
                boolean hasNumber = false; //variable that holds if the password has a number
                boolean hasInappropriateCharacters = false; //variable that holds if the password has inappropriate characters

                //arrays containing the alphabet in lowercase and uppercase and an array containing
                //numbers, we will use these arrays to compare to the password created by the user to see
                //if it meets the requirements
                char[] lowercase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
                char[] uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                char[] numbers = "0123456789".toCharArray();
                char[] specialCharacters = "!.,@#".toCharArray();

                //array containing the characters of the password the user entered
                char[] passwordCharacters = password.getText().toCharArray();

                //checking if the password has lowercase characters
                for (char c : passwordCharacters) {
                    for (char l : lowercase) {
                        if (c == l) {
                            hasLowercase = true;
                            break;
                        }
                    }
                }
                //checking if the password has uppercase characters
                for (char c : passwordCharacters) {
                    for (char u : uppercase) {
                        if (c == u) {
                            hasUppercase = true;
                            break;
                        }
                    }
                }
                //checking if the password has a number
                for (char c : passwordCharacters) {
                    for (char n : numbers) {
                        if (c == n) {
                            hasNumber = true;
                            break;
                        }
                    }
                }

                //checking if the password has any inappropriate special characters
                int counter = 0;
                for (char c : passwordCharacters) {
                    for (char n : numbers) {
                        if (c == n) {
                            counter++;
                        }
                    }
                    for (char l : lowercase) {
                        if (c == l) {
                            counter++;
                        }
                    }
                    for (char u : uppercase) {
                        if (c == u) {
                            counter++;
                        }
                    }
                    for (char s : specialCharacters) {
                        if (c == s) {
                            counter++;
                        }
                    }
                }
                if (counter != password.getText().length()) {
                    hasInappropriateCharacters = true;
                } else {
                    hasInappropriateCharacters = false;
                }

                //if the password meets all requirements proceed
                if (password.getText().length() >= 8 && hasLowercase && hasUppercase && hasNumber
                        && !hasInappropriateCharacters) {
                    //setting the employees password
                    Employee.employees.get(index).setPassword(password.getText());
                    // TODO: 2/16/2021

                    String sql = "UPDATE Employee SET password = ? WHERE id = ?";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, password.getText());
                        statement.setInt(2, Integer.parseInt(employeeID.getText()));
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not update item.");
                    }

                    //getting the employees access type to determine which scene to display
                    accessType = Employee.employees.get(index).getJob();
                    if(accessType.equals("admin")) { //if they're an admin give them access to admin scene
                        //display admin access scene
                        Parent adminAccessParent = FXMLLoader.load(getClass().getResource("adminAccess.fxml"));
                        Scene adminAccessScene = new Scene(adminAccessParent);
                        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
                        window.setScene(adminAccessScene);
                        window.show();
                    } else { //if they're not admin give them access to the regular employee scene
                        //loading the employee access scene
                        Parent employeeAccessParent = FXMLLoader.load(getClass().getResource("employeeAccess.fxml"));
                        Scene employeeAccessScene = new Scene(employeeAccessParent);
                        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        window.setScene(employeeAccessScene);
                        window.show();
                    }
                } else { //if the password does not meet the requirements the label will let them know
                        logInLabel.setText("Your password did not meet the requirements.");
                    }
                }
            } catch(NullPointerException | NumberFormatException e) {
            logInLabel.setText("Both fields are empty or contain inappropriate values.");
        }
    }

    //method to take you back to the original log in screen
    public void backButtonOnAction(ActionEvent event) throws IOException {
        //loading original log in screen
        Parent logInParent = FXMLLoader.load(getClass().getResource("returningUser.fxml"));
        Scene logInScene = new Scene(logInParent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(logInScene);
        window.show();
    }

    //method to check if user inputs an existing employee ID
    public static boolean verifyEmployeeID(int id) {
        boolean idInUse = false;
        for(Employee employee : Employee.employees) {
            if(employee.getId() == id) {
                idInUse = true;
                break;
            }
        }
        if(idInUse) {
            return true;
        }else {
            return false;
        }
    }
}
