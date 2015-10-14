package formality.html.auth;

import formality.content.SystemInfo;
import formality.content.Course;
import formality.content.database.mysql.DbCourse;
import formality.html.GeneralHtml;
import formality.controller.FormalitySubsystem;
import formality.controller.TeacherSubsystem;
import formality.controller.AuthoringSubsystem;
import formality.admin.StudentUser;

import java.sql.Connection;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Jun 14, 2011
 * Time: 11:54:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClassListOperationsPage {
    private Connection conn;

    public ClassListOperationsPage(Connection conn) {
        this.conn=conn;
    }

    public static void getJavascriptCheckDelete(StringBuffer page) {
        page.append("\nfunction checkDeleteStudent() {").append("\r\n");
        page.append("  if(window.confirm(\"Deleting a student is a permanent operation.   Are you sure you wish to delete this student?\"))").append("\r\n");
        page.append("      return true;").append("\r\n");
        page.append("  else return false;").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
    }

    public static void getJavascriptCheckClearData(StringBuffer page) {
        page.append("\nfunction checkClearStudentData() {").append("\r\n");
        page.append("  if(window.confirm(\"Clearing student data is a permanent operation.   Are you sure you wish to clear this data?\"))").append("\r\n");
        page.append("      return true;").append("\r\n");
        page.append("  else return false;").append("\r\n");
        page.append("}").append("\r\n").append("\r\n");
    }

    public void createClassList(SystemInfo info, String msg, List<Course> courses, Course selectedCourse, StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("Class List Operations", false, page, info, false);
        GeneralHtml.getJavascriptBegin(page);
        getJavascriptCheckClearData(page);
        getJavascriptCheckDelete(page);
        GeneralHtml.getJavascriptEnd(page);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Class List Operations", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        if (selectedCourse == null)
            page.append("<h3>").append("Select a course from the list below").append("</h3>").append("\r\n");


        page.append("<br><FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.classListOperations_));
        page.append("\" METHOD=\"POST\" NAME=\"ClassListOpsForm\">").append("\r\n");


        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee>").append("\r\n");
        page.append("<td>&nbsp;Course&nbsp;</td><td>").append("\r\n");
        AuthorHtml.getCourseSelect(info, courses,selectedCourse,page);
        page.append("</td></tr></table><br>");
        // If a course is sent in then display the students in this course. o/w this form will
        // be used to select a course and then we return to this with the selected course.
        if (selectedCourse != null) {
            getClassListTable(info, selectedCourse,page);
        }
        else page.append("<TABLE WIDTH=100%><TR><TD ALIGN=left><INPUT TYPE=\"SUBMIT\" NAME=\"SAVE\" VALUE=\"Submit\"></TD></TR></TABLE>").append("\r\n");

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

    public void getClassListTable (SystemInfo info, Course course, StringBuffer page) throws Exception {
        List<StudentUser> users= DbCourse.getStudentList(conn,Integer.parseInt(course.getCourseID()));
        page.append("<table border=\"1\"><tr><td>ID</td><td>userName</td><td>Initials</td><td>Age</td><td>Gender</td><td>Edit</td><td>Clear Data</td><td>Delete</td></tr>\n");
        for (StudentUser stud: users) {
            String[] params = new String[] {GeneralHtml.studentID_,Integer.toString(stud.getId()),
                        GeneralHtml.classID_,course.getCourseID()};
            String url = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),AuthoringSubsystem.editStudentMode_,params);
            String url2 = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),AuthoringSubsystem.clearStudentDataMode_,params);
            String url3 = GeneralHtml.getAuthorUrl(info.getServletRootAndID(),AuthoringSubsystem.deleteStudentMode_,params);

            page.append("<tr><td>" +stud.getId()+ "</td>"+
                    "<td>" +stud.getLoginName()+ "</td><td>" +stud.getInitials()+
                    "</td><td>" +stud.getAge()+ "</td><td>" +stud.getGender()+ "</td>"+
                    "<td><a href=\"" +url+ "\">edit</a></td>" +
                    "<td><a onClick=\"return checkClearStudentData();\" href=\"" +url2+ "\">clear data</a></td>" +
                    "<td><a onClick=\"return checkDeleteStudent();\" href=\"" +url3+ "\">delete</a></td>" +
                    "</tr>\n");
        }
        page.append("</table>\n");
        
    }
}
