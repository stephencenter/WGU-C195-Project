package scheduler;

import java.sql.Timestamp;
import java.text.ParseException;

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

}
