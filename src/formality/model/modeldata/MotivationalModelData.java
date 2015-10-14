package formality.model.modeldata;

import formality.Util.DataTuple;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 */
public class MotivationalModelData extends BaseModelData{

    ArrayList<DataTuple> scoreData_ = null;
    String maxScore_;
    int selectedIndex_;

    String curMessage_=null;

    public String getCurMessage() {
        return curMessage_;
    }

    public void setCurMessage(String curMessage_) {
        this.curMessage_ = curMessage_;
    }

   public Iterator<DataTuple> getScoreDataIterator() throws Exception {
         return scoreData_.iterator();
   }

   public ArrayList<DataTuple> getScoreDataCopy() throws Exception {
        return scoreData_;
   }

    public void setScoreData(ArrayList<DataTuple> scoreData_) {
        this.scoreData_ = scoreData_;
    }

    public int getSelectedIndex(){
        return selectedIndex_;
    }

    public String getMaxScore(){
        return maxScore_;
    }

    public void setMaxScore(String max){
        maxScore_ = max;
    }
}
