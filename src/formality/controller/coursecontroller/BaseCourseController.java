package formality.controller.coursecontroller;

import formality.content.*;
import formality.content.database.mysql.DbModule;
import formality.controller.FormalitySubsystem;
import formality.controller.StudentSubsystem;
import formality.html.GeneralHtml;
import formality.model.ModelEvent;
import formality.systemerror.AuthoringException;

import java.sql.Connection;
import java.util.Map;

/**
 * <p>Title: BaseCourseController</p>
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class BaseCourseController extends CourseController {


    public BaseCourseController() {
    }


    protected Question evaluatePracticeQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        ModelEvent e = new ModelEvent(ModelEvent.EventTypes.evaluateQuestion);
        return super.evaluatePracticeQuestion(params, info, conn, page, e);
    }

    protected void evaluateSurveyQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        ModelEvent e = null;
        if (subAns.equals("")) {
            msg_ = "Choose an answer choice and click \"Check Answer\"";
            e = new ModelEvent(ModelEvent.EventTypes.noResponse);
        } else e = new ModelEvent(ModelEvent.EventTypes.evaluateSurveyQuestion);
        super.evaluateSurveyQuestion(params, info, conn, page, e);
    }


    /**
     * Displays a question but presumably not for the first time because this doesn't put an entry in the response log
     *
     * @param params
     * @param info
     * @param conn
     * @param e
     * @param page
     * @param eventType
     * @throws Exception
     */
    protected void viewQuestion2(Map params, SystemInfo info,
                                 Connection conn, ModelEvent e, StringBuffer page, String eventType) throws Exception {
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        module_ = DbModule.loadModule(mID, conn);
        module_.adjustQuestionOrder(conn, info.getUserID(), dbAccessor_);
        loadStudentResponseData(dbAccessor_, info, conn);
        String selHintId = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, null);
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals(""))
            level = FormalitySubsystem.defaultHintLevel;//default
        Question q = module_.getLinkedQuestion(selQIndex);
        e.setItemID(q.getID());
        e.setContentID(mID);
        e.setqIndex(selQIndex);
        e.setContentInfo(q.getContentInfoArray());
        updateModels(e);

        Question qq = (q.getType().equals("mc")) ? loadMultipleChoiceQuestion(q.getID(), info, conn) : loadShortAnswerQuestion(q.getID(), conn);
        qq.setSelLevel(level);
        qq.setSelHintID(selHintId);
        module_.setCurrentIndex(selQIndex);
        if (q.getType().equals("mc"))
            studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) qq, info, hintRenderer_, page);
        else
            studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, hintRenderer_, page);
        if (eventType.equals(StudentSubsystem.selectLevelMode_) && selHintId != null) {
            Hint h = dbAccessor_.getHint(conn, selHintId);
            logHintEvent(info, conn, mID, module_.getType(), selHintId, selQIndex, eventType, null, info.getUserID(), q, qq, h, System.currentTimeMillis());
        } else {
            int eventID = logSimpleEvent(info, conn, System.currentTimeMillis(), mID, module_, selQIndex, qq, null, eventType);
            if (eventType.equals(StudentSubsystem.selectLevelMode_))
                dbAccessor_.updateEventHintLevel(conn, eventID, level);
        }

    }


}
