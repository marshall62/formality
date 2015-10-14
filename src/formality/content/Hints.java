package formality.content;

import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * hints are stored as Hint objects in a 5x6 matrix,
 * where the rows are coaches and columns are the steps
 * empty cells are given a Hint object with a null ID field.
 *
 * @author not attributable
 * @version 1.0
 */
public class Hints {

    public static final int COACHES = 4;
    public static final int STEPS = 5;

    Hint[][] hintMatrix_;

    public Hints() {
        initHints();
    }

    private void initHints() {
        hintMatrix_ = new Hint[COACHES][STEPS];
        for (int i = 0; i <COACHES; i++)
          for (int j = 0; j <STEPS; j++)
              hintMatrix_[i][j] = new Hint();
    }

    public void addHint(Hint h) {
        int stepIndex =  h.getLevelAsInt()-1;
        int coachIndex = h.getCoachIndex()-1;
        hintMatrix_[coachIndex][stepIndex] = h;
    }

    //may return empty iterator
    public String[] getAllHintIDsByCoachID(String coachID) {
        String[] hintIDArray= new String[STEPS];
        int rowIndex= Integer.parseInt(coachID)-1;
        for (int i = 0; i < STEPS; i++) {
            Hint h = hintMatrix_[rowIndex][i];
            hintIDArray[i] = h.getID();
        }
        return hintIDArray;
    }

    //may return empty iterator
    public Hint[] getAllCoachHints(int coachID) {
        Hint[] hintArray= new Hint[STEPS];
        int rowIndex= coachID-1;
        for (int i = 0; i < STEPS; i++)
          hintArray[i] = hintMatrix_[rowIndex][i];
        return hintArray;
    }

    //may return null if hint does not exist in the matrix
    public Hint getHint(String hID){
        for (int i = 0; i <COACHES; i++)
          for (int j = 0; j <STEPS; j++){
              Hint h = hintMatrix_[i][j];
              String curID = h.getID();
              if(curID !=null && curID.equals(hID))
                  return h;
          }
        return null;
    }

    //may return null
    public Iterator<Hint> getAllStepHintsIter(String step){
        ArrayList<Hint> stepHints = new ArrayList<Hint>();
        int stepIndex=Integer.parseInt(step)-1;
        for (int i = 0; i <COACHES; i++) {
             stepHints.add(hintMatrix_[i][stepIndex]);
        }
        Iterator<Hint> hintIter= stepHints.iterator();
        return hintIter;
    }

        //return an iterator of all hints for coaches except the specified coach
        // may return null if no coaches at this step
    public Iterator<Hint> getStepHintsIterMinusCoach(int step, String coachID){
        Iterator<Hint> hintIter = null;
        ArrayList<Hint> stepHints = new ArrayList<Hint>();
        int stepIndex=step-1;
        for (int i = 0; i <COACHES; i++) {
           Hint h = hintMatrix_[i][stepIndex];
           String curID = h.getCoachID();
           if(curID!=null && !curID.equals(coachID))
               stepHints.add(h);
        }
        if(stepHints.size()>0)
          hintIter = stepHints.iterator();
        return hintIter;
    }

    //if the hint doesn't exist it will have a null id
    public Hint getHint(int coachID, int step){
        int coachIndex = coachID-1;
        int stepIndex =  step-1;
        Hint h = hintMatrix_[coachIndex][stepIndex];
        return h;
    }

    public String getHintResponse(String hID){
        Hint h = getHint(hID);
        // DM fixed bug because h sometimes comes back null
        if (h != null)
            return h.getResponse();
        else return "";
   }
}
