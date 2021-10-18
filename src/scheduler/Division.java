package scheduler;

/**
 * This class corresponds to the objects stored in the divisions table of the database
 */
public class Division {
    private final int id;
    private final String name;
    private final int country_id;

    public Division(String id, String division, String country_id)  {
        this.id = Integer.parseInt(id);
        this.name = division;
        this.country_id = Integer.parseInt(country_id);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getCountryId() { return country_id; }
}
