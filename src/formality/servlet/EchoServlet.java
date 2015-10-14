package formality.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class EchoServlet extends HttpServlet
{
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
		out.print( "<HEAD><TITLE>4mality Test Servlet</TITLE></HEAD>" );
		out.print( "<BODY>" );

		// TO DO: your HTML generation code goes here, for example
                	out.print( "<h2>4mality Test</h2>" );

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
		out.print( "<HEAD><TITLE>4mality Test Servlet</TITLE></HEAD>" );
		out.print( "<BODY>" );

		// TO DO: your HTML generation code goes here

		out.print( "</BODY>" );
		out.print( "</HTML>" );
		out.close();
	}
}

