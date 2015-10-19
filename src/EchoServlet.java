
import java.io.*;
import java.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

/**
 * This should be called http://localhost:8082/wo4/EchoServlet?testDb=4 to test 4mality db or testDb=w to test wayang db.
 * Can omit the testDb param just to see if servlets are working.
 */
public class EchoServlet extends HttpServlet



{

    protected static String tomcatDataSourceURL="jdbc/formalitydblocal"; // make sure META-INF/context.xml defines this datasource
	/**
	 * Initializes the servlet. The init method is invoked once, automatically, each time
	 * the servlet is loaded. It is guaranteed to finish before any service requests are
	 * accepted.
	 */
	public void init() throws ServletException
	{
		// TO DO: your initialization code goes here
	}

	/**
	 * Destroys the servlet, cleaning up whatever resources are being held. This
	 * method is invoked, once, automatically, each time the servlet is unloaded.
	 */
	public void destroy()
	{
		// TO DO: your cleanup code goes here
	}

    protected Connection getConnection () throws SQLException {
           // Obtain our environment naming context
           Context initCtx;
           try {
               initCtx = new InitialContext();
               Context envCtx = (Context) initCtx.lookup("java:comp/env");

               // Look up our data source by name
               DataSource ds = (DataSource) envCtx.lookup(tomcatDataSourceURL);
               Connection conn = ds.getConnection();
               // sometimes after a long time period a connection is returned that is no longer valid
               // and we get an exception.   This will keep requesting connections until a valid one
               // is returned

                conn = ds.getConnection();

               return conn;
           } catch (Exception e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
           }
           return null;
       }

    private void processParams(HttpServletRequest req, PrintWriter out) {
        String[] vals = req.getParameterValues("testDb");
        if (vals != null) {
            if (vals[0].equals("4"))
                tomcatDataSourceURL="jdbc/formalitydb";
            else tomcatDataSourceURL="jdbc/wodb";
            Connection conn=null;
            try {
                conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("select NOW(), USER(), DATABASE(), VERSION();");
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {

                    Timestamp now =rs.getTimestamp(1);
                    String user =  rs.getString(2);
                    String dbname =  rs.getString(3);
                    String version =  rs.getString(4);
                    // test
                    out.print("<br>DB: Now: " + now + " User: " + user + " Schema: " + dbname + " Version: " +version);

                }
            else out.print("<br>DATABASE QUERY FAILED!");
            }

            catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

	/**
	 * Handles HTTP GET requests.
	 */
	public void doGet( HttpServletRequest req, HttpServletResponse res )
		throws ServletException, IOException
	{
		res.setContentType( "text/html" );	// set the response type before sending data
		PrintWriter out = res.getWriter();	// for outputting text
		// TO DO: your request processing code goes here

		out.print( "<HTML>" );
		out.print( "<HEAD><TITLE>Test Servlet</TITLE></HEAD>" );
		out.print( "<BODY>" );

		// TO DO: your HTML generation code goes here, for example
        out.print( "<br><br>This is a simple servlet that should demonstrate that servlets are working. Call it with ?testDb=4 to try connecting to a formality DB and ?testDb=w to try connecting to a wayangoutpost db" );

        processParams(req,out);
		out.print( "</BODY>" );
		out.print( "</HTML>" );
		out.close();
	}



    /**
	 * Handles HTTP POST requests.
	 */
	public void doPost( HttpServletRequest req, HttpServletResponse res )
		throws ServletException, IOException
	{
		res.setContentType( "text/html" );	// set the response type before sending data
		PrintWriter out = res.getWriter();	// for outputting text

		// TO DO: your request processing code goes here

		out.print( "<HTML>" );
		out.print( "<HEAD><TITLE> Test Servlet</TITLE></HEAD>" );
		out.print( "<BODY>" );
        out.print( "<br><br>This is a simple servlet that should demonstrate that servlets are working. Call it with ?testDb=4 to try connecting to a formality DB and ?testDb=w to try connecting to a wayangoutpost db" );

		// TO DO: your HTML generation code goes here
        processParams(req,out);
		out.print( "</BODY>" );
		out.print( "</HTML>" );

		out.close();
	}
}


