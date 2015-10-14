package formality.controller;

import java.util.Map;

import formality.content.*;
import formality.content.database.mysql.DBAccessor;
import formality.html.*;
import formality.controller.coursecontroller.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
/**
 * <p>Title: StudentSubsystem</p>
 *
 * <p>Description: This subsystem manages the student's request at the top level:
 * it loads the pedagogical agent that is associated with the student's course
 * and hand off control to this agent.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class StudentSubsystem {
    //student modes
    public static final String studentHomeMode_ = "shome";
    public static final String studentAllCoursesMode_ = "schome";
    public static final String viewModuleMode_="viewmod";
    public static final String viewTestModuleMode_="viewtmod";
    public static final String viewQuestionMode_ = "viewq";
    public static final String selectLevelMode_ = "sellevel";
    public static final String viewHintMode_ = "selhint";
    public static final String clearQuestionMode_ = "clearq";
    public static final String viewTestQuestionMode_ = "viewtq";
    public static final String viewSurveyTestQuestionMode_ = "survey.viewtq";
    public static final String evaluateQuestionMode_="evalq";
    public static final String evaluateSurveyQuestionMode_="survey.evalq";
    public static final String evaluateSurveyTestQuestionMode_="survey.evaltq";
    public static final String evaluateTestQuestionMode_="evaltq";
    public static final String getStudentExamMode_= "gstex";
    public static final String viewExternalActivity = "viewextact";
    public static final String viewGlossary = "viewglossary";
    public static final String readAloud = "readAloud";

    /*Published Events
studentExam
studentHome
viewModule
viewTestModule
viewQuestion
viewTestQuestion
evaluatePracticeQuestion
evaluateTestQuestion
clearQuestion
clearTestQuestion
viewHint
selectHintLevel
    */

    DBAccessor dbAccessor_;
    CourseController courseController_;
    StudentHtml studentPage_;
    String msg_;


    public StudentSubsystem() {
    }

  public boolean handleRequest(Map params, SystemInfo info, Connection conn, StringBuffer page, HttpServletResponse response)throws Exception{
    String mode = GeneralHtml.getParamStr(params, FormalitySubsystem.modeParam, "");
    dbAccessor_ = new DBAccessor();
    //load the course information for this user
    try{
      dbAccessor_.loadStudentCourses(info.getUserInfo(), conn);
    }catch(Exception ex){
        throw new Exception("Error loading course for student login "+info.getUserInfo().getLogin()+" "+ex.getMessage());
    }
    // DM added this since the FormalitySubsystem sets the selected Course for demo users when they log in.
    if (info.getUserInfo().getSelectedCourse() == null) {
        String courseID = GeneralHtml.getParamStr(params, FormalitySubsystem.courseIDParam, null);
        info.getUserInfo().setSelectedCourseInfo(courseID);
    }

    ///////delete when multi courses implemented///////////////////
   // dbAccessor_.getUserCourseInfo(info.getUserInfo(), conn);/////
   ///////////////////////////////////////////////////////////

    String controllerName = info.getUserInfo().getCourseController();
    courseController_ = getCourseController(controllerName);
    return courseController_.handleRequest(params, info, conn, page, response);
   }

    //current choices:
    //BaseCourseController, NoUpdateCourseController, ScoreboardController
    // return  BaseCourseController by default
    private CourseController  getCourseController(String controllerName) throws Exception {
     // DM 1/12/12  Eliminated the use of NoUpdateController.   The demo class now uses the two regular controllers
     // and we just delete the data created by demo users with a demon.
     if(controllerName==null)
       return new BaseCourseController();
     else if(controllerName.equals("ScoreboardController"))
        return new ScoreboardController();
     else
        return new BaseCourseController();
    }
}
