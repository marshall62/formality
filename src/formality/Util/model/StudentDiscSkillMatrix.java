package formality.Util.model;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Dec 10, 2006
 * Time: 9:00:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentDiscSkillMatrix {
    public static final String[] skillNames_={"4.N.1","4.N.2","4.N.3","4.N.4",
        "4.N.5","4.N.6","4.N.7","4.N.8","4.N.9","4.N.10","4.N.11","4.N.12",
        "4.N.13","4.N.14","4.N.15","4.N.16","4.N.17","4.N.18","4.P.1","4.P.2","4.P.3","4.P.4","4.P.5","4.P.6",
        "4.G.1","4.G.2","4.G.3","4.G.4","4.G.5","4.G.6","4.G.7","4.G.8","4.G.9","4.M.1","4.M.2","4.M.3","4.M.4",
        "4.M.5","4.D.1","4.D.2","4.D.3","4.D.4","4.D.5","4.D.6"};

    private static final String[] questionIDs_={"36","37","39","40","41","42","43","46","47","48","51","52","54","56","60",
"62","63","68","69","70","73","74","75","79","80","81","82","83","84","85",
"86","87","88","89","90","91","93","95","96","97","98","99","100","101","102",
"103","104","105","106","109","110","111","112","113","117","118","126","135",
"137","138","139","140","141"};

   private static final String[] testquestionIDs_={
       "0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25",
    "26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49",
        "50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70","71","72",
        "73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88","89","90","91","92","93","94","95",
        "96","97","98","99","100","101","102","103","104","105","106","107","108","109","110","111","112","113","114","115",
        "116","117","118","119","120","121","122","123","124","125","126","127","128","129","130","131","132","133","134",
        "135","136","137","138","139","140","141","142","143","144","145","146","147","148","149","150","151","152","153",
        "154","155","156","157","158","159","160","161","162","163","164","165","166","167","168","169","170","171","172",
        "173","174","175","176","177","178","179","180","181","182","183","184","185","186","187","188","189","190","191",
        "192","193","194","195","196","197","198","199","200","201","202","203","204","205","206","207","208","209","210",
        "211","212","213","214","215","216","217","218","219","220","221","222","223","224","225","226","227","228","229",
        "230","231","232","233","234","235","236","237","238","239","240","241","242","243","244","245","246","247","248",
        "249","250","251","252","253","254","255","256","257","258","259","260","261","262","263","264","265","266","267",
        "268","269","270","271","272","273","274","275","276","277","278","279","280","281","282","283","284","285","286",
        "287","288","289","290","291","292","293","294","295","296","297","298","299"};

    private static final double[] meanSkillVals_={0.865,0.695,0.775,0.555,0.62,0.585,0.72,0.715,0.68,0.748,0.689,
                                                  0.8,0.689,0.688,0.689,0.689,0.545,0.42,0.6867,0.55,0.683,0.695,
                                                  0.74,0.705,0.675,0.665,0.61,0.665,0.66,0.665,0.665,0.66,0.71,0.665
                                                  ,0.635,0.695,0.665,0.665,0.81,0.79,0.86,0.765,0.73,0.73};

    //each col is skill, a, b
    double[][] skillMatrix_ = new double[3][skillNames_.length];
    //each col is P(a=1|s=1), a, b
  //  double[][] qS1Matrix_ = new double[3][questionIDs_.length];
    //each col is P(a=1|s=0), a, b
 //   double[][] qS0Matrix_ = new double[3][questionIDs_.length];

    //each col is P(a=1|s=1), a, b
    double[][] qS1Matrix_ = new double[3][testquestionIDs_.length];
    //each col is P(a=1|s=0), a, b
    double[][] qS0Matrix_ = new double[3][testquestionIDs_.length];

    double decayFactor_=3.0;

   public double getSkillVal(String name){
         return skillMatrix_[0][getSkillIndex(name)];
   }

   public double getAlphaVal(String name){
         return skillMatrix_[1][getSkillIndex(name)];
   }

   public double getBetaVal(String name){
         return skillMatrix_[2][getSkillIndex(name)];
   }

  public double getQS1Val(String qID){
          return qS1Matrix_[0][getQIndex(qID)];
    }
  public double getQS0Val(String qID){
          return qS0Matrix_[0][getQIndex(qID)];
    }

  public double getQS1Alpha(String qID){
          return qS1Matrix_[1][getQIndex(qID)];
    }
 public double getQS0Alpha(String qID){
          return qS0Matrix_[1][getQIndex(qID)];
    }
  public double getQS1Beta(String qID){
          return qS1Matrix_[2][getQIndex(qID)];
    }
   public double getQS0Beta(String qID){
          return qS0Matrix_[2][getQIndex(qID)];
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

   public void setQS1Val(String qID, double val){
         qS1Matrix_[0][getQIndex(qID)]=val;
  }

   public void setQS0Val(String qID, double val){
         qS0Matrix_[0][getQIndex(qID)]=val;
  }

   public void setQS1Alpha(String qID, double val){
         qS1Matrix_[1][getQIndex(qID)]=val;
  }

     public void setQS0Alpha(String qID, double val){
         qS0Matrix_[1][getQIndex(qID)]=val;
  }

   public void setQS1Beta(String qID, double val){
         qS1Matrix_[2][getQIndex(qID)]=val;
  }

   public void setQS0Beta(String qID, double val){
         qS0Matrix_[2][getQIndex(qID)]=val;
  }

  public boolean hasQuestion(String qID) {
      return (getQIndex(qID)>=0);
  }

   public int getSkillIndex(String name){
     int index = -1;
     for(int i=0;i<skillNames_.length;i++){
        if(name.equals(skillNames_[i]))
              index=i;
       }
     return index;
   }
  /*
  public int getQIndex(String name){
     int index = -1;
     for(int i=0;i<questionIDs_.length;i++){
        if(name.equals(questionIDs_[i]))
              index=i;
       }
     return index;
   }
 */
      public int getQIndex(String name){
     int index = -1;
     for(int i=0;i<testquestionIDs_.length;i++){
        if(name.equals(testquestionIDs_[i]))
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
     initQmatrices();
    }
/*
   public void initQmatrices(){
     for(int i=0;i<questionIDs_.length;i++){
       qS1Matrix_[0][i]=0.5;
       qS0Matrix_[0][i]=0.5;
     }
     for(int i=0;i<questionIDs_.length;i++){
       qS1Matrix_[1][i]=1.0;
       qS0Matrix_[1][i]=1.0;
     }
     for(int i=0;i<questionIDs_.length;i++){
        qS1Matrix_[2][i]=1.0;
        qS0Matrix_[2][i]=1.0;
     }
   }
 */
      public void initQmatrices(){
     for(int i=0;i<testquestionIDs_.length;i++){
       qS1Matrix_[0][i]=0.5;
       qS0Matrix_[0][i]=0.5;
     }
     for(int i=0;i<testquestionIDs_.length;i++){
       qS1Matrix_[1][i]=1.0;
       qS0Matrix_[1][i]=1.0;
     }
     for(int i=0;i<testquestionIDs_.length;i++){
        qS1Matrix_[2][i]=1.0;
        qS0Matrix_[2][i]=1.0;
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
     initQmatrices();
   }

    public String debugPrint(){
      StringBuffer out = new StringBuffer();
     for(int i=0;i<skillNames_.length;i++)
        out.append(skillNames_[i]).append('\t');
     out.append("\r\n");
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
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
       out.append(testquestionIDs_[i]).append('\t');
     out.append("\r\n");
     out.append("P(A=1|S=1)").append("\r\n");
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
        out.append(testquestionIDs_[i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
        out.append(qS1Matrix_[0][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
        out.append(qS1Matrix_[1][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
        out.append(qS1Matrix_[2][i]).append('\t');
     out.append("\r\n");
     out.append("P(A=1|S=0)").append("\r\n");
     out.append("\r\n");
        for(int i=0;i<testquestionIDs_.length;i++)
        out.append(testquestionIDs_[i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
        out.append(qS0Matrix_[0][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
        out.append(qS0Matrix_[1][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<testquestionIDs_.length;i++)
        out.append(qS0Matrix_[2][i]).append('\t');
     out.append("\r\n");
     return out.toString();
   }

    /*
       public String debugPrint(){
      StringBuffer out = new StringBuffer();
     for(int i=0;i<skillNames_.length;i++)
        out.append(skillNames_[i]).append('\t');
     out.append("\r\n");
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
     out.append("\r\n");
     for(int i=0;i<questionIDs_.length;i++)
       out.append(questionIDs_[i]).append('\t');
     out.append("\r\n");
     out.append("P(A=1|S=1)").append("\r\n");
     out.append("\r\n");
     for(int i=0;i<questionIDs_.length;i++)
        out.append(qS1Matrix_[0][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<questionIDs_.length;i++)
        out.append(qS1Matrix_[1][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<questionIDs_.length;i++)
        out.append(qS1Matrix_[2][i]).append('\t');
     out.append("\r\n");
     out.append("P(A=1|S=0)").append("\r\n");
     out.append("\r\n");
     for(int i=0;i<questionIDs_.length;i++)
        out.append(qS0Matrix_[0][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<questionIDs_.length;i++)
        out.append(qS0Matrix_[1][i]).append('\t');
     out.append("\r\n");
     for(int i=0;i<questionIDs_.length;i++)
        out.append(qS0Matrix_[2][i]).append('\t');
     out.append("\r\n");
     return out.toString();
   }
   */

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

