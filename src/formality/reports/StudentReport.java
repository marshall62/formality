package formality.reports;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Dec 30, 2010
 * Time: 4:06:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentReport {


    int[] modIds= new int[0];
    String courseName=null;

    protected String getModuleIdList() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < modIds.length; i++) {
            int modId = modIds[i];
            sb.append(modId+",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }


    protected List<String> getReportTitle () {
        List<String> row = new ArrayList<String>();
        int z = courseName!=null?1:0;
        if (modIds.length > 0) z++;
        String[] titleCols = new String[modIds.length + z];
        int j=0;
        if (this.courseName != null)
            titleCols[j++]=courseName;
        if (modIds.length > 0)
            titleCols[j++]="Modules:";

        for (int i = 0; i < modIds.length; i++)
            titleCols[i+j]=Integer.toString(modIds[i]);
        addRowValues(row, titleCols);
        return row;
    }

    protected List<String> addRowValues(List<String> row, String[] values) {
        for (String v : values)
            row.add(v);
        return row;
    }

    public String getCourseName (Connection conn, int courseId) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select coursename from course where courseid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,courseId);
            rs = stmt.executeQuery();
            if (rs.next())
               return rs.getString(1);
            else return "";
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }    

      protected Connection getConnection () throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String url;
        String dbPrefix="jdbc:mysql";
        String dbHost="cadmium.cs.umass.edu";
//        String dbHost="localhost";
        String dbSource="formality";
        String dbUser="4malityUser";
        String dbPw="4malityUser";
        if (dbPrefix.equals("jdbc:mysql"))  {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            url = dbPrefix +"://"+ dbHost +"/"+ dbSource; // +"?user="+ dbUser+"&password="+ dbPw; // preferred by MySQL

        }
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


    public void toCSV (List<List<String>> results, StringBuffer sb) {
        for (List<String> row: results) {
            for (String v: row) {
                sb.append(v + ",");
            }
            sb.deleteCharAt(sb.length()-1); // eliminate trailing comma
            sb.append("\n");
        }
    }    
}
