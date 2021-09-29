package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;

public class AddAppointmentController {
    @FXML TextField title_field;
    @FXML TextField description_field;
    @FXML TextField location_field;
    @FXML TextField type_field;
    @FXML DatePicker start_datepicker;
    @FXML ComboBox<String> start_hour_box;
    @FXML ComboBox<String> start_minute_box;
    @FXML ComboBox<String> start_ampm_box;
    @FXML DatePicker end_datepicker;
    @FXML ComboBox<String> end_hour_box;
    @FXML ComboBox<String> end_minute_box;
    @FXML ComboBox<String> end_ampm_box;
    @FXML ComboBox<Customer> customer_box;
    @FXML ComboBox<Contact> contact_box;
    @FXML Label error_label;

    public void initialize() throws SQLException {
        // Set hour combobox lists
        ObservableList<String> hour_list = FXCollections.observableArrayList("12", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11");

        start_hour_box.setItems(hour_list);
        end_hour_box.setItems(hour_list);

        // Set minute combobox lists
        ObservableList<String> minute_list = FXCollections.observableArrayList();
        for (int i = 0; i<60; i++) {
            if (i < 10) {
                minute_list.add("0" + i);
            }
            else {
                minute_list.add(String.valueOf(i));
            }
        }

        start_minute_box.setItems(minute_list);
        end_minute_box.setItems(minute_list);

        // Set am/pm combobox lists
        ObservableList<String> ampm_list = FXCollections.observableArrayList("AM", "PM");
        start_ampm_box.setItems(ampm_list);
        end_ampm_box.setItems(ampm_list);

        // Set customer combobox list
        customer_box.setItems(Database.GetCustomerList());
        Callback<ListView<Customer>, ListCell<Customer>> customer_factory = new Callback<>() {

            @Override
            public ListCell<Customer> call(ListView<Customer> l) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(Customer item, boolean empty) {
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

        customer_box.setButtonCell(customer_factory.call(null));
        customer_box.setCellFactory(customer_factory);

        // Set contact combobox list
        contact_box.setItems(Database.GetContactList());
        Callback<ListView<Contact>, ListCell<Contact>> contact_factory = new Callback<>() {

            @Override
            public ListCell<Contact> call(ListView<Contact> l) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(Contact item, boolean empty) {
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

        contact_box.setButtonCell(contact_factory.call(null));
        contact_box.setCellFactory(contact_factory);
    }

    public String MergeDateTimeStrings(String date, String hour, String minute, String ampm) {
        if (hour.equals("12") && ampm.equalsIgnoreCase("am")) {
            hour = "00";
        }

        if (ampm.equalsIgnoreCase("pm") && Integer.parseInt(hour) < 12) {
            hour = String.valueOf(Integer.parseInt(hour) + 12);
        }

        return date + " " + hour + ":" + minute + ":00";
    }

    public void SaveAppointmentInfo() throws ParseException, SQLException {
        String title = title_field.getText();
        String desc = description_field.getText();
        String location = location_field.getText();
        String type = type_field.getText();
        LocalDate start_date = start_datepicker.getValue();
        String start_hour = start_hour_box.getValue();
        String start_minute = start_minute_box.getValue();
        String start_ampm = start_ampm_box.getValue();
        LocalDate end_date = end_datepicker.getValue();
        String end_hour = end_hour_box.getValue();
        String end_minute = end_minute_box.getValue();
        String end_ampm = end_ampm_box.getValue();
        Customer customer = customer_box.getValue();
        Contact contact = contact_box.getValue();

        error_label.setVisible(true);

        if (title.isBlank()) {
            error_label.setText("Title cannot be blank");
            return;
        }

        if (desc.isBlank()) {
            error_label.setText("Description cannot be blank");
            return;
        }

        if (location.isBlank()) {
            error_label.setText("Location cannot be blank");
            return;
        }

        if (type.isBlank()) {
            error_label.setText("Appointment Type cannot be blank");
            return;
        }

        if (start_date == null || start_hour == null || start_minute == null || start_ampm == null) {
            error_label.setText("Start date and time must be completely filled out");
            return;
        }

        if (end_date == null || end_hour == null || end_minute == null || end_ampm == null) {
            error_label.setText("End date and time must be completely filled out");
            return;
        }

        if (customer == null) {
            error_label.setText("A customer must be selected");
            return;
        }

        if (contact == null) {
            error_label.setText("A contact must be selected");
            return;
        }

        if (title.length() > 50) {
            error_label.setText("Title cannot be longer than 50 characters");
            return;
        }

        if (desc.length() > 50) {
            error_label.setText("Description cannot be longer than 50 characters");
            return;
        }

        if (location.length() > 50) {
            error_label.setText("Location cannot be longer than 50 characters");
        }

        if (type.length() > 50) {
            error_label.setText("Type cannot be longer than 50 characters");
            return;
        }

        String start_time = MergeDateTimeStrings(start_date.toString(), start_hour, start_minute, start_ampm);
        String end_time = MergeDateTimeStrings(end_date.toString(), end_hour, end_minute, end_ampm);

        Timestamp start_timestamp = Database.ParseDate(start_time);
        Timestamp end_timestamp = Database.ParseDate(end_time);
        Timestamp current_timestamp = new Timestamp(new java.util.Date().getTime());

        if (start_timestamp.compareTo(current_timestamp) < 0) {
            error_label.setText("Start time cannot be in the past!");
            return;
        }

        if (start_timestamp.compareTo(end_timestamp) > 0) {
            error_label.setText("Start time cannot be after end time!");
            return;
        }

        error_label.setVisible(false);

        Database.AddAppointmentToDatabase(title, desc, location, type, start_time, end_time, customer, contact);
    }
}
