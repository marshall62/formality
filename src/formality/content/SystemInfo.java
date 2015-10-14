package formality.content;

import formality.controller.FormalitySubsystem;
import formality.model.UserInfo;

import java.util.Vector;
import java.util.Iterator;
import java.util.TreeMap;


/**
 * <p>Title: </p>
 *
 */
public class SystemInfo {

    TreeMap tags_;
    Vector categories_;
    TreeMap coaches_;
    TreeMap catQCounts_;
    Vector strategies_;
    Vector catQuestions_;
    Vector standards_;
    Vector modules_;
    String contextPath_; // the web app name, i.e.:  /4mality
    String servletContext_; // the servlet context, e.g. http://cadmium.cs.umass.edu/formality
    String servletPath_;  // the servlet path, i.e.:  /servlet/MainServlet
    String resourcePath_; // path to images,javascript,css and other resources
    String soundPath;
    long probElapsedTime=0;
    long elapsedTime=0;
    long lastPageGenerationTime=0;
    String wayangContext;

    //user data
    UserInfo userInfo_;

    public SystemInfo() {
        tags_=new TreeMap();
        categories_=new Vector();
        coaches_=new TreeMap();
        strategies_=new Vector();
        standards_=new Vector();
        modules_=new Vector();
        catQCounts_=new TreeMap();
        catQuestions_=null;
        userInfo_=null;
        int accessLevel_=0;
    }

    public void addModule(QModule m){
        modules_.add(m);
    }

    public Iterator getCatQuestions(){
      return catQuestions_.iterator();
    }

    public void setCatQuestions(Vector cq){
      catQuestions_=cq;
    }

    public Iterator getModules(){
        return modules_.iterator();
    }

    public Iterator getPracticeModules(){
        Vector pmods=new Vector();
      Iterator mit = modules_.iterator();
      while(mit.hasNext()){
          QModule m=(QModule)mit.next();
          if(m.getType().equals("practice"));
              pmods.add(m);
      }
      return pmods.iterator();
    }

    public Iterator getTestModules(){
        Vector tmods=new Vector();
      Iterator mit = modules_.iterator();
      while(mit.hasNext()){
          QModule m=(QModule)mit.next();
          if(m.getType().equals("test"));
              tmods.add(m);
      }
      return tmods.iterator();
    }
    public void addCatCount(String cID, String c){
        catQCounts_.put(cID, c);
    }

    public void setCatCounts(TreeMap counts){
        catQCounts_=counts;
    }

    public String getCatCount(String cID){
        return (String)catQCounts_.get(cID);
    }

    public void addCategory(String cID, String c){
        categories_.add(new DualInfo(cID, c));
    }

    public Iterator getCategoryIDs(){
        Vector res=new Vector();
        for(int i=0;i<categories_.size();i++){
           DualInfo d = (DualInfo)categories_.get(i);
           res.add(d.getFirst());
        }
        return res.iterator();
    }

    //may return null
    public String getCategoryName(String cID){
        String res=null;
        for(int i=0;i<categories_.size();i++){
           DualInfo d = (DualInfo)categories_.get(i);
           if(d.getFirst().equals(cID))
               res=d.getSecond();
        }
        return res;
    }

    public int getCategorySize() throws Exception{
        return categories_.size();
    }

    public String getCategoryID(int index) throws Exception{
        DualInfo d = (DualInfo)categories_.get(index);
        return d.getFirst();
    }

    public String getCategoryName(int index) throws Exception{
        DualInfo d = (DualInfo)categories_.get(index);
        return d.getSecond();
    }

    public void setTagColor(String tag, String color){
        tags_.put(tag, color);
    }

    public String getTagColor(String tag){
      return (String)tags_.get(tag);
   }

   public UserInfo getUserInfo(){
     return userInfo_;
   }

   public void setServletPath(String r){
       servletPath_=r;
   }

    public String getContextPath(){
         return contextPath_;
   }

    // TODO this is holding a path to the web root.   Refactor this to have an appropriate name
    // and make sure all users of this really want the web root instead of the servlet context.
    public void setContextPath(String r){
       contextPath_=r;
   }

