package formality.content.database.mysql;

import formality.content.*;
import formality.content.database.BaseDBAccessor;

import java.util.Vector;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.*;

import formality.systemerror.AuthoringException;
import formality.model.StudentInfo;
import formality.model.TeacherInfo;
import formality.model.UserInfo;
import formality.Util.DataTuple;
import formality.Util.ReportDataUtils;
import formality.controller.FormalitySubsystem;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 14, 2010
 * Time: 10:07:37 PM
 * <p/>
 * Access Levels:
 * 1- Student
 * 2- Teacher
 * 3- Author
 * 4- Admin
 */

public class DBAccessor extends BaseDBAccessor {
    private static Logger logger = Logger.getLogger(DBAccessor.class);

  public static final int duplicateRowError = 2627;
  public static final int keyConstraintViolation = 1062;

    public DBAccessor() {
    }

    //return dt: qID, score, ansSeq, hints
    // may return an empty array
    public ArrayList<DataTuple> loadModuleScores(String UID, String MID,
                                                 Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        int mID = Integer.parseInt(MID);
        ArrayList<DataTuple> scores = new ArrayList<DataTuple>();
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM ModuleScore ");
        query.append("WHERE userID= '").append(uID).append("' ");
        query.append("AND modID='").append(mID).append("'; ");
        ResultSet res = stmt.executeQuery(query.toString());
        while (res.next()) {
            DataTuple dt = new DataTuple();
            dt.setFirst(res.getString("qID"));
            dt.setSecond(res.getString("score"));
            String a = res.getString("ansSeq");
            if (a != null && a.equals("NULL"))
                a = null;
            dt.setThird(a);
            dt.setFourth(res.getString("hints"));
            scores.add(dt);
        }
        res.close();
        stmt.close();
        return scores;
    }

