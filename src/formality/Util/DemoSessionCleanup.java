package formality.Util;

import formality.content.database.mysql.DbUser;
import org.apache.log4j.Logger;

import java.util.TimerTask;
import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Jan 11, 2012
 * Time: 12:42:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DemoSessionCleanup extends TimerTask  {
    private static Logger logger = Logger.getLogger(DemoSessionCleanup.class);
    Connection conn;

    public DemoSessionCleanup(Connection conn) {
        this.conn = conn;
    }

    public void run() {
       ResultSet rs=null;
       PreparedStatement stmt=null;
       try {
           String q = "select userid, id from demosession where loginTime<=?";
           stmt = conn.prepareStatement(q,ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
           long now = System.currentTimeMillis();
           long dayAgo = now - (1000 * 60 * 60 * 24);
           // delete demo users that logged in over a day ago.
           stmt.setTimestamp(1,new Timestamp(dayAgo));
           rs = stmt.executeQuery();
           while (rs.next()) {
               int uid= rs.getInt(1);
               int id = rs.getInt(2);
               logger.info("Demo user " + uid + " data is being deleted");
               removeAllStudentData(uid);
               rs.deleteRow();
           }

       }
       catch (SQLException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } finally {
           if (stmt != null)
               try {
                   stmt.close();
               } catch (SQLException e) {
                   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }
           if (rs != null)
               try {
                   rs.close();
               } catch (SQLException e) {
                   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }
       }
    }

    private void removeAllStudentData(int uid) throws SQLException {
        DbUser.deleteUserData(conn,uid);
        DbUser.deleteUser(conn,uid);
    }
}
