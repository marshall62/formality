package formality.reports;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Nov 3, 2010
 * Time: 12:08:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuestionPerformanceData {
    private List<String> standardSkills; // something like 4.n.3
    private boolean isCorrect;
    private int hintsBeforeFirstAttempt;
    private int hintsBeforeCorrectAttempt;
    private int totalHintsSeen;
    private boolean sawQuestion;
    private int numAttemptsForCorrect; // this will always >= 0.   Must look at isCorrect to know if problem was solved correctly

    public QuestionPerformanceData(List<String> standardSkills, boolean correct, int hintsBeforeFirstAttempt, int hintsBeforeCorrectAttempt, int totalHintsSeen, boolean sawQuestion, int numAttemptsForCorrect) {
        this.standardSkills = standardSkills;
        isCorrect = correct;
        this.hintsBeforeFirstAttempt = hintsBeforeFirstAttempt;
        this.hintsBeforeCorrectAttempt = hintsBeforeCorrectAttempt;
        this.totalHintsSeen = totalHintsSeen;
        this.sawQuestion = sawQuestion;
        this.numAttemptsForCorrect=numAttemptsForCorrect;
    }

    public List<String> getStandardSkills() {
        return standardSkills;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public int getHintsBeforeFirstAttempt() {
        return hintsBeforeFirstAttempt;
    }

    public int getHintsBeforeCorrectAttempt() {
        return hintsBeforeCorrectAttempt;
    }

    public int getTotalHintsSeen() {
        return totalHintsSeen;
    }

    public boolean isSawQuestion() {
        return sawQuestion;
    }

    public int getNumAttemptsForCorrect() {
        return numAttemptsForCorrect;
    }
}
