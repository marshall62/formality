package formality.content.database;

import formality.Util.DataTuple;
import formality.model.UserInfo;
import formality.model.StudentInfo;
import formality.controller.FormalitySubsystem;
import formality.content.database.BaseDBAccessor;

import java.util.Vector;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Sep 2, 2005
 * Time: 10:56:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportAccessor extends BaseDBAccessor{

    public Vector getUserNamesAndID(String CID, Connection conn) throws Exception{
      int cID = Integer.parseInt(CID);
      Statement stmt=conn.createStatement();
      ResultSet res;
      StringBuffer query=new StringBuffer();
      query.append("select U.Login, U.UserID from UserTable U, UserCourseLinks C ");
      query.append("where U.UserID=C.UserID AND C.CourseID='").append(cID).append("' ");
      res=stmt.executeQuery(query.toString());
      Vector data=new Vector();
      while(res.next()){
       DataTuple dt = new DataTuple();
       dt.setFirst(res.getString("Login"));
       dt.setSecond(res.getString("UserID"));
       data.add(dt);
     }
     res.close();
     stmt.close();
     return data;
   }

   // for a module:  Tree Map   key: studentID value: ArrayList<DataTuple>
   //                       DataTuple: (String)qID, (Boolean)correct
    public TreeMap<String, ArrayList<DataTuple>> getStudentAttemptsModHistory(String cidStr,
                                                                              String midStr,
                                       Connection conn) throws Exception{
      int cID = Integer.parseInt(cidStr);
      int mID = Integer.parseInt(midStr);
      TreeMap<String, ArrayList<DataTuple>> allStudentData = new TreeMap<String, ArrayList<DataTuple>>();
      Statement stmt = conn.createStatement();
      ResultSet res;
      StringBuffer query=new StringBuffer();
      query.append("SELECT UserID, QuestionID, Correct, ResponseTS from ResponseLog ");
      query.append("WHERE ModID='").append(mID).append("' ");
      query.append("AND ResponseString IS NOT NULL ");
      query.append("AND ResponseString !='").append(FormalitySubsystem.questionStartString).append("' ");
      query.append("AND UserID IN ");
      query.append("(SELECT UserID from Usercourselinks where CourseID='").append(cID).append("') ");
      query.append("GROUP BY UserID, QuestionID, Correct, ResponseTS ");
      res=stmt.executeQuery(query.toString());
      while(res.next()){
         String userID = res.getString("UserID");
         String qID = res.getString("QuestionID");
         boolean curCorrect = res.getBoolean("Correct");
         ArrayList<DataTuple> studentData = allStudentData.get(userID);
         if(studentData==null){
            studentData =new ArrayList<DataTuple>();
            allStudentData.put(userID, studentData);
         }
         DataTuple newDT = new DataTuple();
         newDT.setFirst(qID);
         newDT.setSecond(new Boolean(curCorrect));
         studentData.add(newDT);
      }
     res.close();
     stmt.close();
     return allStudentData;
    }

  public Vector getCorrectAnswersForModuleInOrder(String MID, Vector qIDs,
                                                  Connection conn)throws Exception {
    int mID = Integer.parseInt(MID);
    Statement stmt=conn.createStatement();
    ResultSet res;
    Vector data = new Vector();
    StringBuffer query=new StringBuffer();
    query.append("select QuestionID, Correct from MultiChoiceAnswers WHERE QuestionID IN ");
    query.append("(select L.QuestionID from Module M, ModQuestionLinks L WHERE M.Status=1 ");
    query.append("AND M.ModuleID=L.ModuleID AND ModuleID='").append(mID).append("') ");
    res=stmt.executeQuery(query.toString());
    while(res.next()){
     DataTuple dt = new DataTuple();
     dt.setFirst(res.getString("QuestionID"));
     dt.setSecond(res.getString("Correct"));
     data.add(dt);
    }
   Vector r = new Vector();
   for(int i=0;i<qIDs.size();i++) {
       String curID=(String)qIDs.get(i);
       for(int j=0;j<data.size();j++) {
           DataTuple dt=(DataTuple)data.get(j);
           if(curID.equals(dt.getFirst()))
               r.add(dt.getSecond());
       }

   }
   res.close();
   stmt.close();
   return r;
  }

    // use student names unless only the login is present
    public Vector getAllStudentSkillData(String CID, int skillsDataSize,
                                          Connection conn) throws Exception{
     int cID = Integer.parseInt(CID);
     Vector data = new Vector();
     Statement stmt=conn.createStatement();
     ResultSet res;
     StringBuffer query = new StringBuffer();
     query.append("SELECT U.UserID, U.Login, U.FirstName, U.LastName,f4N1,f4N2,f4N3,f4N4,f4N5,f4N6,f4N7,f4N8,f4N9,f4N10,f4N11,");
     query.append("f4N12,f4N13,f4N14,f4N15,f4N16,f4N17,f4N18,f4P1,f4P2,f4P3,f4P4,f4P5,f4P6,");
     query.append("f4G1,f4G2,f4G3,f4G4,f4G5,f4G6,f4G7,f4G8,f4G9,f4M1,f4M2,f4M3,f4M4,f4M5,f4D1,");
     query.append("f4D2,f4D3,f4D4,f4D5,f4D6 from StudentFrameworkData D, UserTable U, ");
     query.append("UserCourseLinks C where U.UserID=C.UserID AND D.UserID=C.UserID ");
     query.append("AND C.CourseID='").append(cID).append("'");
     res=stmt.executeQuery(query.toString());
     while(res.next()){
       Vector sData = new Vector();
       sData.add(res.getString("UserID"));
       String sName = "";
       String login = res.getString("Login");
       String fName = res.getString("FirstName");
       String lName = res.getString("LastName");
       //Use the name if possible, else use login
       if ((fName!=null && fName.length()>0 && !fName.equals(" "))
               || (lName!=null && lName.length()>0 && !lName.equals(" "))){
            sName = fName+" "+lName;
       }
       else
            sName = login;
       sData.add(sName);
       for(int j=0;j<skillsDataSize;j++)
        sData.add(new Double(res.getDouble(standardsDBNames_[j])));
       data.add(sData);
     }
     res.close();
     stmt.close();
     return data;
    }


   /* get any hints for a question for a user that occurs before time bound*/
    public int hintEntryCt(String SID, String QID, double ts,
                                     Connection conn) throws Exception{
       int sID = Integer.parseInt(SID);
       int qID = Integer.parseInt(QID);
      int ct=0;
      Statement stmt=conn.createStatement();
      ResultSet res;
      StringBuffer query=new StringBuffer();
      query.append("select count(HintID)as ct from HintLog where UserID='").append(sID).append("' AND ");
      query.append("QuestionID='").append(qID).append("' AND ");
      query.append("HintTS < ").append(ts);
       String q=query.toString();
      res=stmt.executeQuery(q);
      if(res.next()){
        ct = res.getInt("ct");
      }
      res.close();
      stmt.close();
      return ct;
    }

    /* get the hint TS log entries for a question */
 public int getHintCount(String SID, String QID, Connection conn) throws Exception{
        int sID = Integer.parseInt(SID);
        int qID = Integer.parseInt(QID);
         int ct = 0;
         Statement stmt=conn.createStatement();
         ResultSet res;
         StringBuffer query=new StringBuffer();
         query.append("select count(hintTS)as ct from HintLog where UserID='").append(sID).append("' AND ");
         query.append("QuestionID='").append(qID).append("' ");
         res=stmt.executeQuery(query.toString());
         if(res.next()){
            ct = res.getInt("ct");
         }
         res.close();
         stmt.close();
         return ct;
       }


    public List<String> getQSkills(String QID, Connection conn) throws Exception{
      int qID = Integer.parseInt(QID);
      List<String> skills = new ArrayList<String>();
      Statement stmt=conn.createStatement();
      ResultSet res;
      StringBuffer query=new StringBuffer();
      query.append("select Std1, Std2, Std3 from Question ");
      query.append("where QuestionID='").append(qID).append("'");
      res=stmt.executeQuery(query.toString());
      if(res.next()){
       skills.add(res.getString("Std1"));
       String skillStr2 = res.getString("Std2");
       if(skillStr2!=null && skillStr2.length()>0)
          skills.add(skillStr2);
       String skillStr3 = res.getString("Std3");
       if(skillStr3!=null && skillStr3.length()>0)
         skills.add(skillStr3);
      }
     res.close();
     stmt.close();
     return skills;
    }

    //descending order means earliest time first in the return, otherwise the latest
    // time is first in the list.   For a given question, this returns a list of <responseTimestamp, isCorrect>
    public ArrayList<DataTuple> getQuestionResponseTimesAndCorrect(String userIDStr,
                                            String modIDStr,
                                            String qIDStr,
                                            boolean descendingOrder,
                                            Connection conn) throws Exception {
      int sID = Integer.parseInt(userIDStr);
      int mID = Integer.parseInt(modIDStr);
      int qID = Integer.parseInt(qIDStr);
      ArrayList<DataTuple> data = new ArrayList<DataTuple>();
      Statement stmt=conn.createStatement();
      ResultSet res;
      StringBuffer query=new StringBuffer();
      query.append("SELECT ResponseTS, Correct FROM ResponseLog ");
      query.append("WHERE UserID ='").append(sID).append("' ");
      query.append("AND ModID ='").append(mID).append("' ");
      query.append("AND QuestionID ='").append(qID).append("' ");
      query.append("AND ResponseString !='").append(FormalitySubsystem.questionStartString).append("' ");
      query.append("ORDER BY ResponseTS ");
      if(descendingOrder)
        query.append("DESC");
      res=stmt.executeQuery(query.toString());
      while(res.next()){
         DataTuple dt = new DataTuple();
         String eventTS = res.getString("ResponseTS");
         boolean corr = res.getBoolean("Correct");
         dt.setFirst(eventTS);
         dt.setSecond(new Boolean(corr));
         data.add(dt);
      }
      res.close();
      stmt.close();
      return data;
    }

    public ArrayList<String> getHintResponseTimes(String userIDStr,
                                            String modIDStr,
                                            String qIDStr,
                                            boolean descendingOrder,
                                            Connection conn) throws Exception {
      int sID = Integer.parseInt(userIDStr);
      int mID = Integer.parseInt(modIDStr);
      int qID = Integer.parseInt(qIDStr);
      ArrayList<String> data = new ArrayList<String>();
      Statement stmt=conn.createStatement();
      ResultSet res;
      StringBuffer query=new StringBuffer();
      query.append("SELECT HintTS FROM HintLog ");
      query.append("WHERE UserID ='").append(sID).append("' ");
      query.append("AND ModID ='").append(mID).append("' ");
      query.append("AND QuestionID ='").append(qID).append("' ");
      query.append("ORDER BY HintTS ");
      if(descendingOrder)
        query.append("DESC");
      res=stmt.executeQuery(query.toString());
      while(res.next()){
         String eventTS = res.getString("HintTS");
         data.add(eventTS);
      }
      res.close();
      stmt.close();
      return data;
    }
}
