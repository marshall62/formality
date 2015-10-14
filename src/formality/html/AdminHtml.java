package formality.html;

import java.util.Vector;
import formality.FileReaderWriter;
import formality.content.SystemInfo;
import formality.controller.FormalitySubsystem;
import formality.controller.AuthoringSubsystem;
import formality.controller.TeacherSubsystem;
import formality.html.*;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AdminHtml {
    public static final String adminHomeMode = "adminhome";
    public AdminHtml() {
    }

    public void getAdminHomePage(SystemInfo info, StringBuffer page)throws Exception{
       GeneralHtml.getPageHeader("Admin Home Page", page);
        page.append("<body>").append("\r\n");
       GeneralHtml.getPageBanner("Admin Home Page", page);
       Vector links = new Vector();
       links.add(GeneralHtml.getLinkStr(GeneralHtml.getAuthorUrl(info.getServletRootAndID(),
                                             AuthoringSubsystem.authorHomeMode_),
                                             "author home"));
       links.add(GeneralHtml.getLinkStr(GeneralHtml.getUrl(info.getServletRootAndID(),
                                             FormalitySubsystem.teacherFxnParam,
                                              TeacherSubsystem.teacherHomeMode_, null, null),
                                             "teacher home"));
       GeneralHtml.getGeneralPageFooter(links, page);
    }

     public void getNewUserPage(String lName, String inst, String role,
                                SystemInfo info, String msg, StringBuffer page)throws Exception{
        GeneralHtml.getPageHeader("Enter New User", page);
         page.append("<body>").append("\r\n");
        GeneralHtml.getPageBanner("Enter New User", page);
 //TODO the form        
         Vector links = new Vector();
         links.add(GeneralHtml.getLinkStr(GeneralHtml.getUrl(info.getServletRootAndID(),
                                               FormalitySubsystem.adminFxnParam,
                                                adminHomeMode, null, null),
                                               "admin home"));

         GeneralHtml.getGeneralPageFooter(links, page);

     }
    /////////////////////////////////
    public void writeAdminHomePage(SystemInfo info) throws Exception {
        FileReaderWriter frw = new FileReaderWriter();
        StringBuffer page = new StringBuffer();
        getAdminHomePage(info, page);
        frw.writeHtmlFile(".html", page);
    }

        public void writeNewUserPage(String lName, String inst, String role,
                                SystemInfo info, String msg) throws Exception {
        FileReaderWriter frw = new FileReaderWriter();
        StringBuffer page = new StringBuffer();
        getNewUserPage(lName, inst, role,
                                info, msg, page);
        frw.writeHtmlFile("adminHome.html", page);
    }
}
