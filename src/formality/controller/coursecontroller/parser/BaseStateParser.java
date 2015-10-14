package formality.controller.coursecontroller.parser;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Jan 8, 2006
 * Time: 11:24:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseStateParser implements StateParser{

     protected static final String theDelims=".,?\":; \n\t\r";
     String stateStr=null;
     int modIndex=-1;
    Random r;
    /* the state is initially null
       then, set it to pt

       pt- pretest
       p- practice and test
       s- select the post test (if exists)
       pst- posttest
       fin- done
    */
    // Vector set1Mods = new Vector();
    // String test1ModID = null;
    // Vector set2Mods = new Vector();
    // String test2ModID = null;
   //  String[] toks = {null, null, null, null, null, null, null};
     //assume the pattern: stateStr 1,2,3,4,5,6
     // 2 practice, then 1 test, then 2 practice and 1 test

     public void parseState(String state)throws Exception{
      if(state==null || state.equals("")){
          stateStr="pt";
       }
      else
        stateStr=state.trim();
    }

    public void setPt(){stateStr="pt";}
    public void setP(){stateStr="p";}
    public void setS(){stateStr="s";}
    public void setPst(){stateStr="pst";}
    public void setFin(){stateStr="fin";}

    public String getState(){
       //StringBuffer st=new StringBuffer();
       return stateStr;
    }

      public boolean isPreTest(){
        return (stateStr!=null && stateStr.equals("pt"));
       }

       public boolean showPandTMods(){
        return (stateStr.equals("p") || stateStr.equals("s")
                || stateStr.equals("fin"));
       }
       public boolean showSelect(){
        return (stateStr!=null && stateStr.equals("s"));
       }
       public boolean showPostTest(){
        return (stateStr!=null && stateStr.equals("pst"));
       }

       public boolean isFin(){
        return (stateStr!=null && stateStr.equals("fin"));
       }



    public String getStateStr(){
             return stateStr;
       }

     public String getDBStateStr(){
             return stateStr;//+" "+Integer.toString(modIndex);
       }
    public int chooseModIndex(Vector mods){return 0;}
    public boolean isTestState(){return false;}

    public boolean isSelect1(){return false;}
    public boolean showT1(){return false;}
    public boolean showP2(){return false;}
    public boolean isSelect2(){return false;}
    public boolean showT2(){return false;}
    public boolean isSelect3(){return false;}
    public boolean isPostTest(){return false;}
    public boolean isTest2(){return false;}
    public boolean isTest1(){return false;}
/*
   private void parseStateStr(String input) throws Exception{
     StringTokenizer tokens =new StringTokenizer(input, theDelims);
     int i=0;
     while (tokens.hasMoreTokens()){
        if(i>2)
          throw new Exception("Error in parseState- #tokens>index.");
        String nextTok=tokens.nextToken();
        if(i==0)
          stateStr=nextTok;
        else
          modIndex=Integer.parseInt(nextTok);
        i++;
      }
  }
  */
}