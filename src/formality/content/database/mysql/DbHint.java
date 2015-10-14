package formality.content.database.mysql;

import formality.content.Hints;
import formality.content.Question;
import formality.content.Hint;
import formality.content.SystemInfo;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 4, 2010
 * Time: 11:05:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbHint {


    //get all hints liked to the question
    public static List<String> getAllHintIDsLinkedToQuestion(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        List<String> hintIDs = new ArrayList<String>();
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT HintID FROM HintQuestionLinks WHERE QuestionID='" + qID + "'");
        while (res.next()) {
            hintIDs.add(res.getString("HintID"));
        }
        res.close();
        stmt.close();
        return hintIDs;
    }

    //load all hints for a given qID
    private static String getLoadQuestionHintsQuery(String qID) {
        StringBuffer query = new StringBuffer();
        query.append("select Hint.HintID, Hint.Ask, Hint.Response, ");
        query.append("Hint.Step, Hint.CoachID, Coach.Name, ");
        query.append("Coach.Image, Hint.StrategyID, Hint.AnsLink, ");
        query.append("Hint.AuthorID ");
        query.append("from HintQuestionLinks, Hint, Coach ");
        query.append("where HintQuestionLinks.QuestionID = '").append(qID).append("' ");
        query.append("and HintQuestionLinks.HintID=Hint.HintID ");
        query.append("and Hint.CoachID=Coach.CoachID ");
        query.append("order by Hint.HintID ");
        return query.toString();
    }

    public static Hints loadAllQuestionHints(Question q, Connection conn) throws Exception {
        Hints hints = new Hints();
        String query = getLoadQuestionHintsQuery(q.getID());
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            Hint h = new Hint(res.getString("HintID"));
            h.setQuery(res.getString("Ask"));
            h.setResponse(res.getString("Response"));
            h.setLevel(res.getString("Step"));
            h.setCoachID(res.getString("CoachID"));
            h.setCoachName(res.getString("Name"));
            h.setCoachImg(res.getString("Image"));
            ;
            h.setStrategyID(res.getString("StrategyID"));
            h.setAnsChoiceLink(res.getString("AnsLink"));
            hints.addHint(h);
        }
        res.close();
        stmt.close();
        return hints;
    }

    //deleting a hint-question link only
    public static void deleteHintQuestionLink(String QID, String HID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int hID = Integer.parseInt(HID);
        Statement stmt = conn.createStatement();
        String qs = "DELETE FROM HintQuestionLinks WHERE QuestionID=" + qID + " AND HintID=" + hID;
        stmt.executeUpdate(qs);

        stmt.close();
    }


    public static void deleteHint(String hID, Connection conn) throws Exception {

        if (DbQuestion.hasQuestionHintLinks(hID, conn))
            throw new Exception("This hint is linked to questions. Delete these links first before deleting this hint.");
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM Hint");
        query.append(" WHERE ");
        query.append("HintID= '").append(hID).append("'");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();

    }

    //get all questions liked to the hint
    public static List<String> getQuestionsLinkedToHint(String HID, Connection conn) throws Exception {
        int hID = Integer.parseInt(HID);
        Vector questionIDs = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT QuestionID FROM HintQuestionLinks WHERE HintID='" + hID + "'");
        while (res.next()) {
            questionIDs.add(res.getString("QuestionID"));
        }
        res.close();
        stmt.close();
        return questionIDs;
    }


    //deleting all links to a particular hint
    public void deleteHintQuestionLinks(String HID, Connection conn) throws Exception {
        int hID = Integer.parseInt(HID);
        Statement stmt = conn.createStatement();
        String qs = "DELETE FROM HintQuestionLinks WHERE HintID=" + hID;
        stmt.executeUpdate(qs);

        stmt.close();
    }

    //create a link between a hint and a question
    public static void linkHintToQuestion(String QID, String HID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int hID = Integer.parseInt(HID);
        //check for existing link
        Statement stmt = conn.createStatement();
        String qs = "INSERT INTO HintQuestionLinks (QuestionID, HintID) VALUES (" + qID + ", " + hID + ")";
        stmt.executeUpdate(qs);

        stmt.close();
    }


    public static boolean hintQuestionLinkExists(String QID, String HID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int hID = Integer.parseInt(HID);
        boolean exists = false;
        Statement stmt = conn.createStatement();
        String qs = "Select * FROM HintQuestionLinks WHERE QuestionID='" + qID + "' AND HintID='" + hID + "'";
        ResultSet res = stmt.executeQuery(qs);
        if (res.next())
            exists = true;
        else
            exists = false;
        res.close();
        stmt.close();
        return exists;
    }

    //create a link between a hint and a question
    public static void linkNewHintToQuestion(String QID, String HID, Connection conn) throws Exception {
        if (DbHint.hintQuestionLinkExists(QID, HID, conn)) {
            throw new Exception("A link between question " + QID + " and hint " + HID + " already exists.");
        }
        int qID = Integer.parseInt(QID);
        int hID = Integer.parseInt(HID);
        Statement stmt = conn.createStatement();
        String qs = "INSERT INTO HintQuestionLinks (QuestionID, HintID) VALUES (" + qID + ", " + hID + ")";
        stmt.executeUpdate(qs);

        stmt.close();
    }
    

    public static int saveNewHint(Hint h, Connection conn) throws Exception {
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO Hint (");
        query.append("Ask, Response, Step, CoachID, StrategyID, AnsLink, AuthorID) ");
        query.append(" VALUES ( ");
        query.append("'").append(DbUtil.safeStringCheck(h.getQuery())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(h.getResponse())).append("', ");
        query.append("'").append(h.getLevel()).append("', ");
        query.append("'").append(h.getCoachID()).append("', ");
        query.append("'").append(h.getStrategyID()).append("',");
        query.append("'").append(h.getAnsChoiceLink()).append("',");
        query.append("'").append(h.getAuthor()).append("')");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        ResultSet rs=null;
        try {
            stmt.execute(qs);
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

    public static void saveHint(Hint h, Connection conn) throws Exception {
        int hID = Integer.parseInt(h.getID());
        StringBuffer query = new StringBuffer();
        query.append("UPDATE Hint SET ");
        query.append("Ask='").append(DbUtil.safeStringCheck(h.getQuery())).append("', ");
        query.append("Response='").append(DbUtil.safeStringCheck(h.getResponse())).append("', ");
        query.append("Step='").append(h.getLevel()).append("', ");
        query.append("CoachID='").append(h.getCoachID()).append("', ");
        query.append("StrategyID='").append(h.getStrategyID()).append("',");
        query.append("AnsLink='").append(h.getAnsChoiceLink()).append("'");
        query.append(" WHERE HintID= '").append(hID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);
        stmt.close();
    }

    public static Hints loadAllHints(String QID, SystemInfo info, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Hints hints = new Hints();
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM Hint H, hintquestionlinks L ");
        query.append("WHERE H.hintid=L.hintid AND L.questionid='");
        query.append(qID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        ResultSet res = stmt.executeQuery(qs);
        while (res.next()) {
            Hint h = new Hint(res.getString("hintID"));
            h.setQuery(res.getString("Ask"));
            h.setResponse(res.getString("Response"));
            h.setLevel(res.getString("Step"));
            h.setCoachID(res.getString("CoachID"));
            h.setStrategyID(res.getString("StrategyID"));
            h.setAnsChoiceLink(res.getString("AnsLink"));
            h.setAuthor(res.getString("AuthorID"));
            h.setCoachName(info.getCoachName(h.getCoachID()));
            h.setCoachImg(info.getCoachImg(h.getCoachID()));
            hints.addHint(h);
        }
        res.close();
        stmt.close();
        return hints;
    }

    public static Hint loadHint(String hintID, SystemInfo info, Connection conn) throws Exception {
        int hID = Integer.parseInt(hintID);
        Hint h = new Hint(hintID);
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM Hint WHERE HintID =");
        query.append("'").append(hID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        ResultSet res = stmt.executeQuery(qs);
        while (res.next()) {
            h.setQuery(res.getString("Ask"));
            h.setResponse(res.getString("Response"));
            h.setLevel(res.getString("Step"));
            h.setCoachID(res.getString("CoachID"));
            h.setStrategyID(res.getString("StrategyID"));
            h.setAnsChoiceLink(res.getString("AnsLink"));
            h.setAuthor(res.getString("AuthorID"));
        }
        res.close();
        stmt.close();
        h.setCoachName(info.getCoachName(h.getCoachID()));
        h.setCoachImg(info.getCoachImg(h.getCoachID()));
        return h;
    }

    public static List<Hint> loadAllHintsWithQLinks(String QID, SystemInfo info, Connection conn) throws Exception {
        boolean avoidQLink = true;
        if (QID == null || QID.equals("")) //load all q/h links
            avoidQLink = false;
        List<Hint> allFinalHints = new ArrayList<Hint>();
        List<Hint> allHints = DbHint.loadAllHints(info, conn);
        for (Hint curH: allHints) {
            List<String> qIDs = DbQuestion.getQuestionsLinkedToHint(curH.getID(), conn);
            if (!avoidQLink || !qIDs.contains(QID)) {
                curH.setQuestionLinks(qIDs);
                allFinalHints.add(curH);
            }
        }
        allHints = null;
        return allFinalHints;
    }

    public static List<Hint> loadAllHints(SystemInfo info, Connection conn) throws Exception {
        List<Hint> allHints = new ArrayList<Hint>();
        String query = "SELECT * FROM Hint";
        //TODO: create a query that returns order by #questions linked to
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            Hint h = new Hint(res.getString("HintID"));
            h.setQuery(res.getString("Ask"));
            h.setResponse(res.getString("Response"));
            h.setLevel(res.getString("Step"));
            h.setCoachID(res.getString("CoachID"));
            h.setStrategyID(res.getString("StrategyID"));
            h.setAnsChoiceLink(res.getString("AnsLink"));
            h.setCoachName(info.getCoachName(h.getCoachID()));
            allHints.add(h);
        }
        res.close();
        stmt.close();
        return allHints;
    }


    public static Hints getAllHints(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Hints hints = new Hints();
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM Hint WHERE HintID IN (");
        query.append("SELECT HintID FROM HintQuestionLinks WHERE QuestionID='").append(qID).append("'");
        query.append(") ORDER BY Level");
        ResultSet res = stmt.executeQuery(query.toString());
        while (res.next()) {
            Hint h = new Hint(res.getString("HintID"));
            h.setQuery(res.getString("Ask"));
            h.setResponse(res.getString("Response"));
            String level = res.getString("Step");
            h.setLevel(level);
            h.setCoachID(res.getString("CoachID"));
            h.setStrategyID(res.getString("StrategyID"));
            h.setAnsChoiceLink(res.getString("AnsLink"));
            hints.addHint(h);
        }
        res.close();
        stmt.close();
        return hints;
    }


    public List<Hint> getAllHintsAtLevel(String QID, String level, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        List<Hint> hints = new ArrayList<Hint>();
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM Hint WHERE HintID IN (");
        query.append("SELECT HintID FROM HintQuestionLinks WHERE QuestionID='").append(qID).append("'");
        query.append(") ORDER BY Level");
        ResultSet res = stmt.executeQuery(query.toString());
        while (res.next()) {
            Hint h = new Hint(res.getString("HintID"));
            h.setQuery(res.getString("Ask"));
            h.setResponse(res.getString("Response"));
            String lev = res.getString("Step");
            h.setLevel(level);
            h.setCoachID(res.getString("CoachID"));
            h.setStrategyID(res.getString("StrategyID"));
            h.setAnsChoiceLink(res.getString("AnsLink"));
            if (level.equals(lev))
                hints.add(h);
        }
        res.close();
        stmt.close();
        return hints;
    }


}
