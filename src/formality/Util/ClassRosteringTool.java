package formality.Util;

import formality.FileReaderWriter;
import formality.parser.StringEncoder;
import formality.content.database.mysql.DBAccessor;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Feb 5, 2006
 * Time: 3:02:33 PM
 *
 * This class reads a tab-delimited roster file (see below) and enters students
 * into the UserTable, the UserCourseLinks table, and initializes the StudentFrameworkData
 * table. An existing classID is required.
 *
 * This class is for SQL server databses
 *
 * NOTE: a class must be created in the database before rostering may proceed.
 * Each Login must be unique, so the classID will be appended to the login supplied
 * in the roster file.
 *
 * This class reads in a tab delimited text file with the following headings:
 * Login	Password	FirstName	LastName	Institution   Gender
 *
 * NOTE: delete any header fields in the file- only data rows.  To add fields you have to
 * change this code!
 *
 * The code then enters each student into the UserTable. These are the fields in that table:
 * `UserID` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `Login` VARCHAR(50) NULL ,
  `Password` VARCHAR(50) NULL ,
  `FirstName` VARCHAR(50) NULL ,
  `LastName` VARCHAR(50) NULL ,
  `AccessLevel` INT NULL ,
  `Institution` VARCHAR(50) NULL ,
  `Gender` VARCHAR(50) NULL ,
  `Age` INT NULL ,
  `NativeEnglish` TINYINT NULL ,
  `PrevExperience` TINYINT NULL ,
  `SessionTS` DATETIME NULL ,
  `State` VARCHAR(100) NULL ,
  `PreTestIndex` INT NULL ,
  `PostTestIndex` INT NULL ,
  `DistrictID` INT NOT NULL FOREIGN KEY references DISTRICT DistrictID,
  `ModRecord` VARCHAR(100) NULL ,
  `Condition` SMALLINT NULL ,

 *
 *
 */
public class ClassRosteringTool extends DBConnectorSQLServer {
    /* Districts
    1	Amherst
    2	Springfield
    3	Greenfield
    4	Hudson
    5   All
    6   Northampton
*/

 //default values
    String Age="9";
    String Access="1";
    String NativeEnglish="1";
    String PrevExperience="0";
    String District="1";
    String Condition="0";

    int totalRegistered_ = 0;

  public ClassRosteringTool(){}

  public void rosterClass(String classID, String inputFile)throws Exception {
    ArrayList<String> studentData = new ArrayList<String>();
    //put each row of the file into the array
    readRosterFile(inputFile, studentData);
    // add defaults to array and create insert statements
    ArrayList<String> insertStatements = createInsertStatements(studentData, classID);
    Connection conn = null;
    try{
    conn = getConnection();
    } catch(Exception ex){
        throw new Exception(ex.getMessage()+"\n 0 records entered");
    }
    try{
        rosterAllStudents(classID, insertStatements, conn);
        System.out.println("total registered = "+getTotalRegistered()+" for classID "+classID);
    } catch(Exception ex){
        throw new Exception(ex.getMessage()+"\n"+totalRegistered_+" records entered");
    }
  }

  public void rosterAllStudents(String classID,
                               ArrayList<String> insertStmnts,
                               Connection conn)throws Exception{

     for(int i=0;i<insertStmnts.size();i++){
        String insertStr = insertStmnts.get(i);
        String studentID = saveNewStudent(classID, insertStr, conn);
        initStudentTables(classID, studentID, conn);
        totalRegistered_++;
     }
 }

  public void initStudentTables(String classID,
                                String studentID,
                                Connection conn)throws Exception{
        linkStudentToClass(studentID, classID, conn);
        initStudentSkills(studentID,conn);
        updateStudentSkills(studentID,conn);
        initStudentAlpha(studentID,conn);
        initStudentBeta(studentID,conn);
   }

  //returns the userid
  public String saveNewStudent(String classID,
                               String insertQuery,
                               Connection conn) throws Exception{
      Statement stmt=conn.createStatement();
      int sID = 0;
      try{stmt.executeQuery(insertQuery);}
      catch(Exception ex) {String msg=ex.getMessage();
         if(!msg.contains("No result sets "))
               throw ex;}
       stmt.close();
       try{
           sID=DBAccessor.getInsertionID("UserID", "UserTable", conn);
       } catch(Exception ex) {String msg=ex.getMessage();
           if(!msg.equals("No result sets "))
               throw ex;}
        return Integer.toString(sID);
 }

