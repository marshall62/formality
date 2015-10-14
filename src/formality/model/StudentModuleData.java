package formality.model;

import formality.content.Question;

import java.util.Vector;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Aug 3, 2005
 * Time: 9:52:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentModuleData {
    //StudentModuleData
    //UserID, ModID, ModStartTime, Qindex, Correct, SubAns
    String sID, mID, modStartTime;
    TreeMap questionData_;

   public StudentModuleData(){
       sID=null;
       mID=null;
       modStartTime=null;
       questionData_=new TreeMap();
   }

   public StudentModuleData(String sid, String mid, String mst){
       sID=sid;
       mID=mid;
       modStartTime=mst;
       questionData_=new TreeMap();
   }

   public void addQdata(int qind, boolean corr, String sa,
                       String ca){
       Question q=new Question();
       q.setCorrect(corr);
       q.setSubmittedAnswer(sa);
       q.setCorrectAnswer(ca);
       questionData_.put(new Integer(qind), q);
   }

    public void addQdata(int qind, Question q){
        questionData_.put(new Integer(qind), q);
    }

  public Vector getAllQdata(){
       return (Vector)questionData_.values();
   }

  public Question getQdataAtIndex(int ind)throws Exception{
       return (Question)questionData_.get(new Integer(ind));
   }

   public void setModID(String m){
        mID=m;
   }

   public String getModID(){
        return mID;
   }

   public void setStudentID(String s){
        sID=s;
   }

   public String getStudentID(){
        return sID;
   }

    public void setModStartTime(String s){
        modStartTime=s;
   }

    public String getModStartTime(){
        return modStartTime;
   }
}
