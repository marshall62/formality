package formality.html.contenttable;

import formality.content.SystemInfo;
import formality.content.QModule;
import formality.model.StudentInfo;
import formality.controller.StudentSubsystem;
import formality.controller.FormalitySubsystem;
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
public class BaseContentTable implements CourseContentTable {

    public static final String postTestSelMsg_ = "You did it! You have completed all practice and test questions. Click on the button when you are ready to take the post test.";
    public static final String finalMsg_ = "Congratulations! You have completed all class material. You may continue to practice the modules above. Good Work!";
    List<QModule> ptMods = null;
    List<QModule> pMods = null;
    List<QModule> tMods = null;
    List<QModule> pstMods = null;
    StateParser stateParser_ = null;

    public void setState(StateParser state) {
        stateParser_ = state;
    }

    //content is a Vector of Vectors
    public void setContent(List<List<QModule>> content) throws Exception {
        //assume the vectors are not null
        ptMods =  content.get(0);
        pMods = content.get(1);
        tMods =  content.get(2);
        pstMods =  content.get(3);
    }

    public void getContentTable(SystemInfo info,
                                StringBuffer page) throws Exception {

        StudentInfo si = (StudentInfo) info.getUserInfo();

        boolean showOnlyPostTestMods = stateParser_.showPostTest();

        //begin page/////////////////////////////////////////////////
        page.append("<table><tr><td>");

        if (!stateParser_.isPreTest() && ptMods.size() > 0) {
            showPreTestResult(si, page, false);
            page.append("<br><br>");
        } else if (stateParser_.isPreTest()) {
            //PreTest Modules
            showPrePostModules("Pre Test Modules", info, ptMods, si, page, showOnlyPostTestMods);
        }
        //end pretest mods

        if (!stateParser_.isPreTest()) {
            page.append("<table width=100%><tr><td valign=top width=50%>\r\n");
            //Practice Modules
            showPracticeMods("Practice Modules", info, pMods, si, page, showOnlyPostTestMods);
            page.append("<br></td>\r\n");
            page.append("<td valign=top width=50%>\r\n");
            //Test Modules
            showTestMod("Test Modules", info, tMods, si, page, showOnlyPostTestMods);
            page.append("</td></tr></table>\r\n");
        }
        if (stateParser_.isFin() && pstMods.size() > 0) {
            showPostTestResult(si, page, false);
            page.append("<br><br>\r\n");
        }
        if (showOnlyPostTestMods)
            //PostTest Modules
            showPrePostModules("Post Test Modules", info, pstMods, si, page, false);
        else if (stateParser_.showSelect())
            displayTestSelection("pst", info, postTestSelMsg_, si, page);
        page.append("</td></tr></table>\r\n");
    }

