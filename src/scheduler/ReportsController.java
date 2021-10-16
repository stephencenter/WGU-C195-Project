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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ReportsController {
    @FXML ListView<String> appt_type_list;
    @FXML ListView<String> appt_month_list;
    @FXML TableView<Contact> contact_table;
    @FXML Label past_appt_label;
    @FXML Label future_appt_label;
    @FXML Label present_appt_label;

    public void initialize() throws SQLException, ParseException {
        AppointmentsByTypeMonth();
        CreateContactTable();
        PastPresentFutureAppointments();
    }

    public void AppointmentsByTypeMonth() throws SQLException, ParseException {
        Map<String, Integer> type_count = new HashMap<>();
        Map<String, Integer> month_count = new HashMap<>();

        for (Appointment appt : Database.GetAppointmentList()) {
            String type_key = appt.getAppointmentType();

            if (type_count.containsKey(type_key)) {
                type_count.put(type_key, type_count.get(type_key) + 1);
            }

            else {
                type_count.put(type_key, 1);
            }

            SimpleDateFormat get_month = new SimpleDateFormat("MMMM");
            String month_key = get_month.format(appt.getStartTime());

            if (month_count.containsKey(month_key)) {
                month_count.put(month_key, month_count.get(month_key) + 1);
            }

            else {
                month_count.put(month_key, 1);
            }
        }

        ObservableList<String> type_strings = FXCollections.observableArrayList();
        for (String key : type_count.keySet()) {
            type_strings.add(String.format("(%s)    %s", type_count.get(key), key));
        }
        appt_type_list.setItems(type_strings);

        ObservableList<String> month_strings = FXCollections.observableArrayList();
        for (String key : month_count.keySet()) {
            month_strings.add(String.format("(%s)    %s", month_count.get(key), key));
        }
        appt_month_list.setItems(month_strings);
    }

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

    public void PastPresentFutureAppointments() throws SQLException, ParseException {
        int past_appts = 0;
        int future_appts = 0;
        int present_appts = 0;

        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (Appointment appt : Database.GetAppointmentList()) {
            if (appt.getEndTime().before(now)) {
                past_appts++;
            }

            else if (appt.getStartTime().after(now)) {
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

    public void ViewContactSchedule(Event event) throws IOException {
        Contact selected_contact = contact_table.getSelectionModel().getSelectedItem();

        if (selected_contact == null) {
            return;
        }

        StateManager.SetStoredContact(selected_contact);
        Main.LoadForm(getClass().getResource("ContactScheduleForm.fxml"), event, "Contact schedule");
    }

    public void SwitchToMainMenuForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("MainMenuForm.fxml"), event, "Main Menu");
    }

    public void Logout(Event event) throws IOException {
        StateManager.SetCurrentUser(null);
        Main.LoadForm(getClass().getResource("LoginForm.fxml"), event, "Login to Database");
    }
}