package formality.controller.coursecontroller;

import java.util.Vector;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import formality.content.*;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DbModule;
import formality.content.database.mysql.DbQuestion;
import formality.content.database.mysql.DbHint;
import formality.html.*;
import formality.html.hints.BaseHintRenderer;
import formality.html.hints.HintRenderer2;
import formality.html.contenttable.NoUpdateContentTable;
import formality.controller.FormalitySubsystem;
import formality.controller.StudentSubsystem;
import formality.model.StudentInfo;
import formality.systemerror.AuthoringException;

import javax.servlet.http.HttpServletResponse;
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
public class NoUpdateCourseController extends BaseCourseController {


    public NoUpdateCourseController() {
        super();
    }

    public boolean handleRequest(Map params, SystemInfo info, Connection conn, StringBuffer page, HttpServletResponse resp) throws Exception {
        mode_ = GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "");
        dbAccessor_ = new DBAccessor();
        loadStudentData(dbAccessor_, mode_, info, conn);
        studentPage_ = new StudentHtml();
        hintRenderer_ = new HintRenderer2();
        msg_ = "";

        if (mode_.equals(StudentSubsystem.studentHomeMode_)) {
            String cID = info.getUserInfo().getCourseID();
            // String controller = info.getUserInfo().getCourseController();
            String sID = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, "");
            if (sID.equals("null"))
                sID = "";
            contentTable_ = new NoUpdateContentTable();
            List<QModule> ptMods = DbModule.loadActiveModulesOfType(cID, "PreTest", conn);
            List<QModule> pMods = DbModule.loadActiveModulesOfType(cID, "Practice", conn);
            List<QModule> tMods = DbModule.loadActiveModulesOfType(cID, "Test", conn);
            List<QModule> pstMods = DbModule.loadActiveModulesOfType(cID, "PostTest", conn);
            List<List<QModule>> content = new ArrayList<List<QModule>>();
            content.add(ptMods);
            content.add(pMods);
            content.add(tMods);
            content.add(pstMods);
            contentTable_.setContent(content);
            dbAccessor_.getUserCourseInfo(info.getUserInfo(), conn);
            studentPage_.getCustomStudentHomePage(contentTable_, null, info, page);
        } else if (mode_.equals(StudentSubsystem.viewModuleMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            StudentInfo si = (StudentInfo) info.getUserInfo();
            studentPage_.getModulePage(msg_, m, info, page);
        } else if (mode_.equals(StudentSubsystem.viewTestModuleMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }

            QModule m = DbModule.loadModule(mID, conn);
            StudentInfo si = (StudentInfo) info.getUserInfo();
            studentPage_.getTestModulePage(msg_, m, info, page);
        } else if (mode_.equals(StudentSubsystem.viewQuestionMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
            if (selQIndex < 1)
                throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals("")) {
                level = FormalitySubsystem.defaultHintLevel;
            }
            try {
                Question q = m.getLinkedQuestion(selQIndex);
                if (q.getType().equals("mc")) {
                    MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
                    mcq.setSelLevel(level);
                    m.setCurrentIndex(selQIndex);
                    studentPage_.getMCQuestionPage(msg_, m, mcq, info, hintRenderer_, page);
                } else if (q.getType().equals("sa")) {
                    ;//not implemented
                }
            } catch (Exception ex) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Error loading question QModule.getLinkedQuestion. questionID " + m.getLinkedQuestionID(selQIndex) + " mod index " + selQIndex);
                throw ae;
            }
        } else if (mode_.equals(StudentSubsystem.viewHintMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
            QModule m = DbModule.loadModule(mID, conn);
            loadStudentResponseData(dbAccessor_, info, conn);
            int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
            if (selQIndex < 1)
                throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals(""))
                level = FormalitySubsystem.defaultHintLevel;//default
            Question q = m.getLinkedQuestion(selQIndex);
            MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            mcq.setSelLevel(level);
            if (!hID.equals("")) {
                Hint h = mcq.getHints().getHint(hID);
                if (h != null)
                    mcq.setActiveTag(GeneralHtml.getHighlightTag(mcq.getSelLevel(), h.getCoachID()));
            }
            mcq.setSelHintID(hID);
            m.setCurrentIndex(selQIndex);
            studentPage_.getMCQuestionPage(msg_, m, mcq, info, null, hintRenderer_, page);
        } else if (mode_.equals(StudentSubsystem.selectLevelMode_)) {
            viewQuestion(params, info, conn, hintRenderer_, page);
        } else if (mode_.equals(StudentSubsystem.clearQuestionMode_)) {
            viewQuestion(params, info, conn, hintRenderer_, page);
        } else if (mode_.equals(StudentSubsystem.viewTestQuestionMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            StudentInfo si = (StudentInfo) info.getUserInfo();
            int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
            if (selQIndex < 1)
                throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
            m.setCurrentIndex(selQIndex);
            Question q = m.getLinkedQuestion(selQIndex);
            MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            m.setCurrentIndex(selQIndex);
            studentPage_.getMCTestQuestionPage(msg_, m, mcq, info, false, page);
        } else if (mode_.equals(StudentSubsystem.evaluateQuestionMode_)) {
            evaluatePracticeQuestion(params, info, conn, page);
        } else if (mode_.equals(StudentSubsystem.evaluateTestQuestionMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            //TODO- load content with fewer queries?
            QModule m = DbModule.loadModule(mID, conn);
            StudentInfo si = (StudentInfo) info.getUserInfo();
            int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
            if (selQIndex < 1) {
                throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
            }
            int curTestIndex = 0;
            // latestQID is null if no questions have been answered
            String latestQID = si.getLatestQuestionIDAnswered(mID);
            if (latestQID != null)
                curTestIndex = m.getQuestionIndex(latestQID);
            Question q = m.getLinkedQuestion(selQIndex);
            MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
            if (subAns.equals("")) {
                msg_ = "Choose an answer choice and click \"Check Answer\"";
                m.setCurrentIndex(selQIndex);
                studentPage_.getMCTestQuestionPage(msg_, m, mcq, info, false, page);
            } else {
                //evaluate
                if (mcq.getCorrectAnswer().equalsIgnoreCase(subAns)) {
                    mcq.setCorrect(true);
                }
                boolean completed = (selQIndex == m.getQuestionCount());
                if (completed) {
                    //get viewTestModulePage
                    m.setCompleted(true);
                    studentPage_.getTestModulePage("Note that your answers are not recorded for this demo.", m, info, page);
                } else {
                    //load and view next question
                    selQIndex++;
                    m.setCurrentIndex(selQIndex);
                    q = m.getLinkedQuestion(selQIndex);
                    mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
                    m.setCurrentIndex(selQIndex);
                    studentPage_.getMCTestQuestionPage(msg_, m, mcq, info, false, page);
                }
            }//end the answer string was not blank
        } else if (mode_.equals(StudentSubsystem.viewExternalActivity))
            ; // do nothing
        else if (mode_.equals(StudentSubsystem.viewGlossary))
            ; // do nothing
        else if (mode_.equals(StudentSubsystem.readAloud))
            return readAloud(params, info, conn, resp, false);
        return false;
    }

    protected Question evaluatePracticeQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        boolean evaluate = true;
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals("")) {
            level = FormalitySubsystem.defaultHintLevel;
        }
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
        module_ = DbModule.loadModule(mID, conn);
        Question q = module_.getLinkedQuestion(selQIndex);

        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(q);
        DbQuestion.loadMultipleChoiceQuestionAnswers(mcq, conn);
        mcq.setHints(DbHint.loadAllHints(mcq.getID(), info, conn));
        mcq.setSelLevel(level);
        mcq.setSubmittedAnswer(subAns);
        if (subAns.equals("")) {
            msg_ = "Choose an answer choice and click \"Check Answer\"";
            evaluate = false;
        }
        if (evaluate) {
            if (mcq.getCorrectAnswer().equalsIgnoreCase(subAns))
                mcq.setCorrect(true);
            mcq.setEvaluated(true);
            //no need to update the log for this demo
        }
        module_.setCurrentIndex(selQIndex);
        studentPage_.getMCQuestionPage(msg_, module_, mcq, info, null, hintRenderer_, page);
        return mcq;
    }

    protected void viewQuestion(Map params, SystemInfo info,
                                Connection conn, BaseHintRenderer hintRenderer,
                                StringBuffer page) throws Exception {
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
        loadStudentResponseData(dbAccessor_, info, conn);
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals("")) {
            level = FormalitySubsystem.defaultHintLevel;//default
        }
        Question q = m.getLinkedQuestion(selQIndex);
        MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
        mcq.setSelLevel(level);
        m.setCurrentIndex(selQIndex);
        studentPage_.getMCQuestionPage(msg_, m, mcq, info, null, hintRenderer, page);
    }

    protected void loadStudentData(DBAccessor dbAccessor, String mode,
                                   SystemInfo info, Connection conn) throws Exception {
        loadStudentResponseData(dbAccessor, info, conn);
    }


    /**
     * Loads module questions following ID order provided by the qID iterator.
     * @param qIDit
     * @param conn
     * @return
     * @throws Exception
     */
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

}

