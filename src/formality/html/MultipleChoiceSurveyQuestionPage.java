package formality.html;

import formality.content.MultipleChoiceQuestion;
import formality.content.SystemInfo;
import formality.content.MultiChoiceSurveyQuestion;
import formality.content.QModule;
import formality.controller.AuthoringSubsystem;
import formality.controller.FormalitySubsystem;
import formality.controller.TeacherSubsystem;
import formality.controller.StudentSubsystem;
import formality.parser.HighlightParser;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 20, 2010
 * Time: 12:20:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultipleChoiceSurveyQuestionPage extends MultipleChoiceQuestionPage {

    protected HighlightParser hltParser_ = new HighlightParser();


 public  void getAnswerChoices(MultiChoiceSurveyQuestion q, SystemInfo info,
                                 StringBuffer page) throws Exception {
        String studAns = q.getSubmittedAnswer();  // if question has been answered this will be non null
        page.append("<table><tr>").append("\r\n");
        boolean radioOn = studAns != null && studAns.equalsIgnoreCase("a");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT " + (radioOn ? "checked ": "") + " type=\"radio\" value=\"A\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;A.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceA(), page, info);
        page.append("\r\n");
        page.append("</td>");
        if (q.isSequentialLayout())
            page.append("</tr><tr>").append("\r\n");
        else page.append("\r\n");
        radioOn = studAns != null && studAns.equalsIgnoreCase("b");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT "+(radioOn ? "checked ": "")+" type=\"radio\" value=\"B\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;B.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceB(), page, info);
        page.append("\r\n");
        page.append("</td></tr><tr>").append("\r\n");
        radioOn = studAns != null && studAns.equalsIgnoreCase("c");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT "+(radioOn ? "checked ": "")+"type=\"radio\" value=\"C\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;C.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceC(), page, info);
        page.append("\r\n");
        page.append("</td>");
        if (q.isSequentialLayout())
            page.append("</tr><tr>").append("\r\n");
        else page.append("\r\n");
        radioOn = studAns != null && studAns.equalsIgnoreCase("d");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT "+(radioOn ? "checked ": "")+"type=\"radio\" value=\"D\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;D.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceD(), page, info);
        page.append("\r\n");
        page.append("</td></tr></table>").append("\r\n");
    }

    public void getAuthorModuleSurveyQuestionPage(QModule m, MultiChoiceSurveyQuestion q,
                                                SystemInfo info, String msg,
                                                StringBuffer page) throws Exception {
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
            page.append("<tr><td>").append("\r\n");
            getAnswerChoices(q, info, page);
            page.append("</td></tr></table>").append("\r\n");
            //row for submit and clear buttons

            //hidden params
            GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
            page.append("\r\n");
            GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
            GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
            GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
            page.append("\r\n");
            if (q.getSelLevel() == null) {
                GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, "", page);
                page.append("\r\n");
            }
            else {
                GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, q.getSelLevel(), page);
                page.append("\r\n");
            }
            if (q.getSelHintID() == null) {
                GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, "", page);
                page.append("\r\n");
            }
            else {
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




    public void getAuthorSurveyQuestionPage(MultiChoiceSurveyQuestion q, SystemInfo info,
                                      String msg, String paramStr,
                                      StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        hltParser_.setActiveTag(q.getActiveTag());
        GeneralHtml.getSmallPageHeader("Survey Question Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        //getJavascriptLevelHintSelBlock("QForm", page);
        GeneralHtml.getSmallBanner("Question Viewer", page);
        String clrurl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewQuestionMode_);
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
         page.append("<FORM ACTION=\"");
              page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                      AuthoringSubsystem.evaluateSurveyQuestionMode_, null, null));
              page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");
        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");
        String mediaServlet = info.getMediaServlet();

        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
//     page.append("<B>#1</B><br>").append("\r\n");

        hltParser_.parseStem(q.getStem(), page, info, false);
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        page.append("<tr><td>").append("\r\n");
        getAnswerChoices(q, info, page);
        page.append("</td></tr>").append("\r\n");

        //third main table cell
        page.append("<td valign=top width=30%>").append("\r\n");
        //hints go here
