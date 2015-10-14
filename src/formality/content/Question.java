package formality.content;

import java.util.TreeMap;
import java.util.Vector;
import java.util.Iterator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 */
public class Question {

    String stem_=null;
    String categoryID_=null;
    String activeTag_=null;
    String qType_=null;
    String ID_;
    String author_=null;
    String source_=null;
    String degree_=null;
    String std1_;
    String std2_;
    String std3_;
    String ccss1;
    String ccss2;
    String ccss3;
    String topic_;
    Hints hints_;
    Vector curLevelHints_;
    boolean status_;
    double diffLevel_;
    String correctAnswer_=null;
    //state variables
    String selLevel_;
    String selHintID_;
    String submittedAns_;
    boolean evaluated_;
    boolean correct_;
    QModule module_;
    String audioFile;
    String spanishAudioFile;
    boolean sequentialLayout;

    //aggregate data from question/hint log:
    //most recent
    int globalOrdinality_;
    int sessionOrdinality_;
    int numHintsBeforeFirstAns_;

    //this vector contains a list of pairs:
    //coachID(row) and step
    Vector hintsBeforeFirstAns_;
    int numHintsAfterFirstAns_;
    //this vector contains a list of pairs:
    //coachID(row) and step
    Vector hintsAfterFirstAns_;
    boolean firstAnswer_;
    boolean secondAnswer_;
    boolean firstAnswerCorrect_;
    boolean secondAnswerCorrect_;
    long timeToFirstAnswer_;
    long timeToFirstHint_;
    public static final int ANSWER_LAYOUT_SEQUENTIAL = 0;
    public static final int ANSWER_LAYOUT_TABLE = 1;
    public static final String SURVEY_QUESTION = "Survey Question";
    private boolean isSurvey;


    public Question() {
        ID_=null;
        init();
    }

    public Question (Question q) {
      this(q.getID());
      this.setType(q.getType());
      this.setAuthor(q.getAuthor());
      this.setSource(q.getSource());
      this.setDegree(q.getDegree());
      this.setCategoryID(q.getCategoryID());
      this.setStatus(q.getStatus());
      this.setDiffLevel(q.getDiffLevel());
      this.setStem(q.getStem());
      this.setStd1(q.getStd1());
      this.setStd2(q.getStd2());
      this.setStd3(q.getStd3());
      this.setTopic(q.getTopic());
      this.setSurvey(q.isSurvey());
      this.setSequentialLayout(q.isSequentialLayout());
    }

    public Question(String id) {
       ID_=id;
       init();
    }

    private void init(){
        //state variables
        evaluated_=false;
        correct_=false;
        selLevel_=null;//what is the default?
        selHintID_=null;
        module_=null;
        //end state variables
        audioFile=null;
        spanishAudioFile=null;
        std1_=null;
        std2_=null;
        std3_=null;
        topic_=null;
        hints_=new Hints();
        diffLevel_=0;
        status_=false;
        isSurvey=false;
        curLevelHints_ = new Vector();
    }

    public String getTopic() {
        return topic_;
    }

    public void setTopic(String topic_) {
        this.topic_ = topic_;
    }

    public void setCorrectAnswer(String c){
        correctAnswer_=c;
    }

    public String getCorrectAnswer(){
        return correctAnswer_;
    }
    
    public void setID(String id){
       ID_=id;
    }

   public String getID(){
      return ID_;
   }

    public void setCorrect(boolean s){
        correct_=s;
    }

    public boolean isCorrect(){
        return correct_;
    }

    public void setSubmittedAnswer(String a){
        submittedAns_=a;
    }

    public String getSubmittedAnswer(){
        return submittedAns_;
    }

    public void setEvaluated(boolean s){
        evaluated_=s;
    }

    public boolean isEvaluated(){
        return evaluated_;
    }

    public String getSelHintID(){
       return selHintID_;
    }

    public void setSelHintID(String d){
       selHintID_=d;
   }

   //the order of this question in a module
   public int getModIndex()throws Exception{
     return module_.getQuestionIndex(ID_);
   }

    public QModule getModule(){
     return module_;
   }

   public void setModule(QModule m){
     module_=m;
   }

    public String getSelLevel(){
       return selLevel_;
    }

    public void setSelLevel(String d){
       selLevel_=d;
   }

   public void setDegree(String d){
      degree_=d;
   }

   public String getStd1(){
      return std1_;
   }

   public void setStd1(String d){
      std1_=d;
   }

   public String getStd2(){
      return std2_;
   }

   public void setStd2(String d){
      std2_=d;
   }

   public String getStd3(){
      return std3_;
   }

   public void setStd3(String d){
      std3_=d;
   }

  public String[] getContentInfoArray(){
      String[] info = {std1_,std2_,std3_,Double.toString(diffLevel_),degree_};
      return info;
  }

  public String getDegree(){
     return degree_;
   }

   public void setAuthor(String a){
      author_=a;
   }

  public String getAuthor(){
     return author_;
   }

   public void setSource(String s){
      source_=s;
   }

  public String getSource(){
     return source_;
   }

   public void setType(String id){
      qType_=id;
   }

   public String getType(){
     return qType_;
   }

    public void setStatus(boolean s){
        status_=s;
    }

    public boolean getStatus(){
        return status_;
    }

    public void setDiffLevel(double d){
        diffLevel_=d;
    }

    public double getDiffLevel(){
        return diffLevel_;
    }

    public void setHints(Hints h){
        hints_=h;
    }

    public void setHintLevel(Vector h){
        curLevelHints_ =h;
    }

    public void setStem(String s){
            stem_=s;
    }

