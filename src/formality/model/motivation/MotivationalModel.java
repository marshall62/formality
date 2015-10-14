package formality.model.motivation;

import formality.Util.DataTuple;
import formality.content.database.mysql.DBAccessor;
import formality.model.ModelListener;
import formality.model.ModelEvent;
import formality.model.modeldata.MotivationalModelData;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 10, 2007
 * Time: 12:14:06 PM
 * this class handles the description of the scoring and feedback for the
 * motivational representation of the student.
 */
public class MotivationalModel implements ModelListener {
    public static final String delim_ = "#";
    //key:  answer sequence#hint flag    val: score, feedback string
    String[] modelKeys_ = {"0#1", "0#0", "1#1", "1#0", "00#1", "00#0", "01#1",
            "01#0", "000#1", "000#0", "001#1", "001#0",
            "0001#1", "0001#0", "0000#1", "0000#0"};
    // DM 11/11 Altered positions 6 to "7" when it used be "6" and position
    // 10 to "5" when it was "4"/   Also alterred position 12 to be 4 instead of 3.
    String[] modelScores_ = {"0", "0", "8", "8", "0", "0", "7", "5", "0",
            "0", "5", "4", "2", "1", "1", "0"};
    String[] modelMsgs_ = {"You checked with the coaches, but you did not solve the problem.  Try a different coach for more ideas.  Your score is 0 out of 8.",
            "You did not solve the problem. The coaches are waiting to help you. Your score is 0 out of 8.",
            "You checked with the coaches and you solved the problem! Your score is 8 out of 8.",
            "You solved the problem! Your score is 8 out of 8.",
            "You checked with the coaches, but you did not solve the problem. Try a different coach for more ideas.  Your score is 0 out of 8.",
            "You have tried twice, but did not solve the problem. The coaches are waiting to help you. Your score is 0 out of 8.",
            "You checked with the coaches and you solved the problem!  Your score is 7 out of 8.",
            "You solved the problem!  You will score more points if you check with the coaches before you answer. Your score is 5 out of 8.",
            "You checked with the coaches, but you did not solve the problem.  Try a different coach for more ideas. Your score is 0 out of 8.",
            "You have tried three times, but did not solve the problem. The coaches are waiting to help you. Your score is 0 out of 8.",
            "You checked with the coaches and you solved the problem! Your score is 5 out of 8.",
            "You solved the problem!  You will score more points if you check with the coaches before you answer. Your score is 4 out of 8.",
            "You checked with the coaches and you solved the problem! Your score is 2 out of 8.",
            "You solved the problem! You will score more points if you check with the coaches before you answer. Your score is 1 out of 8.",
            "You checked with the coaches but you did not solve the problem. Your score is 1 out of 8.",
            "You did not solve the problem. Checking with the coaches will help you score more points. Your score is 0 out of 8."
            };

    private final static String maxScore_ = "8";
    //if hint count is above this value, set to this value
    /**
     * ****************CHANGE IF HINTS TO BE COUNTED***************
     */
    private final static int maxHintCount_ = 1;
    /**
     * ************************************************************
     */
    //dt: qID, score, ansSeq, hints
    ArrayList<DataTuple> dBScores_;
    //dt: qID, score
    ArrayList<DataTuple> scoreData_;
    List<String> modQuestionIDs_;
    String curMessage_;
    String selectedQID_;
    int selectedIndex_;

    DBAccessor dbAccessor_;
    Connection conn_;

    public MotivationalModel() {
        dBScores_ = null;
        curMessage_ = null;
        modQuestionIDs_ = null;
        curMessage_ = "";
        selectedQID_ = null;
    }

    public void init(Object[] initItems) throws Exception {
        dbAccessor_ = (DBAccessor) initItems[0];
        conn_ = (Connection) initItems[1];
    }

