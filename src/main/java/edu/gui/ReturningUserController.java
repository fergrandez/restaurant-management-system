package edu.gui;

import edu.data.Employee;
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

import javax.swing.*;
import java.io.IOException;

public class ReturningUserController {
    @FXML
    private Button logInButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField employeeID;
    @FXML
    private PasswordField password;
    @FXML
    private Label logInLabel;

    //variable to store whether employee is front, back, or admin
    static String accessType = "";

    public void logInButtonOnAction(ActionEvent event) throws IOException {
        try {
            //checks to see if ID is in the database, the program won't allow you to log in if your ID is not
            if (!verifyEmployeeID(Integer.parseInt(employeeID.getText()))) {
                logInLabel.setText("Employee ID not found in database. Please try again.");
            } else { //proceed, the employee ID was registered in the database
                int index = 0; //variable to store index of employee
                for (Employee employee : Employee.employees) { //loop searching for employee based on ID entered
                    if (employee.getId() == Integer.parseInt(employeeID.getText())) {
                        index = Employee.employees.indexOf(employee);
                    }
                }
                //if the user is in the database but their password is null, it means they have not used the program
                //before, so they must proceed as a new user and create a password
                if(Employee.employees.get(index).getPassword() == null) {
                    logInLabel.setText("You haven't set up a password yet.");
                    //switching to new user log in scene
                    Parent newUserParent = FXMLLoader.load(getClass().getResource("newUser.fxml"));
                    Scene newUserScene = new Scene(newUserParent);
                    Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
                    window.setScene(newUserScene);
                    window.show();
                }else if(Employee.employees.get(index).getPassword().equals(password.getText())) { //password and ID match
                    accessType = Employee.employees.get(index).getJob();
                    //this checks to see what type of access the employee has
                    if(accessType.equals("admin")) {
                        //display admin access scene
                        Parent adminAccessParent = FXMLLoader.load(getClass().getResource("adminAccess.fxml"));
                        Scene adminAccessScene = new Scene(adminAccessParent);
                        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
                        window.setScene(adminAccessScene);
                        window.show();
                    }else {
                        //display employee access scene
                        Parent employeeAccessParent = FXMLLoader.load(getClass().getResource("employeeAccess.fxml"));
                        Scene employeeAccessScene = new Scene(employeeAccessParent);
                        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
                        window.setScene(employeeAccessScene);
                        window.show();
                    }
                }else { //if the employee id and password do not match the label will let them know
                    logInLabel.setText("Employee ID and password do not match.");
                }
            }
        } catch(NullPointerException | NumberFormatException e) {
            logInLabel.setText("Both fields are empty or contain inappropriate values.");
        }
    }

    //closes the window
    public void exitButtonOnAction(ActionEvent event) throws IOException {
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    //method to check if user inputs an employee ID registered in the data base
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
