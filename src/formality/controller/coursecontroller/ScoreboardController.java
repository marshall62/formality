package formality.controller.coursecontroller;

import formality.content.*;
import formality.content.database.mysql.DbModule;
import formality.controller.FormalitySubsystem;
import formality.controller.StudentSubsystem;
import formality.html.GeneralHtml;
import formality.model.BetaStudentModel;
import formality.model.ModelEvent;
import formality.model.ModelListener;
import formality.model.modeldata.MotivationalModelData;
import formality.model.motivation.MotivationalModel;
import formality.systemerror.AuthoringException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 15, 2010
 * Time: 4:12:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class ScoreboardController extends CourseController {


    boolean useScoreboard_;
    MotivationalModel scoreboard_ ;

    public ScoreboardController() {
        useScoreboard_=true;
        scoreboard_ = new MotivationalModel();

    }

    protected void evaluateSurveyQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        ModelEvent e=null;
        if (subAns.equals("")) {
            msg_ = "Choose an answer choice and click \"Check Answer\"";
            e = new ModelEvent(ModelEvent.EventTypes.noResponse);
        }
        else e = new ModelEvent(ModelEvent.EventTypes.evaluateSurveyQuestion);
        super.evaluateSurveyQuestion(params,info,conn,page,e);
        // overwrite the page produced by default and build one with a scoreboard
        page.delete(0,page.length());
        MotivationalModelData mdata = (MotivationalModelData) scoreboard_.getModelData();
        studentPage_.getMCSurveyQuestionPage(msg_, module_, mcq_, info, mdata, page);
    }


    protected Question evaluatePracticeQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
        ModelEvent e=null;
        if (subAns.equals("")) {
            msg_ = "Choose an answer choice and click \"Check Answer\"";
            e = new ModelEvent(ModelEvent.EventTypes.noResponse);
        }
        else e = new ModelEvent(ModelEvent.EventTypes.evaluateQuestion);
        Question q = super.evaluatePracticeQuestion(params,info,conn,page,e);
        // overwrite the page produced by default and build one with a scoreboard
        page.delete(0,page.length());
        MotivationalModelData mdata = (MotivationalModelData) scoreboard_.getModelData();

//        studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) mcq_, info, mdata, hintRenderer_, page);

        if (q.getType().equals("mc"))
            studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) q, info, mdata,hintRenderer_, page);
        else if (!q.isSurvey())
            studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) q, info, mdata,hintRenderer_, page);
        else
            studentPage_.getSASurveyQuestionPage(msg_,module_,(ShortAnswerQuestion) q,info,mdata,page);


        return q;
    }

    protected Question viewHint(Map params, SystemInfo info, Connection conn, StringBuffer page) throws Exception {
        Question q = super.viewHint(params,info,conn,page);
        if (useScoreboard_) {
            // wipe out the page built in viewHint and make a new one with a scoreboard
            page.delete(0,page.length());
            MotivationalModelData mdata = (MotivationalModelData) scoreboard_.getModelData();
//            studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion)mcq_, info, mdata, hintRenderer_, page);

            if (q.getType().equals("mc"))
               studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) q, info, mdata,hintRenderer_, page);
            else studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) q, info, mdata,hintRenderer_, page);
        }
        return q;
    }

    protected Question viewQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page, String eventType) throws Exception {
        Question q = super.viewQuestion(params,info,conn,page, StudentSubsystem.viewQuestionMode_);
        if (useScoreboard_) {
            // wipe out what viewQuestion wrote into the page
            page.delete(0,page.length());
            MotivationalModelData mdata = (MotivationalModelData) scoreboard_.getModelData();
//            if (mcq_ instanceof MultipleChoiceQuestion)
//                studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) mcq_, info, mdata, hintRenderer_, page);
//            else
//                studentPage_.getMCSurveyQuestionPage(msg_,module_,mcq_,info,page);


            if (q.isSurvey() && q.getType().equals("mc"))
                studentPage_.getMCSurveyQuestionPage(msg_, module_, (MultiChoiceSurveyQuestion) q, info, mdata, page);
            else if (q.getType().equals("mc"))
                studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) q, info, mdata, hintRenderer_, page);
            else if (q.getType().equals("sa"))
                studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) q, info, mdata, hintRenderer_, page);
        }
        return q;
    }


    protected void viewQuestion2(Map params, SystemInfo info,
                                Connection conn, ModelEvent e, StringBuffer page, String eventType) throws Exception {
        String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        module_ = DbModule.loadModule(mID, conn);
         module_.adjustQuestionOrder(conn,info.getUserID(), dbAccessor_);
        loadStudentResponseData(dbAccessor_, info, conn);
        int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
        if (selQIndex < 1)
            throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
        String selHintId = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_,null);
        String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
        if (level.equals(""))
            level = FormalitySubsystem.defaultHintLevel;//default
        Question q = module_.getLinkedQuestion(selQIndex);
        e.setItemID(q.getID());
        e.setContentID(mID);
        e.setqIndex(selQIndex);
        e.setContentInfo(q.getContentInfoArray());
        e.setContentInfoArray(module_.getLinkedQuestionIDList());
        updateModels(e);
        Question qq=null;
        if (q.getType().equals("mc")) {
            qq = loadMultipleChoiceQuestion(q.getID(), info, conn);
            qq.setSelLevel(level);
            module_.setCurrentIndex(selQIndex);
            if (useScoreboard_) {
                MotivationalModelData mdata = (MotivationalModelData) scoreboard_.getModelData();
                studentPage_.getMCQuestionPage(msg_, module_,  (MultipleChoiceQuestion) qq, info, mdata, hintRenderer_, page);
            } else
                studentPage_.getMCQuestionPage(msg_, module_, (MultipleChoiceQuestion) qq, info, hintRenderer_, page);
        } else if (q.getType().equals("sa")) {
            qq = loadShortAnswerQuestion(q.getID(),conn);
            qq.setSelLevel(level);
            module_.setCurrentIndex(selQIndex);
            if (useScoreboard_) {
                MotivationalModelData mdata = (MotivationalModelData) scoreboard_.getModelData();
                studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, mdata, hintRenderer_, page);
            }
            else
               studentPage_.getShortAnswerQuestionPage(msg_, module_, (ShortAnswerQuestion) qq, info, hintRenderer_, page);
        }

        if (eventType.equals(StudentSubsystem.selectLevelMode_) && selHintId != null) {
                Hint h = dbAccessor_.getHint(conn,selHintId);
                logHintEvent(info,conn,mID,module_.getType(),selHintId,selQIndex,eventType,null,info.getUserID(),q,qq,h,System.currentTimeMillis());
            }
            else {
                int eventID = logSimpleEvent(info, conn, System.currentTimeMillis(), mID, module_, selQIndex, qq, null, eventType);
                if (eventType.equals(StudentSubsystem.selectLevelMode_))
                    dbAccessor_.updateEventHintLevel(conn,eventID,level);
            }
    }




    //create the list of models that will be updated in this controller
    //this is currently hard-coded HERE, might become a database or xml file option?
    protected ArrayList<ModelListener> initModelListeners(Connection conn) throws Exception {
        ArrayList<ModelListener> modelListeners = new ArrayList<ModelListener>();
        if (useScoreboard_) {
            Object[] scoreboardInitObjs = {dbAccessor_, conn};
            scoreboard_.init(scoreboardInitObjs);
            modelListeners.add(scoreboard_);
        }
        ModelListener bsm = new BetaStudentModel();
        Object[] bsmInitObjs = {dbAccessor_, reptAccessor_, conn};
        bsm.init(bsmInitObjs);
        modelListeners.add(bsm);
        return modelListeners;
    }

