package formality.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 20, 2010
 * Time: 9:56:48 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AdminUser {
    /**
     * Old-style connection creation based on settings in the web.xml file that name the database, user, password, etc.
     * @return
     * @throws java.sql.SQLException
     */
    protected Connection getConnection () throws SQLException {
        String url;
        String dbPrefix="jdbc:mysql";
        String dbHost="cadmium.cs.umass.edu";
//        String dbHost="localhost";
        String dbSource="formality";
        String dbUser="4malityUser";
        String dbPw="4malityUser";
        if (dbPrefix.equals("jdbc:mysql"))
            url = dbPrefix +"://"+ dbHost +"/"+ dbSource; // +"?user="+ dbUser+"&password="+ dbPw; // preferred by MySQL
        else // JDBCODBCBridge
            url = dbPrefix +":"+ dbSource;
//        url = "jdbc:mysql://localhost:3306/test";
//        url = "jdbc:mysql://localhost/rashidb"; // this works
        try {
            System.out.println("connecting to db on url " + url);
            return DriverManager.getConnection(url,dbUser, dbPw);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw e;
        }
    }

    public int getDistrictID (Connection conn, String districtName) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select districtId from district where name=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,districtName);
            rs = stmt.executeQuery();
            if (rs.next()) {
               return rs.getInt(1);
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

    /**
     * Goes through a list of users and makes sure that there are not already users in the db with this
     * login name.  For each login name that is found the User object will have its ID inserted
     * so that the caller can know which logins are already in use.
     * @param conn
     * @param users
     * @return true if one of the given login names exists
     * @throws java.sql.SQLException
     */
    public boolean validateUserLogin(Connection conn, List<User> users) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        boolean found = false;
        try {
            String q = "select userid from usertable where login=?";
            for (User u: users) {
                stmt = conn.prepareStatement(q);
                stmt.setString(1,u.loginName);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    int id= rs.getInt(1);
                    found = true;
                    u.setId(id);
                }
            }
            return found;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    /**
     * Returns a String that reports the logins that are already in use.  Will return null
     * if none of the logins are in use.
     * @param conn
     * @param users
     * @return
     * @throws java.sql.SQLException
     */
    public String getAlreadyUsedLoginsMessage (Connection conn, List<User> users) throws SQLException {
        boolean found = validateUserLogin(conn,users);
        StringBuffer sb = new StringBuffer("These logins are already in use: ");
        // some loginName is already in use
        if (found) {
            for (User u: users) {
                if (u.id > 0)
                    sb.append(u.loginName + ", ");
            }
            int len = sb.length();
            sb.delete(len-2,len-1); // gets rid of trailing ', '
            return sb.toString();
        }
        else return null;
    }

    /**
     * Given a list of StudentUsers that are produced from a CSV parse,  check to see if any are in
     * use.   If so, report an error; otherwise,  insert the students and report the id/logins.
     * @param conn
     * @param users
     * @throws SQLException
     */
    public void doInsert (Connection conn, List<User> users) throws Exception {
        String inUse = getAlreadyUsedLoginsMessage(conn, users);
        if (inUse != null)
            throw new Exception( "Not inserting any users: \n" + inUse);
        else {
            insertNewUsers(conn, users);
            System.out.println("Inserted new users: \n");
            for (User u: users) {
                System.out.println(u.id + ": " + u.loginName);
            }
        }
    }

    /**
     * Given a list of StudentUser objects created from a CSV file, insert them into the db.
     * @param conn
     * @param users
     * @return
     * @throws SQLException
     */
    public boolean insertNewUsers(Connection conn, List<User> users) throws SQLException {
        for (User u : users) {
            int districtId = getDistrictID(conn,u.districtName);
            u.setDistrictID(districtId);
            int studId = insertNewUser(conn,u);
            u.setId(studId);
        }
        return true;
    }

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

    public abstract int insertNewUser(Connection conn, User u) throws SQLException ;

    public static void main(String[] args) {
        StudentAdmin a = new StudentAdmin();
        try {
            Connection conn = a.getConnection();
            a.deleteUsers(conn,1508,1585);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}
