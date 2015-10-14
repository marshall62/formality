package formality.html;

import formality.content.*;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.ModuleCoachData;

import java.util.*;

import formality.html.hints.BaseHintRenderer;
import formality.html.contenttable.CourseContentTable;
import formality.model.TeacherInfo;
import formality.Util.DataTuple;
import formality.controller.FormalitySubsystem;
import formality.controller.AuthoringSubsystem;
import formality.controller.StudentSubsystem;
import formality.controller.TeacherSubsystem;
import formality.parser.HighlightParser;
import formality.reports.ModulePerformanceData;
import formality.reports.QuestionPerformanceData;

import java.text.DecimalFormat;

/**
 * <p>Title: </p>
 */
public class TeacherHtml {
    public static final String alreadyWorkedOnColor = "#ff66ff";
    public static final String haveNotWorkedOnColor = "#99ff99";
    public static final String hasAnsweredCorrectlyColor = "#99ff99";
    protected static final String theDelimiters_ = ",?\":; \n\t\r";

    public TeacherHtml() {
    }

    public void getTeacherAllCoursesPage(String msg, SystemInfo info,
                                         StringBuffer page) throws Exception {

        GeneralHtml.getSmallPageHeader("My Courses Page", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner(info.getUserInfo().getUserFnameOrLogin() + "'s Courses Page", page);
        page.append("<table BORDER=0 CELLPADDING=3 CELLSPACING=0><tr><td>");
        if (msg != null && !msg.equals(""))
            page.append("<h3><div CLASS=pgtxt>").append(msg).append("</div></h3>").append("\r\n");
        page.append("<h3><div CLASS=pgtxt>").append("Your Courses:");
        page.append("</div></h3>").append("\r\n");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0>");
        ArrayList<Course> courses = info.getUserInfo().getCourses();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            page.append("<tr>");
            page.append("<td align=center bgcolor=#dddddd width=10%>");
            page.append("<a href=\"");
            String tUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                    TeacherSubsystem.teacherHomeMode_,
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

    public void getTeacherHomePage(SystemInfo info, String msg,
                                   boolean showContent, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Teacher Home", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Teacher Home", page);
        if (msg != null) {
            page.append("<font face=\" Comic Sans MS\", color = #9999ff>");
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
            page.append("</font>");
        }
        page.append("<h3><font face=\" Comic Sans MS\", color = #9999ff>");
        page.append(info.getUserInfo().getCourseName()).append(", ").append(info.getUserInfo().getInstitution());
        page.append("</font><h3>");
        page.append("<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=60%>\r\n");
        page.append("<tr><td>");

        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=100%>").append("\r\n");

        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=center width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.studentHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a></td>").append("\r\n");
        page.append("<td><b>View Class Content</b></td></tr>").append("\r\n");
//todo test this for all teachers?
        /*
      String district = info.getUserInfo().getDistrict();
       if(district != null && (district.equals("Springfield") || district.equals("Hudson"))){
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=center width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/pencil.gif\" ALT=\"new question\" BORDER=0></a></td>").append("\r\n");
        page.append("<td><b>Edit Class Content</b></td></tr>").append("\r\n");
       }
*/
        page.append("</table>").append("\r\n");
        page.append("</td></tr><tr><td>");
        page.append("<b>Reports:</b><br>");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=100%>").append("\r\n");
        //report selections
        //module report
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=center width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.viewClassActivityMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a></td>").append("\r\n");
        page.append("<td><b>View All Student Module Activity</b></td></tr>");
        //skill report
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=center width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.getSkillReportMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"skill report\" BORDER=0></a></td>").append("\r\n");
        page.append("<td><b>View All Student Skills Report</b></td></tr>");
        // coach report
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=center width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.getCoachReportMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"coach report\" BORDER=0></a></td>").append("\r\n");
        page.append("<td><b>View Class Coach Use Report</b></td></tr>");

