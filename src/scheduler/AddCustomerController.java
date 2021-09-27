package scheduler;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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

    private Customer customer_to_edit;

    public void initialize() throws SQLException, ParseException {
        country_combo.setItems(Database.GetCountryList());

        Callback<ListView<Country>, ListCell<Country>> cell_factory = new Callback<>() {
            @Override
            public ListCell<Country> call(ListView<Country> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Country item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getCountry());
                        }
                    }
                };
            }
        };
        country_combo.setButtonCell(cell_factory.call(null));
        country_combo.setCellFactory(cell_factory);

        customer_to_edit = Database.RetrieveCustomerAndClear();
        if (customer_to_edit == null) {
            return;
        }
        add_or_modify_label.setText("Modify an existing customer");

        name_field.setText(customer_to_edit.getName());
        address_field.setText(customer_to_edit.getAddress());
        zipcode_field.setText(customer_to_edit.getZipcode());
        phonenum_field.setText(customer_to_edit.getPhoneNum());

        Country the_country = Database.GetCountryWithDivisionID(customer_to_edit.getDivisionId());
        if (the_country == null) {
            return;
        }
        for (int i = 0; i < country_combo.getItems().size(); i++) {
            if (country_combo.getItems().get(i).getId() == the_country.getId()) {
                country_combo.getSelectionModel().select(i);
            }
        }

        UpdateRegionList();

        for (int i = 0; i < region_combo.getItems().size(); i++) {
            if (region_combo.getItems().get(i).getId() == customer_to_edit.getDivisionId()) {
                region_combo.getSelectionModel().select(i);
            }
        }
    }

    public void UpdateRegionList() throws SQLException, ParseException {
        System.out.println("yo");
        Country chosen_country = country_combo.getValue();
        if (chosen_country == null) {
            region_combo.setItems(null);
            region_combo.setValue(null);
            region_combo.setDisable(true);
            return;
        }

        Stream<Division> division_stream = Database.GetDivisionList().stream().filter(c -> c.getCountryId() == chosen_country.getId());
        region_combo.setItems(division_stream.collect(Collectors.toCollection(FXCollections::observableArrayList)));

        Callback<ListView<Division>, ListCell<Division>> cell_factory = new Callback<>() {
            @Override
            public ListCell<Division> call(ListView<Division> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Division item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getDivision());
                        }
                    }
                };
            }
        };
        region_combo.setButtonCell(cell_factory.call(null));
        region_combo.setCellFactory(cell_factory);

        region_combo.setValue(null);
        region_combo.setDisable(false);
    }

    public void SaveCustomerInfo(Event event) throws SQLException, IOException {
        String name = name_field.getText();
        String address = address_field.getText();
        String zipcode = zipcode_field.getText();
        String phonenum = phonenum_field.getText();
        Division division = region_combo.getValue();

        if (name.isBlank()) {
            error_label.setVisible(true);
            error_label.setText("Name cannot be blank!");
            return;
        }

        if (address.isBlank()) {
            error_label.setVisible(true);
            error_label.setText("Address cannot be blank!");
            return;
        }

        if (zipcode.isBlank()) {
            error_label.setVisible(true);
            error_label.setText("Zipcode cannot be blank!");
            return;
        }

        if (phonenum.isBlank()) {
            error_label.setVisible(true);
            error_label.setText("Phone Number cannot be blank!");
            return;
        }

        if (division == null) {
            error_label.setVisible(true);
            error_label.setText("Country and region cannot be blank!");
            return;
        }

        error_label.setVisible(false);
        if (customer_to_edit == null) {
            Database.AddCustomerToDatabase(name, address, zipcode, phonenum, division);
        }
        else {
            Database.UpdateExistingCustomer(name, address, zipcode, phonenum, division, customer_to_edit.getId());
        }
        ReturnToCustomerTableForm(event);
    }

    public void ReturnToCustomerTableForm(Event event) throws IOException {
        Parent customer_table_form = FXMLLoader.load(getClass().getResource("CustomerTableForm.fxml"));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(customer_table_form);
        the_stage.setScene(the_scene);
        the_stage.show();
    }
}
