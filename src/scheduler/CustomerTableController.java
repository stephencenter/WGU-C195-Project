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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomerTableController {
    @FXML TableView<Customer> customer_table;
    @FXML TextField customer_searchbar;
    @FXML Label customer_delete_message;

    private boolean customer_confirm_delete = false;

    /**
     * This method is called when the CustomerTableForm loads.
     * It is responsible for creating and populating the customer table
     * @throws SQLException Populating the table can potentially throw an exception when querying the database
     */
    public void initialize() throws SQLException {
        CreateCustomerTable();
        PopulateCustomerTable();
    }

    /**
     * This method creates the columns for the customer table, including
     * headers and the functions that cell data will be retrieved with
     */
    public void CreateCustomerTable() {
        TableColumn<Customer, Integer> id_column = new TableColumn<>("ID");
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Customer, String> name_column = new TableColumn<>("Name");
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Customer, String> address_column = new TableColumn<>("Address");
        address_column.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, String> zipcode_column = new TableColumn<>("Zipcode");
        zipcode_column.setCellValueFactory(new PropertyValueFactory<>("zipcode"));

        TableColumn<Customer, String> phonenum_column = new TableColumn<>("Phone #");
        phonenum_column.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        TableColumn<Customer, String> country_column = new TableColumn<>("Country");
        country_column.setCellValueFactory(new PropertyValueFactory<>("countryName"));

        TableColumn<Customer, String> division_column = new TableColumn<>("Division");
        division_column.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

        customer_table.getColumns().add(id_column);
        customer_table.getColumns().add(name_column);
        customer_table.getColumns().add(address_column);
        customer_table.getColumns().add(phonenum_column);
        customer_table.getColumns().add(country_column);
        customer_table.getColumns().add(division_column);
    }

    /**
     * This method queries the database for a list of all customers and then inserts them into our
     * Customer table
     * @throws SQLException Querying the database could cause an exception
     */
    public void PopulateCustomerTable() throws SQLException {
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

        if (!selected_customer.GetAppointments().isEmpty()) {
            customer_confirm_delete = false;
            customer_delete_message.setText("Can't delete customer with appointments set");
            customer_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        // If the user has already clicked the delete button once before, then we delete the customer.
        // Otherwise, we set a flag that indicates they've now clicked the button once, and the
        // next click should delete the customer
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

    /**
     * This method is called when hitting the add button. It switches the current form
     * to the AddCustomerForm so the user can add a new customer to the database
     * @param event a JavaFX event
     * @throws IOException Attempting to laod the new form could raise an IOException
     */
    public void SwitchToAddCustomerForm(Event event) throws IOException {
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AddCustomerForm.fxml")));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.show();
    }

    /**
     * This method is called when hitting the log out button. It sets the current user to null
     * and then switches back to the login screen so the user can login as a different user
     * @param event a JavaFX event
     * @throws IOException Attempting to laod the new form could raise an IOException
     */
    public void Logout(Event event) throws IOException {
        Database.SetCurrentUser(null);
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginForm.fxml")));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.show();
    }

    /**
     * This method is called when clicking off the current customer to a different one.
     * Deleting a customer requires clicking the delete button twice to prevent accidental deletions.
     * The code in this method resets this two-click requirement
     */
    public void ResetCustomerDeleteStatus() {
        customer_delete_message.setVisible(false);
        customer_confirm_delete = false;
    }

    /**
     * This method is called when the modify button is pressed. It switches the current form
     * to the AddCustomerForm with a flag set that tells the form to pre-fill out the textfields and
     * comboboxes with the selected customer's information. It also tells the form to update the customer
     * in the database instead of creating a new customer
     * @param event a JavaFX event
     * @throws IOException Attempting to laod the new form could raise an IOException
     */
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

    public void SwitchToAppointmentForm(Event event) throws IOException {
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AppointmentTableForm.fxml")));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.show();
    }
}
