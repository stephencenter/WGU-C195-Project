package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomerTableController {
    @FXML TableView<Customer> customer_table;
    @FXML TextField customer_searchbar;
    @FXML Label customer_delete_message;

    private boolean customer_confirm_delete = false;

    public void initialize() throws SQLException, ParseException {
        CreateCustomerTable();
        PopulateCustomerTable();
    }

    public void CreateCustomerTable() {
        TableColumn<Customer, Integer> id_column = new TableColumn<>("ID");
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Customer, String> name_column = new TableColumn<>("Name");
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Customer, Double> price_column = new TableColumn<>("Address");
        price_column.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, Integer> stock_column = new TableColumn<>("Zipcode");
        stock_column.setCellValueFactory(new PropertyValueFactory<>("zipcode"));

        TableColumn<Customer, Integer> minstock_column = new TableColumn<>("Phone #");
        minstock_column.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        TableColumn<Customer, Integer> maxstock_column = new TableColumn<>("Division");
        maxstock_column.setCellValueFactory(new PropertyValueFactory<>("divisionId"));

        customer_table.getColumns().add(id_column);
        customer_table.getColumns().add(name_column);
        customer_table.getColumns().add(price_column);
        customer_table.getColumns().add(stock_column);
        customer_table.getColumns().add(minstock_column);
        customer_table.getColumns().add(maxstock_column);
    }

    public void PopulateCustomerTable() throws SQLException, ParseException {
        String search_string = customer_searchbar.getText().toLowerCase();
        ObservableList<Customer> customer_list = Database.GetCustomerList();

        if (search_string.isBlank()) {
            customer_table.setItems(customer_list);
            customer_table.setPlaceholder(new Label("Database has no customers"));
            return;
        }

        // Get the list of all customers whose name contains the search_string
        Stream<Customer> filtered_by_name = customer_list.stream().filter(c -> c.getName().toLowerCase().contains(search_string));
        ObservableList<Customer> visible_customers = filtered_by_name.collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Check if there's a customer whose ID matches the search_string, and if so add it to the list
        try {
            Stream<Customer> filtered_by_id = customer_list.stream().filter(c -> c.getId() == Integer.parseInt(search_string));
            Optional<Customer> other_customer = filtered_by_id.findFirst();
            if (other_customer.isPresent() && !visible_customers.contains(other_customer.get())) {
                visible_customers.add(other_customer.get());
            }
        } catch (NumberFormatException ignored) {}

        // Populate the table with the customers that were found
        customer_table.setItems(visible_customers);
        customer_table.setPlaceholder(new Label("No customers found with that name or ID"));
    }

    public void DeleteSelectedCustomer() throws SQLException, ParseException {
        Customer selected_customer = customer_table.getSelectionModel().getSelectedItem();
        customer_delete_message.setVisible(true);

        if (selected_customer == null) {
            customer_delete_message.setText("Select a customer first");
            customer_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        if (Database.DoesCustomerHaveAppointments(selected_customer.getId())) {
            customer_confirm_delete = false;
            customer_delete_message.setText("Can't delete customer with appointments set");
            customer_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        if (!customer_confirm_delete) {
            customer_confirm_delete = true;
            customer_delete_message.setText("Click again to confirm customer deletion");
            customer_delete_message.setTextFill(Color.web("#000000"));
            return;
        }

        if (Database.DeleteCustomerWithID(selected_customer.getId())) {
            customer_delete_message.setText("Customer deleted sucessfully!");
            customer_delete_message.setTextFill(Color.web("#000000"));
        }

        else {
            customer_delete_message.setText("Failed to delete customer");
            customer_delete_message.setTextFill(Color.web("#FF0000"));
        }

        customer_confirm_delete = false;
        PopulateCustomerTable();
    }

    public void SwitchToAddCustomerForm(Event event) throws IOException {
        Parent add_customer_form = FXMLLoader.load(getClass().getResource("AddCustomerForm.fxml"));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(add_customer_form);
        the_stage.setScene(the_scene);
        the_stage.show();
    }

    public void Logout(Event event) throws IOException {
        Database.SetCurrentUser(null);
        Parent login_form= FXMLLoader.load(getClass().getResource("LoginForm.fxml"));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(login_form);
        the_stage.setScene(the_scene);
        the_stage.show();
    }

    public void ResetCustomerDeleteStatus() {
        customer_delete_message.setVisible(false);
        customer_confirm_delete = false;
    }

    public void SwitchToModifyCustomerForm(Event event) throws IOException {
        Customer selected_customer = customer_table.getSelectionModel().getSelectedItem();
        customer_delete_message.setVisible(true);

        if (selected_customer == null) {
            customer_delete_message.setText("Select a customer first");
            customer_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        Database.SetCustomerForEditing(selected_customer);
        SwitchToAddCustomerForm(event);
    }
}
