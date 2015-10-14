package formality.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Mar 9, 2011
 * Time: 1:25:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DbConnection {

    // dbhost should be passed in as either localhost or cadmium.cs.umass.edu
    public static Connection getConnection (String host) throws SQLException {
        String url;
        String dbPrefix="jdbc:mysql";
        String dbHost= host;
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



}