        page.append("</table>").append("\r\n");
        page.append("</td></tr><tr><td>");

        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=100%>").append("\r\n");
        page.append("<tr bgcolor=#eeeeee>");
        if (showContent)
            page.append("<td colspan=\"3\">");
        else
            page.append("<td colspan=\"2\">");
        page.append("<b>View Individual Student Progress</b><br>");
        page.append("Click on ");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"sel image\" BORDER=0>");
        page.append(" to view that student's report.<br>").append("\r\n");
        if (showContent) {
            page.append("Click on ");
            page.append("<img SRC=\"").append(info.getContextPath()).append("/images/pencil.gif\" ALT=\"student content\" BORDER=0>");
            page.append(" to view student-specific content.<br>").append("\r\n");
        }
        page.append("</td></tr>").append("\r\n");
        TeacherInfo ti = (TeacherInfo) info.getUserInfo();
        Iterator cit = ti.getStudentNames();
        while (cit.hasNext()) {
            DataTuple dt = (DataTuple) cit.next();
            String sID = (String) dt.getFirst();
            page.append("<tr bgcolor=#eeeeee>");

            page.append("<td align=center width=10%>");
            page.append("<a href=\"");
            page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.viewStudentActivityMode_,
                    GeneralHtml.studentID_, sID, FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
            page.append("\"><img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"student report\" BORDER=0></a></td>");
            if (showContent) {
                page.append("<td align=center width=10%>");
                page.append("<a href=\"");
                page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.studentHomeMode_,
                        GeneralHtml.studentID_, sID, FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
                page.append("\"><img SRC=\"").append(info.getContextPath()).append("/images/pencil.gif\" ALT=\"student content\" BORDER=0></a></td>");
            }
            page.append("<td>").append((String) dt.getSecond());
            page.append("</td></tr>").append("\r\n");
        }
        page.append("</table>");

        page.append("</td></tr></table>");
        Vector links = new Vector();
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getStudentHomePage(List<QModule> ptMods, List<QModule> pMods,
                                   List<QModule> tMods, List<QModule> pstMods,
                                   String msg, SystemInfo info, StringBuffer page) throws Exception {
        if (ptMods.size() == 0)
            ptMods = null;
        if (pstMods.size() == 0)
            pstMods = null;
        if (tMods.size() == 0)
            tMods = null;
        GeneralHtml.getSmallPageHeader("View Course Content", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("View Course Content", page);
        if (msg != null && !msg.equals(""))
            page.append("<h3><div CLASS=pgtxt>").append(msg).append("</div></h3>").append("\r\n");
        page.append("<h3><div CLASS=pgtxt>").append("Your class: ");
        page.append(info.getUserInfo().getCourseName()).append(", ").append(info.getUserInfo().getInstitution());
        page.append("</div></h3>").append("\r\n");
        //explanation text
        page.append("<div CLASS=pgtxt>").append("This page provides you with a way to ");
        page.append("step through all of the content your students see. Your answers are not logged as ");
        page.append("they are for students. Therefore, you will not see your scores change as you ");
        page.append("answer the questions.");
        page.append("</div><br>");
        page.append("<a href=\"javascript: extraWindow('");
        page.append(info.getContextPath()).append("/TeacherCoaches.html')\">Click HERE for information about the Hint Coaches.</a>");
        page.append("<BR><br>").append("\r\n");

        if (ptMods != null) {
            //PreTest Modules
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=45%><tr>");
            page.append("<td colspan=3 bgcolor=#dddddd>");
            page.append("<b><div CLASS=pgtxt>PreTest Modules</b></td></tr>");
            for (QModule m : ptMods) {
                int totalQ = m.getQuestionCount();
                int numCorrect = 0;
                int numAnswered = 0;
                page.append("<tr>");
                page.append("<td align=center bgcolor=");
                if (m.getTouch())
                    page.append(alreadyWorkedOnColor);
                else
                    page.append(haveNotWorkedOnColor);
                page.append(" width=10%><a href=\"");
                page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                        StudentSubsystem.viewTestModuleMode_,
                        GeneralHtml.moduleID_, m.getID(),
                        FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath());
                page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                page.append("</td>");
                page.append("<td bgcolor=#eeeeee><div CLASS=pgtxt>");
                page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
                page.append("<font size=\"-1\" color=#9586f9 face=\"Comic Sans MS\"><b>");
                page.append("Answered: n.a.<br>Total questions: " + totalQ);
                page.append("</b></font></td>");
                page.append("</tr>");
            }
            page.append("</table><br><br>");
        }
        //end pretest mods
        page.append("<table width=100%><tr><td valign=top width=50%>");

        //Practice Modules
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
        page.append("<td colspan=3 bgcolor=#dddddd>");
        page.append("<b><div CLASS=pgtxt>Practice Modules</b></td></tr>");
        int pCt = 0;
        for (QModule m : pMods) {
            int totalQ = m.getQuestionCount();
            int numCorrect = 0;
            int numAnswered = 0;

            page.append("<tr><td align=center bgcolor=");
            if (m.getTouch())
                page.append(alreadyWorkedOnColor);
            else
                page.append(haveNotWorkedOnColor);
            page.append(" width=10%><a href=\"");
            page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                    StudentSubsystem.viewModuleMode_,
                    GeneralHtml.moduleID_, m.getID(),
                    FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
            page.append("\">");
            page.append("<img SRC=\"").append(info.getContextPath());
            page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
            page.append("</td>");
            page.append("<td bgcolor=#eeeeee><div CLASS=pgtxt>");
            page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
            page.append("<font size=\"-1\" color=#9586f9 face=\"Comic Sans MS\"><b>");
            page.append("Correct: n.a.<br>Total questions: " + totalQ);
            page.append("</b></font></td></tr>");
        }
        page.append("</table>");
        page.append("<br>");
        page.append("</td>");
        page.append("<td valign=top width=50%>");
        //Test Modules
        if (tMods != null) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
            page.append("<td colspan=3 bgcolor=#dddddd>");
            page.append("<b><div CLASS=pgtxt>Test Modules</div></b></td></tr>");
            int tCt = 0;
            for (QModule m : tMods) {
                int totalQ = m.getQuestionCount();
                int numCorrect = 0;
                int numAnswered = 0;
                page.append("<tr><td align=center bgcolor=");
                if (m.getTouch())
                    page.append(alreadyWorkedOnColor);
                else
                    page.append(haveNotWorkedOnColor);
                page.append(" width=10%><a href=\"");
                page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                        StudentSubsystem.viewTestModuleMode_,
                        GeneralHtml.moduleID_, m.getID(),
                        FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath());
                page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                page.append("</td>");
                page.append("<td bgcolor=#eeeeee><div CLASS=pgtxt>");
                page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
                page.append("<font size=\"-1\" color=#9586f9 face=\"Comic Sans MS\"><b>");
                page.append("Correct: n.a.<br>Total questions: " + totalQ);
                page.append("</b></font></td></tr>");

            }
            page.append("</table>");
        }
        page.append("</td></tr></table>");
        if (pstMods != null) {
            //PostTest Modules
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=45%><tr>");
            page.append("<td colspan=3 bgcolor=#dddddd>");
            page.append("<b><div CLASS=pgtxt>PostTest Modules</b></td></tr>");
            for (QModule m : pstMods) {
                int totalQ = m.getQuestionCount();
                int numCorrect = 0;
                int numAnswered = 0;

                page.append("<tr>");
                page.append("<td align=center bgcolor=");
                if (m.getTouch())
                    page.append(alreadyWorkedOnColor);
                else
                    page.append(haveNotWorkedOnColor);
                page.append(" width=10%><a href=\"");
                page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                        StudentSubsystem.viewTestModuleMode_,
                        GeneralHtml.moduleID_, m.getID(),
                        FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath());
                page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                page.append("</td>");
                page.append("<td bgcolor=#eeeeee><div CLASS=pgtxt>");
                page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
                page.append("<font size=\"-1\" color=#9586f9 face=\"Comic Sans MS\"><b>");
                page.append("Answered: n.a.<br>Total questions: " + totalQ);
                page.append("</b></font></td>");
                page.append("</tr>");
            }
            page.append("</table>");
        }
        page.append("<br>");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getStudentHomePage(CourseContentTable contentTable,
                                   String msg, SystemInfo info,
                                   StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("View Student-Specific Content", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("View Student-Specific Content", page);
        if (msg != null && !msg.equals(""))
            page.append("<h3><div CLASS=pgtxt>").append(msg).append("</div></h3>").append("\r\n");
        //explanation text
        page.append("<div CLASS=pgtxt>").append("This page provides you with a way to ");
        page.append("step through all of the student-specific content that 4mality has created ");
        page.append(" to test this student based on performance on the pretest and practice modules. ");
        page.append("</div><br>");
        contentTable.getContentTable(info, page);
        page.append("<br>");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getModulePage(String msg, QModule m, String sID,
                              SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Viewer", page);
        GeneralHtml.getJavascriptBegin(page);
        String testUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
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
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=\"POST\" NAME=\"ViewModForm\">").append("\r\n");
        page.append("<b>Questions In This Module:</b><br>").append(
                "\r\n");
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
                page.append("&nbsp;none&nbsp;");
                page.append(GeneralHtml.incorrChoice_).append("&nbsp;");
                page.append("</td></tr>").append("\r\n");
                i++;
            }
            page.append("</table>").append("\r\n");
            page.append("<br>");
            page.append("<TABLE WIDTH=60%><TR>");
            if (m.getType().equalsIgnoreCase("test") && !m.isReentrant())
                page.append("<TD ALIGN=\"right\"><INPUT TYPE=\"BUTTON\" NAME=\"test\" VALUE=\"Begin Module- Test Mode\" onClick=\"changeSub(ViewModForm)\"></TD>");
            else
                page.append("<TD ALIGN=\"left\"><INPUT TYPE=\"SUBMIT\" NAME=\"practice\" VALUE=\"Begin Module- Practice Mode\"></TD>");
            if (m.getType().equals("Test") || m.getType().equals("Student")) {
                page.append("<TD ALIGN=\"right\"><INPUT TYPE=\"BUTTON\" NAME=\"test\" VALUE=\"Begin Module- Test Mode\" onClick=\"changeSub(ViewModForm)\"></TD>");
            }
            page.append("</TR></TABLE>").append("\r\n");
        } else {
            page.append("No questions are linked to this module.");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.studentID_, sID, page);
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_,
                GeneralHtml.studentID_, sID,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getTestModulePage(String msg, QModule m, String sID,
                                  SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Viewer", page);
        GeneralHtml.getJavascriptBegin(page);
        String practiceUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptChangeSubmit(practiceUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<h3>").append(m.getName()).append("</h3>").append("\r\n");
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                StudentSubsystem.viewTestQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=\"POST\" NAME=\"ViewModForm\">").append("\r\n");
        page.append("<b>Questions In This Module:</b><br>").append(
                "\r\n");
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

                page.append("&nbsp;NA&nbsp;");
                /*
                     String corStr="0";//(String)dt.getThird();
                     if(corStr.equals("1"))
                       page.append(GeneralHtml.corrChoice_).append("&nbsp;");
                     else
                       page.append(GeneralHtml.incorrChoice_).append("&nbsp;");
                 */
                page.append("</td></tr>").append("\r\n");
                i++;
            }
            page.append("</table>").append("\r\n");
            page.append("<br>");
            page.append("<TABLE WIDTH=60%><TR>");
            page.append("<TD ALIGN=\"left\"><INPUT TYPE=\"SUBMIT\" NAME=\"test\" VALUE=\"Begin Module- Test Mode\"></TD>");
            if (!m.getType().equals("PreTest") && !m.getType().equals("PostTest"))
                page.append("<TD ALIGN=\"right\"><INPUT TYPE=\"BUTTON\" NAME=\"practice\" VALUE=\"Begin Module- Practice Mode\" onClick=\"changeSub(ViewModForm)\"></TD>");
            page.append("</TR></TABLE>").append("\r\n");

        } else {
            page.append("No questions are linked to this module.");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.first_, "1", page);
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.studentID_, sID, page);
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_,
                GeneralHtml.studentID_, sID,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }


    public void getMCSurveyQuestionPage(String msg, String sID, QModule m,
                                        MultiChoiceSurveyQuestion q,
                                        SystemInfo info,
                                        StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        MultipleChoiceSurveyQuestionPage mcqp = new MultipleChoiceSurveyQuestionPage();
        mcqp.getTeacherModuleSurveyQuestionPage(m, sID, q, info, msg, page);
    }

    public void getMCTestSurveyQuestionPage(String msg, String sID, QModule m, MultiChoiceSurveyQuestion q,
                                            SystemInfo info, boolean showClock, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");  //disabling claock for now 11.07.07

        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        MultipleChoiceSurveyQuestionPage mcqp = new MultipleChoiceSurveyQuestionPage();
        mcqp.getTeacherModuleSurveyQuestionPage(m, sID, q, info, msg, page);
    }

    public void getSAQuestionPage(String msg, String sID, QModule m, ShortAnswerQuestion sq, SystemInfo info,
                                  BaseHintRenderer hintRenderer, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();
        pg.getTeacherModuleQuestionPage(m, sID, sq, info, msg, hintRenderer, page);
    }

    public void getMCQuestionPage(String msg, String sID, QModule m,
                                  MultipleChoiceQuestion q,
                                  SystemInfo info,
                                  BaseHintRenderer hintRenderer,
                                  StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        MultipleChoiceQuestionPage mcqp = new MultipleChoiceQuestionPage();
        mcqp.getTeacherModuleQuestionPage(m, sID, q, info, msg, hintRenderer, page);
    }

    public void getMCTestQuestionPage(String msg, String sID, QModule m, MultipleChoiceQuestion q,
                                      SystemInfo info, boolean showClock, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");  //disabling claock for now 11.07.07
        //2 args:   1,   'Aug 03, 2005 09:34:19'
// String dateStr=GeneralHtml.getDateTimeStr();
        // GeneralHtml.getBodyShowTimeOnLoad("1", dateStr, page);
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        MultipleChoiceQuestionPage mcqp = new MultipleChoiceQuestionPage();
        mcqp.getTeacherTestModuleQuestionPage(m, sID, q, info, msg, showClock, page);
    }


    public void getSATestQuestionPage(String msg, String sID, QModule m, ShortAnswerQuestion q,
                                      SystemInfo info, boolean showClock, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");  //disabling claock for now 11.07.07
        //2 args:   1,   'Aug 03, 2005 09:34:19'
// String dateStr=GeneralHtml.getDateTimeStr();
        // GeneralHtml.getBodyShowTimeOnLoad("1", dateStr, page);
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();
        if (q.isSurvey())
            pg.getTeacherTestModuleQuestionPage(m, sID, q, info, msg, showClock, page);
//            pg.getTeacherTestModuleSASurveyQuestionPage(m,sID,q,info,msg,page);
        else pg.getTeacherTestModuleQuestionPage(m, sID, q, info, msg, showClock, page);
    }


    /**
     * Produces a report that shows how the student did on each question in a module.
     *
     * @param reportData
     * @param name
     * @param info
     * @param page
     * @param skillLevels
     * @throws Exception
     */
    public void getStudentAllModuleReportPage(List<ModulePerformanceData> reportData, String name,
                                              SystemInfo info, StringBuffer page, double[] skillLevels
    ) throws Exception {
        GeneralHtml.getSmallPageHeader(name + " Individual Activity Report", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Individual Activity Report", page);
        page.append("<h3>Student Activity Report for: ").append(name).append("</h3>").append("\r\n");
        page.append("<p>");
        page.append("The column labeled Num Attempts will show a number with a <table><tr><td bgcolor=\"80ff80\"> green</td></tr></table> background if the student answered the problem correctly;  otherwise the number indicates the number of incorrect attempts");
        page.append("<p><table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%>");
        // Report the performance of each module in the course
        for (ModulePerformanceData modData : reportData) {

            QModule m = modData.module;
            page.append("<tr>");
            page.append("<td bgcolor=#eeeeee colspan=\"7\"><table width=100%><tr><td width=70%>Module:   <b>").append(m.getName()).append("</b></td>");
            page.append("<td>Type:&nbsp;&nbsp;").append(m.getType()).append("</td></tr></table></td>");
            page.append("<tr bgcolor=#dddddd><td>Q#</td><td>Skill</td><td>Num Attempts</td><td>Hints Before 1st Answer</td>");
            page.append("<td>Hints Before 1st Correct Answer</td><td>Total Hints</td></tr>");

            //Maps questionId -> QuesstionPerformanceData
            TreeMap<String, QuestionPerformanceData> qData = modData.performanceData;
            //question data: Skills, Correct, hints before 1st ans
            // Hints bef corr, total hints
            Iterator qIDIter = m.getLinkedQuestionIDs();
            int qOrder = 1;
            while (qIDIter.hasNext()) {
                String qID = (String) qIDIter.next();
                QuestionPerformanceData performanceData = qData.get(qID);
                page.append("<tr bgcolor=#eeeeee>");
                String qcol = "<a href=\".\" title=\"" + qID + "\">" + qOrder + "</a>";
                page.append("<td align=\"center\" width=\"5\">").append(qcol).append("</td>");
                String skillsAndLevels = getSkillString(performanceData, skillLevels);
                page.append("<td>").append(skillsAndLevels).append("</td>");
                if (performanceData.isSawQuestion()) {
                    page.append("<td align=\"center\" width=\"5\" ");
                    if (performanceData.isCorrect()) {
                        // make bg color for this cell green to indicate problem was solved correctly
                        // after the given number of attempts
                        page.append("bgcolor=\"#80ff80\">" + performanceData.getNumAttemptsForCorrect() + "</td>");
                    } else {
                        // make bg color for this cell red (wrong) to indicate that the given number
                        // of attempts did not result in a correct answer
                        page.append(">" + performanceData.getNumAttemptsForCorrect() + "</td>");
                    }
                    page.append("<td>").append(performanceData.getHintsBeforeFirstAttempt()).append("</td>");
                    page.append("<td>").append(performanceData.getHintsBeforeCorrectAttempt()).append("</td>");
                    page.append("<td>").append(performanceData.getTotalHintsSeen()).append("</td>");
                } else {
                    page.append("<td colspan=\"4\">Not seen</td>");
                }
                page.append("</tr>");
                qOrder++;
            }
        }
        page.append("</table>");

        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletPath(),
                TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.userIDParam, info.getUserID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    private String getSkillString(QuestionPerformanceData performanceData, double[] skillLevels) {
        StringBuffer sb = new StringBuffer();
        List<String> qSkills = performanceData.getStandardSkills();
        boolean hasSkills = false;
        for (String sk : qSkills) {
            if (sk != null) {
                hasSkills = true;
                double lev = getSkillLev(skillLevels, sk);
                // want something like <a href="." title="0.34">4.N.2</a>
                sb.append("<a href=\".\" title=\"" + String.format("%3.2f", lev) + "\">" + sk + "</a>");
                sb.append(",");
            }
        }
        if (hasSkills)
            sb.deleteCharAt(sb.length() - 1);
        else sb.append("&nbsp;");
        return sb.toString();
    }

    private double getSkillLev(double[] skillLevels, String sk) {
        // find the skill level (using the two parallel arrays of skillnames and skilllevels
        for (int i = 0; i < DBAccessor.standardsNames_.length; i++)
            if (DBAccessor.standardsNames_[i].equalsIgnoreCase(sk))
                return skillLevels[i];
        return 0;
    }

    public void getStudentModuleReportPage(String msg, QModule m,
                                           SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Viewer", page);
        GeneralHtml.getJavascriptBegin(page);
        String testUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
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
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=\"POST\" NAME=\"ViewModForm\">").append("\r\n");
        page.append("<b>Questions In This Module:</b><br>").append(
                "\r\n");
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
                DataTuple dt = null;//info.getStudentInfo().getModAnswerDataTuple(q.getID());
                if (dt == null)
                    page.append("&nbsp;none&nbsp;");
                else {
                    page.append("&nbsp;").append(dt.getSecond()).append("&nbsp;");
                    String corStr = (String) dt.getThird();
                    if (corStr.equals("1"))
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
            if (m.getType().equals("Test") || m.getType().equals("Student")) {
                page.append("<TD ALIGN=\"right\"><INPUT TYPE=\"BUTTON\" NAME=\"test\" VALUE=\"Begin Module- Test Mode\" onClick=\"changeSub(ViewModForm)\"></TD>");
            }
            page.append("</TR></TABLE>").append("\r\n");
        } else {
            page.append("No questions are linked to this module.");
        }
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getSelectAllStudentModuleReportPage(String msg, List<DataTuple> data,
                                                    SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Select Module Report", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Module Report", page);
        GeneralHtml.getJavascriptBegin(page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<h3>").append("Select a report of class activity for a module.");
        page.append("</h3>").append("\r\n");
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<table BORDER=1 CELLPADDING=7 CELLSPACING=0>\r\n");
        if (data.size() < 1) {
            page.append("<tr><td>No module data.</td></tr>");
        } else {
            page.append("<tr bgcolor=#dddddd><td>Select</td><td>Module Name</td><td>Type</td></tr>\r\n");
            for (DataTuple dt : data) {
                page.append("<tr bgcolor=#eeeeee><td align=center width=10%><a href=\"");
                String mID = (String) dt.getFirst();
                page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                        TeacherSubsystem.getModuleReportMode_,
                        GeneralHtml.moduleID_, mID,
                        FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath());
                page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                page.append("</td>");
                page.append("<td>").append((String) dt.getSecond()).append("</td>");
                page.append("<td>").append((String) dt.getThird()).append("</td>");
                page.append("</tr>\r\n");
            }
        }
        page.append("</td></tr></table>\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getAllStudentModuleReportPage(String msg,
                                              QModule m,
                                              TreeMap<String, ArrayList<DataTuple>> data,
                                              SystemInfo info,
                                              StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Report", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Report", page);
        GeneralHtml.getJavascriptBegin(page);
        GeneralHtml.getJavascriptEnd(page);
        if (msg != null)
            page.append("<b>").append(msg).append("</b>").append("\r\n");
        String expl = "This report shows the number of attempts and correctness for each question in this module for each student. " +
                "Correctness is indicated by the color of the box. Note that only answer attempts are counted here. A student may have accessed a question " +
                "without submitting an answer. This access is not counted in this report. NA signifies the student has not attempted the question";
        page.append("<table>");
        page.append("<tr><td colspan=\"2\">").append(expl).append("</td></tr>");
        page.append("<tr><td width=\"5%\" bgcolor=").append(hasAnsweredCorrectlyColor).append(">");
        page.append("&nbsp;</td><td>&nbsp;This color is displayed if a correct answer was ever recorded for a question.</td></tr>");
        page.append("</table>\r\n");
        page.append("<p>\r\n");
        page.append("<table BORDER=1 CELLPADDING=7 CELLSPACING=0>\r\n");
        page.append("<tr><th valign=\"bottom\">Student</th>");
        String[] modQIDs = m.getLinkedQuestionIDVecAsArray();
        List<Question> questions = m.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            page.append("<th valign=\"bottom\">Q").append(i + 1);
            Question q = questions.get(i);
            String std1 = q.getStd1();
            String std2 = q.getStd2();
            String std3 = q.getStd3();
            if (std1 != null && !std1.equals(""))
                page.append("<br><i>" + std1 + "</i>");
            if (std2 != null && !std2.equals(""))
                page.append("<br><i>" + std2 + "</i>");
            if (std3 != null && !std3.equals(""))
                page.append("<br><i>" + std3 + "</i>");
            page.append("</td>");
        }
        page.append("</tr>\r\n");
        TeacherInfo ti = (TeacherInfo) info.getUserInfo();
        Iterator studentIter = ti.getStudentNames();
        while (studentIter.hasNext()) {
            DataTuple dt = (DataTuple) studentIter.next();
            String sID = (String) dt.getFirst();
            String name = (String) dt.getSecond();
            page.append("<tr><td>").append(name).append("</td>");
            ArrayList<DataTuple> studentData = data.get(sID);
            for (int i = 0; i < modQIDs.length; i++) {
                String curQID = modQIDs[i];
                boolean found = false;
                if (studentData != null) {
                    for (int j = 0; j < studentData.size(); j++) {
                        DataTuple qdt = studentData.get(j);
                        String qID = (String) qdt.getFirst();
                        if (qID.equals(curQID)) {
                            found = true;
                            Integer attCt = (Integer) qdt.getSecond();
                            Boolean corrVal = (Boolean) qdt.getThird();
                            if (corrVal.booleanValue())
                                page.append("<td bgcolor=").append(hasAnsweredCorrectlyColor).append(" align=\"middle\">");
                            else
                                page.append("<td align=\"middle\">");
                            page.append(attCt.toString()).append("</td>");
                        }
                    }
                }
                if (!found)
                    page.append("<td align=\"middle\">NA</td>");
                else
                    found = false;
            }
            page.append("</tr>\r\n");
        }
        page.append("</table>\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.viewClassActivityMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "select module report"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getAllStudentSkillsReportPage(String msg, Vector data, double[] meanVals,
                                              SystemInfo info, StringBuffer page) throws Exception {
        DecimalFormat twoDig = new DecimalFormat("0.00");
        GeneralHtml.getSmallPageHeader("Skills Report", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Skills Report", page);
        GeneralHtml.getJavascriptBegin(page);
        GeneralHtml.getJavascriptEnd(page);
        if (msg != null)
            page.append("<b>").append(msg).append("</b>").append("\r\n");
        String expl = "Click on the skill for more info about that Framework Standard.";
        page.append("<table>");
        page.append("<tr><td>").append(expl).append("</td></tr>");

//      page.append("<tr><td width=\"5%\" bgcolor=").append(haveNotWorkedOnColor).append(">");
//      page.append("&nbsp;</td><td>&nbsp;This color signifies a correct answer was recorded for a question.</td></tr>");
        page.append("</table>");
        page.append("<table BORDER=1 CELLPADDING=7 CELLSPACING=0>\r\n");
        //the data: name, s1,s2,...,sn
        if (data.size() < 1) {
            page.append("<tr><td>No skills data.</td></tr>");
        } else {
            page.append("<tr bgcolor=#eeeeee><td>").append("<b>Framework Skills:</b>").append("</td>");
            for (int i = 0; i < DBAccessor.standardsNames_.length; i++) {
                page.append("<td><b>").append(getSkillsStr(DBAccessor.standardsNames_[i])).append("</b></td>");
            }
            page.append("</tr>");
            page.append("<tr bgcolor=#eeeeee><td>").append("<b>Mean (Statewide) Values:</b>").append("</td>");
            for (int i = 0; i < meanVals.length; i++) {
                page.append("<td><b>").append(twoDig.format(meanVals[i])).append("</b></td>");
            }
            page.append("</tr>\r\n");

            for (int i = 0; i < data.size(); i++) {
                Vector sData = (Vector) data.get(i);
                //sData: name, s1,s2,...,sn
                page.append("<tr bgcolor=#eeeeee><td>");
                String sID = (String) sData.get(0);
                String sName = (String) sData.get(1);
                page.append(sName);
                page.append("</td>");
                for (int j = 2; j < sData.size(); j++) {
                    Double curD = (Double) sData.get(j);
                    double curDatum = curD.doubleValue();
                    page.append("<td>");
                    page.append(twoDig.format(curDatum)).append("</td>");
                }
                page.append("</tr>\r\n");
            }
        }
        page.append("</table>\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

public void getClassCoachUseReportPage(String msg,List<ModuleCoachData> coachUseData,
                                              SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Coach Use Report", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Coach Use Report", page);
        GeneralHtml.getJavascriptBegin(page);
        GeneralHtml.getJavascriptEnd(page);
        if (msg != null)
            page.append("<b>").append(msg).append("</b>").append("\r\n");

        page.append("<table BORDER=1 CELLPADDING=7 CELLSPACING=0>\r\n");
        page.append("<tr><td>mod Id</td><td>mod Name</td><td>Estella Total Views</td><td>Hound total view</td><td>Bear Total Views</td><td>Vicuna Total Views</td></tr>\n");
        for (ModuleCoachData d: coachUseData) {
            page.append("<tr><td>");
            page.append(d.modId + "</td><td>" + d.modName + "</td>" + "<td>" + d.estella + "</td><td>" + d.hound + "</td><td>" +
                    d.bear + "</td><td>" + d.vicuna + "</td></tr>\n");
        }
        page.append("</table>\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()), "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getEditContentPage(String msg, SystemInfo info, StringBuffer page) throws Exception {
        // int access=info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("Edit Class Content", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Edit Class Content", page);
        if (msg != null) {
            page.append("<font face=\" Comic Sans MS\", color = #9999ff>");
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
            page.append("</font>");
        }
        page.append("<h3>You may create a new module, add questions to it, <br>" +
                "and link it to your class, or choose an existing <br>" +
                "module to link to your class.</h3>").append("\r\n");
        page.append("<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=60%>\r\n");
        page.append("<tr><td>");
        page.append("<b>New Modules</b><br>");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=100%><tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=center width=10%><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newModuleMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a>");
        page.append("</td>");
        page.append("<td><b>Create a New Module</b></td></tr></table>").append("\r\n");
        page.append("<b>Existing Modules</b><br>");
        page.append("Click on \"Select\" to select from a list of existing modules.<br>").append("\r\n");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=100%><tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=center width=10%><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a>");
        page.append("</td>");
        page.append("<td><b>Link/View Existing Modules</b></td></tr></table>").append("\r\n");
        page.append("</td></tr></table>");

        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getUrl(info.getServletRootAndID(),
                FormalitySubsystem.teacherFxnParam,
                TeacherSubsystem.teacherHomeMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()),
                "class home"));
        if (info.getUserInfo().hasMultiCourses())
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "all courses"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public static String getPercentCorrect(String corr, String ans) {
        if (corr == null || corr.equals(""))
            return "0%";
        if (ans == null || ans.equals(""))
            return "0%";
        double c = Double.parseDouble(corr);
        double a = Double.parseDouble(ans);
        double d = c / a;
        d = d * 100;
        return (int) d + "%";
    }

    public void getQuestionDisplay(SystemInfo info, QModule m, StringBuffer page) {
        Iterator qit = m.getLinkedQuestions();
        if (qit.hasNext()) {
            page.append(
                    "<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").
                    append("\r\n");
            page.append(
                    "<td>#</td><td>Type</td><td>Stem</td><td># Correct</td><td># Answers</td><td>% Correct</td></tr>").
                    append("\r\n");
            int i = 1;
            while (qit.hasNext()) {
                Question q = (Question) qit.next();
                page.append("<tr bgcolor=#eeeeee>");
                page.append("<td>").append(i).append("</td>");
                page.append("<td>").append(info.getCategoryName(q.getCategoryID())).append("</td>");

                page.append("<td>").append(GeneralHtml.getAbbrevString(
                        HighlightParser.removeTags(q.getStem()), 25));
                page.append("</td>");
                TeacherInfo ti = (TeacherInfo) info.getUserInfo();
                DataTuple dt = (DataTuple) ti.getModQData().get(q.getID());
                if (dt == null) {
                    page.append("<td>0</td>");
                    page.append("<td>0</td>");
                    page.append("<td>0%</td>");
                } else {
                    String ans = (String) dt.getFirst();
                    String corr = (String) dt.getSecond();
                    page.append("<td>").append(corr).append("</td>");
                    page.append("<td>").append(ans).append("</td>");
                    String pc = getPercentCorrect(corr, ans);
                    page.append("<td>").append(pc).append("</td>");
                }
                page.append("</td></tr>").append("\r\n");
                i++;
            }
            page.append("</table>").append("\r\n");
        }
    }

    private String getSkillsStr(String skills) {
        StringBuffer str = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer(skills, theDelimiters_);
        while (tokens.hasMoreTokens()) {
            String skill = tokens.nextToken();
            str.append(getSkillStr(skill)).append(" ");
        }
        return str.toString();
    }

    private String getSkillStr(String skill) {
        boolean found = false;
        StringBuffer str = new StringBuffer();
        try {
            for (int j = 0; j < DBAccessor.standardsNames_.length; j++)
                if (skill.equals(DBAccessor.standardsNames_[j])) {
                    GeneralHtml.getJSExtraWindowCall(GeneralHtml.stdsUrls[j], skill, str);
                    found = true;
                }
        } catch (Exception ex) {
        }
        if (found)
            return str.toString();
        else
            return skill;
    }


}
