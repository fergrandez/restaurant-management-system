package edu.gui;

import edu.data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class AdminAccessController implements Initializable {
    @FXML private Button logOutButton;
    @FXML private Button addItemButton;
    @FXML private Button deleteItemButton;
    @FXML private Button addEmployeeButton;
    @FXML private Button deleteEmployeeButton;
    @FXML private TextField employeeName;
    @FXML private TextField employeeID;
    @FXML private TextField employeePosition;
    @FXML private TextField itemName;
    @FXML private TextField itemID;
    @FXML private TextField itemQuantity;
    @FXML private TextField itemPrice;
    @FXML private DatePicker itemExpirationDate;
    @FXML private Label messageLabel;
    @FXML private Label errorLabel;
    @FXML private MenuItem foodMenuItem;
    @FXML private MenuItem drinkMenuItem;
    @FXML private MenuItem equipmentMenuItem;
    @FXML private TableView tableView;
    @FXML private TableColumn itemTypeColumn;
    @FXML private TableColumn idColumn;
    @FXML private TableColumn nameColumn;
    @FXML private TableColumn quantityColumn;
    @FXML private TableColumn priceColumn;
    @FXML private TableColumn expirationDateColumn;
    @FXML private TableView<Employee> employeeTableView;
    @FXML private TableColumn<Employee, Integer> employeeIDColumn;
    @FXML private TableColumn<Employee, String> employeeNameColumn;
    @FXML private TableColumn<Employee, String> employeePositionColumn;

    //string to store item's type (food, equipment, drink)
    static String itemType = "none";

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

    //method to initialize employee table to display all employees as soon as admin scene opens
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
        employeeIDColumn.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
        employeePositionColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("job"));
        employeeTableView.setItems(getEmployees());

        //makes employee table editable
        editEmployee();
    }

    //displaying all food items on the inventory table when the food menu item is clicked
    public void foodOnAction(ActionEvent event) {
        resetLabel();
        itemType = "food";

        //displaying food items
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("itemType"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Food, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Food, Integer>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Food, Double>("price"));
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("expirationDate"));
        tableView.setItems(EmployeeAccessController.getFood());

        //making the items table editable
        editInventoryItem();
    }

    //displaying all drink items on the inventory table when the drink menu item is clicked
    public void drinkOnAction(ActionEvent event) {
        resetLabel();
        itemType = "drink";

        //displaying drink items
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<Drink, String>("itemType"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Drink, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Drink, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Drink, Integer>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Drink, Double>("price"));
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<Drink, String>("expirationDate"));
        tableView.setItems(EmployeeAccessController.getDrinks());

        //making the items table editable
        editInventoryItem();
    }

    //displaying all equipment items on the inventory table when the equipment menu item is clicked
    public void equipmentOnAction(ActionEvent event) {
        resetLabel();
        itemType = "equipment";

        //displaying equipment items
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<Equipment, String>("itemType"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Equipment, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Equipment, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Equipment, Integer>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Equipment, Double>("price"));
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<Equipment, String>("expirationDate"));
        tableView.setItems(EmployeeAccessController.getEquipment());

        //making the items table editable
        editInventoryItem();
    }

    //method to edit a selected item's price and save the changes in the data base as well as update
    //the inventory table to display the changes
    public void changeItemIDCellEvent(TableColumn.CellEditEvent editID) {
        //getting the selected item
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();

        if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                int newId = (int) editID.getNewValue();
                //only sets the new ID entered if it is not already in use
                if(!EmployeeAccessController.isDuplicateID(newId)) {
                    int oldId = 0;
                    for (Food food : Food.foodItems) {
                        if(tableView.getSelectionModel().getSelectedItem() == food) {
                            oldId = food.getId();
                            food.setId(newId);
                            break;
                        }
                    }
                    //updating item in data base
                    String sql = "UPDATE Food SET id = ? WHERE id = ?";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, newId);
                        statement.setInt(2,oldId);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not update item.");
                    }
                    errorLabel.setText("Successfully edited item!");
                } else {
                    errorLabel.setText("You entered an ID that is already in use please try again.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("drink")) {
            try {
                int newId = (int) editID.getNewValue();
                //only sets the new ID entered if it is not already in use
                if(!EmployeeAccessController.isDuplicateID(newId)) {
                    int oldId = 0;
                    for (Drink drink : Drink.drinkItems) {
                        if(tableView.getSelectionModel().getSelectedItem() == drink) {
                            oldId = drink.getId();
                            drink.setId(newId);
                            break;
                        }
                    }
                    //updating item in data base
                    String sql = "UPDATE Drink SET id = ? WHERE id = ?";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, newId);
                        statement.setInt(2,oldId);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not update item.");
                    }
                    errorLabel.setText("Successfully edited item!");
                } else {
                    errorLabel.setText("You entered an ID that is already in use please try again.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else {
            try {
                int newId = (int) editID.getNewValue();
                //only sets the new ID entered if it is not already in use
                if(!EmployeeAccessController.isDuplicateID(newId)) {
                    int oldId = 0;
                    for (Equipment equipment : Equipment.equipmentItems) {
                        if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                            oldId = equipment.getId();
                            equipment.setId(newId);
                            break;
                        }
                    }
                    //updating item in data base
                    String sql = "UPDATE Equipment SET id = ? WHERE id = ?";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, newId);
                        statement.setInt(2,oldId);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not update item.");
                    }
                    errorLabel.setText("Successfully edited item!");
                } else {
                    errorLabel.setText("You entered an ID that is already in use please try again.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        }
    }

    //method to edit a selected item's name and save the changes in the data base as well as update
    //the inventory table to display the changes
    public void changeItemNameCellEvent(TableColumn.CellEditEvent editName) {
        //getting the item selected
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();
        if(itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                String newName = editName.getNewValue().toString();
                String oldName = "";
                for (Food food : Food.foodItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == food) {
                        oldName = food.getName();
                        food.setName(newName);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Food SET name = ? WHERE name = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, newName);
                    statement.setString(2,oldName);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (Exception e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else if(itemSelected.getClass().toString().substring(15).equalsIgnoreCase("drink")) {
            try {
                String newName = editName.getNewValue().toString();
                String oldName = "";
                for (Drink drink : Drink.drinkItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == drink) {
                        oldName = drink.getName();
                        drink.setName(newName);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Drink SET name = ? WHERE name = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, newName);
                    statement.setString(2,oldName);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (Exception e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else {
            try {
                String newName = editName.getNewValue().toString();
                String oldName = "";
                for (Equipment equipment : Equipment.equipmentItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                        oldName = equipment.getName();
                        equipment.setName(newName);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Equipment SET name = ? WHERE name = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, newName);
                    statement.setString(2,oldName);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (Exception e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        }
    }

    //method to edit a selected item's quantity and save the changes in the data base as well as update
    //inventory table to display the changes
    public void changeItemQuantityCellEvent(TableColumn.CellEditEvent editQuantity) {
        //getting the item selected
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();
        if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                int newQuantity = (int) editQuantity.getNewValue();
                int itemId = 0;
                for (Food food : Food.foodItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == food) {
                        itemId = food.getId();
                        food.setQuantity(newQuantity);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Food SET quantity = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, newQuantity);
                    statement.setInt(2, itemId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("drink")) {
            try {
                int newQuantity = (int) editQuantity.getNewValue();
                int itemId = 0;
                for (Drink drink : Drink.drinkItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == drink) {
                        itemId = drink.getId();
                        drink.setQuantity(newQuantity);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Drink SET quantity = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, newQuantity);
                    statement.setInt(2, itemId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else {
            try {
                int newQuantity = (int) editQuantity.getNewValue();
                int itemId = 0;
                for (Equipment equipment : Equipment.equipmentItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                        itemId = equipment.getId();
                        equipment.setQuantity(newQuantity);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Equipment SET quantity = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, newQuantity);
                    statement.setInt(2,itemId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        }
    }

    //method to edit a selected item's price and save the changes in the data base as well as update
    //the inventory table to display the changes
    public void changeItemPriceCellEvent(TableColumn.CellEditEvent editPrice) {
        //getting the selected item
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();
        if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                double newPrice = (double) editPrice.getNewValue();
                int itemId = 0;
                for (Food food : Food.foodItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == food) {
                        itemId = food.getId();
                        food.setPrice(newPrice);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Food SET price = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setDouble(1, newPrice);
                    statement.setInt(2, itemId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("drink")) {
            try {
                double newPrice = (double) editPrice.getNewValue();
                int itemId = 0;
                for (Drink drink : Drink.drinkItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == drink) {
                        itemId = drink.getId();
                        drink.setPrice(newPrice);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Drink SET price = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setDouble(1, newPrice);
                    statement.setInt(2,itemId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else {
            try {
                double newPrice = (double) editPrice.getNewValue();
                int itemId = 0;
                for (Equipment equipment : Equipment.equipmentItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                        itemId = equipment.getId();
                        equipment.setPrice(newPrice);
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Equipment SET price = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setDouble(1, newPrice);
                    statement.setInt(2,itemId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        }
    }

    //method that allows the user to edit a selected employee's ID to an ID that is not currently
    //being used and saves the changes in the data base and displays the changes on the employee
    //table
    public void changeEmployeeIDCellEvent(TableColumn.CellEditEvent editID) {
        Employee employeeSelected = employeeTableView.getSelectionModel().getSelectedItem();
        try {
            int newId = (int) editID.getNewValue();
            if(!EmployeeAccessController.isDuplicateID(newId)) {
                int oldId = 0;
                for (Employee employee : Employee.employees) {
                    if(employeeSelected == employee) {
                        oldId = employee.getId();
                        employee.setId(newId);
                        break;
                    }
                }
                //updating employee in data base
                String sql = "UPDATE Employee SET id = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, newId);
                    statement.setInt(2,oldId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
                errorLabel.setText("Successfully edited ID!");
            } else {
                messageLabel.setText("You entered an ID that is already in use please try again.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("You entered an inappropriate value for that field.");
        }
    }

    //method that allows the user to edit a selected employee's name and saves the changes in the data base
    //and displays the changes on the employee table
    public void changeEmployeeNameCellEvent(TableColumn.CellEditEvent editName) {
        Employee employeeSelected = employeeTableView.getSelectionModel().getSelectedItem();
        try {
            String newName = editName.getNewValue().toString();
            int id = 0;
            for (Employee employee : Employee.employees) {
                if(employeeSelected == employee) {
                    id = employee.getId();
                    employee.setName(newName);
                    break;
                }
            }
            //updating employee in data base
            String sql = "UPDATE Employee SET name = ? WHERE id = ?";
            try (Connection connection = this.connect()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, newName);
                statement.setInt(2,id);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Could not update item.");
            }
        } catch (Exception e) {
            messageLabel.setText("You entered an inappropriate value for that field.");
        }
    }

    //method that allows the user to edit a selected employee's position to front, back, or admin
    //and saves the changes in the data base and displays the changes on the employee table
    public void changeEmployeePositionCellEvent(TableColumn.CellEditEvent editName) {
        Employee employeeSelected = employeeTableView.getSelectionModel().getSelectedItem();
        try {
            String newPosition = editName.getNewValue().toString();
            if(!newPosition.equals("back") && !newPosition.equals("front") && !newPosition.equals("admin")) {
                messageLabel.setText("Please enter either 'back', 'front', or 'admin' for the position field.");
            } else {
                int id = 0;
                for (Employee employee : Employee.employees) {
                    if(employeeSelected == employee) {
                        id = employee.getId();
                        employee.setJob(newPosition);
                        break;
                    }
                }
                //updating employee in data base
                String sql = "UPDATE Employee SET position = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, newPosition);
                    statement.setInt(2,id);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            }
        } catch (Exception e) {
            messageLabel.setText("You entered an inappropriate value for that field.");
        }
    }

    //method that deletes selected item(s) from the data base and removes the item(s) from the
    //inventory table
    public void deleteItemButtonOnAction(ActionEvent event) {
        if (itemType.equals("food")) {
            ObservableList<Food> selectedRows, allFoodItems;
            allFoodItems = tableView.getItems();
            //getting item(s) selected from inventory table to delete
            selectedRows = tableView.getSelectionModel().getSelectedItems();

            if (selectedRows.size() < 1) {
                errorLabel.setText("No item(s) were selected.");
            } else {
                for (Food food : selectedRows) {
                    //deleting selected item(s) from data base
                    String sql = "DELETE FROM Food WHERE id = ?;";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, food.getId());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not delete item.");
                    }
                    allFoodItems.remove(food);
                    Food.foodItems.remove(food);
                    Inventory.deleteItem(food);
                }
                errorLabel.setText("Successfully deleted selected food item(s)!");
            }
        } else if (itemType.equals("drink")) {
            ObservableList<Drink> selectedRows, allDrinks;
            allDrinks = tableView.getItems();
            //getting item(s) selected from inventory table to delete
            selectedRows = tableView.getSelectionModel().getSelectedItems();

            if (selectedRows.size() < 1) {
                errorLabel.setText("No item(s) were selected.");
            } else {
                for (Drink drink : selectedRows) {
                    //deleting selected item(s) from data base
                    String sql = "DELETE FROM Drink WHERE id = ?;";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, drink.getId());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not delete item.");
                    }
                    allDrinks.remove(drink);
                    Drink.drinkItems.remove(drink);
                    Inventory.deleteItem(drink);
                }
                errorLabel.setText("Successfully deleted selected drink(s)!");
            }
        } else {
            ObservableList<Equipment> selectedRows, allEquipment;
            allEquipment = tableView.getItems();
            //getting item(s) selected from inventory table to delete
            selectedRows = tableView.getSelectionModel().getSelectedItems();
            if(selectedRows.size() < 1) {
                errorLabel.setText("No item(s) were selected.");
            } else {
                for (Equipment equipment : selectedRows) {
                    //deleting selected item(s) from data base
                    String sql = "DELETE FROM Equipment WHERE id = ?;";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, equipment.getId());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not delete item.");
                    }
                    allEquipment.remove(equipment);
                    Equipment.equipmentItems.remove(equipment);
                    Inventory.deleteItem(equipment);
                }
                errorLabel.setText("Successfully deleted selected equipment item(s)!");
            }
        }
    }

    //method that deletes selected employee(s) from the data base and removes the item(s) from
    //the employee table
    public void deleteEmployeeButtonOnAction(ActionEvent event) {
        ObservableList<Employee> selectedRows, allEmployees;
        allEmployees = employeeTableView.getItems();
        //getting employee(s) selected from employee table to delete
        selectedRows = employeeTableView.getSelectionModel().getSelectedItems();

        for (Employee employee : selectedRows) {
            //deleting selected employee(s) from data base
            String sql = "DELETE FROM Employee WHERE id = ?;";
            try (Connection connection = this.connect()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, employee.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Could not delete item.");
            }
            allEmployees.remove(employee);
            Employee.employees.remove(employee);
            Inventory.deleteItem(employee);
            messageLabel.setText("Successfully deleted " + employee.getName().toUpperCase() + " from employee roster!");
        }
    }

    //method that takes what the user inputted in the employee textfields (name, ID, position)
    //and adds a new employee with those properties to the data base and updates the employee
    //table to show the new employee
    public void addEmployeeButtonOnAction(ActionEvent event) {
        try {
            int newEmployeeID = Integer.parseInt(employeeID.getText());
            String newEmployeeName = employeeName.getText();
            String newEmployeePosition = employeePosition.getText();
            //employee must have a unique ID and a valid position; either front, back, or admin
            if(!EmployeeAccessController.isDuplicateID(newEmployeeID) && (newEmployeePosition.equals("back")
            || newEmployeePosition.equals("front") || newEmployeePosition.equals("admin"))) {
                //if the new employee attributes entered meet the requirements, add the new employee to the data base
                String sql = "INSERT INTO Employee (id, name, position, password) " +
                        "VALUES (?, ?, ?, ?);";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, newEmployeeID);
                    statement.setString(2,newEmployeeName);
                    statement.setString(3,newEmployeePosition);
                    statement.setString(4,null);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
                Employee newEmployee = new Employee(newEmployeeID, newEmployeeName, newEmployeePosition);

                Employee.employees.add(newEmployee);
                Inventory.addItem(newEmployee);
                employeeTableView.getItems().add(newEmployee);
                messageLabel.setText("Successfully added " + newEmployeeName.toUpperCase() + " to employee roster!");
                //resetting text fields
                employeeName.setText(null);
                employeeID.setText(null);
                employeePosition.setText(null);
            } else {
                messageLabel.setText("You entered an ID that is already in use or an invalid position. Please try again.");
            }
        } catch (NullPointerException | NumberFormatException e){
            messageLabel.setText("One or more fields are empty or you inputted an inappropriate value.");
        }
    }

    //method that takes what the user inputted in the item textfields (name, ID, quantity, price, exp. date)
    //and adds a new item with those properties to the data base and updates the inventory table to
    //display the new item
    public void addItemButtonOnAction(ActionEvent event) {
        if(itemType.equals("food")) {
            try {
                int newItemID = Integer.parseInt(itemID.getText());
                String newItemName = itemName.getText();
                int newItemQuantity = Integer.parseInt(itemQuantity.getText());
                double newItemPrice = Double.parseDouble(itemPrice.getText());
                //formatting the expiration date entered to a string
                LocalDate expirationDate = itemExpirationDate.getValue();

                String formattedDate = expirationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

                //if the new item entered has a unique ID and a valid expiration date then allow it to be added to the data base
                if(!EmployeeAccessController.isDuplicateID(newItemID) && !expirationDate.isBefore(LocalDate.now())) {
                    //adding item to data base
                    String sql = "INSERT INTO Food (id, name, quantity, price, expirationDate) " +
                            "VALUES (?, ?, ?, ?, ?);";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, newItemID);
                        statement.setString(2,newItemName);
                        statement.setInt(3,newItemQuantity);
                        statement.setDouble(4,newItemPrice);
                        statement.setString(5,formattedDate);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not update item.");
                    }
                    Food newFoodItem = new Food(newItemQuantity, newItemPrice, formattedDate);
                    newFoodItem.setName(newItemName);
                    newFoodItem.setId(newItemID);

                    Food.foodItems.add(newFoodItem);
                    Inventory.addItem(newFoodItem);
                    tableView.getItems().add(newFoodItem);
                    errorLabel.setText("Successfully added new food item!");
                } else {
                    errorLabel.setText("You entered an ID that is already in use or an invalid date. Please try again.");
                }
            } catch (NullPointerException | NumberFormatException e){
                errorLabel.setText("One or more fields are empty or you inputted an inappropriate value.");
            }
        } else if(itemType.equals("drink")) {
            try {
                int newItemID = Integer.parseInt(itemID.getText());
                String newItemName = itemName.getText();
                int newItemQuantity = Integer.parseInt(itemQuantity.getText());
                double newItemPrice = Double.parseDouble(itemPrice.getText());
                //formatting the expiration date entered to a string
                LocalDate expirationDate = itemExpirationDate.getValue();
                String formattedDate = expirationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

                //if the new item entered has a unique ID and a valid expiration date then allow it to be added to the data base
                if(!EmployeeAccessController.isDuplicateID(newItemID) && !expirationDate.isBefore(LocalDate.now())) {
                    //adding item to data base
                    String sql = "INSERT INTO Drink (id, name, quantity, price, expirationDate) " +
                            "VALUES (?, ?, ?, ?, ?);";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, newItemID);
                        statement.setString(2,newItemName);
                        statement.setInt(3,newItemQuantity);
                        statement.setDouble(4,newItemPrice);
                        statement.setString(5,formattedDate);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not update item.");
                    }
                    Drink newDrink = new Drink(newItemQuantity, newItemPrice, formattedDate);
                    newDrink.setName(newItemName);
                    newDrink.setId(newItemID);

                    Drink.drinkItems.add(newDrink);
                    Inventory.addItem(newDrink);
                    tableView.getItems().add(newDrink);
                    errorLabel.setText("Successfully added new drink!");
                } else {
                    errorLabel.setText("You entered an ID that is already in use. Please try again.");
                }
            } catch (NullPointerException | NumberFormatException e){
                errorLabel.setText("One or more fields are empty or you inputted an inappropriate value.");
            }
        } else if(itemType.equals("equipment")){
            try {
                int newItemID = Integer.parseInt(itemID.getText());
                String newItemName = itemName.getText();
                int newItemQuantity = Integer.parseInt(itemQuantity.getText());
                double newItemPrice = Double.parseDouble(itemPrice.getText());

                //if the new equipment item entered has a unique ID then allow it to be added to the data base
                if(!EmployeeAccessController.isDuplicateID(newItemID)) {
                    //adding item to data base
                    String sql = "INSERT INTO Equipment (id, name, quantity, price) " +
                            "VALUES (?, ?, ?, ?);";
                    try (Connection connection = this.connect()) {
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setInt(1, newItemID);
                        statement.setString(2,newItemName);
                        statement.setInt(3,newItemQuantity);
                        statement.setDouble(4,newItemPrice);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Could not update item.");
                    }
                    Equipment newEquipment = new Equipment(newItemQuantity, newItemPrice);
                    newEquipment.setName(newItemName);
                    newEquipment.setId(newItemID);

                    Equipment.equipmentItems.add(newEquipment);
                    Inventory.addItem(newEquipment);
                    tableView.getItems().add(newEquipment);
                    errorLabel.setText("Successfully added new equipment!");
                } else {
                    errorLabel.setText("You entered an ID that is already in use. Please try again.");
                }
            } catch (NullPointerException | NumberFormatException e){
                errorLabel.setText("One or more fields are empty or you inputted an inappropriate value.");
            }
        } else {
            errorLabel.setText("You have not selected an inventory to add an item to.");
        }
    }

    //method that takes the user back to the original log in scene
    public void logOutButtonOnAction (ActionEvent event) throws IOException {
        Parent logInParent = FXMLLoader.load(getClass().getResource("returningUser.fxml"));
        Scene logInScene = new Scene(logInParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(logInScene);
        window.show();
    }

    //method that allows the user to edit an item's properties
    public void editInventoryItem() {
        tableView.setEditable(true);
        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    //method that allows the user to edit an employee's properties
    public void editEmployee() {
        employeeTableView.setEditable(true);
        employeeNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        employeeIDColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        employeePositionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        employeeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    //method that returns an observable list of employee items that we can display on the employee table
    public static ObservableList<Employee> getEmployees() {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        for (Employee employee : Employee.employees) {
            employees.add(employee);
        }
        return employees;
    }

    //method to reset labels and text fields in the scene
    public void resetLabel() {
        //resets label
        errorLabel.setText("Double click the property of an item or employee you would like to edit to change it.");
        //resets text fields
        itemName.setText(null);
        itemID.setText(null);
        itemQuantity.setText(null);
        itemPrice.setText(null);
        itemExpirationDate.setValue(null);
    }
}
