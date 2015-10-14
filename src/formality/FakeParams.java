package formality;
import formality.controller.*;
import java.util.TreeMap;
import formality.html.GeneralHtml;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
// public static final String moduleID_ = "mID";
// public static final String hintID_ = "hID";
// public static final String readyStatus_ = "ready";
 // public static final String level_ = "level";
public class FakeParams {
    public FakeParams() {
    }

    public TreeMap getFakeLoginParams(){
        TreeMap p = new TreeMap();
    //  params.put(FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam);
        p.put(FormalitySubsystem.fxnParam, FormalitySubsystem.loginFxnParam);
      p.put(FormalitySubsystem.modeParam, FormalitySubsystem.loginMode);
    //  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showHintMode_);
    //   params.put(FormalitySubsystem.modeParam, AuthoringSubsystem.authorHomeMode_);
     // p.put(FormalitySubsystem.userLogin, "hello");
    //  p.put(FormalitySubsystem.userPassword, "pwd");
        return p;
    }
        public TreeMap getFakeAuthHomeParams(){
        TreeMap p = new TreeMap();
    //  params.put(FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam);
        p.put(FormalitySubsystem.fxnParam, FormalitySubsystem.loginFxnParam);
     // p.put(FormalitySubsystem.modeParam, AuthoringSubsystem.authorHomeMode_);
    //  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showHintMode_);
    //   params.put(FormalitySubsystem.modeParam, AuthoringSubsystem.authorHomeMode_);
      p.put(FormalitySubsystem.userLogin, "gordon");
      p.put(FormalitySubsystem.userPassword, "Lydia904");
        return p;
    }
        public TreeMap getFakeNewUserParams(){
        TreeMap p = new TreeMap();
    //  params.put(FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam);
        p.put(FormalitySubsystem.fxnParam, FormalitySubsystem.adminFxnParam);
    //  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showQuestionMode_);
    //  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showHintMode_);
      p.put(FormalitySubsystem.modeParam, AdminSubsystem.saveNewUserMode);
      p.put(FormalitySubsystem.userLogin, "gordon");
      p.put(FormalitySubsystem.userPassword, "Lydia904");
      p.put(FormalitySubsystem.userFirstName, "gordon");
      p.put(FormalitySubsystem.userLastName, "anderson");
      p.put(FormalitySubsystem.userAccessLevel, "4");
      p.put(FormalitySubsystem.userInstitution, "umass");
      p.put(FormalitySubsystem.userRole, "admin");
        return p;
    }
    public TreeMap getFakeNewQuestionParams(){
        TreeMap p = new TreeMap();
    //  params.put(FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam);
        p.put(FormalitySubsystem.fxnParam, FormalitySubsystem.authorFxnParam);
    //  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showQuestionMode_);
    //  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showHintMode_);
    //   params.put(FormalitySubsystem.modeParam, AuthoringSubsystem.authorHomeMode_);
        p.put(FormalitySubsystem.modeParam, AuthoringSubsystem.newQuestionMode_);
        return p;
    }

