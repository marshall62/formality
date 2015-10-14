package formality.model;

import formality.Util.DataTuple;
import formality.content.Course;

import java.util.TreeMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Aug 1, 2005
 * Time: 11:46:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserInfo {

    public static final int STUDENT = 1;
    public static final int TEACHER = 2;
    public static final int AUTHOR = 3;

    public enum Gender {
        male,
        female
    }
    public enum Level {
        below,
        on,
        above,
        unknown
    }
    public enum IEP {
        none,
        math,
        reading,
        other,
        both,
        unknown
    }
    public enum Title {
        none,
        ela,
        math,
        unspecified
    }

    String initals;
    Gender gender;
    String userFname_;
    String userLname_;
    String userID_;
    int accessLevel_;
    String userPwd_;
    String login_;
    String language;
    ArrayList<Course> courses_;
    Course selectedCourse_;
//    String course_;
//    String courseID_;
//    String courseNotes_;
    //    String courseController_;
    String userInstitution_;
    String gender_;
    String state_;
    String district_;
    String modrec_;
    //    boolean useScoreboard_;
    int age_;
    boolean nativeEnglish_;
    boolean prevExp_;
    IEP iep;
    Level readingLev;
    Level mathLev;
    Title title;
    double[] skillValues_;
    double[] meanModel_;
    //use for assignment to an experimental condition
    int condition_;

    public UserInfo() {
        userFname_ = null;
        userLname_ = null;
        userID_ = null;
        // course_=null;
        userInstitution_ = null;
        gender_ = null;
        state_ = null;
        int accessLevel_ = 0;
        condition_ = 0;
        courses_ = null;
        selectedCourse_ = null;
        iep = IEP.unknown;
        readingLev = Level.unknown;
        mathLev = Level.unknown;
        title = Title.unspecified;
    }

    // n.b. this must be called after setting the list of courses for the user.
    public void setSelectedCourseInfo(String selectedCourseID) {
        if (selectedCourseID == null)
            if (courses_.size() < 2)
                selectedCourse_ = courses_.get(0);
            else
                return;
        else {
            for (int i = 0; i < courses_.size(); i++) {
                Course c = courses_.get(i);
                if (c.getCourseID().equals(selectedCourseID))
                    selectedCourse_ = c;
            }
        }
    }

    public boolean hasMultiCourses() {
        if (courses_ != null && courses_.size() > 1)
            return true;
        else
            return false;
    }

    public boolean isUseScoreboard() {
        if (selectedCourse_ == null)
            return false;
        else
            return selectedCourse_.isUseScoreboard();
    }

    public Course getSelectedCourse() {
        return selectedCourse_;
    }

    public String getModrec() {
        return modrec_;
    }

    public void setModrec(String modrec) {
        modrec_ = modrec;
    }

    public String getDistrict() {
        return district_;
    }

    public void setDistrict(String district_) {
        this.district_ = district_;
    }

    public double[] getMeanSkillModel() {
        return meanModel_;
    }

    public void setMeanSkillModel(double[] meanModel_) {
        this.meanModel_ = meanModel_;
    }

    public double[] getSkillValues() {
        return skillValues_;
    }

    public void setSkillValues(double[] skillValues_) {
        this.skillValues_ = skillValues_;
    }

    public String getGender() {
        return gender_;
    }

    public void setGender(String gender_) {
        this.gender_ = gender_;
    }

    public String getState() {
        return state_;
    }

    public void setState(String state_) {
        this.state_ = state_;
    }

    public String getCourseName() {
        return selectedCourse_.getCourseName();
    }

    public String getLogin() {
        return login_;
    }

    public void setLogin(String login_) {
        this.login_ = login_;
    }

    public String getUserPwd() {
        return userPwd_;
    }

    public void setUserPwd(String userPwd_) {
        this.userPwd_ = userPwd_;
    }

    public String getInstitution() {
        if (selectedCourse_ == null)
            return null;
        else
            return selectedCourse_.getInstitution();
    }

    public String getUserInstitution() {
        return userInstitution_;
    }

    public void setInstitution(String institution_) {
        this.userInstitution_ = institution_;
    }

    public int getAccessLevel() {
        return accessLevel_;
    }

    public void setAccessLevel(int accessLevel_) {
        this.accessLevel_ = accessLevel_;
    }

    public String getUserFnameOrLogin() {
        if (userFname_ != null && userFname_.length() > 0 && !userFname_.equals(" "))
            return userFname_;
        else
            return login_;
    }

    public String getUserFname() {
        return userFname_;
    }

    public void setUserFname(String userFname_) {
        this.userFname_ = userFname_;
    }

    public String getUserLname() {
        return userLname_;
    }

    public void setUserLname(String userLname_) {
        this.userLname_ = userLname_;
    }

    public int getAge() {
        return age_;
    }

    public void setAge(int age_) {
        this.age_ = age_;
    }


    public boolean isNativeEnglish() {
        return nativeEnglish_;
    }

    public void setNativeEnglish(boolean nativeEnglish_) {
        this.nativeEnglish_ = nativeEnglish_;
    }

    public boolean isPrevExp() {
        return prevExp_;
    }

    public void setPrevExp(boolean prevExp_) {
        this.prevExp_ = prevExp_;
    }

    public String getUserID() {
        return userID_;
    }

    public void setUserID(String userID_) {
        this.userID_ = userID_;
    }


    public String getCourseID() {
        return selectedCourse_.getCourseID();
    }

    public String getCourseNotes() {
        return selectedCourse_.getCourseNotes();
    }

    public String getCourseController() {
        if (selectedCourse_ == null)
            return null;
        else
            return selectedCourse_.getCourseController();
    }

    public ArrayList<Course> getCourses() {
        return courses_;
    }

    public void setCourses(ArrayList<Course> courses) {
        courses_ = courses;
    }

    public int getCondition() {
        return condition_;
    }

    public void setCondition(int condition_) {
        this.condition_ = condition_;
    }

    public String getInitals() {
        return initals;
    }

    public void setInitals(String initals) {
        this.initals = initals;
    }

    public IEP getIep() {
        return iep;
    }

    public void setIep(String iep) {
        if (iep != null && !iep.equals(""))
            this.iep = UserInfo.IEP.valueOf(iep);
        else this.iep = UserInfo.IEP.unknown;
    }

    public Level getReadingLev() {
        return readingLev;
    }

    public void setReadingLev(String readingLev) {
        if (readingLev != null && !readingLev.equals(""))
            this.readingLev = UserInfo.Level.valueOf(readingLev);
        else this.readingLev= UserInfo.Level.unknown;
    }

    public Level getMathLev() {
        return mathLev;
    }

    public void setMathLev(String mathLev) {
        if (mathLev != null && !mathLev.equals(""))
            this.mathLev = UserInfo.Level.valueOf(mathLev);
        else this.mathLev= UserInfo.Level.unknown;;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && !title.equals(""))
            this.title = UserInfo.Title.valueOf(title);
        else this.title = UserInfo.Title.unspecified;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}
