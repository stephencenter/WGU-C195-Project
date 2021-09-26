package scheduler;

import java.sql.Timestamp;
import java.text.ParseException;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String zipcode;
    private String phone_num;
    private Timestamp created_time;
    private String created_by;
    private Timestamp updated_time;
    private String updated_by;
    private int division_id;

    public Customer(String id, String name, String address, String zipcode, String phone_num, String created_time,
        String created_by, String updated_time, String updated_by, String division_id) throws ParseException {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.address = address;
        this.zipcode = zipcode;
        this.phone_num = phone_num;
        this.created_time = Database.ParseDate(created_time);
        this.created_by = created_by;
        this.updated_time = Database.ParseDate(updated_time);
        this.updated_by = updated_by;
        this.division_id = Integer.parseInt(division_id);
    }

    public int getId() { return id; }
    public void setId(int new_value) { this.id = new_value; }

    public String getName() { return name; }
    public void setName(String new_value) { this.name = new_value; }

    public String getAddress() { return address; }
    public void setAddress(String new_value) { this.address = new_value; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String new_value) { this.zipcode = new_value; }

    public String getPhoneNum() { return phone_num; }
    public void setPhoneNum(String new_value) { this.phone_num = new_value; }

    public Timestamp getCreatedTime() { return created_time; }
    public void setCreatedTime(Timestamp new_value) { this.created_time = new_value; }

    public String getCreatedBy() { return created_by; }
    public void setCreatedBy(String new_value) { this.created_by = new_value; }

    public Timestamp getUpdatedTime() { return updated_time; }
    public void setUpdatedTime(Timestamp new_value) { this.updated_time = new_value; }

    public String getUpdatedBy() { return updated_by; }
    public void setUpdatedBy(String new_value) { this.updated_by = new_value; }

    public int getDivisionId() { return division_id; }
    public void setDivisionId(int new_value) { this.division_id = new_value; }
}
