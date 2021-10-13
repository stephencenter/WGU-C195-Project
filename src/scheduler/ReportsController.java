package scheduler;

import javafx.event.Event;
import java.io.IOException;

public class ReportsController {
    public void SwitchToMainMenuForm(Event event) throws IOException {
        Main.LoadForm(getClass().getResource("MainMenuForm.fxml"), event, "Main Menu");
    }

    public void Logout(Event event) throws IOException {
        Database.SetCurrentUser(null);
        Main.LoadForm(getClass().getResource("LoginForm.fxml"), event, "Login to Database");
    }
}