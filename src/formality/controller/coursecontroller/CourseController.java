package formality.controller.coursecontroller;

import formality.content.*;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.AdaptiveModuleAccessor;
import formality.content.database.mysql.DbModule;
import formality.content.database.mysql.DbQuestion;
import formality.content.database.mysql.DbHint;
import formality.content.database.ReportAccessor;
import formality.model.StudentInfo;
import formality.model.ModelEvent;
import formality.model.ModelListener;
import formality.model.BetaStudentModel;
import formality.controller.StudentSubsystem;
import formality.controller.FormalitySubsystem;
import formality.controller.coursecontroller.parser.BaseStateParser;
import formality.html.GeneralHtml;
import formality.html.StudentHtml;
import formality.html.hints.BaseHintRenderer;
import formality.html.hints.HintRenderer2;
import formality.html.contenttable.CourseContentTable;
import formality.html.contenttable.BaseContentTable;
import formality.systemerror.AuthoringException;
import formality.records.EventLogger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Dec 26, 2005
 * Time: 3:23:03 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CourseController {

    DBAccessor dbAccessor_;
    StudentHtml studentPage_;
    BaseHintRenderer hintRenderer_;
    String msg_;
    ArrayList<ModelListener> modelListeners_;
    QModule module_;
    MultiChoiceSurveyQuestion mcq_;
    CourseContentTable contentTable_;
    AdaptiveModuleAccessor atAccessor_;
    ReportAccessor reptAccessor_;
    String mode_;

    public boolean handleRequest(Map params, SystemInfo info, Connection conn, StringBuffer page, HttpServletResponse resp) throws Exception {
        mode_ = GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "");
        hintRenderer_ = new HintRenderer2();
        dbAccessor_ = new DBAccessor();
        atAccessor_ = new AdaptiveModuleAccessor();
        reptAccessor_ = new ReportAccessor();
        modelListeners_ = initModelListeners(conn);

        if (info.getUserInfo().getSelectedCourse() == null && info.getUserInfo().getCourses().size() > 1)
            mode_ = StudentSubsystem.studentAllCoursesMode_;
        studentPage_ = new StudentHtml();
        msg_ = "";

        if (mode_.equals(StudentSubsystem.getStudentExamMode_)) {
            String testNum = GeneralHtml.getParamStr(params, "testNum", "");
            String state = testNum;
            info.getUserInfo().setState(state);
            mode_ = StudentSubsystem.studentHomeMode_;
        }

        if (mode_.equals(StudentSubsystem.studentHomeMode_))
            studentHome(info, conn, page);
        else if (mode_.equals(StudentSubsystem.studentAllCoursesMode_))
            studentPage_.getStudentAllCoursesPage(null, info, page);
        else if (mode_.equals(StudentSubsystem.viewModuleMode_))
            viewModule(params, info, conn, page);
        else if (mode_.equals(StudentSubsystem.viewTestModuleMode_))
            viewTestModule(params, info, conn, page);
        else if (mode_.equals(StudentSubsystem.viewQuestionMode_))
            viewQuestion(params, info, conn, page, StudentSubsystem.viewQuestionMode_);
            // process asynchronous call from client when user views an external activity
        else if (mode_.equals(StudentSubsystem.viewExternalActivity))
            logUserAction(params, info, conn, StudentSubsystem.viewExternalActivity);
        else if (mode_.equals(StudentSubsystem.viewGlossary))
            logUserAction(params, info, conn, StudentSubsystem.viewGlossary);
        else if (mode_.equals(StudentSubsystem.viewHintMode_))
            viewHint(params, info, conn, page);
        else if (mode_.equals(StudentSubsystem.selectLevelMode_))
            reviewQuestion(params, info, conn, page, StudentSubsystem.selectLevelMode_, new ModelEvent(ModelEvent.EventTypes.selectHintLevel));
        else if (mode_.equals(StudentSubsystem.clearQuestionMode_))
            reviewQuestion(params, info, conn, page, StudentSubsystem.clearQuestionMode_, new ModelEvent(ModelEvent.EventTypes.clearQuestion));
        else if (mode_.equals(StudentSubsystem.viewTestQuestionMode_))
            viewTestQuestion(params, info, conn, page, StudentSubsystem.viewTestQuestionMode_);
        else if (mode_.equals(StudentSubsystem.evaluateQuestionMode_))
            evaluatePracticeQuestion(params, info, conn, page);
        else if (mode_.equals(StudentSubsystem.evaluateSurveyQuestionMode_))
            evaluateSurveyQuestion(params, info, conn, page);
        else if (mode_.equals(StudentSubsystem.evaluateTestQuestionMode_) ||
                mode_.equals(StudentSubsystem.evaluateSurveyTestQuestionMode_))
            evaluateTestQuestion(params, info, conn, page, mode_);
            // note that this one returns a true code because it writes to the servlet output buffer and closes it
        else if (mode_.equals(StudentSubsystem.readAloud))
            return readAloud(params, info, conn, resp, true);
        return false;

    }


    protected boolean readAloud(Map params, SystemInfo info, Connection conn, HttpServletResponse resp, boolean logEvent) throws Exception {
        String lang = GeneralHtml.getParamStr(params, GeneralHtml.language_, "eng");
        String qid = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
        if (logEvent)
            logUserAction(params, info, conn, StudentSubsystem.readAloud);
//        String fn = dbAccessor_.getQuestionAudioFile(conn,qid, lang);
//        byte[] audio = dbAccessor_.getQuestionAudio(conn, Integer.parseInt(qid), lang);
        String fn = dbAccessor_.getQuestionAudioFile(conn,Integer.parseInt(qid), lang);
        StringBuilder out = new StringBuilder();
//        out.append(audio);
        resp.setContentType("audio/mpeg");
//            response.setContentType("application/octet-stream");
        ServletOutputStream st = resp.getOutputStream();
        // create a buffer of 500K to prevent flushing early ??
        BufferedOutputStream bos = new BufferedOutputStream(st, 1024 * 500);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(info.getSoundPath()+"\\"+fn));
        int bufsize=1024;
        byte[] buf= new byte[bufsize];
        while (bis.read(buf,0,bufsize) != -1)
            bos.write(buf);
        bis.close();
        bos.close();

        return true;
    }

    protected abstract Question evaluatePracticeQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception;

    protected abstract void evaluateSurveyQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception;

    protected abstract void viewQuestion2(Map params, SystemInfo info, Connection conn, ModelEvent e, StringBuffer page, String eventType) throws Exception;


    //create the list of models that will be updated in this controller
    //this is currently hard-coded HERE, might become a database or xml file option?
    protected ArrayList<ModelListener> initModelListeners(Connection conn) throws Exception {
        ArrayList<ModelListener> modelListeners = new ArrayList<ModelListener>();
        ModelListener bsm = new BetaStudentModel();
        Object[] bsmInitObjs = {dbAccessor_, reptAccessor_, conn};
        bsm.init(bsmInitObjs);
        modelListeners.add(bsm);
        return modelListeners;
    }

    protected long getSessElapsedTime(SystemInfo info, long now) {
        long sessStart = ((StudentInfo) info.getUserInfo()).getSessionTS();
        return now - sessStart;

    }

    public void updateModels(ModelEvent e) throws Exception {
        Iterator<ModelListener> modelIter = modelListeners_.iterator();
        while (modelIter.hasNext()) {
            ModelListener curModel = modelIter.next();
            curModel.updateModel(e);
        }
    }

    //get response data for all modules
    protected void loadStudentResponseData(DBAccessor dbAccessor,
                                           SystemInfo info, Connection conn) throws Exception {
        StudentInfo si = (StudentInfo) info.getUserInfo();
        loadStudentResponseData(dbAccessor, si, conn);
    }

    //get response data for all modules
    protected void loadStudentResponseData(DBAccessor dbAccessor,
                                           StudentInfo si, Connection conn) throws Exception {
        //get answered and correct question IDs for each mod
        //the TreeMap has key: mID  val:  Vector of DataTuples: first= qID, second=Correct third=responseTS
        TreeMap modData = new TreeMap();
        dbAccessor.getQuestionResponseDataForEachMod(si.getUserID(), modData, conn);
        si.setModResponseData(modData);
    }


    public Vector<Question> getModuleQuestions(Iterator qIDit, Connection conn) throws Exception {
        Vector<Question> questions = new Vector();
        while (qIDit.hasNext()) {
            String qID = (String) qIDit.next();
            Question q = DbQuestion.loadQuestion(qID, conn);
            questions.add(q);
        }
        return questions;
    }

    public void addModel(ModelListener ml) throws Exception {
        modelListeners_.add(ml);
    }


    public ModelListener getModel(String modelName) throws Exception {
        ModelListener model = null;
        for (int i = 0; i < modelListeners_.size(); i++) {
            ModelListener curModel = modelListeners_.get(i);
            if (curModel.getClass().getSimpleName().equals(modelName))
                model = curModel;
        }
        return model;
    }


    //given a Question object of type mc, load the hints and mc answers
    protected void loadMultipleChoiceQuestion(Question q, SystemInfo info, Connection conn) throws Exception {
        DbQuestion.loadMultipleChoiceQuestionAnswers((MultipleChoiceQuestion) q, conn);
        //MultipleChoiceQuestion q =dbAccessor_.loadMultipleChoiceQuestion(qID, conn);
        //takes a Question arg:
        Hints hints = DbHint.loadAllQuestionHints(q, conn);
        q.setHints(hints);
        //return q;
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

    /**
     * Used only when a question is being displayed the first time
     *
     * @param info
     * @param conn
     * @param curTS
     * @param mID
     * @param m
     * @param selQIndex
     * @param mcq
     * @param userInput
     * @param eventType
     * @throws SQLException
     */
    protected void logViewQuestionEvent(SystemInfo info, Connection conn, long curTS, String mID, QModule m, int selQIndex,
                                        Question mcq, String userInput, String eventType) throws SQLException {
        long sessElapsedTime = getSessElapsedTime(info, curTS);
        dbAccessor_.setUserProblemStartTime(info.getUserID(), new Long(curTS), conn);
        EventLogger.logSimpleEvent(conn, info.getUserID(), mcq.getID(), mID, m.getType(), selQIndex, eventType,
                userInput, new Long(0), sessElapsedTime, mcq.isCorrect());
    }

    protected void logViewSurveyQuestionEvent(SystemInfo info, Connection conn, long curTS, String mID, QModule m, int selQIndex,
                                              Question q, String userInput, String eventType) throws SQLException {
        long sessElapsedTime = getSessElapsedTime(info, curTS);
        dbAccessor_.setUserProblemStartTime(info.getUserID(), new Long(curTS), conn);
        EventLogger.logSimpleEvent(conn, info.getUserID(), q.getID(), mID, m.getType(), selQIndex, eventType,
                userInput, new Long(0), sessElapsedTime, false);
    }

    protected void logViewShortAnswerQuestionEvent(SystemInfo info, Connection conn, long curTS, String mID, QModule m, int selQIndex,
                                                   ShortAnswerQuestion saq, String userInput, String eventType) throws SQLException {
        long sessElapsedTime = getSessElapsedTime(info, curTS);
        dbAccessor_.setUserProblemStartTime(info.getUserID(), new Long(curTS), conn);
        EventLogger.logSimpleEvent(conn, info.getUserID(), saq.getID(), mID, m.getType(), selQIndex, eventType,
                userInput, new Long(0), sessElapsedTime, false);
    }

    /**
     * Typically used when viewing questions or viewing a module (mcq will then be null)
     *
     * @param info
     * @param conn
     * @param curTS
     * @param mID
     * @param m
     * @param selQIndex
     * @param q
     * @param userInput
     * @param eventType
     * @throws SQLException
     */
    protected int logSimpleEvent(SystemInfo info, Connection conn, long curTS, String mID, QModule m, int selQIndex,
                                 Question q, String userInput, String eventType) throws SQLException {
        long sessElapsedTime = getSessElapsedTime(info, curTS);
        Long probStartTime = dbAccessor_.getUserProblemStartTime(info.getUserInfo().getUserID(), conn);
        Long probElapsedTime = probStartTime != null ? curTS - probStartTime : null;
        String qid = (q == null) ? "0" : q.getID();
        boolean isCorrect = (q == null) ? false : q.isCorrect();
        return EventLogger.logSimpleEvent(conn, info.getUserID(), qid, mID, m.getType(), selQIndex, eventType,
                userInput, probElapsedTime, sessElapsedTime, isCorrect);
    }

    protected int logSimpleEvent0(SystemInfo info, Connection conn, long curTS, String mID, QModule m, int selQIndex,
                                  MultipleChoiceQuestion mcq, String userInput, String eventType) throws SQLException {
        long sessElapsedTime = getSessElapsedTime(info, curTS);
        String qid = (mcq == null) ? "0" : mcq.getID();
        boolean isCorrect = (mcq == null) ? false : mcq.isCorrect();
        return EventLogger.logSimpleEvent(conn, info.getUserID(), qid, mID, m.getType(), selQIndex, eventType,
                userInput, new Long(0), sessElapsedTime, isCorrect);
    }


    protected int logHintEvent(SystemInfo info, Connection conn, String mID, String mType, String hID, int qIndex, String eventType,
                               String extActivityUrl, String userID, Question q, Question question, Hint hint, long curTS) throws SQLException {
        Long probStartTime = dbAccessor_.getUserProblemStartTime(userID, conn);
        long sessElapsedTime = getSessElapsedTime(info, curTS);
        Long probElapsedTime = probStartTime != null ? curTS - probStartTime : null;
        return EventLogger.logHintEvent(conn, userID, q.getID(), mID, mType, qIndex, hint.getCoachID(), hID, hint.getLevel(),
                eventType, extActivityUrl, probElapsedTime, sessElapsedTime, question.isCorrect());
    }


    // This method is inherited and sets variables module_ and mcq_ which are subsequently used
    // by the subclass that calls this.
    protected Question viewQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page,
                                String eventType) throws Exception {
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.viewQuestion);
        e.setUserID(info.getUserID());
        long curTS = System.currentTimeMillis();
        e.setTimeStampMillis(curTS);
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID viewQuestionMode.");
            throw ae;
        }
        e.setContentID(mID);
        // loads the module which keeps a list of question IDS ordered by ID
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        loadStudentResponseData(dbAccessor_, info, conn);
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals("")) {
            level = FormalitySubsystem.defaultHintLevel;
        }
        try {
            Question q = module_.getLinkedQuestion(selQIndex);
            e.setItemID(q.getID());
            e.setLevel(Integer.toString(selQIndex));
            e.setContentInfoArray(module_.getLinkedQuestionIDList());
            updateModels(e);
            if (q.isSurvey() && q.getType().equals("mc")) {
                mcq_ = DbQuestion.loadMultipleChoiceSurveyQuestion(q.getID(), conn);
                mcq_.setSelLevel(level);
                module_.setCurrentIndex(selQIndex);
                dbAccessor_.setUserProblemStartTime(info.getUserInfo().getUserID(), new Long(curTS), conn);
                dbAccessor_.insertIntoResponseLog(q.getID(), mID,
                        mType,
                        info.getUserID(), selQIndex,
                        curTS, FormalitySubsystem.questionStartString,
                        false, 0, conn);
                logViewSurveyQuestionEvent(info, conn, curTS, mID, module_, selQIndex, mcq_, null,
                        StudentSubsystem.viewSurveyTestQuestionMode_);

                studentPage_.getMCSurveyQuestionPage(msg_, module_, mcq_, info, page);
                return mcq_;
            } else if (q.getType().equals("mc")) {
                mcq_ = loadMultipleChoiceQuestion(q.getID(), info, conn);
                mcq_.setSelLevel(level);
                module_.setCurrentIndex(selQIndex);
                dbAccessor_.setUserProblemStartTime(info.getUserInfo().getUserID(), new Long(curTS), conn);
                dbAccessor_.insertIntoResponseLog(q.getID(), mID,
                        mType,
                        info.getUserID(), selQIndex,
                        curTS, FormalitySubsystem.questionStartString,
                        false, 0, conn);
                logViewQuestionEvent(info, conn, curTS, mID, module_, selQIndex, (MultipleChoiceQuestion) mcq_,
                        null, StudentSubsystem.viewTestQuestionMode_);

                studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) mcq_, info, hintRenderer_, page);
                return mcq_;
            } else if (q.getType().equals("sa")) {
                ShortAnswerQuestion quest = loadShortAnswerQuestion(q.getID(), conn);
                quest.setSelLevel(level);
                module_.setCurrentIndex(selQIndex);
                dbAccessor_.setUserProblemStartTime(info.getUserInfo().getUserID(), new Long(curTS), conn);
                dbAccessor_.insertIntoResponseLog(quest.getID(), mID,
                        mType,
                        info.getUserID(), selQIndex,
                        curTS, FormalitySubsystem.questionStartString,
                        false, 0, conn);
                logViewShortAnswerQuestionEvent(info, conn, curTS, mID, module_, selQIndex, quest, null,
                        StudentSubsystem.viewTestQuestionMode_);

                studentPage_.getShortAnswerQuestionPage(msg_, module_, quest, info, hintRenderer_, page);
                return quest;
            }
            return null;
        } catch (Exception ex) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Error loading question QModule.getLinkedQuestion. questionID " + module_.getLinkedQuestionID(selQIndex) + " mod index " + selQIndex);
            throw ae;
        }
    }

    protected void viewTestQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page, String eventType) throws Exception {
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.viewTestQuestion);
        e.setUserID(info.getUserID());
        long curTS = System.currentTimeMillis();
        e.setTimeStampMillis(curTS);
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1) {
            throw new Exception("Missing question index in view question mode. ModID " + mID + " UserID " + info.getUserID());
        }
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        Question q = module_.getLinkedQuestionByIndex(selQIndex);
        if (q == null)
            throw new Exception("question index not found in view question mode. ModID " + mID + " UserID " + info.getUserID());
        e.setItemID(q.getID());
        e.setContentID(mID);
        updateModels(e);
        module_.setCurrentIndex(selQIndex);
        if (q.isSurvey() && q.getType().equals("mc")) {
            mcq_ = DbQuestion.loadMultipleChoiceSurveyQuestion(q.getID(), conn);
//            mcq_.setSelLevel(1);
            module_.setCurrentIndex(selQIndex);
            dbAccessor_.setUserProblemStartTime(info.getUserInfo().getUserID(), new Long(curTS), conn);
            dbAccessor_.insertIntoResponseLog(q.getID(), mID,
                    module_.getType(),
                    info.getUserID(), selQIndex,
                    curTS, FormalitySubsystem.questionStartString,
                    false, 0, conn);
            logViewSurveyQuestionEvent(info, conn, curTS, mID, module_, selQIndex, mcq_, null, "survey." + eventType);

            studentPage_.getMCSurveyQuestionPage(msg_, module_, mcq_, info, page);
        } else {
            Question quest = (q.getType().equals("mc")) ? loadMultipleChoiceQuestion(q.getID(), info, conn) : loadShortAnswerQuestion(q.getID(), conn);
            dbAccessor_.insertIntoResponseLog(q.getID(), mID,
                    module_.getType(),
                    info.getUserID(), selQIndex,
                    curTS, FormalitySubsystem.questionStartString,
                    false, 0, conn);
            // DM added event logging
            long sessElapsedTime = getSessElapsedTime(info, curTS);
            logViewQuestionEvent(info, conn, curTS, mID, module_, selQIndex, quest, null, eventType);
            module_.setCurrentIndex(selQIndex);
            if (q.getType().equals("mc"))
                studentPage_.getMCTestQuestionPage(msg_, module_, (MultipleChoiceQuestion) quest, info, false, page);
            else
                studentPage_.getSATestQuestionPage(msg_, module_, (ShortAnswerQuestion) quest, info, false, page);
        }
    }

    /**
     * Logs in the eventLog entries associated with user events such as clicking on a glossary entry, clicking an external
     * activity, or clicking to hear question read aloud
     *
     * @param params
     * @param info
     * @param conn
     * @param eventType
     * @throws Exception
     */
    protected void logUserAction(Map params, SystemInfo info, Connection conn, String eventType) throws Exception {
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
        String qx = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, null);
        int qIndex = -1;
        if (qx != null && !qx.equals(""))
            qIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1); //only provided when question is playing for student (w/in a module)
        String extActivityUrl = GeneralHtml.getParamStr(params, GeneralHtml.externalActivityUrl_, "");
        String glossaryTerm = GeneralHtml.getParamStr(params, GeneralHtml.glossaryTerm_, "");
        String lang = GeneralHtml.getParamStr(params, GeneralHtml.language_, "eng");
        String userID = info.getUserID();
        QModule m = DbModule.loadModule(mID, conn);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals(""))
            level = FormalitySubsystem.defaultHintLevel;//default
        Question q = m.getLinkedQuestion(qIndex);
        if (q.getType().equals("mc")) {
            MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            mcq.setSelLevel(level);
            if (eventType.equals(StudentSubsystem.viewExternalActivity)) {

                if (!hID.equals("")) {
                    Hint hint = mcq.getHints().getHint(hID);
                    if (hint != null) {
                        long curTS = System.currentTimeMillis();
                        mcq.setActiveTag(GeneralHtml.getHighlightTag(mcq.getSelLevel(), hint.getCoachID()));
                        logHintEvent(info, conn, mID, mType, hID, qIndex, eventType, extActivityUrl, userID, q, mcq, hint, curTS);
                    }
                }
            }
            // the activity is a glossaryView
            else if (eventType.equals(StudentSubsystem.viewGlossary) || eventType.equals(StudentSubsystem.readAloud)) {
                if (!hID.equals("")) {
                    Hint hint = mcq.getHints().getHint(hID);
                    // If the glossary entry is in a hint, log it with the info about the hint
                    if (hint != null) {
                        long curTS = System.currentTimeMillis();
                        mcq.setActiveTag(GeneralHtml.getHighlightTag(mcq.getSelLevel(), hint.getCoachID()));
                        logHintEvent(info, conn, mID, mType, hID, qIndex, eventType,
                                glossaryTerm, userID, q, mcq, hint, curTS);
                    }
                }
                // log the glossary entry or the readAloud request in the context of the question
                else logSimpleEvent(info, conn, System.currentTimeMillis(), mID, m, qIndex, mcq,
                        (eventType.equals(StudentSubsystem.viewGlossary) ? glossaryTerm : lang), eventType);
            }
        }
    }

    // N.B.  called by subclasses which then rely on this having set the variables module_ and mcq_
    protected Question viewHint(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.viewHint);
        e.setUserID(info.getUserID());
        e.setTimeStampMillis(System.currentTimeMillis());
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        String mType = GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID viewHintMode.");
            throw ae;
        }
        e.setContentID(mID);
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        loadStudentResponseData(dbAccessor_, info, conn);
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals(""))
            level = FormalitySubsystem.defaultHintLevel;//default
        Question q = module_.getLinkedQuestion(selQIndex);
        e.setItemID(q.getID());
        e.setqIndex(selQIndex);
        e.setContentInfoArray(module_.getLinkedQuestionIDList());
        Question qq = (q.getType().equals("mc")) ? loadMultipleChoiceQuestion(q.getID(), info, conn): loadShortAnswerQuestion(q.getID(),conn);
        qq.setSelLevel(level);
        String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
        if (!hID.equals("")) {
            Hint hint = qq.getHints().getHint(hID);
            if (hint != null) {
                long curTS = System.currentTimeMillis();
                dbAccessor_.initHintLog(hID, q.getID(), info.getUserID(), hint.getCoachID(), mID, mType,
                        Integer.parseInt(hint.getLevel()), 0, curTS, 0, conn);
                qq.setActiveTag(GeneralHtml.getHighlightTag(qq.getSelLevel(), hint.getCoachID()));
                logHintEvent(info, conn, mID, mType, hID, Integer.parseInt(level), StudentSubsystem.viewHintMode_,
                        null, info.getUserID(), q,  qq, hint, curTS);
            }
        }
        qq.setSelHintID(hID);
        e.setHintID(hID);
        updateModels(e);
        module_.setCurrentIndex(selQIndex);
        if (qq.getType().equals("mc"))
            studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) qq, info, hintRenderer_, page);
        else studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, hintRenderer_, page);
        return qq;
    }

    // Displays the question again when the hint level is set back to 1 or if the clear hints button is hit.
    protected void reviewQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page, String eventType, ModelEvent e) throws Exception {
        e.setUserID(info.getUserID());
        e.setTimeStampMillis(System.currentTimeMillis());
        viewQuestion2(params, info, conn, e, page, eventType);
    }

    //get response data for a module
    protected void loadStudentModResponseData(DBAccessor dbAccessor,
                                              SystemInfo info, String modID, Connection conn) throws Exception {
        StudentInfo si = (StudentInfo) info.getUserInfo();
        //get answered and correct question IDs for each mod
        //the TreeMap has key: mID  val:  Vector of DataTuples: first= qID, second=Correct third=responseTS
        TreeMap modData = new TreeMap();
        dbAccessor.getModQuestionResponseData(info.getUserID(), modID, modData, conn);
        si.setModResponseData(modData);
    }

    protected void evaluateSurveyQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page, ModelEvent e) throws Exception {
        e.setUserID(info.getUserID());
        long currentTS = System.currentTimeMillis();
        e.setTimeStampMillis(currentTS);
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        e.setContentID(mID);
        loadStudentModResponseData(dbAccessor_, info, mID, conn);
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals(""))
            level = FormalitySubsystem.defaultHintLevel;
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        Question q = module_.getLinkedQuestion(selQIndex);
        if (q.getType().equals("mc")) {
            mcq_ = new MultiChoiceSurveyQuestion(q);
            DbQuestion.loadMultipleChoiceSurveyQuestionAnswers(mcq_, conn);
            String studAns = dbAccessor_.getSurveyQuestionStudentLastResponse(conn, info.getUserID(), q.getID());
            mcq_.setStudAns(studAns);
            mcq_.setSelLevel(level);
            mcq_.setSubmittedAnswer(subAns);
            e.setItemID(mcq_.getID());
            e.setContentInfo(mcq_.getContentInfoArray());
            e.setResponse(subAns);
            e.setqIndex(selQIndex);

            e.setContentInfoArray(module_.getLinkedQuestionIDList());
            if (subAns == null || subAns.equals("")) {
                msg_ = "Choose an answer choice and click \"Check Answer\"";
                updateModels(e);
                logSimpleEvent(info, conn, currentTS, mID, module_, selQIndex, mcq_, null, StudentSubsystem.evaluateQuestionMode_);

            } else {
                mcq_.setEvaluated(true);
                mcq_.setCorrect(true);
                e.setCorrect(true);
                msg_ = "Thanks for your input.  Please move on to another problem.";
                dbAccessor_.insertIntoResponseLog(mcq_.getID(), mID,
                        module_.getType(),
                        info.getUserID(), selQIndex,
                        currentTS, subAns,
                        true, 0, conn);
                logSimpleEvent(info, conn, currentTS, mID, module_, selQIndex, mcq_, subAns, StudentSubsystem.evaluateSurveyQuestionMode_);
                updateModels(e);
                loadStudentModResponseData(dbAccessor_, info, mID, conn);
            }
            module_.setCurrentIndex(selQIndex);
            studentPage_.getMCSurveyQuestionPage(msg_, module_, mcq_, info, page);
        } else if (type.equals("sa")) {
            ;//
        }
    }

    protected Question evaluatePracticeQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page,
                                            ModelEvent e) throws Exception {

        e.setUserID(info.getUserID());
        long currentTS = System.currentTimeMillis();
        e.setTimeStampMillis(currentTS);
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        e.setContentID(mID);
        loadStudentModResponseData(dbAccessor_, info, mID, conn);
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals(""))
            level = FormalitySubsystem.defaultHintLevel;
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        Question q = module_.getLinkedQuestion(selQIndex);

        Question qq = (q.getType().equals("mc")) ? new MultipleChoiceQuestion(q) : new ShortAnswerQuestion(q);

        if (q.getType().equals("mc"))
            DbQuestion.loadMultipleChoiceQuestionAnswers((MultipleChoiceQuestion) qq, conn);
        else
            DbQuestion.loadShortAnswerQuestionAnswers((ShortAnswerQuestion) qq,conn);
        qq.setHints(DbHint.loadAllHints(qq.getID(), info, conn));
        qq.setSelLevel(level);
        qq.setSubmittedAnswer(subAns);
        e.setItemID(qq.getID());
        e.setContentInfo(qq.getContentInfoArray());
        e.setResponse(subAns);
        e.setqIndex(selQIndex);
        e.setContentInfoArray(module_.getLinkedQuestionIDList());
        if (subAns.equals("")) {
            msg_ = "Choose an answer choice and click \"Check Answer\"";
            updateModels(e);
            logSimpleEvent(info, conn, currentTS, mID, module_, selQIndex, (MultipleChoiceQuestion) qq, null, StudentSubsystem.evaluateQuestionMode_);

        } else {
            if (qq.isSurvey() || qq.gradeAnswer(subAns)) {
                qq.setCorrect(true);
                e.setCorrect(true);
            }
            qq.setEvaluated(true);
            dbAccessor_.insertIntoResponseLog(qq.getID(), mID,
                    module_.getType(),
                    info.getUserID(), selQIndex,
                    currentTS, subAns,
                    qq.isCorrect(), 0, conn);
            logSimpleEvent(info, conn, currentTS, mID, module_, selQIndex, qq, subAns, StudentSubsystem.evaluateQuestionMode_);
            updateModels(e);
            loadStudentModResponseData(dbAccessor_, info, mID, conn);
        }
        module_.setCurrentIndex(selQIndex);
        if (qq.getType().equals("mc"))
            studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) qq, info, hintRenderer_, page);
        else if (!qq.isSurvey())
            studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, hintRenderer_, page);
        else
            studentPage_.getSASurveyQuestionPage(msg_,module_,(ShortAnswerQuestion) qq,info,page);
       return qq;
    }

    protected void evaluateTestQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page,
                                        String eventType) throws Exception {

        boolean isEvalSurveyQuestion = eventType.equals(StudentSubsystem.evaluateSurveyTestQuestionMode_);
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.evaluateTestQuestion);
        e.setUserID(info.getUserID());
        long curTS = System.currentTimeMillis();
        e.setTimeStampMillis(curTS);
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        e.setContentID(mID);
        loadStudentModResponseData(dbAccessor_, info, mID, conn);
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        StudentInfo si = (StudentInfo) info.getUserInfo();
        // selQIndex is the index of the question being answered. This comes from the page.
        // curTestIndex is the index of the last question answered in the module.
        // the selQIndex should be one greater than the curTestIndex. otherwise the student
        // might have used the browser back button.
        // At the beginning of a test mod,  selQIndex = 1, curTestIndex = 0
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1) {
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        }
        int curTestIndex = 0;
        // latestQID is null if no questions have been answered
        String latestQID = si.getLatestQuestionIDAnswered(mID);
        if (latestQID != null)
            curTestIndex = module_.getQuestionIndex(latestQID);
        //if the last answered index is one less, proceed to evaluate- else- display the proper question
        if (curTestIndex == (selQIndex - 1)) {
            Question q = module_.getLinkedQuestion(selQIndex);
            MultiChoiceSurveyQuestion mcq;
            Question qq;
            // its survey question
            if (isEvalSurveyQuestion && q.getType().equals("mc"))
                qq = DbQuestion.loadMultipleChoiceSurveyQuestion(q.getID(), conn);
                // its a regular multi-choice question.
            else if (isEvalSurveyQuestion && q.getType().equals("sa"))
                qq = DbQuestion.loadShortAnswerQuestion(q.getID(), conn);
            else if (q.getType().equals("mc"))
                qq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            else
                qq = DbQuestion.loadShortAnswerQuestion(q.getID(), conn);

            String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
            if (subAns.equals("")) {
                msg_ = "Choose or enter an answer and click \"Check Answer\"";
                module_.setCurrentIndex(selQIndex);
                logSimpleEvent(info, conn, curTS, mID, module_, selQIndex, qq, null, eventType);
                if (isEvalSurveyQuestion && q.getType().equals("mc"))
                    studentPage_.getMCSurveyQuestionPage(msg_, module_, (MultiChoiceSurveyQuestion) qq, info, page);
                else if (isEvalSurveyQuestion && q.getType().equals("sa"))
                    studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, null, page);
                else if (q.getType().equals("mc"))
                    studentPage_.getMCTestQuestionPage(msg_, module_, (MultipleChoiceQuestion) qq, info, false, page);
                else
                    studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, null, page);
            } else {
                e.setItemID(qq.getID());
                e.setContentInfo(qq.getContentInfoArray());
                e.setResponse(subAns);
                //evaluate and record
                // added qq.isSurvey because sa survey questions do not come in with an event indicating that
                // evaluation of survey question is necessary.  We must check the question itself to know
                // whether it is evaluated.
                if (isEvalSurveyQuestion || qq.isSurvey() || qq.gradeAnswer(subAns)) {
                    qq.setCorrect(true);
                    e.setCorrect(true);
                }
                dbAccessor_.insertIntoResponseLog(qq.getID(), mID,
                        module_.getType(),
                        info.getUserID(), selQIndex,
                        curTS, subAns,
                        qq.isCorrect(), 0, conn);
                logSimpleEvent(info, conn, curTS, mID, module_, selQIndex, qq, subAns, eventType);
                boolean completed = (selQIndex == module_.getQuestionCount());
                updateModels(e);
                // if the module is completed, go back to the module summary page.
                if (completed) {
                    //get viewTestModulePage
                    module_.setCompleted(true);
                    loadStudentResponseData(dbAccessor_, info, conn);
                    // log a second event after the user initiated event (eval).  This event is to view the test module
                    // and is marked as system generated.
                    logSimpleEvent(info, conn, curTS, mID, module_, selQIndex, null, null, StudentSubsystem.viewTestModuleMode_ + ".system");
                    studentPage_.getTestModulePage(msg_, module_, info, page);
                }
                // go to the next question in the test module.
                else {
                    //load and view next question
                    selQIndex++;
                    q = module_.getLinkedQuestion(selQIndex);
                    if (q.isSurvey() && q.getType().equals("mc"))
                        qq = DbQuestion.loadMultipleChoiceSurveyQuestion(q.getID(), conn);
                    else if (q.isSurvey() && q.getType().equals("sa"))
                        qq = DbQuestion.loadShortAnswerQuestion(q.getID(), conn);
                    else if (q.getType().equals("mc"))
                        qq = loadMultipleChoiceQuestion(q.getID(), info, conn);
                    else
                        qq = DbQuestion.loadShortAnswerQuestion(q.getID(), conn);
                    curTS = System.currentTimeMillis();
                    dbAccessor_.insertIntoResponseLog(qq.getID(), mID,
                            module_.getType(),
                            info.getUserID(), selQIndex,
                            curTS, FormalitySubsystem.questionStartString,
                            qq.isCorrect(), 0, conn);

                    module_.setCurrentIndex(selQIndex);
                    // log the event to see a question but append "system" onto event type because the user-generated event
                    // was evalquestion.   Viewing the next question happens automatically
                    if (q.isSurvey()) {
                        logViewSurveyQuestionEvent(info, conn, curTS, mID, module_, selQIndex, qq, null, StudentSubsystem.viewSurveyTestQuestionMode_ + ".system");
                        if (q.getType().equals("mc"))
                            studentPage_.getMCSurveyQuestionPage(msg_, module_, (MultiChoiceSurveyQuestion) qq, info, page);
                        else  
                            studentPage_.getSASurveyQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, null, page);
                    } else {
                        logViewQuestionEvent(info, conn, curTS, mID, module_, selQIndex, qq, null, StudentSubsystem.viewTestQuestionMode_ + ".system");
                        if (q.getType().equals("mc"))
                            studentPage_.getMCTestQuestionPage(msg_, module_, (MultipleChoiceQuestion) qq, info, false, page);
                        else {
                            studentPage_.getSATestQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, false, page);

                        }
                    }
                }
            }//end the answer string was not blank
        }// else- the back button was used
        else {
            curTestIndex = Math.max(1, curTestIndex);
            curTestIndex++;
            int ix = module_.getQuestions().size();
            curTestIndex = Math.min(curTestIndex+1,ix);

            Question q = module_.getLinkedQuestion(curTestIndex);
            Question qq;
            msg_ = "Please do not use the \"Back\" or \"Forward\" buttons on your browser.";
            module_.setCurrentIndex(curTestIndex);
            if (isEvalSurveyQuestion) {
                if (q.getType().equals("mc")) {
                    qq = DbQuestion.loadMultipleChoiceSurveyQuestion(q.getID(), conn);
                    studentPage_.getMCSurveyQuestionPage(msg_, module_, (MultiChoiceSurveyQuestion) qq, info, page);
                } else {
                    qq = DbQuestion.loadShortAnswerQuestion(q.getID(), conn);
                    studentPage_.getSASurveyQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, page);
                }
            } else {
                if (q.getType().equals("mc")) {
                    qq = loadMultipleChoiceQuestion(q.getID(), info, conn);
                    studentPage_.getMCTestQuestionPage(msg_, module_, (MultipleChoiceQuestion) qq, info, false, page);
                } else {
                    qq = DbQuestion.loadShortAnswerQuestion(q.getID(), conn);
                    studentPage_.getSATestQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, false, page);
                }
            }
        }
    }

    protected void studentHome(SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.studentHome);
        e.setUserID(info.getUserID());
        e.setTimeStampMillis(System.currentTimeMillis());
        contentTable_ = new BaseContentTable();
        String state = info.getUserInfo().getState();
        BaseStateParser parser = new BaseStateParser();
        parser.parseState(state); // does nothing other than set it to pretest mode or trim the state string
        contentTable_.setState(parser);
        atAccessor_.setStudentState(info.getUserID(), parser.getDBStateStr(), conn);
        StudentInfo si = (StudentInfo) info.getUserInfo();
        loadStudentResponseData(dbAccessor_, si, conn);
        contentTable_.setContent(getContentVector(info, parser, conn));
        updateModels(e);
        long sessElapsedTime = getSessElapsedTime(info, System.currentTimeMillis());
        EventLogger.logHomeEvent(conn, info.getUserID(), sessElapsedTime, StudentSubsystem.studentHomeMode_);
        studentPage_.getStudentCourseHomePage(contentTable_, null, info, page);
    }


    protected void viewModule(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.viewModule);
        e.setUserID(info.getUserID());
        e.setTimeStampMillis(System.currentTimeMillis());
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        e.setContentID(mID);
        QModule m = DbModule.loadModule(mID, conn);
        // adjust the order of questions to either be the fixed order defined by author
        // or a random ordering if this is the first time this module has been opened by the student.
        m.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        loadStudentResponseData(dbAccessor_, info, conn);
        e.setContentInfoArray(m.getLinkedQuestionIDList());
        updateModels(e);
        logSimpleEvent(info, conn, System.currentTimeMillis(), mID, m, 0, null, null, StudentSubsystem.viewModuleMode_);
        studentPage_.getModulePage(msg_, m, info, null, page);
    }

    /**
     * Handle the event coming in when user clicks on button to start up a pretest/posttest/test module or when
     * they click the exit button during a test.
     *
     * @param params
     * @param info
     * @param conn
     * @param page
     * @throws Exception
     */
    protected void viewTestModule(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.viewTestModule);
        e.setUserID(info.getUserID());
        e.setTimeStampMillis(System.currentTimeMillis());
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        loadStudentResponseData(dbAccessor_, info, conn);
        StudentInfo si = (StudentInfo) info.getUserInfo();
        //for a test we know they have been presented inm order
        int numAnswered = si.getDistinctNumAnsweredInModule(mID);
        boolean complete = (numAnswered == module_.getQuestionCount());
        module_.setCompleted(complete);
        //the question index is one-based
        module_.setCurrentIndex(numAnswered + 1);
        updateModels(e);
        logSimpleEvent(info, conn, System.currentTimeMillis(), mID, module_, 0, null, null, StudentSubsystem.viewTestModuleMode_);
        studentPage_.getTestModulePage(msg_, module_, info, page);
    }


    // load the content, calculate state, place content into the Vector
    // also: the pre and post test modIDs are set into the student info object
    protected List<List<QModule>> getContentVector(SystemInfo info, BaseStateParser parser,
                                                   Connection conn) throws Exception {
        String courseID = info.getUserInfo().getCourseID();
        //String studentID = info.getUserID();
        StudentInfo si = (StudentInfo) info.getUserInfo();
        //module vectors may be empty, but not null
        List<QModule> ptMods = DbModule.loadActiveModulesOfType(courseID, "PreTest", conn);
        List<QModule> pMods = DbModule.loadActiveModulesOfType(courseID, "Practice", conn);
        List<QModule> tMods = DbModule.loadActiveModulesOfType(courseID, "Test", conn);
        List<QModule> pstMods = DbModule.loadActiveModulesOfType(courseID, "PostTest", conn);
        si.setPreTestModData(ptMods);
        si.setPostTestModData(pstMods);
        boolean allPreTestModsComplete = si.isPreTestCompleted();
        boolean allPracticeModsComplete = areModsCompleted(pMods, si);
        boolean allTestModsComplete = areModsCompleted(tMods, si);
        boolean allPostTestModsComplete = si.isPostTestCompleted();
        boolean isPostTest = parser.showPostTest();
        if (ptMods.size() > 0 && !allPreTestModsComplete)
            parser.setPt();
        else
            parser.setP();
        if (allPracticeModsComplete && allTestModsComplete) {
            if (isPostTest) {
                if (allPostTestModsComplete)
                    parser.setFin();
                else
                    parser.setPst();
            } else {
                if (pstMods.size() > 0)
                    parser.setS();
                else if (pMods.size() == 0)
                    ;
                else
                    parser.setFin();
            }
        } else if (allPracticeModsComplete &&
                (allTestModsComplete || tMods.size() == 0) &&
                allPreTestModsComplete &&
                pstMods.size() > 0 &&
                !allPostTestModsComplete) {
            parser.setPst();
        }

        List<List<QModule>> content = new ArrayList<List<QModule>>();
        content.add(ptMods);
        content.add(pMods);
        content.add(tMods);
        content.add(pstMods);
        return content;
    }

    protected boolean areModsCompleted(List<QModule> mods, StudentInfo si) {
        if (mods == null || mods.size() == 0)
            return true;
        boolean allCompleted = true;
        for (QModule m : mods) {
            if (si.isCompletedModule(m.getID(), m.getQuestionCount()))
                m.setCompleted(true);
            else
                allCompleted = false;
        }
        return allCompleted;
    }


}
