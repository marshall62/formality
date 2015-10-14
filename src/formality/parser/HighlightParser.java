package formality.parser;

import formality.content.SystemInfo;
import formality.servlet.FormalityServlet;

import java.util.TreeMap;
import java.util.Vector;
import java.util.StringTokenizer;

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
public class HighlightParser {

    static final String beginTable_ = "<table border=1><tr><td>";
    static final String stdTD_ = "<td nowrap>";
    static final String stdETD_ = "</td>";
    static final String tagETD_ ="</span>";
    static final String gTagEnd_ ="</a>";
    static final String endTable_ = "</td></tr></table>\r\n";
    static final int numCharsPerLineStem_ = 47;
    String activeTag_;
    String activeTagEnd_;
    TreeMap infoTags_ =null;

    public HighlightParser(){
        activeTag_="";
    }

    public HighlightParser(TreeMap tagInfo){
        infoTags_ =tagInfo;
    }

    public void setActiveTag(String at){
       // if(at==null || at.equals("")){
       //   SystemException sex=new SystemException();
       //   sex.setMessage("error in HltParser, setActiveTag was "+at);
       //   throw sex;
       // }
        if(at!=null){
          activeTag_=at;
          setActiveTagEnd(activeTag_);
        }
    }

    public void setActiveTagEnd(String ate){
        if(ate==null || ate.equals(""))
            activeTagEnd_="";
        else{
            String temp = ate.substring(1, ate.length());
            activeTagEnd_ = "{/" + temp;
        }
    }

    public void parseStem(String stem, StringBuffer page, SystemInfo info)throws Exception{
        infoTags_ =info.getTags();
        Vector lines = new Vector();
        // first, break into blocks separated by <BR> tags, if any.
        //Note: the <BR> tags will be in blocks of their own
        Vector blocks = getBlocks(stem);
        //remove all inactive tags
        for(int i=0;i<blocks.size();i++){
            String str = (String) blocks.get(i);
            //break text into lines of 47 chars
            getLines(numCharsPerLineStem_, str, lines);
        }
        for(int j=0;j<lines.size();j++){
            String str = (String) lines.get(j);
            page.append(replaceActiveTags(str, info));
        }
    }

    public void parseStem(String stem, StringBuffer page,
                          SystemInfo info, boolean useLines)throws Exception{
        if(useLines)
           parseStem(stem, page, info);
        else{
        infoTags_ =info.getTags();
        Vector lines = new Vector();
        // first, break into blocks separated by <BR> tags, if any.
        //Note: the <BR> tags will be in blocks of their own
        Vector blocks = getBlocks(stem);
        for(int i=0;i<blocks.size();i++){
            String str = (String) blocks.get(i);
            page.append(replaceActiveTags(str, info));
        }
      }
    }

    public void parseText(String text, StringBuffer page,
                          SystemInfo info)throws Exception{
        infoTags_ =info.getTags();
        page.append(replaceActiveTags(removeInactiveTags(text), info));
        /*
        Vector lines = new Vector();
        // first, break into blocks separated by <BR> tags, if any.
        //Note: the <BR> tags will be in blocks of their own
        Vector blocks = getBlocks(text);
        //remove all inactive tags
        for(int i=0;i<blocks.size();i++){
            String str = (String) blocks.get(i);
            //break text into lines of 47 chars
            getLines(numCharsPerLineStem_, str, lines);
        }
        for(int j=0;j<lines.size();j++){
            String str = (String) lines.get(j);
            page.append(replaceActiveTags(str));
        }
*/
    }

    public String removeInactiveTags(String inputStr){
        StringBuffer outStr = new StringBuffer();
        StringBuffer tag = null;
        boolean onTag = false;
        for(int i=0;i<inputStr.length();i++){
            char c = inputStr.charAt(i);
            if(c=='{'){
                onTag=true;
                tag = new StringBuffer();
                tag.append(c);
            }
            if(c=='}'){
                onTag=false;
                tag.append(c);
                String curTag=tag.toString();
                if(curTag.equals(activeTag_)||curTag.equals(activeTagEnd_)||
                   !infoTags_.containsKey(removeSlash(curTag)) || (curTag.indexOf("G")>-1))
                    outStr.append(curTag);
                tag=null;
            }
            if(!onTag){
                if (c != '}')
                    outStr.append(c);
            }
            else if(c!='{')
                tag.append(c);
        }
        return outStr.toString();
    }

