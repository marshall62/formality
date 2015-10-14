package formality.content.database.mysql;

import formality.content.QModule;
import formality.content.Question;
import formality.content.ModModuleLink;
import formality.systemerror.AuthoringException;
import formality.Util.DataTuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Oct 4, 2010
 * Time: 9:45:21 AM
 * All queries rewritten to use prepared statments
 */
public class DbModule {

    public static int saveNewModule (QModule m, Connection conn) throws Exception {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "insert into Module (Name, ModuleType, Status, Author, OrderStr, isReentrant) values (?,?,?,?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,DbUtil.safeStringCheck(m.getName()));
            stmt.setString(2,DbUtil.safeStringCheck(m.getType()));
            stmt.setInt(3,0);
            stmt.setString(4,DbUtil.safeStringCheck(m.getAuthorID()));
            stmt.setString(5,m.getQuestionSequencing().name());
            stmt.setBoolean(6,false); // by default modules are not re-entrant
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

    public static int saveModule(QModule m, Connection conn) throws Exception {
        String mID = m.getID();
        if (mID == null) {
            AuthoringException ae = new AuthoringException();
            ae.setMessage("Module ID=null, it is not initialized.");
            throw ae;
        }
        PreparedStatement stmt=null;
        try {
            String q = "update Module set Name=?, ModuleType=?, OrderStr=?, Status=?, IsReentrant=? where ModuleID=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,DbUtil.safeStringCheck(m.getName()));
            stmt.setString(2,m.getType());
            stmt.setString(3,m.getQuestionSequencing().name());
            stmt.setBoolean(4, m.getStatus());
            stmt.setBoolean(5, m.isReentrant());
            stmt.setInt(6, Integer.parseInt(m.getID()));
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }




    public static List<QModule> loadModules(Connection conn) throws Exception {
        List<QModule> mods = new ArrayList<QModule>();
        ResultSet res=null;
        PreparedStatement stmt=null;
        try {
            String q = "select * from Module";
            QModule m;
            stmt = conn.prepareStatement(q);
            res = stmt.executeQuery();
            while (res.next()) {
                String mID = res.getString("ModuleID");
                String name = res.getString("Name");
                String type = res.getString("ModuleType");
                boolean status = res.getBoolean("Status");
                m = new QModule(mID, type);
                m.setName(name);
                m.setStatus(status);
                String order = res.getString("OrderStr");
                if (res.wasNull())
                    m.setQuestionSequencing(QModule.DEFAULT_ORDERING.name());
                else m.setQuestionSequencing(order);
                boolean isReentrant = res.getBoolean("IsReentrant");
                m.setReentrant(isReentrant);
                mods.add(m);
            }
            return mods;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (res != null)
                res.close();
        }
    }

    // Return a list of modules except those provided in the modIds list.
    public static List<QModule> loadModulesWithQIDsExcluding(Connection conn, List<String> modIDs) throws Exception {
        List<QModule> mods = new ArrayList<QModule>();
        List<QModule> activeMods = DbModule.loadActiveModules(conn);
        for (QModule m: activeMods) {
            if (!memberOf(m.getID(),modIDs)) {
                // load the linked questions and then sets the Module's question list to contain Question objects
                // in order prescribed by how loadModuleQuestionLinks returns ids (as strings)
                List<String> qids = loadModuleQuestionLinks(m.getID(), conn, m.getQuestionSequencing());
                m.setLinkedQuestionIDs(qids);
                m.setLinkedQuestions(getModuleQuestions(qids, conn));
                mods.add(m);
            }
        }
        return mods;
    }

    private static boolean memberOf(String curID, List<String> modIDs) {
        for (String id: modIDs)
            if (id.equals(curID))
                return true;
        return false;
    }


    public static List<QModule> loadModulesWithQIDs(Connection conn) throws Exception {
        List<QModule> mods = new ArrayList<QModule>();
        List<String> ids = DbModule.loadModuleIDs(conn);
        for (String curID: ids) {
            QModule curM = DbModule.loadModule(curID, conn);
            mods.add(curM);
        }
        return mods;
    }

    public static List<QModule> loadActiveModules(Connection conn) throws Exception {
        List<QModule> mods = new ArrayList<QModule>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        String query = "SELECT * FROM Module WHERE Status=1";
        res = stmt.executeQuery(query);
        boolean ok = false;
        QModule m;
        while (res.next()) {
            String mID = res.getString("ModuleID");
            String name = res.getString("Name");
            String type = res.getString("ModuleType");
            boolean status = res.getBoolean("Status");
            m = new QModule(mID, type);
            m.setName(name);
            m.setStatus(status);
            String order = res.getString("OrderStr");
            if (res.wasNull())
                m.setQuestionSequencing(QModule.DEFAULT_ORDERING.name());
            else m.setQuestionSequencing(order);
            boolean isReentrant = res.getBoolean("IsReentrant");
            m.setReentrant(isReentrant);
            String auth = res.getString("Author");
            m.setAuthorName(auth);
            mods.add(m);
        }
        res.close();
        stmt.close();
        return mods;
    }

    public static List<DataTuple> loadActiveModules(String CID, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        List<DataTuple> mods = new ArrayList<DataTuple>();  // TODO DM Used to be a Vector
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT M.ModuleID, M.Name, M.ModuleType ");
        query.append("FROM Module M, ModCourseLinks C WHERE ");
        query.append("C.CourseID='").append(cID).append("' ");
        query.append("AND M.ModuleID=C.ModuleID AND M.Status=1");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            DataTuple dt = new DataTuple();
            dt.setFirst(res.getString("ModuleID"));
            dt.setSecond(res.getString("Name"));
            dt.setThird(res.getString("ModuleType"));
            mods.add(dt);
        }
        res.close();
        stmt.close();
        return mods;
    }


    public static List<QModule> loadActiveModuleObjects(String CID, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        List<QModule> mods = new ArrayList<QModule>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT M.ModuleID, M.Name, M.ModuleType, M.OrderStr, M.Author, M.status ");
        query.append("FROM Module M, ModCourseLinks C WHERE ");
        query.append("C.CourseID='").append(cID).append("' ");
        query.append("AND M.ModuleID=C.ModuleID AND M.Status=1");
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            String mID = res.getString("ModuleID");
            String name = res.getString("Name");
            String type = res.getString("ModuleType");
            String ord = res.getString("OrderStr");
            String auth = res.getString("Author");
            boolean stat = res.getBoolean("status");
            QModule m = new QModule(mID, type);
            if (res.wasNull())
                m.setQuestionSequencing(QModule.DEFAULT_ORDERING.name());
            else m.setQuestionSequencing(ord);
            m.setName(name);
            m.setAuthorName(auth);
            m.setStatus(stat);
            mods.add(m);
        }
        res.close();
        stmt.close();
        for (QModule qm :mods) {
            qm.setLinkedQuestionIDs(DbModule.loadModuleQuestionLinks(qm.getID(), conn, qm.getQuestionSequencing()));
        }
        return mods;
    }


