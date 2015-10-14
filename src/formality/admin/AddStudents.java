package formality.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Feb 16, 2011
 * Time: 4:42:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddStudents extends StudentAdmin {
    int classId;

    public AddStudents (int classId) {
        this.classId = classId;
    }

     public int insertNewUser(Connection conn, User student) throws SQLException {
         int id = insertNewUserToDb(conn, (StudentUser) student);
         student.setId(id);
         insertStudentFrameworkDataToDb(conn,id);
         insertStudentCourse(conn,student);
         return id;
     }

    private void insertStudentCourse(Connection conn, User student) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "insert into usercourselinks (userid, courseid) values (?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,student.id);
            stmt.setInt(2,this.classId);
            stmt.execute();
        }

        finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static void main (String[] args) {
        String csvfilename = args[0];
        String classIdArg= args[1];
        int classId= Integer.parseInt(classIdArg);
        AddStudents a = new AddStudents(classId);

        try {
            FileInputStream fis = new FileInputStream(csvfilename);
            List<User> students = AddStudents.readStudentCSVStream(fis,"Amherst","1");
            Connection conn = a.getConnection();
            a.doInsert(conn,students);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Must pass two args to the program a file and the class id
    public static void main2(String[] args) {
        String csvfilename = args[0];
        String classIdArg= args[1];
        int classId= Integer.parseInt(classIdArg);
        AddStudents a = new AddStudents(classId);
        a.classId = Integer.parseInt(classIdArg);
        try {
            List<User> students = a.readStudentCSVFile2(csvfilename);
            Connection conn = a.getConnection();
            a.doInsert(conn,students);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}