package formality.Util;

import formality.parser.StringEncoder;
import formality.html.GeneralHtml;
import formality.controller.FormalitySubsystem;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Nov 6, 2008
 * Time: 10:42:56 AM
 * A class to parse out the parameters passed to a servlet during initialization.
 * The values are store as key, value pairs in this object.
 *
 * @author <b>University of Massachusetts</b>, Copyright &#169; 1999
 */
public class InitParams {

     String debugParam_ = "debug";
     String driverParam_ = "db.driver";
     String dbUserParam_ = "db.user";
     String dbPwdParam_ =  "db.pwd";
     String dbNameParam_ = "db.name";
     String dbPrefixParam_ = "db.prefix";
     String dbHostParam_ = "db.hostname";
     String sql17Param_ = "db.sql7";
     String nowarningsParam_ = "db.nowarnings";
     String dbDebugParam_ = "db.debug";
     String errorLogFile_ = "error.logfile";
     String techEmail_ = "error.adminemail";
//    private static String adminEmail_ = null;
     String techMailServer_ = "error.smtpserver";

     TreeMap<String, String> params_;

    public InitParams(){
        params_ = new TreeMap<String, String> ();
    }

    public String getErrorParams(Map params){
        StringBuffer errorParamsStr = new StringBuffer();
        try{
         errorParamsStr.append("fxn ").append(GeneralHtml.getParamStr(params, FormalitySubsystem.fxnParam, "")).append('\n');
         errorParamsStr.append("mode ").append(GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "")).append('\n');
         errorParamsStr.append("uid ").append(GeneralHtml.getParamStr(params, FormalitySubsystem.userIDParam, null)).append('\n');
         errorParamsStr.append("modID ").append(GeneralHtml.getParamStr(params, GeneralHtml.moduleID_, "")).append('\n');
         errorParamsStr.append("qID ").append(GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "")).append('\n');
        }catch (Exception ex) {
            errorParamsStr.append(ex.getMessage());
        }
        return errorParamsStr.toString();
    }

    /*
    Stores init values that are defined for the servlet (ServletConfig)
    This shadows values that may have been set at the webapp level.
    NOTA BENE:  If the web app bound multiple values to a particular key and
    the servlet binds a value to this key, all the values bound by the web app are lost!
    */
    public void storeParameters(ServletConfig config)
    {
        Enumeration e = config.getInitParameterNames();
        while(e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            if (params_.get(key) != null) // remove webapp binding
                params_.remove(key);
            params_.put(key, config.getInitParameter(key));
        }
    }

    /** Takes the parameters defined for the webapp (ServletContext).  These become part of every
     *  servlet context.
     */
    public void storeParameters(ServletContext ctxt) {
        Enumeration e = ctxt.getInitParameterNames();
        while(e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            params_.put(key, ctxt.getInitParameter(key));
        }
    }

    public String getDbDriver() throws Exception {
        String val = params_.get(driverParam_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+driverParam_);
        return val;
    }

    public String getDbLogin() throws Exception {
        String val = params_.get(dbUserParam_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+dbUserParam_);
        return val;
    }

    public String getDbPwd() throws Exception {
        String val = params_.get(dbPwdParam_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+dbPwdParam_);
        return val;
    }

   public String getDbName() throws Exception {
        String val = params_.get(dbNameParam_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+dbNameParam_);
        return val;
    }

   public String getDbPrefix() throws Exception {
        String val = params_.get(dbPrefixParam_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+dbPrefixParam_);
        return val;
    }

    public String getDbHost() throws Exception {
        String val = params_.get(dbHostParam_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+dbHostParam_);
        return val;
    }

   public String getSQL17() throws Exception {
        String val = params_.get(sql17Param_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+sql17Param_);
        return val;
    }

     public String getNowarnings() throws Exception {
        String val = params_.get(nowarningsParam_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+nowarningsParam_);
        return val;
    }

     public boolean isDbDebug(){
        boolean res = false;
        String val = params_.get(dbDebugParam_);
        if(val==null)
          res=false;
        if(val.equals("true"))
           res = true;
        return res;
    }

    public boolean isDebug(){
        boolean res = false;
        String val = params_.get(debugParam_);
        if(val==null)
          res=false;
        if(val.equals("true"))
           res = true;
        return res;
    }

   public String getTechEmail() throws Exception {
        String val = params_.get(techEmail_);
        if(val==null)
          throw new Exception("Error: no init parameter value for "+techEmail_);
        return val;
    }

    public String getTechEmailServer() throws Exception {
         String val = params_.get(techMailServer_);
         if(val==null)
           throw new Exception("Error: no init parameter value for "+techMailServer_);
         return val;
     }

    public String getErrorLogFile() throws Exception {
         String val = params_.get(errorLogFile_);
         if(val==null)
           throw new Exception("Error: no init parameter value for "+errorLogFile_);
         return val;
     }


  /*   microsoft
     String driverNameMicrosoft_ = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    url = "jdbc:sqlserver://tomato.cs.umass.edu:1439;password_=";
                    url += password_;
                    url += ";database=" + databaseName + ";user=owl_login"
  */
    // "jdbc:inetdae : tomato.cs.umass.edu:1438 ? sql7= true & nowarnings= false & password= & database=pvma_develop & user=pvma_login");
    public String getDbUrlString()throws Exception {
       // String dbp = StringEncoder.encode(getDbPwd());
       StringBuffer dbUrlstr = new StringBuffer();
       //"jdbc:inetdae"
       String dbPrefix = getDbPrefix();
       //tomato.cs.umass.edu:1438
       String dbHost = getDbHost();
       //"true"
       String sql17 =  getSQL17();
       //"false"
       String nowarnings = getNowarnings();
        //"w{:Tgd4V8je6"
       String dbpwd = StringEncoder.decode(getDbPwd());
       //"4mality"
       String dbName = getDbName();
        //"4mality_login"
       String dbUser = getDbLogin();
//jdbc:inetdae:128.119.243.3:1439?sql7=true&nowarnings=false&password=&user=owl_login&database=OwlTestDevelopSteve

       dbUrlstr.append(dbPrefix).append(":").append(dbHost).append("?");
       dbUrlstr.append("sq17=").append(sql17).append("&");
       dbUrlstr.append("nowarnings=").append(nowarnings).append("&");
       dbUrlstr.append("password=").append(dbpwd).append("&");
       dbUrlstr.append("user=").append(dbUser).append("&");
       dbUrlstr.append("database=").append(dbName);

       return dbUrlstr.toString();
    }

    // "jdbc:inetdae : tomato.cs.umass.edu:1438 ? sql7= true & nowarnings= false & password= & database=pvma_develop & user=pvma_login");
    public String obfuscatePwd(String urlStr)throws Exception {
       int index = urlStr.indexOf("password")+9;
       String rest = urlStr.substring(index, urlStr.length());
       String first = urlStr.substring(0, index);
       int end = rest.indexOf('&');
       String last = rest.substring(end, rest.length());
       return first+"xxxxxxxxxxxx"+last;
    }

}
