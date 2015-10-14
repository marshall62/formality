package formality.html.hints;

import formality.parser.HighlightParser;
import formality.content.Hints;
import formality.content.SystemInfo;
import formality.content.Question;
import formality.content.Hint;
import formality.html.hints.BaseHintRenderer;
import formality.html.GeneralHtml;

import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Jul 8, 2009
 * Time: 4:32:04 PM
 * ID   Coach
 * 1	Estella Explainer
 * 2	How To Hound
 * 3	Chef Math Bear
 * 4	Visual Vera
 */
public class HintRenderer2 extends BaseHintRenderer {
    HighlightParser hltParser_;
    String contextPath_;
    Hints hints_;
    int condition_;
    String selLevel_;
    String selHintID_;
    boolean isEvaluated_;

    // call this before any other public methods
    public void initHintRenderer(SystemInfo info, Question q) throws Exception {
        String activeTag = q.getActiveTag();
        TreeMap tagInfo = info.getTags();
        hltParser_ = new HighlightParser(tagInfo);
        hltParser_.setActiveTag(activeTag);
        contextPath_ = info.getContextPath();

        hints_ = q.getHints();
        condition_ = info.getUserInfo().getCondition();
        selLevel_ = q.getSelLevel();
        selHintID_ = q.getSelHintID();
        isEvaluated_ = q.isEvaluated();
    }

