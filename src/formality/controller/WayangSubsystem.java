package formality.controller;

import formality.content.*;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DbQuestion;
import formality.content.database.mysql.DbHint;
import formality.html.GeneralHtml;
import formality.html.WayangQuestionPage;
import formality.html.hints.HintRenderer2;
import formality.systemerror.AuthoringException;
import formality.servlet.FormalityServlet;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.sql.Connection;

/**
 * WayangOutpost makes requests to Formality.   The requests are limited to:
 * getProblem (give qid, wayang studId)
 * process an answer choice
 * forward this event to wayang
 * process a hint selection
 * forward this event to wayang
 * return a hint
 * forward hint selected to wayang
 * process an end-problem event forward to wayang
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Jun 22, 2011
 * Time: 2:20:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class WayangSubsystem extends AuthoringSubsystem {

    private static Logger logger = Logger.getLogger(WayangSubsystem.class);
    public static final String STUD_ID = "wayangStudId";
    public static final String SESS_ID = "wayangSessId";
    public static final String PROB_ID = "wayangProbId";
    public static final String TIME = "pageGenerationTime";
    public static final String ELAPSED_TIME = "elapsedTime";
    public static final String PROB_ELAPSED_TIME = "probElapsedTime";
    public static final String WAYANG_SERVLET_CONTEXT = "wayangServletContext";

    private String wayangSessId;
    private String wayangStudId;
    private String wayangProbId;
    private long wayangElapsedTime;
    private long wayangProbElapsedTime;
    private long pageGenerationTime;
    private boolean callWayang=true;
    public String wayangContext;
    public static String servletHost;

    private static String endQuestionMode_ = "endQuestion";


    public static String wayangURI;

    public WayangSubsystem() {
    }

    public String callWayangGET(String action, String params, SystemInfo info, boolean addTime) throws Exception {

        String actcmd = "?" + "action" + "=" + action;
        params += "&studId=" + wayangStudId + "&sessionId=" + wayangSessId;
        if (addTime) {
            logger.info("Times: elapsedTime="+info.getElapsedTime()+" probElapsedTime="+ info.getProbElapsedTime() );
            params += "&elapsedTime="+info.getElapsedTime()+"&probElapsedTime="+ info.getProbElapsedTime();
        }
        String urlparams = actcmd+params;
        String urlStr = wayangURI + wayangContext + "/TutorBrain"+ urlparams;
        return callWayang ? callServerGet(urlStr) : null;
    }

    public String callServerGet(String urlStr) {
        HttpURLConnection connection = null;
        OutputStreamWriter wr = null;
        BufferedReader rd = null;
        StringBuilder sb = null;
        String line = null;
        logger.debug("calling wayang with " + urlStr);
        URL serverAddress = null;

        try {
            serverAddress = new URL(urlStr);
            //set up out communications stuff
            connection = null;

            //Set up the initial connection
            connection = (HttpURLConnection) serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);

            connection.connect();

            //get the output stream writer and write the output to the server
            //not needed in this example
            //wr = new OutputStreamWriter(connection.getOutputStream());
            //wr.write("");
            //wr.flush();

            //read the result from the server
            rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            sb = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                sb.append(line + '\n');
            }

//          System.out.println(sb.toString());
            logger.debug("wayang reply: " + sb.toString());
            return sb.toString();

        } catch (Exception e) {
            System.out.println("Failed to reach wayang servlet at:" + urlStr);
            e.printStackTrace();
        } finally {
            //close the connection, set all objects to null
            connection.disconnect();
            rd = null;
            sb = null;
            wr = null;
            connection = null;
        }
        return null;
    }



    private void setHost (HttpServletRequest req) {

        String servletName;
        String servletContext;
        String queryString;
        String host;
        String reqURI;
        int port;
        servletName = req.getServletPath();     //  /FormalityServlet
        servletContext = req.getContextPath();           //   /wo4
        queryString = req.getQueryString();           // fxn=wayang&mode=viewq&qid=....
        reqURI = req.getRequestURI();            // /wo4/FormalityServlet
        servletHost = req.getServerName();            // localhost
        port = req.getServerPort();
        wayangURI = "http://" + servletHost + ":" + port + "/";     // context will be passed as an param on query String
                                                            // to allow wayang to control who to call

    }


    // first get a session going with wayang with http://localhost:8080/TutorBrain?action=login&uname=ARMS_033&password=ARMS
    // test case: http://localhost:8082/wo4/FormalityServlet?fxn=wayang&mode=viewq&qID=109&un=1864&wayangStudId=134&wayangSessId=30105&elapsedTime=10000
    // test case: http://localhost:8080/wo4?fxn=wayang&mode=viewq&qID=109&un=1770&wayangStudId=134&wayangSessId=30105
    public boolean handleRequest(Map params, SystemInfo info, Connection conn, StringBuffer page, HttpServletRequest req, HttpServletResponse response) throws Exception {
        // set up necessary pieces for calls to wayang
        setHost(req);
        wayangContext = GeneralHtml.getParamStr(params, WayangSubsystem.WAYANG_SERVLET_CONTEXT, "woj2");
        info.setWayangContext(wayangContext);

        String mode = GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "");
        String qid = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, null);
        wayangSessId = GeneralHtml.getParamStr(params, WayangSubsystem.SESS_ID, "");
        wayangStudId = GeneralHtml.getParamStr(params, WayangSubsystem.STUD_ID, "");
        wayangProbId = GeneralHtml.getParamStr(params, WayangSubsystem.PROB_ID, "");
        wayangElapsedTime = GeneralHtml.getParamLong(params, WayangSubsystem.ELAPSED_TIME, 0);
        wayangProbElapsedTime = GeneralHtml.getParamLong(params, WayangSubsystem.PROB_ELAPSED_TIME, 0);
        // all calls to 4mality that expect a subsequent call to wayang need to pass this param with a value of woj2, woj2_debug, etc

        info.setElapsedTime(wayangElapsedTime);
        info.setProbElapsedTime(wayangProbElapsedTime);
        long now = System.currentTimeMillis();
        pageGenerationTime = GeneralHtml.getParamLong(params, WayangSubsystem.TIME, now);
        info.setLastPageGenerationTime(pageGenerationTime);
        new DBAccessor().loadStudentCourses(info.getUserInfo(), conn);
        HintRenderer2 hintRenderer = new HintRenderer2();
        logger.info("mode: " + mode);
        if (mode.equals(viewQuestionMode_)) {
            // TODO note that javascript within the problem HTML needs to have probElapsedTime and elapsedTime as hidden inputs
            // in the HTML page so that external activities send the correct times to wayang.   This means setting timers
            // based on the two input times and then sending time diffs to wayang
            getQuestion(params, info, conn, page, qid, hintRenderer);
            // its a new problem so probElapsed is 0 and the elapsedTime is incremented by a little
            this.callWayangGET("FormalityBeginProblem", "&probId=" + wayangProbId, info, true);

//        else if (mode.equals(readAloudMode)) {
//            return FormalitySubsystem.readAloud(params,info,conn,response);
        }
        if (mode.equals(clearQuestionMode_)) {
            getQuestion(params, info, conn, page, qid, hintRenderer);
        }
        // an external activity is launched
        else if (mode.equals(StudentSubsystem.viewExternalActivity)) {
            now = System.currentTimeMillis();
            info.setElapsedTime(info.getElapsedTime() + (now - info.getLastPageGenerationTime()));
            info.setProbElapsedTime(info.getProbElapsedTime() + (now - info.getLastPageGenerationTime()));
            info.setLastPageGenerationTime(now);
            String extActivityUrl = GeneralHtml.getParamStr(params, GeneralHtml.externalActivityUrl_, "");
            // TODO wayang is expecting an integer hint id so that it can process the hint correctly.
            // This is not passing a number.   It is passing URLs and wayang will fail.
           // this.callWayangGET("FormalityExternalActivity", "&userInput=" + extActivityUrl, info, true);

        }
        // when the user submits an answer
        else if (mode.equals(evaluateQuestionMode_)) {
            evaluateResponse(params, info, conn, page, hintRenderer);

        }
        // when a hint is selected
        else if (mode.equals(viewActiveQuestionMode_)) {
            getHint(params, info, conn, page, hintRenderer);
        }
        // don't need this event.   When a problem ends, client will call Wayang server.
//        else if (mode.equals(WayangSubsystem.endQuestionMode_)) {
//            // TODO haven't figured out how a question terminates.
//            this.callWayangGET("FormalityEndProblem", "&probID=" + qid, info, true);
//        }
        return false;
    }

    private void getHint(Map params, SystemInfo info, Connection conn, StringBuffer page, HintRenderer2 hintRenderer) throws Exception {
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
        String hID = GeneralHtml.getParamStr(params, GeneralHtml.selHintID_, "");

        Question q = (type.equals("mc")) ? loadMultipleChoiceQuestion(qID, conn) : loadShortAnswerQuestion(qID, conn);
        q.setSelHintID(hID);
        q.setSelLevel(level);
        q.setSubmittedAnswer(GeneralHtml.getParamStr(params, GeneralHtml.submittedAns_, ""));
        String wayangUserInput = "";
        // note hID will be "" when the "back to stratgies" button is clicked (not a hint request)
        if (!hID.equals("")) {
            Hint h = q.getHints().getHint(hID);
            wayangUserInput = hID;
            if (h != null) {
                q.setActiveTag(GeneralHtml.getHighlightTag(q.getSelLevel(), h.getCoachID()));
                wayangUserInput = URLEncoder.encode(h.getCoachName()) + "." + hID;
            }
        }
        WayangQuestionPage wqp = new WayangQuestionPage(params);
        if (q.isMultipleChoice())
            wqp.getMultipleChoiceQuestion(q, info, null, paramStr.toString(), hintRenderer, page, hID, params);
        else
            wqp.getShortAnswerQuestion(q, info, null, paramStr.toString(), hintRenderer, page, hID, params);
        // only call wayang when its a hint request
        if (!hID.equals(""))
            callWayangGET("FormalityHint", "&userInput=" + wayangUserInput+ "&probId="+wayangProbId, info, true);

    }

    private void evaluateResponse(Map params, SystemInfo info, Connection conn, StringBuffer page, HintRenderer2 hintRenderer) throws Exception {
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
            Question q = (type.equals("mc")) ? loadMultipleChoiceQuestion(qID, conn) : DbQuestion.loadShortAnswerQuestion(qID, conn);
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
            if (q.isMultipleChoice()) {
                WayangQuestionPage pg = new WayangQuestionPage(params);
                pg.getMultipleChoiceQuestion(q, info, msg, paramStr.toString(), hintRenderer, page, hID, params);
            } else {
                WayangQuestionPage pg = new WayangQuestionPage(params);
                pg.getShortAnswerQuestion(q, info, msg, paramStr.toString(), hintRenderer, page, hID, params);
            }
            callWayangGET("FormalityAttempt", "&isCorrect=" + q.isCorrect() + "&userInput=" + subAns.trim() + "&probId="+wayangProbId, info, true);
        }
    }



    private void getQuestion(Map params, SystemInfo info, Connection conn, StringBuffer page, String qid, HintRenderer2 hintRenderer) throws Exception {
        Question q = DbQuestion.loadQuestion(qid, conn);
        Question q2;
        if (q.isMultipleChoice())
            q2 = DbQuestion.loadMultipleChoiceQuestion(qid, conn);
        else q2 = new ShortAnswerQuestion(q);
        Hints hints = DbHint.loadAllQuestionHints(q2, conn);
        q2.setHints(hints);
        WayangQuestionPage wqp = new WayangQuestionPage(params);
        q2.setSelLevel(FormalitySubsystem.defaultHintLevel);
        if (q.isMultipleChoice())
            wqp.getMultipleChoiceQuestion(q2, info, null, "", hintRenderer, page, null, params);
        else {
            wqp.getShortAnswerQuestion(q2, info, null, "", hintRenderer, page, null, params);
        }
    }

    private long getProbElapsedTime () {
        return wayangProbElapsedTime + (System.currentTimeMillis() - pageGenerationTime);
    }
    private long getElapsedTime () {
        return wayangElapsedTime + (System.currentTimeMillis() - pageGenerationTime);
    }

    public static void main(String[] args) {
        WayangSubsystem ws = new WayangSubsystem();
        try {
            String out = ws.callServerGet("http://www.google.com/");
            System.out.println(out);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