    public static List<QModule> loadActiveModulesOfType(String type, Connection conn) throws Exception {
        List<QModule> mods = new ArrayList<QModule>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT ModuleID, Name, count(QuestionID)as TotalQCt ");
        query.append(" FROM Module M, ModQuestionLinks L WHERE Status=1 AND ");
        query.append("ModuleType='").append(DbUtil.safeStringCheck(type)).append("' ");
        query.append("AND M.ModuleID=L.ModuleID group by ModuleID, Name");
        res = stmt.executeQuery(query.toString());
        boolean ok = false;
        QModule m;
        while (res.next()) {
            String mID = res.getString("ModuleID");
            String name = res.getString("Name");
            int ct = res.getInt("TotalQCt");
            m = new QModule(mID, type);
            m.setName(name);
            m.setQuestionCount(ct);
            mods.add(m);
        }
        res.close();
        stmt.close();

        for (QModule qm : mods) {
            // if(qm.getType().equals("Test"))
            qm.setParentModIDs(DbModule.getModModuleLinks(qm.getID(), conn));

        }
        return mods;
    }


    //returns a Vector of QModule objects
    public static List<QModule> loadActiveModulesOfType(String CID, String type, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        List<QModule> mods = new ArrayList<QModule>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT C.ModuleID, Name, count(QuestionID)as TotalQCt ");
        query.append("FROM Module M, ModQuestionLinks L, ModCourseLinks C WHERE Status=1 ");
        query.append("AND M.ModuleType='").append(DbUtil.safeStringCheck(type)).append("' AND C.CourseID='");
        query.append(cID).append("' ");
        query.append("AND C.ModuleID=M.ModuleID AND M.ModuleID=L.ModuleID ");
        query.append("group by C.ModuleID, Name");
        res = stmt.executeQuery(query.toString());
        boolean ok = false;
        QModule m;
        while (res.next()) {
            String mID = res.getString("ModuleID");
            String name = res.getString("Name");
            int ct = res.getInt("TotalQCt");
            m = new QModule(mID, type);
            m.setName(name);
            m.setQuestionCount(ct);
            mods.add(m);
        }
        res.close();
        stmt.close();
        ///  If a course has a setting that indicates that linkage of modules is defined for the course
        // as opposed to globally (withing the modules), then get the parent modules using the course info.
        boolean hasCourseModuleLinkage = DbModule.hasCourseModuleLinkage(conn,CID);
        for (QModule qm: mods)
            if (hasCourseModuleLinkage)
                qm.setParentModIDs(DbModule.getClassModModuleLinks(qm.getID(),CID,conn));
            else qm.setParentModIDs(DbModule.getModModuleLinks(qm.getID(), conn));
        return mods;
    }


