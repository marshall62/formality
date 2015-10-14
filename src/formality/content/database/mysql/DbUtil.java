package formality.content.database.mysql;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 4, 2010
 * Time: 11:07:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbUtil {

   public static final String[] badDBStrings_ =
              {"select", "drop", ";", "--", "insert", "delete", "xp_"};    

   public static String safeStringCheck(String inputStr){
     inputStr = killChars(inputStr);
     inputStr = repSingleQuote(inputStr);
     return inputStr;
   }

   public static String killChars(String inputStr) {
      if(inputStr == null || inputStr.equals(""))
             return "";
      String safeStr = "";
      for(int i=0;i<badDBStrings_.length;i++){
        inputStr = inputStr.replace(badDBStrings_[i], "");
      }
     return inputStr;
    }

   public static String repSingleQuote(String inputStr){
     if(inputStr!=null)
       inputStr = inputStr.replace("'", "''");
     return inputStr;
   }
}
