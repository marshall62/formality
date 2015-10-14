package formality.model;

import formality.Util.DataTuple;
import formality.content.QModule;

import java.util.TreeMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Aug 1, 2005
 * Time: 11:46:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentInfo extends UserInfo{

    
    long sessionTS_ ;
    int preTestIndex=0;
    int postTestIndex=0;
    //STUDENT DATA a TreeMap where the key is a Module ID,
    // and the values are Vectors of DataTuples. Each DataTuple
    // contains:  First- a QuestionID (String), Second Correctness (Boolean)
    //third- response time stamp
    //Note that each DataTuple Represents an attempt to answer the question
    //so the same QID may be repeatred in the Vector.
    TreeMap modResponseData_ ;
    //qID, ans, corr 1 or 0
//    Vector modAnswerData_=null;
    Vector completedModuleIDs_;
    TreeMap modCts_;
    int numQuestionsAnswered_;
    int numSessionQuestionsAnswered_;
    //Pre and Post test info
    Vector preTestModData_;
    Vector postTestModData_;

    //holds the framework standards and a Double value that
    //represents the probability of knowing that standard
    double[] standardsData_;

    public StudentInfo(){
        super();
        modResponseData_=null;
        preTestModData_= new Vector();
        postTestModData_= new Vector();
        completedModuleIDs_= new Vector();
    }

    public int getPreTestIndex() {
        return preTestIndex;
    }

    public void setPreTestIndex(int preTestIndex) {
        this.preTestIndex = preTestIndex;
    }

    public int getPostTestIndex() {
        return postTestIndex;
    }

    public void setPostTestIndex(int postTestIndex) {
        this.postTestIndex = postTestIndex;
    }

   public long getSessionTS() {
           return sessionTS_;
   }

   public void setSessionTS(long sessionTS_) {
           this.sessionTS_ = sessionTS_;
   }

   public void setModResponseData(TreeMap md){
        modResponseData_=md;
   }

    //tuple contains answer (String), correct(String 1 or 0)- may return a null DataTuple
    // the qIDs are in order of earliest answered to latest answered
  //assume all entries represent answers to questions
   //may return null if no data
   public String getLatestQuestionIDAnswered(String modID)throws Exception{
       String curQID = null;
       Vector qData= (Vector)modResponseData_.get(modID);
       if(qData != null){
           DataTuple dt = (DataTuple)qData.lastElement();
           curQID = (String)dt.getFirst();
       }
       return curQID;
    }


  //tuple contains answer (String), correct(String 1 or 0)- may return a null DataTuple
  // the qIDs are in order of earliest answered to latest answered
   public String getLatestQuestionAnswerCorrectness(String qID, String modID){
     String correctStr = null;
     boolean found = false;
     Vector qData= (Vector)modResponseData_.get(modID);
     if(qData != null){
       for(int i=qData.size()-1;i>=0;i--){
         DataTuple dt = (DataTuple)qData.get(i);
         String curQID = (String)dt.getFirst();
         if(curQID.equals(qID)){
           if(!found){
             found = true;
             Boolean BoolVal = (Boolean)dt.getSecond();
             if(BoolVal.booleanValue())
                 correctStr = "1";
             else
                 correctStr = "0";
           }
         }
       }
     }
     return correctStr;
   }

    public boolean isCompletedModule(String mID, int totalQCt){
        int numAnswered = getDistinctNumAnsweredInModule(mID);
        return (numAnswered>=totalQCt);
   }

  //a count of the number of DISTICT questions answered- correct or not
  //modResponseData_ is a TreeMap has key: mID  val:  Vector of DataTuples: first= qID, second=Correct third=responseTS
   public int getDistinctNumAnsweredInModule(String mID){
     TreeMap distinctAnsweredQs = new TreeMap();
     Vector qData= (Vector)modResponseData_.get(mID);
     if(qData!=null){
        Iterator qit= qData.iterator();
        while(qit.hasNext()){
          DataTuple dt = (DataTuple)qit.next();
          String qID = (String)dt.getFirst();
          distinctAnsweredQs.put(qID, null);
        }
      }
     return distinctAnsweredQs.size();
   }

    //a count of the number of question answers (not DISTINCT)
  //modResponseData_ is a TreeMap has key: mID  val:  Vector of DataTuples: first= qID, second=Correct third=responseTS
   public int getNumAnswersInModule(String mID){
     int numAnswers = 0;
     Vector qData= (Vector)modResponseData_.get(mID);
     if(qData!=null)
       numAnswers = qData.size();
     return numAnswers;
   }

   public int getNumCorrectInModule(String mID){
      TreeMap correctQs = new TreeMap();
      Vector qData= (Vector)modResponseData_.get(mID);
      if(qData!=null){
        Iterator qit= qData.iterator();
        while(qit.hasNext()){
          DataTuple dt = (DataTuple)qit.next();
          Boolean correct = (Boolean)dt.getSecond();
          if(correct.booleanValue()){
            String qID = (String)dt.getFirst();
            correctQs.put(qID, null);
          }
        }
      }
      return correctQs.size();
    }

    public void setModCts(TreeMap modCts_) {
        this.modCts_ = modCts_;
    }

   public void setStudentStandardsData(double[] d)throws Exception{
          standardsData_=d;
   }

    public double[] getStudentStandardsData()throws Exception{
          return standardsData_;
   }

   // assume the student's response data has been loaded
    public void setPreTestModData(List<QModule> mods) throws Exception {
        for (QModule m: mods) {
          String testMID = m.getID();
          int qCt = 0;
          try{m.getNumLinkedQuestions();
          }catch(Exception ex){
              qCt = m.getQuestionCount();
          }
          int numAnswered = getDistinctNumAnsweredInModule(testMID);
          int numCorrect = getNumCorrectInModule(testMID);
          boolean completed = (qCt == numAnswered);
          preTestModData_.add(new TestModuleInfo(m.getID(), numAnswered, numCorrect,
                                  qCt, completed));
       }
    }

       // assume the student's response data has been loaded
    public void setPostTestModData(List<QModule> mods) throws Exception {
        for (QModule m: mods) {
          String testMID = m.getID();
          int qCt = 0;
          try{m.getNumLinkedQuestions();
          }catch(Exception ex){
              qCt = m.getQuestionCount();
          }
          int numAnswered = getDistinctNumAnsweredInModule(testMID);
          int numCorrect = getNumCorrectInModule(testMID);
          boolean completed = (qCt == numAnswered);
          postTestModData_.add(new TestModuleInfo(m.getID(), numAnswered, numCorrect,
                                  qCt, completed));
       }
    }

    // ratio of total correct/ total questions
    public double getPreTestScore() {
        double score=0.0;
        score = getPreTestNumCorrect()/(double)getPreTestTotalQuestions();
        return score;
    }

    public double getPostTestScore() {
        double score=0.0;
        score = getPostTestNumCorrect()/(double)getPostTestTotalQuestions();
        return score;
    }

    public int getPreTestNumCorrect() {
        int numCorr=0;
        Iterator pit = preTestModData_.iterator();
        while(pit.hasNext()){
            TestModuleInfo ti = (TestModuleInfo)pit.next();
            numCorr+= ti.getTestNumCorrect();
        }
        return numCorr;
    }

    public int getPreTestTotalQuestions() {
       int totalQ=0;
       Iterator pit = preTestModData_.iterator();
        while(pit.hasNext()){
            TestModuleInfo ti = (TestModuleInfo)pit.next();
            totalQ+= ti.getTestTotalQuestions();
        }
       return totalQ;
    }

    public boolean isPreTestCompleted() {
        if(preTestModData_.size()==0)
          return false;
        boolean completed=true;
        Iterator pit = preTestModData_.iterator();
        while(pit.hasNext()){
            TestModuleInfo ti = (TestModuleInfo)pit.next();
            if(!ti.isTestCompleted())
                completed=false;
        }
        return completed;
    }

    public int getPostTestNumCorrect() {
        int numCorr=0;
        Iterator pit = postTestModData_.iterator();
        while(pit.hasNext()){
            TestModuleInfo ti = (TestModuleInfo)pit.next();
            numCorr+= ti.getTestNumCorrect();
        }
        return numCorr;
    }

    public int getPostTestTotalQuestions() {
       int totalQ=0;
       Iterator pit = postTestModData_.iterator();
        while(pit.hasNext()){
            TestModuleInfo ti = (TestModuleInfo)pit.next();
            totalQ+= ti.getTestTotalQuestions();
        }
       return totalQ;
    }

    public boolean isPostTestCompleted() {
        if(postTestModData_.size()==0)
          return false;
        boolean completed=true;
        Iterator pit = postTestModData_.iterator();
        while(pit.hasNext()){
            TestModuleInfo ti = (TestModuleInfo)pit.next();
            if(!ti.isTestCompleted())
                completed=false;
        }
        return completed;
    }

    public int getNumSessionQuestionsAnswered() {
        return numSessionQuestionsAnswered_;
    }

    public void setNumSessionQuestionsAnswered(int numSessionQuestionsAnswered_) {
        this.numSessionQuestionsAnswered_ = numSessionQuestionsAnswered_;
    }

    public int getNumQuestionsAnswered() {
        return numQuestionsAnswered_;
    }

    public void setNumQuestionsAnswered(int numQuestionsAnswered_) {
        this.numQuestionsAnswered_ = numQuestionsAnswered_;
    }
    //hold "test" module data
   private class TestModuleInfo {
      String testModID;
      int testQIndex;
      int testNumCorrect;
      int testTotalQuestions;
      boolean testCompleted;

   public TestModuleInfo(String testMID, int testQInd, int testNumC,
                                  int testTotalQ, boolean testComp){
      testModID=testMID;
      testQIndex=testQInd;
      testNumCorrect=testNumC;
      testTotalQuestions=testTotalQ;
      testCompleted=testComp;
   }
     public String getTestModID() {
        return testModID;
    }

    public void setTestModID(String tmID) {
        testModID = tmID;
    }

    public int getTestQIndex() {
        return testQIndex;
    }

    public void setTestQIndex(int testQI) {
        testQIndex = testQI;
    }

    public int getTestNumCorrect() {
        return testNumCorrect;
    }

    public void setTestNumCorrect(int testNumC) {
        testNumCorrect = testNumC;
    }

    public int getTestTotalQuestions() {
        return testTotalQuestions;
    }

    public void setTestTotalQuestions(int testTotalQ) {
        testTotalQuestions = testTotalQ;
    }

    public boolean isTestCompleted() {
        return testCompleted;
    }

    public void setTestCompleted(boolean tc) {
        testCompleted = tc;
    }
 }

}
