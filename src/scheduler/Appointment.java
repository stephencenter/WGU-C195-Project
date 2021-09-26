package scheduler;

import java.sql.Timestamp;
import java.text.ParseException;

public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String app_type;
    private Timestamp start_time;
    private Timestamp end_time;
    private Timestamp created_time;
    private String created_by;
    private Timestamp updated_time;
    private String updated_by;
    private int customer_id;
    private int user_id;
    private int contact_id;

    public Appointment(String id, String title, String description, String location, String app_type, String start_time,
        String end_time, String created_time, String created_by, String updated_time, String updated_by, String customer_id, String user_id, String contact_id) throws ParseException {
        this.id = Integer.parseInt(id);
        this.title = title;
        this.description = description;
        this.location = location;
        this.app_type = app_type;
        this.start_time = Database.ParseDate(start_time);
        this.end_time = Database.ParseDate(end_time);
        this.created_time = Database.ParseDate(created_time);
        this.created_by = created_by;
        this.updated_time = Database.ParseDate(updated_time);
        this.updated_by = updated_by;
        this.customer_id = Integer.parseInt(customer_id);
        this.user_id = Integer.parseInt(user_id);
        this.contact_id = Integer.parseInt(contact_id);
    }

    public int getId() { return id; }
    public void setId(int new_value) { this.id = new_value; }

    public String getTitle() { return title; }
    public void setTitle(String new_value) { this.title = new_value; }

    public String getDescription() { return description; }
    public void setDescription(String new_value) { this.description = new_value; }

    public String getLocation() { return location; }
    public void setLocation(String new_value) { this.location = new_value; }

    public String getAppointmentType() { return app_type; }
    public void setAppointmentType(String new_value) { this.app_type = new_value; }

    public Timestamp getStartTime() { return start_time; }
    public void setStartTime(Timestamp new_value) { this.start_time = new_value; }

    public Timestamp getEndTime() { return end_time; }
    public void setEndTime(Timestamp new_value) { this.end_time = new_value; }

    public Timestamp getCreatedTime() { return created_time; }
    public void setCreatedTime(Timestamp new_value) { this.created_time = new_value; }

    public String getCreatedBy() { return created_by; }
    public void setCreatedBy(String new_value) { this.created_by = new_value; }

    public Timestamp getUpdatedTime() { return updated_time; }
    public void setUpdatedTime(Timestamp new_value) { this.updated_time = new_value; }

    public String getUpdatedBy() { return updated_by; }
    public void setUpdatedBy(String new_value) { this.updated_by = new_value; }

    public int getCustomerId() { return customer_id; }
    public void setCustomerId(int new_value) { this.customer_id = new_value; }

    public int getUserId() { return user_id; }
    public void setUserId(int new_value) { this.user_id = new_value; }

    public int getContactId() { return contact_id; }
    public void setContactId(int new_value) { this.contact_id = new_value; }

}
