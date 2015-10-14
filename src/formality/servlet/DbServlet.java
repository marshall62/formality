package formality.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Aug 2, 2010
 * Time: 11:31:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbServlet extends HttpServlet {


    public String db;
       public static String resourcePath;

       public void init(ServletConfig config) throws ServletException {
           db = config.getInitParameter("db");
           super.init(config);
       }


 /**
     * New-style connection creation based on Tomcat data source that is defined in the META-INF/context.xml file.   The advantage
     * of using this technique is that it uses a connection pool and better deals with closing connections automatically if the
     * code neglects to.   With Tomcat 6,  the MySQL driver jar file must be placed in the TCHOME/libs folder.   With Tomcat 5.X the Mysql driver
     * must be placed in TCHOME/common/libs  (if that doesn't work, try TCHOME/shared/libs).  N.B. Make sure the mysql jar is not found
     * anywhere else on the classpath or it will cause failure to obtain a db connection.
     *
     * @return
     * @throws java.sql.SQLException
     */
    protected Connection getConnection() throws SQLException {
        // Obtain our environment naming context
        Context initCtx;
        try {
            initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source by name
            DataSource ds = (DataSource) envCtx.lookup(db);
            Connection conn = ds.getConnection();
            // sometimes after a long time period a connection is returned that is no longer valid
            // and we get an exception.   This will keep requesting connections until a valid one
            // is returned

            // No longer necessary because of validationQuery and associated flags in context.xml
            // Not true.   These things still seem to leave some stale connections.
            while (!connectionIsValid(conn)) {
                conn = ds.getConnection();
            }
            return conn;
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    /**
       * do a cheap query to see if the connection is valid (because the Connection.isValid
       * method does not work).   If it isn't valid, free the resource.
       *
       * @param dbConn
       * @return
       */
      private boolean connectionIsValid(Connection dbConn) {
          //log.debug("ENTER connectionIsValid(): "+dbConn);
          boolean result = true;

          PreparedStatement psr = null;
          try {
              //Prepared statement is used to cache the compiled SQL
              psr = dbConn.prepareStatement("SELECT 1");
              psr.executeQuery();
          } catch (SQLException e) {
//            logger.debug("Excpetion occured, connection is not valid. "+e.getMessage());
              try {
                  dbConn.close(); //dbConn is never null at this point
                  dbConn = null;
              } catch (Exception ee) {
                  //quite
              }
              result = false;
          } finally {
              try {
                  //free up resource kept by the test statement
                  if (psr != null) {
                      psr.close();
                  }
                  psr = null;
              } catch (Exception e) {
                  //quite
              }
          }
          return result;
      }
    
}