  public TreeMap getFakeSaveNewQuestionParams(){
    TreeMap p=new TreeMap();
//  params.put(FormalitySubsystem.fxnParam, FormalitySubsystem.studentFxnParam);
   p.put(FormalitySubsystem.fxnParam, FormalitySubsystem.authorFxnParam);
//  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showQuestionMode_);
//  params.put(FormalitySubsystem.modeParam, StudentSubsystem.showHintMode_);
//   params.put(FormalitySubsystem.modeParam, AuthoringSubsystem.authorHomeMode_);
   p.put(FormalitySubsystem.modeParam, AuthoringSubsystem.saveNewQuestionMode_);
  // p.put(FormalitySubsystem.questionIDParam, "15");
  // p.put(FormalitySubsystem.hintIDParam, "1");
  // p.put(FormalitySubsystem.levelParam, "3");
   //p.put(FormalitySubsystem.coachParam, "a");

       p.put("catID", "3");
        StringBuffer stem=new StringBuffer();
        stem.append("On a test, {h2a}{h3a}{h3b}Hannah{/h2a}{/h3a}{/h3b} scored 8 points {h3a}higher than{/h3a} ");
        stem.append("{h2a}{h3a}{h3b}Todd{/h2a}{/h3a}{/h3b}. On the same test, {h2a}{h3a}{h3b}Hannah{/h2a}{/h3a}{/h3b} scored 7 points ");
        stem.append("{h3a}lower than{/h3a} {h2a}{h3a}{h3b}Juanita{/h2a}{/h3a}{/h3b}.<br>");
        stem.append("{h3a}<i>H</i>{/h3a} represents Hannah's score on the test.<br>");
        stem.append("{h3a}<i>T</i>{/h3a} represents Todd's score on the test.<br>");
        stem.append("{h3a}<i>J</i>{/h3a} represents Juanita's score on the test.<br>");
        stem.append("Based on the information above, which of the following");
        stem.append("statements is true?<br>");
        p.put("stem", stem.toString());
        p.put("ansa", "<i>J</i> &lt; <i>T</i>");
        p.put("fdbka", "feedback A");
        p.put("ansb", "<i>T</i> &lt; <i>J</i>");
        p.put("fdbkb", "feedback B");
        p.put("ansc", "<i>H</i> &gt; <i>J</i>");
        p.put("fdbkc", "feedback C");
        p.put("ansd", "<i>J</i> &lt; <i>H</i>");
        p.put("fdbkd", "feedback D");
        p.put("corans", "B");

     /*
        //hints
        int levelInt =Integer.parseInt(level);
        switch(levelInt){
        case 1:
            ;
        case 2:
            ;
        case 3:
            Vector h=new Vector();
            Hint h1 = new Hint("1");
            h1.setCoach("a");
            h1.setCoachName("Athena");
            h1.setLevel(level);
            h1.setTag(Hint.getHintTag(level,"a"));
            h1.setQuery("How can I make a drawing to help with this question?");
            h1.setResponse("First, we are told about the relationship between the people:"+
                           "<br>Hannah higher than Todd,<br>"+
                           "Hannah lower than Juanita.<br>"+
                           "Then, we are told that H = Hannah,<br> T = Todd, and J = Juanita.<br>"+
                           "You might make drawings to show the relationships."+
                           "<A HREF=\"javascript: extraWindow('drawing1q15a.html')\">Click here to see the drawings.</a>");
            h.add(h1);
            Hint h2 = new Hint("2");
            h2.setCoach("b");
            h2.setCoachName("How To Hound");
            h2.setLevel(level);
            h2.setTag(Hint.getHintTag(level,"b"));
            h2.setQuery("How can I use numbers to tell me about the question?");
            h2.setResponse("We are told about the relationships between the people:<br>"+
                           "Let's pick numbers that could be their scores. We have to be careful<br>"+
                           "to pick numbers that agree with the question:"+
                           "Hannah 8 points higher than Todd,<br>"+
                           "so let's pick 10 for Hannah's score. Then, Todd's score has to be 2.<br>"+
                           "Then, we are told that:"+
                           "Hannah is 7 points lower than Juanita, so 10 is 7 points lower than 17.<br>");
            h.add(h2);
            Hint h3 = new Hint("3");
            h3.setCoach("c");
            h3.setCoachName("Chef Bear");
            h3.setLevel(level);
            h3.setTag(Hint.getHintTag(level,"c"));
            h3.setQuery("How can I use the &lt;  &gt; symbols?");
            h3.setResponse("We are told about the relationships between the people:<br>"+
                           "Hannah 8 points higher than Todd.<br>"+
                           "The &gt; symbol means greater than, or higher than, so Hannah &gt; Todd."+
                           "Then, we are told that Hannah is 7 points lower than Juanita. The &gt; symbol"+
                           "means less than, or lower than, so Hannah &lt; Juanita. Another way to think"+
                           "about this is to say that Juanita is higher than Hannah, so Juanita &gt; Hannah.");
            h.add(h3);
            q.setHintLevel(h);
        case 4:
            ;
        case 5:
            ;
        }
        */
      return p;
    }

    public TreeMap getFakeSaveNewHintParams(){
        TreeMap p = new TreeMap();
        p.put(FormalitySubsystem.fxnParam, FormalitySubsystem.authorFxnParam);
        p.put(FormalitySubsystem.modeParam, AuthoringSubsystem.saveNewHintMode_);
        p.put(GeneralHtml.level_, "3");
        p.put(GeneralHtml.coach_, "1");
        p.put(GeneralHtml.query_, "How can I make a drawing to help with this question?");
        p.put(GeneralHtml.response_, "First, we are told about the relationship between the people:"+
          "<br>Hannah higher than Todd,<br>"+
          "Hannah lower than Juanita.<br>"+
          "Then, we are told that H = Hannah,<br> T = Todd, and J = Juanita.<br>"+
          "You might make drawings to show the relationships."+
          "<A HREF=\"javascript: extraWindow('drawing1q15a.html')\">Click here to see the drawings.</a>");
       p.put(GeneralHtml.strategyID_, "3");
       p.put(GeneralHtml.questionID_, "1");
       return p;
    }
}
