package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

public class AddAppointmentController {
    @FXML TextField title_field;
    @FXML TextField description_field;
    @FXML TextField location_field;
    @FXML TextField type_field;
    @FXML DatePicker appt_datepicker;
    @FXML ComboBox<String> start_hour_box;
    @FXML ComboBox<String> start_minute_box;
    @FXML ComboBox<String> start_ampm_box;
    @FXML ComboBox<String> end_hour_box;
    @FXML ComboBox<String> end_minute_box;
    @FXML ComboBox<String> end_ampm_box;
    @FXML ComboBox<Customer> customer_box;
    @FXML ComboBox<Contact> contact_box;
    @FXML Label error_label;
    @FXML Label timezone_label;
    @FXML Label add_or_modify_label;
    @FXML Label error_count_label;

    Appointment appointment_to_edit = null;

    public void initialize() throws SQLException {
        timezone_label.setText("Your timezone is " + TimeZone.getDefault().getDisplayName(Locale.getDefault()));
        CreateComboBoxes();

        appointment_to_edit = StateManager.RetrieveStoredAppointment();
        if (appointment_to_edit != null) {
            add_or_modify_label.setText("Modify an appointment");
            FillOutPreexistingInfo();
        }
    }

    public void CreateComboBoxes() throws SQLException {
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

    public void FillOutPreexistingInfo() {
        title_field.setText(appointment_to_edit.getTitle());
        description_field.setText(appointment_to_edit.getDescription());
        location_field.setText(appointment_to_edit.getLocation());
        type_field.setText(appointment_to_edit.getAppointmentType());

        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        Timestamp start_time = new Timestamp(appointment_to_edit.getStartTime().getTime() + timezone_offset);
        Timestamp end_time = new Timestamp(appointment_to_edit.getEndTime().getTime() + timezone_offset);

        appt_datepicker.setValue(start_time.toLocalDateTime().toLocalDate());

        SimpleDateFormat get_hours = new SimpleDateFormat("hh");
        start_hour_box.setValue(get_hours.format(start_time));
        end_hour_box.setValue(get_hours.format(end_time));

        SimpleDateFormat get_minutes = new SimpleDateFormat("mm");
        start_minute_box.setValue(get_minutes.format(start_time));
        end_minute_box.setValue(get_minutes.format(end_time));

        SimpleDateFormat get_ampm = new SimpleDateFormat("a");
        start_ampm_box.setValue(get_ampm.format(start_time));
        end_ampm_box.setValue(get_ampm.format(end_time));

        for (int i = 0; i < contact_box.getItems().size(); i++) {
            if (contact_box.getItems().get(i).getId() == appointment_to_edit.getContactId()) {
                contact_box.getSelectionModel().select(i);
                break;
            }
        }

        for (int i = 0; i < customer_box.getItems().size(); i++) {
            if (customer_box.getItems().get(i).getId() == appointment_to_edit.getCustomerId()) {
                customer_box.getSelectionModel().select(i);
                break;
            }
        }

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

    public void SaveAppointmentInfo(Event event) throws ParseException, SQLException, IOException {
        String title = title_field.getText();
        String desc = description_field.getText();
        String location = location_field.getText();
        String type = type_field.getText();
        LocalDate appt_date = appt_datepicker.getValue();
        String start_hour = start_hour_box.getValue();
        String start_minute = start_minute_box.getValue();
        String start_ampm = start_ampm_box.getValue();
        String end_hour = end_hour_box.getValue();
        String end_minute = end_minute_box.getValue();
        String end_ampm = end_ampm_box.getValue();
        Customer customer = customer_box.getValue();
        Contact contact = contact_box.getValue();

        error_label.setVisible(true);

        List<String> form_error_list = GetFormErrors(title, desc, location, type, appt_date, start_hour, start_minute, start_ampm, end_hour, end_minute, end_ampm, customer, contact);

        if (!form_error_list.isEmpty()) {
            DisplayErrors(form_error_list);
            return;
        }

        String start_merged = MergeDateTimeStrings(appt_date.toString(), start_hour, start_minute, start_ampm);
        String end_merged = MergeDateTimeStrings(appt_date.toString(), end_hour, end_minute, end_ampm);

        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        Timestamp start_timestamp = new Timestamp(Database.ParseDate(start_merged).getTime() - timezone_offset);
        Timestamp end_timestamp = new Timestamp(Database.ParseDate(end_merged).getTime() - timezone_offset);
        Timestamp current_timestamp = new Timestamp(System.currentTimeMillis() - timezone_offset);

        List<String> time_error_list = GetTimeErrors(start_timestamp, end_timestamp, current_timestamp, customer);
        if (!time_error_list.isEmpty()) {
            DisplayErrors(time_error_list);
            return;
        }

        if (appointment_to_edit == null) {
            Database.AddAppointmentToDatabase(title, desc, location, type, start_timestamp.toString(), end_timestamp.toString(), customer, contact);
        }
        else {
            Database.UpdateExistingAppointment(title, desc, location, type, start_timestamp.toString(), end_timestamp.toString(), customer, contact, appointment_to_edit.getId());
        }

        ReturnToAppointmentTableForm(event);
    }

    public List<String> GetFormErrors(String title, String desc, String location, String type, LocalDate appt_date, String start_hour, String start_minute, String start_ampm, String end_hour, String end_minute, String end_ampm, Customer customer, Contact contact) {
        List<String> error_list = new ArrayList<>();

        if (title.isBlank()) {
            error_list.add("Title cannot be blank");
        }

        if (desc.isBlank()) {
            error_list.add("Description cannot be blank");
        }

        if (location.isBlank()) {
            error_list.add("Location cannot be blank");
        }

        if (type.isBlank()) {
            error_list.add("Appointment Type cannot be blank");
        }

        if (appt_date == null || start_hour == null || start_minute == null || start_ampm == null) {
            error_list.add("Start date and time must be completely filled out");
        }

        if (end_hour == null || end_minute == null || end_ampm == null) {
            error_list.add("End time must be completely filled out");
        }

        if (customer == null) {
            error_list.add("A customer must be selected");
        }

        if (contact == null) {
            error_list.add("A contact must be selected");
        }

        if (title.length() > 50) {
            error_list.add("Title cannot be longer than 50 characters");
        }

        if (desc.length() > 50) {
            error_list.add("Description cannot be longer than 50 characters");
        }

        if (location.length() > 50) {
            error_list.add("Location cannot be longer than 50 characters");
        }

        if (type.length() > 50) {
            error_list.add("Type cannot be longer than 50 characters");
        }

        return error_list;
    }

    public List<String> GetTimeErrors(Timestamp start_timestamp, Timestamp end_timestamp, Timestamp current_timestamp, Customer customer) throws SQLException, ParseException {
        List<String> error_list = new ArrayList<>();

        if (start_timestamp.before(current_timestamp)) {
            error_list.add("Start time cannot be in the past");
        }

        if (start_timestamp.after(end_timestamp)) {
            error_list.add("Start time cannot be after end time");
        }

        Function<Timestamp, Integer> get_hours = (c) -> Integer.parseInt(new SimpleDateFormat("HH").format(c));
        if (get_hours.apply(start_timestamp) > 2 && get_hours.apply(start_timestamp) < 12) {
            error_list.add("The appointment must fall within business hours");
        }

        if (get_hours.apply(end_timestamp) > 2 && get_hours.apply(end_timestamp) < 12) {
            error_list.add("The appointment must fall within business hours");
        }

        int to_exclude = -1;
        if (appointment_to_edit != null) {
            to_exclude = appointment_to_edit.getId();
        }
        if (customer.HasOverlappingAppointments(start_timestamp, end_timestamp, to_exclude)) {
            error_list.add("The customer already has an appointment then");
        }

        return error_list;
    }

    public void DisplayErrors(List<String> error_list) {
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
    }

    public void ReturnToAppointmentTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("AppointmentTableForm.fxml"), event, "Appointment Table");
    }
}
