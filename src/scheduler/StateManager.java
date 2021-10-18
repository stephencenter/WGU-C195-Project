package scheduler;

/**
 * This class stores some of the information that we want to maintain
 * between forms, such as the currently logged in user and which
 * object we had selected before switching forms
 */
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

    /**
     * This method returns the currently stored customer, and also stops storing it.
     * Because this is the only way to retrieve the stored customer outside of this class,
     * the stored customer is effectively only retrievable once (this is intentional)
     * @return the stored customer
     */
    public static Customer RetrieveStoredCustomer() {
        Customer return_value = stored_customer;
        stored_customer = null;
        return return_value;
    }

    public static void SetStoredAppointment(Appointment appointment) {
        stored_appointment = appointment;
    }

    /**
     * This method returns the currently stored appointment, and also stops storing it.
     * Because this is the only way to retrieve the stored appointment outside of this class,
     * the stored appointment is effectively only retrievable once (this is intentional)
     * @return the stored appointment
     */
    public static Appointment RetrieveStoredAppointment() {
        Appointment return_value = stored_appointment;
        stored_appointment = null;
        return return_value;
    }

    public static void SetStoredContact(Contact contact) {
        stored_contact = contact;
    }

    /**
     * This method returns the currently stored contact, and also stops storing it.
     * Because this is the only way to retrieve the stored contact outside of this class,
     * the stored contact is effectively only retrievable once (this is intentional)
     * @return the stored contact
     */
    public static Contact RetrieveStoredContact() {
        Contact return_value = stored_contact;
        stored_contact = null;
        return return_value;

    }
}
