package formality.controller;

import formality.content.MultipleChoiceQuestion;
import formality.content.Question;
import formality.content.ShortAnswerQuestion;
import formality.content.SystemInfo;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DbQuestion;
import formality.content.database.mysql.DbUser;
import formality.content.database.mysql.DbCourse;
import formality.html.auth.AuthorHtml;
import formality.html.GeneralHtml;
import formality.html.GuestUserCreation;
import formality.html.GuestWelcome;
import formality.model.StudentInfo;
import formality.model.UserInfo;
import formality.parser.HighlightParser;
import formality.records.EventLogger;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

/**
 * <p>Title: </p>
 */
public class FormalitySubsystem {

    private static Logger logger = Logger.getLogger(FormalitySubsystem.class);
    //params
    public static final String fxnParam = "fxn";
    public static final String modeParam = "mode";
    //fxn
    public static final String authorFxnParam = "aut";
    public static final String teacherFxnParam = "tea";
    public static final String adminFxnParam = "adm";
    public static final String studentFxnParam = "stu";
    public static final String loginFxnParam = "log";
    public static final String wayangFxnParam = "wayang";
    public static final String createGuestLoginParam = "createGuestLogin";

    //modes
    public static final String loginMode = "login";
    public static final String demoMode = "demo";
    //user params
    public static final String userIDParam = "un";
    public static final String userLogin = "ul";
    public static final String guestLogin = "guestLogin";
    public static final String userPassword = "up";
    public static final String userEmail = "email";
    public static final String userFirstName = "ufn";
    public static final String userLastName = "uln";
    public static final String userAccessLevel = "ual";
    public static final String userInstitution = "ui";
    public static final String userRole = "ur";
    public static final String logoutParam = "logout";
    public static final String courseIDParam = "courseID";

    //Access Levels:
    public static final int studentAccess = 1;
    public static final int teacherAccess = 2;
    public static final int authorAccess = 3;
    public static final int adminAccess = 4;
    //public static final int localAuthorAccess =5;

    public static final String defaultHintLevel = "1";
    // entered for the ResponseString column when a student starts a question
    public static final String questionStartString = "$S$";

    public static final String DEMO_USER_PREFIX = "demoUser";
    public static final String DEMO_USER_PW = "demoUserPw";

    AuthoringSubsystem as;
    AdminSubsystem ads;
    StudentSubsystem ss;
    TeacherSubsystem ts;
    GeneralHtml gh;
    DBAccessor dbAccessor_;

    public FormalitySubsystem() {
    }

