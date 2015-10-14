package formality.content.database.mysql;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: Feb 25, 2012
 * Time: 9:21:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModuleCoachData {
    public ModuleCoachData(int modId, String name) {
        this.modId = modId;
        this.modName= name;
    }

    public int modId;
    public String modName;
    public int vicuna;
    public int hound;
    public int bear;
    public int estella;

    public void incrementCoachUse(int coachId) {
        if (coachId == 1)
            estella++;
        else if (coachId == 2)
            hound++;
         else if (coachId == 3)
            bear++;
         else if (coachId == 4)
            vicuna++;
    }
}
