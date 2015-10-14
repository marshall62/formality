package formality.servlet;

import formality.content.SystemInfo;
import formality.content.database.dbconnection.TDSdriver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import formality.controller.FormalitySubsystem;
import formality.html.GeneralHtml;
import formality.Util.InitParams;
import formality.Util.Emailer;
import formality.Util.FileRW;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 29, 2010
 * Time: 4:41:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDbServlet extends HttpServlet {// extends  DbBaseServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doService(req, res);
    }

    public void doService(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");    // set the response type before sending data
        PrintWriter out = response.getWriter();    // for outputting text

        // TO DO: your request processing code goes here
        Connection conn = null;
        String message = null;
        String errorUrlStr = null;

        //Get the Web App init params from the WEBINF file
        InitParams initParams = new InitParams();
        initParams.storeParameters(this.getServletContext());

        try {
//            TDSdriver dbDriver = new TDSdriver();
//            conn = dbDriver.getDbConnection(initParams);
//            errorUrlStr = dbDriver.getErrorUrlStr();
            conn = getConnection();
            if (conn != null)
                message = "Connection established";
        }
        catch (Exception ex) {
            message = "Exception thrown while attempting to connect. " + ex.getMessage();
            StringBuffer stackBuffer = new StringBuffer();
            StackTraceElement[] st = ex.getStackTrace();
            for (int i = 0; i < st.length; i++) {
                stackBuffer.append(st[i].toString()).append("\n");
            }
            message += stackBuffer.toString();
        }

        out.print("<HTML>");
        out.print("<HEAD><TITLE>4mality Test DB Connection Servlet</TITLE></HEAD>");
        out.print("<BODY>");

        out.print(message);

        out.print("</BODY>");
        out.print("</HTML>");
        out.close();
    }


    protected Connection getConnection() throws SQLException {
        // Obtain our environment naming context
        Context initCtx;
        try {
            initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source by name
            DataSource ds = (DataSource) envCtx.lookup("jdbc/formalitydblocal");
            Connection conn = ds.getConnection();
            // sometimes after a long time period a connection is returned that is no longer valid
            // and we get an exception.   This will keep requesting connections until a valid one
            // is returned

//             No longer necessary because of validationQuery and associated flags in context.xml
//            while (!connectionIsValid(conn)) {
//                conn = ds.getConnection();
//            }
            return conn;
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

}// end servlet


