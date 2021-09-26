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

public class AddCustomerController {
    @FXML TextField name_field;
    @FXML TextField address_field;
    @FXML TextField zipcode_field;
    @FXML TextField phonenum_field;
    @FXML ComboBox<String> country_combo;
    @FXML ComboBox<Division> region_combo;
    @FXML Label error_label;

    private Customer customer_to_edit;

    public void initialize() {
        country_combo.setItems(FXCollections.observableArrayList("Canada", "United Kingdom", "United States"));

        customer_to_edit = Database.RetrieveCustomerAndClear();
        if (customer_to_edit == null) {
            System.out.println("No customer was selected");
        }
        else {
            System.out.println(customer_to_edit.getName() + " was selected");
        }
    }

    public void UpdateRegionList() throws SQLException, ParseException {
        String chosen_country = country_combo.getValue();
        if (chosen_country == null) {
            region_combo.setItems(null);
            region_combo.setValue(null);
            region_combo.setDisable(true);
            return;
        }

        if (chosen_country.equals("Canada")) {
            region_combo.setItems(Database.GetDivisionList(38));
        }

        if (chosen_country.equals("United Kingdom")) {
            region_combo.setItems(Database.GetDivisionList(230));
        }

        if (chosen_country.equals("United States")) {
            region_combo.setItems(Database.GetDivisionList(231));
        }

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
        Database.AddCustomerToDatabase(name, address, zipcode, phonenum, division);
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
