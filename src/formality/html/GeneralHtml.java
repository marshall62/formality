package formality.html;

import java.awt.print.Pageable;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

import formality.controller.FormalitySubsystem;
import formality.controller.AuthoringSubsystem;
import formality.controller.StudentSubsystem;
import formality.controller.WayangSubsystem;
import formality.content.SystemInfo;
import formality.content.Question;
import formality.model.modeldata.MotivationalModelData;
import formality.model.StudentInfo;
import formality.Util.DataTuple;
import formality.systemerror.AuthoringException;

/**
 * <p>Title: </p>
 */
public class GeneralHtml {
    public static final String questionID_ = "qID";
    public static final String questionIndex_ = "qindex";
    public static final String questionTopic_ = "qtopic";
    public static final String categoryID_ = "catID";
    public static final String categoryName_ = "catname";
    public static final String moduleID_ = "mID";
    public static final String classID_ = "classID";
    public static final String hintID_ = "hID";
    public static final String coachID_ = "chID";
    public static final String studentID_ = "sID";
    public static final String teacherID_ = "teacherID";
    public static final String courseController_ = "courseController";
    public static final String district_ = "district";
    public static final String institution_ = "institution";
    public static final String moduleType_ = "mtype";
    public static final String moduleName_ = "mname";
    public static final String questionType_ = "qtype";
    public static final String exitQuestion_ = "exitq";
    public static final String query_ = "hquery";
    public static final String response_ = "hresp";
    public static final String coach_ = "coach";
    public static final String strategyID_ = "stratID";
    public static final String readyStatus_ = "readyStatus";
    public static final String isReentrant_ = "isReentrant";
    public static final String author_ = "author";
    public static final String source_ = "source";
    public static final String level_ = "level";
    public static final String stem_ = "stem";
    public static final String degree_ = "degree";
    public static final String first_ = "first";
    public static final String ansChoiceLink_ = "anslk";
    public static final String correctAnsChoice_ = "corans";
    public static final String ansChoiceA_ = "ansa";
    public static final String ansChoiceAFeedback_ = "fdbka";
    public static final String ansChoiceB_ = "ansb";
    public static final String ansChoiceBFeedback_ = "fdbkb";
    public static final String ansChoiceC_ = "ansc";
    public static final String ansChoiceCFeedback_ = "fdbkc";
    public static final String ansChoiceD_ = "ansd";
    public static final String ansChoiceDFeedback_ = "fdbkd";
    public static final String standard1_ = "std1";
    public static final String standard2_ = "std2";
    public static final String standard3_ = "std3";
    public static final String ccss1 = "ccss1";
    public static final String ccss2 = "ccss2";
    public static final String ccss3 = "ccss3";
    public static final String parentModID_ = "pmid";
    public static final String childModID_ = "cmid";
    public static final String orderIndex_ = "oind";
    public static final String externalActivityUrl_ = "extacturl";
    public static final String glossaryTerm_ = "glossaryTerm";
    public static final String language_ = "language";
    public static final String includeModule_ = "includeModule";
    public static final String courseName_ = "courseName";
    public static final String fname = "fname";
    public static final String lname = "lname";
    public static final String uname = "uname";
    public static final String password = "password";
    public static final String initials = "initials";
    public static final String age = "age";
    public static final String gender = "gender";
//    public static final String language = "language";
    public static final String iep = "iep";
    public static final String readingLevel = "readingLev";
    public static final String mathLevel = "mathLev";
    public static final String title = "title";

    public static final String selLevel_ = "sellevel";
    public static final String selHintID_ = "selhint";
    public static final String submittedAns_ = "subans";
    public static final String answerLayout = "answerLayout";
    public static final String moduleQuestionOrder_ = "modQuestionOrder";

    public static final String[] degrees = {"3rd grade", "easier", "medium", "harder"};
    public static final String[] levels = {"1", "2", "3", "4", "5"};
    public static final String[] coaches = {"a", "b", "c", "d"};
    public static final String[] ans = {"A", "B", "C", "D"};
    public static final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    public static final String corrStyleBeginTag_ = "<span style=\"color:black;background-color:#Adff2f;font-weight: bold;font-size: -1;\">";
    public static final String incorrStyleBeginTag_ = "<span style=\"color:black;background-color:#dc143c;font-weight: bold;font-size: -1;\">";
    public static final String styleEndTag_ = "</span>";
    public static final String corrChoice_ = "<font color=\"green\"><b>ok</b></font>";
    public static final String incorrChoice_ = "<font color=\"red\"><b>X</b></font>";
    public static final String greenCheck_ = "greenCheck.gif";

    public static final String largeTitleFontPx_ = "24";
    public static final String smallTitleFontPx_ = "16";
    public static final String linkFont_ = "<font color=\"#9586f9\" face=\"Comic Sans MS\">";
    public static final String navSelectedColor_ = "#ff66ff";

    /*framework skills links (to Vrroom). corresponds to this array in StudentInfo:
   "4.N.1","4.N.2","4.N.3","4.N.4","4.N.5","4.N.6","4.N.7","4.N.8","4.N.9","4.N.10","4.N.11","4.N.12",
    "4.N.13","4.N.14","4.N.15","4.N.16","4.N.17","4.N.18","4.P.1","4.P.2","4.P.3","4.P.4","4.P.5","4.P.6",
    "4.G.1","4.G.2","4.G.3","4.G.4","4.G.5","4.G.6","4.G.7","4.G.8","4.G.9","4.M.1","4.M.2","4.M.3","4.M.4",
    "4.M.5","4.D.1","4.D.2","4.D.3","4.D.4","4.D.5","4.D.6"};
    */
    public static final String[] stdsUrls = {
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,754",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,755",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,756",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,757",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,758",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,759",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,760",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,761",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,762",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,763",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,764",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,765",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,766",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,767",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,768",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,769",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,770",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,853,782,772,771",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,984,883,879,873",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,984,883,879,874",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,984,883,879,875",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,984,883,879,876",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,984,883,879,877",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,984,883,879,878",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1010",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1011",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1012",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1013",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1014",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1015",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1016",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1017",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1114,1023,1019,1018",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1194,1146,1138,1133",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1194,1146,1138,1134",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1194,1146,1138,1135",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1194,1146,1138,1136",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1194,1146,1138,1137",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1279,1221,1215,1209",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1279,1221,1215,1210",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1279,1221,1215,1211",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1279,1221,1215,1212",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1279,1221,1215,1213",
            "http://onion.cs.umass.edu/vrroomj/servlet/VrroomServlet?mode=2&subMode=1&tree.path=720,1279,1221,1215,1214"
    };



    public GeneralHtml() {
    }


    public void getLoginPage(String login, String pwd, SystemInfo info,
                             String msg, StringBuffer page) {
        getPageHeader("Login", true, page);
        getBodyOnLoad("setTextFocus()", page);
        getPageBanner("Login", page);
        getJavascriptBegin(page);
        getJavascriptFunction("setTextFocus", "", "document.forms[0]." + FormalitySubsystem.userLogin + ".focus();", page);
        getJavascriptEnd(page);
        page.append("<DIV align=\"center\">");
        page.append("<h2>Please Login</h2>\r\n");
//       page.append("<FORM ACTION=\"").append(getLoginUrl(info.getServletRootAndID()));
        page.append("<FORM ACTION=\"").append(getLoginUrl(info.getServletRootAndID()));
        page.append("\" METHOD=\"POST\" NAME=\"loginForm\">\r\n");
        page.append("<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=\"60%\">\r\n");
        page.append("<TR><TD><B>&nbsp;Login: </B></FONT></TD><TD><INPUT NAME=\"");
        page.append(FormalitySubsystem.userLogin).append("\" SIZE=\"36\" TYPE=\"text\" ");
        if (login != null) {
            page.append("value = \"").append(login).append("\"");
        }
        page.append("></TD>\r\n");
        page.append("</TR><TR><TD><B>&nbsp;Password: </B></FONT></TD><TD><INPUT NAME=\"");
        page.append(FormalitySubsystem.userPassword).append("\" SIZE=\"36\" TYPE=\"password\" ");
        if (pwd != null) {
            page.append("value = \"").append(pwd).append("\"");
        }
        page.append("></TD>\r\n");
        page.append("</TR></TABLE>");
        page.append("<font color=orange>").append(msg).append("</font>");
        page.append("<BR>");
        page.append("<TABLE BORDER=\"0\" WIDTH=\"30%\"><tr>\r\n");
        page.append("<td align=\"left\"><INPUT TYPE=\"submit\" VALUE=\"Login\"></td>");
        page.append("<td align=\"middle\"><INPUT TYPE=\"submit\" NAME=\"guestLogin\" VALUE=\"Guest Login\"></td>");
        page.append("<td align=\"right\"><INPUT TYPE=\"reset\" VALUE=\"Clear\"></td></tr></table><BR>\r\n");
        page.append("</div>\r\n</FORM>\r\n");
        Vector links = new Vector();
        //links.add(getLinkStr(info.getContextPath()+"/index.html", "welcome page"));  //http://zuke.cs.umass.edu/4mality
        getGeneralPageFooter(links, page);
    }

