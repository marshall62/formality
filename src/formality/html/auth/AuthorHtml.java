package formality.html.auth;

import formality.content.*;
import formality.FileReaderWriter;
import formality.servlet.QuestionFileUploadServlet;
import formality.model.UserInfo;
import formality.Util.DataTuple;

import java.util.Vector;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;

import formality.content.SystemInfo;
import formality.controller.*;
import formality.html.hints.BaseHintRenderer;
import formality.html.GeneralHtml;
import formality.html.MultipleChoiceQuestionPage;
import formality.html.MultipleChoiceSurveyQuestionPage;
import formality.html.ShortAnswerQuestionPage;
import formality.parser.HighlightParser;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 */
public class AuthorHtml {
    public AuthorHtml() {
    }

    public void getAuthorHomePage(String msg, SystemInfo info, StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        //String authLogin = info.getUserLogin();
        // boolean canEdit = false;
        String title = "Author Home";
        if (access == FormalitySubsystem.adminAccess)
            title = "Admin Home";
        GeneralHtml.getSmallPageHeader(title, false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner(title, page);

        if (msg != null) {
            page.append("<font face=\" Comic Sans MS\", color = #9999ff>");
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
            page.append("</font>");
        }
        /*
       page.append("<script src=\"/4mality/testjax.js\"></script>\n");
             page.append("<form>\n");
       page.append("First Name:\n");
       page.append("<input type=\"text\" id=\"txt1\" onkeyup=\"showHint(this.value)\">\n");
       page.append("</form>\n");
       page.append("<p>Suggestions: <span id=\"txtHint\"></span></p>\n");
        */
        genNewQuestion(info, page);
        genQuestionCategories(info, page);
        genModuleControls(info, page);
        if (access == FormalitySubsystem.adminAccess) {
            genHintControls(info, page);
            genClassAndUserControls(info, page);
        }

        Vector links = new Vector();

        links.add(GeneralHtml.getLinkStr(GeneralHtml.getLogoutUrl(info.getServletPath(), info.getUserID()), "log out"));

        GeneralHtml.getGeneralPageFooter(links, page);
    }

    private void genClassAndUserControls(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<br>");
        // Add Class/Student/Teacher creation buttons
        page.append("<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=60%>\r\n");
        page.append("<tr><td colspan=2 bgcolor=#dddddd>");
        page.append("<b>Class/User Creation</b></td></tr>");
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newClassMode_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a></td>").append("\r\n");
        page.append("<td>Create a New Class</td></tr>").append("\r\n");

        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newTeacherMode_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a></td>").append("\r\n");
        page.append("<td>Create a New Teacher Account</td></tr>").append("\r\n");

        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newStudentMode_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a>");
        page.append("</td>").append("\r\n");
        page.append("<td>Create a New Student Account</td></tr>").append("\r\n");

        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newStudentsUpload_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a>");
        page.append("</td>").append("\r\n");
        page.append("<td>Create a New Students from an input file</td></tr>").append("\r\n");

        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.classListOperations_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"class List Operations\" BORDER=0></a>");
        page.append("</td>").append("\r\n");
        page.append("<td>Perform operations on students from a class list</td></tr>").append("\r\n");

        page.append("</table>");
    }

    private void genHintControls(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<br>");
        page.append("<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=60%>\r\n");
        page.append("<tr><td colspan=2 bgcolor=#dddddd>");
        page.append("<b>Hints</b></td></tr>");
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectHintMode_));
        page.append("\"><img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a></td>");
        page.append("<td>Edit/View All Hints</td></tr></table>").append("\r\n");
    }

    private void genModuleControls(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<br><TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=60%>\r\n");
        page.append("<tr><td colspan=2 bgcolor=#dddddd>");
        page.append("<b>Modules</b><br></td></tr>");
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newModuleMode_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a>");
        page.append("</td>");
        page.append("<td>Create a New Module</td></tr>").append("\r\n");

        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a>");
        page.append("</td>");
        page.append("<td>Edit/View Existing Modules</td></tr>").append("\r\n");
//        page.append("</td></tr></table>");
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectClassMode_)).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a>");
        page.append("</td>");
        page.append("<td>Edit Class Module Linkage</td></tr>").append("\r\n");
        page.append("</table>");
    }

    private void genNewQuestion(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=60%>\r\n");
        page.append("<tr bgcolor=\"#dddddd\"><td colspan=\"2\">");
        page.append("<b>New Questions</b>");
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        // Row for New multiple choice question
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newQuestionMode_, GeneralHtml.questionType_, "mc")).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a></td>").append("\r\n");
        page.append("<td>Create a New Multiple Choice Question</td></tr>").append("\r\n");

        // Row for new survey multi choice question
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newSurveyQuestionMode_, GeneralHtml.questionType_, "mc")).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a></td>").append("\r\n");
        page.append("<td>Create a New Survey Multiple Choice Question</td></tr>").append("\r\n");

        // Row for short answer question
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newQuestionMode_, GeneralHtml.questionType_, "sa")).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a>");
        page.append("</td>").append("\r\n");
        page.append("<td>Create a New Short Answer Question</td></tr>").append("\r\n");

        // Row for short answer survey question
        page.append("<tr bgcolor=#eeeeee>").append("\r\n");
        page.append("<td align=right width=10%>").append("\r\n");
        page.append("<a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.newSurveyQuestionMode_, GeneralHtml.questionType_, "sa")).append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"new question\" BORDER=0></a>");
        page.append("</td>").append("\r\n");
        page.append("<td>Create a New Short Answer Survey Question</td></tr></table>").append("\r\n");