    public static QModule cloneModule (String mid, Connection conn) throws Exception {
        // copy mid's module row,  copy modulequestionlinks using the new mod id
        // for now do not copy parents
        QModule modToCopy = loadModule(mid,conn);
        modToCopy.setName(modToCopy.getName() + " Clone");
        int newId = saveNewModule(modToCopy,conn);
        String mid2 = Integer.toString(newId);
        String[] qids = modToCopy.getLinkedQuestionIDVecAsArray();
        for (String qid: qids) {
            DbQuestion.linkQuestionToModule(qid,mid2,conn);
        }
        return loadModule(mid2,conn);
    }

    //load module and q links (as qIDs)
    public static QModule loadModule(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        QModule m = new QModule();
        Statement stmt = conn.createStatement();
        ResultSet res;
        String query = "SELECT * FROM Module WHERE ModuleID='" + mID + "'";
        res = stmt.executeQuery(query);
        while (res.next()) {
            m.setID(res.getString("ModuleID"));
            m.setName(res.getString("Name"));
            m.setType(res.getString("ModuleType"));
            m.setStatus(res.getBoolean("Status"));
            m.setAuthorID(res.getString("Author"));
            String ord = res.getString("OrderStr");
            if (res.wasNull())
                m.setQuestionSequencing(QModule.DEFAULT_ORDERING.name());
            else m.setQuestionSequencing(ord);
            boolean isReentrant = res.getBoolean("IsReentrant");
            m.setReentrant(isReentrant);
        }
        res.close();
        stmt.close();

        // load the linked questions and then sets the Module's question list to contain Question objects
        // in order prescribed by how loadModuleQuestionLinks returns ids (as strings)
        List<String> qids = loadModuleQuestionLinks(MID, conn, m.getQuestionSequencing());
        m.setLinkedQuestionIDs(qids);
        m.setLinkedQuestions(getModuleQuestions(qids, conn));
        m.setParentModIDs(getModModuleLinks(MID, conn));
        return m;
    }

    /**
     * Make a list of Question objects that follow the order given in the list of qids.
     *
     * @param qids
     * @param conn
     * @return
     * @throws Exception
     */
    public static List<Question> getModuleQuestions(List<String> qids, Connection conn) throws Exception {
        List<Question> questions = new ArrayList<Question>();
        for (String qID : qids) {
            Question q = DbQuestion.loadQuestion(qID, conn);
            questions.add(q);
        }
        return questions;
    }


