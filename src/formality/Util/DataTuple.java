package formality.Util;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Aug 21, 2005
 * Time: 10:36:30 PM
 * To change this template use File | Settings | File Templates.
 */

//holds three data now
public class DataTuple {

    Object first;
    Object second;
    Object third;
    Object fourth;
    Object fifth;
    Object sixth;



   public DataTuple(){
          first=null;
          second=null;
          third=null;
          fourth=null;
          fifth=null;
          sixth=null;
      }
    public Object getFirst() {
        return first;
    }

    public void setFirst(Object f) {
       first = f;
    }

    public Object getSecond() {
        return second;
    }

    public void setSecond(Object s) {
        second = s;
    }

    public Object getThird() {
        return third;
    }

    public void setThird(Object t) {
        third = t;
    }
    
    public Object getFourth() {
        return fourth;
    }

    public void setFourth(Object f) {
        fourth = f;
    }

    public Object getFifth() {
        return fifth;
    }

    public void setFifth(Object f) {
        fifth = f;
    }

    public Object getSixth() {
        return sixth;
    }

    public void setSixth(Object s) {
        sixth = s;
    }

    public boolean hasFirst(){
        if(first!=null)
            return true;
        else
            return false;
    }
    public boolean hasSecond(){
        if(second!=null)
            return true;
        else
            return false;
    }
    public boolean hasThird(){
        if(third!=null)
            return true;
        else
            return false;
    }
    public boolean hasFourth(){
        if(fourth!=null)
            return true;
        else
            return false;
    }
    public boolean hasFifth(){
        if(fifth!=null)
            return true;
        else
            return false;
    }
    public boolean hasSixth(){
        if(sixth!=null)
            return true;
        else
            return false;
    }
}
