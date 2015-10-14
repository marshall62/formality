package formality.content;

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
public class AnswerChoice {
    public static final String multipleChoiceType = "mc";
    public static final String shortAnswerType = "sa";
    String type_;//mc or sa
    String text_;
    String feedback_;
    boolean isCorrect_;

    public AnswerChoice(String type) {
        type_=type;
        isCorrect_=false;
    }

    public String getType(){
        return type_;
    }

    public void setText(String t){
        text_=t;
    }

    public String getText(){
        return text_;
    }

    public void setFeedback(String f){
        feedback_=f;
    }

    public String getFeedback(){
        return feedback_;
    }

    public void setCorrect(){
        isCorrect_=true;
    }

    public boolean isCorrect(){
        return isCorrect_;
    }
}
