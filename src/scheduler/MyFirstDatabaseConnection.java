package scheduler;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class MyFirstDatabaseConnection {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://wgudb.ucertify.com:3306/WJ079MN";
        Connection conn = DriverManager.getConnection(url, "U079MN", "53688965284");

        Date created_date = new Date(1631221000000L);
        Date updated_date = new Date(1631221967126L);
        List<String> updates = Arrays.asList(
            //"INSERT INTO users VALUES ('0', 'stephen', 'sloth101', '" + created_date + "', 'stephen', '" + updated_date + "', 'stephen')"

            //"INSERT INTO contacts VALUES ('3', 'Borston Borg', 'bornstonborg@gmail.com')",
            //"INSERT INTO contacts VALUES ('2', 'Snood von Wallop', 'snoodvonwallop@gmail.com')"
        );

        for (String update : updates) {
            PreparedStatement statement = conn.prepareStatement(update);
            statement.executeUpdate();
        }

        List<String> queries = Arrays.asList(
            "SELECT Contact_Name FROM contacts"
        );

        for (String query : queries) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                System.out.println(results.getString(1));
            }
        }
    }
}
