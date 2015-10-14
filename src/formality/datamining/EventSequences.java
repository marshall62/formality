package formality.datamining;

import formality.Util.DataTuple;

import formality.Util.DbBootstrap;
import formality.content.database.BaseDBAccessor;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collections;
/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: May 6, 2009
 * Time: 4:45:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventSequences extends DbBootstrap{
  String outfileStr = "S:/User/4mality/reports/eventSequences.txt";

  public void run()throws Exception{
      TreeMap<String, ArrayList<String>> data = new TreeMap<String, ArrayList<String>>();
      Connection conn = getDBConnection();

      String courseID = "31";
      getStudentIDs(courseID, data, conn);
      processStudentData(data, conn);
      String report = getEventReport(data);
      printResults(report, outfileStr);
  }

   public void getStudentIDs(String courseID, TreeMap<String, ArrayList<String>> data,
                             Connection conn) throws Exception{
    Statement stmt=conn.createStatement();
    String query = getStudentIDsQuery(courseID);
    ResultSet res=stmt.executeQuery(query.toString());
    while(res.next()){
        String uid = res.getString("UserID");
        ArrayList<String> sData = new ArrayList<String>();
        data.put(uid, sData);
    }
    res.close();
    stmt.close();
 }

   public String getStudentIDsQuery(String courseID) throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("select U.userID from usertable U, usercourselinks L ");
    query.append("where L.userID=U.userID and L.courseID='").append(courseID).append("' ");
    query.append("and U.accesslevel=1");
    return query.toString();
  }

  public void processStudentData(TreeMap<String, ArrayList<String>> data,
                              Connection conn) throws Exception{
     Iterator<String> sIDIter = data.keySet().iterator();
     while(sIDIter.hasNext()){
         String sID = sIDIter.next();
         if(sID.equals("874")){
             int f=0;
         }
         ArrayList<String> sData = data.get(sID);
        try{
          ArrayList<DataTuple> qData = getStudentQuestionData(sID, conn);
          processStudentQuestionData(sID, qData, sData, conn);
        } catch (Exception ex){
             String s = ex.getMessage();
         }
     }
  }

    public void processStudentQuestionData(String sID,
                                           ArrayList<DataTuple> qData, 
                                           ArrayList<String> sData,
                                           Connection conn)throws Exception{
       if(qData.size()<1)
             sData.add("NA\n");
       else{
       Iterator<DataTuple> qIDIter = qData.iterator();
       while(qIDIter.hasNext()){
         DataTuple qdt = qIDIter.next();
         String qID = (String)qdt.getFirst();
         if(qID==null)
             sData.add("NA\n");
         else{
           String startTS  = (String)qdt.getSecond();
           String endTS = (String)qdt.getFifth();
           ArrayList<DataTuple> hData = getStudentHintData(sID, qID, startTS, endTS, conn);
           sData.add(getMergedData(qdt, hData));
         }
       }
      }
    }
        
   public ArrayList<DataTuple> getStudentQuestionData(String sID,
                              Connection conn) throws Exception{
     ArrayList<DataTuple> data = new ArrayList<DataTuple>();
     Statement stmt=conn.createStatement();
     String query = getStudentQuestionDataQuery(sID);
     ResultSet res=stmt.executeQuery(query.toString());
     while(res.next()){
         DataTuple dt = new DataTuple();
         dt.setFirst(res.getString("questionid"));
         dt.setSecond(res.getString("questionstartts"));
         dt.setThird(res.getString("ans1ts"));
         dt.setFourth(res.getString("ans2ts"));
         dt.setFifth(res.getString("questionendts"));
         dt.setSixth(res.getString("correct"));
         data.add(dt);
     }
     res.close();
     stmt.close();
     return data;
  }

  public String getStudentQuestionDataQuery(String sID) throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("select questionid, questionstartts, ans1ts, ans2ts, questionendts, correct ");
    query.append("from questionlog where userid='").append(sID).append("' ");
    query.append("and not(ans1ts IS NULL)and modtype='Practice' ");
    query.append("order by questionstartts desc ");
    return query.toString();
  }

  public ArrayList<DataTuple> getStudentHintData(String sID, String qID,
                                      String startTS, String endTS,
                                      Connection conn) throws Exception{
     ArrayList<DataTuple> hData = new ArrayList<DataTuple>();
     Statement stmt=conn.createStatement();
     String query = getStudentHintDataQuery(sID, qID, startTS, endTS);
     ResultSet res=stmt.executeQuery(query.toString());
     while(res.next()){
         DataTuple dt = new DataTuple();
         dt.setFirst(res.getString("hintts"));
         dt.setSecond(res.getString("coachid"));
         dt.setThird(res.getString("step"));
         hData.add(dt);
     }
     res.close();
     stmt.close();
     return hData;
  }

    /*need userid, questionid, question start and end timestamps */
   public String getStudentHintDataQuery(String sID, String qID,
                                            String startTS, String endTS) throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("select hintts, coachid, step from hintlog ");
    query.append("where userid='").append(sID).append("' ");
    query.append("and questionid='").append(qID).append("' ");
    query.append("and hintts>='").append(startTS).append("' ");
    query.append("and hintts<='").append(endTS).append("' ");
    query.append("order by hintts ");
    return query.toString();
  }

  public String getMergedData(DataTuple qdt, ArrayList<DataTuple> hData){
      StringBuffer report = new StringBuffer();
      ArrayList<Integer> diffs = new ArrayList<Integer>();
      String qID = (String)qdt.getFirst();
      report.append(qID).append('\t');
      String startTS  = (String)qdt.getSecond();
      String ans1TS  = (String)qdt.getThird();
      if(ans1TS!=null && ans1TS.length()>0){
          Integer d1 = new Integer(getDiffInSeconds(startTS, ans1TS));
          diffs.add(d1);
      }
      String ans2TS  = (String)qdt.getFourth();
      if(ans2TS!=null && ans2TS.length()>0){
          Integer d2 = new Integer(getDiffInSeconds(startTS, ans2TS));
          diffs.add(d2);
      }
      String endTS = (String)qdt.getFifth();
      String correct  = (String)qdt.getSixth();
      for(int i=0;i<hData.size();i++){
          DataTuple hdt = hData.get(i);
          String hTS = (String)hdt.getFirst();
          Integer dh = new Integer(getDiffInSeconds(startTS, hTS));
          diffs.add(dh);
      }
      Collections.sort(diffs);

      for(int i=0;i<diffs.size();i++){
          Integer curDiff = diffs.get(i);
          report.append(curDiff.toString());
          if(i<diffs.size()-1)
            report.append('\t');
          else
            report.append('\n');
      }
      return report.toString();
  }

   public int getDiffInSeconds(String startTS, String endTS){
      int diff = 0;
      long start = Long.parseLong(startTS);
      long end = Long.parseLong(endTS);
      long lDiff = (end-start)/1000;
      diff = (int)lDiff;
      return diff;
  }

  public String getEventReport(TreeMap<String, ArrayList<String>> data){
      StringBuffer report = new StringBuffer();
      Iterator<String> sIDIter = data.keySet().iterator();
      while(sIDIter.hasNext()){
         String sID = sIDIter.next();
         ArrayList<String> sData = data.get(sID);
         for(int i=0;i<sData.size();i++){
            report.append(sID).append('\t');
            report.append(sData.get(i));
         }
      }
      return report.toString();
    }

   public static void main(String[] args) {
       EventSequences app = new EventSequences();
       try{ app.run();
       System.out.println("done");}
      catch (Exception e){ System.out.print("ERROR: "+e.getMessage());}
    }
}
