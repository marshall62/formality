package formality.controller;

import java.util.*;

import formality.content.*;
import formality.content.database.ReportAccessor;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.*;
import formality.html.*;
import formality.html.hints.BaseHintRenderer;
import formality.html.hints.HintRenderer2;
import formality.html.contenttable.TeacherAdaptModContentTable;
import formality.html.contenttable.CourseContentTable;
import formality.controller.coursecontroller.parser.StateParser;
import formality.controller.coursecontroller.parser.BaseStateParser;
import formality.model.TeacherInfo;
import formality.model.UserInfo;
import formality.Util.DataTuple;
import formality.systemerror.AuthoringException;
import formality.reports.ModulePerformanceData;
import formality.reports.QuestionPerformanceData;

import java.sql.Connection;

/**
 * <p>Title: </p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p/>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TeacherSubsystem {
    // modes
    public static final String teacherHomeMode_ = "teachhome";
    public static final String teacherAllCoursesMode_ = "teachallhome";
    //reports
    public static final String viewClassActivityMode_ = "classact";
    public static final String viewStudentActivityMode_ = "stuact";
    public static final String getModuleReportMode_ = "mrept";
    public static final String getSkillReportMode_ = "srept";
    public static final String getCoachReportMode_ = "coachrept";

    public static final String studentHomeMode_ = "shome";
    public static final String viewModuleMode_ = "viewmod";
    public static final String viewTestModuleMode_ = "viewtmod";
    public static final String viewQuestionMode_ = "viewq";
    public static final String selectLevelMode_ = "sellevel";
    public static final String viewHintMode_ = "selhint";
    public static final String clearQuestionMode_ = "clearq";
    public static final String clearTestQuestionMode_ = "cleartq";
    public static final String viewTestQuestionMode_ = "viewtq";
    public static final String evaluateQuestionMode_ = "evalq";
    public static final String evaluateSurveyQuestionMode_ = "evalSurveyq";
    public static final String evaluateTestQuestionMode_ = "evaltq";
    public static final String editContentMode_ = "edct";

    DBAccessor dbAccessor_;
    ReportAccessor reptAccessor_;
    AdaptiveModuleAccessor atAccessor_;
    TeacherHtml teacherPage_;
    BaseHintRenderer hintRenderer_;
    String msg_;

    public TeacherSubsystem() {
    }

    public void handleRequest(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        String mode = GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "");
        dbAccessor_ = new DBAccessor();
        reptAccessor_ = new ReportAccessor();
        atAccessor_ = new AdaptiveModuleAccessor();
        dbAccessor_.loadStudentCourses(info.getUserInfo(), conn);
        String courseID = GeneralHtml.getParamStr(params, FormalitySubsystem.courseIDParam, null);
        info.getUserInfo().setSelectedCourseInfo(courseID);
        teacherPage_ = new TeacherHtml();
        hintRenderer_ = new HintRenderer2();
        msg_ = "";

        if (mode.equals(teacherHomeMode_)) {
            //check for more than one course
            if (info.getUserInfo().getSelectedCourse() == null && info.getUserInfo().getCourses().size() > 1) {
                mode = TeacherSubsystem.teacherAllCoursesMode_;
            } else {
                //load class info
                loadTeacherData(dbAccessor_, info.getUserInfo(), conn);
                String msg = GeneralHtml.getParamStr(params, "msg", null);
                String controller = info.getUserInfo().getCourseController();
                //for system generated content modules
                boolean showStudentSpecificContent = false;
                if (controller != null && controller.equals("AdaptiveTestModController"))
                    showStudentSpecificContent = true;
                teacherPage_.getTeacherHomePage(info, msg, showStudentSpecificContent, page);
            }
        }

        //display all courses
        if (mode.equals(TeacherSubsystem.teacherAllCoursesMode_)) {
            teacherPage_.getTeacherAllCoursesPage(null, info, page);
        } else if (mode.equals(editContentMode_)) {
            String msg = "";
            teacherPage_.getEditContentPage(msg, info, page);
        } else if (mode.equals(viewClassActivityMode_)) {
            List<DataTuple> data = DbModule.loadActiveModules(info.getUserInfo().getCourseID(), conn);
            teacherPage_.getSelectAllStudentModuleReportPage(null, data, info, page);
        } else if (mode.equals(getModuleReportMode_)) {
            loadTeacherData(dbAccessor_, info.getUserInfo(), conn);
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, null);
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID in getModuleReportMode.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            TreeMap<String, ArrayList<DataTuple>> rawModReptData = reptAccessor_.getStudentAttemptsModHistory(info.getUserInfo().getCourseID(), mID, conn);
            // modReptData:  Tree Map   key: studentID value: ArrayList<DataTuple>
            //                       DataTuple: (String)qID (Integer)#answers (Boolean)correct
            TreeMap<String, ArrayList<DataTuple>> modReptData = processModActivityData(rawModReptData);
            msg_ = "All Student Activity for Module: \"" + m.getName() + "\"";
            teacherPage_.getAllStudentModuleReportPage(msg_, m, modReptData, info, page);
        } else if (mode.equals(getSkillReportMode_)) {
            ReportAccessor ra = new ReportAccessor();
            double[] meanVals = dbAccessor_.getMeanModelData(DBAccessor.standardsDBNames_.length, conn);
            Vector skillReptData = ra.getAllStudentSkillData(info.getUserInfo().getCourseID(), DBAccessor.standardsDBNames_.length, conn);
            msg_ = null;
            teacherPage_.getAllStudentSkillsReportPage(msg_, skillReptData, meanVals, info, page);

        } else if (mode.equals(getCoachReportMode_)) {
            ReportAccessor ra = new ReportAccessor();

            List<ModuleCoachData> coachUseData = dbAccessor_.getClassCoachUseData(conn,courseID);
            msg_ = null;
            teacherPage_.getClassCoachUseReportPage(msg_, coachUseData, info, page);
        }
        else if (mode.equals(viewStudentActivityMode_)) {
            String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
            if (sID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing studentID in viewStudentActivityMode.");
                throw ae;
            }
            String cID = info.getUserInfo().getCourseID();
            String name = dbAccessor_.getStudentNameOrLogin(sID, conn);
            List<ModulePerformanceData> reportData = getStudentPerformanceData(sID, cID, conn);
            double[] skillLevs = dbAccessor_.getStudentSkillData(sID, DBAccessor.standardsDBNames_.length, conn);
            teacherPage_.getStudentAllModuleReportPage(reportData, name, info, page, skillLevs);
        } else if (mode.equals(studentHomeMode_)) {
            String cID = info.getUserInfo().getCourseID();
            String controller = info.getUserInfo().getCourseController();
            String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, "");
            if (sID.equals("null"))
                sID = "";
            if (controller.equals("AdaptiveTestModController") && sID != null && !sID.equals("")) {
                String name = dbAccessor_.getStudentNameOrLogin(sID, conn);
                String msg = "Content Modules for " + name;
                CourseContentTable table = new TeacherAdaptModContentTable();
                StateParser p = new BaseStateParser();
                p.parseState(sID);
                table.setState(p);
                List<List<QModule>> content = getContentVector(cID, sID, conn);
                table.setContent(content);
                teacherPage_.getStudentHomePage(table, msg, info, page);
            } else {
                List<QModule> ptMods = null;
                List<QModule> pMods = null;
                List<QModule> tMods = null;
                List<QModule> pstMods = null;
                ptMods = DbModule.loadActiveModulesOfType(cID, "PreTest", conn);
                pMods = DbModule.loadActiveModulesOfType(cID, "Practice", conn);
                tMods = DbModule.loadActiveModulesOfType(cID, "Test", conn);
                pstMods = DbModule.loadActiveModulesOfType(cID, "PostTest", conn);
                teacherPage_.getStudentHomePage(ptMods, pMods, tMods, pstMods, null, info, page);
            }
        } else if (mode.equals(viewModuleMode_)) {
            String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
            String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = null;
            if (mType.equals("Student"))
                m = atAccessor_.loadStudentModule(mID, conn);
            else
                m = DbModule.loadModule(mID, conn);
            teacherPage_.getModulePage(msg_, m, sID, info, page);
        } else if (mode.equals(viewTestModuleMode_)) {
            String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = null;
            if (mType.equals("Student"))
                m = atAccessor_.loadStudentModule(mID, conn);
            else
                m = DbModule.loadModule(mID, conn);
            teacherPage_.getTestModulePage(msg_, m, sID, info, page);
        } else if (mode.equals(viewQuestionMode_)) {
            viewQuestion1(params, info, conn, page);
        } else if (mode.equals(viewHintMode_)) {
            String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
            String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = null;
            if (mType.equals("Student"))
                m = atAccessor_.loadStudentModule(mID, conn);
            else
                m = DbModule.loadModule(mID, conn);
            String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
            if (selQIndex.equals(""))
                selQIndex = "1";
            int qIndex = Integer.parseInt(selQIndex);
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals("")) {
                level = FormalitySubsystem.defaultHintLevel;//default
            }
            Question q = m.getLinkedQuestion(qIndex);
            Question qq = (q.getType().equals("mc")) ? loadMultipleChoiceQuestion(q.getID(), info, conn) : loadShortAnswerQuestion(q.getID(), conn);
            qq.setSelLevel(level);
            String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
            if (!hID.equals("")) {
                Hint h = qq.getHints().getHint(hID);
                if (h != null) {
                    qq.setActiveTag(GeneralHtml.getHighlightTag(qq.getSelLevel(), h.getCoachID()));
                }
            }
            qq.setSelHintID(hID);
            m.setCurrentIndex(qIndex);
            if (q.getType().equals("mc"))
                teacherPage_.getMCQuestionPage(msg_, sID, m, (MultipleChoiceQuestion) qq, info, hintRenderer_, page);
            else teacherPage_.getSAQuestionPage(msg_, sID, m, (ShortAnswerQuestion) qq, info, hintRenderer_, page);

        } else if (mode.equals(selectLevelMode_)) {
            viewQuestion(params, info, conn, page);
        } else if (mode.equals(clearQuestionMode_)) {
            viewQuestion(params, info, conn, page);
        } else if (mode.equals(clearTestQuestionMode_)) {
            viewTestQuestion(params, info, conn, page);
        } else if (mode.equals(viewTestQuestionMode_)) {
            viewTestQuestion1(params, info, conn, page);
        } else if (mode.equals(evaluateQuestionMode_)) {
            evaluateQuestion(params, info, conn, page);

        } else if (mode.equals(evaluateTestQuestionMode_)) {
            evaluateTestQuestion(params, info, conn, page);
        }
    }

    private void evaluateQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        boolean isCurQuestion = false;
        boolean evaluate = true;
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        QModule m = null;
        if (mType.equals("Student"))
            m = atAccessor_.loadStudentModule(mID, conn);
        else
            m = DbModule.loadModule(mID, conn);
        String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
        if (selQIndex.equals(""))
            selQIndex = "1";
        int qIndex = Integer.parseInt(selQIndex);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals("")) {
            level = FormalitySubsystem.defaultHintLevel;
        }
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
        Question q = m.getLinkedQuestion(qIndex);
        Question qq = q.getType().equals("mc") ? loadMultipleChoiceQuestion(q.getID(), info, conn) : loadShortAnswerQuestion(q.getID(), conn);
        qq.setSelLevel(level);
        qq.setSubmittedAnswer(subAns);
        if (evaluate && subAns.equals("")) {
            msg_ = "Enter or choose an answer and click \"Check Answer\"";
            m.setCurrentIndex(qIndex);
            evaluate = false;
        }
        if (evaluate) {
            //evaluate
            if (qq.gradeAnswer(subAns))
                qq.setCorrect(true);
            qq.setEvaluated(true);
            //update the question log
        }
        m.setCurrentIndex(qIndex);
        if (q.getType().equals("mc"))
            teacherPage_.getMCQuestionPage(msg_, sID, m, (MultipleChoiceQuestion) qq, info, hintRenderer_, page);
        else teacherPage_.getSAQuestionPage(msg_, sID, m, (ShortAnswerQuestion) qq, info, hintRenderer_, page);
    }


    private void evaluateTestQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        boolean isCurQuestion = false;
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        QModule m = null;
        if (mType.equals("Student"))
            m = atAccessor_.loadStudentModule(mID, conn);
        else
            m = DbModule.loadModule(mID, conn);
        String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
        if (selQIndex.equals(""))
            selQIndex = "1";
        int curIndex = Integer.parseInt(selQIndex);
        Question q = m.getLinkedQuestion(curIndex);
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        String type = q.getType();
        Question quest = (q.getType().equals("mc")) ? loadMultipleChoiceQuestion(q.getID(), info, conn) : loadShortAnswerQuestion(q.getID(), conn);
        if (subAns.equals("")) {
            msg_ = "Choose an answer choice and click \"Check Answer\"";
            m.setCurrentIndex(curIndex);
            if (q.getType().equals("mc"))
                teacherPage_.getMCTestQuestionPage(msg_, sID, m, (MultipleChoiceQuestion) quest, info, false, page);
            else teacherPage_.getSATestQuestionPage(msg_, sID, m, (ShortAnswerQuestion) quest, info, false, page);
        } else {
            //evaluate
            boolean corr = false;
            if (quest.gradeAnswer(subAns))
                corr = true;
            boolean completed = (m.getNumLinkedQuestions() == curIndex);
            if (completed) {
                //get viewTestModulePage
                m.setCompleted(true);
                teacherPage_.getTestModulePage(msg_, m, sID, info, page);
            } else {
                // TODO non survey SA and MC questions should be evaluated and then re-displayed with
                // grading.   The nav bar should make links out the problems below so the teacher can advance
                // to the next question.
                // For now just put a message at the top of the next question that tells whether they answered
                // the previous question correctly
                if (!q.isSurvey())
                        msg_ = corr ? "Your last answer was correct" : "Your last answer was incorrect.   Expected " + quest.getCorrectAnswer();
             //load and view next question
                curIndex++;
                q = m.getLinkedQuestion(curIndex);
                produceTestQuestionPage(info,conn,page,sID,m,curIndex,q);

            }
        } //end eval

    }

    private void viewQuestion1(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        long curTS = System.currentTimeMillis();
        String endQ = GeneralHtml.getParamStr(params, GeneralHtml.exitQuestion_, "");
        QModule m = null;
        if (mType.equals("Student"))
            m = atAccessor_.loadStudentModule(mID, conn);
        else
            m = DbModule.loadModule(mID, conn);
        String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
        if (selQIndex.equals(""))
            selQIndex = "1";
        int qIndex = Integer.parseInt(selQIndex);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals("")) {
            level = FormalitySubsystem.defaultHintLevel;//default
        }
        Question q = m.getLinkedQuestion(qIndex);
        if (q.isSurvey() && q.getType().equals("mc")) {
            MultiChoiceSurveyQuestion mcsq = DbQuestion.loadMultipleChoiceSurveyQuestion(q.getID(), conn);
            m.setCurrentIndex(qIndex);
            mcsq.setSelLevel(level);
            teacherPage_.getMCSurveyQuestionPage(msg_, sID, m, mcsq, info, page);
        } else if (q.getType().equals("mc")) {
            MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            mcq.setSelLevel(level);
            m.setCurrentIndex(qIndex);
            teacherPage_.getMCQuestionPage(msg_, sID, m, mcq, info, hintRenderer_, page);
        } else if (q.getType().equals("sa")) {
            ShortAnswerQuestion sq = loadShortAnswerQuestion(q.getID(), conn);
            sq.setSelLevel(level);
            m.setCurrentIndex(qIndex);
            teacherPage_.getSAQuestionPage(msg_, sID, m, sq, info, hintRenderer_, page);
        }
    }

    protected void viewQuestion(Map params, SystemInfo info,
                                Connection conn, StringBuffer page) throws Exception {
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        QModule m = null;
        if (mType.equals("Student"))
            m = atAccessor_.loadStudentModule(mID, conn);
        else
            m = DbModule.loadModule(mID, conn);
        String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
        if (selQIndex.equals(""))
            selQIndex = "1";
        int qIndex = Integer.parseInt(selQIndex);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals("")) {
            level = FormalitySubsystem.defaultHintLevel;//default
        }
        Question q = m.getLinkedQuestion(qIndex);
        Question qq = (q.getType().equals("mc")) ? loadMultipleChoiceQuestion(q.getID(), info, conn) : loadShortAnswerQuestion(q.getID(), conn);
        qq.setSelLevel(level);
        m.setCurrentIndex(qIndex);
        if (q.getType().equals("mc"))
            teacherPage_.getMCQuestionPage(msg_, sID, m, (MultipleChoiceQuestion) qq, info, hintRenderer_, page);
        else teacherPage_.getSAQuestionPage(msg_, sID, m, (ShortAnswerQuestion) qq, info, hintRenderer_, page);


    }

    private void viewTestQuestion1(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        QModule m = null;
        if (mType.equals("Student"))
            m = atAccessor_.loadStudentModule(mID, conn);
        else
            m = DbModule.loadModule(mID, conn);
        int qIndex = 1;
        String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
        if (selQIndex.equals(""))
            qIndex = 1;
        else
            qIndex = Integer.parseInt(selQIndex);
        Question q = m.getLinkedQuestion(qIndex);
        produceTestQuestionPage(info, conn, page, sID, m, qIndex, q);


    }

    private void produceTestQuestionPage(SystemInfo info, Connection conn, StringBuffer page, String sID, QModule m, int qIndex, Question q) throws Exception {
        // TODO NEED TO VERIFY that all 4 of these produce a page with footer that lists other questions
        // but does not make links to them.   (pageFooter should be passing null URL to getNavTable)
        // TODO Also need to make sure evaltq does similar to this since each question eval leads to the
        // next question which is equiv to this.
        m.setCurrentIndex(qIndex);
        if (q.isSurvey() && q.getType().equals("mc")) {
            MultiChoiceSurveyQuestion mcsq = DbQuestion.loadMultipleChoiceSurveyQuestion(q.getID(), conn);
            // TODO this one is not producing correct page.  Page is missing a submit button
            // page footer should not have links to other questions.
            teacherPage_.getMCTestSurveyQuestionPage(msg_, sID, m, mcsq, info, false, page);
        } else if (q.isSurvey() && q.getType().equals("sa")) {
            ShortAnswerQuestion sq = loadShortAnswerQuestion(q.getID(), conn);
            // VERiFIED produces correct nav at bottom
            teacherPage_.getSATestQuestionPage(msg_, sID, m, sq, info, true, page);
        } else if (q.getType().equals("mc")) {
            MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            // VERIFIED THAT this produces correct nav at bottom
            teacherPage_.getMCTestQuestionPage(msg_, sID, m, mcq, info, true, page);
        } else if (q.getType().equals("sa")) {
            ShortAnswerQuestion sq = loadShortAnswerQuestion(q.getID(), conn);
            // VERIFIED 
            teacherPage_.getSATestQuestionPage(msg_, sID, m, sq, info, true, page);
        }
    }

    protected void viewTestQuestion(Map params, SystemInfo info,
                                    Connection conn, StringBuffer page) throws Exception {
        boolean isCurQuestion = false;
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, null);
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }

        QModule m = null;
        if (mType.equals("Student"))
            m = atAccessor_.loadStudentModule(mID, conn);
        else
            m = DbModule.loadModule(mID, conn);
        String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
        if (selQIndex.equals(""))
            selQIndex = "1";
        int curIndex = Integer.parseInt(selQIndex);

        Question q = m.getLinkedQuestion(curIndex);
        Question qq = (q.getType().equals("mc")) ? loadMultipleChoiceQuestion(q.getID(), info, conn) : loadShortAnswerQuestion(q.getID(),conn);
        m.setCurrentIndex(curIndex);
        if (q.getType().equals("mc"))
            teacherPage_.getMCTestQuestionPage(msg_, sID, m, (MultipleChoiceQuestion) qq, info, false, page);
        else teacherPage_.getSATestQuestionPage(msg_, sID, m, (ShortAnswerQuestion) qq, info, false, page);


    }

    //load all pertinent info for this student
    protected void loadTeacherData(DBAccessor dbAccessor,
                                   UserInfo info, Connection conn) throws Exception {
        TeacherInfo ti = (TeacherInfo) info;
        dbAccessor.getAllStudentsInCourseNames(ti, conn);
    }

    public Vector getModuleQuestions(Iterator qIDit, Connection conn) throws Exception {
        Vector questions = new Vector();
        while (qIDit.hasNext()) {
            String qID = (String) qIDit.next();
            Question q = DbQuestion.loadQuestion(qID, conn);
            questions.add(q);
        }
        return questions;
    }

    protected MultipleChoiceQuestion loadMultipleChoiceQuestion(String qID, SystemInfo info, Connection conn) throws Exception {
        MultipleChoiceQuestion q = DbQuestion.loadMultipleChoiceQuestion(qID, conn);
        Hints hints = DbHint.loadAllQuestionHints(q, conn);
        q.setHints(hints);
        return q;
    }

    protected ShortAnswerQuestion loadShortAnswerQuestion(String qID, Connection conn) throws Exception {
        ShortAnswerQuestion q = DbQuestion.loadShortAnswerQuestion(qID, conn);
        Hints hints = DbHint.loadAllQuestionHints(q, conn);
        q.setHints(hints);
        return q;
    }

    private List<List<QModule>> getContentVector(String cID, String sID, Connection conn) throws Exception {
        List<QModule> tempPtMods = DbModule.loadActiveModulesOfType(cID, "PreTest", conn);
        List<QModule> tempPstMods = DbModule.loadActiveModulesOfType(cID, "PostTest", conn);
        List<QModule> ptMods = new ArrayList<QModule>();
        List<QModule> pstMods = new ArrayList<QModule>();
        String ptModID = atAccessor_.getPreTestModID(sID, conn);
        String pstModID = atAccessor_.getPostTestModID(sID, conn);
        if (ptModID != null) {
            ptMods.add(getQMod(ptModID, tempPtMods));
            pstMods.add(getQMod(pstModID, tempPstMods));
        }

        List<QModule> pMods = DbModule.loadActiveModulesOfType(cID, "Practice", conn);
        Vector tMods = atAccessor_.loadStudentModules(sID, conn);

        //assume that the first mod in tMods is test1, etc.
        Vector t1Mods = new Vector();
        Vector t2Mods = new Vector();
        int sz = tMods.size();
        if (sz == 1)
            t1Mods.add(tMods.get(0));
        if (sz == 2) {
            t1Mods.add(tMods.get(0));
            t2Mods.add(tMods.get(1));
        }
        Vector content = new Vector();
        content.add(ptMods);
        content.add(t1Mods);
        content.add(t2Mods);
        content.add(pstMods);
        return content;
    }


    /**
     * Returns data about how this student did on each question in each module
     *
     * @param sID
     * @param cID
     * @param conn
     * @return
     * @throws Exception
     */
    private List<ModulePerformanceData> getStudentPerformanceData(String sID, String cID,
                                                                  Connection conn) throws Exception {
        List<ModulePerformanceData> reportData = new ArrayList<ModulePerformanceData>();
        //first, get all course modules
        List<QModule> mods = DbModule.loadActiveModuleObjects(cID, conn);
        Vector smods = new Vector(); // DM no longer have a studentModule table : atAccessor_.loadStudentModuleObjects(sID, conn);
//        if (smods.size() > 0)
//            mods.addAll(smods);
        for (QModule m : mods) {
            //now get the question stats for the module
            TreeMap<String, QuestionPerformanceData> qData = getQData(m, sID, conn);
            reportData.add(new ModulePerformanceData(qData, m));
        }
        return reportData;
    }

    /**
     * Given a module and student id, map each question in the module to a DataTuple which stores
     * information about how the student did on that question (including a flag in the sixth field which
     * indicates whether the student even saw the question).
     * <p/>
     * The DataTuple contains:  skill, ever got it correct, hints before 1st ans, hints before 1st corr ans,
     * total hints, sawQuestion
     *
     * @param m
     * @param sID
     * @param conn
     * @return A map questionId -> DataTuple for each question in the given module
     * @throws Exception
     */
    public TreeMap<String, QuestionPerformanceData> getQData(QModule m, String sID, Connection conn) throws Exception {
        String mID = m.getID();
        TreeMap<String, QuestionPerformanceData> qData = new TreeMap<String, QuestionPerformanceData>();
        List<String> qIDs = m.getLinkedQuestionIDList();
        for (String qID : qIDs) {
            QuestionPerformanceData dt = getCurQData(sID, mID, qID, conn);
            qData.put(qID, dt);
        }
        return qData;
    }

    /**
     * Given a student, module, and question, return performance data.
     *
     * @param sID
     * @param mID
     * @param qID
     * @param conn
     * @return
     * @throws Exception
     */
    public QuestionPerformanceData getCurQData(String sID,
                                               String mID,
                                               String qID,
                                               Connection conn) throws Exception {
        int hintBeforeFirstAnsCt = 0;
        int hintBeforeFirstCorrectCt = 0;
        int numAttemptsForCorrect = 0;
        boolean foundCorrect = false;
        List<String> standards = reptAccessor_.getQSkills(qID, conn);
        ArrayList<DataTuple> responseData = reptAccessor_.getQuestionResponseTimesAndCorrect(sID, mID, qID, false, conn);
        ArrayList<String> hintTimes = reptAccessor_.getHintResponseTimes(sID, mID, qID, false, conn);
//        double[] skillLevs = new double[standards.size()];
//        for (int i=0;i<standards.size();i++) {
//            skillLevs[i] = DBAccessor.getStudentSkillLevel(sID,standards.get(i),conn);
//        }
        boolean sawQuestion = false;
        if (responseData.size() == 0 && hintTimes.size() > 0) {
            hintBeforeFirstAnsCt = hintTimes.size();
            hintBeforeFirstCorrectCt = hintTimes.size();
            sawQuestion = true;
        } else if (responseData.size() > 0) {
            sawQuestion = true;

            numAttemptsForCorrect = responseData.size(); // initialize to the number of attempts
            for (int i = 0; i < responseData.size(); i++) {
                DataTuple dt = responseData.get(i);
                String curRespTS = (String) dt.getFirst();
                Boolean corrBool = (Boolean) dt.getSecond();
                if (i == 0) {
                    for (int j = 0; j < hintTimes.size(); j++) {
                        String curHintTS = hintTimes.get(j);
                        if (curRespTS.compareTo(curHintTS) > 0)
                            hintBeforeFirstAnsCt++;
                    }
                }
                // when the first correct answer is found remember its position (this is the number of attempts it took
                // before the problem is answered correctly)
                if (!foundCorrect && corrBool) {
                    numAttemptsForCorrect = i + 1;
                    foundCorrect = true;
                    // find the number of hints seen before this correct answer
                    for (int j = 0; j < hintTimes.size(); j++) {
                        String curHintTS = hintTimes.get(j);
                        if (curRespTS.compareTo(curHintTS) > 0)
                            hintBeforeFirstCorrectCt++;
                    }

                }


            }
        }
        int totalHints = hintTimes.size();
        return new QuestionPerformanceData(standards, foundCorrect, hintBeforeFirstAnsCt, hintBeforeFirstCorrectCt, totalHints, sawQuestion, numAttemptsForCorrect);
    }

    //INPUT:  Tree Map   key: studentID value: ArrayList<DataTuple>
    //                       DataTuple: (String)qID, (Boolean)correct
    //
    //OUTPUT:  Tree Map   key: studentID value: ArrayList<DataTuple>
    //                       DataTuple: (String)qID (Integer)#answers (Boolean)correct

    protected TreeMap<String, ArrayList<DataTuple>> processModActivityData(TreeMap<String,
            ArrayList<DataTuple>> rawModReptData)
            throws Exception {
        TreeMap<String, ArrayList<DataTuple>> allStudentData = new TreeMap<String, ArrayList<DataTuple>>();
        Iterator<String> rawDataIter = rawModReptData.keySet().iterator();
        while (rawDataIter.hasNext()) {
            String curSID = rawDataIter.next();
            ArrayList<DataTuple> processedQData = new ArrayList<DataTuple>();
            ArrayList<DataTuple> rawQData = rawModReptData.get(curSID);
            for (int i = 0; i < rawQData.size(); i++) {
                DataTuple curDT = rawQData.get(i);
                String curQID = (String) curDT.getFirst();
                Boolean oldCorrect = (Boolean) curDT.getSecond();
                addNewQData(curQID, oldCorrect, processedQData);
            }
            allStudentData.put(curSID, processedQData);
        }
        return allStudentData;
    }

    //update processedQData with this new information
    //  DataTuple: (String)qID (Integer)#answers (Boolean)correct
    private void addNewQData(String qID, Boolean correct, ArrayList<DataTuple> processedQData) {
        //locate the record for this qID- if it exists, if not-create a new one
        boolean found = false;
        for (int i = 0; i < processedQData.size(); i++) {
            DataTuple curDT = processedQData.get(i);
            String curQID = (String) curDT.getFirst();
            if (curQID.equals(qID)) {
                found = true;
                Integer oldCtInt = (Integer) curDT.getSecond();
                int newCt = oldCtInt.intValue();
                newCt++;
                curDT.setSecond(new Integer(newCt));
                Boolean oldCorrectBool = (Boolean) curDT.getThird();
                boolean newCorrect = (oldCorrectBool.booleanValue() || correct.booleanValue());
                curDT.setThird(new Boolean(newCorrect));
            }
        }
        if (!found) { //create a new entry for this questionID
            DataTuple newDT = new DataTuple();
            newDT.setFirst(qID);
            newDT.setSecond(new Integer(1));
            newDT.setThird(new Boolean(correct));
            processedQData.add(newDT);
        }
    }

    private QModule getQMod(String mID, List<QModule> mods) throws Exception {

        for (QModule m : mods) {
            if (m.getID().equals(mID))
                return m;
        }
        return null;
    }
}

