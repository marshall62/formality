package formality;

import formality.controller.*;

import java.util.*;
import java.sql.*;
//import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Date;

import formality.content.database.mysql.DBAccessor;
import formality.content.SystemInfo;
import formality.controller.FormalitySubsystem;
import formality.Util.DataTuple;
import formality.Util.xml.XmlFileParser;
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
public class AppMain {
    public static Connection conn=null;
    public static SystemInfo info = null;
    public static String modScoresFile =  "module_scores.xml";

    public static void main(String[] args) throws Exception{
     XmlFileParser p = new XmlFileParser();
        TreeMap<String, DataTuple> itemTree = new TreeMap<String, DataTuple>();
        p.parseModuleScoreItems(modScoresFile, itemTree);

    }
}
/*
    long ct=  System.currentTimeMillis();
        Properties p=System.getProperties();
  // begin output
 System.out.println("Current Time");

 // create a Pacific Standard Time time zone
 SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, "America/New_York");

 // set up rules for daylight savings time
 pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
 pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

 // create a GregorianCalendar with the Pacific Daylight time zone
 // and the current date and time
 Calendar calendar = new GregorianCalendar(pdt);
 java.util.Date trialTime = new java.util.Date();
 calendar.setTime(trialTime);
        System.out.println("ERA: " + calendar.get(Calendar.ERA));
         System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
         System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
         System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
         System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
         System.out.println("DATE: " + calendar.get(Calendar.DATE));
         System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
         System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
         System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
         System.out.println("DAY_OF_WEEK_IN_MONTH: "
                            + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
         System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
         System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
         System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
         System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
         System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
         System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
         System.out.println("ZONE_OFFSET: "
                            + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000)));
         System.out.println("DST_OFFSET: "
                            + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000)));

         System.out.println("Current Time, with hour reset to 3");
         calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
         calendar.set(Calendar.HOUR, 3);
         System.out.println("ERA: " + calendar.get(Calendar.ERA));
         System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
         System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
         System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
         System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
         System.out.println("DATE: " + calendar.get(Calendar.DATE));
         System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
         System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
         System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
         System.out.println("DAY_OF_WEEK_IN_MONTH: "
                            + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
         System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
         System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
         System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
         System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
         System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
         System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
         System.out.println("ZONE_OFFSET: "
                + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000))); // in hours
         System.out.println("DST_OFFSET: "
                + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000))); // in hours

        /*
   try{
     Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
     //conn= DriverManager.getConnection("jdbc:odbc:great1", "great", "great");
       conn= DriverManager.getConnection("jdbc:odbc:4mality", "4mality_login", "!4Te$t<pR*p>");
     }
   catch(Exception sqlx) {
           System.err.println("Error- could not load db driver");
           sqlx.printStackTrace();
   }

        try {//parse the params
            FakeParams f = new FakeParams();
            //  TreeMap params = f.getFakeSaveNewQuestionParams();
            TreeMap params = f.getFakeNewUserParams();//.getFakeSaveNewHintParams();
            info = new SystemInfo();
            FormalitySubsystem fs = new FormalitySubsystem();
            fs.handleRequest(params, info, conn, null);
        } catch (Exception ex) {
            String errStr=ex.getMessage();
            System.err.println(errStr);

        }
    }
    }
       */
