package formality.html;

import formality.content.*;
import formality.html.hints.BaseHintRenderer;
import formality.controller.AuthoringSubsystem;
import formality.controller.StudentSubsystem;
import formality.controller.FormalitySubsystem;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Jan 10, 2011
 * Time: 9:14:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ShortAnswerQuestionPage extends MultipleChoiceQuestionPage {

    public ShortAnswerQuestionPage() {
        super();
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
        page.append("<input name=\"" + GeneralHtml.submittedAns_ + "\" value=\"" + ans + "\" type=\"text\">");
        page.append("</td></tr>").append("\r\n");
    }




    public void getAuthorModuleSurveyQuestionPage(QModule m, ShortAnswerQuestion q, SystemInfo info,
                                                  String msg, StringBuffer page) throws Exception {
        hltParser_.setActiveTag(q.getActiveTag());
        String clrurl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveModuleMode_);
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.evalModuleMode_, GeneralHtml.moduleType_, m.getType()));
        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");
        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");


        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
        hltParser_.parseStem(q.getStem(), page, info, false);
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        insertAnswerControls(q,info,page);
        page.append("</table>").append("\r\n");
        //row for submit and clear buttons

        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "sa", page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
        page.append("\r\n");
        if (q.getSelLevel() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, "", page);
            page.append("\r\n");
        } else {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, q.getSelLevel(), page);
            page.append("\r\n");
        }
        if (q.getSelHintID() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, "", page);
            page.append("\r\n");
        } else {
            GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, q.getSelHintID(), page);
            page.append("\r\n");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        page.append("\r\n");

        page.append("</FORM>").append("\r\n");

        Vector links = new Vector();
        String modPageUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewModuleMode_,
                GeneralHtml.moduleID_, m.getID());
        modPageUrl = GeneralHtml.appendToUrl(modPageUrl, GeneralHtml.moduleType_, m.getType());
        links.add(GeneralHtml.getLinkStr(modPageUrl, "module page"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_), "select module"));
        String url = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveModuleMode_, GeneralHtml.moduleID_, m.getID());
        url = GeneralHtml.appendToUrl(url, GeneralHtml.moduleType_, m.getType());
        getPageFooter(m, url, modPageUrl, links, page);
        page.append("</body></html>").append("\r\n");
    }

    public void getModuleSASurveyQuestionPage(QModule m, ShortAnswerQuestion q, SystemInfo info,
                                              String msg, Object o, StringBuffer page) throws Exception {
      boolean testMode = m.isTest();
        hltParser_.setActiveTag(q.getActiveTag());
        String clrurl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.clearQuestionMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=\"#000066\"><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                testMode ? StudentSubsystem.evaluateSurveyTestQuestionMode_ : StudentSubsystem.evaluateSurveyQuestionMode_,
                GeneralHtml.moduleType_, m.getType(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");
        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");

        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
        page.append("<B>#").append(m.getCurrentIndex()).append("</B><br>").append("\r\n");
        hltParser_.parseStem(q.getStem(), page, info, false);
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        q.setModule(m); // DM don't know why this wasn't here before

        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        insertAnswerControls(q,info,page);
        //row for submit and clear buttons
         page.append("<table width=100%><tr><td align=\"left\">");
        page.append("<INPUT type=\"SUBMIT\" value=\"Submit\" name=\"checkAns\">");
        page.append("</td></tr></table>");
        page.append("<tr><td>").append("\r\n");
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");

        page.append("</tr></table>").append("\r\n");
        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "sa", page);
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
        if (q.getSelLevel() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, "", page);
        }
        else {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, q.getSelLevel(), page);
        }

//        GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, "", page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        page.append("\r\n");
        page.append("</table>").append("\r\n");
        page.append("</FORM>").append("\r\n");
        //  end of page is different depending on module type
        if (testMode) {
            getTestModuleBottomPage(m, info, page);
        }
        else {
            getPracticeModuleBottomPage(m, info, page);
        }

    }

    private void getPracticeModuleBottomPage(QModule m, SystemInfo info, StringBuffer page) throws Exception {
        Vector links = null; //new Vector();
        String modUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.viewModuleMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.exitQuestion_, "1");
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.moduleType_, m.getType());
        String nextQurl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_, GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.exitQuestion_, "1");
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.moduleType_, m.getType());
        getPageFooter(m, nextQurl, modUrl, links, info, page);
        page.append("</body></html>").append("\r\n");
    }

    private void getTestModuleBottomPage(QModule m, SystemInfo info, StringBuffer page) throws Exception {
        page.append(GeneralHtml.linkFont_).append("Test Mode:<br>");
        page.append("You are on question ").append(m.getCurrentIndex());
        page.append(" of ").append(m.getNumLinkedQuestions()).append(".<br>");
//        page.append("</td>").append("\r\n");
//        page.append("</tr></table>").append("\r\n");
        page.append("<p>").append("\r\n");
        Vector links = null; //new Vector();
        String modUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.viewTestModuleMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.exitQuestion_, "1");
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.moduleType_, m.getType());
        getPageFooter(m, null, modUrl, links, page);
        page.append("</body></html>").append("\r\n");
    }


    public void getTeacherTestModuleSASurveyQuestionPage(QModule m, String sID, ShortAnswerQuestion q, SystemInfo info, String msg, StringBuffer page) {
        // TODO this has to be built
    }
}
