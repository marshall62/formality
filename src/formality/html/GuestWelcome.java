package formality.html;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Jun 14, 2012
 * Time: 3:16:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuestWelcome {
    String user;
    int userId;

    public GuestWelcome(String user, int userId) {
        this.user=user;
        this.userId = userId;
    }

    public String getPage () {
        String s = "<html>\n" +
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
                "<font size=+2> Guest&nbsp;Welcome</font></div></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "<p>\n" +
                "  <script type=\"text/javascript\">function setTextFocus()\n" +
                "{\n" +
                "document.forms[0].ul.focus();\n" +
                "}\n" +
                "//--></script>\n" +
                "<p><span class=\"style2\">Thanks for joining the community of 4mality users, " +user+ "!</span></p>\n" +
                "<p><span class=\"style2\">After a period of 3 months if you have not logged in we will attempt to send you an email to let you know that your account is expiring soon. &nbsp;If you don't login within 10 days of the warning, your account will be deleted. &nbsp;If you did not provide an email, your account will expire after 3 months of inactivity.</span></p>\n" +
                "<p><font color=orange></font><BR>\n" +
                "</p>\n" +
                "<FORM ACTION=\"FormalityServlet?fxn=createGuestLogin\" METHOD=\"POST\" NAME=\"loginForm\">\n" +
                "<input type=\"hidden\" name=\"ul\" value=\"" + user+ "\"/>" +
                "<input type=\"hidden\" name=\"un\" value=\"" + userId+ "\"/>" +
                "  <TABLE BORDER=\"0\" WIDTH=\"30%\"><tr>\n" +
                "  <td align=\"left\"><input type=\"submit\" name=\"mode\" value=\"Continue\"></td>\n" +
                "  <td align=\"middle\">&nbsp;</td>\n" +
                "  <td align=\"right\">&nbsp;</td>\n" +
                "  </tr></table>\n" +
                "<p class=\"style4\">&nbsp;</p>\n" +
                "<p><BR>\n" +
                "  </div>\n" +
                "</p>\n" +
                "</FORM>\n" +
                "<hr>\n" +
                "<table WIDTH=100%><TR bgColor=#eeeeee>\n" +
                "<TD><table width=100%><tr>\n" +
                "  <td>&nbsp;</TD>\n" +
                "  <td align=right>\n" +
                "<TABLE bgColor=#eeeeee border=0><TR><TD>\n" +
                "<font color=\"#9586f9\" face=\"Comic Sans MS\">\n" +
                "</font></td></TR></TABLE></td></TR></TABLE></td></TR></TABLE>\n" +
                "</body></html>";
        return s;
    }
}