    public void updateModuleScoreHintFlag(String UID, String MID, String QID,
                                          int hint, Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        int mID = Integer.parseInt(MID);
        int qID = Integer.parseInt(QID);
        StringBuffer query = new StringBuffer();
        query.append("UPDATE ModuleScore SET hints='").append(hint).append("' ");
        query.append("WHERE userID= '").append(uID).append("' ");
        query.append("AND modID='").append(mID).append("' ");
        query.append("AND qID='").append(qID).append("';");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void updateModuleScoreAnsSeq(String UID, String MID, String QID,
                                        String ansSeq, String scoreStr, Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        int mID = Integer.parseInt(MID);
        int qID = Integer.parseInt(QID);
        int score = Integer.parseInt(scoreStr);
        StringBuffer query = new StringBuffer();
        query.append("UPDATE ModuleScore SET ansSeq='").append(safeStringCheck(ansSeq)).append("', ");
        query.append("score='").append(score).append("' ");
        query.append("WHERE userID= '").append(uID).append("' ");
        query.append("AND modID='").append(mID).append("' ");
        query.append("AND qID='").append(qID).append("'");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void resetModuleScore(String UID, String MID, String QID,
                                 Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        int mID = Integer.parseInt(MID);
        int qID = Integer.parseInt(QID);
        StringBuffer query = new StringBuffer();
        query.append("UPDATE ModuleScore SET ");
        query.append("score='0', ansSeq='NULL', hints='0' ");
        query.append("WHERE userID= '").append(uID).append("' ");
        query.append("AND modID='").append(mID).append("' ");
        query.append("AND qID='").append(qID).append("'");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void initModuleScore(String UID, String MID, String QID,
                                Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        int mID = Integer.parseInt(MID);
        int qID = Integer.parseInt(QID);
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO ModuleScore ");
        query.append("(userID, modID, qID, score, ansSeq, hints) ");
        query.append(" VALUES ( ");
        query.append("'").append(uID).append("', ");
        query.append("'").append(mID).append("', ");
        query.append("'").append(qID).append("', ");
        query.append("'0', ");
        query.append("'NULL', ");
        query.append("'0')");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();

    }

    public void loadSystemInfo(SystemInfo info, Connection conn) throws Exception {
        loadTags(conn, info);
        loadCategories(conn, info);
        loadCoaches(conn, info);
        loadStandards(conn, info);
        loadStrategies(conn, info);
    }

    //set the  correct user info object
    public boolean loginUser(String login, String pwd,
                             SystemInfo info, Connection conn) throws Exception {
        boolean ok = false;
        //the allowed length in the DB table
        if (login.length() > 50 || pwd.length() > 50)
            return ok;
        UserInfo userInfo = null;
        String userID = "";
        String userFname = "";
        String userLname = "";
        int accessLevel = 0;
        String institution = "";
        String gender = "";
        String state = "";
        int age = 0;
        boolean nativeEnglish = true;
        boolean prevExp = false;
        long sessionTS = 0;
        String dname = "";
        String modrec = "";
        int condition = 0;
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT U.UserID, U.FirstName, U.LastName, U.AccessLevel, U.Institution, ");
        query.append("U.Gender, U.Age, U.nativeLang, U.PrevExperience, U.SessionTS, U.State, ");
        query.append("U.ModRecord, D.Name as DName, U.cond ");
        query.append("FROM UserTable U, District D WHERE D.DistrictID=U.DistrictID ");
        query.append("AND U.Login='").append(safeStringCheck(login)).append("'");
        query.append(" AND U.Password='").append(safeStringCheck(pwd)).append("'");
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            ok = true;
            userID = res.getString("UserID");
            userFname = res.getString("FirstName");
            userLname = res.getString("LastName");
            accessLevel = res.getInt("AccessLevel");
            institution = res.getString("Institution");
            gender = res.getString("Gender");
            age = res.getInt("Age");
            String natLang = res.getString("nativeLang");
            if (!res.wasNull())
                nativeEnglish = natLang.equalsIgnoreCase("English");
            prevExp = res.getBoolean("PrevExperience");
            Timestamp sessTS = res.getTimestamp("SessionTS");
            if (res.wasNull())
                sessionTS = 0;
            else sessionTS = sessTS.getTime();
            state = res.getString("State");
            modrec = res.getString("ModRecord");
            dname = res.getString("DName");
            condition = res.getInt("cond");
        }
        res.close();
        stmt.close();
        if (accessLevel == UserInfo.STUDENT) {
            userInfo = new StudentInfo();
            StudentInfo si = (StudentInfo) userInfo;
            si.setSessionTS(sessionTS);
        } else if (accessLevel == UserInfo.TEACHER) {
            userInfo = new TeacherInfo();
        } else {
            userInfo = new UserInfo();
        }
        userInfo.setUserID(userID);
        userInfo.setUserFname(userFname);
        userInfo.setUserLname(userLname);
        userInfo.setAccessLevel(accessLevel);
        userInfo.setInstitution(institution);
        userInfo.setGender(gender);
        userInfo.setAge(age);
        userInfo.setNativeEnglish(nativeEnglish);
        userInfo.setPrevExp(prevExp);
        userInfo.setState(state);
        userInfo.setModrec(modrec);
        userInfo.setDistrict(dname);
        userInfo.setCondition(condition);
        info.setUserInfo(userInfo);
        return ok;
    }

    //must have: login, password, access level
    //return -1 if not successful, else return the new user ID
    public int saveNewUser(String login, String pwd, String fName, String lName,
                           int level, String inst, String r, Connection conn) throws Exception {
        int uID = -1;
        if (login.length() > 50 || pwd.length() > 50 || fName.length() > 50 || lName.length() > 50
                || inst.length() > 50)
            return uID;
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO UserTable (");
        query.append("Login, Password, FirstName, LastName, AccessLevel, ");
        query.append("Institution, Role)");
        query.append(" VALUES ( ");
        query.append("'").append(safeStringCheck(login)).append("', ");
        query.append("'").append(safeStringCheck(pwd)).append("', ");
        query.append("'").append(safeStringCheck(fName)).append("', ");
        query.append("'").append(safeStringCheck(lName)).append("', ");
        query.append(level).append(", ");
        query.append("'").append(safeStringCheck(inst)).append("', ");
        query.append("'").append(safeStringCheck(r)).append("')");
        String qs = query.toString();
        Statement stmt = conn.createStatement();
        ResultSet res = null;
        stmt.execute(qs);
        res = stmt.getGeneratedKeys();
        if (res.next()) {
            uID = res.getInt("NewID");
        }
        res.close();
        stmt.close();
        return uID;
    }

    public int updateStudentSessionTS(String studId, long time, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String q = "update UserTable set SessionTS=? where userID=?";
            stmt = conn.prepareStatement(q);
            stmt.setTimestamp(1, new Timestamp(time));
            stmt.setInt(2, Integer.parseInt(studId));
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    //return false if an ID is found for this login
    public boolean checkUniqueLogin(String login, Connection conn) throws Exception {
        boolean ok = false;
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append(" SELECT ").append("UserID");
        query.append(" FROM ").append("UserTable");
        query.append(" WHERE ").append("Login");
        query.append("='").append(login).append("'");
        ResultSet results = null;
        // the identity value
        String qs = query.toString();
        results = stmt.executeQuery(qs);
        if (results.next())
            ok = false;
        else {
            ok = true;
        }
        results.close();
        stmt.close();
        return ok;
    }

    public boolean validateUser(SystemInfo info, String UID, Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        boolean ok = false;
        UserInfo userInfo = null;
        String login = "";
        String pwd = "";
        String userFname = "";
        String userLname = "";
        int accessLevel = 0;
        String institution = "";
        String gender = "";
        int age = 0;
        boolean nativeEnglish = true;
        boolean prevExp = false;
        long sessionTS = 0;
        String state = "";
        String dname = "";
        int condition = 0;
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT U.Login, U.Password, U.FirstName, U.LastName, U.AccessLevel, U.Institution, ");
        query.append("U.Gender, U.Age, U.nativeLang, U.PrevExperience, U.SessionTS, U.State, D.Name as DName, U.cond ");
        query.append("FROM UserTable U, District D WHERE D.DistrictID=U.DistrictID AND U.UserID='").append(uID).append("'");
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            login = res.getString("Login");
            pwd = res.getString("Password");
            userFname = res.getString("FirstName");
            userLname = res.getString("LastName");
            accessLevel = res.getInt("AccessLevel");
            institution = res.getString("Institution");
            gender = res.getString("Gender");
            age = res.getInt("Age");
            String natLang = res.getString("nativeLang");
            if (!res.wasNull())
                nativeEnglish = natLang.equalsIgnoreCase("English");
            prevExp = res.getBoolean("PrevExperience");
            Timestamp sessTS = res.getTimestamp("SessionTS");
            if (!res.wasNull())
                sessionTS = sessTS.getTime();
            state = res.getString("State");
            dname = res.getString("DName");
            condition = res.getInt("cond");
            ok = true;
        }
        res.close();
        stmt.close();
        if (accessLevel == 1) {
            userInfo = new StudentInfo();
            StudentInfo si = (StudentInfo) userInfo;
            si.setSessionTS(sessionTS);
        } else if (accessLevel == 2) {
            userInfo = new TeacherInfo();
        } else {
            userInfo = new UserInfo();
        }
        userInfo.setUserID(Integer.toString(uID));
        userInfo.setLogin(login);
        userInfo.setUserPwd(pwd);
        userInfo.setUserFname(userFname);
        userInfo.setUserLname(userLname);
        userInfo.setAccessLevel(accessLevel);
        userInfo.setInstitution(institution);
        userInfo.setGender(gender);
        userInfo.setAge(age);
        userInfo.setNativeEnglish(nativeEnglish);
        userInfo.setPrevExp(prevExp);
        userInfo.setState(state);
        userInfo.setDistrict(dname);
        userInfo.setCondition(condition);
        info.setUserInfo(userInfo);
        return ok;
    }

    public void loadStudentInfo(String SID, StudentInfo info, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT UserID, Login, Password, FirstName, LastName, AccessLevel, Institution, ");
        query.append("Gender, Age, nativeLang, PrevExperience ");
        query.append("FROM UserTable WHERE UserID='").append(sID).append("'");
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            info.setUserID(res.getString("UserID"));
            info.setLogin(res.getString("Login"));
            info.setUserPwd(res.getString("Password"));
            info.setUserFname(res.getString("FirstName"));
            info.setUserLname(res.getString("LastName"));
            info.setAccessLevel(res.getInt("AccessLevel"));
            info.setInstitution(res.getString("Institution"));
            info.setGender(res.getString("Gender"));
            info.setAge(res.getInt("Age"));
            String natLang=res.getString("nativeLang");
            if (!res.wasNull())
                info.setNativeEnglish(natLang.equalsIgnoreCase("English"));
            info.setPrevExp(res.getBoolean("PrevExperience"));
        }
        res.close();
        stmt.close();
    }

    public String getStudentLogin(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        String name = null;
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT Login ");
        query.append("FROM UserTable WHERE UserID='").append(sID).append("'");
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            name = res.getString("Login");
        }
        res.close();
        stmt.close();
        return name;
    }

    //Use the name if possible, else use login
    public String getStudentNameOrLogin(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        String sName = "";
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT Login, FirstName, LastName  ");
        query.append("FROM UserTable WHERE UserID='").append(sID).append("'");
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            String login = res.getString("Login");
            String fName = res.getString("FirstName");
            String lName = res.getString("LastName");
            if ((fName != null && fName.length() > 0 && !fName.equals(" "))
                    || (lName != null && lName.length() > 0 && !lName.equals(" "))) {
                sName = fName + " " + lName;
            } else
                sName = login;
        }
        res.close();
        stmt.close();
        return sName;
    }

