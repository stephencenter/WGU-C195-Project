package scheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primary_stage) throws Exception {
        Database.ConnectToDatabase();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginForm.fxml")));
        primary_stage.setTitle("Log in to Database");
        primary_stage.setScene(new Scene(root, 425, 375));
        primary_stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}