    public boolean handleRequest(Map params, SystemInfo info, Connection conn, StringBuffer page, HttpServletRequest req,
                                 HttpServletResponse resp) throws Exception {
        dbAccessor_ = new DBAccessor();
        dbAccessor_.loadSystemInfo(info, conn);
        String fxn = GeneralHtml.getParamStr(params, fxnParam, "");
        String mode = GeneralHtml.getParamStr(params, modeParam, "");
        String uID = GeneralHtml.getParamStr(params, userIDParam, null);
        logger.debug("fxn= " + fxn);
        if (fxn.equals("test")) {
            page.append("Hi there Gordy!");
        }
        else if (fxn.equals(authorFxnParam)) {
            HttpSession sess = req.getSession(false); // get a session if one exists
            // If the servlet session is null because of idleness, then force a relogin
            if (sess == null) {
                String msg = "You have been automatically logged out because your session was idle.   Please re-login";
                gh = new GeneralHtml();
                gh.getLoginPage(info.getUserLogin(), info.getUserPwd(), info, msg, page);
                return false;
            }
            if (validateAuthor(info, uID, conn)) {
                as = new AuthoringSubsystem();

                return as.handleRequest(params, info, conn, page, req, resp);
            } else {//not valid
                fxn = loginFxnParam;
            }
        } else if (fxn.equals(adminFxnParam)) {
            //  if(validateAuthor(params, info, conn)){
            HttpSession sess = req.getSession(false); // get a session if one exists
            // If the servlet session because of idleness, then force a relogin
            if (sess == null) {
                String msg = "You have been automatically logged out because your session was idle.   Please re-login";
                gh = new GeneralHtml();
                gh.getLoginPage(info.getUserLogin(), info.getUserPwd(), info, msg, page);
                return false;
            }
            ads = new AdminSubsystem();
            ads.handleRequest(params, info, conn, page);
            //}
            // else//not valid
            //   fxn=loginFxnParam;
        } else if (fxn.equals(studentFxnParam)) {
            if (validateStudent(info, uID, conn)) {
                ss = new StudentSubsystem();
                return ss.handleRequest(params, info, conn, page, resp);
            } else {//not valid
                fxn = loginFxnParam;
            }
        } else if (fxn.equals(teacherFxnParam)) {
            if (validateTeacher(info, uID, conn)) {
                ts = new TeacherSubsystem();
                ts.handleRequest(params, info, conn, page);
            } else {//not valid
                fxn = loginFxnParam;
            }
        } else if (fxn.equals(loginFxnParam))
            processLogin(params, info, conn, page, req, resp, mode, uID);
        else if (fxn.equals(createGuestLoginParam))
            processGuestLogin(params,info, mode, conn,page,req,resp);
        else if (fxn.equals(wayangFxnParam))  {
            logger.debug("processing wayang request");
            uID =  Integer.toString(DbUser.getWayangUser(conn));
            logger.debug("for wayang user " + uID);
            // uID is a user created for wayang.   NOthing special about it other than it needs to exist so that
            // formality has what it needs to process the request.
            if (validateStudent(info, uID, conn))  {
                logger.debug("user is valid ");
                return new WayangSubsystem().handleRequest(params,info,conn,page, req, resp);
            }
        }
        // This provides a service to Wayang which needs a simple snapshot of what a problem looks like
        else if (fxn.equals("questionSnapshot")) {
            String qid = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");
            Question q = DbQuestion.loadQuestion(qid,conn);
            HighlightParser hp = new HighlightParser();
            if (q.isMultipleChoice()) {
                StringBuffer sb = new StringBuffer();
                MultipleChoiceQuestion mcq = DbQuestion.loadMultipleChoiceQuestion(qid,conn);
                hp.parseStem(mcq.getStem(),sb,info);
                req.setAttribute("questionStem",sb.toString());
                insertAnswerChoice(info, req, hp, mcq.getAnswerChoiceA(),"ansA");
                insertAnswerChoice(info, req, hp, mcq.getAnswerChoiceB(),"ansB");
                insertAnswerChoice(info, req, hp, mcq.getAnswerChoiceC(),"ansC");
                insertAnswerChoice(info, req, hp, mcq.getAnswerChoiceD(),"ansD");
                RequestDispatcher disp = req.getRequestDispatcher("/jsp/mcproblem.jsp");
                disp.forward(req, resp);
                return true;
            }
            else if (q.isShortAnswer()) {
                ShortAnswerQuestion saq = DbQuestion.loadShortAnswerQuestion(qid,conn);
                StringBuffer sb = new StringBuffer();
                hp.parseStem(saq.getStem(),sb,info);
                req.setAttribute("questionStem",sb.toString());
                RequestDispatcher disp = req.getRequestDispatcher("/jsp/saproblem.jsp");
                disp.forward(req, resp);
                return true;
            }
        }

        return false;
    }

    private void insertAnswerChoice(SystemInfo info, HttpServletRequest req, HighlightParser hp, String ans, String label) throws Exception {
        StringBuffer sb;
        sb = new StringBuffer();
        hp.parseText(ans, sb, info);
        req.setAttribute(label,sb.toString());
    }


    private void processGuestLogin(Map params, SystemInfo info, String mode, Connection conn, StringBuffer page, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // this is an event coming from the welcome page which means we are done logging the user in.
        if (mode.equals("Continue")) {
            String user = GeneralHtml.getParamStr(params, FormalitySubsystem.userLogin,"");
            String userId = GeneralHtml.getParamStr(params, FormalitySubsystem.userIDParam,"-1");
            if (validateStudent(info,userId,conn))
                doLogin(info,conn,page,req,resp,userId);

        }
        else {
            // this is a request to create guest user login.   If it already exists, return an error message;  otherwise show a welcome screen with the new user info.
            String user = GeneralHtml.getParamStr(params, FormalitySubsystem.userLogin,"");
            String password = GeneralHtml.getParamStr(params, FormalitySubsystem.userPassword,"");
            String email = GeneralHtml.getParamStr(params, FormalitySubsystem.userEmail,"");
            int id = DbUser.getUsername(conn,user);
            if (id != -1)
                page.append(new GuestUserCreation("The user " + user + " is already taken.  Try another.").getPage());
            else {
                id = DbUser.createGuestUser(conn,user,password,email);
                DbUser.insertStudentFrameworkDataToDb(conn,id);
                DbCourse.addUserToCourse(conn,id,DbCourse.getGuestCourse(conn));
                page.append(new GuestWelcome(user, id).getPage());
            }
        }
    }

