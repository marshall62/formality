package formality.html.contenttable;

import formality.content.SystemInfo;
import formality.content.QModule;
import formality.model.StudentInfo;
import formality.controller.StudentSubsystem;
import formality.controller.coursecontroller.parser.StateParser;
import formality.html.GeneralHtml;
import formality.html.StudentHtml;

import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/**
 * Produces a table with all of the course content
 * rendered according to the controller policy
 * User: gordon
 * Date: Dec 28, 2005
 * Time: 10:10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoUpdateContentTable implements CourseContentTable {

    List<QModule> ptMods = null;
    List<QModule> pMods = null;
    List<QModule> tMods = null;
    List<QModule> pstMods = null;

    public void setState(StateParser state) {
    }

    public void setContent(List<List<QModule>> content) throws Exception {
        //assume the vectors are not null
        ptMods = content.get(0);
        pMods = content.get(1);
        tMods = content.get(2);
        pstMods = content.get(3);
    }

    public void getContentTable(SystemInfo info,
                                StringBuffer page) throws Exception {

        StudentInfo si = (StudentInfo) info.getUserInfo();
        //begin page/////////////////////////////////////////////////
        page.append("<table><tr><td>");
        //PreTest Modules
        showPrePostModules("Pre Test Modules", info, ptMods, si, page, false);

        //end pretest mods
        page.append("<table width=100%><tr><td valign=top width=50%>");

        //Practice Modules
        showPracticeMods("Practice Modules", info, pMods, si, page, false);
        page.append("<br></td>");
        page.append("<td valign=top width=50%>");
        //Test Modules
        showTestMod("Test Modules", info, tMods, si, page, false);
        page.append("</td></tr></table>");
        //PostTest Modules
        showPrePostModules("Post Test Modules", info, pstMods, si, page, false);
        page.append("</td></tr></table>");
    }

    //////////////////////////////////////////////////////////////////////////
    public void showPrePostModules(String title, SystemInfo info, List<QModule> mods,
                                   StudentInfo si, StringBuffer page, boolean greyed) throws Exception {
        if (mods != null && mods.size() > 0) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=45%><tr>");
            page.append("<td colspan=3 bgcolor=#dddddd>");
            if (greyed)
                page.append("<b><div CLASS=greypgtxt>");
            else
                page.append("<b><div CLASS=pgtxt>");
            page.append(title).append("</b></td></tr>");
            for (QModule m : mods) {
                int totalQ = m.getQuestionCount();
                int numCorrect = 0;
                int numAnswered = 0;
                page.append("<tr>");
                page.append("<td align=center bgcolor=");
                page.append(StudentHtml.haveNotWorkedOnColor);
                page.append(" width=10%><a href=\"");
                page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                        StudentSubsystem.viewTestModuleMode_,
                        GeneralHtml.moduleID_, m.getID()));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath());
                page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                page.append("</td>");
                page.append("<td bgcolor=#eeeeee>");
                page.append("<div CLASS=pgtxt>");
                page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
                page.append("<font size=\"-1\" ");
                if (greyed)
                    page.append("color=#dddddd ");
                else
                    page.append("color=#9586f9 ");
                page.append("face=\"Comic Sans MS\"><b>");
                page.append("Answered: " + numAnswered + "<br>Total questions: " + totalQ);
                page.append("</b></font></td>");
                page.append("</tr>");
            }
            page.append("</table>");
        }
    }

    public void showPracticeMods(String title, SystemInfo info, List<QModule> mods,
                                 StudentInfo si, StringBuffer page,
                                 boolean greyed) throws Exception {
        if (mods != null && mods.size() > 0) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
            page.append("<td colspan=3 bgcolor=#dddddd>");
            if (greyed)
                page.append("<b><div CLASS=greypgtxt>");
            else
                page.append("<b><div CLASS=pgtxt>");
            page.append(title).append("</b></td></tr>");
            for (QModule m : mods) {
                boolean isClearForDisplay = true;
                isClearForDisplay = (isClearForDisplay && checkDependencies(si, m, tMods));
                if (isClearForDisplay) {
                    int totalQ = m.getQuestionCount();
                    int numCorrect = 0;
                    int numAnswered = 0;
                    if (numAnswered != 0)
                        m.setTouch(true);
                    page.append("<tr>");
                    if (!greyed) {
                        page.append("<td align=center bgcolor=");
                        if (m.getTouch())
                            page.append(StudentHtml.alreadyWorkedOnColor);
                        else
                            page.append(StudentHtml.haveNotWorkedOnColor);
                        page.append(" width=10%><a href=\"");
                        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                                StudentSubsystem.viewModuleMode_,
                                GeneralHtml.moduleID_, m.getID()));
                        page.append("\">");
                        page.append("<img SRC=\"").append(info.getContextPath());
                        page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                        page.append("</td>");
                    }
                    page.append("<td bgcolor=#eeeeee>");
                    if (greyed)
                        page.append("<b><div CLASS=greypgtxt>");
                    else
                        page.append("<b><div CLASS=pgtxt>");
                    page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
                    page.append("<font size=\"-1\" ");
                    if (greyed)
                        page.append("color=#dddddd ");
                    else
                        page.append("color=#9586f9 ");
                    page.append("face=\"Comic Sans MS\"><b>");
                    page.append("Correct: " + numCorrect + "<br>Total questions: " + totalQ);
                    page.append("</b></font></td></tr>");
                }
            }
            page.append("</table>");
        }
    }

    public void showTestMod(String title, SystemInfo info, List<QModule> mods,
                            StudentInfo si, StringBuffer page, boolean greyed) throws Exception {
        if (mods != null && mods.size() > 0) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
            page.append("<td colspan=3 bgcolor=#dddddd>");
            if (greyed)
                page.append("<b><div CLASS=greypgtxt>");
            else
                page.append("<b><div CLASS=pgtxt>");
            page.append(title).append("</div></b></td></tr>");
            for (QModule m : mods) {
                boolean isClearForDisplay = true;
                isClearForDisplay = (isClearForDisplay && checkDependencies(si, m, tMods));
                if (isClearForDisplay) {
                    int totalQ = m.getQuestionCount();
                    int numCorrect = 0;
                    int numAnswered = 0;
                    if (numAnswered != 0) {
                        m.setTouch(true);
                    }
                    page.append("<tr>");
                    if (!greyed) {
                        page.append("<td align=center bgcolor=");
                        if (m.getTouch())
                            page.append(StudentHtml.alreadyWorkedOnColor);
                        else
                            page.append(StudentHtml.haveNotWorkedOnColor);
                        page.append(" width=10%><a href=\"");
                        String tUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                                StudentSubsystem.viewTestModuleMode_,
                                GeneralHtml.moduleID_, m.getID());
                        tUrl = GeneralHtml.appendToUrl(tUrl, GeneralHtml.moduleType_, m.getType());
                        page.append(tUrl);
                        page.append("\">");
                        page.append("<img SRC=\"").append(info.getContextPath());
                        page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                        page.append("</td>");
                    }
                    page.append("<td bgcolor=#eeeeee>");
                    if (greyed)
                        page.append("<b><div CLASS=greypgtxt>");
                    else
                        page.append("<b><div CLASS=pgtxt>");
                    page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
                    page.append("<font size=\"-1\" ");
                    if (greyed)
                        page.append("color=#dddddd ");
                    else
                        page.append("color=#9586f9 ");
                    page.append("face=\"Comic Sans MS\"><b>");
                    page.append("Correct: " + numCorrect + "<br>Total questions: " + totalQ);
                    page.append("</b></font></td></tr>");
                }
            }
            page.append("</table>");
        }
    }

    public void showPreTestResult(StudentInfo si, StringBuffer page, boolean greyed) {
        int corr = si.getPreTestNumCorrect();
        int total = si.getPreTestTotalQuestions();
        double score = corr / (double) total;
        if (greyed)
            GeneralHtml.getTestScoreDisplay(score, corr, total, "<b><div CLASS=greypgtxt>PreTest Results</b>", page);
        else
            GeneralHtml.getTestScoreDisplay(score, corr, total, "<b><div CLASS=pgtxt>PreTest Results</b>", page);
    }

    public void displayTestSelection(String testNum, SystemInfo info, String msg,
                                     StudentInfo si, StringBuffer page) throws Exception {
        page.append("<table CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
        page.append("<td><b><div CLASS=pgtxt>");
        page.append(msg);
        page.append("</td></tr><tr>");
        page.append("<FORM ACTION=\"");
        String action = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.getStudentExamMode_,
                GeneralHtml.studentID_, si.getUserID()) +
                "&testNum=" + testNum;
        page.append(action);
        page.append("\" METHOD=\"POST\" NAME=\"TestSelForm\">").append("\r\n");
        page.append("<TD ALIGN=center><INPUT TYPE=\"SUBMIT\" NAME=\"test\" VALUE=\"Test Me!\"></TD>").
                append("\r\n");
        page.append("</form>").append("\r\n");
        page.append("</tr></table>");
    }

    public void showPostTestResult(StudentInfo si, StringBuffer page, boolean greyed) {
        int corr = si.getPostTestNumCorrect();
        int total = si.getPostTestTotalQuestions();
        double score = corr / (double) total;
        if (greyed)
            GeneralHtml.getTestScoreDisplay(score, corr, total, "<b><div CLASS=greypgtxt>PostTest Results</b>", page);
        else
            GeneralHtml.getTestScoreDisplay(score, corr, total, "<b><div CLASS=pgtxt>PostTest Results</b>", page);
    }

    public boolean checkDependencies(StudentInfo info, QModule m, List<QModule> practiceMods) {
        boolean isClearForDisplay = true;
        Iterator parentIt = m.getParentModIDs();
        while (parentIt.hasNext()) {
            String curMID = (String) parentIt.next();
            for (QModule parentM : practiceMods) {
                if (parentM.getID().equals(curMID))
                    if (!info.isCompletedModule(curMID, parentM.getQuestionCount()))
                        isClearForDisplay = false;
            }
        }
        return isClearForDisplay;
    }

    private boolean areModsCompleted(Vector mods, StudentInfo si) {
        if (mods == null)
            return false;
        boolean allCompleted = true;
        for (int i = 0; i < mods.size(); i++) {
            QModule m = (QModule) mods.get(i);
            if (si.isCompletedModule(m.getID(), m.getQuestionCount()))
                m.setCompleted(true);
            else
                allCompleted = false;
        }
        return allCompleted;
    }
}


