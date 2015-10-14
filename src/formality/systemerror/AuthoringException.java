package formality.systemerror;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

    public class AuthoringException extends Exception {
        String msg_;
        String returnUrl_;

        public AuthoringException() {
            msg_ = "An authoring error has occurred: ";
        }

        public void setMessage(String msg){
           msg_=msg_+" "+msg;
        }

        public String getMessage(){
            return msg_;
        }

     public void setReturnUrl(String url){
       returnUrl_= url;
    }

    public String getReturnUrl(){
        return returnUrl_;
    }

    }
