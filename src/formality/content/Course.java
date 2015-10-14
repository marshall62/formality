package formality.content;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 14, 2007
 * Time: 10:49:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class Course {

    String courseID_;
    String courseName_;
    String institution_;
    String courseNotes_;
    String courseController_;
    int district_;
    boolean useScoreboard_;
    String teacher;
    String teacherId;
    String districtName;

    public Course(String courseID_, String courseName_, String institution_, String courseController_, int district_) {
        this.courseID_ = courseID_;
        this.courseName_ = courseName_;
        this.institution_ = institution_;
        this.courseController_ = courseController_;
        this.district_ = district_;
    }

    public Course() {
        
    }

    public boolean isUseScoreboard() {
        return useScoreboard_;
    }

    public void setUseScoreboard(boolean useScoreboard) {
        useScoreboard_ = useScoreboard;
    }

    public int getDistrict() {
        return district_;
    }

    public String getDistrictStr () {
        return Integer.toString(district_);
    }

    public void setDistrict(int district) {
        district_ = district;
    }

    public String getCourseController() {
        return courseController_;
    }

    public void setCourseController(String courseController) {
        courseController_ = courseController;
    }

    public String getCourseNotes() {
        return courseNotes_;
    }

    public void setCourseNotes(String courseNotes) {
        courseNotes_ = courseNotes;
    }

    public String getInstitution() {
        return institution_;
    }

    public void setInstitution(String institution) {
        institution_ = institution;
    }

    public String getCourseName() {
        return courseName_;
    }

    public void setCourseName(String courseName) {
        courseName_ = courseName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getCourseID() {
        return courseID_;
    }

    public void setCourseID(String courseID) {
        courseID_ = courseID;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
