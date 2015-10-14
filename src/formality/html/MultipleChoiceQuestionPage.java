package formality.html;

import formality.parser.HighlightParser;
import formality.content.*;

import java.util.Vector;

import formality.controller.AuthoringSubsystem;
import formality.controller.StudentSubsystem;
import formality.controller.FormalitySubsystem;
import formality.controller.TeacherSubsystem;
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
public class MultipleChoiceQuestionPage {

    protected HighlightParser hltParser_;
    protected boolean wayangMode=false;

    public MultipleChoiceQuestionPage() {
        hltParser_ = new HighlightParser();
    }

     public MultipleChoiceQuestionPage(boolean wayangMode) {
        hltParser_ = new HighlightParser();
         this.wayangMode=wayangMode;
    }

//    public void getAuthorSurveyQuestionPage(MultipleChoiceQuestion q, SystemInfo info,
//                                      String msg, BaseHintRenderer hintRenderer,
//                                      StringBuffer page) throws Exception {
//        getAuthorSurveyQuestionPage(q, info, msg, null, hintRenderer, page, needHintID);
//    }

    public void getAuthorQuestionPage(Question q, SystemInfo info,
                                      String msg, String paramStr, BaseHintRenderer hintRenderer,
                                      StringBuffer page, String hintId) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        hltParser_.setActiveTag(q.getActiveTag());
        if (!wayangMode)
            GeneralHtml.getSmallPageHeader("Question Viewer", false, page, info, false);
        else GeneralHtml.getSmallPageHeader("", false, page, info, false);
        page.append("<body>").append("\r\n");
        //getJavascriptLevelHintSelBlock("QForm", page);
        if (!wayangMode)
            GeneralHtml.getSmallBanner("Question Viewer", page);
        String clrurl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                (q.isSurvey()) ? AuthoringSubsystem.viewSurveyQuestionMode_ : AuthoringSubsystem.viewQuestionMode_);
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        if (!wayangMode)
            page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.evaluateQuestionMode_, null, null));
        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");
        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");
        String mediaServlet = info.getMediaServlet();

        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
//     page.append("<B>#1</B><br>").append("\r\n");
        getStem(q, info, page);
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        insertReadAloudPlayers(q, info, page);
        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        insertAnswerControls(q, info, page);
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
        String hintNavUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveQuestionMode_, null, null);
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

        GeneralHtml.getHiddenParam(GeneralHtml.questionType_,q.getType(), page);
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
            if (access == FormalitySubsystem.teacherAccess) {
                links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
                links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
            }
        }
        if (access == FormalitySubsystem.authorAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    (q.isSurvey()) ? AuthoringSubsystem.editSurveyQuestionMode_ : AuthoringSubsystem.editQuestionMode_,
                    new String[] {GeneralHtml.questionID_,q.getID(),GeneralHtml.questionType_,q.getType() }), "edit question"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        }
        if (access == FormalitySubsystem.adminAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID() ,
                    (q.isSurvey()) ? AuthoringSubsystem.editSurveyQuestionMode_ : AuthoringSubsystem.editQuestionMode_,
                    new String[] {GeneralHtml.questionID_,q.getID(),GeneralHtml.questionType_,q.getType() }), "edit question"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        }
        GeneralHtml.getPageLinksFooter(links, page);
        page.append("</td></tr></table>").append("\r\n");

    }




    public void getAuthorModuleQuestionPage(QModule m, Question q,
                                            SystemInfo info, String msg, BaseHintRenderer hintRenderer,
                                            StringBuffer page) throws Exception {
        hltParser_.setActiveTag(q.getActiveTag());
        String clrurl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveModuleMode_);
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.evalModuleMode_, GeneralHtml.moduleType_, m.getType()));
        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");
        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");


        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
        page.append("<B>#").append(m.getCurrentIndex()).append("</B><br>").append("\r\n");
        getStem(q, info, page);
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        insertReadAloudPlayers(q, info, page);
        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        page.append("<tr><td>").append("\r\n");
        insertAnswerControls(q,info,page);