//        String hintNavUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
//                AuthoringSubsystem.viewActiveQuestionMode_, null, null);
//        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.questionType_, q.getType());
//        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.questionID_, q.getID());
//        if (paramStr != null)
//            hintNavUrl += paramStr;


        //end hints
        //end table cell
        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");

        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
        page.append("\r\n");

        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        page.append("\r\n");
        String catID = null;
        if (paramStr != null) {
            //catID=1&  catname=Number%20Sense&  mID=38
//http://bean.cs.umass.edu:8080/4mality/servlet/FormalityServlet?un=9&fxn=aut&mode=selq&catID=1&catname=Number%20Sense&mID=38
            catID = GeneralHtml.extractParamVal(GeneralHtml.categoryID_, paramStr);
            String catName = GeneralHtml.extractParamVal(GeneralHtml.categoryName_, paramStr);
            String linkMID = GeneralHtml.extractParamVal(GeneralHtml.moduleID_, paramStr);
            GeneralHtml.getHiddenParam(GeneralHtml.categoryID_, catID, page);
            page.append("\r\n");
            GeneralHtml.getHiddenParam(GeneralHtml.categoryName_, catName, page);
            page.append("\r\n");
            GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, linkMID, page);
            page.append("\r\n");
        }
        page.append("</FORM>").append("\r\n");
        Vector links = new Vector();
        if (paramStr != null && catID != null && (catID.length() > 0)) {

            String selLink = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectQuestionMode_);
            selLink = GeneralHtml.addToLink(GeneralHtml.studentID_, info.getUserInfo().getUserID(), selLink);


            selLink = selLink + paramStr;

            links.add(GeneralHtml.getLinkStr(selLink, "select question"));
            if (access == FormalitySubsystem.teacherAccess) {
                links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
                links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
            }
        }
        if (access == FormalitySubsystem.authorAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editSurveyQuestionMode_,
                    GeneralHtml.questionID_, q.getID()), "edit question"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        }
        if (access == FormalitySubsystem.adminAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editSurveyQuestionMode_,
                    GeneralHtml.questionID_, q.getID()), "edit question"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        }
        GeneralHtml.getPageLinksFooter(links, page);
        page.append("</td></tr></table>").append("\r\n");
    }


    public  void getModuleMCSurveyQuestionPage(QModule m, MultiChoiceSurveyQuestion q, SystemInfo info, String msg, StringBuffer page) throws Exception {
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
        page.append("<tr><td>").append("\r\n");
        getAnswerChoices(q, info, page);
        page.append("</td></tr>").append("\r\n");
        //row for submit and clear buttons
         page.append("<table width=100%><tr><td align=\"left\">");
        page.append("<INPUT type=\"SUBMIT\" value=\"Submit\" name=\"checkAns\">");
        page.append("</td></tr></table>");
        page.append("<tr><td>").append("\r\n");
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");

        page.append("</tr></table>").append("\r\n");
        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
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

    public void getTestModuleQuestionPage(QModule m, MultipleChoiceQuestion q,
                                            SystemInfo info, String msg, boolean showClock,
                                            StringBuffer page) throws Exception {

    }

    // TODO problem:  This gets called when teacher is previewing a module.   For a MC survey question,
    // it neither puts in a submit button nor does it put in a footer with correct nav (want no links
    // on problems)
 public void getTeacherModuleSurveyQuestionPage(QModule m, String sID, MultiChoiceSurveyQuestion q,
                                               SystemInfo info, String msg,
                                               StringBuffer page) throws Exception {
        hltParser_.setActiveTag(q.getActiveTag());
        String clrurl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.clearQuestionMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                m.isTest() ? TeacherSubsystem.evaluateTestQuestionMode_ : TeacherSubsystem.evaluateSurveyQuestionMode_, GeneralHtml.moduleType_, m.getType(),
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
        page.append("</td></tr>").append("\r\n");
        page.append("<tr><td>").append("\r\n");

        getAnswerChoices(q, info, page);
        page.append("</td></tr></table>").append("\r\n");;
        if (m.isTest())
            getSubmit(page);
        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
        page.append("\r\n");
        if (q.getSelLevel() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, "", page);
            page.append("\r\n");
        }
        else {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, q.getSelLevel(), page);
            page.append("\r\n");
        }
       
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.studentID_, sID, page);
        page.append("\r\n");
        page.append("</FORM>").append("\r\n");
        Vector links = null; //new Vector();
        String modUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.viewModuleMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.exitQuestion_, "1");
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.moduleType_, m.getType());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.studentID_, sID);
        //  links.add(GeneralHtml.getLinkStr(modUrl, "module page"));
        //  String homeUrl=GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_);
        // homeUrl=GeneralHtml.appendToUrl(homeUrl, GeneralHtml.exitQuestion_, "1");
        // links.add(GeneralHtml.getLinkStr(homeUrl, "home"));

        String nextQurl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.viewQuestionMode_, GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.exitQuestion_, "1");
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.moduleType_, m.getType());
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.studentID_, sID);
        getPageFooter(m, m.isTest() ? null : nextQurl, modUrl, links, page);
        page.append("</body></html>").append("\r\n");
    }


//    public void getTeacherModuleSurveyQuestionPage(QModule module, String sID, MultiChoiceSurveyQuestion q, SystemInfo info, String msg, StringBuffer page) throws Exception {
//        getModuleMCSurveyQuestionPage(module,q,info,msg,page);
//    }
}
