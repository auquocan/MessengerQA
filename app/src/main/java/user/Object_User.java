package user;

/**
 * Created by MyPC on 27/04/2016.
 */
public class Object_User {
    public String fullName;
    public String dataOfBirth;
    public String sexual;
    public String avataUser;
    public String userEmail;
    public Object_User(){};

    public Object_User(String avataUser, String dataOfBirth, String fullName, String sexual,  String userEmail) {
        this.avataUser = avataUser;
        this.dataOfBirth = dataOfBirth;
        this.fullName = fullName;
        this.sexual = sexual;
        this.userEmail = userEmail;
    }
}