    //set the level to 5 to show feedback instead of hints
    public void setHintUrl(String hintUrl_) {
        if (isEvaluated_ || selLevel_.equals("5"))
            this.hintUrl_ = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selLevel_, "5");
        else
            this.hintUrl_ = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selLevel_, selLevel_);
    }

    // assume that the "selLevel_" variable always refers to an actual hint
    public void getHintDisplay(StringBuffer page, String hintId) throws Exception {
        if (isEvaluated_ || selLevel_.equals("5"))
            getHintFeedbackDisplay(page, hintId);
        else
            getPreAnswerHintDisplay(page, hintId);
    }

    public String getReducedCoachImage(String img) {
        img = img.replace("_sm", "_xsm");
        return img;
    }

    public String getIncreasedCoachImage(String img) {
        img = img.replace("_sm", "_lg");
        return img;
    }

    public String getCoachSizedImage(Hint h, String selectedHintID, boolean forceEnlarge) {
        if (forceEnlarge || (selectedHintID != null && selectedHintID.equals(h.getID())))
            return getIncreasedCoachImage(h.getCoachImg());
        else if (selectedHintID == null || selectedHintID.equals(""))
            return h.getCoachImg();
        else return getReducedCoachImage(h.getCoachImg());
    }

    protected void getPreAnswerHintDisplay(StringBuffer page, String hintId) throws Exception {
        String bgColorSelectedHint = "bgcolor=\"#A7D6A0\"";
        boolean forceEnlarge=false;
        Hint[] estellaHints = hints_.getAllCoachHints(1);
        boolean estellaHasHint = hasHint(estellaHints);
        Hint mathBear4 = hints_.getHint(3, 4);
        Hint hound4 = hints_.getHint(2, 4);
        Hint vera4 = hints_.getHint(4, 4);
        boolean hasStrategyHint = (mathBear4.getID() != null || hound4.getID() != null || vera4.getID() != null);
        page.append("<!-- HINT DISPLAY -->").append("\r\n");
        page.append("<table cellSpacing=0 cellPadding=5 bgColor=#eeeeee width=\"100%\" border=1>\r\n");
        page.append("<tr><td align=\"middle\">");
        if (!estellaHasHint && !hasStrategyHint)
            page.append("<font face=\"Comic Sans MS\">No Hints Available</font>\r\n");
        else {
            //get the hints for Estella -(at most the first three). There may not be three hints.
            if (estellaHasHint) {
                page.append("<font face=\"Comic Sans MS\">Understanding the Problem</font></td></tr>\r\n");


                Hint selectedHint = null;
                String prevUrl = null;
                String nextUrl = null;
                boolean inSequence=false;
                if (selLevel_.equals("1")) {
                    selectedHint = estellaHints[0];
                    Hint nextH = getNextHint(1, estellaHints);
                    if (nextH != null && !nextH.getLevel().equals("5")) {
                        nextUrl = GeneralHtml.appendToUrl(hintNavUrl_, GeneralHtml.selLevel_, nextH.getLevel());
                        nextUrl = GeneralHtml.appendToUrl(nextUrl, GeneralHtml.selHintID_, nextH.getID());
                        inSequence = true;
                    }
                }
                else if (selLevel_.equals("2")) {
                    selectedHint = estellaHints[1];
                    Hint nextH = getNextHint(2, estellaHints);
                    if (nextH != null && !nextH.getLevel().equals("5")) {
                        nextUrl = GeneralHtml.appendToUrl(hintNavUrl_, GeneralHtml.selLevel_, nextH.getLevel());
                        nextUrl = GeneralHtml.appendToUrl(nextUrl, GeneralHtml.selHintID_, nextH.getID());
                        
                        inSequence=true;
                    }
                    Hint prevH = getPrevHint(0, estellaHints);
                    if (prevH != null)  {
                        prevUrl = GeneralHtml.appendToUrl(hintNavUrl_, GeneralHtml.selLevel_, prevH.getLevel());
                        prevUrl = GeneralHtml.appendToUrl(prevUrl, GeneralHtml.selHintID_, prevH.getID());
                        inSequence=true;
                    }
                }
                else if (selLevel_.equals("3")) {
                    selectedHint = estellaHints[2];
                    Hint nextH = getNextHint(3, estellaHints);
                    if (nextH != null && !nextH.getLevel().equals("5")) {
                        nextUrl = GeneralHtml.appendToUrl(hintNavUrl_, GeneralHtml.selLevel_, nextH.getLevel());
                        nextUrl = GeneralHtml.appendToUrl(nextUrl, GeneralHtml.selHintID_, nextH.getID());

                        inSequence=true;
                    }
                    Hint prevH = getPrevHint(1, estellaHints);
                    if (prevH != null)  {
                        prevUrl = GeneralHtml.appendToUrl(hintNavUrl_, GeneralHtml.selLevel_, prevH.getLevel());
                        prevUrl = GeneralHtml.appendToUrl(prevUrl, GeneralHtml.selHintID_, prevH.getID());
                        inSequence=true;
                    }
                }
                else if (selLevel_.equals("4")) {
                    selectedHint = estellaHints[3];
                    Hint prevH = getPrevHint(2, estellaHints);
                    if (prevH != null)  {
                        prevUrl = GeneralHtml.appendToUrl(hintNavUrl_, GeneralHtml.selLevel_, prevH.getLevel());
                        prevUrl = GeneralHtml.appendToUrl(prevUrl, GeneralHtml.selHintID_, prevH.getID());
                        inSequence=true;
                    }
                }
//                page.append("<tr><td>\r\n");
                if (selectedHint.getID().equals(hintId) || (inSequence && hintId!=null &&  hintId.equals("")))   {
                    page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                    forceEnlarge=true;
                }
                else page.append("<tr><td>\r\n");
                // <font face="Comic Sans MS">
                page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font face=\"Comic Sans MS\" size=\"-1\">Estella Explainer</font><br>\r\n");
                page.append("</td><td align=\"right\" nowrap>\r\n");
                if (prevUrl == null)
                    ;// page.append("<font color=\"grey\" size=\"-1\">back</font>");
                else
                    page.append("<a href=\"").append(prevUrl).append("\"><font size=\"-1\">&lt; back</font></a>");
                page.append("&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
                if (nextUrl == null)
                    ;// page.append("<font color=\"grey\" size=\"-1\">next</font>");
                else
                    page.append("<a href=\"").append(nextUrl).append("\"><font size=\"-1\">next &gt;</font></a>");

                page.append("</td></tr><tr><TD align=center width=25%>\r\n");
                String estellaUrl = "#";
                if (selectedHint != null)
                    estellaUrl = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, selectedHint.getID());
                page.append("<a id=\"estellaLinka\" href=\"").append(estellaUrl).append("\">");
                page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(selectedHint,hintId, forceEnlarge)).append("\"");
                forceEnlarge=false;
//     page.append(" ALT=\"hint coach estella img\" BORDER=0  height=\"65\" width=\"55\"></a></td>\r\n");
                page.append(" ALT=\"hint coach estella img\" BORDER=0></a></td>\r\n");
                page.append("<td align=middle>\r\n");
                page.append("<a id=\"estellaLinkb\" href=\"").append(estellaUrl).append("\">\r\n");
                page.append("<font face=\"Comic Sans MS\">" + HighlightParser.removeTags(selectedHint.getQuery()) + "</font>");
                page.append("</a></td>\r\n");
                page.append("</tr></table>\r\n");
                page.append("</td></tr>\r\n");
            }
            //Solving Strategies part
            if (hasStrategyHint) {
                page.append("<tr><td align=\"middle\"><font face=\"Comic Sans MS\">Solving Strategies</font></td></tr>\r\n");
                //math bear
                if (mathBear4.getID() != null) {
                    String mathBear4Url = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, mathBear4.getID());
                    if (mathBear4.getID().equals(hintId))
                        page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                    else page.append("<tr><td>\r\n");
                    page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font face=\"Comic Sans MS\" size=\"-1\">Chef Math Bear</font></td></tr>\r\n");
                    page.append("<tr><TD align=center width=25%>\r\n");
                    page.append("<a id=\"bearLinka\" href=\"").append(mathBear4Url).append("\">");
                    page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(mathBear4,hintId, forceEnlarge)).append("\"");
