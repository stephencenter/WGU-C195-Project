package scheduler;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This method is attached to the ContactScheduleForm. Its methods are called when the
 * user interacts with that form. It's responsible for creating and displaying the
 * appointment table for the contact chosen on the Reports form
 */
public class ContactScheduleController {
    @FXML Label contact_name_label;
    @FXML TableView<Appointment> appointment_table;

    Contact the_contact;

    /**
     * This method is called when the form is loaded. It retrieves the stored contact and
     * creates the appointment table
     * @throws SQLException could be thrown when retrieving the appointments for the table
     * @throws ParseException could be thrown when parsing the dates for the appointment
     */
    public void initialize() throws SQLException, ParseException {
        the_contact = StateManager.RetrieveStoredContact();
        contact_name_label.setText(String.format("%s's appointments", the_contact.getName()));
        CreateAppointmentTable();
    }

    /**
     * This method creates and populates the appointment table
     * @throws SQLException could be thrown when retrieving the appointments for the table
     * @throws ParseException could be thrown when parsing the dates for the appointment
     */
    public void CreateAppointmentTable() throws SQLException, ParseException {
        TableColumn<Appointment, Integer> id_column = new TableColumn<>("ID");
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> title_column = new TableColumn<>("Title");
        title_column.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Appointment, String> desc_column = new TableColumn<>("Description");
        desc_column.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Appointment, String> location_column = new TableColumn<>("Location");
        location_column.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Appointment, String> contact_column = new TableColumn<>("Contact");
        contact_column.setCellValueFactory(new PropertyValueFactory<>("contactName"));

        TableColumn<Appointment, String> type_column = new TableColumn<>("Type");
        type_column.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));

        TableColumn<Appointment, String> start_column = new TableColumn<>("Start Time");
        start_column.setCellValueFactory(new PropertyValueFactory<>("startTimeLocal"));

        TableColumn<Appointment, String> end_column = new TableColumn<>("End Time");
        end_column.setCellValueFactory(new PropertyValueFactory<>("endTimeLocal"));

        TableColumn<Appointment, Integer> customerid_column = new TableColumn<>("Customer ID");
        customerid_column.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Appointment, Integer> userid_column = new TableColumn<>("User ID");
        userid_column.setCellValueFactory(new PropertyValueFactory<>("userId"));

        appointment_table.getColumns().add(id_column);
        appointment_table.getColumns().add(title_column);
        appointment_table.getColumns().add(desc_column);
        appointment_table.getColumns().add(location_column);
        appointment_table.getColumns().add(contact_column);
        appointment_table.getColumns().add(type_column);
        appointment_table.getColumns().add(start_column);
        appointment_table.getColumns().add(end_column);
        appointment_table.getColumns().add(customerid_column);
        appointment_table.getColumns().add(userid_column);

        Stream<Appointment> appt_stream = Database.GetAppointmentList().stream().filter(c -> c.getContactId() == the_contact.getId());
        appointment_table.setItems(appt_stream.collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    /**
     * This method is called when hitting the return button. It switches the current form to the Reports form
     * @param event a JavaFX event
     * @throws IOException could be thrown when loading the form
     */
    public void SwitchToReportsForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("ReportsForm.fxml"), event, "Main Menu");
    }

    /**
     * This method is called when hitting the main menu button. It switches the current form to the Main Menu form
     * @param event a JavaFX event
     * @throws IOException could be thrown when loading the form
     */
    public void SwitchToMainMenuForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("MainMenuForm.fxml"), event, "Main Menu");
    }

    /**
     * This method is called when clicking logout. It sets the current user to null and switches
     * to the login form
     * @param event a JavaFX event
     * @throws IOException could be thrown when loading the form
     */
    public void Logout(Event event) throws IOException {
        StateManager.SetCurrentUser(null);
        Main.LoadForm(getClass().getResource("LoginForm.fxml"), event, "Login to Database");
    }
}