    public void getErrorPage(String msg, String retUrl, StringBuffer page, SystemInfo info, StringBuffer stackTrace) {
        GeneralHtml.getSmallPageHeader("Error Notice", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Error Notice", page);
        page.append("<table height=15%><TR><TD>");
        page.append("<h3><font color=orange face=\"Comic Sans MS\">").append("An error has occurred.<br>");
        if (msg != null)
            page.append(msg).append("<br>");
        getLink(retUrl, "return.", page);
        page.append("</font></h3>");
        page.append("</td></tr></table>");
        page.append(stackTrace.toString());
        getGeneralPageFooter(null, page);
    }

    public static void getPageHeader(String title, StringBuffer page) {
        page.append("<html>").append("\r\n");
        page.append("<head>").append("\r\n");
        page.append("<title>").append(title).append("</title>").append(
                "\r\n");
        getPageStyle(largeTitleFontPx_, page);
        page.append("<SCRIPT LANGUAGE=\"JavaScript\">").append("\r\n");
        // getJavascriptExtraWindow("600", "600", page);
        page.append("</SCRIPT>").append("\r\n");
        page.append("</head>").append("\r\n");
    }

    public static void getPageHeader(String title, boolean comic, StringBuffer page) {
        page.append("<html>").append("\r\n");
        page.append("<head>").append("\r\n");
        page.append("<title>").append(title).append("</title>").append(
                "\r\n");
        if (comic)
            getComicPageStyle(page);
        else
            getPageStyle(largeTitleFontPx_, page);
        //   page.append("<SCRIPT LANGUAGE=\"JavaScript\">").append("\r\n");
        //   getJavascriptExtraWindow("600", "600", page);
        //   page.append("</SCRIPT>").append("\r\n");
        page.append("</head>").append("\r\n");
    }

    public static void getSmallPageHeader(String title, boolean comic, StringBuffer page,
                                          SystemInfo info, boolean wayangMode) {
        page.append("<html>").append("\r\n");
        page.append("<head>").append("\r\n");
        page.append("<meta name=\"google-translate-customization\" " +
                "content=\"b1d4b81c063d281b-29808bd6cdebdd2d-gad74625aa8b4b2a0-1d\"></meta>\n"
                );
        page.append("<title>").append(title).append("</title>").append(
                "\r\n");
//          if(comic)
//              getComicPageStyle(page);
        //         else
        //            getPageStyle(smallTitleFontPx_, page);
        getStyleAndJSLinks(page, true, info, wayangMode);
        //page.append("<SCRIPT LANGUAGE=\"JavaScript\">").append("\r\n");
        //getJavascriptExtraWindow("600", "600", page);
        //getGlossaryFunctions(page);
        //page.append("</SCRIPT>").append("\r\n");
        page.append("</head>").append("\r\n");
    }

    public static void getPageHeaderForWayang(String title, boolean comic, StringBuffer page,
                                              SystemInfo info, boolean wayangMode, long elapsedTime, long probElapsedTime, String clrURL) {
        page.append("<html>").append("\r\n");
        page.append("<head>").append("\r\n");
        page.append("<title>").append(title).append("</title>").append(
                "\r\n");
        // doesn't close the <script> tag so that we can add some more functions and then put in the close tag ourselves.
        getStyleAndJSLinks(page, false, info, wayangMode);
        getJavascriptTimers(page, elapsedTime, probElapsedTime);
        page.append("\n</script>");
        GeneralHtml.getJavascriptClearQ(clrURL, page);
        page.append("</head>").append("\r\n");
    }

    // Adds a page ready function that runs at page load time.  When the submitButton gets clicked,  this will
    // update the elapsed time hidden input.
    private static void getJavascriptTimers(StringBuffer page, long elapsedTime, long probElapsedTime) {
        String x = "\nvar startTime=0;\n" +
                "var elapsedTime;\n" +
                "var probElapsed;\n" +
                "$(document).ready(function() {\n" +
                "\tvar d =new Date();\n" +
                "\telapsedTime= " +elapsedTime +";\n" +
                "\tprobElapsed= " +probElapsedTime + ";\n" +
                "\tstartTime = d.getTime();\n" +
                "\t$('#submitButton').click(function () {\n" +
                "\t\tvar a= incrementTime();\n" +
                "\t\t$('#elapsedTime').attr('value',a[1]);\n" +
                "\t\t$('#probElapsedTime').attr('value',a[0]);\n" +
                "\t\t$('#submitForm').submit();\n" +
                "\t});\n" +
                "\t$('#estellaLinka,#estellaLinkb').click(function () {\n" +
                "\t\tvar a= incrementTime();\n" +
                "\t\tvar estellaURL = $('#estellaLinkb').attr('href');\n" +
                "\t\tgotoHint(estellaURL,a[0],a[1]);\n" +
                "\t\treturn false;\n" +
                "\t});\n" +
                "\t$('#h2hLinka,#h2hLinkb').click(function () {\n" +
                "\t\tvar a= incrementTime();\n" +
                "\t\tvar h2hURL = $('#h2hLinkb').attr('href');\n" +
                "\t\tgotoHint(h2hURL,a[0],a[1]);\n" +
                "\t\treturn false;\n" +
                "\t});\n" +
                "\t$('#bearLinka,#bearLinkb').click(function () {\n" +
                "\t\tvar a= incrementTime();\n" +
                "\t\tvar bearURL = $('#bearLinkb').attr('href');\n" +
                "\t\tgotoHint(bearURL,a[0],a[1]);\n" +
                "\t\treturn false;\n" +
                "\t});\n" +

                "\t$('#backToHints').click(function () {\n" +
                "\t\tvar a= incrementTime();\n" +
                "\t\tvar url = $('#backToHints').attr('href');\n" +
                "\t\tgotoHint(url,a[0],a[1]);\n" +
                "\t\treturn false;\n" +
                "\t});\n" +

                "\t$('#vicunaLinka,#vicunaLinkb').click(function () {\n" +
                "\t\tvar a= incrementTime();\n" +
                "\t\tvar vicunaURL = $('#vicunaLinkb').attr('href');\n" +
                "\t\tgotoHint(vicunaURL,a[0],a[1]);\n" +
                "\t\treturn false;\n" +
                "\t});\n" +
                "});\n" +
                "function gotoHint (url, probElapsed, elapsedTime) {\n" +
                "            url += \"&elapsedTime=\" + elapsedTime + \"&probElapsedTime=\" + probElapsed;\n" +
                "        //    alert('In gotoHint with ' + url);\n" +
                "            document.location.href=url;\n" +
                "        }\n" +
                "\n" +
                "function incrementTime () {\n" +
                "        var dt = new Date();\n" +
                "        var now = dt.getTime();\n" +
                "        probElapsed += now - startTime;\n" +
                "        elapsedTime += now - startTime;\n" +
                "        var a = [probElapsed, elapsedTime];\n" +
                "        return a;\n" +
                "    }";
        page.append(x);

    }


    public static void getSmallPageHeader2(String title, boolean comic, StringBuffer page, SystemInfo info) {
        page.append("<html>").append("\r\n");
        page.append("<head>").append("\r\n");
        page.append("<title>").append(title).append("</title>").append(
                "\r\n");
        getStyleAndJSLinks(page, true, info, false);
    }


    public static void getPageBanner(String title, StringBuffer page) {
        page.append("<table WIDTH=100% CELLPADDING=10 HEIGHT=65>").append("\r\n");
        page.append("<tr><td CLASS=titlebar>").append("\r\n");
        page.append("<div CLASS=titlebar>").append("\r\n");
        page.append("<font size=+3 color=#ffff66>4</font>").append("\r\n");
        page.append("<font size=+3 color=#ff8c00>m</font>").append("\r\n");
        page.append("<font size=+3 color=#Adff2f>a</font>").append("\r\n");
        page.append("<font size=+3 color=#ffa500>l</font>").append("\r\n");
        page.append("<font size=+3 color=#87cefa>i</font>").append("\r\n");
        page.append("<font size=+3 color=#ffff66>t</font>").append("\r\n");
        page.append("<font size=+3 color=#Adff2f>y</font>").append("\r\n");
        page.append("<font size=+3 color=#87cefa>:</font>").append("\r\n");
        page.append("<font size=+2>&nbsp;").append(title);
        page.append("</font></div>").append("</td>").append("\r\n");
        page.append("</tr>").append("\r\n");
        page.append("</table>").append("\r\n");
    }

    public static void getSmallBanner(String title, StringBuffer page) {
        page.append("<table WIDTH=100% CELLPADDING=10 HEIGHT=5>").append("\r\n");
        page.append("<tr><td CLASS=titlebar>").append("\r\n");
        page.append("<div CLASS=titlebar>").append("\r\n");
        page.append("<font color=#ffff66>4</font>").append("\r\n");
        page.append("<font color=#ff8c00>m</font>").append("\r\n");
        page.append("<font color=#Adff2f>a</font>").append("\r\n");
        page.append("<font color=#ffa500>l</font>").append("\r\n");
        page.append("<font color=#87cefa>i</font>").append("\r\n");
        page.append("<font color=#ffff66>t</font>").append("\r\n");
        page.append("<font color=#Adff2f>y</font>").append("\r\n");
        page.append("<font color=#87cefa>:</font>").append("\r\n");
        page.append("&nbsp;").append(title);
        page.append("</div>").append("</td>").append("\r\n");
        page.append("</tr>").append("\r\n");
        page.append("</table>").append("\r\n");
        /*
        <table WIDTH=100% CELLPADDING=10 HEIGHT=5>
<tr><td CLASS=titlebar>
<div CLASS=titlebar>
<font  color=#ffff66>4</font>
<font  color=#ff8c00>module</font>
<font  color=#Adff2f>a</font>
<font  color=#ffa500>l</font>
<font  color=#87cefa>i</font>
<font  color=#ffff66>t</font>
<font  color=#Adff2f>y</font>
<font  color=#87cefa>:</font>
<font >&nbsp;Question Viewer</font></div></td>
</tr>
</table>
*/
    }

    // motivational scoreboard assumed not null
    public static void getSmallBannerWithScore(String title,
                                               SystemInfo info,
                                               MotivationalModelData motModel,
                                               StringBuffer page) throws Exception {
        page.append("<TABLE WIDTH=100% CELLPADDING=10 HEIGHT=5 bgcolor=#9586f9>").append("\r\n");
        page.append("<tr><td CLASS=titlebar>").append("\r\n");
        page.append("<div CLASS=titlebar>").append("\r\n");
        page.append("<font color=#ffff66>4</font>").append("\r\n");
        page.append("<font color=#ff8c00>m</font>").append("\r\n");
        page.append("<font color=#Adff2f>a</font>").append("\r\n");
        page.append("<font color=#ffa500>l</font>").append("\r\n");
        page.append("<font color=#87cefa>i</font>").append("\r\n");
        page.append("<font color=#ffff66>t</font>").append("\r\n");
        page.append("<font color=#Adff2f>y</font>").append("\r\n");
        page.append("&nbsp;").append(title);
        page.append("</div>").append("</td>").append("\r\n");
        page.append("<td align=right><TABLE><tr><td><div CLASS=titlebar>").append("\r\n");
        page.append("&nbsp;Scoreboard:&nbsp;</div></td><td>").append("\r\n");
        getScoreboard(motModel, page);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td></tr></table>").append("\r\n");
    }

    // if images are ever required: info.getContextPath()
    public static void getScoreboard(MotivationalModelData motModel, StringBuffer page) throws Exception {
        page.append("<TABLE BORDER=1 CELLPADDING=2 CELLSPACING=0>").append("\r\n");
        StringBuffer topRow = new StringBuffer();
        topRow.append("<TR BGCOLOR=#dddddd VALIGN=top><TD align=center><div CLASS=pgtxt> question </div></TD>");
        StringBuffer middleRow = new StringBuffer();
        middleRow.append("<TR BGCOLOR=#eeeeee VALIGN=top><TD align=center><div CLASS=pgtxt>my score </div></TD>");
        StringBuffer bottomRow = new StringBuffer();
        bottomRow.append("<TR BGCOLOR=#dddddd VALIGN=top><TD align=center><div CLASS=pgtxt> max score </div></TD>");
        Iterator scoreIter = motModel.getScoreDataIterator();
        //dt: qID, score
        int count = 1;
        while (scoreIter.hasNext()) {
            DataTuple dt = (DataTuple) scoreIter.next();
            if (count == motModel.getSelectedIndex()) { //highlight the selected column
                topRow.append("<TD align=center BGCOLOR=#ffffff><div CLASS=pgtxt>&nbsp;").append(count).append("&nbsp;</div></td>");
                middleRow.append("<TD align=center BGCOLOR=#ffffff><div CLASS=pgtxt>&nbsp;").append(dt.getSecond()).append("&nbsp;</div></td>");
                bottomRow.append("<TD align=center BGCOLOR=#ffffff><div CLASS=pgtxt>&nbsp;").append(motModel.getMaxScore()).append("&nbsp;</div></td>");
            } else {
                topRow.append("<TD align=center><div CLASS=pgtxt>&nbsp;").append(count).append("&nbsp;</div></td>");
                middleRow.append("<TD align=center><div CLASS=pgtxt>&nbsp;").append(dt.getSecond()).append("&nbsp;</div></td>");
                bottomRow.append("<TD align=center><div CLASS=pgtxt>&nbsp;").append(motModel.getMaxScore()).append("&nbsp;</div></td>");
            }
            count++;
        }
        //can add aggregates here in future
        topRow.append("</TR>").append("\r\n");
        middleRow.append("</TR>").append("\r\n");
        bottomRow.append("</TR>").append("\r\n");
        page.append(topRow.toString());
        page.append(middleRow.toString());
        page.append(bottomRow.toString());
        page.append("</table>").append("\r\n");
    }

    public static void getWelcomeBanner(StringBuffer page) {
        page.append("<table bgcolor=#ddaodd width=100%><tr><td align=center>");
        page.append("<h1>Welcome to 4Mality</h1>");
        page.append("<h2> An MCAS 4th Grade Math Test Prep Tutor Utilizing an Adaptive, Bayesian Student Model</h3>");
        page.append("<h3>Where succeeding on the MCAS is merely a <i>4MALITY</i>!</h3></td></tr>");
        page.append("</table>").append("\r\n");
    }

    public static void getPageFooter(StringBuffer page) {
        page.append("</body>").append("\r\n");
        page.append("</html>").append("\r\n");
    }

    public static void getPageStyle(String titleFontSizePx, StringBuffer page) {
        page.append("<style type=\"text/css\">").append("\r\n");
        page.append("body {background-color:#F8F8FF;}").append("\r\n");
        page.append("A:hover,A:active {text-decoration:underline}").append("\r\n");
        page.append("table.titlebar, td.titlebar {background-color:#9586f9}").append("\r\n");
        if (titleFontSizePx == null || titleFontSizePx.equals(""))
            titleFontSizePx = "24";
        page.append("div.titlebar {color:#FFFFFF; font-size:");
        page.append(titleFontSizePx).append("px; font-family:Comic Sans MS}").append("\r\n");
        page.append("div.pgtxt {color:#9586f9; font-family:Comic Sans MS}");
        page.append("div.greypgtxt {color:#bbbbbb; font-family:Comic Sans MS}");
        page.append("</STYLE>").append("\r\n");
    }

    public static void getComicPageStyle(StringBuffer page) {
        page.append("<style type=\"text/css\">").append("\r\n");
        page.append("body {font-family: Comic Sans MS, sans-serif;color:#9999ff}").append("\r\n");
        page.append("body {background-color:#f5f5f5}").append("\r\n");
        page.append("table.titlebar, td.titlebar {background-color:#9586f9}").append("\r\n");
        page.append("div.titlebar {color:#FFFFFF; font-family:Comic Sans MS}").append("\r\n");
        page.append("</STYLE>").append("\r\n");
    }

    public static String getCorrectStyleFormat(String str) {
        return "<font color=\"green\">" + str + "</font>";
        // return corrStyleBeginTag_+str+styleEndTag_;
    }

    public static String getIncorrectStyleFormat(String str) {
        return "<font color=\"red\">" + str + "</font>";
        //return incorrStyleBeginTag_+str+styleEndTag_;
    }

    public static void getFormattedLink(String url, String text, StringBuffer page) {
        page.append("<a href=\"").append(url);
        page.append("\"><font color=\"#9586f9\" face=\"Comic Sans MS\">");
        page.append(text).append("</font></a>");
    }

    public static String getFormattedLinkStr(String url, String text) {
        StringBuffer page = new StringBuffer();
        page.append("<a href=\"").append(url);
        page.append("\"><font color=\"#9586f9\" face=\"Comic Sans MS\">");
        page.append(text).append("</font></a>");
        return page.toString();
    }


    public static void getLink(String url, String text, StringBuffer page) {
        page.append("<a href=\"").append(url);
        page.append("\">");
        page.append(text).append("</a>");
    }

    public static void addToLink(String name, String value, StringBuffer link) {
        link.append("&").append(name).append("=").append(value);
    }

    public static String addToLink(String name, String value, String link) {
        return link + "&" + name + "=" + value;
    }

    public static String getLinkStr(String url, String text) {
        StringBuffer page = new StringBuffer();
        page.append("<a href=\"").append(url);
        page.append("\">");
        page.append(text).append("</a>");
        return page.toString();
    }


    public static void getGeneralPageFooter(Vector links, StringBuffer page) {
        page.append("<hr>").append("\r\n");
        page.append("<table WIDTH=100%><TR bgColor=#eeeeee>").append("\r\n");
        page.append("<TD><table width=100%><tr><td>").append("\r\n");
        page.append("&nbsp;").append("\r\n");
        page.append("</TD><td align=right>").append("\r\n");
        page.append("<TABLE bgColor=#eeeeee border=0><TR><TD>").append("\r\n");
        page.append("<font color=\"#9586f9\" face=\"Comic Sans MS\">").append("\r\n");
        if (links != null) {
            for (int i = 0; i < links.size(); i++) {
                String curLink = (String) links.get(i);
                if (i + 1 != links.size())
                    page.append(curLink).append("&nbsp;|&nbsp;");
                else
                    page.append(curLink);
            }
        }
        page.append("</font></td></TR></TABLE></td></TR></TABLE></td></TR></TABLE>").append("\r\n");
        page.append("</body></html>").append("\r\n");
    }

    public static void getPageLinksFooter(Vector links, StringBuffer page) {
        page.append("<hr>").append("\r\n");
        page.append("<table WIDTH=100%><TR bgColor=#eeeeee>").append("\r\n");
        page.append("<TD><table width=100%><tr><td>").append("\r\n");
        page.append("&nbsp;").append("\r\n");
        page.append("</TD><td align=right>").append("\r\n");
        page.append("<TABLE bgColor=#eeeeee border=0><TR><TD>").append("\r\n");
        page.append("<font color=\"#9586f9\" face=\"Comic Sans MS\">").append("\r\n");
        if (links != null) {
            for (int i = 0; i < links.size(); i++) {
                String curLink = (String) links.get(i);
                if (i + 1 != links.size())
                    page.append(curLink).append("&nbsp;|&nbsp;");
                else
                    page.append(curLink);
            }
        }
        page.append("</TR></TABLE></td></TR></TABLE></td></TR></TABLE>").append("\r\n");
    }

    public static String getLoginUrl(String root) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=savenewq
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.loginFxnParam);
        return link.toString();
    }

    public static String getLoginUrl(String root, String param, String val) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=savenewq
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.loginFxnParam);
        if (param != null && !param.equals("")) {
            link.append("&");
            link.append(param).append("=").append(val);
        }
        return link.toString();
    }

    public static String getSaveNewQuestionSubmit(String root) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=savenewq
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.saveNewQuestionMode_);
        return link.toString();
    }

    public static String getSaveNewSurveyQuestionSubmit(String root) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=savenewq
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.saveNewSurveyQuestionMode_);
        return link.toString();
    }

    public static String getSaveQuestionSubmit(String root, String qID) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=savenewq
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.saveQuestionMode_);
        link.append("&");
        link.append(questionID_).append("=").append(qID);
        return link.toString();
    }

    public static String getSaveSurveyQuestionSubmit(String root, String qID) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=savenewq
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.saveSurveyQuestionMode_);
        link.append("&");
        link.append(questionID_).append("=").append(qID);
        return link.toString();
    }

    public static String getEditHintUrl(String root, String hID) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=edithint&hID=xx
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.editHintMode_);
        link.append("&");
        link.append(hintID_).append("=").append(hID);
        return link.toString();
    }

    public static String getSaveHintSubmit(String root, String hID) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=savehint&hID=xx
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.saveHintMode_);
        link.append("&");
        link.append(hintID_).append("=").append(hID);
        return link.toString();
    }

    public static String getEditHintUrl(String root, String hID, String qID) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=edithint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.editHintMode_);
        link.append("&");
        link.append(hintID_).append("=").append(hID);
        link.append("&");
        link.append(questionID_).append("=").append(qID);
        return link.toString();
    }

    public static String getSelectHintUrl(String root, String qID) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.selectHintMode_);
        link.append("&");
        link.append(questionID_).append("=").append(qID);
        return link.toString();
    }

    public static String getSelectHintUrl(String root) {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(AuthoringSubsystem.selectHintMode_);
        return link.toString();
    }

    // the Vector has entries in name/value pairs- may be null
    public static String getAuthorUrl(String root, String mode,
                                      Vector params) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (params != null) {
            for (int i = 0; i < params.size(); i += 2) {
                String n = (String) params.get(i);
                String v = (String) params.get(i + 1);
                link.append("&").append(n).append("=").append(v);
            }
        }
        return link.toString();
    }

    // the String[] has entries in name/value pairs- where s[i]=name and s[i+1]=value
    public static String getAuthorUrl(String root, String mode,
                                      String[] params) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (params != null) {
            for (int i = 0; i < params.length; i += 2) {
                String n = params[i];
                String v = params[i + 1];
                link.append("&").append(n).append("=").append(v);
            }
        }
        return link.toString();
    }

    // the Vector has entries in name/value pairs- may be null
    public static String getAuthorQuestionUrl(String root, String mode, String qtype,
                                              Vector params) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (params != null) {
            for (int i = 0; i < params.size(); i += 2) {
                String n = (String) params.get(i);
                String v = (String) params.get(i + 1);
                link.append("&").append(n).append("=").append(v);
            }
        }
        if (qtype != null)
            link.append("&" + GeneralHtml.questionType_ + "=" + qtype);
        return link.toString();
    }

    public static String getAuthorUrl(String root, String mode) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        return link.toString();
    }


    public static String getAuthorUrl(String root, String mode,
                                      String paramName, String paramVal) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (paramName != null && paramVal != null)
            link.append("&").append(paramName).append("=").append(paramVal);
        return link.toString();
    }

    public static String getWayangUrl(String root, String mode,
                                      String[] paramNames, String[] paramVals) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.wayangFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (paramNames !=null) {
            if (paramNames.length != paramVals.length)
                throw new Exception("paramNames and paramVals must have same number params");
            for (int i=0;i<paramNames.length;i++)
                link.append("&").append(paramNames[i]).append("=").append(paramVals[i]);

        }
        return link.toString();
    }



    public static String getTeacherUrl(String root, String mode) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.teacherFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        return link.toString();
    }

    public static String getTeacherUrl(String root, String mode,
                                       String paramName, String paramVal) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.teacherFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (paramName != null && paramVal != null)
            link.append("&").append(paramName).append("=").append(paramVal);
        return link.toString();
    }

    public static String getTeacherUrl(String root, String mode,
                                       String pName1, String pVal1,
                                       String pName2, String pVal2) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.teacherFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (pName1 != null && pVal1 != null)
            link.append("&").append(pName1).append("=").append(pVal1);
        if (pName2 != null && pVal2 != null)
            link.append("&").append(pName2).append("=").append(pVal2);
        return link.toString();
    }

    public static String getStudentUrl(String root, String mode) throws Exception {
        StringBuffer link = new StringBuffer();
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.studentFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        return link.toString();
    }

    // the Vector has entries in name/value pairs- may be null
    public static String getStudentUrl(String root, String mode,
                                       Vector params) throws Exception {
        StringBuffer link = new StringBuffer();
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.studentFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (params != null) {
            for (int i = 0; i < params.size(); i += 2) {
                String n = (String) params.get(i);
                String v = (String) params.get(i + 1);
                link.append("&").append(n).append("=").append(v);
            }
        }
        return link.toString();
    }

    public static String getStudentUrl(String root, String mode,
                                       String param, String val) throws Exception {
        StringBuffer link = new StringBuffer();
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.studentFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (param != null && !param.equals("")) {
            link.append("&");
            link.append(param).append("=").append(val);
        }
        return link.toString();
    }

    public static String getStudentUrl(String root, String mode,
                                       String param1, String val1,
                                       String param2, String val2) throws Exception {
        StringBuffer link = new StringBuffer();
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.studentFxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (param1 != null && !param1.equals("")) {
            link.append("&");
            link.append(param1).append("=").append(val1);
        }
        if (param2 != null && !param2.equals("")) {
            link.append("&");
            link.append(param2).append("=").append(val2);
        }
        return link.toString();
    }

    public static String getLogoutUrl(String servletURL, String userID) throws Exception {
        String url = getUrl(servletURL, FormalitySubsystem.loginFxnParam,
                FormalitySubsystem.loginMode,
                FormalitySubsystem.logoutParam, "1");
        return url + "&" + FormalitySubsystem.userIDParam + "=" + userID;
    }

    public static String getUrl(String root, String fxnParam,
                                String mode, String param, String val) throws Exception {
        StringBuffer link = new StringBuffer();
        ///...servlet/4mality?fxn=aut&mode=selecthint&hID=xx&qID=yy
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(fxnParam);
        link.append("&");
        link.append(FormalitySubsystem.modeParam).append("=").append(mode);
        if (param != null && val != null)
            link.append("&").append(param).append("=").append(val);
        return link.toString();
    }

    public static String getBaseAuthorUrl(String root) throws Exception {
        StringBuffer link = new StringBuffer();
        link.append(root);
        link.append(FormalitySubsystem.fxnParam).append("=").append(FormalitySubsystem.authorFxnParam);
        return link.toString();
    }

    public static String appendToUrl(String url, String param, String val) {
        StringBuffer link = new StringBuffer();
        link.append(url);
        link.append("&");
        link.append(param).append("=");
        if (val == null)
            link.append("");
        else
            link.append(val);
        return link.toString();
    }

    public static void getHiddenParam(String name, String value, StringBuffer page) {
        page.append("<INPUT id=\"").append(name).append("\" TYPE=HIDDEN NAME=\"").append(name);
        page.append("\" VALUE=\"").append(value).append("\">\r\n");
    }

    public static String getAbbrevString(String str, int maxLen) {
        String newStr = "";
        if (str.length() > maxLen)
            newStr = str.substring(0, maxLen) + "...";
        else
            newStr = str;
        //remove any html by looking for <
        int ind = newStr.indexOf('<');
        if (ind > -1) {
            newStr = newStr.substring(0, ind);
            newStr += "&nbsp;";
        }
        return newStr;
    }

    public static String getParamStr(Map params, String key, String alt) throws Exception {
        String str = null;
        String[] val;
        //Either cast the object to a String[] (easier) or use the methods in java.lang.reflect.Array (more general).
        Object o = params.get(key);
        if (o instanceof String)
            str = (String) o;
        else if (o instanceof String[]) {
            val = (String[]) o;
            str = val[0];
            //some implementations: hrow new Exception(" val[0] = "+val[0]);
        }
        if (str == null)
            return alt;
        else
            return str;
    }

    public static List<String> getParamStrings(Map params, String key) throws Exception {
        //Either cast the object to a String[] (easier) or use the methods in java.lang.reflect.Array (more general).
        Object o = params.get(key);
        List<String> temp = new ArrayList<String>();

        if (o instanceof String) {
            temp.add(((String) o));
            return temp;
        } else if (o instanceof String[]) {
            String[] oo = (String[]) o;
            for (String s : oo) {
                if (s != null)
                    temp.add(s.trim());
            }
            return temp;
        } else
            return null;
    }


    public static int getParamInt(Map params, String key, int alt) throws Exception {
        String str = null;
        String[] val;
        //Either cast the object to a String[] (easier) or use the methods in java.lang.reflect.Array (more general).
        Object o = params.get(key);
        if (o instanceof String)
            str = (String) o;
        else if (o instanceof String[]) {
            val = (String[]) o;
            str = val[0];
            //some implementations: hrow new Exception(" val[0] = "+val[0]);
        }
        if (str == null)
            return alt;
        else
            return Integer.parseInt(str);
    }

    public static long getParamLong(Map params, String key, long alt) throws Exception {
        String str = null;
        String[] val;
        //Either cast the object to a String[] (easier) or use the methods in java.lang.reflect.Array (more general).
        Object o = params.get(key);
        if (o instanceof String)
            str = (String) o;
        else if (o instanceof String[]) {
            val = (String[]) o;
            str = val[0];
            //some implementations: hrow new Exception(" val[0] = "+val[0]);
        }
        if (str == null)
            return alt;
        else
            return Long.parseLong(str);
    }

    public static boolean getParamBoolean(Map params, String key, boolean alt) {
        String str = null;
        String[] val;
        Object o = params.get(key);
        if (o instanceof String)
            str = (String) o;
        else if (o instanceof String[]) {
            val = (String[]) o;
            str = val[0];
            //some implementations: hrow new Exception(" val[0] = "+val[0]);
        }

        if (str != null && str.equals("yes"))
            return true;
        else
            return false;
    }

    public static void getJavascriptBegin(StringBuffer page) {
        page.append("\n<script type=\"text/javascript\">");
    }

    public static void getJavascriptFunction(String name, String arg,
                                             String body, StringBuffer page) {
        page.append("function ").append(name).append("(").append(arg).append(")").append("\r\n");
        page.append("{").append("\r\n");
        page.append(body).append("\r\n");
        page.append("}").append("\r\n");
    }

    public static void getBodyOnLoad(String fnName, StringBuffer page) {
        page.append("<BODY onLoad=\"").append(fnName).append("\">");
    }

    public static void getJavascriptEnd(StringBuffer page) {
        page.append("//-->\r\n");
        page.append("</script>");
    }

    public static void getJavascriptEnd2(StringBuffer page) {
        page.append("\n</script>");
        page.append("\n</head>");
    }

    /*
      public static void getJavascriptExtraWindow(String height, String width,
                                                  StringBuffer page){
        page.append("function extraWindow(url)").append("\r\n");
        page.append("{").append("\r\n");
        page.append("var remote;").append("\r\n");
        page.append("remote = window.open(url, 'remote', 'HEIGHT=");
        page.append(height).append(",WIDTH=").append(width);
        page.append(",resizable=yes,scrollbars=yes,toolbar=no');").append("\r\n");
        page.append("remote.opener = self;").append("\r\n");
        page.append("self.extra = remote;").append("\r\n");
        page.append("self.extra.focus();").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
      }
    */
    public static void getGlossaryFunctions(StringBuffer page) {
        page.append("function gWindow(url, ht, wd)").append("\r\n");
        page.append("{").append("\r\n");
        page.append("var remoteG;").append("\r\n");
        page.append("remoteG = window.open(url, 'Glossary', 'HEIGHT='+ht+',WIDTH='+wd+'left=800,screenX=800,top=400,");
        page.append("screenY=400,resizable=yes,scrollbars=yes,toolbar=no');").append("\r\n");
        page.append("remoteG.opener = self;").append("\r\n");
        page.append("self.extra = remoteG;").append("\r\n");
        page.append("self.extra.focus();").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
        page.append("function closeG(){").append("\r\n");
        page.append("  if(remote)").append("\r\n");
        page.append("    remote.close();").append("\r\n");
        page.append("  if(remoteG)").append("\r\n");
        page.append("    remoteG.close();").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
        page.append("window.onunload=closeG;").append("\r\n");
    }

    public static void getJSExtraWindowCall(String url, String linkTitle, StringBuffer page) {
        page.append("<A HREF=\"javascript: extraWindow('").append(url).append("')\">");
        page.append(linkTitle).append("</A>").append("\r\n");
    }

    public static void getJavascriptCheckDelete(String delUrl, StringBuffer page) {
        page.append("\nfunction checkDel(theForm) {").append("\r\n");
        page.append("  if(window.confirm(\"OK to delete record from database?\")){").append("\r\n");
        page.append("   theForm.action=\"").append(delUrl).append("\"").append("\r\n");
        page.append("   theForm.submit()").append("\r\n");
        page.append("   }").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
    }

    // This is used when we don't know the item that is being deleted until runtime.  The params that identify it will be passed as
    // args to the js function.
    public static void getJavascriptCheckDelete2(String delUrl, StringBuffer page) {
        page.append("function checkDel(param, value) {").append("\r\n");
        page.append("  if(window.confirm(\"OK to delete record from database?\")){").append("\r\n");
        page.append("      getUrl(\"").append(delUrl).append("&\" +").append("param + \"=\" + value\");\r\n");
        page.append("    return false;");
        page.append("   }").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
    }

    public static void getJavascriptChangeSubmit(String url, StringBuffer page) {
        page.append("function changeSub(theForm) {").append("\r\n");
        page.append("   theForm.action=\"").append(url).append("\"").append("\r\n");
        page.append("   theForm.submit()").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
    }

    public static String getJavascriptImageReload() {
        StringBuffer page = new StringBuffer();
        page.append("function reloadImg(imgURL, id) {\r\n");
        page.append("    document.getElementById(id).src=imgURL;\r\n");
        page.append("}");
        return page.toString();
    }

    public static void getJavascriptClearQ(String url, StringBuffer page) {
        getJavascriptBegin(page);
        page.append("function checkClear(theForm) {").append("\r\n");
        page.append("document.forms[0].selhint.value=\"\";").append("\r\n");
        page.append("   theForm.action=\"").append(url).append("\"").append("\r\n");
        page.append("   theForm.submit()").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
        getJavascriptEnd(page);
    }

    public static void getJavascriptSetHiddenVal(String form, String param, StringBuffer page) {
        page.append("function setHidden").append(param).append("(newval)").append("\r\n");
        page.append("{").append("\r\n");
        page.append("document.").append(form).append(".").append(param).append(".value=newval;").append("\r\n");
        page.append("return true;").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
    }

    //2 args:   1,   'Aug 03, 2005 09:34:19'
    public static void getBodyShowTimeOnLoad(String f, String dateStr, StringBuffer page) {
        page.append("<body onLoad=\"showTime(").append(f);
        page.append(", '").append(dateStr).append("')\">").append("\r\n");
    }

    public static void getJavascriptShowTime(StringBuffer page) {
        getJavascriptBegin(page);
        page.append("var offset = 0;").append("\r\n");
        page.append("function showTime(first, dbtime)").append("\r\n");
        page.append("{").append("\r\n");
        page.append(" if(first == 1)").append("\r\n");
        page.append(" {").append("\r\n");
        page.append("   now = new Date(dbtime);").append("\r\n");
        page.append("   owlTime = now.getTime();").append("\r\n");
        page.append("   now = null;").append("\r\n");
        page.append("now = new Date();").append("\r\n");
        page.append("clientTime = now.getTime();").append("\r\n");
        page.append("offset = owlTime - clientTime;").append("\r\n");
        page.append("}").append("\r\n");
        page.append("else").append("\r\n");
        page.append("{").append("\r\n");
        page.append("now = new Date();").append("\r\n");
        page.append("clientTime = now.getTime();").append("\r\n");
        page.append("}").append("\r\n");
        page.append("displayTime = clientTime + offset;").append("\r\n");
        page.append("now.setTime(displayTime);").append("\r\n");
        page.append("var curHour = now.getHours();").append("\r\n");
        page.append("var curMin = now.getMinutes();").append("\r\n");
        page.append("now = null;").append("\r\n");
        page.append("if(curHour >= 12) ").append("\r\n");
        page.append("{").append("\r\n");
        page.append("curHour = curHour - 12;").append("\r\n");
        page.append("Ampm = \"PM\";").append("\r\n");
        page.append("} ").append("\r\n");
        page.append("else").append("\r\n");
        page.append("{ ").append("\r\n");
        page.append(" Ampm = \"AM\";").append("\r\n");
        page.append("}").append("\r\n");
        page.append("if(curHour == 0)").append("\r\n");
        page.append("{").append("\r\n");
        page.append("  curHour = 12;").append("\r\n");
        page.append("}").append("\r\n");
        page.append("if(curMin < 10)").append("\r\n");
        page.append("{").append("\r\n");
        page.append("   curMin = \"0\" + curMin;").append("\r\n");
        page.append("}").append("\r\n");
        page.append("curHour = \"\" + curHour;").append("\r\n");
//    page.append(" time = curHour + \":\" + curMin + \" \" + Ampm;").append("\r\n");
        page.append(" time = curHour + \":\" + curMin + \" \";").append("\r\n");
        page.append("document.clocktext.clock.value = time;").append("\r\n");
        page.append("setTimeout (\"showTime(0,0)\", 1000 * 15);").append("\r\n");
        page.append("}").append("\r\n");
        getJavascriptEnd(page);
    }

    public static String getHighlightTag(String level, String coach) throws AuthoringException {
        StringBuffer tag = new StringBuffer();
        tag.append("{h");
        if (level.equals("1"))
            tag.append("1");
        else if (level.equals("2"))
            tag.append("2");
        else if (level.equals("3"))
            tag.append("3");
        else if (level.equals("4"))
            tag.append("4");
        else if (level.equals("5"))
            tag.append("5");
        else {
            AuthoringException aex = new AuthoringException();
            aex.setMessage("error producing highlight tag- invalid level: " + level);
            throw aex;
        }
        if (coach.equals("1"))
            tag.append("a");
        else if (coach.equals("2"))
            tag.append("b");
        else if (coach.equals("3"))
            tag.append("c");
        else if (coach.equals("4"))
            tag.append("d");
        else {
            AuthoringException aex = new AuthoringException();
            aex.setMessage("error producing highlight tag- invalid coach: " + coach);
            throw aex;
        }
        tag.append("}");
        return tag.toString();
    }

    // looks like:  10:34
    public static String getTimeStr() {
        Calendar calendar = new GregorianCalendar();
        java.util.Date trialTime = new java.util.Date();
        calendar.setTime(trialTime);
        int hr = calendar.get(Calendar.HOUR);
        if (hr == 0)
            hr = 12;
        String timeStr = hr + " : ";
        int min = calendar.get(Calendar.MINUTE);
        String minstr = "";
        if (min < 10)
            minstr = "0" + Integer.toString(min);
        else
            minstr = Integer.toString(min);
        timeStr += minstr;
        return timeStr;
    }

    //looks like:  Aug 03, 2005 09:34:19
    public static String getDateTimeStr() {
        Calendar calendar = new GregorianCalendar();
        java.util.Date trialTime = new java.util.Date();
        calendar.setTime(trialTime);
        StringBuffer timeStr = new StringBuffer();
        timeStr.append(months[calendar.get(Calendar.MONTH)]).append(" ");
        String dayStr = "";
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10)
            dayStr = "0" + day;
        else
            dayStr = (Integer.toString(day));
        timeStr.append(dayStr).append(", ");
        timeStr.append(calendar.get(Calendar.YEAR)).append(" ");
        timeStr.append(calendar.get(Calendar.HOUR)).append(":");
        timeStr.append(calendar.get(Calendar.MINUTE)).append(":");
        timeStr.append(calendar.get(Calendar.SECOND));
        return timeStr.toString();
    }

    public static boolean isEmpty(String s) {
        if (s == null || s.equals("") || s.length() == 0)
            return true;
        else
            return false;
    }

    public static String getPercentString(double score) {
        score = score * 100;
        return (int) score + "%";
    }

    public static void getTestScoreDisplay(double score, int numCorrect,
                                           int totalQ, String testTitle,
                                           StringBuffer page) {
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=75%>");
        page.append("<tr bgcolor=#dddddd><td colspan=3>").append(testTitle).append("</td></tr>");
        page.append("<tr bgcolor=#eeeeee>");
        page.append("<td>Correct Answers: ").append(numCorrect).append("</td>");
        page.append("<td>Total Questions: ").append(totalQ).append("</td>");
        page.append("<td>Percent Correct: ").append(GeneralHtml.getPercentString(score)).append("</td>");
        page.append("</tr></table>");
    }

    public static String extractParamVal(String pName, String paramStr) throws Exception {
        StringBuffer val = new StringBuffer();
        int index = paramStr.indexOf(pName);
        if (index < 0)
            return "";
        index += pName.length();
        String rest = paramStr.substring(index, paramStr.length());
        char[] arr = rest.toCharArray();
        if (arr[0] != '=')
            return "";
        for (int i = 1; i < arr.length; i++) {
            char c = arr[i];
            if (c == '&')
                return val.toString();
            else
                val.append(c);
        }
        return val.toString();
    }

    public static boolean isImage(String item) {
        return (item.contains(".gif") || item.contains(".jpg")
                || item.contains(".bmp"));
    }

    //produce a scoreboard for a module- the data is of the form:
    // DataTuple first, second
    public static void getModuleScoreDisplay(ArrayList data, StringBuffer page) {
        page.append("<td align=right>").append("\r\n");
        page.append("<table><tr><td><div CLASS=titlebar>&nbsp;My Scoreboard:&nbsp;</div></td><td>").append("\r\n");
        page.append("<TABLE BORDER=1 CELLPADDING=0 CELLSPACING=0>").append("\r\n");
        page.append("<TR BGCOLOR=#dddddd VALIGN=top>").append("\r\n");
        StringBuffer topRow = new StringBuffer();
        StringBuffer bottomRow = new StringBuffer();
        Iterator dataIter = data.iterator();
        while (dataIter.hasNext()) {
            DataTuple dt = (DataTuple) dataIter.next();
            topRow.append("<td align=center>").append((String) dt.getFirst()).append("</td>");
            bottomRow.append("<td align=center>").append((String) dt.getSecond()).append("</td>");
        }
        page.append(topRow.toString()).append("\r\n");
        page.append("</tr>").append("\r\n");
        page.append("<TR BGCOLOR=#dddddd VALIGN=top>").append("\r\n");
        page.append(bottomRow.toString()).append("\r\n");
        page.append("</tr>").append("\r\n");
        page.append("</table>").append("\r\n");
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");
    }

    // student scores table to appear on the practice module question page
    public static void getStudentPracticeModuleScoreTable(Iterator qIter,
                                                          String mID,
                                                          SystemInfo si,
                                                          StringBuffer page) {
        while (qIter.hasNext()) {
            Question q = (Question) qIter.next();
            page.append("<td width align=\"middle\">");
            StudentInfo stInfo = (StudentInfo) si.getUserInfo();
            String correctStr = stInfo.getLatestQuestionAnswerCorrectness(q.getID(), mID);
            if (correctStr == null)
                page.append("&nbsp;-&nbsp;");
            else if (q.isSurvey() && (q.getSubmittedAnswer() != null)) {
                page.append("<img SRC=\"").append(si.getContextPath()).append("/images/").append(GeneralHtml.greenCheck_).append("\" ALT=\"correct img\" BORDER=0>");
            } else {
                if (correctStr.equals("1"))


                    page.append("<img SRC=\"").append(si.getContextPath()).append("/images/").append(GeneralHtml.greenCheck_).append("\" ALT=\"correct img\" BORDER=0>");
                else
                    page.append(GeneralHtml.incorrChoice_);
            }
            page.append("</td>");
        }
    }

    public static void getStyleAndJSLinks(StringBuffer page, boolean closeScript, SystemInfo info,
                                          boolean wayangMode) {
        page.append("<link rel=\"STYLESHEET\" href=\"" + info.getResourcePath() + "/css/FourmalityCSS.css\" type=\"text/css\"/>\r\n");
        page.append("<SCRIPT language=\"JavaScript\" SRC=\"" + info.getResourcePath() + "/javascript/FourmalityJS.js\">\r\n</SCRIPT>\r\n");
        page.append("<SCRIPT language=\"JavaScript\" SRC=\"" + info.getResourcePath() + "/javascript/ajax.js\">\r\n</SCRIPT>\r\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + info.getResourcePath() + "/css/docs.css\" media=\"all\" />\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + info.getResourcePath() + "/css/jquery.mcdropdown.css\" media=\"all\" />\n");
        page.append("<SCRIPT language=\"JavaScript\" SRC=\"" + info.getResourcePath() + "/javascript/jquery-1.7.2.js\">\r\n</SCRIPT>\r\n");
        page.append("<SCRIPT language=\"JavaScript\" SRC=\"" + info.getResourcePath() + "/javascript/jquery.mcdropdown.js\">\r\n</SCRIPT>\r\n");
        page.append("<SCRIPT language=\"JavaScript\" SRC=\"" + info.getResourcePath() + "/javascript/jquery.bgiframe.js\">\r\n</SCRIPT>\r\n");
//        page.append("<script type=\"text/javascript\" src=\"ddsmoothmenu.js\">\n");
        page.append("<script type=\"text/javascript\" src=\"" + info.getResourcePath() + "/javascript/audio-player.js\"></script>\r\n" +
                "           <script type=\"text/javascript\">  \n" +
                "               AudioPlayer.setup(\"" + info.getResourcePath() + "/flash/player.swf\", {\n" +
                "                   width: 290,\n" +
                "                   initialvolume: 100,\n" +
                "                   transparentpagebg: \"yes\",\n" +
                "                   left: \"000000\",\n" +
                "                   lefticon: \"FFFFFF\"\n" +
                "               });  \n" +
                getJavascriptExternalActivity(info, wayangMode) + "\n" +
                getJavascriptGlossary(info) + "\n" +
                getJavascriptImageReload() + "\n" +
                getJqueryOnReady() + "\n");
        if (closeScript)
            page.append("</script>");
    }

    // This is including a jquery docready for widgets in the AuthQuestionPage
    private static String getJqueryOnReady() {
        String s = "\n\n" +
                "$(document).ready(function (){\n" +
                "\t$(\"#category\").mcDropdown(\"#categorymenu\");\n" +
                "\t$(\"#ccss1\").mcDropdown(\"#ccssmenu1\");\n" +
                "\t$(\"#ccss2\").mcDropdown(\"#ccssmenu2\");\n" +
                "\t$(\"#ccss3\").mcDropdown(\"#ccssmenu3\");\n" +
                "});\n" ;
        return s;
    }

    public static void getStyleAndJSLinksForWayang(StringBuffer page, boolean includeJQuery, SystemInfo info,
                                          boolean wayangMode) {
        page.append("<link rel=\"STYLESHEET\" href=\"" + info.getResourcePath() + "/css/FourmalityCSS.css\" type=\"text/css\"/>\r\n");
        page.append("<SCRIPT language=\"JavaScript\" SRC=\"" + info.getResourcePath() + "/javascript/FourmalityJS.js\">\r\n</SCRIPT>\r\n");
        page.append("<SCRIPT language=\"JavaScript\" SRC=\"" + info.getResourcePath() + "/javascript/ajax.js\">\r\n</SCRIPT>\r\n");
        page.append(" <script type=\"text/javascript\" src=\"" + info.getResourcePath() + "/javascript/audio-player.js\"></script>  \n" +
                "           <script type=\"text/javascript\">  \n" +
                "               AudioPlayer.setup(\"" + info.getResourcePath() + "/flash/player.swf\", {\n" +
                "                   width: 290,\n" +
                "                   initialvolume: 100,\n" +
                "                   transparentpagebg: \"yes\",\n" +
                "                   left: \"000000\",\n" +
                "                   lefticon: \"FFFFFF\"\n" +
                "               });  \n" +
                getJavascriptExternalActivity(info, wayangMode) + "\n" +
                getJavascriptGlossary(info) + "\n" +
                getJavascriptImageReload() + "\n" +
                "</script>\n<script type=\"text/javascript\" src=\"" + info.getResourcePath() + "/javascript/jquery-1.7.2.js\"></script>");
    }

    public static String makeParam(String marker, String name, String value) {
        return marker + name + "=" + value;
    }

    private static String getJavascriptExternalActivity(SystemInfo info, boolean wayangMode) {
        String userId = info.getUserInfo().getUserID();
        StringBuffer sb = new StringBuffer();
        sb.append("function getVal (elt) { \n");
        sb.append("   if (elt != null) \n");
        sb.append("      return elt.value;");
        sb.append("   else return \"\"");
        sb.append("}\n");
        sb.append("function showExternalActivity (url, h, w) {\n");
//        sb.append("    window.open(url,'external activity');\n");
        sb.append("    extraWindow(url,h,w);\n");

        StringBuffer servletCall = new StringBuffer();
        sb.append("    var qtype= getVal(document.getElementById('" + GeneralHtml.questionType_ + "')); \n");
        sb.append("    var mId= getVal(document.getElementById('" + GeneralHtml.moduleID_ + "')); \n");
        sb.append("    var modType= getVal(document.getElementById('" + GeneralHtml.moduleType_ + "')); \n");
        sb.append("    var qindex= getVal(document.getElementById('" + GeneralHtml.questionIndex_ + "')); \n");
        sb.append("    var selLev= getVal(document.getElementById('" + GeneralHtml.selLevel_ + "')); \n");
        sb.append("    var hId= getVal(document.getElementById('" + GeneralHtml.selHintID_ + "')); \n");
        sb.append("    var qId= getVal(document.getElementById('" + GeneralHtml.questionID_ + "')); \n");
        if (wayangMode) {
            sb.append("    var wayangStudId= getVal(document.getElementById('" + WayangSubsystem.STUD_ID + "')); \n");
            sb.append("    var wayangSessId= getVal(document.getElementById('" + WayangSubsystem.SESS_ID + "')); \n");
            sb.append("    var elapsedTime= getVal(document.getElementById('" + WayangSubsystem.ELAPSED_TIME + "')); \n");
            sb.append("    var probElapsed= getVal(document.getElementById('" + WayangSubsystem.PROB_ELAPSED_TIME + "')); \n");
            sb.append("    var pageGenTime= getVal(document.getElementById('" + WayangSubsystem.TIME + "')); \n");
        }
        // a string together in the javascript
        servletCall.append(info.getFormalityServlet());
        if (!wayangMode)
            servletCall.append(makeParam("?", FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam));
        else servletCall.append(makeParam("?", FormalitySubsystem.fxnParam, FormalitySubsystem.wayangFxnParam));

        servletCall.append(makeParam("&", FormalitySubsystem.modeParam, StudentSubsystem.viewExternalActivity));
        servletCall.append(makeParam("&", FormalitySubsystem.userIDParam, userId));
        servletCall.append(makeParam("&", GeneralHtml.questionType_, "'+qtype+'"));
        servletCall.append(makeParam("&", GeneralHtml.moduleID_, "'+mId+'"));
        servletCall.append(makeParam("&", GeneralHtml.moduleType_, "'+modType+'"));
        servletCall.append(makeParam("&", GeneralHtml.questionIndex_, "'+qindex+'"));
        servletCall.append(makeParam("&", GeneralHtml.selLevel_, "'+selLev+'"));
        servletCall.append(makeParam("&", GeneralHtml.selHintID_, "'+hId+'"));
        servletCall.append(makeParam("&", GeneralHtml.questionID_, "'+qId+'"));
        servletCall.append(makeParam("&", GeneralHtml.externalActivityUrl_, "'+escape(url)+'"));
        if (wayangMode) {
            servletCall.append(makeParam("&", WayangSubsystem.STUD_ID, "'+wayangStudId+'"));
            servletCall.append(makeParam("&", WayangSubsystem.SESS_ID, "'+wayangSessId+'"));
            servletCall.append(makeParam("&", WayangSubsystem.ELAPSED_TIME, "'+elapsedTime+'"));
            servletCall.append(makeParam("&", WayangSubsystem.PROB_ELAPSED_TIME, "'+probElapsed+'"));
            servletCall.append(makeParam("&", WayangSubsystem.TIME, "'+pageGenTime+'"));
        }
        servletCall.append(makeParam("&", "dummy", "'+ new Date().getTime()"));

        // If no moduleId is present then the call is being made from within the authoring tool and we don't want to log the event
        if (!wayangMode) // if this is a wayangMode problem we want to call the server even though there is no modId
            sb.append("    if (mId != \"\")\n");
        sb.append("       getUrl('" + servletCall.toString() + ", true, null);\n");

        sb.append("}\n");
        return sb.toString();
    }

    private static String getJavascriptGlossary(SystemInfo info) {
        String userId = info.getUserInfo().getUserID();
        StringBuffer sb = new StringBuffer();
        sb.append("function showGlossary (keyword) {\n");
//        sb.append("    window.open(url,'external activity');\n");
        // build a URL like 'http://localhost:8080/formality/glossary.html#howManyMore'
        sb.append("var gURL = '" + info.getResourcePath() + "/glossary.html#' + " + "keyword;\n");
        sb.append("    gWindow(gURL,400,400);\n");
        StringBuffer servletCall = new StringBuffer();
        sb.append("    var qtype= getVal(document.getElementById('" + GeneralHtml.questionType_ + "')); \n");
        sb.append("    var mId= getVal(document.getElementById('" + GeneralHtml.moduleID_ + "')); \n");
        sb.append("    var modType= getVal(document.getElementById('" + GeneralHtml.moduleType_ + "')); \n");
        sb.append("    var qindex= getVal(document.getElementById('" + GeneralHtml.questionIndex_ + "')); \n");
        sb.append("    var selLev= getVal(document.getElementById('" + GeneralHtml.selLevel_ + "')); \n");
        sb.append("    var hId= getVal(document.getElementById('" + GeneralHtml.selHintID_ + "')); \n");
        sb.append("    var qId= getVal(document.getElementById('" + GeneralHtml.questionID_ + "')); \n");

        // a string together in the javascript
        servletCall.append(info.getFormalityServlet());
        servletCall.append(makeParam("?", FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam));
        servletCall.append(makeParam("&", FormalitySubsystem.modeParam, StudentSubsystem.viewGlossary));
        servletCall.append(makeParam("&", FormalitySubsystem.userIDParam, userId));
        servletCall.append(makeParam("&", GeneralHtml.questionType_, "'+qtype+'"));
        servletCall.append(makeParam("&", GeneralHtml.moduleID_, "'+mId+'"));
        servletCall.append(makeParam("&", GeneralHtml.moduleType_, "'+modType+'"));
        servletCall.append(makeParam("&", GeneralHtml.questionIndex_, "'+qindex+'"));
        servletCall.append(makeParam("&", GeneralHtml.selLevel_, "'+selLev+'"));
        servletCall.append(makeParam("&", GeneralHtml.selHintID_, "'+hId+'"));
        servletCall.append(makeParam("&", GeneralHtml.questionID_, "'+qId+'"));
        servletCall.append(makeParam("&", GeneralHtml.glossaryTerm_, "'+escape(keyword)+'"));
        // forces the browser to send the URL because the dummy param is different every time.
        servletCall.append(makeParam("&", "dummy", "'+ new Date().getTime()"));
        // If no moduleId is present then the call is being made from within the authoring tool and we don't want to log the event

        sb.append("    if (mId != \"\")\n");
        sb.append("        getUrl('" + servletCall.toString() + ", true, null);\n");

        sb.append("}\n");
        return sb.toString();
    }

    public static String getBodyWithWindowCloser() {
        return "<body onunload=\"javascript:closeWindows();\">\n";
    }
}