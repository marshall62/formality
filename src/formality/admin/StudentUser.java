package formality.admin;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 13, 2010
 * Time: 10:30:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudentUser extends User {
    String title1= null;
    String nativeLang = "English";
    int age = -1;
    String sped = null;
    String gender="F";
    String initials=null;
    String iep;
    String mathRating;
    String readingRating;

    public StudentUser(String loginName, String password, String districtName, String institution) {
        super(institution, loginName, password, districtName);
    }

    public StudentUser(String institution, String loginName, String password, String districtName,
                       String title1, String nativeLang, String gender, int age, String sped,
                       String initials) {
        super(institution, loginName, password, districtName);
        this.title1 = title1;
        this.nativeLang = nativeLang;
        this.age = age;
        this.sped = sped;
        this.gender= gender;
        this.initials = initials;
    }

    public StudentUser(String institution, String loginName, String password, String districtName, String title1,
                       String nativeLang, int age, String gender, String initials, String iep,
                       String mathRating, String readingRating) {
        super(institution, loginName, password, districtName);
        this.title1 = title1;
        this.nativeLang = nativeLang;
        this.age = age;
        this.gender = gender;
        this.initials = initials;
        this.iep = iep;
        this.mathRating = mathRating;
        this.readingRating = readingRating;
    }

    public String getInitials() {
        return initials;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }
}
