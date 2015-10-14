
    package formality.Util.model;

    import cern.jet.stat.Gamma;
    import java.util.ArrayList;
    import java.util.Iterator;
    import java.util.TreeMap;

    /**
     * Created by IntelliJ IDEA.
     * User: gordon
     * Date: Dec 9, 2006
     * Time: 10:09:40 PM
     * To change this template use File | Settings | File Templates.
     */
    public class BetaTwoStudentModel {
         String gSkillName="G";
        //TreeMap:  String sID, StudentSkillMatrix
        public TreeMap allStudentModels_;

        public BetaTwoStudentModel(){
            allStudentModels_= new TreeMap();
        }

       public TreeMap getAllStudentModels() {
            return allStudentModels_;
        }

     //Data is of this form:
       //questionid, moduleid, userid, correct, skill, difflevel, skills before answer
//ASSUME: data sorted by studentID, then by qID. So all data for a student's
//attempt at a question is consecutive.
        public void processData(ArrayList inputFileData) throws Exception
        {
         int linect=0;
           int curQuestionCt = 0;
           int curQID = 0;
           String curStudentID="";
           Iterator fileiter = inputFileData.iterator();
           String ans1=null, ans2=null;
            while(fileiter.hasNext()){
                linect++;
              ArrayList line = (ArrayList)fileiter.next();
              int qID = Integer.parseInt((String)line.get(0));
              String sID= (String)line.get(2);
              int ans = Integer.parseInt((String)line.get(3));
              String skillName = (String)line.get(4);
              String diffLevel = (String)line.get(5);
              if(line.size()>6)
                ans1 = (String)line.get(6);
              if(line.size()>7){
                ans2 = (String)line.get(7);
                if(ans2.equals("NULL"))
                  ans2=null;
              }
              int numHints = 0;
              if(line.size()>8)
                numHints = Integer.parseInt((String)line.get(8));
              //if change of student- set up the student model
              if(!curStudentID.equals(sID)){
                  if(allStudentModels_.get(sID)==null){
                    StudentTwoSkillMatrix m = new StudentTwoSkillMatrix();
                    m.init();
                    //  module.initMeanPriors();
                    allStudentModels_.put(sID, m);
                  }
                  curStudentID=sID;
                  //the question count has to be reset
                  curQuestionCt = 0;
                  curQID = 0;
              }
              if(curQID==qID){
                  curQuestionCt++;
              }
              else{
                  curQID = qID;
                  curQuestionCt = 1;
              }
              // bump up question count if second answer
              if(ans2!=null)
                curQuestionCt++;
              StudentTwoSkillMatrix curModel = (StudentTwoSkillMatrix)allStudentModels_.get(sID);
              //update the question-specific skill i.e. 4.N.1
              double curAlpha = curModel.getAlphaVal(skillName);
              double curBeta = curModel.getBetaVal(skillName);
              double curTheta = curModel.getSkillVal(skillName);
              double[] params = calculateNewParameterValues(curAlpha, curBeta,
                                          curQuestionCt, numHints,ans);
              double newAlpha = params[0];
              double newBeta = params[1];
              double newTheta = calculateNewSkillValue(curTheta, newAlpha, newBeta);
              curModel.setSkillVal(skillName, newTheta);
              curModel.setAlphaVal(skillName, newAlpha);
              curModel.setBetaVal(skillName, newBeta);
             //now update for the global skill G
              double curgAlpha = curModel.getAlphaVal(gSkillName);
              double curgBeta = curModel.getBetaVal(gSkillName);
              double curgTheta = curModel.getSkillVal(gSkillName);
              double[] gparams = calculateNewParameterValues(curgAlpha, curgBeta,
                                          curQuestionCt, numHints,ans);
             double newgAlpha = gparams[0];
             double newgBeta = gparams[1];
             double newgTheta = calculateNewSkillValue(curgTheta, newgAlpha, newgBeta);
             curModel.setSkillVal(gSkillName, newgTheta);
             curModel.setAlphaVal(gSkillName, newgAlpha);
             curModel.setBetaVal(gSkillName, newgBeta);
          }
         int f=linect;
        }

        private double[] calculateNewParameterValues(double curAlpha, double curBeta,
                                          int timesVisited, int hints,
                                          int ans) throws Exception {

            //W1 takes away .25 for each revisitation of a question,
            // where a visit is an entry in question log (at least one answer).
             // Don't count the first visit.
            double newAlpha=0.0, newBeta=0.0;
            //assume first time timesVisited=1
            timesVisited=timesVisited-1;
            double W1=Math.max(0.0, (1.0-0.25*timesVisited));
            double W2=1.0-0.2*hints;
            if(ans==1){
              newAlpha=curAlpha+(W1*W2);
              newBeta=curBeta;
            }
            else{
              newBeta=curBeta+(W1*W2);
              newAlpha=curAlpha;
            }
           double[] newParams = new double[2];
           newParams[0]=newAlpha;
           newParams[1]=newBeta;
           return newParams;
         }

         private double calculateNewSkillValue(double theta, double alpha, double beta){
             double newSkillVal = 0.0;
             /* the formula is:
                1/B(a,b) * theta^alpha-1 * (1-theta)^beta-1
                the mean is alpha/(alpha+beta)
             */
             double a= alpha-1;
             double b= beta-1;
             double B = Gamma.beta(alpha, beta);
             double term1 = Math.pow(theta, a);
             double term2 = Math.pow((1-theta), b);
             newSkillVal=alpha/(alpha+beta);//(term1*term2)/B;
             return newSkillVal;
         }
    }

