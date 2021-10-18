package scheduler;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class corresponds to the objects stored in the customer table of the database
 */
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

    /**
     * This method searches the database for the Division with the matching division id, then returns it
     * @return this customer's division
     * @throws SQLException could be thrown when retrieving the list from the database
     */
    public Division GetDivision() throws SQLException {
        for (Division division : Database.GetDivisionList()) {
            if (division.getId() == division_id) {
                return division;
            }
        }
        return null;
    }

    /**
     * This method searches the database for the Country with the matching division id, then returns it
     * @return this customer's country
     * @throws SQLException could be thrown when retrieving the list from the database
     */
    public Country GetCountry() throws SQLException {
        for (Country country : Database.GetCountryList()) {
            if (country.getId() == GetDivision().getCountryId()) {
                return country;
            }
        }
        return null;
    }

    /**
     * @return the name of this customer's country
     * @throws SQLException could be thrown when retrieving the customer's country from the database
     */
    public String getCountryName() throws SQLException {
        return GetCountry().getName();
    }
    /**
     *
     * @return the name of this customer's division
     * @throws SQLException could be thrown when retrieving the customer's division from the database
     */
    public String getDivisionName() throws SQLException {
        return GetDivision().getName();
    }

    /**
     * This method queries the database for every appointment whose customer_id matches this customer,
     * and returns them all in a list
     * @return a list of all of this customer's appointments
     * @throws SQLException could be thrown when retrieving the appointment list
     * @throws ParseException could be thrown when retrieving the appointment list
     */
    public List<Appointment> GetAppointments() throws SQLException, ParseException {
        List<Appointment> appt_list = new ArrayList<>();
        for (Appointment appointment : Database.GetAppointmentList()) {
            if (appointment.getCustomerId() == id) {
                appt_list.add(appointment);
            }
        }
        return appt_list;

    }

    /**
     * This method accepts a start time and an end time, then searches through the list of all the customer's
     * appointments to see if any of them overlap with these times. This is used to prevent
     * accidentally scheduling two appointments at the same time
     * @param new_start the start time of the new appointment
     * @param new_end the end time of the new appointment
     * @param exclude any appointment with this ID will be ignored when checking for overlaps.
     *                This is necessary for editing existing appointments
     * @return true if overlapping appointments were found, false otherwise
     * @throws SQLException could be thrown when retrieving the appointment list
     * @throws ParseException could be thrown when retrieving the appointment list
     */
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
        }
        return false;
    }
}
