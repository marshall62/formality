package formality.content;

import java.util.TreeMap;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 */
public class MultipleChoiceQuestion extends MultiChoiceSurveyQuestion {

    String answerChoiceAFeedback_ = null;
    String answerChoiceBFeedback_ = null;
    String answerChoiceCFeedback_ = null;
    String answerChoiceDFeedback_ = null;

    public MultipleChoiceQuestion() {
        super();
        setSurvey(false);
    }

    public MultipleChoiceQuestion(String id) {
        super(id);
    }

    public MultipleChoiceQuestion(Question q) {
        super(q);
        setSurvey(false);

    }


    public boolean isCorrectAnswer(String a) {
        if (correctAnswer_ == null)
            return false;
        if (correctAnswer_.equalsIgnoreCase(a))
            return true;
        else
            return false;
    }


    public void setAnsChoiceAFeedback(String a) {
        answerChoiceAFeedback_ = a;
    }

    public void setAnsChoiceBFeedback(String b) {
        answerChoiceBFeedback_ = b;
    }

    public void setAnsChoiceCFeedback(String c) {
        answerChoiceCFeedback_ = c;
    }

    public void setAnsChoiceDFeedback(String d) {
        answerChoiceDFeedback_ = d;
    }


    public String getAnsChoiceAFeedback() {
        return answerChoiceAFeedback_;
    }

    public String getAnsChoiceBFeedback() {
        return answerChoiceBFeedback_;
    }

    public String getAnsChoiceCFeedback() {
        return answerChoiceCFeedback_;
    }

    public String getAnsChoiceDFeedback() {
        return answerChoiceDFeedback_;
    }

    public boolean areAnswersLoaded() {
        boolean loaded =
                correctAnswer_ == null ||
                        answerChoiceA_ == null ||
                        answerChoiceAFeedback_ == null ||
                        answerChoiceB_ == null ||
                        answerChoiceBFeedback_ == null ||
                        answerChoiceC_ == null ||
                        answerChoiceCFeedback_ == null ||
                        answerChoiceD_ == null ||
                        answerChoiceDFeedback_ == null;
        return !loaded;
    }

}
