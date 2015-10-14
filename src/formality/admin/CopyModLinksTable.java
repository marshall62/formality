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
public class CopyModLinksTable {

    public CopyModLinksTable() {

    }
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


   public void copyData (Connection conn) throws SQLException {
       ResultSet rs=null;
       PreparedStatement stmt=null;
       PreparedStatement stmt2=null;
       String q2 ="insert into modmodlinks2 (pModID,cModID,courseID) values (?,?,?)";
       try {
           String q = "select parentModID,childModID,courseID from modmodulelinks order by parentModId,childModID";
           stmt = conn.prepareStatement(q);
           rs = stmt.executeQuery();
           while (rs.next()) {
               int p= rs.getInt(1);
               int c= rs.getInt(2);
               int crs= rs.getInt(3);
               stmt2 = conn.prepareStatement(q2);
               stmt2.setInt(1,p);
               stmt2.setInt(2,c);
               stmt2.setInt(3,crs);
               System.out.println("Insert p="+p+" c="+c+" crs="+crs);
               stmt2.executeUpdate();
               stmt2.close();
           }
       }
       finally {
           if (stmt != null)
               stmt.close();
           if (rs != null)
               rs.close();
       }
   }


    public static void main(String[] args) {
        CopyModLinksTable a = new CopyModLinksTable();
        try {
            Connection conn = a.getConnection();
            a.copyData(conn);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}