    public static String removeTags(String inputStr){
        inputStr = replaceMediaRoot(inputStr);
        StringBuffer outStr = new StringBuffer();
        StringBuffer tag = null;
        boolean onTag = false;
        for(int i=0;i<inputStr.length();i++){
            char c = inputStr.charAt(i);
            if(c=='{'){
                onTag=true;
                tag = new StringBuffer();
                tag.append(c);
            }
            if(c=='}'){
                onTag=false;
                tag.append(c);
                String curTag=tag.toString();
               // if(curTag.equals(activeTag_)||curTag.equals(activeTagEnd_)||
                //   !infoTags_.containsKey(removeSlash(curTag)))
                   // outStr.append(curTag);
                tag=null;
            }
            if(!onTag){
                if (c != '}')
                    outStr.append(c);
            }
            else if(c!='{')
                tag.append(c);
        }
        return outStr.toString();
    }
    /*
                   tag=
             "</td><td bgcolor="+info.get(activeTag_)+"><B><font face=arial,sans-serif color=black size=-1>"

             /tag=
             "</font></b></td><td nowrap>"


     <table border=1><tr>
     <td nowrap>On a test,Hannah scored 8 points higher than</td>
    </tr></table>


    <table border=1><tr>
     <td nowrap>On a test,</td>
     <td bgcolor=#99ff99><B><font face=arial,sans-serif color=black size=-1>Hannah</font></b></td>
     <td nowrap>scored 8 points</td>
    <td nowrap bgcolor=#99ff99><B><font face=arial,sans-serif color=black size=-1>higher than</font></b></td>
    </tr></table>

*/
    public String replaceActiveTags(String inputStr, SystemInfo info)throws Exception{
        StringBuffer resStr = new StringBuffer();
       // resStr.append(beginTable_);
        replaceTagsHelper(inputStr, resStr, info);
//replaceActiveTagsHelper(inputStr, resStr);
       // resStr.append(endTable_);
        return resStr.toString();
    }

    private void replaceActiveTagsHelper(String inputStr, StringBuffer resStr){
        if(inputStr==null || inputStr.length()==0)
            return;
        int start = inputStr.indexOf('{');
        int end = inputStr.indexOf('}');
        if(start<0){//no tag in string-done
           //resStr.append(stdTD_);
           resStr.append(inputStr);
          // resStr.append(stdETD_);
           return;
        }
        if(start>0){//some chars before the tag
           //resStr.append(stdTD_);
           resStr.append(inputStr.substring(0,start));
          // resStr.append(stdETD_);
           replaceActiveTagsHelper(inputStr.substring(start,inputStr.length()), resStr);
        }
        if(start==0){//tag is at the beginning
           resStr.append(getStartTagString());
           String rest = inputStr.substring(end+1,inputStr.length());
           start = rest.indexOf('{');
           resStr.append(rest.substring(0,start));
           resStr.append(tagETD_);
           end = rest.indexOf('}');
           if(end==rest.length())
               return;
           replaceActiveTagsHelper(rest.substring(end+1,rest.length()), resStr);
        }
    }

    // replace active tags and glossary tags
   private void replaceTagsHelper(String inputStr,
                                  StringBuffer resStr, SystemInfo info)throws Exception{
        inputStr = replaceMediaRoot(inputStr);
        if(inputStr==null || inputStr.length()==0)
            return;
        int start = inputStr.indexOf('{');
        int end = inputStr.indexOf('}');
        if(start<0){//no tag in string-done
           //resStr.append(stdTD_);
           resStr.append(inputStr);
          // resStr.append(stdETD_);
           return;
        }
        if(start>0){//some chars before the tag
           //resStr.append(stdTD_);
           resStr.append(inputStr.substring(0,start));
          // resStr.append(stdETD_);
           replaceTagsHelper(inputStr.substring(start,inputStr.length()), resStr, info);
        }
      try{
        if(start==0){//tag is at the beginning
           String rest = null;
           //see if the tag is a glossary tag  {H2a}  {Gaddition}
           if(inputStr.charAt(1)=='G'){
             rest = inputStr.substring(end+1,inputStr.length());
             String glabel = inputStr.substring(start+2,end);
             resStr.append(getStartGlossaryTagString(glabel, info));
             start = getEndGTagIndex(rest);
              //in case there are highlights to parse:
             String t= rest.substring(0,start);
             String s =   replaceActiveTags(t, null);
             resStr.append(s);
             resStr.append(gTagEnd_);
             end = start+3;
           }
           else{
            resStr.append(getStartTagString());  // TODO set activeTag_
            rest = inputStr.substring(end+1,inputStr.length());
            start = rest.indexOf('{');
            resStr.append(rest.substring(0,start));
            resStr.append(tagETD_);
            end = rest.indexOf('}');
           }
           if(end==rest.length())
               return;
           replaceTagsHelper(rest.substring(end+1,rest.length()), resStr, info);
        }
           }catch (Exception ex){
          throw new Exception("error in highlight parser "+inputStr+ex.getMessage());
          }
    }

    private static String replaceMediaRoot(String inputStr) {
        return inputStr.replace("{MediaRoot}", FormalityServlet.resourcePath + "/media");
    }

