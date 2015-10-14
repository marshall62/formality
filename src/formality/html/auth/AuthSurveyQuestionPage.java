package formality.html.auth;

import formality.content.MultiChoiceSurveyQuestion;
import formality.content.SystemInfo;
import formality.controller.FormalitySubsystem;
import formality.controller.AuthoringSubsystem;
import formality.html.GeneralHtml;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 15, 2010
 * Time: 9:38:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthSurveyQuestionPage extends AuthQuestionPage {

    public static void getNewMultiChoiceSurveyQuestionPage(MultiChoiceSurveyQuestion q, String msg, SystemInfo info, StringBuffer page) throws Exception {
               GeneralHtml.getSmallPageHeader("New Multiple Survey Choice Question", false, page, info, false);
               page.append("<body>").append("\r\n");
               GeneralHtml.getSmallBanner("New Multiple Choice Survey Question", page);
               GeneralHtml.getJavascriptBegin(page);
               getEditFormValidateJS(page);
               GeneralHtml.getJavascriptEnd(page);
               if (msg != null)
                   page.append("<h3>").append(msg).append("</h3>").append("\r\n");
               page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveNewSurveyQuestionSubmit(info.getServletRootAndID()));
               page.append("\" METHOD=\"POST\"  NAME=\"QEForm\">").append("\r\n");

               page.append("<BR>").append("\r\n");
               page.append("<B>Survey question Stem:</B>");
               page.append("<a href=\"javascript: extraWindow('");
               page.append(info.getContextPath()).append("/htmlHelp.html')\">Html and Hint Tag Help</a>");
               page.append("<BR>").append("\r\n");
               page.append("<TEXTAREA NAME=\"").append(GeneralHtml.stem_);
               page.append("\" COLS=100 ROWS=12 WRAP=virtual>");
               if (q != null && q.getStem() != null)
                   page.append(q.getStem());
               page.append("</TEXTAREA>").append("\r\n");
               page.append("<BR>").append("\r\n");
               String choice = q != null ? q.getAnswerChoiceA() : "";
               getSurveyAnswerChoice("A", choice, page);
               page.append("<BR>").append("\r\n");
               choice = q != null ? q.getAnswerChoiceB() : "";
               getSurveyAnswerChoice("B", choice, page);
               page.append("<BR>").append("\r\n");
               choice = q != null ? q.getAnswerChoiceC() : "";
               getSurveyAnswerChoice("C", choice, page);
               page.append("<BR>").append("\r\n");
               choice = q != null ? q.getAnswerChoiceD() : "";
               getSurveyAnswerChoice("D", choice, page);

               page.append("<br><b>Answer Layout:</b><br>");
               page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + ((q == null || q.isSequentialLayout()) ? "checked" : "") + " name=\"answerLayout\" value=\"sequential\"/>Sequential");
               page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + ((q == null || q.isSequentialLayout()) ? "checked" : "") + "  name=\"answerLayout\" value=\"twoUp\"/>2-up Table");

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


    public static void getEditMultiChoiceSurveyQuestionPage(MultiChoiceSurveyQuestion q, SystemInfo info,
                                                        String msg, StringBuffer page) throws Exception {
           GeneralHtml.getSmallPageHeader("Survey Question Editor", false, page, info, false);
           GeneralHtml.getJavascriptBegin(page);
           String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteQuestionMode_,
                   GeneralHtml.questionID_, q.getID());
           GeneralHtml.getJavascriptCheckDelete(delUrl, page);
           // GeneralHtml.getJavascriptExtraWindow("600", "500", page);
           getEditFormValidateJS(page);
           GeneralHtml.getJavascriptEnd(page);
           page.append("<body>").append("\r\n");
           GeneralHtml.getSmallBanner("Survey Question Editor", page);
           if (msg != null && !msg.equals(""))
               page.append("<h3>").append(msg).append("</h3>").append("\r\n");
           page.append("<br>&nbsp<a href=\"");
           Vector viewParams = new Vector();
           viewParams.add(GeneralHtml.questionID_);
           viewParams.add(q.getID());
           viewParams.add(GeneralHtml.questionType_);
           viewParams.add(q.getType());
           page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewSurveyQuestionMode_,
                   viewParams));
           page.append("\">View Survey Question</a>");

           page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveSurveyQuestionSubmit(
                   info.getServletRootAndID(), q.getID()));
           page.append("\" METHOD=\"POST\" NAME=\"QEEditForm\">").append("\r\n");

           page.append("<BR>").append("\r\n");
           page.append("<B>Survey Stem:</B>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           page.append("<a href=\"javascript: extraWindow('");
           page.append(info.getContextPath()).append("/htmlHelp.html')\">Html and Hint Tag Help</a>");
           page.append("<BR>").append("\r\n");
           page.append("<TEXTAREA NAME=\"").append(GeneralHtml.stem_);
           page.append("\" COLS=100 ROWS=12 WRAP=virtual>");
           page.append(q.getStem()).append("</TEXTAREA>").append("\r\n");
           page.append("<BR>").append("\r\n");

           page.append("<TABLE WIDTH=60%><TR><TD ALIGN=left>").append("\r\n");
           page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Save\" onClick=\"return validate()\">");
           page.append("</td><TD ALIGN=right>");
           page.append("<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(QEEditForm)\">");
           page.append("</td></tr></table>");


           page.append("<BR>").append("\r\n");
           getSurveyAnswerChoice("A", q.getAnswerChoiceA(), page);
           page.append("<BR>").append("\r\n");
           getSurveyAnswerChoice("B", q.getAnswerChoiceB(), page);
           page.append("<BR>").append("\r\n");
           getSurveyAnswerChoice("C", q.getAnswerChoiceC(), page);
           page.append("<BR>").append("\r\n");
           getSurveyAnswerChoice("D", q.getAnswerChoiceD(), page);
           page.append("<BR>").append("\r\n");

           page.append("<br><b>Answer Layout:</b><br>");
           page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + (q.isSequentialLayout() ? "checked" : "") + " name=\"answerLayout\" value=\"sequential\"/>Sequential");
           page.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\"" + (!q.isSequentialLayout() ? "checked" : "") + "  name=\"answerLayout\" value=\"twoUp\"/>2-up Table");

           page.append("<BR><BR>").append("\r\n");
           GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);

           page.append("<TABLE WIDTH=60%><TR><TD ALIGN=left>").append("\r\n");
           // TODO make sure the validate JS is appropriate for survey q
           page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Save\" onClick=\"return validate()\">");
           page.append("</td><TD ALIGN=right>");
           page.append("<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(QEEditForm)\">");
           page.append("</td></tr></table>");
           page.append("<br>Don't forget to save any changes before viewing.<br>");
           page.append("<a href=\"");
           page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewSurveyQuestionMode_,
                   viewParams));
           page.append("\">View Survey Question</a>");
           page.append("</TD></TR></TABLE>").append("\r\n");
           // GeneralHtml.getHiddenParam( ,info.getUserID(), page);
           page.append("</form>").append("\r\n");
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


    public static void getSurveyAnswerChoice(String choice, String text, StringBuffer page) {
        if (text == null)
            text = "";
        page.append("<B>Answer Choice ").append(choice).append(":</B><BR>").append("\r\n");
        page.append("<TEXTAREA NAME=\"");
        String ansName = getAnswerNameAndFeedbackName(choice)[0];
        page.append(ansName);
        page.append("\" COLS=50 ROWS=3 WRAP=virtual>").append(text).append("</TEXTAREA>").append("\r\n");
//         page.append("</td></tr><tr><td>&nbsp;Feedback&nbsp;</td><td>").append("\r\n");
    }


}
