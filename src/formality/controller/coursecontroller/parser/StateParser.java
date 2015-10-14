package formality.controller.coursecontroller.parser;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Mar 7, 2006
 * Time: 2:51:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StateParser {
    public void parseState(String state)throws Exception;
    public void setPt();
    public void setP();
    public void setS();
    public void setPst();
    public void setFin();
    public String getState();
    public boolean isPreTest();
    public boolean isTestState();
    public boolean showPandTMods();
    public boolean showSelect();
    public boolean showPostTest();
    public boolean isFin();
    public String getStateStr();
    public String getDBStateStr();
    public int chooseModIndex(Vector mods);

    public boolean isSelect1();
    public boolean showT1();
    public boolean showP2();
    public boolean isSelect2();
    public boolean showT2();
    public boolean isSelect3();
    public boolean isPostTest();
    public boolean isTest2();
    public boolean isTest1();
}
