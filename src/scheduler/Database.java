package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The Database class is resposible for interfacing with the MySQL database.
 * Its methods retrieve, modify, and add data from the database.
 */
public class Database {
    private static final String db_url = "jdbc:mysql://localhost:3306/";
    private static final String db_name = "client_schedule";
    private static final String db_username = "sqlUser";
    private static final String db_password = "Passw0rd!";
    private static Connection db_connection;

    /**
     * This method initiates a connection to the database so we can interact with it
     */
    public static void ConnectToDatabase() throws SQLException {
        db_connection = DriverManager.getConnection(db_url + db_name, db_username, db_password);
    }

    /**
     * This method queries the database to find which user has the matching
     * login information. Returns null if nothing is found
     * @param username the desired username
     * @param password the desired password
     * @return the user whose login information matches the arguments
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static User GetUserWithLoginInfo(String username, String password) throws SQLException {
        List<List<String>> user_data = GetResultsFromQuery("SELECT User_ID, User_Name, Password FROM users WHERE User_Name='" + username + "' AND Password='" + password + "'");

        if (user_data.size() == 0) {
            return null;
        }

        List<String> val = user_data.get(0);
        return new User(val.get(0), val.get(1), val.get(2));
    }

    /**
     * Queries the customer table for a list of all customers
     * @return a list of every customer in the database
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static ObservableList<Customer> GetCustomerList() throws SQLException {
        List<List<String>> customer_data = GetResultsFromQuery("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers");
        ObservableList<Customer> customer_list = FXCollections.observableArrayList();

        for (List<String> val : customer_data) {
            customer_list.add(new Customer(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5)));
        }

        return customer_list;
    }

    /**
     * Queries the division table for a list of all divisions
     * @return a list of every division in the database
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static ObservableList<Division> GetDivisionList() throws SQLException {
        List<List<String>> division_data = GetResultsFromQuery("SELECT Division_ID, Division, Country_ID FROM first_level_divisions");
        List<Division> division_list = new ArrayList<>();

        for (List<String> val : division_data) {
            division_list.add(new Division(val.get(0), val.get(1), val.get(2)));
        }

        return FXCollections.observableList(division_list);
    }

    /**
     * Queries the country table for a list of all countries
     * @return a list of every country in the database
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static ObservableList<Country> GetCountryList() throws SQLException {
        List<List<String>> country_data = GetResultsFromQuery("SELECT Country_ID, Country FROM countries");
        List<Country> country_list = new ArrayList<>();

        for (List<String> val : country_data) {
            country_list.add(new Country(val.get(0), val.get(1)));
        }

        return FXCollections.observableList(country_list);
    }

    /**
     * Queries the contact table for a list of all contacts
     * @return a list of every contact in the database
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static ObservableList<Contact> GetContactList() throws SQLException {
        List<List<String>> contact_data = GetResultsFromQuery("SELECT * FROM contacts");
        List<Contact> contact_list = new ArrayList<>();

        for (List<String> val : contact_data) {
            contact_list.add(new Contact(val.get(0), val.get(1), val.get(2)));
        }

        return FXCollections.observableList(contact_list);
    }

    /**
     * Queries the appointment table for a list of all appointments
     * @return a list of every customer in the database
     * @throws SQLException Interacting with the database could throw a SQLException
     * @throws ParseException Parsing the appointment dates could throw a ParseException
     */
    public static ObservableList<Appointment> GetAppointmentList() throws SQLException, ParseException {
        List<List<String>> appointment_data = GetResultsFromQuery("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID FROM appointments");
        List<Appointment> appointment_list = new ArrayList<>();

        for (List<String> val : appointment_data) {
            appointment_list.add(new Appointment(val.get(0), val.get(1), val.get(2), val.get(3), val.get(4), val.get(5), val.get(6), val.get(7), val.get(8), val.get(9)));
        }

        return FXCollections.observableList(appointment_list);
    }