//       page.append(" ALT=\"hint coach math bear img\" BORDER=0 height=\"65\" width=\"55\"></a></td>\r\n");
                    page.append(" ALT=\"hint coach math bear img\" BORDER=0></a></td>\r\n");
                    page.append("<td align=middle><a id=\"bearLinkb\" href=\"").append(mathBear4Url).append("\">\r\n");
                    // <font face="Comic Sans MS">Understanding the Problem</font>
                    page.append("<font face=\"Comic Sans MS\">" + mathBear4.getQuery() + "</font>");
                    page.append("</a></td>\r\n");
                    page.append("</tr></table>\r\n");
                    page.append("</td></tr>\r\n");
                }
                //how to hound
                if (hound4.getID() != null) {
                    String hound4Url = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, hound4.getID());
                    if (hound4.getID().equals( hintId))
                        page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                    else page.append("<tr><td>\r\n");
                    page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font face=\"Comic Sans MS\" size=\"-1\">How To Hound</font></td>\r\n");
                    page.append("</tr><tr><TD align=center width=25%>\r\n");
                    page.append("<a id=\"h2hLinka\" href=\"").append(hound4Url).append("\">");
                    page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(hound4,hintId, forceEnlarge)).append("\"");
//      page.append(" ALT=\"hint coach how to hound img\" BORDER=0 height=\"65\" width=\"55\"></a></td>\r\n");
                    page.append(" ALT=\"hint coach how to hound img\" BORDER=0></a></td>\r\n");
                    page.append("<td align=middle><a id=\"h2hLinkb\" href=\"").append(hound4Url).append("\">\r\n");
                    // <font face="Comic Sans MS">Understanding the Problem</font>
                    page.append(HighlightParser.removeTags("<font face=\"Comic Sans MS\">" + hound4.getQuery() + "</font>"));
                    page.append("</a></td>\r\n");
                    page.append("</tr></table>\r\n");
                    page.append("</td></tr>\r\n");
                }
                //visual vera
                if (vera4.getID() != null) {
                    if (vera4.getID().equals( hintId) )
                        page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                    else page.append("<tr><td>\r\n");
                    String vera4Url = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, vera4.getID());
                    page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font face=\"Comic Sans MS\" size=\"-1\">Visual Vicuna</font></td>\r\n");
                    page.append("</tr><tr><TD align=center width=25%>&nbsp;&nbsp;\r\n");
                    page.append("<a id=\"vicunaLinka\" href=\"").append(vera4Url).append("\">");
                    page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(vera4,hintId, forceEnlarge)).append("\"");
