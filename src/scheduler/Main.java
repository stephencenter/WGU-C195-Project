package scheduler;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;

/**
 * This is our main class. It is the class that we start the application with
 */
public class Main extends Application {
    /**
     * This method is called when the application is started
     * @param primary_stage the primary stage for our application
     * @throws SQLException could be thrown when connecting to the database
     * @throws IOException could be thrown when loading the form
     */
    @Override
    public void start(Stage primary_stage) throws SQLException, IOException {
        Database.ConnectToDatabase();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginForm.fxml")));
        primary_stage.setTitle("Login to Database");
        primary_stage.setScene(new Scene(root, 425, 375));
        primary_stage.show();
    }

    /**
     * This method is used throughout the application to easily load forms in one line
     * @param form_path the path of the form to the loaded
     * @param event a JavaFX event, necessary to the the current scene and window
     * @param title_text what the text in the title bar should be set to
     * @throws IOException Loading the form could throw an IOException
     */
    public static void LoadForm(URL form_path, Event event, String title_text) throws IOException {
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(form_path));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.setTitle(title_text);
        the_stage.show();
    }

    /**
     * Main method
     * @param args any arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}