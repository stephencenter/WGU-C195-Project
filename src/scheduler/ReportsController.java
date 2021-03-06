package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * This class is attached to the ReportsForm. Its methods are called when the user interacts with that form.
 * It is responsible for generating and displaying the reports to the user.
 */
public class ReportsController {
    @FXML ListView<String> appt_type_list;
    @FXML TableView<Contact> contact_table;
    @FXML Label past_appt_label;
    @FXML Label future_appt_label;
    @FXML Label present_appt_label;

    /**
     * This method is called when the form is loaded. It generates all the reports and shows them
     * to the user
     * @throws SQLException could be thrown when retrieving the appointment list
     * @throws ParseException could be thrown when retrieving the appointment list
     */
    public void initialize() throws SQLException, ParseException {
        AppointmentsByTypeMonth();
        CreateContactTable();
        PastPresentFutureAppointments();
    }

    /**
     * This method generates the report on the number of appointments type and month
     * @throws SQLException could be thrown when retrieving the appointment list
     * @throws ParseException could be thrown when retrieving the appointment list
     */
    public void AppointmentsByTypeMonth() throws SQLException, ParseException {
        Map<String, Integer> type_count = new HashMap<>();

        for (Appointment appt : Database.GetAppointmentList()) {
            SimpleDateFormat get_month = new SimpleDateFormat("MMMM");
            String dict_key = String.format("%s %s", get_month.format(appt.getStartTime()), appt.getAppointmentType());

            if (type_count.containsKey(dict_key)) {
                type_count.put(dict_key, type_count.get(dict_key) + 1);
            }

            else {
                type_count.put(dict_key, 1);
            }
        }

        ObservableList<String> type_strings = FXCollections.observableArrayList();
        for (String key : type_count.keySet()) {
            type_strings.add(String.format("(%s)    %s", type_count.get(key), key));
        }
        appt_type_list.setItems(type_strings);
    }

    /**
     * This method creates the table of every contact in the database. You can select
     * a contact in this table to view its schedule
     * @throws SQLException could be thrown when retrieving the contact list
     */
    public void CreateContactTable() throws SQLException {
        TableColumn<Contact, Integer> id_column = new TableColumn<>("ID");
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Contact, String> name_column = new TableColumn<>("Name");
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Contact, String> address_column = new TableColumn<>("Email");
        address_column.setCellValueFactory(new PropertyValueFactory<>("email"));

        contact_table.getColumns().add(id_column);
        contact_table.getColumns().add(name_column);
        contact_table.getColumns().add(address_column);
        contact_table.setPlaceholder(new Label("Database has no contacts"));
        contact_table.setItems(Database.GetContactList());
    }

    /**
     * This method is my custom report. It calculates how many appointments took place in the
     * past, how many are in the future, and how many are currently ongoing
     * @throws SQLException could be thrown when retrieving the appointment list
     * @throws ParseException could be thrown when retrieving the appointment list
     */
    public void PastPresentFutureAppointments() throws SQLException, ParseException {
        int past_appts = 0;
        int future_appts = 0;
        int present_appts = 0;

        long now = System.currentTimeMillis() -  TimeZone.getDefault().getOffset(System.currentTimeMillis());
        for (Appointment appt : Database.GetAppointmentList()) {
            if (appt.getEndTime().getTime() < now) {
                past_appts++;
            }

            else if (appt.getStartTime().getTime() > now) {
                future_appts++;
            }

            else {
                present_appts++;
            }
        }

        past_appt_label.setText(String.format("There are %s appointments in the past", past_appts));
        future_appt_label.setText(String.format("There are %s appointments in the future", future_appts));
        present_appt_label.setText(String.format("There are %s appointments currently ongoing", present_appts));
    }

    /**
     * This method is called when clicking the view schedule button. If a contact is selected,
     * this this method will switch to the ContactScheduleForm so the user can view that contact's schedule
     * @param event a JavaFX event
     * @throws IOException could be thrown when loading the new form
     */
    public void ViewContactSchedule(Event event) throws IOException {
        Contact selected_contact = contact_table.getSelectionModel().getSelectedItem();

        if (selected_contact == null) {
            return;
        }

        StateManager.SetStoredContact(selected_contact);
        Main.LoadForm(getClass().getResource("ContactScheduleForm.fxml"), event, "Contact schedule");
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