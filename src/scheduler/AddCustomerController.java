package scheduler;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is attached to the AddCustomerForm. Its methods are called when the user interacts
 * with that form. Its responsible for handling logic involving verifying the information entered
 * into the form and sending that information to the database for inserting or updating
 */
public class AddCustomerController {
    @FXML Label add_or_modify_label;
    @FXML TextField name_field;
    @FXML TextField address_field;
    @FXML TextField zipcode_field;
    @FXML TextField phonenum_field;
    @FXML ComboBox<Country> country_combo;
    @FXML ComboBox<Division> division_combo;
    @FXML Label error_label;
    @FXML Label error_count_label;

    private Customer customer_to_edit;

    /**
     * This method is called upon loading the form. It calls the methods required to
     * prepare the form for use by the user
     * @throws SQLException calling FillOutPreexistingInfo() can throw a SQLException
     */
    public void initialize() throws SQLException {
        CreateComboBoxes();

        customer_to_edit = StateManager.RetrieveStoredCustomer();
        if (customer_to_edit != null) {
            add_or_modify_label.setText("Modify an existing customer");
            FillOutPreexistingInfo();
        }
    }

    /**
     * This method creates both the Country combo box and the Division combo box.
     * It also populates the Country combo box, but not the Division box (that is populated dynamically later)
     * @throws SQLException Retrieving the list of contacts could cause a SQLException
     */
    public void CreateComboBoxes() throws SQLException {
        Callback<ListView<Country>, ListCell<Country>> country_cell_factory = new Callback<>() {
            @Override
            public ListCell<Country> call(ListView<Country> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Country item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        };

        country_combo.setButtonCell(country_cell_factory.call(null));
        country_combo.setCellFactory(country_cell_factory);
        country_combo.setItems(Database.GetCountryList());

        Callback<ListView<Division>, ListCell<Division>> div_cell_factory = new Callback<>() {
            @Override
            public ListCell<Division> call(ListView<Division> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Division item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        };

        division_combo.setButtonCell(div_cell_factory.call(null));
        division_combo.setCellFactory(div_cell_factory);
    }

    /**
     * This method is only called if the form is loaded with a customer_to_edit selected.
     * It takes the information from that customer and uses it to fill out the form
     * @throws SQLException Interacting with the Database could throw a SQLException
     */
    public void FillOutPreexistingInfo() throws SQLException {
        name_field.setText(customer_to_edit.getName());
        address_field.setText(customer_to_edit.getAddress());
        zipcode_field.setText(customer_to_edit.getZipcode());
        phonenum_field.setText(customer_to_edit.getPhoneNum());

        Country the_country = customer_to_edit.GetCountry();
        if (the_country == null) {
            return;
        }

        // Iterate through the country list until we find the country that matches the customer,
        // then select it
        for (int i = 0; i < country_combo.getItems().size(); i++) {
            if (country_combo.getItems().get(i).getId() == the_country.getId()) {
                country_combo.getSelectionModel().select(i);
                break;
            }
        }

        // Now that we have a country selected we update the division list to match
        UpdateDivisionList();

        // Now we iterate through the division list to find and select the right division
        for (int i = 0; i < division_combo.getItems().size(); i++) {
            if (division_combo.getItems().get(i).getId() == customer_to_edit.getDivisionId()) {
                division_combo.getSelectionModel().select(i);
                break;
            }
        }
    }

    /**
     * This method is called when the country combo box has its value changed.
     * It sets the division combo box's items to match the selected country
     *
     * LAMBDA EXPLANATION: In this method I used a lambda expression as the argument for the
     * filter method. This allowed me to take the list of all divisions in the database and
     * narrow it down to only the divisions that belong to the selected country
     *
     * @throws SQLException Querying the database could throw a SQLException
     */
    public void UpdateDivisionList() throws SQLException {
        Country chosen_country = country_combo.getValue();
        if (chosen_country == null) {
            division_combo.setItems(null);
            division_combo.setValue(null);
            division_combo.setDisable(true);
            return;
        }

        // Query the database for a list of all divisions. Convert it to a stream, then filter the stream
        // to only the elements that have a matching CountryID. Finally we convert it to an ObservableList
        // and use it to populate the division combobox
        Stream<Division> division_stream = Database.GetDivisionList().stream().filter(c -> c.getCountryId() == chosen_country.getId());
        division_combo.setItems(division_stream.collect(Collectors.toCollection(FXCollections::observableArrayList)));
        division_combo.setDisable(false);
    }

    /**
     * This method is called when hitting the "save" button. It takes all of the information
     * entered into the forms, verifies that it's valid, and then sends it off to the database
     * @param event a JavaFX event
     * @throws SQLException Saving the customer's information to the database could throw an error
     * @throws IOException Returning to the table form could throw an error
     */
    public void SaveCustomerInfo(Event event) throws SQLException, IOException {
        String name = name_field.getText();
        String address = address_field.getText();
        String zipcode = zipcode_field.getText();
        String phonenum = phonenum_field.getText();
        Division division = division_combo.getValue();

        // We analyze the information from the forms for errors.
        // If there are errors then we do not save the information to the database,
        // and we alert the user of the errors
        List<String> error_list = GetFormErrors(name, address, zipcode, phonenum, division);
        if (!error_list.isEmpty()) {
            error_label.setVisible(true);
            error_label.setText(error_list.get(0));

            // The secondary label says how many additional errors there were
            error_count_label.setVisible(true);
            if (error_list.size() > 2) {
                error_count_label.setText(String.format("%s more problems found", error_list.size() - 1));
            }

            // If there are only two errors, then the secondary label will instead display the second error
            else if (error_list.size() == 2) {
                error_count_label.setText(error_list.get(1));
            }

            // If there was only one error then we set the label to be invisible
            else {
                error_count_label.setVisible(false);
            }

            return;
        }

        // Call the appropriate database method depending on whether we're editing or adding a customer
        if (customer_to_edit == null) {
            Database.AddCustomerToDatabase(name, address, zipcode, phonenum, division);
        }
        else {
            Database.UpdateExistingCustomer(name, address, zipcode, phonenum, division, customer_to_edit.getId());
        }

        // Once the customer has been added/updated, we return to the table
        ReturnToCustomerTableForm(event);
    }

    /**
     * This method analyzes the information the user entered into the form and checks it for errors.
     * It returns a list of all the errors detected so the user knows what mistakes were made
     *
     * @param name The text from the name field
     * @param address The text from the address field
     * @param zipcode The text from the zipcode field
     * @param phonenum The text from the phone number field
     * @param division The division selected from the division combobox
     * @return Return a list of all errors detected
     */
    public List<String> GetFormErrors(String name, String address, String zipcode, String phonenum, Division division) {
        List<String> list_of_errors = new ArrayList<>();

        if (name.isBlank()) {
            list_of_errors.add("Name cannot be blank");
        }

        if (address.isBlank()) {
            list_of_errors.add("Address cannot be blank");
        }

        if (zipcode.isBlank()) {
            list_of_errors.add("Zipcode cannot be blank");
        }

        if (phonenum.isBlank()) {
            list_of_errors.add("Phone Number cannot be blank");
        }

        if (division == null) {
            list_of_errors.add("Both a country and a division must be selected");
        }

        if (name.length() > 50) {
            list_of_errors.add("Name cannot be longer than 50 characters");
        }

        if (address.length() > 100) {
            list_of_errors.add("Address cannot be longer than 100 characters");
        }

        if (zipcode.length() > 50) {
            list_of_errors.add("Zipcode cannot be longer than 50 characters");
        }

        if (phonenum.length() > 50) {
            list_of_errors.add("Phone number cannot be longer than 50 characters");
        }

        return list_of_errors;
    }

    /**
     * This method is called when hitting either the "cancel" or "save" buttons.
     * It switches the current form to the customer table form
     * @param event a JavaFX event
     * @throws IOException Switching forms could raise an IOException
     */
    public void ReturnToCustomerTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("CustomerTableForm.fxml"), event, "Customer Table");
    }
}
