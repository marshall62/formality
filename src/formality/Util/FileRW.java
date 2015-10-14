package formality.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Mar 19, 2009
 * Time: 8:16:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileRW {

     public static void writeMessageAppend(boolean addDate, String message, String outputFilePath) throws Exception{
      if(addDate)
         message= getDateMessage()+message;
      message+="\n*************\n";
      File outFile=new File(outputFilePath);
      FileOutputStream outFileStream=new FileOutputStream(outFile, true);//not append=false
      PrintWriter outStream=new PrintWriter(outFileStream);
      outStream.println(message);
      outStream.close();
  }

   public static String getDateMessage(){
      Date date = new Date(System.currentTimeMillis());
      Calendar calendar = new GregorianCalendar();
      StringBuffer errorMsg = new StringBuffer();
      errorMsg.append("Error occurred on: ").append(date.toString()).append(" at: ");
      errorMsg.append(calendar.get(Calendar.HOUR_OF_DAY)).append(":");
      errorMsg.append(calendar.get(Calendar.MINUTE)).append(":");
      errorMsg.append(calendar.get(Calendar.SECOND)).append('\n');
      return errorMsg.toString();
   }
}