    public static List<String> getClassModModuleLinks(String CMID, String courseID, Connection conn) throws Exception {
        List<String> parentIDs = new ArrayList<String>();
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select ParentModID from ModModuleLinks where ChildModID=? and courseId=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,CMID);
            stmt.setString(2,courseID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String pmID =rs.getString(1);
                parentIDs.add(pmID);
            }
            return parentIDs;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static List<String> getModModuleLinks(String CMID, Connection conn) throws Exception {
        int cmID = Integer.parseInt(CMID);
        List<String> parentIDs = new ArrayList<String>();
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT ParentModID FROM ModModuleLinks WHERE ChildModID='" + cmID + "'");
        while (res.next()) {
            parentIDs.add(res.getString("ParentModID"));
        }
        res.close();
        stmt.close();
        return parentIDs;
    }

    //get a List of qIDs linked to this module
    public static List<String> loadModuleQuestionLinks(String MID, Connection conn, QModule.OrderType orderType) throws Exception {
        int mID = Integer.parseInt(MID);
        List<String> linkedQIDs = new ArrayList<String>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        if (orderType == QModule.OrderType.fixed)
            query.append("SELECT QuestionID from ModQuestionLinks where ModuleID='").append(mID).append("' ORDER BY seq ASC");
        else query.append("SELECT QuestionID from ModQuestionLinks where ModuleID='").append(mID).append("' ORDER BY id ASC");
        String qs = query.toString();
        res = stmt.executeQuery(qs);
        while (res.next()) {
            linkedQIDs.add(res.getString("QuestionID"));
        }
        res.close();
        stmt.close();
        return linkedQIDs;
    }


    public static List<String> loadModuleIDs(Connection conn) throws Exception {
        List<String> modIDs = new ArrayList<String>();
        Statement stmt = conn.createStatement();
        ResultSet res;
        String query = "SELECT ModuleID FROM Module";
        res = stmt.executeQuery(query);
        boolean ok = false;
        QModule m;
        while (res.next()) {
            modIDs.add(res.getString("ModuleID"));
        }
        res.close();
        stmt.close();
        return modIDs;
    }

    public static boolean modModuleLinkExists(String PMID, String CMID,
                                       Connection conn) throws Exception {
        int pmID = Integer.parseInt(PMID);
        int cmID = Integer.parseInt(CMID);
        boolean hasit = false;
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("SELECT ParentModID FROM ModModuleLinks WHERE ParentModID='");
        query.append(pmID).append("'");
        query.append(" AND ChildModID='").append(cmID).append("'");
        ResultSet res = stmt.executeQuery(query.toString());
        if (res.next()) {
            hasit = true;
        }
        res.close();
        stmt.close();
        return hasit;
    }

    //want to exclude this mod and those with links to it
    public static List<QModule> loadModulesWithQIDs(String cmID, Connection conn) throws Exception {
        List<QModule> mods = new ArrayList<QModule>();
        List<String> ids = DbModule.loadModuleIDs(conn);
        for (String curID: ids) {
            if (!cmID.equals(curID) && !modModuleLinkExists(curID, cmID, conn)) {
                QModule curM = loadModule(curID, conn);
                mods.add(curM);
            }
        }
        return mods;
    }

