package formality.servlet;

import formality.html.GeneralHtml;
import formality.content.database.mysql.DBAccessor;
import formality.content.SystemInfo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Aug 4, 2010
 * Time: 12:41:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaServlet extends DbServlet {
     private static Logger logger = Logger.getLogger(MediaServlet.class);
    public static final String soundPathVar = "questionAudioPath";
    public String soundPath;

     public void init(ServletConfig config) throws ServletException {
        super.init(config);
        soundPath = config.getInitParameter(soundPathVar);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private String getParamsString(Map params) {
        Set<Map.Entry<String,String>> entries = params.entrySet();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry entry: entries) {
            sb.append("["+ entry.getKey()+"=");
            for (String v: (String []) entry.getValue())
                sb.append(v+",");
            sb.deleteCharAt(sb.length()-1);
            sb.append("]");
        }
        return sb.toString();
    }

    // TODO process the incoming files and question id so that a question can have a sound file and
    // a video file saved with it.
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        Map params = request.getParameterMap();
        params = request.getParameterMap();    // get the param map (which is locked)
        logger.info(">"+getParamsString(params));
        try {
            int qid = GeneralHtml.getParamInt(params, GeneralHtml.questionID_, -1);
            String lang = GeneralHtml.getParamStr(params, GeneralHtml.language_, "eng");
            DBAccessor db = new DBAccessor();
            String fn = db.getQuestionAudioFile(getConnection(),qid, lang);

//            byte[] audio=readSoundFile( soundPath+fn);
            response.setContentType("audio/mpeg");
//            response.setContentType("application/octet-stream");
            ServletOutputStream st = response.getOutputStream();
            // create a buffer of 500K to prevent flushing early ??
            BufferedOutputStream bos = new BufferedOutputStream(st,1024*500);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(soundPath+"\\"+fn));
//            bis.write(audio);

//            byte[] buf = new byte[4 * 1024]; // 4K buffer
//            int bytesWritten=0;
//            for (int i =0;i<300;i++) {
//                bis.write(audio,i*1024,1024);
//            }
            int bufsize=1024;
            byte[] buf= new byte[bufsize];
            while (bis.read(buf,0,bufsize) != -1)
                bos.write(buf);
            bis.close();
            bos.close();
            logger.info("<done");
//            bis.flush();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

 public static boolean readAloud(Map params, SystemInfo info, Connection conn, HttpServletResponse resp) throws Exception {
        String lang = GeneralHtml.getParamStr(params, GeneralHtml.language_, "eng");
        String qid = GeneralHtml.getParamStr(params, GeneralHtml.questionID_, "");

//        String fn = dbAccessor_.getQuestionAudioFile(conn,qid, lang);
//        byte[] audio = dbAccessor_.getQuestionAudio(conn, Integer.parseInt(qid), lang);
        String fn = new DBAccessor().getQuestionAudioFile(conn,Integer.parseInt(qid), lang);
        StringBuilder out = new StringBuilder();
//        out.append(audio);
        resp.setContentType("audio/mpeg");
//            response.setContentType("application/octet-stream");
        ServletOutputStream st = resp.getOutputStream();
        // create a buffer of 500K to prevent flushing early ??
        BufferedOutputStream bos = new BufferedOutputStream(st, 1024 * 500);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(info.getSoundPath()+"\\"+fn));
        int bufsize=1024;
        byte[] buf= new byte[bufsize];
        while (bis.read(buf,0,bufsize) != -1)
            bos.write(buf);
        bis.close();
        bos.close();

        return true;
    }        



    // TODO process the incoming files and question id so that a question can have a sound file and
    // a video file saved with it.
    protected void doPost2(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        Map params = request.getParameterMap();
        try {
            int qid = GeneralHtml.getParamInt(params, GeneralHtml.questionID_, -1);
            String lang = GeneralHtml.getParamStr(params, GeneralHtml.language_, "eng");
            DBAccessor db = new DBAccessor();
            byte[] audio = db.getQuestionAudio(getConnection(),qid, lang);
            StringBuilder out = new StringBuilder();
            out.append(audio);
            response.setContentType("audio/mpeg");
//            response.setContentType("application/octet-stream");
            ServletOutputStream st = response.getOutputStream();
            // create a buffer of 500K to prevent flushing early ??
            BufferedOutputStream bis = new BufferedOutputStream(st,1024*500);
//            bis.write(audio);

//            byte[] buf = new byte[4 * 1024]; // 4K buffer
//            int bytesWritten=0;
//            for (int i =0;i<300;i++) {
//                bis.write(audio,i*1024,1024);
//            }
            bis.write(audio);
             bis.close();
//            bis.flush();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
