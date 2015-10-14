package formality.content;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Feb 3, 2011
 * Time: 12:49:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModModuleLink {
    String childMod;
    String childModName;
    String parentMod;
    String parentModName;
    String courseId;

    public ModModuleLink(String childMod, String childModName, String parentMod, String parentModName, String courseId) {
        this.childMod = childMod;
        this.childModName = childModName;
        this.parentMod = parentMod;
        this.parentModName = parentModName;
        this.courseId = courseId;
    }

    public ModModuleLink(String childMod, String parentMod, String courseId) {
        this.childMod = childMod;
        this.parentMod = parentMod;
        this.courseId = courseId;
    }

    public String getChildMod() {
        return childMod;
    }

    public String getParentMod() {
        return parentMod;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getChildModName() {
        return childModName;
    }

    public String getParentModName() {
        return parentModName;
    }
}
