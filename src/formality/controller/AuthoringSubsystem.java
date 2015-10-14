package formality.controller;

import java.util.TreeMap;

import formality.content.*;
import formality.html.*;
import formality.html.auth.*;
import formality.html.hints.BaseHintRenderer;
import formality.html.hints.HintRenderer2;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DbModule;
import formality.content.database.mysql.DbQuestion;
import formality.content.database.mysql.DbHint;
import formality.content.database.mysql.DbCourse;
import formality.content.database.mysql.DbUser;
import formality.systemerror.AuthoringException;
import formality.model.UserInfo;
import formality.Util.DataTuple;
import formality.admin.StudentUser;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p/>                                                                                m
 * <p>Description: </p>
 */
public class AuthoringSubsystem {

    private static Logger logger = Logger.getLogger(AuthoringSubsystem.class);
    //author modes
    public static final String authorHomeMode_ = "authhome";
    public static final String newQuestionMode_ = "newq";
    public static final String newSurveyQuestionMode_ = "newSurveyq";
    public static final String saveNewQuestionMode_ = "savenewq";
    public static final String saveNewSurveyQuestionMode_ = "savenewSurveyq";
    public static final String saveQuestionMode_ = "saveq";
    public static final String saveSurveyQuestionMode_ = "saveSurveyq";
    public static final String viewQuestionMode_ = "viewq";
    public static final String clearQuestionMode_ = "clearq";
    public static final String viewSurveyQuestionMode_ = "viewSurveyq";
    public static final String viewActiveQuestionMode_ = "viewaq";
    public static final String evaluateQuestionMode_ = "evalq";
    public static final String evaluateSurveyQuestionMode_ = "evalSurveyq";
    public static final String editQuestionMode_ = "editq";
    public static final String editSurveyQuestionMode_ = "editSurveyq";
    public static final String deleteQuestionMode_ = "delq";
    public static final String deleteSurveyQuestionMode_ = "delSurveyq";
    public static final String selectQuestionHintMode_ = "selqhint";
    public static final String linkQuestionHintMode_ = "linkqhint";
    public static final String unlinkQuestionHintMode_ = "unlinkqhint";
    public static final String editQuestionHintMode_ = "editqhint";
    public static final String selectQuestionMode_ = "selq";
    public static final String selectSurveyQuestionMode_ = "selSurveyq";
    public static final String newModuleMode_ = "newmod";
    public static final String selectModuleMode_ = "selmod";
    public static final String saveNewModuleMode_ = "savenewmod";
    public static final String saveClonedModuleMode_ = "saveclonedmod";
    public static final String editModuleMode_ = "editmod";
    public static final String cloneModuleMode_ = "clonemod";
    public static final String saveModuleMode_ = "savemod";
    public static final String deleteModuleMode_ = "delmod";
    public static final String viewModuleMode_ = "viewmod";
    public static final String viewActiveModuleMode_ = "viewactmod";
    public static final String evalModuleMode_ = "evalmod";
    public static final String linkModuleQuestionMode_ = "linkmodq";
    public static final String unlinkModuleQuestionMode_ = "unlinkmodq";
    public static final String selectHintMode_ = "selhint";
    public static final String editHintMode_ = "edithint";
    public static final String saveHintMode_ = "savehint";
    public static final String saveNewHintMode_ = "savenewhint";
    public static final String deleteHintMode_ = "delhint";
    public static final String selectModModuleMode_ = "selmodmod";
    public static final String linkModModuleMode_ = "linkmodmod";
    public static final String unlinkModModuleMode_ = "unlinkmodmod";
    public static final String questionInventoryMode_ = "qinv";
    public static final String moveupModuleQuestionMode_= "moveUPModQ";
    public static final String movedownModuleQuestionMode_= "moveDOWNModQ";
    public static final String editClassModuleLinksMode_= "editClassModLinks";
    public static final String viewSourceModuleLinkMode_ = "viewClassSourceModules";
    public static final String selClassSourceModuleLinkMode_= "selClassSourceModule";
    public static final String viewClassParentModuleLinkMode_ = "viewClassParentModule";
    public static final String selClassParentModuleLinkMode_ = "selClassParentModule";
    public static final String deleteClassModuleLinkMode_ = "delClassModuleLink";
    public static final String selectClassMode_ = "selectClass";
    public static final String removeClassModuleMode_ = "removeClassModule";
    public static final String selClassModuleMode_ = "selClassModule";
    public static final String addClassModulesMode_ = "addClassModules";
    public static String newClassMode_ = "createNewClass";
    public static String saveNewClassMode_ = "saveNewClass";
    public static String newTeacherMode_ = "createTeacherUser";
    public static String saveNewTeacherMode_ = "saveNewTeacherUser";
    public static String newStudentMode_ = "createStudentUser";
    public static String saveNewStudentMode_ = "saveNewStudentUser";
    public static String saveEditedStudentMode_ = "saveEditedStudentUser";
    public static String newStudentsUpload_ = "createStudentsFromFile";
    public static String saveNewStudentsUpload_ = "saveNewStudentsFromFile";
    public static String classListOperations_ = "classListOps";
    public static String editStudentMode_ = "editStudent";
    public static String deleteStudentMode_ = "deleteStudent";
    public static String clearStudentDataMode_ = "clearStudentData";
    public static String studId = "studId";
    public static final String readAloudMode = "readAloud";

    DBAccessor dbAccessor_;
    AuthorHtml authorPage_;
    MultipleChoiceQuestionPage mcQPage_;
    BaseHintRenderer hintRenderer_;
    private String mID,qID, msg_;


    public AuthoringSubsystem() {
        dbAccessor_ = new DBAccessor();
    }

    public boolean handleRequest(Map params, SystemInfo info,
                                 Connection conn, StringBuffer page, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String mode = GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "");
        dbAccessor_ = new DBAccessor();
        //      dbAccessor_.loadSystemInfo(info, conn);
        loadUserCourseData(dbAccessor_, info.getUserInfo(), conn);
        authorPage_ = new AuthorHtml();
        hintRenderer_ = new HintRenderer2();
        boolean ok = false;
        String msg_ = "";

        if (mode.equals(authorHomeMode_)) {
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            List<QModule> mods = DbModule.loadModules(conn);
            authorPage_.getAuthorHomePage("", info, page);
        }

        // question modes
        else if (mode.equals(selectQuestionMode_)) {
            String cID = GeneralHtml.getParamStr(params, GeneralHtml.categoryID_, "");
            String cName = GeneralHtml.getParamStr(params, GeneralHtml.categoryName_, "");
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            StringBuffer paramStr = new StringBuffer();
            GeneralHtml.addToLink(GeneralHtml.categoryID_, cID, paramStr);
            GeneralHtml.addToLink(GeneralHtml.categoryName_, cName, paramStr);
            GeneralHtml.addToLink(GeneralHtml.moduleID_, mID, paramStr);
            List<Question> cq = null;
            boolean isSurveyQ = cName.equals(Question.SURVEY_QUESTION);
            if (mID.equals(""))
                cq = DbQuestion.getAllQuestionsInCategory(cID, conn);
            else
                cq = DbQuestion.getAllQuestionsInCategory(mID, cID, conn);
            authorPage_.getSelectQuestionPage(cName, cq, info,
                    mID, paramStr.toString(), page, isSurveyQ);
        } else if (mode.equals(newQuestionMode_)) {
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            if (type.equals("mc"))
                AuthMCQuestionPage.getNewMultiChoiceQuestionPage(null,null, info, page, conn);
            else if (type.equals("sa"))
                AuthSAQuestionPage.getNewShortAnswerQuestionPage(null, null, info, page, conn);
        }
        else if (mode.equals(newSurveyQuestionMode_)) {
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            if (type.equals("mc"))
                AuthSurveyQuestionPage.getNewMultiChoiceSurveyQuestionPage(null,null, info, page);
            else if (type.equals("sa"))
                AuthSAQuestionPage.getNewShortAnswerSurveyQuestionPage(null, null, info, page);
        }
        else if (mode.equals(saveNewQuestionMode_)) {
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            if (type.equals("mc"))
                saveNewMultiChoiceQuestion(params, conn, info, page);
            else if (type.equals("sa"))
                saveNewShortAnswerQuestion(params, conn, info, page);
        }
        else if (mode.equals(saveNewSurveyQuestionMode_)) {
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            if (type.equals("mc"))
                saveNewMultiChoiceSurveyQuestion(params, conn, info, page);
            else if (type.equals("sa"))
                saveNewShortAnswerSurveyQuestion(params, conn, info, page);
        }
        else if (mode.equals(saveQuestionMode_)) {
            ok = false;
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            Question q = (type.equals("mc")) ? getMultipleChoiceQuestion(params) : getShortAnswerQuestion(params);
            q.setID(GeneralHtml.getParamStr(params, GeneralHtml.questionID_, ""));
            Hint h = getNewHint(params);
            try {
                if (type.equals("mc"))
                    validateMultipleChoiceQuestion( (MultipleChoiceQuestion) q);
                else validateShortAnswerQuestion((ShortAnswerQuestion) q);
                int hVal = validateNewHint(h);
                if (type.equals("mc"))
                    DbQuestion.saveMultipleChoiceQuestion((MultipleChoiceQuestion) q, conn);
                else
                    DbQuestion.saveShortAnswerQuestion((ShortAnswerQuestion) q,conn);
                if (hVal == 1) {
                    int hID = DbHint.saveNewHint(h, conn);
                    DbHint.linkNewHintToQuestion(q.getID(),
                            Integer.toString(hID), conn);
                }
                msg_ = "Question edits were saved successfully.";
                ok = true;
            } catch (Exception e) {
                //  if(e.)
                msg_ = "Question edits were not saved. " + e.getMessage();
                ok = false;
            }
            if (!ok && type.equals("mc"))
                AuthMCQuestionPage.getEditMultiChoiceQuestionPage((MultipleChoiceQuestion) q, null, info, msg_, page, conn);
            else if (!ok && type.equals("sa"))
                AuthSAQuestionPage.getEditShortAnswerQuestionPage((ShortAnswerQuestion) q, null, info, msg_, page, conn);
            else if (type.equals("mc")) {
                q = loadMultipleChoiceQuestion(q.getID(), conn);
                AuthMCQuestionPage.getEditMultiChoiceQuestionPage((MultipleChoiceQuestion)q, null, info, msg_, page, conn);
            }
            else if (type.equals("sa")) {
                q = loadShortAnswerQuestion(q.getID(), conn);
                AuthSAQuestionPage.getEditShortAnswerQuestionPage((ShortAnswerQuestion) q,null,info,"Question edits were saved successfully.",page, conn);
            }
        }

