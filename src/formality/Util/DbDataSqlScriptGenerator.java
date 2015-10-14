package formality.Util;


import formality.parser.StringEncoder;
import formality.content.database.BaseDBAccessor;
import formality.content.database.BaseDBAccessor;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Mar 30, 2010
 * Time: 2:29:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbDataSqlScriptGenerator {
    String dbPrefix = "jdbc:inetdae7";
    String dbDriver = "com.inet.tds.TdsDriver";
    String dbName = "CESD4Mality";
    String dbPwd = "vc1m+JP0TN3qYSC3SwwT";
    String dbHost = "128.119.243.3:1439";
    String dbUser = "fourmality_login";
    String sqll7 = "true";
    String nowarnings = "false";
    int loginTimeout_ = 30;

    String[] tableNames = {
"District",
"TagColor",
"Category",
"Coach",
"Course",
"FrameworkStandards",
"Strategy",
"UserTable",
"Question",
"Hint",
"Module",
"ModuleScore",
"MeanModel",
"ResponseLog",
/*"QuestionLog", */
"HintLog",
"StudentFrameworkData",
"MultiChoiceAnswers",
"UserCourseLinks",
"QuestionFrameworkLinks",
"ModQuestionLinks",
"ModModuleLinks",
"ModCourseLinks",
"HintQuestionLinks"};



      //need a preceding null for auto increment
    String[] NeedNulltableNames = {
"FrameworkStandards",
"MeanModel",
"HintLog",
"ResponseLog",
"QuestionLog",
"TagColor",
};


   String tableName_;
   String outfilePathStr = "S:/User/4mality/reports/";

   public DbDataSqlScriptGenerator(String dbTableName, String outPath){
       tableName_ = dbTableName;
       outfilePathStr=outPath+dbTableName+".txt";
   }

  public DbDataSqlScriptGenerator(String outPath){
      outfilePathStr = outPath;
  }


   public void run()throws Exception{
     Connection conn = getConnection();
     StringBuffer insertStatements = new StringBuffer();
     processTable(tableName_, insertStatements, conn);
     System.out.println("processed table "+tableName_);
     writeResults(insertStatements.toString(), outfilePathStr);
   }

   public void runMulti()throws Exception{
     String outputFileStr = outfilePathStr+"FormalityDataLoad.txt";
     Connection conn = getConnection();
     StringBuffer insertStatements = new StringBuffer();
     for(int i=0;i<tableNames.length;i++){
         tableName_ = tableNames[i];
         insertStatements.append("\n\n/* Table ").append(tableName_).append("   */\n\n");
         processTable(tableName_, insertStatements, conn);
         System.out.println("processed table "+tableName_);
     }
     writeResults(insertStatements.toString(), outputFileStr);
   }

    // INSERT INTO CUSTOMER VALUES('10010','Ramas','Alfred','A','615','844-2573','0');
   public void processTable(String tableName, StringBuffer insertStatements,
                             Connection conn) throws Exception{
    boolean needsNull = lookupNullTable(tableName);
    ArrayList<String> cols = getColumns(tableName, conn);
    Statement stmt=conn.createStatement();
    String query = "select * from "+tableName;
    ResultSet res=stmt.executeQuery(query.toString());
    while(res.next()){
      insertStatements.append("insert into ").append(tableName).append(" values(");
      if(needsNull)
         insertStatements.append("NULL,");
      for(int i=0;i<cols.size();i++){
         if(i>0)
          insertStatements.append(",");
         String curColName = cols.get(i);
         String colData = res.getString(curColName);
//kludge until the table gets fixed
if(tableName.equals("ResponseLog"))
     colData = colData.trim();
         colData = BaseDBAccessor.repSingleQuote(colData);
         if(colData==null || colData.equalsIgnoreCase("null"))
            insertStatements.append(colData);
         else if(isTimestampField(curColName)){
           colData = getDateTimeValue(colData);
           if(tableName.equals("HintLog") && curColName.equals("QuestionStartTS")){
              if(colData.equals("0")){
                 colData="NULL";
                 insertStatements.append(colData);
              }
             else
               insertStatements.append("'").append(colData).append("'");
           }
           else
               insertStatements.append("'").append(colData).append("'");
         }
         else
           insertStatements.append("'").append(colData).append("'");
      }
      insertStatements.append(");\n");
    }
    res.close();
    stmt.close();
   }

  public boolean lookupNullTable(String tableName){
      boolean found = false;
      for(int i=0;i<NeedNulltableNames.length;i++){
          if(NeedNulltableNames[i].equals(tableName))
              found=true;
      }
      return found;
  }

  public ArrayList<String> getColumns(String tableName,
                                      Connection conn)throws Exception{
      ArrayList<String> cols = new ArrayList<String>();
      String query = "select col.name from sysobjects obj inner join syscolumns col on obj.id = col.id where obj.name = '"+tableName+"'";
      Statement stmt=conn.createStatement();
      ResultSet res=stmt.executeQuery(query.toString());
      while(res.next()){
        cols.add(res.getString(1));
      }
      res.close();
      stmt.close();
      return cols;
  }

   public Connection getConnection()throws Exception{
       Connection conn = null;
     try{
        //String urlstr = getDbUrlString();
        String urlstr = "jdbc:inetdae7:128.119.40.96:1439?sq17=true&nowarnings=false&password=vc1m+JP0TN3qYSC3SwwT&user=fourmality_login&database=CESD4Mality";
        int loginTimeout_ = 30;
        DriverManager.setLoginTimeout(loginTimeout_);
        Class.forName(dbDriver);
        conn = DriverManager.getConnection(urlstr, null, null);
     }catch(Exception ex){
         System.out.print(ex.getMessage());
     }
       return conn;
   }

    public String getDbUrlString()throws Exception {
         StringBuffer dbUrlstr = new StringBuffer();
         String dbpwd = StringEncoder.decode(dbPwd);
         dbUrlstr.append(dbPrefix).append(":").append(dbHost).append("?");
         dbUrlstr.append("sq17=").append(sqll7).append("&");
         dbUrlstr.append("nowarnings=").append(nowarnings).append("&");
         dbUrlstr.append("password=").append(dbpwd).append("&");
         dbUrlstr.append("user=").append(dbUser).append("&");
         dbUrlstr.append("database=").append(dbName);
         return dbUrlstr.toString();
      }

   public boolean isTimestampField(String curColName){
       boolean found = false;
       for(int i=0;i<curColName.length();i++){
          char c = curColName.charAt(i);
           if(c=='T' && !found && (i+1<curColName.length()))
              found = (curColName.charAt(i+1)=='S');
       }
      return found;
   }

  public String getDateTimeValue(String colData){
    if(colData==null || colData.length()<6)
      return colData;
    ReportDataUtils rdu = new ReportDataUtils();
    Long temp = new Long(colData);
    return rdu.getDateTimeStringYYYYMMDD(temp.longValue(), true, true, true);
  }

    public void writeResults(String report, String outfileStr) throws Exception{
         PrintStream out = System.out;
         out = new PrintStream(new FileOutputStream(outfileStr));
         out.print(report);
         out.close();
    }

     public static void main(String[] args) {
           //arg 1 is the output path: s:/user/, arg 2 is the table name
         //if arg 2 omitted, use the table names array in the class
       DbDataSqlScriptGenerator app;
       String path = args[0];
       if(args.length>1){
           app = new DbDataSqlScriptGenerator(args[0], args[1]);
           try{ app.run();
             System.out.println("done");}
           catch (Exception e){ System.out.print("ERROR: "+e.getMessage());}
       }
       else{
           app = new DbDataSqlScriptGenerator(args[0]);
           try{ app.runMulti();
            System.out.println("done");}
            catch (Exception e){ System.out.print("ERROR: "+e.getMessage());}
       }
    }

}
