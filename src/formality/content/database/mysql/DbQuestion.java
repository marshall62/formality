package formality.content.database.mysql;

import formality.content.*;
import formality.systemerror.AuthoringException;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import java.util.List;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 4, 2010
 * Time: 10:57:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbQuestion {

    public static Question loadQuestion(Question q,
                                        Connection conn) throws Exception {
        int qID = Integer.parseInt(q.getID());
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM Question WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        String queryStr = query.toString();
        ResultSet res = stmt.executeQuery(queryStr);
        String nm = res.getMetaData().getColumnName(11);
        int sz = res.getMetaData().getColumnDisplaySize(11);
        while (res.next()) {
            q.setType(res.getString("QType"));
            q.setAuthor(res.getString("Author"));
            q.setSource(res.getString("Source"));
            q.setDegree(res.getString("Degree"));
            q.setCategoryID(res.getString("CategoryID"));
            q.setStatus(res.getBoolean("Ready"));
            q.setDiffLevel(res.getDouble("DiffLevel"));
            String stm = res.getString("Stem");
            q.setStem(stm);
            q.setStd1(res.getString("Std1"));
            q.setStd2(res.getString("Std2"));
            q.setStd3(res.getString("Std3"));
            q.setCcss1(res.getString("ccss1"));
            q.setCcss2(res.getString("ccss2"));
            q.setCcss3(res.getString("ccss3"));
            q.setTopic(res.getString("Topic"));
            q.setSpanishAudioFile(res.getString("spanishaudioFile"));
            q.setAudioFile(res.getString("audioFile"));
            q.setSequentialLayout(res.getInt("answerLayout") == Question.ANSWER_LAYOUT_SEQUENTIAL);
            q.setSurvey(res.getBoolean("isSurvey"));

        }
        res.close();
        stmt.close();
        return q;
    }

    //get the question data
    public static Question loadQuestion(String QID,
                                        Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Question q = new Question(QID);
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM Question WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query.toString());
        while (res.next()) {
            q.setType(res.getString("QType"));
            q.setAuthor(res.getString("Author"));
            q.setSource(res.getString("Source"));
            q.setDegree(res.getString("Degree"));
            q.setCategoryID(res.getString("CategoryID"));
            q.setStatus(res.getBoolean("Ready"));
            q.setDiffLevel(res.getDouble("DiffLevel"));
            q.setStem(res.getString("Stem"));
            q.setStd1(res.getString("Std1"));
            q.setStd2(res.getString("Std2"));
            q.setStd3(res.getString("Std3"));
            q.setCcss1(res.getString("ccss1"));
            q.setCcss2(res.getString("ccss2"));
            q.setCcss3(res.getString("ccss3"));
            q.setTopic(res.getString("Topic"));
            q.setAudioFile(res.getString("audioFile"));
            q.setSpanishAudioFile(res.getString("spanishAudioFile"));
            q.setSequentialLayout(res.getInt("answerLayout") == Question.ANSWER_LAYOUT_SEQUENTIAL);
            q.setSurvey(res.getBoolean("isSurvey"));
        }
        res.close();
        stmt.close();
        return q;
    }


    public static List<Question> getAllQuestionsInCategory(String CID, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        List<Question> catQs = new ArrayList<Question>();
        StringBuffer query = new StringBuffer();
        query.append("SELECT QuestionID, QType, Stem, Author, Std1, Std2, Std3, ccss1, ccss2, ccss3, Topic,audioFile,spanishAudioFile,answerLayout,isSurvey FROM Question WHERE CategoryID= '").append(cID).append("' ");
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query.toString());
        while (res.next()) {
            Question q = new Question(res.getString("QuestionID"));
            q.setType(res.getString("QType"));
            q.setStem(res.getString("Stem"));
            q.setAuthor(res.getString("Author"));
            q.setAudioFile(res.getString("audioFile"));
            q.setSpanishAudioFile(res.getString("spanishAudioFile"));
            q.setSequentialLayout(res.getInt("answerLayout") == Question.ANSWER_LAYOUT_SEQUENTIAL);
            String std = res.getString("Std1");
            if (res.wasNull())
                std = null;
            String topicStr = res.getString("Topic");
            if (std != null && !std.equals(""))
                q.setStd1(std);
            std = res.getString("Std2");
            if (res.wasNull())
                std = null;
            if (std != null && !std.equals(""))
                q.setStd2(std);
            std = res.getString("Std3");
            if (res.wasNull())
                std = null;
            if (std != null && !std.equals(""))
                q.setStd3(std);
            q.setCcss1(res.getString("ccss1"));
            q.setCcss2(res.getString("ccss2"));
            q.setCcss3(res.getString("ccss3"));
            if (topicStr == null || topicStr.equals(""))
                topicStr = "null";
            q.setTopic(topicStr);
            q.setSurvey(res.getBoolean("isSurvey"));
            catQs.add(q);
        }
        res.close();
        stmt.close();
        return catQs;
    }

    public static List<Question> getAllQuestionsInCategory(String MID, String CID, Connection conn) throws Exception {
        List<String> linkedQIDs = DbModule.loadModuleQuestionLinks(MID, conn, QModule.OrderType.link);
        List<Question> catQs = new ArrayList<Question>();
        StringBuffer query = new StringBuffer();
        int cID = Integer.parseInt(CID);
        query.append("SELECT QuestionID, QType, Stem, Author, Std1, Std2, Std3,ccss1,ccss2,ccss3, Topic, audioFile,spanishAudioFile,answerLayout,isSurvey FROM Question WHERE CategoryID= '").append(cID).append("' ");
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query.toString());
        while (res.next()) {
            String qID = res.getString("QuestionID");
            if (!linkedQIDs.contains(qID)) {
                Question q = new Question(qID);
                q.setType(res.getString("QType"));
                q.setStem(res.getString("Stem"));
                q.setAuthor(res.getString("Author"));
                q.setAudioFile(res.getString("audioFile"));
                q.setSpanishAudioFile(res.getString("spanishAudioFile"));
                q.setSequentialLayout(res.getInt("answerLayout") == Question.ANSWER_LAYOUT_SEQUENTIAL);
                String std = res.getString("Std1");
                String topicStr = res.getString("Topic");
                if (std != null && !std.equals(""))
                    q.setStd1(std);
                std = res.getString("Std2");
                if (std != null && !std.equals(""))
                    q.setStd2(std);
                std = res.getString("Std3");
                if (std != null && !std.equals(""))
                    q.setStd3(std);
                q.setCcss1(res.getString("ccss1"));
                q.setCcss2(res.getString("ccss2"));
                q.setCcss3(res.getString("ccss3"));
                if (topicStr == null || topicStr.equals(""))
                    topicStr = "null";
                q.setTopic(topicStr);
                q.setSurvey(res.getBoolean("isSurvey"));
                catQs.add(q);
            }
        }
        return catQs;
    }

    public static TreeMap getQuestionsInCategoriesCounts(Iterator cIDs, Connection conn) throws Exception {
        TreeMap cts = new TreeMap();
        while (cIDs.hasNext()) {
            String cID = (String) cIDs.next();
            String ct = getQuestionCountInCategory(cID, conn);
            cts.put(cID, ct);
        }
        return cts;
    }

    public static String getQuestionCountInCategory(String CID, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        String count = "";
        StringBuffer query = new StringBuffer();
        query.append("SELECT Count(*) as 'ct' FROM Question WHERE CategoryID= '").append(cID).append("' ");
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next())
            count = res.getString("ct");
        return count;
    }

    /*
    public static void loadQuestionStandards(Question q,
                                      Connection conn) throws Exception {
        int qID = Integer.parseInt(q.getID());
        StringBuffer query = new StringBuffer();
        query.append("SELECT FrameworkStd FROM QuestionFrameworkLinks WHERE q.QuestionID= '");
        query.append(qID).append("' ");
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query.toString());
        int i = 1;
        while (res.next()) {
            if (i == 1)
                q.setStd1(res.getString("FrameworkStd"));
            if (i == 2)
                q.setStd2(res.getString("FrameworkStd"));
            if (i == 3)
                q.setStd3(res.getString("FrameworkStd"));
            i++;
        }
        res.close();
        stmt.close();
    }
    */

    public static MultiChoiceSurveyQuestion loadMultipleChoiceSurveyQuestion(String qID,
                                                                             Connection conn) throws Exception {
        MultiChoiceSurveyQuestion q = new MultiChoiceSurveyQuestion(qID);
        loadQuestion(q, conn);
        loadMultipleChoiceSurveyQuestionAnswers(q, conn);
        return q;
    }

    public static MultipleChoiceQuestion loadMultipleChoiceQuestion(String qID,
                                                                    Connection conn) throws Exception {
        MultipleChoiceQuestion q = new MultipleChoiceQuestion(qID);
        loadQuestion(q, conn);
        loadMultipleChoiceQuestionAnswers(q, conn);
        return q;
    }

    public static ShortAnswerQuestion loadShortAnswerQuestion(String qID,
                                                              Connection conn) throws Exception {
        ShortAnswerQuestion q = new ShortAnswerQuestion(qID);
        loadQuestion(q, conn);
        loadShortAnswerQuestionAnswers(q, conn);
        return q;
    }

    public static void deleteMultipleChoiceQuestion(String qID, Connection conn) throws Exception {
        //deleteQuestionHintLinks(qID, conn);
        //deleteMultipleChoiceQuestionAnswers(qID, conn);
        //deleteQuestion(qID, conn);
    }

    public static void loadMultipleChoiceSurveyQuestionAnswers(MultiChoiceSurveyQuestion q, Connection conn) throws Exception {
        StringBuffer query = new StringBuffer();
        int qID = Integer.parseInt(q.getID());
        query.append("SELECT * FROM MultiChoiceAnswers ");
        query.append("WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            q.setAnswerChoiceA(res.getString("AnswerA"));
            q.setAnswerChoiceB(res.getString("AnswerB"));
            q.setAnswerChoiceC(res.getString("AnswerC"));
            q.setAnswerChoiceD(res.getString("AnswerD"));
        }
        res.close();
        stmt.close();
    }

    public static void loadShortAnswerQuestionAnswers(ShortAnswerQuestion saq, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select id, ans from shortanswervariant where qid=?";
            List<String> answers = new ArrayList<String>();
            List<Integer> ansIds = new ArrayList<Integer>();
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(saq.getID()));
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                ansIds.add(id);
                String a = rs.getString(2);
                answers.add(a);
            }
            saq.setAnswers(answers);
            saq.setAnswerIds(ansIds);
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static void loadMultipleChoiceQuestionAnswers(MultipleChoiceQuestion q, Connection conn) throws Exception {
        StringBuffer query = new StringBuffer();
        int qID = Integer.parseInt(q.getID());
        query.append("SELECT * FROM MultiChoiceAnswers ");
        query.append("WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            q.setAnswerChoiceA(res.getString("AnswerA"));
            q.setAnswerChoiceB(res.getString("AnswerB"));
            q.setAnswerChoiceC(res.getString("AnswerC"));
            q.setAnswerChoiceD(res.getString("AnswerD"));
            q.setAnsChoiceAFeedback(res.getString("FeedbackA"));
            q.setAnsChoiceBFeedback(res.getString("FeedbackB"));
            q.setAnsChoiceCFeedback(res.getString("FeedbackC"));
            q.setAnsChoiceDFeedback(res.getString("FeedbackD"));
            q.setCorrectAnswer(res.getString("Correct"));
        }
        res.close();
        stmt.close();
    }

    //return the new ID number
    public static int saveNewMultipleChoiceSurveyQuestion(MultiChoiceSurveyQuestion q, Connection conn) throws Exception {
        int qID = DbQuestion.saveNewSurveyQuestion(q, conn);
        q.setID(Integer.toString(qID));
        DbQuestion.saveNewMultipleChoiceSurveyQuestionAnswers(q, conn);
        return qID;
    }

    public static int saveNewShortAnswerQuestion(ShortAnswerQuestion q, Connection conn) throws Exception {
        int qID = DbQuestion.saveNewQuestion(q, conn);
        q.setID(Integer.toString(qID));
        DbQuestion.saveShortAnswerQuestionAnswers(q, conn); // update and create rows in shortanswervariants table
        return qID;
    }

    public static void saveShortAnswerQuestion(ShortAnswerQuestion q, Connection conn) throws Exception {
        DbQuestion.saveQuestion(q, conn);
        DbQuestion.saveShortAnswerQuestionAnswers(q, conn);
    }

    //return the new ID number
    public static int saveNewMultipleChoiceQuestion(MultipleChoiceQuestion q, Connection conn) throws Exception {
        int qID = DbQuestion.saveNewQuestion(q, conn);
        q.setID(Integer.toString(qID));
        DbQuestion.saveNewMultipleChoiceQuestionAnswers(q, conn);
        return qID;
    }

    public static void saveMultipleChoiceQuestion(MultipleChoiceQuestion q, Connection conn) throws Exception {
        DbQuestion.saveQuestion(q, conn);
        DbQuestion.saveMultipleChoiceQuestionAnswers(q, conn);
    }

    public static void saveMultipleChoiceSurveyQuestion(MultiChoiceSurveyQuestion q, Connection conn) throws Exception {
        DbQuestion.saveSurveyQuestion(q, conn);
        DbQuestion.saveMultipleChoiceSurveyQuestionAnswers(q, conn);
    }


    public static void saveNewMultipleChoiceSurveyQuestionAnswers(MultiChoiceSurveyQuestion quest, Connection conn) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "insert into MultiChoiceAnswers (QuestionID, AnswerA,AnswerB,AnswerC,AnswerD) values (?,?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(quest.getID()));
            stmt.setString(2, quest.getAnswerChoiceA());
            stmt.setString(3, quest.getAnswerChoiceB());
            stmt.setString(4, quest.getAnswerChoiceC());
            stmt.setString(5, quest.getAnswerChoiceD());
            stmt.execute();

        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }


    public static int saveSurveyQuestion(Question qu, Connection conn) throws Exception {
        PreparedStatement stmt = null;
        try {
            String q = "update Question set Stem=?, answerLayout=? where QuestionID=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1, qu.getStem());
            stmt.setBoolean(2, !qu.isSequentialLayout());
            stmt.setInt(3, Integer.parseInt(qu.getID()));
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static int saveNewSurveyQuestion(Question quest, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "insert into Question (qtype, isSurvey, author,ready, stem, answerLayout, categoryID) values (?,1,?,?,?,?, (select categoryID from category where name='Survey Question'))";
            stmt = conn.prepareStatement(q);
            stmt.setString(1, quest.getType());
            stmt.setString(2, quest.getAuthor());
            stmt.setBoolean(3, true); // make survey questions always ready
            stmt.setString(4, quest.getStem());
            stmt.setBoolean(5, !quest.isSequentialLayout());
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

    public static int saveShortAnswerSurveyQuestion (ShortAnswerQuestion sq, Connection conn) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "update question set stem=? where questionid=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,sq.getStem());
            stmt.setInt(2,Integer.parseInt(sq.getID()));
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }


    public static void saveQuestion(Question q, Connection conn) throws Exception {
        String QID = q.getID();
        if (QID == null) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Question ID=null, it is not initialized.");
            throw ae;
        }
        int qID = Integer.parseInt(QID);
        String deg = DbUtil.safeStringCheck(q.getDegree());
        int cID = Integer.parseInt(q.getCategoryID());
        StringBuffer query = new StringBuffer();
        query.append("UPDATE Question SET ");
        query.append("Source='").append(DbUtil.safeStringCheck(q.getSource())).append("', ");
        query.append("Degree='").append(deg).append("', ");
        query.append("CategoryID='").append(cID).append("', ");
        if (q.getStatus() == true)
            query.append("Ready='").append(1).append("', ");
        else
            query.append("Ready='").append(0).append("', ");
        query.append("Stem='").append(DbUtil.safeStringCheck(q.getStem())).append("', ");
        query.append("Std1='").append(q.getStd1()).append("', ");
        query.append("Std2='").append(q.getStd2()).append("', ");
        query.append("Std3='").append(q.getStd3()).append("', ");
        query.append("ccss1='").append(q.getCcss1()).append("', ");
        query.append("ccss2='").append(q.getCcss2()).append("', ");
        query.append("ccss3='").append(q.getCcss3()).append("', ");
        query.append("Topic='").append(DbUtil.safeStringCheck(q.getTopic())).append("',");
        query.append("answerLayout=" + (q.isSequentialLayout() ? "0" : "1"));
        query.append(" WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }


    public static int saveNewQuestion(Question q, Connection conn) throws Exception {
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO Question (");
        query.append("QType, Author, Source, Degree, CategoryID, Ready, ");
        query.append("DiffLevel, Stem, Std1, Std2, Std3, ccss1, ccss2, ccss3, Topic, answerLayout) ");
        query.append(" VALUES(");
        query.append("'").append(DbUtil.safeStringCheck(q.getType())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAuthor())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getSource())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getDegree())).append("', ");
        query.append("'").append(q.getCategoryID()).append("', ");
        if (q.getStatus() == true)
            query.append("'").append(1).append("', ");
        else
            query.append("'").append(0).append("', ");
        query.append("'").append(q.getDiffLevel()).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getStem())).append("', ");
        query.append("'").append(q.getStd1()).append("', ");
        query.append("'").append(q.getStd2()).append("', ");
        query.append("'").append(q.getStd3()).append("', ");
        query.append("'").append(q.getCcss1()).append("', ");
        query.append("'").append(q.getCcss2()).append("', ");
        query.append("'").append(q.getCcss3()).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getTopic())).append("', ");
        query.append("'").append(q.isSequentialLayout() ? "0" : "1").append("' ");
        query.append(")");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        ResultSet rs = null;
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


    public static void saveNewMultipleChoiceQuestionAnswers(MultipleChoiceQuestion q, Connection conn) throws Exception {
        StringBuffer query = new StringBuffer();
        int qID = Integer.parseInt(q.getID());
        query.append("INSERT INTO MultiChoiceAnswers (");
        query.append("QuestionID, Correct, ");
        query.append("AnswerA, AnswerB, AnswerC, AnswerD, ");
        query.append("FeedbackA, FeedbackB, FeedbackC, FeedbackD)");
        query.append(" VALUES(");
        query.append("'").append(qID).append("', ");
        query.append("'").append(q.getCorrectAnswer()).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnswerChoiceA())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnswerChoiceB())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnswerChoiceC())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnswerChoiceD())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnsChoiceAFeedback())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnsChoiceBFeedback())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnsChoiceCFeedback())).append("', ");
        query.append("'").append(DbUtil.safeStringCheck(q.getAnsChoiceDFeedback())).append("'");
        query.append(")");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);
        stmt.close();
    }

    public static int saveMultipleChoiceSurveyQuestionAnswers(MultiChoiceSurveyQuestion qu, Connection conn) throws Exception {
        PreparedStatement stmt = null;
        try {
            String q = "update MultiChoiceAnswers set AnswerA=?, AnswerB=?, AnswerC=?, AnswerD=? where QuestionID=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1, qu.getAnswerChoiceA());
            stmt.setString(2, qu.getAnswerChoiceB());
            stmt.setString(3, qu.getAnswerChoiceC());
            stmt.setString(4, qu.getAnswerChoiceD());
            stmt.setInt(5, Integer.parseInt(qu.getID()));
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    //we select the answer variants out in order of ID so they will match the incoming order because
    // the order of variants on the page was created by pulling them out in ID order.  Each variant
    // has its ans updated in case the user altered it.   Any new ones
    // will be at end of list and will be inserted.
    public static void saveShortAnswerQuestionAnswers(ShortAnswerQuestion saq, Connection conn) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select ans,id from shortanswervariant where qid=? order by id";
            stmt = conn.prepareStatement(q, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setInt(1, Integer.parseInt(saq.getID()));
            rs = stmt.executeQuery();
            List<String> answers = saq.getAnswers();
            Iterator itr = answers.iterator();
            // update all the existing answers.   If the incoming field is now blank, interpret this as a deletion
            while (rs.next()) {
                String a = rs.getString(1);
                int id = rs.getInt(2);
                String ans = (String) itr.next();
                if (ans.equals(""))  {
                    rs.deleteRow();
                    rs.previous();
                    itr.remove();
                }
                else {
                    rs.updateString(1, ans);
                    rs.updateRow();
                }
            }
            // any remaining answers are new and must be inserted.  Skip over any that are blank.
            while (itr.hasNext()) {
                String ans = (String) itr.next();
                if (ans != null && !ans.equals("")) {
                    q = "insert into shortanswervariant (qid, ans) values (?,?)";
                    stmt = conn.prepareStatement(q);
                    stmt.setInt(1, Integer.parseInt(saq.getID()));
                    stmt.setString(2,ans);
                    stmt.execute();
                    rs = stmt.getGeneratedKeys();
                    rs.next();
                    int k = rs.getInt(1);
                    saq.addAnswerId(k);
                }
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static void saveNewShortAnswerQuestionAnswers(ShortAnswerQuestion saq, Connection conn) throws Exception {
        int qID = Integer.parseInt(saq.getID());
        List<String> answers = saq.getAnswers();
        List<Integer> ansids = new ArrayList<Integer>();
        for (String ans : answers) {
            if (ans != null && !ans.equals("")) {
                ResultSet rs = null;
                PreparedStatement stmt = null;
                try {
                    String q = "insert into shortanswervariant (qid, ans) values (?,?)";
                    stmt = conn.prepareStatement(q);
                    stmt.setInt(1, qID);
                    stmt.setString(2, ans);
                    stmt.execute();
                    rs = stmt.getGeneratedKeys();
                    rs.next();
                    ansids.add(rs.getInt(1));
                }
                finally {
                    if (rs != null)
                        rs.close();
                    if (stmt != null)
                        stmt.close();
                }
            }
        }
        saq.setAnswerIds(ansids);

    }

    public static void saveMultipleChoiceQuestionAnswers(MultipleChoiceQuestion q, Connection conn) throws Exception {
        int qID = Integer.parseInt(q.getID());
        StringBuffer query = new StringBuffer();
        query.append("UPDATE MultiChoiceAnswers SET ");
        query.append("Correct='").append(q.getCorrectAnswer()).append("', ");
        query.append("AnswerA='").append(DbUtil.safeStringCheck(q.getAnswerChoiceA())).append("', ");
        query.append("AnswerB='").append(DbUtil.safeStringCheck(q.getAnswerChoiceB())).append("', ");
        query.append("AnswerC='").append(DbUtil.safeStringCheck(q.getAnswerChoiceC())).append("', ");
        query.append("AnswerD='").append(DbUtil.safeStringCheck(q.getAnswerChoiceD())).append("', ");
        query.append("FeedbackA='").append(DbUtil.safeStringCheck(q.getAnsChoiceAFeedback())).append("', ");
        query.append("FeedbackB='").append(DbUtil.safeStringCheck(q.getAnsChoiceBFeedback())).append("', ");
        query.append("FeedbackC='").append(DbUtil.safeStringCheck(q.getAnsChoiceCFeedback())).append("', ");
        query.append("FeedbackD='").append(DbUtil.safeStringCheck(q.getAnsChoiceDFeedback())).append("'");
        query.append(" WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);
        stmt.close();
    }

    public static void deleteMultipleChoiceQuestionAnswers(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM MultiChoiceAnswers ");
        query.append("WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }

    public static void deleteQuestion(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM question ");
        query.append("WHERE QuestionID= '").append(qID).append("' ");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }

    public static void expungeSAQuestion(String qid, Connection conn, StringBuffer msg) throws Exception {
        msg.append("Question " + qid + " deleted. ");
        List<String> hints = DbHint.getAllHintIDsLinkedToQuestion(qid, conn);
        if (hints.size() > 0)
            msg.append("The question uses these hints: ");
        for (String hid : hints) {
            msg.append("<br>" + hid);
        }
        if (hints.size() > 0)
            msg.append(".<br>   Make sure to delete these hints if no other question uses them.");
        deleteAllQuestionToFrameworkLinks(qid, conn);
        deleteSAQuestionAnswers(qid, conn);
        // deleteQuestionShortAnswer();
        deleteAllQuestionToModuleLinks(qid, conn);
        // need to delete hint/question links
        deleteQuestionHintLinks(qid, conn);
        deleteQuestion(qid, conn);
        // this may leave hints in the system that don't connect to any question
    }

    private static int deleteSAQuestionAnswers(String qid, Connection conn) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from shortanswervariant where qid=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,qid);
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static void expungeQuestion(String qid, Connection conn, StringBuffer msg) throws Exception {
        msg.append("Question " + qid + " deleted. ");
        List<String> hints = DbHint.getAllHintIDsLinkedToQuestion(qid, conn);
        if (hints.size() > 0)
            msg.append("The question uses these hints: ");
        for (String hid : hints) {
            msg.append("<br>" + hid);
        }
        if (hints.size() > 0)
            msg.append(".<br>   Make sure to delete these hints if no other question uses them.");
        deleteAllQuestionToFrameworkLinks(qid, conn);
        deleteMultipleChoiceQuestionAnswers(qid, conn);
        // deleteQuestionShortAnswer();
        deleteAllQuestionToModuleLinks(qid, conn);
        // need to delete hint/question links
        deleteQuestionHintLinks(qid, conn);
        deleteQuestion(qid, conn);
        // this may leave hints in the system that don't connect to any question
    }

    public static boolean isQuestionActive(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        boolean isActive = false;
        Statement stmt = conn.createStatement();
        ResultSet res;
        String query = "SELECT Ready from Question WHERE QuestionID='" + qID + "'";
        res = stmt.executeQuery(query);
        if (res.next()) {
            isActive = res.getBoolean("Ready");
        }
        res.close();
        stmt.close();
        return isActive;
    }

    public static int getLastModQuestionPosition(String MID, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select max(seq) from ModQuestionLinks where moduleId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(MID));
            rs = stmt.executeQuery();
            while (rs.next()) {
                int c = rs.getInt(1);
                return c;
            }
            return -1;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    //create a link between a hint and a question
    public static int linkQuestionToModule(String QID, String MID, Connection conn) throws Exception {
        //the question must be active
        boolean isActive = isQuestionActive(QID, conn);
        if (!isActive)
            throw new Exception("You are attempting to link an inactive question to a module. Go to the question" +
                    " edit page and click the \"Ready Status\" check box and then link to the module.");
        //check for existing link
        if (modHasQuestion(MID, QID, conn))
            throw new Exception("You are attempting to link a question to a module that is already in the module.");
        int qID = Integer.parseInt(QID);
        int mID = Integer.parseInt(MID);
        int pos = getLastModQuestionPosition(MID, conn);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "INSERT INTO ModQuestionLinks (ModuleID, QuestionID, seq) VALUES (?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, mID);
            stmt.setInt(2, qID);
            stmt.setInt(3, pos + 1);
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

    private static boolean modHasQuestion(String mid, String qid, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select id from modquestionlinks where moduleId=? and questionId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, Integer.parseInt(mid));
            stmt.setInt(2, Integer.parseInt(qid));
            rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else return false;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static boolean hasQuestionHintLinks(String HID, Connection conn) throws Exception {
        int hID = Integer.parseInt(HID);
        boolean linked = false;
        Statement stmt = conn.createStatement();
        String qs = "SELECT * FROM HintQuestionLinks WHERE HintID=" + hID;
        ResultSet res = stmt.executeQuery(qs);
        while (res.next())
            linked = true;
        res.close();
        stmt.close();
        return linked;
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

    //create a link between a hint and a question
    public static void deleteQuestionToModuleLink(String QID, String MID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int mID = Integer.parseInt(MID);
        // change the index of all the following questions before deleting this one.
        moveQuestionsDown(QID, MID, conn);
        //check for existing link
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ModQuestionLinks WHERE ModuleID='").append(mID).append("'");
        query.append(" AND QuestionID='").append(qID).append("'");
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }

    // before deleting the row for the given qid, reduce the position of each of the questions that follow it
    // in the module.
    private static void moveQuestionsDown(String qid, String mid, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        int qid1 = Integer.parseInt(qid);
        try {
            String q = "select seq,questionId,id from ModQuestionLinks where moduleId=? order by seq";
            stmt = conn.prepareStatement(q, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.setInt(1, Integer.parseInt(mid));
            rs = stmt.executeQuery();
            boolean moveDown = false;
            while (rs.next()) {
                int seq = rs.getInt(1);
                int qid2 = rs.getInt(2);
                if (moveDown) {
                    rs.updateInt(1, seq - 1);
                    rs.updateRow();
                } else if (qid1 == qid2)
                    moveDown = true;

            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }


    //delete all links between a question and its standards
    public static void deleteAllQuestionToFrameworkLinks(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Statement stmt = conn.createStatement();
        String qs = "DELETE From QuestionFrameworkLinks WHERE QuestionID='" + qID + "'";
        stmt.executeUpdate(qs);

        stmt.close();
    }


    //deleting a question
    public static void deleteAllQuestionToModuleLinks(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ModQuestionLinks WHERE ");
        query.append(" QuestionID='").append(qID).append("'");
        stmt.executeUpdate(query.toString());

        stmt.close();
    }

    //deleting all links to a particular question
    public static void deleteQuestionHintLinks(String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Statement stmt = conn.createStatement();
        String qs = "DELETE FROM HintQuestionLinks WHERE QuestionID=" + qID;
        stmt.executeUpdate(qs);

        stmt.close();
    }

    //create a link between a standard and a question
    public static void linkQuestionToFramework(String QID, String std, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Statement stmt = conn.createStatement();
        String qs = "INSERT INTO QuestionFrameworkLinks (QuestionID, FrameworkStd) VALUES (" + qID + ", " + DbUtil.safeStringCheck(std) + ")";
        stmt.executeUpdate(qs);

        stmt.close();
    }

    //delete a link between a standard and a question
    public static void deleteQuestionToFrameworkLink(String QID, String std, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        Statement stmt = conn.createStatement();
        String qs = "DELETE From QuestionFrameworkLinks WHERE QuestionID='" + qID + "' AND FrameworkStd='" + DbUtil.safeStringCheck(std) + "'";
        stmt.executeUpdate(qs);

        stmt.close();
    }


    public static int updateQuestionAudioFile(Connection conn, int qid, String col, byte[] audio) throws SQLException {
        String q = "update Question set " + col + "=? where questionID=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setBytes(1, audio);
        ps.setInt(2, qid);
        int n = ps.executeUpdate();
        ps.close();
        return n;
    }


    public static void saveAudioFile(Connection conn, int probId, String audioFile) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select audioFile from question where questionid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,probId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return;
            }
            else {
                q = "insert into question (audioFile) values (?)";
                stmt = conn.prepareStatement(q);
                stmt.setString(1,audioFile);
                stmt.execute();
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static void saveSpanishAudioFile(Connection conn, int probId, String spanFilename) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select spanishAudioFile from question where questionid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,probId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return;
            }
            else {
                q = "insert into question (spanishAudioFile) values (?)";
                stmt = conn.prepareStatement(q);
                stmt.setString(1,spanFilename);
                stmt.execute();
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }
}