        else if (mode.equals(saveSurveyQuestionMode_)) {
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            ok = false;
            Question q = (type.equals("mc")) ? getMultipleChoiceQuestion(params) : getShortAnswerQuestion(params);
            try {
                if (type.equals("mc")) {
                    validateMultipleChoiceSurveyQuestion((MultiChoiceSurveyQuestion) q);
                    DbQuestion.saveMultipleChoiceSurveyQuestion((MultiChoiceSurveyQuestion) q, conn);
                }
                else {
                    DbQuestion.saveShortAnswerSurveyQuestion((ShortAnswerQuestion) q,conn);
                }
                msg_ = "Survey Question edits were saved successfully.";
                ok = true;
            } catch (Exception e) {
                //  if(e.)
                msg_ = "Survey Question edits were not saved. " + e.getMessage();
                ok = false;
            }
            if (!ok && type.equals("mc"))
                AuthSurveyQuestionPage.getEditMultiChoiceSurveyQuestionPage((MultiChoiceSurveyQuestion) q, info, msg_, page);
            else if (!ok)
                AuthSAQuestionPage.getEditShortAnswerSurveyQuestionPage((ShortAnswerQuestion) q,info,msg_,page);
            else if (type.equals("mc")) {
                q = loadMultipleChoiceSurveyQuestion(q.getID(), info, conn);
                AuthSurveyQuestionPage.getEditMultiChoiceSurveyQuestionPage((MultiChoiceSurveyQuestion) q, info, msg_, page);
            }
            else {
                q = new ShortAnswerQuestion(q);
                q = DbQuestion.loadQuestion(q,conn);
                AuthSAQuestionPage.getEditShortAnswerSurveyQuestionPage((ShortAnswerQuestion) q, info, msg_, page);
            }
        } else if (mode.equals(editQuestionMode_)) {
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "mc");
            if (type.equals("mc")) {
                MultipleChoiceQuestion q = loadMultipleChoiceQuestion(qID, conn);
                AuthMCQuestionPage.getEditMultiChoiceQuestionPage(q, null, info, msg_, page, conn);
            }
            else if (type.equals("sa")) {
                ShortAnswerQuestion q = loadShortAnswerQuestion(qID, conn);
                AuthSAQuestionPage.getEditShortAnswerQuestionPage(q,null,info,"",page, conn);
            }
        }
        else if (mode.equals(editSurveyQuestionMode_)) {
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            String type = GeneralHtml.getParamStr(params,GeneralHtml.questionType_,"mc");
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            if (type.equals("mc")) {
                MultiChoiceSurveyQuestion q = loadMultipleChoiceSurveyQuestion(qID, info, conn);
                AuthSurveyQuestionPage.getEditMultiChoiceSurveyQuestionPage(q, info, msg_, page);
            }
            else {
                ShortAnswerQuestion q = DbQuestion.loadShortAnswerQuestion(qID,  conn);
                AuthSAQuestionPage.getEditShortAnswerSurveyQuestionPage(q, info, "", page);
            }
        }
        else if (mode.equals(deleteSurveyQuestionMode_)) {
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            StringBuffer msg = new StringBuffer();
            if (type.equals("sa")) {
                DbQuestion.expungeSAQuestion(qID,conn,msg);
                TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
                info.setCatCounts(cts);
                List<QModule> mods = DbModule.loadModules(conn);
                authorPage_.getAuthorHomePage(msg.toString(), info, page);
            }
        }
        else if (mode.equals(deleteQuestionMode_)) {
            msg_ = "";
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            try {
                StringBuffer msg = new StringBuffer();
                DbQuestion.expungeQuestion(qID, conn, msg);
                msg_ = msg.toString();
                ok = true;
            } catch (Exception e) {
                msg_ = "Question was not deleted. ";//+e.getMessage();
                ok = false;
            }
            if (!ok) {
                MultipleChoiceQuestion q = loadMultipleChoiceQuestion(qID, conn);
                AuthMCQuestionPage.getEditMultiChoiceQuestionPage(q, null, info, msg_, page, conn);
            } else {
                TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
                info.setCatCounts(cts);
                List<QModule> mods = DbModule.loadModules(conn);
                authorPage_.getAuthorHomePage(msg_, info, page);
            }
        }
        else if (mode.equals(viewQuestionMode_) || mode.equals(viewSurveyQuestionMode_)) {
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            String cID = GeneralHtml.getParamStr(params, GeneralHtml.categoryID_, "");
            String cName = GeneralHtml.getParamStr(params, GeneralHtml.categoryName_, "");
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            StringBuffer paramStr = null;
            if (cID.length() > 0 || cName.length() > 4) {
                paramStr = new StringBuffer();
                GeneralHtml.addToLink(GeneralHtml.categoryID_, cID, paramStr);
                GeneralHtml.addToLink(GeneralHtml.categoryName_, cName, paramStr);
                GeneralHtml.addToLink(GeneralHtml.moduleID_, mID, paramStr);
            }
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals("")) {
                level = FormalitySubsystem.defaultHintLevel;//default
            }
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            if (mode.equals(viewQuestionMode_) && type.equals("mc")) {
                MultipleChoiceQuestion q = loadMultipleChoiceQuestion(qID, conn);
                q.setSelLevel(level);
                mcQPage_ = new MultipleChoiceQuestionPage();
                String pstr = null;
                if (paramStr != null)
                    pstr = paramStr.toString();
                mcQPage_.getAuthorQuestionPage(q, info, null, pstr, hintRenderer_, page, null);
            }
            else if (mode.equals(viewSurveyQuestionMode_) && type.equals("mc")) {
                MultiChoiceSurveyQuestion q = loadMultipleChoiceSurveyQuestion(qID, info, conn);
                q.setSelLevel(level);
                MultipleChoiceSurveyQuestionPage mcSQPage = new MultipleChoiceSurveyQuestionPage();
                String pstr = null;
                if (paramStr != null)
                    pstr = paramStr.toString();
                mcSQPage.getAuthorSurveyQuestionPage(q, info, null, pstr, page);
            }
            else if (type.equals("sa")) {
                ShortAnswerQuestion quest = loadShortAnswerQuestion(qID, conn);
                quest.setSelLevel(level);
                ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();
                String pstr = null;
                if (paramStr != null)
                    pstr = paramStr.toString();
                pg.getAuthorQuestionPage(quest, info, null, pstr, hintRenderer_, page, null);
            }
        } else if (mode.equals(viewActiveQuestionMode_)) {
            String cID = GeneralHtml.getParamStr(params, GeneralHtml.categoryID_, "");
            String cName = GeneralHtml.getParamStr(params, GeneralHtml.categoryName_, "");
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            StringBuffer paramStr = new StringBuffer();
            GeneralHtml.addToLink(GeneralHtml.categoryID_, cID, paramStr);
            GeneralHtml.addToLink(GeneralHtml.categoryName_, cName, paramStr);
            GeneralHtml.addToLink(GeneralHtml.moduleID_, mID, paramStr);
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing selected level.");
                throw ae;
            }
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            if (type.equals("mc")) {
                MultipleChoiceQuestion q = loadMultipleChoiceQuestion(qID, conn);
                String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
                q.setSelHintID(hID);
                q.setSelLevel(level);
                q.setSubmittedAnswer(GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, ""));
                if (!hID.equals("")) {
                    Hint h = q.getHints().getHint(hID);
                    if (h != null)
                        q.setActiveTag(GeneralHtml.getHighlightTag(q.getSelLevel(), h.getCoachID()));
                }
                mcQPage_ = new MultipleChoiceQuestionPage();
                mcQPage_.getAuthorQuestionPage(q, info, null, paramStr.toString(), hintRenderer_, page, hID);
            } else if (type.equals("sa")) {
                ShortAnswerQuestion q = loadShortAnswerQuestion(qID, conn);
                String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
                q.setSelHintID(hID);
                q.setSelLevel(level);
                q.setSubmittedAnswer(GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, ""));
                if (!hID.equals("")) {
                    Hint h = q.getHints().getHint(hID);
                    if (h != null)
                        q.setActiveTag(GeneralHtml.getHighlightTag(q.getSelLevel(), h.getCoachID()));
                }
                mcQPage_ = new ShortAnswerQuestionPage();
                String pstr = null;
                if (paramStr != null)
                    pstr = paramStr.toString();
                mcQPage_.getAuthorQuestionPage(q, info, null, pstr, hintRenderer_, page, null);

            }
        } else if (mode.equals(evaluateQuestionMode_)) {
            String msg = null;
            String cID = GeneralHtml.getParamStr(params, GeneralHtml.categoryID_, "");
            String cName = GeneralHtml.getParamStr(params, GeneralHtml.categoryName_, "");
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            StringBuffer paramStr = new StringBuffer();
            GeneralHtml.addToLink(GeneralHtml.categoryID_, cID, paramStr);
            GeneralHtml.addToLink(GeneralHtml.categoryName_, cName, paramStr);
            GeneralHtml.addToLink(GeneralHtml.moduleID_, mID, paramStr);
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing selected level.");
                throw ae;
            }
            String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            if (type.equals("mc") || type.equals("sa")) {
                Question q = (type.equals("mc")) ? loadMultipleChoiceQuestion(qID, conn) : DbQuestion.loadShortAnswerQuestion(qID,conn);
                q.setSelLevel(level);
                String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
                if (!hID.equals("")) {
                    Hint h = q.getHints().getHint(hID);
                    if (h != null)
                        q.setActiveTag(GeneralHtml.getHighlightTag(q.getSelLevel(), h.getCoachID()));
                }
                q.setSelHintID(hID);
                q.setSubmittedAnswer(subAns);
                if (subAns.equals("")) {
                    msg = "You need to fill in an answer before clicking \"Check Answer\"";
                } else {
                    //evaluate
                    if (q.gradeAnswer(subAns.trim()))
                        q.setCorrect(true);
                    q.setEvaluated(true);
                }
                if (type.equals("mc")) {
                    mcQPage_ = new MultipleChoiceQuestionPage();
                    mcQPage_.getAuthorQuestionPage(q, info, msg, paramStr.toString(), hintRenderer_, page, hID);
                }
                else {
                   ShortAnswerQuestionPage p = new ShortAnswerQuestionPage();
                   p.getAuthorQuestionPage(q,info,msg,paramStr.toString(),hintRenderer_,page,hID);
                }
            }
        }

        //hint modes
        else if (mode.equals(selectHintMode_)) {
            msg_ = "";
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, null);
            Question q= DbQuestion.loadQuestion(qID,conn);
            List<Hint> allHints = DbHint.loadAllHintsWithQLinks(qID, info, conn);
            authorPage_.getSelectHintPage(msg_, allHints, info, page, q);
        } else if (mode.equals(editHintMode_)) {
            String hID = GeneralHtml.getParamStr(params, GeneralHtml.hintID_, "");
            if (hID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing hintID.");
                throw ae;
            }
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            Hint h = DbHint.loadHint(hID, info, conn);
            List<String> qIDs = DbQuestion.getQuestionsLinkedToHint(h.getID(), conn);
            h.setQuestionLinks(qIDs);
            authorPage_.getEditHintPage(h, info, qID, msg_, page);
        } else if (mode.equals(linkQuestionHintMode_)) {
            msg_ = "";
            String hID = GeneralHtml.getParamStr(params, GeneralHtml.hintID_, "");
            if (hID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing hintID.");
                throw ae;
            }
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            try {
                DbHint.linkHintToQuestion(qID, hID, conn);
                msg_ = "Hint linked successfully";
            } catch (Exception e) {
                msg_ = "Hint was not linked. " + e.getMessage();
            }
            String type = GeneralHtml.getParamStr(params,GeneralHtml.questionType_,"mc");
            Question q = (type.equals("mc"))? loadMultipleChoiceQuestion(qID, conn) : loadShortAnswerQuestion(qID,conn);
            if (type.equals("mc"))
                AuthMCQuestionPage.getEditMultiChoiceQuestionPage((MultipleChoiceQuestion) q, null, info, msg_, page, conn);
            else AuthSAQuestionPage.getEditShortAnswerQuestionPage((ShortAnswerQuestion) q, null,info,msg_,page, conn);
        } else if (mode.equals(unlinkQuestionHintMode_)) {
            msg_ = "";
            String hID = GeneralHtml.getParamStr(params, GeneralHtml.hintID_, "");
            if (hID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing hintID.");
                throw ae;
            }
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            Question quest = DbQuestion.loadQuestion(qID,conn);
            if (qID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing questionID.");
                throw ae;
            }
            try {
                DbHint.deleteHintQuestionLink(qID, hID, conn);
                msg_ = "Link deleted successfully";
            } catch (Exception e) {
                msg_ = "Link was not deleted. " + e.getMessage();
            }
            if (quest.getType().equals("mc")) {
                MultipleChoiceQuestion q = loadMultipleChoiceQuestion(qID, conn);
                AuthMCQuestionPage.getEditMultiChoiceQuestionPage(q, null, info, msg_, page, conn);
            }
            else {
                ShortAnswerQuestion q = loadShortAnswerQuestion(qID, conn);
                AuthSAQuestionPage.getEditShortAnswerQuestionPage(q, null, info, msg_, page, conn);
            }
        } else if (mode.equals(saveHintMode_)) {
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            Hint h = getHint(params);
            try {
                validateHint(h);
                DbHint.saveHint(h, conn);
                msg_ = "Hint edits were saved successfully.";
            } catch (Exception e) {
                msg_ = "Hint edits were not saved. " + e.getMessage();
            }
            h = DbHint.loadHint(h.getID(), info, conn);
            authorPage_.getEditHintPage(h, info, qID, msg_, page);
        } else if (mode.equals(deleteHintMode_)) {
            String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, null);
            msg_ = "";
            String hID = GeneralHtml.getParamStr(params, GeneralHtml.hintID_, "");
            if (hID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing hintID.");
                throw ae;
            }
            try {
                DbHint.deleteHint(hID, conn);
                msg_ = "Hint successfully deleted";
                if (qID == null) { //select hint
                    Question q= DbQuestion.loadQuestion(qID,conn);
                    List<Hint> allHints = DbHint.loadAllHintsWithQLinks(null, info, conn);
                    authorPage_.getSelectHintPage(msg_, allHints, info, page, q);
                } else { //edit question
                    MultipleChoiceQuestion q = loadMultipleChoiceQuestion(qID, conn);
                    AuthMCQuestionPage.getEditMultiChoiceQuestionPage(q, null, info, msg_, page, conn);
                }
            } catch (Exception e) {
                msg_ = "Hint was not deleted. " + e.getMessage();
                Hint h = DbHint.loadHint(hID, info, conn);
                List<String> qIDs = DbQuestion.getQuestionsLinkedToHint(h.getID(), conn);
                h.setQuestionLinks(qIDs);
                authorPage_.getEditHintPage(h, info, qID, msg_, page);
            }
        } else if (mode.equals(saveNewHintMode_)) {
            // int id = saveNewHint(params, conn);
            //MultipleChoiceQuestion q=loadMultipleChoiceQuestion(Integer.toString(id), conn);
            // authorPage_.writeEditQuestionPage(q, info);
        }