//     page.append(" ALT=\"hint coach visual vera img\" BORDER=0 height=\"65\" width=\"55\"></a></td>\r\n");
                    page.append(" ALT=\"hint coach visual Vicuna img\" BORDER=0></a></td>\r\n");
                    page.append("<td align=middle><a id=\"vicunaLinkb\" href=\"").append(vera4Url).append("\">\r\n");
                    page.append("<font face=\"Comic Sans MS\">" + HighlightParser.removeTags(vera4.getQuery()) + "</font>");
                    page.append("</a></td></tr></table>\r\n");
                }
            }
        }
        page.append("</td></tr></table>\r\n");
        page.append("<!--END HINT DISPLAY -->").append("\r\n");
    }

    // assume that the question has been answered
    protected void getHintFeedbackDisplay(StringBuffer page, String hintId) throws Exception {
        boolean forceEnlarge=false;
        String bgColorSelectedHint = "bgcolor=\"#A7D6A0\"";
        Hint estella5 = hints_.getHint(1, 5);
        Hint mathBear5 = hints_.getHint(3, 5);
        Hint hound5 = hints_.getHint(2, 5);
        Hint vera5 = hints_.getHint(4, 5);
        page.append("<!-- HINT FEEDBACK DISPLAY -->").append("\r\n");
        page.append("<table cellSpacing=0 cellPadding=5 bgColor=#eeeeee width=\"100%\" border=1>\r\n");
        boolean hasFeedbackHint = (estella5.getID() != null || mathBear5.getID() != null || hound5.getID() != null || vera5.getID() != null);
        if (hasFeedbackHint) {
            page.append("<tr><td align=\"middle\"><font face=\"Comic Sans MS\">Feedback</font></td></tr>\r\n");
            //estella
            if (estella5.getID() != null) {
                String estellaUrl = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, estella5.getID());
                if (estella5.getID().equals( hintId))
                    page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                else page.append("<tr><td>\r\n");
                page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font size=\"-1\">Estella Explainer</font></td></tr>\r\n");
                page.append("<tr><TD align=center width=25%>\r\n");
                page.append("<a id=\"estellaLinka\" href=\"").append(estellaUrl).append("\">");
                page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(estella5,hintId, forceEnlarge)).append("\"");
//       page.append(" ALT=\"hint coach math bear img\" BORDER=0 height=\"65\" width=\"55\"></a></td>\r\n");
                page.append(" ALT=\"hint coach estella img\" BORDER=0></a></td>\r\n");
                page.append("<td align=middle><a id=\"estellaLinkb\" href=\"").append(estellaUrl).append("\">\r\n");
                page.append(estella5.getQuery());
                page.append("</a></td>\r\n");
                page.append("</tr></table>\r\n");
                page.append("</td></tr>\r\n");
            }
            //math bear
            if (mathBear5.getID() != null) {
                String mathBearUrl = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, mathBear5.getID());
                if (mathBear5.getID().equals( hintId) )
                    page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                else page.append("<tr><td>\r\n");
                page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font size=\"-1\">Chef Math Bear</font></td></tr>\r\n");
                page.append("<tr><TD align=center width=25%>\r\n");
                page.append("<a id=\"bearLinka\" href=\"").append(mathBearUrl).append("\">");
                page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(mathBear5,hintId, forceEnlarge)).append("\"");
//       page.append(" ALT=\"hint coach math bear img\" BORDER=0 height=\"65\" width=\"55\"></a></td>\r\n");
                page.append(" ALT=\"hint coach math bear img\" BORDER=0></a></td>\r\n");
                page.append("<td align=middle><a id=\"estellaLinkb\" href=\"").append(mathBearUrl).append("\">\r\n");
                page.append(mathBear5.getQuery());
                page.append("</a></td>\r\n");
                page.append("</tr></table>\r\n");
                page.append("</td></tr>\r\n");
            }
            //how to hound
            if (hound5.getID() != null) {
                String houndUrl = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, hound5.getID());
                if (hound5.getID().equals( hintId))
                    page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                else page.append("<tr><td>\r\n");
                page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font size=\"-1\">How To Hound</font></td>\r\n");
                page.append("</tr><tr><TD align=center width=25%>\r\n");
                page.append("<a id=\"h2hLinka\" href=\"").append(houndUrl).append("\">");
                page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(hound5,hintId, forceEnlarge)).append("\"");
                page.append(" ALT=\"hint coach how to hound img\" BORDER=0></a></td>\r\n");
