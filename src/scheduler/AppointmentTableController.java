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
import java.util.TimeZone;

public class AppointmentTableController {
    @FXML TableView<Appointment> appointment_table;
    @FXML Label appointment_delete_message;
    @FXML RadioButton unfiltered_radio;
    @FXML RadioButton month_radio;
    @FXML RadioButton week_radio;
    @FXML Label upcoming_appt_label;
    boolean appointment_confirm_delete = false;

    public void initialize() throws SQLException, ParseException {
        CreateAppointmentTable();
        PopulateAppointmentTable();
    }

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

        AlertOfUpcomingAppointments();
    }

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

    public void AlertOfUpcomingAppointments() throws SQLException, ParseException {
        upcoming_appt_label.setVisible(true);

        long current_time = System.currentTimeMillis();
        int fifteen_minutes = 900000;
        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());

        for (Appointment appt : Database.GetAppointmentList()) {
            // I have NO IDEA why I have to add a timezone offset to convert back to UTC.
            // My dates are stored as UTC and look correct in the database, and Timestamp.getTime() says
            // that it returns milliseconds in UTC time, but for some reason it doesn't work
            long time_diff = appt.getStartTime().getTime() - current_time + timezone_offset;

            if (time_diff > 0 && time_diff < fifteen_minutes) {
                upcoming_appt_label.setText(String.format("Upcoming appointment ID #%s on %s", appt.getId(), appt.getStartTimeLocal()));
                return;
            }
        }

        upcoming_appt_label.setText("There are no upcoming appointments");
    }

    public void DeleteSelectedAppointment() throws SQLException, ParseException {
        Appointment selected_appointment = appointment_table.getSelectionModel().getSelectedItem();
        appointment_delete_message.setVisible(true);

        if (selected_appointment == null) {
            appointment_delete_message.setText("Select an appointment first");
            appointment_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        if (!appointment_confirm_delete) {
            appointment_confirm_delete = true;
            appointment_delete_message.setText("Click again to confirm appointment deletion");
            appointment_delete_message.setTextFill(Color.web("#000000"));
            return;
        }

        if (Database.DeleteAppointmentWithID(selected_appointment.getId())) {
            appointment_delete_message.setText("Appointment deleted sucessfully!");
            appointment_delete_message.setTextFill(Color.web("#000000"));
        }

        else {
            appointment_delete_message.setText("Failed to delete appointment");
            appointment_delete_message.setTextFill(Color.web("#FF0000"));
        }

        appointment_confirm_delete = false;
        PopulateAppointmentTable();
    }

    public void SwitchToAddAppointmentForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("AddAppointmentForm.fxml"), event, "Add an Appointment");
    }

    public void SwitchToModifyAppointmentForm(Event event) throws IOException {
        Appointment selected_appointment = appointment_table.getSelectionModel().getSelectedItem();
        appointment_delete_message.setVisible(true);

        if (selected_appointment == null) {
            appointment_delete_message.setText("Select an appointment first");
            appointment_delete_message.setTextFill(Color.web("#FF0000"));
            return;
        }

        Database.SetAppointmentForEditing(selected_appointment);
        Main.LoadForm(getClass().getResource("AddAppointmentForm.fxml"), event, "Modify an Appointment");
    }

    public void SwitchToMainMenuForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("MainMenuForm.fxml"), event, "Main Menu");
    }

    public void Logout(Event event) throws IOException {
        Database.SetCurrentUser(null);
        Main.LoadForm(getClass().getResource("LoginForm.fxml"), event, "Login to Database");
    }
}
