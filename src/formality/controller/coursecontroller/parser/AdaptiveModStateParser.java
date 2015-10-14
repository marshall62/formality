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
public class AdaptiveModStateParser implements StateParser{

     protected static final String theDelims=".,?\":; \n\t\r";
     String stateStr=null;
     int modIndex=-1;
    Random r;
    /* the state is initially null
       then, set it to pt 1 where the number is the index of the pt mod (chosen at random)
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

      public void preTestComplete(){
      if(stateStr.equals("pt"))
           //pt completed, so the new state will be practice set 1,
          // followed by the set 1 mods
           stateStr="p1";
    }

    public void p1Complete(){
      if(stateStr.equals("p1")|| stateStr.equals("pt"))
           //p1 completed, so the new state will be s1 (show the test option button)
          // followed by the set 1 mods
          stateStr="s1";
    }
   public void p2Complete(){
      if(stateStr.equals("p1") || stateStr.equals("p2"))
           //p1 completed, so the new state will be s1 (show the test option button)
          // followed by the set 1 mods
          stateStr="s2";
    }
    public void setP1(){stateStr="p1";}
    public void setP2(){stateStr="p2";}
    public void setS1(){stateStr="s1";}
    public void setS2(){stateStr="s2";}
    public void setS3(){stateStr="s3";}
    public void setT1(){stateStr="t1";}
    public void setT2(){stateStr="t2";}
    //t3 is the post test
    public void setT3(){stateStr="t3";}
    public void setFin(){stateStr="fin";}
    public String getState(){
       //StringBuffer st=new StringBuffer();
       return stateStr;
    }
      public boolean isTestState(){
       if(stateStr.equals("pt")|| stateStr.equals("t1")||
            stateStr.equals("t2") || stateStr.equals("t3"))
         return true;
      else
        return false;
     }

     public boolean showP2(){
       if(stateStr.equals("p2")|| stateStr.equals("s2")||
            stateStr.equals("s3")||
            stateStr.equals("fin"))
         return true;
       else
         return false;
      }

    public boolean showT1(){
         return showP2();
      }

    public boolean showT2(){
       if(stateStr.equals("s3")|| stateStr.equals("fin"))
         return true;
       else
         return false;
      }
      public boolean isPreTest(){
        return (stateStr!=null && stateStr.equals("pt"));
       }
      public boolean isTest1(){
        return (stateStr!=null && stateStr.equals("t1"));
       }
       public boolean isTest2(){
        return (stateStr!=null && stateStr.equals("t2"));
       }
       public boolean isTest3(){
        return (stateStr!=null && stateStr.equals("t3"));
       }
       public boolean isSelect1(){
        return (stateStr!=null && stateStr.equals("s1"));
       }
       public boolean isSelect2(){
        return (stateStr!=null && stateStr.equals("s2"));
       }
        public boolean isSelect3(){
        return (stateStr!=null && stateStr.equals("s3"));
       }
       public boolean isP1(){
        return (stateStr!=null && stateStr.equals("p1"));
       }
       public boolean isP2(){
        return (stateStr!=null && stateStr.equals("p2"));
       }
       public boolean isPostTest(){
        return (stateStr!=null && stateStr.equals("t3"));
       }
       public boolean isFin(){
        return (stateStr!=null && stateStr.equals("fin"));
       }
       public String getDBStateStr(){
             return stateStr;//+" "+Integer.toString(modIndex);
       }

    public String getStateStr(){
             return stateStr;
       }

    public int getModIndex() {
        return modIndex;
    }

    //*************   now we have 2 pt and pst mods  *****************
    private int generateModIndex(){
       r=new Random();
       int temp=r.nextInt(100);
       if(temp<50)
         return 0;
        else
         return 1;
    }

    //assume 2 mods in the vector
    public int chooseModIndex(Vector mods){
       int sz = mods.size();
       int index = -1;
       if(sz==1){
         index=0;
       }
       else if(sz>1){
         index = generateModIndex();
       }
      return index;
    }



   // public void parseState(String state)throws Exception;
    public void setPt(){}
    public void setP(){}
    public void setS(){}
    public void setPst(){}

   // public String getState();
   // public boolean isPreTest();
    public boolean showPandTMods(){return false;}
    public boolean showSelect(){return false;}
    public boolean showPostTest(){return (stateStr!=null && stateStr.equals("p2"));}

    //public String getStateStr(){return null;}
   // public String getDBStateStr(){return null;}
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