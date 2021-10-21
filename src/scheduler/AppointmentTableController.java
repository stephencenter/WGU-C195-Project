package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;

/**
 * This class is attached to the AppointmentTableForm. Its methods are called when
 * the user interacts with that form. It is responsible for creating and displaying
 * the appointment table and enabling the user to add, modify and delete appointments
 */
public class AppointmentTableController {
    @FXML TableView<Appointment> appointment_table;
    @FXML Label appointment_delete_message;
    @FXML RadioButton unfiltered_radio;
    @FXML RadioButton month_radio;
    @FXML RadioButton week_radio;
    boolean appointment_confirm_delete = false;

    /**
     * This method is called when the form loads. It is responsible for creating
     * and populating the appointment table
     * @throws SQLException could be thrown when retrieving the appointment list from the database
     * @throws ParseException could be thrown when retrieving the appointment list from the database
     */
    public void initialize() throws SQLException, ParseException {
        CreateAppointmentTable();
        PopulateAppointmentTable();
    }

    /**
     * This method creates the columns for the appointment table
     */
    public void CreateAppointmentTable() {
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
    }

    /**
     * This method populates the appointment table with our list of appointments from the database.
     * Depending on which radio button is selected, we will filter this list in different ways
     * @throws SQLException could be thrown when retrieving the appointment list from the database
     * @throws ParseException could be thrown when retrieving the appointment list from the database
     */
    public void PopulateAppointmentTable() throws SQLException, ParseException {
        ObservableList<Appointment> appointment_list = Database.GetAppointmentList();
        ObservableList<Appointment> filtered_list = FXCollections.observableArrayList();

        Calendar cal_now = Calendar.getInstance();
        cal_now.setTimeInMillis(System.currentTimeMillis());

        for (Appointment appt : appointment_list) {
            if (unfiltered_radio.isSelected()) {
                filtered_list = appointment_list;
                break;
            }

            Calendar cal_appt = Calendar.getInstance();
            cal_appt.setTimeInMillis(appt.getStartTime().getTime());

            if (week_radio.isSelected()) {
                if (cal_appt.get(Calendar.WEEK_OF_YEAR) == cal_now.get(Calendar.WEEK_OF_YEAR)) {
                    filtered_list.add(appt);
                }
            }

            if (month_radio.isSelected()) {
                if (cal_appt.get(Calendar.MONTH) == cal_now.get(Calendar.MONTH)) {
                    filtered_list.add(appt);
                }
            }
        }

        appointment_table.setItems(filtered_list);
        appointment_table.setPlaceholder(new Label("Database has no appointments"));
    }

    /**
     * This method deletes the currently selected appointment from the database.
     * This requires two clicks, one to initiate deletion and another to confirm. This is to
     * prevent accidental deletions. Clicking away from the currently selected appointment will
     * reset this two click requirement.
     * @throws SQLException could be thrown when retrieving the appointment list from the database
     * @throws ParseException could be thrown when retrieving the appointment list from the database
     */
    public void DeleteSelectedAppointment() throws SQLException, ParseException {
        Appointment selected_appointment = appointment_table.getSelectionModel().getSelectedItem();
        appointment_delete_message.setVisible(true);

        if (selected_appointment == null) {
            appointment_delete_message.setText("Select an appointment first");
            appointment_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        int appt_id = selected_appointment.getId();
        if (!appointment_confirm_delete) {
            appointment_confirm_delete = true;
            appointment_delete_message.setText(String.format("Click again to confirm deletion of appointment #%s", appt_id));
            appointment_delete_message.setTextFill(Color.web("#000000"));
            return;
        }

        String appt_type = selected_appointment.getAppointmentType();
        if (Database.DeleteAppointmentWithID(selected_appointment.getId())) {
            appointment_delete_message.setText(String.format("Deleted appointment #%s of type '%s'", appt_id, appt_type));
            appointment_delete_message.setTextFill(Color.web("#000000"));
        }

        else {
            appointment_delete_message.setText("Failed to delete appointment");
            appointment_delete_message.setTextFill(Color.web("#FF0000"));
        }

        appointment_confirm_delete = false;
        PopulateAppointmentTable();
    }

    /**
     * This method is called when hitting the add button. It switches the current form
     * to the AddAppointmentForm so the user can add a new appointment to the database
     * @param event a JavaFX event
     * @throws IOException Attempting to laod the new form could raise an IOException
     */
    public void SwitchToAddAppointmentForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("AddAppointmentForm.fxml"), event, "Add an Appointment");
    }

    /**
     * This method is called when the modify button is pressed. It switches the current form
     * to the AddAppointmentForm with a flag set that tells the form to pre-fill out the textfields and
     * comboboxes with the selected appointment's information. It also tells the form to update the appointment
     * in the database instead of creating a new appointment
     * @param event a JavaFX event
     * @throws IOException Attempting to laod the new form could raise an IOException
     */
    public void SwitchToModifyAppointmentForm(Event event) throws IOException {
        Appointment selected_appointment = appointment_table.getSelectionModel().getSelectedItem();
        appointment_delete_message.setVisible(true);

        if (selected_appointment == null) {
            appointment_delete_message.setText("Select an appointment first");
            appointment_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        StateManager.SetStoredAppointment(selected_appointment);
        Main.LoadForm(getClass().getResource("AddAppointmentForm.fxml"), event, "Modify an Appointment");
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