    private void processLogin(Map params, SystemInfo info, Connection conn, StringBuffer page, HttpServletRequest req, HttpServletResponse resp,
                              String mode, String userId) throws Exception {
        String msg = "";
        String guestLogin = GeneralHtml.getParamStr(params, FormalitySubsystem.guestLogin,"");
        if (mode.equals(loginMode)) {
            String logout = GeneralHtml.getParamStr(params, logoutParam, "");
            if (!logout.equals("") && info.getUserInfo() != null) {
                long sessTime = System.currentTimeMillis()-((StudentInfo) info.getUserInfo()).getSessionTS();
                EventLogger.logout(conn,userId, sessTime);
                msg = "You have been logged out.";
            }
            else if (!logout.equals("") && info.getUserInfo() == null)
                msg="You have been logged out.";
            gh = new GeneralHtml();
            gh.getLoginPage(info.getUserLogin(), info.getUserPwd(), info, msg, page);

        }
        // see if the guestlogin button was clicked
        else if (guestLogin.equals("Guest Login")) {
            page.append(new GuestUserCreation().getPage());
        }


        else {
            String login = GeneralHtml.getParamStr(params, FormalitySubsystem.userLogin, "");
            String pwd = GeneralHtml.getParamStr(params, FormalitySubsystem.userPassword, "");
             if (mode.equals(demoMode)) {
                // generate a user/pw in the special demo class
                // TODO could pass the courseID on arg string so we could have more than one demo course AND avoid looking up a fixed courseID for demos)
                UserInfo userInfo = genDemoUser(DEMO_USER_PREFIX, DEMO_USER_PW,conn);
                // insert a entry in the demo user sessions table (so a cleanup demon can blow away the data from this job)
                int sessId = DbUser.recordDemoSession(userInfo.getUserID(),System.currentTimeMillis(),conn);
                info.setUserInfo(userInfo);
                // do a normal login with the user/pw
                doLogin(info,conn,page,req,resp,userId);
            }
            else if (login == null || login.equals("")) {
                msg = "Enter a login!";
                gh = new GeneralHtml();
                gh.getLoginPage(login, pwd, info, msg, page);
            }
            else if (pwd == null || pwd.equals("")) {
                msg = "Enter a password!";
                gh = new GeneralHtml();
                gh.getLoginPage(login, pwd, info, msg, page);
            }
            else {
                if (!dbAccessor_.loginUser(login, pwd, info, conn)) {
                    msg = "Could not log you in. Please try again or ask for your login info.";
                    gh = new GeneralHtml();
                    gh.getLoginPage(login, pwd, info, msg, page);
                }
                else { //the person is now logged in

                    doLogin(info, conn, page, req, resp, userId);
                }
            }
        }
    }