//        page.append("<td><font color=#bbbbbb>Create a New Short Answer Question</font></td></tr></table>").append("\r\n");
//        page.append("</td></tr>\n");
    }

    private void genQuestionCategories(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<br>");
        page.append("<TABLE BORDER=\"0\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\" WIDTH=60%>\r\n");
        page.append("<tr><td colspan=\"3\" bgcolor=\"#dddddd\"> <b>Existing Questions</b><br></td></tr>");

        page.append("<tr bgcolor=#dddddd>");
        page.append("<td>Select</td><td>Category</td><td>Count</td></tr>").append("\r\n");
        Iterator cit = info.getCategoryIDs();
        while (cit.hasNext()) {
            String cID = (String) cit.next();
            page.append("<tr bgcolor=#eeeeee><td align=right width=10%><a href=\"");
            Vector params = new Vector();
            params.add(GeneralHtml.categoryID_);
            params.add(cID);
            params.add(GeneralHtml.categoryName_);
            params.add(info.getCategoryName(cID));
            page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectQuestionMode_,
                    params));
            page.append("\"><img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a></td><td>");
            page.append(info.getCategoryName(cID));
            page.append("</td><td width=10%>").append(info.getCatCount(cID)).append("</td></tr>").append("\r\n");
        }
        page.append("<tr bgcolor=#eeeeee><td align=right width=10%><a href=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.questionInventoryMode_));
        page.append("\"><img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a></td>");

        page.append("<td>View Full Question Inventory</td><td width=10%></td></tr>");
        page.append("</table>");
    }

    public void getQuestionInventoryPage(Vector data, SystemInfo info, StringBuffer page) throws Exception {
        GeneralHtml.getPageHeader("Question Inventory", page);
        page.append("<body>").append("\r\n");
        GeneralHtml.getPageBanner("Question Inventory", page);
        page.append("<h2>This report shows only active questions, i.e. Ready Status = 1.</h2>");
        page.append("<TABLE BORDER=\"1\"  BGCOLOR=\"#F0F0F0\" CELLPADDING=\"1\" CELLSPACING=\"2\">\r\n");
        page.append("<tr><td>Standard</td><td>Total Questions</td><td>Linked to Mod</td><td>Unlinked</td></tr>");
        for (int i = 0; i < data.size(); i++) {
            DataTuple dt = (DataTuple) data.get(i);
            String std = (String) dt.getFirst();
            String totalQ = (String) dt.getSecond();
            String linkedQ = (String) dt.getThird();
            String unlinkedQ = (String) dt.getFourth();
            page.append("<tr><td>").append(std);
            page.append("</td><td>").append(totalQ);
            page.append("</td><td>").append(linkedQ);
            page.append("</td><td>").append(unlinkedQ);
            page.append("</td></tr>");
        }
        page.append("</table>");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }


    private void insertHintFileUploads(StringBuffer page, Hint h, String qId, UserInfo info) {
        String id = h.getID();
        page.append("<FORM ACTION=\"QuestionFileUploadServlet\"");
        page.append("\" METHOD=\"POST\" enctype=\"multipart/form-data\" NAME=\"HintFileForm\">").append("\r\n");
        page.append("Image: <input type=\"file\" name=\"" + QuestionFileUploadServlet.HIMAGE + "\" size=\"80\">");
        page.append("<BR/>");
        page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Upload the file\">");
        page.append("<INPUT TYPE=\"hidden\" NAME=\"" + QuestionFileUploadServlet.HID + "\" VALUE=\"" + id + "\" >");
        page.append("<INPUT TYPE=\"hidden\" NAME=\"" + QuestionFileUploadServlet.ACTION + "\" VALUE=\"" + QuestionFileUploadServlet.HFILEUPLOAD + "\" >");
        if (qId != null && !qId.equals(""))
            page.append("<INPUT TYPE=\"hidden\" NAME=\"" + QuestionFileUploadServlet.QID + "\" VALUE=\"" + qId + "\" >");
        page.append("<INPUT TYPE=\"hidden\" NAME=\"" + QuestionFileUploadServlet.USERID + "\" VALUE=\"" + info.getUserID() + "\" >");
        page.append("</form>");
    }


    public void getEditShortAnswerQuestionPage(ShortAnswerQuestion q, Hint h, SystemInfo info,
                                               String msg, StringBuffer page) throws Exception {
        GeneralHtml.getPageHeader("Question Editor", page);
        GeneralHtml.getJavascriptBegin(page);
        String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteQuestionMode_,
                GeneralHtml.questionID_, q.getID());
        GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<body>").append("\r\n");
        GeneralHtml.getPageBanner("Question Editor", page);
        if (msg != null && !msg.equals(""))
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>").append("* denotes a required field").append("</h3>\r\n");

        Vector links = new Vector();
        Vector selParams = new Vector();
        selParams.add(GeneralHtml.categoryID_);
        selParams.add(q.getCategoryID());
        selParams.add(GeneralHtml.categoryName_);
        selParams.add(info.getCategoryName(q.getCategoryID()));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectQuestionMode_, selParams), "select question"));
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }


    public void getSelectQuestionPage(String catName, List<Question> catQs, SystemInfo info,
                                      String mID, StringBuffer page) throws Exception {
        getSelectQuestionPage(catName, catQs, info, mID, null, page, catName.equals(Question.SURVEY_QUESTION));
    }

    public void getSelectQuestionPage(String catName, List<Question> catQs, SystemInfo info,
                                      String mID, String paramStr, StringBuffer page, boolean isSurveyQ) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        String authLogin = info.getUserLogin();

        boolean selToLink = false;
        if (mID != null && !mID.equals(""))
            selToLink = true;
        GeneralHtml.getSmallPageHeader("Select Question", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Question", page);
        page.append("<h3>Questions in Category " + catName + "</h3>");
        if (selToLink) {
            page.append("Click on the Link button to link a question to module #" + mID + ".").append("\r\n");
        } else
            page.append("Click on the Edit button to go to the question's edit page.").append("\r\n");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=\"80%\"><tr bgcolor=#dddddd>").append("\r\n");
        if (selToLink) {
            page.append("<td>Link</td>");
            page.append("<td>View</td>");
        } else {
            page.append("<td>Edit</td>");
            //page.append("<td>View</td>");
        }
        page.append("<td>ID</td><td>Type</td><td>Author</td><td>Status</td><td>Topic</td><td>CCSS</td><td>Stem</td></tr>").append("\r\n");
        for (Question q : catQs) {
            page.append("<tr bgcolor=#eeeeee><td align=center>");
            Vector params = new Vector();
            params.add(GeneralHtml.questionID_);
            params.add(q.getID());
            if (selToLink) {
                page.append("<a href=\"");
                params.add(GeneralHtml.moduleID_);
                params.add(mID);
                // catID=1&  catname=Number%20Sense&  mID=38
                page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.linkModuleQuestionMode_, params));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath()).append("/images/link.gif\" ALT=\"link question to module\" BORDER=0></a>");
                page.append("</td>");

                page.append("<td>");
                page.append("<a href=\"");
                params.add(GeneralHtml.studentID_);
                params.add(info.getUserInfo().getUserID());
                params.add(GeneralHtml.questionType_);
                params.add(q.getType());
                // catID=1&  catname=Number%20Sense&  mID=38
                String param = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), (isSurveyQ ? AuthoringSubsystem.viewSurveyQuestionMode_ : AuthoringSubsystem.viewQuestionMode_), params);
                if (paramStr != null)
                    param += paramStr;
                page.append(param);
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath());
                page.append("/images/sel.gif\" ALT=\"link question to module\" BORDER=0>");
                page.append("</a>");

            } else {
                if (access == FormalitySubsystem.authorAccess) {
                    if (authLogin.equals(q.getAuthor())) {
                        page.append("<a href=\"");
                        page.append(GeneralHtml.getAuthorQuestionUrl(info.getServletRootAndID(),
                                (isSurveyQ ? AuthoringSubsystem.editSurveyQuestionMode_ : AuthoringSubsystem.editQuestionMode_), q.getType(), params));
                        page.append("\">");
                        page.append("<img SRC=\"").append(info.getContextPath()).append("/images/edit.gif\" ALT=\"edit question \" BORDER=0>");
                        page.append("</a>");
                    } else
                        page.append("&nbsp");
                } else {
                    page.append("<a href=\"");
                    page.append(GeneralHtml.getAuthorQuestionUrl(info.getServletRootAndID(), (isSurveyQ ? AuthoringSubsystem.editSurveyQuestionMode_ : AuthoringSubsystem.editQuestionMode_), q.getType(), params)
                    );
                    page.append("\">");
                    page.append("<img SRC=\"").append(info.getContextPath()).append("/images/edit.gif\" ALT=\"edit question \" BORDER=0>");
                    page.append("</a>");
                }
            }
            page.append("</td>");
            page.append("<td>").append(q.getID()).append("</td>");
            page.append("<td>").append(q.getType()).append("</td>");
            page.append("<td>").append(q.getAuthor()).append("</td>");
            if (q.getStatus())
                page.append("<td>").append(1).append("</td>");
            else
                page.append("<td>").append(0).append("</td>");

            page.append("<td>").append(q.getTopic()).append("</td>");
            page.append("<td>").append(getStandardsList(q.getCcss1(), q.getCcss2(), q.getCcss3())).append("</td>");
            try {
                String sstem = HighlightParser.removeTags(q.getStem());
                page.append("<td>").append(GeneralHtml.getAbbrevString(sstem, 45));
            } catch (Exception ex) {
                page.append("<td><font color=\"red\">Parse failed- check highlight tag syntax.</font>");
            }
            page.append("</td></tr>").append("\r\n");
        }
        page.append("</table>").append("\r\n");
        Vector links = new Vector();
        if (selToLink) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editModuleMode_,
                    GeneralHtml.moduleID_, mID), "back to edit module"));
        }
        if (access == FormalitySubsystem.teacherAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
        }
        if (access == FormalitySubsystem.authorAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        if (access == FormalitySubsystem.adminAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getSelectModulePage(String msg, List<QModule> mods, SystemInfo info,
                                    StringBuffer page) throws Exception {
        // boolean selToLink=false;
        //  if(mID!=null)
        //  selToLink=true;
        int access = info.getUserInfo().getAccessLevel();
        boolean canEdit = false;
        GeneralHtml.getSmallPageHeader("Select Module", false, page, info, false);
//

        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Module", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>Existing Modules</h3>");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=80%>").append("\r\n");
        page.append("<tr bgcolor=#dddddd><td>Edit</td><td>View</td><td>Clone</td><td>Delete</td><td>ModID</td><td>Author</td><td>Mod Name</td><td>Status</td><td>Mod Type</td><td>Linked Question IDs</td></tr>").append("\r\n");
        for (QModule m : mods) {
            canEdit = false;
            page.append("<tr bgcolor=#eeeeee><td align=center width=10%>");
            String l = info.getUserLogin();
            if (access == FormalitySubsystem.authorAccess)// && info.getUserLogin().equals(module.getAuthorID()))
                canEdit = true;
            if (access == FormalitySubsystem.adminAccess || access == FormalitySubsystem.teacherAccess)
                canEdit = true;
            if (canEdit) {
                page.append("<a href=\"");
                page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                        AuthoringSubsystem.editModuleMode_,
                        GeneralHtml.moduleID_, m.getID()));
                page.append("\"><img SRC=\"").append(info.getContextPath());
                page.append("/images/edit.gif\" ALT=\"edit module\" BORDER=0></a>");
                page.append("\r\n");
            } else
                page.append("&nbsp;");
            page.append("</td>");
            page.append("<td align=center width=10%>");
            page.append("<a href=\"");
            page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.viewModuleMode_,
                    GeneralHtml.moduleID_, m.getID()));
            page.append("\"><img SRC=\"").append(info.getContextPath());
            page.append("/images/view.gif\" ALT=\"view module\" BORDER=0></a></td>");
            page.append("\r\n");
            // clone button
            page.append("<td>");
            if (canEdit) {
                page.append("<a href=\"");
                page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                        AuthoringSubsystem.cloneModuleMode_,
                        GeneralHtml.moduleID_, m.getID()));
                page.append("\"><img SRC=\"").append(info.getContextPath());
                page.append("/images/clone.gif\" ALT=\"clone module\" BORDER=0></a>");
                page.append("\r\n");
            } else
                page.append("&nbsp;");
            page.append("</td>");
            // end clone button
            // delete button
            page.append("<td>");
            if (access == FormalitySubsystem.adminAccess) {
                String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteModuleMode_, GeneralHtml.moduleID_, m.getID());
                page.append("<a onClick=\"javascript:return checkModDel('" + delUrl + "');\"");
