package scheduler;

public class StateManager {
    private static User current_user = null;
    private static Customer stored_customer = null;
    private static Appointment stored_appointment = null;
    private static Contact stored_contact = null;

    public static void SetCurrentUser(User new_user) {
        current_user = new_user;
    }

    public static User GetCurrentUser() {
        return current_user;
    }

    public static void SetStoredCustomer(Customer customer) {
        stored_customer = customer;
    }

    public static Customer RetrieveStoredCustomer() {
        Customer return_value = stored_customer;
        stored_customer = null;
        return return_value;
    }

    public static void SetStoredAppointment(Appointment appointment) {
        stored_appointment = appointment;
    }

    public static Appointment RetrieveStoredAppointment() {
        Appointment return_value = stored_appointment;
        stored_appointment = null;
        return return_value;
    }

    public static void SetStoredContact(Contact contact) {
        stored_contact = contact;
    }

    public static Contact RetrieveStoredContact() {
        Contact return_value = stored_contact;
        stored_contact = null;
        return return_value;

    }
}