    public void loadCategories(Connection conn, SystemInfo info) throws Exception {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM Category";
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            info.addCategory(res.getString("CategoryID"), res.getString("Name"));
        }
        res.close();
        stmt.close();
    }

    public void loadStandards(Connection conn, SystemInfo info) throws Exception {
        Statement stmt = conn.createStatement();
        String query = "SELECT Title FROM FrameworkStandards";
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            info.addStandard(res.getString("Title"));
        }
        res.close();
        stmt.close();
    }

//     public void updateStudentSessionTS(String SID, long ts,
//                                   Connection conn)throws Exception{
//         int sID = Integer.parseInt(SID);
//           StringBuffer query=new StringBuffer();
//           query.append("UPDATE UserTable SET");
//           query.append("SessionTS='").append(ts).append("'");
//           query.append(" WHERE UserID= '").append(sID).append("'");
//           Statement stmt=conn.createStatement();
//           String qs=query.toString();
//           try{
//              stmt.executeUpdate(qs);}
//              catch(Exception sex) {
//                String msg=sex.getMessage();
//                if(!msg.contains(sqlExceptionStr_))
//                   throw sex;
//               }
//          stmt.close();
//        }


    public long getStudentSessionTS(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        long ts = 0;
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append(" SELECT SessionTS FROM UserTable");
        query.append(" WHERE UserID='").append(sID).append("'");
        ResultSet results = null;
        String qs = query.toString();
        results = stmt.executeQuery(qs);
        if (results.next()) {
            ts = results.getTimestamp("SessionTS").getTime();
        }
        results.close();
        stmt.close();
        return ts;
    }

    public void loadCoaches(Connection conn, SystemInfo info) throws Exception {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM Coach";
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            info.addCoach(res.getString("CoachID"),
                    res.getString("Name"),
                    res.getString("Image"));
        }
        res.close();
        stmt.close();
    }

    public void loadStrategies(Connection conn, SystemInfo info) throws Exception {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM Strategy";
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            info.addStrategy(res.getString("StrategyID"),
                    res.getString("Name"));
        }
        res.close();
        stmt.close();
    }

    public void loadTags(Connection conn, SystemInfo info) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet res;
        String query = "SELECT * FROM " + tagColor;
        res = stmt.executeQuery(query);
        boolean ok = false;
        while (res.next()) {
            info.setTagColor(res.getString("Tag"), res.getString("Color"));
        }
        res.close();
        stmt.close();
    }



    public void getUserCourseInfo(UserInfo info, Connection conn) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        int sID = Integer.parseInt(info.getUserID());
        query.append("SELECT C.CourseID, CourseName, Institution, Notes, Controller, ");
        query.append("Scoreboard FROM Course C, UserCourseLinks S ");
        query.append("WHERE C.CourseID=S.CourseID AND S.UserID='");
        query.append(sID).append("'");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            Course c = new Course();
            c.setCourseID(res.getString("CourseID"));
            c.setCourseName(res.getString("CourseName"));
            c.setInstitution(res.getString("Institution"));
            c.setCourseNotes(res.getString("Notes"));
            c.setCourseController(res.getString("Controller"));
            c.setUseScoreboard(res.getBoolean("Scoreboard"));
        }
        res.close();
        stmt.close();
    }

    public void loadStudentCourses(UserInfo info, Connection conn) throws Exception {
        ArrayList<Course> courses = new ArrayList<Course>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        int sID = Integer.parseInt(info.getUserID());
        query.append("SELECT C.CourseID, CourseName, Institution, Notes, Controller, District, Scoreboard FROM Course C, UserCourseLinks S ");
        query.append("WHERE C.CourseID=S.CourseID AND S.UserID='");
        query.append(info.getUserID()).append("'");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            Course c = new Course();
            c.setCourseID(res.getString("CourseID"));
            c.setCourseName(res.getString("CourseName"));
            c.setInstitution(res.getString("Institution"));
            c.setCourseNotes(res.getString("Notes"));
            c.setCourseController(res.getString("Controller"));
            c.setDistrict(res.getInt("District"));
            c.setUseScoreboard(res.getBoolean("Scoreboard"));
            courses.add(c);
        }
        res.close();
        stmt.close();
        info.setCourses(courses);
    }

    public static Course getCourse (String courseId, Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select c.courseName,c.institution from course c" +
                    " where c.courseid=?";
            stmt = conn.prepareStatement(q);
            int cID =Integer.parseInt(courseId);
            stmt.setInt(1,cID);
            rs = stmt.executeQuery();
            // we aren't joining the course and the usertable this allows us to fetch a course that
            // may not have a teacher associated with it yet.

            if (rs.next()) {
                String name = rs.getString("c.courseName");
                String inst = rs.getString("c.institution");
                Course c = new Course();
                c.setCourseID(courseId);
                c.setCourseName(name);
                c.setInstitution(inst);
                String qq = "select u.lastname from usertable u, usercourselinks l where " +
                        "l.courseId=? and u.userId=l.userId";
                PreparedStatement ps = conn.prepareStatement(qq);
                ps.setInt(1, cID);
                rs = ps.executeQuery();
                c.setTeacher("");
                if (rs.next()) {
                    c.setTeacher(rs.getString(1));
                }
                return c;
            }
            else return null;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public String getCourseControllerName(UserInfo info, Connection conn) throws Exception {
        int cID = Integer.parseInt(info.getCourseID());
        String cName = null;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT Controller FROM CourseController ");
        query.append("WHERE CourseID= '").append(cID).append("'");
        res = stmt.executeQuery(query.toString());
        if (res.next())
            cName = res.getString("Controller");
        res.close();
        stmt.close();
        return cName;
    }

    public Vector getAllCourses(Connection conn) throws Exception {
        Vector info = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT C.CourseID, C.CourseName, D.Name FROM Course C, District D ");
        query.append("WHERE D.DistrictID=C.District");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            DataTuple dt = new DataTuple();
            dt.setFirst(res.getString("CourseID"));
            dt.setSecond(res.getString("CourseName"));
            dt.setThird(res.getString("Name"));
            info.add(dt);
        }
        res.close();
        stmt.close();
        return info;
    }

    public Vector getAllCourseIDs(Connection conn) throws Exception {
        Vector info = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT CourseID FROM Course");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            info.add(res.getString("CourseID"));
        }
        res.close();
        stmt.close();
        return info;
    }

    public String getUserCourseID(String UID, Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        String courseID = "0";
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select CourseID from UserCourseLinks ");
        query.append("WHERE UserID='").append(uID).append("'");
        String qs = query.toString();
        res = stmt.executeQuery(qs);
        if (res.next()) {
            courseID = res.getString("CourseID");
        }
        res.close();
        stmt.close();
        return courseID;
    }


    public void getAllStudentsInCourseNames(TeacherInfo info, Connection conn) throws Exception {
        Vector names = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        int cID = Integer.parseInt(info.getCourseID());
        StringBuffer query = new StringBuffer();
        query.append("select U.UserID, U.Login, FirstName, LastName from UserTable U, UserCourseLinks S ");
        query.append("WHERE U.UserID=S.UserID and U.AccessLevel=1 ");
        query.append("and S.CourseID='").append(cID).append("'");
        query.append(" order by LastName");
        String qs = query.toString();
        res = stmt.executeQuery(qs);
        while (res.next()) {
            DataTuple dt = new DataTuple();
            dt.setFirst(res.getString("UserID"));
            String sName = "";
            String login = res.getString("Login");
            String fName = res.getString("FirstName");
            String lName = res.getString("LastName");
            //Use the name if possible, else use login
            if ((fName != null && fName.length() > 0 && !fName.equals(" "))
                    || (lName != null && lName.length() > 0 && !lName.equals(" "))) {
                sName = fName + " " + lName;
            } else
                sName = login;
            dt.setSecond(sName);
            names.add(dt);
        }
        res.close();
        stmt.close();
        info.setStudentNames(names);
    }

 //TreeMap:  key: userID, value:  firstname + lastname
    public TreeMap<String, String> getAllTeachers(Connection conn) throws Exception {
        TreeMap<String, String> data = new TreeMap<String, String>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select U.UserID, FirstName, LastName from UserTable U ");
        query.append("WHERE U.AccessLevel=2 ");
        query.append("order by LastName");
        String qs = query.toString();
        res = stmt.executeQuery(qs);
        while (res.next()) {
            String idStr = res.getString("UserID");
            String nameStr = res.getString("FirstName") + " " + res.getString("LastName");
            data.put(idStr, nameStr);
        }
        res.close();
        stmt.close();
        return data;
    }

    //TreeMap:  key: userID, value:  firstname + lastname
    public TreeMap<String, String> getAllStudentsInCourseNamesAndUserIDs(String courseID, Connection conn) throws Exception {
        TreeMap<String, String> data = new TreeMap<String, String>();
        int cID = Integer.parseInt(courseID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select U.UserID, FirstName, LastName from UserTable U, UserCourseLinks S ");
        query.append("WHERE U.UserID=S.UserID and U.AccessLevel=1 ");
        query.append("and S.CourseID='").append(cID).append("'");
        query.append(" order by LastName");
        String qs = query.toString();
        res = stmt.executeQuery(qs);
        while (res.next()) {
            String idStr = res.getString("UserID");
            String nameStr = res.getString("FirstName") + " " + res.getString("LastName");
            data.put(idStr, nameStr);
        }
        res.close();
        stmt.close();
        return data;
    }




















































    /*deletes the links to hints first
    public void deleteQuestion(String qID, Connection conn)throws Exception {
        deleteQuestionHintLinks(qID, conn);
        deleteAllQuestionToModuleLinks(qID, conn);
        Statement stmt=conn.createStatement();
        String qs="DELETE FROM Question WHERE QuestionID="+qID;
        try{
          stmt.executeQuery(qs);}
          catch(Exception sex) {
            String msg=sex.getMessage();
            if(!msg.contains(sqlExceptionStr_))
               throw sex;
           }
        stmt.close();
    }
 */


    public Vector getModuleIDsInCourse(String CID, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        Vector modIDs = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT ModuleID FROM ModCourseLinks WHERE CourseID='" + cID + "'");
        while (res.next()) {
            modIDs.add(res.getString("ModuleID"));
        }
        res.close();
        stmt.close();
        return modIDs;
    }

    public Vector getCourseIDsForModule(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        Vector cIDs = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT CourseID FROM ModCourseLinks WHERE ModuleID='" + mID + "'");
        while (res.next()) {
            cIDs.add(res.getString("CourseID"));
        }
        res.close();
        stmt.close();
        return cIDs;
    }



    public static int getInsertionID(String idFieldStr, String table,
                                     Connection conn) throws Exception {
        int ID = -1;
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT Max(" + idFieldStr + ") as MaxID FROM " + table);
        if (results.next() != false)
            ID = results.getInt("MaxID");
        results.close();
        stmt.close();
        return ID;
    }

    /*
insert into StudentFrameworkData (UserID,f4D5,f4D4,f4D3,f4D2,f4D1,f4M5,f4M4,f4M3,f4M2,
f4M1,f4G9,f4G8,f4G7,f4G6,f4G5,f4G4,f4G3,f4G2,f4G1,f4P6,f4P5,f4P4,f4P3,f4P2,f4P1,f4N18,
f4N17,f4N16,f4N15,f4N14,f4N13, f4N12,f4N11,f4N10,f4N9,f4N8,f4N7,f4N6,f4N5,f4N4,f4N3,f4N2,
f4N1) values ('2','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',
'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',
'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',
'0.5')

*/

    public double[] getStudentSkillData(String SID, int skillsDataSize,
                                        Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        double[] skillData = new double[skillsDataSize];
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT f4N1,f4N2,f4N3,f4N4,f4N5,f4N6,f4N7,f4N8,f4N9,f4N10,f4N11,f4N12,");
        query.append("f4N13,f4N14,f4N15,f4N16,f4N17,f4N18,f4P1,f4P2,f4P3,f4P4,f4P5,f4P6,");
        query.append("f4G1,f4G2,f4G3,f4G4,f4G5,f4G6,f4G7,f4G8,f4G9,f4M1,f4M2,f4M3,f4M4,");
        query.append("f4M5,f4D1,f4D2,f4D3,f4D4,f4D5,f4D6 from StudentFrameworkData ");
        query.append("where UserID='").append(sID).append("'");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            for (int j = 0; j < skillsDataSize; j++)
                skillData[j] = res.getDouble(standardsDBNames_[j]);
        }
        else
            logger.debug("Expecting framework data for student " + SID + " but found NONE!");
        res.close();
        stmt.close();
        return skillData;
    }

    // get rid of .
    public static String convertToColName (String standard) {
        String[] x = standard.split("\\.");
        StringBuffer sb = new StringBuffer("f");
        for (String y: x) {
            sb.append(y);
        }
        return sb.toString();
    }

    public static double getStudentSkillLevel(String SID, String standard,
                                        Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        double skillLev=0;
        StringBuffer query = new StringBuffer();
        query.append("SELECT " + convertToColName(standard) + " from StudentFrameworkData ");
        query.append("where UserID='").append(sID).append("'");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            skillLev = res.getDouble(1);
        }
        else
            logger.debug("Expecting framework data for student " + SID + " but found NONE!");
        res.close();
        stmt.close();
        return skillLev;
    }

    public double[] getStudentAlphaData(String SID, int skillsDataSize,
                                        Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        double[] skillData = new double[skillsDataSize];
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT a4N1,a4N2,a4N3,a4N4,a4N5,a4N6,a4N7,a4N8,a4N9,a4N10,a4N11,a4N12,");
        query.append("a4N13,a4N14,a4N15,a4N16,a4N17,a4N18,a4P1,a4P2,a4P3,a4P4,a4P5,a4P6,");
        query.append("a4G1,a4G2,a4G3,a4G4,a4G5,a4G6,a4G7,a4G8,a4G9,a4M1,a4M2,a4M3,a4M4,");
        query.append("a4M5,a4D1,a4D2,a4D3,a4D4,a4D5,a4D6 from StudentFrameworkData ");
        query.append("where UserID='").append(sID).append("'");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            for (int j = 0; j < skillsDataSize; j++)
                skillData[j] = res.getDouble(alphaDBNames_[j]);
        }
        else
            logger.debug("Expecting framework data for student " + SID + " but found NONE!");
        res.close();
        stmt.close();
        return skillData;
    }

    public double[] getStudentBetaData(String SID, int skillsDataSize,
                                       Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        double[] skillData = new double[skillsDataSize];
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT b4N1,b4N2,b4N3,b4N4,b4N5,b4N6,b4N7,b4N8,b4N9,b4N10,b4N11,b4N12,");
        query.append("b4N13,b4N14,b4N15,b4N16,b4N17,b4N18,b4P1,b4P2,b4P3,b4P4,b4P5,b4P6,");
        query.append("b4G1,b4G2,b4G3,b4G4,b4G5,b4G6,b4G7,b4G8,b4G9,b4M1,b4M2,b4M3,b4M4,");
        query.append("b4M5,b4D1,b4D2,b4D3,b4D4,b4D5,b4D6 from StudentFrameworkData ");
        query.append("where UserID='").append(sID).append("'");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            for (int j = 0; j < skillsDataSize; j++)
                skillData[j] = res.getDouble(betaDBNames_[j]);
        }
        else
            logger.debug("Expecting framework data for student " + SID + " but found NONE!");
        res.close();
        stmt.close();
        return skillData;
    }

    public double[] getMeanModelData(int skillsDataSize,
                                     Connection conn) throws Exception {
        double[] skillData = new double[skillsDataSize];
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT f4N1,f4N2,f4N3,f4N4,f4N5,f4N6,f4N7,f4N8,f4N9,f4N10,f4N11,f4N12,");
        query.append("f4N13,f4N14,f4N15,f4N16,f4N17,f4N18,f4P1,f4P2,f4P3,f4P4,f4P5,f4P6,");
        query.append("f4G1,f4G2,f4G3,f4G4,f4G5,f4G6,f4G7,f4G8,f4G9,f4M1,f4M2,f4M3,f4M4,");
        query.append("f4M5,f4D1,f4D2,f4D3,f4D4,f4D5,f4D6 from MeanModel ");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            for (int j = 0; j < skillsDataSize; j++)
                skillData[j] = res.getDouble(standardsDBNames_[j]);
        }
        res.close();
        stmt.close();
        return skillData;
    }

    public void updateStudentModelData(String SID, String[] stdNames,
                                       double[] standardsData, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int uID = -1;
        StringBuffer query = new StringBuffer();
        query.append("UPDATE StudentFrameworkData SET ");
        for (int i = 0; i < stdNames.length; i++) {
            query.append(stdNames[i]).append("=").append(standardsData[i]);
            if (i != stdNames.length - 1)
                query.append(", ");
        }
        query.append(" WHERE UserID= '").append(sID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        // There is a bug where studentframework data disappears (don't know why yet).  This error handling is allowing
        // failing update statements to proceed when the data loss has occured.
        catch (Exception exc) {
            String msg = exc.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw exc;
            else logger.debug("Expecting framework data for student " + SID + " but found NONE!");
        }
        stmt.close();
    }


    public void saveStudentModuleStartTime(String SID, String MID, String modStartTime,
                                           Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int mID = Integer.parseInt(MID);
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO StudentModuleData (");
        query.append("UserID, ModID, ModStartTime) ");
        query.append(" VALUES ( ");
        query.append("'").append(sID).append("', ");
        query.append("'").append(mID).append("', ");
        query.append("'").append(modStartTime).append("')");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    //return null if no start time
    public String getStudentModuleStartTime(String SID, String MID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int mID = Integer.parseInt(MID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT ModStartTime FROM StudentModuleData WHERE UserID='");
        query.append(sID).append("' AND ModID='").append(mID).append("'");
        res = stmt.executeQuery(query.toString());
        String start = null;
        if (res.next()) {
            start = res.getString("ModStartTime");
        }
        res.close();
        stmt.close();
        return start;
    }

    public void deleteStudentModuleStartTime(String SID, String MID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int mID = Integer.parseInt(MID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM StudentModuleData WHERE UserID='");
        query.append(sID).append("' AND ModID='").append(mID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }



    public int updateResponseLog(String questionID, String modID,
                                  String modType,
                                  String userID, int modIndex,
                                  long ansTS, String ansString,
                                  boolean isCorrect, int ordinality,
                                  Connection conn) throws Exception {
        try {
            // normally we'll do an update but in case the demo gets changed we need to first try an insert and then do update when it fails
            insertIntoResponseLog(questionID,modID,modType,userID,modIndex,ansTS,ansString,isCorrect,ordinality,conn);
            return 1;
        } catch (SQLException e) {
            int qID = Integer.parseInt(questionID);
            int uID = Integer.parseInt(userID);
            int mID = Integer.parseInt(modID);
            PreparedStatement stmt=null;
            try {
                String q = "update ResponseLog set QuestionID=?, ModID=?, ModType=?,UserID=?,ModIndex=?,ResponseTS=?,ResponseString=?,Correct=?,Ordinality=? where QuestionID=? and UserID=?";
                stmt = conn.prepareStatement(q);
                stmt.setInt(1,qID);
                stmt.setInt(2,mID);
                stmt.setString(3,modType);
                stmt.setInt(4,uID);
                stmt.setInt(5,modIndex);
                stmt.setTimestamp(6,new Timestamp(ansTS));
                stmt.setString(7,ansString);
                stmt.setBoolean(8,isCorrect);
                stmt.setInt(9,ordinality);
                stmt.setInt(10,qID);
                stmt.setInt(11,uID);
                return stmt.executeUpdate();
            } finally {
                if (stmt != null)
                    stmt.close();
            }
        }
    }


    //RESPONSE LOG
    public void insertIntoResponseLog(String questionID, String modID,
                                  String modType,
                                  String userID, int modIndex,
                                  long ansTS, String ansString,
                                  boolean isCorrect, int ordinality,
                                  Connection conn) throws Exception {
        int qID = Integer.parseInt(questionID);
        int uID = Integer.parseInt(userID);
        int mID = Integer.parseInt(modID);
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO ResponseLog (");
        query.append("QuestionID, ModID, ModType, UserID, ModIndex, ResponseTS, ");
        query.append("ResponseString, Correct, Ordinality)");
        query.append(" VALUES ( ");
        query.append("'").append(qID).append("', ");
        query.append("'").append(mID).append("', ");
        query.append("'").append(safeStringCheck(modType)).append("', ");
        query.append("'").append(uID).append("', ");
        query.append("'").append(modIndex).append("', ");
        query.append("'").append(ReportDataUtils.getTimestampString(ansTS)).append("', ");
        query.append("'").append(safeStringCheck(ansString)).append("', ");
        if (isCorrect)
            query.append("'").append(1).append("', ");
        else
            query.append("'").append(0).append("', ");
        query.append("'").append(ordinality).append("') ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    //get the mod index for the last entry in the ResponseLog. returns -1 if no entry
    public int getCurrentResponseLogEntryIndex(String sID, Connection conn) throws Exception {
        int modIndex = -1;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT ModIndex from ResponseLog ");
        query.append("WHERE UserID='").append(sID).append("' ");
        query.append("AND ResponseTS = ");
        query.append("(SELECT max(ResponseTS) from ResponseLog ");
        query.append("WHERE UserID='").append(sID).append("')");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            modIndex = res.getInt("ModIndex");
        }
        res.close();
        stmt.close();
        return modIndex;
    }

    //HINT LOG
    public void initHintLog(String HID, String QID, String SID,
                            String COACHID, String MID, String mType,
                            int step, long qStartTS,
                            long hintTS, int ord,
                            Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int sID = Integer.parseInt(SID);
        int mID = Integer.parseInt(MID);
        int hID = Integer.parseInt(HID);
        int coachID = Integer.parseInt(COACHID);
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO HintLog (");
        query.append("HintID, QuestionID, UserID, CoachID, ModID, ModType, Step, ");
        query.append("QuestionStartTS, HintTS, Ordinality)");

        query.append(" VALUES ( ");
        query.append("'").append(hID).append("', ");
        query.append("'").append(qID).append("', ");
        query.append("'").append(sID).append("', ");
        query.append("'").append(coachID).append("', ");
        query.append("'").append(mID).append("', ");
        query.append("'").append(safeStringCheck(mType)).append("', ");
        query.append("'").append(step).append("', ");
        query.append("'").append(ReportDataUtils.getTimestampString(qStartTS)).append("', ");
        query.append("'").append(ReportDataUtils.getTimestampString(hintTS)).append("', ");
        query.append("'").append(ord).append("') ");


        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public int getCurrentHintLogEntryOrd(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int ord = -1;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select MAX(Ordinality) as Ord from HintLog ");
        query.append("where UserID='").append(sID).append("'");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            ord = res.getInt("Ord");
        }
        res.close();
        stmt.close();
        return ord;
    }

    /* get answered question IDs for each mod
       the TreeMap has key: mID  val:  Vector of DataTuples:
       first= qID, second=Correct, third=responseTime, fourth=modType  */
    public void getQuestionResponseDataForEachMod(String SID, TreeMap modData, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT ModID, ModType, QuestionID, Correct, ResponseTS from ResponseLog ");
        query.append("WHERE UserID='").append(sID).append("' ");
        query.append("AND ResponseString IS NOT NULL ");
        query.append("AND ResponseString !='").append(FormalitySubsystem.questionStartString).append("' ");
        query.append("GROUP BY ModID, QuestionID, ModType, Correct, ResponseTS ");
        query.append("ORDER BY ResponseTS ");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            String mID = res.getString("ModID");
            String mType = res.getString("ModType");
            String qID = res.getString("QuestionID");
            boolean correct = res.getBoolean("Correct");
            String responseTS = res.getString("ResponseTS");
            DataTuple dt = new DataTuple();
            dt.setFirst(qID);
            dt.setSecond(new Boolean(correct));
            dt.setThird(responseTS);
            dt.setFourth(mType);
            Vector modQData = (Vector) modData.get(mID);
            if (modQData == null) { //this modID is not in the map
                modQData = new Vector();
                modQData.add(dt);
                modData.put(mID, modQData);
            } else {
                modQData.add(dt);
            }
        }
        res.close();
        stmt.close();
    }

    /* get answered question IDs for a mod
       the TreeMap has key: mID  val:  Vector of DataTuples: first= qID, second=Correct*/
    public void getModQuestionResponseData(String SID, String modID, TreeMap modData, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT QuestionID, Correct, ResponseTS from ResponseLog ");
        query.append("WHERE UserID='").append(sID).append("' ");
        query.append("AND ModID='").append(modID).append("' ");
        query.append("AND ResponseString IS NOT NULL ");
        query.append("AND ResponseString !='").append(FormalitySubsystem.questionStartString).append("' ");
        query.append("ORDER BY ResponseTS ");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            String qID = res.getString("QuestionID");
            boolean correct = res.getBoolean("Correct");
            String responseTS = res.getString("ResponseTS");
            DataTuple dt = new DataTuple();
            dt.setFirst(qID);
            dt.setSecond(new Boolean(correct));
            dt.setThird(responseTS);
            Vector modQData = (Vector) modData.get(modID);
            if (modQData == null) { //this modID is not in the map
                modQData = new Vector();
                modQData.add(dt);
                modData.put(modID, modQData);
            } else {
                modQData.add(dt);
            }
        }
        res.close();
        stmt.close();
    }

    /* get the question id for the last question answered for a student for a module- return -1 if not worked */
    public int getLastQuestionIDAnswered(String SID, String MID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int mID = Integer.parseInt(MID);
        int qID = -1;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT Max(ResponseTS)as LastID FROM ResponseLog ");
        query.append("WHERE UserID='").append(sID).append("' ");
        query.append("AND ModID='").append(mID).append("' ");
        query.append("AND ResponseString IS NOT NULL ");
        query.append("AND ResponseString !='").append(FormalitySubsystem.questionStartString).append("' ");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            qID = res.getInt("LastID");
        }
        res.close();
        stmt.close();
        return qID;
    }

    /* get the total # of ACTIVE questions linked to this standard*/
    public int getQCountLinkedToAStd(String stdID, Connection conn) throws Exception {
        int ct = 0;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT count(QuestionID)as Qct from Question ");
        query.append("WHERE Std1='").append(safeStringCheck(stdID)).append("' AND Ready=1");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            ct = res.getInt("Qct");
        }
        res.close();
        stmt.close();
        return ct;
    }

    /* get the total # of questions linked to this standard that are linked to a module*/
    public int getQCountLinkedToAStdAndAMod(String stdID, Connection conn) throws Exception {
        int ct = 0;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT count(QuestionID)as Qct from Question ");
        query.append("WHERE Std1='").append(safeStringCheck(stdID)).append("' ");
        query.append("and QuestionID in (select QuestionID from ModQuestionLinks)");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            ct = res.getInt("Qct");
        }
        res.close();
        stmt.close();
        return ct;
    }

    public String loadModuleScoreboard(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        String data = null;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select ModRecord from UserTable ");
        query.append("where UserID='").append(sID).append("' ");
        res = stmt.executeQuery(query.toString());
        boolean ok = false;
        if (res.next()) {
            data = res.getString("ModRecord");
            if (data == null)
                data = " ";
        }
        res.close();
        stmt.close();
        return data;
    }

    public void updateModuleScoreboard(String SID, String data, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("update Usertable set modrecord='").append(safeStringCheck(data)).append("' ");
        query.append("where UserID='").append(sID).append("' ");
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void resetModuleScoreboard(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("update Usertable set modrecord=' ' ");
        query.append("where UserID='").append(sID).append("' ");
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void updateModuleScoreHintCount(String UID, String MID, String QID,
                                           int hint, Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        int mID = Integer.parseInt(MID);
        int qID = Integer.parseInt(QID);
        StringBuffer query = new StringBuffer();
        query.append("UPDATE ModuleScore SET hints='").append(hint).append("' ");
        query.append("WHERE userID= '").append(uID).append("' ");
        query.append("AND modID='").append(mID).append("' ");
        query.append("AND qID='").append(qID).append("'");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public byte[] getQuestionAudio(Connection conn, int qid, String lang) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q;
            if (lang.equals("eng"))
                q = "select audio from Question where QuestionId=?";
            else q = "select spanishAudio from Question where QuestionId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, qid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                byte[] c = rs.getBytes(1);
                return c;
            }

        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return null;
    }

    public String getQuestionAudioFile(Connection conn, int qid, String lang) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String col = (lang.equals("eng") ? "audioFile" : "spanishAudioFile");
        try {
            String q;
            q = "select " +col+ " from Question where QuestionId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, qid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String f = rs.getString(col);
                return f;
            }

        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return null;
    }

    public int setUserProblemStartTime(String userID, Long curTS, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String q = "update usertable set lastProbStartTime=? where userId=?";
            stmt = conn.prepareStatement(q);
            if (curTS != null)
                stmt.setLong(1, curTS);
            else stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2, Integer.parseInt(userID));
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public Long getUserProblemStartTime(String userId, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select lastProbStartTime from usertable where userId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(userId));
            rs = stmt.executeQuery();
            if (rs.next()) {
                Long t = rs.getLong(1);
                if (rs.wasNull())
                    return null;
                else return t;
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return new Long(0);
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        Timestamp ts = new Timestamp(t);
        long t2 = ts.getTime();
        System.out.println(t == t2);
        System.out.println("order string for fixed is : " + QModule.OrderType.fixed.name());
    }

    public Hint getHint(Connection conn, String hID) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select * from hint where hintID=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(hID));
            rs = stmt.executeQuery();
            if (rs.next()) {
                String ask = rs.getString("Ask");
                String resp = rs.getString("Response");
                int step = rs.getInt("Step");
                int coachId = rs.getInt("coachID");
                int stratId = rs.getInt("strategyID");
                String ansLink = rs.getString("AnsLink");
                int authID = rs.getInt("authorID");
                return new Hint(hID, Integer.toString(step), Integer.toString(coachId), Integer.toString(stratId), ansLink, ask, resp, Integer.toString(authID));
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
        return null;
    }

    public int updateEventHintLevel(Connection conn, int eventID, String hintLevel) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String q = "update eventlog set hintstep=? where id=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(hintLevel));
            stmt.setInt(2, eventID);
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    /**
     * Return the last response to a question (used for survey questions only)
     *
     * @param conn
     * @param userID
     * @param qid
     * @return
     * @throws SQLException
     */
    public String getSurveyQuestionStudentLastResponse(Connection conn, String userID, String qid) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select ResponseString from responselog where userid=? and questionid=? order by ResponseTS DESC";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(userID));
            stmt.setInt(2, Integer.parseInt(qid));
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else return null;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    /**
     * Given a student ID (perhaps -1 if we are looking up a fixed ordering defined by author) for a module
     * return the ordering of questions in the module
     *
     * @param conn
     * @param studId
     * @param modId
     * @return the question ids in the module based on the ordering.   If no ordering exists, returns null
     * @throws SQLException
     */
    public Vector<String> getModuleQuestionSequence(Connection conn, String studId, String modId) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            Vector<String> ordering = new Vector<String>();
            String q = "select qid from ModuleQuestionSequence where studId=? and modId=? order by seq ASC";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(studId));
            stmt.setInt(2, Integer.parseInt(modId));
            rs = stmt.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                int qid = rs.getInt(1);
                ordering.add(Integer.toString(qid));
            }
            if (found)
                return ordering;
            else return null;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    /**
     * Called to set (remove + insert) a module question sequence for an author making changes to a fixed
     * sequence.   Should never be necessary for a student since his sequence is generated randomly and only once
     *
     * @param conn
     * @param modId
     * @param idSequence
     */
    public void resetModuleQuestionSequence(Connection conn, String modId, List<String> idSequence) throws SQLException {
        deleteModuleQuestionSequence(conn, "-1", modId);
        setModuleQuestionSequence(conn, "-1", modId, idSequence);
    }

    /**
     * Delete all entries for the given student (for now the special id -1 indicating an author) and module
     *
     * @param conn
     * @param studId
     * @param modId
     */
    private int deleteModuleQuestionSequence(Connection conn, String studId, String modId) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String q = "delete from ModuleQuestionSequence where studId=? and modId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(studId));
            stmt.setInt(2, Integer.parseInt(modId));
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    /**
     * Called to set (insert) a module question sequence for a student
     *
     * @param conn
     * @param studId
     * @param modId
     * @param idSequence
     */
    public void setModuleQuestionSequence(Connection conn, String studId, String modId, List<String> idSequence) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "insert into ModuleQuestionSequence (studId, ModId, QID, seq) values (?,?,?,?)";
            for (int seq = 0; seq < idSequence.size(); seq++) {
                String qid = idSequence.get(seq);
                stmt = conn.prepareStatement(q);
                stmt.setInt(1, Integer.parseInt(studId));
                stmt.setInt(2, Integer.parseInt(modId));
                stmt.setInt(3, Integer.parseInt(qid));
                stmt.setInt(4, seq);
                stmt.execute();
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }

    public static List<Course> getCourses(Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            List<Course> courses = new ArrayList<Course>();
            String q = "select courseId,CourseName from course";
            stmt = conn.prepareStatement(q);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String id= rs.getString(1);
                String n= rs.getString(2);
                Course c = new Course();
                c.setCourseName(n);
                c.setCourseID(id);
                courses.add(c);
            }
            return courses;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static Map<String,String> getAllDistricts (Connection conn) throws SQLException {
        Map<String,String> m = new TreeMap<String,String>();
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select districtId,name from district ";
            stmt = conn.prepareStatement(q);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id= rs.getInt(1);
                String name = rs.getString(2);
                m.put(Integer.toString(id),name);
            }
            return m;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }


   public ModuleCoachData getModCoachData (List<ModuleCoachData> data, int modId) {
        for (ModuleCoachData d: data) {
            if (d.modId == modId)
                return d;
        }
       return null;
   }

    public List<ModuleCoachData> getClassCoachUseData(Connection conn, String courseID) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        List<ModuleCoachData> data = new ArrayList<ModuleCoachData>();
        try {
            String q = "select hl.modid, hl.coachid, hl.questionid,m.name from hintlog hl, module m, usertable u, usercourselinks l" +
                    " where u.userid=l.userid and l.courseId=? and hl.userid=u.userid and u.accessLevel=1 and m.moduleid=hl.modid";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,courseID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int modId= rs.getInt(1);
                int coachId= rs.getInt(2);
                int qid= rs.getInt(3);
                String modName = rs.getString(4);
                ModuleCoachData d = getModCoachData(data,modId);
                if (d == null)
                    data.add(d = new ModuleCoachData(modId,modName));
                d.incrementCoachUse(coachId); 
            }
            return data;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }
}
