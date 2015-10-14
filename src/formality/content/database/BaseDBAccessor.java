package formality.content.database;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Feb 28, 2009
 * Time: 11:08:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class BaseDBAccessor {

//insert into Coach (Name, Image) values('How To Hound', '<img src="#3" alt="how to img">') select @@IDENTITY as 'ID'
    public static final String tagColor = "TagColor";

    public static final String sqlExceptionStr_ = "No result sets were produced";

    public static final String[] standardsDBNames_={"f4N1","f4N2","f4N3","f4N4","f4N5","f4N6","f4N7","f4N8","f4N9","f4N10","f4N11","f4N12",
    "f4N13","f4N14","f4N15","f4N16","f4N17","f4N18","f4P1","f4P2","f4P3","f4P4","f4P5","f4P6",
    "f4G1","f4G2","f4G3","f4G4","f4G5","f4G6","f4G7","f4G8","f4G9","f4M1","f4M2","f4M3","f4M4",
    "f4M5","f4D1","f4D2","f4D3","f4D4","f4D5","f4D6"};
   public static final String[] standardsNames_={"4.N.1","4.N.2","4.N.3","4.N.4",
    "4.N.5","4.N.6","4.N.7","4.N.8","4.N.9","4.N.10","4.N.11","4.N.12",
    "4.N.13","4.N.14","4.N.15","4.N.16","4.N.17","4.N.18","4.P.1","4.P.2","4.P.3","4.P.4","4.P.5","4.P.6",
    "4.G.1","4.G.2","4.G.3","4.G.4","4.G.5","4.G.6","4.G.7","4.G.8","4.G.9","4.M.1","4.M.2","4.M.3","4.M.4",
    "4.M.5","4.D.1","4.D.2","4.D.3","4.D.4","4.D.5","4.D.6"};

    public static final String[] alphaDBNames_={"a4N1","a4N2","a4N3","a4N4","a4N5","a4N6",
      "a4N7","a4N8","a4N9","a4N10","a4N11","a4N12","a4N13","a4N14","a4N15","a4N16","a4N17",
      "a4N18","a4P1","a4P2","a4P3","a4P4","a4P5","a4P6","a4G1","a4G2","a4G3","a4G4","a4G5",
      "a4G6","a4G7","a4G8","a4G9","a4M1","a4M2","a4M3","a4M4","a4M5","a4D1","a4D2","a4D3",
      "a4D4","a4D5","a4D6"};

    public static final String[] betaDBNames_={"b4N1","b4N2","b4N3","b4N4","b4N5","b4N6",
      "b4N7","b4N8","b4N9","b4N10","b4N11","b4N12","b4N13","b4N14","b4N15","b4N16","b4N17",
      "b4N18","b4P1","b4P2","b4P3","b4P4","b4P5","b4P6","b4G1","b4G2","b4G3","b4G4","b4G5",
      "b4G6","b4G7","b4G8","b4G9","b4M1","b4M2","b4M3","b4M4","b4M5","b4D1","b4D2","b4D3",
      "b4D4","b4D5","b4D6"};

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
