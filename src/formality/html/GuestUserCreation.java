package formality.html;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Jun 14, 2012
 * Time: 2:49:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuestUserCreation {
    private String message;

    public GuestUserCreation() {
        this.message = "";
    }

    public GuestUserCreation (String message){
        this.message = message;
    }


    public String getPage () {
        String page = "<html>\n" +
                "<head>\n" +
                "<title>Login</title>\n" +
                "<style type=\"text/css\">\n" +
                "body {font-family: Comic Sans MS, sans-serif;color:#9999ff}\n" +
                "body {background-color:#f5f5f5}\n" +
                "table.titlebar, td.titlebar {background-color:#9586f9}\n" +
                "div.titlebar {color:#FFFFFF; font-family:Comic Sans MS}\n" +
                ".style2 {\n" +
                "\tfont-family: Arial, Helvetica, sans-serif;\n" +
                "\tcolor: #000000;\n" +
                "}\n" +
                ".style4 {\n" +
                "\tfont-family: Arial, Helvetica, sans-serif;\n" +
                "\tcolor: #FF0000;\n" +
                "\tfont-weight: bold;\n" +
                "}\n" +
                "</STYLE>\n" +
                "</head>\n" +
                "<BODY onLoad=\"setTextFocus()\"><table WIDTH=100% CELLPADDING=10 HEIGHT=65>\n" +
                "<tr><td CLASS=titlebar>\n" +
                "<div CLASS=titlebar>\n" +
                "<font size=+3 color=#ffff66>4</font>\n" +
                "<font size=+3 color=#ff8c00>m</font>\n" +
                "<font size=+3 color=#Adff2f>a</font>\n" +
                "<font size=+3 color=#ffa500>l</font>\n" +
                "<font size=+3 color=#87cefa>i</font>\n" +
                "<font size=+3 color=#ffff66>t</font>\n" +
                "<font size=+3 color=#Adff2f>y</font>\n" +
                "<font size=+3 color=#87cefa>:</font>\n" +
                "<font size=+2> Guest&nbsp;Login</font></div></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "<p>\n" +
                "  <script type=\"text/javascript\">function setTextFocus()\n" +
                "{\n" +
                "document.forms[0].ul.focus();\n" +
                "}\n" +
                "//-->\n" +
                "  </script>\n" +
                "  <DIV align=\"center\">\n" +
                "<span class=\"style2\">You can use 4mality as a guest user. &nbsp;&nbsp; Create a user name and password which will allow you to return to the system and use it as much as you want.* &nbsp;</span></p>\n" +
                "<h2>Create Guest User Login</h2>\n" +
                "<FORM ACTION=\"FormalityServlet?fxn=createGuestLogin\" METHOD=\"POST\" NAME=\"loginForm\">\n" +
                "<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=\"60%\">\n" +
                "<TR><TD><B>&nbsp;Login: </B></FONT></TD><TD><INPUT NAME=\"ul\" SIZE=\"36\" TYPE=\"text\" ></TD>\n" +
                "</TR><TR><TD><B>&nbsp;Password: </B></FONT></TD><TD><INPUT NAME=\"up\" SIZE=\"36\" TYPE=\"password\" ></TD>\n" +
                "</TR>\n" +
                "<TR>\n" +
                "  <TD><B>&nbsp;Email (optional):</B></TD>\n" +
                "  <TD><INPUT NAME=\"email\" SIZE=\"36\" TYPE=\"text\" id=\"email\" ></TD>\n" +
                "</TR>\n" +
                "</TABLE>\n" +
                "<font color=orange></font><BR><TABLE BORDER=\"0\" WIDTH=\"30%\"><tr>\n" +
                "  <td align=\"left\"><input type=\"submit\" value=\"Save\"></td>\n" +
                "  <td align=\"middle\">&nbsp;</td><td align=\"right\"><INPUT TYPE=\"reset\" VALUE=\"Clear\"></td></tr></table>\n" +
                "<p class=\"style4\"> " +message+  "</p>\n" +
                "<p><BR>\n" +
                "    </div>\n" +
                "</p>\n" +
                "</FORM>\n" +
                "<hr>\n" +
                "<table WIDTH=100%><TR bgColor=#eeeeee>\n" +
                "<TD><table width=100%><tr>\n" +
                "  <td><span class=\"style2\">*After a period of 3 months if you have not logged in we will attempt to send you an email to let you know that your account is expiring soon. &nbsp;If you don't login within 10 days of the warning, your account will be deleted. &nbsp;If you do not provide an email, your account will expire after 3 months of inactivity..</span></TD>\n" +
                "  <td align=right>\n" +
                "<TABLE bgColor=#eeeeee border=0><TR><TD>\n" +
                "<font color=\"#9586f9\" face=\"Comic Sans MS\">\n" +
                "</font></td></TR></TABLE></td></TR></TABLE></td></TR></TABLE>\n" +
                "</body></html>";
        return page;
    }
}
