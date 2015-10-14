package formality.Util;


import formality.parser.StringEncoder;
import formality.content.database.BaseDBAccessor;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Mar 30, 2010
 * Time: 2:29:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBConnectorSQLServer{
    String dbPrefix = "jdbc:inetdae7";
    String dbDriver = "com.inet.tds.TdsDriver";
    String dbName = "CESD4Mality";
    String dbPwd = "vc1m+JP0TN3qYSC3SwwT";
    String dbHost = "128.119.243.3:1439";
    String dbUser = "fourmality_login";
    String sqll7 = "true";
    String nowarnings = "false";
    int loginTimeout_ = 30;

   public DBConnectorSQLServer(){
   }

   public Connection getConnection()throws Exception{
       Connection conn = null;
     try{
        //String urlstr = getDbUrlString();
        String urlstr = "jdbc:inetdae7:128.119.40.96:1439?sq17=true&nowarnings=false&password=vc1m+JP0TN3qYSC3SwwT&user=fourmality_login&database=CESD4Mality";
        int loginTimeout_ = 30;
        DriverManager.setLoginTimeout(loginTimeout_);
        Class.forName(dbDriver);
        conn = DriverManager.getConnection(urlstr, null, null);
     }catch(Exception ex){
         System.out.print(ex.getMessage());
     }
       return conn;
   }

    public String getDbUrlString()throws Exception {
         StringBuffer dbUrlstr = new StringBuffer();
         String dbpwd = StringEncoder.decode(dbPwd);
         dbUrlstr.append(dbPrefix).append(":").append(dbHost).append("?");
         dbUrlstr.append("sq17=").append(sqll7).append("&");
         dbUrlstr.append("nowarnings=").append(nowarnings).append("&");
         dbUrlstr.append("password=").append(dbpwd).append("&");
         dbUrlstr.append("user=").append(dbUser).append("&");
         dbUrlstr.append("database=").append(dbName);
         return dbUrlstr.toString();
      }

}
