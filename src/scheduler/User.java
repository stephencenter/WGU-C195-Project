package scheduler;

/**
 * This class corresponds to the objects stored in the users table of the database
 */
public class User {
    private final int id;
    private final String username;
    private final String password;

    public User(String id, String username, String password) {
        this.id = Integer.parseInt(id);
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
