package formality.Util.model;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Dec 10, 2006
 * Time: 7:48:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentTwoSkillMatrix {
    public static final String[] skillNames_={"4.N.1","4.N.2","4.N.3","4.N.4",
         "4.N.5","4.N.6","4.N.7","4.N.8","4.N.9","4.N.10","4.N.11","4.N.12",
         "4.N.13","4.N.14","4.N.15","4.N.16","4.N.17","4.N.18","4.P.1","4.P.2","4.P.3","4.P.4","4.P.5","4.P.6",
         "4.G.1","4.G.2","4.G.3","4.G.4","4.G.5","4.G.6","4.G.7","4.G.8","4.G.9","4.M.1","4.M.2","4.M.3","4.M.4",
         "4.M.5","4.D.1","4.D.2","4.D.3","4.D.4","4.D.5","4.D.6", "G"};

     private static final double[] meanSkillVals_={0.865,0.695,0.775,0.555,0.62,0.585,0.72,0.715,0.68,0.748,0.689,
                                                   0.8,0.689,0.688,0.689,0.689,0.545,0.42,0.6867,0.55,0.683,0.695,
                                                   0.74,0.705,0.675,0.665,0.61,0.665,0.66,0.665,0.665,0.66,0.71,0.665
                                                   ,0.635,0.695,0.665,0.665,0.81,0.79,0.86,0.765,0.73,0.73,0.5};

     double[][] skillMatrix_ = new double[3][skillNames_.length];

     double decayFactor_=15.0;

    public double getSkillVal(String name){
          return skillMatrix_[0][getSkillIndex(name)];
    }

    public double getAlphaVal(String name){
          return skillMatrix_[1][getSkillIndex(name)];
    }

    public double getBetaVal(String name){
          return skillMatrix_[2][getSkillIndex(name)];
    }

   public void setSkillVal(String name, double val){
          skillMatrix_[0][getSkillIndex(name)]=val;
   }

   public void setAlphaVal(String name, double val){
          skillMatrix_[1][getSkillIndex(name)]=val;
   }

   public void setBetaVal(String name, double val){
          skillMatrix_[2][getSkillIndex(name)]=val;
   }

    public int getSkillIndex(String name){
      int index = -1;
      for(int i=0;i<skillNames_.length;i++){
         if(name.equals(skillNames_[i]))
               index=i;
        }
      return index;
    }

     public void init(){
      for(int i=0;i<skillNames_.length;i++){
         skillMatrix_[0][i]=0.5;
      }
      for(int i=0;i<skillNames_.length;i++){
        skillMatrix_[1][i]=1.0;
      }
      for(int i=0;i<skillNames_.length;i++){
         skillMatrix_[2][i]=1.0;
      }
    }

     public void initMeanPriors(){
      for(int i=0;i<meanSkillVals_.length;i++){
         double m = meanSkillVals_[i];
         skillMatrix_[0][i]=m;
         double a = calculateAlpha(m);
         skillMatrix_[1][i]=a;
         double b = calculateBeta(a);
         skillMatrix_[2][i]=b;
      }
    }

     public String debugPrint(){
       StringBuffer out = new StringBuffer();
      for(int i=0;i<skillNames_.length;i++){
         out.append(skillMatrix_[0][i]).append('\t');
      }
      out.append("\r\n");
      for(int i=0;i<skillNames_.length;i++){
         out.append(skillMatrix_[1][i]).append('\t');
      }
      out.append("\r\n");
      for(int i=0;i<skillNames_.length;i++){
         out.append(skillMatrix_[2][i]).append('\t');
      }
      out.append("\r\n");
      return out.toString();
    }

   // the mean of a beta is= a/a+b, decayFactor_=a+b,
     //  a/decayFactor_ = mean,
       // so the formula is:
     //a= decayFactor_*mean, b=decayFactor_-a
    private double calculateAlpha(double mean){
       return decayFactor_*mean;
    }

   private double calculateBeta(double a){
       return decayFactor_-a;
    }
 }