    public void getLines(int numCharsPerLine, String inputStr, Vector lines){
        StringBuffer curLine = new StringBuffer();
        int count = 0, increment=0;
        StringTokenizer tokens = new StringTokenizer(inputStr);
        while(tokens.hasMoreTokens()){
            String curWord = tokens.nextToken();
            int breakTagIndex=containsBreakTag(curWord);
            if(breakTagIndex>=0){
                lines.add(inputStr);
            }
            int tagIndex=containsHighlightTag(curWord);
            if(tagIndex>=0){
               increment=getIncrement(curWord);
            }
            else if(containsTag(curWord)>=0 && breakTagIndex<0){
                increment=getIncrement(curWord);
            }
            else{
                increment=curWord.length();
            }
            count+=increment;
            if(count>numCharsPerLine){
                count=increment+1;
                lines.add(curLine.toString());
                curLine = new StringBuffer();
                curLine.append(curWord).append(" ");
            }
            else{
              curLine.append(curWord).append(" ");
              count++;
            }
        }
        lines.add(curLine.toString());
    }

    public Vector getBlocks(String inputStr){
     Vector blocks = new Vector();
     blockHelper(blocks, inputStr);
     return blocks;
 }

    private void blockHelper(Vector blocks, String inputStr){
        int index = containsBreakTag(inputStr);
        if(index==-1){
            blocks.add(removeInactiveTags(inputStr));
            return;
        }
        else{
            //add the block
            blocks.add(removeInactiveTags(inputStr.substring(0, index)));
            //add the <br> tag
            blocks.add(inputStr.substring(index, index+4));
            blockHelper(blocks, inputStr.substring(index+4, inputStr.length()));
        }

    }

    public String removeSlash(String tag){
        int ind=tag.indexOf('/');
        if(ind<0 || ind!=1)
            return tag;
        else //so it's </
            return "{"+tag.substring(2, tag.length());
    }

    public int containsTag(String str){
     return str.indexOf('{');
    }

    public int containsBreakTag(String str){
        String temp = str.toUpperCase();
        int res = -1;
        for(int i=0;i<temp.length();i++){
            char c = temp.charAt(i);
            if(c=='<'){
                if (temp.length() >= i + 4) {
                    char b = temp.charAt(i + 1);
                    char r = temp.charAt(i + 2);
                    char b2 = temp.charAt(i + 3);
                    if (b == 'B' && r == 'R' && b2 == '>')
                        return i;
                }
            }
        }
        return res;
    }

    public int containsHighlightTag(String str){
     return str.indexOf('{');
 }

   private int getIncrement(String word){
       int count = 0;
       boolean onTag=false;
       for(int i=0;i<word.length();i++){
         char c = word.charAt(i);
         if(c=='{'||c=='<'){
             onTag = true;
             count++;
         }
         if(c=='}'||c=='>'){
            onTag=false;
            count++;
         }
         else if(onTag && (c!='{' || c!='<')){
             count++;
         }
       }
       return word.length()-count;
   }

   private int getEndGTagIndex(String inputStr){
     int index = -1;
     int len=inputStr.length();
     for(int i=0;i<len;i++){
       char c = inputStr.charAt(i);
       if(c=='{'){
        char  a= inputStr.charAt(i+1);
        char b= inputStr.charAt(i+2);
         if(len>(i+2) && inputStr.charAt(i+1)=='/' && inputStr.charAt(i+2)=='G')
           return i;
       }
     }
     return index;
   }

   private String getStartTagString(){
      // DM added this line to prevent a throw when activeTag_ is null.   This is happening because
       // I am trying to render a hint text that contains formatting tags (e.g. {h1d} but I don't know
       // how these tags should be replaced and it doesn't really matter for the preview button anyway
       if (activeTag_ == null)
        return "";
      String tagColor = (String) infoTags_.get(activeTag_);
      return "<span style=\"color:black;background-color:"+tagColor+";font-weight: bold;font-size: -1;\">";

   }

  private String getStartGlossaryTagString(String itemName, SystemInfo info){
      String resourceURI = info.getContextPath();
     return "<a HREF=\"javascript: showGlossary('"+itemName+"')\">";
   }

   public void getHighlightedString(String txt, StringBuffer str){
    String tagColor = (String) infoTags_.get(activeTag_);
    str.append("<span style=\"color:black;background-color:");
    str.append(tagColor).append(";font-weight: bold;font-size: -1;\">");
    str.append(txt).append(tagETD_);
   }

   public void getHighlightedTD(String txt, StringBuffer str){
       if(txt==null){
         str.append("<td>&nbsp;</td>");
       }
       else {
       String tagColor = (String) infoTags_.get(activeTag_);
       str.append("<td rowspan=\"2\" bgcolor=").append(tagColor);
       str.append("><B><font face=arial,sans-serif color=black size=-1>");
       str.append(txt).append("</font></B></td>");
       }
   }

   public void getHighlightedTD(String txt, String align, StringBuffer str){
    String tagColor = (String) infoTags_.get(activeTag_);
    str.append("<td align=").append(align).append(" bgcolor=").append(tagColor);
    str.append("><B><font face=arial,sans-serif color=black size=-1>");
    str.append(txt).append("</font></B></td>");
  }


}
