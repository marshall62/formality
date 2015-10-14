package formality.admin;

import cern.jet.stat.Gamma;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 * A temporary class used to move some skill settings for 4.N.4 to 4.n.2
 * User: marshall
 * Date: Nov 8, 2010
 * Time: 1:47:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixSkills extends AdminUser {

    public int insertNewUser(Connection conn, User u) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String[] args) {
        FixSkills fs = new FixSkills();
        fs.addModQuestionLinksSequencing();
//        fs.correctSkills();
    }

// the return:  newParams[0]=newAlpha,  newParams[1]=newBeta;
   private double[] calculateNewParameterValues(double curAlpha,
                                                double curBeta,
                                                int timesAnswered,
                                                int numHintsBeforeThisAnswer,
                                                boolean correct
                                               ) throws Exception {
        double newAlpha=0.0, newBeta=0.0;
        double W1 = Math.max(0.0, (1.0 - (0.25 * timesAnswered)));
        double W2 = 1.0 - (0.2 * numHintsBeforeThisAnswer);
        if(correct){
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

     private double calculateNewSkillValue(double theta, double alpha, double beta)throws Exception{
         double newSkillVal = 0.0;
         /* the formula is:
            1/B(a,b) * theta^alpha-1 * (1-theta)^beta-1
            the mean is alpha/(alpha+beta)
         */
         double a= alpha-1;
         double b= beta-1;
         try{
         double B = Gamma.beta(alpha, beta);
         } catch (Exception ex){
             throw new Exception(" error calculating Gamma dist for new skill value, params: alpha "+a+" beta "+b);
         }
         double term1 = Math.pow(theta, a);
         double term2 = Math.pow((1-theta), b);
         newSkillVal=alpha/(alpha+beta);//(term1*term2)/B;
         return newSkillVal;
     }

    private void addModQuestionLinksSequencing () {
        try {
            Connection conn= getConnection();
            ResultSet rs=null;
            PreparedStatement stmt=null;
            try {
                String q = "select id,moduleid,seq,questionid from modquestionlinks order by moduleid,id";
                stmt = conn.prepareStatement(q,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
                int lastMid=-1;
                int seq=0;
                rs = stmt.executeQuery();
                while (rs.next()) {
                    int id= rs.getInt(1);
                    int mid= rs.getInt(2);
                    int qid = rs.getInt(4);
                    // a change in modules means to reset the seq counter
                    if (mid != lastMid) {
                        seq=0;
                        lastMid=mid;
                    }
                     System.out.println(id + ":" + mid + ", " + qid + " seq: " + seq);
                    rs.updateInt(3,seq++);

                    rs.updateRow();
                }
            }
            finally {
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void correctSkills() {
        try {
            Connection conn= getConnection();
            ResultSet rs=null;
            PreparedStatement stmt=null;
            try {
                for (int i=1508;i<=1606;i++) {
                    String q = "select userid,f4n2,a4n2,b4n2,f4n4,a4n4,b4n4 from studentframeworkdata where userid=?";
                    stmt = conn.prepareStatement(q,ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                    stmt.setInt(1,i);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        double f2 = rs.getDouble(2);
                        double a2 = rs.getDouble(3);
                        double b2 = rs.getDouble(4);
                        double f4 = rs.getDouble(5);
                        double a4 = rs.getDouble(6);
                        double b4 = rs.getDouble(7);
                        // update skill2 with a correct answer
                        if (a4 > 0.75) {
                            double[] ab = calculateNewParameterValues(a2,b2,1,0,true);
                            f2 = calculateNewSkillValue(f2,ab[0],ab[1]);
                            rs.updateDouble(2,f2);
                            rs.updateDouble(3,ab[0]);
                            rs.updateDouble(4,ab[1]);
                            rs.updateDouble(5,0.5);
                            rs.updateDouble(6,0.001);
                            rs.updateDouble(7,0.001);
                            rs.updateRow();
                        }
                        // update skill2 with an incorrect answer
                        else if (b4 > 0.75) {
                            double[] ab = calculateNewParameterValues(a2,b2,1,0,false);
                            f2 = calculateNewSkillValue(f2,ab[0],ab[1]);
                            rs.updateDouble(2,f2);
                            rs.updateDouble(3,ab[0]);
                            rs.updateDouble(4,ab[1]);
                            rs.updateDouble(5,0.5);
                            rs.updateDouble(6,0.001);
                            rs.updateDouble(7,0.001);
                            rs.updateRow();
                        }

                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