    public String getStem(){
        return stem_;
    }

    public void setCategoryID(String cID){
        categoryID_=cID;
    }

    public String getCategoryID(){
        return categoryID_;
    }

    public void setActiveTag(String at){
            activeTag_=at;
    }

    public String getActiveTag(){
        return activeTag_;
    }

    public String getIDString(){
        return "Question "+ID_;
    }

   public Hints getHints(){
      return hints_;
   }

   //may return null
   public String getHintResponse(String hID){
       if(hints_!=null)
        return hints_.getHintResponse(hID);
       else
           return "";
   }

    //Student Data Section
    public long getTimeToFirstHint() {
        return timeToFirstHint_;
    }

    public void setTimeToFirstHint(long timeToFirstHint_) {
        this.timeToFirstHint_ = timeToFirstHint_;
    }

    public long getTimeToFirstAnswer() {
        return timeToFirstAnswer_;
    }

    public void setTimeToFirstAnswer(long timeToFirstAnswer_) {
        this.timeToFirstAnswer_ = timeToFirstAnswer_;
    }

    public boolean isSecondAnswerCorrect() {
        return secondAnswerCorrect_;
    }

    public void setSecondAnswerCorrect(boolean secondAnswerCorrect_) {
        this.secondAnswerCorrect_ = secondAnswerCorrect_;
    }

    public boolean isFirstAnswerCorrect() {
        return firstAnswerCorrect_;
    }

    public void setFirstAnswerCorrect(boolean firstAnswerCorrect_) {
        this.firstAnswerCorrect_ = firstAnswerCorrect_;
    }

    public boolean isSecondAnswer() {
        return secondAnswer_;
    }

    public void setSecondAnswer(boolean secondAnswer_) {
        this.secondAnswer_ = secondAnswer_;
    }

    public boolean isFirstAnswer() {
        return firstAnswer_;
    }

    public void setFirstAnswer(boolean firstAnswer_) {
        this.firstAnswer_ = firstAnswer_;
    }

    // contains DualInfo objects
    public Vector getHintsAfterFirstAns() {
        return hintsAfterFirstAns_;
    }

    // contains DualInfo objects
    public void setHintsAfterFirstAns(Vector hintsAfterFirstAns_) {
        this.hintsAfterFirstAns_ = hintsAfterFirstAns_;
    }

    public int getNumHintsAfterFirstAns() {
        return numHintsAfterFirstAns_;
    }

    public void setNumHintsAfterFirstAns(int numHintsAfterFirstAns_) {
        this.numHintsAfterFirstAns_ = numHintsAfterFirstAns_;
    }

    // contains DualInfo objects
    public Vector getHintsBeforeFirstAns() {
        return hintsBeforeFirstAns_;
    }

    // contains DualInfo objects
    public void setHintsBeforeFirstAns(Vector hintsBeforeFirstAns_) {
        this.hintsBeforeFirstAns_ = hintsBeforeFirstAns_;
    }

    public int getNumHintsBeforeFirstAns() {
        return numHintsBeforeFirstAns_;
    }

    public void setNumHintsBeforeFirstAns(int numHintsBeforeFirstAns_) {
        this.numHintsBeforeFirstAns_ = numHintsBeforeFirstAns_;
    }

    public int getSessionOrdinality() {
        return sessionOrdinality_;
    }

    public void setSessionOrdinality(int sessionOrdinality_) {
        this.sessionOrdinality_ = sessionOrdinality_;
    }

    public int getGlobalOrdinality() {
        return globalOrdinality_;
    }

    public void setGlobalOrdinality(int globalOrdinality_) {
        this.globalOrdinality_ = globalOrdinality_;
    }

   public boolean areAnswersLoaded(){
       return false;
   }

    public String getAudioFile() {
        return audioFile;
    }

    public String getSpanishAudioFile() {
        return spanishAudioFile;
    }

    public void setAudioFile(String audioFile) {
        if (audioFile!=null && audioFile.equals(""))
            this.audioFile=null;
        else this.audioFile = audioFile;
    }

    public void setSpanishAudioFile(String spanAudioFile) {
        if (spanAudioFile!=null && spanAudioFile.equals(""))
            this.spanishAudioFile=null;
        else this.spanishAudioFile = spanAudioFile;
    }



    public boolean isSequentialLayout() {
        return sequentialLayout;
    }

    public void setSequentialLayout(boolean sequentialLayout) {
        this.sequentialLayout = sequentialLayout;
    }

    public void setSurvey(boolean aBoolean) {
        this.isSurvey=aBoolean;
    }

    public boolean isSurvey () {
        return this.isSurvey;
    }

    public boolean isMultipleChoice () {
        return this.getType().equals("mc");
    }

    public boolean isShortAnswer () {
        return this.getType().equals("sa");  
    }

    public String getCcss1() {
        return ccss1;
    }

    public void setCcss1(String ccss1) {
        this.ccss1 = ccss1;
    }

    public String getCcss2() {
        return ccss2;
    }

    public void setCcss2(String ccss2) {
        this.ccss2 = ccss2;
    }

    public String getCcss3() {
        return ccss3;
    }

    public void setCcss3(String ccss3) {
        this.ccss3 = ccss3;
    }

    public boolean gradeAnswer(String subAns) {
        return subAns.equalsIgnoreCase(this.correctAnswer_);
    }

    private class DualInfo{
      String first;
      String second;
      public DualInfo(String f, String s){
          first=f;
          second=s;
      }
      public String getFirst(){
        return first;
     }
     public String getSecond(){
       return second;
     }
  }
}