//      page.append(" ALT=\"hint coach how to hound img\" BORDER=0 height=\"65\" width=\"55\"></a></td>\r\n");
                page.append("<td align=middle><a id=\"estellaLinkb\" href=\"").append(houndUrl).append("\">\r\n");
                page.append(hound5.getQuery());
                page.append("</a></td>\r\n");
                page.append("</tr></table>\r\n");
                page.append("</td></tr>\r\n");
            }
            //visual vera
            if (vera5.getID() != null) {
                if (vera5.getID().equals( hintId))
                    page.append("<tr " +bgColorSelectedHint+ "><td>\r\n");
                else page.append("<tr><td>\r\n");
                String veraUrl = GeneralHtml.appendToUrl(hintUrl_, GeneralHtml.selHintID_, vera5.getID());
                page.append("<TABLE width=\"100%\" border=0><tr><td nowrap><font size=\"-1\">Visual Vicuna</font></td>\r\n");
                page.append("</tr><tr><TD align=center width=25%>&nbsp;&nbsp;\r\n");
                page.append("<a id=\"vicunaLinka\" href=\"").append(veraUrl).append("\">");
                page.append("<img SRC=\"").append(contextPath_).append("/images/").append(getCoachSizedImage(vera5,hintId, forceEnlarge)).append("\"");
                page.append(" ALT=\"hint coach visual Vicuna img\" BORDER=0></a></td>\r\n");
//      page.append(" ALT=\"hint coach visual vera img\" BORDER=0 height=\"65\" width=\"55\"></a></td>\r\n");
                page.append("<td align=middle><a id=\"vicunaLinkb\" href=\"").append(veraUrl).append("\">\r\n");
                page.append(vera5.getQuery());
                page.append("</a></td></tr></table>\r\n");
            }
        }
        else
            page.append("<tr><td align=\"middle\"><font face=\"Comic Sans MS\">No Feedback Available</font></td></tr>\r\n");
        page.append("</td></tr>\r\n");
        //link to regular hints

        String toHintsUrl = GeneralHtml.appendToUrl(hintNavUrl_, GeneralHtml.selLevel_, "1");
        page.append("<tr><td align=\"middle\"><a id=\"backToHints\" href=\"").append(toHintsUrl).append("\">");
        page.append("<font face=\"Comic Sans MS\">Back to Strategies</font></a></td></tr>\r\n");
        page.append("</td></tr></table>\r\n");
        page.append("<!--END FEEDBACK HINT DISPLAY -->").append("\r\n");
    }

    // check the estella hints for the next hint
    //returns null if no next hint
    protected Hint getNextHint(int curLevel, Hint[] estellaHints) {
        Hint nextHint = null;
        for (int i = curLevel; i < estellaHints.length; i++) {
            Hint curHint = estellaHints[i];
            if (curHint.getID() != null) {
               return curHint;
            }
        }
        return null;
    }

    protected Hint getPrevHint(int curLevel, Hint[] estellaHints) {
        for (int i = curLevel; i >= 0; i--) {
            Hint curHint = estellaHints[i];
            if (curHint.getID() != null) {
               return curHint;
            }
        }
        return null;
    }

    // check the estella hints for the prev hint
    //returns null if no prev hint
    protected String getPrevHintLevel(int curLevel, Hint[] estellaHints) {
        String nextLevel = null;
        for (int i = curLevel - 2; i >= 0 && i < estellaHints.length; i--) {
            Hint curHint = estellaHints[i];
            if (curHint.getID() != null) {
                nextLevel = curHint.getLevel();
                break;
            }
        }
        return nextLevel;
    }

    protected boolean hasHint(Hint[] estellaHints) {
        boolean hasHint = false;
        for (int i = 0; i < estellaHints.length; i++) {
            Hint curHint = estellaHints[i];
            if (curHint.getID() != null) {
                hasHint = true;
                break;
            }
        }
        return hasHint;
    }
}
