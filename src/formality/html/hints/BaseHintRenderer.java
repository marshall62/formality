package formality.html.hints;

import formality.content.SystemInfo;
import formality.content.Question;


/**
 super class for hint rendering
         */

  public abstract class BaseHintRenderer {
            String hintNavUrl_;
            String hintUrl_;

    // call this before any other public methods
    public abstract void initHintRenderer(SystemInfo info, Question q) throws Exception;

    public String getHintNavUrl() {
            return hintNavUrl_;
     }

    public void setHintNavUrl(String hintNavUrl_) {
            this.hintNavUrl_ = hintNavUrl_;
    }

    public String getHintUrl() {
            return hintUrl_;
    }

    public void setHintUrl(String hintUrl_) {
            this.hintUrl_ = hintUrl_;
    }

   public abstract void getHintDisplay(StringBuffer page, String hintId)throws Exception ;

}
