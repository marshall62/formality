package formality.content.database.dbconnection;

import formality.Util.InitParams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 30, 2010
 * Time: 1:46:45 PM
 *  The various MysqlDataSource classes support the following
   parameters (through standard set mutators):

     * user

     * password

     * serverName (see the previous section about fail-over hosts)

     * databaseName

     * port
 */
public class MySQLConnector {
        private String errorUrlStr_ = null;

    public Connection getDbConnection(InitParams initParams)throws Exception{
        Connection conn=null;
        String driverClassName = initParams.getDbDriver();
        String urlstr = initParams.getDbUrlString();
        if(initParams.isDbDebug())
                 errorUrlStr_ = initParams.obfuscatePwd(urlstr);
        int loginTimeout_ = 30;
        DriverManager.setLoginTimeout(loginTimeout_);
        if (initParams.isDbDebug()){
              DriverManager.setLogWriter(new PrintWriter(System.out));
        }
        //Instantiate the driver and get the Connection object
        Class.forName(driverClassName);
        conn = DriverManager.getConnection(urlstr, null, null);
        return conn;
    }

    public String getErrorUrlStr() {
        return errorUrlStr_;
    }
}
