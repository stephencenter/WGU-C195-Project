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
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primary_stage) throws Exception {
        Database.ConnectToDatabase();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginForm.fxml")));
        primary_stage.setTitle("Login to Database");
        primary_stage.setScene(new Scene(root, 425, 375));
        primary_stage.show();
    }

    public static void LoadForm(URL form_url, Event event, String title_text) throws IOException {
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(form_url));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.setTitle(title_text);
        the_stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}