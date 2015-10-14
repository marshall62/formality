package formality.reports;

import formality.content.database.mysql.DBAccessor;
import formality.content.database.BaseDBAccessor;

import java.sql.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Dec 30, 2010
 * Time: 4:07:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Report3 extends StudentReport {

    public List<List<String>> writeUsers(int buid, int euid, Connection conn) throws Exception {
        PreparedStatement stmt = null;

        try {
            List<List<String>> results;
            String q = "select userid,login,initials from usertable where userid>=? and userid<=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, buid);
            stmt.setInt(2, euid);
            results = doQuery(conn, stmt);
            return results;
        } finally {
            stmt.close();

        }
    }

    public List<List<String>> writeCourse(int courseId, Connection conn) throws Exception {
        PreparedStatement stmt = null;

        try {
            List<List<String>> results;
            String q = "select u.userid,login,initials from usertable u, usercourselinks l where u.userId=l.userId and l.courseId=? and u.AccessLevel=1";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, courseId);
            results = doQuery(conn,stmt);
            return results;
        }
        finally {
            stmt.close();

        }
    }

    public List<List<String>> doQuery(Connection conn, PreparedStatement stmt ) throws Exception {
        ResultSet rs = null;
        try {
            List<List<String>> results = new ArrayList<List<String>>();
            results.add(getReportTitle());            
            rs = stmt.executeQuery();
            List<String> row = new ArrayList<String>();
            addRowValues(row,new String[]{"userid","login","initials","module id", "module name", "type", "module score", "survey responses" });
            results.add(row);
            while (rs.next()) {
//                row = new ArrayList<String>();
//                results.add(row);
                int id = rs.getInt("userid");
                String studId = Integer.toString(id);
                String login = rs.getString("login");
                String initials = rs.getString("initials");
                if (initials != null)
                    initials = initials.replace(',',' ');
                else initials = "";
//                addRowValues(row, new String[] {studId, login, initials});
                addPerModuleInfo(conn, results, id,login,initials);

            }
            return results;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }


    private void addPerModuleInfo(Connection conn, List<List<String>> rows, int studId, String login, String initials) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q;
            if (this.modIds.length == 0)
                q= "select modid, m.name, modType from eventlog, module m where userid=?  and modid=m.moduleid group by modid";
            else q= "select modid, m.name, modType from eventlog, module m where userid=?  and modid=m.moduleid and m.moduleid in (" +getModuleIdList()+ ") group by modid";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                List row = new ArrayList<String>();
                 addRowValues(row, new String[] {Integer.toString(studId), login, initials});
//                addRowValues(row,new String[]{"","",""});
                int modId= rs.getInt("modid");
                row.add(Integer.toString(modId));
                String n = rs.getString("m.name");
                row.add(n);
                String type = rs.getString("modType");
                row.add(type);
                addModuleScore(conn,row,modId,studId, type.equals("Practice"));
                rows.add(row);
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    private void addModuleScore(Connection conn, List row, int modId, int studId, boolean isPractice) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select isCorrect,userInput,eventType,qid from eventlog where userid=? and (eventType='survey.evalq' or eventType='evalq' or eventType='evaltq' or eventType='survey.evaltq') and modId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,studId);
            stmt.setInt(2,modId);
            rs = stmt.executeQuery();
            StringBuffer surveyAnswers = new StringBuffer();
            int nQuestions=0;
            int nCorrect=0;
            while (rs.next()) {
                boolean isCorrect= rs.getBoolean(1);
                String input =rs.getString(2);
                String eventType =rs.getString(3);
                int qid = rs.getInt(4);
                if (eventType.equals("survey.evalq") || eventType.equals("survey.evaltq"))
                    surveyAnswers.append("(" + qid + " " + input + ") |");
                else {
                    nQuestions++;
                    if (isCorrect) nCorrect++;
                }

            }
            if (surveyAnswers.length() > 0)
                surveyAnswers.deleteCharAt(surveyAnswers.length()-1);
            if (!isPractice)
                row.add(String.format("%5.2f",(nCorrect / ((double) nQuestions) * 100.0)));
            else row.add("");
            row.add(surveyAnswers.toString());


        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }


    /**
     * Sample program params for a class 52 with some specified modules
     * Report3 -c:52 -f:u:/formality/Greenfield2010/CyczReport3-GeoProbGraph.csv -m:237,246,247,250,252,259,261,265,268
     * @param args
     */

    public static void main(String[] args) {
        Report3 r = new Report3();
        try {
            SimpleJSAP jsap = new SimpleJSAP("Report3", "Report 3", new Parameter[]{
                    new QualifiedSwitch("beginUserId", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'b', "buid",
                            "Takes a begin Id.  Must be combined with -e --euid"),
                    new QualifiedSwitch("endUserId", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'e', "euid",
                            "Takes a end Id (must be combined with -b --buid"),
                    new QualifiedSwitch("classId", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'c', "classId",
                            "Takes the course id."),
                    new QualifiedSwitch("outfile", FileStringParser.getParser(), JSAP.NO_DEFAULT, JSAP.REQUIRED, 'f', "file",
                            "The output file (full path) to a comma separated value file."),
                    new QualifiedSwitch("modules", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'm', "modules",
                            "Module IDs to report on").setList(true).setListSeparator(',')
            });
            JSAPResult config = jsap.parse(args);
            if (jsap.messagePrinted()) System.exit(1);

            File f = config.getFile("outfile");
            FileWriter fw = new FileWriter(f);
            Connection conn = null;
            conn = r.getConnection();
            StringBuffer sb = new StringBuffer();
            int beginId = 0, endId = 0, courseId;
            List<List<String>> rows = null;
            int[] modules = config.getIntArray("modules");
            r.modIds = modules;
            if (config.getBoolean("beginUserId") && config.getBoolean("endUserId")) {
                beginId = config.getInt("beginUserId");
                endId = config.getInt("endUserId");
                rows = r.writeUsers(beginId, endId, conn);
            } else if (config.getBoolean("classId")) {
                courseId = config.getInt("classId");
                r.courseName = r.getCourseName(conn, courseId);
                rows = r.writeCourse(courseId, conn);
            }
            r.toCSV(rows, sb);
            System.out.println(sb.toString());
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main2(String[] args) {
        Report3 r = new Report3();
        try {
            File f = new File("F:\\\\Formality\\report3.csv");
            FileWriter fw = new FileWriter(f);
            Connection conn = r.getConnection();
            StringBuffer sb = new StringBuffer();
            List<List<String>> rows= r.writeUsers(1508,1637, conn);
            r.toCSV(rows,sb);
            System.out.println(sb.toString());
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}