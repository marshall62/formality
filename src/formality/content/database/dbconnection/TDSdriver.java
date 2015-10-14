package formality.content.database.dbconnection;

import formality.Util.InitParams;

import java.sql.DriverManager;
import java.sql.Connection;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 29, 2010
 * Time: 4:52:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TDSdriver {

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
