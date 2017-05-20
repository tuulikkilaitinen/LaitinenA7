package sdccd.edu.laitinena7.Utils;

/**
 * Created by Tuulikki Laitinen on 5/18/2017.
 */

public class User {

    private String id;
    private String name;
    private String location;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUserId(String userId) {
        this.id = userId;
    }

    public String getUserId() {
        return this.id;
    }

    public String getUserName() {
        return this.name;
    }

    public String getUserLocation() {
        return this.location;
    }
}