    /**
     * This method takes a list of column and a list of values and uses them to format an INSERT statement
     * so we can add a new object to the database. This method allows us to easily format INSERT statements
     * with arbitrary number of columns and values
     * @param table the name of the table
     * @param columns a list of all the column names
     * @param values a list of all the object's values for each column
     * @return a PreparedStatement that when executed will add the object to the database
     * @throws SQLException Preparing the statement could throw a SQLException
     */
    public static PreparedStatement FormatInsertStatement(String table, List<String> columns, List<String> values) throws SQLException {
        String column_string = String.join(", ", columns);
        String value_string = String.join(", ", Collections.nCopies(values.size(), "?"));
        String unprepared = String.format("INSERT INTO %s(%s) VALUES(%s)", table, column_string, value_string);
        PreparedStatement prepared = db_connection.prepareStatement(unprepared);

        // We use setString() to format the values to avoid any SQL injections or similar
        for (int i = 0; i < values.size(); i++) {
            prepared.setString(i + 1, values.get(i));
        }

        return prepared;
    }

    /**
     * This method takes a list of columns and a list of values and uses them to format an UPDATE statement
     * so we can update an existing object in the database. This method allows us to easily format UPDATE statements
     * with arbitrary number of columns and values
     * @param table the name of the table
     * @param primary_key_name the name of the table's primary key column
     * @param primary_key_value the object's value for the primary key
     * @param columns a list of all the column names
     * @param values a list of all the object's values for each column
     * @return a PreparedStatement that when executed will update the object's values in the database
     * @throws SQLException Preparing the statement could throw a SQLException
     */
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

        // We use setString() to format the values to avoid any SQL injections or similar
        for (int i = 0; i < values.size(); i++) {
            prepared.setString(i + 1, values.get(i));
        }

