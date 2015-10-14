package formality.html.auth;

import formality.content.SystemInfo;
import formality.content.ShortAnswerQuestion;
import formality.content.Hint;
import formality.content.Question;
import formality.html.GeneralHtml;
import formality.controller.AuthoringSubsystem;

import java.sql.Connection;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Dec 17, 2010
 * Time: 8:58:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class AuthSAQuestionPage extends AuthQuestionPage {

    public static void getNewShortAnswerQuestionPage(ShortAnswerQuestion q, String msg, SystemInfo info,
                                                     StringBuffer page, Connection conn) throws Exception {
        GeneralHtml.getSmallPageHeader("New Short Answer Question", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("New Short Answer Question", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
        page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveNewQuestionSubmit(info.getServletRootAndID()));
        page.append("\" METHOD=\"POST\"  NAME=\"QEForm\">").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Author&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(info.getUserInfo().getLogin()).append("\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Type&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append("mc").append("&nbsp;\r\n");
        page.append("</td></tr>").append("\r\n");
        insertClassificationAttributes(q, info, false, page, conn);
        insertStem(q, info, page);
        page.append("<br>Answer: (enter all variations)<br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");

        page.append("<br><BR>").append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.author_, info.getUserInfo().getLogin(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "sa", page);
        page.append("<TABLE WIDTH=100%><TR><TD ALIGN=left>").append("\r\n");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"SAVE\" VALUE=\"Save\">");
        page.append("</TD></TR></TABLE>").append("\r\n");
        page.append("</form>").append("\r\n");

        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        links.add(GeneralHtml.getLinkStr(info.getContextPath() + "/index.html", "welcome page"));
        GeneralHtml.getGeneralPageFooter(links, page);

    }

    public static void getNewShortAnswerSurveyQuestionPage(ShortAnswerQuestion q, String msg, SystemInfo info,
                                                           StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("New Short Answer Survey Question", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("New Short Answer Survey Question", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
//        page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
        page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveNewSurveyQuestionSubmit(info.getServletRootAndID()));
        page.append("\" METHOD=\"POST\"  NAME=\"QEForm\">").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Author&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(info.getUserInfo().getLogin()).append("\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Type&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append("sa").append("&nbsp;\r\n");
        page.append("</td></tr></table>").append("\r\n");
        insertStem(q, info, page);

        GeneralHtml.getHiddenParam(GeneralHtml.author_, info.getUserInfo().getLogin(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "sa", page);
        page.append("<TABLE WIDTH=100%><TR><TD ALIGN=left>").append("\r\n");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"SAVE\" VALUE=\"Save\">");
        page.append("</TD></TR></TABLE>").append("\r\n");
        page.append("</form>").append("\r\n");

        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        links.add(GeneralHtml.getLinkStr(info.getContextPath() + "/index.html", "welcome page"));
        GeneralHtml.getGeneralPageFooter(links, page);

    }

    public static void getEditShortAnswerQuestionPage(ShortAnswerQuestion q, Hint h, SystemInfo info, String msg, StringBuffer page, Connection conn) throws Exception {
        GeneralHtml.getSmallPageHeader2("Edit Short Answer Question", false, page, info);
        GeneralHtml.getJavascriptBegin(page);
        String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteQuestionMode_,
                GeneralHtml.questionID_, q.getID());
        GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        // GeneralHtml.getJavascriptExtraWindow("600", "500", page);
        getEditFormValidateJS(page);
        GeneralHtml.getJavascriptEnd2(page);

        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Edit Short Answer Question", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<br>&nbsp<a href=\"");

        Vector viewParams = new Vector();
        viewParams.add(GeneralHtml.questionID_);
        viewParams.add(q.getID());
        viewParams.add(GeneralHtml.questionType_);
        viewParams.add(q.getType());
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewQuestionMode_,
                viewParams));
        page.append("\">View Question</a>");
        page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveQuestionSubmit(info.getServletRootAndID(), q.getID()));
        page.append("\" METHOD=\"POST\"  NAME=\"saform\">").append("\r\n");
        page.append("* denotes a required field:");
        page.append("<TABLE BORDER=1 CELLSPACING=0>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;QuestionID&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(q.getID()).append("&nbsp;\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Author&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(info.getUserInfo().getLogin()).append("\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Type&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append("sa").append("&nbsp;\r\n");
        page.append("</td></tr>").append("\r\n");
        insertClassificationAttributes(q, info, true, page, conn);
        insertStem(q, info, page);
        page.append("<br>Answer: (enter all variations)<br>");
        for (String a : q.getAnswers()) {
            if (!a.equals(""))
                page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\" VALUE=\"" + a + "\"><br>");
        }
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");
        page.append("<INPUT TYPE=\"text\" NAME=\"" + GeneralHtml.correctAnsChoice_ + "\"><br>");

        page.append("<br><BR>").append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.author_, info.getUserInfo().getLogin(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "sa", page);
        //----------------
        getHintTable(info, q.getHints(), q.getID(), page);
        page.append("<BR>").append("\r\n");
        if (h != null) {
            getUnlinkEditHintTable(h, info.getServletRootAndID(), info, q.getID(), page);
            page.append("<BR>").append("\r\n");
        }
        page.append("<b>Link an Existing Hint To This Question:</b><br>");
        page.append("<a HREF=\"").append(GeneralHtml.getSelectHintUrl(info.getServletRootAndID(), q.getID()));
        page.append("\"> <b>Select from Existing Hints</b></a>").append("\r\n");
        page.append("<BR>").append("\r\n");
        page.append("<TABLE BGCOLOR=\"#eeeeee\" CELLSPACING=0 CELLPADDING=0><TR>").append("\r\n");
        page.append("<TD>&nbsp;<b>New Hint</b>&nbsp;</TD></tr><tr><td>").append("\r\n");
        page.append("<TABLE CELLSPACING=0><tr><td>&nbsp;Step&nbsp;</td><td>").append("\r\n");
        getLevelSelect(page);
        page.append("</td></tr><tr><td>&nbsp;Coach&nbsp;</td><td>").append("\r\n");
        getCoachSelect(info, page);
        page.append("</td></tr><tr><td>&nbsp;Answer Choice Link&nbsp;</td><td>").append("\r\n");
        AuthorHtml.getAnsChoiceLinkSelect(page);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td></tr><tr><TD><table><tr><td>&nbsp;Query&nbsp;</td><td>").append("\r\n");
        page.append("<TEXTAREA NAME=\"").append(GeneralHtml.query_);
        page.append("\" COLS=50 ROWS=3 WRAP=virtual></TEXTAREA></td>").append("\r\n");
        page.append("</tr><tr><td>&nbsp;Response&nbsp;</td><td>").append("\r\n");
        page.append("<TEXTAREA NAME=\"").append(GeneralHtml.response_);
        page.append("\" COLS=50 ROWS=10 WRAP=virtual></TEXTAREA></td>").append("\r\n");
        page.append("</tr></table></TD></TR><tr><td><TABLE BORDER=0 CELLSPACING=0><tr><td>").append("\r\n");
        page.append("&nbsp;Strategies&nbsp;").append("\r\n");
        getStrategySelect(info, page);
        page.append("</td></tr></table></TD></TR><TR><TD>&nbsp;</TD></TR></table>").append("\r\n");
        page.append("<BR>").append("\r\n");
        page.append("<TABLE WIDTH=60%><TR><TD ALIGN=left>").append("\r\n");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Save\" onClick=\"return validate()\">");
        page.append("</td>\n<TD ALIGN=right>");
        // javascript:return checkModDel('FormalityServlet?un=1474&fxn=aut&mode=delmod&mID=49');
        page.append("\n<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(document.saform)\">");
