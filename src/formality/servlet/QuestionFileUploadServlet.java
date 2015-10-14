package formality.servlet;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.sql.SQLException;
import java.sql.Connection;

import formality.content.database.mysql.DBAccessor;
import formality.content.database.mysql.DbQuestion;
import formality.content.database.mysql.DbHint;
import formality.content.MultipleChoiceQuestion;
import formality.content.SystemInfo;
import formality.content.Hint;
import formality.html.auth.AuthorHtml;
import formality.html.auth.AuthMCQuestionPage;
import formality.controller.AuthoringSubsystem;
import formality.model.UserInfo;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Jul 13, 2010
 * Time: 3:36:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuestionFileUploadServlet extends DbServlet {

    public static final String QFILEUPLOAD="questionFileUpload";
    public static final String HFILEUPLOAD="hintFileUpload";
    public static final String QID="qestId";
    public static final String HID="hintId";
    public static final String ACTION="action";
    public static final String USERID="un";
    public static final String QSPANISH ="qSpanish";
    public static final String QENGLISH ="qSound";
    public static final String QIMAGE="qImage";
    public static final String HIMAGE="hImage";
    private String imageFileDir;
    public static final String soundPathVar = "questionAudioPath";
    public String soundPath;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        imageFileDir = this.getServletContext().getInitParameter("imgFileDir");
        resourcePath = config.getServletContext().getInitParameter("formalityResourceURI"); // path to APache folder where resources come from
        soundPath = config.getInitParameter(soundPathVar);

    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    // TODO process the incoming files and question id so that a question can have a sound file and
    // a video file saved with it.   
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        int probId = 0;
        String hintId="";
        String uid = "";
        byte[] bytes = null;
        byte[] spbytes = null;
        byte[] imgbytes = null;
        String filename="";
        String action="";
        Connection conn=null;
        try {
            conn = getConnection();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (ServletFileUpload.isMultipartContent(request)) {
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items = null;
            try {
                items = upload.parseRequest(request);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Iterator it = items.iterator();
            while (it.hasNext()) {
                FileItem item = (FileItem) it.next();
                if (item.isFormField() && item.getFieldName().equals(QID)) {
                    probId = new Integer(item.getString()).intValue();

                }
                else if (item.isFormField() && item.getFieldName().equals(HID)) {
                    hintId = item.getString();

                }
                else if (item.isFormField() && item.getFieldName().equals(USERID)) {
                    uid = item.getString();

                }
                else if (item.isFormField() && item.getFieldName().equals(ACTION)) {
                    action = item.getString();
                }
                else if (item.getFieldName().equals(QENGLISH)) {
                    if (item.getSize() > 0)
                        bytes = item.get();
                }
                else if (item.getFieldName().equals(QSPANISH)) {
                    if (item.getSize() > 0)
                        spbytes = item.get();
                }
                else if (item.getFieldName().equals(QIMAGE)) {
                    if (item.getSize() > 0)   {
                        imgbytes = item.get();
                        filename = item.getName();
                    }
                }
                 else if (item.getFieldName().equals(HIMAGE)) {
                    if (item.getSize() > 0)   {
                        imgbytes = item.get();
                        filename = item.getName();
                    }
                }
            }
            try {
                formality.content.SystemInfo info = new SystemInfo();
//        info.setContextPath(request.getContextPath());//   /4mality
                info.setContextPath(resourcePath);//   path to apache web folder where images,css,javascript is
                info.setServletPath("/FormalityServlet");//  /servlet/FormalityServlet
                StringBuffer msg = new StringBuffer();
                if (action.equals(QFILEUPLOAD))
                    processQuestionFileUpload(response, probId, uid, bytes, spbytes, imgbytes, filename, info, msg, conn);
                else if (action.equals(HFILEUPLOAD))
                    processHintFileUpload(response,probId,hintId,uid,imgbytes,filename,info,msg, conn);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    private void processHintFileUpload(HttpServletResponse response, int probId, String hintId, String uid, byte[] imgbytes, String filename,
                                       SystemInfo info, StringBuffer msg, Connection conn) throws Exception {
        if (imgbytes != null && imgbytes.length > 0)
                msg.append("<br> " + saveHintImageFile(probId,filename,imgbytes));
        UserInfo userInfo = new UserInfo();

        DBAccessor db = new DBAccessor();
        db.loadSystemInfo(info, conn);
        userInfo.setUserID(uid);
        info.setUserInfo(userInfo);

        AuthorHtml authorPage = new AuthorHtml();
        AuthoringSubsystem as = new AuthoringSubsystem();
        boolean ok = db.validateUser(info, uid, conn);
        response.setContentType("text/html");
        Hint h= DbHint.loadHint(hintId, info, conn);
        List<String> qIDs= DbQuestion.getQuestionsLinkedToHint(h.getID(), conn);
        h.setQuestionLinks(qIDs);
        StringBuffer out = new StringBuffer();
        authorPage.getEditHintPage(h, info, Integer.toString(probId), msg.toString(), out);
        PrintWriter wr = new PrintWriter(response.getOutputStream());
        wr.print(out);
        wr.flush();
        wr.close();
    }

    private void processQuestionFileUpload(HttpServletResponse response, int probId, String uid,
                                           byte[] bytes, byte[] spbytes, byte[] imgbytes, String filename,
                                           SystemInfo info, StringBuffer msg, Connection conn) throws Exception {
        if ((bytes != null && bytes.length > 0) ||
                (spbytes != null && spbytes.length > 0))
            msg.append(saveAudioFiles(probId, bytes, spbytes, conn));
        else if (imgbytes != null && imgbytes.length > 0)
            msg.append("<br> " + saveImageFile(probId,filename,imgbytes));
        UserInfo userInfo = new UserInfo();
        new DBAccessor().loadSystemInfo(info, conn);
        userInfo.setUserID(uid);
        info.setUserInfo(userInfo);
        AuthorHtml authorPage = new AuthorHtml();
        AuthoringSubsystem as = new AuthoringSubsystem();

//       MultipleChoiceQuestion q=loadMultipleChoiceQuestion(qID, info, conn);
//       authorPage_.getEditMultiChoiceQuestionPage(q, null, info, msg_, page);
        response.setContentType("text/html");
        MultipleChoiceQuestion q = as.loadMultipleChoiceQuestion(Integer.toString(probId), conn);
        StringBuffer out = new StringBuffer();
        AuthMCQuestionPage.getEditMultiChoiceQuestionPage(q, null, info, msg.toString(), out, conn);
        PrintWriter wr = new PrintWriter(response.getOutputStream());
        wr.print(out);
        wr.flush();
        wr.close();
    }

    public static String getAudioFilename (int probId) {
        return "prob." + probId + ".mp3";
    }


    public static String getSpanAudioFilename (int probId) {
        return "prob." + probId + ".span.mp3";
    }

    private String saveAudioFiles(int probId, byte[] engSoundBytes, byte[] spanSoundBytes, Connection conn) throws IOException, SQLException {
        StringBuffer sb = new StringBuffer();
        if (engSoundBytes != null && engSoundBytes.length > 0) {
            String fn = getAudioFilename(probId);
            sb.append(saveAudioFile(fn,engSoundBytes));
            DbQuestion.saveAudioFile(conn,probId,fn);
        }
        if (spanSoundBytes !=null && spanSoundBytes.length > 0) {
            sb.append("<br>");
            String fn = getSpanAudioFilename(probId);
            sb.append(saveAudioFile(fn,spanSoundBytes));
            DbQuestion.saveSpanishAudioFile(conn,probId,fn);
        }
        return sb.toString();
    }

    private String saveAudioFile(String filename, byte[] engSoundBytes) throws IOException {
        String msg = "";

        String path =soundPath + "\\" + filename ;
        File f = new File(path);
        if (f.exists()) {
            msg= "Existing file " +filename+ " has been overwritten.";
            f.delete();
        }
        else {
            msg="New file " + filename + " has been written.";
        }
        System.out.println("Writing file " + path);
        BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(f));
        bis.write(engSoundBytes);
        bis.close();
        return msg;
    }

    private String saveImageFile(int probId, String filename, byte[] imgbytes) throws IOException {
        String msg = "";
        String path =imageFileDir + "\\" + filename ;
        File f = new File(path);
        if (filename.indexOf(Integer.toString(probId)) == -1)
             return "File not written: Filename does not contain the problem ID. Name it something like stem"+probId+".jpg";
        else if (f.exists()) {
            msg= "Existing file " +filename+ " has been overwritten.";
            msg += "<br>Refer to it as &lt;img src=&quot;{MediaRoot}/images/" +filename+ "&quot;/&gt;";
            f.delete();
        }
        else {
            msg="New file " + filename + " has been written.";
            msg += "<br>Refer to it as &lt;img src=&quot;{MediaRoot}/images/" +filename+ "&quot;/&gt;";
        }
        System.out.println("Writing file " + path);
        BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(f));
        bis.write(imgbytes);
        bis.close();
        return msg;
    }

    private String saveHintImageFile(int probId, String filename, byte[] imgbytes) throws IOException {
        String msg = "";
        String path =imageFileDir + "\\" + filename ;
        File f = new File(path);
        if (filename.indexOf(Integer.toString(probId)) == -1)
             return "File not written: Filename does not contain the problem ID. Name it something like VV_"+probId+"_1.jpg";
        else if (f.exists()) {
            msg= "Existing file " +filename+ " has been overwritten.";
            msg += "<br>Refer to it as &lt;img src=&quot;{MediaRoot}/images/" +filename+ "&quot;/&gt;";
            f.delete();
        }
        else {
            msg="New hint file " + filename + " has been written.";
            msg += "<br>Refer to it as &lt;img src=&quot;{MediaRoot}/images/" +filename+ "&quot;/&gt;";
        }
        System.out.println("Writing file " + path);
        BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(f));
        bis.write(imgbytes);
        bis.close();
        return msg;
    }

    // no longer saving sounds files in the db.
    private boolean saveAudioFile2(Connection conn, int probId, byte[] engAudio, byte[] spanAudio) throws SQLException {
        boolean success=false;
        if (engAudio != null && engAudio.length > 0)
            success = DbQuestion.updateQuestionAudioFile(conn, probId, "audio", engAudio) != 0;
        if (spanAudio != null && spanAudio.length > 0)
            success = DbQuestion.updateQuestionAudioFile(conn, probId, "spanishAudio", spanAudio) != 0;
        return success;
    }

}
