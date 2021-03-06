package formality.Util.xml;

import org.jdom.Document;
import org.jdom.Element;

import java.util.Iterator;
import java.util.TreeMap;

import formality.Util.xml.XmlUtils;
import formality.Util.DataTuple;
import formality.model.motivation.MotivationalModel;

/**
 * <p>Title:
 * </p>
 * <p>Description:
 * </p>
 * <p>Copyright: Copyright (c) Sep 9, 2005</p>
 * <p>Company: CCBIT, University of Massachusetts, Amherst</p>
 * User: battisti
 * Date: Sep 9, 2005
 * Time: 11:07:24 AM
 */
public class XmlFileParser
{

    /**

     <modulescores>
     <item>
     <answer>0</answer>
     <hints>1</hints>
     <score>0</score>
     <message> You checked with ...score.</message>
     </item>

     * @param xmlFile String
     * @param itemTree
     * @throws Exception
     */
    public void parseModuleScoreItems(String xmlFile, TreeMap<String, DataTuple> itemTree) throws Exception
    {
      Document doc = XmlUtils.getXmlDocument(xmlFile);
      Element root = doc.getRootElement();
      Iterator iter = root.getChildren("item").iterator();
      while(iter.hasNext()){
        Element itemElem = (Element)iter.next();
        String ans = itemElem.getChildTextTrim("answer");
        String hints = itemElem.getChildTextTrim("hints");
        String score = itemElem.getChildTextTrim("score");
        String msg = itemElem.getChildTextTrim("message");
        // key: answer#hints, val: score, msg
        String key = ans+MotivationalModel.delim_+hints;
        DataTuple dt = new DataTuple();
        dt.setFirst(score);
        dt.setSecond(msg);
        itemTree.put(key, dt);
      }
    }


}
