package scheduler;

import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;

/**
 * This class corresponds to the objects stored in the appointments table of the database
 */
public class Appointment {
    private final int id;
    private final String title;
    private final String description;
    private final String location;
    private final String app_type;
    private final Timestamp start_time;
    private final Timestamp end_time;
    private final int customer_id;
    private final int user_id;
    private final int contact_id;

    public Appointment(String id, String title, String description, String location, String app_type, String start_time,
        String end_time, String customer_id, String user_id, String contact_id) throws ParseException {
        this.id = Integer.parseInt(id);
        this.title = title;
        this.description = description;
        this.location = location;
        this.app_type = app_type;
        this.start_time = Database.ParseDate(start_time);
        this.end_time = Database.ParseDate(end_time);
        this.customer_id = Integer.parseInt(customer_id);
        this.user_id = Integer.parseInt(user_id);
        this.contact_id = Integer.parseInt(contact_id);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getAppointmentType() { return app_type; }
    public Timestamp getStartTime() { return start_time; }
    public Timestamp getEndTime() { return end_time; }
    public int getCustomerId() { return customer_id; }
    public int getUserId() { return user_id; }
    public int getContactId() { return contact_id; }

    /**
     * This method searches through all the contacts to find the one with the matching contact id,
     * then returns that contact's name
     * @return the name of this appointment's contact
     * @throws SQLException could be thrown when retrieving the list from the database
     */
    public String getContactName() throws SQLException {
        ObservableList<Contact> contact_list = Database.GetContactList();
        for (Contact contact : contact_list) {
            if (contact.getId() == contact_id) {
                return contact.getName();
            }
        }
        return null;
    }

    /**
     * This method returns a human readable string version of the start_time formatted to the user's timezone
     * @return a human readable start_time
     */
    public String getStartTimeLocal() {
        return Database.FormatDateToTimezone(start_time);
    }

    /**
     * This method returns a human readable string version of the end_time formatted to the user's timezone
     * @return a human readable end_time
     */
    public String getEndTimeLocal() {
        return Database.FormatDateToTimezone(end_time);
    }
}
