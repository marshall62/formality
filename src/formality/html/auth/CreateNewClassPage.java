package formality.html.auth;

import formality.html.GeneralHtml;
import formality.controller.AuthoringSubsystem;
import formality.controller.FormalitySubsystem;
import formality.controller.TeacherSubsystem;
import formality.content.SystemInfo;
import formality.content.Course;
import formality.content.database.mysql.DBAccessor;

import java.util.Vector;
import java.util.TreeMap;
import java.util.Set;
import java.util.Map;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Apr 6, 2011
 * Time: 10:35:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateNewClassPage {
    TreeMap<String,String> teacherMap;
    Map<String,String> districtMap;


    public CreateNewClassPage (Connection conn) throws Exception {
        teacherMap = new DBAccessor().getAllTeachers(conn);
        districtMap = new DBAccessor().getAllDistricts(conn);
    }


    public void createPage(String msg, SystemInfo info, Course course,
                           StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("Create New Class", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Create New Class", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
        page.append("<br><FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.saveNewClassMode_));
        page.append("\" METHOD=\"POST\" NAME=\"NewClassForm\">").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee><td>&nbsp;ID&nbsp;</td>").append("\r\n");
        if (course != null)
            page.append("<td>" + course.getCourseID() + "</td>");
        else page.append("<td>&nbsp;</td>");
        page.append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Name&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.courseName_);
        if (course != null)
            page.append("\" VALUE=\"").append(course.getCourseName()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Teacher&nbsp;</td><td>").append("\r\n");
        getTeacherSelect(info, course !=null?course.getTeacherId():"",page);
        page.append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;District&nbsp;</td><td>").append("\r\n");
        getDistrictSelect(info, course!=null?course.getDistrictStr():"",page);
        page.append("</td></tr>");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Institution&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.institution_);
        if (course != null)
            page.append("\" VALUE=\"").append(course.getInstitution()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>Course Controller</td><td>");
        getControllerSelect(course,page);
        page.append("</td></tr></table><br>").append("\r\n");
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

    private void getTeacherSelect(SystemInfo info,  String selectedTeacherId, StringBuffer page) {

        page.append("<SELECT NAME=\"").append(GeneralHtml.teacherID_);
        page.append("\" SIZE=1>").append("\r\n");
        Set<String> keys = teacherMap.keySet();
        for (String id: keys) {
            String name = teacherMap.get(id);
            if (selectedTeacherId.equals(id))
                page.append("<OPTION VALUE=\"" + id + "\" SELECTED>" + name + "").append("</option>\r\n");
            else
                page.append("<OPTION VALUE=\"" + id + "\">" + name + "").append("</option>\r\n");
        }
        page.append("</SELECT>");

    }

    private void getDistrictSelect(SystemInfo info,  String selectedDistrictId, StringBuffer page) {

        page.append("<SELECT NAME=\"").append(GeneralHtml.district_);
        page.append("\" SIZE=1>").append("\r\n");
        Set<String> keys = districtMap.keySet();
        for (String id: keys) {
            String name = districtMap.get(id);
            if (selectedDistrictId.equals(id))
                page.append("<OPTION VALUE=\"" + id + "\" SELECTED>" + name + "").append("</option>\r\n");
            else
                page.append("<OPTION VALUE=\"" + id + "\">" + name + "").append("</option>\r\n");
        }
        page.append("</SELECT>");

    }

    private void getControllerSelect(Course course, StringBuffer page) {

        page.append("<SELECT NAME=\"").append(GeneralHtml.courseController_);
        page.append("\" SIZE=1>").append("\r\n");
        if (course != null && course.getCourseController().equals("BaseCourseController"))
            page.append("<option SELECTED value=\"BaseCourseController\">Plain</option>\n");
        else page.append("<option value=\"BaseCourseController\">Plain</option>\n");
        if (course != null && course.getCourseController().equals("ScoreboardController"))
            page.append("<option SELECTED value=\"ScoreboardController\">Scoreboard</option>\n");
        else page.append("<option value=\"ScoreboardController\">Scoreboard</option>\n");
        page.append("</SELECT>");

    }


}
