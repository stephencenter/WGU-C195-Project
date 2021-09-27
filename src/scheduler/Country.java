package scheduler;

import java.sql.Timestamp;
import java.text.ParseException;

public class Country {
    private int id;
    private String country;
    private Timestamp created_time;
    private String created_by;
    private Timestamp updated_time;
    private String updated_by;

    public Country(String id, String country, String created_time, String created_by, String updated_time, String updated_by) throws ParseException {
        this.id = Integer.parseInt(id);
        this.country = country;
        this.created_time = Database.ParseDate(created_time);
        this.created_by = created_by;
        this.updated_time = Database.ParseDate(updated_time);
        this.updated_by = updated_by;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Timestamp getCreatedTime() { return created_time; }
    public void setCreatedTime(Timestamp created_time) { this.created_time = created_time; }

    public String getCreatedBy() { return created_by; }
    public void setCreatedBy(String created_by) { this.created_by = created_by; }

    public Timestamp getUpdatedTime() { return updated_time; }
    public void setUpdatedTime(Timestamp updated_time) { this.updated_time = updated_time; }

    public String getUpdatedBy() { return updated_by; }
    public void setUpdatedBy(String updated_by) { this.updated_by = updated_by; }
}
