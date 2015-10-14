package formality.model;

import formality.content.Question;
import formality.content.SystemInfo;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DBAccessor;
import formality.content.database.ReportAccessor;
import formality.content.database.ReportAccessor;
import formality.Util.DataTuple;
import formality.model.modeldata.BetaStudentModelData;
import cern.jet.stat.Gamma;

import java.sql.Connection;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Jan 29, 2006
 * Time: 9:42:01 PM
 * <p/>
 * CURRENT SETTINGS:
 * prior weighting:  a+b=3
 */
public class BetaStudentModel implements ModelListener {

    DBAccessor dbAccessor_;
    ReportAccessor reptAccessor_;
    Connection conn_;
    double[] skillVals_;
    double[] alphaVals_;
    double[] betaVals_;
    //supported queries
    public static final String meanModelQuery_ = "meanModel";

    public static final double standardsPassLevel_ = 0.5;

    //expect initItems: DBAccessor dbA, AdaptiveModuleAccessor atA, Connection conn
    public void init(Object[] initItems) throws Exception {
        dbAccessor_ = (DBAccessor) initItems[0];
        reptAccessor_ = (ReportAccessor) initItems[1];
        conn_ = (Connection) initItems[2];
    }

    public void updateModel(ModelEvent e) throws Exception {
        ModelEvent.EventTypes eventType = e.getType();
        String sID = e.getUserID();
        String qID = e.getItemID();
        String mID = e.getContentID();
        String[] qInfo = e.getContentInfo();
        int ansOrdinality = e.getAnswerOrdinality();
        boolean correct = e.isCorrect();
        switch (eventType) {
            case evaluateQuestion:
                updateStudentModel(sID, qID, mID, qInfo, ansOrdinality, correct, conn_);
                break;

            case evaluateTestQuestion:
                updateStudentModel(sID, qID, mID, qInfo, ansOrdinality, correct, conn_);
                break;
        }
    }

    /* now update the student model-
      For ONLY the first skill
       1. get the current alpha, beta values
       2. get the updated alpha, beta values based on the user actions (hints, etc)
       3. calculate the new skill value with the new alpha, beta.
       4. store the new values
     // @argument ans 0= test mode, 1= first ans, 2= second ans
        */
    public void updateStudentModel(String sID, String qID, String mID, String[] qInfo, int ordinality,
                                   boolean correct, Connection conn) throws Exception {
        String[] skills = new String[3];
        skills[0] = qInfo[0];
        skills[1] = qInfo[1];
        skills[2] = qInfo[2];
        skillVals_ = dbAccessor_.getStudentSkillData(sID, DBAccessor.standardsDBNames_.length, conn);
        alphaVals_ = dbAccessor_.getStudentAlphaData(sID, DBAccessor.standardsDBNames_.length, conn);
        betaVals_ = dbAccessor_.getStudentBetaData(sID, DBAccessor.standardsDBNames_.length, conn);
        String curSkillName = null;
        // As of 11/2010 we only want to update the first skill associated with the problem.
        // This is a time-saving effort so that authors do not need to go and remove all but the first skill
        // from each question.   Their desire is for a problem to only cover one skill.
        if (skills.length > 0) {
            curSkillName = skills[0];
            if (curSkillName != null && curSkillName.length() > 4) {
                int index = getSkillIndex(curSkillName);
                double skillValue = skillVals_[index];
                double alpha = alphaVals_[index];
                double beta = betaVals_[index];
                // the return:  int qData[0] timesAnswered, int qData[1] numHintsBeforeTheLatestAnswer, int qData[2] 1=correct, 0=incorrect
                int[] qData = getCurQData(sID, mID, qID, conn);
                // newParams[0]=newAlpha,  newParams[1]=newBeta;
                // don't update skills if the problem has already been solved correctly and this
                // is another correct attempt
                if (!(qData[2] == 1 && correct)) {
                    double[] newParams = calculateNewParameterValues(alpha, beta, qData[0], qData[1],
                            correct, conn);
                    double newSkillVal = calculateNewSkillValue(skillValue, newParams[0], newParams[1]);
                    skillVals_[index] = newSkillVal;
                    alphaVals_[index] = newParams[0];
                    betaVals_[index] = newParams[1];
                    updateStudentModelValues(sID, skillVals_, alphaVals_, betaVals_, conn);
                }
            }
        }

    }