        return prepared;
    }

    /**
     * This method converts SQL's date strings to Timestamps.
     * SQL stores dates in a specific string format, this method parses it and
     * converts it to a timestamp so we can work with it
     * @param date_string a date string in SQL's specific format (yyyy-MM-dd HH:mm:ss)
     * @return a Timestamp equivalent to the date string
     * @throws ParseException Parsing the date could throw an exception
     */
    public static Timestamp ParseDate(String date_string) throws ParseException {
        java.util.Date util_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_string);
        return new Timestamp(util_date.getTime());
    }

    /**
     * This method takes a Timestamp object and converts it to a human readable string
     * in the user's timezone
     * @param timestamp the Timestamp to format
     * @return a human-readable string equivalent of the Timestamp
     */
    public static String FormatDateToTimezone(Timestamp timestamp) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd 'at' h:mm a");//"yyyy-MM-dd hh:mm:ss aa");
        int timezone_offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        return date_format.format(new Timestamp(timestamp.getTime() + timezone_offset));
    }

    /**
     * This method takes a string and uses it to query the database.
     * Each item found in this query is stored as a list of strings,
     * each of which are added to a bigger list that is then returned
     * @param query the query that we want the results from
     * @return a list of every object found in the query, where each object is stored as list of values
     * @throws SQLException Querying the database could throw a SQLException
     */
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

    /**
     * This method takes a series of values and uses them to add a new customer to the database
     * @param name the customer's name
     * @param address the customer's address
     * @param zipcode the customer's zipcode
     * @param phonenum the customer's phone number
     * @param division the customer's division id
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static void AddCustomerToDatabase(String name, String address, String zipcode, String phonenum, Division division) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = StateManager.GetCurrentUser().getUsername();
        String division_id = String.valueOf(division.getId());

        List<String> columns = Arrays.asList("Customer_Name", "Address", "Postal_Code", "Phone", "Create_Date", "Created_By", "Last_Update", "Last_Updated_By", "Division_ID");
        List<String> values = Arrays.asList(name, address, zipcode, phonenum, current_date, c_username, current_date, c_username, division_id);

        PreparedStatement new_customer_prepared = FormatInsertStatement("customers", columns, values);
        new_customer_prepared.executeUpdate();
    }

    /**
     * This method finds the customer with the matching customer_id in the database and updates its values
     *
     * @param name the customer's new name
     * @param address the customer's new address
     * @param zipcode the customer's new zipcode
     * @param phonenum the customer's new phone number
     * @param division the customer's new division id
     * @param customer_id the customer's new customer id
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static void UpdateExistingCustomer(String name, String address, String zipcode, String phonenum, Division division, int customer_id) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = StateManager.GetCurrentUser().getUsername();
        String division_id = String.valueOf(division.getId());

        List<String> columns = Arrays.asList("Customer_Name", "Address", "Postal_Code", "Phone", "Last_Update", "Last_Updated_By", "Division_ID");
        List<String> values = Arrays.asList(name, address, zipcode, phonenum, current_date, c_username, division_id);

        PreparedStatement update_customer_prepared = FormatUpdateStatement("customers", "Customer_ID", customer_id, columns, values);
        update_customer_prepared.executeUpdate();
    }

    /**
     * This method takes a series of values and uses them to add a new Appointment to the database
     * @param title the appointment's title
     * @param desc the appointment's description
     * @param location the appointment's location
     * @param type the appointment's type
     * @param start_time the appointment's starting time
     * @param end_time the appointment's ending time
     * @param customer the appointment's customer id
     * @param contact the appointment's contact id
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static void AddAppointmentToDatabase(String title, String desc, String location, String type, String start_time, String end_time, Customer customer, Contact contact) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = StateManager.GetCurrentUser().getUsername();
        String c_userid = String.valueOf(StateManager.GetCurrentUser().getId());
        String customer_id = String.valueOf(customer.getId());
        String contact_id = String.valueOf(contact.getId());

        List<String> columns = Arrays.asList("Title", "Description", "Location", "Type", "Start", "End", "Create_Date", "Created_By", "Last_Update", "Last_Updated_By", "Customer_ID", "User_ID", "Contact_ID");
        List<String> values = Arrays.asList(title, desc, location, type, start_time, end_time, current_date, c_username, current_date, c_username, customer_id, c_userid, contact_id);

        PreparedStatement new_appointment_prepared = FormatInsertStatement("appointments", columns, values);
        new_appointment_prepared.executeUpdate();
    }

    /**
     * This method finds the appointment with the matching appt_id in the database and updates its values
     * @param title the appointment's new title
     * @param desc the appointment's new descrption
     * @param location the appointment's new location
     * @param type the appointment's new type
     * @param start_time the appointment's new start-time
     * @param end_time the appointment's new end-time
     * @param customer the appointment's new customer-id
     * @param contact the appointment's new contact-id
     * @param appt_id the appointment's new ID
     * @throws SQLException Interacting with the database could throw a SQLException
     */
    public static void UpdateExistingAppointment(String title, String desc, String location, String type, String start_time, String end_time, Customer customer, Contact contact, int appt_id) throws SQLException {
        String current_date = new Timestamp(new java.util.Date().getTime()).toString();
        String c_username = StateManager.GetCurrentUser().getUsername();
        String c_userid = String.valueOf(StateManager.GetCurrentUser().getId());
        String customer_id = String.valueOf(customer.getId());
        String contact_id = String.valueOf(contact.getId());

        List<String> columns = Arrays.asList("Title", "Description", "Location", "Type", "Start", "End", "Last_Update", "Last_Updated_By", "Customer_ID", "User_ID", "Contact_ID");
        List<String> values = Arrays.asList(title, desc, location, type, start_time, end_time, current_date, c_username, customer_id, c_userid, contact_id);

        PreparedStatement update_appointment_prepared = FormatUpdateStatement("appointments", "Appointment_ID", appt_id, columns, values);
        update_appointment_prepared.executeUpdate();
    }

    /**
     * Deletes the specified customer from the database
     * @param id the ID of the customer to be deleted
     * @return true if the deletion succeeded, false otherwise
     */
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

    /**
     * Deletes the specified appointment from the database
     * @param id the ID of the appointment to be deleted
     * @return true if the deletion succeeded, false otherwise
     */
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

}