  public void linkStudentToClass(String sID, String cID,
                                 Connection conn)throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("insert into UserCourseLinks (UserID, CourseID) ");
    query.append("values ('").append(sID).append("', '").append(cID).append("')");
    Statement stmt=conn.createStatement();
    String qs=query.toString();
    try{
          stmt.executeQuery(qs);}
          catch(Exception sex) {
            String msg=sex.getMessage();
            if(!msg.contains("No result sets "))
               throw sex;
      }
     stmt.close();
  }

  public void initStudentSkills(String sID,
                                   Connection conn)throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("insert into StudentFrameworkData ");
    query.append("(UserID,f4D6,f4D5,f4D4,f4D3,f4D2,f4D1,f4M5,f4M4,");
    query.append("f4M3,f4M2,f4M1,f4G9,f4G8,f4G7,f4G6,f4G5,f4G4,f4G3,f4G2,f4G1,f4P6,f4P5,f4P4,f4P3,f4P2,");
query.append("f4P1,f4N18,f4N17,f4N16,f4N15,f4N14,f4N13, f4N12,f4N11,f4N10,f4N9,f4N8,f4N7,f4N6,f4N5,");
query.append("f4N4,f4N3,f4N2,f4N1) values ('").append(sID).append("','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',");
query.append("'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',");
query.append("'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',");
query.append("'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5')");
      Statement stmt=conn.createStatement();
      String qs=query.toString();
      try{
            stmt.executeQuery(qs);}
            catch(Exception sex) {
              String msg=sex.getMessage();
              if(!msg.contains("No result sets "))
                 throw sex;
        }
       stmt.close();
    }

  public void updateStudentSkills(String sID,
                                   Connection conn)throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("update StudentFrameworkData set f4P1='0.6867',f4P2='0.55',f4P3='0.683',f4P4='0.695',");
query.append("f4P5='0.74',f4P6='0.705',f4N1='0.865',f4N2='0.695',f4N3='0.775',f4N4='0.555',f4N5='0.62',");
query.append("f4N6='0.585',f4N7='0.72',f4N8='0.715',f4N9='0.68',f4N10='0.748',f4N11='0.689',f4N12='0.8',");
query.append("f4N13='0.689',f4N14='0.689',f4N15='0.689',f4N16='0.689',f4N17='0.545',f4N18='0.42',f4M1='0.665',");
query.append("f4M2='0.635',f4M3='0.695',f4M4='0.665',f4M5='0.665',f4D1='0.81',f4D2='0.79',f4D3='0.86',");
query.append("f4D4='0.765',f4D5='0.73',f4D6='0.73',f4G1='0.675',f4G2='0.665',f4G3='0.61',f4G4='0.665',");
query.append("f4G5='0.66',f4G6='0.665',f4G7='0.665',f4G8='0.66',f4G9='0.71'");
query.append("where UserID='").append(sID).append("'");
      Statement stmt=conn.createStatement();
            String qs=query.toString();
            try{
                  stmt.executeQuery(qs);}
                  catch(Exception sex) {
                    String msg=sex.getMessage();
                    if(!msg.contains("No result sets "))
                       throw sex;
              }
             stmt.close();
          }

/* this query updates ALL student alpha */
  public void initStudentAlpha(String sID,
                                   Connection conn)throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("update StudentFrameworkData set a4N1='2.595',a4N2='2.085',a4N3='2.325',a4N4='1.665',");
    query.append("a4N5='1.86',a4N6='1.755',a4N7='2.16',a4N8='2.145',a4N9='2.04',a4N10='2.244',a4N11='2.067',");
    query.append("a4N12='2.4',a4N13='2.067',a4N14='2.067',a4N15='2.067',a4N16='2.067',a4N17='1.635',a4N18='1.26',");
    query.append("a4P1='2.0601',a4P2='1.65',a4P3='2.049',a4P4='2.085',a4P5='2.22',a4P6='2.115',a4G1='2.025',");
    query.append("a4G2='1.995',a4G3='1.83',a4G4='1.995',a4G5='1.98',a4G6='1.995',a4G7='1.995',a4G8='1.98',");
    query.append("a4G9='2.13',a4M1='1.995',a4M2='1.905',a4M3='2.085',a4M4='1.995',");
    query.append("a4M5='1.995',a4D1='2.43',a4D2='2.37',a4D3='2.58',a4D4='2.295',a4D5='2.19',a4D6='2.19'");
    query.append("where UserID='").append(sID).append("'");
Statement stmt=conn.createStatement();
      String qs=query.toString();
      try{
            stmt.executeQuery(qs);}
            catch(Exception sex) {
              String msg=sex.getMessage();
              if(!msg.contains("No result sets "))
                 throw sex;
        }
       stmt.close();
    }