    /* now update the student model-
      for each skill(s):
       1. get the current alpha, beta values
       2. get the updated alpha, beta values based on the user actions (hints, etc)
       3. calculate the new skill value with the new alpha, beta.
       4. store the new values
     // @argument ans 0= test mode, 1= first ans, 2= second ans
        */
    public void updateStudentModelOld(String sID, String qID, String mID, String[] qInfo, int ordinality,
                                      boolean correct, Connection conn) throws Exception {
        String[] skills = new String[3];
        skills[0] = qInfo[0];
        skills[1] = qInfo[1];
        skills[2] = qInfo[2];
        skillVals_ = dbAccessor_.getStudentSkillData(sID, DBAccessor.standardsDBNames_.length, conn);
        alphaVals_ = dbAccessor_.getStudentAlphaData(sID, DBAccessor.standardsDBNames_.length, conn);
        betaVals_ = dbAccessor_.getStudentBetaData(sID, DBAccessor.standardsDBNames_.length, conn);
        String curSkillName = null;
        for (int i = 0; i < skills.length; i++) {
            curSkillName = skills[i];
            if (curSkillName != null && curSkillName.length() > 4) {
                int index = getSkillIndex(curSkillName);
                double skillValue = skillVals_[index];
                double alpha = alphaVals_[index];
                double beta = betaVals_[index];
                // the return:  int qData[0] timesAnswered, int qData[1] numHintsBeforeTheLatestAnswer, int qData[2] 1=correct, 0=incorrect
                int[] qData = getCurQData(sID, mID, qID, conn);
                // newParams[0]=newAlpha,  newParams[1]=newBeta;
                // don't update skills if the problem has already been solved correctly and this
                // is another correct attempt
                double[] newParams = calculateNewParameterValues(alpha, beta, qData[0], qData[1],
                        correct, conn);
                double newSkillVal = calculateNewSkillValue(skillValue, newParams[0], newParams[1]);
                skillVals_[index] = newSkillVal;
                alphaVals_[index] = newParams[0];
                betaVals_[index] = newParams[1];
            }
        }
        updateStudentModelValues(sID, skillVals_, alphaVals_, betaVals_, conn);
    }


    // the return:  int[0] timesAnswered, int[1] numHintsBeforeTheLatestAnswer, int[2] 1=correct, 0=incorrect
    public int[] getCurQData(String sID,
                             String mID,
                             String qID,
                             Connection conn) throws Exception {
        int timesAnswered = 0;
        int numHintsBeforeTheLatestAnswer = 0;
        int correct = 0;
        // the false arg means the lists are ordered from earliest to latest event times
        ArrayList<DataTuple> responseData = reptAccessor_.getQuestionResponseTimesAndCorrect(sID, mID, qID, false, conn);
        ArrayList<String> hintTimes = reptAccessor_.getHintResponseTimes(sID, mID, qID, false, conn);
        timesAnswered = responseData.size();
        DataTuple dt = responseData.get(0);
        String curRespTS = (String) dt.getFirst();
        // are any of the previous responses correct?
        // NOte we dont look at the last tuple which is the current response.
        for (int i=0;i<responseData.size()-1;i++)
            if ((Boolean) responseData.get(i).getSecond())
                correct = 1;
        for (int j = 0; j < hintTimes.size(); j++) {
            String curHintTS = hintTimes.get(j);
            if (curRespTS.compareTo(curHintTS) > 1)
                numHintsBeforeTheLatestAnswer++;
        }
        int[] qData = {timesAnswered, numHintsBeforeTheLatestAnswer, correct};
        return qData;
    }

