package formality.Util;

import formality.parser.StringEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Sep 13, 2006
 * Time: 3:31:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportDataUtils {

    public static final String reportFile_ = "4MALITY_ITEM_REPORT.txt";
    private final static String tab_ = "\t";
    private final static String endline_ = "\r\n";

    public static final String ItemReport = "ItemReport";

    public static final String itemReportQuery_ = "select Q.userid, C.courseName, Q.modtype, Q.modid, Q.questionid, Q.correct, " +
            "Q.ans1, Q.ans2, Q.questionstartts, Q.questionendts " +
            "from questionlog Q, usercourselinks U, course C " +
            "where C.courseid=U.courseid and U.userid=Q.userid and " +
            "Q.userid in " +
            "(select userid from usercourselinks where courseid=20 or courseid=21 or courseid=22 " +
            "or courseid=23 or courseid=27 or courseid=28) order by Q.userid, Q.modtype";

    public void generateReport(String rept) throws Exception {
        Connection con = getConnection();
        if (con == null)
            System.out.println("no connection");
        else {
            if (rept.equals(ItemReport)) {
                writeReport(getItemReport(con));
            } else
                System.out.println("not a valid report");
        }
    }

    private String getItemReport(Connection conn) throws Exception {
        StringBuffer output = new StringBuffer();
        Statement stmt = conn.createStatement();
        ResultSet res;
        res = stmt.executeQuery(itemReportQuery_);
        while (res.next()) {
            output.append(res.getString("userid")).append(tab_);
            output.append(res.getString("courseName")).append(tab_);
            output.append(res.getString("modtype")).append(tab_);
            output.append(res.getString("modid")).append(tab_);
            output.append(res.getString("questionid")).append(tab_);
            output.append(res.getString("correct")).append(tab_);
            output.append(res.getString("ans1")).append(tab_);
            output.append(res.getString("ans2")).append(tab_);
            output.append(getDateTimeString(res.getLong("questionstartts"))).append(tab_);
            output.append(getDateTimeString(res.getLong("questionendts"))).append(endline_);
        }
        res.close();
        stmt.close();
        return output.toString();
    }

    public Connection getConnection() throws Exception {
        Connection conn = null;
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String dbpwd = "w{:Tgd4V8je6";
        //COMPILE THIS LINE FOR ONION: conn= DriverManager.getConnection("jdbc:odbc:4malityDB", "4mality_login", StringEncoder.decode(dbpwd));
        //The develeopment DB is 4malityDev:
        // conn= DriverManager.getConnection("jdbc:odbc:4mality_TomatoDev", "4mality_login", StringEncoder.decode(dbpwd));
        //The user DB is 4mality:
        conn = DriverManager.getConnection("jdbc:odbc:4mality", "4mality_login", StringEncoder.decode(dbpwd));
        return conn;
    }

    public void writeReport(String report) throws Exception {
        File outFile = new File(reportFile_);
        FileOutputStream outFileStream = new FileOutputStream(outFile, true);//not append=false
        PrintWriter outStream = new PrintWriter(outFileStream);
        outStream.print(report);
        outStream.close();
    }

    public static String getTimestampString(long ts) {

        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(ts));
        return getDateTimeStringYYYYMMDD(cal, true, false);
    }

    public static String getDateTimeString(long ts) {

        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(ts));
        return getDateTimeString(cal, true, false);
    }

    public String getDateTimeString(long ts, boolean inclSecs, boolean includeMS) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(ts));
        return getDateTimeString(cal, inclSecs, includeMS);
    }

    public String getDateTimeStringYYYYMMDD(long ts, boolean inclSecs, boolean includeMS, boolean military) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(ts));
        return getDateTimeStringYYYYMMDD(cal, inclSecs, includeMS);
    }

    /**
     * get the date time string for the calendar object in the format
     * MM/DD/YYYY hh:mm:ss.lll
     *
     * @param cal                 the calendar object with timezone and date/time specified
     * @param includeSeconds      include the seconds on the string
     * @param includeMilliseconds include the milliseconds on the string
     * @return the date/time string
     */
    public static String getDateTimeString(Calendar cal,
                                           boolean includeSeconds,
                                           boolean includeMilliseconds) {
        StringBuffer ds = new StringBuffer();
        int hour12 = cal.get(Calendar.HOUR_OF_DAY);         // 0..24
        int minutes = cal.get(Calendar.MINUTE);      // 0..59
        int seconds = cal.get(Calendar.SECOND);      // 0..59
        int millis = cal.get(Calendar.MILLISECOND);
        ds.append(getDateStringYYYYMMDD(cal));
        ds.append(" ");
        ds.append(hour12).append(":");
        displayDigits(ds, minutes, 2);
        if (includeSeconds) {
            ds.append(":");
            displayDigits(ds, seconds, 2);
        }
        if (includeMilliseconds) {
            ds.append(".");
            displayDigits(ds, millis, 3);
        }

        return ds.toString();
    }

    /**
     * get the date time string for the calendar object in the format
     * MM/DD/YYYY hh:mm:ss.lll
     *
     * @param cal                 the calendar object with timezone and date/time specified
     * @param includeSeconds      include the seconds on the string
     * @param includeMilliseconds include the milliseconds on the string
     * @return the date/time string
     */
    public static String getDateTimeStringYYYYMMDD(Calendar cal,
                                                   boolean includeSeconds,
                                                   boolean includeMilliseconds
                                                   ) {
        StringBuffer ds = new StringBuffer();
        int hour12 = cal.get(Calendar.HOUR_OF_DAY);         // 0..11
        //   if(hour12 == 0) hour12 = 12;
        int minutes = cal.get(Calendar.MINUTE);      // 0..59
        int seconds = cal.get(Calendar.SECOND);      // 0..59
        int millis = cal.get(Calendar.MILLISECOND);
        boolean am = cal.get(Calendar.AM_PM) == Calendar.AM;
        ds.append(getDateStringyyyymmdd(cal));
        ds.append(" ");
        ds.append(hour12).append(":");
        displayDigits(ds, minutes, 2);
        if (includeSeconds) {
            ds.append(":");
            displayDigits(ds, seconds, 2);
        }
        if (includeMilliseconds) {
            ds.append(".");
            displayDigits(ds, millis, 3);
        }
/*
    if(am){
      ds.append(" AM");
    }
    else{
      ds.append(" PM");
    }
*/
        return ds.toString();
    }

    public static String getDateString(Calendar cal) {
        StringBuffer ds = new StringBuffer();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // 0..11
        int day = cal.get(Calendar.DAY_OF_MONTH);
        ds.append(month).append("/").append(day).append("/").append(year);
        return ds.toString();
    }

     public static String getDateStringYYYYMMDD(Calendar cal) {
        StringBuffer ds = new StringBuffer();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // 0..11
        int day = cal.get(Calendar.DAY_OF_MONTH);
        ds.append(year).append("-").append(month).append("-").append(day);
        return ds.toString();
    }

    public static String getDateStringyyyymmdd(Calendar cal) {
        StringBuffer ds = new StringBuffer();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // 0..11
        int day = cal.get(Calendar.DAY_OF_MONTH);
        ds.append(year).append("-").append(month).append("-").append(day);
        return ds.toString();
    }

    protected static void displayDigits(StringBuffer ds, int value, int decimals) {
        if (value < 100 && decimals == 3) ds.append("0");
        if (value < 10) ds.append("0");
        ds.append(value);
    }

    public static void main(String[] args) {
        ReportDataUtils r = new ReportDataUtils();
        try {
            r.generateReport(r.ItemReport);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
       }
    }
}