/* this query updates ALL student beta  */
  public void initStudentBeta(String sID,
                                   Connection conn)throws Exception{
    StringBuffer query=new StringBuffer();
    query.append("update StudentFrameworkData set b4N1='0.405',b4N2='0.915',b4N3='.675',b4N4='1.335',");
    query.append("b4N5='1.14',b4N6='1.245',b4N7='.84',b4N8='0.855',b4N9='0.96',b4N10='.756',b4N11='.933',");
    query.append("b4N12='.6',b4N13='.933',b4N14='.933',b4N15='.933',b4N16='.933',b4N17='1.365',b4N18='1.74',");
    query.append("b4P1='.9399',b4P2='1.35',b4P3='.951',b4P4='.915',b4P5='.78',b4P6='.885',b4G1='.975',");
    query.append("b4G2='1.005',b4G3='1.17',b4G4='1.005',b4G5='1.02',b4G6='1.005',b4G7='1.005',b4G8='1.02',");
    query.append("b4G9='.87',b4M1='1.005',b4M2='1.095',b4M3='.915',b4M4='1.005',b4M5='1.005',b4D1='.57',");
    query.append("b4D2='.63',b4D3='.42',b4D4='.705',b4D5='.81',b4D6='.81'");
    query.append("where UserID='").append(sID).append("'");
    Statement stmt=conn.createStatement();
      String qs=query.toString();
      try{
            stmt.executeQuery(qs);}
            catch(Exception sex) {
              String msg=sex.getMessage();
              if(!msg.contains("No result sets "))
                 throw sex;
        }
       stmt.close();
    }

    public String escapeSQL(String input){
        String output = input.replace("'","''");
        return output;
    }

    public void readRosterFile(String inputFile, ArrayList<String> studentData)throws Exception{
      FileReaderWriter rw=new FileReaderWriter();
      try{
       rw.readFileToArray(inputFile, studentData);
      } catch(Exception ex){
        throw new Exception(ex.getMessage()+"\n Couldn't read roster file- 0 records entered.");
       }
    }

    public ArrayList<String> createInsertStatements(ArrayList<String> studentData,
                                                   String classID) throws Exception{
       ArrayList<String> insertStmnts = new ArrayList<String> ();
       for(int i=0;i<studentData.size();i++){
          String fileRowStr = studentData.get(i);
          String insertStr = getStudentInsertStatement(fileRowStr, classID);
          insertStmnts.add(insertStr);
       }
      return insertStmnts;
    }

    public String getStudentInsertStatement(String fileRowStr, String classID)throws Exception {
      StringTokenizer tokens = new StringTokenizer(fileRowStr, "\n\t\r");
      StringBuffer query = new StringBuffer();
      query.append("INSERT INTO UserTable (Login, Password, FirstName, LastName, ");
      query.append("AccessLevel, Institution, Gender, Age, NativeEnglish, PrevExperience, District, Condition) ");
      query.append("values (");
      // the row fields are:
     //Login	Password	FirstName	LastName	Institution   Gender
      int fieldCt = 0;
      while (tokens.hasMoreTokens()){
         if(fieldCt==0) {  //append the classID to the login
           String value = escapeSQL(tokens.nextToken());
           query.append("'").append(value).append(classID).append("', ");
         }
         else if(fieldCt==4)
            query.append("'").append(Access).append("', ");
         else{
           String value =  escapeSQL(tokens.nextToken());
           query.append("'").append(value).append("', ");
         }
         fieldCt++;
      }
      if(fieldCt<7)
         throw new Exception("Error Parsing Roster File: incorrect number of fields in row "+fileRowStr);
      query.append("'").append(Age).append("', ");
      query.append("'").append(NativeEnglish).append("', ");
      query.append("'").append(PrevExperience).append("', ");
      query.append("'").append(District).append("', ");
      query.append("'").append(Condition).append("')");
      return query.toString();
    }

    public int getTotalRegistered() {
        return totalRegistered_;
    }

    /** This is a temporary main method written so that I can repair some users that didn't get there skill data
     * initialized correctly due to a bug (missing gender caused no skill data to be built).
     * @param args
     */
    public static void main(String[] args) {
        ClassRosteringTool rt = new ClassRosteringTool();
       // args: classID, rosterFile
        if(args.length !=1){
          System.out.println("Required args: studID");
        } else{
          String studId = args[0];
          try{
              rt.initStudentSkills(studId,rt.getConnection());
              rt.updateStudentSkills(studId,rt.getConnection());
              rt.initStudentAlpha(studId,rt.getConnection());
              rt.initStudentBeta(studId,rt.getConnection());
              System.out.println("Student" + args[0]  + " repaired");
         } catch (Exception e){ System.out.print("ERROR: "+e.getMessage());}
        }
    }

    public static void mainX(String[] args) {
        ClassRosteringTool rt = new ClassRosteringTool();
       // args: classID, rosterFile
        if(args.length !=2){
          System.out.println("Required args: classID, rosterFile");
        } else{
          String cid = args[0];
          String rosterFile = args[1];
          try{
              rt.rosterClass(cid, rosterFile);
         } catch (Exception e){ System.out.print("ERROR: "+e.getMessage());}
        }
    }
}
