package formality.admin;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 20, 2010
 * Time: 9:46:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class TeacherUser extends User {
    public String fname;
    public String lname;

    public TeacherUser(String fname, String lname, String loginName, String password, String institution, String districtName) {
        super(institution,loginName,password,districtName);
        this.fname = fname;
        this.lname = lname;

    }
    
}
