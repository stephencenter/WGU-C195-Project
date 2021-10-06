package scheduler;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final int id;
    private final String name;
    private final String address;
    private final String zipcode;
    private final String phone_num;
    private final int division_id;

    public Customer(String id, String name, String address, String zipcode, String phone_num, String division_id) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.address = address;
        this.zipcode = zipcode;
        this.phone_num = phone_num;
        this.division_id = Integer.parseInt(division_id);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getZipcode() { return zipcode; }
    public String getPhoneNum() { return phone_num; }
    public int getDivisionId() { return division_id; }

    public Division GetDivision() throws SQLException {
        for (Division division : Database.GetDivisionList()) {
            if (division.getId() == division_id) {
                return division;
            }
        }
        return null;
    }

    public Country GetCountry() throws SQLException {
        for (Country country : Database.GetCountryList()) {
            if (country.getId() == GetDivision().getCountryId()) {
                return country;
            }
        }
        return null;
    }

    public String getCountryName() throws SQLException {
        return GetCountry().getName();
    }

    public String getDivisionName() throws SQLException {
        return GetDivision().getName();
    }

    public List<Appointment> GetAppointments() throws SQLException, ParseException {
        List<Appointment> appt_list = new ArrayList<>();
        for (Appointment appointment : Database.GetAppointmentList()) {
            if (appointment.getCustomerId() == id) {
                appt_list.add(appointment);
            }
        }
        return appt_list;

    }

    public boolean HasOverlappingAppointments(Timestamp new_start, Timestamp new_end, int exclude) throws SQLException, ParseException {
        for (Appointment appt : GetAppointments()) {
            if (appt.getId() == exclude) {
                continue;
            }
            if (appt.getStartTime().equals(new_start) || appt.getEndTime().equals(new_end)) {
                return true;
            }
            if (new_start.after(appt.getStartTime()) && new_start.before(appt.getEndTime())) {
                return true;
            }
            if (new_end.after(appt.getStartTime()) && new_end.before(appt.getEndTime())) {
                return true;
            }
            System.out.println(appt.getStartTime() + " " + appt.getEndTime() + " " + new_start + " " + new_end);
        }
        return false;
    }
}
