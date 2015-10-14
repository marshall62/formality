package formality.admin;

import com.Ostermiller.util.CSVParser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import formality.model.UserInfo;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 13, 2010
 * Time: 9:47:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudentAdmin extends AdminUser {

    /**
     * Insert a new student given the minimal set of inputs necessary.   All other fields will default to NULL
     * @param conn
     * @param student
     * @return
     */
    public int insertNewUserToDb(Connection conn, StudentUser student) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into usertable (login, password, accessLevel, institution, districtId, " +
                    "cond, age,nativeLang, title1, iep, gender,initials,mathLevel,readingLevel) " +
                    "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,student.loginName);
            stmt.setString(2,student.password);
            stmt.setInt(3,1); // student access level is 1
            stmt.setString(4,student.institution);
            stmt.setInt(5,student.districtID);
            stmt.setInt(6,0);  // cond is 0 for students (??)
            stmt.setInt(7,student.age);
            if (student.nativeLang != null)
                stmt.setString(8,student.nativeLang);
            else stmt.setNull(8, Types.VARCHAR);
            if (student.title1 != null)
                stmt.setString(9,student.title1);
            else stmt.setNull(9,Types.VARCHAR);
            if (student.iep != null)
                stmt.setString(10,student.iep);
            else stmt.setNull(10,Types.VARCHAR);
            if (student.gender != null)
                stmt.setString(11,student.gender);
            else stmt.setNull(11,Types.VARCHAR);
            if (student.initials !=null)
                stmt.setString(12,student.initials);
            else stmt.setNull(12,Types.VARCHAR);
            if (student.mathRating !=null)
                stmt.setString(13,student.mathRating);
            else stmt.setNull(13,Types.VARCHAR);
            if (student.readingRating !=null)
                stmt.setString(14,student.readingRating);
            else stmt.setNull(14,Types.VARCHAR);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }

     public int insertNewUser(Connection conn, User student) throws SQLException {
         int id = insertNewUserToDb(conn, (StudentUser) student);
         insertStudentFrameworkDataToDb(conn,id);
         return id;
     }



    /**
     * When a new student is created, this adds in some initial values for his framework scores.
     * @param conn
     * @param id
     * @throws SQLException
     */
    public void insertStudentFrameworkDataToDb (Connection conn, int id) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into StudentFrameworkData (UserID,f4D6,f4D5,f4D4,f4D3,f4D2,f4D1,f4M5,f4M4,f4M3,\n" +
                    "f4M2,f4M1,f4G9,f4G8,f4G7,f4G6,f4G5,f4G4,f4G3,f4G2,f4G1,f4P6,f4P5,f4P4,f4P3,f4P2,f4P1,\n" +
                    "f4N18,f4N17,f4N16,f4N15,f4N14,f4N13,f4N12,f4N11,f4N10,f4N9,f4N8,f4N7,f4N6,f4N5,f4N4,\n" +
                    "f4N3,f4N2,f4N1,a4N1,a4N2,a4N3,a4N4,a4N5,a4N6,a4N7,a4N8,a4N9,a4N10,a4N11,a4N12,a4N13,\n" +
                    "a4N14,a4N15,a4N16,a4N17,a4N18,a4P1,a4P2,a4P3,a4P4,a4P5,a4P6,a4G1,a4G2,a4G3,a4G4,a4G5,\n" +
                    "a4G6,a4G7,a4G8,a4G9,a4M1,a4M2,a4M3,a4M4,a4M5,a4D1,a4D2,a4D3,a4D4,a4D5,a4D6,b4N1,b4N2,\n" +
                    "b4N3,b4N4,b4N5,b4N6,b4N7,b4N8,b4N9,b4N10,b4N11,b4N12,b4N13,b4N14,b4N15,b4N16,b4N17,\n" +
                    "b4N18,b4P1,b4P2,b4P3,b4P4,b4P5,b4P6,b4G1,b4G2,b4G3,b4G4,b4G5,b4G6,b4G7,b4G8,b4G9,b4M1,\n" +
                    "b4M2,b4M3,b4M4,b4M5,b4D1,b4D2,b4D3,b4D4,b4D5,b4D6) values (?,'0.5','0.5','0.5','0.5',\n" +
                    "'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',\n" +
                    "'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5',\n" +
                    "'0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.5','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001','0.001',\n" +
                    "'0.001','0.001')";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,id);
            stmt.execute();
        }

        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }

    private int computeAge (String dob) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        String[] mdy = dob.split("/");
        c.add(Calendar.MONTH,Integer.parseInt(mdy[0]) * -1);
        c.add(Calendar.DATE,Integer.parseInt(mdy[1]) * -1);
        c.add(Calendar.YEAR,Integer.parseInt(mdy[2]) * -1);
        return c.get(Calendar.YEAR);
    }

    private String getIEP (String unspec, String ela, String math) {
        if (unspec.equalsIgnoreCase("X"))
            return "unspecified";
        else {
            StringBuffer sb = new StringBuffer();
            int count=0;
            if (ela.equalsIgnoreCase("X")) {
                sb.append("ela");
                count++;
            }
            if (math.equalsIgnoreCase("X")) {
                sb.append((count > 0) ? "," : "").append("math");
                count++;
            }
            if (count == 0)
                return null;
            else return sb.toString();
        }

    }

    private String getTitle1 (String unspec, String ela, String math) {
        return getIEP(unspec,ela,math);
    }

 //login, Initials,Age,Gender,Language,IEP-Math, IEP-Reading, Title1,Reading Level,Math Level
    public List<User> readStandardStudentCSVFile(String filename) throws Exception {
        List<User> students = new ArrayList<User>();
        // Parse the data
        int i=0;
        try {
        String[][] values = CSVParser.parse(filename);
        // Display the parsed data

        for (i=0; i<values.length; i++) {
            String classId = "57";
            String inits = values[i][0];
            String age = values[i][1];
            String gender = values[i][2];
            String nativeLang = values[i][3];
            String iep_unspec = values[i][4];
            String institution = "Plainville School";
            String district = "Plainville";
            String readingLev = values[i][5];
            String mathLev = values[i][6];
            String loginName = values[i][7];
            String password = loginName;
            String iep_ela = "";
            String iep_math = "";
            students.add(new StudentUser(institution,loginName,password,district,"",nativeLang,
                    gender, Integer.parseInt(age),iep_unspec, inits));
        }
        } catch (Exception e) {
            System.out.println("Failed in processing input file line " + i+1);
            throw(e);
        }
        return students;
    }


    // CSV file in the form:
    //login, Initials,Age,Gender,Language,IEP-Math, IEP-Reading, Title1,Reading Level,Math Level
    public List<User> readStudentCSVFile2(String filename) throws Exception {
        List<User> students = new ArrayList<User>();
        // Parse the data
        int i=0;
        try {
        String[][] values = CSVParser.parse(new FileReader(filename));
        // Display the parsed data

        for (i=0; i<values.length; i++) {
            String classId = "57";
            String inits = values[i][0];
            String age = values[i][1];
            String gender = values[i][2];
            String nativeLang = values[i][3];
            String iep_unspec = values[i][4];
            String institution = "Plainville School";
            String district = "Plainville";
            String readingLev = values[i][5];
            String mathLev = values[i][6];
            String loginName = values[i][7];
            String password = loginName;
            String iep_ela = "";
            String iep_math = "";
            students.add(new StudentUser(institution,loginName,password,district,"",nativeLang,
                    gender, Integer.parseInt(age),iep_unspec, inits));
        }
        } catch (Exception e) {
            System.out.println("Failed in processing input file line " + i+1);
            throw(e);
        }
        return students;
    }

    // CSV file in the form:
    // loginName, password, initials, age , gender, nativeLang [English], IEP, title1, mathRating, readingRating
    // See UserInfo enum IEP, Level, Title for legal values for final 5 fields
    public static List<User> readStudentCSVStream(InputStream csvStream, String institution, String districtName) throws Exception {
        List<User> students = new ArrayList<User>();
        // Parse the data
        int i=0;
        try {
        String[][] values = CSVParser.parse(new InputStreamReader(csvStream));

        // Skipping the first row in the values.  This row MUST be a set of column headers.
        // I want the col headers in the file so that authors can use Excel, leave blanks in various
        // cells and then just export as CSV without having to worry about the number of commas
        for (i=1; i<values.length; i++) {
            String loginName = values[i][0];
            String password = values[i][1];
            password = !password.equals("") ? password : loginName;
            String inits = values[i][2];
            String agestr = values[i][3];
            int age = (!agestr.equals(""))? Integer.parseInt(agestr) : 10;   // default age is 10
            String gender = values[i][4];
            String nativeLang = values[i][5];
            UserInfo.Title title1 = !values[i][6].equals("") ? UserInfo.Title.valueOf(values[i][6]) : UserInfo.Title.unspecified;
            UserInfo.IEP iep = !values[i][7].equals("") ? UserInfo.IEP.valueOf(values[i][7]) : UserInfo.IEP.unknown;
            UserInfo.Level mathLev = !values[i][8].equals("") ? UserInfo.Level.valueOf(values[i][8]) : UserInfo.Level.unknown;
            UserInfo.Level readingLev = !values[i][9].equals("") ? UserInfo.Level.valueOf(values[i][9]) : UserInfo.Level.unknown;
            students.add(new StudentUser(institution,loginName,password,districtName,title1.toString(),nativeLang,age,gender,inits,iep.toString(),
                   mathLev.toString(),readingLev.toString()));
        }
        } catch (Exception e) {
            int lineNum= i+1;
            System.out.println("Failed in processing input file line " + lineNum);
            throw new Exception("Failure during parse of input file on line " + lineNum, e);
        }
        return students;
    }



     //Login name	Class	Initials	Gender	DOB	Rdg Level	Math Level	Home Lang	ELL	IEP-unspec	IEP-ELA	IEP-Math	Title-unspec	Title-ELA	Title-Math

    // CSV file in the form:
    // loginName, password, institution, district name, title1 [yes/no], nativeEnglish [yes/no], Age, SPED [yes/no]
    public List<User> readStudentCSVFile(String filename) throws IOException {
        List<User> students = new ArrayList<User>();
        // Parse the data
        String[][] values = CSVParser.parse(new FileReader(filename));
        // Display the parsed data
        for (int i=0; i<values.length; i++) {
            String loginName = values[i][0];
            String password = loginName;
            String classId = values[i][1];
            String inits = values[i][2];
            String gender = values[i][3];
            String institution = "Greenfield Middle School";
            String district = "Greenfield";
            String dob = values[i][4];
            int age = computeAge(dob);
            String readingLev = values[i][5];
            String mathLev = values[i][6];
            String nativeLang = values[i][7];
            String ell = values[i][8];
            String iep_unspec = values[i][9];
            String iep_ela = values[i][10];
            String iep_math = values[i][11];
            String iep = getIEP(iep_unspec,iep_ela,iep_math);
            String title_unspec = values[i][12];
            String title_ela = values[i][13];
            String title_math = values[i][14];
            String title1 = getTitle1(title_unspec,title_ela,title_math);
            students.add(new StudentUser(institution,loginName,password,district,title1,nativeLang, gender, age,iep, inits));
        }
        return students;
    }

    // Deletes all tables associated with a student.
    // First get rid of the studentframeworkdata
    // Then use the superclass method to get rid of the other rows.
    public void deleteStudents (Connection conn, int minId, int maxId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from studentframeworkdata where  userId>=? and userId<=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,minId);
            stmt.setInt(2,maxId);
            stmt.executeUpdate();
            super.deleteUsers(conn,minId,maxId);
            // Now get rid of all the log data
            for (int i=minId;i<=maxId;i++)
                clearStudentData(conn,i);
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }




    public void clearStudentData (Connection conn, int studId) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from responselog where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from eventlog where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from modulescore where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
            q = "delete from hintlog where userid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }



    // A temporary main method that allows me to fix users that don't have
    // stduent framework data rows because something (e.g. gender) wasn't provided
    // correctly when the student file was uploaded and parsed and the system
    // is not correctly catching it and instead building an incomplete user.
    // TODO need to make teh upload more robust so this does not happen.
    public static void main (String[] args) {
        StudentAdmin a = new StudentAdmin();
        try {
            int n = args.length;
            Connection conn = a.getConnection();
            for (int i=0;i<n;i++) {
                String sid = args[i];
                a.insertStudentFrameworkDataToDb(conn,Integer.parseInt(sid));
                System.out.println("Updated student " + sid);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static void mainX (String[] args) {
        String csvfilename = args[0];
        StudentAdmin a = new StudentAdmin();
        try {
            List<User> students = a.readStudentCSVFile2(csvfilename);
            Connection conn = a.getConnection();
            a.doInsert(conn,students);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
}
