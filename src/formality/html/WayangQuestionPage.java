package formality.html;

import formality.parser.HighlightParser;
import formality.content.*;

import java.util.Vector;
import java.util.Map;

import formality.controller.*;
import formality.html.hints.BaseHintRenderer;

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
public class WayangQuestionPage extends MultipleChoiceQuestionPage {

    protected HighlightParser hltParser_;
    String[] wayangParamNames;
    String[] wayangParamVals;

    public WayangQuestionPage(Map params) throws Exception {
        hltParser_ = new HighlightParser();
        wayangParamNames = new String[]{WayangSubsystem.SESS_ID, WayangSubsystem.STUD_ID, WayangSubsystem.PROB_ID,
                WayangSubsystem.WAYANG_SERVLET_CONTEXT};
        wayangParamVals = new String[] {GeneralHtml.getParamStr(params, WayangSubsystem.SESS_ID, ""),
                GeneralHtml.getParamStr(params, WayangSubsystem.STUD_ID, ""),
                GeneralHtml.getParamStr(params,WayangSubsystem.PROB_ID,""),
                GeneralHtml.getParamStr(params,WayangSubsystem.WAYANG_SERVLET_CONTEXT,"")};

    }

     public void getCheckAnswerAndClear(StringBuffer page, Question q) {
        page.append("<table width=100%><tr><td align=\"left\">");
        // sa survey questions need this check so button is omitted.
        // mc survey questions don't come in here
        if (!(q.isSurvey() && q.getType().equals("sa")))
            page.append("<INPUT id=\"submitButton\" type=\"SUBMIT\" value=\"CHECK ANSWER\" name=\"checkAns\">");
        page.append("</td><td align=\"right\">");
        page.append("<INPUT type=\"button\" value=\"CLEAR\" name=\"clear\" onClick=\"checkClear(QForm)\">");
        page.append("</td></tr></table>").append("\r\n");
    }

    private void getQuestionPart1 (Question q, SystemInfo info, String msg, StringBuffer page, Map params) throws Exception {

           hltParser_.setActiveTag(q.getActiveTag());
           String clrurl = GeneralHtml.getWayangUrl(info.getServletRootAndID(),
                   AuthoringSubsystem.clearQuestionMode_,
                   wayangParamNames, wayangParamVals);
           GeneralHtml.getPageHeaderForWayang("", false, page, info, true,
                   info.getElapsedTime(), info.getProbElapsedTime(), clrurl);
           page.append("<body>").append("\r\n");
           //getJavascriptLevelHintSelBlock("QForm", page);


           if (msg != null)
               page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
           page.append("<FORM id=\"submitForm\" ACTION=\"");
           // the extra params to pass back to wayang will ideally be:
           // studId, sessionId,  probElapsedTime, elapsedTime,   action=Action, userInput=?, isCorrect=true/false
           page.append(GeneralHtml.getWayangUrl(info.getServletRootAndID(),
                   AuthoringSubsystem.evaluateQuestionMode_, wayangParamNames, wayangParamVals));
           page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
           // These hidden params are added so that the showExternalActivity javascript function
           // can get the two values for its call to the formality servlet in wayang mode
           GeneralHtml.getHiddenParam(WayangSubsystem.STUD_ID, GeneralHtml.getParamStr(params, WayangSubsystem.STUD_ID, ""), page);
           GeneralHtml.getHiddenParam(WayangSubsystem.SESS_ID, GeneralHtml.getParamStr(params, WayangSubsystem.SESS_ID, ""), page);
           // TODO need to include the probElapsedTime and elapsedTime so that these can be sent too.   Javascript may have to
           // start a timer on page load so that it can figure out how much to add to the two times.

           // At the time of page generation we compute new probElapsed and elapsed times.   This is done by finding the
           // difference between the current time and the time the last page was generated (a timestamp was inserted as a hidden param
           // in the HTML).   This time is then added to the probElapsedTime and the elapsedTime
          long now = System.currentTimeMillis();
          info.setElapsedTime(info.getElapsedTime() + (now - info.getLastPageGenerationTime()));
          info.setProbElapsedTime(info.getProbElapsedTime() + (now - info.getLastPageGenerationTime()));
          info.setLastPageGenerationTime(now);
           // TODO currently we are just sending back the elapsed and probElapsed that was given at generation of page time.
          GeneralHtml.getHiddenParam(WayangSubsystem.ELAPSED_TIME, Long.toString(info.getElapsedTime()), page);
          GeneralHtml.getHiddenParam(WayangSubsystem.PROB_ELAPSED_TIME, Long.toString(info.getProbElapsedTime()) , page);
           // the time is inserted in the page so that when an event comes back to the 4mality server it can figure out how
           // much time went by
           GeneralHtml.getHiddenParam(WayangSubsystem.TIME, Long.toString(info.getLastPageGenerationTime()), page);
           page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
           page.append("<!-- MAIN TABLE -->").append("\r\n");
           page.append("<table border=0 width=100% HEIGHT=85%><tr>");
           page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
           page.append("<table border=0 width=100% height=100%>");
           //row for stem
           page.append("<tr><td>").append("\r\n");

           page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
           getStem(q, info, page);
           page.append("<br><br>");
           page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
           insertReadAloudPlayers(q, info, page);
           page.append("</td></tr>").append("\r\n");
       }


