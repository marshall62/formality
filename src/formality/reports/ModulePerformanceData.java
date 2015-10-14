package formality.reports;

import formality.content.QModule;
import formality.Util.DataTuple;

import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Nov 3, 2010
 * Time: 11:15:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModulePerformanceData {
    public QModule module;
    public TreeMap<String, QuestionPerformanceData> performanceData; // map of questionId -> student performance data for that question

    public ModulePerformanceData(TreeMap<String, QuestionPerformanceData> perfData, QModule m) {
        this.performanceData = perfData;
        this.module = m;
    }
}
