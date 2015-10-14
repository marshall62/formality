package formality.html.contenttable;

import formality.content.SystemInfo;
import formality.content.QModule;
import formality.controller.coursecontroller.parser.StateParser;

import java.util.Vector;
import java.util.List;

/**
 * Implemented by all ContentTable classes- used by CourseControllers
 * to display the course content in its own way.
 * User: gordon
 * Date: Dec 28, 2005
 * Time: 10:29:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CourseContentTable {

    public void setState(StateParser state);

    public void setContent(List<List<QModule>> content)throws Exception;

    public void getContentTable(SystemInfo info,
                               StringBuffer page)throws Exception;
}
