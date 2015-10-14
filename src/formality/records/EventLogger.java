package formality.records;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 15, 2010
 * Time: 11:06:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class EventLogger {



    public static int logEvent(Connection conn, String userID, String questionID, String modID,
                        String modType, int modIndex, String coachID, String hintID, String hintStep,
                        String eventType, String userInput,
                        Long probElapsedTime, long sessElapsedTime,
                        boolean isCorrect) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "insert into eventlog (userID, modID,qID,hintID,coachID,modType,hintStep,eventType,userInput," +
                    "isCorrect,time,probElapsedTime,sessionElapsedTime, modIndex) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(userID));
            stmt.setInt(2, Integer.parseInt(modID));
            stmt.setInt(3, Integer.parseInt(questionID));
            if (hintID != null)
                stmt.setInt(4, Integer.parseInt(hintID));
            else stmt.setNull(4, Types.INTEGER);
            if (coachID != null)
                stmt.setInt(5, Integer.parseInt(coachID));
            else stmt.setNull(5, Types.INTEGER);
            stmt.setString(6, modType);
            if (hintStep != null)
                stmt.setInt(7, Integer.parseInt(hintStep));
            else stmt.setNull(7, Types.INTEGER);
            stmt.setString(8, eventType);
            if (userInput != null)
                stmt.setString(9, userInput);
            else stmt.setNull(9, Types.VARCHAR);
            stmt.setBoolean(10, isCorrect);
            stmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            if (probElapsedTime != null)
                stmt.setLong(12, probElapsedTime);
            else stmt.setNull(12, Types.INTEGER);
            stmt.setLong(13, sessElapsedTime);
            stmt.setInt(14, modIndex);
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

    public static int logSimpleEvent(Connection conn, String userId, String questionID, String modID, String modType, int modIndex, String eventType,
                              String userInput, Long probElapsedTime, long sessElapsedTime, boolean isCorrect) throws SQLException {
        return logEvent(conn, userId, questionID, modID, modType, modIndex, null, null, null, eventType, userInput, probElapsedTime, sessElapsedTime,
                isCorrect);
    }

    public static int logHintEvent(Connection conn, String userId, String questionID, String modID, String modType, int modIndex,
                            String coachID, String hintID, String hintStep, String eventType,
                            String userInput, Long probElapsedTime, long sessElapsedTime, boolean isCorrect) throws SQLException {
        return logEvent(conn, userId, questionID, modID, modType, modIndex, coachID, hintID, hintStep, eventType, userInput, probElapsedTime, sessElapsedTime,
                isCorrect);
    }

    public static int logHomeEvent(Connection conn, String userId, long sessElapsedTime, String event) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "insert into eventlog (userID,eventType,time,sessionElapsedTime) values (?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setString(2, event);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(4, sessElapsedTime);
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

 public static int logout(Connection conn, String uID, long sessTime) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into eventlog (eventType, userID, time, sessionElapsedTime) values (?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,"logout");
            stmt.setInt(2,Integer.parseInt(uID));
            stmt.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
            stmt.setLong(4,sessTime);
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

 public static int login(Connection conn, String uID) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into eventlog (eventType, userID, time) values (?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,"login");
            stmt.setInt(2,Integer.parseInt(uID));
            stmt.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
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


}