    public void updateModel(ModelEvent e) throws Exception {
        /*
    studentExam,
    studentHome,
    viewModule,
    viewTestModule,
    viewQuestion,
    viewTestQuestion,
    evaluatePracticeQuestion,
    evaluateTestQuestion,
    clearQuestion,
    clearTestQuestion,
    viewHint,
    selectHintLevel
        */
        ModelEvent.EventTypes eventType = e.getType();
        String sID = e.getUserID();
        String qID = e.getItemID();
        String mID = e.getContentID();
        int qIndex = e.getqIndex();
        setModQuestionIDs(e.getContentInfoArray());
        boolean correct = e.isCorrect();
        //  String answer = e.getResponse();
        switch (eventType) {
            //module scores may be empty at first.
            case viewModule:
                setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                if (getdBScores().size() < 1) //mod has not been visited before
                    //need to initialize the Module Score table for each question in the mod:
                    //userID, modID, questionID, score=0, ansSequence=NULL, hints=0
                    initModuleScore(sID, mID, dbAccessor_, conn_);
                //load the qID, scores into the scoreData_ array, set the message in curMessage_
                createScoreData();
                break;

            case viewQuestion:
                setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                // sets the global variables selectedIndex_ and selectedQID_
                setSelectedQuestionInfo(qIndex, qID);
                //check the selected question ans sequence. if terminal, reset
                // DM 10/12 this seems like strange behavior.   I can go back to any question that was previously
                // scored and it will wipe out the score and allow me to do the problem again and get a new score.
                // TODO try to see how it works without this score reseting
                checkSelectedState(sID, mID, dbAccessor_, conn_);
                createScoreData();
                break;

            case viewHint:
                setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                createScoreData();
                updateHints(sID, mID, qID, dbAccessor_, conn_);
                break;                            
            case evaluateSurveyQuestion:
                setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
//                updateModuleScore(sID, mID, qID, correct, dbAccessor_, conn_);
//                setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                setSelectedQuestionInfo(qIndex, qID);
                createScoreDataForSurveyEval();
                break;
            case evaluateQuestion:
                setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                updateModuleScore(sID, mID, qID, correct, dbAccessor_, conn_);
                setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                setSelectedQuestionInfo(qIndex, qID);
                createScoreData();
                break;
                /*
                        case selectHintLevel:  if(mID!=null){
                                    setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                                    setModQuestionIDs(e.getContentInfoArray());
                                    setSelectedQuestionInfo(qIndex, qID);
                                    createScoreData();
                                  }
                */
            case viewTestQuestion:
                break;
            case evaluateTestQuestion:
                break;
            case clearTestQuestion:
                break;
            default:
                if (mID != null) {
                    setdBScores(dbAccessor_.loadModuleScores(sID, mID, conn_));
                    setModQuestionIDs(e.getContentInfoArray());
                    //setSelectedQuestionInfo(qIndex, qID);
                    createScoreData();
                }

        }
    }

    public Object getModelData() throws Exception {
        MotivationalModelData data = new MotivationalModelData();
        try {
            data.setScoreData(getScoreDataCopy());
            data.setCurMessage(curMessage_);
            data.setMaxScore(maxScore_);
        } catch (Exception ex) {
            data.setErrorStr(ex.getMessage());
        }
        return data;
    }

    public Object getModelData(Object queryInfo) throws Exception {
        return null;
    }

    public String getCurMessage() {
        return curMessage_;
    }

    public void setCurMessage(String curMessage) {
        curMessage_ = curMessage;
    }

    public ArrayList<DataTuple> getdBScores() {
        return dBScores_;
    }

    public void setdBScores(ArrayList<DataTuple> dBScores) {
        dBScores_ = dBScores;
    }

    public String getMaxScore() {
        return maxScore_;
    }

    public void setModQuestionIDs(List<String> modQuestionIDs) {
        modQuestionIDs_ =  modQuestionIDs;
    }

    //check that these two are in agreement
    public boolean checkModQuestionIDs() {
        boolean res = true;
        for (int i = 0; i < dBScores_.size(); i++) {
            DataTuple dt = dBScores_.get(i);
            String curQID = (String) dt.getFirst();
            if (!modQuestionIDs_.contains(curQID))
                res = false;
        }
        return res;
    }

    public String getSelectedQID() {
        return selectedQID_;
    }

    public void setSelectedQID(String selectedQID) {
        selectedQID_ = selectedQID;
    }

    public int getSelectedIndex() {
        return selectedIndex_;
    }

