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
import formality.content.database.mysql.DbCourse;
import formality.content.MultipleChoiceQuestion;
import formality.content.SystemInfo;
import formality.content.Hint;
import formality.content.Course;
import formality.html.auth.AuthorHtml;
import formality.html.auth.AuthMCQuestionPage;
import formality.html.auth.CreateNewStudentPage;
import formality.html.GeneralHtml;
import formality.controller.AuthoringSubsystem;
import formality.controller.FormalitySubsystem;
import formality.model.UserInfo;
import formality.admin.AddStudents;
import formality.admin.User;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Jul 13, 2010
 * Time: 3:36:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentsFileUploadServlet extends DbServlet {

    public static final String STUDFILEUPLOAD="studentFileUpload";
    public static final String COURSEID="classID";
    public static final String ACTION="action";
    public static final String USERID="un";
    public static final String STUDFILE="studFile";
    private String studentFileDir;
    public String filePath;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
       // studentFileDir = this.getServletContext().getInitParameter("studentFileDir");
//        resourcePath = config.getServletContext().getInitParameter("resourceURI"); // path to APache folder where resources come from

    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        int courseId = 0;
        InputStream uploadStream = null;
        String filename="";
        String uid="";
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
                if (item.isFormField() && item.getFieldName().equals(COURSEID)) {
                    courseId = new Integer(item.getString()).intValue();

                }

                else if (item.isFormField() && item.getFieldName().equals(FormalitySubsystem.userIDParam)) {
                    uid = item.getString();
                }

                else if (item.getFieldName().equals(STUDFILE)) {
                    if (item.getSize() > 0)   {
                        uploadStream = item.getInputStream();
//                        filename = item.getName();
                    }
                }

            }
            try {
                SystemInfo info = new SystemInfo();
                info.setContextPath(resourcePath);//   path to apache web folder where images,css,javascript is
                info.setServletPath("/FormalityServlet");//  /servlet/FormalityServlet
                StringBuffer msg = new StringBuffer();
                Course course = DbCourse.getCourse(conn,courseId);
                if (uploadStream.available() > 0)  {
                    AddStudents a = new AddStudents(Integer.parseInt(course.getCourseID()));
                    List<User> students = AddStudents.readStudentCSVStream(uploadStream, course.getInstitution(),course.getDistrictName());
                    a.doInsert(conn,students);
                    response.sendRedirect("FormalityServlet?" + FormalitySubsystem.fxnParam + "=" +FormalitySubsystem.authorFxnParam+ "&" +FormalitySubsystem.modeParam+ "="+ AuthoringSubsystem.saveNewStudentsUpload_ +
                            "&" + FormalitySubsystem.userIDParam + "=" +uid+ "&"+ GeneralHtml.classID_+"="+courseId);
                }
            } catch (Exception ee) {
                ee.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                response.getWriter().println(ee.getMessage());
                ee.printStackTrace(response.getWriter());
            }
        }

    }

    private void processStudentFileUpload(HttpServletResponse response, int courseId, byte[] filebytes, String filename, SystemInfo info, StringBuffer msg, Connection conn) {
        //To change body of created methods use File | Settings | File Templates.
    }





    public static String getAudioFilename (int probId) {
        return "prob." + probId + ".mp3";
    }


    public static String getSpanAudioFilename (int probId) {
        return "prob." + probId + ".span.mp3";
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