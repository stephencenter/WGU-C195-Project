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

/**
 * This class is attached to the AddAppointmentForm. Its methods are called
 * when the user interacts with that form. It is responsible for handling the information
 * entered into the form, validating it and adding it to the database if it is valid. It
 * also can be used to modify appointments
 */
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

    /**
     * This method is called when the form is first loaded.
     * It sets up the form so the user can enter their information. It also
     * fills out the forms if the user is modifying an appointment
     * @throws SQLException Setting the item list for the comboboxes could throw a SQLException
     */
    public void initialize() throws SQLException {
        timezone_label.setText("Your timezone is " + TimeZone.getDefault().getDisplayName(Locale.getDefault()));
        CreateComboBoxes();

        appointment_to_edit = StateManager.RetrieveStoredAppointment();
        if (appointment_to_edit != null) {
            add_or_modify_label.setText("Modify an appointment");
            FillOutPreexistingInfo();
        }
    }

    /**
     * This method populates the various comboboxes used on this form
     * @throws SQLException Retrieving the lists from the database could throw a SQLException
     */
    public void CreateComboBoxes() throws SQLException {
        // Set hour combobox lists
        ObservableList<String> hour_list = FXCollections.observableArrayList("12", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11");
        start_hour_box.setItems(hour_list);
        end_hour_box.setItems(hour_list);

        // We create an observable list and then add one string for every integer from 0 to 59
        // If the integer is less than 10 we also add a leading zero
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

        customer_box.setItems(Database.GetCustomerList());

        // The combobox item list is filled with customers, but we can't just print out the raw customer object.
        // We need a method to convert the customer to a string. In this case we modify the updateItem method
        // so it will set the text to be the customer's name. Otherwise it would just display a memory address
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

        // We do the same thing we did with the customer combobox, this time for the contact combobox
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

    /**
     * Thsi method is only called if the user is editing an appointment. It fills out the form
     * with the previous information for the appointment
     */
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

    /**
     * This method takes a date, hour, minute, and am/pm, and converts them to a string
     * in the same format as the timestamps stored in the SQL database
     * @param date the date
     * @param hour the hour
     * @param minute the minute
     * @param ampm am/pm
     * @return a merged time string in the proper format
     */
    public String MergeDateTimeStrings(String date, String hour, String minute, String ampm) {
        if (hour.equals("12") && ampm.equalsIgnoreCase("am")) {
            hour = "00";
        }

        if (ampm.equalsIgnoreCase("pm") && Integer.parseInt(hour) < 12) {
            hour = String.valueOf(Integer.parseInt(hour) + 12);
        }

        return date + " " + hour + ":" + minute + ":00";
    }

    /**
     * This method is called when the Save button is clicked. It takes all the information entered into
     * the form, verifies that it's valid, and if so saves it to the database as a new (or updated)
     * Appointment
     * @param event a JavaFX event
     * @throws ParseException Parsing the dates could throw a ParseException
     * @throws SQLException Retrieving the lists from the database could throw a SQLException
     * @throws IOException Switching forms could throw an IOException
     */
    public void SaveAppointmentInfo(Event event) throws ParseException, SQLException, IOException {
        // First we get all the information the user entered into the form
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

        // Check the information for erros
        List<String> form_error_list = GetFormErrors(title, desc, location, type, appt_date, start_hour, start_minute, start_ampm, end_hour, end_minute, end_ampm, customer, contact);

        // If any errors were found we display them and then return without saving to the database
        if (!form_error_list.isEmpty()) {
            DisplayErrors(form_error_list);
            return;
        }

        // Merge the date, start hour, start minute, and am/pm into one single string that we can parse into a Timestamp
        String start_merged = MergeDateTimeStrings(appt_date.toString(), start_hour, start_minute, start_ampm);
        String end_merged = MergeDateTimeStrings(appt_date.toString(), end_hour, end_minute, end_ampm);

        // Parse the string into a Timestamp and then convert it to UTC by subtracting the timezone offset
        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        Timestamp start_timestamp = new Timestamp(Database.ParseDate(start_merged).getTime() - timezone_offset);
        Timestamp end_timestamp = new Timestamp(Database.ParseDate(end_merged).getTime() - timezone_offset);
        Timestamp current_timestamp = new Timestamp(System.currentTimeMillis() - timezone_offset);

        // Check the timestamps for errors
        List<String> time_error_list = GetTimeErrors(start_timestamp, end_timestamp, current_timestamp, customer);

        // If any errors were found we display them and then return without saving to the database
        if (!time_error_list.isEmpty()) {
            DisplayErrors(time_error_list);
            return;
        }

        // Add or update the new information to the database
        if (appointment_to_edit == null) {
            Database.AddAppointmentToDatabase(title, desc, location, type, start_timestamp.toString(), end_timestamp.toString(), customer, contact);
        }
        else {
            Database.UpdateExistingAppointment(title, desc, location, type, start_timestamp.toString(), end_timestamp.toString(), customer, contact, appointment_to_edit.getId());
        }

        // Return to the appointment table
        Main.LoadForm(getClass().getResource("AppointmentTableForm.fxml"), event, "Appointment Table");
    }

    /**
     * This method analyzes the information entered into the form to verify that every form
     * was filled out and that no textboxes had too many characters used. It keeps track
     * of every error discovered and returns a list of all of them
     * @param title what the user entered into the title box
     * @param desc what the user entered into the description box
     * @param location what the user entered into the location box
     * @param type what the user entered into the type box
     * @param appt_date what the user chose as the appointment date
     * @param start_hour what the user chose as the starting hour
     * @param start_minute what the user chose as the starting minute
     * @param start_ampm what the user chose for starting am/pm
     * @param end_hour what the user chose as the ending hour
     * @param end_minute what the user chose as the ending minute
     * @param end_ampm what the user chose for ending am/pm
     * @param customer what the user chose as the customer
     * @param contact what the user chose as the contact
     * @return a list of every error discovered
     */
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

    /**
     * This method analyzes the appointment times entered by the user and checks to see if they
     * fit the requirements. Specifically, an appointment must take place in the future, must start
     * before it ends, must fall within business hours, and cannot overlap with another appointment
     * for the same customer
     * @param start_timestamp the time the appointment is set to start (UTC)
     * @param end_timestamp the time the appointment is set to end (UTC)
     * @param current_timestamp the current time (UTC)
     * @param customer the customer the appoinement is for
     * @return a list of every error found
     * @throws SQLException could be thrown when checking for overlapping appointments
     * @throws ParseException could be thrown when parsing the dates for the appointments
     */
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

    /**
     * This method takes a list of all errors that occured and shows them to the user
     * @param error_list a list of every error that occurred
     */
    public void DisplayErrors(List<String> error_list) {
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
    }

    /**
     * This method is called when clicking the cancel button. It switches the current form
     * to the AppointmentTableForm
     * @param event a JavaFX event
     * @throws IOException Loading the form could throw an IOException
     */
    public void ReturnToAppointmentTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("AppointmentTableForm.fxml"), event, "Appointment Table");
    }
}