    public void setSelectedIndex(int selectedIndex) {
        selectedIndex_ = selectedIndex;
    }

    // sets the global variables selectedIndex_ and selectedQID_
    public void setSelectedQuestionInfo(int ind, String qid) {
        selectedIndex_ = ind;
        selectedQID_ = qid;
    }

    //check the selected question ans sequence. if terminal, reset
    public void checkSelectedState(String uID, String mID,
                                   DBAccessor db, Connection conn) throws Exception {
        DataTuple dt = null;
        dt = getDataTuple(selectedQID_);
        if (dt == null) {
            db.initModuleScore(uID, mID, selectedQID_, conn);
            // throw new Exception(" error in MotivationalModel.checkSelectedState: record not found: moduleID "+mID+" selectedQID "+selectedQID_);
        }
        // DM 6/1/10 fixed because the data tuple was null
        String ansSeq = null;
        if (dt != null)
            ansSeq = (String) dt.getThird();
        if (ansSeq != null && isTerminalSeq(ansSeq)) {
            //reset in db                                     
            db.resetModuleScore(uID, mID, selectedQID_, conn);
            resetDbDataTuple(dt);
        }
    }

    //creates and populates the scoreData_ array with DataTuples: qID, score
    // First, look up the dt by qID in the scores array that was loaded from the database.
    // Get all of the mod qIDs and scores. If no existing score, score=0.
    // add the dt to the array of scores for this module.
    // Also sets the global variable curMessage_ based on ansSeq and hints
    //
    public void createScoreData() throws Exception {
        scoreData_ = new ArrayList<DataTuple>();
        String curScore = "0";
        for (int i = 0; i < modQuestionIDs_.size(); i++) {
            DataTuple dt = new DataTuple();
            String curQID = (String) modQuestionIDs_.get(i);
            dt.setFirst(curQID);
            //dbData: qID, score, ansSeq, hints
            DataTuple dbData = getDataTuple(curQID);
            if (dbData == null)
                curScore = "0";
            else { //get score and message
                curScore = (String) dbData.getSecond();
                if (curQID.equals(selectedQID_))
                    //sets the global variable curMessage_ based on ansSeq and hints
                    assignMessage((String) dbData.getThird(), (String) dbData.getFourth());
            }
            dt.setSecond(curScore);
            scoreData_.add(dt);
        }
    }

    public void createScoreDataForSurveyEval() throws Exception {
        scoreData_ = new ArrayList<DataTuple>();
        String curScore = "0";
        for (int i = 0; i < modQuestionIDs_.size(); i++) {
            DataTuple dt = new DataTuple();
            String curQID = (String) modQuestionIDs_.get(i);
            dt.setFirst(curQID);
            //dbData: qID, score, ansSeq, hints
            DataTuple dbData = getDataTuple(curQID);
            if (dbData == null) {
                curScore = "0";
                if (curQID.equals(selectedQID_))
                    curMessage_= "Thanks for your input.  Please move on to another question.";
            }
            else { //get score and message
                curScore = (String) dbData.getSecond();
                if (curQID.equals(selectedQID_))
                    //sets the global variable curMessage_ based on ansSeq and hints
                    assignMessage((String) dbData.getThird(), (String) dbData.getFourth());
            }
            dt.setSecond(curScore);
            scoreData_.add(dt);
        }
    }

    //this mod has no data, set an initial record in the ModuleScore
    // table for each question in this module:
    //  userID, modID, questionID, score=0, ansSequence=NULL, hints=0
    public void initModuleScore(String uID, String mID,
                                DBAccessor db, Connection conn) throws Exception {
        for (int i = 0; i < modQuestionIDs_.size(); i++) {
            String qID = (String) modQuestionIDs_.get(i);
            db.initModuleScore(uID, mID, qID, conn);
        }
    }

    //mod has no data, set first three fields
    public void resetAllModuleScores(String uID, String mID,
                                     DBAccessor db, Connection conn) throws Exception {
        for (int i = 0; i < modQuestionIDs_.size(); i++) {
            String qID = (String) modQuestionIDs_.get(i);
            db.resetModuleScore(uID, mID, qID, conn);
        }
    }

