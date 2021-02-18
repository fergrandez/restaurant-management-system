package edu.gui;

import edu.data.Drink;
import edu.data.Equipment;
import edu.data.Food;
import edu.data.Inventory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class EmployeeAccessController {
    @FXML private Button logOutButton;
    @FXML private Label inventoryLabel;
    @FXML private Label accessLabel;
    @FXML private Label editLabel;
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

    //variables to hold user access types from both log in screens (new and returning)
    //depending on which one they used to log in
    String returningAccessType = ReturningUserController.accessType;
    String newAccessType = NewUserController.accessType;

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

    //method that displays all food items on the table when the food menu item is clicked
    public void foodOnAction(ActionEvent event) {
        resetScene();
        //displaying food items
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("itemType"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Food, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Food, Integer>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Food, Double>("price"));
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("expirationDate"));
        tableView.setItems(getFood());

        //determining whether or not employee can edit these items based on access type
        if(hasBackAccess()) {
            editItem();
        } else {
            editLabel.setText("CANNOT EDIT");
        }
    }

    //method that displays all drink items on the table when the drink menu item is clicked
    public void drinkOnAction(ActionEvent event) {
        resetScene();
        //displaying drink items
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<Drink, String>("itemType"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Drink, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Drink, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Drink, Integer>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Drink, Double>("price"));
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<Drink, String>("expirationDate"));
        tableView.setItems(getDrinks());

        //determining whether or not employee can edit these items based on access type
        if(hasBackAccess()) {
            editItem();
        } else {
            editLabel.setText("CANNOT EDIT");
        }
    }

    //method to display all equipment items on the table when the equipment menu item is clicked
    public void equipmentOnAction(ActionEvent event) {
        resetScene();
        //displaying all equipment items
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<Equipment, String>("itemType"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Equipment, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Equipment, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Equipment, Integer>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Equipment, Double>("price"));
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<Equipment, String>("expirationDate"));
        tableView.setItems(getEquipment());

        //determining if employee can edit these items based on access type
        if(!hasBackAccess()) {
            editItem();
        } else {
            editLabel.setText("CANNOT EDIT");
        }
    }

    //method to edit a selected item's ID
    public void changeIDCellEvent(TableColumn.CellEditEvent editID) {
        //getting the selected item
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();

        if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                int newId = (int) editID.getNewValue();
                //checking to see if new ID entered is already in use
                if(!isDuplicateID(newId)) { //if the new ID entered is original proceed
                    int oldId = 0;
                    for (Food food : Food.foodItems) {
                        if(tableView.getSelectionModel().getSelectedItem() == food) {
                            oldId = food.getId();
                            food.setId(newId); //setting the item's ID to the new ID
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
                //checking to see if new ID entered is already in use
                if(!isDuplicateID(newId)) { //if the new ID entered is original proceed
                    int oldId = 0;
                    for (Drink drink : Drink.drinkItems) {
                        if(tableView.getSelectionModel().getSelectedItem() == drink) {
                            oldId = drink.getId();
                            drink.setId(newId); //setting the item's ID to the new ID
                            break;
                        }
                    }
                    //updating item in the data base
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
                //checking to see if new ID entered is already in use
                if(!isDuplicateID(newId)) { //if the new ID entered is original proceed
                    int oldId = 0;
                    for (Equipment equipment : Equipment.equipmentItems) {
                        if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                            oldId = equipment.getId();
                            equipment.setId(newId); //setting the item's ID to the new ID
                            break;
                        }
                    }
                    //updating item in the data base
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

    //method to edit a selected item's name
    public void changeNameCellEvent(TableColumn.CellEditEvent editName) {
        //getting the object selected
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();

        if(itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                String newName = editName.getNewValue().toString(); //getting the new name entered
                int id = 0;
                for (Food food : Food.foodItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == food) {
                        id = food.getId();
                        food.setName(newName); //setting the item's name to the new name
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Food SET name = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, newName);
                    statement.setInt(2,id);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (Exception e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else if(itemSelected.getClass().toString().substring(15).equalsIgnoreCase("drink")) {
            try {
                String newName = editName.getNewValue().toString(); //getting the new name entered
                int id = 0;
                for (Drink drink : Drink.drinkItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == drink) {
                        id = drink.getId();
                        drink.setName(newName); //setting the item's name to the new name
                        break;
                    }
                }
                //updating data base
                String sql = "UPDATE Drink SET name = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, newName);
                    statement.setInt(2,id);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (Exception e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        } else {
            try {
                String newName = editName.getNewValue().toString(); //getting the new name entered
                int id = 0;
                for (Equipment equipment : Equipment.equipmentItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                        id = equipment.getId();
                        equipment.setName(newName); //setting the item's name to the new name
                        break;
                    }
                }
                //updating item in data base
                String sql = "UPDATE Equipment SET name = ? WHERE id = ?";
                try (Connection connection = this.connect()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, newName);
                    statement.setInt(2,id);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Could not update item.");
                }
            } catch (Exception e) {
                errorLabel.setText("You entered an inappropriate value for that field.");
            }
        }
    }

    //method to edit a selected item's quantity
    public void changeQuantityCellEvent(TableColumn.CellEditEvent editQuantity) {
        //getting the object selected
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();
        if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                int newQuantity = (int) editQuantity.getNewValue(); //getting new quantity entered
                int itemId = 0;
                for (Food food : Food.foodItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == food) {
                        itemId = food.getId();
                        food.setQuantity(newQuantity); //setting the quantity to the new quantity
                        break;
                    }
                }
                //updating the item in the data base
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
                int newQuantity = (int) editQuantity.getNewValue(); //getting new quantity entered
                int itemId = 0;
                for (Drink drink : Drink.drinkItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == drink) {
                        itemId = drink.getId();
                        drink.setQuantity(newQuantity); //setting the quantity to the new quantity
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
                int newQuantity = (int) editQuantity.getNewValue(); //getting new quantity entered
                int itemId = 0;
                for (Equipment equipment : Equipment.equipmentItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                        itemId = equipment.getId();
                        equipment.setQuantity(newQuantity); //setting the quantity to the new quantity
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

    //method to change a selected item's price
    public void changePriceCellEvent(TableColumn.CellEditEvent editPrice) {
        //getting the object selected
        Object itemSelected = tableView.getSelectionModel().getSelectedItem();

        if (itemSelected.getClass().toString().substring(15).equalsIgnoreCase("food")) {
            try {
                double newPrice = (double) editPrice.getNewValue(); //getting new price entered
                int itemId = 0;
                for (Food food : Food.foodItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == food) {
                        itemId = food.getId();
                        food.setPrice(newPrice); //setting the price to the new price
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
                double newPrice = (double) editPrice.getNewValue(); //getting new price entered
                int itemId = 0;
                for (Drink drink : Drink.drinkItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == drink) {
                        itemId = drink.getId();
                        drink.setPrice(newPrice); //setting the price to the new price
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
                double newPrice = (double) editPrice.getNewValue(); //getting new quantity entered
                int itemId = 0;
                for (Equipment equipment : Equipment.equipmentItems) {
                    if(tableView.getSelectionModel().getSelectedItem() == equipment) {
                        itemId = equipment.getId();
                        equipment.setPrice(newPrice); //setting the price to the new price
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

    //method to log out and return to original log in scene
    public void logOutButtonOnAction (ActionEvent event) throws IOException {
        Parent logInParent = FXMLLoader.load(getClass().getResource("returningUser.fxml"));
        Scene logInScene = new Scene(logInParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(logInScene);
        window.show();
    }

    //method that returns true if the employee currently logged in has access to back of house
    //and false if they have access to front of house
    public boolean hasBackAccess() {
        if(returningAccessType.equalsIgnoreCase("back") || newAccessType.equalsIgnoreCase("back")) {
            accessLabel.setText("ACCESS TYPE: BACK");
            return true;
        } else  {
            accessLabel.setText("ACCESS TYPE: FRONT");
            return false;
        }
    }

    //method that allows the user to edit an item's properties
    public void editItem() {
        tableView.setEditable(true);
        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
    }

    //method that checks if item ID is already in use so that two items don't have the same ID
    public static boolean isDuplicateID(int id) {
        boolean idInUse = false;
        for(Inventory item : Inventory.getInventory()) {
            if(item.getId() == id) {
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

    //method to reset labels and whether the table can be edited
    public void resetScene() {
        editLabel.setText("");
        errorLabel.setText("Double click the property of an item you would like to edit to change it.");
        tableView.setEditable(false);
    }

    //method that returns an observable list of food items that we can display on the table
    public static ObservableList<Food> getFood() {
        ObservableList<Food> foodItems = FXCollections.observableArrayList();
        for (Food food : Food.foodItems) {
            foodItems.add(food);
        }
        return foodItems;
    }

    //method that returns an observable list of drink items that we can display on the table
    public static ObservableList<Drink> getDrinks() {
        ObservableList<Drink> drinks = FXCollections.observableArrayList();
        for (Drink drink : Drink.drinkItems) {
            drinks.add(drink);
        }
        return drinks;
    }

    //method that returns an observable list of equipment items that we can display on the table
    public static ObservableList<Equipment> getEquipment() {
        ObservableList<Equipment> equipmentItems = FXCollections.observableArrayList();
        for (Equipment equipment : Equipment.equipmentItems) {
            equipmentItems.add(equipment);
        }
        return equipmentItems;
    }
}
