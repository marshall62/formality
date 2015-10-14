package formality.content.database.mysql;

import formality.Util.DataTuple;
import formality.content.QModule;
import formality.content.Question;
import formality.content.database.BaseDBAccessor;

import java.util.Vector;
import java.util.TreeMap;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Apr 14, 2010
 * Time: 10:11:45 PM
 * To change this template use File | Settings | File Templates.
 */

public class AdaptiveModuleAccessor extends BaseDBAccessor {

    public Vector loadStudentModules(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Vector mods = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT L.StudentModID, M.ModuleName, M.ModuleType, count(L.QuestionID)as TotalQCt ");
        query.append("FROM StudentModule M, StudentModQuestionLinks L WHERE ");
        query.append("M.StudentID='").append(sID).append("' ");
        query.append("AND L.StudentModID=M.ModuleID ");
        query.append("group by L.StudentModID, M.ModuleName, M.ModuleType");
        res = stmt.executeQuery(query.toString());
        boolean ok = false;
        QModule m;
        while (res.next()) {
            String mID = res.getString("StudentModID");
            String name = res.getString("ModuleName");
            String type = res.getString("ModuleType");
            int ct = res.getInt("TotalQCt");
            m = new QModule(mID, type);
            m.setName(name);
            m.setQuestionCount(ct);
            mods.add(m);
        }
        res.close();
        stmt.close();
        return mods;
    }