    public void updateModuleScore(String uID, String mID, String qID,
                                  boolean correct,
                                  DBAccessor db, Connection conn) throws Exception {
        DataTuple dt = getDataTuple(qID);
        String curAnsSeq = (String) dt.getThird();
        String curHints = (String) dt.getFourth();
        if (curAnsSeq != null && isTerminalSeq(curAnsSeq))
            return;
        else {
            String newAnsSeq = getNewAnsSeq(curAnsSeq, correct);
            String score = getNewScore(newAnsSeq, curHints);
            db.updateModuleScoreAnsSeq(uID, mID, qID, newAnsSeq, score, conn);
        }
    }

    //dbData: qID, score, ansSeq, hints
    public void updateHints(String uID, String mID, String qID,
                            DBAccessor db, Connection conn) throws Exception {
        DataTuple dt = getDataTuple(qID);
        String curAnsSeq = (String) dt.getThird();
        if (curAnsSeq != null && isTerminalSeq(curAnsSeq))
            return;
        else {
            String curHintStr = (String) dt.getFourth();
            int curHint = Integer.parseInt(curHintStr);
            db.updateModuleScoreHintCount(uID, mID, qID, curHint + 1, conn);
        }
    }


    //dbData: qID, score, ansSeq, hints    DataTuple dbDt = getDataTuple(qID);
    public String getNewAnsSeq(String curAnsSeq, boolean correct) throws Exception {
        String newAnsSeq = null;
        if (curAnsSeq == null)
            if (correct)
                newAnsSeq = "1";
            else
                newAnsSeq = "0";
        else {
            if (isTerminalSeq(curAnsSeq)) {
                newAnsSeq = curAnsSeq;
            } else {
                if (correct)
                    newAnsSeq = curAnsSeq + "1";
                else
                    newAnsSeq = curAnsSeq + "0";
            }
        }
        return newAnsSeq;
    }

    //dbData: qID, score, ansSeq, hints
    // assume the ansSeq has been updated
    public String getNewScore(String curAnsSeq, String curHints) throws Exception {
        int index = getModelIndex(curAnsSeq + delim_ + getFilteredHintCount(curHints));
        return modelScores_[index];
    }

    public Iterator<DataTuple> getScoreDataIterator() throws Exception {
        return scoreData_.iterator();
    }

    public ArrayList<DataTuple> getScoreDataCopy() throws Exception {
        return scoreData_;
    }

    // sets the global variable curMessage_ based on the current state:
    // the sequence of incorrect answers and hints selection, i.e. 00Y
    private void assignMessage(String ansSeq, String hints) throws Exception {
        int index = getModelIndex(ansSeq + delim_ + getFilteredHintCount(hints));
        if (index > -1)
            curMessage_ = modelMsgs_[index];
    }

    private int getModelIndex(String key) {
        for (int i = 0; i < modelKeys_.length; i++) {
            if (key.equals(modelKeys_[i]))
                return i;
        }
        return -1;
    }

    //look up the dt by qID in the scores array that was loaded from the database.
    //dbData: qID, score, ansSeq, hints
    //  return null if not found
    private DataTuple getDataTuple(String qID) {
        DataTuple res = null;
        for (int i = 0; i < dBScores_.size(); i++) {
            DataTuple dt = dBScores_.get(i);
            String curQID = (String) dt.getFirst();
            if (qID.equals(curQID))
                res = dt;
        }
        return res;
    }

    private boolean isTerminalSeq(String seq) {
        boolean res = false;
        if (seq.equals("0000") ) {
            res = true;
        } else if (seq.contains("1")) {
            res = true;
        }
        return res;
    }

    private void resetDbDataTuple(DataTuple dt) {
        //dbData: qID, score, ansSeq, hints
        dt.setSecond("0");
        dt.setThird(null);
        dt.setFourth("0");
    }

    //filter hint count
    private String getFilteredHintCount(String curHints) {
        int hintCt = Integer.parseInt(curHints);
        int filteredHintCt = Math.min(hintCt, maxHintCount_);
        return Integer.toString(filteredHintCt);
    }
}