//        page.append("\n<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"javascript:return checkModDel('FormalityServlet?un=1474&fxn=aut&mode=delmod&mID=49'\">");
        page.append("</td></tr></table>");
        page.append("\n<br>Don't forget to save any changes before viewing.<br>");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewQuestionMode_,
                viewParams));
        page.append("\">View Question</a>");
        page.append("</TD></TR></TABLE>").append("\r\n");
        // GeneralHtml.getHiddenParam( ,info.getUserID(), page);
        page.append("</form>").append("\r\n");
        insertFileUploads(page, q, info.getUserInfo());
        Vector links = new Vector();
        Vector selParams = new Vector();
        selParams.add(GeneralHtml.categoryID_);
        selParams.add(q.getCategoryID());
        selParams.add(GeneralHtml.categoryName_);
        selParams.add(info.getCategoryName(q.getCategoryID()));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectQuestionMode_, selParams), "select question"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }


    public static void getEditShortAnswerSurveyQuestionPage(ShortAnswerQuestion q, SystemInfo info, String msg, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader2("Edit Short Answer Survey Question", false, page, info);
        GeneralHtml.getJavascriptBegin(page);
        String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteSurveyQuestionMode_,
                GeneralHtml.questionID_, q.getID());
        GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        // GeneralHtml.getJavascriptExtraWindow("600", "500", page);
        getEditFormValidateJS(page);
        GeneralHtml.getJavascriptEnd2(page);

        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Edit Short Answer Survey Question", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<br>&nbsp<a href=\"");

        Vector viewParams = new Vector();
        viewParams.add(GeneralHtml.questionID_);
        viewParams.add(q.getID());
        viewParams.add(GeneralHtml.questionType_);
        viewParams.add(q.getType());
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewSurveyQuestionMode_,
                viewParams));
        page.append("\">View Question</a>");
        page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveSurveyQuestionSubmit(info.getServletRootAndID(), q.getID()));
        page.append("\" METHOD=\"POST\"  NAME=\"saform\">").append("\r\n");
        page.append("* denotes a required field:");
        page.append("<TABLE BORDER=1 CELLSPACING=0>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;QuestionID&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(q.getID()).append("&nbsp;\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Author&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(info.getUserInfo().getLogin()).append("\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Type&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append("sa").append("&nbsp;\r\n");
        page.append("</td></tr></table>").append("\r\n");
        insertStem(q, info, page);
        page.append("<br><BR>").append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.author_, info.getUserInfo().getLogin(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "sa", page);

        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewSurveyQuestionMode_,
                viewParams));
        page.append("\">View Question</a>");
        page.append("</TD></TR></TABLE>").append("\r\n");

        page.append("<TABLE WIDTH=60%><TR><TD ALIGN=left>").append("\r\n");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Save\" onClick=\"return validate()\">");
        page.append("</td>\n<TD ALIGN=right>");
        page.append("\n<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(document.saform)\">");
        page.append("</td></tr></table>");

        page.append("</form>").append("\r\n");
        insertFileUploads(page, q, info.getUserInfo());
        Vector links = new Vector();
        Vector selParams = new Vector();
        selParams.add(GeneralHtml.categoryID_);
        selParams.add(q.getCategoryID());
        selParams.add(GeneralHtml.categoryName_);
        selParams.add(info.getCategoryName(q.getCategoryID()));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectSurveyQuestionMode_, selParams), "select question"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);

    }


    // An override to supply no feedback on short answers.   The MultipleChoiceQuestionPage.getAuthorModuleQuestionPage is reused for presentation
    // of ShortAnswerQuestions with only minor differences for presenting answer inputs.   These differences are handled using these overrides.
    public void addAnswerSpecificFeedback(Question q, StringBuffer page, String selAns) {
        page.append("&nbsp;\r\n");
    }


    //
    public void insertAnswerControls(Question q, SystemInfo info, StringBuffer page) throws Exception {
        page.append("<tr><td>").append("\r\n");
        String ans = q.getSubmittedAnswer();
        if (ans == null)
            ans = "";
        if (q.isEvaluated())
            page.append("<input name=\"" + GeneralHtml.submittedAns_ + "\" value=\"" + ans + "\" type=\"text\">");
        page.append("</td></tr>").append("\r\n");
    }
}