    // will set the variable to something like  http://localhost:8080/formality
    public void setServletContextPath(String servletURL) {
        servletContext_ = servletURL;
        servletContext_ = servletContext_.substring(0,servletContext_.lastIndexOf('/'));
    }

    // returns something link http://localhost:8080/formality
    public String getServletContext () {
        return servletContext_;
    }

    public String getServletPath () {
        return "FormalityServlet?";
    }

//    public String getServletPath(){
//         return servletPath_+"?";
//   }

    public String getServletRootAndID(){
      if(userInfo_!=null){
         return servletPath_.substring(1,servletPath_.length()) + "?"+FormalitySubsystem.userIDParam+"="+userInfo_.getUserID()+"&";
      }
     else
         return  servletPath_.substring(1,servletPath_.length()) +"?";
   }

   public String getServletRootAndID2(){
      if(userInfo_!=null){
         return getContextPath()+servletPath_+"?"+FormalitySubsystem.userIDParam+"="+userInfo_.getUserID()+"&";
      }
     else
         return getContextPath()+servletPath_+"?";
   }

   public TreeMap getTags(){
     return tags_;
   }

   public void addStandard(String std){
     standards_.add(std);
   }

   public Iterator getStandards(){
     return standards_.iterator();
   }
   public void addCoach(String cID, String n, String i){
    coaches_.put(cID, new DualInfo(n, i));
   }

   public Iterator getCoachIDs(){
       return coaches_.keySet().iterator();
   }

   public String getCoachName(String cID){
     DualInfo di = (DualInfo)coaches_.get(cID);
     return di.getFirst();
   }
   public String getCoachImg(String cID){
     DualInfo di = (DualInfo)coaches_.get(cID);
     return di.getSecond();
   }

   public void addStrategy(String sID, String n){
    strategies_.add(new DualInfo(sID,n));
   }

   public Vector getStrategyNames(){
     Vector res = new Vector();
     for(int i=0;i<strategies_.size();i++){
         DualInfo di = (DualInfo) strategies_.get(i);
         res.add(di.getSecond());
     }
     return res;
   }
   public Vector getStrategyIDs(){
      Vector res = new Vector();
      for(int i=0;i<strategies_.size();i++){
       DualInfo di = (DualInfo) strategies_.get(i);
       res.add(di.getFirst());
     }
    return res;
   }

    public String getMediaServlet() {
        return servletContext_ + "/MediaServlet";
    }

    public String getFormalityServlet() {
        return servletContext_ + "/FormalityServlet";
    }

    private class DualInfo{
      String first;
      String second;
      public DualInfo(String f, String s){
          first=f;
          second=s;
      }
      public String getFirst(){
        return first;
     }
     public String getSecond(){
       return second;
     }
  }

    public String getUserPwd() {
        if(userInfo_==null)
          return null;
        else
        return userInfo_.getUserPwd();
    }

    public String getUserLogin() {
        if(userInfo_==null)
          return null;
        else
          return userInfo_.getLogin();
    }

    public String getUserID() {
        if(userInfo_==null)
          return null;
        else
        return userInfo_.getUserID();
    }

    public void setUserInfo(UserInfo userInfo_) {
        this.userInfo_ = userInfo_;
    }

    public String getResourcePath() {
        return resourcePath_;
    }

    public void setResourcePath(String resourcePath_) {
        this.resourcePath_ = resourcePath_;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

    public long getProbElapsedTime() {
        return probElapsedTime;
    }

    public void setProbElapsedTime(long probElapsedTime) {
        this.probElapsedTime = probElapsedTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getLastPageGenerationTime() {
        return lastPageGenerationTime;
    }

    public void setLastPageGenerationTime(long lastPageGenerationTime) {
        this.lastPageGenerationTime = lastPageGenerationTime;
    }

    public String getWayangContext() {
        return wayangContext;
    }

    public void setWayangContext(String wayangContext) {
        this.wayangContext = wayangContext;
    }
}
