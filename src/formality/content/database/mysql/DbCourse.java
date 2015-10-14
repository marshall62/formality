package formality.content.database.mysql;

import formality.content.Course;
import formality.admin.StudentUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Apr 6, 2011
 * Time: 1:26:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbCourse {

    public static final String DEMO_COURSE = "4mality Interactive Demo";


    public static Course createCourse(Connection conn, String name, String institution, String controller, int district) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into course (CourseName, Controller, District, institution, setsModuleLinkage,scoreboard) values (?,?,?,?,1,0)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,name);
            stmt.setString(2,controller);
            stmt.setInt(3,district);
            stmt.setString(4,institution);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            rs.next();
            int id= rs.getInt(1);
            return new Course(Integer.toString(id),name,institution,controller,district);
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

    public static void linkTeacherCourse(Connection conn, int courseId, int teacherId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "insert into usercourselinks (userid, courseid) values (?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,teacherId);
            stmt.setInt(2,courseId);
            stmt.execute();
        }
        catch (SQLException e) {
            System.out.println(e.getErrorCode());
            if (e.getErrorCode() == DBAccessor.duplicateRowError ||e.getErrorCode() == DBAccessor.keyConstraintViolation )
                throw e;
            else throw e;
        }
        finally {
            if (stmt != null)
                stmt.close();
        }
    }


    public static Course getCourse (Connection conn, int courseID) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select c.courseID,c.courseName,c.institution, c.district, d.name from course c, district d where c.courseid=? and c.district=d.districtID";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,courseID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Course c = new Course();
                c.setCourseID(Integer.toString(rs.getInt(1)));
                c.setCourseName(rs.getString(2));
                c.setInstitution(rs.getString(3));
                c.setDistrict(rs.getInt(4));
                c.setDistrictName(rs.getString(5));
                return c;
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

    public static List<Course> getAllCourses(Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select * from course";
            stmt = conn.prepareStatement(q);
            rs = stmt.executeQuery();
            List<Course> courses = new ArrayList<Course>();
            while (rs.next()) {
                int id= rs.getInt("courseId");
                String name= rs.getString("courseName");
                String inst= rs.getString("institution");
                int district= rs.getInt("district");
                Course c = new Course();
                c.setCourseID(Integer.toString(id));
                c.setCourseName(name);
                c.setInstitution(inst);
                c.setDistrict(district);
                courses.add(c);

            }
            return courses;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static final void addUserToCourse (Connection conn, int userId, int courseId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "insert into usercourselinks (userid, courseid) values (?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,userId);
            stmt.setInt(2,courseId);
            stmt.execute();
        }

        finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static final void updateUserCourse (Connection conn, int userId, int courseId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "update usercourselinks set courseid=? where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,courseId);
            stmt.setInt(2,userId);
            stmt.executeUpdate();
        }

        finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static List<StudentUser> getStudentList(Connection conn, int courseId) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        List<StudentUser> result = new ArrayList<StudentUser>();
        try {
            String q = "select u.login, u.userid, u.initials, u.age, u.gender from usertable u, usercourselinks l where u.userid=l.userid and l.courseid=? and accessLevel=1";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,courseId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String login = rs.getString(1);
                int uid = rs.getInt(2);
                String inits = rs.getString(3);
                int age = rs.getInt(4);
                String gender = rs.getString(5);
                StudentUser su = new StudentUser(null,login,null,null,null,null,age,gender,inits,null,null,null);
                su.setId(uid);
                result.add(su);
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return result;
    }

    public static Course getDemoCourse(Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select courseid,controller,district from course where coursename=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,DEMO_COURSE);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String cid= rs.getString(1);
                String controller= rs.getString(2);
                int district= rs.getInt(3);
                Course c = new Course(cid,DEMO_COURSE,"",controller,district);
                return c;
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

    public static int getGuestCourse(Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select courseid from course where coursename='Guest Users'";
            stmt = conn.prepareStatement(q);
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
}
