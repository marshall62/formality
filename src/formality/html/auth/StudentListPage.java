package formality.html.auth;

import formality.Util.DataTuple;
import formality.html.GeneralHtml;
import formality.controller.TeacherSubsystem;
import formality.controller.FormalitySubsystem;
import formality.admin.StudentUser;
import formality.content.SystemInfo;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Jun 14, 2011
 * Time: 11:00:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudentListPage {
    Connection conn;

    public StudentListPage(Connection conn) {
        this.conn = conn;
    }

    public void createStudentList(SystemInfo info, String msg, List<StudentUser> students, StringBuffer page) throws Exception {
        page.append("<table>");
        for (StudentUser s: students) {

              String sID = s.getLoginName();
              page.append("<tr bgcolor=#eeeeee>");
              page.append("<td>" + sID + "</td>");
              page.append("<td align=center width=10%>");
              page.append("<a href=\"");
              page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(), "will be DeleteStudentMode",
                      new String[] {GeneralHtml.studentID_, sID,
                              FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()}));
              page.append("\"><img SRC=\"").append(info.getContextPath()).append("/images/sel.gif\" ALT=\"Delete Student\" BORDER=0></a></td>");

              page.append("<td>");
              page.append("</tr>").append("\r\n");
          }
          page.append("</table>");


    }
}
