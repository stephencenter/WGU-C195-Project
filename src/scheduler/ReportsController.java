package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportsController {
    @FXML ListView<String> appt_type_list;
    @FXML ListView<String> appt_month_list;
    @FXML Label past_appt_label;
    @FXML Label future_appt_label;
    @FXML Label present_appt_label;

    public void initialize() throws SQLException, ParseException {
        PastPresentFutureAppointments();
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

    public void SwitchToMainMenuForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("MainMenuForm.fxml"), event, "Main Menu");
    }

    public void Logout(Event event) throws IOException {
        Database.SetCurrentUser(null);
        Main.LoadForm(getClass().getResource("LoginForm.fxml"), event, "Login to Database");
    }
}