    public static void moveQuestionEarlier(QModule m, String qID, Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        int qid = Integer.parseInt(qID);
        try {
            String q = "select seq, questionId, id from modquestionlinks where moduleId=? order by seq";
            stmt = conn.prepareStatement(q,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            stmt.setInt(1,Integer.parseInt(m.getID()));
            rs= stmt.executeQuery();
            int i=0;
            while (rs.next()) {
                int thisqid=rs.getInt(2);
                // make sure its not try to move the first element earlier
                if (i>0 &&  thisqid== qid) {
                    int seq = rs.getInt(1);
                    // Swap the positions of this row and the previous row
                    // give this row the next earlier position
                    rs.updateInt(1,seq-1);
                    rs.updateRow();
                    // give the previous row the next later position
                    rs.previous();
                    rs.updateInt(1,seq);
                    rs.updateRow();
                    return;
                }
                i++;
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }    
    }

    public static void moveQuestionLater(QModule m, String qID, Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        int qid = Integer.parseInt(qID);
        try {
            // work through the list in descending order so we can make sure that we don't try to move
            // a question beyond the current end.
            String q = "select seq, questionid, id from modquestionlinks where moduleId=? order by seq desc";
            stmt = conn.prepareStatement(q,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            stmt.setInt(1,Integer.parseInt(m.getID()));
            rs= stmt.executeQuery();
            int i=0;
            while (rs.next()) {
                int thisqid=rs.getInt(2);
                // Swap the sequence values of this row and the previous row
                // make sure its not try to move the first element
                if (i>0 && thisqid == qid) {
                    int seq = rs.getInt(1);
                    rs.updateInt(1,seq+1);
                    rs.updateRow();
                    rs.previous();
                    rs.updateInt(1,seq);
                    rs.updateRow();
                    return;
                }
                i++;
            }
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static List<String> getModuleFixedQuestionSequence (Connection conn, String mid) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        List<String> ids = new ArrayList<String>();
        boolean found = false;
        try {
            String q = "select questionid from modquestionlinks where moduleid=? order by seq";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,Integer.parseInt(mid));
            rs = stmt.executeQuery();
            while (rs.next()) {
                found =true;
                int qid= rs.getInt(1);
                ids.add(Integer.toString(qid));
            }
            if (found)
                return ids;
            else return null;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }


    /* if deleting a module */
    public static void deleteModCourseLinks(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ModCourseLinks WHERE ModuleID='").append(mID).append("'");
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }

    /* if deleting a course */
    public static void deleteAllModCourseLinks(String CID, Connection conn) throws Exception {
        int cID = Integer.parseInt(CID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ModCourseLinks WHERE CourseID='").append(cID).append("'");
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }

    public static void deleteAllModModuleLinks(String CMID, Connection conn) throws Exception {
        int cmID = Integer.parseInt(CMID);
        PreparedStatement stmt=null;
        try {
            String q = "DELETE FROM ModModuleLinks WHERE ChildModID=? or ParentModID=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,cmID);
            stmt.setInt(2,cmID);
            stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static void deleteModCourseLink(String MID, String CID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        int cID = Integer.parseInt(CID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ModCourseLinks WHERE ModuleID='").append(mID).append("'");
        query.append(" AND CourseID='").append(cID).append("'");
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }


    public static void deleteModModuleLink(String PMID, String CMID, Connection conn) throws Exception {
        int pmID = Integer.parseInt(PMID);
        int cmID = Integer.parseInt(CMID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ModModuleLinks WHERE ParentModID='").append(pmID).append("'");
        query.append(" AND ChildModID='").append(cmID).append("'");
        String qs = query.toString();
        stmt.executeUpdate(qs);

        stmt.close();
    }

    /* if deleting a module */
    public static void deleteModQuestionLinks(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        Statement stmt = conn.createStatement();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ModQuestionLinks WHERE ModuleID='").append(mID).append("'");
        String qs = query.toString();
        stmt.executeUpdate(qs);
        stmt.close();
    }

    //create a link between modules
    public static void addModModuleLink(String CMID, String PMID, Connection conn) throws Exception {
        int cmID = Integer.parseInt(CMID);
        int pmID = Integer.parseInt(PMID);
        //check for existing link
        Statement stmt = conn.createStatement();
        String qs = "INSERT INTO ModModuleLinks (ParentModID, ChildModID) VALUES (" + pmID + ", " + cmID + ")";
        stmt.executeUpdate(qs);

        stmt.close();
    }

   //create a link between modules
    public static void addModCourseLink(String MID, String CID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        int cID = Integer.parseInt(CID);
        //check for existing link
        Statement stmt = conn.createStatement();
        String qs = "INSERT INTO ModCourseLinks (ModuleID, CourseID) VALUES (" + mID + ", " + cID + ")";
       stmt.executeUpdate(qs);

        stmt.close();
    }

    public static void deleteModule(String mID, Connection conn) throws Exception {
        DbModule.deleteModQuestionLinks(mID, conn);
        DbModule.deleteAllModModuleLinks(mID,conn);
        DbModule.deleteModCourseLinks(mID,conn);
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM Module");
        query.append(" WHERE ModuleID= '").append(mID).append("'");
        Statement stmt = conn.createStatement();
        String qs = query.toString();
        stmt.executeUpdate(qs);
        
        stmt.close();


    }

    public static boolean visitedModule(String UID, String MID, Connection conn) throws Exception {
        int uID = Integer.parseInt(UID);
        int mID = Integer.parseInt(MID);
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM StudentSessionData WHERE UserID=").append(uID);
        query.append("AND ModID=").append(mID);
        res = stmt.executeQuery(query.toString());
        boolean ok = false;
        if (res.next()) {
            ok = true;
        }
        res.close();
        stmt.close();
        return ok;
    }


/*



    //load module and q links (as qIDs)
    // This used to be called everywhere in the system but all calls then had to modify the module to
    // have a list of Question objects in it that followed the same order as the question ids this loads so I renamed this method
    // and wrote loadModule below so that it does this common operation
    public QModule loadModule1(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        QModule module = new QModule();
        Statement stmt = conn.createStatement();
        ResultSet res;
        String query = "SELECT * FROM Module WHERE ModuleID='" + mID + "'";
        res = stmt.executeQuery(query);
        while (res.next()) {
            module.setID(res.getString("ModuleID"));
            module.setName(res.getString("Name"));
            module.setType(res.getString("ModuleType"));
            module.setStatus(res.getBoolean("Status"));
            module.setAuthorID(res.getString("Author"));
        }
        res.close();
        stmt.close();

        List<String> qids = DbModule.loadModuleQuestionLinks(MID, conn);
        module.setLinkedQuestionIDs(qids);
        module.setParentModIDs(DbModule.getModModuleLinks(MID, conn));
        return module;
    }

    //load module and q links (as qIDs), NO dependencies
    public QModule loadModule2(String MID, Connection conn) throws Exception {
        int mID = Integer.parseInt(MID);
        QModule module = new QModule();
        Vector linkedQIDs = new Vector();
        //the actual question objects
        Vector linkedQs = new Vector();
        Statement stmt = conn.createStatement();
        ResultSet res;
        StringBuffer query = new StringBuffer();
        query.append("select M.ModuleID, M.Name, M.ModuleType, M.Status, M.orderStr, M.Author, Q.QuestionID, Q.Qtype, ");
        query.append("Q.Author, Q.Source, Q.Degree, Q.CategoryID, Q.Ready, Q.DiffLevel, Q.Stem, Q.Std1, Q.Std2, Q.Std3, Q.Topic,Q.audio,Q.spanishAudio,Q.answerLayout ");
        query.append("FROM module M, ModQuestionLinks L, Question Q ");
        query.append("WHERE M.moduleid=L.moduleid AND L.questionid=Q.questionid AND M.moduleid='").append(mID).append("'");
        int i = 0;
        String qID = "";
        String qType = "";
        res = stmt.executeQuery(query.toString());
        while (res.next()) {
            if (i == 0) {
                module.setID(res.getString("ModuleID"));
                module.setName(res.getString("Name"));
                module.setType(res.getString("ModuleType"));
                module.setStatus(res.getBoolean("Status"));
                module.setAuthorID(res.getString("Author"));
            }
            qID = res.getString("QuestionID");
            linkedQIDs.add(qID);
            qType = res.getString("QType");
            if (qType.equals("mc")) {
                MultipleChoiceQuestion q = new MultipleChoiceQuestion(qID);
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
                q.setTopic(res.getString("Topic"));
                q.setAudio(res.getBytes("audio"));
                q.setSpanishAudio(res.getBytes("spanishAudio"));
                q.setSequentialLayout(res.getInt("answerLayout") == Question.ANSWER_LAYOUT_SEQUENTIAL);
                linkedQs.add(q);
            } else {
                Question q = new Question(qID);
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
                q.setTopic(res.getString("Topic"));
                q.setAudio(res.getBytes("audio"));
                q.setSpanishAudio(res.getBytes("spanishAudio"));
                q.setSequentialLayout(res.getInt("answerLayout") == Question.ANSWER_LAYOUT_SEQUENTIAL);
                linkedQs.add(q);
            }
            i++;
        }
        res.close();
        stmt.close();
        module.setLinkedQuestionIDs(linkedQIDs);
        module.setLinkedQuestions(linkedQs);
        //module.setParentModIDs(getModModuleLinks(mID, conn));
        return module;
    }
     */


    public static boolean verifyFixedQuestionSequence(Connection conn, QModule m) throws SQLException {
        List<String> ids = getModuleFixedQuestionSequence(conn,m.getID());
        if (ids != null)
            return true;
        else  {
            // TODO build the rows in the modulequestionsequence table in the order that represents the
            // current list of ids.
            insertQuestionSequence(conn,m.getID(),m.getLinkedQuestionIDList());
            return true;
        }
    }

    private static void insertQuestionSequence(Connection conn, String mId, List<String> linkedQuestionIDList) throws SQLException {
        int i=0;
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            for (String qid: linkedQuestionIDList) {
                String q = "insert into modulequestionsequence (studId,modId,qid,seq) values (?,?,?,?)";
                stmt = conn.prepareStatement(q);
                stmt.setInt(1,-1);
                stmt.setInt(2,Integer.parseInt(mId));
                stmt.setInt(3,Integer.parseInt(qid));
                stmt.setInt(4,i++);
                stmt.execute();
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
    }

    public static List<ModModuleLink> getClassModuleLinks (String classId, Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select parentModId, pm.Name, childModId, cm.Name from modmodulelinks, module pm, module cm" +
                    " where courseid=? and parentModId=pm.moduleId and childModId=cm.moduleId";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,classId);
            rs = stmt.executeQuery();
            List<ModModuleLink> links = new ArrayList<ModModuleLink>();
            while (rs.next()) {
                String pId= rs.getString(1);
                String pmName=rs.getString(2);
                String cId= rs.getString(3);
                String cmName=rs.getString(4);

                links.add(new ModModuleLink(cId,cmName,pId,pmName,classId));
            }
            return links;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static Set<QModule> getClassModules(String classID, Connection conn) throws Exception {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select parentmodid, childmodid from modmodulelinks where courseid=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,Integer.parseInt(classID));
            rs = stmt.executeQuery();
            Set<QModule> mods = new TreeSet<QModule>();
            while (rs.next()) {
                int pmod= rs.getInt(1);
                int cmod= rs.getInt(2);
                mods.add(loadModule(Integer.toString(pmod),conn));
                mods.add(loadModule(Integer.toString(cmod),conn));
            }
            return mods;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static void addCourseModModuleLink(String classID, String parentModId, String childModId, Connection conn) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "insert into modmodulelinks (parentmodid, childmodid, courseid) values (?,?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,parentModId);
            stmt.setString(2,childModId);
            stmt.setString(3,classID);
            stmt.execute();
        }
        finally {
            if (stmt != null)
                stmt.close();
        }
    }

    public static void deleteCourseModModuleLink(String classID, String parentModId, String childModId, Connection conn) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "delete from modmodulelinks where parentmodid=? and childmodid=? and courseid=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,parentModId);
            stmt.setString(2,childModId);
            stmt.setString(3,classID);
            stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    private static boolean hasCourseModuleLinkage(Connection conn, String cID) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select setsModuleLinkage from course where courseid=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            boolean setsLinkage=false;
            if (rs.next()) {
                setsLinkage= rs.getBoolean(1);

            }
            return setsLinkage;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static boolean inClassModuleDependency(String classID, String modID, Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            String q = "select count(*) from modmodulelinks where courseid=? and (parentmodid=? or childmodid=?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,classID);
            stmt.setString(2,modID);
            stmt.setString(3,modID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int c= rs.getInt(1);
                return c>0;
            }
            return false;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static void addCourseModules(String classID, List<String> modIds, Connection conn) throws SQLException {
        for (String modId: modIds)
            addCourseModule(classID,modId,conn);
    }


    public static void addCourseModule(String classID, String modId, Connection conn) throws SQLException {
        PreparedStatement stmt=null;
        try {
            String q = "insert into modCourseLinks (moduleid, courseid) values (?,?)";
            stmt = conn.prepareStatement(q);
            stmt.setString(1,modId);
            stmt.setString(2,classID);
            stmt.execute();
        }
        finally {
            if (stmt != null)
                stmt.close();
        }
    }
}
