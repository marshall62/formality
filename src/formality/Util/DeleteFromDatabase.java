package formality.Util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 12, 2010
 * Time: 2:49:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteFromDatabase extends DBConnectorSQLServer {

    String[] userIDs = {
"1401",
"1403",
"1410",
"1425",
"1426",
"1427"};

    String userIDLow = "1452";
    String userIDHigh = "1457";

  public String[] getDeleteUserQueries(String userID){
    String[] queries = new String[12];
    queries[0] = "delete from ModuleScore where UserID='"+userID+"'";
    queries[1] = "delete from ModuleLog where UserID='"+userID+"'";
    queries[2] = "delete from StudentModuleData where UserID='"+userID+"'";
    queries[3] = "delete from QuestionLog where UserID='"+userID+"'";
    queries[4] = "delete from HintLog where UserID='"+userID+"'";
    queries[5] = "delete from PreTestScores where UserID='"+userID+"'";
    queries[6] = "delete from PostTestScores where UserID='"+userID+"'";
    queries[7] = "delete from StudentModQuestionLinks where StudentModID IN  "+
              "(select moduleID from StudentModule where StudentID='"+userID+"')";
    queries[8] = "delete from StudentModule where StudentID='"+userID+"'";
    queries[9] = "delete from StudentFrameworkData where UserID='"+userID+"'";
    queries[10] = "delete from UserCourseLinks where UserID='"+userID+"'";
    queries[11] = "delete from UserTable where UserID='"+userID+"'";
    return queries;
   }

  public String[] getDeleteRangeUserQueries(String userIDLow, String userIDHigh) {
         String[] queries = new String[12];
    queries[0] = "delete from ModuleScore where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[1] = "delete from ModuleLog where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[2] = "delete from StudentModuleData where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[3] = "delete from QuestionLog where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[4] = "delete from HintLog where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[5] = "delete from PreTestScores where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[6] = "delete from PostTestScores where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[7] = "delete from StudentModQuestionLinks where StudentModID IN  "+
              "(select moduleID from StudentModule where StudentID>='"+userIDLow+"' and StudentID<='"+userIDHigh+"')";
    queries[8] = "delete from StudentModule where StudentID>='"+userIDLow+"' and StudentID<='"+userIDHigh+"'";
    queries[9] = "delete from StudentFrameworkData where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[10] = "delete from UserCourseLinks where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    queries[11] = "delete from UserTable where UserID>='"+userIDLow+"' and UserID<='"+userIDHigh+"'";
    return queries;
  }

   public void deleteIndividualUsers(String[] userIDs, Connection conn) throws Exception {
         for(int i=0; i<userIDs.length;i++){
             deleteUser(userIDs[i], conn);
             System.out.println("deleted user "+userIDs[i]);
         }
   }

   public void deleteUser(String userID, Connection conn) throws Exception{
       String[] deleteQueries = getDeleteUserQueries(userID);
       for(int i=0; i<deleteQueries.length;i++){
         String query = deleteQueries[i];
         runDeleteQuery(query, conn);
       }
   }

  public void runDeleteQuery(String query, Connection conn) throws Exception{
     Statement stmt=conn.createStatement();
      try{
     stmt.executeQuery(query);
      } catch (Exception ex){
          if(ex.getMessage().contains("[TDS Driver]No result sets were produced by "))
              ;
      }
     stmt.close();
  }

   public void deleteUsersInRange(String userIDLow, String userIDHigh, Connection conn) throws Exception {
       String[] deleteQueries = getDeleteRangeUserQueries(userIDLow, userIDHigh);
       for(int i=0; i<deleteQueries.length;i++){
         String query = deleteQueries[i];
         runDeleteQuery(query, conn);
       }
       System.out.println("deleted users "+userIDLow+" to "+userIDHigh);
   }

   public void run()throws Exception{
     Connection conn = getConnection();
     if(userIDs.length>0)
         deleteIndividualUsers(userIDs, conn);
     if(userIDLow != null && userIDHigh != null)
        deleteUsersInRange(userIDLow, userIDHigh, conn);
   }

    public static void main(String[] args) {
            //arg 1 is the table name, arg 2 is the output path: s:/user/
        DeleteFromDatabase app = new DeleteFromDatabase();
        try{ app.run();
        System.out.println("done");}
       catch (Exception e){ System.out.print("ERROR: "+e.getMessage());}
     }



}
