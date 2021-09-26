package scheduler;

import java.sql.Timestamp;
import java.text.ParseException;

public class User {
    private int id;
    private String username;
    private String password;
    private Timestamp created_time;
    private String created_by;
    private Timestamp updated_time;
    private String updated_by;

    public User(String id, String username, String password, String created_time, String created_by, String updated_time, String updated_by) throws ParseException {
        this.id = Integer.parseInt(id);
        this.username = username;
        this.password = password;
        this.created_time = Database.ParseDate(created_time);
        this.created_by = created_by;
        this.updated_time = Database.ParseDate(updated_time);
        this.updated_by = updated_by;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Timestamp getCreatedTime() {
        return created_time;
    }
    public void setCreatedTime(Timestamp created_time) {
        this.created_time = created_time;
    }

    public String getCreatedBy() {
        return created_by;
    }
    public void setCreatedBy(String created_by) {
        this.created_by = created_by;
    }

    public Timestamp getUpdatedTime() {
        return updated_time;
    }
    public void setUpdatedTime(Timestamp updated_time) {
        this.updated_time = updated_time;
    }

    public String getUpdatedBy() {
        return updated_by;
    }
    public void setUpdatedBy(String updated_by) {
        this.updated_by = updated_by;
    }
}
