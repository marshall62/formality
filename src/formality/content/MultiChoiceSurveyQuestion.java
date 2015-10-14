package formality.content;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 15, 2010
 * Time: 2:56:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultiChoiceSurveyQuestion extends Question {
    String answerChoiceA_=null;
    String answerChoiceB_=null;
    String answerChoiceC_=null;
    String answerChoiceD_=null;
    String studAns=null;

    public MultiChoiceSurveyQuestion() {
        super();
        this.setType("mc");
        this.setSurvey(true);
    }

    public MultiChoiceSurveyQuestion(String id) {
       super(id);
       this.setType("mc");
        this.setSurvey(true);
    }

    public MultiChoiceSurveyQuestion(Question q){
      super(q.getID());
      this.setType("mc");
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
      this.setSurvey(true);
      this.setSequentialLayout(q.isSequentialLayout());
    }

    public String getAnswerChoiceA() {
        return answerChoiceA_;
    }

    public void setAnswerChoiceA(String answerChoiceA_) {
        this.answerChoiceA_ = answerChoiceA_;
    }

    public String getAnswerChoiceB() {
        return answerChoiceB_;
    }

    public void setAnswerChoiceB(String answerChoiceB_) {
        this.answerChoiceB_ = answerChoiceB_;
    }

    public String getAnswerChoiceC() {
        return answerChoiceC_;
    }

    public void setAnswerChoiceC(String answerChoiceC_) {
        this.answerChoiceC_ = answerChoiceC_;
    }

    public String getAnswerChoiceD() {
        return answerChoiceD_;
    }

    public void setAnswerChoiceD(String answerChoiceD_) {
        this.answerChoiceD_ = answerChoiceD_;
    }

   public boolean areAnswersLoaded(){
    return
            !(correctAnswer_==null ||
                    answerChoiceA_==null||
                    answerChoiceB_==null||
                    answerChoiceC_==null||
                    answerChoiceD_==null);
   }

    public String getStudAns() {
        return studAns;
    }

    public void setStudAns(String studAns) {
        this.studAns = studAns;
    }
}
