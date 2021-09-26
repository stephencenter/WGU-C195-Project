package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private static final String db_url = "jdbc:mysql://wgudb.ucertify.com:3306/";
    private static final String db_name = "client_schedule"
    private static final String db_username = "sqlUser";
    private static final String db_password = "Passw0rd!";
    private static Connection db_connection;
    private static User current_user;

    private static Customer customer_to_edit = null;
    private static Appointment appointment_to_edit = null;

    public static void ConnectToDatabase() throws SQLException {
        db_connection = DriverManager.getConnection(db_url, db_username, db_password);
    }

    public static User GetUserWithLoginInfo(String username, String password) throws SQLException, ParseException {
        List<List<String>> user_data = GetResultsFromQuery("SELECT * FROM users WHERE User_Name='" + username + "' AND Password='" + password + "'");

        if (user_data.size() == 0) {
            return null;
        }

        List<String> val = user_data.get(0);
        return new User(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5), val.get(6));
    }

    public static ObservableList<Customer> GetCustomerList() throws SQLException, ParseException {
        List<List<String>> customer_data = GetResultsFromQuery("SELECT * FROM customers");
        ObservableList<Customer> customer_list = FXCollections.observableArrayList();

        for (List<String> val : customer_data) {
            customer_list.add(new Customer(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5), val.get(6), val.get(7), val.get(8), val.get(9)));
        }

        return customer_list;
    }

    public static ObservableList<Division> GetDivisionList(int country_id) throws SQLException, ParseException {
        List<List<String>> division_data = GetResultsFromQuery("SELECT * FROM first_level_divisions WHERE COUNTRY_ID=" + country_id);
        List<Division> division_list = new ArrayList<>();

        for (List<String> val : division_data) {
            division_list.add(new Division(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5), val.get(6)));
        }

        return FXCollections.observableList(division_list);
    }

    public static ObservableList<Contact> GetContactList() throws SQLException {
        List<List<String>> contact_data = GetResultsFromQuery("SELECT * FROM contacts");
        List<Contact> contact_list = new ArrayList<>();

        for (List<String> val : contact_data) {
            contact_list.add(new Contact(val.get(0), val.get(1), val.get(2)));
        }

        return FXCollections.observableList(contact_list);
    }

    public static ObservableList<Appointment> GetAppointmentList() throws SQLException, ParseException {
        List<List<String>> appointment_data = GetResultsFromQuery("SELECT * FROM appointments");
        List<Appointment> appointment_list = new ArrayList<>();

        for (List<String> val : appointment_data) {
            appointment_list.add(new Appointment(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5), val.get(6), val.get(7), val.get(8), val.get(9), val.get(10), val.get(11), val.get(12), val.get(13)));
        }

        return FXCollections.observableList(appointment_list);
    }

    public static void SetCurrentUser(User new_user) {
        current_user = new_user;
    }

    public static String FormatInsertValues(String table, List<String> columns, List<String> values) {
        String part_1 = "INSERT INTO " + table;
        String part_2 = "(" + String.join(", ", columns) + ")";
        String part_3 = " VALUES('" + String.join("', '", values) + "')";

        return part_1 + part_2 + part_3;
    }

    public static Timestamp ParseDate(String date_string) throws ParseException {
        java.util.Date util_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_string);
        return new Timestamp(util_date.getTime());
    }

    public static List<List<String>> GetResultsFromQuery(String query) throws SQLException {
        PreparedStatement prepared = db_connection.prepareStatement(query);
        ResultSet results = prepared.executeQuery();
        ResultSetMetaData metadata = results.getMetaData();
        int num_columns = metadata.getColumnCount();

        List<List<String>> result_list = new ArrayList<>();

        while (results.next()) {
            List<String> values = new ArrayList<>();
            for (int i=1; i<num_columns+1; i++) {
                values.add(results.getString(i));
            }
            result_list.add(values);
        }

        return result_list;
    }

    public static void AddCustomerToDatabase(String name, String address, String zipcode, String phonenum, Division division) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = current_user.getUsername();
        String division_id = String.valueOf(division.getId());

        List<String> columns = Arrays.asList("Customer_Name", "Address", "Postal_Code", "Phone", "Create_Date", "Created_By", "Last_Update", "Last_Updated_By", "Division_ID");
        List<String> values = Arrays.asList(name, address, zipcode, phonenum, current_date, c_username, current_date, c_username, division_id);
        String new_customer_statement = FormatInsertValues("customers", columns, values);

        PreparedStatement new_customer_prepared = db_connection.prepareStatement(new_customer_statement);
        new_customer_prepared.executeUpdate();
    }

    public static void AddAppointmentToDatabase(String title, String desc, String location, String type, String start_time, String end_time, Customer customer, Contact contact) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = current_user.getUsername();
        String c_userid = String.valueOf(current_user.getId());
        String customer_id = String.valueOf(customer.getId());
        String contact_id = String.valueOf(contact.getId());

        List<String> columns = Arrays.asList("Title", "Description", "Location", "Type", "Start", "End", "Create_Date", "Created_By", "Last_Update", "Last_Updated_By", "Customer_ID", "User_ID", "Contact_ID");
        List<String> values = Arrays.asList(title, desc, location, type, start_time, end_time, current_date, c_username, current_date, c_username, customer_id, c_userid, contact_id);
        String new_appointment_statement = FormatInsertValues("appointments", columns, values);

        PreparedStatement new_appointment_prepared = db_connection.prepareStatement(new_appointment_statement);
        new_appointment_prepared.executeUpdate();
    }

    public static boolean DoesCustomerHaveAppointments(int id) throws SQLException, ParseException {
        ObservableList<Appointment> appointment_list = GetAppointmentList();

        for (Appointment appt : appointment_list) {
            if (appt.getCustomerId() == id) {
                return true;
            }
        }

        return false;
    }

    public static boolean DeleteCustomerWithID(int id) {
        try {
            PreparedStatement statement = db_connection.prepareStatement("DELETE FROM customers WHERE Customer_ID=" + id);
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            return false;
        }

        return true;
    }

    public static void SetCustomerForEditing(Customer customer) {
        customer_to_edit = customer;
    }

    public static Customer RetrieveCustomerAndClear() {
        Customer return_value = customer_to_edit;
        customer_to_edit = null;
        return return_value;
    }

    public static void SetAppointmentForEditing(Appointment appointment) {
        appointment_to_edit = appointment;
    }

    public static Appointment RetrieveAppointmentAndClear() {
        Appointment return_value = appointment_to_edit;
        customer_to_edit = null;
        return return_value;
    }
}