    public String getPreTestModID(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        String id = null;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT PreTestIndex FROM UserTable WHERE ");
        query.append("UserID='").append(sID).append("' ");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            id = res.getString("PreTestIndex");
        }
        res.close();
        stmt.close();
        return id;
    }

    public void setPreTestModID(String SID, String MID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int mID = Integer.parseInt(MID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("update UserTable set PreTestIndex='").append(mID).append("' ");
        query.append("WHERE UserID='").append(sID).append("' ");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void setPostTestModID(String SID, String MID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int mID = Integer.parseInt(MID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("update UserTable set PostTestIndex='").append(mID).append("' ");
        query.append("WHERE UserID='").append(sID).append("' ");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public String getPostTestModID(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        String id = null;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT PostTestIndex FROM UserTable WHERE ");
        query.append("UserID='").append(sID).append("' ");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            id = res.getString("PostTestIndex");
        }
        res.close();
        stmt.close();
        return id;
    }

    public Vector<Question> getModuleQuestions(Vector<String> qids, Connection conn) throws Exception {
        Vector<Question> questions = new Vector<Question>();
        for (String qID : qids) {
            Question q = DbQuestion.loadQuestion(qID, conn);
            questions.add(q);
        }
        return questions;
    }

    //load module and q links (as qIDs)
    public QModule loadStudentModule(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        QModule m = new QModule();
        Statement stmt = conn.createStatement();
        ResultSet res;
        String query = "SELECT * FROM StudentModule WHERE ModuleID='" + mID + "'";
        res = stmt.executeQuery(query);
        while (res.next()) {
            m.setID(res.getString("ModuleID"));
            m.setName(res.getString("ModuleName"));
            m.setType(res.getString("ModuleType"));
            m.setStatus(res.getBoolean("Status"));
        }
        res.close();
        stmt.close();
        Vector<String> qids = loadStudentModuleQuestionLinks(MID, conn);
        m.setLinkedQuestionIDs(qids);
        m.setLinkedQuestions(getModuleQuestions(qids, conn));
        return m;
    }

    public Vector loadStudentModuleObjects(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Vector mods = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT L.StudentModID, M.ModuleName, M.ModuleType, count(L.QuestionID)as TotalQCt ");
        query.append("FROM StudentModule M, StudentModQuestionLinks L WHERE ");
        query.append("M.StudentID='").append(sID).append("' ");
        query.append("AND L.StudentModID=M.ModuleID ");
        query.append("group by L.StudentModID, M.ModuleName, M.ModuleType");
        res = stmt.executeQuery(query.toString());
        boolean ok = false;
        QModule m;
        while (res.next()) {
            String mID = res.getString("StudentModID");
            String name = res.getString("ModuleName");
            String type = res.getString("ModuleType");
            int ct = res.getInt("TotalQCt");
            m = new QModule(mID, type);
            m.setName(name);
            m.setQuestionCount(ct);
            mods.add(m);
        }
        res.close();
        stmt.close();
        for (int i = 0; i < mods.size(); i++) {
            QModule qm = (QModule) mods.get(i);
            qm.setLinkedQuestionIDs(loadStudentModuleQuestionLinks(qm.getID(), conn));
        }
        return mods;
    }

    //get a list of Stds that are covered by the modules in modIDs
    public Vector getSkillSet(String[] modIDs, Connection conn) throws Exception {
        Vector S = new Vector();
        int numIDs = modIDs.length;
        if (numIDs > 0) {
            Statement stmt = conn.createStatement();
            ResultSet res;
            StringBuffer query = new StringBuffer();
            query.append("select Std1, Std2, Std3 from Question where QuestionID in ");
            query.append("(select QuestionID from ModQuestionLinks where ");
            String curID, curStd = null;
            for (int i = 0; i < numIDs; i++) {
                curID = modIDs[i];
                query.append(" ModID='").append(Integer.parseInt(curID)).append("' ");
                if (i == (numIDs - 1))
                    query.append(") ");
                else
                    query.append("OR");
            }
            res = stmt.executeQuery(query.toString());
            while (res.next()) {
                curStd = res.getString("Std1");
                if (curStd != null && !curStd.equals(""))
                    S.add(curStd);
                curStd = res.getString("Std2");
                if (curStd != null && !curStd.equals(""))
                    S.add(curStd);
                curStd = res.getString("Std3");
                if (curStd != null && !curStd.equals(""))
                    S.add(curStd);
            }
            res.close();
            stmt.close();
        }
        return S;
    }

    //get all questions linked to the skills. The tuples are: qID, degree, skill
    public Vector getQuestionPool(String[] skills, Connection conn) throws Exception {
        Vector qp = new Vector();
        Vector S = new Vector();
        int numIDs = skills.length;
        if (numIDs > 0) {
            Statement stmt = conn.createStatement();
            ResultSet res;
            StringBuffer query = new StringBuffer();
            query.append("select QuestionID, Degree, Std1, Std2, Std3 from question ");
            String curSkill = null;
            for (int i = 0; i < numIDs; i++) {
                curSkill = skills[i];
                if (i == 0) {
                    query.append("where Std1='").append(curSkill).append("' OR Std2='");
                    query.append(curSkill).append("'OR Std3='").append(curSkill).append("' ");
                } else {
                    query.append("OR Std1='").append(curSkill).append("' OR Std2='");
                    query.append(curSkill).append("'OR Std3='").append(curSkill).append("' ");
                }
            }
            res = stmt.executeQuery(query.toString());
            while (res.next()) {
                DataTuple dt = new DataTuple();
                dt.setFirst(res.getString("QuestionID"));
                dt.setSecond(res.getString("Degree"));
                dt.setThird(res.getString("Std1"));
                String curStd = res.getString("Std2");
                if (curStd != null && !curStd.equals(""))
                    dt.setFourth(curStd);
                curStd = res.getString("Std3");
                if (curStd != null && !curStd.equals(""))
                    dt.setFifth(curStd);
                qp.add(dt);
            }
            res.close();
            stmt.close();
        }
        return qp;
    }

    //true if any student mods exist with this name for this student
    public boolean hasStudentMod(String modName, String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        boolean hasit = false;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM StudentModule WHERE ModuleName='");
        query.append(safeStringCheck(modName)).append("' AND StudentID='").append(sID).append("'");
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            hasit = true;
        }
        res.close();
        stmt.close();
        return hasit;
    }

    //get all questions linked to the skills. The tuples are: qID, degree, skill
    public Vector getQuestionPool(Vector skills, Connection conn) throws Exception {
        Vector qp = new Vector();
        Vector S = new Vector();
        int numIDs = skills.size();
        if (numIDs > 0) {
            Statement stmt = conn.createStatement();
            ResultSet res;
            StringBuffer query = new StringBuffer();
            query.append("select QuestionID, Degree, Std1, Std2, Std3 from question ");
            String curSkill = null;
            for (int i = 0; i < numIDs; i++) {
                curSkill = (String) skills.get(i);
                if (i == 0) {
                    query.append("where Std1='").append(curSkill).append("' OR Std2='");
                    query.append(curSkill).append("'OR Std3='").append(curSkill).append("' ");
                } else {
                    query.append("OR Std1='").append(curSkill).append("' OR Std2='");
                    query.append(curSkill).append("'OR Std3='").append(curSkill).append("' ");
                }
            }
            res = stmt.executeQuery(query.toString());
            while (res.next()) {
                DataTuple dt = new DataTuple();
                dt.setFirst(res.getString("QuestionID"));
                dt.setSecond(res.getString("Degree"));
                dt.setThird(res.getString("Std1"));
                String curStd = res.getString("Std2");
                if (curStd != null && !curStd.equals(""))
                    dt.setFourth(curStd);
                curStd = res.getString("Std3");
                if (curStd != null && !curStd.equals(""))
                    dt.setFifth(curStd);
                qp.add(dt);
            }
            res.close();
            stmt.close();
        }
        return qp;
    }

    // get all qids from mods linked to a course
    public Vector getAllQIDsForSkills(String CID, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        Vector qIDs = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select distinct(Q.QuestionID) from ModQuestionLinks M, Question Q, ModCourseLinks L ");
        query.append("where L.CourseID='").append(cID);
        query.append("' AND Q.Ready='1' AND L.ModuleID=M.ModID AND M.QuestionID=Q.QuestionID");
        String curID, curStd = null;
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            qIDs.add(res.getString("QuestionID"));
        }
        res.close();
        stmt.close();
        return qIDs;
    }

    // get all qids from STUDENT mods linked to a student
    public Vector getStudentModQIDs(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Vector qIDs = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select distinct(Q.QuestionID) from StudentModQuestionLinks M, Question Q, StudentModule S ");
        query.append("where S.StudentID='").append(sID);
        query.append("' AND S.ModuleID=M.StudentModID AND M.QuestionID=Q.QuestionID");
        String curID, curStd = null;
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            qIDs.add(res.getString("QuestionID"));
        }
        res.close();
        stmt.close();
        return qIDs;
    }


    //get a vector of qIDs linked to this module
    public Vector<String> loadStudentModuleQuestionLinks(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        Vector<String> linkedQIDs = new Vector<String>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT QuestionID from StudentModQuestionLinks where StudentModID='").append(mID).append("'");
        String qs = query.toString();
        res = stmt.executeQuery(qs);
        while (res.next()) {
            linkedQIDs.add(res.getString("QuestionID"));
        }
        res.close();
        stmt.close();
        return linkedQIDs;
    }

    /* get the hint TS log entries for a question */
    public Vector getHintLogEntries(String SID, String QID, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int sID = Integer.parseInt(SID);
        Vector hData = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select HintTS from HintLog where UserID='").append(sID).append("' AND ");
        query.append("QuestionID='").append(qID).append("' ");
        query.append("order by HintTS");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            hData.add(new Double(res.getDouble("HintTS")));
        }
        res.close();
        stmt.close();
        return hData;
    }

    /* get any hints for a question for a user that occurs within a time bound*/
    public boolean hintEntryExists(String SID, String QID, double low,
                                   double high, Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int sID = Integer.parseInt(SID);
        boolean exists = false;
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select HintID from HintLog where UserID='").append(sID).append("' AND ");
        query.append("QuestionID='").append(qID).append("' AND ");
        query.append("HintTS > ").append(low).append(" AND ");
        query.append("HintTS < ").append(high);
        res = stmt.executeQuery(query.toString());
        if (res.next()) {
            exists = true;
        }
        res.close();
        stmt.close();
        return exists;
    }

    public void setStudentState(String SID, String state, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        StringBuffer query = new StringBuffer();
        query.append("UPDATE UserTable SET ");
        query.append("State='").append(safeStringCheck(state)).append("'");
        query.append(" WHERE UserID= '").append(sID).append("'");
        Statement stmt = conn.createStatement();
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }


    /* new mod to StudentModule table */
    public String createStudentModule(String SID, String name, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        StringBuffer query = new StringBuffer();
        query.append("insert into StudentModule (StudentID, ModuleName, ModuleType, Status)");
        query.append(" VALUES ( ");
        query.append("'").append(sID).append("', ");
        query.append("'").append(safeStringCheck(name)).append("', 'Student', '1')");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        try {
            stmt.executeUpdate(qs);
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
        int mID = getStudenModInsertionID(SID, conn);
        return Integer.toString(mID);
    }

    public int getStudenModInsertionID(String SID,
                                       Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        int ID = -1;
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT Max(ModuleID) as MaxID FROM StudentModule" +
                " where StudentID='" + sID + "'");
        if (results.next() != false)
            ID = results.getInt("MaxID");
        results.close();
        stmt.close();
        return ID;
    }

    /* then add the question links */
    public void linkQuestionsToStudentModule(String mID, Vector qIDs,
                                             Connection conn) throws Exception {
        for (int i = 0; i < qIDs.size(); i++) {
            String qID = (String) qIDs.get(i);
            linkQuestionToStudentModule(mID, qID, conn);
        }
    }

    public void linkQuestionToStudentModule(String MID, String QID,
                                            Connection conn) throws Exception {
        int qID = Integer.parseInt(QID);
        int mID = Integer.parseInt(MID);
        //the question must be active
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("insert into StudentModQuestionLinks (StudentModID, QuestionID) ");
        query.append("values ('").append(mID).append("', '").append(qID).append("')");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void resetStudentData(String sID, boolean adaptiveTest, boolean resetStudentModel,
                                 Connection conn) throws Exception {
        if (adaptiveTest) {
            resetUserTablePrePostIndex(sID, conn);
            deleteStuModQLinks(sID, conn);
            deleteStudentMods(sID, conn);
        }
        resetUserTableState(sID, conn);
        deleteModLog(sID, conn);
        deleteStudentModuleData(sID, conn);
        deleteResponseLog(sID, conn);
        deleteHintLog(sID, conn);
        if (resetStudentModel) {
            resetTheta(sID, conn);
            resetAlpha(sID, conn);
            resetBeta(sID, conn);
        }
    }


    public void resetUserTableState(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("update usertable set State=null ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    //FOR ADAPTIVE MODS
    public void resetUserTablePrePostIndex(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("update usertable set PreTestIndex=null, PostTestIndex=null");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }


    public void deleteModLog(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("delete from ModuleLog  ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void deleteStudentModuleData(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("delete from StudentModuleData  ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void deleteResponseLog(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("delete from ResponseLog  ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    public void deleteHintLog(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("delete from HintLog  ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    //FOR ADAPTIVE MODS
    public void deleteStuModQLinks(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("delete from StudentModQuestionLinks where StudentModID IN ");
        query.append("(select StudentModID from StudentModule where StudentID='").append(sID).append("')");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    //FOR ADAPTIVE MODS
    public void deleteStudentMods(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("delete from StudentModule ");
        query.append("where StudentID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    /* set all to 0.5  */
    public void resetTheta(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("update StudentFrameworkData set f4P1='0.6867',f4P2='0.55',f4P3='0.683',f4P4='0.695',");
        query.append("f4P5='0.74',f4P6='0.705',f4N1='0.865',f4N2='0.695',f4N3='0.775',f4N4='0.555',f4N5='0.62',");
        query.append("f4N6='0.585',f4N7='0.72',f4N8='0.715',f4N9='0.68',f4N10='0.748',f4N11='0.689',f4N12='0.8',");
        query.append("f4N13='0.689',f4N14='0.689',f4N15='0.689',f4N16='0.689',f4N17='0.545',f4N18='0.42',f4M1='0.665',");
        query.append("f4M2='0.635',f4M3='0.695',f4M4='0.665',f4M5='0.665',f4D1='0.81',f4D2='0.79',f4D3='0.86',");
        query.append("f4D4='0.765',f4D5='0.73',f4D6='0.73',f4G1='0.675',f4G2='0.665',f4G3='0.61',f4G4='0.665',");
        query.append("f4G5='0.66',f4G6='0.665',f4G7='0.665',f4G8='0.66',f4G9='0.71' ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    /* this query updates ALL student alpha */
    public void resetAlpha(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("update StudentFrameworkData set a4N1='2.595',a4N2='2.085',a4N3='2.325',a4N4='1.665',");
        query.append("a4N5='1.86',a4N6='1.755',a4N7='2.16',a4N8='2.145',a4N9='2.04',a4N10='2.244',a4N11='2.067',");
        query.append("a4N12='2.4',a4N13='2.067',a4N14='2.067',a4N15='2.067',a4N16='2.067',a4N17='1.635',a4N18='1.26',");
        query.append("a4P1='2.0601',a4P2='1.65',a4P3='2.049',a4P4='2.085',a4P5='2.22',a4P6='2.115',a4G1='2.025',");
        query.append("a4G2='1.995',a4G3='1.83',a4G4='1.995',a4G5='1.98',a4G6='1.995',a4G7='1.995',a4G8='1.98',");
        query.append("a4G9='2.13',a4M1='1.995',a4M2='1.905',a4M3='2.085',a4M4='1.995',");
        query.append("a4M5='1.995',a4D1='2.43',a4D2='2.37',a4D3='2.58',a4D4='2.295',a4D5='2.19',a4D6='2.19' ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

    /* this query updates ALL student beta */
    public void resetBeta(String SID, Connection conn) throws Exception {
        int sID = Integer.parseInt(SID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("update StudentFrameworkData set b4N1='0.405',b4N2='0.915',b4N3='.675',b4N4='1.335',");
        query.append("b4N5='1.14',b4N6='1.245',b4N7='.84',b4N8='0.855',b4N9='0.96',b4N10='.756',b4N11='.933',");
        query.append("b4N12='.6',b4N13='.933',b4N14='.933',b4N15='.933',b4N16='.933',b4N17='1.365',b4N18='1.74',");
        query.append("b4P1='.9399',b4P2='1.35',b4P3='.951',b4P4='.915',b4P5='.78',b4P6='.885',b4G1='.975',");
        query.append("b4G2='1.005',b4G3='1.17',b4G4='1.005',b4G5='1.02',b4G6='1.005',b4G7='1.005',b4G8='1.02',");
        query.append("b4G9='.87',b4M1='1.005',b4M2='1.095',b4M3='.915',b4M4='1.005',b4M5='1.005',b4D1='.57',");
        query.append("b4D2='.63',b4D3='.42',b4D4='.705',b4D5='.81',b4D6='.81' ");
        query.append("where UserID='").append(sID).append("'");
        try {
            stmt.executeUpdate(query.toString());
        }
        catch (Exception sex) {
            String msg = sex.getMessage();
            if (!msg.contains(sqlExceptionStr_))
                throw sex;
        }
        stmt.close();
    }

}

