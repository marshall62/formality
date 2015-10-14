package formality.reports;

import formality.content.database.mysql.DBAccessor;
import formality.content.database.BaseDBAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

import com.martiansoftware.jsap.*;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Dec 30, 2010
 * Time: 4:07:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Report1 extends StudentReport {


    private List<List<String>> doQuery(Connection conn, PreparedStatement stmt) throws Exception {
        ResultSet rs = null;
        List<List<String>> results = new ArrayList<List<String>>();
        results.add(getReportTitle());
        try {
            rs = stmt.executeQuery();
            List<String> row = new ArrayList<String>();
            addRowValues(row, new String[]{"userid", "login", "initials", "gender", "age", "language", "iep", "title1", "NumExternalActivities", "GlossaryViews", "Estella Views", "Hound Views", "Bear Views", "Vera Views"});
            for (String s : BaseDBAccessor.standardsNames_)
                row.add(s);
            results.add(row);
            while (rs.next()) {
                row = new ArrayList<String>();
                results.add(row);
                int id = rs.getInt("userid");
                String studId = Integer.toString(id);
                String login = rs.getString("login");
                String initials = rs.getString("initials");
                if (initials != null)
                    initials = initials.replace(',', ' ');
                else initials = "";
                String gender = rs.getString("gender");
                int age = rs.getInt("age");
                String ageStr = Integer.toString(age);
                String lang = rs.getString("nativeLang");
                if (rs.wasNull())
                    lang = "";
                String iep = rs.getString("iep");
                if (rs.wasNull())
                    iep = "";
                String title1 = rs.getString("title1");
                if (rs.wasNull())
                    title1 = "";
                iep = iep.replace(',', ' ');
                title1 = title1.replace(',', ' ');
                addRowValues(row, new String[]{studId, login, initials, gender, ageStr, lang, iep, title1});
                getExternalActivity(conn, row, id);
                getGlossaryViews(conn, row, id);
                getCoachUse(conn, row, id);
                addSkills(conn, row, id);
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


    public List<List<String>> writeCourse(int courseID, Connection conn) throws Exception {
        PreparedStatement stmt = null;
        try {
            List<List<String>> results;
            String q = "select u.userid,login,initials,gender,age,nativeLang,iep,title1 from usertable u, usercourselinks l where u.userId=l.userId and l.courseId=? and u.AccessLevel=1";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, courseID);
            results = doQuery(conn, stmt);
            return results;
        } finally {
            stmt.close();
        }
    }



    public List<List<String>> writeUsers(int beginId, int endId, Connection conn) throws Exception {
        PreparedStatement stmt = null;
        try {
            List<List<String>> results = new ArrayList<List<String>>();
            String q = "select userid,login,initials,gender,age,nativeLang,iep,title1 from usertable where userid>=? and userid<=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, beginId);
            stmt.setInt(2, endId);
            return doQuery(conn, stmt);
        } finally {
            stmt.close();
        }
    }


    private void getExternalActivity(Connection conn, List<String> row, int studid) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select count(*) from eventlog where userid=? and eventType='viewextact'" + (this.modIds.length > 0 ? " and modId in ("+ this.getModuleIdList()+")" : "");
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, studid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int c = rs.getInt(1);
                row.add(Integer.toString(c));
            } else row.add("0");
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    private void getGlossaryViews(Connection conn, List<String> row, int studid) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select count(*) from eventlog where userid=? and eventType='viewglossary'"+ (this.modIds.length > 0 ? " and modId in ("+ this.getModuleIdList()+")" : "");
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, studid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int c = rs.getInt(1);
                row.add(Integer.toString(c));
            } else row.add("0");
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    private void getCoachUse(Connection conn, List<String> row, int studid) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        int ncoaches = 0;
        try {
            String q = "SELECT coachid,count(*) as numCoaches FROM hintlog h where userid=?"+ (this.modIds.length > 0 ? " and modId in ("+ this.getModuleIdList()+")" : "") + " group by coachid;";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, studid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int c = rs.getInt("numCoaches");
                row.add(Integer.toString(c));
                ncoaches++;
            }
            for (int i = 0; i < 4 - ncoaches; i++)
                row.add("0");
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    private void addSkills(Connection conn, List<String> row, int studid) throws Exception {

        DBAccessor dba = new DBAccessor();
        double[] skilldata = dba.getStudentSkillData(Integer.toString(studid), DBAccessor.standardsDBNames_.length, conn);
        for (double d : skilldata) {
            row.add(Double.toString(d));
        }
    }


    /**
     * Sample of how to call:  Report1 -b:1508 -e:1598 -f:f:/Formality/report1.csv     :  reports on the range of users
     * Report1 -c:52 -f:u:/formality/Greenfield2010/CyczReport1-GeoProbGraph.csv -m:237,246,247,250,252,259,261,265,268
     *
     * @param args
     */
    public static void main(String[] args) {
        Report1 r = new Report1();
        try {
            SimpleJSAP jsap = new SimpleJSAP("Report1", "Report 1", new Parameter[]{
                    new QualifiedSwitch("beginUserId", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'b', "buid",
                            "Takes a begin Id.  Must be combined with -e --euid"),
                    new QualifiedSwitch("endUserId", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'e', "euid",
                            "Takes a end Id (must be combined with -b --buid"),
                    new QualifiedSwitch("classId", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'c', "classId",
                            "Takes the course id."),
                    new QualifiedSwitch("outfile", FileStringParser.getParser(), JSAP.NO_DEFAULT, JSAP.REQUIRED, 'f', "file",
                            "The output file (full path) to a comma separated value file."),
                    new QualifiedSwitch( "modules", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'm', "modules",
                        "Module IDs to report on" ).setList( true ).setListSeparator( ',' )
            });
            JSAPResult config = jsap.parse(args);
            if (jsap.messagePrinted()) System.exit(1);

            File f = config.getFile("outfile");
            FileWriter fw = new FileWriter(f);
            Connection conn = r.getConnection();
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
                r.courseName = r.getCourseName(conn,courseId);
                rows = r.writeCourse(courseId, conn);
            }

//            File f = new File("F:\\\\Formality\\reportJunk.csv");

            r.toCSV(rows, sb);
            System.out.println(sb.toString());
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
