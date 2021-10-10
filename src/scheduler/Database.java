package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database {
    private static final String db_url = "jdbc:mysql://localhost:3306/";
    private static final String db_name = "client_schedule";
    private static final String db_username = "sqlUser";
    private static final String db_password = "Passw0rd!";
    private static Connection db_connection;
    private static User current_user;

    private static Customer customer_to_edit = null;
    private static Appointment appointment_to_edit = null;

    public static void ConnectToDatabase() throws SQLException {
        db_connection = DriverManager.getConnection(db_url + db_name, db_username, db_password);
    }

    public static User GetUserWithLoginInfo(String username, String password) throws SQLException {
        List<List<String>> user_data = GetResultsFromQuery("SELECT User_ID, User_Name, Password FROM users WHERE User_Name='" + username + "' AND Password='" + password + "'");

        if (user_data.size() == 0) {
            return null;
        }

        List<String> val = user_data.get(0);
        return new User(val.get(0), val.get(1), val.get(2));
    }

    public static ObservableList<Customer> GetCustomerList() throws SQLException {
        List<List<String>> customer_data = GetResultsFromQuery("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers");
        ObservableList<Customer> customer_list = FXCollections.observableArrayList();

        for (List<String> val : customer_data) {
            customer_list.add(new Customer(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5)));
        }

        return customer_list;
    }

    public static ObservableList<Division> GetDivisionList() throws SQLException {
        List<List<String>> division_data = GetResultsFromQuery("SELECT Division_ID, Division, Country_ID FROM first_level_divisions");
        List<Division> division_list = new ArrayList<>();

        for (List<String> val : division_data) {
            division_list.add(new Division(val.get(0), val.get(1), val.get(2)));
        }

        return FXCollections.observableList(division_list);
    }

    public static ObservableList<Country> GetCountryList() throws SQLException {
        List<List<String>> country_data = GetResultsFromQuery("SELECT Country_ID, Country FROM countries");
        List<Country> country_list = new ArrayList<>();

        for (List<String> val : country_data) {
            country_list.add(new Country(val.get(0), val.get(1)));
        }

        return FXCollections.observableList(country_list);
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
        List<List<String>> appointment_data = GetResultsFromQuery("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID FROM appointments");
        List<Appointment> appointment_list = new ArrayList<>();

        for (List<String> val : appointment_data) {
            appointment_list.add(new Appointment(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5), val.get(6), val.get(7), val.get(8), val.get(9)));
        }

        return FXCollections.observableList(appointment_list);
    }

    public static void SetCurrentUser(User new_user) {
        current_user = new_user;
    }

    public static PreparedStatement FormatInsertStatement(String table, List<String> columns, List<String> values) throws SQLException {
        String column_string = String.join(", ", columns);
        String value_string = String.join(", ", Collections.nCopies(values.size(), "?"));
        String unprepared = String.format("INSERT INTO %s(%s) VALUES(%s)", table, column_string, value_string);
        PreparedStatement prepared = db_connection.prepareStatement(unprepared);

        for (int i = 0; i < values.size(); i++) {
            prepared.setString(i + 1, values.get(i));
        }

        return prepared;
    }

    public static PreparedStatement FormatUpdateStatement(String table, String primary_key_name, int primary_key_value, List<String> columns, List<String> values) throws SQLException {
        StringBuilder assignments = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            assignments.append(String.format("%s=?", columns.get(i)));
            if (i < columns.size() - 1) {
                assignments.append(", ");
            }
        }

        String unprepared = String.format("UPDATE %s SET %s WHERE %s=%s", table, assignments, primary_key_name, primary_key_value);
        PreparedStatement prepared = db_connection.prepareStatement(unprepared);

        for (int i = 0; i < values.size(); i++) {
            prepared.setString(i + 1, values.get(i));
        }

        return prepared;
    }

    public static Timestamp ParseDate(String date_string) throws ParseException {
        java.util.Date util_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_string);
        return new Timestamp(util_date.getTime());
    }

    public static String FormatDateToTimezone(Timestamp timestamp) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd 'at' h:mm a");//"yyyy-MM-dd hh:mm:ss aa");
        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        return date_format.format(new Timestamp(timestamp.getTime() + timezone_offset));
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

        PreparedStatement new_customer_prepared = FormatInsertStatement("customers", columns, values);
        new_customer_prepared.executeUpdate();
    }

    public static void UpdateExistingCustomer(String name, String address, String zipcode, String phonenum, Division division, int customer_id) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = current_user.getUsername();
        String division_id = String.valueOf(division.getId());

        List<String> columns = Arrays.asList("Customer_Name", "Address", "Postal_Code", "Phone", "Last_Update", "Last_Updated_By", "Division_ID");
        List<String> values = Arrays.asList(name, address, zipcode, phonenum, current_date, c_username, division_id);

        PreparedStatement update_customer_prepared = FormatUpdateStatement("customers", "Customer_ID", customer_id, columns, values);
        update_customer_prepared.executeUpdate();
    }

    public static void AddAppointmentToDatabase(String title, String desc, String location, String type, String start_time, String end_time, Customer customer, Contact contact) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = current_user.getUsername();
        String c_userid = String.valueOf(current_user.getId());
        String customer_id = String.valueOf(customer.getId());
        String contact_id = String.valueOf(contact.getId());

        List<String> columns = Arrays.asList("Title", "Description", "Location", "Type", "Start", "End", "Create_Date", "Created_By", "Last_Update", "Last_Updated_By", "Customer_ID", "User_ID", "Contact_ID");
        List<String> values = Arrays.asList(title, desc, location, type, start_time, end_time, current_date, c_username, current_date, c_username, customer_id, c_userid, contact_id);

        PreparedStatement new_appointment_prepared = FormatInsertStatement("appointments", columns, values);
        new_appointment_prepared.executeUpdate();
    }

    public static void UpdateExistingAppointment(String title, String desc, String location, String type, String start_time, String end_time, Customer customer, Contact contact, int appt_id) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = current_user.getUsername();
        String c_userid = String.valueOf(current_user.getId());
        String customer_id = String.valueOf(customer.getId());
        String contact_id = String.valueOf(contact.getId());

        List<String> columns = Arrays.asList("Title", "Description", "Location", "Type", "Start", "End", "Last_Update", "Last_Updated_By", "Customer_ID", "User_ID", "Contact_ID");
        List<String> values = Arrays.asList(title, desc, location, type, start_time, end_time, current_date, c_username, customer_id, c_userid, contact_id);

        PreparedStatement update_appointment_prepared = FormatUpdateStatement("appointments", "Appointment_ID", appt_id, columns, values);
        update_appointment_prepared.executeUpdate();
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
    public static boolean DeleteAppointmentWithID(int id) {
        try {
            PreparedStatement statement = db_connection.prepareStatement("DELETE FROM appointments WHERE Appointment_ID=" + id);
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