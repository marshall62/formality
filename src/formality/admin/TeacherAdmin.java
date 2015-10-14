package formality.admin;

import com.Ostermiller.util.CSVParser;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 20, 2010
 * Time: 9:45:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class TeacherAdmin extends AdminUser {

        // CSV file in the form:
    // loginName, password, institution, district name
    public List<User> readTeacherCSVFile(String filename) throws IOException {
        List<User> users = new ArrayList<User>();
        // Parse the data
        String[][] values = CSVParser.parse(new FileReader(filename));
        // Display the parsed data
        for (int i=0; i<values.length; i++) {
            String loginName = values[i][0];
            String password = values[i][1];
            String fname = values[i][2];
            String lname = values[i][3];
            String institution = values[i][4];
            String district = values[i][5];
            users.add(new TeacherUser(fname,lname,loginName,password,institution,district));
        }
        return users;
    }

/**
     * Insert a new student given the minimal set of inputs necessary.   All other fields will default to NULL
     * @param conn
     * @param teacherUser
     * @return
     */
    public int insertNewUserToDb(Connection conn, TeacherUser teacherUser) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into usertable (login, password, accessLevel, institution, districtId, cond,firstname,lastname) " +
                    "values (?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,teacherUser.loginName);
            stmt.setString(2,teacherUser.password);
            stmt.setInt(3,2); // teacher access level is 2
            stmt.setString(4,teacherUser.institution);
            stmt.setInt(5,teacherUser.districtID);
            stmt.setInt(6,0);  // cond is 0 ??
            stmt.setString(7,teacherUser.fname);
            stmt.setString(8,teacherUser.lname);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }

     public int insertNewUser(Connection conn, User teacher) throws SQLException {
         int id = insertNewUserToDb(conn, (TeacherUser) teacher);
         return id;
     }

    public static void main(String[] args) {
        String csvfilename = args[0];
        TeacherAdmin a = new TeacherAdmin();
        try {
            List<User> teachers = a.readTeacherCSVFile(csvfilename);
            Connection conn = a.getConnection();
            a.doInsert(conn,teachers);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
