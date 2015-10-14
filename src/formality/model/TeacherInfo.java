package formality.model;

import java.util.Vector;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Aug 31, 2005
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class TeacherInfo extends UserInfo{

    //data tuples: first=userID, second= name
    Vector studentNames_;
    TreeMap modData_;
    TreeMap modQData_;
    StudentInfo studentInfo_;

    public TeacherInfo(){
       super();
       studentNames_=null;
       modData_=null;
       modQData_=null;
    }

    public TreeMap getModData() {
        return modData_;
    }

    public void setModData(TreeMap modData_) {
        this.modData_ = modData_;
    }

    public Iterator getStudentNames() {
        return studentNames_.iterator();
    }

    public void setStudentNames(Vector studentNames_) {
        this.studentNames_ = studentNames_;
    }


    public StudentInfo getStudentInfo() {
        return studentInfo_;
    }

    public void setStudentInfo(StudentInfo studentInfo_) {
        this.studentInfo_ = studentInfo_;
    }


    public TreeMap getModQData() {
        return modQData_;
    }

    public void setModQData(TreeMap modQData_) {
        this.modQData_ = modQData_;
    }
}
