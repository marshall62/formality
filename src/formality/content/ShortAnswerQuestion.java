package formality.content;

import formality.html.GeneralHtml;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Jul 23, 2005
 * Time: 9:59:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShortAnswerQuestion extends Question {
    List<String> answers;
    List<Integer> ansIds;

    public ShortAnswerQuestion() {
        super();
        this.setType("sa");
    }

    public ShortAnswerQuestion (Map params) throws Exception {
        this();
        this.setAuthor(GeneralHtml.getParamStr(params, GeneralHtml.author_, ""));
        this.setSource(GeneralHtml.getParamStr(params, GeneralHtml.source_, ""));        
        this.setStem(GeneralHtml.getParamStr(params, GeneralHtml.stem_, ""));
    }

    public ShortAnswerQuestion (Question q) {
        super(q);
    }

    public ShortAnswerQuestion(String id) {
       super(id);
       this.setType("sa");
    }

   public void setAnswers (List<String> answers) {
       this.answers = answers;
   }

    public String getCorrectAnswer () {
        StringBuffer sb = new StringBuffer();
        for (String ans: this.answers)
            sb.append(ans + ",");
        return sb.toString();
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswerIds (List<Integer> ids) {
        ansIds = ids;
    }

    public boolean areAnswersLoaded(){
        return true;
   }

    public void addAnswerId(int k) {
        if (ansIds != null)
            ansIds.add(k);
    }

    // Its got to be an exact match (ignoring case) to one of the provided answers
    public boolean gradeAnswer(String subAns) {
        for (String a: answers)
            if (subAns.equalsIgnoreCase(a))
                return true;
        return false;
    }

    public boolean hasAnswers () {
        for (String a: answers)
            if (!a.equals(""))
                return true;
        return false;
    }
}
