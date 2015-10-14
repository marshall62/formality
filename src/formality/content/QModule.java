package formality.content;

import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DbModule;
import formality.systemerror.AuthoringException;

import java.sql.Connection;
import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class QModule implements Comparable {
    String mID_;
    String name_;
    String authorName_;
    String authorID_;
    //PreTest, Practice, Test, PostTest
    String modType_;
    String startTime_;
    int questionCount_;
    //ASSUME order in list is the display order
    // the IDs stored as Strings
    List<String> linkedQuestionIDs_;
    //dependencies- user must have done these mods first
    List<String> parentModIDs_;
    //vector of Questions
    List<Question> linkedQuestions_;
    boolean status_;
    //student state
    boolean touch_;
    boolean completed_;
    //the current question index (1-based)
    int currentIndex_;
    int nextIndex_;
    boolean isReentrant; // only applies to modules of type TEST

    /**
     * Try to find a fixed modulequestion sequence  (author created so use id=-1).   If not found,
     * then generate a random sequence by getting the modules questions, reordering them, and then
     * inserting in the module sequence table for the given user id.
     * Side-effects the given module by changing its list of linked question ids and list of Questions
     * to be in a new order.
     * @param conn
     * @param studId
     * @param dbAccessor
     * @return
     * @throws java.sql.SQLException
     */
    public List<String> adjustQuestionOrder(Connection conn, String studId, DBAccessor dbAccessor) throws Exception {
        List<String> qids;
        // Going by link order means questions are retrieved in the order they were linked to the module
        if (isLinkOrder()) {
            return this.linkedQuestionIDs_;
        }
        // Going by fixed order means getting the sequence of IDs from ModuleQuestionSequence table
        else if (isFixedOrder()) {
            // TODO broken because this returns List<Integer>
            //
            qids = DbModule.getModuleFixedQuestionSequence(conn,this.getID());
            if (qids == null)
                throw new Exception("Module defined to have order " + getQuestionSequencing().name() + " but no ModuleQuestionSequence found for this module: " + getID());
        }
        // must be in random order, so try to get a previously saved order for this student (may return null if this is first time student has entered module)
        else
            qids = dbAccessor.getModuleQuestionSequence(conn,studId, getID());
        // if no sequence exists for the student, get the list of ids, randomize it, and save in db
        if (qids == null) {
            qids= getLinkedQuestionIDList();
            Collections.shuffle(qids);
            // insert module question sequence in db for student
            dbAccessor.setModuleQuestionSequence(conn,studId, getID(),qids);
        }
        // Alter the module so that it has a reorderd question list and qid list

        // gets a Question list based on the order in the qids.
        List<Question> questions = DbModule.getModuleQuestions(qids,conn);
        // reset the module's list of question IDs and Question objects
        resetLinkedQuestions(qids, questions);
        return qids;
    }

    public boolean isReentrant() {
        return isReentrant;
    }

    public void setReentrant (boolean reentrant) {
        isReentrant = reentrant;
    }

    public int compareTo(Object o) {
        QModule other = (QModule) o;
        return (this.getID().compareTo(other.getID()));

    }

    public enum OrderType {random, fixed, link};
    public static final OrderType DEFAULT_ORDERING = OrderType.link;
    OrderType questionSequencing;


   public QModule() {
        mID_=null;
        modType_=null;
        init();
    }

    public QModule(String id, String type) {
        mID_=id;
        modType_=type;
        init();
    }

    private void init(){
        parentModIDs_=null;
        linkedQuestions_=null;
        linkedQuestionIDs_=new Vector();
        status_=false;
        touch_=false;
        completed_=false;
        name_=null;
        startTime_="";
        authorID_="";
        authorName_="";
        questionCount_=0;
        currentIndex_ = 1;
        nextIndex_ = 1;
        questionSequencing = DEFAULT_ORDERING;
        isReentrant = false;
    }



    public boolean isTest () {
        return modType_.equalsIgnoreCase("pretest") || modType_.equalsIgnoreCase("posttest") || 
                modType_.equalsIgnoreCase("test");
    }

    public String getAuthorName() {
        return authorName_;
    }

    public void setAuthorName(String authorName_) {
        this.authorName_ = authorName_;
    }

    public String getAuthorID() {
        return authorID_;
    }

    public void setAuthorID(String authorID_) {
        this.authorID_ = authorID_;
    }

    public String getID(){
        return mID_;
    }

    public void setID(String id){
        mID_=id;
    }

    public String getType(){
        return modType_;
    }

   public void setType(String t){
      modType_=t;
    }

    public String getStartTime(){
        return startTime_;
    }

   public void setStartTime(String t){
      startTime_=t;
    }

  public boolean getTouch(){
        return touch_;
    }

   public void setTouch(boolean t){
      touch_=t;
    }

    public void setStatus(boolean s){
        status_=s;
    }

    public boolean getStatus(){
        return status_;
    }

    public int getQuestionCount(){
        return questionCount_;
    }

    public void setName(String n){
        name_=n;
    }

    public String getName(){
        return name_;
    }

    public int getCurrentIndex() {
        return currentIndex_;
    }

    public void setCurrentIndex(int currentIndex_) {
        this.currentIndex_ = currentIndex_;
    }

    public int getNextIndex() {
        return nextIndex_;
    }

    public void setNextIndex(int nextIndex_) {
        this.nextIndex_ = nextIndex_;
    }

    public Iterator getLinkedQuestionIDs(){
        return linkedQuestionIDs_.iterator();
    }

    public List<String> getLinkedQuestionIDList(){
        return linkedQuestionIDs_;
    }

    public String[] getLinkedQuestionIDVecAsArray(){
        String[] ids = new String[linkedQuestionIDs_.size()];
        for(int i=0;i<linkedQuestionIDs_.size();i++)
           ids[i]=(String)linkedQuestionIDs_.get(i);
        return ids;
    }

    public Iterator getLinkedQuestions(){
        return linkedQuestions_.iterator();
    }

    public List<Question> getQuestions () {
        return linkedQuestions_;
    }

    //assumes the argumant is 1 based
    public Question getLinkedQuestion(int index)throws Exception{
        //adjust index
        return (Question)linkedQuestions_.get(index-1);
    }

    //the index is 1 based.
    // -1 returned if not matched.                                                               
    public int getLinkedQuestionIndex(String qID)throws Exception{
        int index = -1;
        for(int i=0;i<linkedQuestions_.size();i++){
           Question curQ = (Question)linkedQuestions_.get(i);
           if(qID.equals(curQ.getID()))
               index = i+1;
        }
        return index;
    }


    public Question getLinkedQuestion(String qID)throws Exception{
        Question q = null;
        for(int i=0;i<linkedQuestions_.size();i++){
           Question curQ = (Question)linkedQuestions_.get(i);
           if(qID.equals(curQ.getID()))
               q = curQ;
        }
        return q;
    }



   //NOTE: the index argument is one based
    //this method may return null or throw an exception if the index is
    // not a number
   public Question getLinkedQuestionByIndex(int index)throws Exception{
       Question curQ = (Question)linkedQuestions_.get(index-1);
       return curQ;
    }

    public int getNumLinkedQuestions()throws Exception{
        if(linkedQuestionIDs_==null && linkedQuestions_==null)
           throw new Exception("linked questions have not been set into this QModule object.");
        int size=0;
        if(linkedQuestionIDs_.size()>0)
          return linkedQuestionIDs_.size();
        if(linkedQuestions_.size()>0)
         return linkedQuestions_.size();
        return size;
    }
    
    public void setLinkedQuestions(List<Question> lqs){
        linkedQuestions_=lqs;
        questionCount_=linkedQuestions_.size();
    }

    public String getLinkedQuestionID(int index){
        return (String)linkedQuestionIDs_.get(index-1);
    }

    //return a ONE-based index
    // throws authoring exception if not found
    public int getQuestionIndex(String qID)throws Exception {
        int res=-1;
        for(int i=0;i<linkedQuestionIDs_.size();i++){
           String curID=(String)linkedQuestionIDs_.get(i);
           if(curID.equals(qID))
               res=i;
        }
       if(res==-1){
         AuthoringException aex=new AuthoringException();
         aex.setMessage("invalid questionID in QModule.getQuestionIndex() ");
         throw new AuthoringException();
       }
       else
         return res+1;
    }

    public void setLinkedQuestionIDs(List<String> qids){
        linkedQuestionIDs_=qids;
        questionCount_=linkedQuestionIDs_.size();
    }

    public void setQuestionCount(int count){
        questionCount_=count;
    }

   public Iterator getParentModIDs(){
        return parentModIDs_.iterator();
    }

   public void setParentModIDs(List<String> pids){
        parentModIDs_=pids;
    }

    public boolean isCompleted() {
           return completed_;
       }

       public void setCompleted(boolean completed_) {
           this.completed_ = completed_;
       }

    public OrderType getQuestionSequencing () {
        return questionSequencing;
    }

    public void setQuestionSequencing (String orderType) {
        if (orderType == null)
            questionSequencing = DEFAULT_ORDERING;
        else if (orderType.equals(OrderType.fixed.name()))
            questionSequencing = OrderType.fixed;
        else if (orderType.equals(OrderType.link.name()))
            questionSequencing = OrderType.link;
        else
            questionSequencing = OrderType.random;
    }


    public boolean isRandomOrder() {
        return questionSequencing == OrderType.random;
    }


    public boolean isFixedOrder() {
        return questionSequencing == OrderType.fixed;
    }


    public boolean isLinkOrder() {
        return questionSequencing == OrderType.link;
    }


    // replace the linked question id list and the list of Question objects in this module
    public void resetLinkedQuestions(List<String> qids, List<Question> questions) {
        this.setLinkedQuestionIDs(qids);
        this.setLinkedQuestions(questions);

    }

    public List<Question> getSurveyQuestions () {
        List<Question> surveyQs = new ArrayList<Question>();
        if (linkedQuestions_ != null)
            for (Question q: this.linkedQuestions_)
                if (q.isSurvey())
                    surveyQs.add(q);
        return surveyQs;
    }

    public void moveQuestionEarlier (String qid) throws Exception {
        int ix = getLinkedQuestionIndex(qid);   // returns a 1-based index
        // make sure its not trying to move the first element earlier
        if (ix > 1) {
            Collections.swap(linkedQuestions_,ix-1, ix-2);
            Collections.swap(linkedQuestionIDs_,ix-1,ix-2);
        }

    }

    public void moveQuestionLater (String qid) throws Exception {
        int ix = getLinkedQuestionIndex(qid); // return a 1-based index
        // make sure its not trying to move the first element earlier
        if (ix < linkedQuestions_.size()) {
            Collections.swap(linkedQuestions_,ix-1, ix);
            Collections.swap(linkedQuestionIDs_,ix-1,ix);
        }
    }
}