    private void getQuestionPart2 (Question q, StringBuffer page, SystemInfo info, BaseHintRenderer hintRenderer,
                                   String hintId, String paramStr) throws Exception {
 //row for submit and clear buttons
        page.append("<tr><td>").append("\r\n");
        if (q.isSurvey())
            getClear(page);
        else getCheckAnswerAndClear(page, q);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");
        page.append("<td>");//second main "TD"
        if (q.isEvaluated())
            getAnswerFeedback(q, page);
        else
            getHintResponse(q, info, page);
        page.append("</td>");
        //third main table cell
        page.append("<td valign=top width=30%>").append("\r\n");
        //hints go here
        // Set up slightly different general parameters on each URL for calling back to the page that gets generated
        wayangParamNames = new String[]{WayangSubsystem.SESS_ID, WayangSubsystem.STUD_ID
                , WayangSubsystem.TIME, WayangSubsystem.PROB_ID, WayangSubsystem.WAYANG_SERVLET_CONTEXT
               };
        String[] vals = new String[] {
                wayangParamVals[0], wayangParamVals[1],
                // Insert elapsedTime and probElapsed + pageGenerateionTime.
                // THese values should be the sames as the ones written in the Part1 function.
                Long.toString(info.getLastPageGenerationTime()),
                wayangParamVals[2],
                wayangParamVals[3]};
        String hintNavUrl = GeneralHtml.getWayangUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveQuestionMode_, wayangParamNames, vals);
        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.questionType_, q.getType());
        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.questionID_, q.getID());
        if (paramStr != null)
            hintNavUrl += paramStr;

        //render hints
        // make sure this works for the other modes too
        hintRenderer.initHintRenderer(info, q);
        hintRenderer.setHintNavUrl(hintNavUrl);
        hintRenderer.setHintUrl(hintNavUrl);
        hintRenderer.getHintDisplay(page, hintId);
        //end hints
        //end table cell
        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");

        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, q.getType(), page);
        page.append("\r\n");
        if (q.getSelLevel() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, "", page);
            page.append("\r\n");
        } else {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, q.getSelLevel(), page);
            page.append("\r\n");
        }
        if (q.getSelHintID() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, "", page);
            page.append("\r\n");
        } else {
            GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, q.getSelHintID(), page);
            page.append("\r\n");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        page.append("\r\n");
        String catID = null;
        if (paramStr != null) {
            //catID=1&  catname=Number%20Sense&  mID=38
//http://bean.cs.umass.edu:8080/4mality/servlet/FormalityServlet?un=9&fxn=aut&mode=selq&catID=1&catname=Number%20Sense&mID=38
            catID = GeneralHtml.extractParamVal(GeneralHtml.categoryID_, paramStr);
            String catName = GeneralHtml.extractParamVal(GeneralHtml.categoryName_, paramStr);
            String linkMID = GeneralHtml.extractParamVal(GeneralHtml.moduleID_, paramStr);
            GeneralHtml.getHiddenParam(GeneralHtml.categoryID_, catID, page);
            page.append("\r\n");
            GeneralHtml.getHiddenParam(GeneralHtml.categoryName_, catName, page);
            page.append("\r\n");
            GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, linkMID, page);
            page.append("\r\n");
        }
        page.append("</FORM>").append("\r\n");
        Vector links = new Vector();
        if (paramStr != null && catID != null && (catID.length() > 0)) {

            String selLink = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectQuestionMode_);
            selLink = GeneralHtml.addToLink(GeneralHtml.studentID_, info.getUserInfo().getUserID(), selLink);


            selLink = selLink + paramStr;

            links.add(GeneralHtml.getLinkStr(selLink, "select question"));

        }

        GeneralHtml.getPageLinksFooter(links, page);
        page.append("</td></tr></table>").append("\r\n");
    }



//    public void getAuthorSurveyQuestionPage(MultipleChoiceQuestion q, SystemInfo info,
//                                      String msg, BaseHintRenderer hintRenderer,
//                                      StringBuffer page) throws Exception {
//        getAuthorSurveyQuestionPage(q, info, msg, null, hintRenderer, page, needHintID);
//    }

    public void getMultipleChoiceQuestion(Question q, SystemInfo info,
                                          String msg, String paramStr, BaseHintRenderer hintRenderer,
                                          StringBuffer page, String hintId, Map params) throws Exception {
        getQuestionPart1(q,info,msg,page,params);
        // answer controls inherited from MultipleChoiceQuestionPage
        insertAnswerControls(q, info, page);
        getQuestionPart2(q,page,info,hintRenderer,hintId,paramStr);


    }


    // only called from within student mode so the page will have module ids and other context present
    public void getShortAnswerQuestion(Question q,
                                       SystemInfo info, String msg, String paramStr, BaseHintRenderer hintRenderer,
                                       StringBuffer page, String hintId, Map params) throws Exception {
        getQuestionPart1(q,info,msg,page,params);
        //row for answer choices
        insertShortAnswerControls(q, info, page);
        getQuestionPart2(q,page,info,hintRenderer,hintId,paramStr);
    }


    //
    public void insertShortAnswerControls(Question q, SystemInfo info, StringBuffer page) throws Exception {
        page.append("<tr><td>").append("\r\n");
        String ans = q.getSubmittedAnswer();
        if (ans == null)
            ans = "";
        page.append("<input name=\"" + GeneralHtml.submittedAns_ + "\" value=\"" + ans + "\" type=\"text\">");
        page.append("</td></tr>").append("\r\n");
    }

}