    //////////////////////////////////////////////////////////////////////////
    public void showPrePostModules(String title, SystemInfo info, List<QModule> mods,
                                   StudentInfo si, StringBuffer page, boolean greyed) throws Exception {
        if (mods != null && mods.size() > 0) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=45%><tr>\r\n");
            page.append("<td colspan=3 bgcolor=#dddddd>\r\n");
            if (greyed)
                page.append("<b><div CLASS=greypgtxt>\r\n");
            else
                page.append("<b><div CLASS=pgtxt>");
            page.append(title).append("</b></td></tr>\r\n");
            for (QModule m : mods) {
                int totalQ = m.getQuestionCount();
                int numCorrect = si.getNumCorrectInModule(m.getID());
                int numAnswered = si.getDistinctNumAnsweredInModule(m.getID());
                boolean isModComplete = si.isCompletedModule(m.getID(),totalQ);
                if (numAnswered != 0) {
                    m.setTouch(true);
                }
                page.append("<tr>");
                if (!greyed) {
                    page.append("<td align=center bgcolor=");
//                    if (module.isCompleted())    // DM changed this because the module does not have its isComplete attribute set.
                    // the info about module completion is only held in the StudentInfo object
                    if (isModComplete)
                        page.append(StudentHtml.alreadyWorkedOnColor);
                    else
                        page.append(StudentHtml.haveNotWorkedOnColor);
                    page.append(" width=10%>");
                    // If the module is not complete, include a link to it.
                    if (!isModComplete) {
                     page.append("<a href=\"");
                        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                                StudentSubsystem.viewTestModuleMode_,
                                GeneralHtml.moduleID_, m.getID(),
                                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
                        page.append("\">");
                        page.append("<img SRC=\"").append(info.getContextPath());
                        page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                    }
                    page.append("</td>\r\n");
                }
                page.append("<td bgcolor=#eeeeee>\r\n");
                if (greyed)
                    page.append("<div CLASS=greypgtxt>");
                else
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
                page.append("</tr>\r\n");
            }
            page.append("</table>\r\n");
        }
    }

    public void showPracticeMods(String title, SystemInfo info, List<QModule> mods,
                                 StudentInfo si, StringBuffer page,
                                 boolean greyed) throws Exception {
        if (mods != null && mods.size() > 0) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>\r\n");
            page.append("<td colspan=3 bgcolor=#dddddd>\r\n");
            if (greyed)
                page.append("<b><div CLASS=greypgtxt>");
            else
                page.append("<b><div CLASS=pgtxt>");
            page.append(title).append("</b></td></tr>\r\n");
            for (QModule m : mods) {
                boolean isClearForDisplay = checkDependencies(si, m, pMods);
                isClearForDisplay = (isClearForDisplay && checkDependencies(si, m, tMods));
                if (isClearForDisplay) {
                    int totalQ = m.getQuestionCount();
                    int surveyQs = m.getSurveyQuestions().size();
                    totalQ -= surveyQs;
                    int numCorrect = si.getNumCorrectInModule(m.getID());
                    int numAnswered = si.getDistinctNumAnsweredInModule(m.getID());
                    if (numAnswered != 0)
                        m.setTouch(true);
                    page.append("<tr>");
                    if (!greyed) {
                        page.append("<td align=center bgcolor=");
                        if (m.isCompleted())
                            page.append(StudentHtml.alreadyWorkedOnColor);
                        else
                            page.append(StudentHtml.haveNotWorkedOnColor);
                        page.append(" width=10%><a href=\"");
                        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                                StudentSubsystem.viewModuleMode_,
                                GeneralHtml.moduleID_, m.getID(),
                                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
                        page.append("\">");
                        page.append("<img SRC=\"").append(info.getContextPath());
                        page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                        page.append("</td>\r\n");
                    }
                    page.append("<td bgcolor=#eeeeee>\r\n");
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
                    page.append("Attempted: " + numAnswered + "<br>Correct: " + numCorrect + "<br>Total questions: " + totalQ);
                    page.append("</b></font></td></tr>\r\n");
                }
            }
            page.append("</table>\r\n");
        }
    }

    public void showTestMod(String title, SystemInfo info, List<QModule> mods,
                            StudentInfo si, StringBuffer page, boolean greyed) throws Exception {
        if (mods != null && mods.size() > 0) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>\r\n");
            page.append("<td colspan=3 bgcolor=#dddddd>\r\n");
            if (greyed)
                page.append("<b><div CLASS=greypgtxt>");
            else
                page.append("<b><div CLASS=pgtxt>");
            page.append(title).append("</div></b></td></tr>\r\n");
            for (QModule m :mods) {
                boolean isClearForDisplay = checkDependencies(si, m, pMods);
                isClearForDisplay = (isClearForDisplay && checkDependencies(si, m, tMods));
                if (isClearForDisplay) {
                    int totalQ = m.getQuestionCount();
                    int numCorrect = si.getNumCorrectInModule(m.getID());
                    int surveyQs = m.getSurveyQuestions().size();
                    totalQ -= surveyQs;
                    int numAnswered = si.getDistinctNumAnsweredInModule(m.getID());
                    if (numAnswered != 0) {
                        m.setTouch(true);
                    }
                    page.append("<tr>");
                    if (!greyed) {
                        page.append("<td align=center bgcolor=");
                        if (m.isCompleted())
                            page.append(StudentHtml.alreadyWorkedOnColor);
                        else
                            page.append(StudentHtml.haveNotWorkedOnColor);
                        page.append(" width=10%>");
                        // only display the select arrow if the test is Reentrant or its not complete
                        if (m.isReentrant() || !m.isCompleted()) {
                            page.append("<a href=\"");
                            String tUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                                    StudentSubsystem.viewTestModuleMode_,
                                    GeneralHtml.moduleID_, m.getID(),
                                    FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
                            tUrl = GeneralHtml.appendToUrl(tUrl, GeneralHtml.moduleType_, m.getType());
                            page.append(tUrl);
                            page.append("\">");
                            page.append("<img SRC=\"").append(info.getContextPath());
                            page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
                        }
                        page.append("</td>\r\n");
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
                    page.append("Attempted: " + numAnswered + "<br>Correct: " + numCorrect + "<br>Total questions: " + totalQ);
                    page.append("</b></font></td></tr>\r\n");
                }
            }
            page.append("</table>\r\n");
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
        page.append("<table CELLPADDING=3 CELLSPACING=0 width=90%><tr>\r\n");
        page.append("<td><b><div CLASS=pgtxt>");
        page.append(msg);
        page.append("</td></tr><tr>\r\n");
        page.append("<FORM ACTION=\"");
        String action = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.getStudentExamMode_,
                GeneralHtml.studentID_, si.getUserID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()) +
                "&testNum=" + testNum;
        page.append(action);
        page.append("\" METHOD=\"POST\" NAME=\"TestSelForm\">").append("\r\n");
        page.append("<TD ALIGN=center><INPUT TYPE=\"SUBMIT\" NAME=\"test\" VALUE=\"Test Me!\"></TD>").
                append("\r\n");
        page.append("</form>").append("\r\n");
        page.append("</tr></table>\r\n");
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

    // Return true if module module is displayable.
    // Its should only be displayed if all its parent modules have been completed (had all questions answered)
    //
    public boolean checkDependencies(StudentInfo info, QModule m, List<QModule> prereqMods) {
        boolean isClearForDisplay = true;
        Iterator parentIt = m.getParentModIDs();
        // Go through all m's parent modules (a list of IDs), look up the module in prereq list and and see if it is complete
        while (parentIt.hasNext()) {
            String curMID = (String) parentIt.next();   // current parent module ID
            // go through all the given prereq modules and find the one representing the current parent module ID
            // If the found parent is not complete, then set a flag saying that this module is not displayable
            // TODO looks like a bug might exist here since a module can have multiple parents, shouldn't this
            // loop exit the minute a parent is found that is not complete.   The way this works is that the
            // loop keeps going and if the last parent checked is complete, then this modules is considered
            // displayable
            for (QModule parentM : prereqMods) {
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