//                page.append("<a onClick=\"alert('"+ delUrl +"'); return false;\"");
                page.append(" href=\"");
                page.append(delUrl);
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath());
                page.append("/images/del.gif\" ALT=\"delete module\" BORDER=0></a>");
                page.append("\r\n");
            } else
                page.append("&nbsp;");
            page.append("</td>");
            // end delete button
            page.append("<td>").append(m.getID()).append("</td>");
            page.append("<td>").append(m.getAuthorID()).append("</td>");
            page.append("<td>").append(m.getName()).append("</td>");
            if (m.getStatus())
                page.append("<td>").append(1).append("</td>");
            else
                page.append("<td>").append(0).append("</td>");
            page.append("<td>").append(m.getType()).append("</td>");
            page.append("<td>");
            getQuestionIDs(m.getLinkedQuestionIDs(), page);
            page.append("</td></tr>");
        }
        page.append("</table><br>").append("\r\n");
        Vector links = new Vector();
        if (access == FormalitySubsystem.teacherAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
        }
        if (access == FormalitySubsystem.adminAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        if (access == FormalitySubsystem.authorAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getSelectModModuleLinkPage(String msg, String childModID, List<QModule> mods, SystemInfo info,
                                           StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("Select Module", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Module", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>Click on \"Link\" To Link Module<b> ").append(childModID);
        page.append(" </b>To One Of The Following Existing Modules:</h3>");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=80%>").append("\r\n");
        page.append("<tr bgcolor=#dddddd><td>Link</td><td>ModID</td><td>Mod Name</td><td>Status</td><td>Mod Type</td><td>Linked Question IDs</td></tr>").append("\r\n");
        for (QModule m : mods) {
            page.append("<tr bgcolor=#eeeeee><td align=center width=10%>");
            page.append("<a href=\"");
            String linkUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.linkModModuleMode_);
            linkUrl = GeneralHtml.appendToUrl(linkUrl, GeneralHtml.childModID_, childModID);
            linkUrl = GeneralHtml.appendToUrl(linkUrl, GeneralHtml.parentModID_, m.getID());
            page.append(linkUrl);
            page.append("\"><img SRC=\"").append(info.getContextPath());
            page.append("/images/link.gif\" ALT=\"link module\" BORDER=0></a></td>");
            page.append("\r\n");
            page.append("<td>").append(m.getID()).append("</td>");
            page.append("<td>").append(m.getName()).append("</td>");
            if (m.getStatus())
                page.append("<td>").append(1).append("</td>");
            else
                page.append("<td>").append(0).append("</td>");
            page.append("<td>").append(m.getType()).append("</td>");
            page.append("<td>");
            getQuestionIDs(m.getLinkedQuestionIDs(), page);
            page.append("</td></tr>");
        }
        page.append("</table><br>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.editModuleMode_,
                GeneralHtml.moduleID_, childModID), "back to edit module"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    // if qID is null- select will go to hint edit page, else the hint will
    //be linked to the qID and return to the question edit page.
    public void getSelectHintPage(String msg, List<Hint> allHints, SystemInfo info,
                                  StringBuffer page, Question quest) throws Exception {
        boolean selToLink = false;
        String qID = quest.getID();
        if (qID != null && !qID.equals(""))
            selToLink = true;
        GeneralHtml.getPageHeader("Select Hint", page);
        page.append("<body>").append("\r\n");
        GeneralHtml.getPageBanner("Select Hint", page);
        if (msg != null && !msg.equals(""))
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        if (selToLink)
            page.append("Click on the Link button to link a hint to question #" + qID + ".").append("\r\n");
        else
            page.append("Click on the Edit button to go to the hint's edit page.").append("\r\n");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").append("\r\n");
        if (selToLink)
            page.append("<td>Link</td>");
        else
            page.append("<td>Edit</td>");
        page.append("<td>HintID</td><td>Step</td><td>Coach</td><td>Query</td><td>Response</td><td>Question Links</td></tr>").append("\r\n");
        for (Hint h : allHints) {
            page.append("<tr bgcolor=#eeeeee><td align=center>");
            page.append("<a href=\"");
            Vector params = new Vector();
            params.add(GeneralHtml.hintID_);
            params.add(h.getID());
            if (selToLink) {
                params.add(GeneralHtml.questionID_);
                params.add(qID);
                page.append(GeneralHtml.getAuthorQuestionUrl(info.getServletRootAndID(), AuthoringSubsystem.linkQuestionHintMode_, quest.getType(), params));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath()).append("/images/link.gif\" ALT=\"link hint to question \" BORDER=0>");
            } else {
                page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.editHintMode_, params));
                page.append("\">");
                page.append("<img SRC=\"").append(info.getContextPath()).append("/images/edit.gif\" ALT=\"edit hint \" BORDER=0>");
            }
            page.append("</a></td>");
            page.append("<td>").append(h.getID()).append("</td>");
            page.append("<td>").append(h.getLevel()).append("</td>");
            page.append("<td>").append(h.getCoachName()).append("</td>");
            page.append("<td>").append(h.getQuery()).append("</td>");
            page.append("<td>").append(GeneralHtml.getAbbrevString(h.getResponse(), 25));
            page.append("</td><td>");
            getQuestionIDs(h.getQuestionLinks(), page);
            page.append("</td></tr>").append("\r\n");
        }
        if (allHints.size() == 0)
            page.append("<tr><td colspan=\"7\">There are no hints that are not linked to this question.</td></tr>").append("\r\n");
        page.append("</table>").append("\r\n");
        Vector links = new Vector();
        if (selToLink) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editQuestionMode_,
                    GeneralHtml.questionID_, qID), "edit question"));
        } else {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editHintMode_), "edit hint"));
        }
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        GeneralHtml.getGeneralPageFooter(links, page);

    }

    //if qID- then need to return to question edit page, else hint select page
    public void getEditHintPage(Hint h, SystemInfo info, String qID, String msg, StringBuffer page) throws Exception {
        boolean accessOK = false;
        int access = info.getUserInfo().getAccessLevel();
        String userID = info.getUserID();
        if (access == FormalitySubsystem.adminAccess)
            accessOK = true;
        if (access == FormalitySubsystem.authorAccess)
            if (userID.equals(h.getAuthor()))
                accessOK = true;
        boolean isQedit = false;
        if (qID != null && !qID.equals(""))
            isQedit = true;
        GeneralHtml.getSmallPageHeader("Edit Hint", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Edit Hint", page);
        GeneralHtml.getJavascriptBegin(page);
        String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteHintMode_,
                GeneralHtml.hintID_, h.getID());
        GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        else
            page.append("<br>").append("\r\n");
        if (isQedit) {
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=60%>").append("\r\n");
            page.append("<tr bgcolor=#eeeeee>").append("\r\n");
            page.append("<td align=center width=10%>").append("\r\n");
            page.append("<a href=\"");
            page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.unlinkQuestionHintMode_));
            page.append("&").append(GeneralHtml.questionID_).append("=").append(qID);
            page.append("&").append(GeneralHtml.hintID_).append("=").append(h.getID()).append("\">");
            page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"unlink\" BORDER=0></a></td>").append("\r\n");
            page.append("<td><b>Unlink this Hint to Question #");
            page.append(qID).append("</b></td></tr></table>").append("\r\n");
            page.append("<BR>").append("\r\n");
        }
        if (accessOK) {
            page.append("<FORM ACTION=\"").append(GeneralHtml.getSaveHintSubmit(
                    info.getServletRootAndID(), h.getID()));
            page.append("\" METHOD=\"POST\" NAME=\"HEditForm\">").append("\r\n");
            page.append("<TABLE BGCOLOR=\"#eeeeee\" CELLSPACING=0 CELLPADDING=0><TR>").append("\r\n");
            page.append("<TD>&nbsp;&nbsp;</TD><td bgcolor=\"white\" width=\"50%\">");
            TreeMap tagInfo = info.getTags();
            HighlightParser hlp = new HighlightParser(tagInfo);
            String ss = hlp.replaceActiveTags(h.getResponse(), info);   //  replace the {MediaRoot}
            page.append(ss).append("</td>");
            page.append("</tr><tr><td>").append("\r\n");
            page.append("<TABLE CELLSPACING=0>");
            page.append("<tr><td colspan=\"2\">");
            page.append("<a href=\"javascript: extraWindow('");
            page.append(info.getContextPath()).append("/htmlHelp.html')\">Html and Hint Tag Help</a><br><br>");
            page.append("</td></tr>").append("\r\n");
            page.append("<tr><td>&nbsp;HintID&nbsp;</td><td>").append("\r\n");
            page.append(h.getID());
            page.append("</td></tr>");
            page.append("<tr><td>&nbsp;Tag&nbsp;</td><td>").append("\r\n");
            page.append(GeneralHtml.getHighlightTag(h.getLevel(), h.getCoachID()));
            page.append("</td></tr>");
            page.append("<tr><td>&nbsp;Step&nbsp;</td><td>").append("\r\n");
            getLevelSelect(h.getLevel(), page);
            page.append("</td></tr>");
            page.append("<tr><td>&nbsp;Coach&nbsp;</td><td>").append("\r\n");
            getCoachSelect(info, h.getCoachID(), page);
            page.append("</td></tr><tr><td>&nbsp;Answer Choice Link&nbsp;</td><td>").append("\r\n");
            getAnsChoiceLinkSelect(h.getAnsChoiceLink(), page);
            page.append("</td></tr></table>").append("\r\n");
            page.append("</td></tr><tr><TD><table><tr><td>&nbsp;Query&nbsp;</td><td>").append("\r\n");
            page.append("<TEXTAREA NAME=\"").append(GeneralHtml.query_);
            page.append("\" COLS=50 ROWS=3 WRAP=virtual>").append(h.getQuery());
            page.append("</TEXTAREA></td>").append("\r\n");
            page.append("</tr><tr><td>&nbsp;Response&nbsp;</td><td>").append("\r\n");
            page.append("<TEXTAREA NAME=\"").append(GeneralHtml.response_);
            page.append("\" COLS=50 ROWS=10 WRAP=virtual>").append(h.getResponse());
            page.append("</TEXTAREA></td>").append("\r\n");
            page.append("</tr></table></TD></TR><tr><td><TABLE BORDER=0 CELLSPACING=0><tr><td>").append("\r\n");
            page.append("&nbsp;Strategies&nbsp;").append("\r\n");
            getStrategySelect(info, h.getStrategyID(), page);
            page.append("\r\n</td></tr><tr><td><br>&nbsp;Linked To These Questions:&nbsp;");
            getQuestionIDs(h.getQuestionLinks(), page);
            page.append("</td><tr></table></TD></TR><TR><TD>&nbsp;</TD></TR></table>\r\n");
            page.append("<BR>").append("\r\n");
            if (isQedit) {
                GeneralHtml.getHiddenParam(GeneralHtml.questionID_, qID, page);
            }
            page.append("<TABLE WIDTH=60%><TR><TD ALIGN=left>").append("\r\n");
            page.append("<INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Save\">");
            page.append("</td><TD ALIGN=right>");
            page.append("<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(HEditForm)\">");
            page.append("</td></tr></table>");
            page.append("</form>").append("\r\n");
            insertHintFileUploads(page, h, qID, info.getUserInfo());
        } else {
            //get uneditable view
            page.append("<h4>You do not have permission to edit this hint.</h4>");
            page.append("<TABLE BGCOLOR=\"#eeeeee\" CELLSPACING=0 CELLPADDING=0><TR>").append("\r\n");
            page.append("<td>").append("\r\n");
            page.append("<TABLE CELLSPACING=0>");
            page.append("<tr><td colspan=\"2\">");
            page.append("&nbsp;<br><br>");
            page.append("</td></tr>").append("\r\n");
            page.append("<tr><td>&nbsp;HintID&nbsp;</td><td>").append("\r\n");
            page.append(h.getID());
            page.append("</td></tr>");
            page.append("<tr><td>&nbsp;Tag&nbsp;</td><td>").append("\r\n");
            page.append(GeneralHtml.getHighlightTag(h.getLevel(), h.getCoachID()));
            page.append("</td></tr>");
            page.append("<tr><td>&nbsp;Step&nbsp;</td><td>").append("\r\n");
            page.append(h.getLevel()).append("</td></tr>");
            page.append("<tr><td>&nbsp;Coach&nbsp;</td><td>").append("\r\n");
            page.append(h.getCoachID());
            page.append("</td></tr><tr><td>&nbsp;Answer Choice Link&nbsp;</td><td>").append("\r\n");
            page.append(h.getAnsChoiceLink());
            page.append("</td></tr></table>").append("\r\n");
            page.append("</td></tr><tr><TD><table><tr><td>&nbsp;Query&nbsp;</td><td>").append("\r\n");
            page.append(h.getQuery());
            page.append("</td>").append("\r\n");
            page.append("</tr><tr><td>&nbsp;Response&nbsp;</td><td>").append("\r\n");
            page.append(h.getResponse());
            page.append("</td>").append("\r\n");
            page.append("</tr></table></TD></TR><tr><td><TABLE BORDER=0 CELLSPACING=0><tr><td>").append("\r\n");
            page.append("&nbsp;Strategies&nbsp;").append("\r\n");
            page.append(h.getStrategyID());
            page.append("\r\n</td></tr><tr><td><br>&nbsp;Linked To These Questions:&nbsp;");
            getQuestionIDs(h.getQuestionLinks(), page);
            page.append("</td><tr></table></TD></TR><TR><TD>&nbsp;</TD></TR></table>\r\n");
            page.append("<BR>").append("\r\n");
        }
        Vector links = new Vector();
        if (isQedit) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editQuestionMode_,
                    GeneralHtml.questionID_, qID), "edit question"));
        } else {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.selectHintMode_), "hint select"));
        }
        if (access == FormalitySubsystem.adminAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        if (access == FormalitySubsystem.authorAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }


    public void getNewModulePage(String msg, SystemInfo info,
                                 Vector courseInfo, StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("New Module", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("New Module", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
        page.append("<br><FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.saveNewModuleMode_));
        page.append("\" METHOD=\"POST\" NAME=\"NewModForm\">").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee><td>&nbsp;*Name&nbsp;</td><td>").append("\r\n");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.moduleName_);
        page.append("\" VALUE=\"\" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Module Type&nbsp;</td><td>").append("\r\n");
        getModTypeSelect(info, null, page);
        page.append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Author&nbsp;</td><td>&nbsp;").append(info.getUserLogin()).append("&nbsp;</td></tr>\r\n");
        page.append("</table><br>").append("\r\n");
        if (access == FormalitySubsystem.teacherAccess)
            getModCourseLinkTable(info.getUserInfo().getCourseID(), courseInfo, page);
        else if (access == FormalitySubsystem.authorAccess)
            getModCourseLinkTable(info, courseInfo, page);
        else  //admin
            getModCourseLinkTable(courseInfo, page);

        page.append("<br>").append("\r\n");
        page.append("<TABLE WIDTH=100%><TR><TD ALIGN=left><INPUT TYPE=\"SUBMIT\" NAME=\"SAVE\" VALUE=\"Save\"></TD></TR></TABLE>").append("\r\n");
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        if (access == FormalitySubsystem.teacherAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
        }
        if (access == FormalitySubsystem.adminAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        if (access == FormalitySubsystem.authorAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    //called if the module has been not been saved
    public void getNewModulePage(QModule m, String msg, SystemInfo info,
                                 Vector courseInfo, StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("New Module", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("New Module", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
        page.append("<br><FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.saveNewModuleMode_));
        page.append("\" METHOD=\"POST\" NAME=\"NewModForm\">").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee><td>&nbsp;*Name&nbsp;</td><td>").append("\r\n");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.moduleName_);
        page.append("\" VALUE=\"").append(m.getName());
        page.append("\" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Module Type&nbsp;</td><td>").append("\r\n");
        getModTypeSelect(info, m.getType(), page);
        page.append("</td></tr></table><br>").append("\r\n");
        if (access == FormalitySubsystem.teacherAccess)
            getModCourseLinkTable(info.getUserInfo().getCourseID(), courseInfo, page);
        else if (access == FormalitySubsystem.authorAccess)
            getModCourseLinkTable(info, courseInfo, page);
        else  //admin
            getModCourseLinkTable(courseInfo, page);
        page.append("<br>").append("\r\n");
        page.append("<TABLE WIDTH=100%><TR><TD ALIGN=left><INPUT TYPE=\"SUBMIT\" NAME=\"SAVE\" VALUE=\"Save\"></TD></TR></TABLE>").append("\r\n");
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        if (access == FormalitySubsystem.teacherAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
        }
        if (access == FormalitySubsystem.adminAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        if (access == FormalitySubsystem.authorAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getEditModulePage(String msg, QModule m, SystemInfo info,
                                  Vector cInfo, Vector cIDs, StringBuffer page)
            throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        // tEditable is true if user is the author of this module. applies to teachers
        boolean userEditable = (info.getUserLogin().equals(m.getAuthorID()));
        boolean editable = (access == FormalitySubsystem.adminAccess || (access == FormalitySubsystem.authorAccess && userEditable)
                || (access == FormalitySubsystem.teacherAccess && userEditable));
        GeneralHtml.getSmallPageHeader("Edit Module", false, page, info, false);
        GeneralHtml.getJavascriptBegin(page);
        String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteModuleMode_,
                GeneralHtml.moduleID_, m.getID());
        GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Edit Module", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.saveModuleMode_,
                GeneralHtml.moduleID_, m.getID()));
        page.append("\" METHOD=\"POST\" NAME=\"EditModForm\">").append("\r\n");
        if (editable) {
            page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
            page.append("<TABLE BORDER=1 CELLSPACING=0>");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;ModID&nbsp;</td><td>&nbsp;").append(m.getID()).append("&nbsp;</td></tr>");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Author&nbsp;</td><td>&nbsp;").append(m.getAuthorID()).append("&nbsp;</td></tr>\r\n");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;*Name&nbsp;</td><td>").append("\r\n");
            page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.moduleName_);
            page.append("\" VALUE=\"").append(m.getName());
            page.append("\" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Module Type&nbsp;</td><td>").append("\r\n");
            getModTypeSelect(info, m.getType(), page);
            page.append("</td></tr>").append("\r\n");
            page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
            page.append("&nbsp;Ready Status&nbsp;").append("\r\n");
            page.append("</td><td>").append("\r\n");
            page.append("<INPUT TYPE=\"CHECKBOX\" NAME=\"").append(GeneralHtml.readyStatus_);
            if (m.getStatus())
                page.append("\" value=\"yes\" CHECKED>");
            else
                page.append("\" value=\"yes\">");
            page.append("</td></tr>").append("\r\n");
            // for test modules, add an attribute for whether it can be re-entered (in practice mode)
            if (m.getType().equalsIgnoreCase("test")) {
                page.append("<tr BGCOLOR=#eeeeee><td>").append("\r\n");
                page.append("&nbsp;Is Re-enterable&nbsp;").append("\r\n");
                page.append("</td><td>").append("\r\n");
                page.append("<INPUT TYPE=\"CHECKBOX\" NAME=\"").append(GeneralHtml.isReentrant_);
                if (m.isReentrant())
                    page.append("\" value=\"yes\" CHECKED>");
                else
                    page.append("\" value=\"yes\">");
                page.append("</td></tr>").append("\r\n");
            }

            page.append("</table>").append("\r\n");
        } else if (!editable) {
            //get the uneditable table
            page.append("<br>");
            page.append("<TABLE BORDER=1 CELLSPACING=0>");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;ModID&nbsp;</td>");
            page.append("<td>&nbsp;").append(m.getID()).append("&nbsp;</td></tr>");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Author&nbsp;</td><td>&nbsp;");
            page.append(m.getAuthorID()).append("&nbsp;</td></tr>\r\n");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Name&nbsp;</td>");
            page.append("<td>&nbsp;").append(m.getName()).append("&nbsp;</td></tr>").append("\r\n");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Module Type&nbsp;</td>");
            page.append("<td>&nbsp;").append(m.getType()).append("&nbsp;</td></tr>").append("\r\n");
            page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Ready Status&nbsp;</td>").append("\r\n");
            page.append("<td>&nbsp;");
            if (m.getStatus())
                page.append("yes");
            else
                page.append("no");
            page.append("&nbsp;</td></tr>").append("\r\n");
            page.append("</table>").append("\r\n");
        }

        // Add Controls for linking questions to this module (a table)
        linkedQuestionsControls(m, info, page, editable);
        if (editable) {
            page.append("<br><b>Link an Existing Question To This Module:</b><br>");
            page.append("Click on \"Select\" to edit/view questions in that category.<br>").append("\r\n");
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=60%><tr bgcolor=#dddddd>");
            page.append("<td>Select</td><td>Category</td><td>Count</td></tr>").append("\r\n");
            Iterator cit = info.getCategoryIDs();
            while (cit.hasNext()) {
                String cID = (String) cit.next();
                page.append("<tr bgcolor=#eeeeee><td align=center width=10%><a href=\"");
                Vector params = new Vector();
                params.add(GeneralHtml.categoryID_);
                params.add(cID);
                params.add(GeneralHtml.categoryName_);
                params.add(info.getCategoryName(cID));
                params.add(GeneralHtml.moduleID_);
                params.add(m.getID());

                page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectQuestionMode_,
                        params));
                page.append("\"><img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"cat\" BORDER=0></a></td><td>");
                page.append(info.getCategoryName(cID));
                page.append("</td><td width=10%>").append(info.getCatCount(cID)).append("</td></tr>").append("\r\n");
            }
            page.append("</table>").append("\r\n");
            ///////
            page.append("<br><b>This Module is Dependent Upon These Modules:</b><br>").append(
                    "\r\n");
            Iterator mit = m.getParentModIDs();
            if (mit.hasNext()) {
                page.append(
                        "<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=20%><tr bgcolor=#dddddd>").
                        append("\r\n");
                page.append(
                        "<td>Unlink</td><td>ParentModID</td></tr>").
                        append("\r\n");
                while (mit.hasNext()) {
                    String pmID = (String) mit.next();
                    page.append("<tr bgcolor=#eeeeee><td align=center>");
                    page.append("<a href=\"");
                    Vector params = new Vector();
                    params.add(GeneralHtml.parentModID_);
                    params.add(pmID);
                    params.add(GeneralHtml.childModID_);
                    params.add(m.getID());
                    page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                            AuthoringSubsystem.
                                    unlinkModModuleMode_,
                            params));
                    page.append("\">");
                    page.append(
                            "<img SRC=\"").append(info.getContextPath()).append("/images/link.gif\" ALT=\"unlink question to module\" BORDER=0>");
                    page.append("</a></td>");
                    page.append("<td>").append(pmID).append("</td></tr>").append("\r\n");
                }
                page.append("</table>").append("\r\n");
            } else {
                page.append("This module has no dependencies.");
            }
            page.append("<br><b>Make This Module Dependent Upon Another Module:</b><br>");
            page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=60%>").append("\r\n");
            page.append("<tr bgcolor=#eeeeee>").append("\r\n");
            page.append("<td align=center width=10%>").append("\r\n");
            page.append("<a href=\"");
            page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModModuleMode_, GeneralHtml.childModID_, m.getID())).append("\">");
            page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a></td>").append("\r\n");
            page.append("<td>Click on \"Select\" to select from a list of existing modules.</td></tr></table>").append("\r\n");

        }
        page.append("<br>");
        if (access == FormalitySubsystem.teacherAccess)
            getSelectedModCourseLinkTable(info.getUserInfo().getCourseID(),
                    cIDs, cInfo, page);
        else if (access == FormalitySubsystem.authorAccess)
            getSelectedModCourseLinkTable(info.getUserInfo(), cIDs, cInfo, page);
        else // admin
            getSelectedModCourseLinkTable(cIDs, cInfo, page, info, m);
        page.append("<br>");
        page.append("<TABLE WIDTH=60%><TR>");
        page.append("<TD ALIGN=left><INPUT TYPE=\"SUBMIT\" NAME=\"SAVE\" VALUE=\"Save\"></TD>").
                append("\r\n");
        page.append("<TD ALIGN=right>");
        if (editable)
            page.append("<INPUT TYPE=\"button\" NAME=\"delete\" VALUE=\"Delete\" onClick=\"checkDel(EditModForm)\">");
        else
            page.append("&nbsp;");
        page.append("</TD></TR></TABLE>").append("\r\n");
        page.append("</form>").append("\r\n");

        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_), "select module"));
        if (access == FormalitySubsystem.teacherAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
        }
        if (access == FormalitySubsystem.authorAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        if (access == FormalitySubsystem.adminAccess)
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));

        GeneralHtml.getGeneralPageFooter(links, page);
    }

    private void linkedQuestionsControls(QModule m, SystemInfo info, StringBuffer page, boolean editable) throws Exception {
        page.append("<br><b>Questions Linked To This Module:</b><br>").append(
                "\r\n");
        Iterator qit = m.getLinkedQuestions();
        // int orderIndex=1;
        if (qit.hasNext()) {
            page.append(
                    "<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").
                    append("\r\n");
            if (editable)
                page.append("<td>Unlink</td>");
            //"<td>Unlink</td><td>Order</td><td>qID</td><td>Author</td><td>Status</td><td>Std</td><td>Stem</td></tr>").
            page.append("<td>qID</td><td>Author</td><td>Status</td><td>CCSS</td><td>Stem</td><td>Move Up</td><td>Move down</td></tr>").
                    append("\r\n");
            while (qit.hasNext()) {
                Question q = (Question) qit.next();
                page.append("<tr bgcolor=#eeeeee>");
                if (editable) {
                    page.append("<td align=center>");
                    insertImageButton(m, info, page, q, AuthoringSubsystem.unlinkModuleQuestionMode_, "/images/link.gif", "Unlink Question From Module");
                    page.append("</td>");
                }
                // page.append("<td><INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.orderIndex_+orderIndex);
                //  page.append("\" VALUE=\"").append(orderIndex).append("\" SIZE=3 ></td>");
                // orderIndex++;
                page.append("<td>").append(q.getID()).append("</td>");
                page.append("<td>").append(q.getAuthor()).append("</td>");
                if (q.getStatus())
                    page.append("<td>").append(1).append("</td>");
                else
                    page.append("<td>").append(0).append("</td>");
                page.append("<td>").append(getStandardsList(q.getCcss1(),
                        q.getCcss2(), q.getCcss3())).append("</td>");
                page.append("<td>").append(GeneralHtml.getAbbrevString(q.
                        getStem(),
                        25));
                page.append("</td>");
                // 2 image buttons for move-up and move-down on this qid
                page.append("<td>\n");
                if (m.getQuestionSequencing() == QModule.OrderType.fixed)
                    insertImageButton(m, info, page, q, AuthoringSubsystem.moveupModuleQuestionMode_, "/images/moveup.gif", "Move Question Up in sequence");
                else page.append("&nbsp;");
                page.append("</td><td>\n");
                if (m.getQuestionSequencing() == QModule.OrderType.fixed)
                    insertImageButton(m, info, page, q, AuthoringSubsystem.movedownModuleQuestionMode_, "/images/movedown.gif", "Move Question Down in sequence");
                else page.append("&nbsp;");
                page.append("</td></tr>").append("\r\n");
            }
            page.append("</table>").append("\r\n");

            insertSequenceControls(page, m);
        } else {
            page.append("No questions are linked to this module.");
        }
    }

    private void insertSequenceControls(StringBuffer page, QModule m) {
        page.append("<br><b>Ordering sequence of problems in this module:</b>");
        page.append("<br><table border=\"0\">\n");
        page.append("<tr><td>\n");
        page.append("<input type=\"radio\" " + (m.isRandomOrder() ? "checked" : "") + " name=\"modQuestionOrder\" value=\"" + QModule.OrderType.random.name() + "\">Random</input>\n");
        page.append("</td><td>\n");
        page.append("<input type=\"radio\" " + (m.isLinkOrder() ? "checked" : "") + " name=\"modQuestionOrder\" value=\"" + QModule.OrderType.link.name() + "\">Link Order</input>\n");
        page.append("</td><td>\n");
        page.append("<input type=\"radio\" " + (m.isFixedOrder() ? "checked" : "") + " name=\"modQuestionOrder\" value=\"" + QModule.OrderType.fixed.name() + "\">Fixed</input>\n");
        page.append("</td></tr></table>");
    }

    private void insertImageButton(QModule m, SystemInfo info, StringBuffer page, Question q, String eventType, String imgURL, String imgAlt) throws Exception {
        page.append("<a href=\"");
        Vector params = new Vector();
        params.add(GeneralHtml.questionID_);
        params.add(q.getID());
        params.add(GeneralHtml.moduleID_);
        params.add(m.getID());
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), eventType, params));
        page.append("\">");
        page.append("<img SRC=\"").append(info.getContextPath()).append(imgURL + "\" ALT=\"" + imgAlt + "\" BORDER=0>");
        page.append("</a>");
    }

    public void getModulePage(String msg, QModule m,
                              SystemInfo info, StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        //    GeneralHtml.getJavascriptBegin(page);
        //  String delUrl= GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteModuleMode_,
        //                          GeneralHtml.moduleID_, module.getID());
        //  GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        //   GeneralHtml.getJavascriptEnd(page);

        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module Viewer", page);
        page.append("<h3>").append(m.getName()).append("</h3>").append("\r\n");
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.viewActiveModuleMode_,
                GeneralHtml.moduleID_, m.getID()));
        page.append("\" METHOD=\"POST\" NAME=\"ViewModForm\">").append("\r\n");
        page.append("<b>Questions In This Module:</b><br>").append(
                "\r\n");
        Iterator qit = m.getLinkedQuestions();
        if (qit.hasNext()) {
            page.append(
                    "<table BORDER=1 CELLPADDING=3 CELLSPACING=0><tr bgcolor=#dddddd>").
                    append("\r\n");
            page.append(
                    "<td>Number</td><td>Category</td><td>Stem</td></tr>").
                    append("\r\n");
            int i = 1;
            while (qit.hasNext()) {
                Question q = (Question) qit.next();
                page.append("<tr bgcolor=#eeeeee>");
                page.append("<td>").append(i).append("</td>");
                page.append("<td>").append(info.getCategoryName(q.getCategoryID())).append("</td>");
                page.append("<td>").append(GeneralHtml.getAbbrevString(q.
                        getStem(),
                        25));
                page.append("</td></tr>").append("\r\n");
                i++;
            }
            page.append("</table>").append("\r\n");
            page.append("<br>");
            page.append("<TABLE WIDTH=60%><TR>");
            page.append("<TD ALIGN=center><INPUT TYPE=\"SUBMIT\" NAME=\"begin\" VALUE=\"Begin Module\"></TD>").
                    append("\r\n");
            page.append("</TR></TABLE>").append("\r\n");
        } else {
            page.append("No questions are linked to this module.");
        }
        page.append("</form>").append("\r\n");
        Vector links = new Vector();
        if (access == FormalitySubsystem.teacherAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.editContentMode_), "edit content"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getTeacherUrl(info.getServletRootAndID(), TeacherSubsystem.teacherHomeMode_), "teacher home"));
        }
        if (access == FormalitySubsystem.authorAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_), "select module"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "author home"));
        }
        if (access == FormalitySubsystem.adminAccess) {
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_), "select module"));
            links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));
        }

        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getModuleMCQuestionPage(String msg, QModule m, MultipleChoiceQuestion q,
                                        SystemInfo info, BaseHintRenderer hintRenderer,
                                        StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        MultipleChoiceQuestionPage mcqp = new MultipleChoiceQuestionPage();
        mcqp.getAuthorModuleQuestionPage(m, q, info, msg, hintRenderer, page);
    }

    public void getModuleSAQuestionPage(String msg, QModule m, ShortAnswerQuestion q,
                                        SystemInfo info, BaseHintRenderer hintRenderer,
                                        StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();
        pg.getAuthorModuleQuestionPage(m, q, info, msg, hintRenderer, page);
    }

    public void getModuleMCSurveyQuestionPage(String msg, QModule m, MultiChoiceSurveyQuestion q,
                                              SystemInfo info,
                                              StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        MultipleChoiceSurveyQuestionPage mcqp = new MultipleChoiceSurveyQuestionPage();
        mcqp.getAuthorModuleSurveyQuestionPage(m, q, info, msg, page);
    }


    public void getModuleSASurveyQuestionPage(String msg, QModule m, ShortAnswerQuestion q,
                                              SystemInfo info,
                                              StringBuffer page) throws Exception {
        GeneralHtml.getSmallPageHeader("Module Viewer", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Module: " + m.getName(), page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        ShortAnswerQuestionPage pg = new ShortAnswerQuestionPage();
        pg.getAuthorModuleSurveyQuestionPage(m, q, info, msg, page);
    }


    public void getStrategySelect(SystemInfo info, String sID, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.strategyID_).append("\" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"\">").append("\r\n");
        Vector sIDs = info.getStrategyIDs();
        Vector sNms = info.getStrategyNames();
        for (int i = 0; i < sIDs.size(); i++) {
            String curSid = (String) sIDs.get(i);
            page.append("<OPTION VALUE=\"").append(curSid);
            if (curSid.equals(sID))
                page.append("\" selected>");
            else
                page.append("\">");
            page.append(sNms.get(i)).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }

    // get all courses and IDs to display in a checkbox table
    public void getModCourseLinkTable(Vector cInfo, StringBuffer page) throws Exception {
        if (cInfo == null || cInfo.size() == 0)
            return;
        page.append("<b>Link this Module to One or More Classes:</b>").append("\r\n");
        ;
        page.append("<TABLE BORDER=1 CELLSPACING=0 width=\"60%\">");
        for (int i = 0; i < cInfo.size(); i++) {
            DataTuple dt = (DataTuple) cInfo.get(i);
            page.append("<tr><td>");
            page.append("<tr BGCOLOR=#eeeeee><td align=\"center\" width=\"10\">");
            page.append("<input type=\"checkbox\" name=\"").append(dt.getFirst()).append("\">");
            page.append("</td><td>");
            page.append(dt.getSecond());
            page.append("</td></tr>").append("\r\n");
        }
        page.append("</TABLE>").append("\r\n");
    }

    // get the course specified by the cID to display in a checkbox table
    public void getModCourseLinkTable(String cID, Vector cInfo, StringBuffer page) throws Exception {
        if (cInfo == null || cInfo.size() == 0)
            return;
        page.append("<b>Link this Module Your Class:</b>").append("\r\n");
        ;
        page.append("<TABLE BORDER=1 CELLSPACING=0 width=\"60%\">");
        for (int i = 0; i < cInfo.size(); i++) {
            DataTuple dt = (DataTuple) cInfo.get(i);
            String curCID = (String) dt.getFirst();
            if (curCID.equals(cID)) {
                page.append("<tr><td>");
                page.append("<tr BGCOLOR=#eeeeee><td align=\"center\" width=\"10\">");
                page.append("<input type=\"checkbox\" name=\"").append(curCID).append("\">");
                page.append("</td><td>");
                page.append(dt.getSecond());
                page.append("</td></tr>").append("\r\n");
            }
        }
        page.append("</TABLE>").append("\r\n");
    }

    // get the course specified by the cID to display in a checkbox table
    public void getModCourseLinkTable(SystemInfo info, Vector cInfo, StringBuffer page) throws Exception {
        if (cInfo == null || cInfo.size() == 0)
            return;
        String userDist = info.getUserInfo().getDistrict();
        page.append("<b>Link this Module A Class in your District:</b>").append("\r\n");
        ;
        page.append("<TABLE BORDER=1 CELLSPACING=0 width=\"60%\">");
        for (int i = 0; i < cInfo.size(); i++) {
            DataTuple dt = (DataTuple) cInfo.get(i);
            String curCID = (String) dt.getFirst();
            String curDist = (String) dt.getThird();
            if (userDist.equals(curDist)) {
                page.append("<tr><td>");
                page.append("<tr BGCOLOR=#eeeeee><td align=\"center\" width=\"10\">");
                page.append("<input type=\"checkbox\" name=\"").append(curCID).append("\">");
                page.append("</td><td>");
                page.append(dt.getSecond());
                page.append("</td></tr>").append("\r\n");
            }
        }
        page.append("</TABLE>").append("\r\n");
    }

    // get all courses and IDs to display in a checkbox table
    public void getSelectedModCourseLinkTable(Vector linkedCIDs, Vector cInfo, StringBuffer page, SystemInfo info, QModule module) throws Exception {
        if (cInfo == null || cInfo.size() == 0)
            return;
        page.append("<b>Link this Module to One or More Classes:</b><br>").append("\r\n");
        page.append("Checked boxes indicate existing links. Check to link, uncheck to unlink.<br>").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0 width=\"60%\">");
        page.append("<tr><th>Select</th><th>Class Name</th><th>Edit Class Module Linkage</td></tr>");

        for (int i = 0; i < cInfo.size(); i++) {
            DataTuple dt = (DataTuple) cInfo.get(i);
            String cID = (String) dt.getFirst();
            page.append("<tr><td>");
            page.append("<tr BGCOLOR=#eeeeee><td align=\"center\" width=\"10\">");
            page.append("<input type=\"checkbox\" name=\"").append(cID).append("\" ");
            boolean inClass = linkedCIDs.contains(cID);
            if (inClass)
                page.append("CHECKED>");
            else
                page.append(">");
            page.append("</td><td>");
            page.append(dt.getSecond());
            page.append("</td>").append("\r\n");
            if (inClass) {
                page.append("<td>");
                page.append("<a href=\"");
                String[] params = new String[]{GeneralHtml.moduleID_, module.getID(), GeneralHtml.classID_, cID};
                page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.editClassModuleLinksMode_, params)).append("\">");
                page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a></td>").append("\r\n");
            } else page.append("<td>&nbsp;</td>");
            page.append("</tr>\r\n");
        }
        page.append("</TABLE>").append("\r\n");
    }


    // get all courses and IDs to display in a checkbox table
    public void getClassTable(List<Course> courses, StringBuffer page, SystemInfo info, QModule module) throws Exception {
        GeneralHtml.getSmallPageHeader("Select Class", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Class", page);
        page.append("<b>Select Class to edit module linkage:</b><br>").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0 width=\"60%\">");
        page.append("<tr><th>Class Name</th><th>Edit Class Module Linkage</td></tr>");

        for (Course c : courses) {
            page.append("<tr><td>");
            page.append(c.getCourseName());
            page.append("</td>").append("\r\n");
            page.append("<td>");
            page.append("<a href=\"");
            String[] params = new String[]{GeneralHtml.classID_, c.getCourseID()};
            page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.editClassModuleLinksMode_, params)).append("\">");
            page.append("<img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"select class\" BORDER=0></a></td>").append("\r\n");
        }
        page.append("</TABLE>").append("\r\n");
        Vector links2 = new Vector();
        links2.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));

        GeneralHtml.getGeneralPageFooter(links2, page);
    }


    // get all courses and IDs to display in a checkbox table
    public void getSelectedModCourseLinkTable(UserInfo info, Vector linkedCIDs,
                                              Vector cInfo, StringBuffer page) throws Exception {
        if (cInfo == null || cInfo.size() == 0)
            return;
        String userDistrict = info.getDistrict();
        page.append("<b>Link this Module to One or More Classes:</b><br>").append("\r\n");
        page.append("Checked boxes indicate existing links. Check to link, uncheck to unlink.<br>").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0 width=\"60%\">");
        for (int i = 0; i < cInfo.size(); i++) {
            DataTuple dt = (DataTuple) cInfo.get(i);
            String district = (String) dt.getThird();
            if (userDistrict.equals(district)) {
                String cID = (String) dt.getFirst();
                page.append("<tr><td>");
                page.append("<tr BGCOLOR=#eeeeee><td align=\"center\" width=\"10\">");
                page.append("<input type=\"checkbox\" name=\"").append(cID).append("\" ");
                if (linkedCIDs.contains(cID))
                    page.append("CHECKED>");
                else
                    page.append(">");
                page.append("</td><td>");
                page.append(dt.getSecond());
                page.append("</td></tr>").append("\r\n");
            }
        }
        page.append("</TABLE>").append("\r\n");
    }

    // get all courses and IDs to display in a checkbox table
    public void getSelectedModCourseLinkTable(String cID, Vector linkedCIDs,
                                              Vector cInfo, StringBuffer page) throws Exception {
        if (cInfo == null || cInfo.size() == 0)
            return;
        page.append("<b>Link this Module to One or More Classes:</b><br>").append("\r\n");
        page.append("Checked boxes indicate existing links. Check to link, uncheck to unlink.<br>").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0 width=\"60%\">");
        for (int i = 0; i < cInfo.size(); i++) {
            DataTuple dt = (DataTuple) cInfo.get(i);
            String curCID = (String) dt.getFirst();
            if (cID.equals(curCID)) {
                page.append("<tr><td>");
                page.append("<tr BGCOLOR=#eeeeee><td align=\"center\" width=\"10\">");
                page.append("<input type=\"checkbox\" name=\"").append(curCID).append("\" ");
                if (linkedCIDs.contains(curCID))
                    page.append("CHECKED>");
                else
                    page.append(">");
                page.append("</td><td>");
                page.append(dt.getSecond());
                page.append("</td></tr>").append("\r\n");
            }
        }
        page.append("</TABLE>").append("\r\n");
    }

    public void getModTypeSelect(SystemInfo info, String type, StringBuffer page) throws Exception {
        if (type == null || type.equals(""))
            getModTypeSelect(info, page);
        else {
            page.append("<SELECT NAME=\"").append(GeneralHtml.moduleType_);
            page.append("\" SIZE=1>").append("\r\n");
            if (type.equals("Practice"))
                page.append("<OPTION VALUE=\"Practice\" SELECTED>Practice").append("\r\n");
            else
                page.append("<OPTION VALUE=\"Practice\">Practice").append("\r\n");
            if (type.equals("Test"))
                page.append("<OPTION VALUE=\"Test\" SELECTED>Test").append("\r\n");
            else
                page.append("<OPTION VALUE=\"Test\">Test").append("\r\n");
            if (type.equals("PreTest"))
                page.append("<OPTION VALUE=\"PreTest\" SELECTED>PreTest").append("\r\n");
            else
                page.append("<OPTION VALUE=\"PreTest\">PreTest").append("\r\n");
            if (type.equals("PostTest"))
                page.append("<OPTION VALUE=\"PostTest\" SELECTED>PostTest").append("\r\n");
            else
                page.append("<OPTION VALUE=\"PostTest\">PostTest").append("\r\n");
            page.append("</SELECT>");
        }
    }

    public void getModTypeSelect(SystemInfo info, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.moduleType_);
        page.append("\" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"Practice\" SELECTED>Practice").append("\r\n");
        page.append("<OPTION VALUE=\"Test\">Test").append("\r\n");
        page.append("<OPTION VALUE=\"PreTest\">PreTest").append("\r\n");
        page.append("<OPTION VALUE=\"PostTest\">PostTest").append("\r\n");
        page.append("</SELECT>");
    }

    public void getLevelSelect(String sel, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.level_).append("\" SIZE=1>").append("\r\n");
        for (int i = 0; i < GeneralHtml.levels.length; i++) {
            page.append("<OPTION VALUE=\"").append(GeneralHtml.levels[i]);
            if (sel.equals(GeneralHtml.levels[i]))
                page.append("\" SELECTED>");
            else
                page.append("\">");
            page.append(GeneralHtml.levels[i]).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }


    public static void getAnsChoiceLinkSelect(StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.ansChoiceLink_).append("\" SIZE=1>").append("\r\n");
        page.append("<OPTION VALUE=\"\">").append("\r\n");
        for (int i = 0; i < GeneralHtml.ans.length; i++) {
            page.append("<OPTION VALUE=\"").append(GeneralHtml.ans[i]);
            page.append("\">");
            page.append(GeneralHtml.ans[i]).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }

    public static void getAnsChoiceLinkSelect(String sel, StringBuffer page) throws Exception {
        if (sel == null || sel.equals(""))
            getAnsChoiceLinkSelect(page);
        else {
            page.append("<SELECT NAME=\"").append(GeneralHtml.ansChoiceLink_).append("\" SIZE=1>").append("\r\n");
            page.append("<OPTION VALUE=\"\">").append("\r\n");
            for (int i = 0; i < GeneralHtml.ans.length; i++) {
                page.append("<OPTION VALUE=\"").append(GeneralHtml.ans[i]);
                if (sel.equals(GeneralHtml.ans[i]))
                    page.append("\" SELECTED>");
                else
                    page.append("\">");
                page.append(GeneralHtml.ans[i]).append("\r\n");
            }
            page.append("</SELECT>").append("\r\n");
        }
    }


    public void getCoachSelect(SystemInfo info, String selID, StringBuffer page) throws Exception {
        page.append("<SELECT NAME=\"").append(GeneralHtml.coachID_).append("\" SIZE=1>").append("\r\n");
        Iterator cits = info.getCoachIDs();
        while (cits.hasNext()) {
            String chID = (String) cits.next();
            page.append("<OPTION VALUE=\"").append(chID);
            if (chID.equals(selID))
                page.append("\" selected>");
            else
                page.append("\">");
            page.append(info.getCoachName(chID)).append("\r\n");
        }
        page.append("</SELECT>").append("\r\n");
    }


    public static void getHintsRow(Hints hints, String root,
                                   String cID, String qID, StringBuffer page) throws Exception {
        String[] hintIDs = hints.getAllHintIDsByCoachID(cID);
        for (int j = 0; j < hintIDs.length; j++) {
            page.append("<td>");
            String hID = hintIDs[j];
            if (hID == null)
                page.append("&nbsp;");
            else {
                page.append("<a href=\"").append(GeneralHtml.getEditHintUrl(root, hID, qID)).append("\">");
                page.append(hID).append("</a>&nbsp;");
            }
            page.append("</td>");
        }
    }

    public static void getQuestionIDs(Iterator qit, StringBuffer page) {
        if (!qit.hasNext())
            page.append("&nbsp;-");
        else {
            boolean f = true;
            while (qit.hasNext()) {
                if (f) {
                    page.append((String) qit.next());
                    f = false;
                } else {
                    page.append(",&nbsp;");
                    page.append((String) qit.next());
                }
            }
        }
    }

    public String getStandardsList(String s1, String s2, String s3) {
        String list = "";
        if (s1 != null)
            list += s1;
        if (s2 != null && !s2.equals("")) {
            list += ", ";
            list += s2;
        }
        if (s3 != null && !s3.equals("")) {
            list += ", ";
            list += s3;
        }
        return list;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void writeAuthorHomePage(SystemInfo info) throws Exception {
        FileReaderWriter frw = new FileReaderWriter();
        StringBuffer page = new StringBuffer();
        getAuthorHomePage("", info, page);
        frw.writeHtmlFile("authorHome.html", page);
    }

    public void writeSelectHintPage(Vector allHints, SystemInfo info) throws Exception {
        FileReaderWriter frw = new FileReaderWriter();
        StringBuffer page = new StringBuffer();
        String qID = "25";
        String msg = "Click on the link button to link a hint to question " + qID;
        Vector links = new Vector();

        //getSelectHintPage(allHints, info, qID, page);
        frw.writeHtmlFile("selectHint.html", page);
    }

    public void writeSelectQuestionPage(SystemInfo info) throws Exception {
        FileReaderWriter frw = new FileReaderWriter();
        StringBuffer page = new StringBuffer();
        List<Question> catQuestions = new ArrayList<Question>();
        String mID = "12";
        getSelectQuestionPage("cat", catQuestions, info, mID, page);
        frw.writeHtmlFile("selectQuestion.html", page);
    }

    public void getEditFormValidateJS(StringBuffer page) {
        page.append("function validate()").append("\r\n");
        page.append("{").append("\r\n");
        page.append(" if(document.forms[0].ansa.value==\"\" || ").append("\r\n");
        page.append("    document.forms[0].ansb.value==\"\" || ").append("\r\n");
        page.append("    document.forms[0].ansc.value==\"\" || ").append("\r\n");
        page.append("    document.forms[0].ansd.value==\"\"){").append("\r\n");
        page.append("     alert(\"You must enter answer choices before saving.\");").append("\r\n");
        page.append("     document.forms[0].ansa.focus();").append("\r\n");
        page.append("   return false;").append("\r\n");
        page.append("  }").append("\r\n");
        page.append(" else if(!document.forms[0].corans[0].checked && ").append("\r\n");
        page.append("    !document.forms[0].corans[1].checked && ").append("\r\n");
        page.append("    !document.forms[0].corans[2].checked && ").append("\r\n");
        page.append("    !document.forms[0].corans[3].checked){").append("\r\n");
        page.append("     alert(\"You must specify a correct answer before saving.\");").append("\r\n");
        page.append("return false;").append("\r\n");
        page.append("  }").append("\r\n");
        page.append(" return true;").append("\r\n");
        page.append("}").append("\r\n");
    }

    public void getEditClassModuleLinkagePage(Connection conn, List<QModule> mods, Course course, SystemInfo info,
                                              StringBuffer page, String msg, List<ModModuleLink> links) throws Exception {
        GeneralHtml.getSmallPageHeader("Edit Class Module Linkage", false, page, info, false);
        GeneralHtml.getJavascriptBegin(page);
//        String delUrl = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteModuleMode_,
//                GeneralHtml.moduleID_, m.getID());
//        GeneralHtml.getJavascriptCheckDelete(delUrl, page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<body>").append("\r\n");

        GeneralHtml.getSmallBanner("Edit Class Module Linkage", page);
        if (msg != null) {
            page.append("<h3>" + msg + "</h3><br>");
        }
        page.append("<br>");
        page.append("<TABLE BORDER=1 CELLSPACING=0>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Class ID&nbsp;</td>");
        page.append("<td>").append(course.getCourseID()).append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>Class Name</td><td>");
        page.append(course.getCourseName()).append("</td></tr>\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Institution</td>");
        page.append("<td>").append(course.getInstitution()).append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Teacher</td>");
        page.append("<td>" + course.getTeacher()).append("</td></tr>").append("\r\n");
        page.append("</table><br><br>").append("\r\n");

        // Show each module that has been connected to this class
        page.append("<h3>This class uses the following modules:</h3>");
        page.append("<table border=1 cellspacing=0>");
        page.append("<tr BGCOLOR=#eeeeee><th>Id</th><th>Name</th><th>Type</th><th>Author</th><th>Unlink Module</th></tr>");
        for (QModule classMod : mods) {
            String mURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editModuleMode_, GeneralHtml.moduleID_, classMod.getID());
            String delURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.removeClassModuleMode_,
                    new String[]{GeneralHtml.moduleID_, classMod.getID(), GeneralHtml.classID_, course.getCourseID()});
            page.append("<tr BGCOLOR=#eeeeee><td>" + classMod.getID() + "</td><td><a href=\"" + mURL + "\">" + classMod.getName() + "</a></td><td>" + classMod.getType() + "</td><td>" +classMod.getAuthorName()+ "</td><td>");
            page.append("<a href=\"" + delURL + "\"><img SRC=\"").append(info.getContextPath());
            page.append("/images/del.gif\" ALT=\"delete link\" BORDER=0></a></td>");
            page.append("</tr>\n");
        }
        page.append("</table><br><br>\n");
        String[] params = new String[]{GeneralHtml.classID_, course.getCourseID()};
        String url = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selClassModuleMode_, params);
        page.append("<b>Click <a href=\"" + url + "\"><img src=\"" + info.getContextPath() + "/images/add.gif\" alt=\"Add module\" border=0></a> to add a new module to this class.</b><br>");

        // Show each linked module that is available.
        page.append("<h3>This class has the following module dependencies:</h3>");
        page.append("<table border=1 cellspacing=0>");
        page.append("<tr BGCOLOR=#eeeeee><th>Parent Mod Id</th><th>Parent Mod name</th><th>Child Mod Id</th><th>Child Mod Name</th><th>Delete Dependency</th></tr>");
        for (ModModuleLink l : links) {
            String cURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editModuleMode_, GeneralHtml.moduleID_, l.getChildMod());
            String pURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.editModuleMode_, GeneralHtml.moduleID_, l.getParentMod());
            String delURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.deleteClassModuleLinkMode_,
                    new String[]{GeneralHtml.childModID_, l.getChildMod(), GeneralHtml.parentModID_, l.getParentMod(), GeneralHtml.classID_, course.getCourseID()});
            page.append("<tr BGCOLOR=#eeeeee><td>" + l.getParentMod() + "</td><td><a href=\"" + pURL + "\">" + l.getParentModName() + "</a></td><td>" + l.getChildMod() + "</td><td><a href=\"" + cURL + "\">" + l.getChildModName() + "</a></td><td>");
            page.append("<a href=\"" + delURL + "\"><img SRC=\"").append(info.getContextPath());
            page.append("/images/del.gif\" ALT=\"delete link\" BORDER=0></a></td>");
            page.append("</tr>\n");
        }
        page.append("</table><br><br>\n");
        params = new String[]{GeneralHtml.classID_, course.getCourseID()};
        url = GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.viewSourceModuleLinkMode_, params);
        page.append("<b>Click <a href=\"" + url + "\"><img src=\"" + info.getContextPath() + "/images/add.gif\" alt=\"Add Dependency\" border=0></a> to add a new module dependency for this class.</b><br>");
        Vector links2 = new Vector();
        links2.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.selectModuleMode_), "select module"));
        links2.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.authorHomeMode_), "admin home"));

        GeneralHtml.getGeneralPageFooter(links2, page);

    }

    public void getClassSourceModuleLinkagePage(Connection conn, List<QModule> mods, Course course, SystemInfo info, StringBuffer page, String msg) throws Exception {
        GeneralHtml.getSmallPageHeader("Select Dependent Module", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Dependent Module", page);

        page.append("<TABLE BORDER=1 CELLSPACING=0>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Class ID&nbsp;</td>");
        page.append("<td>").append(course.getCourseID()).append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>Class Name</td><td>");
        page.append(course.getCourseName()).append("</td></tr>\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Institution</td>");
        page.append("<td>").append(course.getInstitution()).append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Teacher</td>");
        page.append("<td>" + course.getTeacher()).append("</td></tr>").append("\r\n");
        page.append("</table><br><br>").append("\r\n");

        page.append("<h3>Click on \"Select\" To Select a dependent Module");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=80%>").append("\r\n");
        page.append("<tr bgcolor=#dddddd><td>Select</td><td>ModID</td><td>Mod Name</td><td>Status</td><td>Mod Type</td><td>Author</td><td>Linked Question IDs</td></tr>").append("\r\n");
        for (QModule m : mods) {
            page.append("<tr bgcolor=#eeeeee><td align=center width=10%>");
            page.append("<a href=\"");
            String[] params = new String[]{GeneralHtml.childModID_, m.getID(), GeneralHtml.classID_, course.getCourseID()};
            String selURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.selClassSourceModuleLinkMode_, params);
            page.append(selURL);
            page.append("\"><img SRC=\"").append(info.getContextPath());
            page.append("/images/sel.gif\" ALT=\"select dependent module\" BORDER=0></a></td>");
            page.append("\r\n");
            page.append("<td>").append(m.getID()).append("</td>");
            page.append("<td>").append(m.getName()).append("</td>");
            if (m.getStatus())
                page.append("<td>").append(1).append("</td>");
            else
                page.append("<td>").append(0).append("</td>");

            page.append("<td>").append(m.getType()).append("</td>");
             page.append("<td>").append(m.getAuthorName()).append("</td>");
            page.append("<td>");
            getQuestionIDs(m.getLinkedQuestionIDs(), page);
            page.append("</td></tr>");
        }
        page.append("</table><br>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.editClassModuleLinksMode_,
                GeneralHtml.classID_, course.getCourseID()), "back to Class Module linkage page"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public void getClassParentModuleLinkagePage(Connection conn, List<QModule> mods, Course course,
                                                SystemInfo info, StringBuffer page, QModule childMod) throws Exception {
        GeneralHtml.getSmallPageHeader("Select Parent Module", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Parent Module", page);
        page.append("<TABLE BORDER=1 CELLSPACING=0>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Class ID&nbsp;</td>");
        page.append("<td>").append(course.getCourseID()).append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>Class Name</td><td>");
        page.append(course.getCourseName()).append("</td></tr>\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Institution</td>");
        page.append("<td>").append(course.getInstitution()).append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Teacher</td>");
        page.append("<td>" + course.getTeacher()).append("</td></tr>").append("\r\n");
        page.append("</table><br><br>").append("\r\n");

        page.append("<h3>Child Module " + childMod.getID() + ":" + childMod.getName() + " has been selected.");
        page.append("<h3>Now Click on \"Select\" To Select a module that will PRECEDE the child.</h3>");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=80%>").append("\r\n");
        page.append("<tr bgcolor=#dddddd><td>Select</td><td>ModID</td><td>Mod Name</td><td>Status</td><td>Mod Type</td><td>Author</td><td>Linked Question IDs</td></tr>").append("\r\n");
        for (QModule m : mods) {
            page.append("<tr bgcolor=#eeeeee><td align=center width=10%>");
            page.append("<a href=\"");
            String[] params = new String[]{GeneralHtml.parentModID_, m.getID(),
                    GeneralHtml.classID_, course.getCourseID(),
                    GeneralHtml.childModID_, childMod.getID()};
            String selURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                    AuthoringSubsystem.selClassParentModuleLinkMode_, params);
            page.append(selURL);
            page.append("\"><img SRC=\"").append(info.getContextPath());
            page.append("/images/sel.gif\" ALT=\"select parent module\" BORDER=0></a></td>");
            page.append("\r\n");
            page.append("<td>").append(m.getID()).append("</td>");
            page.append("<td>").append(m.getName()).append("</td>");
            if (m.getStatus())
                page.append("<td>").append(1).append("</td>");
            else
                page.append("<td>").append(0).append("</td>");
            page.append("<td>").append(m.getType()).append("</td>");
            page.append("<td>").append(m.getAuthorName()).append("</td>");
            page.append("<td>");
            getQuestionIDs(m.getLinkedQuestionIDs(), page);
            page.append("</td></tr>");
        }
        page.append("</table><br>").append("\r\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.editClassModuleLinksMode_,
                GeneralHtml.classID_, course.getCourseID()), "back to Class Module linkage page"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }


    public void getClassModulePage(Connection conn, List<QModule> mods, Course course, SystemInfo info, StringBuffer page, String msg) throws Exception {
        GeneralHtml.getSmallPageHeader("Select Class Modules", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Select Class Modules", page);

        page.append("<TABLE BORDER=1 CELLSPACING=0>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Class ID&nbsp;</td>");
        page.append("<td>").append(course.getCourseID()).append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>Class Name</td><td>");
        page.append(course.getCourseName()).append("</td></tr>\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Institution</td>");
        page.append("<td>").append(course.getInstitution()).append("</td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>Teacher</td>");
        page.append("<td>" + course.getTeacher()).append("</td></tr>").append("\r\n");
        page.append("</table><br><br>").append("\r\n");
        page.append("<h3>Click checkboxes to include modules in this class <br><br>");

        String submitURL = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.addClassModulesMode_, GeneralHtml.classID_, course.getCourseID());
        // create a URL so that this includes all selected classes with an addClassModulesMode event.
        page.append("<form action=\"" + submitURL + "\" method=\"POST\">");
        page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 WIDTH=80%>").append("\r\n");
        page.append("<tr bgcolor=#dddddd><td>Include</td><td>ModID</td><td>Mod Name</td><td>Author</td><td>Status</td><td>Mod Type</td><td>Linked Question IDs</td></tr>").append("\r\n");
        for (QModule m : mods) {
            page.append("<tr bgcolor=#eeeeee><td align=center width=10%>");
            page.append("<input type=\"checkbox\" value=\"" + m.getID() + "\" name=\"includeModule\"></td>");
            page.append("\r\n");
            page.append("<td>").append(m.getID()).append("</td>");
            page.append("<td>").append(m.getName()).append("</td>");
            page.append("<td>").append(m.getAuthorName()).append("</td>");
            if (m.getStatus())
                page.append("<td>").append(1).append("</td>");
            else
                page.append("<td>").append(0).append("</td>");
            page.append("<td>").append(m.getType()).append("</td>");
            page.append("<td>");
            getQuestionIDs(m.getLinkedQuestionIDs(), page);
            page.append("</td></tr>");
        }
        page.append("</table><br>").append("\r\n");
        page.append("<input type=\"submit\" value=\"Submit\">");
        page.append("</form><br>\n");
        Vector links = new Vector();
        links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), AuthoringSubsystem.editClassModuleLinksMode_,
                GeneralHtml.classID_, course.getCourseID()), "back to Class Module linkage page"));
        GeneralHtml.getGeneralPageFooter(links, page);
    }

    public static void getCourseSelect(SystemInfo info, List<Course> courses, Course selectedCourse, StringBuffer page) {

        page.append("<SELECT NAME=\"").append(GeneralHtml.classID_);
        page.append("\" SIZE=1>").append("\r\n");

        for (Course course: courses) {
            if (selectedCourse != null && selectedCourse.getCourseID().equals(course.getCourseID()))
                page.append("<OPTION VALUE=\"" + course.getCourseID() + "\" SELECTED>" + course.getCourseName() + "").append("</option>\r\n");
            else
                page.append("<OPTION VALUE=\"" + course.getCourseID() + "\">" + course.getCourseName() + "").append("</option>\r\n");
        }
        page.append("</SELECT>");

    }

}