package formality.servlet;

import formality.content.SystemInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.sql.*;
import java.sql.Date;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import formality.controller.FormalitySubsystem;
import formality.html.GeneralHtml;
import formality.Util.InitParams;
import formality.Util.Emailer;
import formality.Util.FileRW;
import formality.Util.DemoSessionCleanup;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Jun 10, 2005
 * Time: 10:10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormalityServlet extends DbServlet {// extends  DbBaseServlet {

    private static Logger logger = Logger.getLogger(FormalityServlet.class);

    public static String resourcePath;
    public static final String soundPathVar = MediaServlet.soundPathVar;
    public String soundPath;
    public static Properties wayangURIMap;

    public void init(ServletConfig config) throws ServletException {
        logger.info("Starting FormalityServlet");
        super.init(config);
        resourcePath = config.getServletContext().getInitParameter("formalityResourceURI"); // path to APache folder where resources come from
        soundPath = config.getInitParameter(soundPathVar);
        Timer t = new Timer(true);
        int dailyMS = 24 * 60 * 60 * 1000;
//            int dailyMS = 60 * 1000;


            // Every 24 hours (starting from now) this will look for idle demo users and delete all their data
        try {
            t.schedule(new DemoSessionCleanup(getConnection()),new java.util.Date(),dailyMS);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }



    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doService(req, res);
    }

    String dbDriver = "com.mysql.jdbc.Driver";
    String dbUser = "4malityUser";
    String dbPassword= "4malityUser";
    String dbPrefix ="jdbc:mysql";
    String dbSource = "formality";
    String dbHost = "localhost";

    protected void loadDbDriver () {
        try {
           Driver d = (Driver) Class.forName(dbDriver).newInstance(); // MySql
            System.out.println(d);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Old-style connection creation based on settings in the web.xml file that name the database, user, password, etc.
     * @return
     * @throws SQLException
     */
    protected Connection getConnection2 () throws SQLException {
        String url;
        if (dbPrefix.equals("jdbc:mysql"))
            url = dbPrefix +"://"+ dbHost +"/"+ dbSource +"?user="+ dbUser ; //+"&password="+ dbPassword; // preferred by MySQL
        else // JDBCODBCBridge
            url = dbPrefix +":"+ dbSource;
//        url = "jdbc:mysql://localhost:3306/test";
//        url = "jdbc:mysql://localhost/rashidb"; // this works
        try {
            logger.debug("connecting to db on url " + url);
            return DriverManager.getConnection(url,dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw e;
        }
    }
    
    public String testConnection (Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select count(*) from usertable";
            stmt = conn.prepareStatement(q);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int c= rs.getInt(1);
                String s = "Database working.   There are " + c + " users in the usertable";
                System.out.println(s);
                return s;
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return "Database failure";
    }

    public void doService(HttpServletRequest request,
                          HttpServletResponse response) {
        String curUrl = null;

        //Get the Web App init params from the WEBINF file
        InitParams initParams = new InitParams();
        initParams.storeParameters(this.getServletContext());

//        String retUrl = (String) session.getAttribute("retUrl");
        StringBuffer contentType = new StringBuffer();
        StringBuffer outstr = new StringBuffer();
        boolean flush = false;
        curUrl = request.getRequestURI() + "?" + request.getQueryString();
        String errorUrlStr = null;
        Map params = null;
        SystemInfo info = new SystemInfo();
        Connection conn=null;
        try {
            //connect to db

            //  TDSdriver  dbDriver = new  TDSdriver();
//         MySQLConnector  dbDriver = new  MySQLConnector();
//         conn = dbDriver.getDbConnection(initParams);
//         errorUrlStr = dbDriver.getErrorUrlStr();
            //
            response.setContentType("text/html");
            params = request.getParameterMap();    // get the param map (which is locked)
            logger.info(">> "+getParamsString(params));
            String dbTest = GeneralHtml.getParamStr(params,"testdb",null);
            String dbTest2 = GeneralHtml.getParamStr(params,"testdb2",null);
            if (dbTest != null || dbTest2 != null) {
                if (dbTest2 != null)
                    loadDbDriver();
                conn = (dbTest!= null) ? getConnection() : getConnection2();
                String res= testConnection(conn);
                response.getWriter().print(res);
                response.getWriter().flush();
                response.getWriter().close();
                return ;
            }
            conn = getConnection();
            info.setSoundPath(soundPath);
            info.setServletContextPath(request.getRequestURL().toString());//  http://localhost:8080/formality
            // if resource path is provided in the web.xml, use it; o/w we assume the servlet itself is serving resources and its context path is the web root
            if (resourcePath != null) {
                // TODO This misuse of the concept contextPath was created during the port.   It seems to be used for dual purposes: content and the servlet context
                // which are not the same in our deployment
                info.setContextPath(resourcePath);//   path to apache web folder where images,css,javascript is
                info.setResourcePath(resourcePath);
            }
            else {
                info.setContextPath(info.getServletContext());
                info.setResourcePath(info.getServletContext());
                resourcePath = info.getServletContext();
            }
            info.setServletPath(request.getServletPath());//  /servlet/FormalityServlet


            FormalitySubsystem fs = new FormalitySubsystem();
            flush = fs.handleRequest(params, info, conn, outstr, request, response);
            if (!flush) {
                // set new content type if changed by handleRequest
                if (contentType.length() > 0) {
                    response.setContentType(contentType.toString());
                }
                flushHtmlOutput(response, outstr.toString());
            }
//            session.setAttribute("retUrl", curUrl);
        }
        catch (Throwable ex) {
            ex.printStackTrace();
            logger.info("Exception",ex);
            StringBuffer msgBuffer = new StringBuffer();
            msgBuffer.append("error msg: ").append(ex.toString());
            msgBuffer.append("\nparams: \n").append(initParams.getErrorParams(params)).append("<br>");
            if (errorUrlStr != null)
                msgBuffer.append("\ndbUrlString: ").append(errorUrlStr).append("<br>");
            StackTraceElement[] st = ex.getStackTrace();
            for (int i = 0; i < st.length; i++) {
                msgBuffer.append(st[i].toString()).append("<br>");
            }
            String errorMessage = msgBuffer.toString();
            try {
//                if (retUrl == null)
//                    retUrl = request.getContextPath() + "/index.html";
                if (initParams.isDebug()) {
                    //TODO- get the email and error log file from web xml file
                    String errorLogFile = initParams.getErrorLogFile();
                    String erroremail = initParams.getTechEmail();
                    String erroremailhost = initParams.getTechEmailServer();
                    boolean sent = Emailer.sendErrorEmail(erroremail, erroremailhost, errorMessage, null);
                    errorMessage += " email sent " + sent;
                    FileRW.writeMessageAppend(true, errorMessage, errorLogFile);
                }
                GeneralHtml page = new GeneralHtml();
                StringBuffer errstr = new StringBuffer();
                page.getErrorPage(" Technical staff have been notified.", "", errstr, info, msgBuffer);

                flushHtmlOutput(response, errstr.toString());
            } catch (Exception e) {
            }
        }
        finally {
            logger.debug("entering finally");
            try {
                if (conn != null && conn.isClosed())
                    logger.info("For some reason the connection is already closed before closing in the finally clause");
                if (conn != null && !conn.isClosed()) {
                    logger.debug("Closing db connection: ");
                    conn.close();
                }
                logger.info("<< done");
            }
            catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                logger.info("Exception",e);
            }
        }
    }

    private String getParamsString(Map params) {
        Set<Map.Entry<String,String>> entries = params.entrySet();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry entry: entries) {
            sb.append("["+ entry.getKey()+"=");
            for (String v: (String []) entry.getValue())
                sb.append(v+",");
            sb.deleteCharAt(sb.length()-1);
            sb.append("]");
        }
        return sb.toString();
    }


    public void destroy() {
        // cleanup code goes here
    }

    public String getServletInfo() {
        StringBuffer contactInfo = new StringBuffer("Center for Knowledge Communication\n");
        contactInfo.append("University of Massachusetts\n");
        contactInfo.append("Amherst, Massachusetts  01003\n");
        return contactInfo.toString();
    }

    protected void flushHtmlOutput(HttpServletResponse resp,
                                   String outstr) throws Exception {
        PrintWriter out = new PrintWriter(resp.getOutputStream());
        out.print(outstr);
        out.flush();
        out.close();
    }

}// end servlet
