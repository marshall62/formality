package formality.html.contenttable;

import formality.content.SystemInfo;
import formality.content.QModule;
import formality.model.StudentInfo;
import formality.controller.StudentSubsystem;
import formality.controller.TeacherSubsystem;
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
public class TeacherAdaptModContentTable implements CourseContentTable{

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

        List<QModule> ptMods = null;
     List<QModule> p1Mods = null;
     List<QModule> p2Mods = null;
     List<QModule> t1Mods = null;
     List<QModule> t2Mods = null;
     List<QModule> pstMods = null;

    String sID=null;

   public void setState(StateParser p){
       sID=p.getState();
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
    page.append("<table><tr><td>");
//PRETEST
    if(ptMods.size()>0){
      showPrePostModules("Pre Test",info, ptMods, page, false);
      page.append("<br>");
      page.append("<hr>");
    }
// SET1
    page.append("<table width=100%><tr><td valign=top width=50%>");
    showPracticeMods("Practice Set 1", info, p1Mods, page, false);
    page.append("<br>");
    page.append("</td>");
    page.append("<td valign=top width=50%>");
    if(t1Mods.size()>0)
      showTestMod("Test 1", info, t1Mods, page, false);
    else
      page.append("&nbsp;");
    page.append("</td></tr></table>");
    page.append("<hr>");
// SET2
    page.append("<table width=100%><tr><td valign=top width=50%>");
    showPracticeMods("Practice Set 2", info, p2Mods, page, false);
    page.append("<br>");
    page.append("</td>");
    page.append("<td valign=top width=50%>");
    if(t2Mods.size()>0)
      showTestMod("Test 2", info, t2Mods, page, false);
    else
      page.append("&nbsp;");
    page.append("</td></tr></table>");
    page.append("<hr>");
//POST TEST
    if(pstMods.size()>0)
      showPrePostModules("Post Test",info, pstMods, page, false);
    page.append("<br>");
    page.append("</td></tr></table>");
 }

  public void showPrePostModules(String title, SystemInfo info, List<QModule> mods,
                           StringBuffer page, boolean greyed)throws Exception {
     if(mods!=null && mods.size()>0){
      page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=45%><tr>");
      page.append("<td colspan=3 bgcolor=#dddddd>");
      if(greyed)
        page.append("<b><div CLASS=greypgtxt>");
      else
        page.append("<b><div CLASS=pgtxt>");
      page.append(title).append("</b></td></tr>");
      for (QModule m:mods) {
       int totalQ=m.getQuestionCount();
       int numCorrect=0;
       int numAnswered = 0;
       if(numAnswered!=0){
           m.setTouch(true);
        }
       page.append("<tr>");
       if(!greyed){
         page.append("<td align=center bgcolor=");
         if(m.getTouch())
           page.append(StudentHtml.alreadyWorkedOnColor);
         else
          page.append(StudentHtml.haveNotWorkedOnColor);
        String vtUrl=GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                    TeacherSubsystem.viewTestModuleMode_,
                    GeneralHtml.moduleID_, m.getID());
        vtUrl=GeneralHtml.appendToUrl(vtUrl, GeneralHtml.studentID_, sID);
          page.append(" width=10%><a href=\"");
          page.append(vtUrl);
          page.append("\">");
          page.append("<img SRC=\"").append(info.getContextPath());
          page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
          page.append("</td>");
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
      page.append("</b></font></td>");
      page.append("</tr>");
      }
      page.append("</table>");
    }
  }

  public void showPracticeMods(String title, SystemInfo info, List<QModule> mods,
                           StringBuffer page,boolean greyed)throws Exception{
      page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
      page.append("<td colspan=3 bgcolor=#dddddd>");
      if(greyed)
        page.append("<b><div CLASS=greypgtxt>");
      else
        page.append("<b><div CLASS=pgtxt>");
      page.append(title).append("</b></td></tr>");
      for (QModule m:mods) {
         int totalQ=m.getQuestionCount();
         int numCorrect=0;
         int numAnswered = 0;
         if(numAnswered!=0)
           m.setTouch(true);
       page.append("<tr>");
       if(!greyed){
         page.append("<td align=center bgcolor=");
         if(m.getTouch())
           page.append(StudentHtml.alreadyWorkedOnColor);
         else
          page.append(StudentHtml.haveNotWorkedOnColor);
         page.append(" width=10%><a href=\"");
         String vmUrl=GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                    TeacherSubsystem.viewModuleMode_,
                    GeneralHtml.moduleID_, m.getID());
        vmUrl=GeneralHtml.appendToUrl(vmUrl, GeneralHtml.studentID_, sID);
         page.append(vmUrl);
         page.append("\">");
         page.append("<img SRC=\"").append(info.getContextPath());
         page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
         page.append("</td>");
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
       page.append("</b></font></td></tr>");
     }
     page.append("</table>");
  }

    public void showTestMod(String title, SystemInfo info, List<QModule> mods,
                            StringBuffer page, boolean greyed)throws Exception{
      page.append("<table BORDER=1 CELLPADDING=3 CELLSPACING=0 width=90%><tr>");
      page.append("<td colspan=3 bgcolor=#dddddd>");
      if(greyed)
        page.append("<b><div CLASS=greypgtxt>");
      else
        page.append("<b><div CLASS=pgtxt>");
      page.append(title).append("</div></b></td></tr>");
      for (QModule m : mods) {
        int totalQ=m.getQuestionCount();
        int numCorrect=0;
        int numAnswered = 0;
        if(numAnswered!=0){
           m.setTouch(true);
        }
        page.append("<tr>");
        if(!greyed){
          page.append("<td align=center bgcolor=");
          if(m.getTouch())
           page.append(StudentHtml.alreadyWorkedOnColor);
          else
           page.append(StudentHtml.haveNotWorkedOnColor);
          page.append(" width=10%><a href=\"");
          String tUrl= GeneralHtml.getTeacherUrl(info.getServletRootAndID(),
                    TeacherSubsystem.viewTestModuleMode_,
                    GeneralHtml.moduleID_, m.getID());
          tUrl=GeneralHtml.appendToUrl(tUrl, GeneralHtml.moduleType_, m.getType());
          tUrl=GeneralHtml.appendToUrl(tUrl, GeneralHtml.studentID_, sID);
          page.append(tUrl);
          page.append("\">");
          page.append("<img SRC=\"").append(info.getContextPath());
          page.append("/images/sel.gif\" ALT=\"select module\" BORDER=0></a>");
          page.append("</td>");
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
        page.append("</b></font></td></tr>");
     }
    page.append("</table>");
  }

 }


