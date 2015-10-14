package formality.html.auth;

import formality.content.SystemInfo;
import formality.content.MultipleChoiceQuestion;
import formality.content.Hint;
import formality.controller.FormalitySubsystem;
import formality.controller.AuthoringSubsystem;
import formality.html.GeneralHtml;

import java.sql.Connection;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 15, 2010
 * Time: 9:47:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthMCQuestionPage extends AuthQuestionPage {




    public static void getEditMultiChoiceQuestionPage(MultipleChoiceQuestion q, Hint h, SystemInfo info,
                                                      String msg, StringBuffer page, Connection conn) throws Exception {
        GeneralHtml.getSmallPageHeader("Question Editor", false, page, info, false);
        GeneralHtml.getJavascriptBegin(page);
        String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteQuestionMode_,
                GeneralHtml.questionID_, q.getID());
        GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        // GeneralHtml.getJavascriptExtraWindow("600", "500", page);
        getEditFormValidateJS(page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Question Editor", page);
        if (msg != null && !msg.equals(""))
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
        page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveQuestionSubmit(
                info.getServletRootAndID(), q.getID()));
        page.append("\" METHOD=\"POST\" NAME=\"QEEditForm\">").append("\r\n");
        page.append("* denotes a required field:");
        page.append("<TABLE BORDER=1 CELLSPACING=0>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;QuestionID&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(q.getID()).append("&nbsp;\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Type&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(q.getType()).append("&nbsp;\r\n");
        page.append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
        page.append("&nbsp;Author&nbsp;").append("\r\n");
        page.append("</td><td>").append("\r\n");
        page.append("&nbsp;").append(q.getAuthor()).append("&nbsp;\r\n");
        page.append("</td></tr>").append("\r\n");
        insertClassificationAttributes(q,info,true,page, conn);
        insertStem(q, info, page);

        page.append("<TABLE WIDTH=60%><TR><TD ALIGN=left>").append("\r\n");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Save\" onClick=\"return validate()\">");
        page.append("</td><TD ALIGN=right>");
        page.append("<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(QEEditForm)\">");
        page.append("</td></tr></table>");

        insertAnswerChoices(q, page);
        // code deleted from here in comment at bottom
        page.append("<BR><BR>").append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
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
        page.append("</td><TD ALIGN=right>");
        page.append("<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(QEEditForm)\">");
        page.append("</td></tr></table>");
        page.append("<br>Don't forget to save any changes before viewing.<br>");
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



    public static void getNewMultiChoiceQuestionPage(MultipleChoiceQuestion q, String msg, SystemInfo info, StringBuffer page, Connection conn) throws Exception {
        GeneralHtml.getSmallPageHeader("New Multiple Choice Question", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("New Multiple Choice Question", page);
        GeneralHtml.getJavascriptBegin(page);
        getEditFormValidateJS(page);
        GeneralHtml.getJavascriptEnd(page);
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
        insertClassificationAttributes(q, info, false,page, conn);
        insertStem(q, info, page);
        insertAnswerChoices(q, page);

        page.append("<br><BR>").append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.author_, info.getUserInfo().getLogin(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
// GeneralHtml.getHiddenParam(GeneralHtml.questionID_, qID, page);
        page.append("<TABLE WIDTH=100%><TR><TD ALIGN=left>").append("\r\n");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"SAVE\" VALUE=\"Save\" onClick=\"return validate()\">");
        page.append("</TD></TR></TABLE>").append("\r\n");
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        String homeStr = "author home";
        if (info.getUserInfo().getAccessLevel() == FormalitySubsystem.adminAccess)
            homeStr = "admin home";
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), homeStr));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    private static void insertAnswerChoices(MultipleChoiceQuestion q, StringBuffer page) {
        page.append("<BR>").append("\r\n");
        if (q!=null)
            getAnswerChoice("A", q.getAnswerChoiceA(), q.getAnsChoiceAFeedback(), q.isCorrectAnswer("A"), page);
        else getAnswerChoice("A", "", "", false, page);

        page.append("<BR>").append("\r\n");

        if (q!=null)
            getAnswerChoice("B", q.getAnswerChoiceB(), q.getAnsChoiceBFeedback(), q.isCorrectAnswer("B"), page);
        else getAnswerChoice("B", "", "", false, page);
        page.append("<BR>").append("\r\n");
        if (q!=null)
            getAnswerChoice("C", q.getAnswerChoiceC(), q.getAnsChoiceCFeedback(), q.isCorrectAnswer("C"), page);
        else getAnswerChoice("C", "", "", false, page);
        page.append("<BR>").append("\r\n");
        if (q!=null)
            getAnswerChoice("D", q.getAnswerChoiceD(), q.getAnsChoiceDFeedback(), q.isCorrectAnswer("D"), page);
        else getAnswerChoice("D", "", "", false, page);

        page.append("<br><b>Answer Layout:</b><br>");
        page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + ((q != null && q.isSequentialLayout()) ? "checked" : "") + " name=\"answerLayout\" value=\"sequential\"/>Sequential");
        page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + ((q != null && !q.isSequentialLayout()) ? "checked" : "") + "  name=\"answerLayout\" value=\"twoUp\"/>2-up Table");
    }



    public static void getAnswerChoice(String choice, String text, String feedback,
                                boolean isCorr, StringBuffer page) {
        if (text == null)
            text = "";
        if (feedback == null)
            feedback = "";
        String ansName = "", feedbackName = "";
        String[] sa = getAnswerNameAndFeedbackName(choice);
        ansName = sa[0];
        feedbackName = sa[1];
        page.append("<B>Answer Choice ").append(choice).append(":</B><BR>").append("\r\n");
        page.append("<TABLE CELLSPACING=0 CELLPADDING=0><TR><TD>&nbsp;").append("\r\n");
        page.append("<INPUT TYPE=\"RADIO\" NAME=\"").append(GeneralHtml.correctAnsChoice_);
        page.append("\" VALUE=\"").append(choice).append("\"");
        if (isCorr)
            page.append(" checked>");
        else
            page.append(">");
        page.append("</TD><TD><table  BGCOLOR=\"#eeeeee\"><tr><td>&nbsp;Text&nbsp;</td><td>").append("\r\n");
        page.append("<TEXTAREA NAME=\"");
        page.append(ansName);
        page.append("\" COLS=50 ROWS=3 WRAP=virtual>").append(text).append("</TEXTAREA>").append("\r\n");
        page.append("</td></tr><tr><td>&nbsp;Feedback&nbsp;</td><td>").append("\r\n");
        page.append("<TEXTAREA NAME=\"");
        page.append(feedbackName);
        page.append("\" COLS=50 ROWS=3 WRAP=virtual>").append(feedback).append("</TEXTAREA>").append("\r\n");
        page.append("</td></tr></table></TD></TR></table>").append("\r\n");
    }













    /*

        page.append("<BR>").append("\r\n");
        if (q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase("a"))
            getAnswerChoice("A", q.getAnswerChoiceA(), q.getAnsChoiceAFeedback(), true, page);
        else
            getAnswerChoice("A", q.getAnswerChoiceA(), q.getAnsChoiceAFeedback(), false, page);
        page.append("<BR>").append("\r\n");
        if (q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase("b"))
            getAnswerChoice("B", q.getAnswerChoiceB(), q.getAnsChoiceBFeedback(), true, page);
        else
            getAnswerChoice("B", q.getAnswerChoiceB(), q.getAnsChoiceBFeedback(), false, page);
        page.append("<BR>").append("\r\n");
        if (q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase("c"))
            getAnswerChoice("C", q.getAnswerChoiceC(), q.getAnsChoiceCFeedback(), true, page);
        else
            getAnswerChoice("C", q.getAnswerChoiceC(), q.getAnsChoiceCFeedback(), false, page);
        page.append("<BR>").append("\r\n");
        if (q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase("d"))
            getAnswerChoice("D", q.getAnswerChoiceD(), q.getAnsChoiceDFeedback(), true, page);
        else
            getAnswerChoice("D", q.getAnswerChoiceD(), q.getAnsChoiceDFeedback(), false, page);

        page.append("<br><b>Answer Layout:</b><br>");

        page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + (q.isSequentialLayout() ? "checked" : "") + " name=\"answerLayout\" value=\"sequential\"/>Sequential");
        page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + (!q.isSequentialLayout() ? "checked" : "") + "  name=\"answerLayout\" value=\"twoUp\"/>2-up Table");

     */


}
