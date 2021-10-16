package scheduler;

import javafx.event.Event;
import java.io.IOException;

public class MainMenuController {
    public void SwitchToAppointmentTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("AppointmentTableForm.fxml"), event, "Appointment Table");
    }

    public void SwitchToCustomerTableForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("CustomerTableForm.fxml"), event, "Customer Table");
    }

    public void SwitchToReportsForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("ReportsForm.fxml"), event, "Reports");
    }

    public void Logout(Event event) throws IOException {
        StateManager.SetCurrentUser(null);
        Main.LoadForm(getClass().getResource("LoginForm.fxml"), event, "Login to Database");
    }
}