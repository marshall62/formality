package formality.admin;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 20, 2010
 * Time: 9:53:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class User {
    int id;
    String loginName;
    String password;
    String districtName;
    int districtID;
    String institution;

    public User(String institution, String loginName, String password, String districtName) {
        this.institution = institution;
        this.loginName = loginName;
        this.password = password;
        this.districtName = districtName;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId () {
        return this.id;
    }

    public String getLoginName() {
        return loginName;
    }
}