// module modes
        else if (mode.equals(newModuleMode_)) {
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            authorPage_.getNewModulePage(null, info, cInfo, page);
        } else if (mode.equals(selectModuleMode_)) {
            List<QModule> mods = DbModule.loadModulesWithQIDs(conn);
            authorPage_.getSelectModulePage(null, mods, info, page);
        } else if (mode.equals(saveNewModuleMode_)) {
            String msg = "";
            int id = -1;
            String userID = info.getUserLogin();
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            QModule m = getNewModule(params);
            m.setAuthorID(userID);
            try {
                validateNewModule(m);
                id = DbModule.saveNewModule(m, conn);
                msg = "New module was saved successfully.";
            } catch (Exception e) {
                msg = "New module was not saved. " + e.getMessage();
            }
            if (id < 0) {  //mod not saved
                authorPage_.getNewModulePage(m, msg, info, cInfo, page);
            } else {
                m = DbModule.loadModule(Integer.toString(id), conn);
                Vector selIDs = getSelectedCourseIDs(params, cInfo);
                setNewModCourseLinks(selIDs, m.getID(), conn);
                TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
                info.setCatCounts(cts);
                Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
                authorPage_.getEditModulePage(msg, m, info, cInfo, cIDs, page);
            }
        }
        else if (mode.equals(saveClonedModuleMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            QModule m = DbModule.loadModule(mID, conn);
            m.setName(GeneralHtml.getParamStr(params, GeneralHtml.moduleName_, ""));
            m.setStatus(false);
            m.setType(GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, ""));
            DbModule.saveModule(m,conn);
             TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        }
        else if (mode.equals(saveModuleMode_)) {
            ok = false;
            QModule m = getModule(params);
            // if the questions are sequenced as FIXED, make sure the modquestionlinks table
            // has a valid fixed ordering.
            if (m.getQuestionSequencing() == QModule.OrderType.fixed)
                DbModule.verifyFixedQuestionSequence(conn,DbModule.loadModule(m.getID(),conn));
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            try {
                validateModule(m);
                DbModule.saveModule(m, conn);
                Vector selIDs = getSelectedCourseIDs(params, cInfo);
                setModCourseLinks(selIDs, cIDs, m.getID(), conn);
                msg_ = "Module edits were saved successfully.";
                ok = true;
            } catch (Exception e) {
                msg_ = "Module edits were not saved. " + e.getMessage();
                ok = false;
            }
            m = DbModule.loadModule(m.getID(), conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        } else if (mode.equals(editModuleMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(null, m, info, cInfo, cIDs, page);
        } else if (mode.equals(selectModModuleMode_)) {
            String cmID = GeneralHtml.getParamStr(params, GeneralHtml.childModID_, "");
            if (cmID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing child ModuleID.");
                throw ae;
            }
            List<QModule> mods = DbModule.loadModulesWithQIDs(cmID, conn);
            authorPage_.getSelectModModuleLinkPage(null, cmID, mods, info, page);
        }
        else if (mode.equals(cloneModuleMode_)) {
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            QModule m = DbModule.cloneModule(mID, conn);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage("Module has been cloned.  Change it's name and add it to classes.", m, info, cInfo, cIDs, page);
        }
        else if (mode.equals(linkModModuleMode_)) {
            msg_ = "";
            String cmID = GeneralHtml.getParamStr(params, GeneralHtml.childModID_, "");
            if (cmID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing child ModuleID.");
                throw ae;
            }
            String pmID = GeneralHtml.getParamStr(params, GeneralHtml.parentModID_, "");
            if (pmID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing parent ModuleID.");
                throw ae;
            }
            try {
                DbModule.addModModuleLink(cmID, pmID, conn);
                msg_ = "Module linked successfully";
            } catch (Exception e) {
                msg_ = "Module was not linked. " + e.getMessage();
            }
            QModule m = DbModule.loadModule(cmID, conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        } else if (mode.equals(unlinkModModuleMode_)) {
            msg_ = "";
            String cmID = GeneralHtml.getParamStr(params, GeneralHtml.childModID_, "");
            if (cmID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing child ModuleID.");
                throw ae;
            }
            String pmID = GeneralHtml.getParamStr(params, GeneralHtml.parentModID_, "");
            if (pmID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing parent ModuleID.");
                throw ae;
            }
            try {
                DbModule.deleteModModuleLink(pmID, cmID, conn);
                msg_ = "Link deleted successfully";
            } catch (Exception e) {
                msg_ = "Link was not deleted. " + e.getMessage();
            }
            QModule m = DbModule.loadModule(cmID, conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        } else if (mode.equals(unlinkModuleQuestionMode_)) {
            checkModAndQuestion(params);
            try {
                DbQuestion.deleteQuestionToModuleLink(qID, mID, conn);
                msg_ = "Link deleted successfully";
            } catch (Exception e) {
                msg_ = "Link was not deleted. " + e.getMessage();
            }
            QModule m = DbModule.loadModule(mID, conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        } else if (mode.equals(linkModuleQuestionMode_)) {
            checkModAndQuestion(params);
            try {
                DbQuestion.linkQuestionToModule(qID, mID, conn);
                msg_ = "Question linked successfully";
            } catch (Exception e) {
                msg_ = "Question was not linked. " + e.getMessage();
            }
            QModule m = DbModule.loadModule(mID, conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        }
        else if (mode.equals(moveupModuleQuestionMode_)) {
            msg_="";
            checkModAndQuestion(params);
            QModule m = DbModule.loadModule(mID, conn);
            if (m.getQuestionSequencing() != QModule.OrderType.fixed)
                msg_ = "Cannot perform move operations.  You must set the ordering sequence of problems to be: Fixed";
            else
                // need to modify ModuleQuestionSequence to move up/down the given question.  Must
                // send a userId of -1 to indicate that this is a fixed sequence designed by an author.
                alterQuestionOrder(m,qID,-1, conn);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        }
         else if (mode.equals(movedownModuleQuestionMode_)) {
            msg_="";
            checkModAndQuestion(params);
            QModule m = DbModule.loadModule(mID, conn);
            if (m.getQuestionSequencing() != QModule.OrderType.fixed)
                msg_ = "Cannot perform move operations.  You must set the ordering sequence of problems to be: Fixed";    
            else
            // need to modify ModuleQuestionSequence to move up/down the given question.  Must
            // send a userId of -1 to indicate that this is a fixed sequence designed by an author.
            alterQuestionOrder(m,qID,1, conn);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
        }
        else if (mode.equals(deleteModuleMode_)) {
            msg_ = "";
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
//            /*
            try{
              DbModule.deleteModule(mID, conn);
              msg_="Module " + mID+ " deleted successfully.";
              DbModule.deleteModCourseLinks(mID, conn);
              ok=true;
            }catch(Exception e){
                msg_="Module was not deleted. "+e.getMessage();
                ok=false;
            }
//            */
               if(!ok){
            QModule m = DbModule.loadModule(mID, conn);
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            Vector cInfo = dbAccessor_.getAllCourses(conn);
            Vector cIDs = dbAccessor_.getCourseIDsForModule(m.getID(), conn);
            authorPage_.getEditModulePage(msg_, m, info, cInfo, cIDs, page);
             }
//            /*
            else{
              List<QModule> mods=DbModule.loadModulesWithQIDs(conn);
              authorPage_.getSelectModulePage(msg_, mods, info, page);
            }
//            */
        } else if (mode.equals(viewModuleMode_)) {
            msg_ = "";
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            authorPage_.getModulePage(msg_, m, info, page);
        } else if (mode.equals(viewActiveModuleMode_)) {
            msg_ = "";
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
            if (selQIndex.equals(""))
                selQIndex = "1";
            int qIndex = Integer.parseInt(selQIndex);
            // String qID = module.getLinkedQuestionID(qIndex);//GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            // if(qID.equals("")){ //present the first question
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals("")) {
                level = FormalitySubsystem.defaultHintLevel;//default
            }
            Question q = m.getLinkedQuestion(qIndex);
            if (!q.isSurvey() && q.getType().equals("mc")) {
                MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), conn);
                mcq.setSelLevel(level);
                String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
                if (!hID.equals("")) {
                    Hint h = mcq.getHints().getHint(hID);
                    if (h != null)
                        mcq.setActiveTag(GeneralHtml.getHighlightTag(mcq.getSelLevel(), h.getCoachID()));
                }
                mcq.setSelHintID(hID);
                m.setCurrentIndex(qIndex);
                authorPage_.getModuleMCQuestionPage(msg_, m, mcq, info, hintRenderer_, page);
            }
            else if (q.isSurvey() && q.getType().equals("mc")) {
                MultiChoiceSurveyQuestion mcq = loadMultipleChoiceSurveyQuestion(q.getID(), info, conn);
                mcq.setSelLevel(level);
                m.setCurrentIndex(qIndex);
                authorPage_.getModuleMCSurveyQuestionPage(msg_, m, mcq, info,  page);
            }
            else if (q.getType().equals("sa")) {
                ShortAnswerQuestion sq = loadShortAnswerQuestion(q.getID(),conn);
                sq.setSelLevel(level);
                m.setCurrentIndex(qIndex);
                if (sq.isSurvey())
                    authorPage_.getModuleSASurveyQuestionPage(msg_, m, sq, info,  page);
                else
                    authorPage_.getModuleSAQuestionPage(msg_,m,sq,info,hintRenderer_,page);
//                ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();
//                pg.getAuthorQuestionPage(sq,info,"",null,hintRenderer_,page,null);
           }
        } else if (mode.equals(evalModuleMode_)) {
            String msg = null;
            String mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            if (mID.equals("")) {
                AuthoringException ae = new AuthoringException();
                ae.setMessage("Missing moduleID.");
                throw ae;
            }
            QModule m = DbModule.loadModule(mID, conn);
            String selQIndex = GeneralHtml.getParamStr(params, GeneralHtml.questionIndex_, "");
            if (selQIndex.equals(""))
                selQIndex = "1";
            int qIndex = Integer.parseInt(selQIndex);
            String level = GeneralHtml.getParamStr(params, GeneralHtml.selLevel_, "");
            if (level.equals("")) {
                level = FormalitySubsystem.defaultHintLevel;//default
            }
            String subAns = GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, "");
            String type = GeneralHtml.getParamStr(params, GeneralHtml.questionType_, "");
            Question q = m.getLinkedQuestion(qIndex);
            if (q.getType().equals("mc")) {
                MultipleChoiceQuestion mcq = loadMultipleChoiceQuestion(q.getID(), conn);
                mcq.setSelLevel(level);
                String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
                if (!hID.equals("")) {
                    Hint h = mcq.getHints().getHint(hID);
                    if (h != null)
                        mcq.setActiveTag(GeneralHtml.getHighlightTag(mcq.getSelLevel(), h.getCoachID()));
                }
                mcq.setSelHintID(hID);
                mcq.setSubmittedAnswer(subAns);
                if (subAns.equals("")) {
                    msg = "Choose an answer choice and click \"Check Answer\"";
                } else {
                    //evaluate
                    if (mcq.getCorrectAnswer().equalsIgnoreCase(subAns))
                        mcq.setCorrect(true);
                    mcq.setEvaluated(true);
                }
                m.setCurrentIndex(qIndex);
                authorPage_.getModuleMCQuestionPage(msg_, m, mcq, info, hintRenderer_, page);
            } else if (q.getType().equals("sa")) {
                ShortAnswerQuestion sq = loadShortAnswerQuestion(q.getID(),conn);
                sq.setSelLevel(level);
                String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");
                if (!hID.equals("")) {
                    Hint h = sq.getHints().getHint(hID);
                    if (h != null)
                        sq.setActiveTag(GeneralHtml.getHighlightTag(sq.getSelLevel(), h.getCoachID()));
                }
                sq.setSelHintID(hID);
                sq.setSubmittedAnswer(subAns);
                if (subAns.equals("")) {
                    msg = "Choose an answer choice and click \"Check Answer\"";
                } else {
                     if (sq.gradeAnswer(subAns))
                        sq.setCorrect(true);
                    sq.setEvaluated(true);
                }
                 m.setCurrentIndex(qIndex);
                authorPage_.getModuleSAQuestionPage(msg_, m, sq, info, hintRenderer_, page);
            }
        } else if (mode.equals(questionInventoryMode_)) {
            Vector data = getQuestionInventory(conn);
            authorPage_.getQuestionInventoryPage(data, info, page);
        }
        else if (mode.equals(editClassModuleLinksMode_)) {
            String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            Course course = DBAccessor.getCourse(classID,conn);
//            List<DataTuple> allMods = DbModule.loadActiveModules(classID,conn);
            List<ModModuleLink> links = DbModule.getClassModuleLinks(classID,conn);
//            Set<QModule> mods = DbModule.getClassModules(classID,conn);
            List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
            authorPage_.getEditClassModuleLinkagePage(conn, classMods, course,info,page, null, links);
        }
        else if (mode.equals(selClassModuleMode_)) {
            String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            Course course = DBAccessor.getCourse(classID,conn);
            List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
            List<String> modIds = new ArrayList<String>();
            for (QModule m: classMods)
                modIds.add((String)m.getID());
            List<QModule> mods = DbModule.loadModulesWithQIDsExcluding(conn,modIds);
            authorPage_.getClassModulePage(conn, mods, course,info,page, null);
        }
        else if (mode.equals(viewSourceModuleLinkMode_)) {
            String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            Course course = DBAccessor.getCourse(classID,conn);
//            List<QModule> mods = DbModule.loadModulesWithQIDs(conn);
            List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
            authorPage_.getClassSourceModuleLinkagePage(conn, classMods, course,info,page, null);
        }
        else if (mode.equals(selClassSourceModuleLinkMode_)) {
            String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            String childModId = GeneralHtml.getParamStr(params, GeneralHtml.childModID_, "");
            Course course = DBAccessor.getCourse(classID,conn);
            QModule cmod = DbModule.loadModule(childModId,conn);
//            List<QModule> mods = DbModule.loadModulesWithQIDs(childModId,conn);
            List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
            authorPage_.getClassParentModuleLinkagePage(conn, classMods, course,info,page, cmod);
        }
        else if (mode.equals(selClassParentModuleLinkMode_)) {
             String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            String parentModId = GeneralHtml.getParamStr(params, GeneralHtml.parentModID_, "");
            String childModId = GeneralHtml.getParamStr(params, GeneralHtml.childModID_, "");
            DbModule.addCourseModModuleLink(classID,parentModId,childModId,conn);
            Course course = DBAccessor.getCourse(classID,conn);
            // TODO should add error check here to make sure a link is not duplicated
            // TODO should add error check here to make sure circular linkages are not created.
            List<ModModuleLink> links = DbModule.getClassModuleLinks(classID,conn);
            List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
//            List<DataTuple> allMods= DbModule.loadActiveModules(classID,conn);
//            Set<QModule> mods = DbModule.getClassModules(classID,conn);
            authorPage_.getEditClassModuleLinkagePage(conn, classMods, course,info,page,
                    "Link beween " + childModId + " and " + parentModId + " has been added.", links);
        }
        else if (mode.equals(deleteClassModuleLinkMode_)) {
            String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            Course course = DBAccessor.getCourse(classID,conn);
            String parentModId = GeneralHtml.getParamStr(params, GeneralHtml.parentModID_, "");
            String childModId = GeneralHtml.getParamStr(params, GeneralHtml.childModID_, "");
            DbModule.deleteCourseModModuleLink(classID,parentModId,childModId,conn);
            List<ModModuleLink> links = DbModule.getClassModuleLinks(classID,conn);
//            List<DataTuple> allMods= DbModule.loadActiveModules(classID,conn);
            List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
//            Set<QModule> mods = DbModule.getClassModules(classID,conn);
            authorPage_.getEditClassModuleLinkagePage(conn, classMods, course,info,page, "Module link between " +parentModId+ " and " + childModId + " deleted.", links);
        }
        else if (mode.equals(selectClassMode_))  {
            // get all courses.
            List<Course> courses = DBAccessor.getCourses(conn);

            authorPage_.getClassTable(courses,page,info,null);
        }

        else if (mode.equals(removeClassModuleMode_)) {
            String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            String modID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
            Course course = DBAccessor.getCourse(classID,conn);
            if (!DbModule.inClassModuleDependency(classID,modID,conn)) { 
                DbModule.deleteModCourseLink(modID,classID,conn);
                msg_ = "Module " +modID+  " removed from class.";
            }
            else msg_ = "Cannot remove module " + modID + " because it is part of a dependency (see below). First remove the dependency.";
            List<ModModuleLink> links = DbModule.getClassModuleLinks(classID,conn);
//            List<DataTuple> allMods= DbModule.loadActiveModules(classID,conn);
//            Set<QModule> mods = DbModule.getClassModules(classID,conn);
            List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
            authorPage_.getEditClassModuleLinkagePage(conn, classMods, course,info,page, msg_, links);
        }
        else if (mode.equals(addClassModulesMode_)) {
            String classID = GeneralHtml.getParamStr(params, GeneralHtml.classID_, "");
            List<String> modIds = GeneralHtml.getParamStrings(params,GeneralHtml.includeModule_);
            DbModule.addCourseModules(classID,modIds,conn);
            Course course = DBAccessor.getCourse(classID,conn);
//            List<DataTuple> allMods = DbModule.loadActiveModules(classID,conn);
            List<ModModuleLink> links = DbModule.getClassModuleLinks(classID,conn);
//            Set<QModule> mods = DbModule.getClassModules(classID,conn);
             List<QModule> classMods= DbModule.loadActiveModuleObjects(classID,conn);
            authorPage_.getEditClassModuleLinkagePage(conn, classMods, course,info,page, "Modules added.", links);

        }
        else if (mode.equals(newClassMode_)) {
            new CreateNewClassPage(conn).createPage("",info,null,page);
        }
        else if (mode.equals(saveNewClassMode_)) {
            String name =  GeneralHtml.getParamStr(params, GeneralHtml.courseName_, "");
            int teacherId =  GeneralHtml.getParamInt(params, GeneralHtml.teacherID_, -1);
            String controller =  GeneralHtml.getParamStr(params, GeneralHtml.courseController_, "");
            int district =  GeneralHtml.getParamInt(params, GeneralHtml.district_, 1);
            String institution =  GeneralHtml.getParamStr(params, GeneralHtml.institution_, "");
            Course c = DbCourse.createCourse(conn,name,institution,controller,district);
            DbCourse.linkTeacherCourse(conn,Integer.parseInt(c.getCourseID()),teacherId);
            c.setTeacherId(Integer.toString(teacherId));
            new CreateNewClassPage(conn).createPage("Course created",info,c,page);
        }
         else if (mode.equals(newTeacherMode_)) {
            new CreateNewTeacherPage(conn).createPage("",info,null,page);
        }
        else if (mode.equals(saveNewTeacherMode_)) {
            String fname =  GeneralHtml.getParamStr(params, GeneralHtml.fname, "");
            String lname =  GeneralHtml.getParamStr(params, GeneralHtml.lname, "");
            String login =  GeneralHtml.getParamStr(params, GeneralHtml.uname, "");
            String pwd =  GeneralHtml.getParamStr(params, GeneralHtml.password, "");
            int district =  GeneralHtml.getParamInt(params, GeneralHtml.district_, 1);
            String institution =  GeneralHtml.getParamStr(params, GeneralHtml.institution_, "");
            UserInfo ui = new UserInfo();
            ui.setUserFname(fname);
            ui.setUserLname(lname);
            ui.setLogin(login);
            ui.setUserPwd(pwd);
            ui.setDistrict(Integer.toString(district));
            ui.setInstitution(institution);
            DbUser.createTeacher(conn,ui);
            new CreateNewTeacherPage(conn).createPage("Teacher created",info,ui,page);

        }
        else if (mode.equals(readAloudMode)) {
            return FormalitySubsystem.readAloud(params,info,conn,resp);
        }
         else if (mode.equals(newStudentMode_)) {
            List<Course> courses = DbCourse.getAllCourses(conn);
            new CreateNewStudentPage(conn).createPage("",info,null,courses,null,page, -1);
        }
        else if (mode.equals(saveNewStudentMode_) || mode.equals(saveEditedStudentMode_)) {
            String id = GeneralHtml.getParamStr(params, GeneralHtml.studentID_, "-1");
            String uname =  GeneralHtml.getParamStr(params, GeneralHtml.uname, "");
            String initials =  GeneralHtml.getParamStr(params, GeneralHtml.initials, "");
            // TODO need to error check age to make sure its an int
            String age =  GeneralHtml.getParamStr(params, GeneralHtml.age, null);
            String pwd =  GeneralHtml.getParamStr(params, GeneralHtml.password, "");
            String gender =  GeneralHtml.getParamStr(params, GeneralHtml.gender, "");
            String lang =  GeneralHtml.getParamStr(params, GeneralHtml.language_, "");
            String iep =  GeneralHtml.getParamStr(params, GeneralHtml.iep, null);
            String readingLev =  GeneralHtml.getParamStr(params, GeneralHtml.readingLevel, null);
            String mathLev =  GeneralHtml.getParamStr(params, GeneralHtml.mathLevel, null);
            String title =  GeneralHtml.getParamStr(params, GeneralHtml.title, null);
            int courseId =  GeneralHtml.getParamInt(params, GeneralHtml.classID_, 1);
            Course course= DbCourse.getCourse(conn,courseId);
            UserInfo ui = new UserInfo();
            ui.setUserID(id);
            ui.setInitals(initials);
            ui.setAge(age != null && !age.equals("") ? Integer.parseInt(age) : 0);
            ui.setLogin(uname);
            ui.setUserPwd(pwd);
            ui.setGender(gender);
            ui.setLanguage(lang);
            ui.setIep(iep);
            ui.setReadingLev(readingLev);
            ui.setMathLev(mathLev);
            ui.setTitle(title);
            ui.setDistrict(Integer.toString(course.getDistrict()));
            ui.setInstitution(course.getInstitution());
            List<Course> courses = DbCourse.getAllCourses(conn);
            if (mode.equals(saveNewStudentMode_)) {
                DbUser.createStudent(conn,ui);
                DbCourse.addUserToCourse(conn,Integer.parseInt(ui.getUserID()),courseId);
                new CreateNewStudentPage(conn).createPage("Student created",info,ui,courses,course,page, courseId);
            }
            else {
                DbUser.updateStudent(conn,ui);
                DbCourse.updateUserCourse(conn,Integer.parseInt(ui.getUserID()),courseId);
                new CreateNewStudentPage(conn).createPage("Student modified",info,ui,courses,course,page, courseId);
            }


        }
        else if (mode.equals(newStudentsUpload_)) {
            List<Course> courses = DbCourse.getAllCourses(conn);
            new CreateNewStudentPage(conn).createFileUploadPage("",info,courses,page);
        }
        else if (mode.equals(saveNewStudentsUpload_)) {
            int courseId =  GeneralHtml.getParamInt(params, GeneralHtml.classID_, 1);
            List<Course> courses = DbCourse.getAllCourses(conn);
            Course course = DbCourse.getCourse(conn,courseId);
            List<StudentUser> classList = DbCourse.getStudentList(conn,courseId);
//            new StudentListPage(conn).createStudentList(info,null,classList,page);
//            new CreateNewStudentPage(conn).createFileUploadPage("Student data was saved for course ID: "+ courseId,info,courses,page);
            new ClassListOperationsPage(conn).createClassList(info,"Student data was saved for course ID: "+ courseId,courses,course,page);
        }
        else if (mode.equals(classListOperations_)) {
            List<Course> courses = DbCourse.getAllCourses(conn);
            int courseId =  GeneralHtml.getParamInt(params, GeneralHtml.classID_, -1);
            Course course=null;
            if (courseId != -1)
                course = DbCourse.getCourse(conn,courseId);
            // show a page that first collects a courseID
            // Then use this page again with the selected courseID plus a class list.
            new ClassListOperationsPage(conn).createClassList(info,null,courses,course,page);
        }
        else if (mode.equals(deleteStudentMode_)) {
            int studId =  GeneralHtml.getParamInt(params, GeneralHtml.studentID_, -1);
            logger.info("1. Deleting user data for " + studId);
            DbUser.deleteUserData(conn,studId);
            DbUser.deleteUser(conn,studId);
            int courseId =  GeneralHtml.getParamInt(params, GeneralHtml.classID_, 1);
            List<Course> courses = DbCourse.getAllCourses(conn);
            Course course = DbCourse.getCourse(conn,courseId);
            new ClassListOperationsPage(conn).createClassList(info,"Student " + studId + " deleted.",courses,course,page);

        }
        else if (mode.equals(clearStudentDataMode_)) {
            int studId =  GeneralHtml.getParamInt(params, GeneralHtml.studentID_, -1);
            logger.info("2. Deleting user data for " + studId);
            DbUser.deleteUserData(conn,studId);
            int courseId =  GeneralHtml.getParamInt(params, GeneralHtml.classID_, 1);
            List<Course> courses = DbCourse.getAllCourses(conn);
            Course course = DbCourse.getCourse(conn,courseId);
            new ClassListOperationsPage(conn).createClassList(info,"Student " + studId + " data deleted.",courses,course,page);
        }
        else if (mode.equals(editStudentMode_)) {
            int studId =  GeneralHtml.getParamInt(params, GeneralHtml.studentID_, -1);
            logger.info("3. Deleting user data for " + studId);
            DbUser.deleteUserData(conn,studId);
            List<Course> courses = DbCourse.getAllCourses(conn);
            int courseId =  GeneralHtml.getParamInt(params, GeneralHtml.classID_, 1);
            Course course = DbCourse.getCourse(conn,courseId);
            UserInfo userInfo = DbUser.getUserInfo(conn,studId);
            new CreateNewStudentPage(conn).createPage("Edit the User info",info,userInfo,courses,course,page, courseId);
        }
        return false;
    }

    // The given question is being moved up or down in the sequence of questions
    // within the module (direction=-1 means its moving to earlier placement in the module
    // , direction=1 means it's moving to a later placement).   Must modify the
    // modulequestionsequence for this module and a user of -1.    Must also alter the
    // data structures that represent the question list in QModule
    private void alterQuestionOrder(QModule m, String qID, int direction, Connection conn) throws Exception {
        if (direction == -1)  {
            DbModule.moveQuestionEarlier(m,qID, conn);
            m.moveQuestionEarlier(qID);
        }
        else {
            DbModule.moveQuestionLater(m,qID, conn);
            m.moveQuestionLater(qID);
        }
    }

    // return modId and qID
    private void checkModAndQuestion(Map params) throws Exception {
        msg_ = "";
        mID = GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "");
        if (mID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing moduleID.");
            throw ae;
        }
        qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
        if (qID.equals("")) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Missing questionID.");
            throw ae;
        }
    }

    public MultipleChoiceQuestion loadMultipleChoiceQuestion(String qID, Connection conn) throws Exception {
        MultipleChoiceQuestion q = DbQuestion.loadMultipleChoiceQuestion(qID, conn);
        Hints hints = DbHint.loadAllQuestionHints(q, conn);
        q.setHints(hints);
        return q;
    }

    public ShortAnswerQuestion loadShortAnswerQuestion(String qID, Connection conn) throws Exception {
        ShortAnswerQuestion q = DbQuestion.loadShortAnswerQuestion(qID, conn);
        Hints hints = DbHint.loadAllQuestionHints(q, conn);
        q.setHints(hints);
        return q;
    }

    public MultiChoiceSurveyQuestion loadMultipleChoiceSurveyQuestion(String qID, SystemInfo info, Connection conn) throws Exception {
        return DbQuestion.loadMultipleChoiceSurveyQuestion(qID, conn);
    }

    protected void deleteQuestion(Map params) throws Exception {

    }

    protected void loadHint(Map params) throws Exception {

    }

    protected void deleteHint(Map params) {

    }

    /*
       protected void saveNewModule(Map params, SystemInfo info,
                                    StringBuffer page, Connection conn)throws Exception {
          String msg="";
          int id=-1;
          String cID= GeneralHtml.getParamStr(params, "1", "");
          QModule module=getNewModule(params);
          try{
              validateNewModule(module);
              id = dbAccessor_.saveNewModule(module, conn);
              msg="New module was saved successfully.";
           }catch(Exception e){
              msg="New module was not saved. "+e.getMessage();
           }
           if(id>0){
              Vector cInfo = dbAccessor_.getAllCourses(conn);
              authorPage_.getNewModulePage(module, msg, info, cInfo, page);
           }
           else{
              module=DbModule.loadModule(Integer.toString(id), conn);
              Vector linkedQs=getModuleQuestions(module.getLinkedQuestionIDs(), conn);
              module.setLinkedQuestions(linkedQs);
              TreeMap cts=dbAccessor_.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
              info.setCatCounts(cts);
              Vector cInfo = dbAccessor_.getAllCourses(conn);
              Vector cIDs = dbAccessor_.getCourseIDsForModule(module.getID(), conn);
              authorPage_.getEditModulePage(msg, module, info, cInfo, cIDs, page);
           }
       }
    */
    protected QModule getNewModule(Map params) throws Exception {
        QModule m = new QModule();
        m.setName(GeneralHtml.getParamStr(params, GeneralHtml.moduleName_, ""));
        m.setStatus(false);
        m.setType(GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, ""));
        return m;
    }

    protected QModule getModule(Map params) throws Exception {
        QModule m = new QModule();
        m.setID(GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, ""));
        m.setName(GeneralHtml.getParamStr(params, GeneralHtml.moduleName_, ""));
        m.setStatus(GeneralHtml.getParamBoolean(params, GeneralHtml.readyStatus_, false));
        m.setReentrant(GeneralHtml.getParamBoolean(params, GeneralHtml.isReentrant_, false));
        m.setType(GeneralHtml.getParamStr(params, GeneralHtml.moduleType_, ""));
        m.setQuestionSequencing(GeneralHtml.getParamStr(params,GeneralHtml.moduleQuestionOrder_,null));
        return m;
    }

    protected boolean validateNewModule(QModule m) throws Exception {
        boolean ok = true;
        String msg = "";
        if (m.getName().equals("")) {
            msg += "Module name is not present. ";
            ok = false;
        }
        if (m.getAuthorID().equals("")) {
            msg += "Author is not present. ";
            ok = false;
        }
        if (!ok) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msg);
            throw ae;
        }
        return ok;
    }

    protected boolean validateModule(QModule m) throws Exception {
        boolean ok = true;
        String msg = "";
        if (m.getID().equals("")) {
            msg += "Module ID is not present. ";
            ok = false;
        }
        if (m.getName().equals("")) {
            msg += "Module name is not present. ";
            ok = false;
        }
        if (!ok) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msg);
            throw ae;
        }
        return ok;
    }

    protected MultiChoiceSurveyQuestion getNewMultipleChoiceSurveyQuestion(Map params) throws Exception {
        MultiChoiceSurveyQuestion q = new MultiChoiceSurveyQuestion();
        q.setAuthor(GeneralHtml.getParamStr(params, GeneralHtml.author_, ""));
        q.setStem(GeneralHtml.getParamStr(params, GeneralHtml.stem_, ""));
        q.setAnswerChoiceA(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceA_, ""));
        q.setAnswerChoiceB(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceB_, ""));
        q.setAnswerChoiceC(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceC_, ""));
        q.setAnswerChoiceD(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceD_, ""));
        q.setSequentialLayout(GeneralHtml.getParamStr(params, GeneralHtml.answerLayout, "sequential").equals("sequential"));
        return q;
    }

    protected MultipleChoiceQuestion getNewMultipleChoiceQuestion(Map params) throws Exception {
        MultipleChoiceQuestion q = new MultipleChoiceQuestion();
        q.setAuthor(GeneralHtml.getParamStr(params, GeneralHtml.author_, ""));
        q.setCategoryID(GeneralHtml.getParamStr(params, GeneralHtml.categoryID_, ""));
        q.setStd1(GeneralHtml.getParamStr(params, GeneralHtml.standard1_, ""));
        q.setStd2(GeneralHtml.getParamStr(params, GeneralHtml.standard2_, ""));
        q.setStd3(GeneralHtml.getParamStr(params, GeneralHtml.standard3_, ""));
        q.setCcss1(GeneralHtml.getParamStr(params, GeneralHtml.ccss1, ""));
        q.setCcss2(GeneralHtml.getParamStr(params, GeneralHtml.ccss2, ""));
        q.setCcss3(GeneralHtml.getParamStr(params, GeneralHtml.ccss3, ""));
        q.setSource(GeneralHtml.getParamStr(params, GeneralHtml.source_, ""));
        q.setDegree(GeneralHtml.getParamStr(params, GeneralHtml.degree_, ""));
        q.setStem(GeneralHtml.getParamStr(params, GeneralHtml.stem_, ""));
        q.setAnswerChoiceA(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceA_, ""));
        q.setAnsChoiceAFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceAFeedback_, ""));
        q.setAnswerChoiceB(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceB_, ""));
        q.setAnsChoiceBFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceBFeedback_, ""));
        q.setAnswerChoiceC(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceC_, ""));
        q.setAnsChoiceCFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceCFeedback_, ""));
        q.setAnswerChoiceD(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceD_, ""));
        q.setAnsChoiceDFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceDFeedback_, ""));
        q.setCorrectAnswer(GeneralHtml.getParamStr(params, GeneralHtml.correctAnsChoice_, ""));
        q.setTopic(GeneralHtml.getParamStr(params, GeneralHtml.questionTopic_, ""));
        q.setSequentialLayout(GeneralHtml.getParamStr(params, GeneralHtml.answerLayout, "sequential").equals("sequential"));
        return q;
    }

    protected ShortAnswerQuestion getShortAnswerQuestion(Map params) throws Exception {
        ShortAnswerQuestion q = new ShortAnswerQuestion(params);
        // when this is called from saveNewShortAnswerQ the ID is not present
        q.setID(GeneralHtml.getParamStr(params, GeneralHtml.questionID_, ""));
        q.setCategoryID(GeneralHtml.getParamStr(params, GeneralHtml.categoryID_, ""));
        q.setStd1(GeneralHtml.getParamStr(params, GeneralHtml.standard1_, ""));
        q.setStd2(GeneralHtml.getParamStr(params, GeneralHtml.standard2_, ""));
        q.setStd3(GeneralHtml.getParamStr(params, GeneralHtml.standard3_, ""));
        q.setCcss1(GeneralHtml.getParamStr(params, GeneralHtml.ccss1, ""));
        q.setCcss2(GeneralHtml.getParamStr(params, GeneralHtml.ccss2, ""));
        q.setCcss3(GeneralHtml.getParamStr(params, GeneralHtml.ccss3, ""));
        q.setDegree(GeneralHtml.getParamStr(params, GeneralHtml.degree_, ""));
        q.setStatus(GeneralHtml.getParamBoolean(params,GeneralHtml.readyStatus_,false));
        List<String> answers =(GeneralHtml.getParamStrings(params, GeneralHtml.correctAnsChoice_));
        q.setAnswers(answers);
        q.setTopic(GeneralHtml.getParamStr(params, GeneralHtml.questionTopic_, ""));
        return q;
    }

    protected boolean validateNewMultipleChoiceQuestion(MultipleChoiceQuestion q
    ) throws Exception {
        boolean ok = true;
        String msg = "";
        if (q.getCategoryID().equals("")) {
            msg += "Category ID is not present. ";
            ok = false;
        }
        if (q.getStd1().equals("") && q.getStd1().equals("") && q.getStd1().equals("")) {
            msg += "At least one standard must be selected. ";
            ok = false;
        }
        if (GeneralHtml.isEmpty(q.getAnswerChoiceA()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceB()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceC()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceD())) {
            msg += "All answer choices must be entered. ";
            ok = false;
        }
        if (GeneralHtml.isEmpty(q.getCorrectAnswer())) {
            msg += "A correct answer must be specified.";
            ok = false;
        }
        if (!ok) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msg);
            throw ae;
        }
        return ok;
    }

 protected boolean validateNewMultipleChoiceSurveyQuestion(MultiChoiceSurveyQuestion q
    ) throws Exception {
        boolean ok = true;
        String msg = "";
        if (GeneralHtml.isEmpty(q.getAnswerChoiceA()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceB()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceC()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceD())) {
            msg += "All answer choices must be entered. ";
            ok = false;
        }
        if (!ok) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msg);
            throw ae;
        }
        return ok;
    }

