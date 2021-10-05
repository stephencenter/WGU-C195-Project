package scheduler;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class LoginController {
    @FXML Label title_label;
    @FXML Label username_label;
    @FXML TextField username_field;
    @FXML Label password_label;
    @FXML PasswordField password_field;
    @FXML Button login_button;
    @FXML Label error_label;
    @FXML Label timezone_label;

    public void initialize() {
        Map<String, Map<String, String>> localization = ParseLocalization();
        Locale locale = Locale.getDefault();
        String user_language = locale.getLanguage();
        title_label.setText(localization.get("title_label").get(user_language));
        username_label.setText(localization.get("username_label").get(user_language));
        password_label.setText(localization.get("password_label").get(user_language));
        login_button.setText(localization.get("login_button").get(user_language));
        error_label.setText(localization.get("error_label").get(user_language));

        String timezone_text = localization.get("timezone_label").get(user_language);
        timezone_label.setText(timezone_text + " " + TimeZone.getDefault().getDisplayName(locale));

        username_field.setText("stephen");
        password_field.setText("sloth101");
        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        System.out.println(timezone_offset);
    }

    public Map<String, Map<String, String>> ParseLocalization() {
        // raw_format is a list that contains every line from the localization.txt file
        List<String> raw_format;
        try {
            raw_format = Files.readAllLines(Paths.get("localization.txt")) ;
        }
        catch (IOException ex) {
            return null;
        }

        // We create a dictionary that we will use to set the appropriate text for each UI element.
        // The key for the dictionary is a string, which corresponds to the name of a UI element.
        // The value for the dictionary is another dictionary, where the key correponds to a language and the value
        // is the appropriate translation of the UI element text in that language
        Map<String, Map<String, String>> localization = new HashMap<>();
        String current_key = "";

        // We parse the txt file to create our dictionary. This could have been easily done with JSON,
        // but every tutorial I found for JSON in Java required an external library which was not allowed.
        // So I made my own parser
        for (int index = 0; index < raw_format.size(); index++) {
            switch (raw_format.get(index).strip()) {
                case "[key]":
                    // If the line is tagged with [key], that means the following line is the string that should
                    // be used as the Key for our dictionary
                    current_key = raw_format.get(index + 1);
                    localization.put(current_key, new HashMap<>());
                    break;

                case "[english]":
                    // If the line is tagged with [english], that means the following line is the English translation
                    // of the desired text for the UI element
                    localization.get(current_key).put("en", raw_format.get(index + 1));
                    break;

                case "[french]":
                    // If the line is tagged with [french], that means the following line is the French translation
                    // of the desired text for the UI element (as provided by Google Translate...)
                    localization.get(current_key).put("fr", raw_format.get(index + 1));
                    break;
            }
        }

        return localization;
    }

    public void PressLoginButton(Event event) throws SQLException, IOException {
        String username = username_field.getText();
        String password = password_field.getText();
        User the_user = Database.GetUserWithLoginInfo(username, password);

        if (the_user == null) {
            error_label.setVisible(true);
            return;
        }

        Database.SetCurrentUser(the_user);
        Parent modify_products_scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AppointmentTableForm.fxml")));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(modify_products_scene);
        the_stage.setScene(the_scene);
        the_stage.show();
    }
}
