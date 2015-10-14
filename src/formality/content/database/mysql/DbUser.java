package formality.content.database.mysql;

import formality.model.UserInfo;
import formality.model.StudentInfo;
import formality.content.Course;
import formality.controller.FormalitySubsystem;

import java.sql.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Apr 11, 2011
 * Time: 10:39:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbUser {
    private static Logger logger = Logger.getLogger(DbUser.class);

    public static UserInfo createTeacher (Connection conn, UserInfo u) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into usertable (login, password,firstname,lastname,accesslevel,institution,districtID, cond) values (?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,u.getLogin());
            stmt.setString(2,u.getUserPwd());
            stmt.setString(3,u.getUserFname());
            stmt.setString(4,u.getUserLname());
            stmt.setInt(5,2);
            stmt.setString(6,u.getUserInstitution());
            stmt.setInt(7,Integer.parseInt(u.getDistrict()));
            stmt.setInt(8,0);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            u.setUserID(Integer.toString(id));
            return u;
        }
        catch (SQLException e) {
            System.out.println(e.getErrorCode());
            if (e.getErrorCode() == DBAccessor.duplicateRowError ||e.getErrorCode() == DBAccessor.keyConstraintViolation )
                throw e;
            else throw e;
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }

    public static UserInfo createStudent (Connection conn, UserInfo u) throws SQLException {
            ResultSet rs=null;
            PreparedStatement stmt=null;
            try {
                String q = "insert into usertable (login, password,initials,accesslevel, age,gender,nativeLang, " +
                        "iep,mathLevel,readingLevel,title1,institution,districtID, cond) " +
                        "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                stmt = conn.prepareStatement(q);
                stmt.setString(1,u.getLogin());
                stmt.setString(2,u.getUserPwd());
                stmt.setString(3,u.getInitals());
                stmt.setInt(4,1);
                stmt.setInt(5,u.getAge());
                stmt.setString(6,u.getGender());
                stmt.setString(7,u.getLanguage());
                stmt.setString(8,u.getIep().toString());
                stmt.setString(9,u.getMathLev().toString());
                stmt.setString(10,u.getReadingLev().toString());
                stmt.setString(11,u.getTitle().toString());
                stmt.setString(12,u.getUserInstitution().toString());    
                stmt.setInt(13,Integer.parseInt(u.getDistrict()));
                stmt.setInt(14,0);
                stmt.execute();
                rs = stmt.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);
                u.setUserID(Integer.toString(id));
                insertStudentFrameworkDataToDb(conn,id);
                return u;
            }
            catch (SQLException e) {
                System.out.println(e.getErrorCode());
                if (e.getErrorCode() == DBAccessor.duplicateRowError ||e.getErrorCode() == DBAccessor.keyConstraintViolation )
                    throw e;
                else throw e;
            }
            finally {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            }
        }

   /**
     * When a new student is created, this adds in some initial values for his framework scores.
     * @param conn
     * @param id
     * @throws SQLException
     */
    public static void insertStudentFrameworkDataToDb (Connection conn, int id) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into StudentFrameworkData (UserID,f4D6,f4D5,f4D4,f4D3,f4D2,f4D1,f4M5,f4M4,f4M3,\n" +
                    "f4M2,f4M1,f4G9,f4G8,f4G7,f4G6,f4G5,f4G4,f4G3,f4G2,f4G1,f4P6,f4P5,f4P4,f4P3,f4P2,f4P1,\n" +
                    "f4N18,f4N17,f4N16,f4N15,f4N14,f4N13,f4N12,f4N11,f4N10,f4N9,f4N8,f4N7,f4N6,f4N5,f4N4,\n" +
                    "f4N3,f4N2,f4N1,a4N1,a4N2,a4N3,a4N4,a4N5,a4N6,a4N7,a4N8,a4N9,a4N10,a4N11,a4N12,a4N13,\n" +
                    "a4N14,a4N15,a4N16,a4N17,a4N18,a4P1,a4P2,a4P3,a4P4,a4P5,a4P6,a4G1,a4G2,a4G3,a4G4,a4G5,\n" +
                    "a4G6,a4G7,a4G8,a4G9,a4M1,a4M2,a4M3,a4M4,a4M5,a4D1,a4D2,a4D3,a4D4,a4D5,a4D6,b4N1,b4N2,\n" +
                    "b4N3,b4N4,b4N5,b4N6,b4N7,b4N8,b4N9,b4N10,b4N11,b4N12,b4N13,b4N14,b4N15,b4N16,b4N17,\n" +
                    "b4N18,b4P1,b4P2,b4P3,b4P4,b4P5,b4P6,b4G1,b4G2,b4G3,b4G4,b4G5,b4G6,b4G7,b4G8,b4G9,b4M1,\n" +
                    "b4M2,b4M3,b4M4,b4M5,b4D1,b4D2,b4D3,b4D4,b4D5,b4D6) values (?,'0.5','0.5','0.5','0.5',\n" +
                    "'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',\n" +
                    "'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',\n" +
                    "'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001')";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,id);
            stmt.execute();
        }

        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }

    /**
     * Delete a set of users (only gets rid of usertable and usercourselinks)
     * @param conn
     * @param beginId
     * @param endId
     * @return
     * @throws SQLException
     */
    public int deleteUsers (Connection conn, int beginId, int endId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from usercourselinks where userId>=? and userId<=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,beginId);
            stmt.setInt(2,endId);
            stmt.executeUpdate();
            q = "delete from usertable where userId>=? and userId<=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,beginId);
            stmt.setInt(2,endId);
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static void  deleteUserData (Connection conn, int studId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            logger.info("Deleting user data for userId="+ studId);
            String q = "delete from studentframeworkdata where userId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from modulescore where userId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from hintlog where userId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from eventlog where userId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static void deleteUser (Connection conn, int studId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from usercourselinks where userId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from usertable where userId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }


    public static UserInfo getUserInfo(Connection conn, int studId) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select * from usertable where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int id= rs.getInt("userid");
                String login = rs.getString("login");
                int age = rs.getInt("age");
                String inits = rs.getString("initials");
                String gender = rs.getString("gender");
                String pw = rs.getString("password");
                String nativeLang = rs.getString("nativeLang");
                String iep = rs.getString("iep");
                String title1 = rs.getString("title1");
                String mathLev = rs.getString("mathLevel");
                String readingLev = rs.getString("readingLevel");
                UserInfo ui = new UserInfo();
                ui.setLogin(login);
                ui.setAge(age);
                ui.setInitals(inits);
                ui.setGender(gender);
                ui.setUserPwd(pw);
                ui.setUserID(Integer.toString(id));
                ui.setLanguage(nativeLang);
                ui.setIep(iep);
                ui.setTitle(title1);
                ui.setMathLev(mathLev);
                ui.setReadingLev(readingLev);
                return ui;
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return null;
    }

    public static int updateStudent(Connection conn, UserInfo ui) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "update usertable set initials=?, age=?, gender=?, login=?, password=?, nativeLang=?, " +
                    "iep=?, readingLevel=?, mathLevel=?, title1=? where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,ui.getInitals());
            stmt.setInt(2,ui.getAge());
            stmt.setString(3,ui.getGender());
            stmt.setString(4,ui.getLogin());
            stmt.setString(5,ui.getUserPwd());
            stmt.setString(6,ui.getLanguage());
            stmt.setString(7,ui.getIep().toString());
            stmt.setString(8,ui.getReadingLev().toString());
            stmt.setString(9,ui.getMathLev().toString());
            stmt.setString(10,ui.getTitle().toString());
            stmt.setInt(11,Integer.parseInt(ui.getUserID()));

            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    /**
     * Generate a new demo user with the base userName + an added integer.   This assumes there will
     * be many demo users in the system even though there will be demon process that completely removes
     * inactive ones and all their data.
     * @param conn
     * @param userName
     * @param pw
     * @throws SQLException
     */
    public static UserInfo genDemoUser (Connection conn, String userName, String pw) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select max(userid), login from usertable where login like ? and password=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,userName+"%");
            stmt.setString(2,pw);
            rs = stmt.executeQuery();
            String login;
            if (rs.next()) {
                int c= rs.getInt(1);
                if (c == 0)
                    login = userName + "-0";
                else login = rs.getString(2); // demo logins of form demoUser-###
                String num = login.substring(login.indexOf('-')+1);
                int uNum = Integer.parseInt(num);
                UserInfo ui = getDemoUserInfo(conn,userName,pw,uNum+1);
                createStudent(conn,ui);
                DbCourse.addUserToCourse(conn,Integer.parseInt(ui.getUserID()),Integer.parseInt(ui.getSelectedCourse().getCourseID()));
                return ui;
            }
            return null;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }


    public static int recordDemoSession(String userId, long timestamp, Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into DemoSession (userId, loginTime) values (?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,userId);
            stmt.setTimestamp(2,new Timestamp(timestamp));
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

    /**
     * GIven the base demo userName and the number to append to it, build a UserInfo object for this user.
     * @param conn
     * @param userName
     * @param pw
     * @param uNum
     * @return
     * @throws SQLException
     */
    private static UserInfo getDemoUserInfo(Connection conn, String userName, String pw, int uNum) throws SQLException {
        UserInfo ui = new StudentInfo();
        ui.setLogin(userName+"-"+uNum);
        ui.setUserPwd(pw);
        ui.setDistrict("");
        ui.setAccessLevel(FormalitySubsystem.studentAccess);
        ui.setInstitution("");
        Course c = DbCourse.getDemoCourse(conn);

        ui.setDistrict(Integer.toString(c.getDistrict()));
        ArrayList<Course> l = new ArrayList<Course>();
        l.add(c);
        ui.setCourses(l);
        ui.setSelectedCourseInfo(c.getCourseID());
        return ui;
    }

    public static int getWayangUser(Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select userid from usertable where login=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1, "wayangStudent");
            rs = stmt.executeQuery();
            if (rs.next()) {
                int id= rs.getInt(1);
                return id;
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return -1;
    }

    public static int getUsername(Connection conn, String user) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select userid from usertable where login=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,user);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int c= rs.getInt(1);
                return c;
            }
            return -1;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static int createGuestUser(Connection conn, String user, String password, String email) throws Exception {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into usertable (login, password, email, districtid, accessLevel) values (?,?,?,1,1)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,user);
            stmt.setString(2,password);
            stmt.setString(3,email);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException e) {
            System.out.println(e.getErrorCode());
            // if its a keyconstraint violation or a duplicate row, we can give a specific error
            if (e.getErrorCode() == 2627 ||e.getErrorCode() == 1062 )
                throw new Exception("That user, password cannot be inserted.   Because it violates a key constraint or creates a duplicate.  Error code: " + e.getErrorCode());
            else throw e;
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }
}
