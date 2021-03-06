package scheduler;

/**
 * This class corresponds to the objects stored in the countries table of the database
 */
public class Country {
    private final int id;
    private final String name;

    public Country(String id, String name) {
        this.id = Integer.parseInt(id);
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}
