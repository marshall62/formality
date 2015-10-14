package formality.model.modeldata;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 */
public class BaseModelData {

    String errorStr_=null;
    
    public String getErrorStr() {
        return errorStr_;
    }

    public void setErrorStr(String errorStr_) {
        this.errorStr_ = errorStr_;
    }

    public boolean hasErrors(){
        return (errorStr_!= null);
    }
}
