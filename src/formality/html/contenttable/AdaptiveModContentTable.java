package formality.html.contenttable;

import formality.content.SystemInfo;
import formality.content.QModule;
import formality.model.StudentInfo;
import formality.controller.StudentSubsystem;
import formality.controller.FormalitySubsystem;
import formality.controller.coursecontroller.parser.AdaptiveModStateParser;
import formality.controller.coursecontroller.parser.StateParser;
import formality.html.GeneralHtml;
import formality.html.StudentHtml;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/**
 *  Produces a table with all of the course content
 * rendered according to the controller policy:
 * The pretest is shown first. When it's completed, then the
 * first set of practice modules will be shown. These sets are
 * currently hard coded in. When this set is completed, i.e. all
 * questions have been answered, the "test me" button is displayed.
 * When the button is pressed, the controller will make up a test
 * module of type StudentModule, with questions selected from all
 * available questions that test the skills presented in the practice
 * module at the current level the student is at, as estimated by the
 * student model. Avalable questions are Ready=1, and not linked to any
 * module in this student's course. This dynamically created module is
 * run in test mode, and is the only content shown to the student until all
 * questions have been answered. Then, the student sees all previous
 * content including the student module along with the next practice
 * group. After all content has been completed, the posttest is shown.
 * It is the only content shown until completed, when all content is
 * shown again. Note: once the pre and post tests have been completed
 * only a results bar will be shown.
 * 
 * States:
 *  null- initial- set to pt
 *  pt  - pretest
 *  p1   - practice set 1
 *  s1   - select offered(practice 1 complete)
 *  t1   - student mod test 1
 *  p2   - practice set 2
 *  s2   - select offered(practice 2 complete)
 *  t2   - student mod test 2
 *  t3 - posttest
 *  fin -all done

 *
 * User: gordon
 * Date: Dec 28, 2005
 * Time: 10:10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdaptiveModContentTable implements CourseContentTable{

    /* the current content setup:
    *  Pretest mod 13
    *  practice set 1
    *    Practice mod1
    *    Practice mod2
    *    StudentMod1
    *  practice set 2
    *    Practice mod1
    *    Practice mod2
    *    StudentMod1
    *  Posttest- mod 15
    */

     public static final String testSelMsg_="You have answered all questions in this practice set. Click on the button below when you're ready for a quiz:";
     public static final String postTestSelMsg_="You did it! You have completed all practice and test questions. Click on the button to take the post test.";
     public static final String finalMsg_="Congratulations! You have completed all class material. You may continue to practice the modules above. Good Work!";
     List<QModule> ptMods = null;
     List<QModule> p1Mods = null;
     List<QModule> p2Mods = null;
     List<QModule> t1Mods = null;
     List<QModule> t2Mods = null;
     List<QModule> pstMods = null;

     StateParser stateParser_=null;

   public void setState(StateParser state){
     stateParser_=state;
   }

   public void setContent(List<List<QModule>> content)throws Exception{
     ptMods = content.get(0);
     p1Mods = content.get(1);
     p2Mods = content.get(2);
     t1Mods = content.get(3);
     t2Mods = content.get(4);
     pstMods = content.get(5);
   }

   public void getContentTable(SystemInfo info,
                               StringBuffer page)throws Exception{
     StudentInfo si=(StudentInfo)info.getUserInfo();
     if(stateParser_.isTestState()){
      displayTestTable(info, si, page);
      return;
     }
//Begin table
    page.append("<table><tr><td>\r\n");
    showPreTestResult(si, page, false);
    page.append("<br>\r\n");
    page.append("<hr>");
//BEGIN SET1
    page.append("<table width=100%><tr><td valign=top width=50%>\r\n");
//Practice Set 1 Modules
     showPracticeMods("Practice Set 1", info, p1Mods, si, page, false);
     page.append("<br>\r\n");
     page.append("</td>");
//begin 2nd half of SET1
     page.append("<td valign=top width=50%>");

//display test or selection
      if(stateParser_.isSelect1()){
       displayTestSelection("1", info, testSelMsg_, si, page);
      }
     else if(stateParser_.showT1()){
       showTestMod("Test 1", info, t1Mods, si, page, false);
     }
   page.append("</td></tr></table>\r\n");
//END SET1
   page.append("<hr>");
//BEGIN SET2
   if(stateParser_.showP2()){
      page.append("<table width=100%><tr><td valign=top width=50%>\r\n");
//Practice Set 2 Modules
      showPracticeMods("Practice Set 2", info, p2Mods, si, page, false);

     page.append("<br>");
     page.append("</td>\r\n");
//begin 2nd half of SET2
    page.append("<td valign=top width=50%>");

//display test or selection
    if(stateParser_.isSelect2()){
      displayTestSelection("2", info, testSelMsg_, si, page);
     }
     else if(stateParser_.showT2()){
      showTestMod("Test 2", info, t2Mods, si, page, false);
     }
   page.append("</td></tr></table>\r\n");
   } //end show P2
//END SET2
   if(stateParser_.isSelect3()){
     if(pstMods.size()>0)
        displayTestSelection("3", info, postTestSelMsg_, si, page);
    }
   if(stateParser_.isFin()){
    page.append("<hr>");
    showPostTestResult(si, page, false);
    page.append("<br>\r\n");
    page.append("<table border=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
    page.append("<td><b><div CLASS=pgtxt>");
    page.append(finalMsg_);
    page.append("</td></tr><tr>\r\n");
   }
   page.append("</td></tr></table>\r\n");
 }

    /********************** ALL TESTS HERE******************************/
  public void displayTestTable(SystemInfo info, StudentInfo si,
                               StringBuffer page)throws Exception {
   page.append("<table border=0><tr><td>\r\n");
   if(stateParser_.isPreTest()){
      showPrePostModules("Pre Test",info, ptMods, si, page, false);
   }
   else {
    showPreTestResult(si, page, true);
    page.append("<br>\r\n");
    page.append("<hr>");
    //BEGIN SET1
    page.append("<table width=100%><tr><td valign=top width=50%>\r\n");
    //Practice Set 1 Modules
    showPracticeMods("Practice Set 1", info, p1Mods, si, page, true);
    page.append("</td>\r\n");
    //begin 2nd half of SET1
    page.append("<td valign=top width=50%>");
    if(stateParser_.isTest1()){
        showTestMod("Test 1", info, t1Mods, si, page, false);
        page.append("</td></tr></table>\r\n");
    }
    else{
      showTestMod("Test 1", info, t1Mods, si, page, true);
      page.append("</td></tr></table>\r\n");
      //END SET1
      page.append("<hr>");
      //BEGIN SET2
      page.append("<table width=100%><tr><td valign=top width=50%>\r\n");
      //Practice Set 2 Modules
      showPracticeMods("Practice Set 2", info, p2Mods, si, page, true);
      page.append("</td>");
      //begin 2nd half of SET2
      page.append("<td valign=top width=50%>");
      if(stateParser_.isTest2())
        showTestMod("Test 2", info, t2Mods, si, page, false);
      else
       showTestMod("Test 2", info, t2Mods, si, page, true);
     page.append("</td></tr></table>\r\n");
     page.append("<br>\r\n");
     page.append("<hr>");
//END SET2
     if(stateParser_.isPostTest())
       showPrePostModules("Post Test",info, pstMods, si, page, false);
    }
  }
 page.append("</td></tr></table>\r\n");
}

  public void showPrePostModules(String title, SystemInfo info, List<QModule> mods,
                           StudentInfo si, StringBuffer page, boolean greyed)throws Exception {
     if(mods!=null && mods.size()>0){
      page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=45%><tr>\r\n");
      page.append("<td colspan=3 bgcolor=#dddddd>\r\n");
      if(greyed)
        page.append("<b><div CLASS=greypgtxt>");
      else
        page.append("<b><div CLASS=pgtxt>");
      page.append(title).append("</b></td></tr>\r\n");
      for (QModule m :mods) {
       int totalQ=m.getQuestionCount();
       int numCorrect=si.getNumCorrectInModule(m.getID());
       int numAnswered = si.getDistinctNumAnsweredInModule(m.getID());
       if(numAnswered!=0){
           m.setTouch(true);
        }
       page.append("<tr>\r\n");
       if(!greyed){
         page.append("<td align=center bgcolor=");
         if(m.getTouch())
           page.append(StudentHtml.alreadyWorkedOnColor);
         else
          page.append(StudentHtml.haveNotWorkedOnColor);
          page.append(" width=10%><a href=\"");
          page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.viewTestModuleMode_,
                    GeneralHtml.moduleID_, m.getID(),
                  FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
          page.append("\">");
          page.append("<img SRC=\"").append(info.getContextPath());
          page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
          page.append("</td>\r\n");
       }
       page.append("<td bgcolor=#eeeeee>");
       if(greyed)
          page.append("<div CLASS=greypgtxt>");
       else
          page.append("<div CLASS=pgtxt>");
      page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
      page.append("<font size=\"-1\" ");
      if(greyed)
         page.append("color=#dddddd ");
      else
        page.append("color=#9586f9 ");
      page.append("face=\"Comic Sans MS\"><b>");
      page.append("Answered: "+numAnswered+"<br>Total questions: "+totalQ);
      page.append("</b></font></td>\r\n");
      page.append("</tr>\r\n");
      }
      page.append("</table>\r\n");
    }
  }

  public void showPracticeMods(String title, SystemInfo info, List<QModule> mods,
                           StudentInfo si, StringBuffer page,
                           boolean greyed)throws Exception{
      page.append("\r\n<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>\r\n");
      page.append("<td colspan=3 bgcolor=#dddddd>");
      if(greyed)
        page.append("<b><div CLASS=greypgtxt>");
      else
        page.append("<b><div CLASS=pgtxt>");
      page.append(title).append("</b></td></tr>\r\n");
      for (QModule m : mods) {
         int totalQ=m.getQuestionCount();
         int numCorrect=si.getNumCorrectInModule(m.getID());
         int numAnswered = si.getDistinctNumAnsweredInModule(m.getID());
         if(numAnswered!=0)
           m.setTouch(true);
       page.append("\r\n<tr>");
       if(!greyed){
         page.append("<td align=center bgcolor=");
         if(m.getTouch())
           page.append(StudentHtml.alreadyWorkedOnColor);
         else
          page.append(StudentHtml.haveNotWorkedOnColor);
         page.append(" width=10%><a href=\"");
         page.append(GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.viewModuleMode_,
                    GeneralHtml.moduleID_, m.getID(),
                 FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID()));
         page.append("\">");
         page.append("<img SRC=\"").append(info.getContextPath());
         page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
         page.append("</td>\r\n");
       }
       page.append("<td bgcolor=#eeeeee>");
       if(greyed)
        page.append("<b><div CLASS=greypgtxt>");
       else
        page.append("<b><div CLASS=pgtxt>");
       page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
       page.append("<font size=\"-1\" ");
       if(greyed)
         page.append("color=#dddddd ");
       else
         page.append("color=#9586f9 ");
       page.append("face=\"Comic Sans MS\"><b>");
       page.append("Correct: "+numCorrect+"<br>Total questions: "+totalQ);
       page.append("</b></font></td></tr>\r\n");
     }
     page.append("</table>\r\n");
  }

    public void showTestMod(String title, SystemInfo info, List<QModule> mods,
                            StudentInfo si, StringBuffer page, boolean greyed)throws Exception{
      page.append("\r\n<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>\r\n");
      page.append("<td colspan=3 bgcolor=#dddddd>");
      if(greyed)
        page.append("<b><div CLASS=greypgtxt>");
      else
        page.append("<b><div CLASS=pgtxt>");
      page.append(title).append("</div></b></td></tr>\r\n");
      for (QModule m :mods) {
        int totalQ=m.getQuestionCount();
        int numCorrect=si.getNumCorrectInModule(m.getID());
        int numAnswered = si.getDistinctNumAnsweredInModule(m.getID());
        if(numAnswered!=0){
           m.setTouch(true);
        }
        page.append("<tr>\r\n");
        if(!greyed){
          page.append("<td align=center bgcolor=");
          if(m.getTouch())
           page.append(StudentHtml.alreadyWorkedOnColor);
          else
           page.append(StudentHtml.haveNotWorkedOnColor);
          page.append(" width=10%><a href=\"");
          String tUrl= GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                    StudentSubsystem.viewTestModuleMode_,
                    GeneralHtml.moduleID_, m.getID(),
                  FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID());
          tUrl=GeneralHtml.appendToUrl(tUrl, GeneralHtml.moduleType_, m.getType());
          page.append(tUrl);
          page.append("\">");
          page.append("<img SRC=\"").append(info.getContextPath());
          page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
          page.append("</td>\r\n");
        }
        page.append("<td bgcolor=#eeeeee>");
        if(greyed)
          page.append("<b><div CLASS=greypgtxt>");
        else
          page.append("<b><div CLASS=pgtxt>");
        page.append(m.getName()).append("</div></td><td bgcolor=#eeeeee nowrap>");
        page.append("<font size=\"-1\" ");
        if(greyed)
         page.append("color=#dddddd ");
        else
         page.append("color=#9586f9 ");
        page.append("face=\"Comic Sans MS\"><b>");
        page.append("Correct: "+numCorrect+"<br>Total questions: "+totalQ);
        page.append("</b></font></td></tr>\r\n");
     }
    page.append("</table>\r\n");
  }

  public void displayTestSelection(String testNum, SystemInfo info, String msg,
                           StudentInfo si, StringBuffer page)throws Exception{
      page.append("<table CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
      page.append("<td><b><div CLASS=pgtxt>");
      page.append(msg);
      page.append("</td></tr><tr>");
      page.append("<FORM ACTION=\"");
      String action=GeneralHtml.getStudentUrl(info.getServletRootAndID(),
                                             StudentSubsystem.getStudentExamMode_,
                                             GeneralHtml.studentID_, si.getUserID(),
              FormalitySubsystem.courseIDParam, info.getUserInfo().getCourseID())+
              "&testNum="+testNum;
     page.append(action);
     page.append("\" METHOD=\"POST\" NAME=\"TestSelForm\">").append("\r\n");
     page.append("<TD ALIGN=center><INPUT TYPE=\"SUBMIT\" NAME=\"test\" VALUE=\"Test Me!\"></TD>").
                    append("\r\n");
     page.append("</form>").append("\r\n");
     page.append("</tr></table>");
  }

  public void showPreTestResult(StudentInfo si, StringBuffer page, boolean greyed){
     int corr=si.getPreTestNumCorrect();
     int total=si.getPreTestTotalQuestions();
     double score=corr/(double)total;
     if(greyed)
         GeneralHtml.getTestScoreDisplay(score, corr,total, "<b><div CLASS=greypgtxt>PreTest Results</b>", page);
      else
         GeneralHtml.getTestScoreDisplay(score, corr,total, "<b><div CLASS=pgtxt>PreTest Results</b>", page);
   }

  public void showPostTestResult(StudentInfo si, StringBuffer page, boolean greyed){
      int corr=si.getPostTestNumCorrect();
      int total=si.getPostTestTotalQuestions();
      double score=corr/(double)total;
      if(greyed)
        GeneralHtml.getTestScoreDisplay(score, corr,total, "<b><div CLASS=greypgtxt>PostTest Results</b>", page);
      else
        GeneralHtml.getTestScoreDisplay(score, corr,total, "<b><div CLASS=pgtxt>PostTest Results</b>", page);
   }

 }