/*        This code is the old way of handling evaluateQuestionMode_
            long currentTS = System.currentTimeMillis();
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            loadStudentModResponseData(dbAccessor_, info, mID, conn);
            int selQIndex = GeneralHtml.getParamInt(params, GeneralHtml.questionIndex_, -1);
            if (selQIndex < 1)
                throw new Exception("missing question index in evaluateTestQuestionMode moduleID: " + mID);
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals(""))
                level = FormalitySubsystem.defaultHintLevel;

            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            QModule module = dbAccessor_.loadModule2(mID, conn);
            Question q = module.getLinkedQuestion(selQIndex);
            Vector linkedQs = getModuleQuestions(module.getLinkedQuestionIDs(), conn);
            module.setLinkedQuestions(linkedQs);
            if (q.getType().equals("mc")) {
                MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(q);
                dbAccessor_.loadMultipleChoiceQuestionAnswers(mcq, conn);
                mcq.setHints(dbAccessor_.loadAllHints(mcq.getID(), info, conn));
                mcq.setSelLevel(level);
                mcq.setSubmittedAnswer(subAns);
                if (subAns.equals("")) {
                    msg_ = "Choose an answer choice and click \"Check Answer\"";
                    e = new ModelEvent(ModelEvent.EventTypes.noResponse);

                } else {
                    e = new ModelEvent(ModelEvent.EventTypes.evaluatePracticeQuestion);
                    if (mcq.getCorrectAnswer().equalsIgnoreCase(subAns)) {
                        mcq.setCorrect(true);
                        e.setCorrect(true);
                    }
                    mcq.setEvaluated(true);
                    dbAccessor_.insertIntoResponseLog(mcq.getID(), mID,
                            module.getType(),
                            info.getUserID(), selQIndex,
                            currentTS, subAns,
                            mcq.isCorrect(), 0, conn);
                    loadStudentModResponseData(dbAccessor_, info, mID, conn);
                }
                e.setTimeStampMillis(currentTS);
                e.setContentID(mID);
                e.setUserID(info.getUserID());
                e.setItemID(mcq.getID());
                e.setContentInfo(mcq.getContentInfoArray());
                e.setResponse(subAns);
                e.setqIndex(selQIndex);
                e.setContentInfoArray(module.getLinkedQuestionIDVec());
                updateModels(e);
                module.setCurrentIndex(selQIndex);
                if (useScoreboard_) {
                    MotivationalModelData mdata = (MotivationalModelData) scoreboard_.getModelData();
                    studentPage_.getMCQuestionPage(msg_, module, mcq, info, mdata, hintRenderer_, page);
                } else
                    studentPage_.getMCQuestionPage(msg_, module, mcq, info, hintRenderer_, page);
            } else if (type.equals("sa")) {
                ;//
            }
            */



}
