package scheduler;

/**
 * This class corresponds to the objects stored in the contacts table of the database
 */
public class Contact {
    private final int id;
    private final String name;
    private final String email;

    public Contact(String id, String name, String email) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