    private void doLogin(SystemInfo info, Connection conn, StringBuffer page, HttpServletRequest req, HttpServletResponse resp, String userId) throws Exception {
        int access = info.getUserInfo().getAccessLevel();
        if (access == studentAccess) {
            Map newP = new TreeMap();
            newP.put(FormalitySubsystem.modeParam, StudentSubsystem.studentHomeMode_);
            StudentSubsystem ss = new StudentSubsystem();
            //for students- set the session TS
            long sessionTS = System.currentTimeMillis();
            if (userId == null || userId.equals(""))
                userId = info.getUserID();
            StudentInfo si = (StudentInfo) info.getUserInfo();
            si.setSessionTS(sessionTS);
            dbAccessor_.updateStudentSessionTS(info.getUserID(), sessionTS, conn);
            // userId will be null for demo sessions
            if (userId != null)
                EventLogger.login(conn, userId);
            dbAccessor_.setUserProblemStartTime(info.getUserID(), null, conn);
            ss.handleRequest(newP, info, conn, page, resp);
        } else if (access == teacherAccess) {
            TeacherSubsystem ts = new TeacherSubsystem();
            Map newP = new TreeMap();
            newP.put(FormalitySubsystem.modeParam, TeacherSubsystem.teacherHomeMode_);
            newP.put("msg", "Welcome " + info.getUserInfo().getUserFnameOrLogin());
            ts.handleRequest(newP, info, conn, page);
        } else if (access == authorAccess) {
            HttpSession sess = req.getSession(); // an HTTP session is created for an author on login
            sess.setMaxInactiveInterval(60 * 60); // session will expire if left idle for 1 hr
            AuthorHtml apage = new AuthorHtml();
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            apage.getAuthorHomePage("Welcome " + info.getUserInfo().getUserFnameOrLogin(), info, page);
        } else if (access == adminAccess) {
            // AdminHtml th=new AdminHtml();
            //th.writeAdminHomePage(info);
            HttpSession sess = req.getSession(); // an HTTP session is created for an admin on login
            sess.setMaxInactiveInterval(60 * 60); // session will expire if left idle for 1 hr
            AuthorHtml apage = new AuthorHtml();
            TreeMap cts = DbQuestion.getQuestionsInCategoriesCounts(info.getCategoryIDs(), conn);
            info.setCatCounts(cts);
            apage.getAuthorHomePage("Welcome " + info.getUserInfo().getUserFnameOrLogin(), info, page);
        }
    }


    private UserInfo genDemoUser(String baseUserName, String pw, Connection conn) throws SQLException {
        return DbUser.genDemoUser(conn,baseUserName,pw);
    }

    public boolean validateAuthor(SystemInfo info, String uID, Connection conn) throws Exception {
        boolean ok = false;
        if (validateUser(info, uID, conn)) {
            if (info.getUserInfo().getAccessLevel() >= teacherAccess)
                ok = true;
        }
        return ok;
    }

    private boolean validateStudent(SystemInfo info, String uID, Connection conn) throws Exception {
        //  throw new Exception("testing error handling");

        boolean ok = false;
        if (validateUser(info, uID, conn)) {
            if (info.getUserInfo().getAccessLevel() >= studentAccess)
                ok = true;
        }
        return ok;

    }

    private boolean validateTeacher(SystemInfo info, String uID,
                                    Connection conn) throws Exception {
        boolean ok = false;
        if (validateUser(info, uID, conn)) {
            if (info.getUserInfo().getAccessLevel() >= teacherAccess)
                ok = true;
        }
        return ok;
    }

    private boolean validateUser(SystemInfo info, String uID, Connection conn) throws Exception {
        boolean ok = false;
        DBAccessor dba = new DBAccessor();
        try {
            ok = dba.validateUser(info, uID, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ok;
    }

    public static boolean readAloud(Map params, SystemInfo info, Connection conn, HttpServletResponse resp) throws Exception {
        String lang = GeneralHtml.getParamStr(params, GeneralHtml.language_, "eng");
        String qid = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");

//        String fn = dbAccessor_.getQuestionAudioFile(conn,qid, lang);
//        byte[] audio = dbAccessor_.getQuestionAudio(conn, Integer.parseInt(qid), lang);
        String fn = new DBAccessor().getQuestionAudioFile(conn,Integer.parseInt(qid), lang);
        StringBuilder out = new StringBuilder();
//        out.append(audio);
        resp.setContentType("audio/mpeg");
//            response.setContentType("application/octet-stream");
        ServletOutputStream st = resp.getOutputStream();
        // create a buffer of 500K to prevent flushing early ??
        BufferedOutputStream bos = new BufferedOutputStream(st, 1024 * 500);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(info.getSoundPath()+"\\"+fn));
        int bufsize=1024;
        byte[] buf= new byte[bufsize];
        while (bis.read(buf,0,bufsize) != -1)
            bos.write(buf);
        bis.close();
        bos.close();

        return true;
    }

}