    // the return:  newParams[0]=newAlpha,  newParams[1]=newBeta;
    private double[] calculateNewParameterValues(double curAlpha,
                                                 double curBeta,
                                                 int timesAnswered,
                                                 int numHintsBeforeThisAnswer,
                                                 boolean correct,
                                                 Connection conn) throws Exception {
        double newAlpha = 0.0, newBeta = 0.0;
        double W1 = Math.max(0.0, (1.0 - (0.25 * timesAnswered)));
        double W2 = 1.0 - (0.2 * numHintsBeforeThisAnswer);
        if (correct) {
            newAlpha = curAlpha + (W1 * W2);
            newBeta = curBeta;
        } else {
            newBeta = curBeta + (W1 * W2);
            newAlpha = curAlpha;
        }
        double[] newParams = new double[2];
        newParams[0] = newAlpha;
        newParams[1] = newBeta;
        return newParams;
    }

    /*the array order:
    4N1,4N2,4N3,4N4,4N5,4N6,4N7,4N8,4N9,4N10,4N11,4N12,4N13,4N14,4N15,4N16,4N17,4N18,4P1,
    4P2,4P3,4P4,4P5,4P6,4G1,4G2,4G3,4G4,4G5,4G6,4G7,4G8,4G9,4M1,4M2,4M3,4M4,4M5,4D1,4D2,
    4D3,4D4,4D5,4D6
    */
    private int getSkillIndex(String skill) throws Exception {
        int index = -1;
        for (int i = 0; i < DBAccessor.standardsNames_.length; i++) {
            if (DBAccessor.standardsNames_[i].equals(skill))
                index = i;
        }
        return index;
    }

    private double calculateNewSkillValue(double theta, double alpha, double beta) throws Exception {
        double newSkillVal = 0.0;
        /* the formula is:
           1/B(a,b) * theta^alpha-1 * (1-theta)^beta-1
           the mean is alpha/(alpha+beta)
        */
        double a = alpha - 1;
        double b = beta - 1;
        try {
            double B = Gamma.beta(alpha, beta);
        } catch (Exception ex) {
            throw new Exception(" error calculating Gamma dist for new skill value, params: alpha " + a + " beta " + b);
        }
        double term1 = Math.pow(theta, a);
        double term2 = Math.pow((1 - theta), b);
        newSkillVal = alpha / (alpha + beta);//(term1*term2)/B;
        return newSkillVal;
    }

    private void updateStudentModelValues(String sID, double[] skillVals, double[] alphaVals,
                                          double[] betaVals, Connection conn) throws Exception {
        dbAccessor_.updateStudentModelData(sID, DBAccessor.standardsDBNames_, skillVals, conn);
        dbAccessor_.updateStudentModelData(sID, DBAccessor.alphaDBNames_, alphaVals, conn);
        dbAccessor_.updateStudentModelData(sID, DBAccessor.betaDBNames_, betaVals, conn);
    }

    public Object getModelData() throws Exception {
        BetaStudentModelData data = new BetaStudentModelData();
        try {
            if (skillVals_ == null)
                skillVals_ = dbAccessor_.getMeanModelData(DBAccessor.standardsDBNames_.length, conn_);
            data.setSkillValues(skillVals_);
        } catch (Exception ex) {
            data.setErrorStr(ex.getMessage());
        }
        return data;
    }

    public Object getModelData(Object queryInfo) {
        BetaStudentModelData data = new BetaStudentModelData();
        if (queryInfo instanceof String) {
            if (queryInfo.equals(meanModelQuery_)) {
                try {
                    double[] mm = getMeanModel();
                    data.setSkillValues(mm);
                } catch (Exception ex) {
                    data.setErrorStr("error getting mean model data " + ex.getMessage());
                }
            } else {
                data.setErrorStr("error getting mean data: unrecognized query.");
            }
        } else
            data.setErrorStr("error getting model data: query was not a String.");
        return data;
    }

    public double[] getMeanModel() throws Exception {
        double[] mm = dbAccessor_.getMeanModelData(DBAccessor.standardsDBNames_.length, conn_);
        return mm;
    }

}
