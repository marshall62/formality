package formality.controller;

import java.util.TreeMap;
import java.util.Vector;
import java.util.Map;

import formality.content.*;
import formality.html.*;
import formality.content.database.mysql.DBAccessor;
import formality.systemerror.SystemException;
import java.sql.Connection;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DBAccessor;
/**
 * <p>Title: </p>
 */
public class AdminSubsystem {
    //modes
    public static final String adminHomeMode ="adminhome";
    public static final String saveNewUserMode = "newuser";

    AuthoringSubsystem as;
    StudentSubsystem ss;
    DBAccessor dbAccessor_;

    public AdminSubsystem() {}
    public void handleRequest(Map params, SystemInfo info, Connection conn, StringBuffer page)throws Exception{
       String mode = GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "");
       dbAccessor_ = new DBAccessor();
      // adminPage_ = new AuthorHtml();

       if (mode.equals(adminHomeMode)) {
         //  dbAccessor_.loadSystemInfo(info, conn);
         //  dbAccessor_.loadModules(conn, info);
         //  dbAccessor_.loadModuleQCounts(conn, info.getModules());
           //getAuthorHomePage(info, page);
          // adminPage_.writeAuthorHomePage(info);
       }
      else if (mode.equals(saveNewUserMode)) {
        String msg="";
        boolean success=false;
        int uID=-1;
        String login = GeneralHtml.getParamStr(params, FormalitySubsystem.userLogin, "");
        String pwd = GeneralHtml.getParamStr(params, FormalitySubsystem.userPassword, "");
        String fName = GeneralHtml.getParamStr(params, FormalitySubsystem.userFirstName, "");
        String lName = GeneralHtml.getParamStr(params, FormalitySubsystem.userLastName, "");
        String access = GeneralHtml.getParamStr(params, FormalitySubsystem.userAccessLevel, "");
        String inst = GeneralHtml.getParamStr(params, FormalitySubsystem.userInstitution, "");
        String role = GeneralHtml.getParamStr(params, FormalitySubsystem.userRole, "");
        if(login.equals(""))
            msg="Must enter a login.";
        else if(pwd.equals(""))
            msg+="Must enter a password.";
        else if(access.equals(""))
            msg+="Must enter an access level.";
        else{
            //check unique login
           success=dbAccessor_.checkUniqueLogin(login, conn);
        }
        if(success){
            try{
            //enter the info- get id
              uID=dbAccessor_.saveNewUser(login, pwd, fName, lName,
                           Integer.parseInt(access), inst, role, conn);
            }
            catch(Exception e){
                msg+="Error adding user to the database. ";
                msg+=e.getMessage();
            }
        }
        else{
            msg+=" This login is already in use. Please enter another login. Tip: use your email address.";
        }
        if(uID>0) {
           info.getUserInfo().setUserID(Integer.toString(uID));
           info.getUserInfo().setUserFname(fName);
           info.getUserInfo().setAccessLevel(Integer.parseInt(access));
           info.getUserInfo().setLogin(login);
           info.getUserInfo().setUserPwd(pwd);
           msg=" New user "+fName+" was entered successfully.";
        }
    // adminPage_.getNewUserPage(lName, inst, role, info, msg, page);
 //adminPage_.writeSelectQuestionPage(lName, inst, role, info, msg,);
     }
  }

}
