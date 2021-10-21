package scheduler;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.TimeZone;

/**
 * This class is attached to the MainMenuForm. Its methods are called when the user interacts with that form.
 * The main menu has buttons that let the user navigate between the various other forms in the application
 */
public class MainMenuController {
    @FXML Label upcoming_appt_label;

    /**
     * This method is called when the form is loaded. It checks the list of appointments for any that starts
     * less than 15 minutes in the future. If it finds one then it will alert the user
     * @throws SQLException could be thrown when retrieving the appointment list from the database
     * @throws ParseException could be thrown when retrieving the appointment list from the database
     */
    public void initialize() throws SQLException, ParseException {
        upcoming_appt_label.setVisible(true);

        long current_time = System.currentTimeMillis();
        int fifteen_minutes = 900000;
        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());

        for (Appointment appt : Database.GetAppointmentList()) {
            long time_diff = appt.getStartTime().getTime() - current_time + timezone_offset;

            if (time_diff > 0 && time_diff < fifteen_minutes) {
                upcoming_appt_label.setText(String.format("Upcoming appointment ID #%s on %s", appt.getId(), appt.getStartTimeLocal()));
                return;
            }
        }

        upcoming_appt_label.setText("There are no upcoming appointments");
    }

    public void SwitchToAppointmentTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("AppointmentTableForm.fxml"), event, "Appointment Table");
    }

    public void SwitchToCustomerTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("CustomerTableForm.fxml"), event, "Customer Table");
    }

    /**
     * This method is called when hitting the reports button. It switches the current form to the Reports form
     * @param event a JavaFX event
     * @throws IOException could be thrown when loading the form
     */
    public void SwitchToReportsForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("ReportsForm.fxml"), event, "Reports");
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