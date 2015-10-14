package formality.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Mar 9, 2011
 * Time: 1:26:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class MigrateQuestionSounds extends DbConnection {

    public static final String folder ="U:\\Formality\\problemSoundFiles\\";

    public String getFN (int probID) {
        return "prob." +probID+ ".mp3";
    }

    public String getFilename (int probId) {
        return folder + getFN(probId);
    }

    public void writeSoundFile (int probId, byte[] data) throws IOException {
        String fn = getFilename(probId);
        System.out.print("\n Writing sound file: " + fn);

        FileOutputStream fs = new FileOutputStream(fn);
        fs.write(data);
        fs.close();
        System.out.print("... " + data.length + " written");
        
    }

    public void migrateQuestionAudio(Connection conn) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select questionid, audio from Question where audio is not null";
            stmt = conn.prepareStatement(q,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                byte[] c = rs.getBytes(2);
                writeSoundFile(id,c);
            }

        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

 public void addFilenameToTable (Connection conn) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String q = "select questionid,audioFile from Question where audio is not null";
            stmt = conn.prepareStatement(q,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String fn = getFN(id);
                rs.updateString("audioFile",fn);
                rs.updateRow();
                System.out.println("Added " + fn + " to row " + id);
            }

        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = getConnection("cadmium.cs.umass.edu");
            MigrateQuestionSounds mqs = new MigrateQuestionSounds();
//            mqs.migrateQuestionAudio(conn);
            mqs.addFilenameToTable(conn);

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
