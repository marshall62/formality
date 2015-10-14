package formality.Util;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * <p>Title: Emailer</p>
 * <p>Description: Static methods to send various types of email
 * messages. </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Emailer {

  public Emailer() {
  }

  /**
 * send an email to user with password info
 * @param userName
 * @param password
 * @param email
 * @return boolean
 */
 public static boolean sendPassword(String techEmail, String emailHost,String userName, String password, String email)
 {
  String body="Your login is: "+userName+'\n'+
              "Your password is: "+password;
  boolean mailSent = Mailer.send(email, techEmail,
                                 "vrroom message ", body, emailHost);
  return mailSent;
 }

  /**
  * send an email to tech support with error info
  * @param exception
  * @return boolean
  */
  public static boolean sendErrorEmail(String techEmail, String emailHost,String msg, Throwable exception)
  {
    Date date = new Date(System.currentTimeMillis());
    Calendar calendar = new GregorianCalendar();
    StringBuffer errorMsg = new StringBuffer();
    errorMsg.append("Error occurred on: ").append(date.toString()).append(" at: ");
    errorMsg.append(calendar.get(Calendar.HOUR_OF_DAY)).append(":");
    errorMsg.append(calendar.get(Calendar.MINUTE)).append(":");
    errorMsg.append(calendar.get(Calendar.SECOND)).append('\n');
    errorMsg.append("message: ");
    errorMsg.append(msg).append('\n');
    if(exception!=null){
      errorMsg.append(exception.getMessage()).append('\n');
      errorMsg.append(getErrorMsg(exception));
    }
    boolean mailSent = Mailer.send(techEmail, techEmail,
                               "4mality error message ", errorMsg.toString(), emailHost);
    return mailSent;
  }

  /**
  * get the error info out of the exception
  * @param exception
  * @return boolean
  */
  public static String getErrorMsg(Throwable exception)
  {
    StringBuffer errorMsg=new StringBuffer();
     StackTraceElement[] elems = exception.getStackTrace();
     for(int i=0;i<elems.length;i++)
     {
       errorMsg.append(elems[i]).append('\n');
     }
    return errorMsg.toString();
  }

}
