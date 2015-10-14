package formality.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the information required by a user model
 * upon the occurrance of a significant event.
 */
public class ModelEvent {

    long timeStampMillis_;
    String dateStr_ = null;
    EventTypes type_ = null;
    String itemID_ = null;
    String contentID_ = null;
    String userID_ = null;
    String altUserID_ = null;
    String response_ = null;
    String hintID_ = null;
    String coachID_=null;
    String level_ = null;
    int qIndex_;
    int itemOrdinality_;
    int answerOrdinality_;
    boolean correct_ = false;
    String[] contentInfoStrArray_ = null;
    List contentInfoArray_ = null;

    //Event types
    public enum EventTypes {
    studentExam,
    studentHome,
    viewModule,
    viewTestModule,
    viewQuestion,
    viewTestQuestion,
    evaluateQuestion,
    evaluateSurveyQuestion,
    evaluateTestQuestion,
    clearQuestion,
    clearTestQuestion,
    viewHint,
    selectHintLevel,
    noResponse
    }

    public ModelEvent(EventTypes type){
        type_ = type;
    }

    public long getTimeStampMillis() {
        return timeStampMillis_;
    }

    public void setTimeStampMillis(long timeStampMillis_) {
        this.timeStampMillis_ = timeStampMillis_;
    }

    public String getDateStr() {
        return dateStr_;
    }

    public void setDateStr(String dateStr_) {
        this.dateStr_ = dateStr_;
    }

    public EventTypes getType() {
        return type_;
    }

    public String getItemID() {
        return itemID_;
    }

    public void setItemID(String itemID_) {
        this.itemID_ = itemID_;
    }

    public String getUserID() {
        return userID_;
    }

    public void setUserID(String userID_) {
        this.userID_ = userID_;
    }

    public String getAltUserID() {
        return altUserID_;
    }

    public void setAltUserID(String altUserID_) {
        this.altUserID_ = altUserID_;
    }

    public String getResponse() {
        return response_;
    }

    public void setResponse(String response_) {
        this.response_ = response_;
    }

    public String getHintID() {
        return hintID_;
    }

    public void setHintID(String hintID_) {
        this.hintID_ = hintID_;
    }

    public String getContentID() {
        return contentID_;
    }

    public void setContentID(String contentID_) {
        this.contentID_ = contentID_;
    }

    public String getLevel() {
        return level_;
    }

    public void setLevel(String level_) {
        this.level_ = level_;
    }

    public String[] getContentInfo() {
        return contentInfoStrArray_;
    }

    public void setContentInfo(String[] contentInfo_) {
        this.contentInfoStrArray_ = contentInfo_;
    }

    public List getContentInfoArray() {
        return contentInfoArray_;
    }

    public void setContentInfoArray(List contentInfoArray_) {
        this.contentInfoArray_ = contentInfoArray_;
    }

    public boolean isCorrect() {
        return correct_;
    }

    public void setCorrect(boolean correct_) {
        this.correct_ = correct_;
    }

    public int getItemOrdinality() {
        return itemOrdinality_;
    }

    public void setItemOrdinality(int itemOrdinality_) {
        this.itemOrdinality_ = itemOrdinality_;
    }

    public int getAnswerOrdinality() {
        return answerOrdinality_;
    }

    public void setAnswerOrdinality(int answerOrdinality_) {
        this.answerOrdinality_ = answerOrdinality_;
    }

    public int getqIndex() {
        return qIndex_;
    }

    public void setqIndex(int qIndex_) {
        this.qIndex_ = qIndex_;
    }

    public String getCoachID() {
        return coachID_;
    }

    public void setCoachID(String coachID_) {
        this.coachID_ = coachID_;
    }
}
