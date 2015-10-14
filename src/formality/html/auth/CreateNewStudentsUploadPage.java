package formality.html.auth;

import formality.html.GeneralHtml;
import formality.controller.AuthoringSubsystem;
import formality.controller.FormalitySubsystem;
import formality.controller.TeacherSubsystem;
import formality.content.SystemInfo;
import formality.content.Course;
import formality.content.database.mysql.DBAccessor;
import formality.model.UserInfo;

import java.util.Vector;
import java.util.List;
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
public class CreateNewStudentsUploadPage {
    Map<String,String> districtMap;


    public CreateNewStudentsUploadPage(Connection conn) throws Exception {
        districtMap = new DBAccessor().getAllDistricts(conn);
    }


    public void createPage(String msg, SystemInfo info, UserInfo teacherInfo, List<Course> courses,
                           StringBuffer page) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        GeneralHtml.getSmallPageHeader("Create New Teacher", false, page, info, false);
        page.append("<body>").append("\r\n");
        GeneralHtml.getSmallBanner("Create New Teacher", page);
        if (msg != null)
            page.append("<h3>").append(msg).append("</h3>").append("\r\n");
        page.append("<h3>").append("* denotes a required field").append("</h3>").append("\r\n");
        page.append("<br><FORM ACTION=\"");
        page.append(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                AuthoringSubsystem.saveNewTeacherMode_));
        page.append("\" METHOD=\"POST\" NAME=\"NewTeacherForm\">").append("\r\n");
        page.append("<TABLE BORDER=1 CELLSPACING=0><tr BGCOLOR=#eeeeee><td>&nbsp;ID&nbsp;</td>").append("\r\n");
        if (teacherInfo != null)
            page.append("<td>" + teacherInfo.getUserID() + "</td>");
        else page.append("<td>&nbsp;</td>");
        page.append("</td></tr>");
        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;First Name&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.fname);
        if (teacherInfo != null)
            page.append("\" VALUE=\"").append(teacherInfo.getUserFname()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Last Name&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.lname);
        if (teacherInfo != null)
            page.append("\" VALUE=\"").append(teacherInfo.getUserLname()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;User Name&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.uname);
        if (teacherInfo != null)
            page.append("\" VALUE=\"").append(teacherInfo.getLogin()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Password&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"password\" NAME=\"").append(GeneralHtml.password);
        if (teacherInfo != null)
            page.append("\" VALUE=\"").append(teacherInfo.getUserPwd()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;District&nbsp;</td><td>").append("\r\n");
        getDistrictSelect(info, teacherInfo!=null?teacherInfo.getDistrict():"",page);
        page.append("</td></tr>");

        page.append("<tr BGCOLOR=#eeeeee><td>&nbsp;Institution&nbsp;</td><td>");
        page.append("<INPUT TYPE=\"text\" NAME=\"").append(GeneralHtml.institution_);
        if (teacherInfo != null)
            page.append("\" VALUE=\"").append(teacherInfo.getUserInstitution()).append("\"");
        else page.append("\"");
        page.append(" SIZE=60 MAXLENGTH=50 ></td></tr>").append("\r\n");

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


    protected void getDistrictSelect(SystemInfo info,  String selectedDistrictId, StringBuffer page) {

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




}