package formality.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.PrintStream;
import java.io.FileOutputStream;


//connect to the database specified
public class DbBootstrap {
    String dbPrefix = "jdbc:inetdae7";
    String dbDriver = "com.inet.tds.TdsDriver";
    String dbName = "CESD4Mality";
    String dbPwd = "vc1m+JP0TN3qYSC3SwwT";
    String dbHostname = "128.119.243.3:1439";
    String dbUser = "fourmality_login";
    String sqll7 = "true";
    String nowarnings = "false";
    int loginTimeout_ = 30;
    String outputFile = "S:/User/4mality/reports/test.txt";

  public DbBootstrap(){

  }

  public Connection getDBConnection() throws Exception {
      Connection conn=null;
      String urlstr = getDbUrlString();
      DriverManager.setLoginTimeout(loginTimeout_);
      Class.forName(dbDriver);
      conn = DriverManager.getConnection(urlstr, null, null);
      return conn;
  }

   public String getDbUrlString()throws Exception {
       StringBuffer dbUrlstr = new StringBuffer();
       dbUrlstr.append(dbPrefix).append(":").append(dbHostname).append("?");
       dbUrlstr.append("sq17=").append(sqll7).append("&");
       dbUrlstr.append("nowarnings=").append(nowarnings).append("&");
       dbUrlstr.append("password=").append(dbPwd).append("&");
       dbUrlstr.append("user=").append(dbUser).append("&");
       dbUrlstr.append("database=").append(dbName);
       return dbUrlstr.toString();
   }

   public void test()throws Exception{
      Connection conn = getDBConnection();
      if(conn==null)
          throw new Exception("conn is null");
      printResults("A-ok", outputFile);
   }

    public void printResults(String report, String outfileStr) throws Exception{
         PrintStream out = System.out;
         out = new PrintStream(new FileOutputStream(outfileStr));
         out.print(report);
         out.close();
    }


    public static void main(String[] args) {
       DbBootstrap app = new DbBootstrap();
       try{ app.test();
       System.out.println("all done");
       }
      catch (Exception e){ System.out.print("ERROR: "+e.getMessage());}
    }
}
