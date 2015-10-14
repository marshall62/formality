package formality.html.auth;

import formality.content.SystemInfo;
import formality.content.Course;
import formality.model.UserInfo;
import formality.html.GeneralHtml;
import formality.controller.AuthoringSubsystem;
import formality.controller.FormalitySubsystem;
import formality.controller.TeacherSubsystem;
import formality.servlet.StudentsFileUploadServlet;

import java.sql.Connection;
import java.util.Vector;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Apr 11, 2011
 * Time: 11:52:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateNewStudentPage extends CreateNewTeacherPage {
    String contextPath;

    public CreateNewStudentPage(Connection conn) throws Exception {
        super(conn);

    }

    public void createFileUploadPage (String msg, SystemInfo info, List<Course> courses,
                            StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        contextPath = info.getContextPath();
        GeneralHtml.getSmallPageHeader("Upload Student File", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Upload Student File", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>").append("Make sure you have first created a new course and select it from the list below").append("</h3>").append("\r\n");

        page.append("<FORM ACTION=\"StudentsFileUploadServlet\"");
        page.append("\" METHOD=\"POST\" enctype=\"multipart/form-data\" NAME=\"NewStudentsUploadForm\">").append("\r\n");

        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee>").append("\r\n");
        page.append("<td>&nbsp;Course&nbsp;</td><td>").append("\r\n");
        AuthorHtml.getCourseSelect(info, courses,null,page);
        page.append("</td></tr></table><br>");

        page.append("Provide a file of information about students in this course.  Uploading this file will result in the creation of new students with the login names you provide.<br><br>");
        page.append("Student File: <input type=\"file\" name=\"" + StudentsFileUploadServlet.STUDFILE + "\" size=\"80\">");
        page.append("<BR/>");
        page.append("<input type=\"hidden\" name=\"" + FormalitySubsystem.userIDParam + "\" value=\"" + info.getUserID() + "\"/>\n");
        page.append("<br><br><INPUT TYPE=\"SUBMIT\" NAME=\"save\" VALUE=\"Upload the file\">");


        page.append("<br><br>").append("\r\n");
        page.append("<p>You must upload a file in comma-separated-value (CSV) format.   &nbsp;You can create a file in Excel and export it in this format by selecting a CSV file type when you click &quot;Save As&quot;. <br />\n" +
                "  <br />\n" +
                "The first row of the file should contain the column names:</p>\n" +
                "<p> login name,password,initials,age,gender,native language,title 1,IEP,math rating,reading rating</p>\n" +
                "<p>Rows of data must immediately follow. Make sure there are no extra empty columns or rows.</p>\n" +
                "<p>The only mandatory value is the login name.<br />\n" +
                "Given below are the legal values for some columns with default values shown in italics. &nbsp;You may only provide one value per cell (e.g. no way to state that a student has both a math AND reading IEP).</p>\n" +
                "<ul>\n" +
                "  <li>IEP:  math, reading, other, none, <em>unknown</em></li>\n" +
                "  <li>title1: none,math,ela,<em>unspecified</em></li>\n" +
                "  <li>math rating: below, above, on, <em>unknown</em></li>\n" +
                "  <li>reading rating: below, above, on, <em>unknown</em></li>\n" +
                "</ul>\n" +
                "<p><a href=\"" +contextPath+ "/sampleUpload.csv\">Here</a> is a sample CSV file that you can download and modify with your data.</p>");

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



    public void createPage(String msg, SystemInfo info, UserInfo userInfo, List<Course> courses,
                           Course selectedCourse, StringBuffer page, int courseId) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader(userInfo==null ? "Create New Student" : "Edit Student", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner(userInfo==null ? "Create New Student" : "Edit Student", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
//        page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
        page.append("<br><FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                userInfo==null? AuthoringSubsystem.saveNewStudentMode_: AuthoringSubsystem.saveEditedStudentMode_));
        page.append("\" METHOD=\"POST\" NAME=\"NewStudentForm\">").append("\r\n");
        if (userInfo != null)
            page.append("<input type=\"hidden\" name=\"" +GeneralHtml.studentID_+ "\" value=\"" + userInfo.getUserID() + "\"/>");
        if (courseId != -1)
            page.append("<input type=\"hidden\" name=\"" +GeneralHtml.classID_+ "\" value=\"" + courseId + "\"/>");
        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee><td>&nbsp;ID&nbsp;</td>").append("\r\n");
        if (userInfo != null)   {
            page.append("<td>" + userInfo.getUserID() + "</td>");
        }
        else page.append("<td>&nbsp;</td>");
        page.append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Initials&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.initials);
        if (userInfo != null)
            page.append("\" VALUE=\"").append(userInfo.getInitals()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Age&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.age);
        if (userInfo != null)
            page.append("\" VALUE=\"").append(userInfo.getAge()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Gender&nbsp;</td><td>");
        String[] vals = new String[UserInfo.Gender.values().length];
        for (int i=0;i<UserInfo.Gender.values().length;i++)
            vals[i]= UserInfo.Gender.values()[i].toString();
        addRadioGroup(GeneralHtml.gender,vals,userInfo != null ? userInfo.getGender() : null,page);
        page.append("</td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;User Name&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.uname);
        if (userInfo != null)
            page.append("\" VALUE=\"").append(userInfo.getLogin()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Password&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"password\" NAME=\"").append(GeneralHtml.password);
        if (userInfo != null)
            page.append("\" VALUE=\"").append(userInfo.getUserPwd()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Home Language&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.language_);
        if (userInfo != null)
            page.append("\" VALUE=\"").append(userInfo.getLanguage()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        vals = new String[UserInfo.IEP.values().length];
        for (int i=0;i<UserInfo.IEP.values().length;i++)
            vals[i]= UserInfo.IEP.values()[i].toString();
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;IEP&nbsp;</td><td>");
        addRadioGroup(GeneralHtml.iep, vals,userInfo != null ? userInfo.getIep().toString() : null,page);
        page.append("</td></tr>").append("\r\n");

        vals = new String[UserInfo.Level.values().length];
        for (int i=0;i<UserInfo.Level.values().length;i++)
            vals[i]= UserInfo.Level.values()[i].toString();
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Reading Level&nbsp;</td><td>");
        addRadioGroup(GeneralHtml.readingLevel,vals,userInfo != null ? userInfo.getReadingLev().toString() : null,page);
        page.append("</td></tr>").append("\r\n");


        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Math Level&nbsp;</td><td>");
        addRadioGroup(GeneralHtml.mathLevel,vals,userInfo != null ? userInfo.getMathLev().toString() : null,page);
        page.append("</td></tr>").append("\r\n");


        vals = new String[UserInfo.Title.values().length];
        for (int i=0;i<UserInfo.Title.values().length;i++)
            vals[i]= UserInfo.Title.values()[i].toString();
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Title 1&nbsp;</td><td>");
        addRadioGroup(GeneralHtml.title,vals,userInfo != null ? userInfo.getTitle().toString() : null,page);
        page.append("</td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Course&nbsp;</td><td>").append("\r\n");
        if (selectedCourse == null)
            AuthorHtml.getCourseSelect(info, courses,userInfo!= null ? selectedCourse : null,page);
        else page.append(selectedCourse.getCourseName());
        page.append("</td></tr>");



        page.append("</table><br>").append("\r\n");
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


    /*
    Math ability:
<table width="200">
    <tr>
      <td><label>
        <input type="radio" name="Math level" value="low" id="Math level_0">
        low</label></td>

      <td><label>
        <input type="radio" name="Math level" value="medium" id="Math level_1">
        medium</label></td>

      <td><label>
        <input type="radio" name="Math level" value="high" id="Math level_2">
        high</label></td>
    </tr>
  </table>
     */
    public void addRadioGroup (String groupName, String[] choices, String selectedChoice, StringBuffer sb) {
        sb.append("<table><tr>");
        for (String choice: choices) {
            sb.append("\n\t<td><label>\n\t\t<input type=\"radio\" " +(selectedChoice != null && selectedChoice.equals(choice) ? "checked" : "") + " name=\"" + groupName + "\" value=\"" + choice + "\">" + choice + "</label></td>");
        }
        sb.append("</tr></table>");
        
    }
}
