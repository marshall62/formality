package formality.content;

import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * @author not attributable
 * @version 1.0
 */
public class Hint implements Comparable{

    String hintID_;
    String level_;
    String coachID_;
    String coachImg_;
    String coachName_;
    String tag_;
    String query_;
    String response_;
    String strategyID_;
    String ansChoiceLink_;
    List<String> questionLinks_;
    String author_;

    public Hint (String id, String lev, String coachID, String strategyID, String ansLink, String query, String response, String authID) {
        this.hintID_ = id;
        this.level_ = lev;
        this.coachID_ = coachID;
        this.strategyID_ = strategyID;
        this.ansChoiceLink_ = ansLink;
        this.query_ = query;
        this.response_ = response;
        this.author_ = authID;
    }

    public Hint() {
        hintID_=null;
        questionLinks_=new Vector();
    }
    public Hint(String id) {
        hintID_=id;
        questionLinks_=new Vector();
    }

    public String getID(){
        return hintID_;
    }

    public String getAuthor() {
        return author_;
    }

    public void setAuthor(String a) {
        this.author_ = a;
    }

    public void addQuestionLink(String qID){
        questionLinks_.add(qID);
    }

    public void setQuestionLinks(List<String> ql){
        questionLinks_=ql;
    }

    public Iterator getQuestionLinks(){
        return questionLinks_.iterator();
    }

    public void setAnsChoiceLink(String t){
        ansChoiceLink_=t;
    }

    public String getAnsChoiceLink(){
        return ansChoiceLink_;
    }

    public void setTag(String t){
        tag_=t;
    }

    public String getTag(){
        return tag_;
    }

    public void setCoachImg(String t){
        coachImg_=t;
    }

    public String getCoachImg(){
        return coachImg_;
    }

    public void setCoachName(String t){
        coachName_=t;
    }

    public String getCoachName(){
        return coachName_;
    }

    public void setQuery(String q){
        query_=q;
    }

    public String getQuery(){
        return query_;
    }

    public void setResponse(String r){
        response_=r;
    }

    public String getResponse(){
        return response_;
    }

    public String getLevel(){
        return level_;
    }

   public int getLevelAsInt(){
        return Integer.parseInt(level_);
    }

    public void setLevel(String l){
        level_=l;
    }

    public String getStrategyID(){
        return strategyID_;
    }

    public void setStrategyID(String s){
        if (s.equals(""))       // DM added this because an empty selection in hint author fails on db insert with empty string so defaults to 1
            strategyID_ = "1";
        else strategyID_=s;
    }
    public String getCoachID(){
        return coachID_;
    }

   public int getCoachIndex(){
        return Integer.parseInt(coachID_);
   }

    public void setCoachID(String c){
        coachID_=c;
    }

    public static String getHintTag(String level, String coach){
        return "{h"+level+coach+"}";
    }

    public int compareTo(Object o){
        int res=0;
        Hint other = (Hint)o;
        int myID = Integer.parseInt(this.coachID_);
        int otherID = Integer.parseInt(other.coachID_);
        if(myID>otherID)
           res=1;
        else if(myID<otherID)
           res=-1;
        return res;
    }
}
