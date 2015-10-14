package formality.html.auth;

import formality.content.database.CCSS;
import formality.content.database.mysql.DbCCSS;
import formality.html.GeneralHtml;
import formality.content.*;
import formality.controller.AuthoringSubsystem;
import formality.model.UserInfo;
import formality.servlet.QuestionFileUploadServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 15, 2010
 * Time: 9:43:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthQuestionPage {

    protected static void getEditFormValidateJS(StringBuffer page) {
        page.append("function validate()").append("\r\n");
        page.append("{").append("\r\n");
        page.append(" if(document.forms[0].ansa.value==\"\" || ").append("\r\n");
        page.append("    document.forms[0].ansb.value==\"\" || ").append("\r\n");
        page.append("    document.forms[0].ansc.value==\"\" || ").append("\r\n");
        page.append("    document.forms[0].ansd.value==\"\"){").append("\r\n");
        page.append("     alert(\"You must enter answer choices before saving.\");").append("\r\n");
        page.append("     document.forms[0].ansa.focus();").append("\r\n");
        page.append("   return false;").append("\r\n");
        page.append("  }").append("\r\n");
        page.append(" else if(!document.forms[0].corans[0].checked && ").append("\r\n");
        page.append("    !document.forms[0].corans[1].checked && ").append("\r\n");
        page.append("    !document.forms[0].corans[2].checked && ").append("\r\n");
        page.append("    !document.forms[0].corans[3].checked){").append("\r\n");
        page.append("     alert(\"You must specify a correct answer before saving.\");").append("\r\n");
        page.append("return false;").append("\r\n");
        page.append("  }").append("\r\n");
        page.append(" return true;").append("\r\n");
        page.append("}").append("\r\n");
    }

    protected static String[] getAnswerNameAndFeedbackName(String choice) {
        String ansName = null, feedbackName = null;
        if (choice.equalsIgnoreCase("a")) {
            ansName = GeneralHtml.ansChoiceA_;
            feedbackName = GeneralHtml.ansChoiceAFeedback_;
        } else if (choice.equalsIgnoreCase("b")) {
            ansName = GeneralHtml.ansChoiceB_;
            feedbackName = GeneralHtml.ansChoiceBFeedback_;
        } else if (choice.equalsIgnoreCase("c")) {
            ansName = GeneralHtml.ansChoiceC_;
            feedbackName = GeneralHtml.ansChoiceCFeedback_;
        } else if (choice.equalsIgnoreCase("d")) {
            ansName = GeneralHtml.ansChoiceD_;
            feedbackName = GeneralHtml.ansChoiceDFeedback_;
        }
        return new String[]{ansName, feedbackName};
    }


    protected static void insertClassificationAttributes(Question q, SystemInfo info, boolean insertReadyStatus, StringBuffer page, Connection conn) throws Exception {
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Source&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        if (q != null && q.getSource() != null)
            page.append("<INPUT TYPE=\"text\" NAME=\"source\" VALUE=\"").append(q.getSource()).append("\" SIZE=30 >");
        else
            page.append("<INPUT TYPE=\"text\" NAME=\"source\" VALUE=\"\" SIZE=30 >");
        page.append("</td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Topic&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        if (q!= null && q.getSource() != null)
            page.append("<INPUT TYPE=\"text\" NAME=\"qtopic\" VALUE=\"").append(q.getTopic()).append("\" SIZE=30 >");
        else
            page.append("<INPUT TYPE=\"text\" NAME=\"qtopic\" VALUE=\"\" SIZE=30 >");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Difficulty&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        if (q != null && q.getDegree() != null)
            getDiffSelect(q.getDegree(), page);
        else
            getDiffSelect("medium", page);
        page.append("</td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        // insert Ready status code here maybe
        if (insertReadyStatus) {
            page.append("&nbsp;Ready Status&nbsp;").append("\r\n");
            page.append("</td><td>").append("\r\n");
            page.append("<INPUT TYPE=\"CHECKBOX\" NAME=\"").append(GeneralHtml.readyStatus_);
            if (q.getStatus())
                page.append("\" value=\"yes\" CHECKED>");
            else
                page.append("\" value=\"yes\">");
            page.append("</td></tr>").append("\r\n");

            page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        }
        // category
        page.append("&nbsp;*Category&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        if (q!= null && q.getCategoryID() != null)
            getCategorySelect(info, q.getCategoryID(), page);
        else
            getCategorySelect(info, page);
        page.append("</td></tr></table>").append("\r\n");
        page.append("<br>* Must choose at least ONE standard:");
        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Framework Standard&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        if (q != null && q.getStd1() != null)
            getFrameworkSelect(GeneralHtml.standard1_, q.getStd1(), info, page);
        else
            getFrameworkSelect(GeneralHtml.standard1_, info, page);
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;Framework Standard&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        if (q != null && q.getStd2() != null)
            getFrameworkSelect(GeneralHtml.standard2_, q.getStd2(), info, page);
        else
            getFrameworkSelect(GeneralHtml.standard2_, info, page);
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;Framework Standard&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        if (q!= null && q.getStd3() != null)
            getFrameworkSelect(GeneralHtml.standard3_, q.getStd3(), info, page);
        else
            getFrameworkSelect(GeneralHtml.standard3_, info, page);
        page.append("</td></tr>");

        page.append("</table>").append("\r\n");
        insertCCSSSelectors(info,page,q);
        insertStandardHierarchy(page, conn,1);
        insertStandardHierarchy(page, conn,2);
        insertStandardHierarchy(page, conn,3);

    }

    private static void insertStandardHierarchy(StringBuffer page, Connection conn, int n) throws SQLException {
        List<CCSS> stds = DbCCSS.getStandards(conn);
        page.append("<ul id=\"ccssmenu" + n + "\" class=\"mcdropdown_menu\">\n");
        int gr = 0;
        for (CCSS std : stds) {
            int g = std.getGrade();
            // on each new grade, close the li and stick in a new li for the grade
            if (g != gr) {
                // omits generating close tags on first grade
                if (gr != 0) {
                    page.append("\t\t\t</ul>\n");
                    page.append("\t\t</li>\n");
                }
                page.append("\t<li rel=\""  + std.getCode() + "\">\n");
                page.append("\t\t" + std.getGrade() + "\n");
                page.append("\t\t\t<ul>\n");
                gr = g;
            }
            page.append("\t\t\t\t<li rel=\"" + std.getCode() + "\">" + std.getCode() + "</li>\n");
        }
        page.append("\t\t\t</ul>\n\t\t</li>\n\t</ul>\n");
    }

    private static void insertCCSSSelectors(SystemInfo info, StringBuffer page, Question q) {
        page.append("<br><table>");
        page.append("<tr><td>CCSS 1</td>\n");
        if (q != null)
            page.append("<td><input type=\"text\" name=\"ccss1\" id=\"ccss1\" value=\"" +q.getCcss1()+ "\" ></input></td></tr>\n");
        else
            page.append("<td><input type=\"text\" name=\"ccss1\" id=\"ccss1\" value=\"\" ></input></td></tr>\n");
        page.append("<tr><td>CCSS 2</td>");
        if (q != null)
            page.append("<td><input type=\"text\" name=\"ccss2\" id=\"ccss2\" value=\"" +q.getCcss2()+ "\" ></input></td></tr>\n");
        else
            page.append("<td><input type=\"text\" name=\"ccss2\" id=\"ccss2\" value=\"\" ></input></td></tr>\n");
        page.append("<tr><td>CCSS 3</td>\n");
        if (q != null)
            page.append("<td><input type=\"text\" name=\"ccss3\" id=\"ccss3\" value=\"" + q.getCcss3() + "\" ></input></td></tr>\n");
        else
            page.append("<td><input type=\"text\" name=\"ccss3\" id=\"ccss3\" value=\"\" ></input></td></tr>\n");
        page.append("</table>\n");
    }

    public static void getDiffSelect(String sel, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.degree_).append("\" SIZE=1>").append("\r\n");
        for (int i = 0; i < GeneralHtml.degrees.length; i++) {
            page.append("<OPTION VALUE=\"").append(GeneralHtml.degrees[i]);
            if (sel.equals(GeneralHtml.degrees[i]))
                page.append("\" SELECTED>");
            else
                page.append("\">");
            page.append(GeneralHtml.degrees[i]).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }


    public static void getCategorySelect(SystemInfo info, String selID, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.categoryID_).append("\" SIZE=1>").append("\r\n");
        for (int i = 0; i < info.getCategorySize(); i++) {
            String catID = info.getCategoryID(i);
            page.append("<OPTION VALUE=\"").append(catID);
            if (catID.equals(selID))
                page.append("\" SELECTED>");
            else
                page.append("\">");
            page.append(info.getCategoryName(i)).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }

    //end pages- begin widgets
    public static void getCategorySelect(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.categoryID_).append("\" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"\">").append("\r\n");
        for (int i = 0; i < info.getCategorySize(); i++) {
            page.append("<OPTION VALUE=\"").append(info.getCategoryID(i));
            page.append("\">").append(info.getCategoryName(i)).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }


    public static void getFrameworkSelect(String name, SystemInfo info, StringBuffer page) {
        page.append("<SELECT NAME=").append(name).append(" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"\" SELECTED>").append("\r\n");
        Iterator sit = info.getStandards();
        while (sit.hasNext()) {
            String std = (String) sit.next();
            page.append("<OPTION VALUE=\"").append(std).append("\">").append(std).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }

    public static void getFrameworkSelect(String name, String selStd, SystemInfo info, StringBuffer page) {
        if (selStd == null || selStd.equals(""))
            getFrameworkSelect(name, info, page);
        else {
            page.append("<SELECT NAME=").append(name).append(" SIZE=1>").append("\r\n");
            Iterator sit = info.getStandards();
            while (sit.hasNext()) {
                String std = (String) sit.next();
                page.append("<OPTION VALUE=\"").append(std);
                if (std.equals(selStd))
                    page.append("\" SELECTED>");
                else
                    page.append("\">");
                page.append(std).append("\r\n");
            }
            page.append("</SELECT>").append("\r\n");
        }
    }

    protected static void insertStem(Question q, SystemInfo info, StringBuffer page) {
        page.append("<BR>").append("\r\n");
        page.append("<B>Stem:</B>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        page.append("<a href=\"javascript: extraWindow('");
        page.append(info.getContextPath()).append("/htmlHelp.html')\">Html and Hint Tag Help</a>");
        page.append("<BR>").append("\r\n");
        page.append("<TEXTAREA NAME=\"").append(GeneralHtml.stem_);
        page.append("\" COLS=100 ROWS=12 WRAP=virtual>");
        if (q!= null && q.getStem() != null)
            page.append(q.getStem());
        page.append("</TEXTAREA>").append("\r\n");
        page.append("<BR>").append("\r\n");
    }

    public static void getHintTable(SystemInfo info, Hints hints,
                             String qID, StringBuffer page) throws Exception {
        page.append("<b>Linked Hints- click on hint ID to unlink/edit.</b>").append("\r\n");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").append("\r\n");
        page.append("<td>Coach</td><td>Step 1</td><td>Step 2</td><td>Step 3</td><td>Step 4</td><td>Step 5</td></tr>").append("\r\n");
        Iterator coachIt = info.getCoachIDs();
        while (coachIt.hasNext()) {
            String cID = (String) coachIt.next();
            page.append("<tr bgcolor=#eeeeee><td>").append("\r\n");
            page.append(info.getCoachName(cID)).append("</td>");
            AuthorHtml.getHintsRow(hints, info.getServletRootAndID(), cID, qID, page);
            page.append("</tr>").append("\r\n");
        }
        page.append("</table>").append("\r\n");
    }

    public static void getUnlinkEditHintTable(Hint h, String root, SystemInfo info,
                                       String qID, StringBuffer page) throws Exception {
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").append("\r\n");
        page.append("<td>UnLink</td><td>Edit</td>");
        page.append("<td>HintID</td><td>Step</td><td>Coach</td><td>Query</td><td>Response</td><td>Question Links</td></tr>").append("\r\n");
        page.append("<tr bgcolor=#eeeeee><td align=center><a href=\"");
        Vector params = new Vector();
        params.add(GeneralHtml.hintID_);
        params.add(h.getID());
        params.add(GeneralHtml.questionID_);
        params.add(qID);
        page.append(GeneralHtml.getAuthorUrl(root, AuthoringSubsystem.unlinkQuestionHintMode_, params));
        page.append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/unlink.gif\" ALT=\"unlink hint to question \" BORDER=0>");
        page.append("</a></td><td align=center><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(root, AuthoringSubsystem.editHintMode_, params));
        page.append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/edit.gif\" ALT=\"edit hint \" BORDER=0>");
        page.append("</a></td>");
        page.append("<td>").append(h.getID()).append("</td>");
        page.append("<td>").append(h.getLevel()).append("</td>");
        page.append("<td>").append(h.getCoachName()).append("</td>");
        page.append("<td>").append(h.getQuery()).append("</td>");
        page.append("<td>").append(GeneralHtml.getAbbrevString(h.getResponse(), 55)).append("</td><td>");
        AuthorHtml.getQuestionIDs(h.getQuestionLinks(), page);
        page.append("</td></tr>").append("\r\n");
        page.append("</table>").append("\r\n");

    }

    public static void getCoachSelect(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.coachID_).append("\" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"\">").append("\r\n");
        Iterator cits = info.getCoachIDs();
        while (cits.hasNext()) {
            String chID = (String) cits.next();
            page.append("<OPTION VALUE=\"").append(chID);
            page.append("\">").append(info.getCoachName(chID)).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }


    public static void getLevelSelect(StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.level_).append("\" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"\">").append("\r\n");
        for (int i = 0; i < GeneralHtml.levels.length; i++) {
            page.append("<OPTION VALUE=\"").append(GeneralHtml.levels[i]);
            page.append("\">");
            page.append(GeneralHtml.levels[i]).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }

    public static void getStrategySelect(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.strategyID_).append("\" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"\">").append("\r\n");
        Vector sIDs = info.getStrategyIDs();
        Vector sNms = info.getStrategyNames();
        for (int i = 0; i < sIDs.size(); i++) {
            page.append("<OPTION VALUE=\"").append(sIDs.get(i));
            page.append("\">").append(sNms.get(i)).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }


    public static void insertFileUploads(StringBuffer page, Question q, UserInfo info) {
        String id = q.getID();
        page.append("<FORM ACTION=\"QuestionFileUploadServlet\"");
        page.append("\" METHOD=\"POST\" enctype=\"multipart/form-data\" NAME=\"QEForm\">").append("\r\n");
        page.append("Image: <input type=\"file\" name=\"" + QuestionFileUploadServlet.QIMAGE + "\" size=\"80\">");
        page.append("<BR/>");
        page.append("Question audio: <input type=\"file\" name=\"" + QuestionFileUploadServlet.QENGLISH + "\" size=\"80\">");
        page.append("<BR/>");
        page.append("Spanish audio: <input type=\"file\" name=\"" + QuestionFileUploadServlet.QSPANISH + "\" size=\"80\">");
        page.append("<BR/>");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Upload the files\">");
        page.append("<INPUT TYPE=\"hidden\" NAME=\"" + QuestionFileUploadServlet.QID + "\" VALUE=\"" + id + "\" >");
        page.append("<INPUT TYPE=\"hidden\" NAME=\"" + QuestionFileUploadServlet.ACTION + "\" VALUE=\"" + QuestionFileUploadServlet.QFILEUPLOAD + "\" >");
        page.append("<INPUT TYPE=\"hidden\" NAME=\"" + QuestionFileUploadServlet.USERID + "\" VALUE=\"" + info.getUserID() + "\" >");
        page.append("</form>");
    }

}
