package formality.html;

import formality.html.contenttable.*;
import formality.html.hints.BaseHintRenderer;
import formality.content.*;
import formality.model.StudentInfo;
import formality.model.modeldata.MotivationalModelData;
import formality.Util.DataTuple;

import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;

import formality.controller.FormalitySubsystem;
import formality.controller.StudentSubsystem;
import formality.parser.HighlightParser;

/**
 * <p>Title: </p>
 */
public class StudentHtml {
    public static final String alreadyWorkedOnColor = "#ff66ff";
    public static final String haveNotWorkedOnColor = "#99ff99";

    public StudentHtml() {
    }

    public void getStudentAllCoursesPage(String msg, SystemInfo info,
                                         StringBuffer page) throws Exception {

        GeneralHtml.getSmallPageHeader("My Home Page", false, page, info, false);
        page.append("<body>").append("\r\n");

        GeneralHtml.getSmallBanner(info.getUserInfo().getUserFnameOrLogin() + "'s Courses Page", page);
        page.append("<table BORDER=0 CELLPADDING=3 CELLSPACING=0><tr><td>");
        if (msg != null && !msg.equals(""))
            page.append("<h3><div CLASS=pgtxt>").append(msg).append("</div></h3>").append("\r\n");
        page.append("<h3><div CLASS=pgtxt>").append("Your Classes:");
        page.append("</div></h3>").append("\r\n");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0>");
        ArrayList<Course> courses = info.getUserInfo().getCourses();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            page.append("<tr>");
            page.append("<td align=center bgcolor=#dddddd width=10%>");
            page.append("<a href=\"");
            String tUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.studentHomeMode_,
                    FormalitySubsystem.courseIDParam, c.getCourseID());
            page.append(tUrl).append("\">");
            page.append("<img SRC=\"").append(info.getContextPath());
            page.append("/images/sel.gif\" ALT=\"select course\" BORDER=0></a>");
            page.append("</td>\r\n");
            page.append("<td bgcolor=#eeeeee><div CLASS=pgtxt>").append(c.getCourseName()).append("</div></td>");
            page.append("</tr>");
        }
        page.append("</table>");
        page.append("</td></tr></table>");
        page.append("<br>\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getStudentCourseHomePage(CourseContentTable contentTable,
                                         String msg, SystemInfo info,
                                         StringBuffer page) throws Exception {

        GeneralHtml.getSmallPageHeader("My Class Home Page", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner(info.getUserInfo().getUserFnameOrLogin() + "'s Class Page", page);
        if (msg != null && !msg.equals(""))
            page.append("<h3><div CLASS=pgtxt>").append(msg).append("</div></h3>").append("\r\n");
        page.append("<h3><div CLASS=pgtxt>").append("Your class: ");
        page.append(info.getUserInfo().getCourseName()).append(", ").append(info.getUserInfo().getInstitution());
        page.append("</div></h3>").append("\r\n");
        page.append("<table><tr><td>");
        page.append("<div CLASS=pgtxt>");
        page.append(info.getUserInfo().getCourseNotes());
        page.append("</div> \r\n");
        page.append("</td></tr></table><br>");
        page.append("<a href=\"javascript: extraWindow('");
        page.append(info.getContextPath()).append("/Coaches.html')\">Click HERE to meet your coaches!</a>");
        page.append("<BR><br>").append("\r\n");
        page.append("<table><tr><td bgcolor=").append(haveNotWorkedOnColor);
        page.append(">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\r\n");
        page.append("<td><div CLASS=pgtxt>This color means you have not worked on this module.</div></td>\r\n");
        page.append("</tr><tr><td bgcolor=").append(alreadyWorkedOnColor);
        page.append(">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\r\n");
        page.append("<td><div CLASS=pgtxt>This color means you have answered all of the questions in this module.</div></td>\r\n");
        page.append("</tr></table>");
        page.append("<br>\r\n");
        contentTable.getContentTable(info, page);
        page.append("<br>");
        Vector links = new Vector();
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.studentHomeMode_, null, null), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getCustomStudentHomePage(CourseContentTable contentTable,
                                         String msg, SystemInfo info,
                                         StringBuffer page) throws Exception {

        GeneralHtml.getSmallPageHeader("My Home Page", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner(info.getUserInfo().getUserFnameOrLogin() + "'s Home Page", page);
        if (msg != null && !msg.equals(""))
            page.append("<h3><div CLASS=pgtxt>").append(msg).append("</div></h3>").append("\r\n");
        page.append("<h3><div CLASS=pgtxt>").append("Your class: ");
        page.append(info.getUserInfo().getCourseName()).append(", ").append(info.getUserInfo().getInstitution());
        page.append("</div></h3>").append("\r\n");
        page.append("<table><tr><td>");
        page.append("<div CLASS=pgtxt>");
        page.append(info.getUserInfo().getCourseNotes());
        page.append("</div> \r\n");
        page.append("</td></tr></table><br>\r\n");
        page.append("<a href=\"javascript: extraWindow('");
        page.append(info.getContextPath()).append("/Coaches.html')\">Click HERE to meet your coaches!</a>");
        page.append("<BR><br>").append("\r\n");
        /*  this page is used just for the no update demo
              page.append("<table><tr><td bgcolor=").append(haveNotWorkedOnColor);
              page.append(">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
              page.append("<td><div CLASS=pgtxt>This color means you have not worked on this module.</div></td>");
              page.append("</tr><tr><td bgcolor=").append(alreadyWorkedOnColor);
              page.append(">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
              page.append("<td><div CLASS=pgtxt>This color means you have already worked on this module.</div></td>");
              page.append("</tr></table>");
              page.append("<br>");
        */
        contentTable.getContentTable(info, page);
        page.append("<br>\r\n");
        Vector links = new Vector();
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.studentHomeMode_, null, null), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getModulePage(String msg, QModule m, SystemInfo info,
                              ArrayList<DataTuple> data, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Viewer", page);
        GeneralHtml.getJavascriptBegin(page);
        String testUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewTestQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptChangeSubmit(testUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<h3>").append(m.getName()).append("</h3>").append("\r\n");
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
//        page.append("Practice Modules: questions in practice modules ");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=\"POST\" NAME=\"ViewModForm\">").append("\r\n");
        page.append("<b>Questions In This Module:</b><br>").append(
                "\r\n");
        StudentInfo si = (StudentInfo) info.getUserInfo();
        Iterator qit = m.getLinkedQuestions();
        if (qit.hasNext()) {
            page.append(
                    "<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").
                    append("\r\n");
            page.append("<td>Number</td><td>Type</td><td>Question says:</td><td>Your Last Answer</td>");
            if (data != null)
                page.append("<td>Score</td>");
            page.append("</tr>").append("\r\n");
            int i = 1;
            while (qit.hasNext()) {
                Question q = (Question) qit.next();
                page.append("<tr bgcolor=#eeeeee align=center>");
                page.append("<td>").append(i).append("</td>");
                page.append("<td>").append(info.getCategoryName(q.getCategoryID())).append("</td>");

                page.append("<td>").append(GeneralHtml.getAbbrevString(
                        HighlightParser.removeTags(q.getStem()), 25));
                page.append("</td><td>");

                String correctStr = si.getLatestQuestionAnswerCorrectness(q.getID(), m.getID());
                if (correctStr == null)//no answer
                    page.append("&nbsp;none&nbsp;");
                else {
                    if (correctStr.equals("1"))
                        page.append(GeneralHtml.corrChoice_).append("&nbsp;");
                    else
                        page.append(GeneralHtml.incorrChoice_).append("&nbsp;");
                }
                page.append("</td>").append("\r\n");
                if (data != null) {
                    //the motivational score
                    DataTuple motDt = data.get(i - 1);
                    page.append("<td align=center>").append(motDt.getSecond()).append("</td>");
                }
                page.append("</tr>").append("\r\n");
                i++;
            }
            page.append("</table>").append("\r\n");
            page.append("<br>");
            page.append("<TABLE WIDTH=60%><TR>");
            page.append("<TD ALIGN=\"left\"><INPUT TYPE=\"SUBMIT\" NAME=\"practice\" VALUE=\"Begin Module- Practice Mode\"></TD>");

            page.append("</TR></TABLE>").append("\r\n");
        } else {
            page.append("No questions are linked to this module.");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, "1", page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, m.getLinkedQuestionID(1), page);
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.studentHomeMode_, null, null), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getModulePage(String msg, QModule m,
                              SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Viewer", page);
        GeneralHtml.getJavascriptBegin(page);
        String testUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewTestQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptChangeSubmit(testUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<h3>").append(m.getName()).append("</h3>").append("\r\n");
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
//        page.append("Practice Modules: questions in practice modules ");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=\"POST\" NAME=\"ViewModForm\">").append("\r\n");
        page.append("<b>Questions In This Module:</b><br>").append(
                "\r\n");
        StudentInfo si = (StudentInfo) info.getUserInfo();
        Iterator qit = m.getLinkedQuestions();
        if (qit.hasNext()) {
            page.append(
                    "<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").
                    append("\r\n");
            page.append(
                    "<td>Number</td><td>Type</td><td>Question says:</td><td>Your Last Answer</td></tr>").
                    append("\r\n");
            int i = 1;
            while (qit.hasNext()) {
                Question q = (Question) qit.next();
                page.append("<tr bgcolor=#eeeeee>");
                page.append("<td>").append(i).append("</td>");
                page.append("<td>").append(info.getCategoryName(q.getCategoryID())).append("</td>");

                page.append("<td>").append(GeneralHtml.getAbbrevString(
                        HighlightParser.removeTags(q.getStem()), 25));
                page.append("</td><td>");

                String correctStr = si.getLatestQuestionAnswerCorrectness(q.getID(), m.getID());
                if (correctStr == null)
                    page.append("&nbsp;none&nbsp;");
                else {
                    if (correctStr.equals("1"))
                        page.append(GeneralHtml.corrChoice_).append("&nbsp;");
                    else
                        page.append(GeneralHtml.incorrChoice_).append("&nbsp;");
                }
                page.append("</td></tr>").append("\r\n");
                i++;
            }
            page.append("</table>").append("\r\n");
            page.append("<br>");
            page.append("<TABLE WIDTH=60%><TR>");
            page.append("<TD ALIGN=\"left\"><INPUT TYPE=\"SUBMIT\" NAME=\"practice\" VALUE=\"Begin Module- Practice Mode\"></TD>");

            page.append("</TR></TABLE>").append("\r\n");
        } else {
            page.append("No questions are linked to this module.");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, "1", page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, m.getLinkedQuestionID(1), page);
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.studentHomeMode_, null, null), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getTestModulePage(String msg, QModule m,
                                  SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Viewer", page);
        GeneralHtml.getJavascriptBegin(page);
        String practiceUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptChangeSubmit(practiceUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<h3>").append(m.getName()).append("</h3>").append("\r\n");
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewTestQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=\"POST\" NAME=\"ViewModForm\">").append("\r\n");
        page.append("<b>Questions In This Module:</b><br>").append(
                "\r\n");
        StudentInfo si = (StudentInfo) info.getUserInfo();
        Iterator qit = m.getLinkedQuestions();
        if (qit.hasNext()) {
            page.append(
                    "<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").
                    append("\r\n");
            page.append(
                    "<td>Number</td><td>Type</td><td>Question says:</td><td>Your Last Answer</td></tr>");
            int i = 1;
            while (qit.hasNext()) {
                Question q = (Question) qit.next();
                page.append("<tr bgcolor=#eeeeee>");
                page.append("<td>").append(i).append("</td>");
                page.append("<td>").append(info.getCategoryName(q.getCategoryID())).append("</td>");
                page.append("<td>").append(GeneralHtml.getAbbrevString(
                        HighlightParser.removeTags(q.getStem()), 25));
                page.append("</td><td>");

                String correctStr = si.getLatestQuestionAnswerCorrectness(q.getID(), m.getID());
                if (correctStr == null)
                    page.append("&nbsp;none&nbsp;");
                else {
                    if (correctStr.equals("1"))
                        page.append(GeneralHtml.corrChoice_).append("&nbsp;");
                    else
                        page.append(GeneralHtml.incorrChoice_).append("&nbsp;");
                }
                page.append("</td></tr>").append("\r\n");
                i++;
            }
            page.append("</table>").append("\r\n");
            page.append("<br>");

            if (m.isCompleted()) {
                if ((m.getType().equals("PreTest"))) {
                    // if(si.isPreTestCompleted()){
                    //    getButtons(true, true, page);
                    // }
                    // else{
                    page.append("<center>Congratulations! You have finished this pre test module.<br></center>");
                    //getButtons(true, false, page);
                    // }
                } else if ((m.getType().equals("PostTest"))) {
                    //  if(si.isPostTestCompleted()){
                    //   getButtons(page);
                    // }
                    // else{
                    page.append("<center>Congratulations! You have finished this post test module.<br></center>");
                    //getButtons(true, false, page);
                    //}
                } else if ((m.getType().equalsIgnoreCase("Test")) && !m.isReentrant()) {
                    //  if(si.isPostTestCompleted()){
                    //   getButtons(page);
                    // }
                    // else{
                    page.append("<center>Congratulations! You have finished this module.<br></center>");
                    //getButtons(true, false, page);
                    //}
                } else { //completed- offer practice mode
                    getButtons(false, true, page);
                    GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, "1", page);
                }
            } else { //not completed- test mode only
                getButtons(true, false, page);
                GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
            }

        } else {
            page.append("No questions are linked to this module.");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.studentHomeMode_, null, null), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getMCSurveyQuestionPage(String msg, QModule m, MultiChoiceSurveyQuestion q,
                                        SystemInfo info, StringBuffer page) throws Exception {
        getMCSurveyQuestionPage(msg, m, q, info, null, page);
    }

    public void getSASurveyQuestionPage(String msg, QModule m, ShortAnswerQuestion q,
                                        SystemInfo info, StringBuffer page) throws Exception {
        getSASurveyQuestionPage(msg, m, q, info, null, page);
    }

    public void getSASurveyQuestionPage(String msg, QModule m, ShortAnswerQuestion q,
                                        SystemInfo info, MotivationalModelData motModData, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        // Any popup windows (glossary, external activities) will close when a new page loads
        page.append(GeneralHtml.getBodyWithWindowCloser());
        if (msg != null && msg.length() > 0)
            msg += "<br>";
        if (motModData == null) {
//         msg += "Thanks for your input.   Please move on to another question.";
            GeneralHtml.getSmallBanner(m.getName(), page);
        } else {
            String motModMsg = motModData.getCurMessage();
            if (motModMsg != null)
                msg += motModMsg;
            GeneralHtml.getSmallBannerWithScore(m.getName(), info, motModData, page);
        }
        ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();

        pg.getModuleSASurveyQuestionPage(m, q, info, msg, null, page);
    }

    public void getMCSurveyQuestionPage(String msg, QModule m, MultiChoiceSurveyQuestion q,
                                        SystemInfo info, MotivationalModelData motModData, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        // Any popup windows (glossary, external activities) will close when a new page loads
        page.append(GeneralHtml.getBodyWithWindowCloser());
        if (msg != null && msg.length() > 0)
            msg += "<br>";
        if (motModData == null) {
//         msg += "Thanks for your input.   Please move on to another question.";
            GeneralHtml.getSmallBanner(m.getName(), page);
        } else {
            String motModMsg = motModData.getCurMessage();
            if (motModMsg != null)
                msg += motModMsg;
            GeneralHtml.getSmallBannerWithScore(m.getName(), info, motModData, page);
        }
        MultipleChoiceSurveyQuestionPage mcqp = new MultipleChoiceSurveyQuestionPage();

        mcqp.getModuleMCSurveyQuestionPage(m, q, info, msg, page);
    }

    public void getShortAnswerQuestionPage(String msg, QModule m, ShortAnswerQuestion q,
                                           SystemInfo info, BaseHintRenderer hintRenderer,
                                           StringBuffer page) throws Exception {
        getShortAnswerQuestionPage(msg, m, q, info, (MotivationalModelData) null, hintRenderer, page);
    }

    public void getMCQuestionPage(String msg, QModule m, MultipleChoiceQuestion q,
                                  SystemInfo info, BaseHintRenderer hintRenderer,
                                  StringBuffer page) throws Exception {
        getMCQuestionPage(msg, m, q, info, null, hintRenderer, page);
    }


    public void getShortAnswerQuestionPage(String msg, QModule m, ShortAnswerQuestion q,
                                           SystemInfo info, MotivationalModelData motModData,
                                           BaseHintRenderer hintRenderer, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        // Any popup windows (glossary, external activities) will close when a new page loads
        page.append(GeneralHtml.getBodyWithWindowCloser());
        if (msg != null && msg.length() > 0)
            msg += "<br>";
        if (motModData == null)
            GeneralHtml.getSmallBanner(m.getName(), page);
        else {
            String motModMsg = motModData.getCurMessage();
            if (motModMsg != null)
                msg += motModMsg;
            GeneralHtml.getSmallBannerWithScore(m.getName(), info, motModData, page);
        }
        ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();

        pg.getModuleQuestionPage(m, q, info, msg, hintRenderer, page);
    }


    public void getMCQuestionPage(String msg, QModule m, MultipleChoiceQuestion q,
                                  SystemInfo info, MotivationalModelData motModData,
                                  BaseHintRenderer hintRenderer, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        // Any popup windows (glossary, external activities) will close when a new page loads
        page.append(GeneralHtml.getBodyWithWindowCloser());
        if (msg != null && msg.length() > 0)
            msg += "<br>";
        if (motModData == null)
            GeneralHtml.getSmallBanner(m.getName(), page);
        else {
            String motModMsg = motModData.getCurMessage();
            if (motModMsg != null)
                msg += motModMsg;
            GeneralHtml.getSmallBannerWithScore(m.getName(), info, motModData, page);
        }
        MultipleChoiceQuestionPage mcqp = new MultipleChoiceQuestionPage();

        mcqp.getModuleQuestionPage(m, q, info, msg, hintRenderer, page);
    }

    public void getSATestQuestionPage(String msg, QModule m, ShortAnswerQuestion q,
                                      SystemInfo info, boolean showClock, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        showClock = false; //turning off for now 11.7.07
        //2 args:   1,   'Aug 03, 2005 09:34:19'
        // String dateStr=GeneralHtml.getDateTimeStr();
        //GeneralHtml.getBodyShowTimeOnLoad("1", dateStr, page);
        // Any extra browser windows (glossary, external activities) will close on page reloads
        page.append(GeneralHtml.getBodyWithWindowCloser());
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();
        pg.getTestModuleQuestionPage(m, q, info, msg, showClock, page);
    }

    public void getMCTestQuestionPage(String msg, QModule m, MultipleChoiceQuestion q,
                                      SystemInfo info, boolean showClock, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        showClock = false; //turning off for now 11.7.07
        //2 args:   1,   'Aug 03, 2005 09:34:19'
        // String dateStr=GeneralHtml.getDateTimeStr();
        //GeneralHtml.getBodyShowTimeOnLoad("1", dateStr, page);
        // Any extra browser windows (glossary, external activities) will close on page reloads
        page.append(GeneralHtml.getBodyWithWindowCloser());
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        MultipleChoiceQuestionPage mcqp = new MultipleChoiceQuestionPage();
        mcqp.getTestModuleQuestionPage(m, q, info, msg, showClock, page);
    }


    public boolean checkDependencies(StudentInfo info, QModule m, Vector practiceMods) {
        boolean isClearForDisplay = true;
        Iterator parentIt = m.getParentModIDs();
        while (parentIt.hasNext()) {
            String curMID = (String) parentIt.next();
            for (int j = 0; j < practiceMods.size(); j++) {
                QModule parentM = (QModule) practiceMods.get(j);
                if (parentM.getID().equals(curMID))
                    if (!info.isCompletedModule(curMID, parentM.getQuestionCount()))
                        isClearForDisplay = false;
            }
        }
        return isClearForDisplay;
    }

    public void getButtons(boolean testMode, boolean practiceMode, StringBuffer page) {
        page.append("<TABLE WIDTH=60%><TR>");
        if (testMode)
            page.append("<TD ALIGN=\"left\"><INPUT TYPE=\"SUBMIT\" NAME=\"test\" VALUE=\"Go To Questions- Test Mode\"></TD>");
        if (practiceMode)
            page.append("<TD ALIGN=\"right\"><INPUT TYPE=\"BUTTON\" NAME=\"practice\" VALUE=\"Go To Questions- Practice Mode\" onClick=\"changeSub(ViewModForm)\"></TD>");
        page.append("</TR></TABLE>").append("\r\n");
    }

    public void getButtons(StringBuffer page) {
        page.append("<TABLE WIDTH=60%><TR>");
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, "1", page);
        page.append("<TD ALIGN=\"left\"><INPUT TYPE=\"SUBMIT\" NAME=\"test\" VALUE=\"Go To Questions- Test Mode\"></TD>");
        page.append("<TD ALIGN=\"right\"><INPUT TYPE=\"BUTTON\" NAME=\"practice\" VALUE=\"Go To Questions- Practice Mode\" onClick=\"changeSub(ViewModForm)\"></TD>");
        page.append("</TR></TABLE>").append("\r\n");
    }
}