//        if (q.areAnswersLoaded()) {
//            if (q.isEvaluated())
//                getEvaluatedAnswerChoices(q, info, page);
//            else
//                getAnswerChoices(q, info, page);
//        } else
//            page.append("No answer choices for this question have been loaded.").append("\r\n");
        page.append("</td></tr>").append("\r\n");
        //row for submit and clear buttons
        page.append("<tr><td>").append("\r\n");
        getCheckAnswerAndClear(page, q);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");
        page.append("<td>");//second main "TD"
        if (q.isEvaluated())
            getAnswerFeedback(q, page);
        else
            getHintResponse(q, info, page);
        page.append("</td>");
        page.append("<td valign=top width=30%>").append("\r\n"); //third main "TD"

        String hintNavUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveModuleMode_, null, null);
        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.questionType_, q.getType());
        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.questionID_, q.getID());
        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()));
        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.moduleID_, m.getID());
        hintNavUrl = GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.moduleType_, m.getType());
        String hintUrl = hintNavUrl;
        //render hints
        hintRenderer.initHintRenderer(info, q);
        hintRenderer.setHintNavUrl(hintNavUrl);
        hintRenderer.setHintUrl(hintNavUrl);
        hintRenderer.getHintDisplay(page, q.getSelHintID());
        //end hints
        //end table cell
        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");
        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
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

        page.append("</FORM>").append("\r\n");

        Vector links = new Vector();
        String modPageUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewModuleMode_,
                GeneralHtml.moduleID_, m.getID());
        modPageUrl = GeneralHtml.appendToUrl(modPageUrl, GeneralHtml.moduleType_, m.getType());
        links.add(GeneralHtml.getLinkStr(modPageUrl, "module page"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_), "select module"));
        String url = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveModuleMode_, GeneralHtml.moduleID_, m.getID());
        url = GeneralHtml.appendToUrl(url, GeneralHtml.moduleType_, m.getType());
        getPageFooter(m, url, modPageUrl, links, page);
        page.append("</body></html>").append("\r\n");
    }


    public static String makeParam(String marker, String name, String value) {
        return marker + name + "=" + value;
    }




    // only called from within student mode so the page will have module ids and other context present
    public void getModuleQuestionPage(QModule m, Question q,
                                      SystemInfo info, String msg, BaseHintRenderer hintRenderer,
                                      StringBuffer page) throws Exception {
        hltParser_.setActiveTag(q.getActiveTag());
        String clrurl =
                GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.clearQuestionMode_,
                    FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());

        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=\"#000066\"><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.evaluateQuestionMode_, GeneralHtml.moduleType_, m.getType(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));

        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");
        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");
        page.append("<div id=\"google_translate_element\"></div><script type=\"text/javascript\">\n" +
                "function googleTranslateElementInit() {\n" +
                "  new google.translate.TranslateElement({pageLanguage: 'en', layout: google.translate.TranslateElement.FloatPosition.TOP_LEFT}, 'google_translate_element');\n" +
                "}\n" +
                "</script><script type=\"text/javascript\" src=\"http://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit\"></script>");
        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
        page.append("<B>#").append(m.getCurrentIndex()).append("</B><br>").append("\r\n");
        getStem(q, info, page);
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        q.setModule(m); // DM don't know why this wasn't here before
        insertStudentReadAloudPlayers(m, q, info, page);

        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        insertAnswerControls(q, info, page);
        //row for submit and clear buttons
        page.append("<tr><td>").append("\r\n");
        getCheckAnswerAndClear(page, q);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");
        page.append("<td>");//second main "TD"
        if (q.isEvaluated())
            getAnswerFeedback(q, page);
        else
            getHintResponse(q, info, page);
        page.append("</td>");
        page.append("<td valign=top width=30%>").append("\r\n"); //third main "TD"

        //BEGIN HINT DISPLAY
        String hintLevelUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.selectLevelMode_, null, null,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.questionType_, q.getType());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.questionID_, q.getID());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()));
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.moduleID_, m.getID());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.moduleType_, m.getType());

        String hintUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewHintMode_, null, null,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.questionType_, q.getType());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.questionID_, q.getID());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()));
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.moduleID_, m.getID());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.moduleType_, m.getType());
        if (hintRenderer != null) {
        //render hints
            hintRenderer.initHintRenderer(info, q);
            hintRenderer.setHintNavUrl(hintLevelUrl);
            hintRenderer.setHintUrl(hintUrl);
            hintRenderer.getHintDisplay(page, q.getSelHintID());
        }
        //END HINT DISPLAY

        //end table cell
        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");
        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
        if (q.getSelLevel() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, "", page);
        } else {
            GeneralHtml.getHiddenParam(GeneralHtml.selLevel_, q.getSelLevel(), page);
        }
        if (q.getSelHintID() == null) {
            GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, "", page);
        } else {
            GeneralHtml.getHiddenParam(GeneralHtml.selHintID_, q.getSelHintID(), page);
            page.append("\r\n");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.questionID_, q.getID(), page);
        page.append("\r\n");
        page.append("</table>").append("\r\n");
        page.append("</FORM>").append("\r\n");
        Vector links = null; //new Vector();
        String modUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.viewModuleMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.exitQuestion_, "1");
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.moduleType_, m.getType());
        String nextQurl = GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.viewQuestionMode_, GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.exitQuestion_, "1");
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.moduleType_, m.getType());
        getPageFooter(m, nextQurl, modUrl, links, info, page);
        page.append("</body></html>").append("\r\n");
    }

    public void insertAnswerControls(Question q, SystemInfo info, StringBuffer page) throws Exception {
        page.append("<tr><td>").append("\r\n");
        if (q.areAnswersLoaded()) {
            if (q.isEvaluated())
                getEvaluatedAnswerChoices((MultipleChoiceQuestion) q, info, page);
            else
                getAnswerChoices((MultipleChoiceQuestion) q, info, page);
        } else
            page.append("No answer choices for this question have been loaded.").append("\r\n");
        page.append("</td></tr>").append("\r\n");
    }

    public void insertAnswerControls2(Question q, SystemInfo info, StringBuffer page) throws Exception {
        page.append("<tr><td>").append("\r\n");
        if (q.isEvaluated())
            getEvaluatedAnswerChoices((MultipleChoiceQuestion) q, info, page);
        else
            getAnswerChoices((MultipleChoiceQuestion) q, info, page);
        page.append("</td></tr>").append("\r\n");
    }

    void insertReadAloudPlayers(Question q, SystemInfo info, StringBuffer page) {
        if (q.getAudioFile() != null) // && q.getAudio().length > 0)
            page.append("English: <p id=\"English\">English</p>  \n" +
                    "           <script type=\"text/javascript\">  \n" +
                    "           AudioPlayer.embed(\"English\", {soundFile: escape(\"" +
                    info.getMediaServlet() + "?" + GeneralHtml.questionID_ + "=" + q.getID() +
                    "&" + GeneralHtml.language_ + "=eng\")});\n" +
                    "           </script>");
        if (q.getSpanishAudioFile() != null)
            page.append("<br/>Espanol:  <p id=\"Espanol\">Espanol</p>  \n" +
                    "           <script type=\"text/javascript\">  \n" +
                    "           AudioPlayer.embed(\"Espanol\", {soundFile: escape(\"" +
                    info.getMediaServlet() + "?" + GeneralHtml.questionID_ + "=" + q.getID() +
                    "&" + GeneralHtml.language_ + "=spanish\")});\n" +
                    "           </script>");
    }

    private void insertStudentReadAloudPlayers(QModule m, Question q, SystemInfo info, StringBuffer page) throws Exception {
        if (q.getAudioFile() != null ) //&& q.getAudio().length > 0)
            page.append("English: <p id=\"English\">English</p>  \n" +
                    "           <script type=\"text/javascript\">  \n" +
//                    "           AudioPlayer.embed(\"English\", {soundFile: escape(\""+info.getMediaServlet()+"?"+GeneralHtml.questionID_+"=" + q.getID() + "&"+GeneralHtml.language_+"=eng\")});\n" +
                    "           AudioPlayer.embed(\"English\", {soundFile: escape(\"" + info.getFormalityServlet() +
                    makeParam("?", GeneralHtml.questionID_, q.getID()) +
                    makeParam("&", FormalitySubsystem.modeParam, StudentSubsystem.readAloud) +
                    makeParam("&", FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam) +
                    makeParam("&", FormalitySubsystem.userIDParam, info.getUserID()) +
                    makeParam("&", GeneralHtml.language_, "eng") +
                    makeParam("&", GeneralHtml.moduleID_, m.getID()) +
                    makeParam("&", GeneralHtml.moduleType_, m.getType()) +
                    // TODO this is gonna fail because the questionID passed in (a real one) is not part of the dummy
                    // module that is created to fake our way through these methods.
                    makeParam("&", GeneralHtml.questionIndex_, Integer.toString(q.getModIndex())) +
                    "\")});\n" +
                    "           </script>");
        if (q.getSpanishAudioFile() != null)
            page.append("<br/>Espanol:  <p id=\"Espanol\">Espanol</p>  \n" +
                    "           <script type=\"text/javascript\">  \n" +
//                    "           AudioPlayer.embed(\"Espanol\", {soundFile: escape(\""+info.getMediaServlet()+"?"+GeneralHtml.questionID_+"=" + q.getID() + "&"+GeneralHtml.language_+"=spanish\")});\n" +
                    "           AudioPlayer.embed(\"Espanol\", {soundFile: escape(\"" + info.getFormalityServlet() +
                    makeParam("?", GeneralHtml.questionID_, q.getID()) +
                    makeParam("&", FormalitySubsystem.modeParam, StudentSubsystem.readAloud) +
                    makeParam("&", FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam) +
                    makeParam("&", FormalitySubsystem.userIDParam, info.getUserID()) +
                    makeParam("&", GeneralHtml.language_, "spanish") +
                    makeParam("&", GeneralHtml.moduleID_, m.getID()) +
                    makeParam("&", GeneralHtml.moduleType_, m.getType()) +
                    makeParam("&", GeneralHtml.questionIndex_, Integer.toString(q.getModIndex())) +
                    "\")});\n" +
                    "           </script>");
    }

    public void getTeacherModuleQuestionPage(QModule m, String sID, Question q,
                                               SystemInfo info, String msg, BaseHintRenderer hintRenderer,
                                               StringBuffer page) throws Exception {
        hltParser_.setActiveTag(q.getActiveTag());
        String clrurl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.clearQuestionMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.evaluateQuestionMode_, GeneralHtml.moduleType_, m.getType(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");
        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");

        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
        page.append("<B>#").append(m.getCurrentIndex()).append("</B><br>").append("\r\n");
        getStem(q, info, page);
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        insertReadAloudPlayers(q, info, page);
        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        insertAnswerControls(q,info,page);

        //row for submit and clear buttons
        page.append("<tr><td>").append("\r\n");
        getCheckAnswerAndClear(page, q);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");
        page.append("<td>");//second main "TD"
        if (q.isEvaluated())
            getAnswerFeedback(q, page);
        else
            getHintResponse(q, info, page);
        page.append("</td>");
        page.append("<td valign=top width=30%>").append("\r\n"); //third main "TD"

        //hint display- first construct the links
        String hintLevelUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.selectLevelMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.questionType_, q.getType());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.questionID_, q.getID());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()));
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.moduleID_, m.getID());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.moduleType_, m.getType());
        hintLevelUrl = GeneralHtml.appendToUrl(hintLevelUrl, GeneralHtml.studentID_, sID);
        String hintUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.viewHintMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.questionType_, q.getType());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.questionID_, q.getID());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()));
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.moduleID_, m.getID());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.moduleType_, m.getType());
        hintUrl = GeneralHtml.appendToUrl(hintUrl, GeneralHtml.studentID_, sID);

        // now render hints
        hintRenderer.initHintRenderer(info, q);
        hintRenderer.setHintNavUrl(hintLevelUrl);
        hintRenderer.setHintUrl(hintUrl);
        hintRenderer.getHintDisplay(page, q.getSelHintID());
        //end hints

        //end table cell
        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");

        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
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
        GeneralHtml.getHiddenParam(GeneralHtml.studentID_, sID, page);
        page.append("\r\n");
        page.append("</FORM>").append("\r\n");
        Vector links = null; //new Vector();
        String modUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.viewModuleMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.exitQuestion_, "1");
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.moduleType_, m.getType());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.studentID_, sID);
        //  links.add(GeneralHtml.getLinkStr(modUrl, "module page"));
        //  String homeUrl=GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_);
        // homeUrl=GeneralHtml.appendToUrl(homeUrl, GeneralHtml.exitQuestion_, "1");
        // links.add(GeneralHtml.getLinkStr(homeUrl, "home"));

        String nextQurl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.viewQuestionMode_, GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.exitQuestion_, "1");
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.moduleType_, m.getType());
        nextQurl = GeneralHtml.appendToUrl(nextQurl, GeneralHtml.studentID_, sID);
        getPageFooter(m, nextQurl, modUrl, links, page);
        page.append("</body></html>").append("\r\n");
    }

    public void getTestModuleQuestionPage(QModule m, Question q,
                                            SystemInfo info, String msg, boolean showClock,
                                            StringBuffer page) throws Exception {
        hltParser_.setActiveTag(q.getActiveTag());
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                StudentSubsystem.evaluateTestQuestionMode_, GeneralHtml.moduleType_, m.getType(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");

        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");

        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
        page.append("<B>#").append(m.getCurrentIndex()).append("</B><br>").append("\r\n");
        //getStem(q, info, page);

        page.append(hltParser_.removeTags(q.getStem()));
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        insertReadAloudPlayers(q, info, page);
        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        insertAnswerControls(q,info,page);
        //row for submit and clear buttons
        page.append("<tr><td>").append("\r\n");
        getSubmit(page);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");
        page.append("<td>&nbsp;</td>");//second main "TD"

        //hidden params
        String qOrd = Integer.toString(m.getCurrentIndex());
        if (qOrd != null) {
            GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, qOrd, page);
            page.append("\r\n");
        }
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, "mc", page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
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
        page.append("</FORM>").append("\r\n");
        page.append("<td bgcolor=#eeeeee valign=top width=30%>").append("\r\n"); //third main "TD"
        page.append(GeneralHtml.linkFont_).append("Test Mode:<br>");
        page.append("You are on question ").append(m.getCurrentIndex());
        page.append(" of ").append(m.getNumLinkedQuestions()).append(".<br>");
        if (showClock) {
            page.append("<form NAME=\"clocktext\">");
            page.append("You started this module at: <br>");
            page.append("<input TYPE=\"text\" NAME=\"started\" VALUE=\"").append(m.getStartTime()).append("\" SIZE=8 MAXLENGTH=8 READONLY><br>");
            page.append("The current time is: <br>");
            page.append("<input TYPE=\"text\" NAME=\"clock\" VALUE=\"\" SIZE=8 MAXLENGTH=8 READONLY><br></font>");
            page.append("</form>");
        }
        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");
        page.append("<p>").append("\r\n");
        Vector links = null; //new Vector();
        String modUrl = GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.viewTestModuleMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.exitQuestion_, "1");
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.moduleType_, m.getType());

        //  links.add(GeneralHtml.getLinkStr(modUrl, "module page"));
        // String homeUrl=GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_);
        // homeUrl=GeneralHtml.appendToUrl(homeUrl, GeneralHtml.exitQuestion_, "1");
        //  links.add(GeneralHtml.getLinkStr(homeUrl, "home"));

        getPageFooter(m, null, modUrl, links, page);

        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");
        page.append("</body></html>").append("\r\n");
    }

    public void getTeacherTestModuleQuestionPage(QModule m, String sID, Question q,
                                                   SystemInfo info, String msg, boolean showClock,
                                                   StringBuffer page) throws Exception {
        showClock = false;
        hltParser_.setActiveTag(q.getActiveTag());
        String clrurl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.clearTestQuestionMode_,
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        GeneralHtml.getJavascriptClearQ(clrurl, page);
        GeneralHtml.getJavascriptShowTime(page);
        if (msg != null)
            page.append("<font color=orange><h3>").append(msg).append("</h3></font>");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                TeacherSubsystem.evaluateTestQuestionMode_, GeneralHtml.moduleType_, m.getType(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
        page.append("\" METHOD=POST NAME=\"QForm\">").append("\r\n");
        page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" HEIGHT=\"450\" border=1><tr><td>\r\n");
        page.append("<!-- MAIN TABLE -->").append("\r\n");

        page.append("<table border=0 width=100% HEIGHT=85%><tr>");
        page.append("<td bgcolor=#ffffff valign=top width=40%>\r\n");  //first main "TD"
        page.append("<table border=0 width=100% height=100%>");
        //row for stem
        page.append("<tr><td>").append("\r\n");

        page.append("<!----------------------------------- QUESTION STEM ---->").append("\r\n");
        page.append("<B>#").append(m.getCurrentIndex()).append("</B><br>").append("\r\n");
        //getStem(q, info, page);
        page.append(hltParser_.removeTags(q.getStem()));
        page.append("<br><br>");
        page.append("\r\n").append("<!------------- END QUESTION STEM ------><br>").append("\r\n");
        insertReadAloudPlayers(q, info, page);
        page.append("</td></tr>").append("\r\n");
        //row for answer choices
        insertAnswerControls(q,info,page);
        //row for submit and clear buttons
        page.append("<tr><td>").append("\r\n");
        getSubmit(page);
        page.append("</td></tr></table>").append("\r\n");
        page.append("</td>").append("\r\n");
        page.append("<td>&nbsp;</td>");//second main "TD"

        //hidden params
        GeneralHtml.getHiddenParam(GeneralHtml.questionType_, q.getType(), page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleID_, m.getID(), page);
        page.append("\r\n");
        GeneralHtml.getHiddenParam(GeneralHtml.moduleType_, m.getType(), page);
        GeneralHtml.getHiddenParam(GeneralHtml.questionIndex_, Integer.toString(m.getCurrentIndex()), page);
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
        GeneralHtml.getHiddenParam(GeneralHtml.studentID_, sID, page);
        page.append("</FORM>").append("\r\n");
        page.append("<td <td bgcolor=#eeeeee valign=top width=30%>").append("\r\n"); //third main "TD"
        page.append(GeneralHtml.linkFont_).append("Test Mode:<br>");
        page.append("You are on question ").append(m.getCurrentIndex());
        page.append(" of ").append(m.getNumLinkedQuestions()).append(".<br>");
        if (showClock) {
            page.append("<form NAME=\"clocktext\">");
            page.append("You started this module at: <br>");
            page.append("<input TYPE=\"text\" NAME=\"started\" VALUE=\"").append(m.getStartTime()).append("\" SIZE=8 MAXLENGTH=8 READONLY><br>");
            page.append("The current time is: <br>");
            page.append("<input TYPE=\"text\" NAME=\"clock\" VALUE=\"\" SIZE=8 MAXLENGTH=8 READONLY><br></font>");
            page.append("</form>");
        }
        page.append("</td>").append("\r\n");
        page.append("</tr></table>").append("\r\n");
        Vector links = null; //new Vector();
        String modUrl = GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.viewTestModuleMode_,
                GeneralHtml.moduleID_, m.getID(),
                FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.moduleType_, m.getType());
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.studentID_, sID);
        modUrl = GeneralHtml.appendToUrl(modUrl, GeneralHtml.exitQuestion_, "1");

        //  links.add(GeneralHtml.getLinkStr(modUrl, "module page"));
        // String homeUrl=GeneralHtml.getStudentUrl(info.getServletRootAndID(), StudentSubsystem.studentHomeMode_);
        // homeUrl=GeneralHtml.appendToUrl(homeUrl, GeneralHtml.exitQuestion_, "1");
        //  links.add(GeneralHtml.getLinkStr(homeUrl, "home"));

        getPageFooter(m, null, modUrl, links, page);
        page.append("</body></html>").append("\r\n");
    }

    public void getStem(Question q, SystemInfo info,
                        StringBuffer page) throws Exception {
        hltParser_.parseStem(q.getStem(), page, info, false);
    }

    public void getEvaluatedAnswerChoices(MultipleChoiceQuestion q, SystemInfo info,
                                          StringBuffer page) throws Exception {
        boolean isCorrect = q.isCorrect();
        String selAns = q.getSubmittedAnswer();
        String corAns = q.getCorrectAnswer();
        boolean selA = false, selB = false, selC = false, selD = false;
        boolean corA = false, corB = false, corC = false, corD = false;
        if (selAns.equalsIgnoreCase("A"))
            selA = true;
        if (selAns.equalsIgnoreCase("B"))
            selB = true;
        if (selAns.equalsIgnoreCase("C"))
            selC = true;
        if (selAns.equalsIgnoreCase("D"))
            selD = true;
        if (corAns.equalsIgnoreCase("A"))
            corA = true;
        if (corAns.equalsIgnoreCase("B"))
            corB = true;
        if (corAns.equalsIgnoreCase("C"))
            corC = true;
        if (corAns.equalsIgnoreCase("D"))
            corD = true;
        //  if(isCorrect)
        page.append("<table><tr>").append("\r\n");
        page.append("<td valign=\"top\">");
        if (selA && corA)
            page.append(GeneralHtml.corrChoice_);
        else if (selA)
            page.append(GeneralHtml.incorrChoice_);
        else
            page.append("&nbsp;&nbsp;");
        page.append("</td>");
        page.append("<td valign=\"top\"><INPUT type=\"radio\" value=\"A\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"");
        if (selA)
            page.append(" checked></td>\r\n");
        else
            page.append("></td>\r\n");
        page.append("<td valign=top><b>&nbsp;A.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceA(), page, info);
        page.append("\r\n");
        page.append("</td></tr><tr>").append("\r\n");
        page.append("<td valign=\"top\">");
        if (selB && corB)
            page.append(GeneralHtml.corrChoice_);
        else if (selB)
            page.append(GeneralHtml.incorrChoice_);
        else
            page.append("&nbsp;&nbsp;");
        page.append("</td>");
        page.append("<td valign=top><INPUT type=\"radio\" value=\"B\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"");
        if (selB)
            page.append(" checked></td>\r\n");
        else
            page.append("></td>\r\n");
        page.append("<td valign=top><b>&nbsp;B.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceB(), page, info);
        page.append("\r\n");
        page.append("</td></tr><tr>").append("\r\n");
        page.append("<td valign=\"top\">");
        if (selC && corC)
            page.append(GeneralHtml.corrChoice_);
        else if (selC)
            page.append(GeneralHtml.incorrChoice_);
        else
            page.append("&nbsp;&nbsp;");
        page.append("</td>");
        page.append("<td valign=top><INPUT type=\"radio\" value=\"C\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"");
        if (selC)
            page.append(" checked></td>\r\n");
        else
            page.append("></td>\r\n");
        page.append("<td valign=top><b>&nbsp;C.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceC(), page, info);
        page.append("\r\n");
        page.append("</td></tr><tr>").append("\r\n");
        page.append("<td valign=\"top\">");
        if (selD && corD)
            page.append(GeneralHtml.corrChoice_);
        else if (selD)
            page.append(GeneralHtml.incorrChoice_);
        else
            page.append("&nbsp;&nbsp;");
        page.append("</td>");
        page.append("<td valign=top><INPUT type=\"radio\" value=\"D\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"");
        if (selD)
            page.append(" checked></td>\r\n");
        else
            page.append("></td>\r\n");
        page.append("<td valign=top><b>&nbsp;D.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceD(), page, info);
        page.append("\r\n");
        page.append("</td></tr></table>").append("\r\n");
    }

    public void getAnswerChoices(MultipleChoiceQuestion q, SystemInfo info,
                                 StringBuffer page) throws Exception {
        page.append("<table><tr>").append("\r\n");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT type=\"radio\" value=\"A\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;A.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceA(), page, info);
        page.append("\r\n");
        page.append("</td>");
        if (q.isSequentialLayout())
            page.append("</tr><tr>").append("\r\n");
        else page.append("\r\n");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT type=\"radio\" value=\"B\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;B.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceB(), page, info);
        page.append("\r\n");
        page.append("</td></tr><tr>").append("\r\n");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT type=\"radio\" value=\"C\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;C.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceC(), page, info);
        page.append("\r\n");
        page.append("</td>");
        if (q.isSequentialLayout())
            page.append("</tr><tr>").append("\r\n");
        else page.append("\r\n");
        page.append("<td>&nbsp;&nbsp;</td><td valign=top><INPUT type=\"radio\" value=\"D\" name=\"");
        page.append(GeneralHtml.submittedAns_).append("\"></td>\r\n");
        page.append("<td valign=top><b>&nbsp;D.&nbsp;</b></td><td>").append("\r\n");
        hltParser_.parseText(q.getAnswerChoiceD(), page, info);
        page.append("\r\n");
        page.append("</td></tr></table>").append("\r\n");
    }

    public void getAnswerFeedback(Question q, StringBuffer page) {
        if (q.isEvaluated()) {
            String selAns = q.getSubmittedAnswer();
            if (selAns == null)
                page.append(GeneralHtml.getIncorrectStyleFormat("No answer was selected. Look over the question again, click on an answer choice and then submit.")).append("\r\n");
            else {
                if (q.isCorrect()) {
                    // page.append("&nbsp;Answer Choice ").append(selAns);
                    page.append(GeneralHtml.getCorrectStyleFormat("Your answer is Correct.<br>"));
                } else {
                    // page.append("&nbsp;Answer Choice ").append(selAns);
                    page.append(GeneralHtml.getIncorrectStyleFormat("Your answer is Incorrect.<br>"));
                }
                addAnswerSpecificFeedback(q, page, selAns);
            }
        } else
            page.append("&nbsp;\r\n");
    }

    public void addAnswerSpecificFeedback(Question q, StringBuffer page, String selAns) {
        if (q.isMultipleChoice()) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
            String feedback = "";
//TODO- parse feedback for highlights?
//*hltParser_.parseStem(q.getAnswerFeedback(), page, info)*/).
            if (selAns.equalsIgnoreCase("A"))
                feedback = mcq.getAnsChoiceAFeedback();
            if (selAns.equalsIgnoreCase("B"))
                feedback = mcq.getAnsChoiceBFeedback();
            if (selAns.equalsIgnoreCase("C"))
                feedback = mcq.getAnsChoiceCFeedback();
            if (selAns.equalsIgnoreCase("D"))
                feedback = mcq.getAnsChoiceDFeedback();
            page.append(feedback).append("&nbsp;\r\n");
        }
    }

    public void getSubmitAndClear(StringBuffer page) {
        page.append("<table width=100%><tr><td align=\"left\">");
        page.append("<INPUT type=\"SUBMIT\" value=\"SUBMIT ANSWER\" name=\"subAns\">");
        page.append("</td><td align=\"right\">");
        page.append("<INPUT type=\"button\" value=\"CLEAR\" name=\"clear\" onClick=\"checkClear(QForm)\">");
        page.append("</td></tr></table>").append("\r\n");
    }

    public void getSubmit(StringBuffer page) {
        page.append("<table width=100%><tr><td align=\"left\">");
        page.append("<INPUT type=\"SUBMIT\" value=\"SUBMIT ANSWER\" name=\"subAns\">");
        // page.append("</td><td align=\"right\">");
        // page.append("<INPUT type=\"button\" value=\"CLEAR\" name=\"clear\" onClick=\"checkClear(QForm)\">");
        page.append("</td></tr></table>").append("\r\n");
    }

    public void getClear(StringBuffer page) {
        page.append("<table width=100%><tr><td align=\"left\">");
        
        page.append("<INPUT type=\"button\" value=\"CLEAR\" name=\"clear\" onClick=\"checkClear(QForm)\">");
        page.append("</td></tr></table>").append("\r\n");
    }

    public void getCheckAnswerAndClear(StringBuffer page, Question q) {
        page.append("<table width=100%><tr><td align=\"left\">");
        // sa survey questions need this check so button is omitted.
        // mc survey questions don't come in here
        if (!(q.isSurvey() && q.getType().equals("sa")))
            page.append("<INPUT type=\"SUBMIT\" value=\"CHECK ANSWER\" name=\"checkAns\">");
        page.append("</td><td align=\"right\">");
        page.append("<INPUT type=\"button\" value=\"CLEAR\" name=\"clear\" onClick=\"checkClear(QForm)\">");
        page.append("</td></tr></table>").append("\r\n");
    }

    //produces <td>...</td>
    public void getHintResponse(Question q, SystemInfo info,
                                StringBuffer page) throws Exception {
        String hintID = q.getSelHintID();
        if (hintID == null || hintID.equals("")) {
            page.append("&nbsp;").append("\r\n");
        } else {
            page.append("<table><tr><td>").append("\r\n");

            hltParser_.parseStem(q.getHintResponse(hintID), page, info, false);
            // hltParser_.getHighlightedTD(q.getHintResponse(hintID), page);
            page.append("</td></tr></table>").append("\r\n");
        }
    }

    public void getNavigation(QModule m, int selQNum, String url, String exitUrl,
                              Vector links, StringBuffer page) throws Exception {
        page.append("<!--NAVIGATION TABLE -->").append("\r\n");
        page.append("<table width=100%><tr><td>").append("\r\n");
        page.append("<!--questions in module navigation -->").append("\r\n");

        getQuestionNavTable(m, url, exitUrl, page);

        page.append("</TD><td align=right>").append('\r').append('\n');
        page.append("<!--system navigation -->").append('\r').append('\n');

        getSystemNavigation(links, page);

        page.append("</td></TR></TABLE>").append("\r\n");
        page.append("<!--END NAVIGATION -->").append("\r\n");
    }

    public void getSystemNavigation(Vector links, StringBuffer page) {
        page.append("<TABLE bgColor=#eeeeee border=0><TR><td>").append('\r').append('\n');
        page.append("<font color=\"#9586f9\" face=\"Comic Sans MS\">").append("\r\n");
        if (links != null) {
            for (int i = 0; i < links.size(); i++) {
                String curLink = (String) links.get(i);
                if (i + 1 != links.size())
                    page.append(curLink).append("&nbsp;|&nbsp;");
                else
                    page.append(curLink);
            }
        }
        page.append("</font></td></TR></TABLE>").append('\r').append('\n');
    }

    public static void getQuestionNavTable(QModule m, String url,
                                    String exitUrl, StringBuffer page) throws Exception {
        page.append("<TABLE bgColor=#eeeeee border=0><TR>").append("\r\n");
        page.append("<TD>");
        page.append(GeneralHtml.linkFont_).append("questions: ");
        page.append("</font></TD>").append("\r\n");
        for (int i = 1; i <= m.getNumLinkedQuestions(); i++) {
            if (m.getCurrentIndex() == i) {
                page.append("<td align=middle bgcolor=").append(GeneralHtml.navSelectedColor_).append(">");
                page.append("&nbsp;&nbsp;").append(i).append("&nbsp;&nbsp;");
            } else {
                page.append("<td align=middle bgColor=#dddddd>");
                if (url != null && !url.equals("")) {
                    Question q = m.getLinkedQuestion(i);
                    String curUrl = GeneralHtml.appendToUrl(url, GeneralHtml.questionID_, q.getID());
                    curUrl = GeneralHtml.appendToUrl(url, GeneralHtml.questionType_, q.getType());
                    curUrl = GeneralHtml.appendToUrl(url, GeneralHtml.questionIndex_, Integer.toString(i));
                    page.append("<a href=\"").append(curUrl).append("\">&nbsp;&nbsp;");
                    page.append(i).append("&nbsp;&nbsp;</a>");
                } else {
                    page.append("&nbsp;&nbsp;").append(i).append("&nbsp;&nbsp;");
                }
            }
            page.append("</td>").append("\r\n");
        }//end for

        page.append("<TD align=middle>&nbsp;<a href=\"");
        page.append(exitUrl).append("\">").append(GeneralHtml.linkFont_).append("exit");
        page.append("</font></a></td>");

        page.append("<TD align=middle>&nbsp;</TD>").append("\r\n");
        page.append("<TD align=middle>&nbsp;</TD>").append("\r\n");
        page.append("<TD align=middle>&nbsp;</TD>").append("\r\n");
        page.append("<TD align=middle>&nbsp;</TD>").append("\r\n");
        page.append("<TD align=middle>&nbsp;</TD>").append("\r\n");
        page.append("</TR></TABLE>").append("\r\n");
    }

    public static void getQuestionNavTableWithScores(QModule m, String url,
                                                     String exitUrl, SystemInfo si, StringBuffer page) throws Exception {
        page.append("<TABLE bgColor=#eeeeee border=0><TR>").append("\r\n");
        page.append("<TD>");
        page.append(GeneralHtml.linkFont_).append("questions: ");
        page.append("</font></TD>").append("\r\n");
        for (int i = 1; i <= m.getNumLinkedQuestions(); i++) {
            if (m.getCurrentIndex() == i) {
                page.append("<td align=middle bgcolor=").append(GeneralHtml.navSelectedColor_).append(">");
                page.append("&nbsp;&nbsp;").append(i).append("&nbsp;&nbsp;");
            } else {
                page.append("<td align=middle bgColor=#dddddd>");
                if (url != null && !url.equals("")) {
                    Question q = m.getLinkedQuestion(i);
                    String curUrl = GeneralHtml.appendToUrl(url, GeneralHtml.questionID_, q.getID());
                    curUrl = GeneralHtml.appendToUrl(url, GeneralHtml.questionType_, q.getType());
                    curUrl = GeneralHtml.appendToUrl(url, GeneralHtml.questionIndex_, Integer.toString(i));
                    page.append("<a href=\"").append(curUrl).append("\">&nbsp;&nbsp;");
                    page.append(i).append("&nbsp;&nbsp;</a>");
                } else {
                    page.append("&nbsp;&nbsp;").append(i).append("&nbsp;&nbsp;");
                }
            }
            page.append("</td>").append("\r\n");
        }//end for

        page.append("<TD align=middle>&nbsp;<a href=\"");
        page.append(exitUrl).append("\">").append(GeneralHtml.linkFont_).append("exit");
        page.append("</font></a></td>");
        page.append("</tr>");
        page.append("<tr><td align=\"middle\" ");
        page.append(GeneralHtml.linkFont_).append("scores: </font>");
        page.append("</td>");
        GeneralHtml.getStudentPracticeModuleScoreTable(m.getLinkedQuestions(), m.getID(), si, page);
        page.append("</tr></table>").append("\r\n");
    }

    protected void getJavascriptLevelHintSelBlock(String form, StringBuffer page) {
        GeneralHtml.getJavascriptBegin(page);
        GeneralHtml.getJavascriptSetHiddenVal(form, GeneralHtml.selLevel_, page);
        page.append("\r\n");
        GeneralHtml.getJavascriptSetHiddenVal(form, GeneralHtml.selHintID_, page);
        page.append("\r\n");
        GeneralHtml.getJavascriptSetHiddenVal(form, FormalitySubsystem.modeParam, page);
        GeneralHtml.getJavascriptEnd(page);
    }

    public static void getPageFooter(QModule m, String url, String exitUrl,
                              Vector links, StringBuffer page) throws Exception {
        page.append("<table WIDTH=100%><TR bgColor=#eeeeee>").append("\r\n");
        page.append("<TD><table width=100%><tr><td>").append("\r\n");
        getQuestionNavTable(m, url, exitUrl, page);
        page.append("</TD><td align=right>").append("\r\n");
        page.append("<TABLE bgColor=#eeeeee border=0><TR><TD>").append("\r\n");
        page.append("<font color=\"#9586f9\" face=\"Comic Sans MS\">").append("\r\n");
        if (links != null) {
            for (int i = 0; i < links.size(); i++) {
                String curLink = (String) links.get(i);
                if (i + 1 != links.size())
                    page.append(curLink).append("&nbsp;|&nbsp;");
                else
                    page.append(curLink);
            }
        }
        page.append("</font></td>");
        page.append("</TR></TABLE></td></TR></TABLE></td></TR></TABLE>").append("\r\n");
    }

    public void getPageFooter(QModule m, String url, String exitUrl,
                              Vector links, SystemInfo si, StringBuffer page) throws Exception {
        page.append("<table WIDTH=100%><tr bgColor=#eeeeee>").append("\r\n");
        page.append("<td><table width=100%><tr><td>").append("\r\n");
        getQuestionNavTableWithScores(m, url, exitUrl, si, page);
        page.append("</td>");
        page.append("<td align=right>").append("\r\n");
        page.append("<TABLE bgColor=#eeeeee border=0><TR><TD>").append("\r\n");
        page.append("<font color=\"#9586f9\" face=\"Comic Sans MS\">").append("\r\n");
        if (links != null) {
            for (int i = 0; i < links.size(); i++) {
                String curLink = (String) links.get(i);
                if (i + 1 != links.size())
                    page.append(curLink).append("&nbsp;|&nbsp;");
                else
                    page.append(curLink);
            }
        }
        page.append("</font></td>");
        page.append("</TR></TABLE>");
        page.append("</td></TR></TABLE></td></TR></TABLE>").append("\r\n");
    }





}

