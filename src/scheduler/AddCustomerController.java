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

public class AddCustomerController {
    @FXML Label add_or_modify_label;
    @FXML TextField name_field;
    @FXML TextField address_field;
    @FXML TextField zipcode_field;
    @FXML TextField phonenum_field;
    @FXML ComboBox<Country> country_combo;
    @FXML ComboBox<Division> region_combo;
    @FXML Label error_label;
    @FXML Label error_count_label;

    private Customer customer_to_edit;

    public void initialize() throws SQLException {
        CreateComboBoxes();

        customer_to_edit = Database.RetrieveCustomerAndClear();
        if (customer_to_edit != null) {
            add_or_modify_label.setText("Modify an existing customer");
            FillOutPreexistingInfo();
        }
    }

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

        region_combo.setButtonCell(div_cell_factory.call(null));
        region_combo.setCellFactory(div_cell_factory);
    }

    public void FillOutPreexistingInfo() throws SQLException {
        name_field.setText(customer_to_edit.getName());
        address_field.setText(customer_to_edit.getAddress());
        zipcode_field.setText(customer_to_edit.getZipcode());
        phonenum_field.setText(customer_to_edit.getPhoneNum());

        Country the_country = customer_to_edit.GetCountry();
        if (the_country == null) {
            return;
        }

        for (int i = 0; i < country_combo.getItems().size(); i++) {
            if (country_combo.getItems().get(i).getId() == the_country.getId()) {
                country_combo.getSelectionModel().select(i);
                break;
            }
        }

        UpdateRegionList();

        for (int i = 0; i < region_combo.getItems().size(); i++) {
            if (region_combo.getItems().get(i).getId() == customer_to_edit.getDivisionId()) {
                region_combo.getSelectionModel().select(i);
                break;
            }
        }
    }

    public void UpdateRegionList() throws SQLException {
        Country chosen_country = country_combo.getValue();
        if (chosen_country == null) {
            region_combo.setItems(null);
            region_combo.setValue(null);
            region_combo.setDisable(true);
            return;
        }

        Stream<Division> division_stream = Database.GetDivisionList().stream().filter(c -> c.getCountryId() == chosen_country.getId());
        region_combo.setItems(division_stream.collect(Collectors.toCollection(FXCollections::observableArrayList)));
        region_combo.setDisable(false);
    }

    public void SaveCustomerInfo(Event event) throws SQLException, IOException {
        String name = name_field.getText();
        String address = address_field.getText();
        String zipcode = zipcode_field.getText();
        String phonenum = phonenum_field.getText();
        Division division = region_combo.getValue();

        List<String> error_list = GetFormErrors(name, address, zipcode, phonenum, division);
        if (!error_list.isEmpty()) {
            error_label.setVisible(true);
            error_label.setText(error_list.get(0));

            error_count_label.setVisible(true);
            if (error_list.size() > 2) {
                error_count_label.setText(String.format("%s more problems found", error_list.size() - 1));
            }

            else if (error_list.size() == 2) {
                error_count_label.setText(error_list.get(1));
            }

            else {
                error_count_label.setVisible(false);
            }

            return;
        }

        if (customer_to_edit == null) {
            Database.AddCustomerToDatabase(name, address, zipcode, phonenum, division);
        }
        else {
            Database.UpdateExistingCustomer(name, address, zipcode, phonenum, division, customer_to_edit.getId());
        }

        ReturnToCustomerTableForm(event);
    }

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
            list_of_errors.add("Both a country and a region must be selected");
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

    public void ReturnToCustomerTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("CustomerTableForm.fxml"), event, "Customer Table");
    }
}
