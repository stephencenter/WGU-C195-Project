package scheduler;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Objects;

public class AppointmentTableController {
    @FXML TableView<Appointment> appointment_table;
    @FXML Label appointment_delete_message;
    @FXML RadioButton unfiltered_radio;
    @FXML RadioButton month_radio;
    @FXML RadioButton week_radio;
    boolean appointment_confirm_delete = false;

    public void initialize() throws SQLException, ParseException {
        CreateAppointmentTable();
        PopulateAppointmentTable();
    }

    public void CreateAppointmentTable() {
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
    }

    public void PopulateAppointmentTable() throws SQLException, ParseException {
        ObservableList<Appointment> appointment_list = Database.GetAppointmentList();
        appointment_table.setItems(appointment_list);
        appointment_table.setPlaceholder(new Label("Database has no appointments"));
    }

    public void Logout(Event event) throws IOException {
        Database.SetCurrentUser(null);
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginForm.fxml")));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.show();
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
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AddAppointmentForm.fxml")));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.show();
    }

    public void SwitchToCustomerTableForm(Event event) throws IOException {
        Parent the_form = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("CustomerTableForm.fxml")));
        Stage the_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene the_scene = new Scene(the_form);
        the_stage.setScene(the_scene);
        the_stage.show();
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
        SwitchToAddAppointmentForm(event);
    }
}
