package formality.content.database;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: 10/5/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class CCSS {
    private int id;
    private String code;
    private int grade;

    public CCSS(int id, String code, int grade) {
        this.id = id;
        this.code = code;
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
