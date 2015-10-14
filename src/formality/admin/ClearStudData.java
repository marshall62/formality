package formality.admin;

import com.Ostermiller.util.CSVParser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 13, 2010
 * Time: 9:47:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClearStudData extends AdminUser {



    public int deleteStudents (Connection conn, int minId, int maxId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from studentframeworkdata where  userId>=? and userId<=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,minId);
            stmt.setInt(2,maxId);
            stmt.executeUpdate();
            return super.deleteUsers(conn,minId,maxId);
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public int insertNewUser(Connection conn, User u) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void clearStudentData (Connection conn, int studId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from responselog where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from eventlog where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from modulescore where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from hintlog where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

public static void main (String[] args) {

    if (args.length < 1) {
        System.out.println("You must provide a student ID as program argument");
        System.exit(0);
    }

    String studId = args[0];

    ClearStudData a = new ClearStudData();
    try {
        Connection conn = a.getConnection();
        a.clearStudentData(conn,Integer.parseInt(studId));
    } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



}