protected boolean validateMultipleChoiceSurveyQuestion(MultiChoiceSurveyQuestion q
    ) throws Exception {
            boolean ok = true;
        String msg = "";
    if (q.getID().equals("")) {
            msg += "Question ID is not present.";
            ok = false;
        }
          if (!ok) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msg);
            throw ae;
        }
    else return validateNewMultipleChoiceSurveyQuestion(q);
}
    protected boolean validateQuestion (Question q, StringBuffer msg) {
         boolean ok = true;
        if (q.getID().equals("")) {
            msg.append("Question ID is not present.");
            ok = false;
        }
        if (q.getCategoryID().equals("")) {
            msg.append("Category ID is not present. ");
            ok = false;
        }
        if (q.getStd1().equals("") && q.getStd1().equals("") && q.getStd1().equals("")) {
            msg.append("At least one standard must be selected. ");
            ok = false;
        }
        return ok;
    }

    protected boolean validateShortAnswerQuestion (ShortAnswerQuestion q) throws Exception {
        StringBuffer b =new StringBuffer();
        boolean ok = validateQuestion(q,b);
        if (!q.hasAnswers()) {
            b.append("Must provide one correct answer.");
            ok=false;
        }

        if (!ok) throw new Exception(b.toString());
        return ok;
    }

    protected boolean validateMultipleChoiceQuestion(MultipleChoiceQuestion q
    ) throws Exception {
        StringBuffer msgb=new StringBuffer();
        boolean ok = validateQuestion(q,msgb);
        if (GeneralHtml.isEmpty(q.getAnswerChoiceA()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceB()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceC()) ||
                GeneralHtml.isEmpty(q.getAnswerChoiceD())) {
            msgb.append("All answer choices must be entered. ");
            ok = false;
        }
        if (GeneralHtml.isEmpty(q.getCorrectAnswer())) {
            msgb.append("A correct answer must be specified.");
            ok = false;
        }

        if (!ok) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msgb.toString());
            throw ae;
        }
        return ok;
    }

    protected MultiChoiceSurveyQuestion getMultipleChoiceSurveyQuestion(Map params) throws Exception {
        String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
        MultiChoiceSurveyQuestion q = new MultiChoiceSurveyQuestion(qID);
        q.setAuthor(GeneralHtml.getParamStr(params, GeneralHtml.author_, ""));
        q.setSource(GeneralHtml.getParamStr(params, GeneralHtml.source_, ""));
        q.setStatus(GeneralHtml.getParamBoolean(params, GeneralHtml.readyStatus_, false));
        q.setStem(GeneralHtml.getParamStr(params, GeneralHtml.stem_, ""));
        q.setAnswerChoiceA(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceA_, ""));
        q.setAnswerChoiceB(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceB_, ""));
        q.setAnswerChoiceC(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceC_, ""));
        q.setAnswerChoiceD(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceD_, ""));
        boolean isSequential = GeneralHtml.getParamStr(params, GeneralHtml.answerLayout, "sequential").equals("sequential");
        q.setSequentialLayout(isSequential);
        return q;
    }

    protected MultipleChoiceQuestion getMultipleChoiceQuestion(Map params) throws Exception {
        String qID = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
        MultipleChoiceQuestion q = new MultipleChoiceQuestion(qID);
        q.setCategoryID(GeneralHtml.getParamStr(params, GeneralHtml.categoryID_, ""));
        q.setStd1(GeneralHtml.getParamStr(params, GeneralHtml.standard1_, ""));
        q.setStd2(GeneralHtml.getParamStr(params, GeneralHtml.standard2_, ""));
        q.setStd3(GeneralHtml.getParamStr(params, GeneralHtml.standard3_, ""));
        q.setCcss1(GeneralHtml.getParamStr(params, GeneralHtml.ccss1, ""));
        q.setCcss2(GeneralHtml.getParamStr(params, GeneralHtml.ccss2, ""));
        q.setCcss3(GeneralHtml.getParamStr(params, GeneralHtml.ccss3, ""));
        q.setAuthor(GeneralHtml.getParamStr(params, GeneralHtml.author_, ""));
        q.setSource(GeneralHtml.getParamStr(params, GeneralHtml.source_, ""));
        q.setDegree(GeneralHtml.getParamStr(params, GeneralHtml.degree_, ""));
        q.setStatus(GeneralHtml.getParamBoolean(params, GeneralHtml.readyStatus_, false));
        q.setStem(GeneralHtml.getParamStr(params, GeneralHtml.stem_, ""));
        q.setAnswerChoiceA(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceA_, ""));
        q.setAnsChoiceAFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceAFeedback_, ""));
        q.setAnswerChoiceB(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceB_, ""));
        q.setAnsChoiceBFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceBFeedback_, ""));
        q.setAnswerChoiceC(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceC_, ""));
        q.setAnsChoiceCFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceCFeedback_, ""));
        q.setAnswerChoiceD(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceD_, ""));
        q.setAnsChoiceDFeedback(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceDFeedback_, ""));
        q.setCorrectAnswer(GeneralHtml.getParamStr(params, GeneralHtml.correctAnsChoice_, ""));
        q.setTopic(GeneralHtml.getParamStr(params, GeneralHtml.questionTopic_, ""));
        boolean isSequential = GeneralHtml.getParamStr(params, GeneralHtml.answerLayout, "sequential").equals("sequential");
        q.setSequentialLayout(isSequential);
        return q;
    }


    protected Hint getHint(Map params) throws Exception {
        String hID = GeneralHtml.getParamStr(params, GeneralHtml.hintID_, "");
        Hint h = new Hint(hID);
        h.setQuery(GeneralHtml.getParamStr(params, GeneralHtml.query_, ""));
        h.setResponse(GeneralHtml.getParamStr(params, GeneralHtml.response_, ""));
        h.setLevel(GeneralHtml.getParamStr(params, GeneralHtml.level_, ""));
        h.setCoachID(GeneralHtml.getParamStr(params, GeneralHtml.coachID_, ""));
        h.setStrategyID(GeneralHtml.getParamStr(params, GeneralHtml.strategyID_, ""));
        return h;
    }

    protected Hint getNewHint(Map params) throws Exception {
        Hint h = new Hint();
        h.setQuery(GeneralHtml.getParamStr(params, GeneralHtml.query_, ""));
        h.setResponse(GeneralHtml.getParamStr(params, GeneralHtml.response_, ""));
        h.setLevel(GeneralHtml.getParamStr(params, GeneralHtml.level_, ""));
        h.setCoachID(GeneralHtml.getParamStr(params, GeneralHtml.coachID_, ""));
        h.setStrategyID(GeneralHtml.getParamStr(params, GeneralHtml.strategyID_, ""));
        h.setAnsChoiceLink(GeneralHtml.getParamStr(params, GeneralHtml.ansChoiceLink_, ""));
        h.setAuthor(GeneralHtml.getParamStr(params, FormalitySubsystem.userIDParam, ""));
        return h;
    }

    //return 0 if blank,  if invalid(throws and exception) or 1 if valid
    protected int validateNewHint(Hint h) throws Exception {
        boolean ok1 = true, ok11 = true, ok2 = true, ok3 = true, ok4 = true;
        String msg = "";
        if (h.getLevel().equals("")) {
            msg += "Step value is not present.";
            ok1 = false;
        }
        if (h.getAuthor().equals("")) {
            msg += "Author value is not present.";
            ok11 = false;
        }
        if (h.getCoachID().equals("")) {
            msg += " Coach value is not present.";
            ok2 = false;
        }
        if (h.getQuery().equals("")) {
            msg += " Query value is not present.";
            ok3 = false;
        }
        if (h.getResponse().equals("")) {
            msg += " Response value is not present.";
            ok4 = false;
        }
        if (!(ok1 || ok2 || ok3 || ok4)) //a new hint is not entered
            return 0;
        if (!(ok1 && ok2 && ok3 && ok4)) {  //forgot a required field
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msg);
            throw ae;
        }
        return 1;
    }

    protected boolean validateHint(Hint h) throws Exception {
        boolean ok0 = true, ok1 = true, ok2 = true, ok3 = true, ok4 = true;
        String msg = "";
        if (h.getID().equals("")) {
            msg += "Hint ID is not present.";
            ok0 = false;
        }
        if (h.getLevel().equals("")) {
            msg += "Step value is not present.";
            ok1 = false;
        }
        if (h.getCoachID().equals("")) {
            msg += " Coach value is not present.";
            ok2 = false;
        }
        if (h.getQuery().equals("")) {
            msg += " Query value is not present.";
            ok3 = false;
        }
        if (h.getResponse().equals("")) {
            msg += " Response value is not present.";
            ok4 = false;
        }

        if (!(ok0 && ok1 && ok2 && ok3 && ok4)) {  //forgot a required field
            AuthoringException ae = new AuthoringException();
            ae.setMessage(msg);
            throw ae;
        }
        return true;
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

     protected void saveNewShortAnswerSurveyQuestion(Map params, Connection conn,
                                              SystemInfo info, StringBuffer page) throws Exception {
         ShortAnswerQuestion q=null;
         try {
             q = new ShortAnswerQuestion(params);
             int id = DbQuestion.saveNewSurveyQuestion(q,conn);
             if (id > 0) {
                 q = DbQuestion.loadShortAnswerQuestion(Integer.toString(id),  conn);
                 AuthSAQuestionPage.getEditShortAnswerSurveyQuestionPage(q, info, "", page);
             }
         } catch (Exception e) {
             AuthSAQuestionPage.getNewShortAnswerSurveyQuestionPage(q, "Failed to save: " + e.getMessage(), info, page);
         }
     }
        
    protected void saveNewMultiChoiceSurveyQuestion(Map params, Connection conn,
                                              SystemInfo info, StringBuffer page) throws Exception {
        String msg = "";
        int id = -1;
        MultiChoiceSurveyQuestion q = getNewMultipleChoiceSurveyQuestion(params);
        try {
            validateNewMultipleChoiceSurveyQuestion(q);
            id = DbQuestion.saveNewMultipleChoiceSurveyQuestion(q, conn);
            msg = "New survey question was saved successfully.";
        } catch (Exception e) {
            msg = "New survey question was not saved. " + e.getMessage();
        }
        if (id < 0)
            AuthSurveyQuestionPage.getNewMultiChoiceSurveyQuestionPage(q, msg, info, page);
        else {
            q = loadMultipleChoiceSurveyQuestion(Integer.toString(id), info, conn);
            AuthSurveyQuestionPage.getEditMultiChoiceSurveyQuestionPage(q, info, msg, page);
        }
    }


    protected void saveNewMultiChoiceQuestion(Map params, Connection conn,
                                              SystemInfo info, StringBuffer page) throws Exception {
        String msg = "";
        int id = -1;
        MultipleChoiceQuestion q = getNewMultipleChoiceQuestion(params);
        try {
            validateNewMultipleChoiceQuestion(q);
            id = DbQuestion.saveNewMultipleChoiceQuestion(q, conn);
            msg = "New question was saved successfully.";
        } catch (Exception e) {
            msg = "New question was not saved. " + e.getMessage();
        }
        if (id < 0)
            AuthMCQuestionPage.getNewMultiChoiceQuestionPage(q, msg, info, page, conn);
        else {
            q = loadMultipleChoiceQuestion(Integer.toString(id), conn);
            AuthMCQuestionPage.getEditMultiChoiceQuestionPage(q, null, info, msg, page, conn);
        }
    }

    protected void saveNewShortAnswerQuestion(Map params, Connection conn,
                                              SystemInfo info, StringBuffer page) throws Exception {

     String msg="";
     int id=-1;
     ShortAnswerQuestion q= getShortAnswerQuestion(params);
     try{
//         validateNewShortAnswerQuestion(q);
         id =DbQuestion.saveNewShortAnswerQuestion(q, conn);
         msg="New question was saved successfully.";
      }catch(Exception e){
         msg="New question was not saved. "+e.getMessage();
      }
      if(id<0)
         AuthSAQuestionPage.getNewShortAnswerQuestionPage(q, msg, info, page, conn);
      else{
         q=DbQuestion.loadShortAnswerQuestion(Integer.toString(id),  conn);
         AuthSAQuestionPage.getEditShortAnswerQuestionPage(q, null, info, msg, page, conn);
         }
    }




    protected void loadUserCourseData(DBAccessor dbAccessor,
                                      UserInfo info, Connection conn) throws Exception {
        dbAccessor.getUserCourseInfo(info, conn);
    }

    public Vector getQuestionInventory(Connection conn) throws Exception {
        Vector data = new Vector();
        String[] stds = DBAccessor.standardsNames_;
        for (int i = 0; i < stds.length; i++) {
            String stdID = stds[i];
            int qct = dbAccessor_.getQCountLinkedToAStd(stdID, conn);
            int lqct = dbAccessor_.getQCountLinkedToAStdAndAMod(stdID, conn);
            DataTuple dt = new DataTuple();
            dt.setFirst(stdID);
            dt.setSecond(Integer.toString(qct));
            dt.setThird(Integer.toString(lqct));
            dt.setFourth(Integer.toString(qct - lqct));
            data.add(dt);
        }
        return data;
    }

    private Vector getSelectedCourseIDs(Map params, Vector cInfo) throws Exception {
        Vector selIDs = new Vector();
        String indicator = "";
        for (int i = 0; i < cInfo.size(); i++) {
            DataTuple dt = (DataTuple) cInfo.get(i);
            String curID = (String) dt.getFirst();
            indicator = GeneralHtml.getParamStr(params, curID, "");
            if (indicator.equals("on"))
                selIDs.add(curID);
        }
        return selIDs;
    }

    private void setNewModCourseLinks(Vector selIDs, String mID,
                                      Connection conn) throws Exception {
        for (int i = 0; i < selIDs.size(); i++) {
            String curID = (String) selIDs.get(i);
            DbModule.addModCourseLink(mID, curID, conn);
        }
    }

    //add selected links that are not already added,
    // delete links that exist but are not selected
    private void setModCourseLinks(Vector selIDs, Vector existingCIDs,
                                   String mID, Connection conn) throws Exception {
        for (int i = 0; i < selIDs.size(); i++) {
            String curID = (String) selIDs.get(i);
            if (!existingCIDs.contains(curID))
                DbModule.addModCourseLink(mID, curID, conn);
        }
        for (int i = 0; i < existingCIDs.size(); i++) {
            String curID = (String) existingCIDs.get(i);
            if (!selIDs.contains(curID))
                DbModule.deleteModCourseLink(mID, curID, conn);
        }
    }
}
