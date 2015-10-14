package formality.html.hints;

import formality.content.*;
import formality.parser.HighlightParser;
import formality.html.hints.BaseHintRenderer;
import formality.html.GeneralHtml;
import java.util.Iterator;
import java.util.TreeMap;

    /**
     * Created by IntelliJ IDEA.
     * User: gordon
     * Date: Mar 18, 2009
     * Time: 9:50:32 AM
     * To change this template use File | Settings | File Templates.
     */
    public class TableHintRenderer extends BaseHintRenderer {

        HighlightParser hltParser_;
        String contextPath_;
        Hints hints_;
        int condition_;
        String selLevel_;
        String selHintID_;

        // call this before any other public methods
    public void initHintRenderer(SystemInfo info, Question q) throws Exception{
        String activeTag =  q.getActiveTag();
        TreeMap tagInfo = info.getTags();
        hltParser_ = new HighlightParser(tagInfo);
        hltParser_.setActiveTag(activeTag);
        contextPath_ = info.getContextPath();

        hints_ = q.getHints();
        condition_ = info.getUserInfo().getCondition();
        selLevel_ = q.getSelLevel();
        selHintID_ = q.getSelHintID();
     }

     public void setHintUrl(String hintUrl_) {
        this.hintUrl_=GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selLevel_, selLevel_);
    }

    public void getHintDisplay(StringBuffer page, String hintId)throws Exception {


       page.append("<!--BEGIN HINT DISPLAY -->").append("\r\n");
       try{
            if(condition_==0){
                 getHintNavigation(hintNavUrl_, selLevel_, page);
                 getHintContent(hints_, contextPath_, selHintID_, selLevel_, hintUrl_, hintNavUrl_, page);
            }
            else if(condition_==1){
                 getHintNavigation1(hintNavUrl_, selLevel_, page);
                 getHintContent1(hints_, contextPath_, selHintID_, selLevel_, hintUrl_, hintNavUrl_, page);
            }
          }catch(Exception ex){
             page.append("Error rendering hints "+ex.getMessage());
          }
      page.append("<!--END HINT DISPLAY -->").append("\r\n");
   }

   protected void getHintNavigation(String hintNavUrl, String selLevel, StringBuffer page)throws Exception {
           int curIntLevel=Integer.parseInt(selLevel);
           page.append("<!--HINT CELL hint level nav widgets -->").append("\r\n");
           page.append("<TABLE cellSpacing=0 cellPadding=5 bgColor=#eeeeee width=\"100%\" border=1>\r\n");
           page.append("<TR><TD colspan=7 align=center>Hint Levels</TD></TR><TR>").append("\r\n");
           page.append("<!--------------------------- HINT LEVEL ------->").append("\r\n");
           for(int i=1;i<6;i++){
           String curLevUrl=GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.selLevel_, Integer.toString(i));
            if(curIntLevel==i){
             page.append("<td align=middle bgcolor=").append(GeneralHtml.navSelectedColor_).append(">");
            }
            else {
             page.append("<td align=middle bgColor=#dddddd>");
            }
           page.append("<a href=\"").append(curLevUrl).append("\">&nbsp;&nbsp;");
           page.append(i).append("&nbsp;&nbsp;</a></td>").append("\r\n");
           }
           page.append("<!---------------END HINT LEVEL ------->").append("\r\n");
           page.append("</TR></TABLE>").append("\r\n");
           page.append("<!--end hint level nav widgets -->").append("\r\n");
       }

        //just display the title "Hints"
        public void getHintNavigation1(String hintNavUrl, String selLevel, StringBuffer page)throws Exception {
           int curIntLevel=Integer.parseInt(selLevel);
           if(curIntLevel==1)
             curIntLevel=4;
           page.append("<!--HINT CELL hint level nav widgets -->").append("\r\n");
           page.append("<TABLE cellSpacing=0 cellPadding=5 bgColor=#eeeeee width=\"100%\" border=1>").append("\r\n");
           page.append("<TR><TD colspan=7 align=center>Hint Levels</TD></TR>").append("\r\n");

            page.append("<TR>").append("\r\n");
           for(int i=4;i<6;i++){
              String curLevUrl=GeneralHtml.appendToUrl(hintNavUrl, GeneralHtml.selLevel_, Integer.toString(i));

              if(curIntLevel==i){
                 page.append("<td align=middle bgcolor=").append(GeneralHtml.navSelectedColor_).append(">");
                 page.append("&nbsp;&nbsp;<b>");
                 if(curIntLevel==4)
                     page.append("1");
                 else
                     page.append("2");
                 page.append("</b>&nbsp;&nbsp;</td>").append("\r\n");
              }

            else {
             page.append("<td align=middle bgColor=#dddddd>");
             page.append("<a href=\"").append(curLevUrl).append("\">&nbsp;&nbsp;");
             if(i==4)
               page.append("1&lt;");
             else
               page.append("&gt;2");
             page.append("&nbsp;&nbsp;</a></td>").append("\r\n");
            }
          }

           page.append("<!---------------END HINT LEVEL ------->").append("\r\n");
           page.append("</TR></TABLE>").append("\r\n");
       }

       public void getHintContent(Hints hints, String contextPath, String hintID, String selLevel,
                            String viewUrl, String levelUrl, StringBuffer page)throws Exception {
         boolean selected=false;
         String hintUrl="";

         page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" border=1>").append("\r\n");
        // page.append("<TR bgColor=#eeeeee><TD align=center>Coaches:</TD></TR>").append("\r\n");

         Iterator<Hint> hintIter = hints.getAllStepHintsIter(selLevel);
         if(hintIter.hasNext()){
           while(hintIter.hasNext()) {
            Hint h = hintIter.next();
         if(hintID!=null && hintID.equals(h.getID()))
            selected=true;
         else
            selected=false;
         page.append("<tr><td><TABLE width=\"100%\" border=0>");
         page.append("<tr><td nowrap>");
         page.append("<font size=\"-1\">").append(h.getCoachName()).append("</font><br>").append("\r\n");
         page.append("</td><td align=\"right\" nowrap>");
         int curLevel = Integer.parseInt(h.getLevel());
         int backLevel=curLevel-1;
         int nextLevel=curLevel+1;
         if(backLevel<1)
              page.append("<font size=\"-1\">back</font>");
         else {
           String backUrl=GeneralHtml.appendToUrl(levelUrl, GeneralHtml.selLevel_, Integer.toString(backLevel));
           page.append("<a href=\"").append(backUrl).append("\"><font size=\"-1\">&lt; back</font></a>").append("\r\n");
         }
         page.append("&nbsp;&nbsp;&nbsp;&nbsp;");
         if(nextLevel>5)
              page.append("<font size=\"-1\">next</font>");
         else {
           String nextUrl=GeneralHtml.appendToUrl(levelUrl, GeneralHtml.selLevel_, Integer.toString(nextLevel));
           page.append("<a href=\"").append(nextUrl).append("\"><font size=\"-1\">next &gt;</font></a>").append("\r\n");
         }
         page.append("</td></tr>");
         page.append("<tr>");
         page.append("<TD align=center width=25%>").append("\r\n");
        // page.append("<font size=\"-1\">").append(h.getCoachName()).append("</font><br>").append("\r\n");
         hintUrl=GeneralHtml.appendToUrl(viewUrl, GeneralHtml.selHintID_, h.getID());
         page.append("<a href=\"").append(hintUrl).append("\">");
         page.append("<img SRC=\"").append(contextPath).append("/images/").append(h.getCoachImg()).append("\" ALT=\"hint coach img\" BORDER=0>");
         page.append("</a>").append("\r\n");
        page.append("</td>").append("\r\n");
         if(selected)
             hltParser_.getHighlightedTD(h.getQuery(), "middle", page);
         else{
             page.append("<td align=middle>");
             page.append("<a href=\"").append(hintUrl).append("\">");
             String hq = h.getQuery();
             hq =HighlightParser.removeTags(hq);
             page.append(hq + "</a>");
             page.append("</td>");
         }
         page.append("\r\n");
         page.append("</tr></table></td></tr>").append("\r\n");
         }
       }
        else{
            page.append("<tr><td>There are no hints at this level.</td></tr>").append("\r\n");
         }
         page.append("</table>").append("\r\n");
       }

      //this version does not show the Explainer hints- and only levels 4 and 5
      public void getHintContent1(Hints hints, String contextPath, String hintID, String selLevelStr,
                            String viewUrl, String levelUrl, StringBuffer page)throws Exception {
         boolean selected=false;
         String hintUrl="";
         page.append("<TABLE cellSpacing=0 cellPadding=5 width=\"100%\" border=1>").append("\r\n");
         int selLevel = Integer.parseInt(selLevelStr);
         if(selLevel==1) //upon first visit to question will be 1
            selLevel=4;

         Iterator<Hint> hintIter = null;
         while (hintIter==null && selLevel<6){
            hintIter = hints.getStepHintsIterMinusCoach(selLevel, "1");
            selLevel++;
         }

         if(hintIter.hasNext()){
          while(hintIter.hasNext()) {
           Hint h = hintIter.next();
             if(hintID!=null && hintID.equals(h.getID()))
               selected=true;
             else
              selected=false;
             page.append("<tr><td><TABLE width=\"100%\" border=0>");
             page.append("<tr><td nowrap>");
             page.append("<font size=\"-1\">").append(h.getCoachName()).append("</font><br>").append("\r\n");
             page.append("</td>");
             page.append("<td align=\"right\" nowrap>");
             page.append("&nbsp;&nbsp;&nbsp;&nbsp;");
             page.append("</td></tr>");
             page.append("<tr>");
             page.append("<TD align=center width=25%>").append("\r\n");
             hintUrl=GeneralHtml.appendToUrl(viewUrl, GeneralHtml.selHintID_, h.getID());
             page.append("<a href=\"").append(hintUrl).append("\">");
             page.append("<img SRC=\"").append(contextPath).append("/images/").append(h.getCoachImg()).append("\" ALT=\"hint coach img\" BORDER=0>");
             page.append("</a>").append("\r\n");
             page.append("</td>").append("\r\n");
             if(selected)
                 hltParser_.getHighlightedTD(h.getQuery(), "middle", page);
             else{
                page.append("<td align=middle>");
                page.append("<a href=\"").append(hintUrl).append("\">");
                page.append(h.getQuery()).append("</a>");
                page.append("</td>");
             }
             page.append("\r\n");
             page.append("</tr></table></td></tr>").append("\r\n");
         }//end of hint iterations
       }//if any hints
        else{
            page.append("<tr><td>There are no hints at this level.</td></tr>").append("\r\n");
         }
         page.append("</table>").append("\r\n");
       }
    }

